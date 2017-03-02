package com.leqee.wms.biz.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.utils.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.ScheduleJobBiz;
import com.leqee.wms.dao.ScheduleJobDao;
import com.leqee.wms.entity.ScheduleJob;
import com.leqee.wms.schedule.job.JobKey;
import com.leqee.wms.schedule.job.QuartzJobFactory;
import com.leqee.wms.schedule.job.QuartzJobFactoryDisallowConcurrentExecution;
//import com.leqee.wms.schedule.job.TriggerKey;

/**
 * 计划任务管理业务层
 * @author hzhang1
 * @date 2016-04-18
 * @version 1.0
 */
@Service
public class ScheduleJobBizImpl implements ScheduleJobBiz {
	public final Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private ScheduleJobDao scheduleJobDao;

	/**
	 * 从数据库中取 区别于getAllJob
	 * 
	 * @return
	 */
	public List<ScheduleJob> getAllTask() {
		return scheduleJobDao.getAll();
	}
	
	/**
	 * 根据type筛选job
	 * 
	 * @return
	 */
	public List<ScheduleJob> getJobByType(String jobType) {
		return scheduleJobDao.getJobByType(jobType);
	}
	
	/**
	 * 获取type列表
	 * @return
	 */
	public List<String> getTypeList() {
		return scheduleJobDao.getTypeList();
	}

	/**
	 * 添加到数据库中 区别于addJob
	 */
	public void addTask(ScheduleJob job) {
		job.setCreateTime(new Date());
		scheduleJobDao.insertSelective(job);
	}

	/**
	 * 从数据库中查询job
	 */
	public ScheduleJob getTaskById(Long jobId) {
		return scheduleJobDao.selectByPrimaryKey(jobId);
	}

	/**
	 * 更改任务状态
	 * 
	 * @throws SchedulerException
	 */
	public void changeStatus(Long jobId, String cmd) throws SchedulerException {
		ScheduleJob job = getTaskById(jobId);
		if (job == null) {
			return;
		}
		if ("stop".equals(cmd)) {
			deleteJob(job);
			job.setJobStatus(ScheduleJob.STATUS_NOT_RUNNING);
		} else if ("start".equals(cmd)) {
			job.setJobStatus(ScheduleJob.STATUS_RUNNING);
			addJob(job);
		}
		scheduleJobDao.updateByPrimaryKeySelective(job);
	}

	/**
	 * 更改任务 cron表达式
	 * 
	 * @throws SchedulerException
	 */
	public void updateCron(Long jobId, String cron) throws SchedulerException {
		ScheduleJob job = getTaskById(jobId);
		if (job == null) {
			return;
		}
		job.setCronExpression(cron);
		if (ScheduleJob.STATUS_RUNNING.equals(job.getJobStatus())) {
			updateJobCron(job);
		}
		scheduleJobDao.updateByPrimaryKeySelective(job);

	}
	
	/**
	 * 更新job参数名值对
	 * @param jobId
	 * @param paramNameValue
	 */
	public void updateParamNameValue(Long jobId, String paramNameValue) throws SchedulerException {
		ScheduleJob job = getTaskById(jobId);
		if (job == null) {
			return;
		}
		job.setParamNameValue(paramNameValue);
		//TODO 是否需要加上这个验证，需要确认
		updateJobParamNameValue(job);
	}
	
	public void deleteScheduleJob(Long jobId) throws SchedulerException {
		ScheduleJob job = getTaskById(jobId);
		if (job == null) {
			return;
		}
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		scheduler.deleteJob(job.getJobName(), job.getJobGroup());
		scheduleJobDao.deleteByPrimaryKey(jobId);
	}

