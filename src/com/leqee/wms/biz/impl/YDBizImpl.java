package com.leqee.wms.biz.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.leqee.wms.biz.YDBiz;
import com.leqee.wms.dao.OrderDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.ShippingYundaTrackingNumberDao;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.DigestUtil;
import com.leqee.wms.util.HttpUtil;
import com.leqee.wms.util.ShippingUrlUtil;

@Service
public class YDBizImpl implements YDBiz {

	private Logger logger = Logger.getLogger(YDBizImpl.class);
	@Autowired
	ShippingUrlUtil shippingUrlUtil;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderDao orderDao;
	@Autowired
	ShippingYundaTrackingNumberDao shippingYundaTrackingNumberDao;

	@Override
	public Map applyOneTrackingNumberByShipmentId(Integer orderId,Integer shipmentId,Map appInfo,Map receiveInfo,Integer warehouseId){
		Map<String, Object> trackingNumberInfoMap = new HashMap<String, Object>();
		//3. 寄件人信息
		Map sendInfo = orderDao.selectSendInfoByOrderId(orderId);
				
		String trackingNumber = "";
		String receiveName = ""+receiveInfo.get("receive_name");
		receiveName = receiveName.replace("—", " ").replace("·", " ").replace("…", "").replace("”", ")").replace("“", "(").replace("–", "-").replace("°", "度").replace("<", "(").replace(">", ")").replace("&", "");
		String receiveAddress = ""+receiveInfo.get("receive_address");
		receiveAddress = receiveAddress.replace("—", " ").replace("·"," ").replace("…", "").replace("”", ")").replace("“", "(").replace("–", "-").replace("°", "度").replace("<", "(").replace(">", ")").replace("&", "");
		
		
		String interfaceName = "interface_receive_order__mailno.php";
		StringBuffer sb = new StringBuffer();
		sb.append("<orders>").append("<order>").append("<order_serial_no>"+orderId+""+shipmentId+"</order_serial_no>")
			.append("<khddh></khddh><nbckh></nbckh><order_type>common</order_type>") 
			.append("<sender>") 
			.append("<name>"+sendInfo.get("name")+"</name>") 
			.append("<company></company>") 
			.append("<city>"+sendInfo.get("send_province")+","+sendInfo.get("send_city")+","+sendInfo.get("send_district")+"</city>") 
			.append("<address>"+sendInfo.get("send_province")+","+sendInfo.get("send_city")+","+sendInfo.get("send_district")+"</address>") 
			.append("<postcode></postcode>") 
		    .append("<phone></phone>") 
		    .append("<mobile>"+sendInfo.get("aftersale_phone")+"</mobile>") 
		    .append("<branch></branch>")
		    .append("</sender>")
		    .append("<receiver>")
		    .append("<name>"+StringEscapeUtils.escapeXml(receiveName)+"</name>")
		    .append("<company></company>")
		    .append("<city>"+StringEscapeUtils.escapeXml((receiveInfo.get("receive_province")+","+receiveInfo.get("receive_city")+","+receiveInfo.get("receive_district")))+"</city>")
		    .append("<address>"+StringEscapeUtils.escapeXml((String.valueOf(receiveInfo.get("receive_province")+","+receiveInfo.get("receive_city")+","+receiveInfo.get("receive_district")+receiveAddress)))+"</address>")
		    .append("<postcode></postcode>")
		    .append("<phone>"+receiveInfo.get("phone_number")+"</phone>")
		    .append("<mobile>"+receiveInfo.get("mobile_number")+"</mobile>")
		    .append("<branch></branch>")
		    .append("</receiver>")
		    .append("<weight></weight><size></size><value></value><collection_value></collection_value><special></special>")
		    .append("<items>")
		    .append("<item>") 
		    .append("<name>母婴用品</name>")  
		    .append("<number>1</number>")  
		    .append("<remark></remark>")  
		    .append("</item>")
		    .append("</items>") 
		    .append("<remark></remark><cus_area1></cus_area1><cus_area2></cus_area2><callback_id></callback_id><wave_no></wave_no>") 
		    .append("</order>") 
		    .append("</orders>");
		String partnerid = String.valueOf(appInfo.get("app_key"));  
		String validation = String.valueOf(appInfo.get("app_secret"));
		
		Map<String, Object> map_request = new HashMap<String, Object>();
		String data = sb.toString();
		String responseResult = "";
		String pdfInfo = "";
		String note = "";
		try{
			String content = DigestUtil.encryptBASE64(data);
			String validation2 = DigestUtil.encryptMD5(content+partnerid+validation).toLowerCase(); //韵达使用此数据为小写才可通过
			map_request.put("partnerid", partnerid);
			map_request.put("version",1.0);
			map_request.put("request", "data");
			map_request.put("xmldata", content);
			map_request.put("validation",validation2);
			
			String res = HttpUtil.post(shippingUrlUtil.getYdServerUrl()+interfaceName,"utf-8",map_request); 
//			//System.out.println("订单"+orderId+"韵达申请反馈信息："+res);
			/**
			 * <responses><response><order_serial_no>338</order_serial_no>
			 * <mail_no>4000052817406</mail_no>
			 * <pdf_info>[[{"order_id":"839922816","order_serial_no":"338","partner_id":"1001051024","partner_orderid":"sys_338","order_type":"common","mailno":"4000052817406","customer_id":"","sender_name":"\u91d1\u4f70\u5229","sender_company":"","sender_area_ids":"","sender_area_names":"\u6d59\u6c5f\u7701,\u5609\u5174\u5e02,\u5609\u5584\u53bf","sender_address":"\u6d59\u6c5f\u7701,\u5609\u5174\u5e02,\u5609\u5584\u53bf","sender_postcode":"","sender_phone":"","sender_mobile":"null","sender_branch":"100105","receiver_name":"\u97f5\u8fbeytchen","receiver_company":"","receiver_area_ids":"430623","receiver_area_names":"\u6e56\u5357,\u5cb3\u9633,\u534e\u5bb9\u53bf","receiver_address":"\u6e56\u5357,\u5cb3\u9633,\u534e\u5bb9\u53bf\u6e56\u5357\u7701\u5cb3\u9633\u5e02\u534e\u5bb9\u53bf\u65b0\u755c\u7267\u5c40\u65c1501\u5ba4\uff08\u6210\u5a1f\uff09\u6536","receiver_postcode":"","receiver_phone":"null","receiver_mobile":"null","receiver_branch":"0","weight":"","remark":"","status":"rs10","time":"2016-04-25 14:29:57","position_no":"430623-01-","position_zz":"0","options":"","send_num":"0","nb_ckh":"","cus_area1":"\u8ba2\u5355\u53f7:sys_338\n","cus_area2":"","position":"\u6e58-\u534e\u5bb9\u53bf","receiver_flag":"1","package_wd":"J410001","callback_id":"","wave_no":"","node_id":"","ems_flag":"","cus_area3":"","trade_code":"640-1","shi1":null,"sheng1":null,"shi2":"430600","sheng2":"430000","collection_value":"0.00","package_wdjc":"\u96c6\u5305\u5730\uff1a\u957f\u6c99zz","sender_branch_jc":"\u5317\u4eac\u4e1c\u71d5\u90caYD","bigpen_code":"640-1","lattice_mouth_no":"","mailno_barcode":"400005281740606217","qrcode":"A9jw8PPwUgR6U+pofvP0Q\/YhN05ANhubcBkIIkUenJQx\/f9nxTngwjL05+INWzgjOlW4JdcpfeldpCa9PjLl38r2d9QFjdca","tname":"mailtmp_s12"},["0621",7]]]</pdf_info>
			 * <status>1</status><msg>创建订单成功</msg></response></responses>
			 */
			/**
			 * <responses><response><order_serial_no>0</order_serial_no><mail_no></mail_no><pdf_info></pdf_info><status>0</status><msg>错误：签名不正确.</msg></response></responses>
			 */
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(res)));
			Element root = doc.getDocumentElement();
			NodeList rootBooks = root.getChildNodes();
			Node rootNode = rootBooks.item(0);
			Element parentElement = (Element)rootNode;
			NodeList books = parentElement.getChildNodes();
			
