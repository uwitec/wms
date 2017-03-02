package com.leqee.wms.biz.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.ZTOBiz;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ShippingTrackingNumberRepositoryDao;
import com.leqee.wms.dao.ShippingZtoMarkDao;
import com.leqee.wms.util.DigestUtil;
import com.leqee.wms.util.HttpUtil;
import com.leqee.wms.util.ShippingUrlUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ZTOBizImpl implements ZTOBiz {
	
	private Logger logger = Logger.getLogger(ZTOBizImpl.class);
	@Autowired
	ShippingUrlUtil shippingUrlUtil;
	@Autowired
	ShippingTrackingNumberRepositoryDao shippingTrackingNumberRepositoryDao;
	@Autowired
	ShippingZtoMarkDao shippingZtoMarkDao;
	@Autowired
	OrderProcessDao orderProcessDao;
	
	public void batchApplyZTOTrackingNumber(Map shippingApp) throws Exception {
		//1. 
		Integer arrlyAmount = Integer.parseInt(String.valueOf(shippingApp.get("apply_amount")));
		//2.
		String appKey = String.valueOf(shippingApp.get("app_key"));
		String appSecret = String.valueOf(shippingApp.get("app_secret"));
		//3. 
		while(true){
			String maxTrackingNumber = shippingTrackingNumberRepositoryDao.searchMaxTrackingNumber(Integer.parseInt(String.valueOf(shippingApp.get("app_id"))));
			if(WorkerUtil.isNullOrEmpty(maxTrackingNumber)){
				maxTrackingNumber = "";
			}
			Map<String, Object> map = new HashMap<String, Object>();
			String data = "{\"number\": "+arrlyAmount+",\"lastno\":\""+maxTrackingNumber+"\"}";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datetime = sdf.format(new Date());
			String content = DigestUtil.encryptBASE64(data);
			map.put("content", content);
			map.put("style", "json");
			map.put("func", "mail.apply");
			map.put("partner", appKey);
			map.put("datetime", datetime);
			map.put("verify",DigestUtil.digest(appKey, datetime, content, appSecret));
			String res = HttpUtil.post(shippingUrlUtil.getZtoServerUrl(), "utf-8", map); // {"result":"true","remark":"","list":[100000020364]}
			//System.out.println(res);
			
			JSONObject json= new JSONObject(res);  
			String result = json.getString("result");
			if("true".equalsIgnoreCase(result)){
				JSONArray mailnos=json.getJSONArray("list");  
				String localUser = (String)SecurityUtils.getSubject().getPrincipal();
				if(WorkerUtil.isNullOrEmpty(localUser)){
					localUser="SYSTEM";
				}
				for(int i=0;i<mailnos.length();i++){ 
					String mailno = mailnos.get(i).toString();
					shippingTrackingNumberRepositoryDao.insertTrackingNumer(Integer.parseInt(String.valueOf(shippingApp.get("app_id"))),mailno,"N",localUser,WorkerUtil.getNow());
				}
			}else{
				String code = json.getString("code");
				String remark = json.getString("remark");
				logger.info("batchApplyZTOTrackingNumber shippingApp ("+shippingApp+")申请中通面单错误：(错误码)"+code+";(描述)"+remark);
				break;
			}
		}
	}

	@Override
	public String getZTOMarkForSingleOrder(Map<String, Object> singleOrder) {
		String mark = "";
		Integer orderId = Integer.parseInt(String.valueOf(singleOrder.get("order_id")));
		String appKey = singleOrder.get("app_key").toString();
		String appSecret = singleOrder.get("app_secret").toString();
		Map<String, Object> map = new HashMap<String, Object>();
		String sendCity = singleOrder.get("send_province")+","+singleOrder.get("send_city")+","+singleOrder.get("send_district");
		String sendAddress = String.valueOf(singleOrder.get("send_address"));
		String receiverCity = singleOrder.get("receive_province")+","+singleOrder.get("receive_city")+","+singleOrder.get("receive_district");
		String receiverAddress = StringEscapeUtils.escapeXml(String.valueOf((singleOrder.get("receive_address"))));
		
		String data = "{\"sendcity\": \""+sendCity+"\",\"sendaddress\":\""+sendAddress+"\",\"receivercity\":\""+receiverCity+"\",\"receiveraddress\":\""+receiverAddress+"\"}";
//		//System.out.println("send_info:"+data);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = sdf.format(new Date());
		try{
			String content = DigestUtil.encryptBASE64(data);
			map.put("content", content);
			map.put("style", "json");
			map.put("func", "order.marke");
//			map.put("partner", "1000139036");
//			map.put("datetime", datetime);
//			map.put("verify",DigestUtil.digest("1000139036", datetime, content, "6KZBV7GC85"));
//			String res = HttpUtil.post("http://partner.zto.cn/partner/interface.php", "utf-8", map); 
			map.put("partner", appKey);
			map.put("datetime", datetime);
			map.put("verify",DigestUtil.digest(appKey, datetime, content, appSecret));
			String res = HttpUtil.post(shippingUrlUtil.getZtoServerUrl(), "utf-8", map); 
//			//System.out.println("response:"+res);
			/**
send_info:{"sendcity": "广东省,东莞市,长安镇","sendaddress":"步步高大道126号","receivercity":"安徽,芜湖,镜湖区","receiveraddress":"赭山公共服务中心银湖南路福达新村11-1-601"}
response:{"mark":"芜湖 16 30","marke":"芜湖 16 30"}
			 */
			JSONObject json= new JSONObject(res);  
			if(json.has("result")){
				String code = json.getString("code");
				String remark = json.getString("remark");
//				logger.info("订单"+orderId+" 申请大头笔返回错误："+code+";(描述)"+remark);
				String ws_mark = shippingZtoMarkDao.getMark(orderId);
				if (!WorkerUtil.isNullOrEmpty(ws_mark)){
					mark = ws_mark;
				}
			}else{
				mark = json.getString("marke");
			}
		}catch (Exception e) {
//			logger.info("订单"+orderId+" 申请大头笔失败！原因："+e.getMessage());
			String ws_mark = shippingZtoMarkDao.getMark(orderId);
			if (!WorkerUtil.isNullOrEmpty(ws_mark)){
				mark = ws_mark;
			}
		}
//		//System.out.println("orderId:"+orderId+" get mark :"+mark);
		orderProcessDao.updateShippingMark(orderId,mark);
		return mark;
	}

	@Override
	public void reportZTOTrackingNumber(Map shippingInfo) throws Exception {
		String appKey = String.valueOf(shippingInfo.get("app_key"));
		String appSecret = String.valueOf(shippingInfo.get("app_secret"));

		String orderId = String.valueOf(shippingInfo.get("order_id"));
		String shipmentId = String.valueOf(shippingInfo.get("shipment_id"));
		
		String sendCity = ""+shippingInfo.get("send_province")+","+shippingInfo.get("send_city")+","+shippingInfo.get("send_district");
		String sendAddress = String.valueOf(shippingInfo.get("address"));
		String sendPhone = String.valueOf(shippingInfo.get("contact_mobile"));
		
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

		String data = "{\"id\":\""+orderId+shipmentId+"\",\"mailno\":\""+tracking_number+"\",\"sender\":{\"name\":\"乐其\",\"phone\":\""+sendPhone+"\",\"city\":\""+sendCity+"\",\"address\":\""+sendAddress+"\"},\"receiver\": {\"name\":\""+receiveName+"\",\"mobile\":\""+receivePhone+"\",\"city\":\""+receiveCity+"\",\"address\":\""+receiveAddress+"\"}}";
//		//System.out.println(data);
		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = sdf.format(new Date());
		try{
			String content = DigestUtil.encryptBASE64(data);
			map.put("content", content);
			map.put("style", "json");
			map.put("func", "order.submit");
//			map.put("partner", "1000004013");
//			map.put("datetime", datetime);
//			map.put("verify",DigestUtil.digest("1000004013", datetime, content, "IQ1U46HKPU"));
//			String res = HttpUtil.post("http://partner.zto.cn/partner/interface.php", "utf-8", map); 
			map.put("partner", appKey);
			map.put("datetime", datetime);
			map.put("verify",DigestUtil.digest(appKey, datetime, content, appSecret));
			String res = HttpUtil.post(shippingUrlUtil.getZtoServerUrl(), "utf-8", map); 
			//{"result": "false","id": "17963312043756","code": "s07","keys": "mailno","remark": "数据内容不符合要求"}
			//{"result":"true","keys":{"id":"17963312043756","orderid":"17963312043756","mailno":"100000062461","mark":"沪西 09-06 89"}}
			JSONObject json= new JSONObject(res);  
			if(json.has("result") && "false".equalsIgnoreCase(json.getString("result"))){
				String keys = json.has("keys")?json.getString("keys"):"";
				String remark = json.getString("remark");
				shippingTrackingNumberRepositoryDao.updateRepositoryTrackingNumber(tracking_number, "E");
				logger.info("快递单号"+tracking_number+" 回传失败！返回错误："+keys+remark);
			}else if("true".equalsIgnoreCase(json.getString("result"))){
				shippingTrackingNumberRepositoryDao.updateRepositoryTrackingNumber(tracking_number, "F");
			}
		}catch (Exception e) {
			logger.info("快递单号"+tracking_number+" 回传失败！原因："+e.getMessage());
		}
	}


}