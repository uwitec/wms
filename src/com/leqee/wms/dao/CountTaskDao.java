package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.TaskCount;
import com.leqee.wms.page.PageParameter;

public interface CountTaskDao {

	List<Map<String,Object>> searchCountSnForPrint(@Param("countSn")String countSn,@Param("physicalWarehouseId")Integer physicalWarehouseId,
			@Param("batchCountSn")String batchCountSn,@Param("batchMark")Integer batchMark);

	Map<String, Object> searchCountSnForPrintGroup(String countSn,Integer physicalWarehouseId);

	/**
	 * 批量插入
	 * @param taskCountList
	 */
	void batchInsert(@Param("list")List<TaskCount> taskCountList);

	void batchInsert2(@Param("list")List<TaskCount> taskCountList);
	
	List<Map<String, Object>> searchCountSnForBatch(String countSn);

	List<Integer> searchCountTaskByNum(String countSn,String countSnStatus,Integer taskNumPerPage);

	Integer batchUpdateMark(@Param("batchCountSn")String batchCountSn, @Param("batchMark")Integer batchMark,
			@Param("taskIdList")List<Integer> taskIdList,@Param("status") String status);

	/**
	 * 获取该盘点任务未开始的数量
	 * @param count_sn
	 * @return
	 */
	int getCountTaskCount(@Param("count_sn")String count_sn);

	/**
	 * 根据盘点任务号取消所有的盘点任务
	 * @param count_sn
	 */
	void cancelTaskCountByCountSn(@Param("count_sn")String count_sn);

	/**
	 * 查询盘点任务
	 * @param physical_warehouse_id
	 * @param customer_id
	 * @param barcode
	 * @param product_name
	 * @param task_type
	 * @param count_sn
	 * @param task_id
	 * @param time
	 * @param status
	 * @param page
	 * @return
	 */
	List<TaskCount> getTaskCountByPage(@Param("physical_warehouse_id")int physical_warehouse_id,
			@Param("customer_id")int customer_id, @Param("barcode")String barcode, @Param("product_name")String product_name,
			@Param("task_type")String task_type, @Param("count_sn")String count_sn, @Param("task_id")int task_id, @Param("time")String time,
			@Param("status")String status, @Param("page")PageParameter page);

	/**
	 * 根据盘点任务id或盘点任务编号查询盘点任务
	 * @param physical_warehouse_id 
	 * @param batch_task_sn 盘点任务编号
	 * @param task_id 盘点任务id
	 * @param mark 第几次盘点
	 * @return
	 */
	List<TaskCount> queryTaskCountByBatchCountSnTaskId(@Param("physical_warehouse_id")int physical_warehouse_id, @Param("batch_task_sn")String batch_task_sn,
			@Param("task_id")int task_id, @Param("mark")int mark);

	List<TaskCount> queryTaskCountByIdList(@Param("taskIdList")List<Integer> taskIdList);

	void updateList(@Param("list")List<TaskCount> list, @Param("mark")int mark, @Param("user_name")String user_name);

	int getNotFulfilledTaskNums(@Param("task_id")int task_id);

	List<TaskCount> getFulfilledTaskByTaskId(@Param("task_id")int task_id);

	void updateImproved(@Param("list")List<TaskCount> list);

	int getNeedImprove(@Param("task_id")int task_id);

	String getCountSnByBatchTaskCount(@Param("batch_task_sn")String batch_task_sn);

	int getTaskNumByCountSnMark(@Param("mark")int batchMark, @Param("count_sn")String count_sn);
}
