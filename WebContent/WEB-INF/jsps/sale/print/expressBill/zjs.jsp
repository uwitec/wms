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
<div class="inside" style="position:absolute;font-size:10pt;top:93%;left:90%;height:5%;width:5%;">${orderInfo.pm}</div>
</div>
</div>
<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:forEach>
</body>
</html>