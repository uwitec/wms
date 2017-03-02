package com.leqee.wms.schedule.job;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.leqee.wms.entity.ScheduleJob;
import com.leqee.wms.util.SpringUtils;
import com.leqee.wms.util.WorkerUtil;



public class TaskUtils {
	public final static Logger log = Logger.getLogger(TaskUtils.class);

	/**
	 * 通过反射调用scheduleJob中定义的方法
	 * 
	 * @param scheduleJob
	 */
	public static void invokMethod(ScheduleJob scheduleJob) {
		Object object = null;
		Class clazz = null;
		if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
			object = SpringUtils.getBean(scheduleJob.getSpringId());
		} else if (StringUtils.isNotBlank(scheduleJob.getBeanClass())) {
			try {
				clazz = Class.forName(scheduleJob.getBeanClass());
				object = clazz.newInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (object == null) {
			log.error("任务名称 = [" + scheduleJob.getJobName() + "]---------------未启动成功，请检查是否配置正确！！！");
			return;
		}
		clazz = object.getClass();
		Method method = null;
		try {
			if( !WorkerUtil.isNullOrEmpty(scheduleJob.getParamNameValue())){
				method = clazz.getDeclaredMethod(scheduleJob.getMethodName(), String.class);
			}else{
				method = clazz.getDeclaredMethod(scheduleJob.getMethodName());
			}
		} catch (NoSuchMethodException e) {
			log.error("任务名称 = [" + scheduleJob.getJobName() + "]---------------未启动成功，方法名设置错误！！！");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (method != null) {
			try {
				Class<?>[] clazzes = method.getParameterTypes();
				if(clazzes != null && clazzes.length > 0 ){
					//当有参数时，传递的就是jobName
					method.invoke(object, scheduleJob.getParamNameValue());
				}else{
					method.invoke(object);
				}
				
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println("任务名称 = [" + scheduleJob.getJobName() + "]----------启动成功");
	}
	
	
	
	/**
	 * 获取参数键值Map
	 * @param paramNameValue
	 * @return
	 */
	public static Map<String, String> getParamNameValueMap(  String paramNameValue   ){
		
		Map<String, String> paramNameValueMap = new HashMap<String, String>();
		//基本验证
		if( WorkerUtil.isNullOrEmpty( paramNameValue ) ){
			return paramNameValueMap;
		}
		if( !paramNameValue.contains("=") ){
			return paramNameValueMap;
		}
		
		//TODO 需要用正则表达式去校验参数格式是否满足xxx=rr&dd=rr这种方式，后面再实现
//		if( !paramNameValue.matches("^[1-9][0-9]*_[0-9]+$") ){
//			logger.error("paramNameValue="+paramNameValue );
//			return paramNameValueMap;
//		}
		
		String[] nameValues = paramNameValue.split("&");
		
		for(String nameValue : nameValues){
			String[] nameValueArr = nameValue.split("=");
			String name =  nameValueArr[0];
			String value = nameValueArr[1];
			paramNameValueMap.put(name, value);
		}
		
		return paramNameValueMap;
	}
	
	
}
