package com.leqee.wms.biz.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.leqee.wms.biz.HTBiz;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ShippingTrackingNumberRepositoryDao;
import com.leqee.wms.util.DigestUtil;
import com.leqee.wms.util.HttpUtil;
import com.leqee.wms.util.ShippingUrlUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class HTBizImpl implements HTBiz {
	
	private Logger logger = Logger.getLogger(HTBizImpl.class);
	@Autowired
	ShippingUrlUtil shippingUrlUtil;
	@Autowired
	ShippingTrackingNumberRepositoryDao shippingTrackingNumberRepositoryDao;
	@Autowired
	OrderProcessDao orderProcessDao;
	
	public void batchApplyHTTrackingNumber(Map shippingApp) throws Exception {
		
		String appKey = String.valueOf(shippingApp.get("app_key"));
		String appSecret = String.valueOf(shippingApp.get("app_secret"));
		Integer arrlyAmount = Integer.parseInt(String.valueOf(shippingApp.get("apply_amount")));
		String bizData = "";
		Map<String, Object> map = new HashMap<String, Object>();
		String msgId = UUID.randomUUID().toString(); 
		bizData = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>"
			+ "<FetchBillCodeRequest xmlns:ems='http://express.800best.com'><count>"+arrlyAmount+"</count></FetchBillCodeRequest>";
		 
		String result = bizData+appSecret;
        String content = DigestUtil.encodeBase64(DigestUtil.toStringHex(DigestUtil.encryptMD5(result)));
		map.put("bizData", bizData);
		map.put("serviceType", "BillCodeFetchRequest");
		map.put("parternID", appKey);
		map.put("digest", content);
		map.put("msgId", msgId);
		String res = HttpUtil.post(shippingUrlUtil.getHtServerUrl(), "utf-8", map); 
//		//System.out.println(res);
		
//		String res = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><FetchBillCodeResponse xmlns:ems='http://express.800best.com'>    <result>SUCCESS</result>    <billCodes>50286300309891</billCodes>    <billCodes>50286308309692</billCodes>   <billCodes>50286300309493</billCodes></FetchBillCodeResponse>";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(res)));

		Element root = doc.getDocumentElement();
		NodeList books = root.getChildNodes();
		String responseResult = "";
		String errorCode = "";
		String errorDesc = "";
		List<String> billCodesList = new ArrayList<String>();  
		
		if (books != null) {
			for (int i = 0; i < books.getLength(); i++) {
				Node book = books.item(i);
				if (book.getNodeName().equals("result")) {
					responseResult = book.getTextContent();
				}else if(book.getNodeName().equals("success")){
					responseResult = book.getTextContent();
				}
				if ("SUCCESS".equalsIgnoreCase(responseResult) && book.getNodeName().equals("billCodes")) {
					billCodesList.add(book.getTextContent());
					continue;
				} else if ("FAIL".equalsIgnoreCase(responseResult)) {
					if (book.getNodeName().equals("errorCode")) {
						errorCode = book.getTextContent();
					}
					if (book.getNodeName().equals("errorDesc")) {
						errorDesc = book.getTextContent();
					}
				}else if("false".equalsIgnoreCase(responseResult)  && book.getNodeName().equals("reason")){
					errorCode = book.getTextContent();
					if(errorCode.equals("S01")){
						errorDesc = "汇通快递系统错误";
					}else if(errorCode.equals("S02")){
						errorDesc = "签名验证失败";
					}else if(errorCode.equals("S03")){
						errorDesc = "无法识别的请求类型";
					}else if(errorCode.equals("S04")){
						errorDesc = "请求格式错误";
					}
				}
			}
			if("SUCCESS".equalsIgnoreCase(responseResult)){
				String localUser = (String)SecurityUtils.getSubject().getPrincipal();
				if(WorkerUtil.isNullOrEmpty(localUser)){
					localUser="system";
				}
				for (String mailno : billCodesList) {
					shippingTrackingNumberRepositoryDao.insertTrackingNumer(Integer.parseInt(String.valueOf(shippingApp.get("app_id"))),mailno,"N",localUser,WorkerUtil.getNow());
				}
			}else{
				logger.info("申请汇通面单错误：(错误码)"+errorCode+";(描述)"+errorDesc);
			}
			
		}
	}


	@Override
	public void reportHTTrackingNumber(Map htShippingInfo) throws Exception{
		String appKey = String.valueOf(htShippingInfo.get("app_key"));
		String appSecret = String.valueOf(htShippingInfo.get("app_secret"));
		String bizData = "";
		Map<String, Object> map = new HashMap<String, Object>();
		String msgId = UUID.randomUUID().toString(); 
		
		String receiveNumber = "";
		if(WorkerUtil.isNullOrEmpty(htShippingInfo.get("phone_number")) || "".equals(htShippingInfo.get("phone_number"))){
			if(WorkerUtil.isNullOrEmpty(htShippingInfo.get("mobile_number")) || "".equals(htShippingInfo.get("mobile_number"))){
				receiveNumber = ""+htShippingInfo.get("contact_mobile");
			}else{
				receiveNumber = ""+htShippingInfo.get("mobile_number");
			}
		}else{
			receiveNumber = ""+htShippingInfo.get("phone_number");
		}
		bizData = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" +
				"<BillCodeFeedbackRequest xmlns:ems='http://express.800best.com'>" +
				"<createPrintFeedbackList>" +
				"<txLogisticID>"+htShippingInfo.get("order_id")+htShippingInfo.get("shipment_id")+"</txLogisticID>" +
				"<mailNo>"+htShippingInfo.get("tracking_number")+"</mailNo>" +
				"<sendMan>乐其</sendMan>" +
				"<sendManPhone>"+htShippingInfo.get("contact_mobile")+"</sendManPhone>" +
				"<sendManAddress>"+htShippingInfo.get("send_province")+htShippingInfo.get("send_city")+htShippingInfo.get("send_district")+htShippingInfo.get("address")+"</sendManAddress>" +
				"<sendProvince>"+htShippingInfo.get("send_province")+"</sendProvince>" +
				"<sendCity>"+htShippingInfo.get("send_city")+"</sendCity>" +
				"<sendCounty>"+htShippingInfo.get("send_district")+"</sendCounty>" +
				"<receiveMan>"+StringEscapeUtils.escapeXml(String.valueOf(htShippingInfo.get("receive_name")))+"</receiveMan>" +
				"<receiveManPhone>"+receiveNumber+"</receiveManPhone>" +
				"<receiveManAddress>"+StringEscapeUtils.escapeXml(String.valueOf((htShippingInfo.get("shipping_address"))))+"</receiveManAddress>" +
				"<receiveProvince>"+htShippingInfo.get("province_name")+"</receiveProvince>" +
				"<receiveCity>"+htShippingInfo.get("city_name")+"</receiveCity>" +
				"<receiveCounty>"+htShippingInfo.get("district_name")+"</receiveCounty>" +
				"</createPrintFeedbackList>" +
				"</BillCodeFeedbackRequest>";
		 
		String result = bizData+appSecret;
        String content = DigestUtil.encodeBase64(DigestUtil.toStringHex(DigestUtil.encryptMD5(result)));
		map.put("bizData", bizData);
		map.put("serviceType", "BillPrintDeliveryRequest");
		map.put("parternID", appKey);
		map.put("digest", content);
		map.put("msgId", msgId);
		String res = HttpUtil.post(shippingUrlUtil.getHtServerUrl(), "utf-8", map); 
//		//System.out.println(res);
		//<?xml version="1.0" encoding="UTF-8" standalone="yes"?><BillCodeFeedbackResponse xmlns:ems="http://express.800best.com">    <result>SUCCESS</result></BillCodeFeedbackResponse>

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(res)));

		Element root = doc.getDocumentElement();
		NodeList books = root.getChildNodes();
		String responseResult = "";
		String errorCode = "";
		String errorDesc = "";
		
		if (books != null) {
			for (int i = 0; i < books.getLength(); i++) {
				Node book = books.item(i);
				if (book.getNodeName().equals("result")) {
					responseResult = book.getTextContent();
				}else if(book.getNodeName().equals("success")){
					responseResult = book.getTextContent();
				}
				if ("SUCCESS".equalsIgnoreCase(responseResult)) {
					break;
				} else if ("FAIL".equalsIgnoreCase(responseResult)) {
					if (book.getNodeName().equals("errorCode")) {
						errorCode = book.getTextContent();
					}
					if (book.getNodeName().equals("errorDesc")) {
						errorDesc = book.getTextContent();
					}
				}
			}
			if("SUCCESS".equalsIgnoreCase(responseResult)){
				shippingTrackingNumberRepositoryDao.updateRepositoryTrackingNumber(String.valueOf(htShippingInfo.get("tracking_number")), "F");
			}else{
				shippingTrackingNumberRepositoryDao.updateRepositoryTrackingNumber(String.valueOf(htShippingInfo.get("tracking_number")), "E");
				logger.info("回传汇通面单错误：(错误码)"+errorCode+";(描述)"+errorDesc);
			}
		}
	}


	/**
	 * 汇通大头笔获取 
	 */
	@Override
	public String getHTMarkForSingleOrder(Map<String, Object> singleOrder) {
		String mark = "";
		if(WorkerUtil.isNullOrEmpty(singleOrder.get("ht_express_code"))) {
			return mark;
		}
		Integer orderId = Integer.parseInt(String.valueOf(singleOrder.get("order_id")));
		String appKey = singleOrder.get("app_key").toString();
		String appSecret = singleOrder.get("app_secret").toString();
		String address = 
				""+singleOrder.get("receive_province")+singleOrder.get("receive_city")+
				(WorkerUtil.isNullOrEmpty(singleOrder.get("receive_district"))?"":singleOrder.get("receive_district"))+
				StringEscapeUtils.escapeXml(String.valueOf((singleOrder.get("receive_address"))));
		String siteCode = String.valueOf(singleOrder.get("ht_express_code")); 
		try{
			String bizData = "";
			Map<String, Object> map = new HashMap<String, Object>();
			String msgId = UUID.randomUUID().toString(); 
			bizData = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>"
				+ "<request xmlns:ems='http://express.800best.com'><siteCode>"+siteCode+"</siteCode>" +
				"<address>"+address+"</address></request>";
//			//System.out.println("bizData:"+bizData);
			String result = bizData+appSecret;
	        String content = DigestUtil.encodeBase64(DigestUtil.toStringHex(DigestUtil.encryptMD5(result)));
			map.put("bizData", bizData);
			map.put("serviceType", "AddressResolutionService");
			map.put("parternID", appKey);
			map.put("digest", content);
			map.put("msgId", msgId);
			String res = HttpUtil.post(shippingUrlUtil.getHtServerUrl(), "utf-8", map); 
			
			/**
			 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?><response>    <province>浙江省</province>  
			 *   <city>嘉兴市</city>    <country>嘉善县</country>    <billMarker>浙-嘉善(浙-3)</billMarker>   
			 *    <successful>true</successful></response>
			 */
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(res)));

			Element root = doc.getDocumentElement();
			NodeList books = root.getChildNodes();
			if (books != null) {
				for (int i = 0; i < books.getLength(); i++) {
					Node book = books.item(i);
					if ("billMarker".equalsIgnoreCase(book.getNodeName())) {
//						logger.info(book.getTextContent());
						mark = book.getTextContent();
						orderProcessDao.updateShippingMark(orderId,mark);
						break;
					}
				}
			}
			if(mark==""){
				logger.info("订单"+orderId+" 申请大头笔失败！res:"+res);
			}
		}catch (Exception e) {
			logger.info("订单"+orderId+" 申请大头笔失败！原因："+e.getMessage());
		}
		return mark;
	}
	
}