	private void updateJobParamNameValue(ScheduleJob scheduleJob) throws SchedulerException  {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		//JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		try {
			scheduler.deleteJob(scheduleJob.getJobName(), scheduleJob.getJobGroup());
			if(scheduleJob.getJobName().contains("-1")){
				scheduleJob.setJobName(scheduleJob.getJobName().replaceAll("-1", ""));
				scheduleJob.setJobGroup(scheduleJob.getJobGroup().replaceAll("-1", ""));
			}else{
				scheduleJob.setJobName(scheduleJob.getJobName()+"-1");
				scheduleJob.setJobGroup(scheduleJob.getJobGroup()+"-1");
			}
			JobDetail jobDetail = new JobDetail(scheduleJob.getJobName(), scheduleJob.getJobGroup(), QuartzJobFactoryDisallowConcurrentExecution.class);
			jobDetail.getJobDataMap().put("scheduleJob", scheduleJob);
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(scheduleJob.getJobName(),scheduleJob.getJobGroup());
			if(trigger == null){
				trigger = new CronTrigger(scheduleJob.getJobName(),scheduleJob.getJobGroup(),scheduleJob.getCronExpression());
			}else{
				trigger.setCronExpression(scheduleJob.getCronExpression());
			}
			if(ScheduleJob.STATUS_RUNNING.equals(scheduleJob.getJobStatus())){
				scheduler.scheduleJob(jobDetail, trigger);
			}
			scheduleJobDao.updateByPrimaryKeySelective(scheduleJob);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 添加任务
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void addJob(ScheduleJob job) throws SchedulerException {
		
		//订单状态是未运行状态或者为null直接返回 
		if (job == null || !ScheduleJob.STATUS_RUNNING.equals(job.getJobStatus())) {
			return;
		}

		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(job.getJobName(),job.getJobGroup());
		
		if(trigger == null){
			Class clazz = ScheduleJob.CONCURRENT_IS.equals(job.getIsConcurrent()) ?  QuartzJobFactoryDisallowConcurrentExecution.class  : QuartzJobFactory.class;
	        JobDetail jobDetail = new JobDetail(job.getJobName(), job.getJobGroup(), clazz); //任务名，任务组，任务执行类  
	        CronTrigger trigger2 = null;
	        jobDetail.getJobDataMap().put("scheduleJob", job);
			try {
				trigger2 = new CronTrigger(job.getJobName(),job.getJobGroup(),job.getCronExpression());
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        scheduler.scheduleJob(jobDetail,trigger2);
		}else{
			try {
				trigger.setCronExpression(job.getCronExpression());
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        scheduler.rescheduleJob(job.getJobName(), job.getJobGroup(), trigger);
		}
	}

	@PostConstruct
	public void init() throws Exception {

		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		// 这里获取任务信息数据
		List<ScheduleJob> jobList = scheduleJobDao.getAll2();
	
		for (ScheduleJob job : jobList) {
			addJob(job);
			//System.out.println("job"+job.getJobName()+" success");
		}
	}

	/**
	 * 获取所有计划中的任务列表
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public List<ScheduleJob> getAllJob() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
		return jobList;
	}

	/**
	 * 所有正在运行的job
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public List<ScheduleJob> getRunningJob() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>(executingJobs.size());
		for (JobExecutionContext executingJob : executingJobs) {
			ScheduleJob job = new ScheduleJob();
			JobDetail jobDetail = executingJob.getJobDetail();
			Key jobKey = jobDetail.getKey();
			Trigger trigger = executingJob.getTrigger();
			job.setJobName(jobKey.getName());
			job.setJobGroup(jobKey.getGroup());
			job.setDescription("触发器:" + trigger.getKey());
			if (trigger instanceof CronTrigger) {
				CronTrigger cronTrigger = (CronTrigger) trigger;
				String cronExpression = cronTrigger.getCronExpression();
				job.setCronExpression(cronExpression);
			}
			jobList.add(job);
		}
		return jobList;
	}

	/**
	 * 暂停一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void pauseJob(ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		scheduler.pauseJob(scheduleJob.getJobName(), scheduleJob.getJobGroup());
	}

	/**
	 * 恢复一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void resumeJob(ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		scheduler.resumeJob(scheduleJob.getJobName(), scheduleJob.getJobGroup());
	}

	/**
	 * 删除一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void deleteJob(ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		scheduler.deleteJob(scheduleJob.getJobName(), scheduleJob.getJobGroup());

	}

	/**
	 * 手动触发一次任务
	 * @throws SchedulerException 
	 */
	@Override
	public void runAJobNow(Long jobId) throws SchedulerException {
		ScheduleJob job = getTaskById(jobId);
		if (job == null) {
			return;
		}
		//1、 如果是正在运行的任务，则手动触发一次
		if (ScheduleJob.STATUS_RUNNING.equals(job.getJobStatus())) {  
			runAJobNow(job);
		}
		//2、如果不是正在运行的任务，则先添加一次，运行一次，然后再删除
		else{
			//>> a 先添加一次
			job.setJobStatus(ScheduleJob.STATUS_RUNNING);
			addJob(job);
			runAJobNow(job);
			
			//>> b 再删除一次
			try { //线程先等待一下
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			deleteJob(job);
		}
		
	}
	
	/**
	 * 立即执行job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void runAJobNowTemp(ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		scheduleJob.setIsConcurrent("1");
		scheduleJob.setJobStatus(ScheduleJob.STATUS_RUNNING);
		addJob(scheduleJob);
	}
	
	public void runAJobNow(ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		scheduleJob.setIsConcurrent("1");
		scheduleJob.setJobStatus(ScheduleJob.STATUS_RUNNING);
		//JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		scheduler.triggerJob(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		//addJob(scheduleJob);
	}

	/**
	 * 更新job时间表达式
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void updateJobCron(ScheduleJob job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		//TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
		CronTrigger trigger = null;
		try {
			trigger = (CronTrigger) scheduler.getTrigger(job.getJobName(),job.getJobGroup());
			trigger.setCronExpression(job.getCronExpression());
			//trigger = new CronTrigger(job.getJobName(),null,job.getCronExpression());
			scheduler.rescheduleJob(job.getJobName(), job.getJobGroup(), trigger);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		//CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
		//trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
		//scheduler.rescheduleJob(triggerKey, trigger);
	}

	public static void main(String[] args) {
		//CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("xxxxx");
	}

}
