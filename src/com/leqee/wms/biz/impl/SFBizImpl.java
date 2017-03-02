package com.leqee.wms.biz.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.SFBiz;
import com.leqee.wms.dao.OrderDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.ShippingTrackingNumberRepositoryDao;
import com.leqee.wms.dao.ShippingWarehouseMappingDao;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.DigestUtil;
import com.leqee.wms.util.HttpUtil;
import com.leqee.wms.util.ShippingUrlUtil;

@Service
public class SFBizImpl implements SFBiz {

//	private static final String SF_SERVER_URL = "http://218.17.248.244:11080/bsp-oisp/"; // TEST_URL
	
	private Logger logger = Logger.getLogger(SFBizImpl.class);
	
	@Autowired
	ShippingUrlUtil shippingUrlUtil;
	@Autowired
	ShippingTrackingNumberRepositoryDao shippingTrackingNumberRepositoryDao;
	@Autowired
	OrderDao orderDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderBiz orderBiz;
	@Autowired
	ShippingWarehouseMappingDao shippingWarehouseMappingDao;

	@Override
	public Map applyOneTrackingNumberByShipmentId(Integer orderId,
			Integer shipmentId, Map appInfo, Map receiveInfo) {
		//3. 寄件人信息
		Map sendInfo = orderDao.selectSendInfoByOrderId(orderId);

		Map<String, Object> resMap = new HashMap<String,Object>();
		String trackingNumber = "";

		StringBuffer sb = new StringBuffer("");


		sb.append("<?xml version='1.0' encoding='UTF-8'?>")
				.append("<Request service=\"")
				.append("OrderService")
				.append("\" lang=\"zh-CN\">")
				.append("<Head>")
				.append(appInfo.get("app_key"))
				//.append("BSPdevelop")
				.append("</Head><Body>")
				.append("<Order orderid=")
				.append("\"")
				.append(orderId+""+shipmentId)
				.append("\" ")
				.append("j_company=\"LEQEE\" ")
				.append("j_contact=")
				.append("\"LEQEE \" ")
				.append("j_tel=\"")
				.append(sendInfo.get("aftersale_phone"))
				.append("\" j_mobile=\"")
				.append(sendInfo.get("aftersale_phone"))
				.append("\" ")
				.append("j_province=\"")
				.append(sendInfo.get("send_province"))
				.append("\" ")
				.append("j_city=\"")
				.append(sendInfo.get("send_city"))
				.append("\" ")
				.append("j_county=\"")
				.append(sendInfo.get("send_district"))
				.append("\" ")
				.append("j_address=\"")
				.append(sendInfo.get("send_province").toString()+sendInfo.get("send_city")+sendInfo.get("send_district"))
				.append("\" ")
				.append("d_company=\"顺丰速运\" ")
				.append("d_contact=\"")
				.append(StringEscapeUtils.escapeXml(receiveInfo.get("receive_name").toString()))
				.append("\" ")
				.append("d_tel=\"")
				.append(receiveInfo.get("receive_mobile"))
				.append("\" ")
				.append("d_mobile=\"")
				.append(receiveInfo.get("receive_phone"))
				.append("\" ")
				.append("d_province=\"")
				.append(receiveInfo.get("receive_province"))
				.append("\" ")
				.append("d_city=\"")
				.append(receiveInfo.get("receive_city"))
				.append("\" ")
				.append("d_county=\"")
				.append(receiveInfo.get("receive_district"))
				.append("\" ")
				.append("d_address=\"")
				.append(StringEscapeUtils.escapeXml(receiveInfo.get("receive_address").toString())).append("\" ")
				.append("express_type=\"").append("2").append("\" ")
				.append("pay_method=\"").append(""+appInfo.get("sf_payment_method")).append("\" ")
				// 付款方式: 1:寄方付 2:收方付 3:第三方付
				.append("custid=\"").append(""+appInfo.get("sf_monthly_balance_account")).append("\" ")
				// 付款账户
				.append("parcel_quantity=\"").append("1").append("\" ")
				// 包裹数
				.append("remark=\"\">").append("<Cargo name=\"")
				.append(sendInfo.get("shipment_category")).append("\"></Cargo>");

		// 保价  to do 
		BigDecimal insure = new BigDecimal(0); // orderBiz.selectSFInsurance(orderId,Integer.parseInt(String.valueOf(receiveInfo.get("customer_id"))));
		
		if(insure.compareTo(new BigDecimal(0))>0)
		{
			sb.append("<AddedService name=\"INSURE\" value=\"").append(insure.toString()).append("\" ").append("/>");
		}

		sb.append("</Order>").append("</Body></Request>");
		String requestXml = sb.toString();
		String checkSum = "";
		try {
			// 加密： requestxml + 密码
			checkSum = DigestUtil.encodeBase64(DigestUtil
					.toStringHex(DigestUtil.encryptMD5(sb.toString()//+"j8DzkIFgmlomPt0aLuwU")));
							+ appInfo.get("app_secret"))));

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("xml", requestXml);
			data.put("verifyCode", checkSum);
			String	res = HttpUtil.post(shippingUrlUtil.getSfServerUrl()+"sfexpressService","utf-8", data);
//			//System.out.println("res:"+res);
			//<?xml version='1.0' encoding='UTF-8'?><Response service="OrderService"><Head>ERR</Head><ERROR code="8016">重复下单</ERROR></Response>
			//<?xml version='1.0' encoding='UTF-8'?><Response service="OrderService"><Head>OK</Head><Body><OrderResponse filter_result="2" destcode="730" mailno="444503555645" origincode="573" orderid="337"/></Body></Response>

			String success = res.substring(
					res.indexOf("<Head>") + 6,
					res.indexOf("</Head>"));
			
			if (success.equals("OK")) {
				trackingNumber = res.substring(
						res.indexOf("mailno") + 8,
						res.indexOf("origincode")-2);

				String origincode=res.substring(
						res.indexOf("origincode=") + 12,
						res.indexOf("orderid")-2);
				
				String destcode=res.substring(
						res.indexOf("destcode=") + 10,
						res.indexOf("mailno")-2);
				resMap.put("result", Response.SUCCESS);
				resMap.put("trackingNumber",trackingNumber);
				resMap.put("origincode",origincode);
				resMap.put("destcode",destcode);
				
			} else {
				String errorDesc = res.substring(
						res.indexOf(">", res.indexOf("ERROR code")) + 1,
						res.indexOf("</ERROR>"));
				resMap.put("result", Response.FAILURE);
				resMap.put("note", "订单"+orderId+" 顺丰面单申请报错："+errorDesc);
			}
		} catch (Exception e) {
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "订单"+orderId+" 顺丰申请面单号时申请失败!");
		}
		return resMap;
	}
 
	/*  to do 顺丰保价费用计算
	@Override
	public BigDecimal getShipmentOrderAmount(Integer orderId) {
		BigDecimal insurance = orderInfoDao.getShipmentOrderAmount(orderId);
		return insurance;
	}

	@Override
	public BigDecimal getLeqeeElecEduInsurance(Integer orderId) {
		Integer insurance = 0;
		List<String> barcodes = orderInfoDao.getLeqeeElecEduInsurance(orderId);
		Map<String,Integer> priceMapping = new HashMap<String,Integer>();
		priceMapping.put("lq0543153", 1800);//	lq0543153	平板电脑H7[纯色]
		priceMapping.put("lq2497708", 1500);//	lq2497708	点读机T2[浅绿色]
		priceMapping.put("lq4155985", 1200);//	lq4155985	平板电脑H8[白色]
		priceMapping.put("444525755", 2700);//	444525755	平板电脑H9[绿色皮套]
		priceMapping.put("444312842", 1500);//	444312842	点读机T2[粉色]
		priceMapping.put("6935632820167", 600);//	6935632820167 	点读机T500S[果绿]
		priceMapping.put("444384561", 2100);//	444384561 	家教机H8S
		priceMapping.put("6935632850201", 3100);//	6935632850201 	家教机S1[香槟金]
		priceMapping.put("6935632850249", 3600);//	6935632850249 	家教机S2
		priceMapping.put("6935632840127", 600);//	6935632840127 	学习机H2
		priceMapping.put("lq5855239", 400);//	lq5855239	学习机S1
		priceMapping.put("lq6441893", 800);//	lq6441893	点读机T800[蓝色]
		priceMapping.put("lq2183098", 1000);//	lq2183098	外语通E50[绿色]
		
		if(barcodes.size()>0){
			for (int i = 0; i < barcodes.size(); i++) {
				if(priceMapping.containsKey(barcodes.get(i))){
					insurance = insurance + priceMapping.get(barcodes.get(i));
				}
			}
		}
		return new BigDecimal(insurance);
	}
	*/

}
