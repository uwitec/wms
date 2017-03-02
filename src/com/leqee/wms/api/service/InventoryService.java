package com.leqee.wms.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.api.LeqeeError;
import com.leqee.wms.api.biz.InventoryItemApiBiz;
import com.leqee.wms.api.biz.OrderInfoApiBiz;
import com.leqee.wms.api.request.GetFrozenRequest;
import com.leqee.wms.api.request.GetInventoryRequest;
import com.leqee.wms.api.request.GetVarianceImproveTaskListRequest;
import com.leqee.wms.api.response.GetFrozenResponse;
import com.leqee.wms.api.response.GetInventoryResponse;
import com.leqee.wms.api.response.GetVarianceImproveTaskListResponse;
import com.leqee.wms.api.response.domain.FrozenDetailResDomain;
import com.leqee.wms.api.response.domain.InventoryDetailResDomain;
import com.leqee.wms.api.response.domain.InventorySummaryResDomain;
import com.leqee.wms.api.response.domain.VarianceImproveTaskResDomain;
import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.util.JacksonJsonUtil;

/**
 * 有关库存相关的业务
 * @author hzhang1
 * @date 2016-2-23
 * @version 1.0.0
 */
@Controller
@RequestMapping(value="/api/inventory")
public class InventoryService {
	private Logger logger = Logger.getLogger(InventoryService.class);
	@Autowired
	InventoryBiz inventoryBiz;
	
	@Autowired
	InventoryItemApiBiz inventoryItemApiBiz;
	
	@Autowired
	OrderInfoApiBiz orderInfoApiBiz;
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getinventory", method = RequestMethod.POST)
	@ResponseBody
	public Object getinventory(HttpServletRequest request){
		
		logger.info("Enter the api of getinventory...");
		
		GetInventoryResponse getInventoryResponse = new GetInventoryResponse();
		GetInventoryRequest getInventoryRequest = null;
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
		
		// 2.将request转换成对象
		try {
			getInventoryRequest = (GetInventoryRequest) JacksonJsonUtil.jsonToBean(data, GetInventoryRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			
			logger.error("getInventoryResponse error:", e);
			getInventoryResponse.setResult("failure");
			getInventoryResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			getInventoryResponse.setErrors(errorsList);
			return getInventoryResponse;
		}
		
		// 3.调用API Biz层查询库存方法
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			resMap = inventoryItemApiBiz.getInventory(getInventoryRequest,customerId);
		} catch (Exception e) {
			logger.error("getInventory异常", e);
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			e.printStackTrace();
		}
		
		if("success".equals(resMap.get("result").toString())){
			getInventoryResponse.setResult("success");
			getInventoryResponse.setNote("success");
			//查询类型为SUMMARY时，初始化Response的inventorySummaryResDomains值
			getInventoryResponse.setErrors(null);
			if( GetInventoryRequest.TYPE_SUMMARY.equals(getInventoryRequest.getType()) ){
				getInventoryResponse.setInventorySummaryResDomains((List<InventorySummaryResDomain>) resMap.get("inventorySummaryResDomains"));
			}
			//查询类型为DETAIL时，初始化Response的inventorySummaryResDomains值
			if( GetInventoryRequest.TYPE_DETAIL.equals(getInventoryRequest.getType()) ){
				getInventoryResponse.setInventoryDetailResDomains((List<InventoryDetailResDomain>) resMap.get("inventoryDetailResDomains"));
			}
		}else{
			getInventoryResponse.setResult("failure");
			getInventoryResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			getInventoryResponse.setErrors(errorsList);
		}
		
		// 4.返回getInventoryResponse
		return getInventoryResponse;
	}
	
	
	@RequestMapping(value="/getfrozen", method = RequestMethod.POST)
	@ResponseBody
	public Object getfrozen(HttpServletRequest request){
		
		logger.info("Enter the api of getfrozen...");
		
		GetFrozenResponse getFrozenResponse = new GetFrozenResponse();
		GetFrozenRequest getFrozenRequest = null;
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
		
		// 2.将request转换成对象
		try {
			getFrozenRequest = (GetFrozenRequest) JacksonJsonUtil.jsonToBean(data, GetFrozenRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			
			logger.error("getFrozenResponse error:", e);
			getFrozenResponse.setResult("failure");
			getFrozenResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			getFrozenResponse.setErrors(errorsList);
			return getFrozenResponse;
		}
		
		// 3.调用API Biz层查询库存方法
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			resMap = inventoryItemApiBiz.getFrozen(getFrozenRequest,customerId);
		} catch (Exception e) {
			logger.error("getFrozen异常", e);
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			logger.error("getFrozen异常", e);
		}
		
		if("success".equals(resMap.get("result").toString())){
			getFrozenResponse.setResult("success");
			getFrozenResponse.setNote("success");
			//查询类型为SUMMARY时，初始化Response的inventorySummaryResDomains值
			getFrozenResponse.setErrors(null);
			getFrozenResponse.setFrozenDetailResDomains((List<FrozenDetailResDomain>) resMap.get("frozenDetailResDomains"));
		}else{
			getFrozenResponse.setResult("failure");
			getFrozenResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			getFrozenResponse.setErrors(errorsList);
		}
		
		// 4.返回getFrozenResponse
		return getFrozenResponse;
		
	}
	
	
	@RequestMapping(value="/getvarianceimprovetasklist", method = RequestMethod.POST)
	@ResponseBody
	public Object getvarianceimprovetasklist(HttpServletRequest request){
		
		logger.info("Enter the api of getvarianceimprovetasklist...");
		
		GetVarianceImproveTaskListResponse getVarianceImproveTaskListResponse = new GetVarianceImproveTaskListResponse();
		GetVarianceImproveTaskListRequest getVarianceImproveTaskListRequest = null;
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
		
		// 2.将request转换成对象
		try {
			getVarianceImproveTaskListRequest = (GetVarianceImproveTaskListRequest) JacksonJsonUtil.jsonToBean(data, GetVarianceImproveTaskListRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			
			logger.error("getVarianceImproveTaskListResponse error:", e);
			getVarianceImproveTaskListResponse.setResult("failure");
			getVarianceImproveTaskListResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			getVarianceImproveTaskListResponse.setErrors(errorsList);
			return getVarianceImproveTaskListResponse;
		}
		
		// 3.调用API Biz层查询库存方法
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			resMap = inventoryItemApiBiz.getVarianceImproveTaskList(getVarianceImproveTaskListRequest,customerId);
		} catch (Exception e) {
			logger.error("getVarianceImproveTaskList异常", e);
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			logger.error("getVarianceImproveTaskList异常", e);
		}
		
		if("success".equals(resMap.get("result").toString())){
			getVarianceImproveTaskListResponse.setResult("success");
			getVarianceImproveTaskListResponse.setNote("success");
			//查询类型为SUMMARY时，初始化Response的inventorySummaryResDomains值
			getVarianceImproveTaskListResponse.setErrors(null);
			getVarianceImproveTaskListResponse.setVarianceImproveTaskResDomainList((List<VarianceImproveTaskResDomain>) resMap.get("varianceImproveTaskResDomainList"));
			getVarianceImproveTaskListResponse.setTotal_count( (Integer)resMap.get("total_count") );

		}else{
			getVarianceImproveTaskListResponse.setResult("failure");
			getVarianceImproveTaskListResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			getVarianceImproveTaskListResponse.setErrors(errorsList);
		}
		
		// 4.返回getVarianceImproveTaskListResponse
		return getVarianceImproveTaskListResponse;
		
	}
	
	
	
}
