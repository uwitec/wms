<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>

<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=request.getContextPath() %>/static/css/arata.css">
<style>
body,html {
width:550px;
height:990px;
}
.wrap {
	position: relative;
	width: 100%;
	height: 100%;
	border: 0px solid black;
}
.inside {
width:550px;
height:990px;
}
</style>
</head>
<body onload="window.print()">
<c:forEach items="${print_info.order_info_list}" var="orderInfo" varStatus="i">
<c:if test="${orderInfo.shipping_code =='ZTO'}">
	<div class="wrap">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:550px;height:990px;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:990px;width:550px;">
	<div class="left-top" style="position:absolute;font-size:20pt;top:2%;left:0%;height:5%;width:50%;text-align:center;border-right:0px;border-bottom:0px;">
		ZTO中通快递
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:2%;left:50%;height:5%;width:50%;text-align:center;border-bottom:0px;">
		${orderInfo.send_province}${orderInfo.send_city}
	</div>
	
	<div class="left" style="position:absolute;font-size:20pt;top:7%;left:0%;height:5%;width:75%;border-right:0px;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:10%;height:90%;width:40%;">
			${orderInfo.tracking_number}
		</div>
		<div class="inside" style="position:absolute;font-size:5pt;top:50%;left:50%;height:30%;width:40%;">
			日期：<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;top:12%;left:0%;height:5%;width:75%;text-align:left;border-right:0px;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:5pt;top:5%;left:3%;height:90%;width:95%;">
			寄件人：${orderInfo.send_name}   联系方式：${orderInfo.aftersale_phone}
			<br>
			地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="right" style="position:absolute;font-size:20pt;top:7%;left:75%;height:10%;width:25%;text-align:center;border-bottom:0px;">
		已验视
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:17%;left:0%;height:10%;width:100%;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:15pt;top:10%;left:6%;height:90%;width:90%;">
			<b>收件人：${orderInfo.receive_name}       电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone} </b>　
			<br>
			<b>地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}</b>
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:30pt;top:32%;left:0%;height:8%;line-height:60px;width:100%;text-align:center;vertical-align:middle;letter-spacing:5px;filter:dropshadow(color=black,offx=1,offy=0) dropshadow(color=black,offx=2,offy=0) dropshadow(color=black,offx=3,offy=0)">
		${orderInfo.mark}
		
	</div>		
	<div style="position:absolute;top:40%;left:0%;height:5%;width:100%;text-align:center;border:0px;">
		<div class="inside" style="position:absolute;top:2%;left:0%;height:100%;width:90%;">
			<p style="font-size:13pt;">
			<c:if test="${fn:length(print_info.order_goods_list)<=4}">
				商品:
				<c:forEach var="infoList" items="${print_info.order_goods_list}">
					<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
				</c:forEach>
			</c:if>
			</p>		
		</div>
	</div>
	<div style="position:absolute;top:45%;left:0%;height:9%;width:100%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0" style="position:absolute;left:4%;top:5%;width:92%;height:70%;margin-top:2px;" />
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:15px;">
			*${orderInfo.tracking_number}*
		</div>
	</div>
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:55%;left:0%;height:5%;width:70%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:5%;height:90%;width:95%;">
		已验视<br>
		您对此单的签收，代表您已验货，确认商品商品信息无误，包装完好
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:55%;left:70%;height:5%;width:30%;text-align:left;">
		<div class="inside" style="position:absolute;font-size:15pt;top:10%;left:5%;height:90%;width:80%;">
			签收
		</div>
	</div>
	
	<div style="position:absolute;top:65%;left:3%;height:5%;width:100%;text-align:left;border:0px;font-size: 20px">
		ZTO中通快递
	</div>
	<div style="position:absolute;top:70%;left:0%;height:10%;width:100%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?&barcode=${orderInfo.tracking_number}&text=0"  style="position:absolute;top:5%;left:5%;width:90%;height:70%;" />
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:15px;">
			*${orderInfo.tracking_number}*
		</div>
	</div>
	
	<div class="left" style="position:absolute;font-size:20pt;top:80%;left:0%;height:4%;width:50%;text-align:center;border-right:0px;border-bottom:0px;">
		${orderInfo.shipment_category}
	</div>
	<div class="right" style="position:absolute;font-size:15pt;top:80%;left:50%;height:4%;width:50%;text-align:center;border-bottom:0px;">
		${tracking_number}
	</div>
	
	<div class="left right" style="position:absolute;font-size:10pt;top:84%;left:0%;height:5%;width:100%;text-align:left;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:5pt;top:5%;left:6%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}       联系方式：${orderInfo.aftersale_phone}
			<br>
			地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:20pt;top:89%;left:0%;height:12%;width:100%;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:6%;height:70%;width:90%;">
			<b>收件人：${orderInfo.receive_name}       电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone} </b>　
			<br>
			<b>地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}</b>
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div>
</c:if>
<c:if test="${orderInfo.shipping_code =='YTO'}">
	<div class="wrap">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;">
	<div class="left-top" style="position:absolute;font-size:20pt;top:2%;left:0%;height:5%;width:50%;text-align:center;border-right:0px;border-bottom:0px;">
		圆通快递
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:2%;left:50%;height:5%;width:50%;text-align:center;border-bottom:0px;">
		${orderInfo.send_province}${orderInfo.send_city}
	</div>
	
	<div class="left" style="position:absolute;font-size:20pt;top:7%;left:0%;height:5%;width:75%;border-right:0px;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:10%;height:90%;width:40%;">
			${orderInfo.tracking_number}
		</div>
		<div class="inside" style="position:absolute;font-size:5pt;top:50%;left:50%;height:30%;width:40%;">
			日期：<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;top:12%;left:0%;height:5%;width:75%;text-align:left;border-right:0px;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:5pt;top:5%;left:3%;height:90%;width:95%;">
			寄件人：${orderInfo.send_name}   联系方式：${orderInfo.aftersale_phone}
			<br>
			地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="right" style="position:absolute;font-size:20pt;top:7%;left:75%;height:10%;width:25%;text-align:center;border-bottom:0px;">
		<!-- <img src="assets/img/Shop2DBarcodes/1953.png" style="margin-top:2px;" width="70px" height="70px"> -->
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:17%;left:0%;height:10%;width:100%;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:15pt;top:10%;left:6%;height:90%;width:90%;">
			<b>收件人：${orderInfo.receive_name}       电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}</b>　
			<br>
			<b> <span  style="font-size:18pt;">地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}</span></b>
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:30pt;top:32%;left:0%;height:8%;line-height:60px;width:100%;text-align:center;vertical-align:middle;letter-spacing:5px;filter:dropshadow(color=black,offx=1,offy=0) dropshadow(color=black,offx=2,offy=0) dropshadow(color=black,offx=3,offy=0)">
		  ${orderInfo.mark}
	</div>		
	<div style="position:absolute;top:40%;left:0%;height:9%;width:100%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0" style="position:absolute;left:4%;top:5%;width:92%;height:70%;margin-top:2px;" />
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:15px;">
			*${orderInfo.tracking_number}*
		</div>
	</div>
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:50%;left:0%;height:10%;width:70%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:5%;height:35%;width:95%;">
		圆通速递将快件送达收件人地址，经收件人或收件人（寄件人）允许的代收人签字，视为送达。
			<p style="font-size:10pt;text-align:left;margin-top:5pt;">
				<c:if test="${fn:length(print_info.order_goods_list)<=4}">
					商品:
					<c:forEach var="infoList" items="${print_info.order_goods_list}">
						<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
					</c:forEach>
				</c:if>
			</p>
		</div>
		<div class="inside" style="position:absolute;top:40%;left:5%;height:60%;width:95%;" >
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:50%;left:70%;height:10%;width:30%;text-align:left;">
		<div class="inside" style="position:absolute;font-size:15pt;top:10%;left:5%;height:90%;width:80%;">
			签收
		</div>
	</div>
	
	<!-- divide -->
	<div style="position:absolute;top:65%;left:3%;height:5%;width:100%;text-align:left;border:0px;font-size: 20px">
			圆通快递
	</div>
	<div style="position:absolute;top:70%;left:0%;height:10%;width:100%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0" style="position:absolute;top:5%;left:5%;width:90%;height:70%;" />
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:15px;">
			*${orderInfo.tracking_number}*
		</div>
	</div>
	
	<div class="left" style="position:absolute;font-size:20pt;top:80%;left:0%;height:4%;width:50%;text-align:center;border-right:0px;border-bottom:0px;">
		${orderInfo.shipment_category}
	</div>
	<div class="right" style="position:absolute;font-size:15pt;top:80%;left:50%;height:4%;width:50%;text-align:center;border-bottom:0px;">
		${orderInfo.tracking_number}
	</div>
	
	<div class="left right" style="position:absolute;font-size:10pt;top:84%;left:0%;height:5%;width:100%;text-align:left;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:5pt;top:5%;left:6%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}       联系方式：${orderInfo.aftersale_phone}
			<br>
			地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:20pt;top:89%;left:0%;height:12%;width:100%;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:6%;height:90%;width:90%;">
			收件人：${orderInfo.receive_name}    电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
			<br>
			此运单仅供圆通速递签约客户使用，相关责任义务以双方合作合同为准
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>
<c:if test="${orderInfo.shipping_code =='YD'}">
	<div class="wrap" style="border:1px solid #FFF; ">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
		<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:810px;width:450px;">
		
		<div class="inside" style="position:absolute;font-size:10pt;top:5pt;left:5pt;height:2%;width:60%;text-align:left">
			始发网点：${orderInfo.sender_branch}
		</div>
		<div class="inside" style="position:absolute;font-size:10pt;top:5pt;left:60%;height:2%;width:30%;text-align:center">
			<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
		<div class="inside" style="position:absolute;font-size:15pt;top:4%;left:0%;height:6%;width:20%;text-align:center;">
			送达地址
		</div>
		<div class="inside" style="position:absolute;font-size:10pt;top:4%;left:26%;height:13%;width:63%;text-align:left;font-size: 20px;font-weight: bold;">
				收件人：${orderInfo.receive_name}   <br>
				收件人电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}<br> 
				${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
		
		<div class="inside" style="position:absolute;font-size:15pt;top:18%;left:00%;height:3%;width:45%;text-align:left;border-left:0px;">
			${orderInfo.package_position}
		</div>
		<div class="inside" style="position:absolute;font-size:15pt;top:20%;left:63%;height:3%;width:35%;text-align:left;border-left:0px;">
			<img src="../common/barcode/generate?barcode=${orderInfo.package_no}&text=0&type=code128" 
			 style="display:block;position:absolute;top:5%;left:0%;width:90%;height:70%;" />
			<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:10%;height:15%;width:90%;letter-spacing:6px;">
				*${orderInfo.package_no}*
			</div>
		</div>
		<div class="left" style="position:absolute;font-size:20pt;top:25%;left:0%;height:20%;width:30%;text-align:center;border-bottom:0px;">
		<!-- 	<img src="assets/img/Shop2DBarcodes/1953.png" style="margin-top:2px;" width="70px" height="70px"> -->
		</div>
		<div class="left" style="position:absolute;font-size:10pt;top:25%;left:30%;height:20%;width:65%;text-align:left;border-right:0px;border-bottom:0px;">
			<div class="inside" style="position:absolute;font-size:32pt;top:0%;left:3%;height:100%;width:100%;font-weight: bold;">
				${orderInfo.station}<br>
				${orderInfo.station_no}　${orderInfo.lattice_mouth_no} 
			</div>
		</div>
		
		<div class="left right" style="position:absolute;font-size:10px;top:2%;left:90%;height:60%;line-height:60px;width:15%;text-align: left;border-top:0px;border-bottom:0px;letter-spacing:3px;">
			<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128"
			style="display:block;position:absolute;-webkit-transform:rotate(-90deg);left:-100px;top:230px;width:400px;height:75px;" />
			<div style="border: 0px;letter-spacing: 12px;position: absolute;-webkit-transform:rotate(-90deg);left:-80px;top: 150px;">${orderInfo.tracking_number}</div>
		</div>
		
		<div style="position:absolute;top:45%;left:0%;height:3%;width:95%;text-align:left;border-left:0px;border-right:0px;">
			<div class="inside" style="position:absolute;font-size:16pt;left:5%;height:100%;width:100%;border-bottom:3px;">
				运单编号：${orderInfo.tracking_number}
			</div>
		</div>
		<div class="left right" style="position:absolute;top:49%;left:0%;height:5%;width:95%;text-align:left;border-top:0px;">
			<div class="inside" style="position:absolute;font-size:10pt;left:5%;height:100%;width:90%;">
				收件人/代签人：  						
				<br>
				签收日期：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日
			</div>
		</div>
		<div class="inside" style="position:absolute;font-size:10pt;top:44%;left:0%;height:22%;width:100%;text-align:left;border-right:0px;">
			<div style="position:absolute;font-size:10pt;top:90%;left:80%;height:15%;width:15%;">
				已 验 视
			</div>
		</div>
		
		<div style="position:absolute;top:76%;left:5%;height:5%;width:50%;text-align:center;border:0px;">
			<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128"
			 style="position:absolute;top:5%;left:0%;width:100%;height:70%;" />
			<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:2px;">
				*${orderInfo.tracking_number}*
			</div>
		</div>
		<div style="position:absolute;top:76%;left:70%;height:6%;width:30%;text-align:left;border:0px;font-size: 25px;vertical-align: middle;font-weight: bold;">
				<div style="letter-spacing: 9px;border: 0px;">YUNDA</div>
				韵达速递
		</div>
		
		<div class="left" style="position:absolute;font-size:10pt;top:84%;left:0%;height:18%;width:50%;text-align:center;border-right:0px;border-bottom:0px;">
			寄件人：${orderInfo.send_name}<br>寄件人电话：${orderInfo.aftersale_phone}<br>
			寄件人地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
			<br>
			<br>
			收件人：${orderInfo.receive_name}   <br>
			收件人电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}<br> 
			${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
		<div class="right" style="position:absolute;top:84%;left:50%;height:16%;width:50%;text-align:center;border-bottom:0px;">
			<div class="inside" style="position:absolute;font-size:20pt;top:2%;left:0%;height:20%;width:90%;">
			${orderInfo.shipment_category}<br/><br/>A
			</div>
		</div>
	
		<div class="left right" style="position:absolute;font-size:10pt;top:100%;left:0%;height:5%;width:100%;text-align:left;border-bottom:0px;">
			<div class="inside" style="position:absolute;font-size:5pt;top:8%;left:6%;height:90%;width:90%;">
				官网地址:http://www.yundaex.com <br/>客户热线 :95546  收件人联
			</div>
			<%-- <div class="inside" style="position:absolute;font-size:5pt;top:5%;left:60%;height:90%;width:40%;">
				第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张<br/><br/>
 第${orderInfo.pm}个包裹，本订单共${packNum}个
			</div> --%>
			<p style="font-size:10pt;text-align:left; margin-top: 35pt;margin-left: 20pt;">
				<c:if test="${fn:length(print_info.order_goods_list)<=4}">
					商品:
					<c:forEach var="infoList" items="${print_info.order_goods_list}">
						<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
					</c:forEach>
				</c:if>
			</p>				
		</div>
		<div class="left" style="position:absolute;font-size:10pt;bottom:-180px;left:0%;height:4%;width:50%;text-align:center;border:0px;">
			第${orderInfo.pm}个包裹，本订单共${packNum}个
		</div>
		<div class="right" style="position:absolute;font-size:10pt;bottom:-180px;left:50%;height:4%;width:50%;text-align:center;border:0px;">
			 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
		</div>
		</div>
	
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>

<c:if test="${orderInfo.shipping_code =='YD_BQ'}">
	<div class="wrap" style="border:1px solid #FFF; ">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:810px;width:450px;">
		
		<div class="inside" style="position:absolute;font-size:10pt;top:5pt;left:5pt;height:2%;width:60%;text-align:left">
			始发网点：${orderInfo.sender_branch}
		</div>
		<div class="inside" style="position:absolute;font-size:10pt;top:5pt;left:60%;height:2%;width:30%;text-align:center">
			<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
		<div class="inside" style="position:absolute;font-size:15pt;top:4%;left:0%;height:6%;width:20%;text-align:center;">
			送达地址
		</div>
		<div class="inside" style="position:absolute;font-size:10pt;top:4%;left:26%;height:13%;width:63%;text-align:left;font-size: 20px;font-weight: bold;">
				收件人：${orderInfo.receive_name}   <br>
				收件人电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}<br> 
				${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
		
		<div class="inside" style="position:absolute;font-size:15pt;top:18%;left:00%;height:3%;width:45%;text-align:left;border-left:0px;">
			${orderInfo.package_position}
		</div>
		<div class="inside" style="position:absolute;font-size:15pt;top:20%;left:63%;height:3%;width:35%;text-align:left;border-left:0px;">
			<img src="../common/barcode/generate?barcode=${orderInfo.package_no}&text=0&type=code128" 
			 style="display:block;position:absolute;top:5%;left:0%;width:90%;height:70%;" />
			<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:10%;height:15%;width:90%;letter-spacing:6px;">
				*${orderInfo.package_no}*
			</div>
		</div>
		<div class="left" style="position:absolute;font-size:20pt;top:25%;left:0%;height:20%;width:30%;text-align:center;border-bottom:0px;">
			
		</div>
		<div class="left" style="position:absolute;font-size:10pt;top:25%;left:30%;height:20%;width:65%;text-align:left;border-right:0px;border-bottom:0px;">
			<div class="inside" style="position:absolute;font-size:32pt;top:0%;left:3%;height:100%;width:100%;font-weight: bold;">
				${orderInfo.station_no}<br>
				${orderInfo.station}<br>
				${orderInfo.lattice_mouth_no} 
			</div>
		</div>
		
		<div class="left right" style="position:absolute;font-size:10px;top:2%;left:90%;height:60%;line-height:60px;width:15%;text-align: left;border-top:0px;border-bottom:0px;letter-spacing:3px;">
			<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128"
			style="display:block;position:absolute;-webkit-transform:rotate(-90deg);left:-100px;top:230px;width:400px;height:75px;" />
			<div style="border: 0px;letter-spacing: 12px;position: absolute;-webkit-transform:rotate(-90deg);left:-80px;top: 150px;">${orderInfo.tracking_number}</div>
		</div>
		
		<div style="position:absolute;top:45%;left:0%;height:3%;width:95%;text-align:left;border-left:0px;border-right:0px;">
			<div class="inside" style="position:absolute;font-size:16pt;left:5%;height:100%;width:100%;border-bottom:3px;">
				运单编号：${orderInfo.tracking_number}
			</div>
		</div>
		<div class="left right" style="position:absolute;top:49%;left:0%;height:5%;width:95%;text-align:left;border-top:0px;">
			<div class="inside" style="position:absolute;font-size:10pt;left:5%;height:100%;width:90%;">
				收件人/代签人：  						
				<br>
				签收日期：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日
			</div>
		</div>
		<div class="inside" style="position:absolute;font-size:10pt;top:44%;left:0%;height:22%;width:100%;text-align:left;border-right:0px;">
			<div style="position:absolute;font-size:10pt;top:90%;left:80%;height:15%;width:15%;">
				已 验 视
			</div>
		</div>
		
		<div style="position:absolute;top:76%;left:5%;height:5%;width:50%;text-align:center;border:0px;">
			<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128"
			 style="position:absolute;top:5%;left:0%;width:100%;height:70%;" />
			<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:2px;">
				*${orderInfo.tracking_number}*
			</div>
		</div>
		<div style="position:absolute;top:76%;left:70%;height:6%;width:30%;text-align:left;border:0px;font-size: 25px;vertical-align: middle;font-weight: bold;">
				<div style="letter-spacing: 9px;border: 0px;">YUNDA</div>
				韵达速递
		</div>
		
		<div class="left" style="position:absolute;font-size:10pt;top:84%;left:0%;height:18%;width:50%;text-align:center;border-right:0px;border-bottom:0px;">
			寄件人：${orderInfo.send_name}<br>寄件人电话：${orderInfo.aftersale_phone}<br>
			寄件人地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
			<br>
			<br>
			收件人：${orderInfo.receive_name}   <br>
			收件人电话：${orderInfo.receive_mobile}&nbsp;${orderInfo.receive_phone}<br> 
			${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
		<div class="right" style="position:absolute;top:84%;left:50%;height:16%;width:50%;text-align:center;border-bottom:0px;">
			<div class="inside" style="position:absolute;font-size:20pt;top:2%;left:0%;height:20%;width:90%;">
			${orderInfo.shipment_category}<br/><br/>B
			</div>
		</div>
	
		<div class="left right" style="position:absolute;font-size:10pt;top:100%;left:0%;height:5%;width:100%;text-align:left;border-bottom:0px;">
			<div class="inside" style="position:absolute;font-size:5pt;top:8%;left:6%;height:90%;width:90%;">
				官网地址:http://www.yundaex.com <br/>客户热线 :95546  收件人联
			</div>
			<%-- <div class="inside" style="position:absolute;font-size:5pt;top:5%;left:60%;height:90%;width:40%;">
				第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张<br/><br/>
 第${orderInfo.pm}个包裹，本订单共${packNum}个
			</div> --%>
			<p style="font-size:10pt;text-align:left; margin-top: 35pt;margin-left: 20pt;">
				<c:if test="${fn:length(print_info.order_goods_list)<=4}">
					商品:
					<c:forEach var="infoList" items="${print_info.order_goods_list}">
						<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
					</c:forEach>
				</c:if>
			</p>					
		</div>
		<div class="left" style="position:absolute;font-size:10pt;bottom:-180px;left:0%;height:4%;width:50%;text-align:center;border:0px;">
			第${orderInfo.pm}个包裹，本订单共${packNum}个
		</div>
		<div class="right" style="position:absolute;font-size:10pt;bottom:-180px;left:50%;height:4%;width:50%;text-align:center;border:0px;">
			 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
		</div>
		</div>
	
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>
<c:if test="${orderInfo.shipping_code =='HT'}">
	<div class="wrap">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:990px;width:550px;">
	<div class="left right" style="position:absolute;font-size:24pt;top:0%;left:0%;height:10%;width:40%;text-align:center;border-top:0px;">
		<img src="<%=request.getContextPath() %>/static/images/ht_logo.png" style="width:90%;">
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:0%;left:38%;height:10%;width:60%;text-align:center;border-bottom:0px;border-left:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:90%;height:70%;" >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:100%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:10%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:10pt;top:20%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:20pt;top:30%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;text-align:center;">
			目的地
		</div>
		<div class="inside" style="position:absolute;font-size:30pt;top:00%;left:12%;height:100%;width:88%;text-align: center;margin: 20px 0;">
		 	${orderInfo.mark}
		</div>
	</div>
	
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:40%;left:0%;height:15%;width:40%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:3%;height:20%;width:70%;">
		收件人签收/日期:
		</div>
			<div class="inside" style="position:absolute;font-size:10pt;top:65%;left:3%;height:35%;width:90%;">
		运单编号：${orderInfo.tracking_number}<br>
		打印日期：<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:40%;left:40%;height:15%;width:60%;text-align:left;">
		
		<div class="inside" style="position:absolute;font-size:5pt;top:2%;left:2%;height:10%;width:95%;">
			已验视
		</div>
		<div class="inside" style="position:absolute;font-size:20pt;top:12%;left:5%;height:20%;width:80%;text-align:center;">
			${orderInfo.shipment_category}
			<p style="font-size:10pt;text-align:left;">
			<c:if test="${fn:length(print_info.order_goods_list)<=4}">
				商品:
				<c:forEach var="infoList" items="${print_info.order_goods_list}">
					<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
				</c:forEach>
			</c:if>
			</p>			
		</div>
		
	</div>
	<div class="left right" style="position:absolute;font-size:5pt;top:55%;left:0%;height:5%;width:95%;text-align:left;border-bottom:0px;">
	您对此单的签收，代表您已确认商品信息无误，包装完好，没有划痕，破损等表面质量问题。
	</div>
	
	<!-- divide -->
	<div style="position:absolute;top:65%;left:0%;height:10%;width:60%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:97%;height:70%;"  >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div style="position:absolute;top:65%;left:60%;height:10%;width:40%;text-align:center;border:0px;font-size: 20px">
		<img src="<%=request.getContextPath() %>/static/images/ht_logo.png" style="width:90%;">
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:75%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:10pt;top:85%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>

<c:if test="${orderInfo.shipping_code =='EMS'}">
	<div class="wrap" style="margin-left:12pt;">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:990px;width:550px;">
	<div class="left right" style="position:absolute;font-size:24pt;top:0%;left:0%;height:10%;width:40%;text-align:center;border-top:0px;">
		<img src="<%=request.getContextPath() %>/static/images/EMS.jpg" style="width:90%;">
		<p style="font-size:30pt;position:absolute;top:0;">快递包裹</p>
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:0%;left:38%;height:10%;width:60%;text-align:center;border-bottom:0px;border-left:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:90%;height:70%;" >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:100%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:10%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;text-align:center;">
			目的地
		</div>
		<div class="inside" style="position:absolute;font-size:30pt;top:00%;left:12%;height:100%;width:88%;text-align: center;margin: 20px 0;">
		 	${orderInfo.mark}
		</div>
	</div>
		
	<div class="left right" style="position:absolute;font-size:20pt;top:20%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:10pt;top:30%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	

	
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:40%;left:0%;height:15%;width:40%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:3%;height:20%;width:70%;">
		收件人签收/日期:
		</div>
			<div class="inside" style="position:absolute;font-size:10pt;top:65%;left:3%;height:35%;width:90%;">
		运单编号：${orderInfo.tracking_number}<br>
		打印日期：<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:40%;left:40%;height:15%;width:60%;text-align:left;">
		
		<div class="inside" style="position:absolute;font-size:5pt;top:2%;left:2%;height:10%;width:95%;">
			已验视
		</div>
		<div class="inside" style="position:absolute;font-size:20pt;top:12%;left:5%;height:20%;width:80%;text-align:center;">
			${orderInfo.shipment_category}
			<p style="font-size:10pt;text-align:left;">
			<c:if test="${fn:length(print_info.order_goods_list)<=4}">
				商品:
				<c:forEach var="infoList" items="${print_info.order_goods_list}">
					<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
				</c:forEach>
			</c:if>
			</p>			
		</div>
		
	</div>
	<div class="left right" style="position:absolute;font-size:5pt;top:55%;left:0%;height:5%;width:95%;text-align:left;border-bottom:0px;">
	您对此单的签收，代表您已确认商品信息无误，包装完好，没有划痕，破损等表面质量问题。
	</div>
	
	<!-- divide -->
	<div style="position:absolute;top:65%;left:0%;height:10%;width:60%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:97%;height:70%;"  >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div style="position:absolute;top:65%;left:60%;height:10%;width:40%;text-align:center;border:0px;font-size: 20px">
		<img src="<%=request.getContextPath() %>/static/images/EMS.jpg" style="width:90%;">
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:75%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:10pt;top:85%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>

<c:if test="${orderInfo.shipping_code =='STO'}">
	<div class="wrap">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:990px;width:550px;">
	<div class="left-top" style="position:absolute;font-size:20pt;top:2%;left:0%;height:5%;width:50%;text-align:center;border-right:0px;border-bottom:0px;">
		申通快递
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:2%;left:50%;height:5%;width:50%;text-align:center;border-bottom:0px;">
		${orderInfo.send_province}${orderInfo.send_city}
	</div>

	<div class="left" style="position:absolute;font-size:20pt;top:7%;left:0%;height:5%;width:75%;border-right:0px;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:10%;height:90%;width:40%;">
			${orderInfo.tracking_number}
		</div>
		<div class="inside" style="position:absolute;font-size:5pt;top:50%;left:50%;height:30%;width:40%;">
			日期：<%=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()) %>
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;top:12%;left:0%;height:5%;width:75%;text-align:left;border-right:0px;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:5pt;top:5%;left:3%;height:90%;width:95%;">
			寄件人：${orderInfo.send_name}   联系方式：${orderInfo.aftersale_phone}
			<br>
			地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="right" style="position:absolute;font-size:20pt;top:7%;left:75%;height:10%;width:25%;text-align:center;border-bottom:0px;">
		<!-- <img src="assets/img/Shop2DBarcodes/1953.png" style="margin-top:2px;" width="70px" height="70px">  -->
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:17%;left:0%;height:23%;width:40%;">
		<div class="inside" style="position:absolute;font-size:10pt;top:10%;left:6%;height:90%;width:90%;">
			<b>收件人：${orderInfo.receive_name}  <br>   电话： ${orderInfo.receive_mobile}　${orderInfo.receive_phone} </b>　
			<br>
			<b>地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}</b>
		</div>
	</div>

	<div class="left right" style="position:absolute;font-size:50px;top:17%;left:40%;height:23%;line-height:60px;width:60%;text-align:right;vertical-align:middle;letter-spacing:3px;filter:dropshadow(color=black,offx=1,offy=0) dropshadow(color=black,offx=2,offy=0) dropshadow(color=black,offx=3,offy=0)">
		<b>  ${orderInfo.receive_province} <br> ${orderInfo.receive_city} <br>  ${orderInfo.receive_district} </b>
	</div>		

	<div style="position:absolute;top:41%;left:0%;height:9%;width:100%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;left:4%;top:5%;width:90%;height:70%;margin-top:2px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:15px;">
			*${orderInfo.tracking_number}*
		</div>
	</div>
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:50%;left:0%;height:10%;width:70%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:5%;height:10%;width:95%;">
			您对此单的签收，代表您已确认商品信息无误，包装完好，没有划痕，破损等表面质量问题。
			<p style="font-size:10pt;text-align:left;">
			<c:if test="${fn:length(print_info.order_goods_list)<=4}">
				商品:
				<c:forEach var="infoList" items="${print_info.order_goods_list}">
					<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
				</c:forEach>
			</c:if>
			</p>			
		</div>
		<div class="inside" style="position:absolute;top:20%;left:5%;height:70%;width:95%;" >
			<%-- ${orderInfo.shipment_category} --%>
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:50%;left:70%;height:10%;width:30%;text-align:left;">
		<div class="inside" style="position:absolute;font-size:15pt;top:10%;left:5%;height:90%;width:80%;">
			签收
		</div>
	</div>

	<!-- divide -->
	<div style="position:absolute;top:65%;left:3%;height:5%;width:100%;text-align:left;border:0px;font-size: 20px">
		申通快递
	</div>
 
	<div style="position:absolute;top:70%;left:0%;height:10%;width:100%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:5%;width:90%;height:70%;"  >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:15px;">
			*${orderInfo.tracking_number}*
		</div>
	</div>

	<div class="left" style="position:absolute;font-size:20pt;top:80%;left:0%;height:4%;width:50%;text-align:center;border-right:0px;border-bottom:0px;">
		${orderInfo.shipment_category}
	</div>
	<div class="right" style="position:absolute;font-size:15pt;top:80%;left:50%;height:4%;width:50%;text-align:center;border-bottom:0px;">
		${orderInfo.tracking_number}
	</div>

	<div class="left right" style="position:absolute;font-size:10pt;top:84%;left:0%;height:5%;width:100%;text-align:left;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:5pt;top:5%;left:6%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}       联系方式：${orderInfo.aftersale_phone}
			<br>
			地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}

		</div>
	</div>

	<div class="left right" style="position:absolute;font-size:20pt;top:89%;left:0%;height:12%;width:100%;border-bottom:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:6%;height:90%;width:90%;">
			收件人：${orderInfo.receive_name}    电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址： ${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
			<br>
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>
<c:if test="${orderInfo.shipping_code =='DEPPON'}">
	<div class="wrap">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:990px;width:550px;">
	<div class="left right" style="position:absolute;font-size:24pt;top:0%;left:0%;height:10%;width:40%;text-align:center;border-top:0px;">
		德邦快递
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:0%;left:38%;height:10%;width:60%;text-align:center;border-bottom:0px;border-left:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:90%;height:70%;" >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:100%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:10%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:10pt;top:20%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:20pt;top:30%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;text-align:center;">
			目的地
		</div>
		<div class="inside" style="position:absolute;font-size:30pt;top:00%;left:12%;height:100%;width:88%;text-align: center;margin: 20px 0;">
		 	${orderInfo.mark}
		</div>
	</div>
	
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:40%;left:0%;height:15%;width:40%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:3%;height:20%;width:70%;">
		收件人签收/日期:
		</div>
			<div class="inside" style="position:absolute;font-size:10pt;top:65%;left:3%;height:35%;width:90%;">
		运单编号：${orderInfo.tracking_number}<br>
		打印日期：<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:40%;left:40%;height:15%;width:60%;text-align:left;">
		
		<div class="inside" style="position:absolute;font-size:5pt;top:2%;left:2%;height:10%;width:95%;">
			已验视
		</div>
		<div class="inside" style="position:absolute;font-size:20pt;top:12%;left:5%;height:20%;width:80%;text-align:center;">
			${orderInfo.shipment_category}
			<p style="font-size:10pt;text-align:left;">
			<c:if test="${fn:length(print_info.order_goods_list)<=4}">
				商品:
				<c:forEach var="infoList" items="${print_info.order_goods_list}">
					<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
				</c:forEach>
			</c:if>
			</p>			
		</div>
		
	</div>
	<div class="left right" style="position:absolute;font-size:5pt;top:55%;left:0%;height:5%;width:95%;text-align:left;border-bottom:0px;">
	您对此单的签收，代表您已确认商品信息无误，包装完好，没有划痕，破损等表面质量问题。
	</div>
	
	<!-- divide -->
	<div style="position:absolute;top:65%;left:0%;height:10%;width:60%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:97%;height:70%;"  >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div style="position:absolute;top:65%;left:60%;height:10%;width:40%;text-align:center;border:0px;font-size: 20px">
		德邦快递
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:75%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:10pt;top:85%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>
<c:if test="${orderInfo.shipping_code =='TTKDEX'}">
	<div class="wrap">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:990px;width:550px;">
	<div class="left right" style="position:absolute;font-size:24pt;top:0%;left:0%;height:10%;width:40%;text-align:center;border-top:0px;">
		天天快递
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:0%;left:38%;height:10%;width:60%;text-align:center;border-bottom:0px;border-left:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:90%;height:70%;" >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:100%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:10%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:10pt;top:20%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:20pt;top:30%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;text-align:center;">
			目的地
		</div>
		<div class="inside" style="position:absolute;font-size:30pt;top:00%;left:12%;height:100%;width:88%;text-align: center;margin: 20px 0;">
		 	${orderInfo.mark}
		</div>
	</div>
	
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:40%;left:0%;height:15%;width:40%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:3%;height:20%;width:70%;">
		收件人签收/日期:
		</div>
			<div class="inside" style="position:absolute;font-size:10pt;top:65%;left:3%;height:35%;width:90%;">
		运单编号：${orderInfo.tracking_number}<br>
		打印日期：<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:40%;left:40%;height:15%;width:60%;text-align:left;">
		
		<div class="inside" style="position:absolute;font-size:5pt;top:2%;left:2%;height:10%;width:95%;">
			已验视
		</div>
		<div class="inside" style="position:absolute;font-size:20pt;top:12%;left:5%;height:20%;width:80%;text-align:center;">
			${orderInfo.shipment_category}
			<p style="font-size:10pt;text-align:left;">
			<c:if test="${fn:length(print_info.order_goods_list)<=4}">
				商品:
				<c:forEach var="infoList" items="${print_info.order_goods_list}">
					<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
				</c:forEach>
			</c:if>
			</p>			
		</div>
		
	</div>
	<div class="left right" style="position:absolute;font-size:5pt;top:55%;left:0%;height:5%;width:95%;text-align:left;border-bottom:0px;">
	您对此单的签收，代表您已确认商品信息无误，包装完好，没有划痕，破损等表面质量问题。
	</div>
	
	<!-- divide -->
	<div style="position:absolute;top:65%;left:0%;height:10%;width:60%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:97%;height:70%;"  >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div style="position:absolute;top:65%;left:60%;height:10%;width:40%;text-align:center;border:0px;font-size: 20px">
		天天快递
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:75%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:10pt;top:85%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>
<c:if test="${orderInfo.shipping_code =='KJKD'}">
	<div class="wrap">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:990px;width:550px;">
	<div class="left right" style="position:absolute;font-size:24pt;top:0%;left:0%;height:10%;width:40%;text-align:center;border-top:0px;">
		快捷快递
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:0%;left:38%;height:10%;width:60%;text-align:center;border-bottom:0px;border-left:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:90%;height:70%;" >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:100%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:10%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:10pt;top:20%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:20pt;top:30%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;text-align:center;">
			目的地
		</div>
		<div class="inside" style="position:absolute;font-size:30pt;top:00%;left:12%;height:100%;width:88%;text-align: center;margin: 20px 0;">
		 	${orderInfo.mark}
		</div>
	</div>
	
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:40%;left:0%;height:15%;width:40%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:3%;height:20%;width:70%;">
		收件人签收/日期:
		</div>
			<div class="inside" style="position:absolute;font-size:10pt;top:65%;left:3%;height:35%;width:90%;">
		运单编号：${orderInfo.tracking_number}<br>
		打印日期：<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:40%;left:40%;height:15%;width:60%;text-align:left;">
		
		<div class="inside" style="position:absolute;font-size:5pt;top:2%;left:2%;height:10%;width:95%;">
			已验视
		</div>
		<div class="inside" style="position:absolute;font-size:20pt;top:12%;left:5%;height:20%;width:80%;text-align:center;">
			${orderInfo.shipment_category}
			<p style="font-size:10pt;text-align:left;">
			<c:if test="${fn:length(print_info.order_goods_list)<=4}">
				商品:
				<c:forEach var="infoList" items="${print_info.order_goods_list}">
					<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
				</c:forEach>
			</c:if>
			</p>			
		</div>
		
	</div>
	<div class="left right" style="position:absolute;font-size:5pt;top:55%;left:0%;height:5%;width:95%;text-align:left;border-bottom:0px;">
	您对此单的签收，代表您已确认商品信息无误，包装完好，没有划痕，破损等表面质量问题。
	</div>
	
	<!-- divide -->
	<div style="position:absolute;top:65%;left:0%;height:10%;width:60%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:97%;height:70%;"  >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div style="position:absolute;top:65%;left:60%;height:10%;width:40%;text-align:center;border:0px;font-size: 20px">
		快捷快递
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:75%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:10pt;top:85%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>
<c:if test="${orderInfo.shipping_code =='ZJS'}">
	<div class="wrap">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:990px;width:550px;">
	<div class="left right" style="position:absolute;font-size:24pt;top:0%;left:0%;height:10%;width:40%;text-align:center;border-top:0px;">
		宅急送
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:0%;left:38%;height:10%;width:60%;text-align:center;border-bottom:0px;border-left:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:90%;height:70%;" >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:100%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:10%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:10pt;top:20%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:20pt;top:30%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;text-align:center;">
			目的地
		</div>
		<div class="inside" style="position:absolute;font-size:30pt;top:00%;left:12%;height:100%;width:88%;text-align: center;margin: 20px 0;">
		 	${orderInfo.mark}
		</div>
	</div>
	
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:40%;left:0%;height:15%;width:40%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:3%;height:20%;width:70%;">
		收件人签收/日期:
		</div>
			<div class="inside" style="position:absolute;font-size:10pt;top:65%;left:3%;height:35%;width:90%;">
		运单编号：${orderInfo.tracking_number}<br>
		打印日期：<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:40%;left:40%;height:15%;width:60%;text-align:left;">
		
		<div class="inside" style="position:absolute;font-size:5pt;top:2%;left:2%;height:10%;width:95%;">
			已验视
		</div>
		<div class="inside" style="position:absolute;font-size:20pt;top:12%;left:5%;height:20%;width:80%;text-align:center;">
			${orderInfo.shipment_category}
			<p style="font-size:10pt;text-align:left;">
			<c:if test="${fn:length(print_info.order_goods_list)<=4}">
				商品:
				<c:forEach var="infoList" items="${print_info.order_goods_list}">
					<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
				</c:forEach>
			</c:if>
			</p>			
		</div>
		
	</div>
	<div class="left right" style="position:absolute;font-size:5pt;top:55%;left:0%;height:5%;width:95%;text-align:left;border-bottom:0px;">
	您对此单的签收，代表您已确认商品信息无误，包装完好，没有划痕，破损等表面质量问题。
	</div>
	
	<!-- divide -->
	<div style="position:absolute;top:65%;left:0%;height:10%;width:60%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:97%;height:70%;"  >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div style="position:absolute;top:65%;left:60%;height:10%;width:40%;text-align:center;border:0px;font-size: 20px">
		宅急送
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:75%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:10pt;top:85%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>
<c:if test="${orderInfo.shipping_code =='CHINAPOST'}">
	<div class="wrap">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:990px;width:550px;">
	<div class="left right" style="position:absolute;font-size:24pt;top:0%;left:0%;height:10%;width:40%;text-align:center;border-top:0px;">
		中国邮政
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:0%;left:38%;height:10%;width:60%;text-align:center;border-bottom:0px;border-left:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:90%;height:70%;" >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:100%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:10%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:10pt;top:20%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:20pt;top:30%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;text-align:center;">
			目的地
		</div>
		<div class="inside" style="position:absolute;font-size:30pt;top:00%;left:12%;height:100%;width:88%;text-align: center;margin: 20px 0;">
		 	${orderInfo.mark}
		</div>
	</div>
	
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:40%;left:0%;height:15%;width:40%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:3%;height:20%;width:70%;">
		收件人签收/日期:
		</div>
			<div class="inside" style="position:absolute;font-size:10pt;top:65%;left:3%;height:35%;width:90%;">
		运单编号：${orderInfo.tracking_number}<br>
		打印日期：<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:40%;left:40%;height:15%;width:60%;text-align:left;">
		
		<div class="inside" style="position:absolute;font-size:5pt;top:2%;left:2%;height:10%;width:95%;">
			已验视
		</div>
		<div class="inside" style="position:absolute;font-size:20pt;top:12%;left:5%;height:20%;width:80%;text-align:center;">
			${orderInfo.shipment_category}
			<p style="font-size:10pt;text-align:left;">
			<c:if test="${fn:length(print_info.order_goods_list)<=4}">
				商品:
				<c:forEach var="infoList" items="${print_info.order_goods_list}">
					<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
				</c:forEach>
			</c:if>
			</p>			
		</div>
		
	</div>
	<div class="left right" style="position:absolute;font-size:5pt;top:55%;left:0%;height:5%;width:95%;text-align:left;border-bottom:0px;">
	您对此单的签收，代表您已确认商品信息无误，包装完好，没有划痕，破损等表面质量问题。
	</div>
	
	<!-- divide -->
	<div style="position:absolute;top:65%;left:0%;height:10%;width:60%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:97%;height:70%;"  >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div style="position:absolute;top:65%;left:60%;height:10%;width:40%;text-align:center;border:0px;font-size: 20px">
		中国邮政
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:75%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:10pt;top:85%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>
<c:if test="${orderInfo.shipping_code =='QFKD'}">
	<div class="wrap">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:990px;width:550px;">
	<div class="left right" style="position:absolute;font-size:24pt;top:0%;left:0%;height:10%;width:40%;text-align:center;border-top:0px;">
		全峰快递
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:0%;left:38%;height:10%;width:60%;text-align:center;border-bottom:0px;border-left:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:90%;height:70%;" >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:100%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:10%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:10pt;top:20%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:20pt;top:30%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;text-align:center;">
			目的地
		</div>
		<div class="inside" style="position:absolute;font-size:30pt;top:00%;left:12%;height:100%;width:88%;text-align: center;margin: 20px 0;">
		 	${orderInfo.mark}
		</div>
	</div>
	
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:40%;left:0%;height:15%;width:40%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:3%;height:20%;width:70%;">
		收件人签收/日期:
		</div>
			<div class="inside" style="position:absolute;font-size:10pt;top:65%;left:3%;height:35%;width:90%;">
		运单编号：${orderInfo.tracking_number}<br>
		打印日期：<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:40%;left:40%;height:15%;width:60%;text-align:left;">
		
		<div class="inside" style="position:absolute;font-size:5pt;top:2%;left:2%;height:10%;width:95%;">
			已验视
		</div>
		<div class="inside" style="position:absolute;font-size:20pt;top:12%;left:5%;height:20%;width:80%;text-align:center;">
			${orderInfo.shipment_category}
			<p style="font-size:10pt;text-align:left;">
			<c:if test="${fn:length(print_info.order_goods_list)<=4}">
				商品:
				<c:forEach var="infoList" items="${print_info.order_goods_list}">
					<span style="font-size:10pt;">${infoList.barcode}-${infoList.goods_number}</span>;
				</c:forEach>
			</c:if>
			</p>			
		</div>
		
	</div>
	<div class="left right" style="position:absolute;font-size:5pt;top:55%;left:0%;height:5%;width:95%;text-align:left;border-bottom:0px;">
	您对此单的签收，代表您已确认商品信息无误，包装完好，没有划痕，破损等表面质量问题。
	</div>
	
	<!-- divide -->
	<div style="position:absolute;top:65%;left:0%;height:10%;width:60%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:97%;height:70%;"  >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div style="position:absolute;top:65%;left:60%;height:10%;width:40%;text-align:center;border:0px;font-size: 20px">
		全峰快递
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:75%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:10pt;top:85%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>
<c:if test="${orderInfo.shipping_code =='LEQEE_ZT'}">
	<div class="wrap">
	<c:if test="${orderInfo.order_status=='CANCEL'}">
	<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
	</c:if>
	
	<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:990px;width:550px;">
	<div class="left right" style="position:absolute;font-size:24pt;top:0%;left:0%;height:10%;width:40%;text-align:center;border-top:0px;">
		内部员工自提
	</div>
	<div class="right-top" style="position:absolute;font-size:20pt;top:0%;left:38%;height:10%;width:60%;text-align:center;border-bottom:0px;border-left:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:90%;height:70%;" >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:100%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:10%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	<div class="left right" style="position:absolute;font-size:10pt;top:20%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:20pt;top:30%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;text-align:center;">
			目的地
		</div>
		<div class="inside" style="position:absolute;font-size:30pt;top:00%;left:12%;height:100%;width:88%;text-align: center;margin: 20px 0;">
		 	${orderInfo.mark}
		</div>
	</div>
	
	<div class="left-bottom" style="position:absolute;font-size:10pt;top:40%;left:0%;height:15%;width:40%;text-align:left;border-right:0px;">
		<div class="inside" style="position:absolute;font-size:10pt;top:5%;left:3%;height:20%;width:70%;">
		收件人签收/日期:
		</div>
			<div class="inside" style="position:absolute;font-size:10pt;top:65%;left:3%;height:35%;width:90%;">
		运单编号：${orderInfo.tracking_number}<br>
		打印日期：<%String datetime=new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %> <%=datetime %>
		</div>
	</div>
	<div class="right-bottom" style="position:absolute;font-size:10pt;top:40%;left:40%;height:15%;width:60%;text-align:left;">
		
		<div class="inside" style="position:absolute;font-size:5pt;top:2%;left:2%;height:10%;width:95%;">
			已验视
		</div>
		<div class="inside" style="position:absolute;font-size:20pt;top:12%;left:5%;height:20%;width:80%;text-align:center;">
			${orderInfo.shipment_category}
		</div>
		
	</div>
	<div class="left right" style="position:absolute;font-size:5pt;top:55%;left:0%;height:5%;width:95%;text-align:left;border-bottom:0px;">
	您对此单的签收，代表您已确认商品信息无误，包装完好，没有划痕，破损等表面质量问题。
	</div>
	
	<!-- divide -->
	<div style="position:absolute;top:65%;left:0%;height:10%;width:60%;text-align:center;border:0px;">
		<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position:absolute;top:5%;left:3%;width:97%;height:70%;"  >
		<div class="inside" style="position:absolute;font-size:10pt;top:80%;left:5%;height:15%;width:90%;letter-spacing:5px;">
			${orderInfo.tracking_number}
		</div>
	</div>
	<div style="position:absolute;top:65%;left:60%;height:10%;width:40%;text-align:center;border:0px;font-size: 20px">
		内部员工自提
	</div>
	<div class="left right" style="position:absolute;font-size:20pt;top:75%;left:0%;height:10%;width:95%;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			收件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:00%;left:12%;height:100%;width:85%;">
			收件人：${orderInfo.receive_name}   
			<br>电话：${orderInfo.receive_mobile}　${orderInfo.receive_phone}
			<br>
			地址：${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
		</div>
	</div>
	
	<div class="left right" style="position:absolute;font-size:10pt;top:85%;left:0%;height:10%;width:95%;text-align:left;border-bottom:0px;">
		<div class="left-top" style="position:absolute;font-size:12pt;top:0%;left:3%;height:100%;width:5%;border-bottom:0px;">
			寄件人
		</div>
		<div class="inside" style="position:absolute;font-size:12pt;top:5%;left:12%;height:90%;width:90%;">
			寄件人：${orderInfo.send_name}
			<br> 联系方式：${orderInfo.aftersale_phone}
			<br> 地址：${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}
		</div>
	</div>
	<div class="left" style="position:absolute;font-size:10pt;bottom:0%;left:0%;height:4%;width:50%;text-align:center;border:0px;">
		第${orderInfo.pm}个包裹，本订单共${packNum}个
	</div>
	<div class="right" style="position:absolute;font-size:10pt;bottom:0%;left:50%;height:4%;width:50%;text-align:center;border:0px;">
		 第${orderInfo.pageIndex}张面单,本波次共${batchNumSize}张
	</div>
	</div>
	</div>
	<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:if>
<c:if test="${(orderInfo.pageIndex)%order_count == 0}">
	<c:if test="${(batchNumSize) != (orderInfo.pageIndex)}">
      <div STYLE="page-break-after: always;"></div>
  </c:if>
</c:if>
</c:forEach>
</body>
</html>