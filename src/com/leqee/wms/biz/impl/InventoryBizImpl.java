/**
 * 库存相关（出入库、库存查询等）
 */
package com.leqee.wms.biz.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.ReplenishmentBiz;
import com.leqee.wms.biz.ShipmentDetailBiz;
import com.leqee.wms.biz.WarehouseBiz;
import com.leqee.wms.dao.ConfigMailDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.InventoryItemDao;
import com.leqee.wms.dao.InventoryItemDetailDao;
import com.leqee.wms.dao.InventoryItemVarianceDao;
import com.leqee.wms.dao.LabelPrepackDao;
import com.leqee.wms.dao.LocationDao;
import com.leqee.wms.dao.OrderDao;
import com.leqee.wms.dao.OrderGoodsDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ProductLocationDetailDao;
import com.leqee.wms.dao.ProductLocationFreezeDetailDao;
import com.leqee.wms.dao.ReplenishmentDao;
import com.leqee.wms.dao.ScheduleQueueCountDao;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.dao.TmpProductLocationDao;
import com.leqee.wms.dao.UserActionOrderDao;
import com.leqee.wms.dao.UserActionShipmentDao;
import com.leqee.wms.dao.UserActionTaskDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.InventoryGoodsFreeze;
import com.leqee.wms.entity.InventoryItem;
import com.leqee.wms.entity.InventoryItemDetail;
import com.leqee.wms.entity.InventoryItemVariance;
import com.leqee.wms.entity.LabelAccept;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderReserveInventoryMapping;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ProductLocationDetail;
import com.leqee.wms.entity.ProductLocationFreezeDetail;
import com.leqee.wms.entity.ScheduleQueueCount;
import com.leqee.wms.entity.Shipment;
import com.leqee.wms.entity.ShipmentDetail;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Task;
import com.leqee.wms.entity.TmpProductLocation;
import com.leqee.wms.entity.UserActionOrder;
import com.leqee.wms.entity.UserActionShipment;
import com.leqee.wms.entity.UserActionTask;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.util.LockUtil;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;
import com.leqee.wms.vo.PurchaseAcceptVO;
import com.leqee.wms.vo.ReturnProductAccDetail;
import com.leqee.wms.vo.SaleProductDelDetail;

/**
 * @author Jarvis
 * @since 2016.02.02
 * @version 0.1
 * 
 */
@Service
public class InventoryBizImpl implements InventoryBiz {
	private Logger logger = Logger.getLogger(InventoryBizImpl.class);
	public final static String STATUS_ID_NORMAL = "NORMAL";
	public final static String STATUS_id_DEFECTIVE = "DEFECTIVE";

	@Autowired
	OrderInfoDao orderInfoDao;

	@Autowired
	OrderDao orderDao;

	@Autowired
	InventoryItemDetailDao inventoryItemDetailDao;

	@Autowired
	InventoryDao inventoryDao;

	@Autowired
	ConfigMailDao configMailDao;

	@Autowired
	OrderGoodsDao orderGoodsDao;

	@Autowired
	InventoryItemDao inventoryItemDao;

	@Autowired
	ProductDao productDao;

	@Autowired
	WarehouseDao warehouseDao;

	@Autowired
	ShipmentDetailBiz shipmentDetailBiz;

	@Autowired
	WarehouseBiz warehouseBiz;

	@Autowired
	OrderBiz orderBiz;

	@Autowired
	OrderProcessDao orderProcessDao;

	@Autowired
	UserActionOrderDao userActionOrderDao;
	
	@Autowired
	LabelPrepackDao labelPrepackDao;

	@Autowired
	ShipmentDao shipmentDao;

	@Autowired
	UserActionShipmentDao userActionShipmentDao;

	@Autowired
	InventoryItemVarianceDao inventoryItemVarianceDao;

	@Autowired
	ProductLocationDao productLocationDao;

	@Autowired
	ProductLocationDetailDao productLocationDetailDao;

	@Autowired
	TaskDao taskDao;

	@Autowired
	ReplenishmentDao replenishmentDao;
	
	@Autowired
	LocationDao locationDao;
	
	@Autowired
	ReplenishmentBiz replenishmentBiz;
	
	@Autowired
	ProductLocationFreezeDetailDao productLocationFreezeDetailDao;
	
	@Autowired
	TmpProductLocationDao tmpProductLocationDao;
	
	@Autowired
	ScheduleQueueCountDao scheduleQueueCountDao;

	@Autowired
	UserActionTaskDao userActionTaskDao;
	private InventoryDao inventoryDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setInventoryDaoSlave(SqlSession sqlSession) {
	  this.inventoryDaoSlave = sqlSession.getMapper(InventoryDao.class);
	}
	
	
	public HashMap<String, Object> checkDealBatchDelivery(Boolean isSerial,
			String serialNums, String status, String productId,
			BigDecimal amount, String orderId, String orderGoodsId,
			String warehouse_id, BigDecimal unitPrice, String batchSn,
			String returnSupplierId, String customer_id) {

		HashMap<String, Object> result = new HashMap<String, Object>();

		if (amount.intValue() == 0) {
			logger.error("amount=" + amount);
			result.put("error", "出库失败");
			result.put("message", "出库数量为0");
			return result;
		}

		String username = (String) SecurityUtils.getSubject().getPrincipal();
		if (WorkerUtil.isNullOrEmpty(username)) {
			result.put("error", "出库失败");
			result.put("message", "获取不到用户，请重新登录");
			return result;
		}

		logger.info("dealBatchDelivery:  isSerial: " + isSerial + " serialNo: "
				+ serialNums + " status: " + status + " orderId: " + orderId
				+ " orderGoodsId: " + orderGoodsId + " productId: " + productId
				+ " warehouse_id: " + warehouse_id + " amount: " + amount
				+ " actionUser: " + username + " unitPrice: " + unitPrice
				+ " batchSn: " + batchSn + " returnSupplierId: "
				+ returnSupplierId);

		if (WorkerUtil.isNullOrEmpty(serialNums)) {
			dealBatchDelivery(null, null, status, productId,
					WorkerUtil.getDecimalValue(amount), orderId, orderGoodsId,
					username, warehouse_id,
					WorkerUtil.getDecimalValue(unitPrice), batchSn,
					returnSupplierId, customer_id + "");
		} else {
			String[] strarray = serialNums.split(",");
			for (int i = 0; i < strarray.length; i++) {
				dealBatchDelivery(true, strarray[i].trim(), status, productId,
						WorkerUtil.getDecimalValue("1"), orderId, orderGoodsId,
						username, warehouse_id,
						WorkerUtil.getDecimalValue(unitPrice), batchSn,
						returnSupplierId, customer_id + "");
			}
		}
		List<Map<String,Object>> goodsReturnPrintList = this.getGoodsReturnPrintList(orderId);
		List<Map<String,Object>> goodsReturnPrintInfo = (List<Map<String, Object>>) goodsReturnPrintList.get(0).get("goodsReturnPrintInfo");
		for (Map<String, Object> map : goodsReturnPrintInfo) {
			Integer quantity = Integer.parseInt(String.valueOf(map.get("quantity")));
			Integer plId = Integer.parseInt(String.valueOf(map.get("pl_id")));
			Integer locationId = Integer.parseInt(String.valueOf(map.get("location_id")));
			//productLocation的减库存
			productLocationDao.updateProductLocationTotal(-quantity, plId);
			ProductLocationDetail productLocationDetail = new ProductLocationDetail();
			productLocationDetail.setPl_id(plId);
			productLocationDetail.setChange_quantity(-quantity);
			productLocationDetail.setTask_id(0); // 任务ID
			productLocationDetail.setDescription("-gt退货出库");
			productLocationDetail.setCreated_user(username);
			productLocationDetail.setLast_updated_user(username);
			productLocationDetail.setOrder_id(Integer.parseInt(orderId));
			productLocationDetail.setOrder_goods_id(Integer.parseInt(orderGoodsId));
			productLocationDetailDao.insert(productLocationDetail);
			//判断源库位是否为空
			Integer isEmpty = productLocationDao.selectMapFromLocationId(locationId);
			if(isEmpty==0){
				locationDao.updateLocationIsEmpty(locationId);
			}
		}
		HashMap<String, Object> paramsForOrderInfoUpdate =  new HashMap<String, Object>();
		paramsForOrderInfoUpdate.put("orderStatus", "FULFILLED");
		paramsForOrderInfoUpdate.put("lastUpdatedUser", username);
		paramsForOrderInfoUpdate.put("lastUpdatedTime", new Date());
		paramsForOrderInfoUpdate.put("orderId", orderId);
		orderInfoDao.updateOrderStatusByOrderId(paramsForOrderInfoUpdate);
		Map<String,Object> paramsForOrderProcessUpdate = new HashMap<String,Object>();
		paramsForOrderProcessUpdate.put("status", "FULFILLED");
		paramsForOrderProcessUpdate.put("orderId", orderId);
		orderProcessDao.updateOrderStatusByOrderId(paramsForOrderProcessUpdate);
		result.put("success", true);
		result.put("message", "出库成功 orderGoodsId:" + orderGoodsId);
		return result;

	}

	/**
	 * 批量出库
	 */
	@Override
	public boolean dealBatchDelivery(Boolean isSerial, String serialNo,
			String status, String productId, BigDecimal amount, String orderId,
			String orderGoodsId, String actionUser, String warehouse_id,
			BigDecimal unitPrice, String batchSn, String returnSupplierId,
			String customerId) {

		logger.info("dealBatchDelivery:  isSerial: " + isSerial + " serialNo: "
				+ serialNo + " status: " + status + " orderId: " + orderId
				+ " orderGoodsId: " + orderGoodsId + " productId: " + productId
				+ " warehouse_id: " + warehouse_id + " amount: " + amount
				+ " actionUser: " + actionUser + " unitPrice: " + unitPrice
				+ " batchSn: " + batchSn + " returnSupplierId: "
				+ returnSupplierId + " customerId:" + customerId);


		OrderInfo orderInfo = orderDao.getOderInfoByIdForUpdate(Integer
				.valueOf(orderId));
		
		Response response = this.deliverOrderInventory(OrderInfo.ORDER_TYPE_SUPPLIER_RETURN,Integer.parseInt(orderId),Integer.parseInt(orderGoodsId));
		if(!Response.OK.equals(response.getCode())){
			throw new RuntimeException("商品出库失败结束,orderId: " + orderId + ", actionUser:"+actionUser+" 扣减inventory_item失败");
		}
		return true;
	}

	private HashMap<String, BigInteger> getInventoryItemsForDeliver(
			String warehouse_id, Boolean isSerial, String productId,
			String status, String serialNo, BigDecimal amount,
			BigDecimal unitPrice, String batchSn, String returnSupplierId) {

		HashMap<String, BigInteger> inventoryItemList = new HashMap<String, BigInteger>();

		String cond = " ";
		if (!WorkerUtil.isNullOrEmpty(isSerial)) {
			cond += " and ii.serial_number = '" + serialNo + "'";
		}
		if (!WorkerUtil.isNullOrEmpty(unitPrice)) {
			cond += " and ii.unit_cost = '" + unitPrice + "'";
		}
		if (!WorkerUtil.isNullOrEmpty(batchSn)) {
			cond += " and ii.batch_sn = '" + batchSn + "'";
		} else {
			cond += " and ii.batch_sn = ''";
		}
		if (!WorkerUtil.isNullOrEmpty(returnSupplierId)) {
			cond += " and ii.provider_code = '" + returnSupplierId + "'";
		}

		HashMap<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("product_id", productId);
		paramsMap.put("warehouse_id", warehouse_id);
		paramsMap.put("status", status);
		paramsMap.put("cond", cond);

		List<Map> itemsListMap = inventoryDao
				.getInventoryItemsForDeliver(paramsMap);

		if (WorkerUtil.isNullOrEmpty(itemsListMap)) {
			logger.info("getInventoryItemsForDeliver inventoryItems is null");
			return null;
		}

		BigInteger req = WorkerUtil.getIntegerValue(amount);
		for (Map map : itemsListMap) {
			String inventoryItemId = String.valueOf(map
					.get("inventory_item_id"));
			BigInteger quantity = new BigInteger(map.get("quantity") + "");

			logger.info("getInventoryItemsForDeliver inventory_item_id:"
					+ inventoryItemId + " quantity:" + quantity);

			if (req.compareTo(quantity) > 0) {
				inventoryItemList.put(inventoryItemId, quantity);
			} else {
				inventoryItemList.put(inventoryItemId, req);
			}

			req = req.subtract(quantity);
			if (req.compareTo(WorkerUtil.getIntegerValue(0)) <= 0) {
				break;
			}

		}
		if (req.compareTo(WorkerUtil.getIntegerValue(0)) > 0) {
			logger.info("getInventoryItemsForDeliver is not enough productId:"
					+ productId + " warehouse_id:" + warehouse_id + " reqLeft:"
					+ req + " amount:" + amount + " serialNo:" + serialNo);
			return null;
		}

		return inventoryItemList;
	}

	private boolean checkOrderProductOutNumber(String orderGoodsId,
			String productId, BigDecimal amount) {

		HashMap<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("orderGoodsId", orderGoodsId);
		paramsMap.put("productId", productId);
		Map outNumMap = inventoryDao.checkOrderProductOutNumber(paramsMap);
		if (WorkerUtil.isNullOrEmpty(outNumMap)) {
			logger.error("checkOrderProductOutNumber sql is null");
			return false;
		}

		BigDecimal goodsNumber = WorkerUtil.getDecimalValue(outNumMap
				.get("goods_number"));
		BigDecimal outNumber = WorkerUtil.getDecimalValue(outNumMap
				.get("out_num"));

		BigDecimal leftNumber = goodsNumber.subtract(outNumber);
		logger.info("checkOrderProductOutNumber goodsNumber:" + goodsNumber
				+ " outNumber:" + outNumber + " leftNumber:" + leftNumber
				+ " amount:" + amount);

		if (leftNumber.compareTo(amount) == -1) {
			return false;
		}

		return true;

	}

	@Override
	public List<Map<String, Object>> selectByOrdersn(Map map) {
		String start = String.valueOf(map.get("created_time"));
		String end = String.valueOf(map.get("delivery_time"));
		if (start.equals("") || start.isEmpty()) {
			java.util.Date now = new java.util.Date();
			Calendar c = Calendar.getInstance();
			c.setTime(now);
			c.set(Calendar.DATE, c.get(Calendar.DATE) - 14); // 最近15天，即从之前的14天开始
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期

			start = dateFormat.format(c.getTime());
		}
		start += " 00:00:00";

		if (end.equals("") || end.isEmpty()) {
			java.util.Date now = new java.util.Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期
			end = dateFormat.format(now);
		}
		end += " 23:59:59";
		map.put("start", start);
		map.put("end", end);
		return inventoryDao.selectByOrdersnByPage(map);
	}


	public BigDecimal getOrderProductMaxOutNumber(String orderGoodsId,
			String productId) {

		HashMap<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("orderGoodsId", orderGoodsId);
		paramsMap.put("productId", productId);
		Map outNumMap = inventoryDao.checkOrderProductOutNumber(paramsMap);
		if (WorkerUtil.isNullOrEmpty(outNumMap)) {
			logger.error("checkOrderProductOutNumber sql is null");
			return new BigDecimal(0);
		}

		BigDecimal goodsNumber = WorkerUtil.getDecimalValue(outNumMap
				.get("goods_number"));
		BigDecimal outNumber = WorkerUtil.getDecimalValue(outNumMap
				.get("out_num"));

		BigDecimal leftNumber = goodsNumber.subtract(outNumber);
		logger.info("checkOrderProductOutNumber goodsNumber:" + goodsNumber
				+ " outNumber:" + outNumber + " leftNumber:" + leftNumber);

		return leftNumber;

	}

	@Override
	public Response saleDeliverInventory(Integer orderId, String actionUser) {
		// 1.初始化返回结果
		Response result = ResponseFactory.createOkResponse("出库成功!");

		// 2.基本数据校验，若Assert断言失败，会抛出异常，终止程序
		Assert.notNull(orderId, "订单号为空!");
		logger.info("出库开始,orderId: " + orderId + ", actionUser: " + actionUser);

		// 3.查看并检查订单状态
		OrderInfo orderInfo = orderDao.selectOrderInfoByIdForUpdate(orderId);
		Assert.notNull(orderInfo, "订单(订单ID:" + orderId + ")不存在!");
		Assert.isTrue(OrderInfo.ORDER_STATUS_DELEVERED.equals(orderInfo
				.getOrder_status()), "订单(订单ID:" + orderId + ")不是 已发货 状态!");

		// 4.过滤业务组,如果是依云业务组，则按照预定到的item出库
		try {
			Response response = this.deliverOrderInventory(OrderInfo.ORDER_TYPE_SALE,orderId,null);
			if(!Response.OK.equals(response.getCode())){
				throw new RuntimeException("商品出库失败结束,orderId: " + orderId + ", actionUser:"+actionUser+" 扣减inventory_item失败");
			}
			response = this.deliverOrderProductLocation(orderId);
			if(!Response.OK.equals(response.getCode())){
				throw new RuntimeException("商品出库失败结束,orderId: " + orderId + ", actionUser:"+actionUser+" 扣减product_location失败");
			}
			// 5.更新订单状态
			int effectRows = orderProcessDao.setSaleOrderStatusFulfilled(orderId);
			Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);
			effectRows = orderBiz.updateOrderInfoStatus(orderId,
					OrderInfo.ORDER_STATUS_FULFILLED, actionUser);
			Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);

			// 6.更新order_process batch_trick_status状态
			orderBiz.updateOrderProcessBatchTrickStatus(orderId, "FULFILLED");
			
