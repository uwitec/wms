package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.Task;

public interface TaskDao {
	
	public int insert(Task task); // 插入task
	
	public Task selectByTaskIdForUpdate(Integer taskId);  // 根据taskId查找任务，并进行行锁
	
	public int updateTaskByTaskId(Map map); // 根据taskId更新任务状态

	public List<Map> selectTaskByIdList(Map map); // 根据taskIdList批量获取任务
	
	public int updateTaskByIdList(Map map); // 根据taskIdList批量更新任务状态
	
	public int updateTaskCancel(Map map); // 取消补货任务
	
	public int addProductLocationNumForCancel(@Param("taskId")Integer taskId, @Param("status")String status); // 加回取消补货任务的数量
	
	public int updateProductLocationNumForCancel(
			@Param("quantity") Integer quantity,
			@Param("locationId") Integer locationId,
			@Param("productId") Integer productId, @Param("status") String status);  
	
	public int updateProductLocationNumForCancelByPlId(
			@Param("quantity") Integer quantity,
			@Param("plId") Integer plId); 
	
	public int updateProductLocationNumForCancelNoReason(
			@Param("quantity") Integer quantity,
			@Param("plId") Integer plId);  
	
	public int updateTaskById(Map map); // 更新单个任务
	
	public Map selectTaskFromRF(Map map); // RF补货获取任务
	
	public Map selectProductFromRF(Map map); // RF补货根据原库位获取商品信息 

	/**
	 * 批量插入
	 * @param taskList
	 */
	public void insertTaskList(@Param("taskList")List<Task> taskList);

	/**
	 * 获取未处理的任务
	 * @param physical_warehouse_id
	 * @param task_level
	 * @return
	 */
	public List<Task> getAllInitTask(@Param("physical_warehouse_id")Integer physical_warehouse_id, @Param("task_level")int task_level);

	/**
	 * 取消任务
	 * @param updateTaskList
	 */
	public void setTaskListCancel(@Param("updateTaskList")List<Task> updateTaskList);

    /**
     * 找出所有正在进行的任务
     * @param physical_warehouse_id
     * @param goodsIdList 
     * @param orderreplenishmenttasklevel
     * @param locationType 
     * @param mark 
     * @return
     */
	public List<Task> getAllOnStartTask(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("list")List<Integer> goodsIdList, @Param("task_level")int orderreplenishmenttasklevel, @Param("location_type")String locationType, @Param("mark")int mark);

	public List<Task> getAllInitTaskForUpdate(@Param("physical_warehouse_id")Integer physical_warehouse_id, @Param("list")List<Integer> goodsIdList, @Param("task_level")int task_level);

	public Integer insertTask(Task task);

	public Integer updateTaskForWEBBind(Integer userId, Integer batchTaskId);

	public List<Task> getTaskIdByBatchTaskId(Integer batchTaskId);
	
	public List<Task> getTaskIdByOrderProcess(Map<String, Object>map);
	
	public List<Map<String, Object>> selectCancelProductLocationByOrderId(Integer order_id);
	
	public int updateTaskToPlId(@Param("taskId")Integer taskId,@Param("toPlId")Integer toPlId);

	public void updateTaskFulfilledAndQty(Integer outTaskId, Integer realOutNum, String username);
	
	public List<Task> getTaskIdByOrderId(Integer order_id);
	
	public int updateTaskFromPlId(@Param("task_id")Integer task_id, @Param("new_pl_id")Integer new_pl_id);
	
	public List<Task> selectTaskByProductId2BatchPicjId(@Param("batch_pick_id")Integer batch_pick_id, @Param("product_id")Integer product_id);

	public int updateTaskStatusById(@Param("task_id")Integer task_id,@Param("task_status")String task_status);
}
