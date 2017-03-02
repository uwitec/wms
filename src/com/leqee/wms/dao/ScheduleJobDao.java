package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.ScheduleJob;


/**
 * 计划任务Dao
 * @author qyyao
 * @date 2016-4-11
 * @version 1.0
 */
public interface ScheduleJobDao {
	int deleteByPrimaryKey(Long jobId);

	int insert(ScheduleJob record);

	int insertSelective(ScheduleJob record);

	ScheduleJob selectByPrimaryKey(Long jobId);

	int updateByPrimaryKeySelective(ScheduleJob record);

	int updateByPrimaryKey(ScheduleJob record);

	List<ScheduleJob> getAll();
	
	List<ScheduleJob> getAll2();

	List<ScheduleJob> selectTaskJob(String job_group);

	/**
	 * 只在每天0:05分的时候删除前一天的
	 * @return
	 */
	List<ScheduleJob> selectDeadSingleBatchPickJob();

	String getStatusByPhysicalWarehouseId(String batchpickjob);

	ScheduleJob getBatchPickPhysicalJob(@Param("job_name")String job_name);

	List<ScheduleJob> getBatchPickPhysicalJobByJobGroup(@Param("job_group")String job_group);

	List<String> getTypeList();

	List<ScheduleJob> getJobByType(String jobType);
}