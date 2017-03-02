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
	width: 350pt;
	height: 100%;
	margin: 0 auto;
	border: 1px solid #fff;
}
.orderPage {
	/*margin: 20pt 0 0 0;*/
	text-align: right;
	overflow: auto;
	position: relative;
}
.orderPage p {
	font-size: 16pt;
	margin: 0;
}
.sendMsg {
	position: relative;
	top: 65pt;
	width: 100%;
}
.goodsMsg {
	position: relative;
	top: 130pt;
	width: 100%;
}
.sendMsg li {
	overflow: auto;
}
.sendMsg li span {
	display: block;
	float: left;
}
li {
	font-size: 0;
	margin: 5pt 0 0 0;
}
span {
	display: inline-block;
	font-size: 8pt;
	line-height: 10pt;
	text-align: center;
	word-break:break-all;/*强制英文单词断行*/
}
.batchPickMsg {
	position: relative;
	top: -188pt;
	text-align: right;
}
.imgBatchPick {
	width: 120pt;
}
.batchPickImg {
	text-align: right;
	margin: -41pt 0 0 0;
}
.goodsName {
	position: relative;
	vertical-align: middle;
}
.orderImg {
	position: relative;
	left: 0px;
}
.imgOrder {
	display: block;
	width: 120pt;
	height: 30pt;
}
</style>
</head>
<body onLoad="window.print()">
	<div>
		<c:forEach items="${orderInfoList}" varStatus="i" var="order">
		<c:set var="max" value="${order.page_line}"></c:set>
			<ul class="pageMsg">
				<c:forEach items="${order.orderGoodsList}" varStatus="j" var="goods">
				<!-- ${(j.index + 1) % max} -->
					<c:if test="${(j.index + 1) % max == 1}">
						<li class="orderPage">
							<p>
								${order.order_num} - ${order.sequence_no}
								<span style="position: absolute;left:10pt;top:10pt;font-size: 16pt;">
									第<fmt:parseNumber type="number" integerOnly="true" value="${(j.index + 1) / max+1}" />页,
									共<fmt:formatNumber type="number" value="${fn:length(order.orderGoodsList) / max + 0.49}" pattern="#,###,###,###"></fmt:formatNumber>页
								</span>
							</p>
						</li>
						<li class="orderImg">
							<img class="imgOrder" src="../common/barcode/generate?barcode=${order.order_id}&text=0">
							<span style="margin: 5px 0 0 0;">${order.order_id}</span>
							<div class="batchPickImg">
								<img class="imgBatchPick" src="../common/barcode/generate?barcode=${order.batch_pick_sn}&text=0">
								<div style="font-size: 8pt">波次单号:${order.batch_pick_sn}</div>
							</div>
						</li>
						<li class="sendMsg">
							<span style="width: 57pt;margin: 0 0 0 0pt">${order.shop_order_sn}</span>
							<span style="width: 57pt;margin: 0 0 0 1pt">${order.order_id}</span>
							<span style="width: 57pt;margin: 0 0 0 1pt">${order.taobao_user_id}</span>
							<span style="width: 57pt;margin: 0 0 0 1pt">${order.receive_name}</span>
							<span style="width: 57pt;margin: 0 0 0 1pt">${order.mobile_number}</span>
							<span style="width: 57pt;margin: 0 0 0 1pt"><fmt:formatNumber type="number" pattern="0.00" maxFractionDigits="2" value="${order.goods_amount}" /></span>
						</li>
<%-- 						<p style="padding:0;margin:0; font-size: 12px; margin: 140pt 0 0 0; text-align: right;">
							<fmt:parseNumber type="number" value="${0.25 + (0.25 % 1 == 0 ? 0 : 0.5)}" />
							第<fmt:parseNumber type="number" integerOnly="true" value="${(j.index + 1) / max+1}" />页,
							共<fmt:formatNumber type="number" value="${fn:length(order.orderGoodsList) / max + 0.49}" pattern="#,###,###,###"></fmt:formatNumber>页
						</p> --%>
					</c:if>
					<li class="goodsMsg">
						<span style="width: 15%; text-align: left;">
							${j.index+1}
						</span>
						<span class="goodsName" style="width: 25%;">${goods.goods_name}</span>
						<span style="width: 20%;">${goods.goods_number}</span>
						<span style="width: 20%;">${goods.barcode}</span>
						<span style="width: 20%;">${goods.location_kw_barcode}</span>
					</li>

					<c:if test="${(j.index + 1) % max == 0 || fn:length(order.orderGoodsList) == j.index+1}">
						<!-- <li class="batchPickMsg">
							<div class="batchPickImg">
								<img class="imgBatchPick" src="../common/barcode/generate?barcode=${order.batch_pick_sn}&text=0">
								<div style="font-size: 8pt">波次单号:${order.batch_pick_sn}</div>
							</div>
						</li> -->
						<p style="padding:0;margin:0; font-size: 16pt; margin: 140pt 0 0 0; text-align: center;">
							本订单商品共${order.kind_num}sku,${order.all_num}件
						</p>
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