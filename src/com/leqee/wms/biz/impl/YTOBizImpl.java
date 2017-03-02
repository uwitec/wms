package com.leqee.wms.biz.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.YTOBiz;
import com.leqee.wms.dao.ShippingTrackingNumberRepositoryDao;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.DigestUtil;
import com.leqee.wms.util.HttpUtil;
import com.leqee.wms.util.ShippingUrlUtil;

@Service
public class YTOBizImpl implements YTOBiz {

	private Logger logger = Logger.getLogger(YTOBizImpl.class);
	@Autowired
	ShippingUrlUtil shippingUrlUtil;
	@Autowired
	ShippingTrackingNumberRepositoryDao shippingTrackingNumberRepositoryDao;
	@Autowired
	OrderBiz orderBiz;

	@Override
	public Map applyOneTrackingNumberByShipmentId(Integer orderId,
			Integer shipmentId, Map appInfo, Map receiveInfo) {
		//3. 寄件人信息
		Map sendInfo = orderBiz.selectSendInfoByOrderId(orderId);

		Map<String, Object> trackingNumberInfoMap = new HashMap<String,Object>();
		String trackingNumber = "";
		String mark = "";
		
		String receiveName = ""+receiveInfo.get("receive_name");
		receiveName = receiveName.replace("—", " ").replace("·", " ").replace("…", "").replace("”", ")").replace("“", "(").replace("–", "-").replace("°", "度").replace("<", "(").replace(">", ")").replace("&", "");
		String receiveAddress = ""+receiveInfo.get("receive_address");
		receiveAddress = receiveAddress.replace("—", " ").replace("·"," ").replace("…", "").replace("”", ")").replace("“", "(").replace("–", "-").replace("°", "度").replace("<", "(").replace(">", ")").replace("&", "");
		
		StringBuffer sb = new StringBuffer("<RequestOrder>");
		sb.append("<clientID>")
			.append(appInfo.get("app_secret"))
			.append("</clientID><logisticProviderID>YTO</logisticProviderID><customerId>")
			.append(appInfo.get("app_secret"))
			.append("</customerId><txLogisticID>")
			.append(String.valueOf(appInfo.get("app_secret"))+shipmentId)
			.append("</txLogisticID><tradeNo>")
			.append(orderId+""+shipmentId)
			.append("</tradeNo><totalServiceFee>0</totalServiceFee><codSplitFee>0</codSplitFee><orderType>1</orderType><serviceType>0</serviceType><flag>1</flag>");

		sb.append("<sender><name>").append(sendInfo.get("name"))
				.append("</name><postCode>").append("0")
				.append("</postCode><phone>")
				.append(sendInfo.get("aftersale_phone"))
				.append("</phone><mobile>0</mobile><prov>")
				.append(sendInfo.get("send_province")).append("</prov><city>")
				.append(sendInfo.get("send_city")).append("</city><address>")
				.append(sendInfo.get("send_district")).append("</address></sender>")
				.append("<receiver><name>")
				.append(receiveName)
				.append("</name><postCode>")
				.append("0")
				.append("</postCode><phone>")
				.append(receiveInfo.get("phone"))
				// 假的手机号，如需改正 请切换成 ： OrderInfo.getMobile_phone()
				.append("</phone><prov>")
				.append(receiveInfo.get("receive_province"))
				.append("</prov><city>")
				.append(receiveInfo.get("receive_city"))
				.append(',')
				.append(receiveInfo.get("receive_district"))
				.append("</city><address>")
				.append(StringEscapeUtils.escapeXml(receiveAddress))
				.append("</address></receiver>");

		String date0 = DateUtils.getDateString(0, "yyyy-MM-dd"," 08:00:00");
		String date1 = DateUtils.getDateString(0, "yyyy-MM-dd"," 24:00:00");

		sb.append("<sendStartTime>")
				.append(date0)
				.append("</sendStartTime><sendEndTime>")
				.append(date1)
				.append("</sendEndTime><goodsValue>0</goodsValue><itemsValue>0</itemsValue><items>");

		sb.append("<item><itemName>")
				.append(sendInfo.get("shipment_category"))
				.append("</itemName><number>1</number><itemValue>0</itemValue></item>");

		sb.append("</items><insuranceValue>0</insuranceValue><special>0</special><remark>0</remark></RequestOrder>");

		String logistics_interface = sb.toString();
//		logger.info(" yto_logistics_interface : "+logistics_interface);
		//<RequestOrder><clientID>K21000119</clientID><logisticProviderID>YTO</logisticProviderID><customerId>K21000119</customerId><txLogisticID>K21000119331</txLogisticID><tradeNo>331</tradeNo><totalServiceFee>0</totalServiceFee><codSplitFee>0</codSplitFee><orderType>1</orderType><serviceType>0</serviceType><flag>1</flag><sender><name>金佰利</name><postCode>0</postCode><phone>0571-28329302</phone><mobile>0</mobile><prov>浙江省</prov><city>嘉兴市</city><address>嘉善县</address></sender><receiver><name>圆通ytchen</name><postCode>0</postCode><phone>15209883578</phone><prov>安徽</prov><city>合肥,肥西县</city><address>高刘镇洪店村陈圩组</address></receiver><sendStartTime>2016-04-22 08:00:00</sendStartTime><sendEndTime>2016-04-22 24:00:00</sendEndTime><goodsValue>0</goodsValue><itemsValue>0</itemsValue><items><item><itemName>母婴用品</itemName><number>1</number><itemValue>0</itemValue></item></items><insuranceValue>0</insuranceValue><special>0</special><remark>0</remark></RequestOrder>
		String data_digest = "";
		try {
			data_digest = DigestUtil.encodeBase64(DigestUtil
					.toStringHex(DigestUtil
							.encryptMD5(logistics_interface
									+ appInfo.get("app_key"))));

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("logistics_interface", logistics_interface);
			data.put("data_digest", data_digest);
			data.put("clientId", appInfo.get("app_secret"));
			
			String res = HttpUtil
					.post(shippingUrlUtil.getYtoServerUrl()+"CommonOrderModeBServlet.action",
							"utf-8", data);
//			logger.info("yto_res:"+res);
			//<Response><logisticProviderID>YTO</logisticProviderID><success>true</success><orderMessage><clientID>K21000119</clientID><customerId>K21000119</customerId><txLogisticID>K21000119331</txLogisticID><tradeNo>331</tradeNo><mailNo>800542423936</mailNo><bigPen>肥西 450-047 003</bigPen><totalServiceFee>0</totalServiceFee><codSplitFee>0</codSplitFee><orderType>1</orderType><serviceType>0</serviceType><flag>1</flag><sender><name>金佰利</name><postCode>0</postCode><phone>0571-28329302</phone><mobile>0</mobile><prov>浙江省</prov><city>嘉兴市</city><address>嘉善县</address></sender><receiver><name>圆通ytchen</name><postCode>0</postCode><phone>15209883578</phone><prov>安徽</prov><city>合肥,肥西县</city><address>高刘镇洪店村陈圩组</address></receiver><items><item><itemName>母婴用品</itemName><number>1</number><itemValue>0</itemValue></item></items><insuranceValue>0</insuranceValue><special>0</special><remark>0</remark></orderMessage></Response>

			String success = res.substring(
					res.indexOf("<success>") + 9,
					res.indexOf("</success>"));

			if (success.equals("true")) {
				trackingNumber = res.substring(
						res.indexOf("<mailNo>") + 8,
						res.indexOf("</mailNo>"));
				mark = res.substring(res.indexOf("<bigPen>") + 8,
						res.indexOf("</bigPen>"));
				trackingNumberInfoMap.put("result", Response.SUCCESS);
				trackingNumberInfoMap.put("trackingNumber", trackingNumber);
				trackingNumberInfoMap.put("mark", mark);
			} else {
				String errorDesc = res.substring(
						res.indexOf("<reason>") + 8,
						res.indexOf("</reason>"));
				trackingNumberInfoMap.put("result", Response.FAILURE);
				trackingNumberInfoMap.put("note", "订单"+orderId+" 圆通申请面单报错："+errorDesc);
			}
		} catch (Exception e) {
			trackingNumberInfoMap.put("result", Response.FAILURE);
			trackingNumberInfoMap.put("note", "订单"+orderId+" 圆通申请面单号时申请失败!");
		}

		return trackingNumberInfoMap;
	}

}
