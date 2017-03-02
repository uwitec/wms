package com.leqee.wms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leqee.wms.entity.BatchPickTask;

public interface BatchPickTaskDao {
	

	public int selectOrdersInTask(Map<String, Object> parmMap);

	public int selectBatchPickTasks(Map<String, Object> parmMap);

	public void insert(BatchPickTask batchPickTask);

	public void insertList(List<BatchPickTask> batchPickTaskList);

	public List<BatchPickTask> selectBatchPickTasksByWarehouse(
			HashMap<String, Object> paramMap);

	public void updateBptStatus(Integer batch_pick_task_id);

}
