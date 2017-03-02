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
	height: 810px;
	width: 450px;
}

.wrap {
	position: relative;
	width: 100%;
	height: 100%;
	border: 0px solid black;
}
</style>
</head>
<body onload="window.print()">
<c:forEach items="${print_info.order_info_list}" var="orderInfo">
<div class="wrap">
<c:if test="${orderInfo.order_status=='CANCEL'}">
<img src="<%=request.getContextPath() %>/static/images/x.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
</c:if>
<div class="inside" style="position:absolute;font-size:10pt;top:0px;left:0px;height:800px;width:450px;">

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
		官网地址:http://www.yundaex.com 客户热线 :95546  收件人联
	</div>
	<div class="inside" style="position:absolute;font-size:5pt;top:5%;left:96%;height:90%;width:5%;">
		${orderInfo.pm}
	</div>
</div>
</div>

</div>
<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:forEach>
</body>
</html>