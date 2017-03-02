package com.leqee.wms.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.BatchTask;

public interface BatchTaskDao {

	public int insert(BatchTask batchTask);

	public Map<String, Object> selectBatchTaskByBatchTaskSn(@Param("batch_task_sn")String batchTaskSn);

	public List<Map<String,Object>> selectTaskIdByBatchTaskSn(String batchTaskSn);
}
