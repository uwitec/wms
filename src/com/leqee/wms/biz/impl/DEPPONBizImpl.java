package com.leqee.wms.biz.impl;

import java.io.StringReader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.leqee.wms.api.util.Base64;
import com.leqee.wms.biz.DepponBiz;
import com.leqee.wms.biz.HTBiz;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ShippingTrackingNumberRepositoryDao;
import com.leqee.wms.util.DigestUtil;
import com.leqee.wms.util.HttpUtil;
import com.leqee.wms.util.ShippingUrlUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class DEPPONBizImpl implements DepponBiz {
	
	private Logger logger = Logger.getLogger(HTBizImpl.class);
	@Autowired
	ShippingUrlUtil shippingUrlUtil;
	@Autowired
	ShippingTrackingNumberRepositoryDao shippingTrackingNumberRepositoryDao;
	@Autowired
	OrderProcessDao orderProcessDao;

	@Override
	public void reportDepponTrackingNumber(Map shippingInfo) throws Exception{
		String appKey = String.valueOf(shippingInfo.get("app_key"));
		//渠道名称：乐其
		//渠道代号：EWBLQ
		//SIGN值：ELQ
		//客户编码：401430484
		String companyCode = "EWBLQ";
		String customerCode = "401430484"; 
//		String appSecret = String.valueOf(shippingInfo.get("app_secret"));//对德邦无用

		String orderId = String.valueOf(shippingInfo.get("order_id"));
		String shipmentId = String.valueOf(shippingInfo.get("shipment_id"));
		String logisticId = "ELQ"+orderId+shipmentId;
		String sendAddress = String.valueOf(shippingInfo.get("address"));
		String sendPhone = String.valueOf(shippingInfo.get("contact_mobile"));
		
		String receiveName = StringEscapeUtils.escapeXml(String.valueOf(shippingInfo.get("receive_name")));
		String receiveAddress = StringEscapeUtils.escapeXml(String.valueOf(shippingInfo.get("shipping_address")));
		String receivePhone = "";
		if(WorkerUtil.isNullOrEmpty(shippingInfo.get("phone_number")) || "".equals(shippingInfo.get("phone_number"))){
			if(WorkerUtil.isNullOrEmpty(shippingInfo.get("mobile_number")) || "".equals(shippingInfo.get("mobile_number"))){
				receivePhone = ""+shippingInfo.get("contact_mobile");
			}else{
				receivePhone = ""+shippingInfo.get("mobile_number");
			}
		}else{
			receivePhone = ""+shippingInfo.get("phone_number");
		}
		
		String tracking_number = String.valueOf(shippingInfo.get("tracking_number"));
		
		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = sdf.format(new Date());
		
		String data = "{\"logisticCompanyID\":\"DEPPON\",\"logisticID\":\""+logisticId+"\",\"mailNo\":\""
		+tracking_number+"\",\"orderSource\":\""+companyCode+"\",\"serviceType\":\"2\",\"customerCode\":\""+
		customerCode+"\",\"sender\":{\"name\":\"乐其\",\"phone\":\""+sendPhone+"\",\"province\":\""+shippingInfo.get("send_province")+
		"\",\"city\":\""+shippingInfo.get("send_city")+"\",\"county\":\""+shippingInfo.get("send_district")+"\",\"address\":\""+sendAddress+
		"\"},\"receiver\": {\"name\":\""+receiveName+"\",\"mobile\":\""+receivePhone+"\",\"province\":\""+shippingInfo.get("province_name")+
		"\",\"city\":\""+shippingInfo.get("city_name")+"\",\"county\":\""+((WorkerUtil.isNullOrEmpty(shippingInfo.get("district_name")))?"":shippingInfo.get("district_name"))+
		"\",\"address\":\""+receiveAddress+"\"},\"gmtCommit\":\""+datetime+"\",\"cargoName\":\"母婴用品\",\"totalNumber\":1,\"totalWeight\":0,\"payType\":\"2\",\"transportType\":\"PACKAGE\",\"deliveryType\":\"3\",\"backSignBill\":\"0\"}";
		long timestamp = System.currentTimeMillis();
		try{
			String content = DigestUtil.encryptBASE64(DigestUtil.digest(data, appKey,timestamp+"","").toLowerCase());
			map.put("params", data);
			map.put("digest", content);
			map.put("timestamp", timestamp);//当前时间戳
			map.put("companyCode", companyCode);
			String res = HttpUtil.post(shippingUrlUtil.getDepponServerUrl(), "utf-8", map); 
			//"{	\"reason\":\"摘要验证失败\",	\"result\":\"false\",	\"resultCode\":\"2002\",	\"uniquerRequestNumber\":\"120362045463047\"}";
//			"{	\"logisticID\":\"ELQ1999915375173337\",	\"reason\":\"成功\",	\"result\":\"true\",	\"resultCode\":\"1000\",	\"uniquerRequestNumber\":\"121714986616809\"}";
			JSONObject json= new JSONObject(res);  
			if(json.has("result") && "false".equalsIgnoreCase(json.getString("result"))){
				String uniquerRequestNumber = json.has("uniquerRequestNumber")?json.getString("uniquerRequestNumber"):"";
				String resultCode = json.has("resultCode")?json.getString("resultCode"):"";
				String reason = json.getString("reason");
				logger.info("DEPPON res:"+res);
				//德邦对接要求 ： 如果出现错误，下次需要再次回传
				shippingTrackingNumberRepositoryDao.updateRepositoryTrackingNumber(tracking_number, "E");
				logger.info("快递单号"+tracking_number+" 回传失败！返回错误："+uniquerRequestNumber+"("+resultCode+":"+reason+")");
			}else if("true".equalsIgnoreCase(json.getString("result"))){
				shippingTrackingNumberRepositoryDao.updateRepositoryTrackingNumber(tracking_number, "F");
			}
		}catch (Exception e) {
			logger.info("快递单号"+tracking_number+" 回传失败！原因："+e.getMessage());
		}
	}

	
}