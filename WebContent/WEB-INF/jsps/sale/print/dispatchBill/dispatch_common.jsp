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
	<style type="text/css">
		html, body, .pageMsg {
			padding: 0;
			margin: 0;
			font-size: 12px;
		}
		li {
			list-style: none;
			/* border: 1px solid #f00; */
		}
    	.orderMsg {
    		position: relative;
    		top: 40pt;
    		left: 0;
    	}
    	.orderPage {
    		position: relative;
    		top: -20pt;
    		left: -50pt;
    		font-size: 24px;
    	}
    	.fl {
			float: left;
/* 			border: 1px solid #f00; */
		}
		.fr {
			float: right;
		}
		.clearfix:after{
		    content: ""; 
		    display: block; 
		    height: 0; 
		    clear: both; 
		    visibility: hidden;
		    font-size: 0;
		}
		.clearfix {
		    /* 触发 hasLayout */ 
		    zoom: 1; 
		}
		.imgOrder {
			display: block;
			width: 220px;
			height: 60px;
		}
		.orderImg {
			position: relative;
			top: -32pt;
			left: -25px;
			display: inline-block;
			width: 220px;
			height: 28.45px;
			text-align: center;
		}
		.margin {
			margin: 0 10pt;
		}
		.shippingName {
			position: relative;
			top: -30pt;
			left: -25pt;
			font-size: 15pt;
		}
		.sendMsg {
			position: relative;
			top: 75pt;
			left: 0;
			z-index: 999;
		}
		.goodsMsg {
			position: relative;
			top: 65pt;
		}
		span {
			display: inline-block;
		}
		.batchPickMsg {
			position: relative;
			top: 463pt;
			left: -25px;
		}
		.batchPickImg {
			text-align: center;
		}
		.imgBatchPick {
			display: block;
			width: 220px;
			height: 60px;
		}
		.shopLogo {
			position: relative;
			left: 0;
			top: 0;
			width: 120px;
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
						<li class="orderPage clearfix">
							<p class="fr">${order.order_num} - ${order.sequence_no}</p>
						</li>
						<li class="orderMsg clearfix">
							<div class="orderNum fl">订单号(SN):${order.order_id}</div>
							<div class="margin fl">|</div>
							<div class="otherOderNum fl">平台订单号:${order.shop_order_sn}</div>
							<div class="orderImg fr">
								<img class="imgOrder" src="../common/barcode/generate?barcode=${order.order_id}&text=0">
								<span>${order.order_id}</span>
							</div>
							<div class="shippingName fr">${order.shipping_name}</div>
						</li>
						<li class="sendMsg">
							<div class="sendMsgItem">
								<span>地址:</span>
								<span>[${order.province}][${order.city}][${order.district}]${order.address}</span>
							</div>
							<div class="sendMsgItem">
								<span>邮编:</span>
								<span>${order.postal_code}</span>
							</div>
							<div class="sendMsgItem">
								<span>姓名:</span>
								<span>${order.receive_name}</span>
							</div>
							<div class="sendMsgItem">
								<span>电话:</span>
								<span>${order.mobile_number}</span>
							</div>
							<div class="sendMsgItem">
								<span>备注:</span>
								<span></span>
							</div>
							<div class="sendMsgItem">
								<span>批拣单号:</span>
								<span>${order.batch_pick_sn}</span>
							</div>
						</li>
						<c:if test="${not empty order.shop_logo_filename}">
							<img class="shopLogo" src="${pageContext.request.contextPath}/static/images/shopLogo/${order.shop_logo_filename}" />
						</c:if>
						<li class="batchPickMsg clearfix">
							<div class="batchPickImg fr">
								<img class="imgBatchPick" src="../common/barcode/generate?barcode=${order.batch_pick_sn}&text=0">
								<div>波次单号:${order.batch_pick_sn}</div>
								<p style="padding:0;margin:0; font-size: 12px; margin: 5px 0 0 0;">
									第<fmt:parseNumber type="number" integerOnly="true" value="${(j.index + 1) / max + 1}" />页,
									共<fmt:formatNumber type="number" value="${fn:length(order.orderGoodsList) / max + 0.49}" pattern="#,###,###,###"></fmt:formatNumber>页
								</p>
							</div>
							<p style="font-size: 18pt; text-align:center; padding-right: 12pt; margin-top: 10pt;">本订单商品共${order.kind_num}sku,${order.all_num}件</p>
						</li>
					</c:if>
					<li class="goodsMsg">
						<span style="width: 6%;">${j.index+1}</span>
						<span style="width: 15%;">${goods.barcode}</span>
						<span style="width: 42%;">${goods.goods_name}</span>
						<span style="width: 9%; word-break:break-all;">${goods.location_kw_barcode}</span>
						<span style="width: 9%;">${goods.goods_number}</span>
						<span style="width: 9%;"></span>
						<span style="width: 10%;"></span>
					</li>
					<c:if test="${(j.index + 1) % max == 0}">
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