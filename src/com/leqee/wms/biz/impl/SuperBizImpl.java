package com.leqee.wms.biz.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.biz.HTBiz;
import com.leqee.wms.biz.SFBiz;
import com.leqee.wms.biz.SuperBiz;
import com.leqee.wms.biz.YDBiz;
import com.leqee.wms.biz.YTOBiz;
import com.leqee.wms.biz.ZTOBiz;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.ConfigPrintDispatchBillDao;
import com.leqee.wms.dao.OrderGoodsDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ReportDao;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.ShipmentDetailDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.ShippingTrackingNumberRepositoryDao;
import com.leqee.wms.dao.ShippingWarehouseMappingDao;
import com.leqee.wms.dao.UserActionShipmentDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.dao.UserActionOrderDao;
import com.leqee.wms.entity.OrderProcess;
import com.leqee.wms.entity.Shipment;
import com.leqee.wms.entity.ShipmentDetail;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.ShippingTrackingNumberRepository;
import com.leqee.wms.entity.UserActionOrder;
import com.leqee.wms.entity.UserActionShipment;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.WorkerUtil;

@Service
public class SuperBizImpl implements SuperBiz {

	private Logger logger = Logger.getLogger(ShipmentBizImpl.class);

	@Autowired
	ShipmentDao shipmentDao;
	@Autowired
	OrderGoodsDao orderGoodsDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderProcessDao orderProcessDao;
	@Autowired
	UserActionOrderDao userActionOrderDao;
	@Autowired
	WarehouseDao warehouseDao;
	@Autowired
	ProductDao productDao;
	@Autowired
	ShippingTrackingNumberRepositoryDao shippingTrackingNumberRepositoryDao;
	@Autowired
	ShipmentDetailDao shipmentDetailDao;
	@Autowired
	UserActionShipmentDao userActionShipmentDao;
	@Autowired
	ConfigPrintDispatchBillDao configPrintDispatchBillDao;
	@Autowired
	YDBiz ydBiz;
	@Autowired
	HTBiz htBiz;
	@Autowired
	ZTOBiz ztoBiz;
	@Autowired
	YTOBiz ytoBiz;
	@Autowired
	SFBiz sfBiz;
	@Autowired
	ConfigDao configDao;
	@Autowired
	ShippingDao shippingDao;
	@Autowired
	ShippingWarehouseMappingDao shippingWarehouseMappingDao;
	private ReportDao reportDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setReportDaoSlave(SqlSession sqlSession) {
		this.reportDaoSlave = sqlSession.getMapper(ReportDao.class);
	}

