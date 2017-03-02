/**
 * 订单相关（包括订单数据查询、订单数据修改等）
 */
package com.leqee.wms.biz.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.api.util.Tools;
import com.leqee.wms.biz.HTBiz;
import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.OrderGoodsBiz;
import com.leqee.wms.biz.SFBiz;
import com.leqee.wms.biz.ShipmentBiz;
import com.leqee.wms.biz.ShipmentDetailBiz;
import com.leqee.wms.biz.ZTOBiz;
import com.leqee.wms.dao.BatchPickDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.LabelAcceptDao;
import com.leqee.wms.dao.LocationDao;
import com.leqee.wms.dao.OrderDao;
import com.leqee.wms.dao.OrderGoodsBatchDao;
import com.leqee.wms.dao.OrderGoodsDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ProductLocationDetailDao;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.ShipmentDetailDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.dao.UserActionOrderDao;
import com.leqee.wms.dao.UserActionShipmentDao;
import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.InventoryItem;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.OmsOrderTransferCode;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderGoodsBatch;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderProcess;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ProductLocationDetail;
import com.leqee.wms.entity.ShipmentDetail;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Task;
import com.leqee.wms.entity.TaskAllotProductlocation;
import com.leqee.wms.entity.UserActionOrder;
import com.leqee.wms.entity.UserActionShipment;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.LockUtil;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;


/**
 * @author Jarvis
 * @CreatedDate 2016.02.01
 * @version 0.1
 *
 */
@Service
public class OrderBizImpl implements OrderBiz {
	Logger logger = Logger.getLogger(OrderBizImpl.class);
	
	@Autowired
	OrderDao orderDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderGoodsDao orderGoodsDao;
	@Autowired
	ProductDao productDao;
	@Autowired
	InventoryBiz inventoryBiz;
	@Autowired
	ShipmentDao shipmentDao;
	@Autowired
	ShipmentDetailDao shipmentDetailDao;
	@Autowired
	ShipmentDetailBiz shipmentDetailBiz;
	@Autowired
	ShipmentBiz shipmentBiz;
	@Autowired
	OrderGoodsBiz orderGoodsBiz;
	@Autowired
	InventoryDao inventoryDao;
	@Autowired
	UserActionOrderDao userActionOrderDao;
	@Autowired
	OrderProcessDao orderProcessDao;
	@Autowired
	ShippingDao shippingDao;
	@Autowired
	BatchPickDao batchPickDao;
	@Autowired
	TaskDao taskDao;
	@Autowired
	SFBiz sfBiz;
	@Autowired
	ZTOBiz ztoBiz;
	@Autowired
	HTBiz htBiz;
	@Autowired
	LabelAcceptDao labelAcceptDao;
	@Autowired
	ProductLocationDao productLocationDao;
	@Autowired
	LocationDao locationDao;
	@Autowired
	ProductLocationDetailDao productLocationDetailDao;
	@Autowired
	UserActionShipmentDao userActionShipmentDao;
	@Autowired
	OrderGoodsBatchDao orderGoodsBatchDao;
	
	private OrderInfoDao orderInfoDaoSlave;
    @Resource(name = "sqlSessionSlave")
	public void setOrderInfoDaoSlave(SqlSession sqlSession) {
	  this.orderInfoDaoSlave = sqlSession.getMapper(OrderInfoDao.class);
	}
	
	public OrderInfo getOrderInfoByOrderId(Integer orderId){
		
		OrderInfo orderInfo = orderDao.selectOrderInfoByOrderId(orderId);
		
		return orderInfo;
	}
	
	public List<OrderGoods> getOrderGoodsByOrderId(Integer orderId){
		
		List<OrderGoods> orderGoodsList = orderDao.selectOrderGoodsByOrderId(orderId);
		
		return orderGoodsList;
	}
	
	// eg. 根据orderStatus查询订单信息
	@Override
	public List<Map> selectOrderInfoListByOrderStatus(String orderStatus) {
		List<Map> orderInfoList = orderDao.selectOrderInfoListByOrderStatus(orderStatus);
		return orderInfoList;
	}
	
	@Override
	public List<Map> selectSaleOrderGoodsMapByOrderId(Integer orderId) {
		List<Map> saleOrderGoodsMap = orderDao.selectSaleOrderGoodsMapByOrderId(orderId);
		return saleOrderGoodsMap;
	}

	// 根据不同条件查询待退货入库退货订单列表
	@Override
	public List<OrderInfo> getReturnOrderInfoList(
			HashMap<String, Object> conditions) {
		// 根据不同条件查询待退货入库退货订单列表
		List<OrderInfo> orderInfoList = orderInfoDao.selectReturnOrderInfoListByPage(conditions);
		
		// 补充商品信息
		if(!WorkerUtil.isNullOrEmpty(orderInfoList)){
			for (OrderInfo orderInfo : orderInfoList) {
				List<OrderGoods> orderGoodslist = orderGoodsDao.selectByOrderId(orderInfo.getOrder_id());
				
				for (OrderGoods orderGoods : orderGoodslist) {
					Product product = productDao.selectByPrimaryKey(orderGoods.getProduct_id());
					orderGoods.setBarcode(product.getBarcode());
				}
				orderInfo.setOrderGoodsList(orderGoodslist);
			}
		}
		return orderInfoList;
	}

	// 获取待入库退货订单数量
	@Override
	public int getCountOfUnfulfilledReturnOrderInfo(List<Integer> warehouseIds,String customerString) {
		int countUnfulfilledReturnOrderInfo = 0;
		countUnfulfilledReturnOrderInfo = orderInfoDao.selectCountUnfulfilledReturnOrderInfo(warehouseIds,customerString);
		return countUnfulfilledReturnOrderInfo;
	}

	// 获取退货商品扫描结果
		@Override
		public Map<String, Object> getReturnOrderGoodsScanResult(String key,
				int orderId) {
			// 初始化
			Map<String, Object> result = new HashMap<String, Object>();
			// 标记用户输入的条码（串号）的查找结果  
			// 0:未找到用户输入的条码（串号） 1:找到了用户输入的条码  2:找到了用户输入的串号  3:用户输入了商品条码，但该商品为串号控制商品  4:用户输入了正确的串号，但该串号商品已经在库存里
			int keyStatus = 0;
			int productId = 0;
			//标记是否需要维护生产日期
			boolean validityFlag = false;
			//标记是否维护批次号 0代表不维护   1 代表维护  
			Integer batchSnFlag = 0;
			
			Integer customerId = null;
			List<Integer> goodIndexList = new ArrayList<Integer>();
			List<Integer> orderGoodsIdList = new ArrayList<Integer>();
			
			// 查找订单的商品列表
			List<OrderGoods> orderGoodsList = orderGoodsDao.selectByOrderId(orderId);
			for (OrderGoods orderGoods : orderGoodsList) {
				customerId = orderGoods.getCustomer_id();
			}
			
			//查询该商品是否维护生产日期
			Product product1 = productDao.selectProductByBarcodeCustomer(key, customerId);
			if("Y".equals(product1.getIs_maintain_warranty())){
				validityFlag = true;
			}
			//查询该商品是否维护批次号
			if("Y".equals(product1.getIs_maintain_batch_sn())){
				batchSnFlag = 1;
			}
			int i = -1;
			for (OrderGoods orderGoods : orderGoodsList) {
				i++;
				Product product = productDao.selectByPrimaryKey(orderGoods.getProduct_id());
				
				if("N".equals(product.getIs_serial())){   // 非串号控制商品
					if(key.equals(product.getBarcode())){  // 找到了用户输入的条码
						keyStatus = 1;
						goodIndexList.add(Integer.valueOf(i));
						orderGoodsIdList.add(orderGoods.getOrder_goods_id());
						productId = orderGoods.getProduct_id();
					}
				}else if("Y".equals(product.getIs_serial())){  // 串号控制商品
					if(key.equals(product.getBarcode())){  // 用户输入了商品条码，但该商品为串号控制商品
						keyStatus = 3;
						goodIndexList.add(Integer.valueOf(i));
						orderGoodsIdList.add(orderGoods.getOrder_goods_id());
					}else{
						// 找到销售订单出库时的串号
						List<String> outSerialNumberList = inventoryBiz.getOutSerialNumbers(orderId, orderGoods.getProduct_id().intValue());
						
						if(!outSerialNumberList.isEmpty() && outSerialNumberList.contains(key)) {
							// 判断该商品是否已经在库存里面，避免相同串号的商品重复入库
							if(inventoryBiz.isSerialGoodInStock(key)) {  // 用户输入了正确的串号，但该串号商品已经在库存里
								keyStatus = 4;
								goodIndexList.add(Integer.valueOf(i));
								orderGoodsIdList.add(orderGoods.getOrder_goods_id());
							}else{                                       // 找到了用户输入的串号
								keyStatus = 2;
								goodIndexList.add(Integer.valueOf(i));
								orderGoodsIdList.add(orderGoods.getOrder_goods_id());
								productId = orderGoods.getProduct_id();
							}
						}
					}
				}
			}
			
			// 封装结果数据
			result.put("key", key);
			result.put("keyStatus", keyStatus);
			result.put("goodIndex", goodIndexList);
			result.put("productId", productId);
			result.put("orderGoodsIdList", orderGoodsIdList);
			result.put("validityFlag", validityFlag);
			result.put("batchSnFlag", batchSnFlag);


			return result;
		}

	
	/**
	 * -V订单相关操作
	 * @author hzhang1
	 */
	public List<Map> getVarianceOrder(Map map){
		return orderInfoDao.selectVarianceOrderByPage(map);
	}
	
	
	/**
	 * 采购订单通过订单状态，来返回订单详情信息
	 * 未处理的订单：不含有标签保存信息
	 * 其他状态订单：需要返回已保存的标签信息
	 * @author hzhang1
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> selectOrderGoodsByOmsOrderSn(String omsOrderSn,String orderStatus) {
		
		Map<String, Object> resMap = new HashMap<String,Object>();
		List<Map> newOrderListNotAccept = new ArrayList<Map>();
		List<Map> newOrderListYesAccept = new ArrayList<Map>();
		List<String> orderGoodsIdList = new ArrayList<String>();
		Map<String,Object> goodsQuantity = new HashMap<String,Object>();
		
		if (OrderInfo.ORDER_STATUS_ACCEPT.equals(orderStatus) && WorkerUtil.isNullOrEmpty(orderInfoDao.selectOrderGoodsByOmsOrderSnV2(omsOrderSn))) {
			
			List<Map> orderList = orderInfoDao.selectOrderGoodsByOmsOrderSn(omsOrderSn);
			for (Map goodsMap : orderList) {
				goodsMap.put("row_num", 1);
				goodsMap.put("not_accept_goods_number", goodsMap.get("goods_number"));
				newOrderListNotAccept.add(goodsMap);
			}
		} else {
			
			List<Map> orderList = orderInfoDao.selectOrderGoodsByOmsOrderSnV2(omsOrderSn);
			if(WorkerUtil.isNullOrEmpty(orderList)){
				orderList = orderInfoDao.selectOrderGoodsByOmsOrderSn(omsOrderSn);
				for (Map goodsMap : orderList) {
					goodsMap.put("row_num", 1);
					goodsMap.put("not_accept_goods_number", goodsMap.get("goods_number"));
					newOrderListNotAccept.add(goodsMap);
				}
			}else{
				String last_order_goods_id = "";
				int count = 0;
				for (Map goodsMap : orderList) {
					String now_order_goods_id = goodsMap.get("order_goods_id").toString().trim();
					orderGoodsIdList.add(now_order_goods_id);
					Integer quantity = Integer.parseInt(goodsMap.get("quantity").toString());
					Integer productId = Integer.parseInt(goodsMap.get("product_id").toString());
					goodsQuantity.put(productId.toString(), goodsQuantity.containsKey(productId.toString())?Integer.parseInt(goodsQuantity.get(productId.toString()).toString())+quantity:quantity);
					
					int tray_number = Integer.parseInt(goodsMap.get("tray_number").toString().trim());
					if(count == 0){
						count = tray_number;
					}
					if(last_order_goods_id.equals(now_order_goods_id) && tray_number > 1){
						if(count == tray_number){
							goodsMap.put("row_num", tray_number);
						}else{
							goodsMap.put("row_num", 0);
						}
					}else{
						goodsMap.put("row_num", tray_number);
					}
					count--;
					newOrderListYesAccept.add(goodsMap);
					last_order_goods_id = now_order_goods_id;
				}
				
				orderList = orderInfoDao.selectOrderGoodsByOmsOrderSn(omsOrderSn);
				if(!WorkerUtil.isNullOrEmpty(orderList)){
					for (Map goodsMap : orderList) {
						Integer goodsNumber = Integer.parseInt(goodsMap.get("goods_number").toString());
						Integer productId = Integer.parseInt(goodsMap.get("product_id").toString());
						String now_order_goods_id = goodsMap.get("order_goods_id").toString().trim();
						Integer quantity = Integer.parseInt(!goodsQuantity.containsKey(productId.toString())?"0":goodsQuantity.get(productId.toString()).toString());
						if(goodsNumber > quantity){
							goodsMap.put("not_accept_goods_number", goodsNumber - quantity);
							goodsMap.put("row_num", 1);
							newOrderListNotAccept.add(goodsMap);
						}
					}
				}
			}
		
		}
		resMap.put("orderGoodsList", newOrderListNotAccept);
		resMap.put("orderGoodsList2", newOrderListYesAccept);
		return resMap;
	}
	
	public OrderInfo selectOrderByOmsOrderSn(String omsOrderSn){
		return orderInfoDao.selectByOmsOrderSn(omsOrderSn);
	}
	
	public List<Map> selectOrdersByBatchOrderSn(String batchOrderSn){
		// 1.获得session
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 2.获取当前物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        HashMap<String,Object> searchMap = new HashMap<String,Object>();
		searchMap.put("physicalWarehouseId", currentPhysicalWarehouse.getPhysical_warehouse_id());
		searchMap.put("batchOrderSn", batchOrderSn);
		List<Map> batchOrderList = orderInfoDao.selectByBatchOrderSn(searchMap);
		
		return batchOrderList;
	}
	
	public void updateOrderInfoStatusV2(Integer orderId,String orderStatus,String actionUser){
		updateOrderInfoStatus(orderId,orderStatus,actionUser);
		UserActionOrder userActionOrder = new UserActionOrder();
		userActionOrder.setOrder_id(orderId);
		userActionOrder.setOrder_status(orderStatus);
		if(OrderInfo.ORDER_STATUS_IN_PROCESS.equals(orderStatus)){
			userActionOrder.setAction_type("START_RECEIVE");
			userActionOrder.setAction_note("采购订单开始验收");
		}else if(OrderInfo.ORDER_STATUS_ON_SHIP.equals(orderStatus)){
			userActionOrder.setAction_type("FINISH_RECEIVE");
			userActionOrder.setAction_note("采购订单验收完成");
		}else if(OrderInfo.ORDER_STATUS_FULFILLED.equals(orderStatus)){
			userActionOrder.setAction_type("GROUP_SUCCESS");
			userActionOrder.setAction_note("采购订单上架完成");
		}else if(OrderInfo.ORDER_STATUS_ACCEPT.equals(orderStatus)){
			userActionOrder.setAction_type("RESET");
			userActionOrder.setAction_note("采购订单重置");
		}
		userActionOrder.setCreated_user(actionUser);
		userActionOrder.setCreated_time(new Date());
		userActionOrderDao.insert(userActionOrder);
		
	}
	
	public void insertUserActionOrder(Integer orderId,String orderStatus,String actionUser){
		// 记录订单操作日志
				UserActionOrder userActionOrder = new UserActionOrder();
				userActionOrder.setAction_note("取消订单完成退货入库");
				userActionOrder.setAction_type("ACCEPT");
				userActionOrder.setCreated_time(new Date());
				userActionOrder.setCreated_user(actionUser);
				userActionOrder.setOrder_id(orderId);
				userActionOrder.setOrder_status(orderStatus);
				userActionOrderDao.insert(userActionOrder);

				logger.info("取消入库结束,orderId: " + orderId + ", actionUser: "
						+ actionUser);
	}
	
	public Map insertGroupTask(Integer orderId,String orderStatus,Integer physicalWarehouseId,Integer customerId,String tagList,String actionUser){
		updateOrderInfoStatus(orderId,orderStatus,actionUser);
		UserActionOrder userActionOrder = new UserActionOrder();
		userActionOrder.setOrder_id(orderId);
		userActionOrder.setOrder_status(orderStatus);
		userActionOrder.setAction_type("FINISH_RECEIVE");
		userActionOrder.setAction_note("采购订单验收完成");
		userActionOrder.setCreated_user(actionUser);
		userActionOrder.setCreated_time(new Date());
		userActionOrderDao.insert(userActionOrder);
		
		try {
			String tagArr[] = tagList.split(",");
			for(String tag:tagArr){
				
				Task task = new Task();
				Map<String,Object> paramsForSelectTagMap = new HashMap<String,Object>();
				paramsForSelectTagMap.put("physicalWarehouseId", physicalWarehouseId);
				paramsForSelectTagMap.put("customerId", customerId);
				paramsForSelectTagMap.put("locationBarcode", tag.trim());
				Map returnMap = labelAcceptDao.selectLabelAcceptByLocationBarcode(paramsForSelectTagMap);
				
				task.setPhysical_warehouse_id(physicalWarehouseId);
				task.setCustomer_id(customerId);
				task.setTask_type(Task.TASK_TYPE_PUT_AWAY);
				task.setTask_status(Task.TASK_STATUS_INIT);
				task.setTask_level(1);
				task.setProduct_id(Integer.parseInt(returnMap.get("product_id").toString()));
				task.setQuantity(Integer.parseInt(returnMap.get("quantity").toString()));
//				task.setBatch_pick_id(Integer.parseInt(returnMap.get("inventory_location_id").toString()));
//				task.setBatch_task_id(Integer.parseInt(returnMap.get("inventory_location_id").toString()));
				task.setOperate_platform("WEB");
				task.setCreated_user(actionUser);
				task.setLast_updated_user(actionUser);
				taskDao.insert(task);
				
				labelAcceptDao.updateLabelAcceptForTaskId(task.getTask_id(), Integer.parseInt(returnMap.get("inventory_location_id").toString()));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new RuntimeException("上架任务创建失败");
		}
		return null;
	}
	
	public 	Map resetPurchaseOrder(Integer orderId,String tagList,String actionUser){
		
		Map<String,Object> returnMap = new HashMap<String,Object>(); 
		Map<String,Object> searchMap = new HashMap<String,Object>(); 
		
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(tagList);
        tagList = m.replaceAll("");
        
		List<Map> isGroupMap = orderDao.selectPurchaseIsGrouping(orderId);
		if(!WorkerUtil.isNullOrEmpty(isGroupMap)){
			returnMap.put("result", "failure");
			returnMap.put("note", "该订单已有标签上架");
			return returnMap;
		}
		
		try {
			List<String> locationBarcodeList = new ArrayList<String>();
			String arr[] = tagList.split(",");
			for(String str :arr){
				locationBarcodeList.add(str);
			}
			if(!WorkerUtil.isNullOrEmpty(locationBarcodeList)){
				searchMap.put("locationBarcodeList", locationBarcodeList);
				orderDao.deletePurchaseLabel(searchMap);
			}
			updateOrderInfoStatusV2(orderId, OrderInfo.ORDER_STATUS_ACCEPT, actionUser);
		} catch (Exception e) {
			returnMap.put("result", "failure");
			returnMap.put("note", e.getMessage());
			e.printStackTrace();
			return returnMap;
		}
		returnMap.put("result", "success");
		returnMap.put("note", "success");
		return returnMap;
	}
	
	/**
	 * 更新订单状态
	 * @param orderId,orderStatus,actionUser
	 * @author hzhang1
	 */
	public int updateOrderInfoStatus(Integer orderId,String orderStatus,String actionUser){
		
		Map<String,Object> paramsForOrderInfoUpdate = new HashMap<String,Object>();
		paramsForOrderInfoUpdate.put("orderStatus", orderStatus);
		paramsForOrderInfoUpdate.put("lastUpdatedUser", actionUser);
		paramsForOrderInfoUpdate.put("lastUpdatedTime", new Date());
		paramsForOrderInfoUpdate.put("orderId", orderId);
		int col = orderInfoDao.updateOrderStatusByOrderId(paramsForOrderInfoUpdate);
		if(col < 1){
			throw new RuntimeException("更新订单状态失败");
		}
		return col;
	}

