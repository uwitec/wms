package com.leqee.wms.biz.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.biz.SupplierReturnBiz;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.InventoryItemDetailDao;
import com.leqee.wms.dao.InventorySummaryDao;
import com.leqee.wms.dao.LocationDao;
import com.leqee.wms.dao.OrderGoodsBatchDao;
import com.leqee.wms.dao.OrderGoodsDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.OrderReserveInventoryMappingDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ProductLocationDetailDao;
import com.leqee.wms.dao.SupplierReturnDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.dao.UserActionTaskDao;
import com.leqee.wms.entity.InventoryItemDetail;
import com.leqee.wms.entity.OrderGoodsBatch;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderReserveInventoryMapping;
import com.leqee.wms.entity.ProductLocationDetail;
import com.leqee.wms.entity.Task;
import com.leqee.wms.entity.UserActionTask;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.WorkerUtil;

@Service
public class SupplierReturnBizImpl implements SupplierReturnBiz {

	@Autowired
	SupplierReturnDao supplierReturnDao;
	@Autowired
	OrderProcessDao orderProcessDao;
	@Autowired
	ProductLocationDao productLocationDao;
	@Autowired 
	ProductLocationDetailDao productLocationDetailDao;
	@Autowired
	LocationDao locationDao;
	@Autowired
	TaskDao taskDao;
	@Autowired
	UserActionTaskDao userActionTaskDao;
	@Autowired
	InventoryDao inventoryDao;
	@Autowired
	InventoryItemDetailDao inventoryItemDetailDao;
	@Autowired
	InventorySummaryDao inventorySummaryDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderGoodsDao orderGoodsDao;
	@Autowired
	OrderReserveInventoryMappingDao orderReserveInventoryMappingDao;
	@Autowired
	OrderGoodsBatchDao orderGoodsBatchDao;
	
	@Override
	public List<Map<String, Object>> supplierReturnSearch(Map<String, Object> searchMap) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		String start = String.valueOf(searchMap.get("startTime"));
		String end = String.valueOf(searchMap.get("endTime"));
		if (start.equals("") || start.isEmpty()) {
			java.util.Date now = new java.util.Date();
			Calendar c = Calendar.getInstance();
			c.setTime(now);
			c.set(Calendar.DATE, c.get(Calendar.DATE) - 7); // 最近15天，即从之前的14天开始
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期

			start = dateFormat.format(c.getTime());
		}
		start += " 00:00:00";
		if (!end.equals("")) {
			end += " 23:59:59";
		}
		searchMap.put("start", start);
		searchMap.put("end", end);
		
		List<Map<String, Object>> supplierReturnList = supplierReturnDao.selectSupplierReturnByPage(searchMap);
		
