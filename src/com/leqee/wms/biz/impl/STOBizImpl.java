package com.leqee.wms.biz.impl;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.leqee.wms.biz.HTBiz;
import com.leqee.wms.biz.STOBiz;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ShippingTrackingNumberRepositoryDao;
import com.leqee.wms.util.DigestUtil;
import com.leqee.wms.util.HttpUtil;
import com.leqee.wms.util.ShippingUrlUtil;
import com.leqee.wms.util.WorkerUtil;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

@Service
public class STOBizImpl implements STOBiz {
	
	private Logger logger = Logger.getLogger(STOBizImpl.class);
	@Autowired
	ShippingUrlUtil shippingUrlUtil;
	@Autowired
	ShippingTrackingNumberRepositoryDao shippingTrackingNumberRepositoryDao;
	@Autowired
	OrderProcessDao orderProcessDao;
	 
    static String statMethod(String errorCode){
    	Map<String,String> map = new HashMap<String,String>();
    	map.put("100", "转换请求参数对象失败");
    	map.put("101", "签名不正确");
    	map.put("103", "查询成功");
    	map.put("104", "没有足够的单号");
    	map.put("105", "推送记录不能大于1000");
    	map.put("106", "运单编号未发放,不允许录单");
    	map.put("107", "运单编号已录单,不允许重复录入");
    	map.put("108", "发放客户或者发放网点必须要填写");
    	map.put("109", "请填写需要推送过来的数据");
    	map.put("110", "撤销运单数量大于100");
    	map.put("111", "参数有误");
    	map.put("112", "运单撤销失败!");
    	map.put("113", "含有无法解析字符,请检查传入的JSON数据是否正确");
    	map.put("114", "推送数据中必须要有运单编号");
    	map.put("115", "寄件日期必须要填写");
    	map.put("116", "网点名称必须要填写");
    	map.put("117", "客户名称必须要填写");
    	map.put("118", "寄件人必须要填写");
    	map.put("119", "寄件人电话必须要填写");
    	map.put("120", "收件人必须要填写");
    	map.put("121", "收件人电话必须要填写");
    	map.put("122", "录入时间必须要填写");
    	map.put("123", "录入人必须要填写");
    	map.put("124", "录入网点必须要填写");
    	map.put("125", "收件省份必须要填写");
    	map.put("126", "收件城市必须要填写");
    	map.put("127", "收件地区必须要填写");
    	map.put("128", "收件地址必须要填写");
    	map.put("129", "寄件省份必须要填写");
    	map.put("130", "寄件城市必须要填写");
    	map.put("131", "寄件地区必须要填写");
    	map.put("132", "寄件地址必须要填写");
    	map.put("133", "客户密码不正确");
    	return map.get(errorCode).toString();
    }
	
	public void batchApplySTOTrackingNumber(Map shippingApp) throws Exception {
		
		String appKey = String.valueOf(shippingApp.get("app_key"));
		String appSecret = String.valueOf(shippingApp.get("app_secret"));
		String cusite = String.valueOf(shippingApp.get("ht_express_code")); //此字段已兼容使用作为申通网点信息
		Integer arrlyAmount = Integer.parseInt(String.valueOf(shippingApp.get("apply_amount")));
		String bizData = "";
		Map<String, Object> map = new HashMap<String, Object>();
		String result = bizData+appSecret;
        //String content = DigestUtil.encodeBase64(DigestUtil.toStringHex(DigestUtil.encryptMD5(result)));
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        //申通调用方法code  vip0009
        nvps.add(new BasicNameValuePair("code","vip0009"));
        //加密验证方式 不受账号信息影响
		nvps.add(new BasicNameValuePair("data_digest","721ed5587c278029099a33ce60e64f85"));
		nvps.add(new BasicNameValuePair("cuspwd",appSecret));
		nvps.add(new BasicNameValuePair("cusname",appKey));
		//cusite 字段考虑添加
		nvps.add(new BasicNameValuePair("cusite",cusite));
		nvps.add(new BasicNameValuePair("len",arrlyAmount.toString()));
		while(true){
			String res = HttpUtil.StoPost(shippingUrlUtil.getStoServerUrl(), nvps); 
			//将获取到的数据转化成json格式
			JSONObject json= new JSONObject(res);  
			result = json.getString("success");
			if("true".equalsIgnoreCase(result)){
				String data = json.getString("data");
				logger.info(data);
				String [] mailnos = data.split(",");
				String localUser = (String)SecurityUtils.getSubject().getPrincipal();
				if(WorkerUtil.isNullOrEmpty(localUser)){
					localUser="SYSTEM";
				}
				for(int i=0;i<mailnos.length;i++){ 
					String mailno = mailnos[i].toString();
					shippingTrackingNumberRepositoryDao.insertTrackingNumer(Integer.parseInt(String.valueOf(shippingApp.get("app_id"))),mailno,"N",localUser,WorkerUtil.getNow());
				}
			}else{
				String code = json.getString("message");
				String message = statMethod(code);
				logger.info("res:"+res);
				logger.info("申请申通面单错误：(错误码)"+code+";信息："+message);
				break;
			}
		}
	}


