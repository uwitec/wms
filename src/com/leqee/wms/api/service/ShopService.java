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
import com.leqee.wms.api.biz.ShopApiBiz;
import com.leqee.wms.api.request.SyncProductRequest;
import com.leqee.wms.api.request.SyncRegionRequest;
import com.leqee.wms.api.request.SyncShopRequest;
import com.leqee.wms.api.response.SyncProductResponse;
import com.leqee.wms.api.response.SyncRegionResponse;
import com.leqee.wms.api.response.SyncShopResponse;
import com.leqee.wms.util.JacksonJsonUtil;

/**
 * @author hchen1
 * @date 2016-8-23
 * @version 1.0.0
 */
@Controller
@RequestMapping(value="/api/shop")
public class ShopService {
	private Logger logger = Logger.getLogger(ShopService.class);
	@Autowired
	ShopApiBiz shopBiz;
	
	@RequestMapping(value="/syncshop") 
	@ResponseBody
	public SyncShopResponse syncShop(HttpServletRequest request) { //
		logger.info("Enter the api of syncShop...");
		String data = (String) request.getAttribute("data");
		String app_key = (String) request.getAttribute("app_key");
		logger.info("data:"+data);
		logger.info("app_key:"+app_key);
		SyncShopRequest shopRequest = null;
		SyncShopResponse shopResponse = new SyncShopResponse();
		
		
		// 1.检查app_key生成customer_id...
		//Integer customerId = regionBiz.getcustomerIdByAppKey(app_key);
				
		// 1.将request转换为regionRequest对象
		try {
			shopRequest = (SyncShopRequest) JacksonJsonUtil.jsonToBean(data, SyncShopRequest.class);
		} catch (Exception e) {
			logger.error("shopRequest error:", e);
			String errorStr = e.getMessage().toString();
			String msg = errorStr.substring(0, errorStr.indexOf("("));
			shopResponse.setResult("faliure");
			shopResponse.setNote("json转换成对象失败:"+msg);
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40005");
			error.setErrorInfo("json转换成对象失败:"+msg);
			errorsList.add(error);
			shopResponse.setErrors(errorsList);
		}
		
		// 2.对region对象操作
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			resMap = shopBiz.syncShop(shopRequest);
		} catch (Exception e) {
			logger.error("syncshop异常", e);
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			e.printStackTrace();
		}
		
		
		// 3.返回regionResponse
		if("failure".equals(resMap.get("result").toString())){
			shopResponse.setResult("failure");
			shopResponse.setNote("失败");
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			shopResponse.setErrors(errorsList);
		}else{
			shopResponse.setResult("success");
			shopResponse.setNote("成功");
			String shopId = resMap.get("shopId").toString();
			String shopName = resMap.get("shopName").toString();
			shopResponse.setShop_id(Integer.parseInt(shopId));
			shopResponse.setShop_name(shopName);
		}
		
        return shopResponse;  
    }
}