	public int updateOrderProcessStatus(Integer orderId,String orderStatus){
		Map<String,Object> paramsForOrderProcessUpdate = new HashMap<String,Object>();
		paramsForOrderProcessUpdate.put("status", orderStatus);
		paramsForOrderProcessUpdate.put("orderId", orderId);
		int col = orderProcessDao.updateOrderStatusByOrderId(paramsForOrderProcessUpdate);
		if(col < 1){
			throw new RuntimeException("更新订单状态失败");
		}
		return col;
	}
	
	/**
	 * 销售订单复核扫描商品
	 * 
	 * @param shipmentId an Integer: 
	 * @param orderGoodsId an Integer: 
	 * @param serialNumber a String: 串号
	 * @return resultMap a Map<String, Object>: 扫描结果
	 * */
	@Override
	public Map<String, Object> getSaleOrderGoodsScanResult(Integer shipmentId,
			Integer orderGoodsId, String serialNumber, String batchSn, Integer time) {
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String result = Response.SUCCESS;
		String note = "扫描商品成功";
		int order_goods_id = 0;
		
		logger.info("（复核）扫描商品，shipmentId: " + shipmentId + ", orderGoodsId: "
				+ orderGoodsId + ", serialNumber: " + serialNumber);

		// Assert判断数据异常，直接抛出异常结束程序执行
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(shipmentId), "数据异常,shipmentId为空!");
		Assert.isTrue(!(WorkerUtil.isNullOrEmpty(orderGoodsId) && WorkerUtil.isNullOrEmpty(serialNumber)),
				"数据异常,orderGoodsId与serialNumber均为空!");
		String localUser = (String) SecurityUtils.getSubject().getPrincipal();
		if(!WorkerUtil.isNullOrEmpty(orderGoodsId)){  // 条码控制商品
			order_goods_id = orderGoodsId.intValue();
			// 查找订单商品记录
			OrderGoods orderGoods = orderGoodsDao.selectByPrimaryKey(orderGoodsId);
			Assert.isTrue(!WorkerUtil.isNullOrEmpty(orderGoods), "数据异常:订单中找不到该商品!orderGoodsId: " + orderGoodsId);
			
			if(!WorkerUtil.isNullOrEmpty(batchSn) && !WorkerUtil.isNullOrEmpty(time) && time == 0 && needBatchSn(orderGoods.getProduct_id()) ) {
				if(!checkBatchSnInBatchPick(batchSn, orderGoods.getOrder_id(), orderGoods.getProduct_id())) {
					return buildBatchSnErrorResultMap(orderGoods.getOrder_id(),orderGoods.getProduct_id(), order_goods_id);
				}
			}
			//增加一层判断： orderGoodsNum>sum(sd.goods_number) 
			Integer rangeNum = shipmentDetailDao.checkDetailNumberByOrderGoodsId(orderGoodsId);
			Assert.isTrue(rangeNum.compareTo(0)>0,"扫描失败,请重新扫描！");
			
			List<ShipmentDetail> shipmentDetails = shipmentDetailBiz.findByShipmentIdAndOrderGoodsId(shipmentId, orderGoodsId);
			
			if(!WorkerUtil.isNullOrEmpty(shipmentDetails)){  // 条码商品且已经有过此面单的绑定记录,更新记录
				ShipmentDetail shipmentDetail = shipmentDetails.get(0);
				Assert.isTrue(shipmentDetail.getGoods_number() < orderGoods.getGoods_number(),
						"数据异常:该商品已经全部绑定到指定的面单!orderGoodsId: " + orderGoodsId);

				shipmentDetail.setGoods_number(shipmentDetail.getGoods_number() + 1);
				int effectRows = shipmentDetailBiz.updateShipmentDetail(shipmentDetail);
				if(effectRows <= 0){
					logger.error("商品绑定面单失败!shipmentDetailId: " + shipmentDetail.getShipment_detail_id());
					result = Response.FAILURE;
					note = "商品绑定面单失败，请重试!";
				}
				UserActionShipment userActionShipment = new UserActionShipment();
				userActionShipment.setShipment_id(shipmentId);
				userActionShipment.setStatus("INIT");
				userActionShipment.setAction_type("update_goodScan");
				userActionShipment.setAction_note("update_goodScan:"+shipmentId+"_"+orderGoodsId);
				userActionShipment.setCreated_user(localUser);
				userActionShipment.setCreated_time(new Date());
				userActionShipmentDao
						.insertUserActionShipmentRecord(userActionShipment);
				this.insertOrUpdateOrderGoodsBatch(shipmentDetail.getOrder_goods_id(), batchSn);
			}else{  // 条码商品还未有过此面单的绑定记录，插入新记录
				ShipmentDetail shipmentDetail = new ShipmentDetail();
				shipmentDetail.setGoods_name(orderGoods.getGoods_name());
				shipmentDetail.setGoods_number(Integer.valueOf(1));
				shipmentDetail.setOrder_goods_id(orderGoods.getOrder_goods_id());
				shipmentDetail.setProduct_id(orderGoods.getProduct_id());
				shipmentDetail.setShipment_id(shipmentId);
				Response response = shipmentDetailBiz.createShipmentDetail(shipmentDetail);
				if(Response.ERROR.equals(response.getCode())){
					logger.error("商品绑定面单失败!orderGoodsId: " + orderGoodsId + ", shipmentId: " + shipmentId);
					result = Response.FAILURE;
					note = "商品绑定面单失败，请重试!";
				}
				UserActionShipment userActionShipment = new UserActionShipment();
				userActionShipment.setShipment_id(shipmentId);
				userActionShipment.setStatus("INIT");
				userActionShipment.setAction_type("insert_goodScan");
				userActionShipment.setAction_note("insert_goodScan:"+shipmentId+"_"+orderGoodsId);
				userActionShipment.setCreated_user(localUser);
				userActionShipment.setCreated_time(new Date());
				userActionShipmentDao
						.insertUserActionShipmentRecord(userActionShipment);
				this.insertOrUpdateOrderGoodsBatch(shipmentDetail.getOrder_goods_id(), batchSn);
			}
			
		}else if(!WorkerUtil.isNullOrEmpty(serialNumber)){  // 串号控制商品
			InventoryItem inventoryItem = inventoryBiz.findOneInventoryItemBySerialNumber(serialNumber);
			// 判断是否有此商品，是否还有库存
			Assert.isTrue(!WorkerUtil.isNullOrEmpty(inventoryItem), "商品扫描完毕/商品在订单中不存在/商品在系统中不存在或无库存!");
			
			// 判断是否已经被绑定到其他面单上面
			Assert.isTrue(!shipmentDetailBiz.isSerialNumberBinded(serialNumber), "该串号商品已经绑定至其他面单，请检查!");
			
			// 尝试寻找可用的orderGoods记录
			OrderGoods orderGoods = findOrderGoodsForShipmentDetail(shipmentId, inventoryItem.getProduct_id());
			Assert.isTrue(!WorkerUtil.isNullOrEmpty(orderGoods), "订单中无此商品，或此商品已经扫描完毕，请确认!");
			
			order_goods_id = orderGoods.getOrder_goods_id();
			
			if(!WorkerUtil.isNullOrEmpty(batchSn) && !WorkerUtil.isNullOrEmpty(time) && time == 0 && needBatchSn(orderGoods.getProduct_id()) ) {
				if(!checkBatchSnInBatchPick(batchSn, orderGoods.getOrder_id(), orderGoods.getProduct_id())) {
					return buildBatchSnErrorResultMap(orderGoods.getOrder_id(),orderGoods.getProduct_id(), order_goods_id);
				}
			}
			
			// 进行绑定（一个串号插入一条记录）
			ShipmentDetail shipmentDetail = new ShipmentDetail();
			shipmentDetail.setGoods_name(orderGoods.getGoods_name());
			shipmentDetail.setGoods_number(Integer.valueOf(1));
			shipmentDetail.setOrder_goods_id(orderGoods.getOrder_goods_id());
			shipmentDetail.setProduct_id(orderGoods.getProduct_id());
			shipmentDetail.setShipment_id(shipmentId);
			shipmentDetail.setSerial_number(serialNumber);
			Response response = shipmentDetailBiz.createShipmentDetail(shipmentDetail);
			if(Response.ERROR.equals(response.getCode())){
				logger.error("商品绑定面单失败!serialNumber: " + serialNumber + ", shipmentId: " + shipmentId);
				result = Response.FAILURE;
				note = "商品绑定面单失败，请重试！";
			}
			UserActionShipment userActionShipment = new UserActionShipment();
			userActionShipment.setShipment_id(shipmentId);
			userActionShipment.setStatus("INIT");
			userActionShipment.setAction_type("insert_goodScan:serialNumber_" + serialNumber);
			userActionShipment.setAction_note("insert_goodScan:"+shipmentId+"_"+orderGoodsId);
			userActionShipment.setCreated_user(localUser);
			userActionShipment.setCreated_time(new Date());
			userActionShipmentDao
					.insertUserActionShipmentRecord(userActionShipment);
			
			this.insertOrUpdateOrderGoodsBatch(shipmentDetail.getOrder_goods_id(), batchSn);
		}
		