			if (books != null) {
				for (int i = 0; i < books.getLength(); i++) {
					Node book = books.item(i);
					if(book.getNodeName().equals("mail_no")){
						trackingNumber = book.getTextContent();
					}else if(book.getNodeName().equals("pdf_info")){
						pdfInfo = book.getTextContent();
					}else if (book.getNodeName().equals("status")) {
						responseResult = book.getTextContent();
//						logger.info("responseResult:"+responseResult);
					}else if(book.getNodeName().equals("msg")){
						note = book.getTextContent();
					}
				}
			}
		}catch (Exception e) {
			trackingNumberInfoMap.put("result", Response.FAILURE);
			trackingNumberInfoMap.put("note", "订单"+orderId+""+sendInfo.get("shipping_name")+"申请面单号时申请失败!");
		}
		if(responseResult.equals("0")){
			trackingNumberInfoMap.put("result", Response.FAILURE);
			trackingNumberInfoMap.put("note", orderId+""+sendInfo.get("shipping_name")+"申请面单报错："+note);
		}else{
			String packagePosition = "";
			String packageNo = "";
			String station = "";
			String stationNo = "";
			String senderBranchNo = "";
			String senderBranch = "";
			String latticeMouthNo = "";
			// 将json字符串转换成jsonObject  
//			//System.out.println("订单"+orderId+"申请到韵达 pdfInfo:"+pdfInfo);
			JSONArray js = JSONArray.fromObject(pdfInfo);
			JSONArray jj = JSONArray.fromObject(js.get(0));
			JSONObject jsonObject = JSONObject.fromObject(jj.get(0));  
			Iterator it = jsonObject.keys();  
			// 遍历jsonObject数据，添加到Map对象  
			while (it.hasNext()){  
				String key = String.valueOf(it.next());  
				String value = String.valueOf(jsonObject.get(key));  
				if("package_wdjc".equalsIgnoreCase(key)){
					packagePosition = value;
				}else if("package_wd".equalsIgnoreCase(key)){
					packageNo = value;
				}else if("position".equalsIgnoreCase(key)){
					station = value;
				}else if("bigpen_code".equalsIgnoreCase(key)){
					stationNo = value;
				}else if("sender_branch".equalsIgnoreCase(key)){
					senderBranchNo = value;
				}else if("sender_branch_jc".equalsIgnoreCase(key)){
					senderBranch = value;
				}else if("lattice_mouth_no".equalsIgnoreCase(key)){
					latticeMouthNo = value;
				}
			}
			try{
				shippingYundaTrackingNumberDao.insertTrackingInfoForOther(
						shipmentId,trackingNumber,packagePosition,packageNo,station,stationNo,senderBranchNo,senderBranch,latticeMouthNo);
				trackingNumberInfoMap.put("result", Response.SUCCESS);
				trackingNumberInfoMap.put("trackingNumber", trackingNumber);
			}catch (Exception e) {
				logger.info("order_id:"+orderId+" apply yunda mailnoInfo:"+shipmentId+";"+trackingNumber+";"+packagePosition+";"+packageNo+";"+station+";"+stationNo+";"+senderBranchNo+";"+senderBranch+";"+latticeMouthNo+"\n"+e.getMessage());
				trackingNumberInfoMap.put("result", Response.FAILURE);
				trackingNumberInfoMap.put("note", "订单"+orderId+""+sendInfo.get("shipping_name")+"成功，但插入数据错误！快递单号："+trackingNumber);
			}
		}
		return trackingNumberInfoMap;
	}

}