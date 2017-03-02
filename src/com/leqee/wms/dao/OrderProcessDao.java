package com.leqee.wms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderProcess;



public interface OrderProcessDao {
	 

	    int deleteByPrimaryKey(Integer order_id);

	    int insert(OrderProcess record);

	    int insertSelective(OrderProcess record);

	    OrderProcess selectByPrimaryKey(Integer order_id);
	    
	    OrderProcess selectByPrimaryKeyForUpdate(Integer order_id);

	    int updateByPrimaryKeySelective(OrderProcess record);

	    int updateByPrimaryKey(OrderProcess record);
	    
	    Integer updateReserveStatus(String status,int order_id);
	    
	    /**
	     * 将订单状态修改为"已复核"
	     * @param orderId
	     * */
	    int setSaleOrderStatusFulfilled(Integer orderId);
	    
	    /**
	     * 将订单状态改为“拣货中” -- 用于 删除面单时订单状态回退
	     */
	    int setStatusPick(Integer orderId);
	    
	    /**
	     * 将订单状态改为“已复核” -- 最后一个耗材绑定时响应
	     */
	    int setStatusRecheckedByOrderId(Integer orderId);
	    
		/**
		 * @author Jarvis
		 * @Description 查询符合波次条件的销售订单
		 * 
		 * @param paramsForOrderProcessSelect a Map: 参数列表
		 * 
		 * @return orderInfoList a List<OrderInfo>: OrderProcess列表
		 * */
		public List<OrderProcess> selectOrderProcessListForBatchPick(Map<String, Object> paramsForOrderProcessSelect);

		/**
		 * @author Jarvis
		 * @Description 更新波次单相关信息
		 * 
		 * @param paramsForOrderProcessUpdate a Map: 参数列表
		 * 
		 * @return effectRows an int: 影响行数
		 * 
		 * */
		public int updateBatchPickInfoByOrderId(Map<String, Object> paramsForOrderProcessUpdate);

		public List<OrderGoods> selectOrderGoodsListForBatchPick(
				HashMap<String, Object> paramsForOrderProcessSelect);

		/**
		 * 
		 * @param orderIds 已经加入到任务表里的订单 需要把标记改为1
		 */
		public void updateProcessByOrders2Task(List<Integer> orderIds);

		/**
		 * 批捡时更新订单信息
		 * @param parmMap parmMap
		 */
		public void updateOrderProcessForBatchPick(HashMap<String, Object> parmMap);

		/**
		 * @author dlyao
		 * 根据id查出对应的订单 并锁住对应的行
		 * @param orderIds
		 * @return List<OrderProcess> List<OrderProcess>
		 */
		public List<OrderProcess> selectOrdersForLock2Task(List<Integer> orderIds);

		public List<OrderProcess> selectUnCancelOrdersForLock2Task(List<Integer> orderIds);
		/**
		 * 批捡时更新订单信息
		 * @param parmMap parmMap
		 */
		public void updateOrderProcessForBatchPick2(HashMap<String, Object> parmMap);
		int updateOrderStatusByOrderId(Map<String, Object> paramMap);
		
		int updateOrderStatusForFulfilledByOrderId(Map<String, Object> paramMap);

		void updateOrderProcessListForBatchPick(HashMap<String, Object> parmMap);

		void updateOrderProcessListForBatchPick2(@Param("batch_pick_id")int batch_pick_id,
				@Param("list")List<Integer> orderInfoList1);

		int setStatusWeigt(Integer orderId);

		int batchSetStatusRecheckedByOrderId(@Param("orderIdList")List<Integer> orderIdList);

		List<Map<String, Object>> batchSelectPrintInfo(String batchPickId);
		
		List<Map<String, Object>> batchSelectGoodsBarcodeList(String batchPickId);

		List<Map<String, Object>> selectPrintInfo(@Param("orderId")Integer orderId, @Param("trackingNumber")String trackingNumber);

		List<String> getOrderStatusList(String batchPickSn);

		Integer selectBatchOrderCount(String batchPickId);

		void updateShippingMark(Integer orderId, String mark);

		String getShippingMark(Integer orderId);

		Map<String, Object> getSingleOrder(Integer orderId);

		void updateStatusToReserved(@Param("gtYList")List<Integer> gtYList, @Param("status")String status);

		List<Map<String, Object>> getGoodsReturnInfo(String orderId);

		List<Map> getshipmentStatusListByPage(Map<String, Object> searchMap);// 包裹状态查询

		Integer getShipmentStatusNum(Map<String, Object> searchMap);// 包裹状态查询(总数量)

		List<Map<String, Object>> selectSingleOrderForMark(@Param("shippingCode")String shippingCode); // 获取需要接口调用大头笔信息的订单

		void updateBatchOrderProcess(@Param("list")List<Integer> orderIdList, @Param("status")String status);

		List<Integer> getAllHasProductOrdersInBatchPick(
				@Param("list")List<Integer> productIdList2, @Param("batch_pick_id")int batch_pick_id);

		void updateForKickOutOrdersStatus(@Param("list")List<Integer> orderIdList);

		void updateForKickOutOrdersStatusV2(@Param("list")List<Integer> orderIdList);

		void updateReserveResultList(@Param("list")List<Integer> orderYList, @Param("reserve_status")String reserve_status);

		String checkOrderForRecheck(int orderId);//根据orderId获取所属波次属性

		List<Map<String, Object>> checkOrderByBatchPickIdForRecheck(Integer batchPickId);

		Integer updateBatchTrickStatus(@Param("batchPickId")Integer batchPickId, @Param("fromStatus")String fromStatus,
				@Param("toStatus")String toStatus);

		Integer updateRecheckType(@Param("orderId")Integer orderId, @Param("fromRecheckType")Integer recheckType, @Param("toRecheckType")Integer toRecheckType);

		Integer updateIsFirstShipment(@Param("orderId")Integer orderId,@Param("fromIsFirstShipment") Integer isFirstShipment,
				@Param("toIsFirstShipment")Integer toIsFirstShipment);

		List<Integer> getToApplyFirstShipmentOrderIdListByBatchPickId(@Param("batchPickId")Integer batchPickId);
		
		int updateOrderProcessBatchTrickStatus(@Param("status")String status,@Param("orderId")Integer orderId);
		
		int deleteOrderProcessBatchPickId(@Param("orderId")Integer orderId);


		List<Integer> getAllCancelOrderByBatchPickId(@Param("batch_pick_id")int batch_pick_id);

		List<Integer> getAllCancelOrderByBatchPickSn(@Param("batch_pick_sn")String batch_pick_sn);


		int batchUpdateIsfirstShipment(@Param("is")Integer is, @Param("updatelist")List<Integer> updatelist);

		List<Integer> getAllCancelOrderByList(@Param("list")List<Integer> orderIdList);

		List<Integer> getAllCancelForFirstShipmentOrderByBatchPickId(
				Integer batchPickId);

		List<Integer> selectOrderIdsByBatchPickId(Integer batch_pick_id);
		
		//获取一个波次单里的所有快递信息
		List<String> getShippingList(String batchPickSn);

		List<OrderGoods> getOrderGoodsListByOrderIdList(@Param("list")List<Integer> orderIdList2);

}