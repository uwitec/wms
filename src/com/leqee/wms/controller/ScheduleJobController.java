package com.leqee.wms.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
//import org.quartz.CronScheduleBuilder;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.api.response.RetObj;
import com.leqee.wms.biz.ScheduleJobBiz;
import com.leqee.wms.entity.ScheduleJob;
import com.leqee.wms.util.SpringUtils;
import com.leqee.wms.util.WorkerUtil;

@Controller
@RequestMapping("/scheduleJob")
public class ScheduleJobController {
	// 日志记录器
	public final Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private ScheduleJobBiz scheduleJobBiz;

	@RequiresPermissions("scheduleJob:list:*")
	@RequestMapping("list")
	public String taskList(HttpServletRequest request) {
		List<ScheduleJob> scheduleJobList = scheduleJobBiz.getAllTask();
		request.setAttribute("scheduleJobList", scheduleJobList);
		return "/scheduleJob/list";
	}
	
	@RequestMapping(value="getListByType")
	@ResponseBody
	public Object getListByType(HttpServletRequest request){
		Map<String,Object> resMap = new HashMap<String,Object>();
		String jobType = request.getParameter("jobType");
		List<ScheduleJob> scheduleJobList = new ArrayList<ScheduleJob>();
		scheduleJobList = scheduleJobBiz.getJobByType(jobType);
		resMap.put("scheduleJobList", scheduleJobList);
		return resMap;
	}
	
	@RequestMapping(value="getType")
	@ResponseBody
	public Object getType(HttpServletRequest request){
		Map<String,Object> resMap = new HashMap<String,Object>();
		List<String> typeList = scheduleJobBiz.getTypeList();
		resMap.put("typeList", typeList);
		return resMap;
	}

	@RequestMapping("add")
	@ResponseBody
	public RetObj taskList(HttpServletRequest request, ScheduleJob scheduleJob) {
		scheduleJob.setBeanClass("");
		RetObj retObj = new RetObj();
		retObj.setFlag(false);
		try {
			//CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
		} catch (Exception e) {
			retObj.setMsg("cron表达式有误，不能被解析！");
			return retObj;
		}
		Object obj = null;
		try {
			if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
				obj = SpringUtils.getBean(scheduleJob.getSpringId());
			} else {
				Class clazz = Class.forName(scheduleJob.getBeanClass());
				obj = clazz.newInstance();
			}
		} catch (Exception e) {
			// do nothing.........
		}
		if (obj == null) {
			retObj.setMsg("未找到目标类！");
			return retObj;
		} else {
			Class clazz = obj.getClass();
			Method method = null;
			try {
				if(!WorkerUtil.isNullOrEmpty(scheduleJob.getParamNameValue())){
					method = clazz.getMethod(scheduleJob.getMethodName(), String.class);
				}else{
					method = clazz.getMethod(scheduleJob.getMethodName(), null);
				}
			} catch (Exception e) {
				// do nothing.....
			}
			if (method == null) {
				retObj.setMsg("未找到目标方法！");
				return retObj;
			}
		}
		try {
			scheduleJobBiz.addTask(scheduleJob);
		} catch (Exception e) {
			e.printStackTrace();
			retObj.setFlag(false);
			retObj.setMsg("保存失败，检查 name group 组合是否有重复！");
			return retObj;
		}

