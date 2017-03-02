package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Product;

/**
 * @author Jarvis
 * @CreatedDate 2016.02.02
 *
 */
public interface BatchPickDao {
	
	/**
	 * @author Jarvis
	 * @Description 插入一条记录，结果获取插入记录的主键
	 * 
	 * @param batchPick a BatchPick:
	 * 
	 * @return effectRows an int: 影响行数
	 * */
	public int insert(BatchPick batchPick);
	
	/**
	 * @author Jarvis
	 * @Description 查询今日可以使用的最大的波次单编号
	 * 
	 * @param curDateStartBatchPickSn a String: 今日的起始编号
	 * 
	 * @return maxBatchPickSn a String: 今日可以使用的最大的波次单编号
	 * */
	public String selectMaxBatchPickSn(String curDateStartBatchPickSn);

	public BatchPick selectBatchPickByBatchPickSn(String batch_pick_sn);

	public void updatebatchPickForBindEmployee(int bind_user_id,int batch_pick_id);

	public BatchPick selectbatchPickById(int batchPickId);
	
	public BatchPick selectbatchPickForUpdateLock(int batch_pick_id);
	public BatchPick selectbatchPickForUpdateLockV2(@Param("batch_pick_id")int batch_pick_id);

	public int selectOrdersInTask(Map<String, Object> parmMap);

	public int selectBatchPickTasks(Map<String, Object> parmMap);
	
	public void updateOrderInfoStatus(String batch_pick_sn);

	public void updateOrderProcessStatus(String batch_pick_sn);

	public List<OrderInfo> getOrderIdByBatchPickSn(String batch_pick_sn);

	public Integer updateBatchProcessType(@Param("batch_pick_id")int batch_pick_id, @Param("batch_process_type")String batch_process_type);

	public int getbatchPickNumInPhySicalWareHouseToday(
			@Param("physical_warehouse_id")Integer physical_warehouse_id, @Param("time")String time);

	public int getorderNumInPhySicalWareHouseToday(
			@Param("physical_warehouse_id")Integer physical_warehouse_id, @Param("time")String time);


	public int getOrdersInPhysicalWareHouseTodayForBp(
			@Param("physical_warehouse_id")Integer physical_warehouse_id, @Param("time")String time);
	
	public BatchPick getBatchBatchPickByBPSn(@Param("batch_pick_sn")String batch_pick_sn);
	

	public List<Map<String, Object>> getAllShipmentByBPSn(@Param("batch_pick_id")int batch_pick_id);

	public int getPhysicalWarehouseIdByBPId(@Param("batch_pick_id")int batch_pick_id);

	public int getErrorNumOrders(@Param("batch_pick_id")int batch_pick_id);

	public int noWeightedOrders(@Param("batch_pick_id")int batch_pick_id);

	public Integer getBatchNumOrders(String batchPickSn);

	public List<Map<String, String>> getErrorOrdersList(@Param("batch_pick_id")int batch_pick_id);

	public int getErrorShipmentNums(@Param("batch_pick_id")int batch_pick_id);

	public List<Integer> getAllShipmentByBPSnAndShippingId(@Param("batch_pick_id")int batch_pick_id,
			@Param("shipping_id")int shipping_id);

	public void updateReserveStatus(@Param("batchpicklist")List<Integer> batchPickEList, @Param("reserve_status")String reserve_status);

	public List<Map<String, Object>> selectBatchOrderListForCleanTns(String batchPickSn);

	public List<BatchPick> selectAllBatchPickListByIdListForUpdate(
			@Param("list")List<Integer> list);

	public List<Integer> getNeedAllotSaleBatchPick(@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("customer_id")Integer customer_id, @Param("reserve_status")String reserve_status,
			@Param("isToB")int isToB, @Param("warehouse_id")int warehouse_id);
	
	public List<Integer> getNeedAllotSaleBatchPickV2(
			@Param("physical_warehouse_id")Integer physical_warehouse_id, @Param("customer_id")Integer customer_id, @Param("reserve_status")String status,
			@Param("isToB")int isToB, @Param("warehouse_id")int warehouse_id);

	public Integer updateBatchProcessTypeAndRecheckMark(Integer batchPickId);

	public List<Map<String,Object>> checkBatchPickForRecheck(Integer batchPickId);

	public BatchPick selectbatchPickBySnForRecheck(@Param("batchPickSn")String batchPickSn);
	
	public List<BatchPick> selectAllEBatchPickByCustomerId(
			@Param("physical_warehouse_id")int physical_warehouse_id, @Param("customer_id")int customer_id);

	public Integer updateFlowStatusForBatchPick(@Param("batchPickId")Integer batchPickId,@Param("fromFlowStatus")String checkFlowStatus, @Param("toFlowStatus")String flowStatus);

	public List<Integer> selectToApplyFirstShipmentBatchPickIdList(Map<String, Object> paramsMap);

	public void updateFlowStatus(Integer batchPickId, String toFlowStatus);
	
	public List<BatchPick> selectBatchPickListForUpdate(@Param("batchPickList")List<Integer> batchPickList);

	public List<Map<String, Object>> searchTnsSequence(String batchPickSn);

	public List<Product> checkNeedBatchSnByPickSn(String batchPickSn);

	public String selectReservedStatusBySn(@Param("batch_pick_sn")String batch_pick_sn);

	/**
	 * @param order_id
	 * @param product_id
	 */
	public List<String> getBatchSnListByOrderAndProduct(@Param("order_id") Integer order_id, @Param("product_id") Integer product_id);

	public int selectWarehouseIdBySn(@Param("batch_pick_sn")String batch_pick_sn);

}