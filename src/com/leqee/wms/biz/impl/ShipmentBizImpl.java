package com.leqee.wms.biz.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.biz.HTBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.SFBiz;
import com.leqee.wms.biz.ShipmentBiz;
import com.leqee.wms.biz.YDBiz;
import com.leqee.wms.biz.YTOBiz;
import com.leqee.wms.biz.ZTOBiz;
import com.leqee.wms.dao.BatchPickDao;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.OrderGoodsBatchDao;
import com.leqee.wms.dao.OrderGoodsDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.PalletDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.ShipmentDetailDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.ShippingTrackingNumberRepositoryDao;
import com.leqee.wms.dao.ShippingWarehouseMappingDao;
import com.leqee.wms.dao.ShippingYundaTrackingNumberDao;
import com.leqee.wms.dao.ShippingZtoMarkDao;
import com.leqee.wms.dao.UserActionBatchPickDao;
import com.leqee.wms.dao.UserActionShipmentDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.dao.UserActionOrderDao;
import com.leqee.wms.entity.OrderProcess;
import com.leqee.wms.entity.Pallet;
import com.leqee.wms.entity.PalletShipmentMapping;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.Shipment;
import com.leqee.wms.entity.ShipmentDetail;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.UserActionBatchPick;
import com.leqee.wms.entity.UserActionOrder;
import com.leqee.wms.entity.UserActionShipment;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ShipmentBizImpl implements ShipmentBiz {

	private static final String RECHECK_NEED_BATCH = "RECHECK_NEED_BATCH";

	private Logger logger = Logger.getLogger(ShipmentBizImpl.class);

	@Autowired
	PalletDao palletDao;
	@Autowired
	BatchPickDao batchpickDao;
	@Autowired
	UserActionBatchPickDao userActionBatchPickDao;
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
	InventoryDao inventoryDao;
	@Autowired
	ShippingTrackingNumberRepositoryDao shippingTrackingNumberRepositoryDao;
	@Autowired
	ShippingWarehouseMappingDao shippingWarehouseMappingDao;
	@Autowired
	ShipmentDetailDao shipmentDetailDao;
	@Autowired
	UserActionShipmentDao userActionShipmentDao;
	@Autowired
	ShippingZtoMarkDao shippingZtoMarkDao;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	@Autowired
	ShippingYundaTrackingNumberDao shippingYundaTrackingNumberDao;
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
	OrderBiz orderBiz;
	@Autowired
	BatchPickDao batchPickDao;
	@Autowired
	ShippingDao shippingDao;
	@Autowired
	OrderGoodsBatchDao orderGoodsBatchDao;
	@Autowired
	ConfigDao configDao;

	private Cache<String, String> isWeighPackBoxCache;
	
	@Override
	public Shipment getShipmentByTrackingNumber(String trackingNumber) {
		return shipmentDao.getShipmentByTrackingNumber(trackingNumber);
	}

	@Override
	public void setShipmentPackageWeight(BigDecimal shipping_wms_weight,
			String tracking_number,String barcode) {
		logger.info("ShipmentBizImpl setShipmentPackageWeight shipping_wms_weight:"
				+ shipping_wms_weight + " tracking_number" + tracking_number + " barcode" + barcode);
		String localUser = (String) SecurityUtils.getSubject().getPrincipal();
		// 查询对应订单 包含的所有包裹（除当前这个）是否均已称重，如果称重则更新订单状态
		Map<String, Object> tnsNotWeight = shipmentDao
				.getTnsNotWeight(tracking_number);
		Map<String, Object> orderInfoMap = shipmentDao
				.getOrderInfoByTn(tracking_number);
		Integer orderId = Integer.parseInt(orderInfoMap.get("order_id")
				.toString());
		Shipment ship = shipmentDao.getShipmentByTrackingNumber(tracking_number);
		Assert.isTrue("INIT".equalsIgnoreCase(ship.getStatus()) ||"WEIGHED".equalsIgnoreCase(ship.getStatus()) ,"包裹状态不允许称重！" );
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKeyForUpdate(orderId);
		Assert.isTrue(OrderInfo.ORDER_STATUS_RECHECKED.equalsIgnoreCase(String
				.valueOf(orderInfo.getOrder_status())) || OrderInfo.ORDER_STATUS_WEIGHED.equalsIgnoreCase(String
						.valueOf(orderInfo.getOrder_status())), "订单" + orderId
				+ "状态不是“已复核”或者“已称重”，不能操作称重");
		

		// 验证耗材 by hzhang1
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		isWeighPackBoxCache = cacheManager.getCache("isWeighPackBoxCache");
		String isWeighPackBox=isWeighPackBoxCache.get(currentPhysicalWarehouse.getPhysical_warehouse_id().toString());
		if(isWeighPackBox == null){
			isWeighPackBox = configDao.getConfigValueByFrezen(currentPhysicalWarehouse.getPhysical_warehouse_id(), 0, "IS_WEIGH_PACKBOX");
			isWeighPackBoxCache.put(currentPhysicalWarehouse.getPhysical_warehouse_id().toString(), isWeighPackBox);
		}
		
		if("Y".equals(isWeighPackBox)){
			Map productInfo = productDao.selectByBarodeForConsume(barcode.trim(),orderInfo.getCustomer_id().toString());
			Assert.isTrue(!WorkerUtil.isNullOrEmpty(productInfo), "条码" + barcode
					+ " 未找到对应耗材信息");
			Assert.isTrue(OrderGoods.ORDER_GOODS_TYPE_PACKBOX
					.equalsIgnoreCase(productInfo.get("product_type").toString()),
					"条码" + barcode + " 并不是耗材");
			String barcode_customer_id = productInfo.get("customer_id").toString();
			String productId = productInfo.get("product_id").toString();
			String productName = productInfo.get("product_name").toString();
			ShipmentDetail shipmentDetail = shipmentDetailDao.selectShipmentDetailForPackBox(ship.getShipment_id());
			if(!WorkerUtil.isNullOrEmpty(shipmentDetail)){
				int effectRows = shipmentDetailDao.updateShipmentDetailForPackBox(shipmentDetail.getShipment_detail_id(), productName, Integer.parseInt(productId));
				effectRows = orderGoodsDao.updateOrderGoodsForPackBox(shipmentDetail.getOrder_goods_id(), Integer.parseInt(productId), productName);
				Assert.isTrue(effectRows > 0, "称重时更新耗材信息失败, orderId:" + orderId);
			}else{
				OrderGoods orderGoods = new OrderGoods();
				orderGoods.setOrder_id(orderId);
				orderGoods.setOms_order_goods_sn("");
				orderGoods.setWarehouse_id(orderInfo.getWarehouse_id());
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
				Integer orderGoodsId = orderGoods.getOrder_goods_id();
				int effectRows = shipmentDao.updatePackBoxStatus(ship.getShipment_id(), productId,
						'N', new Date(), localUser);
				Assert.isTrue(effectRows > 0, "订单耗材添加失败, orderId:" + orderId);
	
				ShipmentDetail shipmentDetailInsert = new ShipmentDetail();
				shipmentDetail.setShipment_id(ship.getShipment_id());
				shipmentDetail.setGoods_name(productName);
				shipmentDetail.setGoods_number(1);
				shipmentDetail.setOrder_goods_id(orderGoodsId);
				shipmentDetail.setProduct_id(Integer.parseInt(productId));
				shipmentDetail.setSerial_number("");
				shipmentDetailDao.insert(shipmentDetail);
	
				UserActionShipment userActionShipment = new UserActionShipment();
				userActionShipment.setShipment_id(ship.getShipment_id());
				userActionShipment.setStatus(ship.getStatus());
				userActionShipment.setAction_type("bind_consume");
				userActionShipment.setAction_note("bind_consume");
				userActionShipment.setCreated_user(localUser);
				userActionShipment.setCreated_time(new Date());
				userActionShipmentDao.insertUserActionShipmentRecord(userActionShipment);
				
			}
			
			shipmentDao.setShipmentPackageWeight(shipping_wms_weight,
					tracking_number,Integer.parseInt(productId), localUser);// 更新称重重量，同时 修改状态为“WEIGHED”
		}else{
			shipmentDao.setShipmentPackageWeight(shipping_wms_weight,
					tracking_number,ship.getPackbox_product_id(), localUser);// 更新称重重量，同时 修改状态为“WEIGHED”
		}
		
		

		// 记录shipment表的操作记录
		Shipment shipment = shipmentDao
				.getShipmentByTrackingNumber(tracking_number);
		UserActionShipment userActionShipment = new UserActionShipment();
		userActionShipment.setShipment_id(shipment.getShipment_id());
		userActionShipment.setStatus(shipment.getStatus());
		userActionShipment.setAction_type("WEIGHING");
		userActionShipment.setAction_note("WEIGHING" + shipping_wms_weight);
		userActionShipment.setCreated_user(localUser);
		userActionShipment.setCreated_time(new Date());

		userActionShipmentDao
				.insertUserActionShipmentRecord(userActionShipment);

		UserActionOrder userActionOrder = new UserActionOrder();
		userActionOrder.setAction_note("包裹（快递单号：" + tracking_number + "）已称重");
		userActionOrder.setAction_type("WEIGHING");
		userActionOrder.setCreated_time(new Date());
		userActionOrder.setCreated_user(localUser);
		userActionOrder.setOrder_id(shipment.getOrder_id());
		userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_WEIGHED);
		userActionOrderDao.insert(userActionOrder);
		if (Integer.parseInt(String.valueOf(tnsNotWeight.get("coun"))) == 0) {
			// 更新订单状态

			Map<String, Object> paramsForOrderInfoUpdate = new HashMap<String, Object>();
			paramsForOrderInfoUpdate.put("orderStatus",
					OrderInfo.ORDER_STATUS_WEIGHED);
			paramsForOrderInfoUpdate.put("lastUpdatedUser", localUser);
			paramsForOrderInfoUpdate.put("lastUpdatedTime", new Date());
			paramsForOrderInfoUpdate.put("orderId", orderId);
			int effectRows = orderInfoDao
					.updateOrderStatusByOrderId(paramsForOrderInfoUpdate);
			Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);

			UserActionOrder userActionOrder2 = new UserActionOrder();
			userActionOrder2.setOrder_id(orderId);
			userActionOrder2.setOrder_status(OrderInfo.ORDER_STATUS_WEIGHED);
			userActionOrder2.setAction_type(OrderInfo.ORDER_STATUS_WEIGHED);
			userActionOrder2.setAction_note("订单完成称重");
			userActionOrder2.setCreated_user(localUser);
			userActionOrder2.setCreated_time(new Date());
			effectRows = userActionOrderDao.insert(userActionOrder2);
			Assert.isTrue(effectRows > 0, "订单操作记录插入失败, orderId:" + orderId);

			effectRows = orderProcessDao.setStatusWeigt(orderId);
			Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);
		}

	}

	@Override
	public String getOrderStatusByTrackingNumber(String tracking_number) {
		Map<String, Object> map = shipmentDao.getOrderStatusByTrackingNumber(tracking_number);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(map),"根据快递单号未搜索到订单信息!");
		Assert.isTrue(!"BATCH".equalsIgnoreCase(map.get("batch_process_type").toString()),"订单属于批量单，请使用波次单号批量称重！");
		Assert.isTrue(!OrderInfo.ORDER_STATUS_CANCEL.equalsIgnoreCase(map.get("status").toString()),"对应订单"+map.get("order_id")+"已被取消！");
		Assert.isTrue(OrderInfo.ORDER_STATUS_RECHECKED.equalsIgnoreCase(map.get("status").toString()) || 
				OrderInfo.ORDER_STATUS_WEIGHED.equalsIgnoreCase(map.get("status").toString()),
				"订单当前状态不是“已复核”或“已称重”，不能操作称重！");
		Assert.isTrue("INIT".equalsIgnoreCase(map.get("s_status").toString()) ||
				"WEIGHED".equalsIgnoreCase(map.get("s_status").toString()),"包裹状态不允许称重！");
		return String.valueOf(map.get("status"));
	}

	public HashMap<String, Object> getTrayBarcodeForMT(Integer number) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		ArrayList<String> MTCodelist = new ArrayList<String>();

		for (int i = 0; i < number; i++) {

			String tag = WorkerUtil.generatorSequence(
					SequenceUtil.KEY_NAME_TAGCODE, "MT", true);
			MTCodelist.add(tag);
			logger.info("shippment getTrayBarcodes WorkerUtil.generatorSequence tag:"
					+ tag);
		}

		if (WorkerUtil.isNullOrEmpty(MTCodelist) || MTCodelist.size() != number) {

			resultMap.put("error", "生成码托条码异常");
			resultMap.put("success", false);
			return resultMap;
		}

		// 检查数据库中是否已经存在相同的码托条码
		int sameCodeNum = shipmentDao.getSameMTCodeNum(MTCodelist);

		if (sameCodeNum > 0) {

			resultMap.put("error", "生成码托条码异常，条码已经存在过");
			resultMap.put("success", false);
			return resultMap;
		}

		String username = (String) SecurityUtils.getSubject().getPrincipal();

		if (WorkerUtil.isNullOrEmpty(username)) {

			resultMap.put("error", "检查是否登录或者会话已过期，重新登录");
			resultMap.put("success", false);
			return resultMap;
		}

		// 保存生成的码托条码

		for (String MTCode : MTCodelist) {

			Pallet pallet = new Pallet();
			pallet.setCreated_time(new Date());
			pallet.setCreated_user(username);
			pallet.setPallet_no(MTCode);
			pallet.setShip_status("init");
			pallet.setShipping_id(0);
			shipmentDao.insertMTcode(pallet);
		}

		resultMap.put("res", MTCodelist.toArray());
		resultMap.put("success", true);

		return resultMap;
	}

	public Pallet selectPalletByPalletSn(String pallet_sn) {
		return shipmentDao.selectPalletByPalletSn(pallet_sn);
	}

	public List<Map> getBindListByByPalletSn(String pallet_sn) {
		return shipmentDao.getBindListByByPalletSn(pallet_sn);
	}

	public Map getOrderStautsByTrackingNo(String tracking_no) {
		// TODO Auto-generated method stub
		return shipmentDao.getOrderStautsByTrackingNo(tracking_no);
	}

	public HashMap<String, Object> getPackageBindMapping(String tracking_no,
			String pallet_sn) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String username = (String) SecurityUtils.getSubject().getPrincipal();
		
		Pallet pallet = palletDao.selectPalletBySnForUpdate(pallet_sn);
		if (WorkerUtil.isNullOrEmpty(pallet)) {
			resultMap.put("error", "码托条码不存在！");
			resultMap.put("success", false);
			resultMap.put("error_id", 1);
			return resultMap;
		} else if ("SHIPPED".equals(pallet.getShip_status())) {
			resultMap.put("error", "该托盘条码已经发货");
			resultMap.put("success", false);
			resultMap.put("error_id", 1);
			return resultMap;
		}
		
		shipmentDao.selectShipmentByTrackingNoForUpdate(tracking_no);
		
		// 记录shipment表的操作记录
		Shipment shipment = shipmentDao
				.getShipmentByTrackingNumber(tracking_no);
		UserActionShipment userActionShipment = new UserActionShipment();
		userActionShipment.setShipment_id(shipment.getShipment_id());
		userActionShipment.setStatus(shipment.getStatus());
		userActionShipment.setAction_type("BIND_PALLET");
		userActionShipment.setAction_note("BIND_PALLET包裹绑定至码托（码托条码：" + pallet_sn +"）");
		userActionShipment.setCreated_user(username);
		userActionShipment.setCreated_time(new Date());

		userActionShipmentDao
				.insertUserActionShipmentRecord(userActionShipment);

		UserActionOrder userActionOrder = new UserActionOrder();
		userActionOrder.setAction_note("包裹（快递单号：" + tracking_no + "）绑定至码托（码托条码：" + pallet_sn +"）");
		userActionOrder.setAction_type("BIND_PALLET");
		userActionOrder.setCreated_time(new Date());
		userActionOrder.setCreated_user(username);
		userActionOrder.setOrder_id(shipment.getOrder_id());
		userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_WEIGHED);
		userActionOrderDao.insert(userActionOrder);		
		
		List<Map> list = shipmentDao
				.getPalletShipmentMappingByTrackingNo(tracking_no);

		// 往pallet表里面插入physicalWarehouseId
		Integer physicalWarehouseId = shipmentDao
				.getOrderPhysicalWarehouseIdByTrackingNumber(tracking_no);
		if (pallet.getPhysical_warehouse_id() == 0
				|| pallet.getPhysical_warehouse_id() - physicalWarehouseId == 0) {
			Map<String, Object> updateMap = new HashMap<String, Object>();
			updateMap.put("physicalWarehouseId", physicalWarehouseId);
			updateMap.put("palletNo", pallet_sn);
			shipmentDao.updatePalletPhysicalWarehouseId(updateMap);
			logger.info(">>>Update pallet physical_warehouse_id");
		} else {
			throw new RuntimeException("更新码托条码物理仓失败");
		}

		if (WorkerUtil.isNullOrEmpty(list)) {
			resultMap = doBindPalletAndShipmentWork(tracking_no, pallet_sn,
					username);
			return resultMap;
		} else {
			Map mappingMap = list.get(0);

			// Map mappingMap =
			// shipmentDao.getPalletShipmentMappingByTrackingNo(tracking_no);

			if (!WorkerUtil.isNullOrEmpty(mappingMap)
					&& mappingMap.get("bind_status").equals("BINDED")) {

				resultMap.put(
						"error",
						"运单号" + tracking_no + "已经与"
								+ mappingMap.get("pallet_no") + "绑定！");
				resultMap.put("success", false);
				resultMap.put("error_id", 1);

				return resultMap;
			}

			for (Map map : list) {
				if (!WorkerUtil.isNullOrEmpty(map)
						&& map.get("bind_status").equals("UNBINDED")
						&& map.get("pallet_no").equals(pallet_sn)) {

					resultMap.put("error", "运单号" + "tracking_no" + "已经与"
							+ mappingMap.get("pallet_no")
							+ "解绑，不能在同一个码托上解绑再绑定！");
					resultMap.put("success", false);
					resultMap.put("error_id", 1);

					return resultMap;
				}
			}

			resultMap = doBindPalletAndShipmentWork(tracking_no, pallet_sn,
					username);
			return resultMap;
		}

	}

	private HashMap<String, Object> doBindPalletAndShipmentWork(
			String tracking_no, String pallet_sn, String username) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		Integer bindNumInteger = shipmentDao.getBindNumByPalletNo(pallet_sn);

		if (bindNumInteger == 0) {

			// 运单号存在，初次绑定
			Map shippingInfoMap = shipmentDao
					.getShippingInfoByTrackingNo(tracking_no);

			if (WorkerUtil.isNullOrEmpty(shippingInfoMap)) {

				resultMap.put("error", "获取快递方式为空");
				resultMap.put("success", false);
				resultMap.put("error_id", 1);

				return resultMap;
			}

			PalletShipmentMapping palletShipmentMapping = new PalletShipmentMapping();

			palletShipmentMapping.setBind_status("BINDED");
			palletShipmentMapping.setBind_time(new Date());
			palletShipmentMapping.setPallet_no(pallet_sn);

			Long ship_id = (Long) shippingInfoMap.get("shipment_id");
			palletShipmentMapping.setShipment_id(ship_id.intValue());
			palletShipmentMapping.setBind_user(username);

			shipmentDao.insertpalletShipmentMapping(palletShipmentMapping);
			shipmentDao.updatePalletShippingIdByPalletNo(
					shippingInfoMap.get("shipping_id").toString(), pallet_sn);

			List<Map> bindList = getBindListByByPalletSn(pallet_sn);
			resultMap.put("success", true);
			resultMap.put("num", bindList.size());
			resultMap.put("way", bindList.get(0).get("shipping_name"));
			resultMap.put("ship", bindList.toArray());

			return resultMap;

		} else {

			Integer shippingIdOne = shipmentDao
					.getShippingIdByTrackingNo(tracking_no);

			Integer shippingIdTwo = shipmentDao
					.getShippingIdByPalletSn(pallet_sn);

			if (WorkerUtil.isNullOrEmpty(shippingIdOne)
					|| WorkerUtil.isNullOrEmpty(shippingIdTwo)) {

				resultMap.put("error", "获取不到运单或者码托的快递方式");
				resultMap.put("success", false);
				resultMap.put("error_id", 1);

				return resultMap;
			}

			if (shippingIdOne == shippingIdTwo) {

				Integer shipmentId = shipmentDao
						.getShipmentIdBytrackingNo(tracking_no);
				PalletShipmentMapping palletShipmentMapping = new PalletShipmentMapping();

				palletShipmentMapping.setBind_status("BINDED");
				palletShipmentMapping.setBind_time(new Date());
				palletShipmentMapping.setPallet_no(pallet_sn);
				palletShipmentMapping.setShipment_id(shipmentId);
				palletShipmentMapping.setBind_user(username);

				shipmentDao.insertpalletShipmentMapping(palletShipmentMapping);

				List<Map> bindList = getBindListByByPalletSn(pallet_sn);
				resultMap.put("success", true);
				resultMap.put("num", bindList.size());
				resultMap.put("way", bindList.get(0).get("shipping_name"));
				resultMap.put("ship", bindList.toArray());

				return resultMap;

			} else {

				resultMap.put("error", "运单快递与码托现有运单快递不符");
				resultMap.put("success", false);
				resultMap.put("error_id", 2);

				return resultMap;

			}
		}
	}

	public Map getShippingInfoByTrackingNoForUnBind(String tracking_no) {
		return shipmentDao.getShippingInfoByTrackingNoForUnBind(tracking_no);
	}

	public HashMap<String, Object> getUnbindShipmentInfoByTrackingNo(
			String tracking_no) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String username = (String) SecurityUtils.getSubject().getPrincipal();

		Map infoMap = shipmentDao
				.getShipmentStatusForUnbindByTrackingNo(tracking_no);

		if (!WorkerUtil.isNullOrEmpty(infoMap)
				&& (infoMap.get("status").equals("DELIVERED") || infoMap.get(
						"shipment_status").equals("SHIPED"))) {

			resultMap.put("error", "解绑失败，该运单号对应的订单已经发货！");
			resultMap.put("success", false);
			resultMap.put("error_id", 2);

			return resultMap;
		}

		String pallet_no = (String) infoMap.get("pallet_no").toString();
		Long shipment_id = (Long) infoMap.get("shipment_id");
		
		Pallet pallet = palletDao.selectPalletBySnForUpdate(pallet_no);
		if ("SHIPPED".equalsIgnoreCase(pallet.getShip_status())) {
			resultMap.put("error", "该运单对应码托已经发货");
			resultMap.put("success", false);
			return resultMap;
		}
		
		// 记录shipment表的操作记录
		Shipment shipment = shipmentDao
				.getShipmentByTrackingNumber(tracking_no);
		UserActionShipment userActionShipment = new UserActionShipment();
		userActionShipment.setShipment_id(shipment.getShipment_id());
		userActionShipment.setStatus(shipment.getStatus());
		userActionShipment.setAction_type("UNBIND_PALLET");
		userActionShipment.setAction_note("UNBIND_PALLET包裹从码托（码托条码：" + pallet_no +"）解绑");
		userActionShipment.setCreated_user(username);
		userActionShipment.setCreated_time(new Date());

		userActionShipmentDao
				.insertUserActionShipmentRecord(userActionShipment);

		UserActionOrder userActionOrder = new UserActionOrder();
		userActionOrder.setAction_note("包裹（快递单号：" + tracking_no + "）从码托（码托条码：" + pallet_no +"）解绑");
		userActionOrder.setAction_type("UNBIND_PALLET");
		userActionOrder.setCreated_time(new Date());
		userActionOrder.setCreated_user(username);
		userActionOrder.setOrder_id(shipment.getOrder_id());
		userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_WEIGHED);
		userActionOrderDao.insert(userActionOrder);

		int bindNum = shipmentDao.getBindNumByPalletNo(pallet_no);

		if (bindNum == 1) {
			shipmentDao.updatePalletShippingIdByPalletNo(null, pallet_no);
		}

		shipmentDao.updatePalletShipMappingForUnbind(username,
				shipment_id.intValue());

		resultMap.put("success", true);

		return resultMap;
	}

	public Boolean deliverPallet(String pallet_sn) {
		Pallet pallet = palletDao.selectPalletBySnForUpdate(pallet_sn);
		Assert.notNull(pallet, "码托(码托No:" + pallet_sn + ")不存在!");
		Assert.isTrue("INIT".equalsIgnoreCase(pallet.getShip_status()), "码托(码托No:" + pallet_sn + ")已发货!");

		List<Map> bindList = getBindListByByPalletSnV2(pallet_sn);

		String username = (String) SecurityUtils.getSubject().getPrincipal();

		for (Map map : bindList) {

			Long shipment_id = (Long) map.get("shipment_id");
			Integer order_id = (Integer) map.get("order_id");
			String tracking_number = (String) map.get("tracking_number");
			
			OrderInfo orderInfo = orderInfoDao.selectByPrimaryKeyForUpdate(order_id);
			Assert.isTrue(!"CANCEL".equalsIgnoreCase(orderInfo.getOrder_status()), "运单号为"+tracking_number+"的包裹，所属订单为："+order_id+"已经取消,请解绑后再操作");

			Integer updateline = shipmentDao.updateShipmentStatusForShip(
					shipment_id.intValue(), username);
			if (WorkerUtil.isNullOrEmpty(updateline)) {
				logger.info("update shipment for ship fail shipment_id:"
						+ shipment_id.intValue());
				throw new RuntimeException(
						"update shipment for ship fail shipment_id:"
								+ shipment_id.intValue());
			}

			UserActionShipment userActionShipment = new UserActionShipment();
			userActionShipment.setStatus("SHIPPED");
			userActionShipment.setAction_note("包裹已发货");
			userActionShipment.setAction_type("SHIP");
			userActionShipment.setCreated_time(new Date());
			userActionShipment.setCreated_user(username);
			userActionShipment.setShipment_id(shipment_id.intValue());

			userActionShipmentDao
					.insertUserActionShipmentRecord(userActionShipment);
			
			UserActionOrder uao = new UserActionOrder();
			uao.setAction_note("码托（码托条码：" + pallet_sn + "）交接发货，包裹（快递单号：" + map.get("tracking_number").toString() +"）已发货");
			uao.setAction_type("SHIP");
			uao.setCreated_time(new Date());
			uao.setCreated_user(username);
			uao.setOrder_id(order_id.intValue());
			uao.setOrder_status(OrderInfo.ORDER_STATUS_DELEVERED);
			userActionOrderDao.insert(uao);

			Integer shipNum = shipmentDao
					.selectShipmentNotShipByShipmentId(shipment_id.intValue());

			if (WorkerUtil.isNullOrEmpty(shipNum) || shipNum == 0) {

				Map<String, Object> paramsForOrderInfoUpdate = new HashMap<String, Object>();
				paramsForOrderInfoUpdate.put("orderStatus",
						OrderInfo.ORDER_STATUS_DELEVERED);
				paramsForOrderInfoUpdate.put("lastUpdatedUser", username);
				paramsForOrderInfoUpdate.put("lastUpdatedTime", new Date());
				paramsForOrderInfoUpdate.put("orderId", order_id.intValue());

				Integer updateLine = orderInfoDao
						.updateOrderStatusShippingByOrderId(paramsForOrderInfoUpdate);
				if (WorkerUtil.isNullOrEmpty(updateLine)) {
					logger.info("update orderInfo for ship fail shipment_id:"
							+ shipment_id.intValue());
					throw new RuntimeException(
							"update order_info for ship fail order_id:"
									+ order_id);
				}

				Integer updateLine2 = orderInfoDao
						.updateOrderProcessStatusByOrderId(paramsForOrderInfoUpdate);
				if (WorkerUtil.isNullOrEmpty(updateLine2)) {
					logger.info("update orderProcess for ship fail shipment_id:"
							+ shipment_id.intValue());
					throw new RuntimeException(
							"update order_process for ship fail order_id:"
									+ order_id);
				}

				UserActionOrder userActionOrder = new UserActionOrder();
				userActionOrder
						.setAction_note("订单已发货");
				userActionOrder
						.setAction_type(OrderInfo.ORDER_STATUS_DELEVERED);
				userActionOrder.setCreated_time(new Date());
				userActionOrder.setCreated_user(username);
				userActionOrder.setOrder_id(order_id.intValue());
				userActionOrder
						.setOrder_status(OrderInfo.ORDER_STATUS_DELEVERED);
				userActionOrderDao.insert(userActionOrder);

			}
		}

		shipmentDao.updatePalletStatusForShip(pallet_sn, username);

		return true;
	}

	private List<Map> getBindListByByPalletSnV2(String pallet_sn) {
		return shipmentDao.getBindListByByPalletSnV2(pallet_sn);
	}

	/**
	 * (复核)加载商品 ytchen
	 */
	@Override
	public Map<String, Object> loadGoodsForOrder(Integer orderId) {
		Map<String, Object> resMap = new HashMap<String, Object>();
				
		String needBatchSn = configDao.getConfigValueByConfigName(RECHECK_NEED_BATCH);
		resMap.put("need_batch_sn", needBatchSn);
		
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
		// 3. 当前绑定信息查询(包含所有发货单信息，是否绑定耗材bind_pack,绑定商品类型product_type)
		List<Map> bindShipmentGoodsInfo = shipmentDao
				.selectBindShipmentGoodsInfoByOrderId(orderId);
		/*Assert.isTrue(
		!String.valueOf(orderGoodsInfo.get(0).get("batch_process_type")).equals("BATCH"), 
		"订单" + orderId + "为批量单（"+ orderGoodsInfo.get(0).get("batch_pick_sn")+ "），请使用批量复核入口操作");*/
		/**
		 * 若为批量单，则需要一系列操作判断+转化
		 */
		if(String.valueOf(orderGoodsInfo.get(0).get("batch_process_type")).equals("BATCH")){
			if(WorkerUtil.isNullOrEmpty(bindShipmentGoodsInfo) ||
					 WorkerUtil.isNullOrEmpty(bindShipmentGoodsInfo.get(0).get("order_goods_id")) 
				){
				Integer batchPickId = Integer.parseInt(String.valueOf(orderGoodsInfo.get(0).get("batch_pick_id")));
				/*
				 * 1. select batch_pick for update 
				 * 2. check type = BATCH , 若不等则跳出继续复核
				 * 3. 判断条件满足（所有订单状态统一在 PICKING,CANCEL中 ； 所有订单都没有任何包裹） ； 若不满足则报错
				 * 4. update type = NORMAL
				 */
				BatchPick batchPick = batchPickDao.selectbatchPickForUpdateLockV2(batchPickId);
				if("BATCH".equalsIgnoreCase(batchPick.getBatch_process_type())){
					List<Map<String,Object>> batchOrders = orderProcessDao.checkOrderByBatchPickIdForRecheck(batchPickId);
					Assert.isTrue(WorkerUtil.isNullOrEmpty(batchOrders), "订单属于批量波次，部分商品已经操作复核且绑定面单  请先去操作取消复核！");
					Integer col = batchPickDao.updateBatchProcessTypeAndRecheckMark(batchPickId);
					Assert.isTrue(col>0, "订单所属波次已批量操作，复核流程转换失败！");
				}
			}else{
				throw new RuntimeException("订单属于批量波次，部分商品已经操作复核且绑定面单  请先去操作取消复核！");
			}
		}
		
		//add 于16你那10月10日 判断is_first_shipment是否为1
		OrderProcess orderProcess = orderProcessDao.selectByPrimaryKey(orderId);
		Integer recheckType = orderProcess.getRecheck_type();
		Assert.isTrue(!recheckType.equals(2), "订单"+orderId+"已经操作超级复核！");
		if(recheckType.equals(0)){
			Integer col = orderProcessDao.updateRecheckType(orderId,recheckType,1);
			Assert.isTrue(col>0, "订单"+orderId+"选择复核流程状态更新失败！");
		}
		
		// 4. 查询未绑定商品信息
		List<Map> unbindGoodsInfo = orderInfoDao.getUnbindGoodsInfo(orderId);
		// 5. 根据绑定信息确定方案
		List<Map> bindGoodsInfo = new ArrayList<Map>();
		// 5.1 发货单不存在，或所有发货单均已绑定过耗材且存在未绑定商品信息
		// 需申请快递面单
		// ，返回result，已扫描（无商品只有trackingNumber，shipmentId），未扫描商品信息列表，及最新shipmentId
		if (WorkerUtil.isNullOrEmpty(bindShipmentGoodsInfo)
				|| (String.valueOf(
						bindShipmentGoodsInfo.get(0).get("bind_pack")).equals(
						"Y") && !WorkerUtil.isNullOrEmpty(unbindGoodsInfo))) {
			Integer shippingId = Integer.parseInt(String.valueOf(orderGoodsInfo
					.get(0).get("shipping_id")));
			// 订单同步时已经设定，排除非空可能
			// 创建发货单
			logger.info("订单" + orderId
					+ " has't create shipment,begin to create shipment");
			Integer shipmentId = 0;
			try {
				shipmentId = createShipment(orderId, shippingId);
			} catch (RuntimeException e) {
				logger.info("订单" + orderId + " create shipment failed!");
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
			} catch (RuntimeException e) {
				logger.info("更新运单号绑定关系失败！shipmentId:" + shipmentId
						+ " , trackingNumber :" + trackingNumber);
				throw new RuntimeException("更新运单号绑定关系失败！shipmentId:"
						+ shipmentId + " , trackingNumber :" + trackingNumber);
			}
			Integer isFirstShipment = orderProcess.getIs_first_shipment();
			if(isFirstShipment.equals(0)){
				orderInfoDao.updateTrackingNumber(orderId, trackingNumber);
				Integer col = orderProcessDao.updateIsFirstShipment(orderId,isFirstShipment,1);
				Assert.isTrue(col>0, "已经有其他方式申请面单成功了，请重新复核！");
			}
			bindGoodsInfo = formatBindShipmentGoodsInfo(bindShipmentGoodsInfo,
					shipmentId, trackingNumber);
			resMap.put("result", Response.SUCCESS);
			resMap.put("unbind_goods_info", unbindGoodsInfo);
			resMap.put("bind_goods_info", bindGoodsInfo);
			resMap.put("shipment_id", shipmentId);
		} else
		// 5.2 存在发货单，但最新发货单没有绑定耗材，直接返回 result，已扫描，未扫描商品信息列表，及最新shipmentId
		if (bindShipmentGoodsInfo.size() > 0
				&& bindShipmentGoodsInfo.get(0).get("bind_pack").toString()
						.equals("N")) {
			Integer shipmentId = Integer.parseInt(String
					.valueOf(bindShipmentGoodsInfo.get(0).get("shipment_id")));
			// 格式化 bindInfo
			bindGoodsInfo = formatBindShipmentGoodsInfo(bindShipmentGoodsInfo,
					0, "");
			resMap.put("result", Response.SUCCESS);
			resMap.put("bind_goods_info", bindGoodsInfo);
			resMap.put("unbind_goods_info", unbindGoodsInfo);
			resMap.put("shipment_id", shipmentId);
		}
		Map<String,Object> recheckNumMap = shipmentDao.selectRecheckNumMap(orderId);
		resMap.put("total_number", recheckNumMap.get("total_number"));
		resMap.put("unbind_number", recheckNumMap.get("unbind_number"));
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
			
			if (!WorkerUtil.isNullOrEmpty(bindGoods.get("order_goods_id")) && !WorkerUtil.isNullOrEmpty(String.valueOf(bindGoods.get("order_goods_id")))) {
				long orderGoodsId = Long.valueOf(String.valueOf(bindGoods.get("order_goods_id")));
				List<String> batchSnList = orderGoodsBatchDao.selectBatchSnsByOrderGoodsId(orderGoodsId);
				bindGoods.put("batch_sns", batchSnList);
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

	public Integer createShipmentForFirstShipment(Integer orderId, Integer shippingId) {
		String localUser = "SYSTEM";
		Shipment s = new Shipment();
		s.setOrder_id(orderId);
		s.setStatus("INIT");
		s.setShipping_id(shippingId);
		s.setCreated_user(localUser);
		s.setCreated_time(new Date());
		s.setLast_updated_user(localUser);
		s.setLast_updated_time(new Date());
		s.setShipment_order_sequence_number(1);
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

	public Integer createShipment(Integer orderId, Integer shippingId) {
		List<Shipment> shipmentList = shipmentDao.selectByOrderId(orderId);
		Integer shipmentOrderSequenceNum = shipmentList.size();
		String localUser = (String) SecurityUtils.getSubject().getPrincipal();
		if(WorkerUtil.isNullOrEmpty(localUser) || "".equalsIgnoreCase(localUser)){
			localUser = "SYSTEM";
		}
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
			localUser = "SYSTEM";
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
//					System.out
//							.println("订单" + orderId + "第" + i + "次抢占运单资源，失败！");
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
	 * (复核)绑定耗材
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
			if (WorkerUtil.isNullOrEmpty(unbindGoodsInfo) || unbindGoodsInfo.size()==0) {
				Map<String, Object> paramsForOrderInfoUpdate = new HashMap<String, Object>();
				paramsForOrderInfoUpdate.put("orderStatus",
						OrderInfo.ORDER_STATUS_RECHECKED);
				paramsForOrderInfoUpdate.put("lastUpdatedUser", localUser);
				paramsForOrderInfoUpdate.put("lastUpdatedTime", new Date());
				paramsForOrderInfoUpdate.put("orderId", orderId);
				effectRows = orderInfoDao
						.updateOrderStatusByOrderId(paramsForOrderInfoUpdate);
				Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);

				effectRows = orderProcessDao.setStatusRecheckedByOrderId(orderId);
				Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);

				UserActionOrder userActionOrder = new UserActionOrder();
				userActionOrder.setOrder_id(orderId);
				userActionOrder
						.setOrder_status(OrderInfo.ORDER_STATUS_RECHECKED);
				userActionOrder
						.setAction_type(OrderInfo.ORDER_STATUS_RECHECKED);
				userActionOrder.setAction_note("订单复核完成");
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

	/**
	 * (复核)追加面单
	 */
	@Override
	public Map<String, Object> addTrackingNumber(Integer orderId) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		// 1. 订单目前最后一个shipment信息获取
		Map<String, String> lastShipmentBindInfo = shipmentDao
				.selectLastShipmentBindInfo(orderId);
		// 1.2 最后一个shipmentId存在绑定关系，且不止耗材
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(lastShipmentBindInfo), "订单"
				+ orderId + " 未查询到绑定记录，不支持追加面单");
		String last_product_type = lastShipmentBindInfo
				.get("last_product_type");
		Assert.isTrue(
				!WorkerUtil.isNullOrEmpty(last_product_type)
						&& last_product_type
								.contains(OrderGoods.ORDER_GOODS_TYPE_GOODS),
				"订单" + orderId + " 当前包裹并没有商品绑定");
		Assert.isTrue(OrderInfo.ORDER_STATUS_PICKING.equalsIgnoreCase(String
				.valueOf(lastShipmentBindInfo.get("order_status"))), "订单"
				+ orderId + "状态并非“拣货中”，不能追加面单");
		// 2. 存在还未绑定商品
		List<Map> unbindGoodsInfo = orderInfoDao.getUnbindGoodsInfo(orderId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(unbindGoodsInfo), "订单"
				+ orderId + " 没有多余商品，无需再追加面单");

		// 3. 创建发货单 + 绑定面单 + 添加记录
		Integer shippingId = Integer.parseInt(String
				.valueOf(lastShipmentBindInfo.get("shipping_id")));
		logger.info("订单" + orderId + " begin to add shipment ");
		// 3.1 创建发货单
		Integer shipmentId = createShipment(orderId, shippingId);
		// 3.2 获取运单号
		Map<String, Object> traMap = applyMailno("ADD", orderId, shipmentId);
		if (Response.FAILURE.equals(traMap.get("result"))) {
			return traMap;
		}
		String trackingNumber = String.valueOf(traMap.get("tracking_number"));
		String mark = String.valueOf(traMap.get("mark"));
		// 4. 更新 绑定状态 + 添加 创建记录， 打印记录 与追加记录
		try {
			shipmentDao.updateTrackingNumber(shipmentId, trackingNumber, mark);
			if (traMap.get("origincode") != null) {
				String origincode = String.valueOf(traMap.get("origincode"));
				String destcode = String.valueOf(traMap.get("destcode"));
				shipmentDao.updateSfShipment(shipmentId, origincode, destcode);
			}
		} catch (RuntimeException e) {
			logger.info("更新运单号绑定关系失败！shipmentId:" + shipmentId
					+ " , trackingNumber :" + trackingNumber);
			throw new RuntimeException("更新运单号绑定关系失败！shipmentId:" + shipmentId
					+ " , trackingNumber :" + trackingNumber);
		}

		resMap.put("result", Response.SUCCESS);
		resMap.put("tracking_number", trackingNumber);
		resMap.put("shipment_id", shipmentId);
		return resMap;
	}

	public List<String> getWrongStatuslListForPalletShip(String pallet_no) {
		return shipmentDao.getWrongStatuslListForPalletShip(pallet_no);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Map> getPalletShipmentList(Map searchMap) {
		
		List<Integer> palletNoList=palletDao.selectPalletIdListNeedToShipmentByPage(searchMap);
		List<Map> new_palletShipmentList = new ArrayList<Map>();
		if(!WorkerUtil.isNullOrEmpty(palletNoList)){
			List<Map> palletShipmentList = palletDao
					.selectPalletShipmentListV2(palletNoList);
			String last_pallet_no = "";
			int count = 1, index = 0, size = palletShipmentList.size();
			
			List<String> trackingNumbers = new ArrayList<String>();
			Map last_map = null;
			for (Map<String, Object> map : palletShipmentList) {

				String now_pallet_no = map.get("pallet_no").toString();
				
				if ("".equals(last_pallet_no)) {
					trackingNumbers.add(map.get("tracking_number").toString());
					if (size == 1) {
						map.put("tracking_number_list", trackingNumbers);
						map.put("tracking_number_count", count);
						new_palletShipmentList.add(map);
						break;
					}
				} else if (last_pallet_no.equalsIgnoreCase(now_pallet_no)) {
					trackingNumbers.add(map.get("tracking_number").toString());
					count++;
				} else {
					last_map.put("tracking_number_list", trackingNumbers);
					last_map.put("tracking_number_count", count);
					new_palletShipmentList.add(last_map);
					trackingNumbers = new ArrayList<String>();
					trackingNumbers.add(map.get("tracking_number").toString());
					count = 1;
				}

				last_pallet_no = now_pallet_no;
				last_map = map;
				if (index == (size - 1)) {
					map.put("tracking_number_list", trackingNumbers);
					map.put("tracking_number_count", count);
					new_palletShipmentList.add(map);
				}
				index++;
			}			
		}
		return new_palletShipmentList;
	}

	public HashMap<String, Object> getUnbindShipmentInfoByTrackingNo2(
			String tracking_no) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String username = (String) SecurityUtils.getSubject().getPrincipal();

		Map infoMap = shipmentDao
				.getShipmentStatusForUnbindByTrackingNo(tracking_no);

		if (!WorkerUtil.isNullOrEmpty(infoMap)
				&& (infoMap.get("status").equals("DELIVERED") || infoMap.get(
						"shipment_status").equals("SHIPED"))) {

			resultMap.put("error", "解绑失败，该运单号对应的订单已经发货！");
			resultMap.put("success", false);
			resultMap.put("error_id", 2);

			return resultMap;
		}
		resultMap.put("success", true);
		resultMap.put("tracking_no", tracking_no);
		resultMap.put("pallet_sn", infoMap.get("pallet_no"));
		return resultMap;
	}

	public HashMap<String, Object> queryByPalletSn(String pallet_sn) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("pallet_sn", pallet_sn);
		resultMap.put("result", true);
		resultMap.put("success", true);

		int count = shipmentDao.findPalletSnInPallet(pallet_sn);
		if (count == 0) {
			resultMap.put("num", 0);
			resultMap.put("error", "无法识别的码托条码");
			resultMap.put("result", false);
			resultMap.put("success", false);
			return resultMap;
		}

		List<Map> list = shipmentDao.queryByPalletSn(pallet_sn);

		if (WorkerUtil.isNullOrEmpty(list)) {
			resultMap.put("num", 0);
			resultMap.put("shipping_name", "");
		} else {
			resultMap.put("num", list.size());
			resultMap.put("shipping_name", list.get(0).get("shipping_name"));
		}
		return resultMap;
	}

	public HashMap<String, Object> queryByTrackingNo(String tracking_no) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("tracking_no", tracking_no);
		resultMap.put("result", true);
		resultMap.put("success", true);

		int count = shipmentDao.findShipmentByTrackingNo(tracking_no);
		if (count == 0) {
			resultMap.put("pallet_sn", "");
			resultMap.put("error", "无法识别的运单号");
			resultMap.put("result", false);
			resultMap.put("success", false);
			return resultMap;
		}

		List<Map> list = shipmentDao.queryByTrackingNo(tracking_no);

		if (WorkerUtil.isNullOrEmpty(list)) {
			resultMap.put("pallet_sn", "");
			resultMap.put("shipping_name", "");
		} else {
			resultMap.put("pallet_sn", list.get(0).get("pallet_no"));
			resultMap.put("shipping_name", list.get(0).get("shipping_name"));
		}
		return resultMap;
	}

	@Override
	public Map selectPrintInfo(Integer orderId,String trackingNumber) {
		Map<String, Object> printInfo = new HashMap<String, Object>();
		List<Map<String, Object>> orderInfoList = orderProcessDao.selectPrintInfo(orderId,trackingNumber);
		printInfo.put("order_info_list", orderInfoList);
		return printInfo;
	}


	/**
	 * 问题单处理 - 删除面单 根据快递单号查订单+包裹商品信息
	 */
	@Override
	public Map<String, Object> loadGoodsForDeleteByTn(String trackingNumber) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> orderInfo = loadOrderInfoByTn(trackingNumber);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(orderInfo), "快递单号"
				+ trackingNumber + "没有匹配的订单信息");
		String orderStatus = orderInfo.get("status").toString();
		orderInfo.put("order_status",
				OrderInfo.ORDER_STATUS_MAP.get(orderStatus));
		String batchPickType = orderInfo.get("batch_process_type").toString();
		Assert.isTrue(!"BATCH".equalsIgnoreCase(batchPickType),"快递单所属订单是批量单，请使用“批量单波次单号”进行取消！");
		List<Map<String, Object>> TnsGoodsInfo = loadBindGoodsByTn(trackingNumber);
		Integer tnsGoodsNumber = 0;
		if (!WorkerUtil.isNullOrEmpty(TnsGoodsInfo)) {
			for (Map<String, Object> map : TnsGoodsInfo) {
				if(!"WMS888888888".equalsIgnoreCase(String.valueOf(map.get("barcode")))){
					tnsGoodsNumber = tnsGoodsNumber + Integer.parseInt(String.valueOf(map.get("goods_number")));
				}
			}
		}
		resultMap.put("result", Response.SUCCESS);
		resultMap.put("order_id", orderInfo.get("order_id"));
		resultMap.put("tracking_number", trackingNumber);
		resultMap.put("order_info", orderInfo);
		resultMap.put("tns_goods_info", TnsGoodsInfo);
		resultMap.put("tns_goods_number", tnsGoodsNumber);
		return resultMap;
	}

	private List<Map<String, Object>> loadBindGoodsByTn(String trackingNumber) {
		return shipmentDao.loadBindGoodsByTn(trackingNumber);
	}

	private Map<String, Object> loadOrderInfoByTn(String trackingNumber) {
		return shipmentDao.getOrderInfoByTn(trackingNumber);
	}

	/**
	 * 问题单处理 - 删除面单 根据订单号查订单信息
	 */
	@Override
	public Map<String, Object> loadGoodsForDeleteByOrderId(Integer orderId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> orderInfo = shipmentDao
				.loadGoodsForDeleteByOrderId(orderId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(orderInfo.get("order_id")),
				"订单号" + orderId + "没有匹配的订单信息");
		String batchPickType = orderInfo.get("batch_process_type").toString();
		Assert.isTrue(!"BATCH".equalsIgnoreCase(batchPickType),"快递单所属订单是批量单，请使用“批量单波次单号”进行取消！");
		String orderStatus = orderInfo.get("status").toString();
		String trackingNumbers = orderInfo.get("tracking_numbers").toString();
		Assert.isTrue(!"".equals(trackingNumbers),"订单"+orderId+"没有绑定的快递单号！");
		orderInfo.put("order_status",
				OrderInfo.ORDER_STATUS_MAP.get(orderStatus));
		resultMap.put("result", Response.SUCCESS);
		resultMap.put("order_id", orderId);
		resultMap.put("tracking_number", "");
		resultMap.put("order_info", orderInfo);
		resultMap.put("tns_goods_info", new ArrayList<Map<String, Object>>());
		resultMap.put("tns_goods_number", 0);
		return resultMap;
	}
	
	/**
	 * 问题单处理 - 删除面单 根据波次单号查订单信息
	 */
	@Override
	public Map<String, Object> loadBatchInfosForDeleteByBatchPickSn(
			String batchPickSn) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Integer orderNum = 0;
		String shippingNumStr = "";
		List<Map<String, Object>> batchOrderInfo = shipmentDao.loadBatchInfosForDeleteByBatchPickSn(batchPickSn);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(batchOrderInfo),"波次单号不是批量单，或不存在需要取消的快递单号！");
		for (Map<String, Object> map : batchOrderInfo) {
			String shippingName = map.get("shipping_name").toString();
			String trackNum = map.get("track_num").toString();
			Integer orderNumMap = Integer.parseInt(String.valueOf(map.get("order_num")));
			String groupShipmentStatus = map.get("group_shipment_status").toString();
			Assert.isTrue(("INIT".equalsIgnoreCase(groupShipmentStatus)), "波次单号存在已称重包裹，不允许取消复核！");
			orderNum = orderNum + orderNumMap;
			shippingNumStr = shippingNumStr + trackNum+"("+shippingName+")  ";
		}
		List<Map<String,Object>> batchOrderList = batchPickDao.selectBatchOrderListForCleanTns(batchPickSn);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(batchOrderList),"根据波次单号没有搜到需要删除的快递单号！");
		
		resultMap.put("result", Response.SUCCESS);
		resultMap.put("batch_pick_sn", batchPickSn);
		resultMap.put("order_num", orderNum);
		resultMap.put("shipping_num_str", shippingNumStr);
		return resultMap;
	}

	/**
	 * 问题单处理 - 删除面单 根据订单号 清空复核记录
	 */
	@Override
	public Map<String, Object> cleanOrderTns(Integer orderId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		OrderProcess orderProcess = orderProcessDao.selectByPrimaryKey(orderId);
		Assert.isTrue(orderProcess.getIs_first_shipment().equals(1), "订单"+orderId+"还没有申请面单，不需要取消复核！");
		Integer recheckType = orderProcess.getRecheck_type();
		Assert.isTrue(!recheckType.equals(0), "订单"+orderId+"还没操作过复核，不需要取消复核!");
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKeyForUpdate(orderId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(orderInfo), "订单号" + orderId
				+ "没有匹配的订单信息");
		Map<String, Object> orderProcessInfo = shipmentDao
				.loadGoodsForDeleteByOrderId(orderId);
		String batchPickType = orderProcessInfo.get("batch_process_type").toString();
		Assert.isTrue(!"BATCH".equalsIgnoreCase(batchPickType),"快递单所属订单是批量单，请使用“批量单波次单号”进行取消！");
		Assert.isTrue("PICKING".equalsIgnoreCase(orderProcessInfo.get("status").toString()) ||
				"RECHECKED".equalsIgnoreCase(orderProcessInfo.get("status").toString()),"订单状态不允许取消复核！");
		List<Shipment> shipmentList = shipmentDao.selectByOrderId(orderId);
		for (Shipment shipment : shipmentList) {
			Assert.isTrue(
					"INIT".equals(shipment.getStatus()),
					"快递单号" + shipment.getTracking_number() + "已称重，不可删除！");
		}
		String orderStatus = orderInfo.getOrder_status().toString();
		
		Assert.isTrue(
				(OrderInfo.ORDER_STATUS_PICKING.equals(orderStatus) || OrderInfo.ORDER_STATUS_RECHECKED
						.equals(orderStatus)), "订单号" + orderId + "所处状态不允许删除面单！");
		
		String needDelTransferCode = "N";
		List<String> needTransferCode = orderInfoDao.selectIsNeedTransferCode(orderId);
		if(!(needTransferCode.size()==0 && needTransferCode.get(0).equalsIgnoreCase("N"))){
			needDelTransferCode = "Y";
		}
		
		try {
			String localUser = (String) SecurityUtils.getSubject()
					.getPrincipal();
			Map<String, Object> paramsForOrderInfoUpdate = new HashMap<String, Object>();
			paramsForOrderInfoUpdate.put("orderStatus",
					OrderInfo.ORDER_STATUS_PICKING);
			paramsForOrderInfoUpdate.put("lastUpdatedUser", localUser);
			paramsForOrderInfoUpdate.put("lastUpdatedTime", new Date());
			paramsForOrderInfoUpdate.put("orderId", orderId);
			int effectRows = orderInfoDao
					.updateOrderStatusByOrderId(paramsForOrderInfoUpdate);
			Assert.isTrue(effectRows > 0, "订单" + orderId+"状态修改失败" );

			effectRows = orderProcessDao.setStatusPick(orderId);
			Assert.isTrue(effectRows > 0, "订单" + orderId+"状态修改失败");
			
			effectRows = orderProcessDao.updateRecheckType(orderId, recheckType, 0);
			Assert.isTrue(effectRows > 0, "订单" + orderId+"复核流程状态改回失败");
			
			orderGoodsDao.deletePackGoods(orderId); // 删除耗材绑定信息
			String trackingNumbers = "";
			for (Shipment shipment : shipmentList) {
				this.cleanOrderGoodsBatch(shipment.getShipment_id());
				if(shipment.getShipment_order_sequence_number()>1){
					trackingNumbers = trackingNumbers
							+ shipment.getTracking_number() + ",";
					shipmentDao.deleteShipment(shipment.getShipment_id());
				}else{
					shipmentDao.updatePackBoxStatus(shipment.getShipment_id(), null, 'E', new Date(), localUser);
				}
				shipmentDetailDao.deleteShipmentDetail(shipment.getShipment_id());
			}
			
			if("Y".equalsIgnoreCase(needDelTransferCode)){
				orderInfoDao.deleteTransferCodeByOrder(orderId);
			}
			UserActionOrder userActionOrder = new UserActionOrder();
			userActionOrder.setOrder_id(orderId);
			userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_PICKING);
			userActionOrder.setAction_type(OrderInfo.ORDER_STATUS_PICKING);
			if(trackingNumbers==""){
				userActionOrder.setAction_note("取消复核");
			}else{
				userActionOrder.setAction_note("状态回到拣货中,删除的面单：" + trackingNumbers);
			}
			
			userActionOrder.setCreated_user(localUser);
			userActionOrder.setCreated_time(new Date());
			effectRows = userActionOrderDao.insert(userActionOrder);
			Assert.isTrue(effectRows > 0, "订单"+ orderId+"操作记录插入失败" );

		} catch (RuntimeException e) {
			throw new RuntimeException("订单" + orderId + "清空记录时，数据更新出错");
		}
		resultMap.put("result", Response.SUCCESS);
		return resultMap;
	}
	
	private void cleanOrderGoodsBatch(int shipmentId) {
		List<ShipmentDetail> detailList = shipmentDetailDao.selectByShipmentId(shipmentId);
		for(ShipmentDetail shipmentDetail: detailList) {
			orderGoodsBatchDao.deleteByOrderGoods(shipmentDetail.getOrder_goods_id());
		}
	}

	/**
	 * 问题单处理 - 删除面单 根据波次单号清空复核记录
	 */
	@Override
	public Map<String,Object> cleanBatchPickOrderTns(String batchPickSn){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//1. 根据波次单号查订单状态
		List<Map<String,Object>> batchOrderList = batchPickDao.selectBatchOrderListForCleanTns(batchPickSn);
		BatchPick batchPick = batchPickDao.getBatchBatchPickByBPSn(batchPickSn);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(batchPick), "根据单号没有查到批量波次信息！"+batchPickSn);
		String fromFlowStatus = batchPick.getFlow_status();
		Assert.isTrue(fromFlowStatus.equalsIgnoreCase("BATCH_RECHECK") , 
				"波次"+batchPickSn+"当前流程不支持取消复核！");
		
		//2. 无shipment不允许
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(batchOrderList),"根据波次单号没有搜到需要删除的快递单号！");
		//3. 非批量波次不允许
		String batchPickType = batchOrderList.get(0).get("batch_process_type").toString();
		Assert.isTrue("BATCH".equalsIgnoreCase(batchPickType),"波次单非批量波次，请使用其他方式单独进行取消！");
		//4. 订单中有已称重包裹不允许 
		List<Integer> cancelOrders = new ArrayList<Integer>(); // 取消的订单
		List<Integer> orders = new ArrayList<Integer>(); //所有订单
		for (Map<String, Object> map : batchOrderList) {
			Integer orderId = Integer.parseInt(String.valueOf(map.get("order_id")));
			String orderStatus = String.valueOf(map.get("status"));
			String shipmentStatus = String.valueOf(map.get("shipment_status_group"));
			Assert.isTrue("INIT".equalsIgnoreCase(shipmentStatus),"波次中包含已称重的包裹，不允许取消复核！");
			if(OrderInfo.ORDER_STATUS_CANCEL.equals(orderStatus)){
				cancelOrders.add(orderId);
			}
			Assert.isTrue(OrderInfo.ORDER_STATUS_PICKING.equalsIgnoreCase(orderStatus)
					|| OrderInfo.ORDER_STATUS_RECHECKED.equalsIgnoreCase(orderStatus)
					|| OrderInfo.ORDER_STATUS_CANCEL.equalsIgnoreCase(orderStatus)
					,"波次中包含已称重的包裹，不允许取消复核！");
			orders.add(orderId);
		}
		//5. select for update order_ids
		List<OrderInfo> orderInfoList = orderInfoDao.selectOrderInfoListByIdForUpdate(orders);
		//6. 若订单已取消，不改状态！注意shipment状态
		String localUser = (String) SecurityUtils.getSubject().getPrincipal();
		Integer col = batchPickDao.updateFlowStatusForBatchPick(batchPick.getBatch_pick_id(),fromFlowStatus,"SHIPMENT_Y");
		Assert.isTrue(col>0, "此波次"+batchPickSn+"复核流程恢复失败！");
		UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
		userActionBatchPick.setAction_note("END_"+fromFlowStatus);
		userActionBatchPick.setAction_type("SHIPMENT_Y");
		userActionBatchPick.setCreated_time(new Date());
		userActionBatchPick.setCreated_user(localUser);
		userActionBatchPick.setStatus(batchPick.getStatus());
		userActionBatchPick.setBatch_pick_id(batchPick.getBatch_pick_id());
		userActionBatchPickDao.insert(userActionBatchPick);
		
		List<Integer> shipmentIds = shipmentDao.selectAllShipmentIdsByOrderIds(orders);
		shipmentDao.batchUpdatePackBoxStatus(shipmentIds,
				null, 'E', new Date(), localUser);
		shipmentDao.deleteBatchShipment(shipmentIds);
		shipmentDetailDao.deleteBatchShipmentDetail(shipmentIds);
		for (OrderInfo orderInfo : orderInfoList) {
			Integer orderId = orderInfo.getOrder_id();
			String orderStatus = orderInfo.getOrder_status();
			Assert.isTrue(OrderInfo.ORDER_STATUS_PICKING.equalsIgnoreCase(orderStatus)
					|| OrderInfo.ORDER_STATUS_RECHECKED.equalsIgnoreCase(orderStatus)
					|| OrderInfo.ORDER_STATUS_CANCEL.equalsIgnoreCase(orderStatus)
					,"波次中包含已称重的包裹，不允许取消复核！");
			String newStatus = OrderInfo.ORDER_STATUS_PICKING;
			if(cancelOrders.contains(orderId)){
				newStatus = orderInfo.ORDER_STATUS_CANCEL;
			}
			
			Map<String, Object> paramsForOrderInfoUpdate = new HashMap<String, Object>();
			paramsForOrderInfoUpdate.put("orderStatus",newStatus);
			paramsForOrderInfoUpdate.put("lastUpdatedUser", localUser);
			paramsForOrderInfoUpdate.put("lastUpdatedTime", new Date());
			paramsForOrderInfoUpdate.put("orderId",orderId);
			int effectRows = orderInfoDao.updateOrderStatusByOrderId(paramsForOrderInfoUpdate);
			Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);

			orderGoodsDao.deletePackGoods(orderId); // 删除耗材绑定信息
			UserActionOrder userActionOrder = new UserActionOrder();
			userActionOrder.setOrder_id(orderId);
			userActionOrder.setOrder_status(newStatus);
			userActionOrder.setAction_type(newStatus);
			userActionOrder.setAction_note("批量波次取消复核，订单状态回退");
			userActionOrder.setCreated_user(localUser);
			userActionOrder.setCreated_time(new Date());
			effectRows = userActionOrderDao.insert(userActionOrder);
			Assert.isTrue(effectRows > 0, "订单操作记录插入失败, orderId:" + orderId);
			if(!cancelOrders.contains(orderId)){
				effectRows = orderProcessDao.setStatusPick(orderId);
				Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);
			}
		}
		resultMap.put("result", Response.SUCCESS);
		return resultMap;
	}
	@Override
	public Map<String, Object> queryOrderByTrackingNo(String no) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		resultMap = shipmentDao.queryOrderByTrackingNo(no);
		if (com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(resultMap)) {
			resultMap = new HashMap<String, Object>();
			resultMap.put("result", "error");
			resultMap.put("message", "未查到对应的订单");
			return resultMap;
		}
		resultMap.put(
				"customer",
				warehouseCustomerDao.selectByCustomerId(
						Integer.parseInt(resultMap.get("customer_id")
							.toString())).getName());
		
		String mtcode=shipmentDao.selectMtCodeByTrackingNo(no);
		if(null==mtcode){
			resultMap.put("mtcode","-");
		}else{
			resultMap.put("mtcode",mtcode);
		}
		
		resultMap.put("tracking_number", no);
		resultMap.put("result", "success");
		return resultMap;
	}

	@Override
	public List<Shipment> selectByOrderIdAndStatus(Integer orderId,
			String status) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		paramMap.put("status", status);

		return shipmentDao.selectByOrderIdAndStatus(paramMap);
	}

	/***
	 *************************************** 
	 * @author dlyao
	 *************************************** 
	 */
	@Override
	public Map<String, Object> queryAllShipment(String batch_pick_sn) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		BatchPick bp = batchpickDao.getBatchBatchPickByBPSn(batch_pick_sn);
		if (WorkerUtil.isNullOrEmpty(bp)) {
			resultMap.put("message", "未找到这个批量单");
			resultMap.put("result", "error");
			return resultMap;
		}
		
		if(!"BATCH_RECHECK".equalsIgnoreCase(bp.getFlow_status())){
			resultMap.put("message", "波此单走了超级复核后门，不允许再走正常流程");
			resultMap.put("result", "error");
			return resultMap;
		}
		int batch_pick_id = bp.getBatch_pick_id();

		int physical_warehouse_id = batchpickDao
				.getPhysicalWarehouseIdByBPId(batch_pick_id);

		// 每个快递
		List<Map<String, Object>> list = batchpickDao
				.getAllShipmentByBPSn(batch_pick_id);
		
		if(com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(list)){
			resultMap.put("message", "波此单上没有绑定运单");
			resultMap.put("result", "failure");
			resultMap.put("success", false);
			return resultMap;
		}
		List<Integer> shipmentIdList = new ArrayList<Integer>();

