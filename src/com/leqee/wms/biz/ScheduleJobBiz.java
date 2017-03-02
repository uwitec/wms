package com.leqee.wms.biz;

import java.util.List;

import org.quartz.SchedulerException;

import com.leqee.wms.entity.ScheduleJob;


/**
 * 任务相关方法
 * @author qyyao
 * @date 2016-4-11
 * @version 1.0
 */
public interface ScheduleJobBiz {

	public List<ScheduleJob> getAllTask() ;
	
	public List<ScheduleJob> getJobByType(String jobType) ;
	
	/**
	 * 获取type列表
	 * @return
	 */
	public List<String> getTypeList() ;

	/**
	 * 添加到数据库中 区别于addJob
	 */
	public void addTask(ScheduleJob job) ;

	/**
	 * 从数据库中查询job
	 */
	public ScheduleJob getTaskById(Long jobId)  ;

	/**
	 * 更改任务状态
	 * 
	 * @throws SchedulerException
	 */
	public void changeStatus(Long jobId, String cmd) throws SchedulerException  ;

	/**
	 * 更改任务 cron表达式
	 * 
	 * @throws SchedulerException
	 */
	public void updateCron(Long jobId, String cron) throws SchedulerException  ;

	/**
	 * 添加任务
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void addJob(ScheduleJob job) throws SchedulerException  ;
	
	public void init() throws Exception  ;

	/**
	 * 获取所有计划中的任务列表
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public List<ScheduleJob> getAllJob() throws SchedulerException  ;

	/**
	 * 所有正在运行的job
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public List<ScheduleJob> getRunningJob() throws SchedulerException  ;
	/**
	 * 暂停一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void pauseJob(ScheduleJob scheduleJob) throws SchedulerException  ;

	/**
	 * 恢复一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void resumeJob(ScheduleJob scheduleJob) throws SchedulerException  ;

	/**
	 * 删除一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void deleteJob(ScheduleJob scheduleJob) throws SchedulerException  ;

	/**
	 * 立即执行job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void runAJobNowTemp(ScheduleJob scheduleJob) throws SchedulerException  ;
	public void runAJobNow(ScheduleJob scheduleJob) throws SchedulerException  ;

	/**
	 * 更新job时间表达式
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void updateJobCron(ScheduleJob scheduleJob) throws SchedulerException ;

	/**
	 * 更新job参数名值对
	 * 
	 * @param jobId
	 * @param paramNameValue
	 */
	public void updateParamNameValue(Long jobId, String paramNameValue) throws SchedulerException   ;
	
	
	public void deleteScheduleJob(Long jobId) throws SchedulerException;
	
	/**
	 * 手动触发一次任务
	 * @param jobId
	 * @throws SchedulerException 
	 */
	public void runAJobNow(Long jobId) throws SchedulerException;
	
}
