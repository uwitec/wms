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

import com.leqee.wms.api.LeqeeError;
import com.leqee.wms.api.biz.ProductApiBiz;
import com.leqee.wms.api.request.SyncProductRequest;
import com.leqee.wms.api.response.SyncProductResponse;
import com.leqee.wms.util.JacksonJsonUtil;

/**
 * @author hzhang1
 * @date 2016-2-23
 * @version 1.0.0
 */
@Controller
@RequestMapping(value="/api/product")
public class ProductService {
	private Logger logger = Logger.getLogger(ProductService.class);
	@Autowired
	ProductApiBiz productBiz;
	
	@RequestMapping(value="/syncproduct") 
	@ResponseBody
	public SyncProductResponse syncproduct(HttpServletRequest request) { //
		logger.info("Enter the api of syncproduct...");
		String data = (String) request.getAttribute("data");
		String app_key = (String) request.getAttribute("app_key");
		logger.info("data:"+data);
		logger.info("app_key:"+app_key);
		SyncProductRequest productRequest = null;
		SyncProductResponse productResponse = new SyncProductResponse();
		
		
		// 1.检查app_key生成customer_id...
		Integer customerId = productBiz.getcustomerIdByAppKey(app_key);
				
		// 1.将request转换为productRequest对象
		try {
			productRequest = (SyncProductRequest) JacksonJsonUtil.jsonToBean(data, SyncProductRequest.class);
		} catch (Exception e) {
			logger.error("ProductResponse error:", e);
			String errorStr = e.getMessage().toString();
			String msg = errorStr.substring(0, errorStr.indexOf("("));
			productResponse.setResult("faliure");
			productResponse.setNote("json转换成对象失败:"+msg);
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40005");
			error.setErrorInfo("json转换成对象失败:"+msg);
			errorsList.add(error);
			productResponse.setErrors(errorsList);
		}
		
		// 2.对product对象操作
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			resMap = productBiz.syncProduct(productRequest,customerId);
		} catch (Exception e) {
			logger.error("syncProduct异常", e);
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			e.printStackTrace();
		}
		
		
		// 3.返回productResponse
		if("failure".equals(resMap.get("result").toString())){
			productResponse.setResult("failure");
			productResponse.setNote("失败");
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo( resMap.get("msg")== null ? "没有对应的msg": resMap.get("msg").toString()  );
			errorsList.add(error);
			productResponse.setErrors(errorsList);
		}else{
			productResponse.setResult("success");
			productResponse.setNote("成功");
			String productId = resMap.get("productId").toString();
			String skuCode = resMap.get("skuCode").toString();
			productResponse.setProduct_id(productId);
			productResponse.setSku_code(skuCode);
		}
		
        return productResponse;  
    }
}