		retObj.setFlag(true);
		return retObj;
	}

	@RequestMapping("changeJobStatus")
	@ResponseBody
	public RetObj changeJobStatus(HttpServletRequest request, Long jobId, String cmd) {
		RetObj retObj = new RetObj();
		retObj.setFlag(false);
		try {
			scheduleJobBiz.changeStatus(jobId, cmd);
		} catch (SchedulerException e) {
			log.error(e.getMessage(), e);
			retObj.setMsg("任务状态改变失败！");
			return retObj;
		}
		retObj.setFlag(true);
		return retObj;
	}

	@RequestMapping("updateCron")
	@ResponseBody
	public RetObj updateCron(HttpServletRequest request, Long jobId, String cron) {
		RetObj retObj = new RetObj();
		retObj.setFlag(false);
		try {
			//CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
		} catch (Exception e) {
			retObj.setMsg("cron表达式有误，不能被解析！");
			return retObj;
		}
		try {
			scheduleJobBiz.updateCron(jobId, cron);
		} catch (SchedulerException e) {
			retObj.setMsg("cron更新失败！");
			return retObj;
		}
		retObj.setFlag(true);
		return retObj;
	}
	@RequestMapping("updateParamNameValue")
	@ResponseBody
	public RetObj updateParamNameValue(HttpServletRequest request, Long jobId, String paramNameValue) {
		RetObj retObj = new RetObj();
		retObj.setFlag(false);
		try {
//			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
			//TODO 键值表达式验证（采用正则表达式验证）
			Assert.notNull(paramNameValue);
		} catch (Exception e) {
			retObj.setMsg("paramNameValue表达式有误，不能被解析！");
			return retObj;
		}
		try {
			scheduleJobBiz.updateParamNameValue(jobId, paramNameValue);
		} catch (SchedulerException e) {
			retObj.setMsg("paramNameValue更新失败！");
			return retObj;
		}
		retObj.setFlag(true);
		return retObj;
	}
	
	@RequestMapping(value="runAJobNow")
	@ResponseBody
	public RetObj runAJobNow(HttpServletRequest request){
		RetObj retObj = new RetObj();
		ScheduleJob scheduleJob = new ScheduleJob();
		scheduleJob.setSpringId(request.getParameter("class"));
		scheduleJob.setMethodName(request.getParameter("method"));
		scheduleJob.setJobId(1L);
		scheduleJob.setJobGroup("hzhang1".toString());
		scheduleJob.setJobName("hzhang1".toString());
		
		Calendar now = Calendar.getInstance();
		int hours = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int day = now.get(Calendar.DAY_OF_MONTH);
		int mouth = now.get(Calendar.MONTH) + 1;
		int year = now.get(Calendar.YEAR);
		String cron = "10/50 "+(minute+1)+" "+hours+" "+day+" "+mouth+" ? "+year;
		scheduleJob.setCronExpression(cron);
		retObj.setFlag(false);
		Object obj = null;
		try {
			if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
				obj = SpringUtils.getBean(scheduleJob.getSpringId());
			} else {
				Class clazz = Class.forName(scheduleJob.getBeanClass());
				obj = clazz.newInstance();
			}
		} catch (Exception e) {
			// do nothing.........
		}
		try {
			scheduleJobBiz.runAJobNowTemp(scheduleJob);
		} catch (SchedulerException e) {
			retObj.setMsg("执行失败！");
			return retObj;
		}
		
		retObj.setFlag(true);
		return retObj;
	}
	
	
	
	@RequestMapping(value="runAJobNow2")
	@ResponseBody
	public Object runAJobNow2(HttpServletRequest request,ScheduleJob scheduleJob){
		Map<String,Object> resMap = new HashMap<String,Object>();
		resMap.put("flag", true);
		Object obj = null;
		try {
			if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
				obj = SpringUtils.getBean(scheduleJob.getSpringId());
			} else {
				Class clazz = Class.forName(scheduleJob.getBeanClass());
				obj = clazz.newInstance();
			}
		} catch (Exception e) {
			// do nothing.........
		}
		if (obj == null) {
			resMap.put("flag", false);
			resMap.put("msg", "未找到目标类！");
			return resMap;
		} else {
			try {
				Calendar now = Calendar.getInstance();
				int hours = now.get(Calendar.HOUR_OF_DAY);
				int minute = now.get(Calendar.MINUTE);
				int day = now.get(Calendar.DAY_OF_MONTH);
				int mouth = now.get(Calendar.MONTH) + 1;
				int year = now.get(Calendar.YEAR);
				String cron = "10/50 "+(minute+1)+" "+hours+" "+day+" "+mouth+" ? "+year;
				scheduleJob.setCronExpression(cron);
				scheduleJobBiz.runAJobNow(scheduleJob);
			} catch (SchedulerException e) {
				e.printStackTrace();
				resMap.put("flag", false);
				resMap.put("msg", "执行失败！"+e.getMessage());
				return resMap;
			}
		}
		
		resMap.put("flag", true);
		return resMap;
	}
	
	@RequestMapping("deleteJob")
	@ResponseBody
	public RetObj deleteJob(HttpServletRequest request, Long jobId) {
		RetObj retObj = new RetObj();
		retObj.setFlag(false);
		try {
			scheduleJobBiz.deleteScheduleJob(jobId);
		} catch (SchedulerException e) {
			retObj.setMsg("deleteJob更新失败！");
			return retObj;
		}
		retObj.setFlag(true);
		return retObj;
	}
	
	@RequestMapping("runAJobNow3")
	@ResponseBody
	public RetObj runAJobNow(HttpServletRequest request, Long jobId ) {
		RetObj retObj = new RetObj();
		retObj.setFlag(false);
		try {
			scheduleJobBiz.runAJobNow(jobId);
		} catch (SchedulerException e) {
			retObj.setMsg("手动触发失败！");
			return retObj;
		}
		retObj.setFlag(true);
		retObj.setMsg("手动触发成功");
		return retObj;
	}
}