		if(!WorkerUtil.isNullOrEmpty(supplierReturnList)){
			String orderId = "";
			String omsOrderSn = "";
			String orderStatus = "";
			String newOrderId = "";
			Map<String, Object> returnMap = new HashMap<String,Object>();
			List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
			for (Map<String, Object> supplierReturnMap : supplierReturnList) {
				newOrderId = String.valueOf(supplierReturnMap.get("order_id"));
				if(!orderId.equals(newOrderId)){
					if(!orderId.equals("")){
						returnMap.put("goodsList", goodsList);
						returnMap.put("order_id", orderId);
						returnMap.put("order_status", orderStatus);
						returnMap.put("oms_order_sn",omsOrderSn);
						returnList.add(returnMap);
						returnMap = new HashMap<String,Object>();
					}
					goodsList = new ArrayList<Map<String,Object>>();
					orderId = newOrderId;
					orderStatus = String.valueOf(supplierReturnMap.get("order_status"));
					omsOrderSn = String.valueOf(supplierReturnMap.get("oms_order_sn"));
				}
				Map<String,Object> goodsMap = new HashMap<String,Object>();
				goodsMap.put("customer_name", String.valueOf(supplierReturnMap.get("customer_name")));
				goodsMap.put("order_time", String.valueOf(supplierReturnMap.get("order_time")));
				goodsMap.put("warehouse_name", String.valueOf(supplierReturnMap.get("warehouse_name")));
				goodsMap.put("barcode", String.valueOf(supplierReturnMap.get("barcode")));
				goodsMap.put("product_name", String.valueOf(supplierReturnMap.get("goods_name")));
				goodsMap.put("goods_status", String.valueOf(supplierReturnMap.get("goods_status")));
				goodsMap.put("goods_number", String.valueOf(supplierReturnMap.get("goods_number")));
				goodsMap.put("out_num", String.valueOf(supplierReturnMap.get("out_num")));
				goodsList.add(goodsMap);
			}
			returnMap.put("goodsList", goodsList);
			returnMap.put("order_id", orderId);
			returnMap.put("order_status", orderStatus);
			returnMap.put("oms_order_sn",omsOrderSn);
			returnList.add(returnMap);
		}
		return returnList;
	}

	//供应商退货打印出库单信息查询
	@Override
	public List<Map<String, Object>> getGoodsReturnPrintList(String orderIds) {
		List<Map<String,Object>> goodsReturnPrintList = new ArrayList<Map<String,Object>>();
		String arr[] = orderIds.split(",");
		for (int i = 0; i < arr.length; i++) {
			Map<String,Object> goodsReturnPrintMap = new HashMap<String,Object>();
			List<Map<String,Object>> goodsReturnPrintInfo = orderProcessDao.getGoodsReturnInfo(arr[i]);
			if(WorkerUtil.isNullOrEmpty(goodsReturnPrintInfo)){
				continue;
			}
			goodsReturnPrintMap.put("customer_name", goodsReturnPrintInfo.get(0).get("customer_name"));
			goodsReturnPrintMap.put("order_id",arr[i]);
			goodsReturnPrintMap.put("oms_order_sn",goodsReturnPrintInfo.get(0).get("oms_order_sn"));
			goodsReturnPrintMap.put("warehouse_name",goodsReturnPrintInfo.get(0).get("warehouse_name"));
			goodsReturnPrintMap.put("provider_name", goodsReturnPrintInfo.get(0).get("receive_name"));
			goodsReturnPrintMap.put("goodsReturnPrintInfo", goodsReturnPrintInfo);
			goodsReturnPrintList.add(goodsReturnPrintMap);
		}
		return goodsReturnPrintList;
	}

	@Override
	public Map<String, Object> outGoods(String orderId,String taskQty) {
		//select for update 
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKeyForUpdate(Integer.parseInt(orderId));
		Assert.isTrue("RESERVED".equalsIgnoreCase(String
				.valueOf(orderInfo.getOrder_status())), "订单只有在分配成功状态下才可以操作出库");
		
		//增加判断：如果已出库数>0 ,不允许再出。并且报错！
		Integer outDoneNum = supplierReturnDao.selectOutDoneNum(orderId);
		Assert.isTrue(outDoneNum==0,"此订单已经出库数量："+outDoneNum+",不支持二次出库！请重新查询！");
		//增加判断：订单预分配量 与 goods_number必须一致
		List<Map> goodsNotEqualReserve = supplierReturnDao.checkGoodsInventoryNum(orderId);
		Assert.isTrue(WorkerUtil.isNullOrEmpty(goodsNotEqualReserve),"订单总商品数量与预分配数量不等，请联系WMS技术部门处理。订单号："+orderId);
		//增加判断：订单分配数量 与goods_number必须一致
		List<Map> goodsNotEqualReservePl = supplierReturnDao.checkGoodsProductLocationNum(orderId);
		Assert.isTrue(WorkerUtil.isNullOrEmpty(goodsNotEqualReservePl),"订单总商品数量与分配数量不等，请联系WMS技术部门处理。订单号："+orderId);
		
		String username = (String) SecurityUtils.getSubject().getPrincipal();
		Map<String, Object> returnMap = new HashMap<String,Object>();
		String arr[] = taskQty.split(",");
		//1. 将taskId实际要出库数作为map映射
		List<Integer> taskIdList = new ArrayList<Integer>() ;
		Map<String,Integer> taskOutQtyMap= new HashMap<String,Integer>();
		for (int i = 0; i < arr.length; i++) {
			String taskQtyArr[] = arr[i].split("_");
			Integer taskId = Integer.parseInt(taskQtyArr[0]);
			Integer quantity = Integer.parseInt(taskQtyArr[1]);
			Integer outQty = Integer.parseInt(taskQtyArr[2]);
			if(outQty.equals(0)){
				taskIdList.add(taskId);
			}else if(outQty.compareTo(0)>0 && outQty.compareTo(quantity)<=0){
				taskIdList.add(taskId);
				taskOutQtyMap.put(""+taskId, outQty);
			}else{
				returnMap.put("result", Response.FAILURE);
				returnMap.put("note", "供应商退货数量不合法，请检查！");
				return returnMap;
			}
		}
		Assert.isTrue(taskIdList.size()>0,"没有获取到出库任务ID！");
		//2. 根据taskId 获取该订单所有任务出库数，将orderGoodsId与realOutNum作为map映射  ， 完成task的出库
		Map<Integer,Integer> orderGoodsNumMap = new HashMap<Integer,Integer>();
		List<Map<String,Object>> taskOrderGoodsList = supplierReturnDao.selectTaskOrderGoodsList(taskIdList);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(taskOrderGoodsList),"根据任务ID没有匹配到库存&订单商品信息！");
		for (Map<String, Object> map : taskOrderGoodsList) {
			Integer outTaskId = Integer.parseInt(String.valueOf(map.get("task_id")));
			Integer orderGoodsId = Integer.parseInt(String.valueOf(map.get("order_goods_id")));
			Integer applyOutNum = Integer.parseInt(String.valueOf(map.get("task_qty")));
			Integer fromPlId = Integer.parseInt(String.valueOf(map.get("pl_id"))); 
			Integer locationId = Integer.parseInt(String.valueOf(map.get("location_id")));
			Integer RealOutNum = WorkerUtil.isNullOrEmpty(taskOutQtyMap.get(""+outTaskId))?0:taskOutQtyMap.get(""+outTaskId);
			String batchSn = String.valueOf(map.get("batch_sn"));
			if(!WorkerUtil.isNullOrEmpty(batchSn) && !"".equalsIgnoreCase(batchSn)){
				this.insertOrUpdateOrderGoodsBatch(orderGoodsId, batchSn,RealOutNum);
			}
			// 2.1 存储map映射
			if(orderGoodsNumMap.containsKey(orderGoodsId)){
				orderGoodsNumMap.put(orderGoodsId, orderGoodsNumMap.get(orderGoodsId)+RealOutNum);
			}else{
				orderGoodsNumMap.put(orderGoodsId, RealOutNum);
			}
			//2.2 判断是否需要还一部分库存给到pl
			if(applyOutNum>RealOutNum) {
				//申请出库数>实际出库数，需要还给pl availableQty=applyOutNum-RealOutNum
				Integer addAvailableNum = applyOutNum-RealOutNum;
				productLocationDao.updateProductLocationForAddAvailable(addAvailableNum, fromPlId);
			}
			//2.3 pl出库 
			productLocationDao.updateProductLocationTotal(-RealOutNum, fromPlId);
			ProductLocationDetail productLocationDetail = new ProductLocationDetail();
			productLocationDetail.setPl_id(fromPlId);
			productLocationDetail.setChange_quantity(-RealOutNum);
			productLocationDetail.setTask_id(outTaskId); // 任务ID
			productLocationDetail.setDescription("-gt退货出库数："+RealOutNum);
			productLocationDetail.setCreated_user(username);
			productLocationDetail.setLast_updated_user(username);
			productLocationDetail.setOrder_id(Integer.parseInt(orderId));
			productLocationDetail.setOrder_goods_id(orderGoodsId);
			productLocationDetailDao.insert(productLocationDetail);
			//判断源库位是否为空
			Integer isEmpty = productLocationDao.selectMapFromLocationId(locationId);
			if(isEmpty==0){
				locationDao.updateLocationIsEmpty(locationId);
			}
			//2.4 update task
			taskDao.updateTaskFulfilledAndQty(outTaskId,RealOutNum,username);
			UserActionTask userActionTask = new UserActionTask();
			userActionTask.setTask_id(outTaskId);
			userActionTask.setAction_note("-gt完成退货出库数量："+RealOutNum);
			userActionTask.setAction_type(Task.TASK_TYPE_PICK);
			userActionTask.setTask_status(Task.TASK_STATUS_FULFILLED);
			userActionTask.setCreated_user(username);
			userActionTask.setCreated_time(new Date());
			userActionTaskDao.insert(userActionTask);
		}
		
		//3. 计算inventoryItem级别出库数，foreach出库orderGoods
		List<Map<String,Object>> orderInventoryItemList = supplierReturnDao.selectInventoryItemByOrderId(orderId);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(orderInventoryItemList),"订单没有指定到实际出库库存，预分配逻辑数据查询失败！");
		for (Map<String, Object> map2 : orderInventoryItemList) {
			Integer orderGoodsId = Integer.parseInt(String.valueOf(map2.get("order_goods_id")));
			String reserveQtyStr = String.valueOf(map2.get("mapping_inventory_item_reserve_total_qty"));
			Integer inventorySummaryId = Integer.parseInt(String.valueOf(map2.get("inventory_summary_id")));
			Integer productId = Integer.parseInt(String.valueOf(map2.get("product_id")));
			Integer customerId = Integer.parseInt(String.valueOf(map2.get("customer_id")));
			Integer warehouseId = Integer.parseInt(String.valueOf(map2.get("warehouse_id")));
			
			String reserveQtyArr[] = reserveQtyStr.split(",");
			Integer realOutNum = 0;
			Integer realBackNum = 0; //需要还给inventorySummary
			List<Map<String,Object>> realBackNumList = new ArrayList<Map<String,Object>>(); // 需要还给reserveMapping
			if(orderGoodsNumMap.containsKey(orderGoodsId)){
				realOutNum = orderGoodsNumMap.get(orderGoodsId);
			}
			
			for (String reserveQty : reserveQtyArr) {
//				预分配逻辑为：此次预定将之前出库数量减去 .故出库的时候不减stock_quantity
//				int changeLine = inventorySummaryDao.updateStockQuantity(inventorySummaryId,-realOutNum);
//				Assert.isTrue(changeLine >= 1,"可扣减库存不足！");
				
				Map<String,Object> realBackMap = new HashMap<String,Object>();
				String[] reserveArr = reserveQty.split("_");
				Integer mappingId = Integer.parseInt(reserveArr[0]);
				Integer inventoryItemId = Integer.parseInt(reserveArr[1]);
				Integer iiReserveQty = Integer.parseInt(reserveArr[2]);
				Integer iiTotalQty = Integer.parseInt(reserveArr[3]);
				if(realOutNum==0){
					realBackMap.put("mappingId", mappingId);
					realBackMap.put("backNum", iiReserveQty);
					realBackNum = realBackNum+iiReserveQty;
					realBackNumList.add(realBackMap);
				}else if(realOutNum<iiReserveQty && realOutNum<=iiTotalQty){
					realBackMap.put("mappingId", mappingId);
					realBackMap.put("backNum", iiReserveQty-realOutNum);
					realBackNum = realBackNum+iiReserveQty-realOutNum;
					
					// 减库存
					int col = inventoryDao.updateInventoryItemByReserveMapping(inventoryItemId, realOutNum);
//					Assert.isTrue(col >= 1,"扣减库存时，预分配指定库存不足！");
					if (col < 1) {
						throw new RuntimeException("扣减库存时，预分配指定库存不足！");
					}
					
					InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
					inventoryItemDetail.setOrder_id(Integer.parseInt(orderId));
					inventoryItemDetail.setProduct_id(productId);
					inventoryItemDetail.setCustomer_id(customerId);
					inventoryItemDetail.setWarehouse_id(warehouseId);
					inventoryItemDetail.setInventory_item_id(inventoryItemId);
					inventoryItemDetail.setOrder_goods_id(orderGoodsId);
					inventoryItemDetail.setChange_quantity(-realOutNum);
					inventoryItemDetail.setPackbox_customer_id(0);
					inventoryItemDetail.setPackbox_warehouse_id(0);
					inventoryItemDetail.setCreated_time(new Date());
					inventoryItemDetail.setCreated_user(username);
					inventoryItemDetail.setLast_updated_user(username);
					inventoryItemDetail.setLast_updated_time(new Date());

					int effectRows = inventoryItemDetailDao.insert(inventoryItemDetail);
					Assert.isTrue(effectRows>0,"出库记录详情插入失败，请重试！");
					
					realOutNum = 0;
					realBackNumList.add(realBackMap);
				}else if (realOutNum>=iiReserveQty){
					int col = inventoryDao.updateInventoryItemByReserveMapping(inventoryItemId, iiReserveQty);
//					Assert.isTrue(col >= 1,"扣减库存时，预分配指定库存不足！");
					if (col < 1) {
						throw new RuntimeException("扣减库存时，预分配指定库存不足！");
					}
					
					InventoryItemDetail inventoryItemDetail = new InventoryItemDetail();
					inventoryItemDetail.setOrder_id(Integer.parseInt(orderId));
					inventoryItemDetail.setProduct_id(productId);
					inventoryItemDetail.setCustomer_id(customerId);
					inventoryItemDetail.setWarehouse_id(warehouseId);
					inventoryItemDetail.setInventory_item_id(inventoryItemId);
					inventoryItemDetail.setOrder_goods_id(orderGoodsId);
					inventoryItemDetail.setChange_quantity(-iiReserveQty);
					inventoryItemDetail.setPackbox_customer_id(0);
					inventoryItemDetail.setPackbox_warehouse_id(0);
					inventoryItemDetail.setCreated_time(new Date());
					inventoryItemDetail.setCreated_user(username);
					inventoryItemDetail.setLast_updated_user(username);
					inventoryItemDetail.setLast_updated_time(new Date());

					int effectRows = inventoryItemDetailDao.insert(inventoryItemDetail);
					Assert.isTrue(effectRows>0,"出库记录详情插入失败，请重试！");
					
					realOutNum = realOutNum-iiReserveQty;
				}else if(realOutNum<iiReserveQty && realOutNum>iiTotalQty){
					throw new RuntimeException("扣减库存时，预分配指定库存不足！");
				}
			}
			// 将剩余的库存加回去
			if(!WorkerUtil.isNullOrEmpty(realBackNumList) && realBackNum>0){
				int changeLine = inventorySummaryDao.updateAvailibleNumberForReserve(inventorySummaryId,-realBackNum);
				Assert.isTrue(changeLine>= 1,"总库存加回可用量失败！");
				changeLine = orderGoodsDao.updateReservedNumBack(orderGoodsId,realBackNum);
				Assert.isTrue(changeLine>=1,"订单商品预分配量加回失败！");
				for (Map<String, Object> realBackMap : realBackNumList) {
					Integer mappingId = Integer.parseInt(String.valueOf(realBackMap.get("mappingId")));
					Integer backNum = Integer.parseInt(String.valueOf(realBackMap.get("backNum")));
					orderReserveInventoryMappingDao.updateReserveNumBack(mappingId,backNum);
				}
			}
			
		}
		Map<String, Object> paramsForOrderInfoUpdate =  new HashMap<String, Object>();
		paramsForOrderInfoUpdate.put("orderStatus", "FULFILLED");
		paramsForOrderInfoUpdate.put("lastUpdatedUser", username);
		paramsForOrderInfoUpdate.put("lastUpdatedTime", new Date());
		paramsForOrderInfoUpdate.put("orderId", orderId);
		orderInfoDao.updateOrderStatusByOrderId(paramsForOrderInfoUpdate);
		Map<String,Object> paramsForOrderProcessUpdate = new HashMap<String,Object>();
		paramsForOrderProcessUpdate.put("status", "FULFILLED");
		paramsForOrderProcessUpdate.put("orderId", orderId);
		orderProcessDao.updateOrderStatusForFulfilledByOrderId(paramsForOrderProcessUpdate);
		returnMap.put("result",Response.SUCCESS);
		return returnMap;
	}
	
	private void insertOrUpdateOrderGoodsBatch(int orderGoodsId, String batchSn,Integer RealOutNum) {
		if(WorkerUtil.isNullOrEmpty(batchSn))
			return;
		OrderGoodsBatch orderGoodsBatch = null;
		orderGoodsBatch = orderGoodsBatchDao.selectByOrderGoodsIdAndBatchSn(orderGoodsId, batchSn);
		if(orderGoodsBatch == null) {
			orderGoodsBatch = new OrderGoodsBatch();
			orderGoodsBatch.setOrder_goods_id(orderGoodsId);
			orderGoodsBatch.setBatch_sn(batchSn);
			orderGoodsBatch.setNum(RealOutNum);
			orderGoodsBatch.setCreated_user((String)SecurityUtils.getSubject().getPrincipal());
			orderGoodsBatch.setCreated_time(new Date());
			orderGoodsBatchDao.insert(orderGoodsBatch);
			return;
		}
		orderGoodsBatch.setNum(orderGoodsBatch.getNum() + RealOutNum); 
		orderGoodsBatchDao.updateByPrimaryKeySelective(orderGoodsBatch);
	}
}
