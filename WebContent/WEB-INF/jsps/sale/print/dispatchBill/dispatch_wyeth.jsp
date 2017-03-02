<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>打印发货单</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<style type="text/css">
html, body {
	margin: 0;
	padding: 0;
	width: 100%;
	height: 100%;
}
.pageMsg {
	position: relative;
	width: 450pt;
	height: 100%;
	margin: 0 auto;
	border: 1px solid #fff;
}
.orderPage {
	position: relative;
	overflow: auto;
}
.orderPage p {
	float: right;
	font-size: 16pt;
}
.sendMsg {
	position: relative;
	top: 70pt;
	left: 20pt;
	width: 100%;
}
.goodsMsg {
	display: table;
	position: relative;
	top: 160pt;
	left: 20pt;
	width: 100%;
}
.sendMsg li {
	display: table-cell;
	vertical-align: middle;
	overflow: auto;
}
.sendMsg li span {
	display: inline-block;
}
li {
	margin: 5pt 0 0 0;
}
span {
	display: inline-block;
	font-size: 8pt;
	line-height: 10pt;
	text-align: center;
	word-break:break-all;/*强制英文单词断行*/
}
.goodsName {
	position: relative;
}
.orderImg {
	position: relative;
	top: -45pt;
	left: 20px;
	display: inline-block;
	width: 120pt;
	text-align: center;
}
.imgOrder {
	display: block;
	width: 120pt;
	height: 30pt;
}
.batchPickMsg {
	position: relative;
	top: 272pt;
	overflow: auto;
}
.batchPickImg {
	float: right;
	text-align: right;
}
.imgBatchPick {
	display: block;
	text-align:center;
}
</style>
</head>
<body onLoad="window.print()">
	<div>
		<c:forEach items="${orderInfoList}" varStatus="i" var="order">
		<c:set var="max" value="${order.page_line}"></c:set>
			<ul class="pageMsg">
				<c:forEach items="${order.orderGoodsList}" varStatus="j" var="goods">
					<c:if test="${(j.index + 1) % max == 1}">
						<li class="orderPage">
							<p>${order.order_num} - ${order.sequence_no}</p>
						</li>
						<li class="orderImg">
							<img class="imgOrder" src="../common/barcode/generate?barcode=${order.order_id}&text=0">
							<span>${order.order_id}</span>
							<p style="padding:0;margin:0; font-size: 12px; margin: 5px 0 0 0;">
								第<fmt:parseNumber type="number" integerOnly="true" value="${(j.index + 1) / max + 1}" />页,
								共<fmt:formatNumber type="number" value="${fn:length(order.orderGoodsList) / max + 0.49}" pattern="#,###,###,###"></fmt:formatNumber>页
							</p>							
						</li>
						<li class="sendMsg">
							<span style="width: 74pt;margin: 0 0 0 0pt">${order.shop_order_sn}</span>
							<span style="width: 74pt;margin: 0 0 0 1pt">${order.order_id}</span>
							<span style="width: 74pt;margin: 0 0 0 1pt">${order.taobao_user_id}</span>
							<span style="width: 74pt;margin: 0 0 0 1pt">${order.receive_name}</span>
							<span style="width: 74pt;margin: 0 0 0 1pt">${order.mobile_number}</span>
							<span style="width: 50pt;margin: 0 0 0 1pt"><fmt:formatNumber type="number" pattern="0.00" maxFractionDigits="2" value="${order.goods_amount}" /></span>
						</li>
					</c:if>
					<li class="goodsMsg">
						<span style="width: 30pt;">${j.index+1}</span>
						<span class="goodsName" style="width: 250pt;">${goods.goods_name}</span>
						<span style="width: 30pt;">${goods.goods_number}</span>
						<span style="width: 77pt;">${goods.barcode}</span>
						<span style="width: 40pt;">${goods.location_kw_barcode}</span>
					</li>
					<c:if test="${(j.index + 1) % max == 0 || fn:length(order.orderGoodsList) == j.index+1}">
						<li class="batchPickMsg">
							<div class="batchPickImg">
								<img class="imgBatchPick" src="../common/barcode/generate?barcode=${order.batch_pick_sn}&text=0">
								<div style="text-align:center;">批拣单号:${order.batch_pick_sn}</div>
								<p style="font-size: 18pt; text-align:center; padding-right: 12pt; margin-top: 10pt;">本订单商品共${order.kind_num}sku,${order.all_num}件</p>
							</div>
						</li>
						<c:if test="${fn:length(order.orderGoodsList) != j.index+1}">
							<div style="page-break-after: always;"></div>
						</c:if>
					</c:if>
				</c:forEach>
			</ul>
			<c:if test="${fn:length(orderInfoList) != (i.index+1)}">
				  <div STYLE="page-break-after: always;">
				</div>
			</c:if>
		</c:forEach>
	</div>
</body>
</html>