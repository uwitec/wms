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
	<div class="inside" style="position:absolute;font-size:10pt;top:75%;left:90%;height:10%;width:5%;">
		${orderInfo.pm}
	</div>
</div>
</div>
</div>
<div STYLE="page-break-after: always;border: 0px;"></div> 
</c:forEach>
</body>
</html>
</html>