			// 7.记录订单操作日志
			UserActionOrder userActionOrder = new UserActionOrder();
			userActionOrder.setAction_note("订单完成出库");
			userActionOrder.setAction_type("INVENTORY_OUT");
			userActionOrder.setCreated_time(new Date());
			userActionOrder.setCreated_user(actionUser);
			userActionOrder.setOrder_id(orderId);
			userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_FULFILLED);
			userActionOrderDao.insert(userActionOrder);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("商品出库失败结束,orderId: " + orderId + "，商品出库失败"+e.getMessage());
		}
		logger.info("出库结束,orderId: " + orderId + ", actionUser: " + actionUser);

		return result;
	}

	
	@Override
	public Response saleDeliverInventoryFor1111(Integer orderId, String actionUser) {
		// 初始化
		Response result = ResponseFactory.createOkResponse("出库成功!");

		// 基本数据校验，若Assert断言失败，会抛出异常，终止程序
		Assert.notNull(orderId, "订单号为空!");

		logger.info("出库开始,orderId: " + orderId + ", actionUser: " + actionUser);

		// 查看并检查订单状态
		OrderInfo orderInfo = orderDao.selectOrderInfoByIdForUpdate(orderId);
		Assert.notNull(orderInfo, "订单(订单ID:" + orderId + ")不存在!");
		Assert.isTrue(OrderInfo.ORDER_STATUS_PICKING.equals(orderInfo
				.getOrder_status()), "订单(订单ID:" + orderId + ")不是 已发货 状态!");

		// 过滤业务组,如果是依云业务组，则按照预定到的item出库
		try {
			Response response = this.deliverOrderInventory(OrderInfo.ORDER_TYPE_SALE,orderId,null);
			if(!Response.OK.equals(response.getCode())){
				throw new RuntimeException("商品出库失败结束,orderId: " + orderId + ", actionUser:"+actionUser+" 扣减inventory_item失败");
			}
			response = this.deliverOrderProductLocation(orderId);
			if(!Response.OK.equals(response.getCode())){
				throw new RuntimeException("商品出库失败结束,orderId: " + orderId + ", actionUser:"+actionUser+" 扣减product_location失败");
			}
			// 更新订单状态
			int effectRows = orderProcessDao.setSaleOrderStatusFulfilled(orderId);
			Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);
			effectRows = orderBiz.updateOrderInfoStatus(orderId,
					OrderInfo.ORDER_STATUS_FULFILLED, actionUser);
			Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);

			shipmentDao.batchUpdateShipmentStatus(orderId);
			
			// 插入shipment_detail
			shipmentDao.batchInsertShipmentDetail(orderId);
			
			orderBiz.updateOrderProcessBatchTrickStatus(orderId, "FULFILLED");

			// 记录订单操作日志
			UserActionOrder userActionOrder = new UserActionOrder();
			userActionOrder.setAction_note("订单完成出库");
			userActionOrder.setAction_type("INVENTORY_OUT");
			userActionOrder.setCreated_time(new Date());
			userActionOrder.setCreated_user(actionUser);
			userActionOrder.setOrder_id(orderId);
			userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_FULFILLED);
			userActionOrderDao.insert(userActionOrder);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("商品出库失败结束,orderId: " + orderId + "，商品出库失败"+e.getMessage());
		}
		logger.info("出库结束,orderId: " + orderId + ", actionUser: " + actionUser);

		return result;
	}
	
	// 耗材出库
	@Override
	public Response packBoxDeliverInventory(Integer shipmentId,
			String actionUser) {
		// 1.初始化返回结果
		Response result = ResponseFactory.createOkResponse("耗材出库成功!");
		logger.info("耗材出库开始,shipmentId: " + shipmentId + ", actionUser: "
				+ actionUser);

		// 2.获取该销售订单需要出库的订单商品及其数量
		Map<String, Object> toDeliverProduct = inventoryDao
				.getNeedOutPackBoxNumByOrder(shipmentId);
		Assert.notNull(toDeliverProduct, "shipmentId:" + shipmentId + ")无耗材信息!");

		// 3.获取耗材相关信息
		Integer orderId = null;
		orderId = WorkerUtil.getIntegerValue(toDeliverProduct.get("order_id"))
				.intValue();
		Integer thisTurnOutNum = WorkerUtil.getIntegerValue(
				toDeliverProduct.get("goods_number")).intValue(); // 该商品需要出库的数量
		Integer orderGoodsId = WorkerUtil.getIntegerValue(
				toDeliverProduct.get("order_goods_id")).intValue();
		Integer productId = WorkerUtil.getIntegerValue(
				toDeliverProduct.get("product_id")).intValue();
		String fromStatusId = WorkerUtil.getStringValue(toDeliverProduct
				.get("status_id"));
		Integer warehouseId = WorkerUtil.getIntegerValue(
				toDeliverProduct.get("warehouse_id")).intValue();
		Integer physicalWarehouseId = WorkerUtil.getIntegerValue(
				toDeliverProduct.get("physical_warehouse_id")).intValue();
		Integer customerId = WorkerUtil.getIntegerValue(
				toDeliverProduct.get("customer_id")).intValue();
		String orderGoodsType = WorkerUtil.getStringValue(toDeliverProduct
				.get("order_goods_type"));
		Integer productCustomerId = WorkerUtil.getIntegerValue(
				toDeliverProduct.get("product_customer_id")).intValue();
		List<String> serialNos = null;

		// 4.耗材仓库查找
		boolean isPackbox = false;
		Integer packboxWarehouseId = null; // 耗材所属仓库
		Integer packboxCustomerId = null; // 耗材所属货主（用以区分通用/专用耗材）
		if ("PACKBOX".equals(orderGoodsType)) {
			isPackbox = true;
			packboxCustomerId = productCustomerId;
			packboxWarehouseId = warehouseId;
//			if (packboxCustomerId.equals(customerId)) {
//				packboxWarehouseId = warehouseId; // 专用耗材
//			} else {
//				Warehouse warehouse = warehouseBiz
//						.findByWarehouseId(warehouseId);
//				packboxWarehouseId = warehouse.getPackbox_warehouse_id(); // 通用耗材
//			}
		} else {
			result = ResponseFactory.createOkResponse("shipmentId:"
					+ shipmentId + ",orderGoodsId:" + orderGoodsId + "耗材信息有误");
			result.setCode(Response.ERROR);
			return result;
		}

		// 5.处理耗材出库
		BigDecimal unitCost = null; // 采购单价
		String batchSn = null; // 批次号
		Response response = createDeliverInventoryTransaction(orderId,
				customerId, productId, thisTurnOutNum, serialNos, orderGoodsId,
				actionUser, fromStatusId, warehouseId, unitCost, batchSn,
				packboxCustomerId, packboxWarehouseId, isPackbox);
		if (WorkerUtil.isNullOrEmpty(response)) {
			result = ResponseFactory
					.createOkResponse("商品出库无反馈消息, orderGoodsId:" + orderGoodsId);
			result.setCode(Response.ERROR);
			return result;
		} else if (!Response.OK.equals(response.getCode())) {
			result = ResponseFactory.createOkResponse("商品出库发生错误, orderGoodsId:"
					+ orderGoodsId + ",错误信息:" + response.getMsg());
			result.setCode(Response.ERROR);
			return result;
		}

		// 耗材出库product_location
		this.deliverPackBoxProductLocation(orderId, orderGoodsId, productId, customerId,physicalWarehouseId, thisTurnOutNum, fromStatusId);
		if(!Response.OK.equals(response.getCode())){
			throw new RuntimeException("耗材出库失败结束,orderId: " + orderId + ", shipmentId:"+shipmentId+" 扣减product_location失败");
		}
		
		// 6.更新Shipment表的耗材出库状态
		shipmentDao.updatePackBoxStatus(shipmentId, null, 'Y', new Date(),
				actionUser);

		// 7.记录Shipment操作日志
		UserActionShipment userActionShipment = new UserActionShipment();
		userActionShipment.setShipment_id(shipmentId);
		userActionShipment.setStatus(Shipment.STATUS_SHIPPED);
		userActionShipment.setAction_type("PACKBOX_OUT");
		userActionShipment.setAction_note("耗材完成出库");
		userActionShipment.setCreated_user(actionUser);
		userActionShipment.setCreated_time(new Date());
		userActionShipmentDao
				.insertUserActionShipmentRecord(userActionShipment);

		// 8.记录订单操作日志
		// UserActionOrder userActionOrder = new UserActionOrder();
		// userActionOrder.setAction_note("订单耗材完成出库");
		// userActionOrder.setAction_type("INVENTORY_OUT");
		// userActionOrder.setCreated_time(new Date());
		// userActionOrder.setCreated_user(actionUser);
		// userActionOrder.setOrder_id(orderId);
		// userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_FULFILLED);
		// userActionOrderDao.insert(userActionOrder);

		logger.info("耗材出库结束,shipmentId: " + shipmentId + ", actionUser: "
				+ actionUser);

		return result;
	}

	/**
	 * 获取该销售订单需要出库的订单商品及其数量等
	 * 
	 * @param orderId
	 * @return
	 */
	private List<Map<String, Object>> getSaleToDeliverProductByOrderId(
			Integer orderId) {
		return inventoryDao.getNeedOutNumByOrder(orderId);
	}

	/**
	 * 创建某个订单商品的销售出库
	 * 
	 * @param orderId
	 *            订单ID
	 * @param customerId
	 *            货主ID
	 * @param productId
	 *            产品ID
	 * @param thisTurnOutNum
	 *            本次需要出库的数量
	 * @param serialNos
	 *            串号列表
	 * @param orderGoodsId
	 *            商品ID
	 * @param actionUser
	 *            操作人
	 * @param fromStatusId
	 *            新旧状态
	 * @param warehouseId
	 *            仓库
	 * @param unitCost
	 *            采购单价
	 * @param batchSn
	 *            批次号
	 * @param packboxWarehouseId
	 *            耗材仓库
	 * @param packboxCustomerId
	 *            耗材货主
	 * @param isPackbox
	 *            是否耗材
	 * @return
	 */
	private Response createDeliverInventoryTransaction(Integer orderId,
			Integer customerId, Integer productId, int thisTurnOutNum,
			List<String> serialNos, Integer orderGoodsId, String actionUser,
			String fromStatusId, Integer warehouseId, BigDecimal unitCost,
			String batchSn, Integer packboxCustomerId,
			Integer packboxWarehouseId, boolean isPackbox) {
		logger.info("createDeliverInventoryTransaction, orderId:" + orderId
				+ ", customerId:" + customerId + ", productId:" + productId
				+ ", orderGoodsId:" + orderGoodsId + ", warehouseId:"
				+ warehouseId + ", fromStatusId:" + fromStatusId
				+ ", packboxCustomerId:" + packboxCustomerId
				+ ", packboxWarehouseId:" + packboxWarehouseId + ", isPackbox:"
				+ isPackbox);
		Response response = null;

		// 获取需要用来销售出库的InventoryItems
		Integer fromCustomerId = isPackbox ? packboxCustomerId : customerId;
		Integer fromWarehouseId = isPackbox ? packboxWarehouseId : warehouseId;
		List<InventoryItem> inventoryItems = getInventoryItemsForDelivery(
				fromWarehouseId, fromCustomerId, serialNos, productId,
				fromStatusId, thisTurnOutNum, unitCost, batchSn);

		List<Map> inventoryItemHasReservedList = inventoryDao.inventoryItemHasReserved(productId,warehouseId);
		Map<String,Object> itemReservedMap = new HashMap<String,Object>();
		for(Map map : inventoryItemHasReservedList){
			itemReservedMap.put(map.get("inventory_item_id").toString(), map.get("quantity"));
		}
		
		// 无库存,直接返回
		if (WorkerUtil.isNullOrEmpty(inventoryItems)) {
			response = ResponseFactory.createErrorResponse("无库存");
			return response;
		}

		int thisTurnOutNumLeft = thisTurnOutNum;
		for (InventoryItem inventoryItem : inventoryItems) {
			String inventoryItemId = inventoryItem.getInventory_item_id().toString();
			int need = inventoryItem.getQuantity();
			
			// 判断耗材出库数量是否已经被预定，如没有，可扣减出库；否则，不允许扣减继续遍历
			if(itemReservedMap.containsKey(inventoryItemId)){
				int tempNum = Integer.parseInt(itemReservedMap.get(inventoryItemId).toString()) ;
				if((tempNum + thisTurnOutNumLeft) > need){
					continue;
				}
			}
			
			
			if (thisTurnOutNumLeft <= need) {
				need = thisTurnOutNumLeft;
			}
			
			// 扣减InventoryItem库存数量
			subQuantityByInventoryItemId(inventoryItem.getInventory_item_id(),
					need, actionUser);

			// 插入InventoryItemDetail记录
			saveInventoryItemDetail(orderId, customerId, productId,
					orderGoodsId, actionUser, warehouseId, inventoryItem, need,
					packboxCustomerId, packboxWarehouseId);

			thisTurnOutNumLeft = thisTurnOutNumLeft - need;
			if (thisTurnOutNumLeft == 0) {
				response = ResponseFactory
						.createOkResponse("all delivery success");
				break;
			}
		}

		return response;
	}

	// 订单出库扣减item数量
	public Response deliverOrderInventory(String orderType,Integer orderId, Integer orderGoodsId) {

		Response response = null;
		List<Map> reserveMappingList = inventoryDao
				.selectDeliverOrderReserverInfo(orderId, null);
		try {
			if(WorkerUtil.isNullOrEmpty(reserveMappingList)){
				throw new RuntimeException("reserve_inventory_item no record");
			}
			for (Map map : reserveMappingList) {

				Integer inventoryItemId = Integer.parseInt(map.get("inventory_item_id").toString());
				Integer quantity = Integer.parseInt(map.get("quantity").toString());
				Integer customerId = Integer.parseInt(map.get("customer_id").toString());
				Integer productId = Integer.parseInt(map.get("product_id").toString());
				Integer warehouseId = Integer.parseInt(map.get("warehouse_id").toString());
				Integer orderGoodsId_ = Integer.parseInt(map.get("order_goods_id").toString());
				String omsOrderGoodsSn = WorkerUtil.isNullOrEmpty(map.get("oms_order_goods_sn"))?"":map.get("oms_order_goods_sn").toString();
				Integer orderGoodsOmsId = WorkerUtil.isNullOrEmpty(map.get("order_goods_oms_id"))?0:Integer.parseInt(map.get("order_goods_oms_id").toString());
				
				if(!WorkerUtil.isNullOrEmpty(orderGoodsId_) && !orderGoodsId_.equals(0)){
					if(!WorkerUtil.isNullOrEmpty(inventoryDao.selectReservedQty2OrderGoodsNumber(orderGoodsId_))){
						throw new RuntimeException("InventoryItem:"
								+ inventoryItemId + ",orderGoodsId:"+orderGoodsId_+"出库数量与订单数量不一致");
					}
				}else{
					if(!WorkerUtil.isNullOrEmpty(orderGoodsOmsId) && !orderGoodsOmsId.equals(0)){
						if(!WorkerUtil.isNullOrEmpty(inventoryDao.selectReservedQty2OrderGoodsOmsNumber(orderGoodsOmsId))){
							throw new RuntimeException("InventoryItem:"
									+ inventoryItemId + ",orderGoodsId:"+orderGoodsOmsId+"出库数量与订单数量不一致");
						}
					}
				}
				
				int col = inventoryDao.updateInventoryItemByReserveMapping(
						inventoryItemId, quantity);
				if (col < 1) {
					throw new RuntimeException("InventoryItem:"
							+ inventoryItemId + "数量不足");
				}
				
				InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
				inventoryItemDetail.setOrder_id(orderId);
				inventoryItemDetail.setProduct_id(productId);
				inventoryItemDetail.setCustomer_id(customerId);
				inventoryItemDetail.setWarehouse_id(warehouseId);
				inventoryItemDetail.setInventory_item_id(inventoryItemId);
				inventoryItemDetail.setOrder_goods_id(orderGoodsId_);
				inventoryItemDetail.setChange_quantity(-quantity);
				inventoryItemDetail.setPackbox_customer_id(0);
				inventoryItemDetail.setPackbox_warehouse_id(0);
				inventoryItemDetail.setCreated_time(new Date());
				inventoryItemDetail.setCreated_user("system");
				inventoryItemDetail.setLast_updated_user("system");
				inventoryItemDetail.setLast_updated_time(new Date());
				inventoryItemDetail.setOms_order_goods_sn(omsOrderGoodsSn);
				inventoryItemDetail.setOrder_goods_oms_id(orderGoodsOmsId);
				
				int effectRows = inventoryItemDetailDao.insert(inventoryItemDetail);
				if (effectRows <= 0) {
					throw new RuntimeException("inventory_item_detail insert error no effect");
				}
				
				if(OrderInfo.ORDER_TYPE_VARIANCE_MINUS.equals(orderType)){
					// 创建一条variance记录
					InventoryItemVariance inventoryItemVariance = new InventoryItemVariance();
					inventoryItemVariance.setComments("库存调整");
					inventoryItemVariance.setCreated_user("system");
					inventoryItemVariance.setInventory_item_id(inventoryItemId);
					inventoryItemVariance.setQuantity(-quantity);
					inventoryItemVariance.setCreated_time(new Date());
					inventoryItemVariance.setLast_updated_time(new Date());
					inventoryItemVarianceDao.insert(inventoryItemVariance);
				}
				
			}
			response = new Response(Response.OK, Response.SUCCESS);
		} catch (Exception e) {
			logger.error(
					"[DeliverOrderInventory] orderId:" + orderId
							+ ",orderGoodsId:" + orderGoodsId + "出库失败，失败原因："
							+ e.getMessage(), e);
			throw new RuntimeException("[DeliverOrderInventory] orderId:"
					+ orderId + ",orderGoodsId:" + orderGoodsId + "出库失败，失败原因："
					+ e.getMessage());
		}

		return response;
	}

	// 订单出库扣减product_location
	public Response deliverOrderProductLocation(Integer orderId) {

		List<OrderGoods> orderGoodsList = orderDao
				.selectOrderGoodsByOrderId(orderId);
		Response response = new Response();

		Integer taskId = 0;
		List<Map> pickTaskList = inventoryDao.selectSaleOrderPickTask(orderId);
		Map<String, Object> mapTemp = new HashMap<String, Object>();
		for (Map map : pickTaskList) {
			taskId = Integer.parseInt(map.get("task_id").toString());
			mapTemp.put(
					map.get("product_id").toString(),
					WorkerUtil.isNullOrEmpty(mapTemp.get(map.get("product_id").toString()))?
							map.get("product_id").toString() + "_"
							+ map.get("pl_id").toString() + "_"
							+ map.get("qty_total").toString():
					mapTemp.get(map.get("product_id").toString()).toString()
							+ "," + map.get("product_id").toString() + "_"
							+ map.get("pl_id").toString() + "_"
							+ map.get("qty_total").toString());
		}
		
		try {
			for (OrderGoods orderGoods : orderGoodsList) {
				Integer orderGoodsId = orderGoods.getOrder_goods_id();
				Integer productId = orderGoods.getProduct_id();
				Integer goodsNumber = orderGoods.getGoods_number();
				String orderGoodsType = orderGoods.getOrder_goods_type();

				if ("PACKBOX".equals(orderGoodsType)) {
					continue;
				}
				String mapStr = "";
				if(mapTemp.containsKey(productId.toString())){
					mapStr = mapTemp.get(productId.toString()).toString();
				}else{
					continue;
				}
				String mapStrArr[] = mapStr.split(",");
				int count = 0;
				for (String temp : mapStrArr) {
					String tempArr[] = temp.split("_");
					if(Integer.parseInt(tempArr[2]) == 0){
						count ++;
					}
					if (Integer.parseInt(tempArr[2]) > goodsNumber) {
						int col = inventoryDao.updateProductLocation(Integer.parseInt(tempArr[1]), goodsNumber);
						ProductLocationDetail productLocationDetail = new ProductLocationDetail();
						productLocationDetail.setPl_id(Integer.parseInt(tempArr[1]));
						productLocationDetail.setChange_quantity(-goodsNumber);
						productLocationDetail.setTask_id(taskId);
						productLocationDetail.setDescription("销售出库扣减虚拟库位库存1");
						productLocationDetail.setCreated_user("system");
						productLocationDetail.setLast_updated_user("system");
						productLocationDetail.setOrder_id(orderId);
						productLocationDetail.setOrder_goods_id(orderGoodsId);
						productLocationDetailDao.insert(productLocationDetail);
						if (col < 1) {
							throw new RuntimeException("ProductLocation数量不足"
									+ tempArr.toString());
						}
						mapTemp.put(productId.toString(), mapTemp.get(productId.toString()).toString().replace(temp, tempArr[0]+"_"+tempArr[1]+"_"+(Integer.parseInt(tempArr[2]) - goodsNumber)));
						break;
					} else {
						if(Integer.parseInt(tempArr[2]) == 0)
							continue;
						int col = inventoryDao.updateProductLocation(
								Integer.parseInt(tempArr[1]),
								Integer.parseInt(tempArr[2]));
						if (col < 1) {
							throw new RuntimeException("ProductLocation数量不足2"
									+ tempArr.toString());
						}
						ProductLocationDetail productLocationDetail = new ProductLocationDetail();
						productLocationDetail.setPl_id(Integer.parseInt(tempArr[1]));
						productLocationDetail.setChange_quantity(-Integer.parseInt(tempArr[2]));
						productLocationDetail.setTask_id(taskId);
						productLocationDetail.setDescription("销售出库扣减虚拟库位库存2");
						productLocationDetail.setCreated_user("system");
						productLocationDetail.setLast_updated_user("system");
						productLocationDetail.setOrder_id(orderId);
						productLocationDetail.setOrder_goods_id(orderGoodsId);
						productLocationDetailDao.insert(productLocationDetail);
						goodsNumber = goodsNumber
								- Integer.parseInt(tempArr[2]);
						mapTemp.put(productId.toString(), mapTemp.get(productId.toString()).toString().replace(temp, tempArr[0]+"_"+tempArr[1]+"_0"));
						if (goodsNumber == 0)
							break;
					}
				}
				if(count != mapStrArr.length){
					response = new Response(Response.OK, Response.SUCCESS);
				}else{
					throw new RuntimeException("ProductLocation中转区数量已为0");
				}
			}
		} catch (NumberFormatException e) {
			logger.error("[DeliverOrderProductLocation] orderId:" + orderId
					+ ",扣减库位库存失败，失败原因：" + e.getMessage());
			throw new RuntimeException("[DeliverOrderProductLocation] orderId:"
					+ orderId + ",扣减库位库存失败，失败原因：" + e.getMessage());
		}
		return response;
	}

	
	// 耗材出库扣减product_location
	public Response deliverPackBoxProductLocation(Integer orderId,
			Integer orderGoodsId, Integer productId,Integer customerId,
			Integer physicalWarehouseId, Integer goodsNumber,String status) {

		ReentrantLock lock = LockUtil.getReentrantLock("physicalWarehouseId_"+ physicalWarehouseId + "_customerId_" + customerId);
		lock.lock();
		
		logger.info("[deliverPackBoxProductLocation start] orderId:" + orderId
				+ ",orderGoodsId:" + orderGoodsId + ",productId:" + productId
				+ ",physicalWarehouseId:" + physicalWarehouseId + ",customerId:"+customerId
				+ ",goodsNumber:" + goodsNumber);
		
		Response response = new Response();

		try {
			boolean flag = false;
			List<ProductLocation> productLocationList = productLocationDao.getFromProductLocation(status, Location.LOCATION_TYPE_PACKBOX_PICK, physicalWarehouseId, customerId, productId);
			if(WorkerUtil.isNullOrEmpty(productLocationList)){
				throw new RuntimeException("productLocationList为空，库位库存为空");
			}
			for(ProductLocation productLocation : productLocationList){
				if(productLocation.getQty_total()>=goodsNumber){
					int col = inventoryDao.updateProductLocationForPackBox(productLocation.getPl_id(), goodsNumber);
					if (col < 1) {
						throw new RuntimeException("ProductLocation数量不足");
					}
					ProductLocationDetail productLocationDetail = new ProductLocationDetail();
					productLocationDetail.setPl_id(productLocation.getPl_id());
					productLocationDetail.setChange_quantity(-goodsNumber);
					productLocationDetail.setTask_id(0);
					productLocationDetail.setDescription("商品上架");
					productLocationDetail.setCreated_user("system");
					productLocationDetail.setLast_updated_user("system");
					productLocationDetail.setOrder_id(orderId);
					productLocationDetail.setOrder_goods_id(orderGoodsId);
					productLocationDetailDao.insert(productLocationDetail);
					flag = true;
					break;
				}
			}
			if(flag)
				response = new Response(Response.OK, Response.SUCCESS);
			else
				throw new RuntimeException("ProductLocation数量不足");
		} catch (Exception e) {
			logger.error("[deliverPackBoxProductLocation] orderId:" + orderId
					+ ",orderGoodsId:" + orderGoodsId + ",productId:"
					+ productId + ",扣减库位库存失败，失败原因：" + e.getMessage());
			throw new RuntimeException("[DeliverOrderProductLocation] orderId:"
					+ orderId + ",orderGoodsId:" + orderGoodsId + ",productId:"
					+ productId + ",扣减库位库存失败，失败原因：" + e.getMessage());
		} finally{
			lock.unlock();
		}
		logger.info("[deliverPackBoxProductLocation end] orderId:" + orderId);
		return response;
	}
	
	private Response createDeliverInventoryTransactionV2(Integer orderId,
			Integer customerId, Integer productId, int thisTurnOutNum,
			List<String> serialNos, Integer orderGoodsId, String actionUser,
			String fromStatusId, Integer warehouseId, BigDecimal unitCost,
			String batchSn, Integer packboxCustomerId,
			Integer packboxWarehouseId, boolean isPackbox) {
		Response response = null;
		return response;
	}

	/**
	 * 获取需要用来销售出库的InventoryItems
	 * 
	 * @param fromWarehouseId
	 * @param customerId
	 * @param serialNos
	 * @param productId
	 * @param fromStatusId
	 * @param thisTurnOutNum
	 * @param batchSn
	 * @param unitCost
	 * @return
	 */
	private List<InventoryItem> getInventoryItemsForDelivery(
			Integer warehouseId, Integer customerId, List<String> serialNos,
			Integer productId, String fromStatusId, Integer thisTurnOutNum,
			BigDecimal unitCost, String batchSn) {

		Map<String, Object> paramsMap = new HashMap<String, Object>();

		paramsMap.put("productId", productId);
		paramsMap.put("fromStatusId", fromStatusId);
		paramsMap.put("fromWarehouseId", warehouseId);
		paramsMap.put("customerId", customerId);
		paramsMap.put("unitCost", unitCost);
		paramsMap.put("batchSn", batchSn);
		paramsMap.put("serialNos", serialNos);
		paramsMap.put("thisTurnOutNum", thisTurnOutNum);

		return inventoryDao.getInventoryItemsForDelivery(paramsMap);
	}

	/**
	 * 保存InventoryItemDetail
	 * 
	 * @param orderId
	 * @param customerId
	 * @param productId
	 * @param orderGoodsId
	 * @param actionUser
	 * @param fromWarehouseId
	 * @param inventoryItem
	 * @param need
	 * @param packboxCustomerId
	 * @param packboxWarehouseId
	 * @return
	 */
	private int saveInventoryItemDetail(Integer orderId, Integer customerId,
			Integer productId, Integer orderGoodsId, String actionUser,
			Integer warehouseId, InventoryItem inventoryItem, int need,
			Integer packboxCustomerId, Integer packboxWarehouseId) {
		InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
		inventoryItemDetail.setOrder_id(orderId);
		inventoryItemDetail.setProduct_id(productId);
		inventoryItemDetail.setCustomer_id(customerId);
		inventoryItemDetail.setWarehouse_id(warehouseId);
		inventoryItemDetail.setInventory_item_id(inventoryItem
				.getInventory_item_id());
		inventoryItemDetail.setOrder_goods_id(orderGoodsId);
		inventoryItemDetail.setChange_quantity(-need);
		inventoryItemDetail.setPackbox_customer_id(packboxCustomerId);
		inventoryItemDetail.setPackbox_warehouse_id(packboxWarehouseId);
		inventoryItemDetail.setCreated_time(new Date());
		inventoryItemDetail.setCreated_user(actionUser);
		inventoryItemDetail.setLast_updated_user(actionUser);
		inventoryItemDetail.setLast_updated_time(new Date());

		int effectRows = inventoryItemDetailDao.insert(inventoryItemDetail);
		if (effectRows <= 0) {
			throw new RuntimeException(
					"inventory_item_detail insert error no effect");
		}

		return effectRows;
	}

	/**
	 * 删减某个inventoryItem的QuantityOnHandTotal数量
	 * 
	 * @param inventoryItemId
	 * @param need
	 *            需要删减的数量
	 * @param actionUser
	 */
	private int subQuantityByInventoryItemId(Integer inventoryItemId, int need,
			String actionUser) {

		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("inventoryItemId", inventoryItemId);
		paramsMap.put("need", need);
		paramsMap.put("actionUser", actionUser);
		paramsMap.put("lastUpdatedTime", new Date());

		int effectRows = inventoryDao.subQuantityByInventoryItemId(paramsMap);
		if (effectRows <= 0) {
			throw new RuntimeException("inventory_item update error no effect");
		}

		return effectRows;
	}

	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.04
	 * 
	 * @Description 查找退货订单相对应的销售订单出库的商品串号
	 * 
	 * @param orderId
	 *            an int: 退货订单ID
	 * @param productId
	 *            an int: 商品ID
	 * @return outSerialNumbers the List<String>: 商品串号列表
	 * 
	 * */
	@Override
	public List<String> getOutSerialNumbers(int orderId, int productId) {
		// 初始化
		List<String> outSerialNumbers = new ArrayList<String>();

		// 查询退货订单
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(Integer
				.valueOf(orderId));

		// 查询对应的销售订单出库的inventory_item_detail记录
		List<InventoryItemDetail> inventoryItemDetailList = inventoryItemDetailDao
				.selectByOrderId(orderInfo.getParent_order_id());

		// 查询对应的销售订单出库的inventory_item记录
		if (!WorkerUtil.isNullOrEmpty(inventoryItemDetailList)) {
			for (InventoryItemDetail inventoryItemDetail : inventoryItemDetailList) {
				InventoryItem inventoryItem = inventoryItemDao
						.selectByPrimaryKey(inventoryItemDetail
								.getInventory_item_id());
				if (inventoryItem.getProduct_id().intValue() == productId) {
					outSerialNumbers.add(inventoryItem.getSerial_number());
				}
			}
		}

		return outSerialNumbers;
	}

	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.04
	 * 
	 * @Description 判断串号商品是否已经在库存中
	 * 
	 * @param serialNumber
	 *            a String: 串号
	 * @return isInStock a boolean: 是否在库
	 * 
	 * */
	@Override
	public boolean isSerialGoodInStock(String serialNumber) {
		// 初始化
		int countQuantityGreaterThanZero = 0;

		countQuantityGreaterThanZero = inventoryItemDao
				.countBySerialNumber(serialNumber);

		if (countQuantityGreaterThanZero > 0) {
			return true;
		}
		return false;
	}

	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.06
	 * 
	 * @Description 退货入库
	 * 
	 * @param orderId
	 *            an Integer: 退货订单订单ID
	 * @param returnProductAccDetails
	 *            a List<ReturnProductAccDetail>: 退货商品清单
	 * @param actionUser
	 *            a String: 操作人
	 * @return response a Response: 反馈信息
	 * */
	@Override
	public Response returnAcceptInventory(Integer orderId,
			List<ReturnProductAccDetail> returnProductAccDetails,
			String actionUser) {
		logger.info("（退货）入库开始, orderId: " + orderId + ", actionUser: "
				+ actionUser);

		// 初始化
		Response response = new Response(Response.OK, "退货入库成功!");
		
		try {
			// 退货商品列表不能为空
			Assert.isTrue(!returnProductAccDetails.isEmpty(), "退货商品列表为空!");

			// 查询订单信息，同时锁定订单
			OrderInfo orderInfo = orderDao
					.selectOrderInfoByIdForUpdate(orderId);
			Assert.isTrue(OrderInfo.ORDER_STATUS_ACCEPT.equals(orderInfo
					.getOrder_status()), "该退货订单不是待入库状态!");

			// 查询所有可用的parentInventoryItem
			List<InventoryItem> parentInventoryItemList = findParentInventoryItem(orderId);

			// 若parentInventoryItemList为空，报错
			Assert.isTrue(!parentInventoryItemList.isEmpty()
					&& (parentInventoryItemList.size() != 0), "父订单已全部退货入库!");
			
			// 遍历退货商品数据
			for (ReturnProductAccDetail returnProductAccDetail : returnProductAccDetails) {
				int num = returnProductAccDetail.getNum(); // 本商品需入库总数量
				int inNum = 0; // 每次需入库数量
				String location_type =null;
				Integer locationId = null;
				if("NORMAL".equals(returnProductAccDetail.getStatus())){
					location_type = "RETURN_NORMAL_LOCATION";
				}else{
					location_type = "RETURN_DEFECTIVE_LOCATION";
				}
				Location location =locationDao.selectLocationByLocationType(orderInfo.getPhysical_warehouse_id(), location_type);
				//创建虚拟库位
				if("RETURN_NORMAL_LOCATION".equals(location_type) && WorkerUtil.isNullOrEmpty(location)){
					response.setMsg("库位暂存区中不存在良品退货区库位,请维护");
					response.setCode(Response.ERROR);
					return response;
				
				}else if("RETURN_DEFECTIVE_LOCATION".equals(location_type) && WorkerUtil.isNullOrEmpty(location))
				{
					response.setMsg("库位暂存区中不存在不良品退货区库位，请维护");
					response.setCode(Response.ERROR);
					return response;
				}else if(!WorkerUtil.isNullOrEmpty(locationDao.selectLocationByLocationType(orderInfo.getPhysical_warehouse_id(), location_type))){
					locationId = locationDao.selectLocationByLocationType(orderInfo.getPhysical_warehouse_id(), location_type).getLocation_id();
				}
				for (InventoryItem inventoryItem : parentInventoryItemList) {
					if (("Y".equals(returnProductAccDetail.getIsSerial()) && returnProductAccDetail.getSerialNumber().equals(
							inventoryItem.getSerial_number()))|| ("N".equals(returnProductAccDetail.getIsSerial()))
							&& returnProductAccDetail.getProductId() == inventoryItem.getProduct_id().intValue()) {
						int qoh = inventoryItem.getQuantity().intValue();
						if (qoh <= 0) {
							continue;
						} else if (num <= 0) {
							break;
						} else if (num > qoh) {
							inNum = qoh;
							num -= qoh;
							inventoryItem.setQuantity(Integer.valueOf(0));
						} else {
							inNum = num;
							inventoryItem.setQuantity(Integer
									.valueOf(inventoryItem.getQuantity()
											.intValue() - num));
							num = 0;
						}
						// 创建单个入库作业
						response = this
								.createReturnAcceptInventoryTransactionSingle(
										orderId,
										inventoryItem.getProduct_id(),
										returnProductAccDetail
												.getSerialNumber(),
										returnProductAccDetail.getStatus(),
										inventoryItem.getProvider_code(),
										Integer.valueOf(inNum),
										inventoryItem.getCustomer_id(),
										inventoryItem
												.getInventory_item_acct_type_id(),
										orderInfo.getWarehouse_id(), orderInfo
												.getPhysical_warehouse_id(),
										inventoryItem.getInventory_item_id(),
										inventoryItem
												.getRoot_inventory_item_id(),
										inventoryItem.getUnit_cost(),
										inventoryItem.getValidity(),
										inventoryItem.getBatch_sn(),
										inventoryItem.getCurrency(), Integer
												.valueOf(returnProductAccDetail
														.getOrderGoodsId()),
										actionUser);
						Assert.isTrue(
								Response.OK.equals(response.getCode()),
								response.getMsg()
										+ ", orderId: "
										+ orderId
										+ ", productId: "
										+ returnProductAccDetail.getProductId()
										+ ", orderGoodsId: "
										+ returnProductAccDetail
												.getOrderGoodsId()
										+ ", serialNumber: "
										+ returnProductAccDetail
												.getSerialNumber()
										+ ", status: "
										+ returnProductAccDetail.getStatus());
						
						ProductLocation productLocation = new ProductLocation();
						Map<String, Object> paramsForPlMap = new HashMap<String, Object>();
						paramsForPlMap.put("productId", returnProductAccDetail.getProductId());
						paramsForPlMap.put("status", returnProductAccDetail.getStatus());
						paramsForPlMap.put("validity", inventoryItem.getValidity());
						//paramsForPlMap.put("batchSn", batchSn);
						paramsForPlMap.put("serialNumber", inventoryItem.getSerial_number());
						paramsForPlMap.put("locationId", locationId);
						productLocation = inventoryDao.selectProductLocation(paramsForPlMap);
						if(WorkerUtil.isNullOrEmpty(productLocation)){
							//Product product = productDao.selectByPrimaryKey(returnProductAccDetail.getProductId());
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						    productLocation = new ProductLocation();
							productLocation.setLocation_id(locationId);
							productLocation.setProduct_id(returnProductAccDetail.getProductId());
							productLocation.setQty_total(inNum);
							productLocation.setQty_reserved(0);
							productLocation.setQty_available(inNum);
							productLocation.setProduct_location_status("NORMAL");
							productLocation.setQty_freeze(0);
							this.setValidityStatus(returnProductAccDetail.getProductId(), productLocation, sdf.format(inventoryItem.getValidity()));
							productLocation.setStatus(returnProductAccDetail.getStatus());
							productLocation.setValidity(WorkerUtil.isNullOrEmpty(inventoryItem.getValidity())?"1970-01-01 00:00:00":sdf.format(inventoryItem.getValidity()));
							if(WorkerUtil.isNullOrEmpty(productLocation.getValidity())){
								productLocation.setValidity("1970-01-01 00:00:00");
							}
							productLocation.setSerial_number(WorkerUtil.isNullOrEmpty(inventoryItem.getSerial_number())?"":inventoryItem.getSerial_number().toString());
							productLocation.setCreated_user(actionUser);
							productLocation.setLast_updated_user(actionUser);
							productLocationDao.insert(productLocation);
						}else{
							productLocationDao.updateProductLocation(inNum,
									productLocation.getPl_id());
						}
						ProductLocationDetail productLocationDetail = new ProductLocationDetail();
						productLocationDetail.setPl_id(productLocation.getPl_id());
						productLocationDetail.setChange_quantity(inNum);
						productLocationDetail.setTask_id(0);
						productLocationDetail.setDescription("订单退货上架");
						productLocationDetail.setCreated_user(actionUser);
						productLocationDetail.setLast_updated_user(actionUser);
						productLocationDetail.setOrder_id(orderId);
						productLocationDetail.setOrder_goods_id(returnProductAccDetail.getOrderGoodsId());
						productLocationDetailDao.insert(productLocationDetail);
					}
					
				}
				if("Y".equals(location.getIs_empty())){
					locationDao.updateLocationIsEmpty1(location.getLocation_id());
				}
				// 判断是否已经全部入库
				Assert.isTrue(num == 0, "商品(productId:"
						+ returnProductAccDetail.getProductId() + ")还有数量:"
						+ num + "无法入库!");
			}
		} catch (Exception e) {
			logger.error("操作退货入库时发生异常，异常信息：" + e.getMessage()
					+ ", 退货订单orderId: " + orderId.toString(), e);
			throw new RuntimeException("操作退货入库时发生异常，异常信息：" + e.getMessage()
					+ " 退货订单orderId: " + orderId.toString());
		}

		// 更新订单状态
		int effectRows = orderBiz.updateOrderInfoStatus(orderId,
				OrderInfo.ORDER_STATUS_FULFILLED, actionUser);
		Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);

		// 记录订单操作日志
		UserActionOrder userActionOrder = new UserActionOrder();
		userActionOrder.setAction_note("订单完成退货入库");
		userActionOrder.setAction_type("ACCEPT");
		userActionOrder.setCreated_time(new Date());
		userActionOrder.setCreated_user(actionUser);
		userActionOrder.setOrder_id(orderId);
		userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_FULFILLED);
		userActionOrderDao.insert(userActionOrder);

		logger.info("（退货）入库结束,orderId: " + orderId + ", actionUser: "
				+ actionUser);

		return response;
	}

	/**
	 * 查找可用的父InventoryItem列表
	 * 
	 * @param orderId
	 *            退货订单ID
	 * @return parentInventoryItemList 父订单出库库存记录列表
	 * */
	private List<InventoryItem> findParentInventoryItem(Integer orderId)
			throws Exception {
		List<InventoryItem> parentInventoryItemList = new ArrayList<InventoryItem>();

		// 退货订单
		OrderInfo returnInOrderInfo = orderInfoDao.selectByPrimaryKey(orderId);
		// 原始销售订单
		OrderInfo originOutOrderInfo = orderInfoDao
				.selectByPrimaryKey(returnInOrderInfo.getParent_order_id());

		// ***查找原始销售订单出库的InventoryItem记录***
		List<InventoryItemDetail> originOutInventoryItemDetailList = inventoryItemDetailDao.selectByReturnOrderId(originOutOrderInfo.getOrder_id());
				
		List<InventoryItemDetail> originOutInventoryItemDetailList1 = new ArrayList<InventoryItemDetail>();

		if (originOutInventoryItemDetailList.isEmpty()) {
			logger.info("Error! originInventoryItemDetailList is empty!");
			return null;
		}

		for (InventoryItemDetail originOutInventoryItemDetail : originOutInventoryItemDetailList) { 
			InventoryItem inventoryItem = inventoryItemDao.selectByPrimaryKey(originOutInventoryItemDetail.getInventory_item_id());
			InventoryItem inventoryItem2 = new InventoryItem();
			inventoryItem2.setBatch_sn(inventoryItem.getBatch_sn());
			inventoryItem2.setCurrency(inventoryItem.getCurrency());
			inventoryItem2.setCustomer_id(inventoryItem.getCustomer_id());
			inventoryItem2.setInventory_item_acct_type_id(inventoryItem.getInventory_item_acct_type_id());
			inventoryItem2.setInventory_item_id(inventoryItem.getInventory_item_id());
			inventoryItem2.setParent_inventory_item_id(inventoryItem2.getParent_inventory_item_id());
			inventoryItem2.setPhysical_warehouse_id(inventoryItem.getPhysical_warehouse_id());
			inventoryItem2.setProvider_code(inventoryItem.getProvider_code());
			inventoryItem2.setProduct_id(inventoryItem.getProduct_id());
			inventoryItem2.setRoot_inventory_item_id(inventoryItem.getRoot_inventory_item_id());
			inventoryItem2.setSerial_number(inventoryItem.getSerial_number());
			inventoryItem2.setStatus(inventoryItem.getStatus());
			inventoryItem2.setUnit_cost(inventoryItem.getUnit_cost());
			inventoryItem2.setValidity(inventoryItem.getValidity());
			inventoryItem2.setWarehouse_id(inventoryItem2.getWarehouse_id());
			inventoryItem2.setQuantity(Integer.valueOf(-1* originOutInventoryItemDetail.getChange_quantity()));
			inventoryItem2.setCreated_time(inventoryItem.getCreated_time());
			inventoryItem2.setCreated_user(inventoryItem.getCreated_user());
			inventoryItem2.setLast_updated_time(inventoryItem.getLast_updated_time());
			inventoryItem2.setLast_updated_user(inventoryItem.getLast_updated_user());
			originOutInventoryItemDetail.setInventoryItem(inventoryItem2);
			originOutInventoryItemDetailList1.add(originOutInventoryItemDetail);
		}

		// ***查找所有相关联的退货订单入库的InventoryItem记录***
		// 获取所有相关联的退货订单的ID
		List<OrderInfo> returnInOrderInfoList = orderInfoDao.selectReturnOrderListByParentOrderId(originOutOrderInfo.getOrder_id());
		
		if (returnInOrderInfoList.isEmpty()) {
			logger.info("Error! returnInOrderInfoList is empty!");
			return null;
		}

		List<Integer> returnInOrderIdList = new ArrayList<Integer>();
		for (OrderInfo returnInOrderInfo1 : returnInOrderInfoList) {
			returnInOrderIdList.add(returnInOrderInfo1.getOrder_id());
		}

		// 获取所有相关联的退货订单入库的InventoryItem记录
		List<InventoryItemDetail> returnInInventoryItemDetailList = inventoryItemDetailDao.selectByOrderIdList(returnInOrderIdList);

		if (!returnInInventoryItemDetailList.isEmpty()) {
			// 扣减已入库的数量
			for (InventoryItemDetail returnInInventoryItemDetail : returnInInventoryItemDetailList) {
				InventoryItem inventoryItem = inventoryItemDao.selectByPrimaryKey(returnInInventoryItemDetail.getInventory_item_id());
				for (InventoryItemDetail originOutInventoryItemDetail : originOutInventoryItemDetailList1) {
					if (originOutInventoryItemDetail.getInventory_item_id().equals(inventoryItem.getParent_inventory_item_id())) {
						originOutInventoryItemDetail.getInventoryItem ().setQuantity(
						Integer.valueOf (originOutInventoryItemDetail.getInventoryItem ().getQuantity() - returnInInventoryItemDetail.getChange_quantity ()
								)
							);
					}
				}
			}
		}

		// ***填充parentInventoryItemList***
		for (InventoryItemDetail originOutInventoryItemDetail : originOutInventoryItemDetailList1) {
			if (originOutInventoryItemDetail.getInventoryItem().getQuantity().compareTo(Integer.valueOf(0)) > 0) {
				parentInventoryItemList.add(originOutInventoryItemDetail.getInventoryItem());
			} else if (originOutInventoryItemDetail.getInventoryItem().getQuantity().compareTo(Integer.valueOf(0)) < 0) {
				throw new RuntimeException("findParentInventoryItem 数据异常！");
			}
		}

		return parentInventoryItemList;
	}

	/**
	 * 创建一个退货入库作业
	 * 
	 * @param orderId
	 *            订单ID
	 * @param productId
	 *            产品ID
	 * @param serialNumber
	 *            串号
	 * @param status
	 *            全新/二手状态
	 * @param providerCode
	 *            供应商代码
	 * @param thisTurnInNum
	 *            本次需要入库的数量
	 * @param customerId
	 *            货主ID
	 * @param inventoryItemAcctTypeId
	 *            库存类型B2C,DX等
	 * @param warehouseId
	 *            仓库ID
	 * @param physicalWarehouseId
	 *            物理仓ID
	 * @param parentInventoryItemId
	 *            父InventoryItemID
	 * @param rootInventoryItemId
	 *            根InventoryItemID
	 * @param unitCost
	 *            采购单价
	 * @param validity
	 *            生产日期
	 * @param batchSn
	 *            批次号
	 * @param currency
	 *            币种
	 * @param orderGoodsId
	 *            订单商品ID
	 * @param actionUser
	 *            操作人
	 * @return response 反馈结果
	 */
	private Response createReturnAcceptInventoryTransactionSingle(
			Integer orderId, Integer productId, String serialNumber,
			String status, String providerCode, Integer thisTurnInNum,
			Integer customerId, String inventoryItemAcctTypeId,
			Integer warehouseId, Integer physicalWarehouseId,
			Integer parentInventoryItemId, Integer rootInventoryItemId,
			BigDecimal unitCost, Date validity, String batchSn,
			String currency, Integer orderGoodsId, String actionUser) {
		// 初始化
		Response response = new Response(Response.OK, "入库成功");
		Date nowTime = new Date();

		// insert inventory_item表记录
		InventoryItem inventoryItem = new InventoryItem();
		inventoryItem.setProduct_id(productId);
		inventoryItem.setSerial_number(serialNumber);
		inventoryItem.setStatus(status);
		inventoryItem.setProvider_code(providerCode);
		inventoryItem.setQuantity(thisTurnInNum);
		inventoryItem.setCustomer_id(customerId);
		inventoryItem.setInventory_item_acct_type_id(inventoryItemAcctTypeId);
		inventoryItem.setWarehouse_id(warehouseId);
		inventoryItem.setPhysical_warehouse_id(physicalWarehouseId);
		inventoryItem.setParent_inventory_item_id(parentInventoryItemId);
		inventoryItem.setRoot_inventory_item_id(rootInventoryItemId);
		inventoryItem.setUnit_cost(unitCost);
		inventoryItem.setValidity(validity);
		inventoryItem.setBatch_sn(batchSn);
		inventoryItem.setCurrency(currency);
		inventoryItem.setCreated_time(nowTime);
		inventoryItem.setCreated_user(actionUser);
		inventoryItem.setLast_updated_time(nowTime);
		inventoryItem.setLast_updated_user(actionUser);

		inventoryItemDao.insert(inventoryItem);
		if (inventoryItem.getInventory_item_id().intValue() <= 0) {
			response = ResponseFactory
					.createErrorResponse("INSERT InventoryItem FAILED!");
			return response;
		}

		// insert inventory_item_detail表记录
		InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
		inventoryItemDetail.setChange_quantity(thisTurnInNum);
		inventoryItemDetail.setCustomer_id(customerId);
		inventoryItemDetail.setInventory_item_id(inventoryItem
				.getInventory_item_id());
		inventoryItemDetail.setOrder_goods_id(orderGoodsId);
		inventoryItemDetail.setOrder_id(orderId);
		inventoryItemDetail.setPackbox_customer_id(Integer.valueOf(0));
		inventoryItemDetail.setPackbox_warehouse_id(Integer.valueOf(0));
		inventoryItemDetail.setProduct_id(productId);
		inventoryItemDetail.setWarehouse_id(warehouseId);
		inventoryItemDetail.setCreated_time(nowTime);
		inventoryItemDetail.setCreated_user(actionUser);
		inventoryItemDetail.setLast_updated_time(nowTime);
		inventoryItemDetail.setLast_updated_user(actionUser);

		inventoryItemDetailDao.insert(inventoryItemDetail);
		if (inventoryItemDetail.getInventory_item_detail_id().intValue() <= 0) {
			response = ResponseFactory
					.createErrorResponse("INSERT InventoryItemDetail FAILED!");
			return response;
		}

		return response;
	}
	
	private Map<String,Object> createReturnAcceptInventoryTransactionSingle1(
			Integer orderId, Integer productId, String serialNumber,
			String status, String providerCode, Integer thisTurnInNum,
			Integer customerId, String inventoryItemAcctTypeId,
			Integer warehouseId, Integer physicalWarehouseId,
			Integer parentInventoryItemId, Integer rootInventoryItemId,
			BigDecimal unitCost, Date validity, String batchSn,
			String currency, Integer orderGoodsId, String actionUser) {
		// 初始化
		Map<String,Object> resultMap = new HashMap<String, Object>();
		Date nowTime = new Date();

		// insert inventory_item表记录
		InventoryItem inventoryItem = new InventoryItem();
		inventoryItem.setProduct_id(productId);
		inventoryItem.setSerial_number(serialNumber);
		inventoryItem.setStatus(status);
		inventoryItem.setProvider_code(providerCode);
		inventoryItem.setQuantity(thisTurnInNum);
		inventoryItem.setCustomer_id(customerId);
		inventoryItem.setInventory_item_acct_type_id(inventoryItemAcctTypeId);
		inventoryItem.setWarehouse_id(warehouseId);
		inventoryItem.setPhysical_warehouse_id(physicalWarehouseId);
		inventoryItem.setParent_inventory_item_id(parentInventoryItemId);
		inventoryItem.setRoot_inventory_item_id(rootInventoryItemId);
		inventoryItem.setUnit_cost(unitCost);
		inventoryItem.setValidity(validity);
		inventoryItem.setBatch_sn(batchSn);
		inventoryItem.setCurrency(currency);
		inventoryItem.setCreated_time(nowTime);
		inventoryItem.setCreated_user(actionUser);
		inventoryItem.setLast_updated_time(nowTime);
		inventoryItem.setLast_updated_user(actionUser);

		inventoryItemDao.insert(inventoryItem);
		if (inventoryItem.getInventory_item_id().intValue() <= 0) {
			resultMap.put("result", "failure");
			resultMap.put("success", false);
			return resultMap;
		}

		// insert inventory_item_detail表记录
		InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
		inventoryItemDetail.setChange_quantity(thisTurnInNum);
		inventoryItemDetail.setCustomer_id(customerId);
		inventoryItemDetail.setInventory_item_id(inventoryItem
				.getInventory_item_id());
		inventoryItemDetail.setOrder_goods_id(orderGoodsId);
		inventoryItemDetail.setOrder_id(orderId);
		inventoryItemDetail.setPackbox_customer_id(Integer.valueOf(0));
		inventoryItemDetail.setPackbox_warehouse_id(Integer.valueOf(0));
		inventoryItemDetail.setProduct_id(productId);
		inventoryItemDetail.setWarehouse_id(warehouseId);
		inventoryItemDetail.setCreated_time(nowTime);
		inventoryItemDetail.setCreated_user(actionUser);
		inventoryItemDetail.setLast_updated_time(nowTime);
		inventoryItemDetail.setLast_updated_user(actionUser);

		inventoryItemDetailDao.insert(inventoryItemDetail);
		if (inventoryItemDetail.getInventory_item_detail_id().intValue() <= 0) {
			resultMap.put("result", "failure");
			resultMap.put("success", false);
			return resultMap;
		}
		resultMap.put("result", "success");
		resultMap.put("success", true);
		return resultMap;
	}

	/**************************************************************
	 * 采购订单入库
	 * 
	 * @author hzhang1 2016-02-19
	 * @param customerId
	 *            , orderId, orderGoodsId, warehouseId, tagCode,
	 *            locationBarcode, actionUser
	 *************************************************************/
	@Override
	public Map createPurchaseAccept(Integer customerId, Integer orderId,
			Integer orderGoodsId, Integer warehouseId,
			Integer physicalWarehouseId, String tagCode,
			String locationBarcode, String actionUser) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 1.根据orderId获得OrderInfo，并且借此锁定该订单
		OrderInfo orderInfo = orderDao.getOderInfoByIdForUpdate(Integer
				.valueOf(orderId));
		
		String order_status = orderInfo.getOrder_status();
		if ("FULFILLED".equals(order_status)) {
			resultMap.put("result", "failure");
			resultMap.put("success", false);
			resultMap.put("note", "订单状态有误");
			return resultMap;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", orderId);
		map.put("orderGoodsId", orderGoodsId);
		map.put("tagCode", tagCode);
		Map<String, Object> purchaseOrderMap = orderDao.selectOrderInfoByGoods(map);
		
		if (!WorkerUtil.isNullOrEmpty(purchaseOrderMap
				.get("location_kw_barcode"))){
			resultMap.put("result", "failure");
			resultMap.put("success", false);
			resultMap.put("note", "该标签已上架");
			return resultMap;
		}
		
		Integer productId = Integer.parseInt(purchaseOrderMap.get("product_id")
				.toString());
		Integer quantity = Integer.parseInt(purchaseOrderMap.get("quantity")
				.toString());
		String isSerial = purchaseOrderMap.get("is_serial").toString();
		String serialNos = "";
		if (WorkerUtil.isNullOrEmpty(purchaseOrderMap.get("serial_no"))) {
			serialNos = "";
		} else {
			serialNos = purchaseOrderMap.get("serial_no").toString();
		}

		String toStatusId = purchaseOrderMap.get("status_id").toString();

		BigDecimal unitcost = new BigDecimal(purchaseOrderMap
				.get("goods_price").toString());

		String goodsBarcode = purchaseOrderMap.get("goods_barcode").toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			if (!WorkerUtil.isNullOrEmpty(purchaseOrderMap.get("validity"))) {
				date = sdf.parse(purchaseOrderMap.get("validity").toString());
			}
		} catch (ParseException e) {
			logger.error("createPurchaseAccept error:", e);
			logger.error(e.getMessage());
		}
		Date validity = date;

		String batchSn = purchaseOrderMap.get("batch_sn").toString();
		String providerId = purchaseOrderMap.get("provider_code").toString();
		String currency = purchaseOrderMap.get("currency").toString();
		String validity_ = WorkerUtil.isNullOrEmpty(purchaseOrderMap.get("validity"))?"":purchaseOrderMap.get("validity").toString();
		String inventoryItemAcctTypeId = purchaseOrderMap.get(
				"provider_order_type").toString();

		Map<String, Object> map_ = new HashMap<String, Object>();
		map_.put("locationBarcode", locationBarcode);
		map_.put("physicalWarehouseId", physicalWarehouseId);
		//map_.put("locationType", Location.LOCATION_TYPE_STOCK);
		if("PACKBOX".equals(purchaseOrderMap.get("product_type").toString())){
			map_.put("locationType", Location.LOCATION_TYPE_PACKBOX);
		}
		if("DEFECTIVE".equals(purchaseOrderMap.get("status_id").toString())){
			map_.put("locationType", Location.LOCATION_TYPE_DEFECTIVE);
		}
		Location location = inventoryDao.selectLocationByCode(map_);
		if (WorkerUtil.isNullOrEmpty(location)) {
			resultMap.put("success", false);
			resultMap.put("result", "failure");
			resultMap.put("note", "库位不存在或目标库位类型有误");
			return resultMap;
		}
		if (!Location.LOCATION_TYPE_STOCK.equals(location.getLocation_type())
				&& !Location.LOCATION_TYPE_BOX_PICK.equals(location.getLocation_type())
				&& !Location.LOCATION_TYPE_PIECE_PICK.equals(location.getLocation_type())
				&& !Location.LOCATION_TYPE_DEFECTIVE.equals(location.getLocation_type())
				&& !Location.LOCATION_TYPE_PACKBOX.equals(location.getLocation_type())){
			resultMap.put("success", false);
			resultMap.put("result", "failure");
			resultMap.put("note", "上架库位类型有误");
			return resultMap;
		}
		
		map_ = new HashMap<String, Object>();
		map_.put("locationId", location.getLocation_id());
		List<Map> isMixMapList = replenishmentDao.selectLocationIsCanMix(map_);
		if (WorkerUtil.isNullOrEmpty(isMixMapList)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("success", Boolean.FALSE);
			resultMap.put("note", "上架库位有误");
			return resultMap;
		} else {
			boolean flag = true;
			for(Map isMixMap :isMixMapList){
				String product_id2 = WorkerUtil.isNullOrEmpty(isMixMap.get("product_id")) ? "" : isMixMap.get("product_id").toString();
				String validity2 = WorkerUtil.isNullOrEmpty(isMixMap.get("validity")) ? "" : isMixMap.get("validity").toString();
				String status2 = WorkerUtil.isNullOrEmpty(isMixMap.get("status")) ? "" : isMixMap.get("status").toString();
				String batchSn2 = WorkerUtil.isNullOrEmpty(isMixMap.get("batch_sn")) ? "" : isMixMap.get("batch_sn").toString();
				Integer warehouseId2 = WorkerUtil.isNullOrEmpty(isMixMap.get("warehouse_id")) ? null : Integer.parseInt(isMixMap.get("warehouse_id").toString());
				String qty_total = WorkerUtil.isNullOrEmpty(isMixMap.get("qty_total"))?"":isMixMap.get("qty_total").toString();
				if (!"".equals(product_id2) && !"0".equals(qty_total)) {
					String can_mix_product = isMixMap.get("can_mix_product").toString();
					String can_mix_batch = isMixMap.get("can_mix_batch").toString();
					if ("0".equals(can_mix_product) && !product_id2.equals(productId.toString())) {
						resultMap.put("result", Response.FAILURE);
						resultMap.put("success", Boolean.FALSE);
						resultMap.put("note", "上架库位不允许混放商品");
						flag = false;
						break;
					} 
					if ("0".equals(can_mix_batch) && productId.toString().equals(product_id2) &&
							 (!validity_.equals(validity2) || !status2.equals(toStatusId) || !batchSn2.equals(batchSn))) {
						resultMap.put("result", Response.FAILURE);
						resultMap.put("success", Boolean.FALSE);
						resultMap.put("note", "上架库位不允许混放批次");
						flag = false;
						break;
					} 
					if(product_id2.equals(productId.toString()) && !warehouseId2.equals(orderInfo.getWarehouse_id())){
						resultMap.put("result", Response.FAILURE);
						resultMap.put("success", Boolean.FALSE);
						resultMap.put("note", locationBarcode + "库位商品渠道不同不允许存放！请重新输入！");
						flag = false;
						break;
					}
						
				}
			}
			
			if(Boolean.FALSE.equals(flag)){
				return resultMap;
			}
		}


		try {
			// 2.调用入库方法
			String serialNoArr[] = serialNos.split(",");
			for (int t = 0; t < serialNoArr.length; t++) {
				serialNos = serialNoArr[t];
				if (!WorkerUtil.isNullOrEmpty(serialNos)) {
					quantity = 1;
				}
				createAcceptInventoryTransactionSingleReal(productId, quantity,
						isSerial, serialNos, orderId, toStatusId, actionUser,
						unitcost, orderGoodsId, warehouseId,
						physicalWarehouseId, tagCode, locationBarcode,
						goodsBarcode, validity, batchSn, providerId,
						customerId, currency, inventoryItemAcctTypeId);
			}

			// 3.更新库位信息
			updateInventortLocation(customerId, physicalWarehouseId, tagCode,
					locationBarcode, orderId, orderGoodsId, productId,
					quantity, actionUser);

			if(orderInfo.getOrder_status().equals(OrderInfo.ORDER_STATUS_IN_PROCESS)){
				orderBiz.updateOrderInfoStatus(orderId, OrderInfo.ORDER_STATUS_ON_SHIP,actionUser);
			}
			
			if(orderInfo.getOrder_status().equals(OrderInfo.ORDER_STATUS_ON_SHIP)){
				orderBiz.updateOrderInfoStatusV2(orderId, OrderInfo.ORDER_STATUS_ON_SHIP,
						actionUser);
			}
			
			// 4.更新订单状态,只有该采购订单里面所有商品都上架才会完结采购单
			if (WorkerUtil.isNullOrEmpty(orderDao.selectPurchaseIsOver(orderId))) {
				orderBiz.updateOrderInfoStatusV2(orderId, "FULFILLED",
						actionUser);
			}

			// 5.插入Product_location
			Map<String, Object> paramsForPlMap = new HashMap<String, Object>();
			paramsForPlMap.put("productId", productId);
			paramsForPlMap.put("status", toStatusId);
			paramsForPlMap.put("validity", validity);
			paramsForPlMap.put("serialNumber", serialNos);
			paramsForPlMap.put("batchSn", batchSn);
			paramsForPlMap.put("locationId", location.getLocation_id());
			paramsForPlMap.put("warehouseId", orderInfo.getWarehouse_id());
			ProductLocation productLocation = inventoryDao
					.selectProductLocation(paramsForPlMap);

			if (WorkerUtil.isNullOrEmpty(productLocation)) {
				productLocation = new ProductLocation();
				productLocation.setProduct_id(productId);
				productLocation.setLocation_id(location.getLocation_id());
				productLocation.setQty_total(quantity);
				productLocation.setQty_available(quantity);
				productLocation.setProduct_location_status("NORMAL");
				productLocation.setQty_freeze(0);
				productLocation.setStatus(toStatusId);
				productLocation.setBatch_sn(batchSn);
				productLocation.setWarehouse_id(orderInfo.getWarehouse_id());
				if (!WorkerUtil.isNullOrEmpty(purchaseOrderMap.get("validity"))) {
					productLocation.setValidity(purchaseOrderMap
							.get("validity").toString());
				}else{
					productLocation.setValidity("1970-01-01");
				}
				productLocation.setSerial_number(serialNos);
				productLocation.setCreated_user(actionUser);
				productLocation.setLast_updated_user(actionUser);
				
				Product product = productDao.selectByPrimaryKey(productId);
				String validityStatus = "NORMAL";
				if("Y".equals(product.getIs_maintain_warranty())){
					validityStatus = productLocation.checkValidityStatus(purchaseOrderMap
							.get("validity").toString(), product.getValidity(), product.getValidity_unit(), product.getWarranty_warning_days(), product.getWarranty_unsalable_days());
				}
				productLocation.setValidity_status(validityStatus);
				productLocationDao.insert(productLocation);
			} else {
				if(!"NORMAL".equals(productLocation.getProduct_location_status())){
					throw new RuntimeException("上架失败,原因：该库位库存不可用！");
				}
				productLocationDao.updateProductLocationAdd(quantity,
						productLocation.getPl_id());
			}
			ProductLocationDetail productLocationDetail = new ProductLocationDetail();
			productLocationDetail.setPl_id(productLocation.getPl_id());
			productLocationDetail.setChange_quantity(quantity);
			productLocationDetail.setTask_id(Integer.parseInt(purchaseOrderMap
					.get("task_id").toString()));
			productLocationDetail.setDescription("采购上架");
			productLocationDetail.setOrder_id(orderId);
			productLocationDetail.setOrder_goods_id(orderGoodsId);
			productLocationDetail.setCreated_user(actionUser);
			productLocationDetail.setLast_updated_user(actionUser);
			productLocationDetailDao.insert(productLocationDetail);

			Map<String, Object> paramsForUpdateMap = new HashMap<String, Object>();
			paramsForUpdateMap.put("taskStatus", Task.TASK_STATUS_FULFILLED);
			paramsForUpdateMap.put("actionUser", actionUser);
			paramsForUpdateMap.put("taskId", purchaseOrderMap.get("task_id")
					.toString());
			taskDao.updateTaskByTaskId(paramsForUpdateMap);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.error("上架失败", e);
			throw new RuntimeException("上架失败,原因" + e.getMessage());
		}
		resultMap.put("result", "success");
		resultMap.put("success", true);
		resultMap.put("note", "success");
		return resultMap;
	}

	public boolean selectPurchaseIsOver(Integer order_id) {
		List<OrderGoods> orderGoodsList = orderDao
				.selectOrderGoodsByOrderId(order_id);
		List<Map> yanhuoGoodsList = orderDao
				.selectPurchaseIsOver_yanhuo(order_id);
		return orderGoodsList.size() == yanhuoGoodsList.size() ? true : false;
	}

	/**
	 * 更新库位信息（将托盘移到库位）
	 * 
	 * @author hzhang1
	 * @date 2016-03-14
	 * @param tagCode
	 *            ,locationBarcode,orderId,orderGoodsId,productId,quantity,
	 *            actionUser
	 */
	public void updateInventortLocation(Integer customerId,
			Integer physicalWarehouseId, String tagCode,
			String locationBarcode, Integer orderId, Integer orderGoodsId,
			Integer productId, Integer quantity, String actionUser) {

		Map<String, Object> map_ = new HashMap<String, Object>();
		map_.put("locationBarcode", locationBarcode);
		map_.put("customerId", customerId);
		map_.put("physicalWarehouseId", physicalWarehouseId);
		Location location = inventoryDao.selectLocationByCode(map_);

		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("tagCode", tagCode);
		LabelAccept inventoryLocation = inventoryDao
				.selectInventoryLocation(searchMap);

		Map<String, Object> updatedMap = new HashMap<String, Object>();
		updatedMap.put("locationKWBarcode", locationBarcode);
		updatedMap.put("actionUser", actionUser);
		updatedMap.put("status", "SUCCESS");
		updatedMap.put("inventoryLocationId",
				inventoryLocation.getInventory_location_id());
		int col = inventoryDao.updateInventoryLocation(updatedMap);
		if (col < 1) {
			throw new RuntimeException("updateInventoryLocationo effect");
		}

		Map<String, Object> updateMap = new HashMap<String, Object>();
		updateMap.put("location_id", location.getLocation_id());
		updateMap.put("is_empty", "N");
		inventoryDao.updateLocation(updateMap);
		// InventoryLocationDetail inventoryLocationDetail = new
		// InventoryLocationDetail();
		// inventoryLocationDetail.setInventory_location_id(inventoryLocation
		// .getInventory_location_id());
		// inventoryLocationDetail.setProduct_id(productId);
		// inventoryLocationDetail.setAction_type("RECEIVE");
		// inventoryLocationDetail.setChange_quantity(quantity);
		// inventoryLocationDetail.setOrder_id(orderId);
		// inventoryLocationDetail.setOrder_goods_id(orderGoodsId);
		// inventoryLocationDetail.setCreated_user(actionUser);
		//
		// col = inventoryDao
		// .insertInventoryLocationDetail(inventoryLocationDetail);
		// if (col < 1) {
		// throw new RuntimeException(
		// "inventoryLocationDetail insert error no effect");
		// }

	}

	/**
	 * 查找采购订单
	 * 
	 * @author hzhang1
	 * @date 2016-03-14
	 * @param map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map> selectPurchaseOrderList(Map map) {

		// 1.根据搜索条件查找采购订单
		List<Map> orderList = orderDao.selectPurchaseOrderListByPage(map);
		List<Map> newOrderList = new ArrayList<Map>();
		if (WorkerUtil.isNullOrEmpty(orderList)) {
			return newOrderList;
		}
		Map<String, Object> tempMap = new HashMap<String, Object>();
		List<Integer> purchaseOrderIdList = new ArrayList<Integer>();
		for (Map orderMap : orderList) {
			purchaseOrderIdList.add(Integer.parseInt(orderMap.get("order_id")
					.toString()));
		}
		map.put("purchaseOrderIdList", purchaseOrderIdList);
		List<Map> batchOrderSnList = orderDao.selectPurchaseBatchOrderSn(map);
		for (Map orderMap : batchOrderSnList) {
			tempMap.put(
					orderMap.get("batch_order_sn").toString(),
					tempMap.containsKey(orderMap.get("batch_order_sn")
							.toString()) ? Integer.parseInt(tempMap.get(
							orderMap.get("batch_order_sn").toString())
							.toString()) + 1 : 1);
		}

		// 2.添加每个订单所含的商品数目
		String last_order_goods_id = "";
		for (Map orderMap : orderList) {
			int index = 0;
			int quantity = 0;
			List<Map> returnMap = orderDao
					.selectPurchaseOrderGoodsListByOrderId(Integer
							.parseInt(orderMap.get("order_id").toString()));
			Map<String, Object> tempGoodsMap = new HashMap<String, Object>();
			for (Map map_ : returnMap) {
				if (!WorkerUtil.isNullOrEmpty(map_.get("quantity"))) {
					quantity += Integer.parseInt(map_.get("quantity")
							.toString());
				}
				tempGoodsMap.put(
						map_.get("order_goods_id").toString(),
						tempGoodsMap.containsKey(map_.get("order_goods_id")
								.toString()) ? Integer.parseInt(tempGoodsMap
								.get(map_.get("order_goods_id").toString())
								.toString()) + 1 : 1);
			}

			for (Map goodMap : returnMap) {
				String order_goods_id = goodMap.get("order_goods_id")
						.toString();
				if (!"".equals(last_order_goods_id)
						&& order_goods_id.equals(last_order_goods_id)) {
					if (index == 0) {
						goodMap.put("row_num", 1);
					} else {
						goodMap.put("row_num", 0);
					}
					continue;
				} else {
					if (index == 0) {
						if (Integer.parseInt(tempGoodsMap.get(
								goodMap.get("order_goods_id").toString())
								.toString()) > 1) {
							goodMap.put("row_num", 1);
							goodMap.put("change_quantity", quantity);
						} else {
							goodMap.put("row_num", returnMap.size());
							goodMap.put(
									"change_quantity",
									WorkerUtil.isNullOrEmpty(goodMap
											.get("quantity")) ? "" : goodMap
											.get("quantity"));
						}

					} else {
						goodMap.put("row_num", 0);
						goodMap.put("change_quantity", WorkerUtil
								.isNullOrEmpty(goodMap.get("quantity")) ? ""
								: goodMap.get("quantity"));
					}
				}
				goodMap.put("oms_order_sn", orderMap.get("oms_order_sn"));
				goodMap.put("order_id", orderMap.get("order_id"));
				goodMap.put("arrival_time", WorkerUtil.isNullOrEmpty(orderMap.get("arrival_time"))?"":
					orderMap.get("arrival_time").toString());
				goodMap.put("order_time", orderMap.get("order_time").toString());
				goodMap.put("order_status", orderMap.get("order_status"));
				goodMap.put("provider_name", orderMap.get("provider_name"));
				goodMap.put("customer_id", orderMap.get("customer_id"));
				goodMap.put("batch_order_sn", orderMap.get("batch_order_sn"));
				goodMap.put("warehouse_id", orderMap.get("warehouse_id"));
				goodMap.put("warehouse_name", orderMap.get("warehouse_name"));
				goodMap.put("name", orderMap.get("name"));
				// goodMap.put("change_quantity", orderMap.get("quantity"));
				goodMap.put("batch_sn_num",
						tempMap.get(orderMap.get("batch_order_sn").toString())
								.toString());
				tempMap.put(orderMap.get("batch_order_sn").toString(), 0);

				newOrderList.add(goodMap);
				last_order_goods_id = order_goods_id;
				index++;
			}

		}

		// 3.返回全新组装的orderList
		return newOrderList;
	}

	/**
	 * 标签报存数据库location
	 * 
	 * @author hzhang1
	 */
	public int generateLocation(String locationBarcode,
			Integer physicalWarehouseId, String actionUser, Integer customerId) {
		Location location = new Location();
		location.setPhysical_warehouse_id(physicalWarehouseId);
		location.setLocation_barcode(locationBarcode);
		location.setLocation_type("PURCHASE_SEQ");
		location.setIs_delete("N");
		location.setCreated_user(actionUser);
		location.setCustomer_id(customerId);
		inventoryDao.insertLocation(location);
		return location.getLocation_id();
	}

	/**
	 * 随机生成托盘标签，插入Location表
	 * 
	 * @author hzhang1
	 * @date 2016-03-14
	 * @param physicalWarehouseId
	 *            ,locationBarcode,locationType,createdUser
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map printTag(String[] tagList, String actionUser) {

		Map<String, Object> resMap = new HashMap<String, Object>();
		List<Map> tagPrintList = new ArrayList<Map>();
		Integer orderId = null;
		try {
			int arrive_number = 0;
			int serial_number = 0;
			List<String> serialNoList = new ArrayList<String>();
			for (String tag : tagList) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(tag);
				tag = m.replaceAll("");
				JSONObject tagJson = JSONObject.fromObject(tag);
				arrive_number = Integer.parseInt(tagJson
						.getString("arrive_number"));
				orderId = Integer.parseInt(tagJson.getString("order_id"));
				String serialNo = tagJson.getString("serial_no");
				if (!WorkerUtil.isNullOrEmpty(serialNo)) {
					String serialArr[] = serialNo.split(",");
					if (!WorkerUtil.isNullOrEmpty(serialNo)
							&& !"".equals(serialNo)) {
						serial_number += serialArr.length;
					}
					for (int i = 0; i < serialArr.length; i++) {
						if (serialNoList.contains(serialArr[i])) {
							resMap.put("result", "failure");
							resMap.put("note", "串号码重复");
							return resMap;
						} else {
							serialNoList.add(serialArr[i]);
						}
					}
				}
			}
			if (serial_number > 0) {
				List<String> list = inventoryDao
						.selectSerialNoIsExist(serialNoList);
				if (!WorkerUtil.isNullOrEmpty(list)) {
					resMap.put("result", "failure");
					resMap.put("note", "已存在串号" + list);
					return resMap;
				}
				if (serial_number != arrive_number) {
					resMap.put("result", "failure");
					resMap.put("note", "到货数量和串号数量不一致");
					return resMap;
				}
			}

			OrderInfo orderInfo = orderDao.getOderInfoByIdForUpdate(Integer
					.valueOf(orderId));
			for (String tag : tagList) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(tag);
				tag = m.replaceAll("");
				JSONObject tagJson = JSONObject.fromObject(tag);

				// 1.得到参数
				Integer physicalWarehouseId = Integer.parseInt(tagJson
						.getString("physical_warehouse_id"));
				Integer customerId = Integer.parseInt(tagJson
						.getString("customer_id"));
				String omsOrderSn = tagJson.getString("oms_order_sn");
				String locationBarcode = tagJson.getString("location_barcode");
				Integer productId = Integer.parseInt(tagJson
						.getString("product_id"));
				Integer warehouseId = Integer.parseInt(tagJson
						.getString("warehouse_id"));
				String serialNo = tagJson.getString("serial_no");
				String barcode = tagJson.getString("barcode");
				Integer arriveNumber = Integer.parseInt(tagJson
						.getString("arrive_number"));
				Integer trayNumber = Integer.parseInt(tagJson
						.getString("tray_number"));
				String validity = tagJson.getString("validity");
				String ti = tagJson.getString("dui_die_length");
				String hi = tagJson.getString("dui_die_width");
				Integer normalNumber = WorkerUtil.isNullOrEmpty(tagJson
						.getString("normal_number")) ? 0 : Integer
						.parseInt(tagJson.getString("normal_number"));
				Integer defectiveNumber = WorkerUtil.isNullOrEmpty(tagJson
						.getString("defective_number")) ? 0 : Integer
						.parseInt(tagJson.getString("defective_number"));
				// Integer orderId =
				// Integer.parseInt(tagJson.getString("order_id"));
				Integer orderGoodsId = Integer.parseInt(tagJson
						.getString("order_goods_id"));
				String batchSn = tagJson.getString("batch_sn");
				String isMaintainBatchSn = tagJson
						.getString("is_maintain_batch_sn");

				// 2.输出日志
				logger.info("Generate locationBarcode：" + locationBarcode
						+ " physicalWarehouseId:" + physicalWarehouseId
						+ " omsOrderSn:" + omsOrderSn + " product_id:"
						+ productId + " warehouse_id:" + warehouseId
						+ " serial_no:" + serialNo + " barcode:" + barcode
						+ " arrive_number:" + arriveNumber + " tray_number:"
						+ trayNumber + " validity:" + validity + " dui_die_ti:"
						+ ti + " dui_die_hi:" + hi + " normal_number:"
						+ normalNumber + " defective_number:" + defectiveNumber
						+ " batchSn:" + batchSn);
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("location_barcode", locationBarcode);
				map.put("normal_number", normalNumber);
				map.put("defective_number", defectiveNumber);
				map.put("validity", validity);
				map.put("order_goods_id", orderGoodsId);
				map.put("physical_warehouse_id", physicalWarehouseId);
				map.put("oms_order_sn", omsOrderSn);
				map.put("goods_name", tagJson.getString("goods_name"));
				map.put("barcode", barcode);
				map.put("batch_sn", batchSn);
				map.put("is_maintain_batch_sn", isMaintainBatchSn);
				tagPrintList.add(map);

				// 3.插入inventoryLocation
				String locationArr[] = locationBarcode.split(",");
				for (String location_barcode : locationArr) {
					Map map_ = inventoryDao.selectByTagCodeV2(location_barcode);
					if (!WorkerUtil.isNullOrEmpty(map_)) {
						continue;
					}
					Map<String, Object> searchMap = new HashMap<String, Object>();
					searchMap.put("orderId", orderId);
					searchMap.put("orderGoodsId", orderGoodsId);
					Map returnMap = orderDao.selectPurchaseAcceptNum(searchMap);
					if (!WorkerUtil.isNullOrEmpty(returnMap)
							&& !WorkerUtil.isNullOrEmpty(returnMap
									.get("quantity"))
							&& (Integer.parseInt(returnMap.get("quantity")
									.toString()) + normalNumber + defectiveNumber) > Integer
										.parseInt(returnMap.get("goods_number")
												.toString())) {
						resMap.put("tagPrintList", tagPrintList);
						resMap.put("result", "failure");
						resMap.put("note", "收货数量超过实际采购数量");
						return resMap;
					}

					Map<String, Object> map2_ = new HashMap<String, Object>();
					map2_.put("locationBarcode", location_barcode);
					map2_.put("physicalWarehouseId", physicalWarehouseId);
					Location location = inventoryDao
							.selectLocationByCode(map2_);
					Integer locationId = location.getLocation_id();


					
					Task task = new Task();
					
					task.setPhysical_warehouse_id(physicalWarehouseId);
					task.setCustomer_id(customerId);
					task.setTask_type(Task.TASK_TYPE_PUT_AWAY);
					task.setTask_status(Task.TASK_STATUS_INIT);
					task.setTask_level(1);
					task.setProduct_id(productId);
					task.setQuantity(arriveNumber);
					task.setOperate_platform("WEB");
					task.setCreated_user(actionUser);
					task.setLast_updated_user(actionUser);
					taskDao.insert(task);
					
				
					LabelAccept labelAccept = new LabelAccept();
					labelAccept.setTask_id(task.getTask_id());
					labelAccept.setLocation_id(locationId);
					labelAccept.setLocation_barcode(locationBarcode);
					labelAccept.setCustomer_id(customerId);
					labelAccept.setPhysical_warehouse_id(physicalWarehouseId);
					labelAccept.setWarehouse_id(warehouseId);
					labelAccept
							.setIs_serial(!WorkerUtil.isNullOrEmpty(serialNo)
									&& !"".equals(serialNo) ? "Y" : "N");
					labelAccept.setProduct_id(productId);
					labelAccept.setSerial_no(serialNo);
					labelAccept.setGoods_barcode(barcode);
					labelAccept.setArrive_number(arriveNumber);
					labelAccept.setTray_number(trayNumber);

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					if (!WorkerUtil.isNullOrEmpty(isMaintainBatchSn)) {
						if ("Y".equals(isMaintainBatchSn)) {
							labelAccept.setBatch_sn(batchSn);// sdf.format(new
																// Date())
						} else if ("O".equals(isMaintainBatchSn)) {
							try {
								if (!WorkerUtil.isNullOrEmpty(validity)) {
									labelAccept
											.setBatch_sn(sdf.format(new Date(
													sdf.parse(validity)
															.getTime())));
								}
							} catch (Exception e) {
								labelAccept.setBatch_sn("1970-01-01");
							}
						} else if ("D".equals(isMaintainBatchSn)) {
							labelAccept.setBatch_sn("1970-01-01");
						} else {
							labelAccept.setBatch_sn("");
						}
					}
					Date date = null;
					try {
						if (!WorkerUtil.isNullOrEmpty(validity)) {
							
							String temp = validity;
							date =  sdf.parse(temp.substring(0, 4)+"-"+temp.substring(5, 7)+"-"+temp.substring(8, 10));
							Calendar cal = Calendar.getInstance();
							cal.setTime(date); 
							if(sdf.parse(validity).compareTo(new Date()) == 1){
								Product product = productDao.selectProductByBarcodeCustomer(barcode, customerId);
								int validity_ = WorkerUtil.isNullOrEmpty(product)?12:product.getValidity();
								String validity_unit = WorkerUtil.isNullOrEmpty(product)?"MONTH":product.getValidity_unit();
								
								if("MONTH".equals(validity_unit)){
									cal.add(Calendar.MONTH, -validity_);
								}else if("DAY".equals(validity_unit)){
									cal.add(Calendar.DATE, -validity_);
								}
								date=cal.getTime();    
								validity = sdf.format(date);
								
								if(cal.after( Calendar.getInstance())){
									resMap.put("result", "failure");
									resMap.put("note", "生产日期不能大于当前时间");
									return resMap;
								}
							}else{
								date = sdf.parse(validity);
							}
							
							labelAccept.setValidity(date);
						}
					} catch (ParseException e) {
						e.printStackTrace();
						logger.error("No validity:", e);
					}

					if (normalNumber > 0) {
						labelAccept.setQuantity(normalNumber);
						labelAccept.setStatus_id("NORMAL");
					}
					if (defectiveNumber > 0) {
						labelAccept.setQuantity(defectiveNumber);
						labelAccept.setStatus_id("DEFECTIVE");
					}
					labelAccept.setTi(ti);
					labelAccept.setHi(hi);
					labelAccept.setCreated_user(actionUser);
					labelAccept.setLast_updated_user(actionUser);
					labelAccept.setOrder_id(orderId);
					labelAccept.setOrder_goods_id(orderGoodsId);
					inventoryDao.insertInventoryLocation(labelAccept);

					// InventoryLocationDetail inventoryLocationDetail = new
					// InventoryLocationDetail();
					// inventoryLocationDetail.setInventory_location_id(inventoryLocation.getInventory_location_id());
					// inventoryLocationDetail.setProduct_id(productId);
					// inventoryLocationDetail.setAction_type("RECEIVE");
					// if (normalNumber > 0) {
					// inventoryLocationDetail
					// .setChange_quantity(normalNumber);
					// }
					// if (defectiveNumber > 0) {
					// inventoryLocationDetail
					// .setChange_quantity(defectiveNumber);
					// }
					// inventoryLocationDetail.setOrder_id(orderId);
					// inventoryLocationDetail.setOrder_goods_id(orderGoodsId);
					// inventoryLocationDetail.setCreated_user(actionUser);
					//
					// inventoryDao.insertInventoryLocationDetail(inventoryLocationDetail);

					UserActionOrder userActionOrder = new UserActionOrder();
					userActionOrder.setOrder_id(orderId);
					userActionOrder.setOrder_status("IN_PROCESS");
					userActionOrder.setAction_type("PRINT_TAG");
					userActionOrder
							.setAction_note("采购订单打印标签" + locationBarcode);
					userActionOrder.setCreated_user(actionUser);
					userActionOrder.setCreated_time(new Date());
					userActionOrderDao.insert(userActionOrder);
					
					
					UserActionTask userActionTask = new UserActionTask();
					userActionTask.setTask_id(task.getTask_id());
					userActionTask.setAction_note("WEB采购收货生成上架任务");
					userActionTask.setAction_type(Task.TASK_TYPE_PUT_AWAY);
					userActionTask.setTask_status(Task.TASK_TYPE_PUT_AWAY);
					userActionTask.setCreated_user(actionUser);
					userActionTask.setCreated_time(new Date());
					userActionTaskDao.insert(userActionTask);
				}
			}
		} catch (NumberFormatException e) {
			logger.error("GenerateLocation error", e);
			throw new RuntimeException("GenerateInventoryLocation error");
		}

		resMap.put("tagPrintList", tagPrintList);
		resMap.put("result", "success");
		resMap.put("note", "success");
		return resMap;
	}

	/**
	 * 打印托盘标签，打印的时候会写入inventory_location & inventory_location_detail
	 * 
	 * @author hzhang1
	 * @date 2016-03-14
	 * @param purchaseAcceptVO
	 *            ,orderId ,createdUser,customerId
	 */
	public Map insertInventoryLocation(PurchaseAcceptVO purchaseAcceptVO,
			Integer orderId, String createdUser, Integer customerId) {

		Map<String, Object> resMap = new HashMap<String, Object>();

		// List<PurchaseProductAccDetail> purchaseProductAccDetailList =
		// purchaseAcceptVO
		// .getPurchaseProductAccDetailList();
		// for (PurchaseProductAccDetail detail : purchaseProductAccDetailList)
		// {
		//
		// List<PurchaseProduct> purchaseProductList = detail
		// .getPurchaseProductList();
		// for (PurchaseProduct detail2 : purchaseProductList) {
		//
		// String locationBarcode = detail2.getTag();
		// Location location = inventoryDao
		// .selectLocationByCode(locationBarcode);
		// if (WorkerUtil.isNullOrEmpty(location)) {
		// resMap.put("result", "failure");
		// resMap.put("note",
		// "InsertInventoryLocation Location is null");
		// return resMap;
		// }
		// if (!WorkerUtil.isNullOrEmpty(detail2.getSerial_no())) {
		// String serialArr[] = detail2.getSerial_no().split(",");
		// if (!WorkerUtil.isNullOrEmpty(detail2.getSerial_no())
		// && !"".equals(detail2.getSerial_no())
		// && serialArr.length != detail.getArrive_number()) {
		// resMap.put("result", "failure");
		// resMap.put("note", "到货数量和串号数量不一致");
		// return resMap;
		// }
		// }
		//
		// InventoryLocation inventoryLocation = new InventoryLocation();
		// inventoryLocation.setLocation_id(location.getLocation_id());
		// inventoryLocation.setLocation_barcode(locationBarcode);
		// inventoryLocation.setCustomer_id(customerId);
		// inventoryLocation.setPhysical_warehouse_id(detail
		// .getPhysical_warehouse_id());
		// inventoryLocation.setWarehouse_id(detail.getWarehouse_id());
		// inventoryLocation.setIs_serial(!WorkerUtil
		// .isNullOrEmpty(detail2.getSerial_no())
		// && !"".equals(detail2.getSerial_no()) ? "Y" : "N");
		// inventoryLocation.setProduct_id(detail.getProduct_id());
		// inventoryLocation.setSerial_no(detail2.getSerial_no());
		// inventoryLocation.setGoods_barcode(detail.getBarcode());
		// inventoryLocation.setArrive_number(detail.getArrive_number());
		// inventoryLocation.setTray_number(detail.getTray_number());
		//
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// inventoryLocation.setBatch_sn(sdf.format(new Date()));
		//
		// String strDate = detail2.getValidity();
		// Date date = null;
		// try {
		// if (!WorkerUtil.isNullOrEmpty(strDate)) {
		// date = sdf.parse(strDate);
		// inventoryLocation.setValidity(date);
		// }
		// } catch (ParseException e) {
		// e.printStackTrace();
		// logger.error("No validity:", e);
		// }
		//
		// Integer change_quantity = 0;
		// if (!WorkerUtil.isNullOrEmpty(detail2.getNormal_number())
		// && detail2.getNormal_number() > 0) {
		// change_quantity = detail2.getNormal_number();
		// inventoryLocation.setQuantity(change_quantity);
		// inventoryLocation.setStatus_id("NORMAL");
		// }
		//
		// if (!WorkerUtil.isNullOrEmpty(detail2.getDefective_number())
		// && detail2.getDefective_number() > 0) {
		// change_quantity = detail2.getDefective_number();
		// inventoryLocation.setQuantity(change_quantity);
		// inventoryLocation.setStatus_id("DEFECTIVE");
		// }
		// inventoryLocation.setPile(detail.getDui_die());
		// inventoryLocation.setCreated_user(createdUser);
		// inventoryLocation.setLast_updated_user(createdUser);
		//
		// int col = inventoryDao
		// .insertInventoryLocation(inventoryLocation);
		//
		// InventoryLocationDetail inventoryLocationDetail = new
		// InventoryLocationDetail();
		// inventoryLocationDetail
		// .setInventory_location_id(inventoryLocation
		// .getInventory_location_id());
		// inventoryLocationDetail.setProduct_id(detail.getProduct_id());
		// inventoryLocationDetail.setAction_type("RECEIVE");
		// inventoryLocationDetail.setChange_quantity(change_quantity);
		// inventoryLocationDetail.setOrder_id(orderId);
		// inventoryLocationDetail.setOrder_goods_id(detail
		// .getOrder_goods_id());
		// inventoryLocationDetail.setCreated_user(createdUser);
		//
		// col = inventoryDao
		// .insertInventoryLocationDetail(inventoryLocationDetail);
		// }
		// }
		//
		// resMap.put("result", "success");
		// resMap.put("note", "InsertInventoryLocation success");
		return resMap;
	}

	/**
	 * 根据托盘标签来查找上架信息
	 * 
	 * @author hzhang1
	 * @date 2016-03-14
	 * @param location_barcode
	 */
	public List<Map> selectByTagCode(Integer physical_warehouse_id,
			String location_barcode) {
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("location_barcode", location_barcode);
		searchMap.put("physical_warehouse_id", physical_warehouse_id);
		Map map = inventoryDao.selectByTagCode(searchMap);
		List<Map> newMapList = new ArrayList<Map>();

		if (WorkerUtil.isNullOrEmpty(map)) {
			logger.info("selectByTagCode tag_code is null");
			Map<String, Object> mapRes = new HashMap<String, Object>();
			mapRes.put("result", "failure");
			mapRes.put("note", "搜索到的标签为空，请检查标签");
			newMapList.add(mapRes);
			return newMapList;
		} else if (!WorkerUtil.isNullOrEmpty(map)
				&& !WorkerUtil.isNullOrEmpty(map.get("location_kw_barcode"))) {
			Map<String, Object> mapRes = new HashMap<String, Object>();
			mapRes.put("result", "failure");
			mapRes.put("note", "搜索到的标签已上架");
			newMapList.add(mapRes);
			return newMapList;
		}

		// 先获取是否有相同的商品在库位，再筛选空库位
		Map<String,Object> searchMap1 = new HashMap<String,Object>();
		searchMap1.put("product_id", map.get("product_id").toString());
		searchMap1.put("warehouse_id",map.get("warehouse_id").toString());
		searchMap1.put("physical_warehouse_id",map.get("physical_warehouse_id").toString());
		searchMap1.put("warehouse_id",map.get("warehouse_id").toString());
		if("PACKBOX".equals(map.get("product_type").toString())){
			searchMap1.put("location_type", Location.LOCATION_TYPE_PACKBOX);
		}
		if("DEFECTIVE".equals(map.get("status_id").toString())){
			searchMap1.put("location_type", Location.LOCATION_TYPE_DEFECTIVE);
		}
		List<String> locationList1 = inventoryDao.selectLocationByProduct(searchMap1);

		if(WorkerUtil.isNullOrEmpty(locationList1)){
			Map<String, Object> searchMap2 = new HashMap<String, Object>();
			searchMap2.put("warehouse_id", map.get("warehouse_id").toString());
			searchMap2.put("physical_warehouse_id", map
					.get("physical_warehouse_id").toString());
			searchMap2.put("location_type", Location.LOCATION_TYPE_STOCK);
			if("PACKBOX".equals(map.get("product_type").toString())){
				searchMap2.put("location_type", Location.LOCATION_TYPE_PACKBOX);
			}
			if("DEFECTIVE".equals(map.get("status_id").toString())){
				searchMap2.put("location_type", Location.LOCATION_TYPE_DEFECTIVE);
			}
			List<String> locationList2 = inventoryDao
					.selectLocationByEmpty(searchMap2);
			locationList1.addAll(locationList2);
		}
		

		map.put("result", "success");
		if (!WorkerUtil.isNullOrEmpty(locationList1)) {
			map.put("location_barcode", locationList1.get(0).toString());
		} else {
			map.put("location_barcode", "空");
		}
		map.put("success", true);
		newMapList.add(map);

		return newMapList;
	}

	// 拣货区库位管理
	public List<Map> getLocation(Map map) {
		// return inventoryDao.selectLocationInfoByPage(map);
		return null;
	}

	/**
	 * 单个采购入库底层操纵数据库方法
	 * 
	 * @author hzhang1
	 * @date 2016-03-14
	 * @param Integer
	 *            productId, Integer amount, String is_serial, String serialNo,
	 *            Integer orderId, String toStatusId, String actionUser,
	 *            BigDecimal unitcost, Integer orderGoodsId, Integer
	 *            warehouse_id, String toContainerId, String locationBarcode,
	 *            String goodsBarcode, Date validity, String batchSn, String
	 *            providerId,Integer customer_id,String currency
	 **/
	private void createAcceptInventoryTransactionSingleReal(Integer productId,
			Integer quantity, String is_serial, String serialNo,
			Integer orderId, String toStatusId, String actionUser,
			BigDecimal unitcost, Integer orderGoodsId, Integer warehouse_id,
			Integer physicalWarehouseId, String toContainerId,
			String locationBarcode, String goodsBarcode, Date validity,
			String batchSn, String providerId, Integer customerId,
			String currency, String inventoryItemAcctTypeId) {

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 新建一个InventoryItem
		InventoryItem inventoryItem = new InventoryItem();
		inventoryItem.setPhysical_warehouse_id(physicalWarehouseId);
		inventoryItem.setWarehouse_id(warehouse_id);
		inventoryItem.setCustomer_id(customerId);
		inventoryItem.setProduct_id(productId);
		inventoryItem.setQuantity(quantity);
		inventoryItem.setStatus(toStatusId);
		inventoryItem.setUnit_cost(unitcost);
		inventoryItem.setProvider_code(providerId);
		if (!"".equals(validity)) {
			inventoryItem.setValidity(validity);
		} else {
			try {
				inventoryItem.setValidity(new Timestamp(sdf.parse(
						"1970-01-01 00:00:01").getTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		inventoryItem.setBatch_sn(batchSn);
		inventoryItem.setSerial_number(serialNo);
		inventoryItem.setInventory_item_acct_type_id(inventoryItemAcctTypeId);
		inventoryItem.setCurrency(currency);
		inventoryItem.setCreated_user(actionUser);
		inventoryItem.setLast_updated_user(actionUser);
		inventoryItem.setCreated_time(new Date());
		inventoryItem.setLast_updated_time(new Date());

		inventoryItem.setParent_inventory_item_id(0);
		inventoryItem.setRoot_inventory_item_id(0);
		int col = inventoryDao.insert(inventoryItem);
		logger.info("返回影响行数：" + col);
		if (col < 1) {
			throw new RuntimeException("inventory_item insert error no effect");
		}
		logger.info("返回插入item的自增主键值为：" + inventoryItem.getInventory_item_id());
		inventoryDao.updateRootItemId(inventoryItem.getInventory_item_id());

		// 创建一条Detail记录，反映InventoryItem数量的变化
		InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
		inventoryItemDetail.setInventory_item_id(inventoryItem
				.getInventory_item_id());
		inventoryItemDetail.setProduct_id(productId);
		inventoryItemDetail.setChange_quantity(quantity);
		inventoryItemDetail.setOrder_id(orderId);
		inventoryItemDetail.setOrder_goods_id(orderGoodsId);
		inventoryItemDetail.setCustomer_id(customerId);
		inventoryItemDetail.setWarehouse_id(warehouse_id);
		inventoryItemDetail.setPackbox_customer_id(0);
		inventoryItemDetail.setPackbox_warehouse_id(0);
		inventoryItemDetail.setCreated_user(actionUser);
		inventoryItemDetail.setCreated_time(new Date());
		inventoryItemDetail.setLast_updated_user(actionUser);
		inventoryItemDetail.setLast_updated_time(new Date());
		col = inventoryItemDetailDao.insert(inventoryItemDetail);
		if (col < 1) {
			throw new RuntimeException(
					"inventoryItemDetail insert error no effect");
		}

	}

	/*************************************************
	 * API 方法（-V调整库存）
	 * 
	 * @author hzhang1
	 ************************************************/
	public Map<String, Object> deleteViranceOrder(Integer orderId,
			String actionUser) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			orderBiz.updateOrderInfoStatusV2(orderId, "FULFILLED", actionUser);
			resMap.put("result", "success");
			resMap.put("note", "删除成功");
		} catch (Exception e) {
			resMap.put("result", "failure");
			resMap.put("note", e.getMessage());
		}
		return resMap;
	}

	public Map<String, Object> deliverInventoryViranceOrderInventory(
			Integer orderGoodsId, String serialNumber, String actionUser) {

		Map<String, Object> resMap = new HashMap<String, Object>();

		// 1.根据orderGoodsId得到具体的某一商品
		Map goodsMap = orderInfoDao
				.selectVarianceOrderByOrderGoodsId(orderGoodsId);

		Integer productId = null, warehouseId = null, customerId = null, orderId = null, quantity = null;
		String batchSn = "", comment = "", orderType = "", inventoryItemTypeName = "", statusId = "", providerId = "";
		BigDecimal unitCost = null;

		try {
			productId = Integer.parseInt(goodsMap.get("product_id").toString());
			warehouseId = Integer.parseInt(goodsMap.get("warehouse_id")
					.toString());
			statusId = goodsMap.get("status_id").toString();
			unitCost = WorkerUtil.getDecimalValue(goodsMap.get("goods_price")
					.toString());
			batchSn = goodsMap.get("batch_sn").toString();
			comment = goodsMap.get("note").toString();
			orderType = goodsMap.get("order_type").toString();
			orderId = Integer.parseInt(goodsMap.get("order_id").toString());
			quantity = Integer
					.parseInt(goodsMap.get("goods_number").toString());
			inventoryItemTypeName = "NON-SERIALIZED";
			customerId = Integer.parseInt(goodsMap.get("customer_id")
					.toString());
			providerId = goodsMap.get("provider_code").toString();
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		}

		// 2.获取发票类型等
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("productId", productId);
		searchMap.put("warehouseId", warehouseId);
		if (!WorkerUtil.isNullOrEmpty(batchSn)) {
			searchMap.put("batch_sn", batchSn);
		}
		Map typeMap = orderInfoDao.selectInventoryItemType(searchMap);
		String inventoryItemAcctTypeName = null, serialNo = null;
		if (!WorkerUtil.isNullOrEmpty(typeMap)) {
			inventoryItemAcctTypeName = typeMap
					.containsKey("inventory_item_acct_type_id")
					&& !WorkerUtil.isNullOrEmpty(typeMap
							.get("inventory_item_acct_type_id")) ? typeMap.get(
					"inventory_item_acct_type_id").toString() : "";
			serialNo = typeMap.containsKey("serial_number") ? typeMap.get(
					"serial_number").toString() : "SERIALIZED";
			if ("SERIALIZED".equals(serialNo)) {
				quantity = 1;
				inventoryItemTypeName = "SERIALIZED";
			}
		}

		if ("VARIANCE_MINUS".equals(orderType)) {
			quantity = -quantity;
		}

		// 3.调整库存方法
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			returnMap = createInventoryItemVarianceByProductId(productId,
					inventoryItemAcctTypeName, inventoryItemTypeName, statusId,
					serialNumber, quantity, unitCost, warehouseId, comment,
					orderId, orderGoodsId, batchSn, customerId, actionUser,
					providerId);
		} catch (Exception e) {
			resMap.put("result", "failure");
			resMap.put("note", "调整库存错误：可能是库存不足" + e.getMessage());
			e.printStackTrace();
		}

		// 4.更改订单状态
		if ("success".equals(returnMap.get("result").toString())) {
			orderBiz.updateOrderInfoStatus(orderId, "FULFILLED", actionUser);
		}

		return returnMap;
	}

	public Map<String, Object> createInventoryItemVarianceByProductId(
			Integer productId, String inventoryItemAcctTypeName,
			String inventoryItemTypeName, String statusId, String serialNumber,
			Integer quantity, BigDecimal unitCost, Integer warehouseId,
			String comment, Integer orderId, Integer orderGoodsId,
			String batchSn, Integer customerId, String actionUser,
			String providerId) {

		Map<String, Object> resMap = new HashMap<String, Object>();

		// 1.参数验证
		if (WorkerUtil.isNullOrEmpty(productId)
				|| WorkerUtil.isNullOrEmpty(statusId)
				|| WorkerUtil.isNullOrEmpty(warehouseId)
				|| WorkerUtil.isNullOrEmpty(orderId)
				|| WorkerUtil.isNullOrEmpty(orderGoodsId)
				|| WorkerUtil.isNullOrEmpty(quantity)
				|| WorkerUtil.isNullOrEmpty(unitCost)) {
			resMap.put("result", "failure");
			resMap.put("error_code", "40001");
			resMap.put("note", "存在参数有误");
			return resMap;
		}

		// 2.判断盘盈 or 盘亏
		String inventoryTransactionTypeId = "";
		if (quantity.compareTo(0) == 0) {
			resMap.put("result", "failure");
			resMap.put("error_code", "40002");
			resMap.put("note", "不需要进行盘盈/盘亏");
			return resMap;
		} else if (quantity.compareTo(0) > 0) {
			inventoryTransactionTypeId = "ITT_VIRANCE_ADD"; // 盘盈
		} else if (quantity.compareTo(0) < 0) {
			inventoryTransactionTypeId = "ITT_VIRANCE_MINUS"; // 盘亏
			quantity = -quantity;
		}

		/***********************************************
		 * 盘盈：实际库存 >　系统库存，所以需要系统入库 盘亏：实际库存 < 系统库存，所以需要系统出库
		 ***********************************************/
		batchSn = (batchSn == null ? "" : batchSn);
		Boolean isSerial = inventoryItemTypeName.equals("SERIALIZED") ? true
				: false;
		try {
			if ("ITT_VIRANCE_MINUS".equals(inventoryTransactionTypeId)) {
				logger.info("【盘亏出库】ITT_VIRANCE_MINUS");
//				createDeliverInventoryTransactionRealForV(
//						inventoryTransactionTypeId, productId, quantity,
//						serialNumber, orderId, orderGoodsId,
//						inventoryItemAcctTypeName, actionUser, statusId,
//						isSerial, warehouseId, unitCost, comment, batchSn,
//						customerId);
				Response response = this.deliverOrderInventory(OrderInfo.ORDER_TYPE_VARIANCE_MINUS,orderId,null);
				if(!Response.OK.equals(response.getCode())){
					throw new RuntimeException("商品出库失败结束,orderId: " + orderId + ", actionUser:"+actionUser+" 扣减inventory_item失败");
				}
				
			} else if ("ITT_VIRANCE_ADD".equals(inventoryTransactionTypeId)) {
				logger.info("【盘盈入库】ITT_VIRANCE_ADD");
				createAcceptInventoryTransactionSingleRealForV(
						"ITT_VIRANCE_ADD", productId, quantity,
						inventoryItemTypeName, serialNumber, orderId,
						orderGoodsId, inventoryItemAcctTypeName, actionUser,
						statusId, providerId, warehouseId, unitCost, comment,
						batchSn, customerId);
			}
			resMap.put("result", "success");
			resMap.put("note", "调整库存成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("VIRANCE ERROR:", e);
			resMap.put("result", "failure");
			resMap.put("error_code", "40003");
			resMap.put("note", e.getMessage());
			throw new RuntimeException("调整库存出错");
		}

		return resMap;
	}

	private void createAcceptInventoryTransactionSingleRealForV(
			String inventoryTransactionTypeId, Integer productId,
			Integer quantity, String inventoryItemTypeName, String serialNo,
			Integer orderId, Integer orderGoodsId,
			String inventoryItemAcctTypeName, String actionUser,
			String statusId, String providerId, Integer warehouseId,
			BigDecimal unitCost, String comments, String myBatchSn,
			Integer customerId) {

		// 1.新建一个InventoryItem
		InventoryItem inventoryItem = new InventoryItem();
		inventoryItem.setQuantity(quantity);
		inventoryItem.setUnit_cost(unitCost);
		inventoryItem.setSerial_number(serialNo);
		inventoryItem.setWarehouse_id(warehouseId);
		Warehouse warehouse = warehouseDao.selectByWarehouseId(warehouseId);
		inventoryItem.setPhysical_warehouse_id(warehouse
				.getPhysical_warehouse_id());

		inventoryItem.setInventory_item_acct_type_id(inventoryItemAcctTypeName);
		inventoryItem.setProduct_id(productId);
		inventoryItem.setStatus(statusId);
		inventoryItem.setCreated_time(new Date());
		inventoryItem.setLast_updated_time(new Date());
		inventoryItem.setProvider_code(providerId);
		inventoryItem.setCustomer_id(customerId);
		inventoryItem.setCurrency("RMB");

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp ts = null;
		String validity = "1970-01-01 00:00:00";
		try {
			ts = new Timestamp(sdf.parse(validity).getTime());
		} catch (ParseException e) {
			logger.info("createAcceptInventoryTransactionSingleReal parse error validity:"
					+ validity);
		}
		inventoryItem.setValidity(ts);
		inventoryItem.setBatch_sn(myBatchSn);
		inventoryItem.setCreated_user(actionUser);
		inventoryItem.setLast_updated_user(actionUser);
		inventoryDao.insert(inventoryItem);

		// 2.更新一个InventoryItem
		// Integer rootItemId = inventoryItem.getInventory_item_id();
		// InventoryItem rootItem =
		// inventoryItemDao.selectByPrimaryKey(rootItemId);
		// inventoryItem.setUnit_cost(rootItem.getUnit_cost());
		// inventoryItemDao.updateInventoryItem(inventoryItem);

		// 3.创建一条Detail记录，反映InventoryItem数量的变化
		InventoryItemDetail inventoryItemDetail = new InventoryItemDetail(); // 已经设置InventoryTransactionId了
		inventoryItemDetail.setInventory_item_id(inventoryItem
				.getInventory_item_id());
		inventoryItemDetail.setProduct_id(productId);
		inventoryItemDetail.setOrder_id(orderId);
		inventoryItemDetail.setCustomer_id(customerId);
		inventoryItemDetail.setWarehouse_id(warehouseId);
		inventoryItemDetail.setOrder_goods_id(orderGoodsId);
		inventoryItemDetail.setChange_quantity(quantity);
		inventoryItemDetail.setLast_updated_time(new Date());
		inventoryItemDetail.setCreated_time(new Date());
		inventoryItemDetail.setPackbox_customer_id(0);
		inventoryItemDetail.setPackbox_warehouse_id(0);
		inventoryItemDetail.setCreated_user(actionUser);
		inventoryItemDetail.setLast_updated_user(actionUser);
		inventoryItemDetailDao.insert(inventoryItemDetail);

		// 4.创建一条variance记录
		InventoryItemVariance inventoryItemVariance = new InventoryItemVariance();
		inventoryItemVariance.setComments(comments);
		inventoryItemVariance.setCreated_user(actionUser);
		inventoryItemVariance.setInventory_item_id(inventoryItemDetail
				.getInventory_item_id());
		inventoryItemVariance.setQuantity(inventoryItemDetail
				.getChange_quantity());
		inventoryItemVariance.setCreated_time(new Date());
		inventoryItemVariance.setLast_updated_time(new Date());
		inventoryItemVariance.setComments("库存调整订单");
		inventoryItemVarianceDao.insert(inventoryItemVariance);

	}

	private void createDeliverInventoryTransactionRealForV(
			String inventoryTransactionTypeId, Integer productId,
			Integer quantity, String serialNo, int orderId, int orderGoodsId,
			String inventoryItemAcctTypeName, String actionUser,
			String statusId, Boolean isSerial, int warehouseId,
			BigDecimal unitCost, String comment, String myBatchSn,
			Integer customerId) {

		// 1.初始化批次号batchSn & returnSupplierId
		String batchSn = "";
		if (myBatchSn.trim().length() > 0) {
			batchSn = myBatchSn;
		}

		try {
			// 2.检查订单商品出库数，如果要出库数大于未出库数则直接抛异常，防止并发
			boolean checkOrderProductOutNumber = checkOrderProductOutNumber(
					orderGoodsId + "", productId.toString(),
					WorkerUtil.getDecimalValue(quantity));

			if (!checkOrderProductOutNumber) {
				throw new IllegalArgumentException(
						"wms error: creatDeliveryInventory createDeliverInventoryTransactionReal amount > needOutNumber ");
			}

			// 3.得到已经预定的并且未出库的inventory_item,quantity映射，兼容部分出库
			List<InventoryItem> inventoryItems = getInventoryItemsForTransferForV(
					orderGoodsId, warehouseId, customerId, "", serialNo,
					productId, statusId, quantity, unitCost, batchSn);

			if (WorkerUtil.isNullOrEmpty(inventoryItems)) {
				throw new RuntimeException(
						"wms error: creatDeliveryInventory cannot select inventoryItems"
								+ " orderGoodsId:" + orderGoodsId + "可能是库存不足");
			}

			// 4.循环出库
			int thisTurnOutNumLeft = quantity;
			for (InventoryItem inventoryItem : inventoryItems) {
				int need = inventoryItem.getQuantity();
				if (thisTurnOutNumLeft <= need) {
					need = thisTurnOutNumLeft;
				}

				subQuantityByInventoryItemId(
						inventoryItem.getInventory_item_id(), need, actionUser);

				saveInventoryItemDetail(orderId, customerId, productId,
						orderGoodsId, actionUser, warehouseId, inventoryItem,
						need, null, null);

				if ("ITT_VIRANCE_MINUS".equals(inventoryTransactionTypeId)) {
					// 创建一条variance记录
					InventoryItemVariance inventoryItemVariance = new InventoryItemVariance();
					inventoryItemVariance.setComments(comment);
					inventoryItemVariance.setCreated_user(actionUser);
					inventoryItemVariance.setInventory_item_id(inventoryItem
							.getInventory_item_id());
					inventoryItemVariance.setQuantity(-need);
					inventoryItemVariance.setCreated_time(new Date());
					inventoryItemVariance.setLast_updated_time(new Date());
					inventoryItemVarianceDao.insert(inventoryItemVariance);
				}

				thisTurnOutNumLeft = thisTurnOutNumLeft - need;
				if (thisTurnOutNumLeft == 0) {
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("createDeliverInventoryTransactionRealForV error", e);
			throw new RuntimeException(
					"wms error: createDeliverInventoryTransactionRealForV"
							+ e.getMessage());

		}
	}

	private List<InventoryItem> getInventoryItemsForTransferForV(
			Integer orderGoodsId, Integer fromWarehouseId, Integer customerId,
			String fromContainerId, String serialNo, Integer productId,
			String fromStatusId, Integer thisTurnOutNum, BigDecimal unitCost,
			String batchSn) {

		List<String> serialNos = null;
		if (!serialNo.isEmpty()) {
			serialNos.add(serialNo);
		}

		Map<String, Object> paramsMap = new HashMap<String, Object>();

		paramsMap.put("productId", productId);
		paramsMap.put("fromStatusId", fromStatusId);
		paramsMap.put("fromWarehouseId", fromWarehouseId);
		paramsMap.put("customerId", customerId);
		paramsMap.put("unitCost", unitCost);
		paramsMap.put("batchSn", batchSn);
		paramsMap.put("serialNos", serialNos);
		paramsMap.put("thisTurnOutNum", thisTurnOutNum);

		return inventoryDao.getInventoryItemsForTransferForV(paramsMap);

	}

	@Override
	public List<Map> getFreezeGoods(Map map) {
		return inventoryDao.getFreezeGoodsListByPage(map);
	}

	public Map<String, Object> dealFreeze(String act, Integer warehouse_id,
			Integer ori_warehouse_id, Integer reserve_number,
			String freeze_reason, Integer mapping_id, Integer customer_id,
			Integer product_id) {

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse currentPhysicalWarehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");

		Map<String, Object> resMap = new HashMap<String, Object>();

		// 1.先搜寻当前商品的库存
		Map<String, Object> map = new HashMap<String, Object>();

		if ("update".equals(act)) {
			map.put("warehouse_id", warehouse_id);
			map.put("product_id", product_id);
			map.put("physical_warehouse_id",
					currentPhysicalWarehouse.getPhysical_warehouse_id());

			Integer nowNumber = 0;
			try {
				nowNumber = inventoryDao.getNowNumber(map);
			} catch (Exception e) {
				resMap.put("result", "failure");
				resMap.put("note", "仓库没有该商品库存");
				return resMap;
			}

			if (nowNumber < reserve_number) {
				resMap.put("result", "failure");
				resMap.put("note", "仓库剩余数量为" + nowNumber + ",小于该预留库存");
				return resMap;
			}

			InventoryGoodsFreeze inventoryGoodsFreeze = new InventoryGoodsFreeze();

			inventoryGoodsFreeze.setMapping_id(mapping_id);
			inventoryGoodsFreeze.setCustomer_id(customer_id);
			inventoryGoodsFreeze.setWarehouse_id(warehouse_id);
			inventoryGoodsFreeze
					.setPhysical_warehouse_id(currentPhysicalWarehouse
							.getPhysical_warehouse_id());
			inventoryGoodsFreeze.setProduct_id(product_id);
			inventoryGoodsFreeze.setReserve_number(reserve_number);
			inventoryGoodsFreeze.setStatus("OK");
			inventoryGoodsFreeze.setCreated_user("system");
			inventoryGoodsFreeze.setLast_updated_user("system");
			inventoryGoodsFreeze.setFreeze_reason(freeze_reason);
			inventoryDao.updateFreeze(inventoryGoodsFreeze);

		} else if ("insert".equals(act)) {

			map.put("warehouse_id", warehouse_id);
			map.put("product_id", product_id);
			map.put("physical_warehouse_id",
					currentPhysicalWarehouse.getPhysical_warehouse_id());
			Integer nowNumber = 0;
			try {
				nowNumber = inventoryDao.getNowNumber(map);
			} catch (Exception e) {
				resMap.put("result", "failure");
				resMap.put("note", "仓库没有该商品库存");
				return resMap;
			}

			if (!WorkerUtil.isNullOrEmpty(inventoryDao
					.getIsExistFreezeGood(map))) {
				resMap.put("result", "failure");
				resMap.put("note", "已存在此冻结数据，请选择调整记录");
				return resMap;
			}

			if (nowNumber < reserve_number) {
				resMap.put("result", "failure");
				resMap.put("note", "仓库剩余数量为" + nowNumber + ",小于该预留库存");
				return resMap;
			}

			InventoryGoodsFreeze inventoryGoodsFreeze = new InventoryGoodsFreeze();
			inventoryGoodsFreeze.setCustomer_id(customer_id);
			inventoryGoodsFreeze.setWarehouse_id(warehouse_id);
			inventoryGoodsFreeze
					.setPhysical_warehouse_id(currentPhysicalWarehouse
							.getPhysical_warehouse_id());
			inventoryGoodsFreeze.setProduct_id(product_id);
			inventoryGoodsFreeze.setReserve_number(reserve_number);
			inventoryGoodsFreeze.setStatus("OK");
			inventoryGoodsFreeze.setCreated_user("system");
			inventoryGoodsFreeze.setLast_updated_user("system");
			inventoryGoodsFreeze.setCreated_time(new Date());
			inventoryGoodsFreeze.setLast_updated_time(new Date());
			inventoryGoodsFreeze.setFreeze_reason(freeze_reason);

			inventoryDao.insertFreeze(inventoryGoodsFreeze);
		} else if ("delete".equals(act)) {

			inventoryDao.deleteFreeze(mapping_id);
		}

		resMap.put("result", "success");
		resMap.put("note", "success");
		return resMap;
	}

	@Override
	public List<Map> getInventoryGoods(Map map) {
		
		List<Map> inventoryGoodsList = new ArrayList<Map>();
		List<Map> trasitGoodsList = new ArrayList<Map>();
		List<Map> orderCancelGoodsList = new ArrayList<Map>();
		inventoryGoodsList = inventoryDaoSlave.selectProductLocationForInventory(map);
		for(Map temp : inventoryGoodsList){
			String tempLocationName = Location.LOCATION_TYPE_MAP.get(temp.get("location_type").toString()).toString();
			temp.put("location_type_name", tempLocationName);
		}
		
		if(!map.containsKey("batch_sn")){
			if(!map.containsKey("location_type") || (map.containsKey("location_type") && "TRANSIT_LOCATION".equals(map.get("location_type").toString()))){
				trasitGoodsList = inventoryDaoSlave.selectProductLocationForTrasitInventory(map);
				inventoryGoodsList.addAll(trasitGoodsList);
			}
		}
		
		return inventoryGoodsList;
	}

	
	// 修改库位库存生产日期
	public Map editProductLocationValidity(Integer plId,String validity,String actionUser){
		Map<String, Object> resMap = new HashMap<String, Object>();
		ProductLocation productLocation = productLocationDao.selectProduLocationById(plId);
		Product product = productDao.selectByPrimaryKey(productLocation.getProduct_id());
		Location location = locationDao.selectLocationById(productLocation.getLocation_id());
		
		Map<String, Object> paramsForPlMap = new HashMap<String, Object>();
		paramsForPlMap.put("productId", productLocation.getProduct_id());
		paramsForPlMap.put("status", productLocation.getStatus());
		paramsForPlMap.put("validity", validity);
		paramsForPlMap.put("serialNumber", productLocation.getSerial_number());
		paramsForPlMap.put("batchSn", productLocation.getBatch_sn());
		paramsForPlMap.put("locationId", productLocation.getLocation_id());
		paramsForPlMap.put("warehouseId", productLocation.getWarehouse_id());
		ProductLocation productLocation2 = inventoryDao.selectProductLocation(paramsForPlMap);
		if(!WorkerUtil.isNullOrEmpty(productLocation2)){
			ReentrantLock lock = LockUtil.getReentrantLock("physicalWarehouseId_"+ location.getPhysical_warehouse_id() + "_customerId_" + product.getCustomer_id());
			if(lock.tryLock()){
				try {
					if(productLocation.getQty_available() < 0 || productLocation.getQty_total() != productLocation.getQty_available()){
						resMap.put("resule", Response.FAILURE);
						resMap.put("note", "可用库存为0货主库存总量和可用库存不等,不能合并库存");
						return resMap;
					}
					int col1 = productLocationDao.updateProductLocationTotalAvaliable(Math.abs(productLocation.getQty_total()),Math.abs(productLocation.getQty_available()), productLocation2.getPl_id());
					if(col1 < 1) {
						resMap.put("resule", Response.FAILURE);
						resMap.put("note", "更新库位库存生产日期失败");
						throw new RuntimeException("更新库位库存生产日期失败");
						//return resMap;
					}
					int col2 = productLocationDao.updateProductLocationTotalAvaliable(-Math.abs(productLocation.getQty_total()),-Math.abs(productLocation.getQty_available()), productLocation.getPl_id());
					if(col2 < 1) {
						resMap.put("resule", Response.FAILURE);
						resMap.put("note", "更新库位库存生产日期失败");
						throw new RuntimeException("更新库位库存生产日期失败");
						//return resMap;
					}
					resMap.put("resule", Response.SUCCESS);
					resMap.put("note", "更新库位库存生产日期成功,已存在记录故合并至"+productLocation2.getPl_id());
					return resMap;
				} catch (Exception e) {
					logger.error("editProductLocationValidity error:", e);
					throw new RuntimeException(e.getMessage());
				} finally {
					// 释放锁
					lock.unlock();
				}
			}else{
				resMap.put("resule", Response.FAILURE);
				resMap.put("note", "已被锁");
				return resMap;
			}
		}
		String validityStatus = "NORMAL";
		if("Y".equals(product.getIs_maintain_warranty())){
			validityStatus = productLocation.checkValidityStatus(validity, product.getValidity(), product.getValidity_unit(), product.getWarranty_warning_days(), product.getWarranty_unsalable_days());
		}else{
			resMap.put("resule", Response.FAILURE);
			resMap.put("note", "该商品不需要维护生产日期");
			return resMap;
		}
		int col = 0;
		if(!productLocation.getValidity_status().equals(validityStatus)){
			col = inventoryDao.updateProductLocationValidity(plId, validity,validityStatus, actionUser);
		}else{
			col = inventoryDao.updateProductLocationValidity(plId, validity,"", actionUser);
		}
		if(col < 1){
			resMap.put("resule", Response.FAILURE);
			resMap.put("note", "更新库位库存生产日期失败");
		}else{
			resMap.put("resule", Response.SUCCESS);
			resMap.put("note", "更新库位库存生产日期成功");
		}
		return resMap;
	}
	
	public Map editProductLocationBatchSn(Integer plId,String batchSn,String actionUser){
		Map<String, Object> resMap = new HashMap<String, Object>();
		if(WorkerUtil.isNullOrEmpty(batchSn)){
			resMap.put("resule", Response.FAILURE);
			resMap.put("note", "批次号不能为空");
			return resMap;
		}
		ProductLocation productLocation = productLocationDao.selectProduLocationById(plId);
		Product product = productDao.selectByPrimaryKey(productLocation.getProduct_id());
		Location location = locationDao.selectLocationById(productLocation.getLocation_id());
		
		Map<String, Object> paramsForPlMap = new HashMap<String, Object>();
		paramsForPlMap.put("productId", productLocation.getProduct_id());
		paramsForPlMap.put("status", productLocation.getStatus());
		paramsForPlMap.put("validity", productLocation.getValidity());
		paramsForPlMap.put("serialNumber", productLocation.getSerial_number());
		paramsForPlMap.put("batchSn", batchSn);
		paramsForPlMap.put("locationId", productLocation.getLocation_id());
		paramsForPlMap.put("warehouseId", productLocation.getWarehouse_id());
		ProductLocation productLocation2 = inventoryDao.selectProductLocation(paramsForPlMap);
		if(!WorkerUtil.isNullOrEmpty(productLocation2)){
			// System.out.println(productLocation.getPl_id() +"-"+productLocation2.getPl_id());
			ReentrantLock lock = LockUtil.getReentrantLock("physicalWarehouseId_"+ location.getPhysical_warehouse_id() + "_customerId_" + product.getCustomer_id());
			if(lock.tryLock()){
				try {
					if(productLocation.getQty_available() < 0 || productLocation.getQty_total() != productLocation.getQty_available()){
						resMap.put("resule", Response.FAILURE);
						resMap.put("note", "可用库存为0货主库存总量和可用库存不等,不能合并库存");
						return resMap;
					}
					int col1 = productLocationDao.updateProductLocationTotalAvaliable(Math.abs(productLocation.getQty_total()),Math.abs(productLocation.getQty_available()), productLocation2.getPl_id());
					if(col1 < 1) {
						resMap.put("resule", Response.FAILURE);
						resMap.put("note", "更新库位库存批次号失败");
						throw new RuntimeException("更新库位库存批次号失败");
						//return resMap;
					}
					int col2 = productLocationDao.updateProductLocationTotalAvaliable(-Math.abs(productLocation.getQty_total()),-Math.abs(productLocation.getQty_available()), productLocation.getPl_id());
					if(col2 < 1) {
						resMap.put("resule", Response.FAILURE);
						resMap.put("note", "更新库位库存批次号失败");
						throw new RuntimeException("更新库位库存批次号失败");
						//return resMap;
					}
					resMap.put("resule", Response.SUCCESS);
					resMap.put("note", "更新库位库存批次号成功,已存在记录故合并至"+productLocation2.getPl_id());
					return resMap;
				} catch (Exception e) {
					logger.error("editProductLocationBatchSn error:", e);
					throw new RuntimeException(e.getMessage());
				} finally {
					// 释放锁
					lock.unlock();
				}
			}else{
				resMap.put("resule", Response.FAILURE);
				resMap.put("note", "已被锁");
				return resMap;
			}
		}
		int col = 0;
		if("Y".equals(product.getIs_maintain_batch_sn())){
			try {
				col = inventoryDao.updateProductLocationBatchSn(plId, batchSn, actionUser);
			} catch (Exception e) {
				logger.error("ProductLocation库位库存更新失败");
			}
		}else{
			resMap.put("resule", Response.FAILURE);
			resMap.put("note", "该商品不需要维护批次号信息");
			return resMap;
		}
		if(col < 1){
			resMap.put("resule", Response.FAILURE);
			resMap.put("note", "更新库位库存批次号失败");
		}else{
			resMap.put("resule", Response.SUCCESS);
			resMap.put("note", "更新库位库存批次号成功");
		}
		return resMap;
	}
	
	// 库位处理
	public Map dealLocation(String act, Integer customerId,
			Integer physicalWarehouseId, String locationBarcode,
			Integer locationId, String locationType, String actionUser) {

		Map<String, Object> resMap = new HashMap<String, Object>();

		if ("insert".equals(act)) {
			// Warehouse warehouse =
			// warehouseDao.selectByWarehouseId(physicalWarehouseId);

			String locationBarcodeArr[] = locationBarcode.split(",");
			for (int t = 0; t < locationBarcodeArr.length; t++) {
				String location_barcode = locationBarcodeArr[t];

				// 1.先判断下改库位是否已经存在
				Map<String, Object> searchMap = new HashMap<String, Object>();
				searchMap.put("customerId", customerId);
				searchMap.put("locationBarcode", location_barcode);
				searchMap.put("physicalWarehouseId", physicalWarehouseId);
				if (!WorkerUtil.isNullOrEmpty(inventoryDao
						.selectLocationIsExist(searchMap))) {
					resMap.put("result", "failure");
					resMap.put("note", "已经存在此编码库位");
					return resMap;
				}

				Location location = new Location();
				location.setPhysical_warehouse_id(physicalWarehouseId);
				location.setLocation_barcode(location_barcode);
				location.setIs_delete("N");
				location.setLocation_type(locationType);
				location.setCreated_user(actionUser);
				location.setCustomer_id(customerId);
				inventoryDao.insertLocation(location);
			}

		} else if ("delete".equals(act)) {

			Map<String, Object> updateMap = new HashMap<String, Object>();
			updateMap.put("location_id", locationId);
			updateMap.put("is_delete", "Y");
			int col = inventoryDao.updateLocation(updateMap);
			if (col == 0) {
				resMap.put("result", "failure");
				resMap.put("note", "库位已有商品，不能删除");
				return resMap;
			}
		}

		resMap.put("result", "success");
		resMap.put("note", "success");
		return resMap;
	}

	@Override
	public InventoryItem findOneInventoryItemBySerialNumber(String serialNumber) {
		List<InventoryItem> inventoryItems = inventoryItemDao
				.selectBySerialNumber(serialNumber);
		InventoryItem inventoryItem = null;
		if (!WorkerUtil.isNullOrEmpty(inventoryItems)) {
			int i = 0; // 计数器
			for (InventoryItem inventoryItemTmp : inventoryItems) {
				if (inventoryItemTmp.getQuantity().intValue() > 1) {
					logger.error("串号商品库存查询异常，库存数量大于1!serialNumber: "
							+ serialNumber);
					throw new RuntimeException(
							"串号商品库存查询异常，库存数量大于1!serialNumber: " + serialNumber);
				} else if (inventoryItemTmp.getQuantity().intValue() == 1) {
					inventoryItem = inventoryItemTmp;
					i++;
				}
			}
			if (i > 1) {
				logger.error("串号商品库存查询异常，库存数量大于1!serialNumber: " + serialNumber);
				throw new RuntimeException("串号商品库存查询异常，库存数量大于1!serialNumber: "
						+ serialNumber);
			}
		}

		return inventoryItem;
	}

	@Override
	public HashMap<String, Object> create_tag(HttpServletRequest req) {

		HashMap<String, Object> resMap = new HashMap<String, Object>();

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		String action_user = ((SysUser) session.getAttribute("currentUser"))
				.getRealname();
		// Integer customerId = ((WarehouseCustomer)
		// session.getAttribute("currentCustomer")).getCustomer_id();

		Integer customerId = Integer.parseInt(req.getParameter("customerId")); // ps1:商品条码
		String goods_barcode = req.getParameter("barcode"); // ps2:商品条码
		// String action_user = req.getParameter("username"); //ps3:商品条码 *
		Integer physicalWarehouseId = ((Warehouse) session
				.getAttribute("currentPhysicalWarehouse"))
				.getPhysical_warehouse_id();// ps4:物理仓库校验 *
		Integer physicalWarehouseId2 = Integer.parseInt(req
				.getParameter("physical_warehouse_id"));
		if (physicalWarehouseId - physicalWarehouseId2 != 0) {
			resMap.put("success", false);
			resMap.put("result", "failure");
			resMap.put("note", "订单到货仓库与登录仓库不一致");
			return resMap;
		}
		String locationBarcode = WorkerUtil.generatorSequence( // ps5:标签条码(不需要从前台传)
				SequenceUtil.KEY_NAME_TAGCODE, "C", true);
		Integer productId = Integer.parseInt(req.getParameter("product_id")); // ps6:商品编号

		Integer dateSelect = Integer.parseInt(req.getParameter("dateSelect"));

		String validity = req.getParameter("validity"); // ps11:生产日期

		String batch_sn = req.getParameter("batch_sn");
		// 如果 dateSelect为1 表面是有效期
		if (dateSelect == 1) {
			Product product = productDao.selectByPrimaryKey(productId);
			if (product.getValidity() > 0) {
				DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date Date = dateFormat1.parse(validity);
					Calendar c = Calendar.getInstance();
					c.setTime(Date);
					if (product.getValidity_unit().equalsIgnoreCase("MONTH")) {
						c.set(Calendar.MONTH,
								c.get(Calendar.MONTH) - product.getValidity());
					} else {
						c.set(Calendar.DATE,
								c.get(Calendar.DATE) - product.getValidity());
					}

					validity = dateFormat1.format(c.getTime());
					if(c.after( Calendar.getInstance())){
						resMap.put("success", false);
						resMap.put("result", "failure");
						resMap.put("note", "生产日期不能大于当前时间");
						return resMap;
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

		}

		Integer warehouseId = Integer
				.parseInt(req.getParameter("warehouse_id")); // ps7:逻辑仓库
		String serialNo = req.getParameter("serial_no"); // ps8:串号
		Integer arriveNumber = Integer.parseInt(req // ps9:到货数量
				.getParameter("arrive_number"));
		Integer trayNumber = Integer.parseInt(req.getParameter("tray_number")); // ps10:托盘数量:1

		// String duiDie = req.getParameter("dui_die"); // ps12:堆叠方式
		String ti = req.getParameter("ti");
		String hi = req.getParameter("hi");
		Integer normalNumber = WorkerUtil.isNullOrEmpty(req // ps13:良品数量
				.getParameter("normal_number")) ? 0 : Integer.parseInt(req
				.getParameter("normal_number"));
		Integer defectiveNumber = WorkerUtil.isNullOrEmpty(req // ps14:不良品数量
				.getParameter("defective_number")) ? 0 : Integer.parseInt(req
				.getParameter("defective_number"));
		Integer orderId = Integer.parseInt(req.getParameter("order_id")); // ps15:订单编号
		Integer orderGoodsId = Integer.parseInt(req // ps16:订单商品标识
				.getParameter("order_goods_id"));

		String oms_order_sn = req.getParameter("oms_order_sn"); // ps17:采购单号
		String goods_name = req.getParameter("goods_name"); // ps18:商品名称

		Location location = new Location();
		location.setPhysical_warehouse_id(physicalWarehouseId);
		location.setLocation_barcode(locationBarcode);
		location.setLocation_type("PURCHASE_SEQ");
		location.setIs_delete("N");
		location.setCreated_user(action_user);
		location.setCustomer_id(customerId);
		inventoryDao.insertLocation(location);

		LabelAccept labelAccept = new LabelAccept();

		labelAccept.setLocation_id(location.getLocation_id());
		labelAccept.setLocation_barcode(locationBarcode);
		labelAccept.setCustomer_id(customerId);
		labelAccept.setPhysical_warehouse_id(physicalWarehouseId);
		labelAccept.setWarehouse_id(warehouseId);
		labelAccept.setIs_serial(!WorkerUtil.isNullOrEmpty(serialNo)
				&& !"".equals(serialNo) ? "Y" : "N");
		labelAccept.setProduct_id(productId);
		labelAccept.setSerial_no(serialNo);
		labelAccept.setGoods_barcode(goods_barcode);
		labelAccept.setArrive_number(arriveNumber);
		labelAccept.setTray_number(trayNumber);
		labelAccept.setBatch_sn(batch_sn);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		labelAccept.setBatch_sn(req.getParameter("product_sn"));

		Date date = null;
		OrderInfo orderInfo = orderDao.getOderInfoByIdForUpdate(Integer
				.valueOf(orderId));

		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("orderId", orderId);
		searchMap.put("orderGoodsId", orderGoodsId);
		Map returnMap = orderDao.selectPurchaseAcceptNum(searchMap);
		if (!WorkerUtil.isNullOrEmpty(returnMap)
				&& !WorkerUtil.isNullOrEmpty(returnMap.get("quantity"))
				&& (Integer.parseInt(returnMap.get("quantity").toString())
						+ normalNumber + defectiveNumber) > Integer
							.parseInt(returnMap.get("goods_number").toString())) {
			resMap.put("success", false);
			resMap.put("result", "failure");
			resMap.put("note", "收货数量超过实际采购数量");
			return resMap;
		}
		if (!WorkerUtil.isNullOrEmpty(serialNo)) {
			String serialArr[] = serialNo.split(",");
			if (!WorkerUtil.isNullOrEmpty(serialNo) && !"".equals(serialNo)
					&& serialArr.length != arriveNumber) {
				resMap.put("success", false);
				resMap.put("result", "failure");
				resMap.put("note", "到货数量和串号数量不一致");
				return resMap;
			}
		}
		try {
			if (!WorkerUtil.isNullOrEmpty(validity)) {
				date = sdf.parse(validity);
				labelAccept.setValidity(date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("No validity:", e);
			resMap.put("success", false);
			resMap.put("result", "failure");
			resMap.put("note", "生产日期错误");
			return resMap;
		}

		if (normalNumber > 0) {
			labelAccept.setQuantity(normalNumber);
			labelAccept.setStatus_id("NORMAL");
		}
		if (defectiveNumber > 0) {
			labelAccept.setQuantity(defectiveNumber);
			labelAccept.setStatus_id("DEFECTIVE");
		}
		// labelAccept.setPile(duiDie);
		labelAccept.setHi(hi);
		labelAccept.setTi(ti);
		labelAccept.setCreated_user(action_user);
		labelAccept.setLast_updated_user(action_user);
		labelAccept.setOrder_id(orderId);
		labelAccept.setOrder_goods_id(orderGoodsId);
		int col = inventoryDao.insertInventoryLocation(labelAccept);

		resMap.put("result", "success");
		resMap.put("locationBarcode", locationBarcode);
		resMap.put("normal_number", normalNumber);
		resMap.put("defective_number", defectiveNumber);
		resMap.put("validity", validity);
		resMap.put("order_goods_id", orderGoodsId);
		resMap.put("physical_warehouse_id", physicalWarehouseId);
		resMap.put("oms_order_sn", oms_order_sn);
		resMap.put("goods_name", goods_name);
		resMap.put("barcode", goods_barcode);
		resMap.put("action_user", action_user);
		resMap.put("success", true);
		resMap.put("batch_sn", batch_sn);
		return resMap;
	}

	/**
	 * wms库位管理二期
	 * 
	 * @author hzhang1 2016-05-14
	 */
	public List<Map> getLocationV2(Map map) {
		
		List<Map> list = new ArrayList<Map>();
		if("search".equals(map.get("type").toString())){
			list = inventoryDao.selectLocationV2ByPage(map);
		}else if("export".equals(map.get("type").toString())){
			list = inventoryDao.selectLocationV2(map);
		}
		for(Map temp : list){
			String tempLocationName = Location.LOCATION_TYPE_MAP.get(temp.get("location_type").toString()).toString();
			temp.put("location_type_name", tempLocationName);
		}
		return list;
	}

	public Map insertLocationV2(Location location, String type) {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		String locationBarcode = location.getLocation_barcode().toUpperCase().replace("-", "");
		
		String partition_id = "", aisle = "", bay = "", lev = "";
		Integer tempInt = null, pickSeq = null, putawaySeq = null;
		if(locationBarcode.length() != 7){
			 returnMap.put("result", "failure");
			 returnMap.put("note", "库位编码长度不正确!");
			 return returnMap;
		}else{
			partition_id = locationBarcode.substring(0, 1);
			char tempChar = partition_id.charAt(0);
			tempInt = (int) tempChar - 55;
			aisle = locationBarcode.substring(1, 3);
			bay = locationBarcode.substring(3, 5);
			lev = locationBarcode.substring(5, 7);
			location.setPartition_id(partition_id);
			location.setAisle(aisle);
			location.setBay(bay);
			location.setLev(lev);
		}
		location.setLocation_barcode(locationBarcode);
		Integer defaultSeq = tempInt
				* 1000000
				+ Integer.parseInt(locationBarcode.substring(1,
						locationBarcode.length()));
		pickSeq = location.getPick_seq();
		putawaySeq = location.getPutaway_seq();
		Map<String, Object> searchMap = new HashMap<String, Object>();
//		if (!WorkerUtil.isNullOrEmpty(pickSeq)
//				|| !WorkerUtil.isNullOrEmpty(putawaySeq)) {
//			searchMap.put("pickSeq", location.getPick_seq());
//			searchMap.put("putawaySeq", location.getPick_seq());
//			searchMap.put("physicalWarehouseId",
//					location.getPhysical_warehouse_id());
//			Map location_ = inventoryDao.selectSequenceIsExist(searchMap);
//			if (!WorkerUtil.isNullOrEmpty(location_)) {
//				if(!location_.get("pick_seq").toString().equals(pickSeq.toString()) || !location_.get("putaway_seq").toString().equals(putawaySeq.toString())){
//					returnMap.put("result", "failure");
//					returnMap.put("note", "拣货顺序或上架顺序维护有误");
//					return returnMap;
//				}
//			}
//		}
		if (WorkerUtil.isNullOrEmpty(pickSeq)) {
			location.setPick_seq(defaultSeq);
		}
		if (WorkerUtil.isNullOrEmpty(putawaySeq)) {
			location.setPutaway_seq(defaultSeq);
		}

		int col = 0;
		if ("insert".equals(type)) {
			// 1.先判断下改库位是否已经存在
			searchMap = new HashMap<String, Object>();
			// searchMap.put("customerId", customerId);
			searchMap.put("locationBarcode", locationBarcode);
			searchMap.put("physicalWarehouseId",
					location.getPhysical_warehouse_id());
			if (!WorkerUtil.isNullOrEmpty(inventoryDao
					.selectLocationIsExist(searchMap))) {
				returnMap.put("result", "failure");
				returnMap.put("note", "已经存在此编码库位");
				return returnMap;
			}

			if(Location.LOCATION_TYPE_RETURN_NORMAL.equals(location.getLocation_type())){
				if(!WorkerUtil.isNullOrEmpty(locationDao.selectAllLocationList(location.getPhysical_warehouse_id(), location.getLocation_type()))){
					returnMap.put("result", "failure");
					returnMap.put("note", "退货良品区只允许存在一个");
					return returnMap;
				}
			}else if(Location.LOCATION_TYPE_RETURN_DEFECTIVE.equals(location.getLocation_type())){
				if(!WorkerUtil.isNullOrEmpty(locationDao.selectAllLocationList(location.getPhysical_warehouse_id(), location.getLocation_type()))){
					returnMap.put("result", "failure");
					returnMap.put("note", "退货不良品区在物理仓只允许存在一个");
					return returnMap;
				}
			}
			col = inventoryDao.insertLocationV2(location);
		} else if ("update".equals(type)) {
			Location location2 = locationDao.selectLocationIdByLocationBarCode(location.getPhysical_warehouse_id(), location.getLocation_barcode(), null);
			if(!WorkerUtil.isNullOrEmpty(location2) && location2.getLocation_type() == location.getLocation_type()){
				returnMap.put("result", "failure");
				returnMap.put("note", "已存在相同编码相同类型的库位了");
				return returnMap;
			}
			col = inventoryDao.updateLocationV2(location);
		}

		if (col == 0) {
			returnMap.put("result", "failure");
			returnMap.put("note", "插入或更新库位异常");
		} else {
			returnMap.put("result", "success");
			returnMap.put("note", "插入或更新库位正确!");
		}
		return returnMap;
	}

	public Map deleteLocationV2(int loc_id) {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		int col = inventoryDao.deleteLocationV2(loc_id);
		if (col == 0) {
			returnMap.put("result", "failure");
			returnMap.put("note", "删除库位异常,不能删除该库位");
		} else {
			returnMap.put("result", "success");
			returnMap.put("note", "删除库位正确!");
		}
		return returnMap;
	}
	
	public Map recoverLocationV2(int loc_id) {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		Location location = locationDao.selectLocationById(loc_id);
		Location location2 = locationDao.selectLocationIdByLocationBarCode(location.getPhysical_warehouse_id(), location.getLocation_barcode(), null);
		if(!WorkerUtil.isNullOrEmpty(location2) && location2.getLocation_type().equals(location.getLocation_type())){
			returnMap.put("result", "failure");
			returnMap.put("note", "已存在相同编码相同类型的库位了");
			return returnMap;
		}
		
		int col = inventoryDao.recoverLocationV2(loc_id);
		if (col == 0) {
			returnMap.put("result", "failure");
			returnMap.put("note", "恢复库位异常,不能删除该库位");
		} else {
			returnMap.put("result", "success");
			returnMap.put("note", "恢复库位正确!");
		}
		return returnMap;
	}

	private static Map<String, Object> locTypeMap = null;
	static {
		locTypeMap = new HashMap<String, Object>();
		locTypeMap.put("存储区", "STOCK_LOCATION");
		locTypeMap.put("件拣货区", "PIECE_PICK_LOCATION");
		locTypeMap.put("箱拣货区", "BOX_PICK_LOCATION");
		locTypeMap.put("Return区", "RETURN_LOCATION");
		locTypeMap.put("质检区", "QUALITY_CHECK_LOCATION");
		locTypeMap.put("二手区", "DEFECTIVE_LOCATION");
		locTypeMap.put("耗材区", "PACKBOX_LOCATION");
	}

	private static ArrayList locArray = null;
	static {
		locArray = new ArrayList();
		locArray.add(0, "physical_warehouse_id");
		locArray.add(1, "customer_id");
		locArray.add(2, "location_barcode");
		locArray.add(3, "area");
		locArray.add(4, "location_type");
		locArray.add(5, "circle_class");
		locArray.add(6, "price_class");
		locArray.add(7, "can_mix_product");
		locArray.add(8, "can_mix_batch");
		locArray.add(9, "not_auto_recmd");
		locArray.add(10, "max_lpn_qty");
		locArray.add(11, "volume");
		locArray.add(12, "weight");
		locArray.add(13, "length");
		locArray.add(14, "width");
		locArray.add(15, "height");
		locArray.add(16, "axis_x");
		locArray.add(17, "axis_y");
		locArray.add(18, "axis_z");
		locArray.add(19, "max_prod_qty");
		locArray.add(20, "ignore_lpn");
		locArray.add(21, "putaway_seq");
		locArray.add(22, "pick_seq");
	}

	public Map uploadLocationV2(InputStream input, boolean isE2007,
			String actionUser, Map physicalWarehouseMap, Map customersMap) {

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse currentPhysicalWarehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");

		Map<String, Object> returnMap = new HashMap<String, Object>();
		int count = 0;
		String msg = "";
		try {
			Workbook wb = null; // 根据文件格式(2003或者2007)来初始化
			if (isE2007)
				wb = new XSSFWorkbook(input);
			else
				wb = new HSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0); // 获得第一个表单
			Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
			while (rows.hasNext()) {
				Row row = rows.next(); // 获得行数据
				if (row.getRowNum() == 0)
					continue;
				//System.out.println("Row #" + row.getRowNum()); // 获得行号从0开始
				Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
				int i = 0;
				JSONObject locJson = new JSONObject();
				while (cells.hasNext()) {
					Cell cell = cells.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					//System.out.println(cell.getCellType());
					//System.out.print("Cell #" + cell.getColumnIndex() + " -  "+ cell.getCellType() + " - "+ cell.getStringCellValue());
					switch (cell.getCellType()) { // 根据cell中的类型来输出数据
					case HSSFCell.CELL_TYPE_STRING:
						//System.out.println(i + " - "+ cell.getStringCellValue());
						if (cell.getColumnIndex() == 0) {
							locJson.put(
									locArray.get(cell.getColumnIndex()),
									physicalWarehouseMap.get(cell
											.getStringCellValue().toString()
											.trim()));
						} else if (cell.getColumnIndex() == 1) {
							locJson.put(
									locArray.get(cell.getColumnIndex()),
									customersMap.get(cell.getStringCellValue()
											.toString().trim()));
						} else if (cell.getColumnIndex() == 4) {
							locJson.put(
									locArray.get(cell.getColumnIndex()),
									locTypeMap.get(cell.getStringCellValue()
											.toString().trim()));
						} else {
							locJson.put(locArray.get(cell.getColumnIndex()),
									cell.getStringCellValue());
						}
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						// //System.out.print(cell.getNumericCellValue());
						locJson.put(locArray.get(cell.getColumnIndex()),
								cell.getNumericCellValue());
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						// //System.out.print(cell.getBooleanCellValue());
						locJson.put(locArray.get(cell.getColumnIndex()),
								cell.getBooleanCellValue());
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						// //System.out.print(cell.getCellFormula());
						locJson.put(locArray.get(cell.getColumnIndex()),
								cell.getCellFormula());
						break;
					default:
						// //System.out.print("unsuported sell type");
						locJson.put(locArray.get(cell.getColumnIndex()), "");
						break;
					}
				}

				Location l = (Location) JSONObject.toBean(locJson,
						Location.class);
				String partition_id = "", aisle = "", bay = "", lev = "", loc_code = l
						.getLocation_barcode().toUpperCase().replace("-", "");
				Integer tempInt = null, pickSeq = null, putawaySeq = null;
				if (loc_code.length() != 7) {
					returnMap.put("result", "failure");
					returnMap.put("note", "第" + (row.getRowNum() + 1)
							+ "行库位编码长度不正确!");
					returnMap.put("excel_note", "第" + (row.getRowNum() + 1)
							+ "行库位编码长度不正确!");
				} else {
					partition_id = loc_code.substring(0, 1);
					partition_id = loc_code.substring(0, 1);
					char tempChar = partition_id.charAt(0);
					tempInt = (int) tempChar - 55;
					aisle = loc_code.substring(1, 3);
					bay = loc_code.substring(3, 5);
					lev = loc_code.substring(5, 7);
					l.setPartition_id(partition_id);
					l.setAisle(aisle);
					l.setBay(bay);
					l.setLev(lev);
				}
				l.setLocation_barcode(loc_code);
				Integer defaultSeq = tempInt
						* 1000000
						+ Integer.parseInt(loc_code.substring(1,
								loc_code.length()));
				pickSeq = l.getPick_seq();
				putawaySeq = l.getPutaway_seq();
				Map<String, Object> searchMap = new HashMap<String, Object>();
//				if (!WorkerUtil.isNullOrEmpty(pickSeq)
//						|| !WorkerUtil.isNullOrEmpty(putawaySeq)) {
//					searchMap.put("pickSeq", l.getPick_seq());
//					searchMap.put("putawaySeq", l.getPick_seq());
//					searchMap
//							.put("physicalWarehouseId",
//									currentPhysicalWarehouse
//											.getPhysical_warehouse_id());
//					Map location_ = inventoryDao
//							.selectSequenceIsExist(searchMap);
//					if (!WorkerUtil.isNullOrEmpty(location_)) {
//						returnMap.put("result", "failure");
//						returnMap.put("note", "拣货顺序或上架顺序维护有误");
//						return returnMap;
//					}
//				}

				if (WorkerUtil.isNullOrEmpty(pickSeq)) {
					l.setPick_seq(defaultSeq);
				}else{
					l.setPick_seq(pickSeq);
				}
				if (WorkerUtil.isNullOrEmpty(putawaySeq)) {
					l.setPutaway_seq(defaultSeq);
				}else{
					l.setPutaway_seq(putawaySeq);
				}

				l.setPhysical_warehouse_id(currentPhysicalWarehouse
						.getPhysical_warehouse_id());
				l.setCreated_user(actionUser);
				l.setLast_updated_user(actionUser);
				l.setIs_delete("N");
				l.setIs_empty("Y");
				if(WorkerUtil.isNullOrEmpty(l.getLocation_type())){
					msg += "LocationBarcode:" + loc_code + "库位类型有错误.";
					continue;
				}
				searchMap = new HashMap<String, Object>();
				// searchMap.put("customerId", customerId);
				searchMap.put("locationBarcode", loc_code);
				searchMap.put("physicalWarehouseId",
						l.getPhysical_warehouse_id());
				if (!WorkerUtil.isNullOrEmpty(inventoryDao
						.selectLocationIsExist(searchMap))) {
					msg += "LocationBarcode:" + loc_code + "已存在只能更新.";
					inventoryDao.updateLocationV3(l);
					//continue;
				}else{
					inventoryDao.insertLocationV2(l);
				}
				count++;
			}
		} catch (Exception ex) {
			logger.error("UploadLocationV2 error:", ex);
			returnMap.put("result", "failure");
			returnMap.put("note", "已经导入" + count + "个库位，第" + (count + 1)
					+ "个错误原因" + ex.getMessage() + msg);
			returnMap.put("excel_note", "已经导入" + count + "个库位，第" + (count + 1)
					+ "个错误原因" + ex.getMessage() + msg);
		}
		returnMap.put("result", "success");
		returnMap.put("note", "已经导入" + count + "个库位" + msg);
		returnMap.put("excel_note", "已经导入" + count + "个库位" + msg);
		return returnMap;
	}

	
	private static ArrayList inventoryArray = null;
	static {
		inventoryArray = new ArrayList();
		inventoryArray.add(0, "customer_id");
		inventoryArray.add(1, "physical_warehouse_id");
		inventoryArray.add(2, "product_barcode");
		inventoryArray.add(3, "location_barcode");
		inventoryArray.add(4, "quantity");
		inventoryArray.add(5, "validity");
		inventoryArray.add(6, "guarantee_validity");
	}
	public Map uploadInventory(InputStream input, boolean isE2007,
			String actionUser, Map physicalWarehouseMap, Map customersMap) {

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse currentPhysicalWarehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");

		List<TmpProductLocation> tmpProductLocationList = new ArrayList<TmpProductLocation>();
		List<String> productBatchList = new ArrayList<String>();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		int count = 0;
		String msg = "";
		try {
			Workbook wb = null; // 根据文件格式(2003或者2007)来初始化
			if (isE2007)
				wb = new XSSFWorkbook(input);
			else
				wb = new HSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0); // 获得第一个表单
			Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
			while (rows.hasNext()) {
				Row row = rows.next(); // 获得行数据
				if (row.getRowNum() == 0)
					continue;
				Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
				int i = 0;
				JSONObject inventoryJson = new JSONObject();
				while (cells.hasNext()) {
					Cell cell = cells.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					logger.info("Cell #" + cell.getColumnIndex() + " -  " + cell.getCellType() + " - " + cell.getStringCellValue());
					switch (cell.getCellType()) { 
					case HSSFCell.CELL_TYPE_STRING:
						if (cell.getColumnIndex() == 1) {
							inventoryJson.put(
									inventoryArray.get(cell.getColumnIndex()),
									physicalWarehouseMap.get(cell
											.getStringCellValue().toString()
											.trim()));
							//System.out.println(physicalWarehouseMap.get(cell.getStringCellValue().toString().trim()));
						} else if (cell.getColumnIndex() == 0) {
							inventoryJson.put(
									inventoryArray.get(cell.getColumnIndex()),
									customersMap.get(cell.getStringCellValue()
											.toString().trim()));
							//System.out.println(customersMap.get(cell.getStringCellValue().toString().trim()));
						} else {
							inventoryJson.put(inventoryArray.get(cell.getColumnIndex()),
									cell.getStringCellValue());
						}
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						inventoryJson.put(inventoryArray.get(cell.getColumnIndex()),
								cell.getNumericCellValue());
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						inventoryJson.put(inventoryArray.get(cell.getColumnIndex()),
								cell.getBooleanCellValue());
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						inventoryJson.put(inventoryArray.get(cell.getColumnIndex()),
								cell.getCellFormula());
						break;
					default:
						inventoryJson.put(inventoryArray.get(cell.getColumnIndex()), "");
						break;
					}
				}
				count++;
				TmpProductLocation tmpProductLocation = (TmpProductLocation) JSONObject.toBean(inventoryJson,
						TmpProductLocation.class);
				tmpProductLocation.setLocation_barcode(tmpProductLocation.getLocation_barcode().toUpperCase().trim().replace("-", ""));
				if (WorkerUtil.isNullOrEmpty(tmpProductLocation
						.getCustomer_id())
						&& WorkerUtil.isNullOrEmpty(tmpProductLocation
								.getPhysical_warehouse_id())
						&& WorkerUtil.isNullOrEmpty(tmpProductLocation
								.getProduct_barcode())
						&& WorkerUtil.isNullOrEmpty(tmpProductLocation
								.getLocation_barcode())) {
					continue;
				}
				if(WorkerUtil.isNullOrEmpty(tmpProductLocation.getCustomer_id())){
					msg += "第"+(count+1)+"行货主为空";
					continue;
				}
				if(WorkerUtil.isNullOrEmpty(tmpProductLocation.getPhysical_warehouse_id())){
					msg += "第"+(count+1)+"行物理仓为空";
					continue;
				}
				if(WorkerUtil.isNullOrEmpty(tmpProductLocation.getProduct_barcode())){
					msg += "第"+(count+1)+"行商品条码为空";
					continue;
				}
				if(WorkerUtil.isNullOrEmpty(tmpProductLocation.getLocation_barcode())){
					msg += "第"+(count+1)+"行库位条码为空";
					continue;
				}
				Product product = productDao.selectProductByBarcodeCustomer(tmpProductLocation.getProduct_barcode(), tmpProductLocation.getCustomer_id());
				if(WorkerUtil.isNullOrEmpty(product)){
					msg += "第"+(count+1)+"行"+tmpProductLocation.getProduct_barcode()+"不存在该商品";
					continue;
				}
				Location location = locationDao.selectLocationIdByLocationBarCode(tmpProductLocation.getPhysical_warehouse_id(),tmpProductLocation.getLocation_barcode(),null);
				if(WorkerUtil.isNullOrEmpty(location)){
					msg += "第"+(count+1)+"行"+tmpProductLocation.getLocation_barcode()+"不存在该库位";
					continue;
				}
				
				if(WorkerUtil.isNullOrEmpty(tmpProductLocation.getValidity())){
					if(WorkerUtil.isNullOrEmpty(tmpProductLocation.getGuarantee_validity())){
						msg += "第"+(count+1)+"行生产日期和过保日期都为空，请至少维护一个";
						continue;
					}else{
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						try {
							String temp = tmpProductLocation.getGuarantee_validity();
							Date date =  sdf.parse(temp.substring(0, 4)+"-"+temp.substring(4, 6)+"-"+temp.substring(6, 8));
							int validity = product.getValidity();
							String validity_unit = product.getValidity_unit();
							
							Calendar cal = Calendar.getInstance();
							cal.setTime(date); 
							if("MONTH".equals(validity_unit)){
								cal.add(Calendar.MONTH, -validity);
							}else if("DAY".equals(validity_unit)){
								cal.add(Calendar.DATE, -validity);
							}
							date=cal.getTime();    
							tmpProductLocation.setValidity(sdf.format(date));
						} catch (Exception e) {
							logger.info("第"+(count+1)+"行过期日期格式有误");
							msg += "第"+(count+1)+"行过期日期格式有误";
							continue;
						}
					}
				}else{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						String temp = tmpProductLocation.getValidity();
						Date date =  sdf.parse(temp.substring(0, 4)+"-"+temp.substring(4, 6)+"-"+temp.substring(6, 8));
						tmpProductLocation.setValidity(temp.substring(0, 4)+"-"+temp.substring(4, 6)+"-"+temp.substring(6, 8));
					} catch (Exception e) {
						logger.info("第"+(count+1)+"行生产日期格式有误");
						msg += "第"+(count+1)+"行生产日期格式有误";
						continue;
					}
				}
				
//				Map<String,Object> map_ = new HashMap<String, Object>();
//				map_.put("locationId", location.getLocation_id());
//				List<Map> isMixMap = replenishmentDao.selectLocationIsCanMix(map_);
//				if (WorkerUtil.isNullOrEmpty(isMixMap)) {
//					msg += "查找不到目标库位";
//					continue;
//				} else {
//					boolean flag = true;
//					for(Map map :isMixMap){
//						String product_id2 = WorkerUtil.isNullOrEmpty(map.get("product_id")) ? "" : map.get("product_id").toString();
//						String validity2 = WorkerUtil.isNullOrEmpty(map.get("validity")) ? "" : map.get("validity").toString();
//						String status2 = WorkerUtil.isNullOrEmpty(map.get("status")) ? "" : map.get("status").toString();
//						if (!"".equals(product_id2)) {
//							String can_mix_product = map.get("can_mix_product").toString();
//							String can_mix_batch = map.get("can_mix_batch") .toString();
//							if ("0".equals(can_mix_product) && !product_id2.equals(tmpProductLocation.getProduct_id())) {
//								msg += "目标库位不允许混放商品";
//								flag = false;
//								break;
//							} else if ("0".equals(can_mix_batch)
//									&& (!validity2.equals(tmpProductLocation.getValidity()) || !status2.equals(tmpProductLocation.getStatus()))) {
//								msg += "目标库位不允许混放批次";
//								flag = false;
//								break;
//							}
//						}
//					}
//					
//					if(Boolean.FALSE.equals(flag)){
//						continue;
//					}
//				}
				tmpProductLocation.setProduct_id(product.getProduct_id());
				tmpProductLocation.setLocation_id(location.getLocation_id());
				if(Location.LOCATION_TYPE_DEFECTIVE.equals(location.getLocation_type())){
					tmpProductLocation.setStatus("DEFECTIVE");
				}else{
					tmpProductLocation.setStatus("NORMAL");
				}
				
				String temp = tmpProductLocation.getProduct_id()+"_"+tmpProductLocation.getLocation_id()+"_"+tmpProductLocation.getValidity()+"_"+tmpProductLocation.getStatus();
				if(productBatchList.contains(temp)){
					msg += "第"+(count+1)+"行该商品批次已存在";
					continue;
				}
				productBatchList.add(temp);
				tmpProductLocation.setSerial_number("");
				tmpProductLocation.setCreated_user(actionUser);
				tmpProductLocation.setLast_updated_user(actionUser);
				tmpProductLocation.setTransfer_status("N");
				tmpProductLocationList.add(tmpProductLocation);
			}
			if(!WorkerUtil.isNullOrEmpty(tmpProductLocationList) && WorkerUtil.isNullOrEmpty(msg)){
				tmpProductLocationDao.insertList(tmpProductLocationList);
				returnMap.put("result", "success");
				returnMap.put("note", "已经导入" + count + "个库位库存" + msg);
				returnMap.put("excel_note", "已经导入" + count + "个库位库存" + msg);
			}else{
				returnMap.put("result", "success");
				returnMap.put("note", "已经导入0个库位库存" + msg);
				returnMap.put("excel_note", "已经导入0个库位库存" + msg);
			}
		} catch (Exception ex) {
			logger.error("UploadLocationV2 error:", ex);
			returnMap.put("result", "failure");
			returnMap.put("note", "已经导入0个库位库存" + ex.getMessage() + msg);
			returnMap.put("excel_note", "已经导入0个库位库存" + ex.getMessage() + msg);
		}
		return returnMap;
	}
	
	@Override
	public int send_sure(Integer physical_warehouse_id, String action_user, OrderInfo orderInfo,OrderGoods ordergoods) {

		List<LabelAccept> list=inventoryDao.getLabelAcceptListByOrderId(orderInfo.getOrder_id());
		
		if(!WorkerUtil.isNullOrEmpty(list)){
			for(LabelAccept labelAccept:list){
				Task task = new Task();

				task.setPhysical_warehouse_id(physical_warehouse_id);
				task.setCustomer_id(orderInfo.getCustomer_id());
				task.setTask_type(Task.TASK_TYPE_PUT_AWAY);
				task.setTask_status(Task.TASK_STATUS_INIT);
				task.setTask_level(1);
				task.setProduct_id(ordergoods.getProduct_id());
				task.setQuantity(labelAccept.getArrive_number());
				task.setBatch_pick_id(0);
				task.setBatch_task_id(0);
				task.setOperate_platform("RF");
				task.setCreated_user(action_user);
				task.setLast_updated_user(action_user);
				taskDao.insert(task);
				
				labelAccept.setTask_id(task.getTask_id());
			}
		}
		UserActionOrder userActionOrder = new UserActionOrder();
		userActionOrder.setOrder_id(orderInfo.getOrder_id());
		userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_ON_SHIP);
		userActionOrder.setAction_type("FINISH_RECEIVE");
		userActionOrder.setAction_note("采购订单验收完成");
		userActionOrder.setCreated_user(action_user);
		userActionOrder.setCreated_time(new Date());
		userActionOrderDao.insert(userActionOrder);

		inventoryDao.updateTaskIdByList(list);
		
		// 将状态更新为待上架
		return orderInfoDao.updateOrderInfoInProcess(orderInfo.getOms_order_sn(), action_user,
				OrderInfo.ORDER_STATUS_ON_SHIP);
	}

	@Override
	public void updateOrderInfoInProcess(String oms_order_sn) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		String action_user = ((SysUser) session.getAttribute("currentUser"))
				.getRealname();
		orderInfoDao.updateOrderInfo2InProcess(oms_order_sn, action_user,
				OrderInfo.ORDER_STATUS_IN_PROCESS);

	}
	
	/**
	 * 退货入库商品信息查询
	 * hchen1
	 */
	public Map<String,Object> selectBybarcode(Integer order_id,String barcode,Integer customer_id,Integer physical_warehouse_id){
		Map<String, Object> searchMap = new HashMap<String, Object>();
		Map<String, Object> resMap = new HashMap<String, Object>();
		Map<String, Object> searchMap3= new HashMap<String, Object>();
		Integer finishNumber = 0;
		Integer unGroundingNumber = 0;
		searchMap.put("order_id", order_id);
		searchMap.put("barcode", barcode);
		searchMap.put("customer_id", customer_id);
		Map map = inventoryDao.selectBybarcode(searchMap);
		if (WorkerUtil.isNullOrEmpty(map)) {
			logger.info("selectBybarcode barcode is null");
			resMap.put("success", false);
			resMap.put("note", "搜索到的商品条码为空");
			return resMap;
		}
		finishNumber = productLocationDetailDao.selectProductLocationDetailByOrderId(order_id, barcode, customer_id);
		if(WorkerUtil.isNullOrEmpty(finishNumber)){
			finishNumber= 0;
		}
		Integer goodsNumber = orderGoodsDao.selectGoodsNumber(order_id, Integer.parseInt(map.get("product_id").toString()));
		unGroundingNumber = goodsNumber -finishNumber;
		map.put("finishNumber", finishNumber); 
		map.put("unGroundingNumber", unGroundingNumber); 
		searchMap3.put("order_id", order_id);
		searchMap3.put("product_id", map.get("product_id").toString());
		searchMap3.put("physical_warehouse_id", physical_warehouse_id);
		Map<String, Object> validityMap =  productLocationDao.selectValidityByOrderId(searchMap3);
		String validity = validityMap.get("validity").toString();
		String batchSn = validityMap.get("batch_sn").toString();
		map.put("validity", validity); 
		map.put("batchSn", batchSn); 
		
		searchMap.put("productId", map.get("product_id").toString());
		searchMap.put("physicalWarehouseId", physical_warehouse_id);
		List<Task> taskList = taskDao.getTaskIdByOrderProcess(searchMap);
		if(WorkerUtil.isNullOrEmpty(taskList)){
			resMap.put("success",false);
			resMap.put("note","该订单并没有生成拣货下架任务");
			return resMap;
		}
//		 先获取是否有相同的商品并且批次相同的库位，再筛选空库位,前提是商品为良品。
		logger.info("推荐良品库位");
		 Map<String,Object> searchMap1 = new HashMap<String,Object>();
		 searchMap1.put("product_id", map.get("product_id").toString());
		 searchMap1.put("warehouse_id",map.get("warehouse_id").toString());
		 searchMap1.put("validity",validity);
		 searchMap1.put("batchSn",batchSn);
		 searchMap1.put("physical_warehouse_id",physical_warehouse_id);
		 List<String> locationList1 =inventoryDao.selectLocationByProductValidityNormal(searchMap1);
		 logger.info("良品库位"+locationList1);
		 if(!WorkerUtil.isNullOrEmpty(locationList1)){
			 map.put("location_barcode_normal", locationList1.get(0).toString()); 
		 }else{
			 List<String> locationList2 = inventoryDao.selectLocationByCanMixBatchNormal(physical_warehouse_id,map.get("product_id").toString());
			 logger.info("良品库位"+locationList2);
			 if(!WorkerUtil.isNullOrEmpty(locationList2)){
				 map.put("location_barcode_normal", locationList2.get(0).toString());
			 }else {
				 Map<String, Object> searchMap2 = new HashMap<String, Object>();
					searchMap2.put("warehouse_id", map.get("warehouse_id").toString());
					searchMap2.put("physical_warehouse_id", physical_warehouse_id.toString());
					searchMap2.put("location_type", Location.LOCATION_TYPE_RETURN);
					List<String> locationList3 = inventoryDao.selectLocationByEmpty(searchMap2);
					logger.info("良品库位"+locationList3);
					if(!WorkerUtil.isNullOrEmpty(locationList3)){
						map.put("location_barcode_normal", locationList3.get(0).toString());
					}else {
						map.put("location_barcode_normal", "");
					}
			}
		 }
//		 先获取是否有相同的商品并且批次相同的库位，再筛选空库位,前提是商品为非良品。
		 logger.info("推荐不良品库位");
		  locationList1 =inventoryDao.selectLocationByProductValidityDefective(searchMap1);
		  logger.info("不良品库位"+locationList1);
		 if(!WorkerUtil.isNullOrEmpty(locationList1)){
			 map.put("location_barcode_defective", locationList1.get(0).toString()); 
		 }else{
			 List<String> locationList2 = inventoryDao.selectLocationByCanMixBatchDefective(physical_warehouse_id,map.get("product_id").toString());
			 logger.info("不良品库位"+locationList2);
			 if(!WorkerUtil.isNullOrEmpty(locationList2)){
				 map.put("location_barcode_defective", locationList2.get(0).toString());
			 }else {
				 Map<String, Object> searchMap2 = new HashMap<String, Object>();
					searchMap2.put("warehouse_id", map.get("warehouse_id").toString());
					searchMap2.put("physical_warehouse_id", physical_warehouse_id.toString());
				    searchMap2.put("locationType", Location.LOCATION_TYPE_QUALITY_CHECK);
					List<String> locationList3 = inventoryDao.selectLocationByEmpty(searchMap2);
					logger.info("不良品库位"+locationList3);
					if(!WorkerUtil.isNullOrEmpty(locationList3)){
						map.put("location_barcode_defective", locationList3.get(0).toString());
					}else {
						map.put("location_barcode_defective", "");
					}
			}
		 }

		map.put("success", true);

		return map;
	}
	
	/**
	 * 退货入库上架
	 * hchen1
	 */
	@Override
	public Map<String,Object> createReturnAccept(Map<Object,Object> map){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> searchMap1 = new HashMap<String, Object>();
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("orderId", map.get("orderId"));
		searchMap.put("orderGoodsId", map.get("orderGoodsId"));
		searchMap.put("barcode", map.get("barcode"));
		
		//根据orderid等信息查询相关信息和一些参数的基本校验
		Map<String, Object> cancelOrderMap = orderDao.selectCancelOrderInfoByGoods(searchMap);
		if(WorkerUtil.isNullOrEmpty(cancelOrderMap)){
			resultMap.put("success",false);
			resultMap.put("note","不存在的cancelOrderMap");
			return resultMap;
		}
		
		Integer quantity = (Integer)map.get("quantity") ;
		if(quantity==0){
			resultMap.put("success",false);
			resultMap.put("note","输入的商品数量不能为0");
			return resultMap;
		}
		Integer productId = Integer.parseInt(cancelOrderMap.get("product_id").toString());
		String isSerial = cancelOrderMap.get("is_serial").toString();
		String serialNos = "";
		Integer numQuantity =0;
		Integer warehouse_id = Integer.parseInt(cancelOrderMap.get("warehouse_id").toString());
		List<Map<String, Object>> ordergoodsList = orderDao.selectOrderGoods( Integer.parseInt(map.get("orderId").toString()), map.get("barcode").toString(), Integer.parseInt(cancelOrderMap.get("customer_id").toString()));
		for (Map<String, Object> map2 : ordergoodsList) {
			numQuantity = numQuantity+Integer.parseInt(map2.get("goods_number").toString());
		}
		String orderId = map.get("orderId").toString();
		String actionUser = map.get("actionUser").toString();
		Integer customerId = (Integer)cancelOrderMap.get("customer_id");
		String orderGoodsId = null;
		
		Integer changeQuantity = productLocationDetailDao.selectProductLocationDetailByOrderId(Integer.parseInt(map.get("orderId").toString()), map.get("barcode").toString(), Integer.parseInt(cancelOrderMap.get("customer_id").toString()));
		if(WorkerUtil.isNullOrEmpty(changeQuantity)){
			changeQuantity = 0;
		}
		if(Integer.parseInt(map.get("quantity").toString())>numQuantity){
			resultMap.put("success", false);
			resultMap.put("result", "failure");
			resultMap.put("note", "输入的商品数量不能大于系统订单的数量");
			return resultMap;
		}
		if(quantity+changeQuantity>numQuantity){
			resultMap.put("success",false);
			resultMap.put("note","该订单商品上架数量已超过该订单的商品数量");
			return resultMap;
		}
		Map<String, Object> map_ = new HashMap<String, Object>();
		map_.put("locationBarcode", map.get("locationBarcode"));
		map_.put("physicalWarehouseId", map.get("physical_warehouse_id"));
		//取消上架只能退到return区和件拣货区,质检区
		Location location = inventoryDao.selectLocationByCodeV1(map_);
		if (WorkerUtil.isNullOrEmpty(location)) {
			resultMap.put("success", false);
			resultMap.put("result", "failure");
			resultMap.put("note", "库位不存在");
			return resultMap;
		}
		
		List<OrderGoods> orderGoodslList =null;
		//校验库位信息的准确性  根据库位推荐原则
		resultMap = validateLocation(location.getLocation_id(), customerId, productId, map.get("validity").toString(), map.get("batchSn").toString(),orderGoodslList,warehouse_id);
		if(!WorkerUtil.isNullOrEmpty(resultMap)){
			return resultMap;
		}
		
		try {
		//首先扣减中转区的库存  对相同的商品有多条记录的进行拆分，记录不同的order_goods_id
		Integer task_id = null;
		String validity = null;
		String batch_sn = null;
		int num = quantity; // 本商品需入库总数量
		int num1=0;
		int num2 = 0;
		int inNum = 0; // 每次需入库数量
		ProductLocation productLocation = new ProductLocation();
		for (Map<String, Object> map2 : ordergoodsList) {
			Integer productlocationdetailtotal=0;//用于记录该商品已上架的数量
			productlocationdetailtotal = productLocationDetailDao.selectProductLocationDetailByOrderIdV1(Integer.parseInt(map2.get("order_id").toString()),Integer.parseInt(map2.get("order_goods_id").toString()));
			if(WorkerUtil.isNullOrEmpty(productlocationdetailtotal)){
				productlocationdetailtotal=0;
			}
			if(Integer.parseInt(map2.get("goods_number").toString())+productlocationdetailtotal <=0){
				continue;
			}
			else if(num<=0){
				break;
			}
			else if(num<=Integer.parseInt(map2.get("goods_number").toString())+productlocationdetailtotal){
				num1=num;
				num2 = num1;
				num =num-num1;
				orderGoodsId = map2.get("order_goods_id").toString();
			}else{
				num1=Integer.parseInt(map2.get("goods_number").toString())+productlocationdetailtotal;
				num2 = num1;
				num =num-num1; 
				orderGoodsId = map2.get("order_goods_id").toString();
			}
			searchMap1.put("order_id", map.get("orderId"));
			searchMap1.put("physicalWarehouseId", map.get("physical_warehouse_id"));
			searchMap1.put("productId",cancelOrderMap.get("product_id").toString());
			List<Task> taskList = taskDao.getTaskIdByOrderProcess(searchMap1);
			logger.info("开始扣减中转区库存");
			Integer pl_id =null;
		
				//拆分task依次进行扣减
				for (Task task : taskList) {
					ProductLocation productLocation1 = productLocationDao.getproduLocations(task.getTask_id(),task.getTo_pl_id());
					validity = productLocation1.getValidity();
					batch_sn = productLocation1.getBatch_sn();
					Integer qty_total = productLocation1.getQty_total();
					serialNos= productLocation1.getSerial_number();
					warehouse_id = productLocation1.getWarehouse_id();
					if(qty_total ==0){
						continue;
					}
					else if(num1<=0){
						break;
					}
					else if(num1<=qty_total){
						inNum = -1*num1;
						num1 = 0;
					}else{
						inNum = -1*qty_total;
						num1 = num1-qty_total;
					}
					task_id = task.getTask_id();
					//更新中转区库存值
					productLocationDao.updateProductLocationTotal(inNum, productLocation1.getPl_id());
					ProductLocationDetail productLocationDetail = new ProductLocationDetail();
					productLocationDetail.setPl_id(productLocation1.getPl_id());
					productLocationDetail.setChange_quantity(inNum);
					productLocationDetail.setTask_id(task_id);
					productLocationDetail.setDescription("订单取消扣减中转区库存");
					productLocationDetail.setCreated_user(actionUser);
					productLocationDetail.setLast_updated_user(actionUser);
					productLocationDetail.setOrder_id(Integer.parseInt(map.get("orderId").toString()));
					productLocationDetail.setOrder_goods_id(Integer.parseInt(orderGoodsId));
					productLocationDetailDao.insert(productLocationDetail);		
				
		
				// 3.取消上架更新库存信息，插入Product_location
					Map<String, Object> paramsForPlMap = new HashMap<String, Object>();
					paramsForPlMap.put("productId", productId);
					paramsForPlMap.put("status", map.get("status").toString());
					paramsForPlMap.put("validity", validity);
					paramsForPlMap.put("batchSn", batch_sn);
					paramsForPlMap.put("warehouseId", warehouse_id);
					if(WorkerUtil.isNullOrEmpty(serialNos)){
					   paramsForPlMap.put("serialNumber", "");
					}else {
					   paramsForPlMap.put("serialNumber", serialNos);
					}
					paramsForPlMap.put("locationId", location.getLocation_id());
					productLocation = inventoryDao.selectProductLocation(paramsForPlMap);
					// 4.更新订单状态,只有该退货订单里面所有商品都上架的商品才会更新订单状态
					logger.info("orderid : "+orderId + "ordergoodsid: "+orderGoodsId + "num: "+inNum*-1);
					
					if (WorkerUtil.isNullOrEmpty(productLocation)) {
						//Product product = productDao.selectByPrimaryKey(Integer.parseInt(map2.get("product_id").toString()));
						productLocation = new ProductLocation();
						productLocation.setProduct_id(productId);
						productLocation.setLocation_id(location.getLocation_id());
						productLocation.setQty_total(inNum*-1);
						productLocation.setQty_available(inNum*-1);
						productLocation.setWarehouse_id(warehouse_id);
						productLocation.setProduct_location_status("NORMAL");
						//productLocation.setQty_exception(0);
						productLocation.setStatus(map.get("status").toString());
						if (!WorkerUtil.isNullOrEmpty(validity)) {
							productLocation.setValidity(validity.toString());
						}
						this.setValidityStatus(Integer.parseInt(map2.get("product_id").toString()), productLocation, validity);
						
						if(!WorkerUtil.isNullOrEmpty(batch_sn)){
							productLocation.setBatch_sn(batch_sn);
						}else {
							productLocation.setBatch_sn("");
						}
						if(WorkerUtil.isNullOrEmpty(serialNos)){
							productLocation.setSerial_number("");
						}else {
							productLocation.setSerial_number(serialNos);
						}
						productLocation.setCreated_user(actionUser);
						productLocation.setLast_updated_user(actionUser);
						productLocationDao.insert(productLocation);
					} else {
						logger.info("查询取消订单的上架库位信息");
						if(!"NORMAL".equals(productLocation.getProduct_location_status())){
							throw new RuntimeException("该库位异常,不允许上架");
						}
						productLocationDao.updateProductLocation(inNum*-1,
								productLocation.getPl_id());
					}
						productLocationDetail = new ProductLocationDetail();
						productLocationDetail.setPl_id(productLocation.getPl_id());
						productLocationDetail.setChange_quantity(inNum*-1);
						productLocationDetail.setTask_id(task_id);
						productLocationDetail.setDescription("订单取消上架");
						productLocationDetail.setCreated_user(actionUser);
						productLocationDetail.setLast_updated_user(actionUser);
						productLocationDetail.setOrder_id(Integer.parseInt(map.get("orderId").toString()));
						productLocationDetail.setOrder_goods_id(Integer.parseInt(orderGoodsId));
						productLocationDetailDao.insert(productLocationDetail);
						if("Y".equals(location.getIs_empty())){
							locationDao.updateLocationIsEmpty1(location.getLocation_id());
						}
		
				}
		if (num1==0) {
			orderBiz.insertUserActionOrder(Integer.parseInt(orderId), OrderInfo.ORDER_STATUS_CANCEL,actionUser);
		}else{
			throw new RuntimeException("该商品没有全部上架");
		}
		} 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("上架失败", e);
			resultMap.put("success", false);
			resultMap.put("note", e.getMessage());
			throw new RuntimeException("上架失败,原因" + e.getMessage());
		}
		resultMap.put("result", "success");
		resultMap.put("success", true);
		resultMap.put("note", "success");
		return resultMap;
	 }
	// 根据SKU查询库存
	public Map<String,Object> getSkuInventory(Integer physicalWarehouseId,Integer customerId,String barcode,String flag){
		Map<String,Object> resMap = new HashMap<String,Object>();
		List<Map> skuInventoryList = inventoryDao.selectSkuInventory(physicalWarehouseId,customerId,barcode,flag);
		List<Map> skuInventoryList2 = inventoryDao.selectSkuInventoryInTransit(physicalWarehouseId,customerId,barcode,flag);
		skuInventoryList.addAll(skuInventoryList2);
		if(WorkerUtil.isNullOrEmpty(skuInventoryList)){
			resMap.put("skuInventoryList", null);
			resMap.put("barcode", barcode);
			resMap.put("goodsName", null);
			resMap.put("allNum", 0);
			resMap.put("result", Response.FAILURE);
			resMap.put("success", Boolean.FALSE);
			resMap.put("note", "根据SKU获取到的库存为空");
		}else{
			Integer allNum = 0;
			String productName = "";
			List<Map> customerMap = new ArrayList<Map>(); 
			List<String> customers = new ArrayList<String>();
			resMap.put("skuInventoryList", skuInventoryList);
			for(Map map : skuInventoryList){
				productName = map.get("product_name").toString();
				allNum += WorkerUtil.isNullOrEmpty(map.get("qty_total"))?0:Integer.parseInt(map.get("qty_total").toString().trim());
				if(!customers.contains(map.get("customer_id").toString())){
					Map<String,Object> tempMap = new HashMap<String,Object>();
					tempMap.put("customer_name",map.get("name").toString());
					tempMap.put("customer_id", map.get("customer_id").toString());
					customerMap.add(tempMap);
					customers.add(map.get("customer_id").toString());
				}
			}
			resMap.put("barcode", barcode);
			resMap.put("productName", productName);
			resMap.put("customerList", customerMap);
			resMap.put("allNum", allNum);
			resMap.put("result", Response.SUCCESS);
			resMap.put("success", Boolean.TRUE);
			resMap.put("note", "成功获取到SKU的库存");
		}
		return resMap;
	}
	
	// 根据LOCATION查询库存
	public Map<String,Object> getLocationInventory(Integer physicalWarehouseId,String locationBarcode,String flag){
		Map<String,Object> resMap = new HashMap<String,Object>();
		List<Map> locationInventoryList= inventoryDao.selectLocationInventory(physicalWarehouseId,locationBarcode,flag);
		if(WorkerUtil.isNullOrEmpty(locationInventoryList)){
			resMap.put("locationInventoryList", null);
			resMap.put("kindNum", 0);
			resMap.put("locationType", "");
			resMap.put("locationBarcode", locationBarcode);
			resMap.put("result", Response.FAILURE);
			resMap.put("success", Boolean.FALSE);
			resMap.put("note", "根据LOCATION获取到的库存为空");
		}else{
			Integer kindNum = 0;
			Integer allNum = 0;
			String locationType = "";
			List<String> barcodeList = new ArrayList<String>();
			for(Map map : locationInventoryList){
				String barcode = map.get("barcode").toString();
				allNum += WorkerUtil.isNullOrEmpty(map.get("qty_freeze"))?0:Integer.parseInt(map.get("qty_freeze").toString().trim());
				locationType = map.get("location_type").toString();
				if(!barcodeList.contains(barcode)){
					++ kindNum;
				}
			}
			resMap.put("locationInventoryList", locationInventoryList);
			resMap.put("kindNum", kindNum);
			resMap.put("allNum", allNum);
			resMap.put("locationType", locationType);
			resMap.put("locationBarcode", locationBarcode);
			resMap.put("result", Response.SUCCESS);
			resMap.put("success", Boolean.TRUE);
			resMap.put("note", "成功获取到LOCATION的库存");
		}
		return resMap;
	}
	
	// 根据SKU冻结库存
	public Map<String,Object> freezeSkuInventory(Integer physicalWarehouseId,Integer customerId,String barcode,Integer plId,Integer quantity,String actionUser){
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		ReentrantLock lock = LockUtil.getReentrantLock("physicalWarehouseId_"+ physicalWarehouseId + "_customerId_" + customerId);
		lock.lock();
		try {
				
			// 1.标记库位库存异常
			int col = productLocationDao.updateProductLocationForFreezeAvailable(quantity , plId);
			if( col < 1){
				throw new RuntimeException("标记库位库存失败");
			}
			
			// 2.插入冻结日志
			ProductLocationFreezeDetail productLocationFreezeDetail = new ProductLocationFreezeDetail();
			productLocationFreezeDetail.setPl_id(plId);
			productLocationFreezeDetail.setType(ProductLocationFreezeDetail.TYPE_FREEZE);
			productLocationFreezeDetail.setChange_quantity(quantity);
			productLocationFreezeDetail.setDescription("根据SKU冻结库存");
			productLocationFreezeDetail.setCreated_user(actionUser);
			productLocationFreezeDetailDao.insert(productLocationFreezeDetail);
				
			resMap.put("result", Response.SUCCESS);
			resMap.put("success", Boolean.TRUE);
			resMap.put("note", "根据SKU冻结库存成功");
		} catch (Exception e) {
			logger.error("根据SKU冻结库存异常", e);
			throw new RuntimeException("标记库位库存失败");
		} finally {
			lock.unlock();
		}
		return resMap;
	}
	
	// 根据LOCATION冻结库存
	public Map<String,Object> freezeLocationInventory(Integer physicalWarehouseId,Integer customerId,String locationBarcode,Integer plId,Integer quantity,String actionUser){
		Map<String,Object> resMap = new HashMap<String,Object>();
		ReentrantLock lock = LockUtil.getReentrantLock("physicalWarehouseId_"+ physicalWarehouseId + "_customerId_" + customerId);
		lock.lock();
		try {
				
			// 1.标记库位库存异常
			int col = productLocationDao.updateProductLocationForFreezeAvailable(quantity , plId);
			if( col < 1){
				throw new RuntimeException("标记库位库存失败");
			}
			
			// 2.插入冻结日志
			ProductLocationFreezeDetail productLocationFreezeDetail = new ProductLocationFreezeDetail();
			productLocationFreezeDetail.setPl_id(plId);
			productLocationFreezeDetail.setType(ProductLocationFreezeDetail.TYPE_FREEZE);
			productLocationFreezeDetail.setChange_quantity(quantity);
			productLocationFreezeDetail.setDescription("根据LOCATION冻结库存");
			productLocationFreezeDetail.setCreated_user(actionUser);
			productLocationFreezeDetailDao.insert(productLocationFreezeDetail);
				
			resMap.put("result", Response.SUCCESS);
			resMap.put("success", Boolean.TRUE);
			resMap.put("note", "根据LOCATION冻结库存成功");
		} catch (Exception e) {
			logger.error("根据LOCATION冻结库存异常", e);
			throw new RuntimeException("标记库位库存失败");
		} finally {
			lock.unlock();
		}
		return resMap;
	}
	
	// 释放冻结库存
	public Map<String,Object> releaseFreezeInventory(Integer physicalWarehouseId,Integer customerId,Integer plId,Integer quantity,String actionUser){
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		ReentrantLock lock = LockUtil.getReentrantLock("physicalWarehouseId_"+ physicalWarehouseId + "_customerId_" + customerId);
		lock.lock();
		try {
			
			// 1.接触库位库存异常
			int col = productLocationDao.updateProductLocationForReleaseAvailable(quantity , plId);
			if( col < 1){
				throw new RuntimeException("释放冻结库存失败");
			}
			
			// 2.插入冻结日志
			ProductLocationFreezeDetail productLocationFreezeDetail = new ProductLocationFreezeDetail();
			productLocationFreezeDetail.setPl_id(plId);
			productLocationFreezeDetail.setType(ProductLocationFreezeDetail.TYPE_UNFREEZE);
			productLocationFreezeDetail.setChange_quantity(quantity);
			productLocationFreezeDetail.setDescription("释放冻结库存");
			productLocationFreezeDetail.setCreated_user(actionUser);
			productLocationFreezeDetailDao.insert(productLocationFreezeDetail);
				
			resMap.put("result", Response.SUCCESS);
			resMap.put("success", Boolean.TRUE);
			resMap.put("note", "释放冻结库存成功");
		} catch (Exception e) {
			logger.error("释放冻结库存异常", e);
			throw new RuntimeException("释放冻结库存失败");
		} finally {
			lock.unlock();
		}
		return resMap;
	}
	

	// RF移库之库存移动 -- 源库位检查
	@Override
	public Map<String, Object> checkStockMoveSourceLocation(
			String locationBarcode,Integer physicalWarehouseId) {
		Map<String,Object> resMap = new HashMap<String,Object>();
		//1. 将locationBarcode转成A010101格式
		locationBarcode = locationBarcode.replaceAll("-","").toUpperCase();
		locationBarcode = locationBarcode.replaceAll("——","").toUpperCase();
		List<String> locationBarcodeList = new ArrayList<String>();
		locationBarcodeList.add(locationBarcode);
		//2. select 
		List<Location> locationTypeEmptyList = locationDao.checkLocationTypeByBarcode(locationBarcodeList,physicalWarehouseId);
		if(WorkerUtil.isNullOrEmpty(locationTypeEmptyList)){
			//3. 空判断 ,不存在
			resMap.put("success", false);
			resMap.put("error", "用户所属物理仓没有此库位信息！");
			return resMap;
		}
		Location locationTypeEmpty = locationTypeEmptyList.get(0);
//		if(Location.LOCATION_TYPE_RETURN.equals(locationTypeEmpty.getLocation_type())){
//			//4. 存在 ，但为Return区
//			resMap.put("success", false);
//			resMap.put("error", "库位属于Return区，不支持库存移动！");
//		}else if("Y".equals(locationTypeEmpty.getIs_empty())){
//			//5. 存在，但为空
		if("Y".equals(locationTypeEmpty.getIs_empty())){
		
			resMap.put("success", false);
			resMap.put("error", "库位上无商品信息，不需要移动！");
		}else{
			Integer locationId = Integer.parseInt(String.valueOf(locationTypeEmpty.getLocation_id()));
			String locationType = String.valueOf(locationTypeEmpty.getLocation_type());
			//6. 返回数据
			List<Map<String,Object>> list = productLocationDao.selectAllProductLocationByLocationId(locationId,"","");
			if(WorkerUtil.isNullOrEmpty(list)){
				resMap.put("success", false);
				resMap.put("error", "库位上无商品信息，不需要移动！");
			}else{
				resMap.put("success", true);
				Integer plSize = list.size();
				String status = String.valueOf(list.get(0).get("status"));
				resMap.put("locationType", locationType);
				resMap.put("locationTypeName", Location.LOCATION_TYPE_MAP.get(locationType));
				resMap.put("plSize", plSize);
				resMap.put("status", status);
				resMap.put("list", list);
			}
		}
		return resMap;
	}

	// RF移库之库存移动 -- 目标库位检查 & 完成移库
	@Override
	public Map<String, Object> stockMove(String fromLocationBarcode,
			String toLocationBarcode, Integer plId, Integer moveNum,Integer physicalWarehouseId) {
		Map<String,Object> resMap = new HashMap<String,Object>();
		fromLocationBarcode = fromLocationBarcode.replaceAll("-","").replaceAll("——","").toUpperCase();
		toLocationBarcode = toLocationBarcode.replaceAll("-","").replaceAll("——","").toUpperCase();
		List<String> locationBarcodeList = new ArrayList<String>();
		locationBarcodeList.add(fromLocationBarcode);
		locationBarcodeList.add(toLocationBarcode);
		//1. 检查 源库位 & 目标库位 所属区的合法对应关系
		List<Location> locationTypeEmptyList = locationDao.checkLocationTypeByBarcode(locationBarcodeList,physicalWarehouseId);
		if(WorkerUtil.isNullOrEmpty(locationTypeEmptyList) || locationTypeEmptyList.size()<2){
			//1.1  空判断 ,不存在
			resMap.put("success", false);
			resMap.put("error", "源库位与目标库位不是用户所属物理仓的合法库位！");
			return resMap;
		}
		Map<String,Object> frompl = productLocationDao.selectMapFromId(plId);
		Location fromLocationMap = new Location();
		Location toLocationMap = new Location();
		if(fromLocationBarcode.equals(locationTypeEmptyList.get(0).getLocation_barcode())){
			fromLocationMap = locationTypeEmptyList.get(0);
			toLocationMap = locationTypeEmptyList.get(1);
		}else{
			fromLocationMap = locationTypeEmptyList.get(1);
			toLocationMap = locationTypeEmptyList.get(0);
		}
		List<String> allowLocationTypeList = new ArrayList<String>();
		allowLocationTypeList.add(Location.LOCATION_TYPE_BOX_PICK);
		allowLocationTypeList.add(Location.LOCATION_TYPE_PIECE_PICK);
		allowLocationTypeList.add(Location.LOCATION_TYPE_STOCK);
		allowLocationTypeList.add(Location.LOCATION_TYPE_QUALITY_CHECK);
		String fromLocationType = fromLocationMap.getLocation_type();
		String toLocationType = toLocationMap.getLocation_type();
		if(!fromLocationType.equals(toLocationType) && 
				!(allowLocationTypeList.contains(fromLocationType) && allowLocationTypeList.contains(toLocationType))
				&& !(fromLocationType.equals(Location.LOCATION_TYPE_RETURN_DEFECTIVE) && toLocationType.equals(Location.LOCATION_TYPE_DEFECTIVE))
				&& !(fromLocationType.equals(Location.LOCATION_TYPE_RETURN_NORMAL) && toLocationType.equals(Location.LOCATION_TYPE_RETURN))
				&& !(fromLocationType.equals(Location.LOCATION_TYPE_RETURN_NORMAL) && toLocationType.equals(Location.LOCATION_TYPE_PIECE_PICK))
				&& !(fromLocationType.equals(Location.LOCATION_TYPE_RETURN) && toLocationType.equals(Location.LOCATION_TYPE_PIECE_PICK))
				&& !(fromLocationType.equals(Location.LOCATION_TYPE_PACKBOX) && toLocationType.equals(Location.LOCATION_TYPE_QUALITY_CHECK))
				
				&& !(fromLocationType.equals(Location.LOCATION_TYPE_PACKBOX) && toLocationType.equals(Location.LOCATION_TYPE_PACKBOX_PICK))
				&& !(fromLocationType.equals(Location.LOCATION_TYPE_PACKBOX_PICK) && toLocationType.equals(Location.LOCATION_TYPE_PACKBOX))
				&& !(fromLocationType.equals(Location.LOCATION_TYPE_PACKBOX_PICK) && toLocationType.equals(Location.LOCATION_TYPE_QUALITY_CHECK))
				){
			/**
			 * 二手 --> 二手
			 * 耗材-->耗材，质检区,耗材拣货
			 * 耗材拣货  -->耗材，质检区，耗材拣货
			 * 退货不良品暂存区 -->二手
			 * 存储，拣货区，质检区 --> 存储区，拣货区，质检区
			 * 退货良品暂存区 -->Return区，件拣区
			 * Return --> 件拣区，Return区
			 */
			resMap.put("success", false);
			resMap.put("error", "源库位与目标库位所属分区不可进行移动！");
			return resMap;
		}
		
		//2. 检查源库位 &plId的available数量>moveNum
		
		if(WorkerUtil.isNullOrEmpty(frompl)){
			resMap.put("success", false);
			resMap.put("error", "没有查到相关的库位库存记录！");
			return resMap;
		}else if(Integer.parseInt(String.valueOf(frompl.get("qty_available")))<moveNum){
			resMap.put("success", false);
			resMap.put("error", "此库位上选中商品的可移动数量不满足需求！");
			return resMap;
		}else if(!fromLocationMap.getLocation_id().equals(Integer.parseInt(String.valueOf(frompl.get("location_id"))))){
			resMap.put("success", false);
			resMap.put("error", "源库位上并不存在请求的库存记录！");
			return resMap;
		}
		//3. 检查目标库位可用性
		
		
		//若目标库位有货主限制（库位无限制时默认为0）,商品有货主限制（商品无限制时默认为1）,则需一致
		if(!WorkerUtil.isNullOrEmpty(toLocationMap.getCustomer_id()) &&  toLocationMap.getCustomer_id()!=0 && 
				Integer.parseInt(String.valueOf(frompl.get("customer_id"))) !=1 && 
				!String.valueOf(toLocationMap.getCustomer_id()).equals(String.valueOf(frompl.get("customer_id")))){
			resMap.put("success", false);
			resMap.put("error", "目标库位所属货主与商品货主不一致！");
			return resMap;
		}
		List<Map<String,Object>> list = productLocationDao.selectAllProductLocationByLocationId(toLocationMap.getLocation_id(),"","");
		if(!WorkerUtil.isNullOrEmpty(list)){
			Integer productId = Integer.parseInt(String.valueOf(frompl.get("product_id")));
			String validity = frompl.get("validity").toString();
			String batchSn = frompl.get("batch_sn").toString();
			Integer can_mix_product = toLocationMap.getCan_mix_product();
			Integer can_mix_batch = toLocationMap.getCan_mix_batch();
			Integer warehouse_id = Integer.parseInt(String.valueOf(frompl.get("warehouse_id")));
			for (Map<String, Object> map : list) {
				Integer productIdx = Integer.parseInt(String.valueOf(map.get("product_id")));
				String validityx = String.valueOf(map.get("validity"));
				String batchSnx = String.valueOf(map.get("batch_sn"));
				Integer warehouse_idx = Integer.parseInt(String.valueOf(map.get("warehouse_id")));
				if(can_mix_product==0 && !productIdx.equals(productId)){
					resMap.put("success", false);
					resMap.put("error", "目标库位不允许混放商品！");
					return resMap;
				}else if(can_mix_batch==0 && productIdx.equals(productId) && (!validityx.equals(validity) || !batchSnx.equalsIgnoreCase(batchSn))){
					resMap.put("success", false);
					resMap.put("error", "目标库位不允许混放批次！");
					return resMap;
				}else if(productIdx.equals(productId) && !warehouse_idx.equals(warehouse_id)){
					resMap.put("success", false);
					resMap.put("error", toLocationMap.getLocation_barcode()+"库位商品渠道不同不允许存放！请重新输入");
					return resMap;
				}
			}
		}
		
		//4. 移动 
		logger.info("stockMove start from:"+fromLocationBarcode+" num:"+moveNum+" to:"+toLocationBarcode+" by pl:"+plId);
		List<Integer> empty0 = new ArrayList<Integer>();
		List<Map> empty1 = new ArrayList<Map>();
		Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
		Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
		List<Product> empty5 = new ArrayList<Product>();
		String result = "";
		try{
			result = replenishmentBiz.lockReplenishJobByWarehouseCustomer(0,
					"","MoveStock",empty0,physicalWarehouseId,Integer.parseInt(String.valueOf(frompl.get("customer_id"))),
					empty1,empty2,empty0,empty4,0,empty5,plId,moveNum,toLocationMap.getLocation_id());
		}catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		if(result.equalsIgnoreCase("LOCKING")){
			resMap.put("success", false);
			resMap.put("error", "库位库存正在执行分配逻辑，请稍后再试！");
		}else if(result.equalsIgnoreCase("SUCCESS")){
			resMap.put("success", true);
			resMap.put("error", "");
		}else{
			resMap.put("success", false);
			resMap.put("error", result);
		}
		
		return resMap;
	}

	//RF移库之库存转移 -- 源库位检查
	@Override
	public Map<String, Object> checkStockTransferSourceLocation(
			String locationBarcode,Integer physicalWarehouseId) {
		Map<String,Object> resMap = new HashMap<String,Object>();
		//1. 将locationBarcode转成A010101格式
		locationBarcode = locationBarcode.replaceAll("-","").toUpperCase();
		locationBarcode = locationBarcode.replaceAll("——","").toUpperCase();
		List<String> locationBarcodeList = new ArrayList<String>();
		locationBarcodeList.add(locationBarcode);
		//2. select 
		List<Location> locationTypeEmptyList = locationDao.checkLocationTypeByBarcode(locationBarcodeList,physicalWarehouseId);
		if(WorkerUtil.isNullOrEmpty(locationTypeEmptyList)){
			//3. 空判断 ,不存在
			resMap.put("success", false);
			resMap.put("error", "用户所属物理仓没有此库位信息！");
			return resMap;
		}
		Location locationTypeEmpty = locationTypeEmptyList.get(0);
		if(Location.LOCATION_TYPE_PACKBOX.equals(locationTypeEmpty.getLocation_type())){
			//4. 存在 ，但为Return区
			resMap.put("success", false);
			resMap.put("error", "库位属于耗材区，不支持库存转移！");
		}else if("Y".equals(locationTypeEmpty.getIs_empty())){
			//5. 存在，但为空
			resMap.put("success", false);
			resMap.put("error", "库位上无商品信息，不需要转移！");
		}else{
			Integer locationId = Integer.parseInt(String.valueOf(locationTypeEmpty.getLocation_id()));
			String locationType = String.valueOf(locationTypeEmpty.getLocation_type());
			//6. 返回数据
			List<Map<String,Object>> list = productLocationDao.selectAllProductLocationByLocationId(locationId,"","");
			if(WorkerUtil.isNullOrEmpty(list)){
				resMap.put("success", false);
				resMap.put("error", "库位上无商品信息，不需要转移！");
			}else{
				resMap.put("success", true);
				Integer plSize = list.size();
				String status = String.valueOf(list.get(0).get("status"));
				resMap.put("locationType", locationType);
				resMap.put("plSize", plSize);
				resMap.put("status", status);
				resMap.put("list", list);
			}
		}
		return resMap;
	}

	// RF移库之库存转移-- 目标库位检查 & 完成移库
	@Override
	public Map<String, Object> stockTransfer(String fromLocationBarcode,
			String toLocationBarcode, Integer plId, Integer moveNum, Integer physicalWarehouseId) {
		Map<String,Object> resMap = new HashMap<String,Object>();
		fromLocationBarcode = fromLocationBarcode.replaceAll("-","").replaceAll("——","").toUpperCase();
		toLocationBarcode = toLocationBarcode.replaceAll("-","").replaceAll("——","").toUpperCase();
		List<String> locationBarcodeList = new ArrayList<String>();
		locationBarcodeList.add(fromLocationBarcode);
		locationBarcodeList.add(toLocationBarcode);
		//1. 检查 源库位 & 目标库位 所属区的合法对应关系
		List<Location> locationTypeEmptyList = locationDao.checkLocationTypeByBarcode(locationBarcodeList,physicalWarehouseId);
		if(WorkerUtil.isNullOrEmpty(locationTypeEmptyList) || locationTypeEmptyList.size()<2){
			//1.1  空判断 ,不存在
			resMap.put("success", false);
			resMap.put("error", "源库位与目标库位不是用户所属物理仓的合法库位！");
			return resMap;
		}
		Location fromLocationMap = new Location();
		Location toLocationMap = new Location();
		if(fromLocationBarcode.equals(locationTypeEmptyList.get(0).getLocation_barcode())){
			fromLocationMap = locationTypeEmptyList.get(0);
			toLocationMap = locationTypeEmptyList.get(1);
		}else{
			fromLocationMap = locationTypeEmptyList.get(1);
			toLocationMap = locationTypeEmptyList.get(0);
		}
		List<String> allowLocationTypeList = new ArrayList<String>();
		allowLocationTypeList.add(Location.LOCATION_TYPE_BOX_PICK);
		allowLocationTypeList.add(Location.LOCATION_TYPE_PIECE_PICK);
		allowLocationTypeList.add(Location.LOCATION_TYPE_STOCK);
		allowLocationTypeList.add(Location.LOCATION_TYPE_RETURN);
		String fromLocationType = fromLocationMap.getLocation_type();
		String toLocationType = toLocationMap.getLocation_type();
		if(!((fromLocationType.equals(Location.LOCATION_TYPE_DEFECTIVE) && allowLocationTypeList.contains(toLocationType) )|| 
				(toLocationType.equals(Location.LOCATION_TYPE_DEFECTIVE) && allowLocationTypeList.contains(fromLocationType)))){
			resMap.put("success", false);
			resMap.put("error", "源库位与目标库位所属分区不可进行移动！");
			return resMap;
		}
		//2. 检查源库位 &plId的available数量>moveNum
		Map<String,Object> frompl = productLocationDao.selectMapFromId(plId);
		if(WorkerUtil.isNullOrEmpty(frompl)){
			resMap.put("success", false);
			resMap.put("error", "没有查到相关的库位库存记录！");
			return resMap;
		}else if(Integer.parseInt(String.valueOf(frompl.get("qty_available")))<moveNum){
			resMap.put("success", false);
			resMap.put("error", "此库位上选中商品的可移动数量不满足需求！");
			return resMap;
		}else if(!fromLocationMap.getLocation_id().equals(Integer.parseInt(String.valueOf(frompl.get("location_id"))))){
			resMap.put("success", false);
			resMap.put("error", "源库位上并不存在请求的库存记录！");
			return resMap;
		}
		//3. 检查目标库位可用性
		if(!WorkerUtil.isNullOrEmpty(toLocationMap.getCustomer_id()) &&  toLocationMap.getCustomer_id()!=0 && 
				!String.valueOf(toLocationMap.getCustomer_id()).equals(String.valueOf(frompl.get("customer_id")))){
			resMap.put("success", false);
			resMap.put("error", "目标库位所属货主与商品货主不一致！");
			return resMap;
		}
		List<Map<String,Object>> list = productLocationDao.selectAllProductLocationByLocationId(toLocationMap.getLocation_id(),"","");
		if(!WorkerUtil.isNullOrEmpty(list)){
			Integer productId = Integer.parseInt(String.valueOf(frompl.get("product_id")));
			String validity = frompl.get("validity").toString();
			String batchSn = frompl.get("batch_sn").toString();
			Integer can_mix_product = toLocationMap.getCan_mix_product();
			Integer can_mix_batch = toLocationMap.getCan_mix_batch();
			Integer warehouse_id = Integer.parseInt(String.valueOf(frompl.get("warehouse_id")));
			for (Map<String, Object> map : list) {
				Integer productIdx = Integer.parseInt(String.valueOf(map.get("product_id")));
				String validityx = String.valueOf(map.get("validity"));
				String batchSnx = String.valueOf(map.get("batch_sn"));
				Integer warehouse_idx = Integer.parseInt(String.valueOf(map.get("warehouse_id")));
				if(can_mix_product==0 && !productIdx.equals(productId)){
					resMap.put("success", false);
					resMap.put("error", "目标库位不允许混放商品！");
					return resMap;
				}else if(can_mix_batch==0 && productIdx.equals(productId) && (!validityx.equals(validity) || !batchSnx.equals(batchSn))){
					resMap.put("success", false);
					resMap.put("error", "目标库位不允许混放批次！");
					return resMap;
				}else if(productIdx.equals(productId) && !warehouse_idx.equals(warehouse_id)){
					resMap.put("success", false);
					resMap.put("error", toLocationMap.getLocation_barcode()+"库位商品渠道不同不允许存放！请重新输入");
					return resMap;
				}
			}
		}
		//4. 转移
		logger.info("stockMove start from:"+fromLocationBarcode+" num:"+moveNum+" to:"+toLocationBarcode+" by pl:"+plId);
		List<Integer> empty0 = new ArrayList<Integer>();
		List<Map> empty1 = new ArrayList<Map>();
		Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
		Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
		List<Product> empty5 = new ArrayList<Product>();
		String result = "";
		try{
			result = replenishmentBiz.lockReplenishJobByWarehouseCustomer(0,
					"","TransferStock",empty0,physicalWarehouseId,Integer.parseInt(String.valueOf(frompl.get("customer_id"))),
					empty1,empty2,empty0,empty4,0,empty5,plId,moveNum,toLocationMap.getLocation_id());
		}catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		if(result.equalsIgnoreCase("LOCKING")){
			resMap.put("success", false);
			resMap.put("error", "库位库存正在执行分配逻辑，请稍后再试！");
		}else if(result.equalsIgnoreCase("SUCCESS")){
			resMap.put("success", true);
			resMap.put("error", "");
		}else{
			resMap.put("success", false);
			resMap.put("error", result);
		}
		
		return resMap;
	}

	//供应商退货打印出库单信息查询
	public List<Map<String, Object>> getGoodsReturnPrintList(String batchOrder) {
		List<Map<String,Object>> goodsReturnPrintList = new ArrayList<Map<String,Object>>();
		String arr[] = batchOrder.split(",");
		for (int i = 0; i < arr.length; i++) {
			Map<String,Object> goodsReturnPrintMap = new HashMap<String,Object>();
			List<Map<String,Object>> goodsReturnPrintInfo = orderProcessDao.getGoodsReturnInfo(arr[i]);
			if(WorkerUtil.isNullOrEmpty(goodsReturnPrintInfo)){
				continue;
			}
			goodsReturnPrintMap.put("brand_name", goodsReturnPrintInfo.get(0).get("brand_name"));
			goodsReturnPrintMap.put("order_id",arr[i]);
			goodsReturnPrintMap.put("provider_name", goodsReturnPrintInfo.get(0).get("receive_name"));
			goodsReturnPrintMap.put("goodsReturnPrintInfo", goodsReturnPrintInfo);
			goodsReturnPrintList.add(goodsReturnPrintMap);
		}
		return goodsReturnPrintList;
	}
	
	
	public Map<String, Object> importProductLocation(Integer customerId,Integer physicalWarehouseId){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		List<Map> waitImportInventoryList = inventoryDao.selectImportProductLocation(customerId, physicalWarehouseId, "N");
		if(WorkerUtil.isNullOrEmpty(waitImportInventoryList)){
			return returnMap;
		}
		int transferNum = 0;
		try {
			for(Map map :waitImportInventoryList){
				Integer productId = Integer.parseInt(map.get("product_id").toString());
				Integer tmpPlId = Integer.parseInt(map.get("tmp_pl_id").toString());
				Integer locationId = Integer.parseInt(map.get("location_id").toString());
				Integer quantity = Integer.parseInt(map.get("quantity").toString());
				String validity = map.get("validity").toString();
				String locationBarcode = map.get("location_barcode").toString();
				String status = "";
				Location location = locationDao.selectLocationIdByLocationBarCode(physicalWarehouseId, locationBarcode, null);
				if(Location.LOCATION_TYPE_DEFECTIVE.equals(location.getLocation_type())){
					status = "DEFECTIVE";
				}else{
					status = "NORMAL";
				}
				
				Task task = new Task();
				task.setPhysical_warehouse_id(physicalWarehouseId);
				task.setCustomer_id(customerId);
				task.setTask_type(Task.TASK_TYPE_VIRTUAL_PUT_AWAY);
				task.setTask_status(Task.TASK_TYPE_VIRTUAL_PUT_AWAY);
				task.setTask_level(1);
				task.setProduct_id(productId);
				task.setQuantity(quantity);
				task.setOperate_platform("WEB");
				task.setCreated_user("system");
				task.setLast_updated_user("system");
				taskDao.insert(task);
				
				Map<String,Object> paramsForSelect = new HashMap<String,Object>();
				paramsForSelect.put("productId", productId);
				paramsForSelect.put("locationId", locationId);
				paramsForSelect.put("status", status);
				paramsForSelect.put("validity", validity);
				ProductLocation plocation = inventoryDao.selectProductLocation(paramsForSelect);
				ProductLocationDetail productLocationDetail = new ProductLocationDetail();
				// 导入item里面存在的库存记录
				if(WorkerUtil.isNullOrEmpty(plocation)){
					ProductLocation productLocation = new ProductLocation();
					productLocation.setProduct_id(productId);
					productLocation.setLocation_id(location.getLocation_id());
					productLocation.setQty_total(quantity);
					productLocation.setQty_available(quantity);
					productLocation.setProduct_location_status("NORMAL");
					productLocation.setQty_freeze(0);
					productLocation.setStatus(status);
					productLocation.setValidity(validity);
					productLocation.setSerial_number("");
					productLocation.setCreated_user("system");
					productLocation.setLast_updated_user("system");
					productLocationDao.insert(productLocation);
					productLocationDetail.setPl_id(productLocation.getPl_id());
				}else{
					productLocationDao.updateProductLocation(quantity, plocation.getPl_id());
					productLocationDetail.setPl_id(plocation.getPl_id());
				}
				
				productLocationDetail.setChange_quantity(quantity);
				productLocationDetail.setTask_id(task.getTask_id());
				productLocationDetail.setDescription("导入库存记录");
				productLocationDetail.setCreated_user("system");
				productLocationDetail.setLast_updated_user("system");
				productLocationDetailDao.insert(productLocationDetail);
				
				Map<String,Object> paramsForUpdateMap = new HashMap<String,Object>();
				paramsForUpdateMap.put("location_id", location.getLocation_id());
				paramsForUpdateMap.put("is_empty", "N");
				inventoryDao.updateLocation(paramsForUpdateMap);
				
				tmpProductLocationDao.update(tmpPlId, customerId, physicalWarehouseId, "system", "Y","success");
				transferNum ++;
			}
			
			
			
			// 判断导入库存数量与实际数量的差异
			// List<Map<String,Object>> mapList = inventoryDao.selectTmpProductLocationByTmpList(waitImportInventoryList);
			List<Map<String,Object>> mapList = inventoryDao.selectItemByCustomerId2PhysicalWarehouseId(customerId, physicalWarehouseId);
			List<Integer> productIdList = new ArrayList<Integer>();
			for(Map map : mapList){
				Integer productId = Integer.parseInt(map.get("product_id").toString());
				if(productIdList.contains(productId)){
					continue;
				}
				productIdList.add(productId);
				String status = map.get("status").toString();
				Integer quantity = Integer.parseInt(map.get("quantity").toString());
				List<InventoryItem> inventoryItemList = inventoryDao.selectItemByProductId(productId, physicalWarehouseId,status);
				List<Map> productLocationList = inventoryDao.selectProductLocationByProductId(productId, physicalWarehouseId,status);
				int countNum = 0 ,plCountNum = 0;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date validityTemp = sdf.parse("1970-01-01");
				for(InventoryItem item : inventoryItemList){
					countNum += item.getQuantity();
					Date validity = item.getValidity();
					if(WorkerUtil.isNullOrEmpty(validity)){
						continue;
					}
					if(validityTemp.getTime() > validity.getTime()){
						validityTemp = validity;
					}
				}
				for(Map plTemp : productLocationList){
					plCountNum += Integer.parseInt(plTemp.get("quantity").toString());
				}
				
				if(plCountNum != countNum){
					
					int diffNum = plCountNum - countNum;
					Task task = new Task();
					task.setPhysical_warehouse_id(physicalWarehouseId);
					task.setCustomer_id(customerId);
					task.setTask_type(Task.TASK_TYPE_VIRTUAL_PUT_AWAY);
					task.setTask_status(Task.TASK_TYPE_VIRTUAL_PUT_AWAY);
					task.setTask_level(1);
					task.setProduct_id(productId);
					task.setQuantity(Math.abs(diffNum));
					task.setOperate_platform("WEB");
					task.setCreated_user("system");
					task.setLast_updated_user("system");
					taskDao.insert(task);
					
					ScheduleQueueCount scheduleQueueCount = new ScheduleQueueCount();
					List<Location> locationList = null;
					Integer locationIdNew = 0;
					String locationBarcode = "";
					if(diffNum > 0){
						locationList = locationDao.selectAllLocationList(physicalWarehouseId, Location.LOCATION_TYPE_VARIANCE_ADD);
						if(locationList.size() == 0){
							Location location = new Location();
							location.setPhysical_warehouse_id(physicalWarehouseId);
							locationBarcode = WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_OTHER,"PY",true);
							location.setLocation_barcode(locationBarcode);
							location.setLocation_type(Location.LOCATION_TYPE_VARIANCE_ADD);
							location.setIs_delete("N");
							location.setCreated_user("system");
							int locationId = inventoryDao.insertLocation(location);
							locationIdNew = location.getLocation_id();
						}else{
							locationIdNew = locationList.get(0).getLocation_id();
							locationBarcode = locationList.get(0).getLocation_barcode();
						}
						scheduleQueueCount.setLocation_type(Location.LOCATION_TYPE_VARIANCE_ADD);
					}else if(diffNum < 0){
						locationList = locationDao.selectAllLocationList(physicalWarehouseId, Location.LOCATION_TYPE_VARIANCE_MIMUS);
						if(locationList.size() == 0){
							Location location = new Location();
							location.setPhysical_warehouse_id(physicalWarehouseId);
							locationBarcode = WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_OTHER,"PK",true);
							location.setLocation_barcode(locationBarcode);
							location.setLocation_type(Location.LOCATION_TYPE_VARIANCE_MIMUS);
							location.setIs_delete("N");
							location.setCreated_user("system");
							int locationId = inventoryDao.insertLocation(location);
							locationIdNew = location.getLocation_id();
						}else{
							locationIdNew = locationList.get(0).getLocation_id();
							locationBarcode = locationList.get(0).getLocation_barcode();
						}
						scheduleQueueCount.setLocation_type(Location.LOCATION_TYPE_VARIANCE_MIMUS);
					}else{
						continue;
					}
					
					if(locationList.size() == 1 || locationList.size() == 0){
						
						Map<String,Object> paramsForSelect = new HashMap<String,Object>();
						paramsForSelect.put("productId", productId);
						paramsForSelect.put("locationId", locationIdNew);
						paramsForSelect.put("status", status);
						ProductLocation plocation = inventoryDao.selectProductLocation(paramsForSelect);
						ProductLocationDetail productLocationDetail = new ProductLocationDetail();
						if(WorkerUtil.isNullOrEmpty(plocation)){
							ProductLocation productLocation2 = new ProductLocation();
							productLocation2.setProduct_id(productId);
							productLocation2.setLocation_id(locationIdNew);
							productLocation2.setQty_total(Math.abs(diffNum));
							productLocation2.setQty_available(Math.abs(diffNum));
							productLocation2.setProduct_location_status("NORMAL");
							productLocation2.setQty_freeze(0);
							productLocation2.setStatus(status);
							productLocation2.setValidity(sdf.format(validityTemp));
							productLocation2.setSerial_number("");
							productLocation2.setCreated_user("system");
							productLocation2.setLast_updated_user("system");
							productLocationDao.insert(productLocation2);
							productLocationDetail.setPl_id(productLocation2.getPl_id());
						}else{
							productLocationDao.updateProductLocationV2(Math.abs(diffNum), plocation.getPl_id());
							productLocationDetail.setPl_id(plocation.getPl_id());
						}
						String countSn = WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_PDCODE,"P",true);
						scheduleQueueCount.setCount_sn(countSn);
						scheduleQueueCount.setPhysical_warehouse_id(physicalWarehouseId);
						scheduleQueueCount.setCustomer_id(customerId);
						scheduleQueueCount.setTask_type("NORMAL");
						scheduleQueueCount.setFrom_location_barcode("");
						scheduleQueueCount.setTo_location_barcode("");
						scheduleQueueCount.setStatus("FULFILLED");
						scheduleQueueCount.setCreated_time(new Date());
						scheduleQueueCount.setLast_updated_user("system");
						scheduleQueueCount.setLast_updated_time(new Date());
						scheduleQueueCount.setCreated_user("system");
						scheduleQueueCountDao.insert(scheduleQueueCount);
						
						
						productLocationDetail.setChange_quantity(diffNum);
						productLocationDetail.setTask_id(task.getTask_id());
						productLocationDetail.setDescription("导入盘盈/盘亏库存记录");
						productLocationDetail.setCreated_user("system");
						productLocationDetail.setLast_updated_user("system");
						productLocationDetail.setCount_sn(countSn);
						productLocationDetailDao.insert(productLocationDetail);
						
					}else{
						throw new RuntimeException("转换库位库存失败,盘盈/盘亏区有多个");
					}
					
				}
			}
			
		} catch (Throwable e) {
			logger.error("转换库位库存失败", e);
			throw new RuntimeException("转换库位库存失败"+e.getMessage());
		} 
		
		returnMap.put("resulte", Response.SUCCESS);
		returnMap.put("note", "转换库位库存成功"+transferNum+"条记录");
		return returnMap;
	}

	
	
	public Map<String, Object> autoPurchaseAccept(Integer orderId,Integer customerId,Integer physicalWarehouseId){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		logger.info("autoPurchaseAccept orderId:"+orderId+",customerId:"+customerId+",physicalWarehouseId:"+physicalWarehouseId);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
			OrderInfo orderInfo = orderDao.selectOrderInfoByOrderId(orderId);
			List<OrderGoods> orderGoodsList = orderDao.selectOrderGoodsByOrderId(orderId);
			String note = orderInfo.getNote();
			JSONObject noteJson  = JSONObject.fromObject(note);
			for(OrderGoods orderGood : orderGoodsList){
				
				Product product = productDao.selectByPrimaryKey(orderGood.getProduct_id());
				String serial_number = noteJson.containsKey("serial_number")?noteJson.getString("serial_number"):"";
				String validity_ = noteJson.containsKey("validity")?noteJson.getString("validity"):"1970-01-01";
				String status = noteJson.containsKey("status")?noteJson.getString("status"):"INV_STTS_AVAILABLE";
				status = "INV_STTS_AVAILABLE".equals(status)?"NORMAL":"DEFECTIVE";
				String batchSn = noteJson.containsKey("batch_sn")?noteJson.getString("batch_sn"):"";
				BigDecimal unitcost = orderGood.getGoods_price();
				Date validity = sdf.parse(validity_);
				
				this.createAcceptInventoryTransactionSingleReal(
						orderGood.getProduct_id(), orderGood.getGoods_number(),
						product.getIs_serial(), serial_number, orderId, status,
						"system", unitcost, orderGood.getOrder_goods_id(),
						orderInfo.getWarehouse_id(), physicalWarehouseId, null,
						null, product.getBarcode(), validity, batchSn,
						orderInfo.getProvider_code(), customerId,
						orderInfo.getCurrency(), orderInfo.getProvider_order_type());
			}
			orderBiz.updateOrderInfoStatusV2(orderId, OrderInfo.ORDER_STATUS_FULFILLED, "system");
		} catch (Exception e) {
			logger.error("autoPurchaseAccept", e);
			throw new RuntimeException("autoPurchaseAccept失败");
		}
		return returnMap;
	}

	
	public Map<String, Object> testAutoPurchaseAccept(Integer orderId,Integer customerId,Integer physicalWarehouseId){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		logger.info("autoPurchaseAccept orderId:"+orderId+",customerId:"+customerId+",physicalWarehouseId:"+physicalWarehouseId);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
			OrderInfo orderInfo = orderDao.selectOrderInfoByOrderId(orderId);
			List<OrderGoods> orderGoodsList = orderDao.selectOrderGoodsByOrderId(orderId);
			List<Integer> locationIdList = locationDao.selectLocationIdForTest(physicalWarehouseId);
			for(OrderGoods orderGood : orderGoodsList){
				
				Product product = productDao.selectByPrimaryKey(orderGood.getProduct_id());
				String validity_ = "2016-01-01";
				String status = "NORMAL";
				String batchSn = "";
				BigDecimal unitcost = orderGood.getGoods_price();
				Date validity = sdf.parse(validity_);
				
				this.createAcceptInventoryTransactionSingleReal(
						orderGood.getProduct_id(), orderGood.getGoods_number(),
						product.getIs_serial(), "", orderId, status,
						"system", unitcost, orderGood.getOrder_goods_id(),
						orderInfo.getWarehouse_id(), physicalWarehouseId, null,
						null, product.getBarcode(), validity, batchSn,
						orderInfo.getProvider_code(), orderGood.getCustomer_id(),
						orderInfo.getCurrency(), orderInfo.getProvider_order_type());
				
				Integer locationId = locationIdList.get((int)(Math.random()*10));
				Map<String,Object> paramsForSelect = new HashMap<String,Object>();
				paramsForSelect.put("productId", orderGood.getProduct_id());
				paramsForSelect.put("locationId", locationId);
				paramsForSelect.put("status", status);
				ProductLocation plocation = inventoryDao.selectProductLocation(paramsForSelect);
				ProductLocationDetail productLocationDetail = new ProductLocationDetail();
				if(WorkerUtil.isNullOrEmpty(plocation)){
					
					ProductLocation productLocation = new ProductLocation();
					productLocation.setProduct_id(orderGood.getProduct_id());
					
					productLocation.setLocation_id(locationId);
					productLocation.setQty_total(orderGood.getGoods_number());
					productLocation.setQty_available(orderGood.getGoods_number());
					productLocation.setProduct_location_status("NORMAL");
					productLocation.setQty_freeze(0);
					productLocation.setStatus(status);
					productLocation.setValidity(validity_);
					productLocation.setSerial_number("");
					productLocation.setCreated_user("system");
					productLocation.setLast_updated_user("system");
					productLocationDao.insert(productLocation);
					productLocationDetail.setPl_id(productLocation.getPl_id());
				}else{
					productLocationDao.updateProductLocation(orderGood.getGoods_number(), plocation.getPl_id());
					productLocationDetail.setPl_id(plocation.getPl_id());
				}
				
				productLocationDetail.setChange_quantity(orderGood.getGoods_number());
				productLocationDetail.setTask_id(0);
				productLocationDetail.setDescription("导入库存记录");
				productLocationDetail.setCreated_user("system");
				productLocationDetail.setLast_updated_user("system");
				productLocationDetailDao.insert(productLocationDetail);
			}
			
			
			orderBiz.updateOrderInfoStatusV2(orderId, OrderInfo.ORDER_STATUS_FULFILLED, "system");
		} catch (Exception e) {
			logger.error("autoPurchaseAccept", e);
			throw new RuntimeException("autoPurchaseAccept失败");
		}
		return returnMap;
	}

	
	public Map<String,Object> getPurchaseLableInfo(String purchaseLable,Integer physicalWarehouseId){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		return inventoryDao.selectPurchaseLableInfo(purchaseLable, physicalWarehouseId);
	}
	
	//取消订单查询
	public Map<String,Object> getOrderCancelList(Map<String,Object> map){
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<String> orderIdList = new ArrayList<String>();
		List<String> orderGoodsIdList = new ArrayList<String>();
		List<String> customerAndBarcodeList = new ArrayList<String>();
		String con = "";
		List<Map<String,Object>> newOrderCancelList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> newOrderCancelList1 = new ArrayList<Map<String,Object>>();
		Integer orderIdNumber = 0;
		Integer goodsTotalNumberInteger =0;
		Integer goodsCategory= 0;
		List<Map<String,Object>> orderCancelList = inventoryDaoSlave.getOrderCancelList(map);
		if(WorkerUtil.isNullOrEmpty(orderCancelList)){
			resultMap.put("message", "不存在取消未上架的订单");
			return resultMap;
		}
		//过滤SQL查询出来的相同记录
//		for (Map<String, Object> map2 : orderCancelList) {
//			if(orderIdList.contains(map2.get("order_id").toString()) && orderGoodsIdList.contains(map2.get("order_goods_id").toString())){
//				logger.info("该记录已存在");
//			}else{
//				orderIdList.add(map2.get("order_id").toString());
//				orderGoodsIdList.add(map2.get("order_goods_id").toString());
//				newOrderCancelList.add(map2);
//			}
//		}
//		
//		orderIdList.removeAll(orderIdList);
//		orderGoodsIdList.removeAll(orderGoodsIdList);
		//统计相关信息
		for (Map<String, Object> map3 : orderCancelList) {
			if(!orderIdList.contains(map3.get("order_id").toString())){
				Map<String,Object> result1Map = new HashMap<String, Object>();
				List<Map<String,Object>> maplist = new ArrayList<Map<String,Object>>();
				Map<String,Object> result2Map = new HashMap<String, Object>();
				orderIdList.add(map3.get("order_id").toString());
				result1Map.put("order_id", map3.get("order_id").toString());
				result1Map.put("name", map3.get("name").toString());
				result2Map.put("barcode", map3.get("barcode").toString());
				result2Map.put("product_name", map3.get("product_name").toString());
				result2Map.put("diff", map3.get("diff").toString());
				maplist.add(result2Map);
				result1Map.put("maplist", maplist);
				newOrderCancelList1.add(result1Map);
				orderIdNumber++;
			}else {
				Map<String,Object> result2Map = new HashMap<String, Object>();
				result2Map.put("barcode", map3.get("barcode").toString());
				result2Map.put("product_name", map3.get("product_name").toString());
				result2Map.put("diff", map3.get("diff").toString());
				List<Map<String,Object>> maplist1 =(List<Map<String, Object>>) newOrderCancelList1.get(orderIdNumber-1).get("maplist");
				maplist1.add(result2Map);
			}
			if(!orderGoodsIdList.contains(map3.get("order_goods_id").toString())){
				orderGoodsIdList.add(map3.get("order_goods_id").toString());
				goodsTotalNumberInteger += Integer.parseInt(map3.get("diff").toString());
			}
			con= map3.get("name").toString() + map3.get("barcode").toString();
			if(!customerAndBarcodeList.contains(con)){
				customerAndBarcodeList.add(con);
				goodsCategory++;
			}
			
			
			
		}
		newOrderCancelList1.get(orderIdNumber-1).put("maplist", (List<Map<String, Object>>) newOrderCancelList1.get(orderIdNumber-1).get("maplist"));
		resultMap.put("newOrderCancelList", newOrderCancelList1);//表格内容
		resultMap.put("orderIdNumber", orderIdNumber);//订单数量
		resultMap.put("goodsTotalNumberInteger", goodsTotalNumberInteger);// 商品数量
		resultMap.put("goodsCategory", goodsCategory); // 商品种类
		
		return  resultMap;
	}

	@Override
	public void doVarianceMinus(int physical_warehouse_id, int customer_id,
			Map<String, List<Map>> varianceMinusImproveOrderGoodsMap, List<Integer> productIdList,List<Integer> orderIdList, List<Integer> orderGoodIdList, int warehouseId) {
		
		List<OrderInfo> lockList=orderInfoDao.selectOrderInfoListByIdForUpdate(orderIdList);
		
		List<ProductLocation> productLocationList=productLocationDao.selectProductlocationListV5(physical_warehouse_id, productIdList, Location.LOCATION_TYPE_VARIANCE_STOCK_MIMUS,warehouseId);
		
		Map<String,Integer> avaliableMap=new HashMap<String,Integer>();
		
		for(ProductLocation pl:productLocationList){
			String key=pl.getProduct_id()+"_"+pl.getStatus();
			if(null!=avaliableMap.get(key)){
				avaliableMap.put(key, avaliableMap.get(key)+pl.getQty_total());
			}else{
				avaliableMap.put(key,pl.getQty_total());
			}
		}
		
		List<InventoryItem> inventoryItemList=inventoryDao.selectAllInventoryItem(productIdList);
		
		List<InventoryItem> inventoryItemUpdateList=new ArrayList<InventoryItem>();
		
		List<InventoryItemDetail> inventoryItemDetailList=new ArrayList<InventoryItemDetail>();
		List<InventoryItemVariance> inventoryItemVarianceList=new ArrayList<InventoryItemVariance>();
		
		Map<String,InventoryItem> inventoryItemMap=new HashMap<String,InventoryItem>();
		for(InventoryItem it:inventoryItemList){
			inventoryItemMap.put(it.getInventory_item_id()+"", it);
		}
		
		Map<String,List<OrderReserveInventoryMapping> > inventoryItemIdMap=new HashMap<String,List<OrderReserveInventoryMapping>>();
		
		//order_goods 和inventory_item
		List<OrderReserveInventoryMapping> list=inventoryDao.selectAllReservedMapper(orderGoodIdList);
		
		for(OrderReserveInventoryMapping orim:list){
			List<OrderReserveInventoryMapping> listtemp =new ArrayList<OrderReserveInventoryMapping>();
			if(null!=inventoryItemIdMap.get(orim.getOrder_goods_id()+"")){
				listtemp=inventoryItemIdMap.get(orim.getOrder_goods_id()+"");
				listtemp.add(orim);
			}else{
				listtemp.add(orim);
				inventoryItemIdMap.put(orim.getOrder_goods_id()+"", listtemp);
			}

		}
		
		List<ProductLocation> updateProductlocationList = new ArrayList<ProductLocation>();
		List<ProductLocationDetail>  pldList=new ArrayList<ProductLocationDetail>();
		List<Integer> orderIdUpdateList=new ArrayList<Integer>();
		
		for(List<Map> li:varianceMinusImproveOrderGoodsMap.values()){
			
			boolean isAvailable=true;
			for(Map m:li){
				int product_id = null==m.get("product_id")?0:Integer.parseInt(m.get("product_id").toString());
				int goods_number = null==m.get("goods_number")?0:Integer.parseInt(m.get("goods_number").toString());
				String status= null==m.get("status_id")?"NORMAL":m.get("status_id").toString();
				
				int available=null==avaliableMap.get(product_id+"_"+status)?0:avaliableMap.get(product_id+"_"+status);
				
				if(goods_number>available){
					isAvailable=false;
					break;
				}
			}
			if(!isAvailable){
				continue;
			}
			
			for(Map m:li){
				int order_id = null==m.get("order_id")? 0:Integer.parseInt(m.get("order_id").toString());
				int order_goods_id = null==m.get("order_goods_id")? 0:Integer.parseInt(m.get("order_goods_id").toString());
				int product_id = null==m.get("product_id")?0:Integer.parseInt(m.get("product_id").toString());
				int goods_number = null==m.get("goods_number")?0:Integer.parseInt(m.get("goods_number").toString());
				int warehouse_id = null==m.get("warehouse_id")?0:Integer.parseInt(m.get("warehouse_id").toString());
				String status= null==m.get("status_id")?"NORMAL":m.get("status_id").toString();
				int available=null==avaliableMap.get(product_id+"_"+status)?0:avaliableMap.get(product_id+"_"+status);
				avaliableMap.put(product_id+"_"+status, available-goods_number);
				int num = 0;
				
				orderIdUpdateList.add(order_id);
				
				List<OrderReserveInventoryMapping> orimList=inventoryItemIdMap.get(order_goods_id+"");
				
				for(OrderReserveInventoryMapping orim:orimList){
					InventoryItem it=inventoryItemMap.get(orim.getInventory_item_id()+"");
					
					int quantity=orim.getQuantity().intValue();
					
					it.setQuantity(it.getQuantity().intValue()-quantity);
					
					InventoryItem itnew=new InventoryItem();
					itnew.setInventory_item_id(it.getInventory_item_id());
					itnew.setQuantity(quantity);
					inventoryItemUpdateList.add(itnew);
					
					InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
					inventoryItemDetail.setOrder_id(order_id);
					inventoryItemDetail.setProduct_id(product_id);
					inventoryItemDetail.setCustomer_id(it.getCustomer_id());
					inventoryItemDetail.setWarehouse_id(it.getWarehouse_id());
					inventoryItemDetail.setInventory_item_id(it.getInventory_item_id());
					inventoryItemDetail.setOrder_goods_id(order_goods_id);
					inventoryItemDetail.setChange_quantity(-quantity);
					inventoryItemDetail.setPackbox_customer_id(0);
					inventoryItemDetail.setPackbox_warehouse_id(0);
					inventoryItemDetail.setCreated_time(new Date());
					inventoryItemDetail.setCreated_user("system");
					inventoryItemDetail.setLast_updated_user("system");
					inventoryItemDetail.setLast_updated_time(new Date());
					
					inventoryItemDetailList.add(inventoryItemDetail);
					
					InventoryItemVariance inventoryItemVariance = new InventoryItemVariance();
					inventoryItemVariance.setComments("库存调整");
					inventoryItemVariance.setCreated_user("system");
					inventoryItemVariance.setInventory_item_id(it.getInventory_item_id());
					inventoryItemVariance.setQuantity(-quantity);
					inventoryItemVariance.setCreated_time(new Date());
					inventoryItemVariance.setLast_updated_time(new Date());
					inventoryItemVarianceList.add(inventoryItemVariance);
				}
				
				
				
				for(ProductLocation pl:productLocationList){
					if(pl.getProduct_id()-product_id==0&&pl.getStatus().equalsIgnoreCase(status)&&pl.getQty_total()>0){
						if(goods_number>num+pl.getQty_total()){
							
							num+=pl.getQty_total();
							
							ProductLocation plnew=new ProductLocation();
							plnew.setPl_id(pl.getPl_id());
							plnew.setQty_total(pl.getQty_total());  //变得值 正数
							
							updateProductlocationList.add(plnew);
							
							ProductLocationDetail productLocationDetail = new ProductLocationDetail();
							productLocationDetail.setPl_id(pl.getPl_id());
							productLocationDetail.setChange_quantity(0-pl.getQty_total());
							productLocationDetail.setTask_id(0); // 任务ID
							productLocationDetail.setDescription("VARIANCE MINUS");
							productLocationDetail.setCreated_user("SYSTEM");
							productLocationDetail.setLast_updated_user("SYSTEM");
							productLocationDetail.setOrder_id(order_id);
							productLocationDetail.setOrder_goods_id(order_goods_id);
							

							pldList.add(productLocationDetail);
							
							pl.setQty_total(0);
						}else{
							ProductLocation plnew=new ProductLocation();
							plnew.setPl_id(pl.getPl_id());
							plnew.setQty_total(goods_number-num);  //变得值 正数
							
							updateProductlocationList.add(plnew);
							
							ProductLocationDetail productLocationDetail = new ProductLocationDetail();
							productLocationDetail.setPl_id(pl.getPl_id());
							productLocationDetail.setChange_quantity(num-goods_number);
							productLocationDetail.setTask_id(0); // 任务ID
							productLocationDetail.setDescription("VARIANCE MINUS");
							productLocationDetail.setCreated_user("SYSTEM");
							productLocationDetail.setLast_updated_user("SYSTEM");
							productLocationDetail.setOrder_id(order_id);
							productLocationDetail.setOrder_goods_id(order_goods_id);
							
							pldList.add(productLocationDetail);
							
							pl.setQty_total(pl.getQty_total()-goods_number+num);
							
							break;
						}
					}
				}
			}
		}
		
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(inventoryItemUpdateList)) {
			//归并其中一部分主键相同的更新记录 quantity
			Map<Integer,InventoryItem> updateItMap =new HashMap<Integer,InventoryItem>();
			List<InventoryItem> updateItList=new ArrayList<InventoryItem>();
			for(InventoryItem it:inventoryItemUpdateList){
				//如果没有则加到记录中
				if(updateItMap.get(it.getInventory_item_id())==null){
					updateItMap.put(it.getInventory_item_id(), it);
					updateItList.add(it);
				}else{
					InventoryItem it2=updateItMap.get(it.getInventory_item_id());
					it2.setQuantity(it2.getQuantity()+it.getQuantity());
				}
			}
			
			inventoryDao
				.updateInventoryItemByVariance(updateItList);
		}
		
		if(!WorkerUtil.isNullOrEmpty(pldList)){
			productLocationDetailDao.batchInsert2(pldList);
		}
		
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(updateProductlocationList)) {
			//归并其中一部分主键相同的更新记录（使用 qty_available - ${item2.qty_available}）所以分配里面记录的变化量应该是差异值，且未正数
			Map<Integer,ProductLocation> updatePlMap =new HashMap<Integer,ProductLocation>();
			List<ProductLocation> updatePlList=new ArrayList<ProductLocation>();
			for(ProductLocation pl:updateProductlocationList){
				//如果没有则加到记录中
				if(updatePlMap.get(pl.getPl_id())==null){
					updatePlMap.put(pl.getPl_id(), pl);
					updatePlList.add(pl);
				}else{
					ProductLocation pl2=updatePlMap.get(pl.getPl_id());
					pl2.setQty_total(pl2.getQty_total()+pl.getQty_total());
				}
			}
			
			productLocationDao
				.updateProductLocationByVariance(updatePlList);
		}
		
		if(!WorkerUtil.isNullOrEmpty(inventoryItemDetailList)){
			inventoryItemDetailDao.batchInsert(inventoryItemDetailList);
		}
		if(!WorkerUtil.isNullOrEmpty(inventoryItemVarianceList)){
			inventoryItemVarianceDao.batchInsert(inventoryItemVarianceList);
		}
		
		if(!WorkerUtil.isNullOrEmpty(orderIdUpdateList)){
			orderProcessDao.updateBatchOrderProcess(orderIdUpdateList,"FULFILLED");
			orderInfoDao.updateBatchOrderInfos(orderIdUpdateList,"FULFILLED");
		}
		
	}

	@Override
	public void doVarianceAdd(int physical_warehouse_id, int customer_id,
			Map<String, List<Map>> varianceAddImproveOrderGoodsMap,
			List<Integer> productIdList, List<Integer> orderIdList,
			List<Integer> orderGoodIdList, int warehouseId) {

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp ts = null;
		String validity = "1970-01-01 00:00:00";
		try {
			ts = new Timestamp(sdf.parse(validity).getTime());
		} catch (ParseException e) {
			logger.info("createAcceptInventoryTransactionSingleReal parse error validity:"
					+ validity);
		}
	
		
		List<OrderInfo> lockList=orderInfoDao.selectOrderInfoListByIdForUpdate(orderIdList);
		
		Map<String,OrderInfo> orderInfoMap=new HashMap<String,OrderInfo>();
		
		for(OrderInfo oi:lockList){
			orderInfoMap.put(oi.getOrder_id()+"", oi);
		}
		
		List<ProductLocation> productLocationList=productLocationDao.selectProductlocationListV5(physical_warehouse_id, productIdList, Location.LOCATION_TYPE_VARIANCE_STOCK_ADD,warehouseId);
		

		Map<String,Integer> avaliableMap=new HashMap<String,Integer>();
		
		for(ProductLocation pl:productLocationList){
			String key=pl.getProduct_id()+"_"+pl.getStatus();
			if(null!=avaliableMap.get(key)){
				avaliableMap.put(key, avaliableMap.get(key)+pl.getQty_total());
			}else{
				avaliableMap.put(key,pl.getQty_total());
			}
		}
				
		List<InventoryItemDetail> inventoryItemDetailList=new ArrayList<InventoryItemDetail>();
		List<InventoryItemVariance> inventoryItemVarianceList=new ArrayList<InventoryItemVariance>();
		
		List<Integer> inventoryItemIdList=new ArrayList<Integer>();
		List<Integer> orderIdUpdateList=new ArrayList<Integer>();
		
		List<ProductLocation> updateProductlocationList = new ArrayList<ProductLocation>();
		List<ProductLocationDetail>  pldList=new ArrayList<ProductLocationDetail>();
		Date nowTime = new Date();
		for(List<Map> li:varianceAddImproveOrderGoodsMap.values()){
			

			boolean isAvailable=true;
			for(Map m:li){
				int product_id = null==m.get("product_id")?0:Integer.parseInt(m.get("product_id").toString());
				int goods_number = null==m.get("goods_number")?0:Integer.parseInt(m.get("goods_number").toString());
				String status= null==m.get("status_id")?"NORMAL":m.get("status_id").toString();
				
				int available=null==avaliableMap.get(product_id+"_"+status)?0:avaliableMap.get(product_id+"_"+status);
				
				if(goods_number>available){
					isAvailable=false;
					break;
				}
			}
			if(!isAvailable){
				continue;
			}
									
			for(Map m:li){
				int order_id = null==m.get("order_id")? 0:Integer.parseInt(m.get("order_id").toString());
				int order_goods_id = null==m.get("order_goods_id")? 0:Integer.parseInt(m.get("order_goods_id").toString());
				int product_id = null==m.get("product_id")?0:Integer.parseInt(m.get("product_id").toString());
				int goods_number = null==m.get("goods_number")?0:Integer.parseInt(m.get("goods_number").toString());
				int warehouse_id = null==m.get("warehouse_id")?0:Integer.parseInt(m.get("warehouse_id").toString());
				String status= null==m.get("status_id")?"NORMAL":m.get("status_id").toString();
				String batch_sn= null==m.get("batch_sn")?"":m.get("batch_sn").toString();
				int available=null==avaliableMap.get(product_id+"_"+status)?0:avaliableMap.get(product_id+"_"+status);
				avaliableMap.put(product_id+"_"+status, available-goods_number);
				
				orderIdUpdateList.add(order_id);
				int num = 0;
				
				OrderInfo oi=orderInfoMap.get(order_id+"");
				
				InventoryItem inventoryItem=new InventoryItem();
				
				inventoryItem.setProduct_id(product_id);
				inventoryItem.setSerial_number("");
				inventoryItem.setStatus(status);
				inventoryItem.setProvider_code(oi.getProvider_code());
				inventoryItem.setQuantity(goods_number);
				inventoryItem.setCustomer_id(oi.getCustomer_id());
				inventoryItem.setInventory_item_acct_type_id(oi.getProvider_order_type());
				inventoryItem.setWarehouse_id(warehouse_id);
				inventoryItem.setPhysical_warehouse_id(physical_warehouse_id);
				inventoryItem.setParent_inventory_item_id(0);
				inventoryItem.setRoot_inventory_item_id(0);
				inventoryItem.setUnit_cost(new BigDecimal(m.get("goods_price").toString()));
				inventoryItem.setValidity(ts);
				inventoryItem.setBatch_sn(batch_sn);
				inventoryItem.setCurrency(oi.getCurrency());
				inventoryItem.setCreated_time(nowTime);
				inventoryItem.setCreated_user("SYSTEM");
				inventoryItem.setLast_updated_time(nowTime);
				inventoryItem.setLast_updated_user("SYSTEM");

				inventoryItemDao.insert(inventoryItem);
				
				inventoryItemIdList.add(inventoryItem.getInventory_item_id());
				
				InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
				inventoryItemDetail.setOrder_id(order_id);
				inventoryItemDetail.setProduct_id(product_id);
				inventoryItemDetail.setCustomer_id(oi.getCustomer_id());
				inventoryItemDetail.setWarehouse_id(oi.getWarehouse_id());
				inventoryItemDetail.setInventory_item_id(inventoryItem.getInventory_item_id());
				inventoryItemDetail.setOrder_goods_id(order_goods_id);
				inventoryItemDetail.setChange_quantity(goods_number);
				inventoryItemDetail.setPackbox_customer_id(0);
				inventoryItemDetail.setPackbox_warehouse_id(0);
				inventoryItemDetail.setCreated_time(new Date());
				inventoryItemDetail.setCreated_user("system");
				inventoryItemDetail.setLast_updated_user("system");
				inventoryItemDetail.setLast_updated_time(new Date());
				
				inventoryItemDetailList.add(inventoryItemDetail);
				
				InventoryItemVariance inventoryItemVariance = new InventoryItemVariance();
				inventoryItemVariance.setComments("库存调整");
				inventoryItemVariance.setCreated_user("system");
				inventoryItemVariance.setInventory_item_id(inventoryItem.getInventory_item_id());
				inventoryItemVariance.setQuantity(goods_number);
				inventoryItemVariance.setCreated_time(new Date());
				inventoryItemVariance.setLast_updated_time(new Date());
				inventoryItemVarianceList.add(inventoryItemVariance);
				
				for(ProductLocation pl:productLocationList){
					if(pl.getProduct_id()-product_id==0&&pl.getStatus().equalsIgnoreCase(status)&&pl.getQty_total()>0){
						if(goods_number>num+pl.getQty_total()){
							
							num+=pl.getQty_total();
							
							ProductLocation plnew=new ProductLocation();
							plnew.setPl_id(pl.getPl_id());
							plnew.setQty_total(pl.getQty_total());  //变得值 正数
							
							updateProductlocationList.add(plnew);
							
							ProductLocationDetail productLocationDetail = new ProductLocationDetail();
							productLocationDetail.setPl_id(pl.getPl_id());
							productLocationDetail.setChange_quantity(0-pl.getQty_total());
							productLocationDetail.setTask_id(0); // 任务ID
							productLocationDetail.setDescription("VARIANCE ADD");
							productLocationDetail.setCreated_user("SYSTEM");
							productLocationDetail.setLast_updated_user("SYSTEM");
							productLocationDetail.setOrder_id(order_id);
							productLocationDetail.setOrder_goods_id(order_goods_id);

							pldList.add(productLocationDetail);
							
							pl.setQty_total(0);
						}else{
							ProductLocation plnew=new ProductLocation();
							plnew.setPl_id(pl.getPl_id());
							plnew.setQty_total(goods_number-num);  //变得值 正数
							
							updateProductlocationList.add(plnew);
							
							ProductLocationDetail productLocationDetail = new ProductLocationDetail();
							productLocationDetail.setPl_id(pl.getPl_id());
							productLocationDetail.setChange_quantity(num-goods_number);
							productLocationDetail.setTask_id(0); // 任务ID
							productLocationDetail.setDescription("VARIANCE ADD");
							productLocationDetail.setCreated_user("SYSTEM");
							productLocationDetail.setLast_updated_user("SYSTEM");
							productLocationDetail.setOrder_id(order_id);
							productLocationDetail.setOrder_goods_id(order_goods_id);
							
							pldList.add(productLocationDetail);
							
							pl.setQty_total(pl.getQty_total()-goods_number+num);
							
							break;
						}
					}
				}
			}
		}

		
		if(!WorkerUtil.isNullOrEmpty(pldList)){
			productLocationDetailDao.batchInsert2(pldList);
		}
		
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(updateProductlocationList)) {
			//归并其中一部分主键相同的更新记录（使用 qty_available - ${item2.qty_available}）所以分配里面记录的变化量应该是差异值，且未正数
			Map<Integer,ProductLocation> updatePlMap =new HashMap<Integer,ProductLocation>();
			List<ProductLocation> updatePlList=new ArrayList<ProductLocation>();
			for(ProductLocation pl:updateProductlocationList){
				//如果没有则加到记录中
				if(updatePlMap.get(pl.getPl_id())==null){
					updatePlMap.put(pl.getPl_id(), pl);
					updatePlList.add(pl);
				}else{
					ProductLocation pl2=updatePlMap.get(pl.getPl_id());
					pl2.setQty_total(pl2.getQty_total()+pl.getQty_total());
				}
			}
			
			productLocationDao
				.updateProductLocationByVariance(updatePlList);
		}
		if(!WorkerUtil.isNullOrEmpty(inventoryItemIdList)){
			inventoryItemDao.batchupdate(inventoryItemIdList);
		}
		
		
		if(!WorkerUtil.isNullOrEmpty(inventoryItemDetailList)){
			inventoryItemDetailDao.batchInsert(inventoryItemDetailList);
		}
		if(!WorkerUtil.isNullOrEmpty(inventoryItemVarianceList)){
			inventoryItemVarianceDao.batchInsert(inventoryItemVarianceList);
		}
		
		if(!WorkerUtil.isNullOrEmpty(orderIdUpdateList)){
			orderProcessDao.updateBatchOrderProcess(orderIdUpdateList,"FULFILLED");
			orderInfoDao.updateBatchOrderInfos(orderIdUpdateList,"FULFILLED");
		}
		
	
		
	}

	@Override
	public Response PrePackBoxOut(Integer physical_warehouse_id,
			Integer customer_id, int order_id, String order_sn,
			int label_prepack_id, int packbox_product_id, int packbox_need_out, int warehouse_id) {
		        // 1.初始化返回结果
				Response result = ResponseFactory.createOkResponse("耗材出库成功!");
				result.setCode(Response.OK);
				logger.info("耗材出库开始,packbox_product_id: " + packbox_product_id);

				// 2.获取inventoryItem  和productLocation记录
				List<Integer> productIdList=new ArrayList<Integer>();
				productIdList.add(packbox_product_id);
				
				int plsum=0;
				int itsum=0;
				List<ProductLocation> productlocationList = productLocationDao
						.selectProductlocationListV4(physical_warehouse_id,
								productIdList, Location.LOCATION_TYPE_PACKBOX,"NORMAL",warehouse_id);
				
				List<InventoryItem> inventoryItemList=inventoryItemDao.selectInventoryItemListV2(physical_warehouse_id,warehouse_id,
						productIdList, "NORMAL");
				
				List<InventoryItem> updateInventoryItemList = new ArrayList<InventoryItem>();
				List<ProductLocation> updateProductlocationList = new ArrayList<ProductLocation>();
				List<ProductLocationDetail> pldList=new  ArrayList<ProductLocationDetail>();
				List<InventoryItemDetail> inventoryItemDetailList=new ArrayList<InventoryItemDetail>();

				for(ProductLocation pl:productlocationList){
					plsum += pl.getQty_available();
				}
				
				for(InventoryItem it:inventoryItemList){
					itsum += it.getQuantity();
				}

				//求出可以扣减的数量
				packbox_need_out=packbox_need_out<(plsum<itsum?plsum:itsum)?packbox_need_out:(plsum<itsum?plsum:itsum);
				if(packbox_need_out<=0){
					return result;
				}
				//扣减productlocation
				int count=0;
				for(ProductLocation pl:productlocationList){
					if(packbox_need_out-count>pl.getQty_available()){
						count += pl.getQty_available();
						
						ProductLocation plnew =new ProductLocation();
						plnew.setPl_id(pl.getPl_id());
						plnew.setQty_available(pl.getQty_available());//变化值
						updateProductlocationList.add(plnew);
						
						ProductLocationDetail productLocationDetail = new ProductLocationDetail();
						productLocationDetail.setPl_id(pl.getPl_id());
						productLocationDetail.setChange_quantity(0-pl.getQty_available());
						productLocationDetail.setTask_count_id(0);
						productLocationDetail.setOrder_id(order_id);
						productLocationDetail.setDescription("预打包耗材出库");
						productLocationDetail.setCreated_user("system");
						productLocationDetail.setLast_updated_user("system");
						productLocationDetail.setCount_sn("");
						pldList.add(productLocationDetail);
						
					}else{
						ProductLocation plnew =new ProductLocation();
						plnew.setPl_id(pl.getPl_id());
						plnew.setQty_available(packbox_need_out-count);//变化值
						updateProductlocationList.add(plnew);
						
						ProductLocationDetail productLocationDetail = new ProductLocationDetail();
						productLocationDetail.setPl_id(pl.getPl_id());
						productLocationDetail.setChange_quantity(count-packbox_need_out);
						productLocationDetail.setTask_count_id(0);
						productLocationDetail.setOrder_id(order_id);
						productLocationDetail.setDescription("预打包耗材出库");
						productLocationDetail.setCreated_user("system");
						productLocationDetail.setLast_updated_user("system");
						productLocationDetail.setCount_sn("");
						pldList.add(productLocationDetail);
						
						break;
					}
				}
				
				
				int countIt=0;
				for(InventoryItem it:inventoryItemList){
					if(packbox_need_out-countIt>it.getQuantity()){
						countIt += it.getQuantity();
						
						InventoryItem itnew=new InventoryItem();
						itnew.setInventory_item_id(it.getInventory_item_id());
						itnew.setQuantity(it.getQuantity());
						updateInventoryItemList.add(itnew);
						
						InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
						inventoryItemDetail.setOrder_id(0);
						inventoryItemDetail.setProduct_id(packbox_product_id);
						inventoryItemDetail.setCustomer_id(it.getCustomer_id());
						inventoryItemDetail.setWarehouse_id(it.getWarehouse_id());
						inventoryItemDetail.setInventory_item_id(it.getInventory_item_id());
						inventoryItemDetail.setOrder_goods_id(0);
						inventoryItemDetail.setChange_quantity(-it.getQuantity());
						inventoryItemDetail.setPackbox_customer_id(0);
						inventoryItemDetail.setPackbox_warehouse_id(0);
						inventoryItemDetail.setCreated_time(new Date());
						inventoryItemDetail.setCreated_user("system");
						inventoryItemDetail.setLast_updated_user("system");
						inventoryItemDetail.setLast_updated_time(new Date());
						inventoryItemDetail.setOrder_prepack_id(order_id);
						
						inventoryItemDetailList.add(inventoryItemDetail);
						
					}
					else{
						InventoryItem itnew=new InventoryItem();
						itnew.setInventory_item_id(it.getInventory_item_id());
						itnew.setQuantity(packbox_need_out-countIt);
						
						updateInventoryItemList.add(itnew);
						
						InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
						inventoryItemDetail.setOrder_id(0);
						inventoryItemDetail.setProduct_id(packbox_product_id);
						inventoryItemDetail.setCustomer_id(it.getCustomer_id());
						inventoryItemDetail.setWarehouse_id(it.getWarehouse_id());
						inventoryItemDetail.setInventory_item_id(it.getInventory_item_id());
						inventoryItemDetail.setOrder_goods_id(0);
						inventoryItemDetail.setChange_quantity(countIt-packbox_need_out);
						inventoryItemDetail.setPackbox_customer_id(0);
						inventoryItemDetail.setPackbox_warehouse_id(0);
						inventoryItemDetail.setCreated_time(new Date());
						inventoryItemDetail.setCreated_user("system");
						inventoryItemDetail.setLast_updated_user("system");
						inventoryItemDetail.setLast_updated_time(new Date());
						inventoryItemDetail.setOrder_prepack_id(order_id);
						
						inventoryItemDetailList.add(inventoryItemDetail);

						break;
					}
				}
				
				if(!WorkerUtil.isNullOrEmpty(updateInventoryItemList)){
					inventoryItemDao
					.updateInventoryItemByPackOut(updateInventoryItemList);
				}
				
				if(!WorkerUtil.isNullOrEmpty(updateProductlocationList)){
					productLocationDao
					.updateProductLocationByPackOut(updateProductlocationList);
				}
				
				
				if(!WorkerUtil.isNullOrEmpty(pldList)){
					productLocationDetailDao.batchInsert(pldList);
				}
				
				if(!WorkerUtil.isNullOrEmpty(inventoryItemDetailList)){
					inventoryItemDetailDao.batchInsert(inventoryItemDetailList);
				}
				
				labelPrepackDao.updatePackBoxNeedOut(label_prepack_id,packbox_need_out);
				return result;
	}
	
	/**
	 * 取消订单商品信息查询
	 * hchen1
	 */
	public Map<String,Object> selectByOrderId(Integer order_id,Integer physical_warehouse_id){
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		Map<String, Object> locationMap = new HashMap<String, Object>();
		List<Integer> productList = new ArrayList<Integer>();
		Integer goodsCategory = 0;
		Integer goodsNumber = 0 ;
		List<OrderGoods> orderGoodslList = orderDao.selectOrderGoodsByOrderIdV1(order_id);
		if (WorkerUtil.isNullOrEmpty(orderGoodslList)) {
			logger.info("没有相关的订单商品信息");
			resMap.put("success", false);
			resMap.put("note", "没有相关的订单商品信息");
			return resMap;
		}
		for (OrderGoods orderGoods : orderGoodslList) {
			if(!productList.contains(orderGoods.getProduct_id())){
				productList.add(orderGoods.getProduct_id());
				goodsCategory++;
			}
			 Map<String, Object> searchlocationMap2 = new HashMap<String, Object>();
				searchlocationMap2.put("warehouse_id", orderGoods.getWarehouse_id());
				searchlocationMap2.put("physical_warehouse_id", physical_warehouse_id.toString());
				searchlocationMap2.put("location_type", Location.LOCATION_TYPE_RETURN);
				List<String> locationList3 = inventoryDao.selectLocationByEmptyV1(searchlocationMap2);
				logger.info("良品库位"+locationList3);
				if(!WorkerUtil.isNullOrEmpty(locationList3)){
					locationMap.put("location_barcode_normal", locationList3.get(0).toString());
				}else {
					locationMap.put("location_barcode_normal", "0");
				}
			goodsNumber = goodsNumber + orderGoods.getGoods_number();
		}
		locationMap.put("goodsCategory", goodsCategory);
		locationMap.put("goodsNumber", goodsNumber);
		locationMap.put("success", true);

		return locationMap;
	}
	
	public Map<String,Object> createAllReturnAccept(Map<Object,Object> map){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ProductLocationDetail> insertProductLocationDetails = new ArrayList<ProductLocationDetail>();
		List<ProductLocation> updateProductLocations1 = new ArrayList<ProductLocation>();
		List<ProductLocation> updateProductLocations = new ArrayList<ProductLocation>();
		//Integer flag = 0;
		OrderInfo  orderInfo = orderDao.selectOrderInfoByIdForUpdate(Integer.parseInt(map.get("orderId").toString()));
		if(WorkerUtil.isNullOrEmpty(orderInfo)){
			resultMap.put("success", false);
			resultMap.put("result", "failure");
			resultMap.put("note", "订单不存在");
			return resultMap;
		}
		if(!orderInfo.ORDER_STATUS_CANCEL.equals(orderInfo.getOrder_status())){
			resultMap.put("success",false);
			resultMap.put("note","订单不是取消订单");
			return resultMap;
		}
		List<OrderGoods> orderGoodslList = orderDao.selectOrderGoodsByOrderIdV1(Integer.parseInt(map.get("orderId").toString()));
		if(WorkerUtil.isNullOrEmpty(orderGoodslList)){
			resultMap.put("success", false);
			resultMap.put("result", "failure");
			resultMap.put("note", "没有相关的商品信息");
			return resultMap;
		}
//		for (OrderGoods orderGoods : orderGoodslList) {
//			Integer productLocationDetailtotal1 = productLocationDetailDao.selectSumTotalByOrderId(orderGoods.getOrder_id(), orderGoods.getOrder_goods_id());
//			if(WorkerUtil.isNullOrEmpty(productLocationDetailtotal1)){
//				productLocationDetailtotal1=0;
//			}
//			if(orderGoods.getGoods_number()-productLocationDetailtotal1!=0){
//				flag=1;
//				break;
//			}
//		}
		List<Map<String,Object>> GroundingLists = inventoryDao.getOrderCancelListByOrderId(orderInfo.getOrder_id());
			if(WorkerUtil.isNullOrEmpty(GroundingLists)){
				resultMap.put("success", false);
				resultMap.put("result", "failure");
				resultMap.put("note", "该订单已经全部上架");
				return resultMap;
			}

		Map<String, Object> map_ = new HashMap<String, Object>();
		map_.put("locationBarcode", map.get("locationBarcode"));
		map_.put("physicalWarehouseId", map.get("physical_warehouse_id"));
		//取消上架只能退到return区和件拣货区,质检区
		Location location = inventoryDao.selectLocationByCodeV1(map_);
		if (WorkerUtil.isNullOrEmpty(location)) {
			resultMap.put("success", false);
			resultMap.put("result", "failure");
			resultMap.put("note", "库位不存在");
			return resultMap;
		}
		
		//校验库位信息的准确性  根据库位推荐原则
		for (OrderGoods orderGoods : orderGoodslList) {
		
			Map<String, Object> searchMapValidity = new HashMap<String, Object>();
			searchMapValidity.put("order_id", orderGoods.getOrder_id());
			searchMapValidity.put("product_id", orderGoods.getProduct_id());
			searchMapValidity.put("physical_warehouse_id", map.get("physical_warehouse_id"));
			Map<String, Object> validityMap =  productLocationDao.selectValidityByOrderId(searchMapValidity);
			String validity =validityMap.get("validity").toString();
			String batchSn = WorkerUtil.isNullOrEmpty(validityMap.get("batch_sn").toString())?"":validityMap.get("batch_sn").toString();
		    resultMap = validateLocation(location.getLocation_id(), orderGoods.getCustomer_id(), orderGoods.getProduct_id(), validity, batchSn,orderGoodslList,orderGoods.getWarehouse_id());
		    if(!WorkerUtil.isNullOrEmpty(resultMap)){
				return resultMap;
			}
	}
		try {
			
			//首先扣减中转区的库存  
			Integer task_id = null;
			String validity = null;
			String batch_sn = null;
			int num = 0; // 本商品需入库总数量
			int inNum = 0; // 每次需入库数量
			ProductLocation productLocation = new ProductLocation();
			for (Map<String, Object> og : GroundingLists) {
				Integer order_id =Integer.parseInt(og.get("order_id").toString());
				Integer order_goods_id =Integer.parseInt(og.get("order_goods_id").toString());
				Integer product_id =Integer.parseInt(og.get("product_id").toString());
				Integer goods_number =Integer.parseInt(og.get("goods_number").toString());
				//Integer productLocationDetailtotal = productLocationDetailDao.selectSumTotalByOrderId(orderGoods.getOrder_id(), orderGoods.getOrder_goods_id());
//				if(WorkerUtil.isNullOrEmpty(productLocationDetailtotal)){
//					productLocationDetailtotal=0;
//				}
//				if(orderGoods.getGoods_number()==productLocationDetailtotal){
//					continue;
//				}
//				Map<String, Object> searchMapValidity = new HashMap<String, Object>();
//				searchMapValidity.put("order_id", order_id);
//				searchMapValidity.put("product_id", product_id);
//				searchMapValidity.put("physical_warehouse_id", map.get("physical_warehouse_id"));
			    String serialNumber = "";
			    num = Integer.parseInt(og.get("diff").toString());
				Integer num1=num;
				Map<String, Object> searchMapTask = new HashMap<String, Object>();
				searchMapTask.put("order_id", order_id);
				searchMapTask.put("physicalWarehouseId", map.get("physical_warehouse_id"));
				searchMapTask.put("productId",product_id);
				List<Task> taskList = taskDao.getTaskIdByOrderProcess(searchMapTask);
				if(WorkerUtil.isNullOrEmpty(taskList)){
					throw new RuntimeException("该订单并没有生成拣货下架任务");
				}
				logger.info("开始扣减中转区库存");
				
				Integer pl_id =null;
				
				//拆分task依次进行扣减
				for (Task task : taskList) {
					ProductLocation productLocation1 = productLocationDao.getproduLocations(task.getTask_id(),task.getTo_pl_id());
					validity = productLocation1.getValidity();
					batch_sn = productLocation1.getBatch_sn();
					Integer qty_total = productLocation1.getQty_total();
					serialNumber = productLocation1.getSerial_number();
					Integer warehouse_id = productLocation1.getWarehouse_id();
					if(qty_total ==0){
						continue;
					}
					else if(num<=0){
						break;
					}
					else if(num<=qty_total){
						inNum = -1*num;
						num = 0;
					}else{
						inNum = -1*qty_total;
						num = num-qty_total;
					}
					task_id = task.getTask_id();
					//更新中转区库存值
//					ProductLocation productLocationUpdate = new ProductLocation();
//					productLocationUpdate.setQty_available(inNum);
//					productLocationUpdate.setQty_total(inNum);
//					productLocationUpdate.setPl_id(productLocation1.getPl_id());
//					updateProductLocations1.add(productLocationUpdate);
					productLocationDao.updateProductLocationTotal(inNum, productLocation1.getPl_id());
					ProductLocationDetail productLocationDetail = new ProductLocationDetail();
					productLocationDetail.setPl_id(productLocation1.getPl_id());
					productLocationDetail.setChange_quantity(inNum);
					productLocationDetail.setTask_id(task_id);
					productLocationDetail.setDescription("订单一键取消扣减中转区库存");
					productLocationDetail.setCreated_user(map.get("actionUser").toString());
					productLocationDetail.setLast_updated_user(map.get("actionUser").toString());
					productLocationDetail.setOrder_id(Integer.parseInt(map.get("orderId").toString()));
					productLocationDetail.setOrder_goods_id(order_goods_id);
					insertProductLocationDetails.add(productLocationDetail);
					//productLocationDetailDao.insert(productLocationDetail);		
				
				
						
					// 3.取消上架更新库存信息，插入Product_location
					Map<String, Object> paramsForPlMap = new HashMap<String, Object>();
					paramsForPlMap.put("productId", product_id);
					paramsForPlMap.put("status", "NORMAL");
					paramsForPlMap.put("validity", validity);
					paramsForPlMap.put("batchSn", batch_sn);
					paramsForPlMap.put("warehouseId", warehouse_id);
					if(WorkerUtil.isNullOrEmpty(serialNumber)){
						paramsForPlMap.put("serialNumber", "");
					}else {
						paramsForPlMap.put("serialNumber", serialNumber);
					}
					paramsForPlMap.put("locationId", location.getLocation_id());
					productLocation = inventoryDao.selectProductLocation(paramsForPlMap);
					// 4.更新订单状态,只有该退货订单里面所有商品都上架的商品才会更新订单状态
					logger.info("orderid : "+order_id + "ordergoodsid: "+order_goods_id + "inNum: "+inNum*-1);
					
					if (WorkerUtil.isNullOrEmpty(productLocation)) {
						productLocation = new ProductLocation();
						productLocation.setProduct_id(product_id);
						productLocation.setLocation_id(location.getLocation_id());
						productLocation.setQty_total(inNum*-1);
						productLocation.setQty_available(inNum*-1);
						productLocation.setWarehouse_id(warehouse_id);
						productLocation.setProduct_location_status("NORMAL");
						//productLocation.setQty_exception(0);
						productLocation.setStatus("NORMAL");
						if (!WorkerUtil.isNullOrEmpty(validity)) {
							productLocation.setValidity(validity.toString());
						}
						if (!WorkerUtil.isNullOrEmpty(batch_sn)) {
							productLocation.setBatch_sn(batch_sn);
						}else {
							productLocation.setBatch_sn("");
						}
						this.setValidityStatus(product_id, productLocation, validity);
						if(WorkerUtil.isNullOrEmpty(serialNumber)){
							productLocation.setSerial_number("");
						}else {
							productLocation.setSerial_number(serialNumber);
						}
						productLocation.setCreated_user(map.get("actionUser").toString());
						productLocation.setLast_updated_user(map.get("actionUser").toString());
						//insertProductLocations.add(productLocation);
						productLocationDao.insert(productLocation);
					} else {
						logger.info("查询取消订单的上架库位信息");
						if(!"NORMAL".equals(productLocation.getProduct_location_status())){
							throw new RuntimeException("该库位异常,不允许上架");
						}
	//					ProductLocation productLocationUpdate = new ProductLocation();
	//					productLocationUpdate.setQty_available(num1);
	//					productLocationUpdate.setQty_total(num1);
	//					productLocationUpdate.setPl_id(productLocation.getPl_id());
	//					updateProductLocations.add(productLocationUpdate);
						productLocationDao.updateProductLocation(inNum*-1,productLocation.getPl_id());
					}
						productLocationDetail = new ProductLocationDetail();
						productLocationDetail.setPl_id(productLocation.getPl_id());
						productLocationDetail.setChange_quantity(inNum*-1);
						productLocationDetail.setTask_id(task_id);
						productLocationDetail.setDescription("订单一键取消上架");
						productLocationDetail.setCreated_user(map.get("actionUser").toString());
						productLocationDetail.setLast_updated_user(map.get("actionUser").toString());
						productLocationDetail.setOrder_id(Integer.parseInt(map.get("orderId").toString()));
						productLocationDetail.setOrder_goods_id(order_goods_id);
						insertProductLocationDetails.add(productLocationDetail);
						//productLocationDetailDao.insert(productLocationDetail);
						if("Y".equals(location.getIs_empty())){
							locationDao.updateLocationIsEmpty1(location.getLocation_id());
								}
							}
			//校验库存扣减的准确性	
			if (num==0) {
				orderBiz.insertUserActionOrder(order_id, OrderInfo.ORDER_STATUS_CANCEL,map.get("actionUser").toString());
			}else{
				throw new RuntimeException("中转区库存不足");
			}
			}
			productLocationDetailDao.batchInsert3(insertProductLocationDetails);
			//productLocationDao.batchInsert(insertProductLocations);
//			if(!WorkerUtil.isNullOrEmpty(updateProductLocations)){
//				productLocationDao.updateProductLocationV5(updateProductLocations);
//			}
//			if(!WorkerUtil.isNullOrEmpty(updateProductLocations1)){
//				productLocationDao.updateProductLocationV6(updateProductLocations1);
//			}else {
//				throw new RuntimeException("中转区库存异常");
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上架失败", e);
			resultMap.put("success", false);
			resultMap.put("note", e.getMessage());
			throw new RuntimeException("上架失败,原因" + e.getMessage());
		}
		resultMap.put("result", "success");
		resultMap.put("success", true);
		resultMap.put("note", "success");
		return resultMap;
	}
	
	// RF移库之库存移动by商品条码&库位 -- 源库位检查
	@Override
	public Map<String, Object> stockMoveLocationGoodsCheck(
			String locationBarcode,Integer physicalWarehouseId,String goodsBarcode) {
		Map<String,Object> resMap = new HashMap<String,Object>();
		//1. 将locationBarcode转成A010101格式
		locationBarcode = locationBarcode.replaceAll("-","").toUpperCase();
		locationBarcode = locationBarcode.replaceAll("——","").toUpperCase();
		List<String> locationBarcodeList = new ArrayList<String>();
		locationBarcodeList.add(locationBarcode);
		//2. select 
		List<Location> locationTypeEmptyList = locationDao.checkLocationTypeByBarcode(locationBarcodeList,physicalWarehouseId);
		if(WorkerUtil.isNullOrEmpty(locationTypeEmptyList)){
			//3. 空判断 ,不存在
			resMap.put("success", false);
			resMap.put("error", "用户所属物理仓没有此库位信息！");
			return resMap;
		}
		Location locationTypeEmpty = locationTypeEmptyList.get(0);
		if("Y".equals(locationTypeEmpty.getIs_empty())){
		
			resMap.put("success", false);
			resMap.put("error", "库位上无商品信息，不需要移动！");
		}else{
			Integer locationId = Integer.parseInt(String.valueOf(locationTypeEmpty.getLocation_id()));
			String locationType = String.valueOf(locationTypeEmpty.getLocation_type());
			//6. 返回数据
			List<Map<String,Object>> list = productLocationDao.selectAllProductLocationByLocationId(locationId,"",goodsBarcode);
			if(WorkerUtil.isNullOrEmpty(list)){
				resMap.put("success", false);
				resMap.put("error", "库位上无此商品信息，不需要移动！");
			}else{
				resMap.put("success", true);
				Integer plSize = list.size();
				String status = String.valueOf(list.get(0).get("status"));
				String productName = String.valueOf(list.get(0).get("product_name"));
				resMap.put("locationType", locationType);
				resMap.put("productName", productName);
				resMap.put("plSize", plSize);
				resMap.put("status", status);
				resMap.put("list", list);
			}
		}
		return resMap;
	}
	
	/**
	 * 新订单退货入库
	 * hchen1
	 */
	@Override
	public Response returnNewAcceptInventory(Integer orderId,
			List<ReturnProductAccDetail> returnProductAccDetails,
			String actionUser) {
		logger.info("（退货）入库开始, orderId: " + orderId + ", actionUser: "
				+ actionUser);

		// 初始化
		Response response = new Response(Response.OK, "退货入库成功!");
		
		try {
			// 退货商品列表不能为空
			Assert.isTrue(!returnProductAccDetails.isEmpty(), "退货商品列表为空!");

			// 查询订单信息，同时锁定订单
			OrderInfo orderInfo = orderDao
					.selectOrderInfoByIdForUpdate(orderId);
			Assert.isTrue(OrderInfo.ORDER_STATUS_ACCEPT.equals(orderInfo
					.getOrder_status()), "该退货订单不是待入库状态!");
			
			// 遍历退货商品数据
			for (ReturnProductAccDetail returnProductAccDetail : returnProductAccDetails) {
				int num = returnProductAccDetail.getNum(); // 本商品需入库总数量
				int inNum = num; // 每次需入库数量
				Product product = productDao.selectByPrimaryKey(returnProductAccDetail.getProductId());
				String location_type =null;
				Integer locationId = null;
				String validity ="1970-01-01 00:00:00";
				String batch_sn = returnProductAccDetail.getBatch_sn();
				String providecode =null;
				String inventoryItemAcctTypeId = null;
				if("NORMAL".equals(returnProductAccDetail.getStatus())){
					location_type = "RETURN_NORMAL_LOCATION";
				}else{
					location_type = "RETURN_DEFECTIVE_LOCATION";
				}
				Location location =locationDao.selectLocationByLocationType(orderInfo.getPhysical_warehouse_id(), location_type);
				//创建虚拟库位
				if("RETURN_NORMAL_LOCATION".equals(location_type) && WorkerUtil.isNullOrEmpty(location)){
					response.setMsg("库位暂存区中不存在良品退货区库位,请维护");
					response.setCode(Response.ERROR);
					return response;
				
				}else if("RETURN_DEFECTIVE_LOCATION".equals(location_type) && WorkerUtil.isNullOrEmpty(location))
				{
					response.setMsg("库位暂存区中不存在不良品退货区库位，请维护");
					response.setCode(Response.ERROR);
					return response;
				}else if(!WorkerUtil.isNullOrEmpty(locationDao.selectLocationByLocationType(orderInfo.getPhysical_warehouse_id(), location_type))){
					locationId = locationDao.selectLocationByLocationType(orderInfo.getPhysical_warehouse_id(), location_type).getLocation_id();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
				if("Y".equals(product.getIs_maintain_warranty())){
					//InventoryItem inventoryItem = inventoryItemDao.selectByProductId(returnProductAccDetail.getProductId());
					validity =returnProductAccDetail.getValidity() ;
//					providecode = inventoryItem.getProvider_code();
//					inventoryItemAcctTypeId = inventoryItem.getInventory_item_acct_type_id();
				}
				
				 logger.info("validity"+validity+"batch_sn"+batch_sn);
						// 创建单个入库作业
						response = this
								.createNewReturnAcceptInventoryTransactionSingle(
										orderId,
										returnProductAccDetail.getProductId(),
										returnProductAccDetail.getSerialNumber(),
										returnProductAccDetail.getStatus(),
										WorkerUtil.isNullOrEmpty(providecode)?"":providecode,
										Integer.valueOf(inNum),
										orderInfo.getCustomer_id(),
										WorkerUtil.isNullOrEmpty(inventoryItemAcctTypeId)?"":inventoryItemAcctTypeId,
										orderInfo.getWarehouse_id(), 
										orderInfo.getPhysical_warehouse_id(),
										new BigDecimal(0),
										sdf.parse(validity),
										batch_sn,
										"RMB", Integer.valueOf(returnProductAccDetail.getOrderGoodsId()),
										actionUser);
						Assert.isTrue(
								Response.OK.equals(response.getCode()),
								response.getMsg()
										+ ", orderId: "
										+ orderId
										+ ", productId: "
										+ returnProductAccDetail.getProductId()
										+ ", orderGoodsId: "
										+ returnProductAccDetail
												.getOrderGoodsId()
										+ ", serialNumber: "
										+ returnProductAccDetail
												.getSerialNumber()
										+ ", status: "
										+ returnProductAccDetail.getStatus());
						
						ProductLocation productLocation = new ProductLocation();
						Map<String, Object> paramsForPlMap = new HashMap<String, Object>();
						paramsForPlMap.put("productId", returnProductAccDetail.getProductId());
						paramsForPlMap.put("status", returnProductAccDetail.getStatus());
						paramsForPlMap.put("validity", validity);
						paramsForPlMap.put("batchSn", returnProductAccDetail.getBatch_sn());
						paramsForPlMap.put("serialNumber", returnProductAccDetail.getSerialNumber());
						paramsForPlMap.put("locationId", locationId);
						paramsForPlMap.put("warehouseId", orderInfo.getWarehouse_id());
						productLocation = inventoryDao.selectProductLocation(paramsForPlMap);
						if(WorkerUtil.isNullOrEmpty(productLocation)){
					    productLocation = new ProductLocation();
						productLocation.setLocation_id(locationId);
						productLocation.setProduct_id(returnProductAccDetail.getProductId());
						productLocation.setQty_total(inNum);
						productLocation.setQty_reserved(0);
						productLocation.setQty_available(inNum);
						productLocation.setWarehouse_id(orderInfo.getWarehouse_id());
						productLocation.setProduct_location_status("NORMAL");
						productLocation.setQty_freeze(0);
						this.setValidityStatus(returnProductAccDetail.getProductId(), productLocation, validity);
						productLocation.setStatus(returnProductAccDetail.getStatus());
						productLocation.setBatch_sn(batch_sn);
						productLocation.setValidity(validity);//WorkerUtil.isNullOrEmpty(inventoryItem.getValidity())?"1970-01-01 00:00:00":sdf.format(inventoryItem.getValidity()));
//						if(WorkerUtil.isNullOrEmpty(productLocation.getValidity())){
//							productLocation.setValidity("1970-01-01 00:00:00");
//						}
						productLocation.setSerial_number(WorkerUtil.isNullOrEmpty(returnProductAccDetail.getSerialNumber())?"":returnProductAccDetail.getSerialNumber().toString());
						productLocation.setCreated_user(actionUser);
						productLocation.setLast_updated_user(actionUser);
						productLocationDao.insert(productLocation);
						}else{
							productLocationDao.updateProductLocation(inNum,
									productLocation.getPl_id());
						}
						ProductLocationDetail productLocationDetail = new ProductLocationDetail();
						productLocationDetail.setPl_id(productLocation.getPl_id());
						productLocationDetail.setChange_quantity(inNum);
						productLocationDetail.setTask_id(0);
						productLocationDetail.setDescription("订单退货上架");
						productLocationDetail.setCreated_user(actionUser);
						productLocationDetail.setLast_updated_user(actionUser);
						productLocationDetail.setOrder_id(orderId);
						productLocationDetail.setOrder_goods_id(returnProductAccDetail.getOrderGoodsId());
						productLocationDetailDao.insert(productLocationDetail);
					
				if("Y".equals(location.getIs_empty())){
					locationDao.updateLocationIsEmpty1(location.getLocation_id());
				}
			}
		 

				// 更新订单状态
				int effectRows = orderBiz.updateOrderInfoStatus(orderId,
						OrderInfo.ORDER_STATUS_FULFILLED, actionUser);
				Assert.isTrue(effectRows > 0, "订单状态修改失败, orderId:" + orderId);
		
				// 记录订单操作日志
				UserActionOrder userActionOrder = new UserActionOrder();
				userActionOrder.setAction_note("订单完成退货入库");
				userActionOrder.setAction_type("ACCEPT");
				userActionOrder.setCreated_time(new Date());
				userActionOrder.setCreated_user(actionUser);
				userActionOrder.setOrder_id(orderId);
				userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_FULFILLED);
				userActionOrderDao.insert(userActionOrder);
		
				logger.info("退货入库结束,orderId: " + orderId + ", actionUser: "
						+ actionUser);
		}catch (Exception e) {
			logger.error("操作退货入库时发生异常，异常信息：" + e.getMessage()
					+ ", 退货订单orderId: " + orderId.toString(), e);
			throw new RuntimeException("操作退货入库时发生异常，异常信息：" + e.getMessage()
					+ " 退货订单orderId: " + orderId.toString());
		}
		return response;
	}
	
	
	/**
	 * hchen1
	 * 新退货入库
	 * @param orderId
	 * @param productId
	 * @param serialNumber
	 * @param status
	 * @param providerCode
	 * @param thisTurnInNum
	 * @param customerId
	 * @param inventoryItemAcctTypeId
	 * @param warehouseId
	 * @param physicalWarehouseId
	 * @param parentInventoryItemId
	 * @param rootInventoryItemId
	 * @param unitCost
	 * @param validity
	 * @param batchSn
	 * @param currency
	 * @param orderGoodsId
	 * @param actionUser
	 * @return
	 */
	private Response createNewReturnAcceptInventoryTransactionSingle(
			Integer orderId, Integer productId, String serialNumber,
			String status, String providerCode, Integer thisTurnInNum,
			Integer customerId, String inventoryItemAcctTypeId,
			Integer warehouseId, Integer physicalWarehouseId,
			BigDecimal unitCost, Date validity, String batchSn,
			String currency, Integer orderGoodsId, String actionUser) {
		// 初始化
		Response response = new Response(Response.OK, "入库成功");
		Date nowTime = new Date();

		// insert inventory_item表记录
		InventoryItem inventoryItem = new InventoryItem();
		inventoryItem.setProduct_id(productId);
		inventoryItem.setSerial_number(serialNumber);
		inventoryItem.setStatus(status);
		inventoryItem.setProvider_code(providerCode);
		inventoryItem.setQuantity(thisTurnInNum);
		inventoryItem.setCustomer_id(customerId);
		inventoryItem.setInventory_item_acct_type_id(inventoryItemAcctTypeId);
		inventoryItem.setWarehouse_id(warehouseId);
		inventoryItem.setPhysical_warehouse_id(physicalWarehouseId);
		inventoryItem.setParent_inventory_item_id(0);
		inventoryItem.setRoot_inventory_item_id(0);
		inventoryItem.setUnit_cost(unitCost);
		inventoryItem.setValidity(validity);
		inventoryItem.setBatch_sn(batchSn);
		inventoryItem.setCurrency(currency);
		inventoryItem.setCreated_time(nowTime);
		inventoryItem.setCreated_user(actionUser);
		inventoryItem.setLast_updated_time(nowTime);
		inventoryItem.setLast_updated_user(actionUser);

		inventoryItemDao.insert(inventoryItem);
		if (inventoryItem.getInventory_item_id().intValue() <= 0) {
			response = ResponseFactory
					.createErrorResponse("INSERT InventoryItem FAILED!");
			return response;
		}

		// insert inventory_item_detail表记录
		InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
		inventoryItemDetail.setChange_quantity(thisTurnInNum);
		inventoryItemDetail.setCustomer_id(customerId);
		inventoryItemDetail.setInventory_item_id(inventoryItem
				.getInventory_item_id());
		inventoryItemDetail.setOrder_goods_id(orderGoodsId);
		inventoryItemDetail.setOrder_id(orderId);
		inventoryItemDetail.setPackbox_customer_id(Integer.valueOf(0));
		inventoryItemDetail.setPackbox_warehouse_id(Integer.valueOf(0));
		inventoryItemDetail.setProduct_id(productId);
		inventoryItemDetail.setWarehouse_id(warehouseId);
		inventoryItemDetail.setCreated_time(nowTime);
		inventoryItemDetail.setCreated_user(actionUser);
		inventoryItemDetail.setLast_updated_time(nowTime);
		inventoryItemDetail.setLast_updated_user(actionUser);

		inventoryItemDetailDao.insert(inventoryItemDetail);
		if (inventoryItemDetail.getInventory_item_detail_id().intValue() <= 0) {
			response = ResponseFactory
					.createErrorResponse("INSERT InventoryItemDetail FAILED!");
			return response;
		}

		return response;
	}
	
	
	
	public Map<String,Object> getMoveList(Map<String,Object> map){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String, Object>> moveList = new ArrayList<Map<String,Object>>();
		moveList = inventoryDaoSlave.selectMoveListByPage(map);
		resultMap.put("moveList", moveList);
		return resultMap;
	}
	
	
	public Map<String,Object> getRecheckList(Map<String,Object> map){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String, Object>> RecheckList = new ArrayList<Map<String,Object>>();
		RecheckList = inventoryDaoSlave.selectRecheckListByPage(map);
		resultMap.put("RecheckList", RecheckList);
		return resultMap;
	}
	
	
	public Map<String,Object> getPickList(Map<String,Object> map){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String, Object>> PickList = new ArrayList<Map<String,Object>>();
		PickList = inventoryDaoSlave.selectPickListByPage(map);
		resultMap.put("PickList", PickList);
		return resultMap;
	}
	
	
	
	public Map<String,Object> getBhList(Map<String,Object> map){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String, Object>> BhList = new ArrayList<Map<String,Object>>();
		BhList = inventoryDaoSlave.selectBhListByPage(map);
		resultMap.put("BhList", BhList);
		return resultMap;
	}
	
	
	
	public Map<String,Object> getRukuList(Map<String,Object> map){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String, Object>> RukuList = new ArrayList<Map<String,Object>>();
		RukuList = inventoryDaoSlave.selectRukuListByPage(map);
		resultMap.put("RukuList", RukuList);
		return resultMap;
	}
	
	
	public Map<String,Object> getGroundingList(Map<String,Object> map){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String, Object>> GroundingAllList = new ArrayList<Map<String,Object>>();
		GroundingAllList = inventoryDaoSlave.selectGroundingPurchaseListByPage(map);
		resultMap.put("GroundingAllList", GroundingAllList);
		return resultMap;
	}
	
	
	public Map<String,Object> getMaintainList(Map<String,Object> map){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String, Object>> MaintainList = new ArrayList<Map<String,Object>>();
		MaintainList = inventoryDaoSlave.selectMaintainListByPage(map);
		resultMap.put("MaintainList", MaintainList);
		return resultMap;
	}
	
	
	
	public void setValidityStatus(Integer product_id,ProductLocation productLocation,String validity){
		Product product = productDao.selectByPrimaryKey(product_id);
		if("Y".equalsIgnoreCase(product.getIs_maintain_warranty())){
			productLocation.setValidity_status(
					ProductLocation.checkValidityStatus(WorkerUtil.isNullOrEmpty(validity)?"1970-01-01 00:00:00":validity,
					product.getValidity(), product.getValidity_unit(), product.getWarranty_warning_days(), product.getWarranty_unsalable_days()));
		}
		
	}
	
	
	//库位合法校验
		public Map<String, Object> validateLocation(Integer locationId,Integer customerId,Integer productId,String validity,String batch_sn,List<OrderGoods> orderGoodslList,Integer warehouse_id){
			
			Map<String, Object> map_ = new HashMap<String, Object>();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			map_.put("locationId", locationId);
			String isEmpty = null;
			String can_mix_product= null;
			String can_mix_batch= null;
			List<Map> isMixMap = replenishmentDao.selectLocationIsCanMix(map_);
			List<String> productList = new ArrayList<String>();
			List<String> validityList = new ArrayList<String>();
			List<String> batchList = new ArrayList<String>();
			List<String> statusList = new ArrayList<String>();
			List<Integer> warehouseList = new ArrayList<Integer>();
			Map<String, Object> qudaomap = new HashMap<String, Object>();
			logger.info("对库位信息进行校验");
			if (WorkerUtil.isNullOrEmpty(isMixMap)) {
				resultMap.put("result", Response.FAILURE);
				resultMap.put("success", Boolean.FALSE);
				resultMap.put("note", "目标库位有误");
				return resultMap;
			} else {
				for (Map map2 : isMixMap) {
					String product_id2 = WorkerUtil.isNullOrEmpty(map2
							.get("product_id")) ? "" : map2
							.get("product_id").toString();
					String validity2 = WorkerUtil.isNullOrEmpty(map2
							.get("validity")) ? "" : map2
							.get("validity").toString();
					String status2 = WorkerUtil.isNullOrEmpty(map2
							.get("status")) ? "" : map2
							.get("status").toString();
					String batchSn2 = WorkerUtil.isNullOrEmpty(map2
							.get("batch_sn")) ? "" : map2
							.get("batch_sn").toString();
					String customerId2 = WorkerUtil.isNullOrEmpty(map2
							.get("customer_id")) ? "" : map2
							.get("customer_id").toString();
					Integer warehouseId2 = WorkerUtil.isNullOrEmpty(map2
							.get("warehouse_id")) ? 0 : Integer.parseInt(map2
							.get("warehouse_id").toString());
					qudaomap.put(product_id2+warehouseId2, "0");
					productList.add(product_id2);
					validityList.add(validity2);
					batchList.add(batchSn2);
					statusList.add(status2);
					warehouseList.add(warehouseId2);
					isEmpty = map2.get("is_empty").toString();
					can_mix_product = map2.get("can_mix_product").toString();
					can_mix_batch = map2.get("can_mix_batch").toString();
					if(!WorkerUtil.isNullOrEmpty(customerId2)&& !customerId2.equals(customerId.toString())){
						resultMap.put("result", Response.FAILURE);
						resultMap.put("success", Boolean.FALSE);
						resultMap.put("note", "存储库位货主与商品货主不同");
						return resultMap;
					}
					if(WorkerUtil.isNullOrEmpty(orderGoodslList)){
						if("Y".equals(map2.get("is_empty").toString())){
							break;}
					}else  {
						if("Y".equals(map2.get("is_empty").toString()) && orderGoodslList.size()==1){
							break;
						}
						if("Y".equals(map2.get("is_empty").toString())&& orderGoodslList.size()>1) {
							if("0".equals(can_mix_batch) ||"0".equals(can_mix_product)){
								resultMap.put("result", Response.FAILURE);
								resultMap.put("success", Boolean.FALSE);
								resultMap.put("note", "存储库位不允许混放批次或者不允许混放商品");
								return resultMap;
							}
						}
					}
				}
				if("N".equals(isEmpty)){
					if (productList.contains(productId.toString())) {
						if(!validityList.contains(validity) || !batchList.contains(batch_sn)){
							if("0".equals(can_mix_batch)){
								resultMap.put("result", Response.FAILURE);
								resultMap.put("success", Boolean.FALSE);
								resultMap.put("note", "存储库位不允许混放批次");
								return resultMap;
							}
						}
						if (!qudaomap.containsKey(productId.toString()+warehouse_id.toString())) {
							
							resultMap.put("result", Response.FAILURE);
							resultMap.put("success", Boolean.FALSE);
							resultMap.put("note", "库位商品渠道不同不允许存放！请重新输入！");
							return resultMap;
							
						}
					}else{
						if("0".equals(can_mix_product)){
							resultMap.put("result", Response.FAILURE);
							resultMap.put("success", Boolean.FALSE);
							resultMap.put("note", "存储库位不允许混放商品");
							return resultMap;
						}
					}
				}	
			}
			return resultMap;
		}


	@Override
	public Map<String, Object> getStockOutList(Map<String, Object> searchMap) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String, Object>> stockOutList = inventoryDaoSlave.selectStockOutListByPage(searchMap);
		resultMap.put("result", true);
		resultMap.put("stockOutList", stockOutList);
		Integer skuOutNum = 0;
		if(!WorkerUtil.isNullOrEmpty(searchMap.get("barcode"))){
			List<Map<String, Object>> stockOutListAll = inventoryDaoSlave.selectStockOutList(searchMap);
			for (Map<String, Object> map : stockOutListAll) {
				skuOutNum = skuOutNum +Integer.parseInt(String.valueOf(map.get("number")));
			}
		}
		resultMap.put("skuOutNum", skuOutNum);
		return resultMap;
	}

	@Override
	public List<Map<String, Object>> getStockOutListForExport(Map<String, Object> searchMap) {
		List<Map<String, Object>> stockOutList = inventoryDaoSlave.selectStockOutList(searchMap);
		return stockOutList;
	}



	
}
