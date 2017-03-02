package com.leqee.wms.dao;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.BatchTaskCount;

public interface BatchTaskCountDao {

	BatchTaskCount getBatchTaskCount(@Param("batch_task_sn")String batch_task_sn);

	void updateBindbatchTaskCount(@Param("binded_user_id")Integer binded_user_id, @Param("batch_task_id")int batch_task_id);

	Integer insertBatchTaskCount(BatchTaskCount batchTaskCount);

}
