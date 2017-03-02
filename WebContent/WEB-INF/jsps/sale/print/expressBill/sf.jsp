<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>

<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/sf-arata.css">
<style>
body,html {
	height:688px;
	width:450px;
}

.wrap {
	position: relative;
	width: 100%;
	height: 100%;
	border:0px solid #000;
}
</style>
</head>
<body onload="window.print()">
<c:forEach items="${print_info.order_info_list}" var="orderInfo">
<div class="wrap">
<c:if test="${orderInfo.order_status=='CANCEL'}">
<img src="<%=request.getContextPath() %>/static/images/x-sf.png" style="position:absolute;top:0;left:0;z-index:999;width:100%;height:100%;">
</c:if>
	<div class="inside" style="position: absolute; font-size: 10pt; top: 0px; left: 0px; width:450px; height:670px;">
		<div style="width: 100%; height: 10%; left: 0%; top: 0%;"
			class="border_bottom">
			<!--此处应有LOGO-->
			<!--COD-->
			<div style="top: 10%; left: 40%; font-size: 30px;" class="inside">
				<c:if test="${orderInfo.is_cod=='Y'}">cod</c:if>
			</div>
		</div>
		<div style="width: 100%; height: 13%; left: 0%; top: 10%;" class="border_bottom">
			<div style="height: 100%; width: 65%; float: left;">
				<img src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128" style="position: absolute; left: 15%; top: 5%; width: 70%; height: 60%; margin-top: 2px;" />
				<div class="inside" style="position: absolute; font-size: 10pt; top: 70%; left: 15%; height: 25%; width: 70%; letter-spacing: 10px; text-align: center; font-family: Arial;">
					${orderInfo.tracking_number}
				</div>
			</div>
			<div style="width: 35%; height: 100%; left: 65%; float: right;">
				<p style="margin: 2px; font-size: 20px; font-family: '黑体';">${orderInfo.alias}</p>
				<p style="margin: 2px; font-size: 6px; font-family: '黑体';">目的地</p>
				<p style="margin: 2px; font-size: 40px; text-align: center; font-family: Arial;">${orderInfo.sf_destcode}</p>
			</div>
			<div style="clear: both;"></div>
		</div>
		<div style="width: 100%; height: 12%; left: 0%; top: 23%;"
			class="border_bottom">
			<!--Receive-->
			<div
				style="height: 100%; width: 5%; float: left; font-family: '黑体'; font-size: 14px;"
				class="right border_bottom">
				<p>收方：</p>
			</div>
			<div style="height: 100%; width: 95%; left: 5%; float: right;"
				class="left border_bottom">
				<p style="font-family: '黑体'; font-size: 8px;">
					[${orderInfo.receive_province}][${orderInfo.receive_city}][${orderInfo.receive_district}]${orderInfo.receive_address}
				</p>
				<p style="font-family: '黑体'; font-size: 8px;">
					收件人：${orderInfo.receive_name};
					电话：${orderInfo.receive_mobile}&nbsp;${orderInfo.receive_phone}</p>
				<!-- 
				<c:if test="${orderInfo.customer_id=='65670'}">
					<p>
						<span
							style="font-family: '黑体'; font-size: 8px; text-align: left; width: 60%;">
							送前电联;核对收货人身份证;开箱内袋无损 </span> <span
							style="font-family: '黑体'; font-size: 8px; text-align: right; width: 40%;">
							打印时间：<%String datetimea = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); %><%=datetimea%></span>
					</p>
				</c:if>
				<c:if test="${orderInfo.customer_id!='65670'}">
					<p style="font-family: '黑体'; font-size: 8px; text-align: right;">
						打印时间：<%String datetimeb = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());%><%=datetimeb%></p>
				</c:if>
				
				 -->
				 <p style="font-family: '黑体'; font-size: 8px; text-align: right;">
						打印时间：<%String datetime0 = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());%><%=datetime0%></p>
			</div>
		</div>
		<div style="width: 100%; height: 16%; left: 0%; top: 35%;">
			<div style="height: 100%; width: 65%; float: left;">
				<p style="font-family: '黑体'; font-size: 8px;">
					件数：1
				</p>
				<p style="font-family: '黑体'; font-size: 8px;">
					付款方式：
					<c:if test="${orderInfo.sf_payment_method==1}">
					寄方付
					</c:if>
					<c:if test="${orderInfo.sf_payment_method==2}">
					收方付
					</c:if>
					<c:if test="${orderInfo.sf_payment_method==3}">
					第三方付 &nbsp;&nbsp;
						第三方地区：${orderInfo.sf_tp_areacode}
					</c:if>
				</p>

				<p style="font-family: '黑体'; font-size: 8px;">
					月结账号：${orderInfo.sf_monthly_balance_account}</p>
				<p style="font-family: '黑体'; font-size: 8px;">实际重量：
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 计费重量：</p>
				<p style="font-family: '黑体'; font-size: 8px;">
					<c:if test="${orderInfo.insurance>0}">
						声明价值：${orderInfo.insurance}元
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 保价费用：&nbsp;&nbsp;&nbsp;&nbsp;元
						</c:if>
				</p>
			</div>
			<div style="width: 35%; height: 100%; left: 65%; float: right;">
				<div style="height: 60%; width: 100%; left: 0%; top: 0%;"
					class="left right">
					<c:if test="${orderInfo.is_cod=='Y'}">
						<p style="font-size: 20px;">
							代收货款：<br> ${orderInfo.sf_cod_note}
						</p>
					</c:if>
				</div>
				<div style="height: 40%; width: 100%; left: 0%; top: 60%;"
					class="inside">
					<p style="font-family: '黑体'; font-size: 8px;">运费：</p>
					<p style="font-family: '黑体'; font-size: 8px;">费用合计：</p>
				</div>
			</div>
			<div style="clear: both;"></div>
		</div>
		<div style="width: 100%; height: 10%; left: 0%; top: 51%;"
			class="border_bottom">
			<!--Receive-->
			<div
				style="height: 100%; width: 15%; float: left; font-family: '黑体'; font-size: 14px;"
				class="right border_bottom">
				<p>寄方：</p>
				<p>原寄地：${orderInfo.sf_origincode}</p>
			</div>
			<div style="height: 100%; width: 35%; left: 15%; float: left;"
				class="left border_bottom">
				<p style="font-family: '黑体'; font-size: 8px;">
					${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}${orderInfo.send_address}
					<br> ${orderInfo.send_name} ${orderInfo.aftersale_phone} <br>

				</p>
			</div>
			<div
				style="height: 100%; width: 20%; left: 50%; float: left; font-family: '黑体'; font-size: 8px;"
				class="right border_bottom">
				<p>
					寄件日：<%
					String datetime1 = new SimpleDateFormat("yyyy-MM-dd")
							.format(Calendar.getInstance().getTime());
				%>
					<%=datetime1%></p>
				<p>收派员：</p>
				<p>派件员：</p>
			</div>
			<div
				style="height: 100%; width: 30%; left: 70%; float: left; font-family: '黑体'; font-size: 14px;"
				class="right border_bottom">
				<p>签收：</p>
				<p>&nbsp;</p>
				<p>日期: &nbsp;&nbsp;&nbsp;月 &nbsp;&nbsp;&nbsp;日</p>
			</div>
		</div>
		<div style="width: 100%; height: 11%; left: 0%; top: 61%;"
			class="border_bottom">
			<div style="height: 100%; width: 46%; float: left;"
				class="border_bottom">
				<!-- SF LOGO	 -->
				<!-- <p style="font-size: 20px;margin-top:10%;">顺丰快递 95538</p> -->
				<img
					src="data:image/png;base64,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">
			</div>
			<div style="width: 54%; height: 100%; left: 46%; float: right;"
				class="border_bottom">
				<img
					src="../common/barcode/generate?barcode=${orderInfo.tracking_number}&text=0&type=code128"
					style="position: absolute; left: 15%; top: 5%; width: 70%; height: 60%; margin-top: 2px;" />
				<div class="inside"
					style="position: absolute; font-size: 10pt; top: 70%; left: 15%; height: 25%; width: 70%; letter-spacing: 6px; text-align: center; font-family: Arial;">
					${orderInfo.tracking_number}</div>
			</div>
			<div style="clear: both;"></div>
		</div>
		<div style="width: 100%; height: 9%; left: 0%; top: 72%;"
			class="border_bottom">
			<div style="height: 100%; width: 10%; float: left;"
				class="right border_bottom">寄方：</div>
			<div
				style="width: 40%; height: 100%; left: 10%; float: left; font-family: '黑体'; font-size: 8px;"
				class="right border_bottom">
				${orderInfo.send_province}${orderInfo.send_city}${orderInfo.send_district}${orderInfo.send_address}
				<br> ${orderInfo.send_name} ${orderInfo.aftersale_phone} <br>
			</div>
			<div style="width: 10%; height: 100%; left: 50%; float: left;"
				class="right border_bottom">收方：</div>
			<div
				style="width: 40%; height: 100%; left: 60%; float: left; font-family: '黑体'; font-size: 8px;"
				class="border_bottom">
				${orderInfo.receive_province}${orderInfo.receive_city}${orderInfo.receive_district}${orderInfo.receive_address}
				<br> ${orderInfo.receive_name}
				${orderInfo.receive_mobile}&nbsp;&nbsp;${orderInfo.receive_phone}
			</div>
			<div style="clear: both;"></div>
		</div>
		<div style="width: 100%; height: 3%; left: 0%; top: 81%;"
			class="border_bottom">
			<div
				style="height: 100%; width: 10%; float: left; text-align: center;"
				class="right border_bottom">数量</div>
			<div
				style="width: 60%; height: 100%; left: 10%; float: left; text-align: center;"
				class="right border_bottom">托寄物</div>
			<div
				style="width: 30%; height: 100%; left: 70%; float: left; text-align: center;"
				class="right border_bottom">备注</div>
			<div style="clear: both;"></div>
		</div>
		<div style="width: 100%; height: 7%; left: 0%; top: 84%;"
			class="border_bottom">
			<div
				style="height: 100%; width: 10%; float: left; text-align: center;"
				class="right border_bottom">1</div>
			<div
				style="width: 60%; height: 100%; left: 10%; float: left; text-align: center;"
				class="right border_bottom">${orderInfo.shipment_category}</div>
			<div
				style="width: 30%; height: 100%; left: 70%; float: left; text-align: center;"
				class="right border_bottom">不到转寄</div>
			<div style="clear: both;"></div>
		</div>
		<div style="width: 100%; height: 8%; left: 0%; top: 91%;"
			class="border_bottom">
			<div
				style="height: 100%; width: 80%; float: left; text-align: center;"
				class="right border_bottom">
				<!-- 此处为用户自备注区
				-->
			</div>
			<div
				style="width: 20%; height: 100%; left: 80%; float: left; text-align: center;"
				class="right border_bottom">
				${orderInfo.pm}
			</div>
			<div style="clear: both;"></div>
		</div>
	</div>
</div>
<div STYLE="page-break-after: always;border: 0px;"></div>
</c:forEach>
</body>
</html>