	/**
	 * (超级复核)加载商品
	 */
	@Override
	public Map<String, Object> loadGoodsForOrder(Integer orderId) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		//增加限制：如果config表中config_value='Y'的仓库业务组合为null ，则禁止进入
		Map<String,Object> configValue = configDao.checkSuperRecheckConfigForOrder(orderId);
		String customerName = WorkerUtil.isNullOrEmpty(configValue)?"未配置":String.valueOf(configValue.get("brand_name"));
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(configValue) &&
				"Y".equalsIgnoreCase(String.valueOf(configValue.get("config_value"))),"订单无权限操作超级复核（"+customerName+"）");
		// 1. 获取订单所有商品信息(包含耗材商品)
		List<Map> orderGoodsInfo = orderGoodsDao
				.selectGoodsInfoByOrderId(orderId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(orderGoodsInfo), "订单" + orderId
				+ "没有搜到任何有关订单商品信息");
		Assert.isTrue(!OrderInfo.ORDER_STATUS_CANCEL.equalsIgnoreCase(String
				.valueOf(orderGoodsInfo.get(0).get("order_status"))), "订单"
				+ orderId + "已取消！");
		Assert.isTrue(OrderInfo.ORDER_STATUS_PICKING.equalsIgnoreCase(String
				.valueOf(orderGoodsInfo.get(0).get("order_status"))), "订单"
				+ orderId + "状态并非“拣货中”，不能操作复核");
		Assert.isTrue(
				!String.valueOf(orderGoodsInfo.get(0).get("batch_process_type")).equals("BATCH"), 
				"订单" + orderId + "属于批量波次（"+ orderGoodsInfo.get(0).get("batch_pick_sn")+ "），不支持超级复核");
		// 2. 用户仓库权限
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<Warehouse> warehouseList = (List<Warehouse>) session
				.getAttribute("userLogicWarehouses");
		Warehouse orderWarehouse = warehouseDao.selectByWarehouseId(Integer
				.parseInt(String.valueOf(orderGoodsInfo.get(0).get(
						"warehouse_id"))));
		Assert.isTrue(warehouseList.contains(orderWarehouse),
				"当前登录用户没有权限操作此订单，请检查用户仓库权限！");
		
		//add 于16你那10月10日 判断is_first_shipment是否为2
		OrderProcess orderProcess = orderProcessDao.selectByPrimaryKey(orderId);
		Integer recheckType = orderProcess.getRecheck_type();
		Assert.isTrue(!recheckType.equals(1), "订单"+orderId+"已经操作普通复核！");
		if(recheckType.equals(0)){
			Integer col = orderProcessDao.updateRecheckType(orderId,recheckType,2);
			Assert.isTrue(col>0, "订单"+orderId+"选择复核流程状态更新失败！");
		}else{
			throw new RuntimeException("订单"+orderId+"复核流程状态异常(已经执行超级复核，但未绑定耗材)，请执行取消复核后重新复核！");
		}
		Integer shipmentId = 0;		
		Integer isFirstShipment = orderProcess.getIs_first_shipment();
		if(isFirstShipment.equals(0)){
			Integer shippingId = Integer.parseInt(String.valueOf(orderGoodsInfo.get(0).get("shipping_id")));
			// 订单同步时已经设定，排除非空可能
			// 创建发货单
			logger.info("订单" + orderId+ " has't create shipment,begin to create shipment");
			
			try {
				shipmentId = createShipment(orderId, shippingId);
			} catch (RuntimeException e) {
				//System.out.println("订单" + orderId + " create shipment failed!");
				throw new RuntimeException("订单" + orderId + "创建发货单失败！");
			}
			logger.info("订单" + orderId + " create shipment end,shipmentId is "
					+ shipmentId);
	
			// 获取运单号
			Map<String, Object> traMap = applyMailno("APPLY", orderId,
					shipmentId);
			Assert.isTrue(Response.SUCCESS.equals(traMap.get("result")),
					String.valueOf(traMap.get("note")));
			String trackingNumber = String.valueOf(traMap
					.get("tracking_number"));
			String mark = String.valueOf(traMap.get("mark"));
	
			// 更新 订单面单信息，快递资源数据
			try {
				shipmentDao.updateTrackingNumber(shipmentId, trackingNumber,
						mark);
				if (traMap.get("origincode") != null) {
					String origincode = String
							.valueOf(traMap.get("origincode"));
					String destcode = String.valueOf(traMap.get("destcode"));
					shipmentDao.updateSfShipment(shipmentId, origincode,
							destcode);
				}
				orderInfoDao.updateTrackingNumber(orderId, trackingNumber);
			} catch (RuntimeException e) {
				logger.info("更新运单号绑定关系失败！shipmentId:" + shipmentId
						+ " , trackingNumber :" + trackingNumber);
				throw new RuntimeException("更新运单号绑定关系失败！shipmentId:"
						+ shipmentId + " , trackingNumber :" + trackingNumber);
			}
			Integer col = orderProcessDao.updateIsFirstShipment(orderId,isFirstShipment,1);
			Assert.isTrue(col>0, "已经有其他方式申请面单成功了，请重新复核！");
		}else{
			shipmentId = shipmentDao.selectByOrderId(orderId).get(0).getShipment_id();
		}
		List<OrderGoods> orderGoods = orderGoodsDao.selectByOrderId(orderId);
		List<ShipmentDetail> shipmentDetailList = new ArrayList<ShipmentDetail>();
		for (OrderGoods goods : orderGoods) {
			//绑定商品
			ShipmentDetail shipmentDetail = new ShipmentDetail();
			shipmentDetail.setGoods_name(goods.getGoods_name());
			shipmentDetail.setGoods_number(goods.getGoods_number());
			shipmentDetail.setOrder_goods_id(goods.getOrder_goods_id());
			shipmentDetail.setProduct_id(goods.getProduct_id());
			shipmentDetail.setShipment_id(shipmentId);
			shipmentDetailList.add(shipmentDetail);
		}
		shipmentDetailDao.batchCreateShipmentDetail(shipmentDetailList);
		
		// 4. 查询未绑定商品信息
		List<Map> unbindGoodsInfo = orderInfoDao.getUnbindGoodsInfo(orderId);
		List<Map> bindShipmentGoodsInfo = shipmentDao.selectBindShipmentGoodsInfoByOrderId(orderId);
		// 5. 根据绑定信息确定方案
		List<Map> bindGoodsInfo = formatBindShipmentGoodsInfo(bindShipmentGoodsInfo,
				0, "");
		resMap.put("result", Response.SUCCESS);
		resMap.put("unbind_goods_info", unbindGoodsInfo);
		resMap.put("bind_goods_info", bindGoodsInfo);
		resMap.put("shipment_id", shipmentId);
		return resMap;
	}

	/**
	 * 根据绑定的快递商品数据与新添加快递 共同组合bindGoodsInfo
	 * 
	 * @param bindShipmentGoodsInfo
	 * @param shipmentId
	 * @param trackingNumber
	 * @return bindGoodsInfo Map bindGoodsMap
	 *         shipment_id,tracking_number,tn_goods_total,shipment_goods_info
	 */
	private List<Map> formatBindShipmentGoodsInfo(
			List<Map> bindShipmentGoodsInfo, Integer shipmentId,
			String trackingNumber) {
		List<Map> bindGoodsInfo = new ArrayList<Map>();
		Map<String, Object> bindGoodsMap = new HashMap<String, Object>();
		List<Integer> shipmentMap = new ArrayList<Integer>();
		if (shipmentId != 0 && trackingNumber != "") {
			bindGoodsMap.put("shipment_id", shipmentId);
			bindGoodsMap.put("tracking_number", trackingNumber);
			bindGoodsMap.put("tn_goods_total", 0);
			bindGoodsMap.put("shipment_goods_info", new ArrayList<Map>());
			shipmentMap.add(shipmentId);
			bindGoodsInfo.add(bindGoodsMap);
			bindGoodsMap = new HashMap<String, Object>();
		}
		if (WorkerUtil.isNullOrEmpty(bindShipmentGoodsInfo)) {
			return bindGoodsInfo;
		}
		Integer tn_goods_total = 0;
		Integer tn_shipment_id = 0;
		List<Map> shipment_goods_info = new ArrayList<Map>();
		for (int i = 0; i < bindShipmentGoodsInfo.size(); i++) {
			Map bindGoods = bindShipmentGoodsInfo.get(i);
			tn_shipment_id = Integer.parseInt(String.valueOf(bindGoods
					.get("shipment_id")));
			String tn_tracking_number = String.valueOf(bindGoods
					.get("tracking_number"));
			Integer goods_number = 0;
			if (!WorkerUtil.isNullOrEmpty(bindGoods.get("goods_number")) && 
					!"WMS888888888".equalsIgnoreCase(String.valueOf(bindGoods.get("barcode")))){
				goods_number = Integer.parseInt(String.valueOf(bindGoods
						.get("goods_number")));
			}

			if (!shipmentMap.contains(tn_shipment_id)) {// 1. 不包含
				shipmentMap.add(tn_shipment_id);
				if (!bindGoodsMap.isEmpty()) {
					bindGoodsInfo.add(bindGoodsMap);
					bindGoodsMap = new HashMap<String, Object>();
				}
				shipment_goods_info = new ArrayList<Map>();
				if (!WorkerUtil.isNullOrEmpty(bindGoods.get("barcode")) &&
					!"WMS888888888".equalsIgnoreCase(String.valueOf(bindGoods.get("barcode")))) {
					shipment_goods_info.add(bindGoods);
				}
				tn_goods_total = goods_number;
				bindGoodsMap.put("shipment_id", tn_shipment_id);
				bindGoodsMap.put("tn_goods_total", tn_goods_total);
				bindGoodsMap.put("tracking_number", tn_tracking_number);
				bindGoodsMap.put("shipment_goods_info", shipment_goods_info);

			} else if (!WorkerUtil.isNullOrEmpty(bindGoods.get("barcode")) &&
					!"WMS888888888".equalsIgnoreCase(String.valueOf(bindGoods.get("barcode")))) {
				shipment_goods_info.add(bindGoods);
				tn_goods_total = tn_goods_total + goods_number;
				bindGoodsMap.put("tn_goods_total", tn_goods_total);
				bindGoodsMap.put("shipment_goods_info", shipment_goods_info);
			}
			if (i == bindShipmentGoodsInfo.size() - 1) {
				bindGoodsInfo.add(bindGoodsMap);
				bindGoodsMap = new HashMap<String, Object>();
			}
		}
		return bindGoodsInfo;
	}

	public Integer createShipment(Integer orderId, Integer shippingId) {
		List<Shipment> shipmentList = shipmentDao.selectByOrderId(orderId);
		Integer shipmentOrderSequenceNum = shipmentList.size();
		String localUser = (String) SecurityUtils.getSubject().getPrincipal();
		Shipment s = new Shipment();
		s.setOrder_id(orderId);
		s.setStatus("INIT");
		s.setShipping_id(shippingId);
		s.setCreated_user(localUser);
		s.setCreated_time(new Date());
		s.setLast_updated_user(localUser);
		s.setLast_updated_time(new Date());
		s.setShipment_order_sequence_number(shipmentOrderSequenceNum + 1);
		shipmentDao.createShipment(s);
		Integer shipment_id = s.getShipment_id();
		UserActionShipment userActionShipment = new UserActionShipment();
		userActionShipment.setAction_note("CREATE_SHIPMENT");
		userActionShipment.setAction_type("CREATE_SHIPMENT");
		userActionShipment.setCreated_user(localUser);
		userActionShipment.setCreated_time(new Date());
		userActionShipment.setShipment_id(shipment_id);
		userActionShipment.setStatus("INIT");
		userActionShipmentDao
				.insertUserActionShipmentRecord(userActionShipment);
		return shipment_id;
	}

	
	public Map<String, Object> applyMailno(String action, Integer orderId,
			Integer shipmentId) {
		logger.info("订单" + orderId + " " + action + " Mailno , shipmentId is "
				+ shipmentId);
		String localUser = (String) SecurityUtils.getSubject().getPrincipal();
		if (WorkerUtil.isNullOrEmpty(localUser)) {
			localUser = "system";
		}
		Map<String, Object> resMap = new HashMap<String, Object>();
		// 分三种数据
		// 1. shipping_app,paper_type,shipping_id,warehouse_id
		Map appInfo = shipmentDao.selectShipOrderPaperType(orderId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(appInfo), "订单" + orderId
				+ "没有配置热敏账号");
		String paperType = String.valueOf(appInfo.get("paper_type"));
		Assert.isTrue("electronic".equalsIgnoreCase(paperType), "订单" + orderId
				+ "面单为普通方式！");
		// 2. 收件人信息
		Map receiveInfo = this.selectReceiveInfo(shipmentId);
		String trackingNumber = String.valueOf(receiveInfo
				.get("tracking_number"));
		String applyType = String.valueOf(appInfo.get("apply_type"));
		String shippingCode = String.valueOf(receiveInfo.get("shipping_code"));
		Integer warehouseId = Integer.parseInt(String.valueOf(receiveInfo.get("warehouse_id")));
		if (!WorkerUtil.isNullOrEmpty(receiveInfo.get("tracking_number"))) {
			resMap.put("result", Response.SUCCESS);
			resMap.put("tracking_number", trackingNumber);
			return resMap;
		}
		// 设置 默认大头笔格式
		String mark = "";
		List<Integer> municipalities = new ArrayList<Integer>();
		municipalities.add(2);
		municipalities.add(3);
		municipalities.add(10);
		municipalities.add(23);
		String province_name = String.valueOf(receiveInfo
				.get("receive_province"));
		Integer province_id = Integer.parseInt(String.valueOf(receiveInfo
				.get("province_id")));
		String city_name = String.valueOf(receiveInfo.get("receive_city"));
		String district_name = String.valueOf(receiveInfo
				.get("receive_district"));
		if ("ZTO".equals(shippingCode) || "HT".equals(shippingCode)) {
			String shippingMark = orderProcessDao.getShippingMark(orderId);
			Map<String,Object> singleOrder = orderProcessDao.getSingleOrder(orderId);
			if((WorkerUtil.isNullOrEmpty(shippingMark) || shippingMark=="") && "ZTO".equals(shippingCode)){
				mark = ztoBiz.getZTOMarkForSingleOrder(singleOrder);
			}else if((WorkerUtil.isNullOrEmpty(shippingMark) || shippingMark=="") && "HT".equals(shippingCode)){
				mark = htBiz.getHTMarkForSingleOrder(singleOrder);
			}else{
				mark = shippingMark;
			}
		}
		if(mark==""){
			if (!WorkerUtil.isNullOrEmpty(receiveInfo.get("receive_city"))) {
				if (city_name.length() > 6) {
					mark = province_name + district_name;
				} else if (municipalities.contains(province_id)) {
					mark = province_name + city_name;
				} else {
					mark = province_name + city_name + district_name;
				}
			} else {
				mark = province_name + district_name;
			}
		}
		
		// 3.判断热敏方式 ：batch/single
		// 3.2 分快递apply
		if (!"batch".equalsIgnoreCase(applyType)) {
			logger.info("订单" + orderId + " applyType is not batch ("
					+ applyType + ")"); // 暂不支持单独申请方式
			// add by dlyao
			if ("YTO".equals(shippingCode)) {
				// bigPen
				logger.info("订单" + orderId
						+ " begin to apply single yto mailnos");
				Map trackingNumberInfoMap = ytoBiz
						.applyOneTrackingNumberByShipmentId(orderId,
								shipmentId, appInfo, receiveInfo);
				Assert.isTrue("success".equalsIgnoreCase(String
						.valueOf(trackingNumberInfoMap.get("result"))), String
						.valueOf(trackingNumberInfoMap.get("note")));
				trackingNumber = String.valueOf(trackingNumberInfoMap
						.get("trackingNumber"));
				mark = String.valueOf(trackingNumberInfoMap.get("mark"));
				try{
					shippingTrackingNumberRepositoryDao
						.insertTrackingNumer(Integer.parseInt(String
								.valueOf(appInfo.get("app_id"))),
								trackingNumber, "Y", localUser, WorkerUtil
										.getNow());
				}catch (Exception e) {
					throw new RuntimeException("数据资源获取失败！请重新尝试");
				}
				logger.info("订单" + orderId + " apply single yto mailnos end");

			} else if ("YD".equals(shippingCode) || "YD_BQ".equalsIgnoreCase(shippingCode)) { 
				logger.info("订单" + orderId
						+ " begin to apply single yunda mailnos");
				Map trackingNumberInfoMap = ydBiz
						.applyOneTrackingNumberByShipmentId(orderId,
								shipmentId, appInfo, receiveInfo, warehouseId);
				Assert.isTrue("success".equalsIgnoreCase(String
						.valueOf(trackingNumberInfoMap.get("result"))), String
						.valueOf(trackingNumberInfoMap.get("note")));
				trackingNumber = String.valueOf(trackingNumberInfoMap
						.get("trackingNumber"));
				try{	
					shippingTrackingNumberRepositoryDao
						.insertTrackingNumer(Integer.parseInt(String
								.valueOf(appInfo.get("app_id"))),
								trackingNumber, "Y", localUser, WorkerUtil
										.getNow());
				}catch (Exception e) {
					throw new RuntimeException("数据资源获取失败！请重新尝试");
				}
				logger.info("订单" + orderId + " apply single yunda mailnos end");

			} else if ("SF".equals(shippingCode) || "SFLY".equals(shippingCode)) {
				logger.info("订单" + orderId
						+ " begin to apply sinfle sf mailnos");
				Map trackingNumberInfoMap = sfBiz
						.applyOneTrackingNumberByShipmentId(orderId,
								shipmentId, appInfo, receiveInfo);
				Assert.isTrue("success".equalsIgnoreCase(String
						.valueOf(trackingNumberInfoMap.get("result"))), String
						.valueOf(trackingNumberInfoMap.get("note")));
				trackingNumber = String.valueOf(trackingNumberInfoMap
						.get("trackingNumber"));
				resMap.put("origincode",
						trackingNumberInfoMap.get("origincode"));
				resMap.put("destcode", trackingNumberInfoMap.get("destcode"));
				try{
					shippingTrackingNumberRepositoryDao
						.insertTrackingNumer(Integer.parseInt(String
								.valueOf(appInfo.get("app_id"))),
								trackingNumber, "Y", localUser, WorkerUtil
										.getNow());
				}catch (Exception e) {
					throw new RuntimeException("数据资源获取失败！请重新尝试");
				}
				logger.info("订单" + orderId + " apply single sf mailnos end");
			} else if("LEQEE_ZT".equals(shippingCode)){
				trackingNumber = "LEQEE"+shipmentId;
			} else {
				resMap.put("result", Response.FAILURE);
				resMap.put("note", "订单" + orderId + "所属快递并未进行系统快递对接");
				return resMap;
			}
		} else {
			Integer appId = Integer.valueOf(String.valueOf(appInfo
					.get("app_id")));
			int i = 1;
			while (true) {
				trackingNumber = shippingTrackingNumberRepositoryDao
						.getTrackingNumber(appId);
				Assert.isTrue(!WorkerUtil.isNullOrEmpty(trackingNumber), "订单"
						+ orderId + "资源库已空，运单不足");
				try {
					shippingTrackingNumberRepositoryDao
							.updateRepositoryTrackingNumber(trackingNumber, "Y");
					break;
				} catch (RuntimeException e) {
					System.out
							.println("订单" + orderId + "第" + i + "次抢占运单资源，失败！");
					Assert.isTrue(i != 3, "订单" + orderId + "尝试三次竞拍单号，均失败");
				}
			}
			
		}
		// shipmentDao.updateMarkForShipment(order_id,big_pen);
		resMap.put("mark", mark);
		if (!WorkerUtil.isNullOrEmpty(trackingNumber)) {
			try {
				String status = receiveInfo.get("status").toString();

				UserActionShipment userActionShipment = new UserActionShipment();
				userActionShipment.setAction_note(action + "_MAILNOS:"
						+ trackingNumber);
				userActionShipment.setAction_type(action + "_MAILNOS");
				userActionShipment.setCreated_user(localUser);
				userActionShipment.setCreated_time(new Date());
				userActionShipment.setShipment_id(shipmentId);
				userActionShipment.setStatus(status);
				userActionShipmentDao
						.insertUserActionShipmentRecord(userActionShipment);
			} catch (RuntimeException e) {
				resMap.put("result", Response.FAILURE);
				resMap.put("note", "订单" + orderId + "，发货单" + shipmentId
						+ "操作记录添加失败:" + trackingNumber);
				return resMap;
			}
		}
		resMap.put("result", "success");
		resMap.put("tracking_number", trackingNumber);

		return resMap;
	}

	private Map selectReceiveInfo(Integer shipmentId) {
		Map receiveInfo = shipmentDao.selectReceiveInfo(shipmentId);
		String phone = "";
		if (!WorkerUtil.isNullOrEmpty(receiveInfo.get("receive_mobile"))
				|| String.valueOf(receiveInfo.get("receive_mobile")).length() > 0) {
			phone = String.valueOf(receiveInfo.get("receive_mobile"));
		} else if (!WorkerUtil.isNullOrEmpty(receiveInfo.get("receive_phone"))
				|| String.valueOf(receiveInfo.get("receive_phone")).length() > 0) {
			phone = String.valueOf(receiveInfo.get("receive_phone"));
		}
		receiveInfo.put("phone", phone);
		return receiveInfo;
	}

	/**
	 * (超级复核)绑定耗材
	 */
	@Override
	public Map<String, Object> bindConsume(Integer shipmentId, String barcode,
			Integer orderId) {
		Map<String, Object> resMap = new HashMap<String, Object>();

		// 1. 判断 发货单号存在性 ，且未绑定过耗材 , 为当前订单最新shipmentId
		Map shipmentInfo = shipmentDao.getShipmentInfoById(shipmentId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(shipmentInfo), "发货单"
				+ shipmentId + " 未找到符合的发货单信息");
		Assert.isTrue(WorkerUtil.isNullOrEmpty(shipmentInfo
				.get("packbox_product_id")), "发货单" + shipmentId + " 已经绑定过耗材");
		Assert.isTrue(
				!WorkerUtil.isNullOrEmpty(shipmentInfo.get("product_id")),
				"发货单" + shipmentId + " 还未绑定过任何商品，不允许绑定耗材");
		Assert.isTrue(
				shipmentInfo.get("shipment_id").equals(
						shipmentInfo.get("last_shipment_id")), "发货单"
						+ shipmentId + " 并不是订单的最新发货单，刷新看看");
		Assert.isTrue(OrderInfo.ORDER_STATUS_PICKING.equalsIgnoreCase(String
				.valueOf(shipmentInfo.get("order_status"))), "订单" + orderId
				+ "状态并非“拣货中”，不能绑定耗材");
		// 2. 判断barcode 存在性，对应是否为耗材 (通用耗材所属业务组 -- 乐其仓库)
		String orderCustomerId = shipmentInfo.get("customer_id").toString();
		Map productInfo = productDao.selectByBarodeForConsume(barcode.trim(),orderCustomerId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(productInfo), "条码" + barcode
				+ " 未找到对应耗材信息");
		Assert.isTrue(OrderGoods.ORDER_GOODS_TYPE_PACKBOX
				.equalsIgnoreCase(productInfo.get("product_type").toString()),
				"条码" + barcode + " 并不是耗材");
		String barcode_customer_id = productInfo.get("customer_id").toString();

		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKeyForUpdate(orderId);
		Assert.isTrue(OrderInfo.ORDER_STATUS_PICKING.equalsIgnoreCase(String
				.valueOf(orderInfo.getOrder_status())), "订单" + orderId
				+ "状态并非“拣货中”，不能绑定耗材");
		Integer warehouseId = orderInfo.getWarehouse_id();

		List<Map> unbindGoodsInfo = orderInfoDao.getUnbindGoodsInfo(orderId);

		String productId = productInfo.get("product_id").toString();
		String productName = productInfo.get("product_name").toString();
		Integer orderGoodsId = 0;
		// 3. insert order_goods,update shipment,insert shipment_detail,insert
		// user_action_shipment
		try {
			String localUser = (String) SecurityUtils.getSubject()
					.getPrincipal();
			int effectRows = 0;
			if (WorkerUtil.isNullOrEmpty(unbindGoodsInfo)) {
				Map<String, Object> paramsForOrderInfoUpdate = new HashMap<String, Object>();
				paramsForOrderInfoUpdate.put("orderStatus",
						OrderInfo.ORDER_STATUS_RECHECKED);
				paramsForOrderInfoUpdate.put("lastUpdatedUser", localUser);
				paramsForOrderInfoUpdate.put("lastUpdatedTime", new Date());
				paramsForOrderInfoUpdate.put("orderId", orderId);
				effectRows = orderInfoDao
						.updateOrderStatusByOrderId(paramsForOrderInfoUpdate);
				Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);

				UserActionOrder userActionOrder = new UserActionOrder();
				userActionOrder.setOrder_id(orderId);
				userActionOrder
						.setOrder_status(OrderInfo.ORDER_STATUS_RECHECKED);
				userActionOrder
						.setAction_type(OrderInfo.ORDER_STATUS_RECHECKED);
				userActionOrder.setAction_note("(超级复核)订单复核完成");
				userActionOrder.setCreated_user(localUser);
				userActionOrder.setCreated_time(new Date());
				userActionOrderDao.insert(userActionOrder);
			}
			OrderGoods orderGoods = new OrderGoods();
			orderGoods.setOrder_id(Integer.parseInt(shipmentInfo
					.get("order_id").toString()));
			orderGoods.setOms_order_goods_sn("");
			orderGoods.setWarehouse_id(warehouseId);
			orderGoods.setCustomer_id(Integer.parseInt(barcode_customer_id));
			orderGoods.setProduct_id(Integer.parseInt(productId));
			orderGoods.setGoods_name(productName);
			orderGoods.setGoods_number(1);
			orderGoods.setGoods_price(new BigDecimal(0));
			orderGoods.setDiscount(new BigDecimal(0));
			orderGoods.setBatch_sn("");
			orderGoods.setStatus_id("NORMAL");// 表示全新
			orderGoods.setTax_rate(new BigDecimal("1.1700"));
			orderGoods.setOrder_goods_type(productInfo.get("product_type")
					.toString());
			orderGoods.setCreated_user(localUser);
			orderGoods.setCreated_time(new Date());
			orderGoods.setLast_updated_user(localUser);
			orderGoods.setLast_updated_time(new Date());
			orderGoodsDao.insert(orderGoods);
			orderGoodsId = orderGoods.getOrder_goods_id();
			effectRows = shipmentDao.updatePackBoxStatus(shipmentId, productId,
					'N', new Date(), localUser);
			Assert.isTrue(effectRows > 0, "订单耗材添加失败, orderId:" + orderId);

			ShipmentDetail shipmentDetail = new ShipmentDetail();
			shipmentDetail.setShipment_id(shipmentId);
			shipmentDetail.setGoods_name(productName);
			shipmentDetail.setGoods_number(1);
			shipmentDetail.setOrder_goods_id(orderGoodsId);
			shipmentDetail.setProduct_id(Integer.parseInt(productId));
			shipmentDetail.setSerial_number("");
			shipmentDetailDao.insert(shipmentDetail);

			UserActionShipment userActionShipment = new UserActionShipment();
			userActionShipment.setShipment_id(shipmentId);
			userActionShipment.setStatus(shipmentInfo.get("status").toString());
			userActionShipment.setAction_type("bind_consume");
			userActionShipment.setAction_note("bind_consume");
			userActionShipment.setCreated_user(localUser);
			userActionShipment.setCreated_time(new Date());
			userActionShipmentDao
					.insertUserActionShipmentRecord(userActionShipment);

			effectRows = orderProcessDao.setStatusRecheckedByOrderId(orderId);
			Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);

		} catch (RuntimeException e) {
			throw new RuntimeException("发货单" + shipmentId + " 绑定耗材时，数据更新出错");
		}
		// 4. return
		resMap.put("result", "success");
		resMap.put("goods_name", productName);
		resMap.put("order_goods_id", orderGoodsId);
		resMap.put("product_type", "PACK");
		resMap.put("is_serial", "N");
		resMap.put("barcode", barcode);
		resMap.put("goods_number", "1");

		return resMap;
	}


	public Map<String, Object> createShipmentTrackingNumber(Integer orderId,
			Integer shippingId, String trackingNumber, String mark,
			String orderStatus) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		List<Shipment> shipmentList = shipmentDao.selectByOrderId(orderId);
		Integer shipmentOrderSequenceNum = shipmentList.size();
		String action = "APPLY";
		if (shipmentOrderSequenceNum > 0) {
			action = "ADD";
		}
		try {
			String localUser = (String) SecurityUtils.getSubject()
					.getPrincipal();
			Shipment s = new Shipment();
			s.setOrder_id(orderId);
			s.setStatus("INIT");
			s.setShipping_id(shippingId);
			s.setCreated_user(localUser);
			s.setCreated_time(new Date());
			s.setLast_updated_user(localUser);
			s.setLast_updated_time(new Date());
			s.setShipment_order_sequence_number(shipmentOrderSequenceNum + 1);
			s.setTracking_number(trackingNumber);
			s.setMark(mark);
			shipmentDao.createShipmentTrackingNumber(s);
			Integer shipment_id = s.getShipment_id();
			UserActionShipment userActionShipment = new UserActionShipment();
			userActionShipment.setAction_note("CREATE_SHIPMENT");
			userActionShipment.setAction_type("CREATE_SHIPMENT");
			userActionShipment.setCreated_user(localUser);
			userActionShipment.setCreated_time(new Date());
			userActionShipment.setShipment_id(shipment_id);
			userActionShipment.setStatus("INIT");
			userActionShipmentDao
					.insertUserActionShipmentRecord(userActionShipment);
			UserActionShipment userActionShipment2 = new UserActionShipment();
			userActionShipment2.setAction_note(action + "_MAILNOS:"
					+ trackingNumber);
			userActionShipment2.setAction_type(action + "_MAILNOS");
			userActionShipment2.setCreated_user(localUser);
			userActionShipment2.setCreated_time(new Date());
			userActionShipment2.setShipment_id(shipment_id);
			userActionShipment2.setStatus("INIT");
			userActionShipmentDao
					.insertUserActionShipmentRecord(userActionShipment2);
			resMap.put("result", Response.SUCCESS);
		} catch (Exception e) {
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "订单" + orderId + "更新快递面单失败");
		}

		return resMap;
	}

	@Override
	public Map selectPrintInfo(Integer orderId, String trackingNumber) {
		Map<String, Object> printInfo = new HashMap<String, Object>();
		List<Map<String, Object>> orderInfoList = orderProcessDao.selectPrintInfo(orderId,trackingNumber);
		printInfo.put("order_info_list", orderInfoList);
		return printInfo;
	}

	@Override
	public Integer getOrderDispatchNum(Integer orderId) {
		//增加限制：如果config表中config_value='Y'的仓库业务组合为null ，则禁止进入
		Map<String,Object> configValue = configDao.checkSuperRecheckConfigForOrder(orderId);
		String customerName = WorkerUtil.isNullOrEmpty(configValue)?"未配置":String.valueOf(configValue.get("brand_name"));
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(configValue) &&
				"Y".equalsIgnoreCase(String.valueOf(configValue.get("config_value"))),"订单不允许操作超级复核（"+customerName+"）");
		// 1. 获取订单所有商品信息(包含耗材商品)
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(orderId);
		Assert.isTrue(!OrderInfo.ORDER_STATUS_CANCEL.equalsIgnoreCase(String.valueOf(orderInfo.getOrder_status())), 
				"订单"+ orderId + "已取消！");
		// 2. 判断订单是否为需要扫描物流码
		List<String> needTransferCode = orderGoodsDao.checkOrderGoodsNeedTransferCode(orderInfo.getOrder_id());
		if(needTransferCode.size()>1 || needTransferCode.get(0).equalsIgnoreCase("Y")){
			throw new RuntimeException("订单中包含需要扫描物流码的商品，不允许超级复核");
		}
		Integer pageLine = configPrintDispatchBillDao.getOrderDispatchNum(orderId);
		List<OrderGoods> orderGoodsList = orderGoodsDao.selectByOrderId(orderId);
		Integer orderGoodsLine = orderGoodsList.size();
		Integer dispatchPage = orderGoodsLine/pageLine + (orderGoodsLine%pageLine==0?0:1);
		return dispatchPage;
	}

	@Override
	public Integer addFromToTN(Integer physicalWarehouseId, String shippingCode,
			String fromTrackingNumber, String toTrackingNumber) {
		Shipping shipping = shippingDao.selectByShippingCode(shippingCode);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(shipping), "根据快递CODE："+shippingCode+"没有找到快递信息");
		Integer shippingAppId = shippingWarehouseMappingDao.selectShippingAppIdOne(shipping.getShipping_id(),physicalWarehouseId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(shippingAppId) && !shippingAppId.equals(0) , "根据快递CODE与物理ID没有找到热敏+线下批量导入设置信息，请检查配置！");
		Long trackingNumber = Long.parseLong(fromTrackingNumber);