//		shipmentIdList = Tools.changeStringListToIntegerList(Tools
//				.changeStringToList(list.get(0).get("shipment_ids").toString(),
//						0));

		// 是否已经生成
		//int inpsm = shipmentDao.getShipmetsBinded(shipmentIdList);
		int inpsm = shipmentDao.getShipmetsBindedByBPD(batch_pick_id);
		if (inpsm == 0) {
			
			
			int errorNumOrders = batchpickDao.getErrorNumOrders(batch_pick_id);
			if (errorNumOrders > 0) {
				List<Map<String,String>> errorOrderList=batchpickDao.getErrorOrdersList(batch_pick_id);
				int errortrackingNums=batchpickDao.getErrorShipmentNums(batch_pick_id);
				resultMap.put("errortrackingNums", "此波此单中共有"+errortrackingNums+"个包裹被取消");
				resultMap.put("errorOrderList", errorOrderList);
				resultMap.put("message", "成功绑定码拖，波次单中已有"+errorNumOrders+"个订单取消");
			}


			int noWeightedOrders=batchpickDao.noWeightedOrders(batch_pick_id);

			if (noWeightedOrders > errorNumOrders) {
				resultMap.put("message", "有订单未称重");
				resultMap.put("result", "failure");
				resultMap.put("success", false);
				return resultMap;
			}
			
			ArrayList<String> MTCodelist = new ArrayList<String>();

			for (int i = 0; i < list.size(); i++) {

				String tag = WorkerUtil.generatorSequence(
						SequenceUtil.KEY_NAME_TAGCODE, "MT", true);
				MTCodelist.add(tag);
				logger.info("shippment getTrayBarcodes WorkerUtil.generatorSequence tag:"
						+ tag);
			}

			if (WorkerUtil.isNullOrEmpty(MTCodelist)
					|| MTCodelist.size() != list.size()) {

				resultMap.put("error", "生成码托条码异常");
				resultMap.put("result", "failure");
				resultMap.put("success", false);
				return resultMap;
			}

			// 检查数据库中是否已经存在相同的码托条码
			int sameCodeNum = shipmentDao.getSameMTCodeNum(MTCodelist);

			if (sameCodeNum > 0) {

				resultMap.put("error", "生成码托条码异常，条码已经存在过");
				resultMap.put("result", "failure");
				resultMap.put("success", false);
				return resultMap;
			}

			String username = (String) SecurityUtils.getSubject()
					.getPrincipal();

			if (WorkerUtil.isNullOrEmpty(username)) {

				resultMap.put("error", "检查是否登录或者会话已过期，重新登录");
				resultMap.put("result", "failure");
				resultMap.put("success", false);
				return resultMap;
			}

			// 保存生成的码托条码

			// 绑定码拖
			Map<String, Object> map = new HashMap<String, Object>();

			for (int i = 0; i < MTCodelist.size(); i++) {

				map = list.get(i);
				shipmentIdList.clear();
				String MTCode = MTCodelist.get(i);
				Pallet pallet = new Pallet();
				pallet.setCreated_time(new Date());
				pallet.setCreated_user(username);
				pallet.setPallet_no(MTCode);
				pallet.setShip_status("init");
				pallet.setShipping_id(Integer.parseInt(map.get("shipping_id")
						.toString()));
				pallet.setPhysical_warehouse_id(physical_warehouse_id);
				shipmentDao.insertMTcodeNew(pallet);

				map.put("MTCode", MTCode);
				
                int shipping_id=Integer.parseInt(map.get("shipping_id").toString());
				shipmentIdList=batchpickDao.getAllShipmentByBPSnAndShippingId(batch_pick_id,shipping_id);
				
				List<Map<String, Object>> shipmentList = shipmentDao.getShipmentByshipmentIdList(shipmentIdList);
				
//				shipmentIdList = Tools.changeStringListToIntegerList(Tools
//						.changeStringToList(map.get("shipment_ids").toString(),
//								0));
				List<PalletShipmentMapping> psmList = new ArrayList<PalletShipmentMapping>();
				List<UserActionOrder> uaoList = new ArrayList<UserActionOrder>();
				List<UserActionShipment> uasList = new ArrayList<UserActionShipment>();
				for (Integer stid : shipmentIdList) {
					PalletShipmentMapping psm = new PalletShipmentMapping();
					psm.setShipment_id(stid);
					psm.setBind_status("BINDED");
					psm.setBind_user(username);
					psm.setBind_time(new Date());
					psm.setPallet_no(MTCode);

					psmList.add(psm);
				}
				shipmentDao.insertpalletShipmentMappingList(psmList);

				for (Map<String, Object> shipmentInfo : shipmentList) {
					UserActionShipment uas = new UserActionShipment();
					uas.setShipment_id(Integer.parseInt(shipmentInfo.get("shipment_id").toString()));
					uas.setStatus(shipmentInfo.get("status").toString());
					uas.setAction_type("BIND_PALLET");
					uas.setAction_note("BIND_PALLET包裹绑定至码托（码托条码：" + MTCode +"）");
					uas.setCreated_user(username);
					uas.setCreated_time(new Date());
					uasList.add(uas);
					
					UserActionOrder uao = new UserActionOrder();
					uao.setAction_note("包裹（快递单号：" + shipmentInfo.get("tracking_number").toString() + "）绑定至码托（码托条码：" + MTCode +"）");
					uao.setAction_type("BIND_PALLET");
					uao.setCreated_time(new Date());
					uao.setCreated_user(username);
					uao.setOrder_id(Integer.parseInt(shipmentInfo.get("order_id").toString()));
					uao.setOrder_status(OrderInfo.ORDER_STATUS_WEIGHED);
					uasList.add(uas);
					uaoList.add(uao);
				}
				userActionShipmentDao.batchInsertUserActionShipmentRecord(uasList);
				userActionOrderDao.batchInsert(uaoList);
			}

			resultMap.put("list", list);
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < list.size(); i++) {
				map = list.get(i);
				
				int shipping_id=Integer.parseInt(map.get("shipping_id").toString());
				shipmentIdList=batchpickDao.getAllShipmentByBPSnAndShippingId(batch_pick_id,shipping_id);
//				shipmentIdList.clear();
//				shipmentIdList = Tools.changeStringListToIntegerList(Tools
//						.changeStringToList(list.get(i).get("shipment_ids")
//								.toString(), 0));
				String mtcode = shipmentDao
						.getMtcodeByshipmentIdList(shipmentIdList);

				map.put("MTCode", mtcode);

			}
			int errorNumOrders = batchpickDao.getErrorNumOrders(batch_pick_id);
			if (errorNumOrders > 0) {
				List<Map<String,String>> errorOrderList=batchpickDao.getErrorOrdersList(batch_pick_id);
				int errortrackingNums=batchpickDao.getErrorShipmentNums(batch_pick_id);
				resultMap.put("errortrackingNums", "此波此单中共有"+errortrackingNums+"个包裹被取消");
				resultMap.put("errorOrderList", errorOrderList);
				resultMap.put("message", "成功绑定码拖，波次单中已有"+errorNumOrders+"个订单取消");
			}
			resultMap.put("list", list);
		}

		resultMap.put("result", "success");
		resultMap.put("success", true);
		return resultMap;
	}

	// 批量称重(检查波次单号)
	public Map getBatchPickIsRechecked(String batchPickSn) {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Map> mapList = shipmentDao.selectBatchPickIsRechecked(batchPickSn);
		if (WorkerUtil.isNullOrEmpty(mapList)) {
			returnMap.put("result", "failure");
			returnMap.put("note", "此批量波次不存在或订单状态不对！");
		} else {
			if(mapList.size() == 2){
				String orderStatus = mapList.get(0).get("order_status").toString()+"_"+mapList.get(1).get("order_status").toString();
				if("CANCEL_RECHECKED".equals(orderStatus)){
					
					List<Map> cancelOrder = shipmentDao.selectBatchPickCancelTrackingNumber(batchPickSn);
					String note = "";
					if(!WorkerUtil.isNullOrEmpty(cancelOrder)){
						for(Map map:cancelOrder){
							if(map.containsKey("tracking_number")){
								note += map.get("tracking_number").toString()+";";
							}else{
								note += map.get("order_id").toString()+"-"+"没有形成运单号;";
							}
						}
					}
					returnMap.put("result", "info");
					returnMap.put("note", "此批量波次内有"+mapList.get(0).get("num").toString()+"个取消订单被过滤,取消的面单号："+note);
				}else{
					returnMap.put("result", "failure");
					returnMap.put("note", "此批量波次内有订单状态有误！");
				}
			}else if(mapList.size() == 3){
				returnMap.put("result", "failure");
				returnMap.put("note", "此批量波次内有订单状态有误！");
			}else {
				if (OrderInfo.ORDER_STATUS_RECHECKED.equals(mapList.get(0)
						.get("order_status").toString())) {
					returnMap.put("result", "success");
					returnMap.put("note", "成功");
				} else {
					returnMap.put("result", "failure");
					returnMap.put("note", "该批量波次内的订单不是复核状态");
				}
			}
		}
		return returnMap;
	}

	// 批量称重(检查快递单号)
	public Map checkBatchTrackingNumber(String batchPickSn,
			String trackingNumber) {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("batchPickSn", batchPickSn);
		searchMap.put("trackingNumber", trackingNumber);

		Map map = shipmentDao.checkBatchTrackingNumber(searchMap);
		if (WorkerUtil.isNullOrEmpty(map)) {
			returnMap.put("result", "failure");
			returnMap.put("note", "该面单号不在该批量波次内！");
		} else {
			if (new BigDecimal(map.get("shipping_wms_weight").toString())
					.compareTo(BigDecimal.ZERO) > 0) {
				returnMap.put("result", "failure");
				returnMap.put("note", "该面单已称重！");
			} else {
				returnMap.put("result", "success");
				returnMap.put("note", "success");
			}
		}
		return returnMap;
	}

	// 批量称重(批量更新同一波次包裹重量)
	public Map updateBatchWeight(String batchPickSn, String trackingNumber,
			String weight, String actionUser) {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		Shipment shipment = shipmentDao
				.getShipmentByTrackingNumber(trackingNumber);

		// 1.更新重量
		Map<String, Object> paramsForBatchWeightUpdate = new HashMap<String, Object>();
		paramsForBatchWeightUpdate.put("batchPickSn", batchPickSn);
		paramsForBatchWeightUpdate.put("weight", new BigDecimal(weight).multiply(new BigDecimal(1000)));
		paramsForBatchWeightUpdate.put("sequenceNumber",
				shipment.getShipment_order_sequence_number());
		try {
			shipmentDao.updateBatchWeight(paramsForBatchWeightUpdate);
		} catch (Exception e1) {
			throw new RuntimeException("批量更新重量失败！");
//			returnMap.put("result", "failure");
//			returnMap.put("note", "批量更新重量失败！");
//			returnMap.put("info", "批量更新重量失败！");
//			return returnMap;
		}

		// 2.更新状态
		List<Map> listMap = shipmentDao.selectBatchWeightIsOver(shipment
				.getOrder_id());
		if (WorkerUtil.isNullOrEmpty(listMap)) {
			Map<String, Object> paramsForOrderInfoUpdate = new HashMap<String, Object>();
			paramsForOrderInfoUpdate.put("orderStatus",
					OrderInfo.ORDER_STATUS_WEIGHED);
			paramsForOrderInfoUpdate.put("lastUpdatedUser", actionUser);
			paramsForOrderInfoUpdate.put("batchPickSn", batchPickSn);
			try {
				shipmentDao.updateBatchOrderStatus(paramsForOrderInfoUpdate);
				paramsForOrderInfoUpdate.put("actionType", "订单已称重");
				shipmentDao.updateBatchOrderAction(paramsForOrderInfoUpdate);
				returnMap.put("info", "success");
			} catch (Exception e) {
				throw new RuntimeException("更新状态失败！");
//				e.printStackTrace();
//				returnMap.put("info", "更新状态失败");
//				return returnMap;
			}
		} else {
			returnMap.put("info", "该批量波次还剩" + listMap.size() + "面单未称重！");
		}

		returnMap.put("result", "success");
		returnMap.put("note", "成功");
		return returnMap;
	}

	/**
	 * 批量复核 获取波次单下所有订单发货单信息
	 */
	@Override
	public Map<String, Object> getAllOrderShipmentInfoByBatchOrderSn(
			String batchPickSn) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 1. orderId,shipmentCount,packbox_count
		List<String> orderStatusList = orderProcessDao.getOrderStatusList(batchPickSn);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(orderStatusList), "根据波次号"
				+ batchPickSn + "没有找到对应订单信息");
		for (String orderStatus : orderStatusList) {
			Assert.isTrue(OrderInfo.ORDER_STATUS_CANCEL.equals(orderStatus) || OrderInfo.ORDER_STATUS_PICKING.equals(orderStatus),"波次"+batchPickSn+"并非“拣货中”状态");
		}
		List<Map<String, Object>> orderShipmentInfoList = shipmentDao.getShipmentCounts(batchPickSn);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(orderShipmentInfoList), "批量中未批拣的波次中没有查到此波次信息");
		Integer maxShipmentCount = 0;
		List<Integer> orderIdList = new ArrayList<Integer>();
		if (!WorkerUtil.isNullOrEmpty(shipmentDao
				.getMaxShipmentCount(batchPickSn))) {
			maxShipmentCount = shipmentDao.getMaxShipmentCount(batchPickSn);
		}
		for (Map<String, Object> orderShipmentInfo : orderShipmentInfoList) {
			if (Integer.parseInt(String.valueOf(orderShipmentInfo
					.get("shipment_count"))) < maxShipmentCount) {
				// 发货单数量与 波次最多发货单数不一致，则需再申请
				orderIdList.add(Integer.parseInt(String
						.valueOf(orderShipmentInfo.get("order_id"))));
			} else if (Integer.parseInt(String.valueOf(orderShipmentInfo
					.get("shipment_count"))) == 0
					|| Integer.parseInt(String.valueOf(orderShipmentInfo
							.get("packbox_count"))) == Integer.parseInt(String
							.valueOf(orderShipmentInfo.get("shipment_count")))) {
				maxShipmentCount = Integer.parseInt(String
						.valueOf(orderShipmentInfo.get("shipment_count"))) + 1;
				orderIdList.add(Integer.parseInt(String
						.valueOf(orderShipmentInfo.get("order_id"))));
			} else if (Integer.parseInt(String.valueOf(orderShipmentInfo
					.get("shipment_count"))) > maxShipmentCount) {
				maxShipmentCount = Integer.parseInt(String
						.valueOf(orderShipmentInfo.get("shipment_count")));
			}
		}
		map.put("batch_order_num", orderShipmentInfoList.size());
		map.put("max_shipment_count", maxShipmentCount); // 波次中订单申请到发货单的最多数量
		map.put("order_id_list", orderIdList); // 波次中需要继续申请发货单的订单
		return map;
	}

	/**
	 * 批量复核 加载 商品信息
	 */
	@Override
	public Map<String, Object> loadOrderGoodsForBatchPick(String batchPickSn) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		List<OrderInfo> orderInfoList = batchPickDao.getOrderIdByBatchPickSn(batchPickSn);
		OrderInfo orderInfo = orderInfoList.get(0);
		List<Map> unbindGoodsInfo = orderInfoDao.getUnbindGoodsInfo(orderInfo.getOrder_id());
		List<Map> bindShipmentGoodsInfo = shipmentDao
				.selectBindShipmentGoodsInfoByOrderId(orderInfo.getOrder_id());
		List<Map> bindGoodsInfo = formatBindShipmentGoodsInfo(
				bindShipmentGoodsInfo, 0, "");
		Integer batchPickId = batchPickDao.selectbatchPickBySnForRecheck(batchPickSn).getBatch_pick_id();
		
		resMap.put("result", Response.SUCCESS);
		resMap.put("unbind_goods_info", unbindGoodsInfo);
		resMap.put("bind_goods_info", bindGoodsInfo);
		resMap.put("batch_pick_id", batchPickId);
		return resMap;

	}

	@Override
	public Map<String, Object> applyShipmentTn(Integer orderId,String applyType) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		// 1. 订单状态再验证
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(orderId);
		if("BATCH".equals(applyType)){
			Assert.isTrue(
					orderInfo.getOrder_status().equals(OrderInfo.ORDER_STATUS_PICKING) || 
					orderInfo.getOrder_status().equals(OrderInfo.ORDER_STATUS_CANCEL), 
					"订单" + orderId + "状态并非“拣货中”，不能操作复核");
		}else{
			Assert.isTrue(
					orderInfo.getOrder_status().equals(OrderInfo.ORDER_STATUS_PICKING),
					"订单" + orderId + "状态并非“拣货中”，不能操作复核");
		}
		
		// 2. 用户仓库权限
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<Warehouse> warehouseList = (List<Warehouse>) session
				.getAttribute("userLogicWarehouses");
		Warehouse orderWarehouse = warehouseDao.selectByWarehouseId(orderInfo
				.getWarehouse_id());
		Assert.isTrue(warehouseList.contains(orderWarehouse), "当前登录用户没有权限操作订单"
				+ orderId + "，请检查用户仓库权限！");
		// 3. 当前绑定信息查询(存在发货单且不存在绑定耗材关系，与前一步查询数据不一致。极有可能是有人在操作。拦截)
		List<Map> bindShipmentGoodsInfo = shipmentDao
				.selectBindShipmentGoodsInfoByOrderId(orderId);
		Assert.isTrue(
				!(bindShipmentGoodsInfo.size() > 0 && bindShipmentGoodsInfo
						.get(0).get("bind_pack").toString().equals("N")), "订单"
						+ orderId + "正在操作中");
		// 4. 创建发货单+申请快递单号
		Integer shippingId = orderInfo.getShipping_id();
		// 订单同步时已经设定，排除非空可能
		// 创建发货单
		logger.info("订单" + orderId + " batch begin to create shipment");
		Integer shipmentId = 0;
		try {
			shipmentId = createShipment(orderId, shippingId);
		} catch (RuntimeException e) {
			logger.info("订单" + orderId + " create shipment failed!"
					+ e.getMessage());
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "订单" + orderId + "创建发货单失败！");
			return resMap;
			// throw new RuntimeException("订单" + orderId + "创建发货单失败！");
		}
		logger.info("订单" + orderId + " create shipment end,shipmentId is "
				+ shipmentId);

		// 获取运单号
		Map<String, Object> traMap = applyMailno("APPLY", orderId, shipmentId);
		if (!Response.SUCCESS.equals(traMap.get("result"))) {
			return traMap;
		}
		String trackingNumber = String.valueOf(traMap.get("tracking_number"));
		String mark = String.valueOf(traMap.get("mark"));

		// 更新 订单面单信息，快递资源数据
		try {
			shipmentDao.updateTrackingNumber(shipmentId, trackingNumber, mark);
			if (traMap.get("origincode") != null) {
				String origincode = String.valueOf(traMap.get("origincode"));
				String destcode = String.valueOf(traMap.get("destcode"));
				shipmentDao.updateSfShipment(shipmentId, origincode, destcode);
			}
//			orderInfoDao.updateTrackingNumber(orderId, trackingNumber);
		} catch (RuntimeException e) {
			logger.info("更新运单号绑定关系失败！shipmentId:" + shipmentId
					+ " , trackingNumber :" + trackingNumber);
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "订单" + orderId + "更新运单号绑定关系失败！快递单号 :"
					+ trackingNumber);
		}
		resMap.put("result", Response.SUCCESS);
		return resMap;
	}

	@Override
	public void batchApplyShipmentTn(
			List<Map<String, Object>> shippingAppOrders, String shippingCode) {
		Shipping shipping = shippingDao.selectByShippingCode(shippingCode);
		String localUser = (String) SecurityUtils.getSubject().getPrincipal();
		if (WorkerUtil.isNullOrEmpty(localUser)) {
			localUser = "system";
		}
		List<String> trackingNumbers = new ArrayList<String>();
		for (Map<String, Object> map : shippingAppOrders) {
			List<Map<String, Object>> orderInfo = (List<Map<String, Object>>) map
					.get("orderMarkInfo"); // 包含大头笔信息

			Integer shippingAppId = Integer.parseInt(String.valueOf(map
					.get("shipping_app_id")));
			logger.info("shipping_app_id" + shippingAppId + " : orderInfoSize " + orderInfo.size());
			Integer trackingNumberSize = orderInfo.size();

			trackingNumbers = shippingTrackingNumberRepositoryDao
					.getTrackingNumbers(shippingAppId, trackingNumberSize);
			Assert.isTrue(!WorkerUtil.isNullOrEmpty(trackingNumbers),
					shipping.getShipping_name() + "热敏资源库已空，缺失" + trackingNumberSize + "个");
			Assert.isTrue(trackingNumbers.size() == trackingNumberSize,
					shipping.getShipping_name() + "热敏资源库不足，缺失"
							+ (trackingNumberSize - trackingNumbers.size())
							+ "个");
			Integer effectRows = shippingTrackingNumberRepositoryDao
					.updateRepositoryTrackingNumbers(trackingNumbers, "R");
//			logger.info(shippingCode+"更新面单数量："+effectRows);
//			logger.info("需要申请数量"+trackingNumberSize);
			Assert.isTrue(effectRows.equals(trackingNumberSize), shipping.getShipping_name()
					+ "资源抢占失败");
			Integer shippingId = Integer.parseInt(String.valueOf(orderInfo.get(
					0).get("shipping_id")));
//			logger.info("orderinfosize:" + orderInfo.size());
			for (int i = 0; i < orderInfo.size(); i++) {
				Integer orderId = Integer.parseInt(String.valueOf(orderInfo
						.get(i).get("order_id")));
				String mark = String.valueOf(orderInfo.get(i).get("mark"));
				String orderStatus = String.valueOf(orderInfo.get(i).get(
						"order_status"));
				Map<String, Object> map2 = createShipmentTrackingNumber(
						orderId, shippingId, trackingNumbers.get(i), mark,
						orderStatus);
				Assert.isTrue(!Response.FAILURE.equals(map2.get("result")), ""
						+ map2.get("note"));
			}
			shippingTrackingNumberRepositoryDao
					.updateRepositoryTrackingNumbers(trackingNumbers, "Y");

		}
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
//			logger.error("订单" + orderId + "更新快递面单失败"+e.getMessage(),e);
			throw new RuntimeException("订单" + orderId + "更新快递面单失败");
		}

		return resMap;
	}

	@Override
	public List<Map<String, Object>> findShipmentOrderGoodsInfoList(
			Integer batchPickId, Integer maxShipmentCount, Integer productId) {
		List<Map<String, Object>> shipmentOrderGoodsInfoList = shipmentDao
				.findShipmentOrderGoodsInfoList(batchPickId, maxShipmentCount,
						productId);
		return shipmentOrderGoodsInfoList;
	}

	@Override
	public Map<String, Object> batchBindConsume(Integer batchPickId,
			Integer maxShipmentCount, String barcode) {
		Map<String, Object> resMap = new HashMap<String, Object>();

		// 1. 判断 发货单号存在性 ，且未绑定过耗材 , 为当前订单最新shipmentId
		List<Map<String, Object>> shipmentInfo = shipmentDao
				.findBatchShipmentIdByBatchOrderId(batchPickId,
						maxShipmentCount);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(shipmentInfo),
				"波次订单未找到符合的发货单信息，请重新加载");
		Assert.isTrue(
				WorkerUtil.isNullOrEmpty(shipmentInfo.get(0).get(
						"packbox_product_id")), "波次订单已经绑定过耗材，请重新加载");
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(shipmentInfo.get(0).get(
				"product_id")), "波次订单还未绑定过任何商品，不允许绑定耗材");
		logger.info("is_max" + shipmentInfo.get(0).get("is_max"));
		Assert.isTrue(shipmentInfo.get(0).get("is_max").toString().equals("0"),
				"波次订单并不是订单的最新记录，刷新看看");
		Assert.isTrue(
				OrderInfo.ORDER_STATUS_PICKING.equalsIgnoreCase(String
						.valueOf(shipmentInfo.get(0).get("order_status")))
						|| OrderInfo.ORDER_STATUS_CANCEL
								.equalsIgnoreCase(String.valueOf(shipmentInfo
										.get(0).get("order_status"))),
				"订单状态“"
						+ OrderInfo.ORDER_STATUS_MAP.get(String
								.valueOf(shipmentInfo.get(0)
										.get("order_status"))) + "”不能绑定耗材");
		// 2. 判断barcode 存在性，对应是否为耗材 (通用耗材所属业务组 -- 乐其仓库)
		String orderCustomerId = shipmentInfo.get(0).get("customer_id").toString();
		Map productInfo = productDao.selectByBarodeForConsume(barcode.trim(),orderCustomerId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(productInfo), "条码" + barcode
				+ " 未找到对应耗材信息");
		Assert.isTrue(OrderGoods.ORDER_GOODS_TYPE_PACKBOX
				.equalsIgnoreCase(productInfo.get("product_type").toString()),
				"条码" + barcode + " 并不是耗材");
		String barcode_customer_id = productInfo.get("customer_id").toString();

		Integer warehouseId = Integer.parseInt(String.valueOf(shipmentInfo.get(
				0).get("warehouse_id")));
		String productId = productInfo.get("product_id").toString();
		String productName = productInfo.get("product_name").toString();
		try {
			List<OrderGoods> orderGoodsList = new ArrayList<OrderGoods>();
			List<ShipmentDetail> shipmentDetailList = new ArrayList<ShipmentDetail>();
			List<UserActionShipment> userActionShipmentList = new ArrayList<UserActionShipment>();
			List<UserActionOrder> userActionOrderList = new ArrayList<UserActionOrder>();
			List<Integer> orderIdList = new ArrayList<Integer>();
			List<Integer> pickingOrderIdList = new ArrayList<Integer>();
			List<Integer> shipmentIdList = new ArrayList<Integer>();
			List<Map> unbindGoodsInfo = orderInfoDao.getUnbindGoodsInfo(Integer
					.parseInt(String.valueOf(shipmentInfo.get(0)
							.get("order_id"))));
			String shipmentStatus = shipmentInfo.get(0).get("status")
					.toString();
			String localUser = (String) SecurityUtils.getSubject()
					.getPrincipal();
			int effectRows = 0;
			for (Map<String, Object> map : shipmentInfo) {
				Integer orderId = Integer.parseInt(String.valueOf(map
						.get("order_id")));
				orderIdList.add(orderId);
				if (OrderInfo.ORDER_STATUS_PICKING.equals(String.valueOf(map
						.get("order_status")))) {
					pickingOrderIdList.add(orderId);
				}
				Integer shipmentId = Integer.parseInt(String.valueOf(map
						.get("shipment_id")));
				shipmentIdList.add(shipmentId);
				OrderGoods orderGoods = new OrderGoods();
				orderGoods.setOrder_id(Integer.parseInt(map.get("order_id").toString()));
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
				orderGoods.setOrder_goods_type(productInfo.get("product_type").toString());
				orderGoods.setCreated_user(localUser);
				orderGoods.setCreated_time(new Date());
				orderGoods.setLast_updated_user(localUser);
				orderGoods.setLast_updated_time(new Date());
				orderGoodsList.add(orderGoods);

				UserActionShipment userActionShipment = new UserActionShipment();
				userActionShipment.setShipment_id(shipmentId);
				userActionShipment.setStatus(shipmentStatus);
				userActionShipment.setAction_type("bind_consume");
				userActionShipment.setAction_note("bind_consume");
				userActionShipment.setCreated_user(localUser);
				userActionShipment.setCreated_time(new Date());
				userActionShipmentList.add(userActionShipment);

				if (WorkerUtil.isNullOrEmpty(unbindGoodsInfo) && !OrderInfo.ORDER_STATUS_CANCEL.equals(String.valueOf(map
						.get("order_status")))) {

					UserActionOrder userActionOrder = new UserActionOrder();
					userActionOrder.setOrder_id(orderId);
					userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_RECHECKED);
					userActionOrder.setAction_type(OrderInfo.ORDER_STATUS_RECHECKED);
					userActionOrder.setAction_note("订单复核完成");
					userActionOrder.setCreated_user(localUser);
					userActionOrder.setCreated_time(new Date());
					userActionOrderList.add(userActionOrder);
				}
			}

			orderGoodsDao.batchInsert(orderGoodsList);
			List<Map<String, Integer>> shipmentOrderGoodsMap = shipmentDao
					.getShipmentOrderGoodsMap(productId, maxShipmentCount,
							orderIdList);

			effectRows = shipmentDao.batchUpdatePackBoxStatus(shipmentIdList,
					productId, 'N', new Date(), localUser);
			for (int i = 0; i < shipmentOrderGoodsMap.size(); i++) {
				Map<String, Integer> shipmentOrderGoods = shipmentOrderGoodsMap
						.get(i);
				ShipmentDetail shipmentDetail = new ShipmentDetail();
				shipmentDetail.setShipment_id(Integer.parseInt(String
						.valueOf(shipmentOrderGoods.get("shipment_id"))));
				shipmentDetail.setGoods_name(productName);
				shipmentDetail.setGoods_number(1);
				shipmentDetail.setOrder_goods_id(Integer.parseInt(String
						.valueOf(shipmentOrderGoods.get("order_goods_id"))));
				shipmentDetail.setProduct_id(Integer.parseInt(productId));
				shipmentDetail.setSerial_number("");
				shipmentDetailList.add(shipmentDetail);
			}
			shipmentDetailDao.batchInsert(shipmentDetailList);
			userActionShipmentDao
					.batchInsertUserActionShipmentRecord(userActionShipmentList);
			if (pickingOrderIdList.size() > 0
					&& WorkerUtil.isNullOrEmpty(unbindGoodsInfo)) {
				effectRows = orderProcessDao
						.batchSetStatusRecheckedByOrderId(pickingOrderIdList);
				Assert.isTrue(effectRows == pickingOrderIdList.size(),
						"订单状态修改失败");
				if (!WorkerUtil.isNullOrEmpty(userActionOrderList)) {
					effectRows = orderInfoDao.batchUpdateOrderStatusByOrderId(
							pickingOrderIdList,
							OrderInfo.ORDER_STATUS_RECHECKED, localUser);
					Assert.isTrue(effectRows == pickingOrderIdList.size(),
							"订单状态修改失败");
					userActionOrderDao.batchInsert(userActionOrderList);
				}
			}
		} catch (Exception e) {
			logger.error("耗材绑定失败，batchPickId：" + batchPickId + ", errorInfo:"
					+ e.getMessage());
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "耗材绑定失败");
			return resMap;
		}
		// 4. return
		resMap.put("result", "success");
		resMap.put("goods_name", productName);
		// resMap.put("order_goods_id", orderGoodsId);
		resMap.put("product_type", "PACK");
		resMap.put("is_serial", "N");
		resMap.put("barcode", barcode);
		resMap.put("goods_number", "1");

		return resMap;
	}

	@Override
	public Map batchSelectPrintInfo(String batchPickId, Integer startNum, Integer endNum) {
		Map<String,Object> printInfo = new HashMap<String,Object>();
		List<Map<String,Object>> orderInfoList = orderProcessDao.batchSelectPrintInfo(batchPickId);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//		logger.info("orderInfoList.size():"+orderInfoList.size());
//		logger.info("startNum:"+startNum);
//		logger.info("endNum:"+endNum);
		for(int i=0;i<orderInfoList.size();i++){
//			logger.info("in....");
			if(startNum.compareTo(i+1)>0) {
//				logger.info("startNum.compareTo(i+1)>0    " );
				continue;
			}
//			logger.info("i="+i);
			Map<String,Object> map = orderInfoList.get(i);
			map.put("pageIndex", i+1);
			list.add(map);
			if(endNum.compareTo(0)>0 && endNum.compareTo(i+1)<=0) {
//				logger.info(" endNum.compareTo(0)>0 && endNum.compareTo(i+1)<=0       ");
				break;
			}
//			logger.info("next.......");
		}
//		logger.info("list.size = "+list.size());
		// to do insurance for some customer like 资生堂，lamer，路易十三，乐其电教etc
		List<Map<String,Object>> orderGoodsList = orderProcessDao.batchSelectGoodsBarcodeList(batchPickId);
		printInfo.put("order_info_list", list);
		printInfo.put("order_goods_list", orderGoodsList);
		Integer batchOrderCount = orderProcessDao.selectBatchOrderCount(batchPickId);
		printInfo.put("order_count", batchOrderCount);
		Integer packNum = orderInfoList.size()/batchOrderCount;
		printInfo.put("packNum", packNum);
		Integer batchNumSize = orderInfoList.size();
		printInfo.put("batchNumSize", batchNumSize);
		return printInfo;

	}

	/**
	 * 杂单复核页面 判断订单是否原为BATCH波次
	 */
	@Override
	public Map<String, Object> checkOrderForRecheck(int orderId) {
		Map<String,Object> resMap = new HashMap<String,Object>();
		String batchProcessType = orderProcessDao.checkOrderForRecheck(orderId);
		if(!WorkerUtil.isNullOrEmpty(batchProcessType) && "BATCH".equalsIgnoreCase(batchProcessType)){
			resMap.put("result", Response.SUCCESS);
			resMap.put("type","BATCH");
		}else{
			resMap.put("result", Response.SUCCESS);
			resMap.put("type","UNBATCH");
		}
		return resMap;
	}

	/**
	 * 批量复核页面 判断波次是否原为BATCH，后被改为NORMAL
	 */
	@Override
	public void checkBatchPickForRecheck(String batchPickSn) {
		BatchPick batchPick = batchPickDao.selectbatchPickBySnForRecheck(batchPickSn);
		if(WorkerUtil.isNullOrEmpty(batchPick)) {
			throw new RuntimeException("根据波次单号没有查到信息!"+batchPickSn);
		}
		Integer batchPickId = batchPick.getBatch_pick_id();
		String batchProcessType = batchPick.getBatch_process_type();
		String recheckMark = batchPick.getRecheck_mark();
		
		if("NORMAL".equalsIgnoreCase(batchProcessType) && "Y".equalsIgnoreCase(recheckMark)){
			logger.info("batchPickSn:"+batchPickSn+"申请从杂单转回批量单");
			batchPick = batchPickDao.selectbatchPickForUpdateLockV2(batchPickId);
			List<Map<String,Object>> batchList = batchPickDao.checkBatchPickForRecheck(batchPickId);
			Assert.isTrue(WorkerUtil.isNullOrEmpty(batchList), "波次"+batchPickSn+"中已经有部分订单操作过散单复核，不能进行批量复核！");
			Integer col = batchPickDao.updateBatchProcessType(batchPickId, "BATCH");
			Assert.isTrue(col>0, "波次属性更新失败，请重试！");
		}else if(!"BATCH".equalsIgnoreCase(batchProcessType)){
			throw new RuntimeException("此波次不属于批量单，不能进行批量复核！");
		}
		
	}

	/**
	 * 批量波次判断增加逻辑：
	 * 批量复核： SHIPMENT_Y -->BATCH_RECHECK ； BATCH_RECHECK
	 * 打印面单即出库： SHIPMENT_Y-->BATCH_TRICK
	 */
	@Override
	public void checkBatchPickFlowStatus(String batchPickSn, String flowStatus) {
		BatchPick batchPick = batchPickDao.selectbatchPickBySnForRecheck(batchPickSn);
		if(WorkerUtil.isNullOrEmpty(batchPick)) {
			throw new RuntimeException("根据波次单号没有查到信息!"+batchPickSn);
		}
		Integer batchPickId = batchPick.getBatch_pick_id();
		String batchProcessType = batchPick.getBatch_process_type();
		String checkFlowStatus = batchPick.getFlow_status();
		Integer printCount = batchPick.getPrint_count();
		Assert.isTrue("BATCH".equalsIgnoreCase(batchProcessType), "波次"+batchPickSn+"不是批量波次，不允许进行此项操作");
		Assert.isTrue(printCount.compareTo(0)>0, "波次"+batchPickSn+"拣货单还未打印！");
		if("SHIPMENT_N".equalsIgnoreCase(checkFlowStatus) || "SHIPMENT_E".equalsIgnoreCase(checkFlowStatus)){
			throw new RuntimeException("此波次"+batchPickSn+"还没有完成申请面单操作，请稍后再试");
		}else if("SHIPMENT_Y".equalsIgnoreCase(checkFlowStatus)){ // update
			Integer col = batchPickDao.updateFlowStatusForBatchPick(batchPickId,checkFlowStatus,flowStatus);
			Assert.isTrue(col>0, "此波次"+batchPickSn+"已经走了其他操作流程！");
			String actionUser = (String) SecurityUtils.getSubject().getPrincipal();
			UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
			userActionBatchPick.setAction_note("BEGIN_"+flowStatus);
			userActionBatchPick.setAction_type(flowStatus);
			userActionBatchPick.setCreated_time(new Date());
			userActionBatchPick.setCreated_user(actionUser);
			userActionBatchPick.setStatus(batchPick.getStatus());
			userActionBatchPick.setBatch_pick_id(batchPick.getBatch_pick_id());
			userActionBatchPickDao.insert(userActionBatchPick);
		}else if(!flowStatus.equalsIgnoreCase(checkFlowStatus)){ 
			throw new RuntimeException("此波次"+batchPickSn+"已经走了其他操作流程！");
		}else if(flowStatus.equalsIgnoreCase(checkFlowStatus) && "BATCH_TRICK".equalsIgnoreCase(checkFlowStatus)){
			//大后门不允许在打印入口重复打印
			throw new RuntimeException("此波次"+batchPickSn+"已经操作过打印快递面单！");
		}
	}

	@Override
	public Integer loadTrickBatchPickSn(String batchPickSn) {
		//16年10月10日增加 ： batch_pick状态flow_status为SHIPMENT_Y 才可操作
		this.checkBatchPickFlowStatus(batchPickSn,"BATCH_TRICK");
		//UPDATE order_process:batch_trick_status INIT-->SUCCESS
		BatchPick batchPick = batchPickDao.selectbatchPickBySnForRecheck(batchPickSn);
		List<Integer> orderIdList = orderProcessDao.selectOrderIdsByBatchPickId(batchPick.getBatch_pick_id());
		orderProcessDao.selectOrdersForLock2Task(orderIdList); //for update ，与取消 防并发 
		Integer col = orderProcessDao.updateBatchTrickStatus(batchPick.getBatch_pick_id(),"INIT","SUCCESS");
		Assert.isTrue(col>0,"波次"+batchPickSn+"包含订单状态属性更新失败！");
		//ADD print记录 
		String actionUser = (String) SecurityUtils.getSubject().getPrincipal();
		UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
		userActionBatchPick.setAction_note("PRINT_BATCH_TRICK");
		userActionBatchPick.setAction_type("BATCH_TRICK");
		userActionBatchPick.setCreated_time(new Date());
		userActionBatchPick.setCreated_user(actionUser);
		userActionBatchPick.setStatus(batchPick.getStatus());
		userActionBatchPick.setBatch_pick_id(batchPick.getBatch_pick_id());
		userActionBatchPickDao.insert(userActionBatchPick);
		return batchPick.getBatch_pick_id();
	}

	@Override
	public Integer reLoadTrickBatchPickSn(String batchPickSn) {
		//16年10月10日增加 ： batch_pick状态flow_status为BATCH_TRICK 才可操作
		BatchPick batchPick = batchPickDao.selectbatchPickBySnForRecheck(batchPickSn);
		if(WorkerUtil.isNullOrEmpty(batchPick)) {
			throw new RuntimeException("根据波次单号没有查到信息!"+batchPickSn);
		}
		Integer batchPickId = batchPick.getBatch_pick_id();
		String batchProcessType = batchPick.getBatch_process_type();
		String checkFlowStatus = batchPick.getFlow_status();
		Assert.isTrue("BATCH".equalsIgnoreCase(batchProcessType), "波次"+batchPickSn+"不是批量波次，不允许进行此项操作!");
		if(!"BATCH_TRICK".equalsIgnoreCase(checkFlowStatus)){
			throw new RuntimeException("此波次"+batchPickSn+"复核流程状态不正确，不允许操作补打印快递面单（"+checkFlowStatus+"）");
		}
		//ADD print记录 
		String actionUser = (String) SecurityUtils.getSubject().getPrincipal();
		UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
		userActionBatchPick.setAction_note("PRINT_BATCH_TRICK");
		userActionBatchPick.setAction_type("BATCH_TRICK");
		userActionBatchPick.setCreated_time(new Date());
		userActionBatchPick.setCreated_user(actionUser);
		userActionBatchPick.setStatus(batchPick.getStatus());
		userActionBatchPick.setBatch_pick_id(batchPickId);
		userActionBatchPickDao.insert(userActionBatchPick);
		return batchPickId;
	}


	@Override
	public void batchApplyShipmentTnForFirstShipment(
			List<Map<String, Object>> shippingAppOrders, String shippingCode) {
		Shipping shipping = shippingDao.selectByShippingCode(shippingCode);
		List<String> trackingNumbers = new ArrayList<String>();
		for (Map<String, Object> map : shippingAppOrders) {
			List<Map<String, Object>> orderInfo = (List<Map<String, Object>>) map
					.get("orderMarkInfo"); // 包含大头笔信息

			Integer shippingAppId = Integer.parseInt(String.valueOf(map
					.get("shipping_app_id")));
			logger.info("shipping_app_id" + shippingAppId + " : orderInfoSize " + orderInfo.size());
			Integer trackingNumberSize = orderInfo.size();

			trackingNumbers = shippingTrackingNumberRepositoryDao
					.getTrackingNumbers(shippingAppId, trackingNumberSize);
			Assert.isTrue(!WorkerUtil.isNullOrEmpty(trackingNumbers),
					shipping.getShipping_name() + "热敏资源库已空，缺失" + trackingNumberSize + "个");
			Assert.isTrue(trackingNumbers.size() == trackingNumberSize,
					shipping.getShipping_name() + "热敏资源库不足，缺失"
							+ (trackingNumberSize - trackingNumbers.size())
							+ "个");
			Integer effectRows = shippingTrackingNumberRepositoryDao
					.updateRepositoryTrackingNumbers(trackingNumbers, "R");
//			logger.info(shippingCode+"更新面单数量："+effectRows);
//			logger.info("需要申请数量"+trackingNumberSize);
			Assert.isTrue(effectRows.equals(trackingNumberSize), shipping.getShipping_name()
					+ "资源抢占失败,请重试");
			Integer shippingId = Integer.parseInt(String.valueOf(orderInfo.get(
					0).get("shipping_id")));
//			logger.info("orderinfosize:" + orderInfo.size());
			for (int i = 0; i < orderInfo.size(); i++) {
				Integer orderId = Integer.parseInt(String.valueOf(orderInfo
						.get(i).get("order_id")));
				String mark = String.valueOf(orderInfo.get(i).get("mark"));
				String orderStatus = String.valueOf(orderInfo.get(i).get(
						"order_status"));
				Map<String, Object> map2 = createShipmentTrackingNumberForFirstShipment(
						orderId, shippingId, trackingNumbers.get(i), mark,
						orderStatus);
				Assert.isTrue(!Response.FAILURE.equals(map2.get("result")), ""
						+ map2.get("note"));
			}
			shippingTrackingNumberRepositoryDao
					.updateRepositoryTrackingNumbers(trackingNumbers, "Y");

		}
	}

	private Map<String, Object> createShipmentTrackingNumberForFirstShipment(Integer orderId,
			Integer shippingId, String trackingNumber, String mark,
			String orderStatus) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			Shipment s = new Shipment();
			s.setOrder_id(orderId);
			s.setStatus("INIT");
			s.setShipping_id(shippingId);
			s.setCreated_user("SYSTEM");
			s.setCreated_time(new Date());
			s.setLast_updated_user("SYSTEM");
			s.setLast_updated_time(new Date());
			s.setShipment_order_sequence_number(1);
			s.setTracking_number(trackingNumber);
			s.setMark(mark);
			shipmentDao.createShipmentTrackingNumber(s);
			Integer shipment_id = s.getShipment_id();
			UserActionShipment userActionShipment = new UserActionShipment();
			userActionShipment.setAction_note("CREATE_SHIPMENT");
			userActionShipment.setAction_type("CREATE_SHIPMENT");
			userActionShipment.setCreated_user("SYSTEM");
			userActionShipment.setCreated_time(new Date());
			userActionShipment.setShipment_id(shipment_id);
			userActionShipment.setStatus("INIT");
			userActionShipmentDao
					.insertUserActionShipmentRecord(userActionShipment);
			UserActionShipment userActionShipment2 = new UserActionShipment();
			userActionShipment2.setAction_note("APPLY_MAILNOS:"
					+ trackingNumber);
			userActionShipment2.setAction_type("APPLY_MAILNOS");
			userActionShipment2.setCreated_user("SYSTEM");
			userActionShipment2.setCreated_time(new Date());
			userActionShipment2.setShipment_id(shipment_id);
			userActionShipment2.setStatus("INIT");
			userActionShipmentDao
					.insertUserActionShipmentRecord(userActionShipment2);
			orderInfoDao.updateTrackingNumber(orderId, trackingNumber);
		} catch (Exception e) {
//			logger.error("订单" + orderId + "更新快递面单失败:"+e.getMessage(),e);
			throw new RuntimeException("订单" + orderId + "更新快递面单失败");
		}
		Integer col = orderProcessDao.updateIsFirstShipment(orderId, 0, 1);
		Assert.isTrue(col>0, "订单"+orderId+"已经有其他方式申请面单成功了，请重新尝试！");
		resMap.put("result", Response.SUCCESS);

		return resMap;
	}

	@Override
	public Map<String, Object> applyShipmentTnForFirstShipment(
			Integer orderId) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		// 1. 订单状态再验证
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(orderId);
		// 4. 创建发货单+申请快递单号
		Integer shippingId = orderInfo.getShipping_id();
		// 订单同步时已经设定，排除非空可能
		// 创建发货单
		logger.info("订单" + orderId + " system begin to create shipment");
		Integer shipmentId = 0;
		try {
			shipmentId = createShipmentForFirstShipment(orderId, shippingId);
		} catch (RuntimeException e) {
			logger.info("订单" + orderId + " create shipment failed!"
					+ e.getMessage());
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "订单" + orderId + "创建发货单失败！");
			return resMap;
		}
		logger.info("订单" + orderId + " create shipment end,shipmentId is "
				+ shipmentId);

		// 获取运单号
		Map<String, Object> traMap = applyMailno("APPLY", orderId, shipmentId);
		if (!Response.SUCCESS.equals(traMap.get("result"))) {
			return traMap;
		}
		String trackingNumber = String.valueOf(traMap.get("tracking_number"));
		String mark = String.valueOf(traMap.get("mark"));

		// 更新 订单面单信息，快递资源数据
		try {
			shipmentDao.updateTrackingNumber(shipmentId, trackingNumber, mark);
			if (traMap.get("origincode") != null) {
				String origincode = String.valueOf(traMap.get("origincode"));
				String destcode = String.valueOf(traMap.get("destcode"));
				shipmentDao.updateSfShipment(shipmentId, origincode, destcode);
			}
			orderInfoDao.updateTrackingNumber(orderId, trackingNumber);
		} catch (RuntimeException e) {
			logger.info("更新运单号绑定关系失败！shipmentId:" + shipmentId
					+ " , trackingNumber :" + trackingNumber);
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "订单" + orderId + "更新运单号绑定关系失败！快递单号 :"
					+ trackingNumber);
		}
		Integer col = orderProcessDao.updateIsFirstShipment(orderId, 0, 1);
		Assert.isTrue(col>0, "订单"+orderId+"已经有其他方式申请面单成功了，请重新尝试！");
		resMap.put("result", Response.SUCCESS);
		return resMap;
	}


	@Override
	public void updatePalletPrintCount(List<String> codeList) {
		palletDao.updatePalletPrintCount(codeList);
	}

	@Override
	public void applyFirstShipmentForCancelOrder(Integer batchPickId) {
		List<Integer> cancelOrderIdList=orderProcessDao.getAllCancelForFirstShipmentOrderByBatchPickId(batchPickId);
		if(!WorkerUtil.isNullOrEmpty(cancelOrderIdList)){
			for (Integer orderId : cancelOrderIdList) {
				OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(orderId);
				Integer shippingId = orderInfo.getShipping_id();
				Integer shipmentId = 0;
				try {
					shipmentId = createShipmentForFirstShipment(orderId, shippingId);
				} catch (RuntimeException e) {
					throw new RuntimeException("订单" + orderId + "创建发货单失败！");
				}
				String trackingNumber = "CANCEL"+orderId+shipmentId;
				String mark = orderInfo.getProvider_name()+""+orderInfo.getCity_name();
				try {
					shipmentDao.updateTrackingNumber(shipmentId, trackingNumber, mark);
					orderInfoDao.updateTrackingNumber(orderId, trackingNumber);
				} catch (RuntimeException e) {
					throw new RuntimeException("(已取消)订单" + orderId +"更新运单号绑定关系失败！");
				}
				Integer col = orderProcessDao.updateIsFirstShipment(orderId, 0, 1);
				Assert.isTrue(col>0, "订单"+orderId+"已经有其他方式申请面单成功了，请重新尝试！");
			}
		}
	}

	@Override
	public void applyShipmentTnForCancelOrder(Integer orderId) {
		//当前绑定信息查询(存在发货单且不存在绑定耗材关系，与前一步查询数据不一致。极有可能是有人在操作。拦截)
		List<Map> bindShipmentGoodsInfo = shipmentDao
				.selectBindShipmentGoodsInfoByOrderId(orderId);
		Assert.isTrue(
				!(bindShipmentGoodsInfo.size() > 0 && bindShipmentGoodsInfo
						.get(0).get("bind_pack").toString().equals("N")), "订单"
						+ orderId + "正在操作中");
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(orderId);
		Integer shippingId = orderInfo.getShipping_id();
		Integer shipmentId = 0;
		try {
			shipmentId = createShipment(orderId, shippingId);
		} catch (RuntimeException e) {
			throw new RuntimeException("订单" + orderId + "创建发货单失败！");
		}
		String trackingNumber = "CANCEL"+orderId+shipmentId;
		String mark = orderInfo.getProvider_name()+""+orderInfo.getCity_name();
		try {
			shipmentDao.updateTrackingNumber(shipmentId, trackingNumber, mark);
		} catch (RuntimeException e) {
			throw new RuntimeException("(已取消)订单" + orderId +"更新运单号绑定关系失败！");
		}
	}

	@Override
	public Map<String, Object> reLoadBatchPickSnForRePrint(String batchPickSn) {
		Map<String, Object> resultMap = new HashMap<String,Object>();
		// batch_pick状态flow_status为BATCH_RECHECK 才可操作
		BatchPick batchPick = batchPickDao.selectbatchPickBySnForRecheck(batchPickSn);
		if(WorkerUtil.isNullOrEmpty(batchPick)) {
			throw new RuntimeException("根据波次单号没有查到信息!"+batchPickSn);
		}
		Integer batchPickId = batchPick.getBatch_pick_id();
		String batchProcessType = batchPick.getBatch_process_type();
		String checkFlowStatus = batchPick.getFlow_status();
		Assert.isTrue("BATCH".equalsIgnoreCase(batchProcessType), "波次"+batchPickSn+"不是批量波次，不允许进行此项操作!");
		if(!"BATCH_RECHECK".equalsIgnoreCase(checkFlowStatus)){
			throw new RuntimeException("此波次"+batchPickSn+"只能在正常批量复核完成后操作补打（"+checkFlowStatus+"）");
		}
		List<String> list = orderProcessDao.getOrderStatusList(batchPickSn);
		for (String orderStatus : list) {
			Assert.isTrue(OrderInfo.ORDER_STATUS_CANCEL.equals(orderStatus) || 
					OrderInfo.ORDER_STATUS_RECHECKED.equals(orderStatus),"波次"+batchPickSn+"中订单状态不是“已复核”-"+orderStatus);
		}		
		//ADD print记录 
		String actionUser = (String) SecurityUtils.getSubject().getPrincipal();
		UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
		userActionBatchPick.setAction_note("RE_PRINT_BATCH_RECHECK");
		userActionBatchPick.setAction_type("BATCH_RECHECK");
		userActionBatchPick.setCreated_time(new Date());
		userActionBatchPick.setCreated_user(actionUser);
		userActionBatchPick.setStatus(batchPick.getStatus());
		userActionBatchPick.setBatch_pick_id(batchPickId);
		userActionBatchPickDao.insert(userActionBatchPick);
		//查询batchpick下总共有的快递单数量
		Integer packNum = shipmentDao.getPackNumByBatchPickId(batchPickId);
		resultMap.put("result", Response.SUCCESS);
		resultMap.put("packNum", packNum);
		resultMap.put("batchPickId", batchPickId);
		return resultMap;
	}

	//批量波次查询面单序号 
	@Override
	public Map<String, Object> searchTnsSequence(String batchPickSn) {
		Map<String, Object> resultMap = new HashMap<String,Object>();
		List<Map<String,Object>> list = batchPickDao.searchTnsSequence(batchPickSn);
		if(WorkerUtil.isNullOrEmpty(list)) {
			throw new RuntimeException("根据波次单号没有查到信息!"+batchPickSn);
		}
		
		String batchProcessType = String.valueOf(list.get(0).get("batch_process_type"));
		Assert.isTrue("BATCH".equalsIgnoreCase(batchProcessType), "波次"+batchPickSn+"不是批量波次，不允许进行此项操作!");
		resultMap.put("result", Response.SUCCESS);
		resultMap.put("list", list);
		return resultMap;
	}

	@Override
	public void checkRecheckBatchForPickSn(String batchPickSn) {
		String needBatchSn = configDao.getConfigValueByConfigName(RECHECK_NEED_BATCH);
		if("Y".equalsIgnoreCase(needBatchSn)){
			//查询波次中是否存在需要扫描批次号的商品
			List<Product> list = batchPickDao.checkNeedBatchSnByPickSn(batchPickSn);
			Assert.isTrue(WorkerUtil.isNullOrEmpty(list), "波次单"+batchPickSn+"有"+list.size()+"种商品需要维护批次号，请操作杂单复核");
		}
	}

	@Override
	public String selectOrderStatusByOrderOrTrackingNumber(Integer orderId, String trackingNumber) {
		OrderInfo orderInfo = null;
		if(!"".equalsIgnoreCase(trackingNumber)){
			orderId = shipmentDao.selectShipmentByTrackingNumber(trackingNumber);
		}
		if(WorkerUtil.isNullOrEmpty(orderId)) return "";
		orderInfo = orderInfoDao.selectByPrimaryKey(orderId);
		if(WorkerUtil.isNullOrEmpty(orderInfo)) return "";
		return orderInfo.getOrder_status();
	}

}