		// 封装返回结果
		resultMap.put("result", result);
		resultMap.put("note", note);
		resultMap.put("order_goods_id", order_goods_id);
		
		return resultMap;
	}
	
		
	/**
	 * @param order_id
	 * @param product_id
	 * @param order_goods_id
	 * @return
	 */
	private Map<String, Object> buildBatchSnErrorResultMap(Integer order_id, Integer product_id, int order_goods_id) {
		List<String> list = batchPickDao.getBatchSnListByOrderAndProduct(order_id, product_id);
		String note = "扫描批次号错误！系统分配批次号【" + StringUtils.join(list.toArray(),",") + "】，请核实后重新扫描!";
		Map<String ,Object> param = new HashMap<String, Object>();
		param.put("order_goods_id", order_goods_id);
		param.put("error_type", "batch_error");
		
		return buildResultMap(Response.FAILURE, note, param);
	}

	/**
	 * @param result
	 * @param note
	 * @param param
	 * @return
	 */
	private Map<String, Object> buildResultMap(String result, String note, Map<String, Object> param) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap .put("result", result);
		resultMap.put("note", note);
		
		if(param.containsKey("order_goods_id")) {
			resultMap.put("order_goods_id", param.get("order_goods_id"));
		}
		
		if(param.containsKey("error_type")) {
			resultMap.put("error_type", param.get("error_type"));
		}
		return resultMap;
	}

	/**
	 * @param batchSn
	 * @param order_id
	 * @param product_id
	 */
	private boolean checkBatchSnInBatchPick(String batchSn, Integer order_id, Integer product_id) {
		List<String> batchSnList = batchPickDao.getBatchSnListByOrderAndProduct(order_id, product_id);
		if(WorkerUtil.isNullOrEmpty(batchSnList)) {
			return false;
		}
		if(!batchSnList.contains(batchSn)) {
			return false;
		}
		return true;
	}

	/**
	 * @param product_id
	 * @return
	 */
	private boolean needBatchSn(Integer product_id) {
		Product product = productDao.selectByPrimaryKey(product_id);
		return "Y".equals(product.getIs_maintain_batch_sn());
	}

	private void insertOrUpdateOrderGoodsBatch(int orderGoodsId, String batchSn) {
		if(WorkerUtil.isNullOrEmpty(batchSn))
			return;
		OrderGoodsBatch orderGoodsBatch = null;
		orderGoodsBatch = orderGoodsBatchDao.selectByOrderGoodsIdAndBatchSn(orderGoodsId, batchSn);
		if(orderGoodsBatch == null) {
			orderGoodsBatch = new OrderGoodsBatch();
			orderGoodsBatch.setOrder_goods_id(orderGoodsId);
			orderGoodsBatch.setBatch_sn(batchSn);
			orderGoodsBatch.setNum(1);
			orderGoodsBatch.setCreated_user((String)SecurityUtils.getSubject().getPrincipal());
			orderGoodsBatch.setCreated_time(new Date());
			orderGoodsBatchDao.insert(orderGoodsBatch);
			return;
		}
		orderGoodsBatch.setNum(orderGoodsBatch.getNum() + 1); 
		orderGoodsBatchDao.updateByPrimaryKeySelective(orderGoodsBatch);
	}
	

	/**
	 * 尝试查找适用于此串号商品的OrderGoods记录
	 * @param shipmentId an Integer
	 * @param productId an Integer
	 * @return orderGoods an OrderGoods: 
	 * */
	private OrderGoods findOrderGoodsForShipmentDetail(Integer shipmentId,
			Integer productId) {
		// 查找订单中的商品列表
		List<OrderGoods> orderGoodsList = orderGoodsBiz.findByShipmentIdAndProductId(shipmentId, productId);
		if(!WorkerUtil.isNullOrEmpty(orderGoodsList)){
			for (OrderGoods orderGoods : orderGoodsList) {
				int sumOfBindedGoodsNumber = 0;
				List<ShipmentDetail> shipmentDetails = shipmentDetailBiz.findByShipmentIdAndOrderGoodsId(shipmentId, orderGoods.getOrder_goods_id());
				if(!WorkerUtil.isNullOrEmpty(shipmentDetails) && shipmentDetails.size() > 0){
					for (ShipmentDetail shipmentDetail : shipmentDetails) {
						sumOfBindedGoodsNumber += shipmentDetail.getGoods_number();
					}
				}
				if(orderGoods.getGoods_number() > sumOfBindedGoodsNumber)
					return orderGoods;
			}
		}
			
		return null;
	}

	
	
	
	/**
	 * 查找销售订单List（分页）
	 * @author hzhang1
	 */
	@Override
	public List<Map> getSaleOrderList(Map map,String param) {
		
		List<Map> saleOrderList = new ArrayList<Map>();
		List<Map> tempSaleOrderList = new ArrayList<Map>();
		List<Map> newSaleOrderList = new ArrayList<Map>();
		if("search".equals(map.get("type").toString())){
			if("batch_pick_sn".equals(param)){
				newSaleOrderList = orderInfoDaoSlave.selectSaleOrderListByBatchPickSnByPage(map);
			}else{
				if("order_id".equals(param)){
					saleOrderList = orderInfoDaoSlave.selectSaleOrderListByOrderIdByPage(map);
				}else if("shop_order_sn".equals(param)){
					saleOrderList = orderInfoDaoSlave.selectSaleOrderListByOrderIdByPage(map);
				}else if("general".equals(param)){
					saleOrderList = orderInfoDaoSlave.selectSaleOrderListByPage(map);
				}
				if(WorkerUtil.isNullOrEmpty(saleOrderList)){
					return saleOrderList;
				}
				Map<String,Object> orderIdBatchPickSnMap = new HashMap<String,Object>();
				tempSaleOrderList = orderInfoDao.selectSaleOrderIdBatchPickSnMap(saleOrderList);
				for(Map tempMap:tempSaleOrderList){
					orderIdBatchPickSnMap.put(tempMap.get("order_id").toString(), tempMap.get("batch_pick_sn"));
				}
				for(Map tempMap:saleOrderList){
					tempMap.put("batch_pick_sn", orderIdBatchPickSnMap.get(tempMap.get("order_id").toString()));
					newSaleOrderList.add(tempMap);
				}
			}
		}else if("export".equals(map.get("type").toString())){
			newSaleOrderList = orderInfoDaoSlave.selectSaleOrderListForExport(map);
		}
		
		return newSaleOrderList;
	}
	
	@Override
	public Map<String, Object> getSaleOrderInfoById(Map map) {
		
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		Integer orderId = Integer.valueOf(map.get("orderId").toString());
		returnMap.put("orderDetial", orderInfoDao.selectSaleOrderById(map));
		returnMap.put("orderGoodsDetial", orderInfoDao.selectSaleOrderGoodsById(map));
		returnMap.put("orderActionDetial", orderInfoDao.selectSaleOrderActionById(map));
		
		return returnMap;
	}
	
	@Override
	public List<Map> getSaleOrderById(Map map) {
		return orderInfoDao.selectSaleOrderById(map);
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map> getSaleOrderGoodsById(OrderInfo orderInfo) {
		
		Map<String,Object> searchMap = new HashMap<String,Object>();
		searchMap.put("orderId", orderInfo.getOrder_id());
		String orderStatus = orderInfo.getOrder_status();
		
		List<Map> saleOrderGoodsList = new ArrayList<Map>();
		List<Map> saleOrderGoodsListV2 = new ArrayList<Map>();
		List<Map> newOrderList = new ArrayList<Map>();
		List<Map> newGoodsList = new ArrayList<Map>();
		// 根据订单状态分两种方式加载包裹商品
		// 对于【波次】状态，如果此时正在进行复核，需要把有面单 & 无面单的都展示，无扫描到面单的商品统一放在一起
		if(orderStatus.equals(OrderInfo.ORDER_STATUS_PICKING)
				|| orderStatus.equals(OrderInfo.ORDER_STATUS_RECHECKED)
				|| orderStatus.equals(OrderInfo.ORDER_STATUS_DELEVERED)
				|| orderStatus.equals(OrderInfo.ORDER_STATUS_FULFILLED)
				|| orderStatus.equals(OrderInfo.ORDER_STATUS_WEIGHED)){
			
			// 找出订单内的所有商品，并且保存至map。目的：对于订单内未被扫描的商品，到时需要统一放在一起。
			saleOrderGoodsListV2 = orderInfoDao.selectSaleOrderGoodsById(searchMap);
			Map<String,Object> orderGoodsMap = new HashMap<String,Object>();
			for(Map map : saleOrderGoodsListV2){
				String goodId = map.get("order_goods_id").toString();
				orderGoodsMap.put(goodId, map);
			}
			
			// 找出已经被扫描至面单号的商品，如果结果是null（没有扫描过商品），则显示所有商品，并返回。
			saleOrderGoodsList = orderInfoDao.selectSaleOrderGoodsByIdV2(searchMap);
			if(WorkerUtil.isNullOrEmpty(saleOrderGoodsList)){
				for(Map map : saleOrderGoodsListV2){
					Integer warehouseId = Integer.valueOf(map.get("warehouse_id").toString());
					Integer productId = Integer.valueOf(map.get("product_id").toString());
					Map<String,Object> inventoryMap = new HashMap<String,Object>();
					inventoryMap.put("warehouseId", orderInfo.getPhysical_warehouse_id());
					inventoryMap.put("productId", productId);
					inventoryMap.put("customerId", orderInfo.getCustomer_id());
					map.put("quantity", inventoryDao.selectInventoryNumByOne(inventoryMap));
					map.put("available_to_reserved", inventoryDao.selectInventoryReserveNumByOne(inventoryMap));
					newGoodsList.add(map);
				}
				Map newMap = new HashMap();
				newMap.put("tracking_number", "");
				newMap.put("shipping_wms_weight", "0.0000");
				newMap.put("order_goods", newGoodsList);
				newOrderList.add(newMap);
				return newOrderList;
			}
			
			// 否则，开始将商品按包裹进行分类
			String old_tracking_number = "",tracking_number = null;
			BigDecimal old_shipping_wms_weight = null,shipping_wms_weight = null;
			int index = 1,sum = saleOrderGoodsList.size();
			for(Map map : saleOrderGoodsList){
				
				tracking_number = map.get("tracking_number").toString();
				shipping_wms_weight = new BigDecimal(map.get("shipping_wms_weight").toString());
				
				if(!WorkerUtil.isNullOrEmpty(map.get("order_goods_id"))){
					String goodsId = map.get("order_goods_id").toString();
					Map mapModify = (Map) orderGoodsMap.get(goodsId);
					mapModify.put("goods_number", 
							Integer.parseInt(mapModify.get("goods_number").toString())-
							Integer.parseInt(map.get("goods_number").toString()));
					orderGoodsMap.put(goodsId,mapModify);
				}else{
					// 碰到空面单号，则把所有未扫描的商品全部加进来，返回。
					Map newMap = new HashMap();
					if(!WorkerUtil.isNullOrEmpty(newGoodsList)){
						newMap.put("tracking_number", old_tracking_number);
						newMap.put("shipping_wms_weight", old_shipping_wms_weight.divide(new BigDecimal(1000),4));
						newMap.put("order_goods", newGoodsList);
						newOrderList.add(newMap);
						newGoodsList = new ArrayList<Map>();
						for (String key : orderGoodsMap.keySet()) {
							Map map2 = (Map) orderGoodsMap.get(key);
							if(Integer.parseInt(map2.get("goods_number").toString()) < 1){
								continue;
							}
							Integer warehouseId = Integer.valueOf(map2.get("warehouse_id").toString());
							Integer productId = Integer.valueOf(map2.get("product_id").toString());
							Map<String,Object> inventoryMap = new HashMap<String,Object>();
							inventoryMap.put("warehouseId", orderInfo.getPhysical_warehouse_id());
							inventoryMap.put("productId", productId);
							inventoryMap.put("customerId", orderInfo.getCustomer_id());
							map2.put("quantity", inventoryDao.selectInventoryNumByOne(inventoryMap));
							map2.put("available_to_reserved", inventoryDao.selectInventoryReserveNumByOne(inventoryMap));
							newGoodsList.add(map2);
						}
						newMap = new HashMap();
						newMap.put("tracking_number", tracking_number);
						newMap.put("shipping_wms_weight", shipping_wms_weight.divide(new BigDecimal(1000),4));
						newMap.put("order_goods", newGoodsList);
						newOrderList.add(newMap);
						return newOrderList;
					}else{
						continue;
					}
				}
				
				Integer warehouseId = Integer.valueOf(map.get("warehouse_id").toString());
				Integer productId = Integer.valueOf(map.get("product_id").toString());
				Map<String,Object> inventoryMap = new HashMap<String,Object>();
				inventoryMap.put("warehouseId", orderInfo.getPhysical_warehouse_id());
				inventoryMap.put("productId", productId);
				inventoryMap.put("customerId", orderInfo.getCustomer_id());
				map.put("quantity", inventoryDao.selectInventoryNumByOne(inventoryMap));
				map.put("available_to_reserved", inventoryDao.selectInventoryReserveNumByOne(inventoryMap));
				
				if("".equals(old_tracking_number)){
					newGoodsList.add(map);
				}else if(tracking_number.equals(old_tracking_number)){
					newGoodsList.add(map);
				}else{
					Map newMap = new HashMap();
					newMap.put("tracking_number", old_tracking_number);
					newMap.put("shipping_wms_weight", old_shipping_wms_weight.divide(new BigDecimal(1000),4));
					newMap.put("order_goods", newGoodsList);
					newOrderList.add(newMap);
					newGoodsList = new ArrayList<Map>();
					newGoodsList.add(map);
					
					if(index == sum){
						newMap = new HashMap();
						newMap.put("tracking_number", tracking_number);
						newMap.put("shipping_wms_weight", shipping_wms_weight.divide(new BigDecimal(1000),4));
						newMap.put("order_goods", newGoodsList);
						newOrderList.add(newMap);
					}
				}
				
				if(index == sum && ("".equals(old_tracking_number) || tracking_number.equals(old_tracking_number))){
					Map newMap = new HashMap();
					newMap.put("tracking_number", tracking_number);
					newMap.put("shipping_wms_weight", shipping_wms_weight.divide(new BigDecimal(1000),4));
					newMap.put("order_goods", newGoodsList);
					newOrderList.add(newMap);
				}
				
				old_tracking_number = tracking_number;
				old_shipping_wms_weight = shipping_wms_weight;
				index ++;
			}
			

			// 碰到空面单号，则把所有未扫描的商品全部加进来，返回。
			Map newMap = new HashMap();
			newGoodsList = new ArrayList<Map>();
			for (String key : orderGoodsMap.keySet()) {
				Map map2 = (Map) orderGoodsMap.get(key);
				if(Integer.parseInt(map2.get("goods_number").toString()) <= 0){
					continue;
				}
				Integer warehouseId = Integer.valueOf(map2.get("warehouse_id").toString());
				Integer productId = Integer.valueOf(map2.get("product_id").toString());
				Map<String,Object> inventoryMap = new HashMap<String,Object>();
				inventoryMap.put("warehouseId", orderInfo.getPhysical_warehouse_id());
				inventoryMap.put("productId", productId);
				inventoryMap.put("customerId", orderInfo.getCustomer_id());
				map2.put("quantity", inventoryDao.selectInventoryNumByOne(inventoryMap));
				map2.put("available_to_reserved", inventoryDao.selectInventoryReserveNumByOne(inventoryMap));
				newGoodsList.add(map2);
				tracking_number = "";
			}
			if(!WorkerUtil.isNullOrEmpty(newGoodsList)){
				newMap = new HashMap();
				if(tracking_number.length()>1){
					newMap.put("tracking_number", tracking_number);
				}else{
					newMap.put("tracking_number", "");
				}
				newMap.put("shipping_wms_weight", "0.0000");
				newMap.put("order_goods", newGoodsList);
				newOrderList.add(newMap);
			}
		
		}else{
			saleOrderGoodsList = orderInfoDao.selectSaleOrderGoodsById(searchMap);
			for(Map map : saleOrderGoodsList){
				Integer warehouseId = Integer.valueOf(map.get("warehouse_id").toString());
				Integer productId = Integer.valueOf(map.get("product_id").toString());
				Map<String,Object> inventoryMap = new HashMap<String,Object>();
				inventoryMap.put("warehouseId", orderInfo.getPhysical_warehouse_id());
				inventoryMap.put("productId", productId);
				inventoryMap.put("customerId", orderInfo.getCustomer_id());
				map.put("quantity", inventoryDao.selectInventoryNumByOne(inventoryMap));
				map.put("available_to_reserved", inventoryDao.selectInventoryReserveNumByOne(inventoryMap));
				newGoodsList.add(map);
			}
			Map newMap = new HashMap();
			newMap.put("tracking_number", "");
			newMap.put("shipping_wms_weight", "0.0000");
			newMap.put("order_goods", newGoodsList);
			newOrderList.add(newMap);
		}
		return newOrderList;
	}
	
	@Override
	public List<Map> getSaleOrderActionById(Map map) {
		// TODO Auto-generated method stub
		return orderInfoDao.selectSaleOrderActionById(map);
	}
	
	@Override
	public List<Map> getBatchPickOrderList(String batchPickSn2OrderId) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		String arr [] = batchPickSn2OrderId.split("_");
		String batchPickId = arr[0];
		try{
			String orderId = arr[1];
			map.put("orderId", orderId);
		}catch(Exception e){
			map.put("orderId", "");
		}
		
		if(!WorkerUtil.isNullOrEmpty(batchPickId)){
			if(batchPickId.contains("F")){
				batchPickId = batchPickId.replace("F", "");
				if(!"".equals(batchPickId) && !WorkerUtil.isNullOrEmpty(batchPickId) )
					map.put("batchPickSn", batchPickId);
			}else{
				map.put("batchPickId", batchPickId);
			}
		}
		
		List<Map> mapList = orderInfoDao.selectBatchPickOrderList(map);
		return mapList;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Map getPrintDispatchInfo(String [] batchPickIdArr) {
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		List<Map> orderInfoList = new ArrayList<Map>();
		String fileName = "";
		for(String batchPickId:batchPickIdArr){
			
			
			// 得到productId,goodsNumber,locationKwBarcode之间的映射关系goodsKwMap
			Map<String,Object> goodsKwMap = new HashMap<String,Object>();
			List<Map> pickOrderGoods = orderInfoDao.selectPickOrderGoodsList(Integer.parseInt(batchPickId));
			for(Map map : pickOrderGoods){
				String productIdTemp = map.get("product_id").toString();
				if(goodsKwMap.containsKey(productIdTemp)){
					goodsKwMap.put(productIdTemp, goodsKwMap.get(productIdTemp).toString()+","+map.get("goods_number")+"_"+map.get("location_kw_barcode"));
				}else{
					goodsKwMap.put(productIdTemp, map.get("goods_number")+"_"+map.get("location_kw_barcode"));
				}
			}

			// 遍历订单得到发货单数据
			int index = 1;
			List<Map> pickOrderList = this.getBatchPickOrderList(batchPickId);
			for(Map map2 : pickOrderList){
				String batch_pick_sn = map2.get("batch_pick_sn").toString();
				List<Map> resList = this.getDispatchBillInfo(Integer.valueOf(map2.get("order_id").toString()));
				
				for(Map map :resList){
					
					Map<String,Object> map_ = new HashMap<String,Object>();
					
					int kindNum = 0,allNum = 0;
					OrderInfo orderInfo = (OrderInfo) map.get("orderInfo");
					List<String> productIdList = new ArrayList<String>();
					List<Map> orderGoodsList = (List<Map>)map.get("orderGoodsList");
					List<Map> newOrderGoodsList = new ArrayList<Map>();
					for(Map goodsMap:orderGoodsList){
						String productId = goodsMap.get("product_id").toString();
						Integer goodsNumber = Integer.parseInt(goodsMap.get("goods_number").toString());
						allNum += Integer.parseInt(goodsMap.get("goods_number").toString());
						if(!productIdList.contains(productId)){
							kindNum++;
							productIdList.add(productId);
						}
						
						String qty2Kw = goodsKwMap.get(productId).toString();
						String [] qty2KwArr = qty2Kw.split(",");
						String msg = "";
						for(String tempArr : qty2KwArr){
							String temp[] = tempArr.split("_");
							if(goodsNumber <= Integer.parseInt(temp[0])){
								msg += temp[1];
								goodsMap.put("location_kw_barcode", msg);
								goodsKwMap.put(productId, qty2Kw.replaceFirst(tempArr, (Integer.parseInt(temp[0])-goodsNumber)+"_"+temp[1]));
								break;
							}else if(goodsNumber > Integer.parseInt(temp[0]) && Integer.parseInt(temp[0]) > 0){
								msg += temp[1]+",";
								goodsNumber = goodsNumber - Integer.parseInt(temp[0]);
								goodsKwMap.put(productId, qty2Kw.replaceFirst(tempArr, "0_"+temp[1]));
							}
						}
						newOrderGoodsList.add(goodsMap);
					}
					
					String shipping_name = map.get("shipping_name").toString();
					fileName = (String) map.get("file_name");
					map_.put("shop_logo_filename",(String) map.get("shop_logo_filename"));
					map_.put("page_line",map.containsKey("page_line")?map.get("page_line"):"6");
					map_.put("sequence_no",index);
					map_.put("order_num", pickOrderList.size());
					map_.put("kind_num", kindNum);
					map_.put("all_num",allNum);
					map_.put("shop_order_sn", orderInfo.getShop_order_sn());
					map_.put("order_id", orderInfo.getOrder_id());
					map_.put("customer_id", orderInfo.getCustomer_id());
					map_.put("shipping_name", shipping_name);
					map_.put("taobao_user_id", orderInfo.getNick_name());
					map_.put("goods_amount", orderInfo.getGoods_amount());
					map_.put("postal_code", orderInfo.getPostal_code());
					map_.put("province", orderInfo.getProvince_name());
					map_.put("city", orderInfo.getCity_name());
					map_.put("district", orderInfo.getDistrict_name());
					map_.put("address", orderInfo.getShipping_address());
					map_.put("batch_pick_sn", batch_pick_sn);
					map_.put("receive_name", orderInfo.getReceive_name());
					map_.put("phone_number", orderInfo.getPhone_number());
					map_.put("mobile_number", orderInfo.getMobile_number());
					map_.put("BB_sale", orderInfo.getSeller_note().contains("刻字")?"Y":"N");
					map_.put("LM_sale", orderInfo.getSeller_note().contains("暗号") || orderInfo.getSeller_note().trim().toUpperCase().contains("OFFERCODE") ?"Y":"N");
					map_.put("orderGoodsList", orderGoodsList);
					orderInfoList.add(map_);
				}
				
				index++;
			}
		}
		resMap.put("orderInfoList", orderInfoList);
		resMap.put("fileName", fileName);
		return resMap;
	}
	
	public List<Map> getDispatchBillInfo(Integer orderId){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		List<Map> newOrderList = new ArrayList<Map>();
		
		try{
			OrderInfo orderInfo = getOrderInfoByOrderId(orderId);
			List<Map> orderGoodsList = orderInfoDao.selectBatchPickOrderGoodsList(orderId);
			resMap.put("orderInfo", orderInfo);
			resMap.put("orderGoodsList", orderGoodsList);
			
			Shipping shipping = shippingDao.selectByPrimaryKey(orderInfo.getShipping_id());
			resMap.put("shipping_name", shipping.getShipping_name());
			
			String shopName = orderInfo.getShop_name();
			Integer customerId = orderInfo.getCustomer_id();
			
			// 1.去店铺查找
			Map<String,Object> searchMap = new HashMap<String,Object>();
			searchMap.put("shopId", orderInfo.getOms_shop_id());
			searchMap.put("shopName", shopName);
			searchMap.put("customerId", customerId);
			Map returnMap = orderInfoDao.selectDispatchBillByShop(searchMap);
			String file_name = "";
			if(!WorkerUtil.isNullOrEmpty(returnMap)){
				resMap.put("result", "success");
				resMap.put("note", "success");
				file_name = returnMap.get("template_file_name").toString();
				resMap.put("page_line", returnMap.get("page_line"));
				resMap.put("file_name", returnMap.get("template_file_name"));
				resMap.put("shop_logo_filename", returnMap.get("shop_logo_filename"));
			}
			
			// 2.去业务组查找
			if(WorkerUtil.isNullOrEmpty(file_name)){
				Map returnMap_= orderInfoDao.selectDispatchBillByCustomer(customerId);
				resMap.put("result", "success");
				resMap.put("note", "success");
				file_name = !WorkerUtil.isNullOrEmpty(returnMap_) && returnMap_.containsKey("dispatch_bill_file_name")?returnMap_.get("dispatch_bill_file_name").toString():"";
				resMap.put("file_name", file_name);
			}
			
			// 3.如果均未找到，则按默认页面打印
			if(WorkerUtil.isNullOrEmpty(file_name)){
				resMap.put("result", "success");
				resMap.put("note", "success");
				resMap.put("file_name", "dispatch_common");
			}
		}catch(Exception e){
			logger.error("GetDispatchBillInfo error:",e);
			resMap.put("result", "failure");
			resMap.put("note", e.getMessage());
			resMap.put("file_name", "");
		}
		
		newOrderList.add(resMap);
		return newOrderList;
	}
	
	
	/**
	 * 打印波次单逻辑
	 * @author hzhang1
	 */
	public List<Map> getPickOrderList(Map searchMap){
		
		List<Map> newPickOrderList = new ArrayList<Map>();
		List<Map> pickOrderList = orderInfoDao.selectPickOrderListByPage(searchMap);
		for(Map map :pickOrderList){
			
			if(Integer.valueOf(map.get("order_num").toString()) == 0)
				continue;
			
			Integer omsShopId = Integer.parseInt(map.get("oms_shop_id").toString());
			Integer customerId = (Integer) map.get("customer_id");
			
			// 1.去店铺查找
			Map<String,Object> searchMap_ = new HashMap<String,Object>();
			searchMap.put("shopId", omsShopId);
			searchMap.put("customerId", customerId);
			Map returnMap = orderInfoDao.selectDispatchBillByShop(searchMap);
			String file_name = "";
			if(!WorkerUtil.isNullOrEmpty(returnMap)){
				file_name = returnMap.get("template_file_name").toString();
				if("dispatch_common".equals(file_name)){
					map.put("file_name", "通用发货单");
				}else{
					map.put("file_name", returnMap.get("shop_name").toString());
				}
				
			}
			
			// 2.去业务组查找
			if(WorkerUtil.isNullOrEmpty(file_name)){
				Map returnMap_ = orderInfoDao.selectDispatchBillByCustomer(customerId);
				if(!WorkerUtil.isNullOrEmpty(returnMap_) && !"dispatch_common".equals(returnMap_.get("dispatch_bill_file_name"))){
					file_name = (String) returnMap_.get("dispatch_bill_file_name");
					map.put("file_name", returnMap_.get("name").toString());
				}else{
					map.put("file_name", "通用发货单");
				}
			}
			
			// 3.如果均未找到，则按默认页面打印
			if(WorkerUtil.isNullOrEmpty(file_name)){
				map.put("file_name", "通用发货单");
			}
			
			newPickOrderList.add(map);
		}
		return newPickOrderList;
	}
	
	public BatchPick getBatchPickBySn(String batchPickSn){
		return orderInfoDao.selectBatchPickBySn(batchPickSn);
	}
	
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public Map<String,Object> dealPickOrder(String batchPickIdList[],String createdUser){
		
		List<Integer> batchPickList = new ArrayList<Integer>();
 		for(String str : batchPickIdList){
			String sn2Id[] = str.split("_");
			batchPickList.add(Integer.parseInt(sn2Id[1]));
		}
 		List<BatchPick> getBatchPickList = batchPickDao.selectBatchPickListForUpdate(batchPickList);
		List<Map> mapList_ = new ArrayList<Map>();
		Map<String,Object> resMap_ = new HashMap<String,Object>();
		try {
			for(String str : batchPickIdList){
				String sn2Id[] = str.split("_");
				String batchPickId = sn2Id[1];
				String flag = sn2Id[0];
				Map<String,Object> resMap = new HashMap<String,Object>();
				List<Map> newPickOrderList = new ArrayList<Map>();
				List<Map<String, Object>> newPickOrderGoods = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> newPickOrderGoods2 = new ArrayList<Map<String, Object>>();
				List<Map> pickOrderList = new ArrayList<Map>();
				List<Map> pickOrderGoods = new ArrayList<Map>();
				
				// 1.搜索该批次的订单
				pickOrderList = orderInfoDao.selectBatchPickOrderListV2(Integer.parseInt(batchPickId));
				
				// 2.统计商品总数
				pickOrderGoods = orderInfoDao.selectPickOrderGoodsList(Integer.parseInt(batchPickId));
				
				int allNum = 0,index = 1,order_id=0,size = pickOrderList.size(),kindNum = 0;
				// 3.库位
				order_id = Integer.parseInt(pickOrderList.get(0).get("order_id").toString());
				List<String> productList = new ArrayList<String>();
				String warehouseName = pickOrderList.get(0).get("warehouse_name").toString();
				for(Map goodsMap:pickOrderGoods){
					//goodsMap.put("location_kw_barcode", inventoryDao.selectKwBarcodeInPick(goodsMap));
					Integer goods_number = Integer.parseInt(goodsMap.get("goods_number").toString());
					allNum += goods_number;
					if(productList.contains(goodsMap.get("product_id").toString())){
						;
					}else{
						productList.add(goodsMap.get("product_id").toString());
						kindNum++;
					}
					newPickOrderGoods.add(goodsMap);
				}
				resMap.put("kindNum",kindNum);
				resMap.put("warehouseName",warehouseName);
				
				// 4.统计每个订单商品数
				//String last_product_id = "",note = "",contain_way = "",old_order_batch_sequence_number = "";
				List<Map> orderGoodsMapList = new ArrayList<Map>();
				List<Map> orderGoodsReserveMapList = new ArrayList<Map>();
				Map<String,Object> orderSeqMap = new HashMap<String,Object>();
				Map<String,Object> tempMap = new HashMap<String,Object>();
				int tempInt = 1;
				for(Map map:pickOrderList){
					if(!orderSeqMap.containsKey(map.get("order_id").toString())){
						orderSeqMap.put(map.get("order_id").toString(), tempInt);
						tempInt++;
					}
				}
				for(Map map:pickOrderList){
					
					String product_id = map.get("product_id").toString();
					String order_batch_sequence_number = orderSeqMap.get(map.get("order_id").toString()).toString();
					//String order_batch_sequence_number = map.get("order_batch_sequence_number").toString();
					Integer goods_number = Integer.parseInt(map.get("goods_number").toString());
					if(tempMap.containsKey(product_id)){
						tempMap.put(product_id, tempMap.get(product_id)+","+order_batch_sequence_number+"-"+goods_number);
					}else{
						tempMap.put(product_id, order_batch_sequence_number+"-"+goods_number);
					}
					
				}
				for(Map goodsMap:newPickOrderGoods){
					
					String product_id = goodsMap.get("product_id").toString();
					Integer goods_number = Integer.parseInt(goodsMap.get("goods_number").toString());
					String msg = tempMap.get(product_id).toString();
					
					String note = "";
					String arr[] = msg.split(",");
					for(String temp:arr){
						msg = tempMap.get(product_id).toString();
						String orderArr[] = temp.split("-");
						Integer numTemp = Integer.parseInt(orderArr[1]);
						if(numTemp == 0){
							continue;
						}
						if(goods_number > numTemp){
							goods_number = goods_number - numTemp;
							note += orderArr[0] +"-"+numTemp+",";
							tempMap.put(product_id, msg.replaceFirst(temp, orderArr[0]+"-0"));
						}else if(goods_number < numTemp){
							note += orderArr[0] +"-"+goods_number;
							tempMap.put(product_id, msg.replaceFirst(temp, orderArr[0]+"-"+(numTemp-goods_number)));
							break;
						}else{
							note += temp;
							tempMap.put(product_id, msg.replaceFirst(temp, orderArr[0]+"-0"));
							break;
						}
					}
					goodsMap.put("note", note);
					newPickOrderGoods2.add(goodsMap);
				}
				//System.setProperty("java.util.Arrays.useLegacyMergeSort", "true"); 
				Collections.sort(newPickOrderGoods2, new Comparator<Map<String, Object>>() {
		            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
		            	 Integer s1 = Integer.parseInt(o1.get("pick_seq").toString());  
		                 Integer s2 = Integer.parseInt(o2.get("pick_seq").toString());  
		    
			             if(s1.compareTo(s2) > 0) {  
			               return 1;  
			             } else if (s1.compareTo(s2) < 0){  
			               return -1;  
			             } else {
			            	  return 0;
			             }
		            }
		        });
				resMap.put("pickOrderGoods",newPickOrderGoods2);
				
				// 插入action
				BatchPick batchPick = batchPickDao.selectbatchPickById(Integer.parseInt(batchPickId));
				if(!"Y".equals(batchPick.getReserve_status())){
					resMap.put("order_num", 0);
					resMap.put("allNum", allNum);
					resMap.put("batch_process_type", batchPick.getBatch_process_type());
					resMap.put("contain_way", "");
					return resMap;
				}
			
				if(batchPick.getPrint_count() == 0){
					Map<String, Object> paramsForOrderInfoUpdate = new HashMap<String, Object>();
					paramsForOrderInfoUpdate.put("orderStatus",OrderInfo.ORDER_STATUS_PICKING);
					paramsForOrderInfoUpdate.put("lastUpdatedUser", createdUser);
					paramsForOrderInfoUpdate.put("batchPickSn", batchPick.getBatch_pick_sn());
					shipmentDao.updateBatchOrderStatusWhenPicking(paramsForOrderInfoUpdate);
					paramsForOrderInfoUpdate.put("actionType", "订单已打印波次");
					shipmentDao.updateBatchOrderAction(paramsForOrderInfoUpdate);
					
					Map<String, Object> paramsForPLUpdate = new HashMap<String, Object>();
					paramsForPLUpdate.put("batchPickId", batchPick.getBatch_pick_id());
					paramsForPLUpdate.put("physicalWarehouseId", batchPick.getPhysical_warehouse_id());
					paramsForPLUpdate.put("actionUser", createdUser);
					
					
					for(Map map:newPickOrderGoods2){
						
						Location location = new Location();
						location.setPhysical_warehouse_id(batchPick.getPhysical_warehouse_id());
						location.setLocation_barcode(WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_TAGCODE,"",true));
						location.setLocation_type(Location.LOCATION_TYPE_TRANSIT);
						location.setIs_delete("N");
						location.setCreated_user(createdUser);
						int locationId = inventoryDao.insertLocation(location);
						paramsForPLUpdate.put("locationId", location.getLocation_id());
						
						Integer goodsNumber = Integer.parseInt(map.get("goods_number").toString());
						int col = inventoryDao.updateProductLocation(Integer.parseInt(map.get("pl_id").toString()), goodsNumber);
						if(col < 1){
							throw new RuntimeException("打印波次单更新订单失败,失败原因："+map.get("pl_id").toString()+"库存不够扣减"+goodsNumber);
						}
						//int col = inventoryDao.updateProductLocationNumWhenPick(paramsForPLUpdate);
						ProductLocationDetail productLocationDetail = new ProductLocationDetail();
						productLocationDetail.setPl_id(Integer.parseInt(map.get("pl_id").toString()));
						productLocationDetail.setChange_quantity(-goodsNumber);
						productLocationDetail.setTask_id(Integer.parseInt(map.get("task_id").toString()));
						productLocationDetail.setDescription("打印波次单拣货下架");
						productLocationDetail.setCreated_user("system");
						productLocationDetail.setLast_updated_user("system");
						productLocationDetailDao.insert(productLocationDetail);
						
						
						ProductLocation productLocation = new ProductLocation();
						productLocation.setLocation_id(location.getLocation_id());
						productLocation.setProduct_id(Integer.parseInt(map.get("product_id").toString()));
						productLocation.setQty_total(goodsNumber);
						productLocation.setQty_reserved(0);
						productLocation.setQty_available(0);
						productLocation.setProduct_location_status("NORMAL");
						productLocation.setQty_freeze(0);
						productLocation.setStatus("NORMAL");
						productLocation.setWarehouse_id(Integer.parseInt(map.get("warehouse_id").toString()));
						productLocation.setBatch_sn(WorkerUtil.isNullOrEmpty(map.get("batch_sn"))?"":map.get("batch_sn").toString());
						productLocation.setValidity(WorkerUtil.isNullOrEmpty(map.get("validity"))?"":map.get("validity").toString());
						productLocation.setSerial_number(WorkerUtil.isNullOrEmpty(map.get("serial_number"))?"":map.get("serial_number").toString());
						productLocation.setCreated_user("system");
						productLocation.setLast_updated_user("system");
						productLocationDao.insert(productLocation);
						
						productLocationDetail = new ProductLocationDetail();
						productLocationDetail.setPl_id(productLocation.getPl_id());
						productLocationDetail.setChange_quantity(goodsNumber);
						productLocationDetail.setTask_id(Integer.parseInt(map.get("task_id").toString()));
						productLocationDetail.setDescription("打印波次单拣货虚拟上架至中转区");
						productLocationDetail.setCreated_user("system");
						productLocationDetail.setLast_updated_user("system");
						productLocationDetailDao.insert(productLocationDetail);
	//					col = inventoryDao.insertProductLocationWhenPick(paramsForPLUpdate);
	//					col = inventoryDao.insertProductLocationDetailWhenPick(paramsForPLUpdate);
						taskDao.updateTaskToPlId(Integer.parseInt(map.get("task_id").toString()), productLocation.getPl_id());
					}
					
					List<Integer> locationIdList = inventoryDao.selectProductLocationNumWhenPick(paramsForPLUpdate);
					for(Integer tempLocationId : locationIdList){
						Integer total = productLocationDao.selectProductLocationQtyByLocationId(tempLocationId);
						if(total.equals(0)){
							locationDao.updateLocationIsEmpty(tempLocationId);
						}
					}
					// 更新打印次数
					int col = orderInfoDao.updateBatchPickPrintCount(Integer.parseInt(batchPickId));
					if(col < 1){
						throw new RuntimeException("打印波次单更新订单失败,失败原因：该波次单已打印过");
					}
					
				}else{
					orderInfoDao.updateBatchPickRePrintCount(Integer.parseInt(batchPickId));
					if(!flag.equals("F"))
						throw new RuntimeException("打印波次单更新订单失败,失败原因：该波次单已打印过");
				}
				
				
			
				String contain_way = "";
				if(batchPick.getBatch_process_type().equals("BATCH")){
					Map<String, Object> paramsForOrderGoodsSelect = new HashMap<String, Object>();
					paramsForOrderGoodsSelect.put("orderId", order_id);
					List<Map> mapList = orderInfoDao.selectSaleOrderGoodsGroupByProductId(paramsForOrderGoodsSelect);
					for(Map map:mapList){
						contain_way += map.get("goods_barcode").toString()+"-"+map.get("goods_number").toString()+",";
					}
					contain_way = contain_way.substring(0, contain_way.length()-1);
				}
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("batchPickSn", batchPick.getBatch_pick_sn());
				map.put("physical_warehouse_id", batchPick.getPhysical_warehouse_id());
				resMap.put("order_num", orderInfoDao.selectPickOrderListByPage(map).get(0).get("order_num"));
				resMap.put("allNum", allNum);
				resMap.put("batch_process_type", batchPick.getBatch_process_type());
				resMap.put("contain_way", contain_way);
				resMap.put("sn", batchPick.getBatch_pick_sn());
				
				mapList_.add(resMap);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.error("打印批件单更新订单失败", e);
			throw new RuntimeException("打印波次单更新订单失败,失败原因："+e.getMessage());
		}
		resMap_.put("mapList", mapList_);
		return resMap_;
	}

	
	public Map<String,Object> printBatchPickTag(String batchPickId){
	
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		// 2.统计商品总数
		List<Map> pickOrderGoods = orderInfoDao.selectPickOrderGoodsList(Integer.parseInt(batchPickId));
		List<Map> newPickOrderGoods = new ArrayList<Map>();
		for(Map map : pickOrderGoods){
			map.put("batchCode", WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_TAGCODE,"B",true));
			newPickOrderGoods.add(map);
		}
		resMap.put("pickOrderGoods",newPickOrderGoods);
		
		return resMap;
	}
	
	
	@Override
	public OrderInfo selectOrderBatchSnBarCode(String batch_sn, String barcode) {
		
		HashMap<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("batch_sn", batch_sn);
		paramMap.put("barcode", barcode);
		
		return orderInfoDao.selectOrderBatchSnBarCode(paramMap);
	}

	@Override
	public OrderGoods selectOrderGoodsBatchSnBarCode(String batch_sn,
			String barcode) {
		HashMap<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("batch_sn", batch_sn);
		paramMap.put("barcode", barcode);
		
		return orderInfoDao.selectOrderGoodsBatchSnBarCode(paramMap);
	}

	@Override
	public Integer selectProductIdBatchSnBarCode(String batch_sn, String barcode) {
		HashMap<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("batch_sn", batch_sn);
		paramMap.put("barcode", barcode);
		return orderInfoDao.selectProductIdBatchSnBarCode(paramMap);
	}

	@Override
	public Map selectSendInfoByOrderId(Integer orderId) {
		Map sendInfo = orderDao.selectSendInfoByOrderId(orderId);
		String shipment_category = "";
		String aftersale_phone = "";
		if(WorkerUtil.isNullOrEmpty(sendInfo.get("oi_shipment_category"))){
			shipment_category = sendInfo.get("w_shipment_category").toString();
		}else{
			shipment_category = sendInfo.get("oi_shipment_category").toString();
		}
		sendInfo.put("shipment_category", shipment_category);
		if(WorkerUtil.isNullOrEmpty(sendInfo.get("oi_aftersale_phone"))){
			aftersale_phone = sendInfo.get("w_aftersale_phone").toString();
		}else{
			aftersale_phone = sendInfo.get("oi_aftersale_phone").toString();
		}
		sendInfo.put("aftersale_phone", aftersale_phone);
		return sendInfo;
	}

	/* to do sf 保价
	@Override
	public BigDecimal selectSFInsurance(Integer orderId,Integer customerId) {
		BigDecimal insurance = new BigDecimal(0);
		if("65628".equalsIgnoreCase(customerId.toString())){//LA MER海蓝之谜 65628  
			insurance = sfBiz.getShipmentOrderAmount(orderId);
		}else if("65653".equalsIgnoreCase(customerId.toString())){// 资生堂/SHISEIDO 65653 
			insurance = sfBiz.getShipmentOrderAmount(orderId);
		}else if("65670".equalsIgnoreCase(customerId.toString())){// 路易十三 65670
			insurance = sfBiz.getShipmentOrderAmount(orderId);
		}else if("16".equalsIgnoreCase(customerId.toString())){//乐其电教16
			insurance = sfBiz.getLeqeeElecEduInsurance(orderId);
		}
		return insurance;
	}
	*/

	@Override
	public int getSumNumsHasInwareHouse(String oms_order_sn) {
		int count=orderInfoDao.selectCountNumsHasInwareHouse(oms_order_sn);
		if(count>0)
		{
			return orderInfoDao.selectSumNumsHasInwareHouse(oms_order_sn);
		}
		return 0;
	}

	@Override
	public Map<String, Object> queryBatchPickByOrder_id(String no) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap=orderInfoDao.queryBatchPickByOrder_id(Integer.parseInt(no));
		if(com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(resultMap)){
			resultMap = new HashMap<String, Object>();
			resultMap.put("result", "error");
			resultMap.put("message", "未查到对应的波次单");
			return resultMap;
		}
		
		if("BINDED".equalsIgnoreCase(resultMap.get("status").toString())){
			String username=orderInfoDao.getUserNameByBP(Integer.parseInt(resultMap.get("batch_pick_id").toString()));
			resultMap.put("username", username);
		}else{
			resultMap.put("username", "");
		}
		resultMap.put("created_time", resultMap.get("created_time").toString());
		int batch_pick_id=Integer.parseInt(resultMap.get("batch_pick_id").toString());
		int count= orderInfoDao.getCountOrdersByBatchPickId(batch_pick_id);
		resultMap.put("count", count);
		resultMap.put("result", "success");
		return resultMap;
	}

	@Override
	public List<Integer> getToDeliverInvSaleOrderIdList(Integer customerId,
			Date startDateTime, Date endDateTime) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerId", customerId);
		paramsMap.put("startDateTime", startDateTime);
		paramsMap.put("endDateTime", endDateTime);
		
		return orderDao.selectToDeliverInvSaleOrderIdList(paramsMap);
	}
	
	@Override
	public List<Integer> getToDeliverInvSaleOrderIdListFor1111(Integer customerId,
			Date startDateTime, Date endDateTime) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerId", customerId);
		paramsMap.put("startDateTime", startDateTime);
		paramsMap.put("endDateTime", endDateTime);
		
		return orderDao.selectToDeliverInvSaleOrderIdListFor1111(paramsMap);
	}
	
	@Override
	public List<Integer> getToAutoPurchaseAccept(Integer customerId , Integer physicalWarehouseId,Date startDateTime, Date endDateTime) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerId", customerId);
		paramsMap.put("physicalWarehouseId", physicalWarehouseId);
		paramsMap.put("startDateTime", startDateTime);
		paramsMap.put("endDateTime", endDateTime);
		
		return orderDao.selectToAutoPurchaseAcceptOrderIdList(paramsMap);
	}

	@Override
	public List<Integer> getTestToAutoPurchaseAccept(Integer customerId , Integer physicalWarehouseId,Date startDateTime, Date endDateTime) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerId", customerId);
		paramsMap.put("physicalWarehouseId", physicalWarehouseId);
		paramsMap.put("startDateTime", startDateTime);
		paramsMap.put("endDateTime", endDateTime);
		
		return orderDao.selectTestToAutoPurchaseAcceptOrderIdList(paramsMap);
	}
	
	@Override
	public List<Integer> getToDeliverInvOrderIdList(Integer customerId,
			Date startDateTime, Date endDateTime){
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerId", customerId);
		paramsMap.put("startDateTime", startDateTime);
		paramsMap.put("endDateTime", endDateTime);
		
		return orderDao.selectToDeliverInvOrderIdList(paramsMap);
	}
	
	
	@Override
	public List<Integer> getToDeliverInvPackBoxShipmentIdList(Integer customerId,
			Date startDateTime, Date endDateTime){

		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerId", customerId);
		paramsMap.put("startDateTime", startDateTime);
		paramsMap.put("endDateTime", endDateTime);
		return orderDao.selectToDeliverInvPackBoxShipmentIdList(paramsMap);
	}
	
	@Override
	public List<Integer> getToDeliverInvPackBoxShipmentIdListV2(Integer customerId,
			Date startDateTime, Date endDateTime){

		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerId", customerId);
		paramsMap.put("startDateTime", startDateTime);
		paramsMap.put("endDateTime", endDateTime);
		return orderDao.selectToDeliverInvPackBoxShipmentIdListV2(paramsMap);
	}
	
	/**
	 * 销售订单复核扫描商品
	 * 
	 * @param shipmentId an Integer: 
	 * @param orderGoodsId an Integer: 
	 * @param serialNumber a String: 串号
	 * @return resultMap a Map<String, Object>: 扫描结果
	 * */
	@Override
	public Map<String, Object> getBatchSaleOrderGoodsScanResult(Integer batchPickId,
			Integer orderGoodsId, Integer maxShipmentCount) {
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String result = Response.SUCCESS;
		String note = "扫描商品成功";
		int order_goods_id = 0;
		
		logger.info("（批量复核）扫描商品，batchPickId: " + batchPickId + ", orderGoodsId: "
				+ orderGoodsId + ", maxShipmentCount: " + maxShipmentCount);

		// Assert判断数据异常，直接抛出异常结束程序执行
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(batchPickId), "数据异常,batchPickId为空!");
		Assert.isTrue(!(WorkerUtil.isNullOrEmpty(orderGoodsId) && WorkerUtil.isNullOrEmpty(maxShipmentCount)),
				"数据异常,orderGoodsId与maxShipmentCount均为空!");

		if(!WorkerUtil.isNullOrEmpty(orderGoodsId)){  // 条码控制商品
			order_goods_id = orderGoodsId.intValue();
			// 查找订单商品记录
			OrderGoods orderGoods = orderGoodsDao.selectByPrimaryKey(orderGoodsId);
			Assert.isTrue(!WorkerUtil.isNullOrEmpty(orderGoods), "数据异常:订单中找不到该商品!orderGoodsId: " + orderGoodsId);
//			List<Shipment> shipments = shipmentBiz.findBatchShipmentIdByBatchOrderId(batchPickId,maxShipmentCount); 
			List<Map<String,Object>> shipmentOrderGoodsInfoList = shipmentBiz.findShipmentOrderGoodsInfoList(batchPickId,maxShipmentCount,orderGoods.getProduct_id());
			
			String addOrUpdate = "ADD";
			Integer shipmentId = Integer.parseInt(String.valueOf(shipmentOrderGoodsInfoList.get(0).get("shipment_id")));
			String orderGoodsIdx = String.valueOf(shipmentOrderGoodsInfoList.get(0).get("order_goods_id_x"));
			String[] orderGoodsIdxArr = orderGoodsIdx.split(",");
			Integer orderGoodsId0 = Integer.parseInt(orderGoodsIdxArr[0]);
			orderGoods = orderGoodsDao.selectByPrimaryKey(orderGoodsId0);
			order_goods_id= orderGoodsId0;
			List<ShipmentDetail> shipmentDetails = shipmentDetailBiz.findByShipmentIdAndOrderGoodsId(shipmentId, orderGoodsId0);
			if(!WorkerUtil.isNullOrEmpty(shipmentDetails)){  // 条码商品且已经有过此面单的绑定记录,更新记录
				ShipmentDetail shipmentDetail = shipmentDetails.get(0);
				Assert.isTrue(shipmentDetail.getGoods_number() < orderGoods.getGoods_number(),
						"数据异常:该商品已经全部绑定到指定的面单!");
				addOrUpdate="UPDATE";
			}
			
			if(addOrUpdate=="ADD"){
				List<ShipmentDetail> shipmentDetailList = new ArrayList<ShipmentDetail>();  
				for (Map<String,Object> shipmentOrderGoodsInfo : shipmentOrderGoodsInfoList) {
					String orderGoodsIdStr = String.valueOf(shipmentOrderGoodsInfo.get("order_goods_id_x"));
					String[] orderGoodsIdxStrArr = orderGoodsIdStr.split(",");
					Integer orderGoodsIdi = Integer.parseInt(orderGoodsIdxStrArr[0]);
					Integer shipmentIdi = Integer.parseInt(String.valueOf(shipmentOrderGoodsInfo.get("shipment_id")));
					ShipmentDetail shipmentDetail = new ShipmentDetail();
					shipmentDetail.setGoods_name(orderGoods.getGoods_name());
					shipmentDetail.setGoods_number(Integer.valueOf(1));
					shipmentDetail.setOrder_goods_id(orderGoodsIdi);
					shipmentDetail.setProduct_id(orderGoods.getProduct_id());
					shipmentDetail.setShipment_id(shipmentIdi);
					shipmentDetailList.add(shipmentDetail);
				}
				if(!WorkerUtil.isNullOrEmpty(shipmentDetailList)){
					try{
						shipmentDetailDao.batchInsert(shipmentDetailList);
					}catch(RuntimeException e){
						logger.error("批量商品绑定面单失败!orderGoodsId: " + orderGoodsId + ", batchPickId: " + batchPickId+" -- e.msg:"+e.getMessage());
						throw new RuntimeException("商品绑定面单失败，请重试!");
					}
				}
			}else{
				List<Integer> shipmentList = new ArrayList<Integer>();
				List<Integer> orderGoodsIdList = new ArrayList<Integer>();
				for (Map<String,Object> shipmentOrderGoodsInfo : shipmentOrderGoodsInfoList) {
					shipmentList.add(Integer.parseInt(String.valueOf(shipmentOrderGoodsInfo.get("shipment_id"))));
					String orderGoodsIdStr = String.valueOf(shipmentOrderGoodsInfo.get("order_goods_id_x"));
					String[] orderGoodsIdxStrArr = orderGoodsIdStr.split(",");
					Integer orderGoodsIdi = Integer.parseInt(orderGoodsIdxStrArr[0]);
					orderGoodsIdList.add(orderGoodsIdi);
				}
				if(!WorkerUtil.isNullOrEmpty(orderGoodsIdList) && !WorkerUtil.isNullOrEmpty(shipmentList) && orderGoodsIdList.size()==shipmentList.size()){
					try{
						shipmentDetailDao.batchUpdateGoodsNumber(shipmentList,orderGoodsIdList);
					}catch(RuntimeException e){
						logger.error("批量商品绑定面单失败!orderGoodsId: " + orderGoodsId + ", batchPickId: " + batchPickId+" -- e.msg:"+e.getMessage());
						throw new RuntimeException("商品绑定面单失败，请重试!");
					}
				}else{
					logger.error("批量商品绑定面单失败!orderGoodsId: " + orderGoodsId + ", batchPickId: " + batchPickId+
							" -- e.msg:没有搜索到合适的单号与商品组合。orderGoodsSize:"+orderGoodsIdList.size()+" ; shipmentIdSize:"+shipmentList.size());
					throw new RuntimeException("商品绑定面单失败，请重试!");
				}
			}
		}
		
		// 封装返回结果
		resultMap.put("result", result);
		resultMap.put("note", note);
		resultMap.put("order_goods_id", order_goods_id);
		
		return resultMap;
	}

	
	@Override
	public List<Integer> getSingleOrderIdByOrderIds(List<Integer> orderIdList) {
		List<Integer> orderIds = orderInfoDao.getSingleOrderIdByOrderIds(orderIdList);
		return orderIds;
	}

	@Override
	public List<Map<String, Object>> getShippingAppOrders(List<Integer> orderIdList,String shippingCode) {
		List<Map<String,Object>> shippingAppIdList = orderInfoDao.getShippingAppIdList(orderIdList,shippingCode);
		
		if(!WorkerUtil.isNullOrEmpty(shippingAppIdList)){
			List<Map<String, Object>> orderMarkInfo = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> map1 : shippingAppIdList) {
				Integer warehouseId = Integer.parseInt(String.valueOf(map1.get("warehouse_id")));
				Integer shippingId = Integer.parseInt(String.valueOf(map1.get("shipping_id")));
				orderMarkInfo = orderInfoDao.getShippingAppOrders(orderIdList,warehouseId,shippingId);
				for (Map<String, Object> map : orderMarkInfo) {
					String mark = "";
					if(shippingCode.equals("ZTO") || shippingCode.equals("HT")){
						if(WorkerUtil.isNullOrEmpty(map.get("mark")) || map.get("mark").equals("")){
							Integer orderId = Integer.parseInt(String.valueOf(map.get("order_id")));
							String shippingMark = orderProcessDao.getShippingMark(orderId);
							Map<String,Object> singleOrder = orderProcessDao.getSingleOrder(orderId);
							if((WorkerUtil.isNullOrEmpty(shippingMark)  || shippingMark=="") && shippingCode.equals("ZTO")){
								mark = ztoBiz.getZTOMarkForSingleOrder(singleOrder);
							}else if((WorkerUtil.isNullOrEmpty(shippingMark) || shippingMark=="") && shippingCode.equals("HT")){
								mark = htBiz.getHTMarkForSingleOrder(singleOrder);
							}else{
								mark = shippingMark;
							}
						}else{
							mark = String.valueOf(map.get("mark"));
						}
					}
					if(mark.equalsIgnoreCase("")){
						List<Integer> municipalities = new ArrayList<Integer>();
						municipalities.add(2);
						municipalities.add(3);
						municipalities.add(10);
						municipalities.add(23);
						String province_name = String.valueOf(map.get("province_name"));
						Integer province_id = Integer.parseInt(String.valueOf(map.get("province_id")));
						String city_name = String.valueOf(map.get("city_name"));
						String district_name = String.valueOf(map.get("district_name"));
						if (!WorkerUtil.isNullOrEmpty(map.get("city_name"))) {
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
					map.put("mark", mark);
				}
				map1.put("orderMarkInfo", orderMarkInfo);
			}
		}
		return shippingAppIdList;
	}

	@Override
	public Map<String, Object> getIsSerialAndWarranty(String batch_sn,
			String barcode) {
		Map<String,Object> map =new HashMap<String,Object>();
		map=orderInfoDao.getIsSerialAndWarranty(batch_sn,barcode);
		return map;
	}

	@Override
	public int getCountOrdersByBatchSnBarCode(String batch_sn, String barcode) {
		return orderInfoDao.getCountOrdersByBatchSnBarCode(batch_sn,barcode);		
	}

	@Override
	public List<Map> doQueryGoods(String batch_pick_sn) {
		return orderInfoDao.selectPickOrderGoodsListByBatchPickSn(batch_pick_sn);
	}
	
	@Override
	public Map<String, Object> getCheckOrderGoodsScanResult(Integer check_order_id, String key){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		return resultMap;
	}

	@Override
	public Map<String, Object> saveTransferCodeForOrder(Integer orderGoodsId,
			String transferCode) {
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("（复核）扫描商品物流码  orderGoodsId: " + orderGoodsId + ", transferCode: " + transferCode);

		// Assert判断数据异常，直接抛出异常结束程序执行
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(orderGoodsId), "数据异常,商品识别码orderGoodsId为空!");
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(transferCode),"数据异常,物流码为空，请重新扫描!");
		String localUser = (String) SecurityUtils.getSubject().getPrincipal();
		Map<String,Object> map = orderGoodsDao.selectProductSkuCode(orderGoodsId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(map), "数据异常:根据识别码 " + orderGoodsId+"没有识别到具体商品！");
		OmsOrderTransferCode omsOrderTransferCode = new OmsOrderTransferCode();
		omsOrderTransferCode.setTransfer_code(transferCode);
		omsOrderTransferCode.setOrder_id(Integer.parseInt(String.valueOf(map.get("order_id"))));
		omsOrderTransferCode.setProduct_id(Integer.parseInt(String.valueOf(map.get("product_id"))));
		omsOrderTransferCode.setSku_code(String.valueOf(map.get("sku_code")));
		omsOrderTransferCode.setCreated_user(localUser);
		orderInfoDao.saveTransferCodeForOrder(omsOrderTransferCode);
		// 封装返回结果
		resultMap.put("result", "SUCCESS");
		resultMap.put("note", "物流码记录成功");
		
		return resultMap;
	}

	@Override
	public Map<String, Object> doKickOut(HttpServletRequest req) {
		// 初始化
			Map<String, Object> resultMap = new HashMap<String, Object>();
			
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession();

			Warehouse warehouse = (Warehouse) session
					.getAttribute("currentPhysicalWarehouse");
			int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
			
			SysUser sysUser = (SysUser) session.getAttribute("currentUser");
			String userName=sysUser.getUsername();
			
			int batch_pick_id = Integer.parseInt(req.getParameter("batch_pick_id"));
			
			List<Integer> orderIdList=new ArrayList<Integer>();
			
			BatchPick bp = batchPickDao.selectbatchPickForUpdateLockV2(batch_pick_id);
			String batch_process_type=bp.getBatch_process_type();
			int warehouse_id=bp.getWarehouse_id();
			
			
			
			if(bp.getReserve_status().equalsIgnoreCase("E")){
				
				Assert.isTrue(bp.getReserve_status().equalsIgnoreCase("E"), "只有库存分配失败的波次单才可以拆解");		
				
				String productIdString = req.getParameter("product_id");
				
				if(null!=productIdString&&(!"".equals(productIdString))){
					List<Integer> productIdList2=new ArrayList<Integer>();
					productIdList2=Tools.changeStringListToIntegerList(Tools.changeStringToList(productIdString, 1));
					
					if(!WorkerUtil.isNullOrEmpty(productIdList2)){
						orderIdList=orderProcessDao.getAllHasProductOrdersInBatchPick(productIdList2,batch_pick_id);
					}
				}
				
				//取消订单
				List<Integer> orderIdList2=orderProcessDao.getAllCancelOrderByBatchPickId(batch_pick_id);
				if(!WorkerUtil.isNullOrEmpty(orderIdList2)){
					orderIdList.addAll(orderIdList2);
				}

				if(!WorkerUtil.isNullOrEmpty(orderIdList)){
					
					List<OrderProcess> oplist=orderProcessDao.selectOrdersForLock2Task(orderIdList);
					
					List<UserActionOrder> userActionOrderList = new ArrayList<UserActionOrder>();
					orderInfoDao.updateForKickOutOrdersStatus(orderIdList);
					if("TOB".equalsIgnoreCase(batch_process_type)||"TOX".equalsIgnoreCase(batch_process_type)){
						orderProcessDao.updateForKickOutOrdersStatus(orderIdList);
					}else{
						orderProcessDao.updateForKickOutOrdersStatusV2(orderIdList);
					}

					
					for(OrderProcess op:oplist){
						UserActionOrder userActionOrder = new UserActionOrder();
	            		userActionOrder.setAction_note("从波次单中拆出："+op.getOrder_id());
	            		userActionOrder.setAction_type("TOACCEPT");
	            		userActionOrder.setCreated_time(new Date());
	            		userActionOrder.setCreated_user(userName);
	            		userActionOrder.setOrder_goods_id(null);
	            		userActionOrder.setOrder_id(op.getOrder_id());
	            		userActionOrder.setOrder_status("ACCPET");
	            		if(op.getStatus().equalsIgnoreCase(OrderInfo.ORDER_STATUS_CANCEL)){
	            			userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_CANCEL);
	            		}
	            		
	            		userActionOrderList.add(userActionOrder);
					}
					
					userActionOrderDao.batchInsert(userActionOrderList);
				}
				
				String batch_pick_sn =req.getParameter("batch_pick_sn");
				
				
				List<Map> pickOrderGoodsSum =doQueryGoods(batch_pick_sn);
				List<Integer> productIdList=new ArrayList<Integer>();
				for (Map m : pickOrderGoodsSum) {
					productIdList.add(Integer
							.parseInt(m.get("product_id").toString()));
				}

				if(WorkerUtil.isNullOrEmpty(productIdList)){
					
					List<ProductLocation> productLocationList =new ArrayList<ProductLocation>();
					List<Map> noPlOrderList=new ArrayList<Map>();
					resultMap.put("success", true);
					resultMap.put("message", "踢出成功");
					resultMap.put("noPlOrderList", noPlOrderList);
					resultMap.put("pickOrderGoodsSum", pickOrderGoodsSum);
					resultMap.put("productLocationList", productLocationList);
					return resultMap;  
				}
				List<ProductLocation> productLocationList = productLocationDao.selectProductlocationListForBatchPick(productIdList,physical_warehouse_id,warehouse_id);
				//全部数据
				Map<Integer,Integer> availableMap1=new HashMap<Integer,Integer>();
				//可分配区域数字
				Map<Integer,Integer> availableMap2=new HashMap<Integer,Integer>();
				

				Map<Integer,Integer> availableMap4=new HashMap<Integer,Integer>();//件
				Map<Integer,Integer> availableMap5=new HashMap<Integer,Integer>();//箱
				Map<Integer,Integer> availableMap6=new HashMap<Integer,Integer>();//存储
				Map<Integer,Integer> availableMap7=new HashMap<Integer,Integer>();//return
				Map<Integer,Integer> availableMap8=new HashMap<Integer,Integer>();//退货良品暂存
				//件拣和return区
				Map<Integer,Integer> availableMap3=new HashMap<Integer,Integer>();
				for(ProductLocation pl:productLocationList){
					if(null!=availableMap1.get(pl.getProduct_id())){
						availableMap1.put(pl.getProduct_id(), availableMap1.get(pl.getProduct_id())+pl.getQty_available());
					}else{
						availableMap1.put(pl.getProduct_id(),pl.getQty_available());
					}
					
					if(!pl.getLocation_type().equalsIgnoreCase("退货良品暂存区")){
						if(null!=availableMap2.get(pl.getProduct_id())){
							availableMap2.put(pl.getProduct_id(), availableMap2.get(pl.getProduct_id())+pl.getQty_available());
						}else{
							availableMap2.put(pl.getProduct_id(),pl.getQty_available());
						}
					}
					

					if(pl.getLocation_type().equalsIgnoreCase("件拣货区")||pl.getLocation_type().equalsIgnoreCase("退货区")){
						if(null!=availableMap3.get(pl.getProduct_id())){
							availableMap3.put(pl.getProduct_id(), availableMap3.get(pl.getProduct_id())+pl.getQty_available());
						}else{
							availableMap3.put(pl.getProduct_id(),pl.getQty_available());
						}
					}
					
					if(pl.getLocation_type().equalsIgnoreCase("件拣货区")){
						if(null!=availableMap4.get(pl.getProduct_id())){
							availableMap4.put(pl.getProduct_id(), availableMap4.get(pl.getProduct_id())+pl.getQty_available());
						}else{
							availableMap4.put(pl.getProduct_id(),pl.getQty_available());
						}
					}else if(pl.getLocation_type().equalsIgnoreCase("箱拣货区")){
						if(null!=availableMap5.get(pl.getProduct_id())){
							availableMap5.put(pl.getProduct_id(), availableMap5.get(pl.getProduct_id())+pl.getQty_available());
						}else{
							availableMap5.put(pl.getProduct_id(),pl.getQty_available());
						}
					}else if(pl.getLocation_type().equalsIgnoreCase("存储区")){
						if(null!=availableMap6.get(pl.getProduct_id())){
							availableMap6.put(pl.getProduct_id(), availableMap6.get(pl.getProduct_id())+pl.getQty_available());
						}else{
							availableMap6.put(pl.getProduct_id(),pl.getQty_available());
						}
					}else if(pl.getLocation_type().equalsIgnoreCase("退货区")){
						if(null!=availableMap7.get(pl.getProduct_id())){
							availableMap7.put(pl.getProduct_id(), availableMap7.get(pl.getProduct_id())+pl.getQty_available());
						}else{
							availableMap7.put(pl.getProduct_id(),pl.getQty_available());
						}
					}else{
						if(null!=availableMap8.get(pl.getProduct_id())){
							availableMap8.put(pl.getProduct_id(), availableMap8.get(pl.getProduct_id())+pl.getQty_available());
						}else{
							availableMap8.put(pl.getProduct_id(),pl.getQty_available());
						}
					}
				}
				
				List<Integer> noPlProductIdList=new ArrayList<Integer>();
				
				for(Map m : pickOrderGoodsSum){
					Integer product_id=Integer.parseInt(m.get("product_id").toString());
					int goods_number=Integer.parseInt(m.get("goods_number").toString());
					int spec = null==m.get("spec").toString()?0:Integer.parseInt(m.get("spec").toString());
					int avaliable1=null==availableMap1.get(product_id)?0:availableMap1.get(product_id);
					int avaliable2=null==availableMap2.get(product_id)?0:availableMap2.get(product_id);
					int avaliable3=null==availableMap3.get(product_id)?0:availableMap3.get(product_id);
					
					
					int avaliable4=null==availableMap4.get(product_id)?0:availableMap4.get(product_id);
					int avaliable5=null==availableMap5.get(product_id)?0:availableMap5.get(product_id);
					int avaliable6=null==availableMap6.get(product_id)?0:availableMap6.get(product_id);
					int avaliable7=null==availableMap7.get(product_id)?0:availableMap7.get(product_id);
					int avaliable8=null==availableMap8.get(product_id)?0:availableMap8.get(product_id);
					
					m.put("avaliable4", avaliable4);
					m.put("avaliable5", avaliable5);
					m.put("avaliable6", avaliable6);
					m.put("avaliable7", avaliable7);
					m.put("avaliable8", avaliable8);
					
					if(spec==0){
						m.put("messageString", "此商品未维护箱规，请维护箱规");
						m.put("mark", 1);
					}
					else if(avaliable3>=goods_number){
						m.put("messageString", "可以分配");
						//noPlProductIdList.add(product_id);
						m.put("mark", 1);
					}
					else if(avaliable2>=goods_number){
						m.put("messageString", "去查看补货任务，如果有任务，请执行补货");
						//noPlProductIdList.add(product_id);
						m.put("mark", 1);
					}else if(avaliable1>=goods_number){
						m.put("messageString", "去退货良品暂存区找找，如果发现库存，请执行移库");
						//noPlProductIdList.add(product_id);
						m.put("mark", 2);
					}else{
						m.put("messageString", "去本页面最下面的表格查看有无取消订单商品尚未上架，如果有记录，请执行退货上架");
						noPlProductIdList.add(product_id);
						m.put("mark", 3);
					}
				}
				
				List<Map> noPlOrderList=new ArrayList<Map>();
				if(!WorkerUtil.isNullOrEmpty(noPlProductIdList)){
					noPlOrderList=orderInfoDao.getNoPlOrderListByProductIdList(noPlProductIdList,physical_warehouse_id,warehouse_id);
				}
				
				resultMap.put("noPlOrderList", noPlOrderList);
				resultMap.put("pickOrderGoodsSum", pickOrderGoodsSum);
				resultMap.put("productLocationList", productLocationList);
				resultMap.put("success", true);
				resultMap.put("message", "踢出成功");
			}
			else if (bp.getReserve_status().equalsIgnoreCase("Y") && bp.getPrint_count()==0){
				
				Assert.isTrue(bp.getReserve_status().equalsIgnoreCase("Y"), "只有库存分配成功的波次单才可以释放库存");		
				
				Assert.isTrue(bp.getPrint_count()==0, "只有未打印的波此单才能释放库存");	
				
				//所有的任务和库存记录
				List<TaskAllotProductlocation> taskListPl = productLocationDao.selectTaskPlByBatchPickId(bp.getBatch_pick_id());				
				
				List<Integer> productIdList=new ArrayList<Integer>();
								
				for(TaskAllotProductlocation m:taskListPl){
					if(m.getQty_available().intValue()<0){
						productIdList.add(m.getProduct_id());
					}
				}
				
				if(!WorkerUtil.isNullOrEmpty(productIdList)){
					orderIdList=orderProcessDao.getAllHasProductOrdersInBatchPick(productIdList,batch_pick_id);
				}
				//取消订单
				List<Integer> orderIdList2=orderProcessDao.getAllCancelOrderByBatchPickId(batch_pick_id);
				if(!WorkerUtil.isNullOrEmpty(orderIdList)){
					orderIdList2.addAll(orderIdList);
				}
				
				if(!WorkerUtil.isNullOrEmpty(orderIdList2)){
				    
                    List<OrderProcess> oplist=orderProcessDao.selectOrdersForLock2Task(orderIdList2);
					
					List<OrderGoods> ogList=orderProcessDao.getOrderGoodsListByOrderIdList(orderIdList2);
				    
					for(OrderGoods og :ogList){
						Integer pid=og.getProduct_id(); 
						Integer num=og.getGoods_number();
						
						for(TaskAllotProductlocation  mm:taskListPl){
							Integer product_id=mm.getProduct_id();
							Integer qty_available=mm.getQty_available();
							Integer quantity=mm.getQuantity();
							
							if(pid.intValue()==product_id.intValue() && quantity>0){
								
								if(num>quantity){
									
									mm.setQuantity(0);
									mm.setChange_quantity(mm.getChange_quantity()+quantity);
									mm.setQty_available(qty_available+quantity);
									num=num-quantity;
									
								}
								else{
									
									mm.setQuantity(quantity-num);
									mm.setChange_quantity(mm.getChange_quantity()+num);
									mm.setQty_available(qty_available+num);
									break;
									
								}
							}
							
						}
					}
					
					//释放库存  task   product_location表
					if (!WorkerUtil.isNullOrEmpty(taskListPl)){
						productLocationDao.addQtyAvailableByTaskAllotProductlocationList(taskListPl);
						productLocationDao.minusQuantityByTaskAllotProductlocationList(taskListPl);
					}
					
					List<UserActionOrder> userActionOrderList = new ArrayList<UserActionOrder>();
					orderInfoDao.updateForKickOutOrdersStatus(orderIdList2);
					if("TOB".equalsIgnoreCase(batch_process_type)||"TOX".equalsIgnoreCase(batch_process_type)){
						orderProcessDao.updateForKickOutOrdersStatus(orderIdList2);
					}else{
						orderProcessDao.updateForKickOutOrdersStatusV2(orderIdList2);
					}

					
					for(OrderProcess op:oplist){
						UserActionOrder userActionOrder = new UserActionOrder();
	            		userActionOrder.setAction_note("从波次单中拆出："+op.getOrder_id());
	            		userActionOrder.setAction_type("TOACCEPT");
	            		userActionOrder.setCreated_time(new Date());
	            		userActionOrder.setCreated_user(userName);
	            		userActionOrder.setOrder_goods_id(null);
	            		userActionOrder.setOrder_id(op.getOrder_id());
	            		userActionOrder.setOrder_status("ACCPET");
	            		if(op.getStatus().equalsIgnoreCase(OrderInfo.ORDER_STATUS_CANCEL)){
	            			userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_CANCEL);
	            		}
	            		
	            		userActionOrderList.add(userActionOrder);
					}
					
					userActionOrderDao.batchInsert(userActionOrderList);					
				    
				}
				
				
				
				resultMap.put("noPlOrderList", new ArrayList<Map>());
				resultMap.put("pickOrderGoodsSum", null);
				resultMap.put("productLocationList", null);
				resultMap.put("success", true);
				resultMap.put("message", "踢出成功");
			}
			
			
			return resultMap;    

	}

	@Override
	public void doBatchKickOut(Integer batch_pick_id, int physical_warehouse_id,
			List<WarehouseCustomer> customers, String userName) {

		// 初始化
			Map<String, Object> resultMap = new HashMap<String, Object>();

			
			
			
			BatchPick bp = batchPickDao.selectbatchPickForUpdateLockV2(batch_pick_id);
			String batch_process_type=bp.getBatch_process_type();
			int warehouse_id=bp.getWarehouse_id();
			
			if(!bp.getReserve_status().equalsIgnoreCase("E")){
				return ;
			}
			
			List<Map> pickOrderGoodsSum =orderInfoDao.selectPickOrderGoodsListByBatchPickId(batch_pick_id);
			
			List<Integer> productIdList=new ArrayList<Integer>();
			for (Map m : pickOrderGoodsSum) {
				productIdList.add(Integer
						.parseInt(m.get("product_id").toString()));
			}

			if(WorkerUtil.isNullOrEmpty(productIdList)){

			}
			List<ProductLocation> productLocationList = productLocationDao.selectProductlocationListForBatchPick(productIdList,physical_warehouse_id,warehouse_id);
			//全部数据
			Map<Integer,Integer> availableMap1=new HashMap<Integer,Integer>();
			//可分配区域数字
			Map<Integer,Integer> availableMap2=new HashMap<Integer,Integer>();

			//件拣和return区
			Map<Integer,Integer> availableMap3=new HashMap<Integer,Integer>();
			for(ProductLocation pl:productLocationList){
				if(null!=availableMap1.get(pl.getProduct_id())){
					availableMap1.put(pl.getProduct_id(), availableMap1.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap1.put(pl.getProduct_id(),pl.getQty_available());
				}
				
				if(!pl.getLocation_type().equalsIgnoreCase("退货良品暂存区")){
					if(null!=availableMap2.get(pl.getProduct_id())){
						availableMap2.put(pl.getProduct_id(), availableMap2.get(pl.getProduct_id())+pl.getQty_available());
					}else{
						availableMap2.put(pl.getProduct_id(),pl.getQty_available());
					}
				}
				

				if(pl.getLocation_type().equalsIgnoreCase("件拣货区")||pl.getLocation_type().equalsIgnoreCase("退货区")){
					if(null!=availableMap3.get(pl.getProduct_id())){
						availableMap3.put(pl.getProduct_id(), availableMap3.get(pl.getProduct_id())+pl.getQty_available());
					}else{
						availableMap3.put(pl.getProduct_id(),pl.getQty_available());
					}
				}
			}
			
			List<Integer> productIdList2=new ArrayList<Integer>();
			
			for(Map m : pickOrderGoodsSum){
				Integer product_id=Integer.parseInt(m.get("product_id").toString());
				int goods_number=Integer.parseInt(m.get("goods_number").toString());
				int spec = null==m.get("spec").toString()?0:Integer.parseInt(m.get("spec").toString());
				int avaliable1=null==availableMap1.get(product_id)?0:availableMap1.get(product_id);
				int avaliable2=null==availableMap2.get(product_id)?0:availableMap2.get(product_id);
				int avaliable3=null==availableMap3.get(product_id)?0:availableMap3.get(product_id);
				
				if(spec==0){
					m.put("messageString", "此商品未维护箱规，请维护箱规");
					m.put("mark", 1);
				}
				else if(avaliable3>=goods_number){
					m.put("messageString", "可以分配");
					m.put("mark", 1);
				}
				else if(avaliable2>=goods_number){
					m.put("messageString", "去查看补货任务，如果有任务，请执行补货");
					m.put("mark", 1);
				}else if(avaliable1>=goods_number){
					m.put("messageString", "去退货良品暂存区找找，如果发现库存，请执行移库");
					m.put("mark", 2);
					productIdList2.add(product_id);
				}else{
					m.put("messageString", "去本页面最下面的表格查看有无取消订单商品尚未上架，如果有记录，请执行退货上架");
					m.put("mark", 3);
					productIdList2.add(product_id);
				}
			}

			List<Integer> orderIdList=new ArrayList<Integer>();
			
			if(!WorkerUtil.isNullOrEmpty(productIdList2)){
				orderIdList=orderProcessDao.getAllHasProductOrdersInBatchPick(productIdList2,batch_pick_id);
			}

			if(!WorkerUtil.isNullOrEmpty(orderIdList)){
				
				List<OrderProcess> oplist=orderProcessDao.selectOrdersForLock2Task(orderIdList);
				
				List<UserActionOrder> userActionOrderList = new ArrayList<UserActionOrder>();
				orderInfoDao.updateForKickOutOrdersStatus(orderIdList);
				if("TOB".equalsIgnoreCase(batch_process_type)||"TOX".equalsIgnoreCase(batch_process_type)){
					orderProcessDao.updateForKickOutOrdersStatus(orderIdList);
				}else{
					orderProcessDao.updateForKickOutOrdersStatusV2(orderIdList);
				}

				
				for(OrderProcess op:oplist){
					UserActionOrder userActionOrder = new UserActionOrder();
            		userActionOrder.setAction_note("从波次单中拆出："+op.getOrder_id());
            		userActionOrder.setAction_type("TOACCEPT");
            		userActionOrder.setCreated_time(new Date());
            		userActionOrder.setCreated_user(userName);
            		userActionOrder.setOrder_goods_id(null);
            		userActionOrder.setOrder_id(op.getOrder_id());
            		userActionOrder.setOrder_status("ACCPET");
            		if(op.getStatus().equalsIgnoreCase(OrderInfo.ORDER_STATUS_CANCEL)){
            			userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_CANCEL);
            		}
            		
            		userActionOrderList.add(userActionOrder);
				}
				
				userActionOrderDao.batchInsert(userActionOrderList);
			}

		
	}
	
	
	public List<Map> selectPickProductLocationByBatchPickSn(String batchPickSn){
		List<Map> pickOrderGoods = orderInfoDao.selectPickOrderGoodsList2(batchPickSn);
		return pickOrderGoods;
	}
	
	public List<Map> selectPickOrdersByBatchPickSn(String batchPickSn){
		List<Map> pickOrders = orderInfoDao.selectPickOrdersList2(batchPickSn);
		return pickOrders;
	}

	
	public Map updatePickProductLocationByBatchPickSn(Integer taskId,Integer goodsNumber,Integer plId,Integer newPlId,Integer customerId,Integer physicalWarehouseId){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		
		ReentrantLock lock = LockUtil.getReentrantLock("physicalWarehouseId_" + physicalWarehouseId + "_customerId_" + customerId);
		lock.lock();
		try {
			// 1.更新task from_pl_id
			int col = taskDao.updateTaskFromPlId(taskId, newPlId);
			if(col < 1){
				throw new RuntimeException("更新task from_pl_id失败");
			}
			
			// 2.回滚pl_id qty_available
			col = productLocationDao.updateProductLocationForAddAvailable(goodsNumber, plId);
			if(col < 1){
				throw new RuntimeException("回滚productLocation 可用库存量失败");
			}
			
			// 3.去new_pl_id扣减库存
			col = productLocationDao.updateProductLocationForMinusAvailable(goodsNumber, newPlId);
			if(col < 1){
				throw new RuntimeException("扣减productLocation库存失败");
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("updatePickProductLocationByBatchPickSn失败"+e.getMessage());
		} finally {
			lock.unlock();
		}
		resMap.put("result", Response.SUCCESS);
		resMap.put("note", "success");
		return resMap;
	}
	
	public Map deleteOrderByBatchPickSn(Integer orderId,Integer batchPickId,Integer physicalWarehouseId){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		// 1.接触order_process波次绑定关系
		orderProcessDao.deleteOrderProcessBatchPickId(orderId);
		
		// 2.释放该订单商品看库存
		List<OrderGoods> orderGoodsList = orderDao.selectOrderGoodsByOrderId(orderId);
		for(OrderGoods orderGood : orderGoodsList){
			Integer productId = orderGood.getProduct_id();
			Integer goodsNumber = orderGood.getGoods_number();
			List<Task> taskList = taskDao.selectTaskByProductId2BatchPicjId(batchPickId, productId);
			for(Task task : taskList){
				Integer quantity = task.getQuantity();
				if(goodsNumber <= quantity){
					productLocationDao.updateProductLocationForAddAvailable(goodsNumber, task.getFrom_pl_id());
					break;
				}else{
					productLocationDao.updateProductLocationForAddAvailable(quantity, task.getFrom_pl_id());
					taskDao.updateTaskStatusById(task.getTask_id(), "CANCEL");
					goodsNumber = goodsNumber - quantity;
				}
				
				if(goodsNumber == 0){
					break;
				}
			}
		}
		resMap.put("result", Response.SUCCESS);
		resMap.put("note", "删除成功");
		return resMap;
	}
	
	public void updateOrderProcessBatchTrickStatus(Integer orderId,String status){
		int col = orderProcessDao.updateOrderProcessBatchTrickStatus(status,orderId);
		if(col < 1){
			throw new RuntimeException("updateOrderProcessBatchTrickStatus更新失败");
		}
	}

	@Override
	public List<Map<String, Object>> getOrdersByShip(
			Map<String, Object> searchMap) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		String start = searchMap.containsKey("startTime")?searchMap.get("startTime").toString():null;
		String end = searchMap.containsKey("endTime")?searchMap.get("endTime").toString():null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期
		if (StringUtils.isBlank(start)) {
			java.util.Date now = new java.util.Date();
			Calendar c = Calendar.getInstance();
			if(StringUtils.isBlank(end)){
				c.setTime(now);
			}else{
				try {
					c.setTime(dateFormat.parse(end));
				} catch (ParseException e) {
				}
			}
			c.add(Calendar.DATE,-7);
			start = dateFormat.format(c.getTime());
		}
		start += " 00:00:00";
		if (!StringUtils.isBlank(end)) {
			end += " 23:59:59";
		}
		searchMap.put("start", start);
		searchMap.put("end", end);
		List<Map<String, Object>> orderlist = orderInfoDaoSlave.getOrdersByShipByPage(searchMap);
		return orderlist;
	}

	@Override
	public String batchChangeShip(List<Integer> customerIdList,Integer[] ids, Integer shippingId,
			String username) {
		StringBuilder msg = new StringBuilder();
		List<Integer> updatelist = new ArrayList<Integer>();
		List<UserActionOrder> actions = new ArrayList<UserActionOrder>();
		List<Shipping> shiplist = shippingDao.selectAllShipping();
		Map<Integer,String> shipmap = new HashMap<Integer,String>();
		for(Shipping s:shiplist){
			shipmap.put(s.getShipping_id(), s.getShipping_name());
		}
		if(!shipmap.containsKey(shippingId)){
			return "无效的目标快递";
		}
		List<OrderInfo> list =orderInfoDao.selectBatchOrderByIdForUpdate(ids);
		for(OrderInfo orderInfo:list){
			if(orderInfo.getShipping_id().equals(shippingId)){
				continue;
			}
			
			if(!OrderInfo.ORDER_STATUS_ACCEPT.equals(orderInfo.getOrder_status())){
				msg.append("【订单号为"+orderInfo.getOrder_id()+"的订单不是未处理的状态】");
				continue;
			}
			if(!customerIdList.contains(orderInfo.getCustomer_id())){
				msg.append("【订单号为"+orderInfo.getOrder_id()+"的订单货主id为"+orderInfo.getCustomer_id()+",您没有该货主的权限】");
				continue;
			}
			updatelist.add(orderInfo.getOrder_id());
			UserActionOrder action = new UserActionOrder();
			action.setCreated_time(new Date());
			action.setOrder_id(orderInfo.getOrder_id());
			action.setOrder_status(OrderInfo.ORDER_STATUS_ACCEPT);
			action.setAction_type("CHANGE_SHIP");
			action.setCreated_user(username);
			action.setAction_note("快递方式由【"+shipmap.get(orderInfo.getShipping_id())+"】变更为【"+shipmap.get(shippingId)+"】");
			actions.add(action);
		}
		if(updatelist.size()>0){
			orderInfoDao.batchUpdateShipId(updatelist,shippingId,username);
			shipmentDao.batchDeleteByOrderId(updatelist);
			orderProcessDao.batchUpdateIsfirstShipment(0,updatelist);
			userActionOrderDao.batchInsert(actions);
		}
		return msg.toString();
	}
}