//		//System.out.println("trackingNumber : "+trackingNumber);
		Integer i = 0;
		while(true){
			Long tempTrackingNumber = trackingNumber+3000;
			if(trackingNumber.compareTo(Long.parseLong(toTrackingNumber))>0){
				break;
			}else if(tempTrackingNumber.compareTo(Long.parseLong(toTrackingNumber))>0){
				tempTrackingNumber = Long.parseLong(toTrackingNumber);
			}else if(tempTrackingNumber.compareTo(Long.parseLong(toTrackingNumber))==0){
				break;
			}
//			logger.info("addFromToTN :tempTrackingNumber_"+tempTrackingNumber);
			List<ShippingTrackingNumberRepository> shippingTrackingNumberRepositoryList = new ArrayList<ShippingTrackingNumberRepository>();
			while(true){
				if(trackingNumber.compareTo(tempTrackingNumber)>0){
					trackingNumber = tempTrackingNumber+1;
					break;
				}else{
					ShippingTrackingNumberRepository shippingTrackingNumberRepository = new ShippingTrackingNumberRepository();
					shippingTrackingNumberRepository.setCreated_time(new Date());
					shippingTrackingNumberRepository.setCreated_user("SYSTEM");
					shippingTrackingNumberRepository.setShipping_app_id(shippingAppId);
					shippingTrackingNumberRepository.setStatus("N");
					shippingTrackingNumberRepository.setTracking_number(trackingNumber+"");
					shippingTrackingNumberRepositoryList.add(shippingTrackingNumberRepository);
					trackingNumber++;
					i++;
				}
			}
			
			try{
				if(!WorkerUtil.isNullOrEmpty(shippingTrackingNumberRepositoryList)){
					shippingTrackingNumberRepositoryDao.batchInsertTnRepository(shippingTrackingNumberRepositoryList);
				}
			}catch (Exception e) {
				logger.info("addFromToTN ERROR :"+e.getStackTrace());
				throw new RuntimeException("批量插入失败！也许有重复单号,请查数据先确认~");
			}
		}
		return i;
	}

	@Override
	public List<Map> exportTNsForBack(Integer physicalWarehouseId,String shippingCode) {
		Shipping shipping = shippingDao.selectByShippingCode(shippingCode);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(shipping), "根据快递CODE："+shippingCode+"没有找到快递信息");
		Integer shippingAppId = shippingWarehouseMappingDao.selectShippingAppIdOne(shipping.getShipping_id(),physicalWarehouseId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(shippingAppId) && !shippingAppId.equals(0) , "根据快递CODE与物理ID没有找到热敏+线下批量导入设置信息，请检查配置！");
		//slave 查询待导出数据 2000条
		List<Map> tnsForFinishList = reportDaoSlave.selectTnsForFinishList(shippingAppId);
		List<String> trackingNumbers = new ArrayList<String>();
		for (Map tnsForFinishMap : tnsForFinishList) {
			 trackingNumbers.add(String.valueOf(tnsForFinishMap.get("tracking_number")));
		}
		//master 更新号段状态为F 
		shippingTrackingNumberRepositoryDao.updateRepositoryTrackingNumbers(trackingNumbers, "F");
		return tnsForFinishList;
	}

}