	@Override
	public void reportSTOTrackingNumber(Map shippingInfo) throws Exception{
		String appKey = String.valueOf(shippingInfo.get("app_key"));
		String appSecret = String.valueOf(shippingInfo.get("app_secret"));
		String cusite = String.valueOf(shippingInfo.get("ht_express_code"));
		String orderId = String.valueOf(shippingInfo.get("order_id"));
		String shipmentId = String.valueOf(shippingInfo.get("shipment_id"));
		
		String sendCity = ""+shippingInfo.get("send_province")+","+shippingInfo.get("send_city")+","+shippingInfo.get("send_district");
		String sendCity1 = ""+shippingInfo.get("send_province")+","+shippingInfo.get("send_city");
		String sendAddress = String.valueOf(shippingInfo.get("address"));
		String sendPhone = String.valueOf(shippingInfo.get("contact_mobile"));
		String name = String.valueOf(shippingInfo.get("name"));
		
		String receiveName = StringEscapeUtils.escapeXml(String.valueOf(shippingInfo.get("receive_name")));
		String receiveCity = ""+shippingInfo.get("province_name")+","+shippingInfo.get("city_name")+((WorkerUtil.isNullOrEmpty(shippingInfo.get("district_name")))?"":(","+shippingInfo.get("district_name")));
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

		//String data = "{\"id\":\""+orderId+shipmentId+"\",\"mailno\":\""+tracking_number+"\",\"sender\":{\"name\":\"乐其\",\"phone\":\""+sendPhone+"\",\"city\":\""+sendCity+"\",\"address\":\""+sendAddress+"\"},\"receiver\": {\"name\":\""+receiveName+"\",\"mobile\":\""+receivePhone+"\",\"city\":\""+receiveCity+"\",\"address\":\""+receiveAddress+"\"}}";
//		//System.out.println(data);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = sdf.format(new Date());
		try{
			//String content = DigestUtil.encryptBASE64(data);
			 List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			    JSONObject json= new JSONObject();  
			 	json.put("billno",tracking_number);
			 	json.put("senddate", datetime);
			 	json.put("sendsite", cusite);
			 	json.put("sendcus", name);
			 	json.put("sendperson", name);
			 	json.put("sendtel", sendPhone);
			 	json.put("receivecus", "");
			 	json.put("receiveperson", receiveName);
			 	json.put("receivetel", receivePhone);
			 	json.put("goodsname", shipmentId);
			 	json.put("inputdate", datetime);
			 	json.put("inputperson", "乐其");
			 	json.put("inputsite", "");
			 	json.put("lasteditdate", "");
			 	json.put("lasteditperson", "");
			 	json.put("lasteditsite", "");
			 	json.put("remark", "");
			 	json.put("receiveprovince",shippingInfo.get("province_name"));
			 	json.put("receivecity",shippingInfo.get("city_name"));
			 	json.put("receivearea",shippingInfo.get("city_name"));
			 	json.put("receiveaddress",receiveAddress);
			 	json.put("sendprovince",shippingInfo.get("send_province"));
			 	json.put("sendcity",shippingInfo.get("send_city"));
			 	json.put("sendarea",shippingInfo.get("send_city"));
			 	json.put("sendaddress",sendAddress);
			 	json.put("weight","");
			 	json.put("productcode","");
			 	json.put("sendpcode","");
			 	json.put("sendccode","");
			 	json.put("sendacode","");
			 	json.put("receivepcode","");
			 	json.put("receiveccode","");
			 	json.put("receiveacode","");
			 	json.put("bigchar",sendCity1);
			 	json.put("orderno",orderId);
//			 	logger.info(json);
		        nvps.add(new BasicNameValuePair("code","vip0007"));
				nvps.add(new BasicNameValuePair("data_digest","721ed5587c278029099a33ce60e64f85"));
				nvps.add(new BasicNameValuePair("cuspwd",appSecret));
				nvps.add(new BasicNameValuePair("data","["+json.toString()+"]"));
				//nvps.add(new BasicNameValuePair("cusname",appKey));
				//nvps.add(new BasicNameValuePair("cusite","广东东莞公司"));
				//nvps.add(new BasicNameValuePair("data","推送数据"));
				String res = HttpUtil.StoPost(shippingUrlUtil.getStoServerUrl(), nvps); 
				logger.info(res);
			JSONObject json1= new JSONObject(res);  
			if(json1.has("success") && "false".equalsIgnoreCase(json1.getString("success"))){
				String message = json1.has("message")?json1.getString("message"):"";
				shippingTrackingNumberRepositoryDao.updateRepositoryTrackingNumber(tracking_number, "E");
				logger.info("快递单号"+tracking_number+" 回传失败！返回错误："+message);
			}else if("true".equalsIgnoreCase(json1.getString("success"))){
				String data = json1.has("data")?json1.getString("data"):"";
				logger.info(data);
				shippingTrackingNumberRepositoryDao.updateRepositoryTrackingNumber(tracking_number, "F");
			}
		}catch (Exception e) {
			logger.info("快递单号"+tracking_number+" 回传失败！原因："+e.getMessage());
		}
	}

	
}