package com.leqee.wms.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.leqee.wms.api.ApiException;
import com.leqee.wms.api.LeqeeError;
import com.leqee.wms.api.LeqeeResponse;
import com.leqee.wms.api.util.LeqeeHashMap;
import com.leqee.wms.api.util.LeqeeUtils;
import com.leqee.wms.api.util.RequestParametersHolder;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.util.JacksonJsonUtil;

/**
* API URL过滤验证是否有权限调用
* @author hzhang1
*/
public class ApiFilter extends OncePerRequestFilter {
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	
	private Logger logger = Logger.getLogger(ApiFilter.class);
//	private String encode = "UTF-8";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// 1.获取到请求的URL
		String uri = request.getRequestURI();
		logger.info("此次请求获取到的url:"+uri);
		
		
		// 2.解析请求的request
		int contentLength = request.getContentLength();
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {
            int readlen = request.getInputStream().read(buffer, i,contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        String content = new String(buffer, "UTF-8");
        
        //System.out.println(content);
        
        JSONObject reqJson  = JSONObject.fromObject( content.trim().toString());
        logger.info("reqJson:"+reqJson.toString());
		request.setAttribute("app_key", reqJson.getString("app_key"));
		request.setAttribute("data", reqJson.getString("data"));
		request.setAttribute("api_method_name", reqJson.getString("api_method_name"));
		
			
		// 解析获取到的请求
		String appKey = reqJson.getString("app_key");
		String apiMethodName = reqJson.getString("api_method_name");
        String msgId = reqJson.getString("msg_id");
        String data = reqJson.getString("data");
        String appSecret = null;
        if("sync.region.request".equals(apiMethodName)){
        	 appSecret = "appsecret123456";
        }else if ("sync.shop.request".equals(apiMethodName)) {
        	 appSecret = "appsecret123456";
		}else{
        	appSecret = findAppSercetByAppKey(appKey);  //伪方法
        }
        
        TreeMap<String, String> requestParamsMap = new TreeMap<String, String>();  
		requestParamsMap.put("app_key", appKey);
		requestParamsMap.put("api_method_name", apiMethodName);
		requestParamsMap.put("app_secret", appSecret);
		requestParamsMap.put("msg_id", msgId);
		requestParamsMap.put("data", data);
  	   
  	  
  	    String client_sign = reqJson.getString("sign");
  	    String server_sign = LeqeeUtils.createSign(requestParamsMap);
  	    
  	    //System.out.println("client sign:"+client_sign);
        //System.out.println("server sign:"+server_sign);
        
		
		
		if(client_sign.equals(server_sign)){
			//System.out.println("success");
			// 如果不执行过滤，则继续
			filterChain.doFilter(request, response);
		}else{
			//System.out.println("fail");
			// 如果session中不存在登录者实体，则弹出框提示重新登录
			// 设置request和response的字符集，防止乱码
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			
			LeqeeResponse leqeeResponse = new LeqeeResponse() {
			};
			leqeeResponse.setResult("failure");
			leqeeResponse.setNote("错误的授权参数appKey:"+appKey +"appSecret:" + appSecret );
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("400015");
			error.setErrorInfo("错误的授权参数appKey:"+appKey +"appSecret:" + appSecret );
			errorsList.add(error);
			leqeeResponse.setErrors(errorsList);
			
			String respJsonStr = "";
			try {
				respJsonStr = JacksonJsonUtil.beanToJson(leqeeResponse);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			out.print(respJsonStr);
		} 
		
	}
	
	
	private String findAppSercetByAppKey(String appKey) {
		WarehouseCustomer warehouseCustomer =  warehouseCustomerDao.selectByAppKey(appKey);
		return warehouseCustomer != null ? warehouseCustomer.getApp_secret():"";
	}

}

