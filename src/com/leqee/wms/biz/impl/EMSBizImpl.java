package com.leqee.wms.biz.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.EMSBiz;
import com.leqee.wms.dao.ShippingTrackingNumberRepositoryDao;
import com.leqee.wms.util.HttpUtil;
import com.leqee.wms.util.ShippingUrlUtil;
import com.leqee.wms.util.WorkerUtil;
@Service
public class EMSBizImpl implements EMSBiz {

	private Logger logger = Logger.getLogger(EMSBizImpl.class);
	@Autowired
	ShippingUrlUtil shippingUrlUtil;
	@Autowired
	ShippingTrackingNumberRepositoryDao shippingTrackingNumberRepositoryDao;
	
	@Override
	public void batchApplyEMSTrackingNumber(Map shippingApp) throws Exception {
		
		
		String bizData = "<?xml version='1.0' encoding='UTF-8'?><XMLInfo>";
		String sysAccount = String.valueOf(shippingApp.get("app_key"));
		String appKey = "TD18a0F12f74874fc";
		String passWord = String.valueOf(shippingApp.get("app_secret"));
		String businessType = "9";
		String billNoAmount = String.valueOf(shippingApp.get("apply_amount"));
		List<String> billCodesList = new ArrayList<String>();  
		
		bizData += "<sysAccount>"+sysAccount+"</sysAccount>";
		bizData += "<passWord>"+passWord+"</passWord>";
		bizData += "<appKey>"+appKey+"</appKey>";
		bizData += "<businessType>"+businessType+"</businessType>";
		bizData += "<billNoAmount>"+billNoAmount+"</billNoAmount></XMLInfo>";
		
		String res = HttpUtil.get(shippingUrlUtil.getEmsServerUrl()+"?method=getBillNumBySys&xml="+URLEncoder.encode(new sun.misc.BASE64Encoder().encode(bizData.getBytes()),"UTF-8")); 
		Document doc = DocumentHelper.parseText(res);
		Element rootElt = doc.getRootElement(); // 获取根节点
       
		String result = rootElt.elementTextTrim("result");
		String errorCode = rootElt.elementTextTrim("errorCode");
		if("1".equals(result) && "E000".equals(errorCode)){
			List<Element> listElement=rootElt.element("assignIds").elements();
		    for(Element e:listElement){
		       if("assignId".equals(e.getName())){
		    	   billCodesList.add(e.elementTextTrim("billno"));
		       }
		    }  
		    
		    String localUser = (String)SecurityUtils.getSubject().getPrincipal();
			if(WorkerUtil.isNullOrEmpty(localUser)){
				localUser="system";
			}
		    for (String mailno : billCodesList) {
				shippingTrackingNumberRepositoryDao.insertTrackingNumer(Integer.parseInt(String.valueOf(shippingApp.get("app_id"))),mailno,"N",localUser,WorkerUtil.getNow());
			}
		}
		
	
	}

	@Override
	public void reportEMSTrackingNumber(Map shippingInfo) throws Exception {
		
		String bizData = "<?xml version='1.0' encoding='UTF-8'?><XMLInfo>";
		String sysAccount = String.valueOf(shippingInfo.get("app_key"));
		String appKey = "TD18a0F12f74874fc";
		String passWord = String.valueOf(shippingInfo.get("app_secret"));
		String printKind = "2";
		String businessType = "9";
		
		String receiveNumber = "";
		if(WorkerUtil.isNullOrEmpty(shippingInfo.get("phone_number")) || "".equals(shippingInfo.get("phone_number"))){
			if(WorkerUtil.isNullOrEmpty(shippingInfo.get("mobile_number")) || "".equals(shippingInfo.get("mobile_number"))){
				receiveNumber = ""+shippingInfo.get("contact_mobile");
			}else{
				receiveNumber = ""+shippingInfo.get("mobile_number");
			}
		}else{
			receiveNumber = ""+shippingInfo.get("phone_number");
		}
		
		bizData += "<sysAccount>"+sysAccount+"</sysAccount>";
		bizData += "<passWord>"+passWord+"</passWord>";
		bizData += "<printKind>"+printKind+"</printKind>";
		bizData += "<appKey>"+appKey+"</appKey><printDatas><printData>";
		bizData += "<bigAccountDataId>"+shippingInfo.get("order_id")+"</bigAccountDataId>";
		bizData += "<businessType>"+businessType+"</businessType>";
		bizData += "<billno>"+shippingInfo.get("tracking_number")+"</billno>";
		
		bizData += "<scontactor>乐其网络</scontactor>";
		bizData += "<scustMobile>"+shippingInfo.get("contact_mobile")+"</scustMobile>";
		bizData += "<scustAddr>"+shippingInfo.get("send_district")+"</scustAddr>";
		bizData += "<tcontactor>"+shippingInfo.get("receive_name")+"</tcontactor>";
		bizData += "<tcustMobile>"+receiveNumber+"</tcustMobile>";
		bizData += "<tcustAddr>"+shippingInfo.get("district_name")+"</tcustAddr></printData></printDatas></XMLInfo>";
		
		String res = HttpUtil.get(shippingUrlUtil.getEmsServerUrl()+"?method=updatePrintDatas&xml="+URLEncoder.encode(new sun.misc.BASE64Encoder().encode(bizData.getBytes()),"UTF-8")); 
		
		Document doc = DocumentHelper.parseText(res);
		Element rootElt = doc.getRootElement(); 
       
		String result = rootElt.elementTextTrim("result");
		String errorCode = rootElt.elementTextTrim("errorCode");
		if("1".equals(result) && "E000".equals(errorCode)){
			shippingTrackingNumberRepositoryDao.updateRepositoryTrackingNumber(String.valueOf(shippingInfo.get("tracking_number")), "F");
		}else{
			List<Element> listElement=rootElt.element("errorDetail").elements();
			String errorDesc = "";
			for(Element e:listElement){
				 if("dataError".equals(e.getName())){
					 errorDesc = e.getData().toString();
			     }
		    }  
			shippingTrackingNumberRepositoryDao.updateRepositoryTrackingNumber(String.valueOf(shippingInfo.get("tracking_number")), "E");
			logger.info("回传EMS面单错误：(错误码)"+errorCode+";(描述)"+errorDesc);
		}
	}

}
