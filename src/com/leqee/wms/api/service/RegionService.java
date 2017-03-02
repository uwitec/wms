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
import org.springframework.web.bind.annotation.ResponseBody;

import antlr.collections.impl.IntRange;

import com.leqee.wms.api.LeqeeError;
import com.leqee.wms.api.biz.ProductApiBiz;
import com.leqee.wms.api.biz.RegionApiBiz;
import com.leqee.wms.api.request.SyncProductRequest;
import com.leqee.wms.api.request.SyncRegionRequest;
import com.leqee.wms.api.response.SyncProductResponse;
import com.leqee.wms.api.response.SyncRegionResponse;
import com.leqee.wms.util.JacksonJsonUtil;

/**
 * @author hchen1
 * @date 2016-8-23
 * @version 1.0.0
 */
@Controller
@RequestMapping(value="/api/region")
public class RegionService {
	private Logger logger = Logger.getLogger(RegionService.class);
	@Autowired
	RegionApiBiz regionBiz;
	
	@RequestMapping(value="/syncregion") 
	@ResponseBody
	public SyncRegionResponse syncRegion(HttpServletRequest request) { //
		logger.info("Enter the api of syncRegion...");
		String data = (String) request.getAttribute("data");
		String app_key = (String) request.getAttribute("app_key");
		logger.info("data:"+data);
		logger.info("app_key:"+app_key);
		SyncRegionRequest regionRequest = null;
		SyncRegionResponse regionResponse = new SyncRegionResponse();
		
		
		// 1.检查app_key生成customer_id...
		Integer customerId = regionBiz.getcustomerIdByAppKey(app_key);
				
		// 1.将request转换为regionRequest对象
		try {
			regionRequest = (SyncRegionRequest) JacksonJsonUtil.jsonToBean(data, SyncRegionRequest.class);
		} catch (Exception e) {
			logger.error("regionRequest error:", e);
			String errorStr = e.getMessage().toString();
			String msg = errorStr.substring(0, errorStr.indexOf("("));
			regionResponse.setResult("faliure");
			regionResponse.setNote("json转换成对象失败:"+msg);
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40005");
			error.setErrorInfo("json转换成对象失败:"+msg);
			errorsList.add(error);
			regionResponse.setErrors(errorsList);
		}
		
		// 2.对region对象操作
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			resMap = regionBiz.syncRegion(regionRequest);
		} catch (Exception e) {
			logger.error("syncRegion异常", e);
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			e.printStackTrace();
		}
		
		
		// 3.返回regionResponse
		if("failure".equals(resMap.get("result").toString())){
			regionResponse.setResult("failure");
			regionResponse.setNote("失败");
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			regionResponse.setErrors(errorsList);
		}else{
			regionResponse.setResult("success");
			regionResponse.setNote("成功");
			String regionId = resMap.get("regionId").toString();
			String regionName = resMap.get("regionName").toString();
			regionResponse.setRegion_id(Integer.parseInt(regionId));
			regionResponse.setRegion_name(regionName);
		}
		
        return regionResponse;  
    }
}
