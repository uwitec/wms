package com.leqee.wms.job;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.leqee.wms.schedule.job.TaskUtils;
import com.leqee.wms.util.WorkerUtil;

public abstract class CommonJob {

	protected Map<String, Object> getServiceParamsMap( String paramNameValue ){
		
		Map<String, String> paramNameValueMap = TaskUtils.getParamNameValueMap(paramNameValue);
		Map<String, Object> serviceParamsMap = new HashMap<String, Object>();
		
		Integer groupId = paramNameValueMap.get("groupId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("groupId")) ; //默认为0
		Integer customerId = paramNameValueMap.get("customerId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("customerId")); //默认为0
		Double hours = paramNameValueMap.get("hours") == null ? 24 : Double.parseDouble(paramNameValueMap.get("hours"));  //默认近24个小时
		String endDate = paramNameValueMap.get("endDate") == null ? WorkerUtil.formatDatetime(new Date()) : paramNameValueMap.get("endDate");  //默认当前时间
		Integer physicalWarehouseId = paramNameValueMap.get("physicalWarehouseId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("physicalWarehouseId")); //默认为0
		String batchPickGroupIds = paramNameValueMap.get("batchPickGroupIds")== null ?"":paramNameValueMap.get("batchPickGroupIds"); 
		String shippingCode = paramNameValueMap.get("shippingCode")== null ?"":paramNameValueMap.get("shippingCode"); 
		
		serviceParamsMap.put("groupId", groupId);
		serviceParamsMap.put("customerId", customerId);
		serviceParamsMap.put("hours", hours);
		serviceParamsMap.put("endDate", endDate);
		serviceParamsMap.put("physicalWarehouseId", physicalWarehouseId);
		serviceParamsMap.put("batchPickGroupIds", batchPickGroupIds);
		serviceParamsMap.put("shippingCode", shippingCode);
		
		return serviceParamsMap;
	}
	

	protected Map<String, Object> getServiceParamsMapByDlYao( String paramNameValue ){
		
		Map<String, String> paramNameValueMap =   TaskUtils.getParamNameValueMap(paramNameValue);
		Map<String, Object> serviceParamsMap =   new HashMap<String, Object>();
		
		Integer physical_warehouse_id = paramNameValueMap.get("physical_warehouse_id") == null ? 0 : Integer.parseInt(paramNameValueMap.get("physical_warehouse_id")) ; //默认为0
		Integer groupId = paramNameValueMap.get("groupId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("groupId")) ; //默认为0
		Integer customerId = paramNameValueMap.get("customerId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("customerId")); //默认为0
		Integer days = paramNameValueMap.get("days") == null ? 0 : Integer.parseInt(paramNameValueMap.get("days")); //默认为0
		
		serviceParamsMap.put("physical_warehouse_id", physical_warehouse_id);
		serviceParamsMap.put("groupId", groupId);
		serviceParamsMap.put("customerId", customerId);
		serviceParamsMap.put("days", days);
		
		return serviceParamsMap;		
	}
}
