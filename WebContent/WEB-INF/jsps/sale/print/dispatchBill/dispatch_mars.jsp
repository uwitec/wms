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
	<title></title>
	<style>
		html, body {
			margin: 0;
			padding: 0;
			width: 100%;
			height: 100%;
		}
		.pageMsg {
			position: relative;
			margin: 0 auto;
		}
		ul li {
			list-style: none;
		  /*  	border: 1px solid #f00; */
		}

		li.sendMsg {
			position: relative;
			margin-bottom:160pt;
			/* padding-bottom: 32pt; */
			top: 120pt;
		}
		li.goodsMsg {
			margin-top: 20pt;
			position: relative;
			left: -10pt;
		}
		li.orderMsg {
			/*  padding-bottom: 45pt; */
			position: relative;
			top: 75pt;
		}
		li.orderMsg span,li.sendMsg span {
			display: inline-block;
			margin-right: 15pt;
			min-width: 15%;
		}
		li.goodsMsg span {
			display: inline-block;
			margin-right:16pt;
			text-align: center;
		}
		div.barcode {
			text-align: center;
			width: 25%;
			position: relative;
			top: 15pt;
			margin-right: 10pt;
		}
		.batchPickMsg {
			position: relative;
			top: 315pt;
			right: 0pt;
		}
		.batchPickImg {
			text-align: center;
			position: absolute;
			right: 10pt;
			top: 5pt;
		}
		.orderPage {
    		position: relative;
    		font-size: 20pt;
    	}
    	.figure {
    		position: absolute;
    		top:0;
    		right: 90pt;
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
	</style>
</head>
<body onload="window.print()">
		<c:forEach items="${orderInfoList}" varStatus="i" var="order">
		<%-- <c:set var="max" value="${order.page_line}"></c:set> --%>
 		 <c:set var="max" value="4"></c:set>
		<ul class="pageMsg">
			<c:forEach items="${order.orderGoodsList}" varStatus="j" var="goods">
				<c:if test="${(j.index + 1) % max == 1}">
 					<li class="orderPage clearfix">
						<p class="fr figure">${order.order_num} - ${order.sequence_no}</p>
					</li>
					<li class="orderMsg">
						<span>${order.order_id}</span>
						<span>${order.shop_order_sn}</span>
						<span>${order.shipping_name}</span>
						<div style="display:inline-block; text-align:center;" class="barcode">
							<img class="" src="../common/barcode/generate?barcode=${order.order_id}&text=0" width="100%" height="100%">
							<span>${order.order_id}</span>
						</div>
					</li>
					<li class="sendMsg">
						<span style="position: absolute; top: 20pt;">[${order.province}][${order.city}][${order.district}]${order.address}</span>
						<span style="margin-left:80pt;">${order.postal_code}</span>
						<span>${order.receive_name}</span>
						<span>${order.mobile_number}</span>
						<span>${order.batch_pick_sn}</span>
					</li>
 					<li class="batchPickMsg clearfix">
						<div class="batchPickImg fr">
							<img class="imgBatchPick" src="../common/barcode/generate?barcode=${order.batch_pick_sn}&text=0">
							<div>波次单号:${order.batch_pick_sn}</div>
							<p style="padding:0;margin:0; font-size: 12px; margin: 5px 0 0 0;">
								第<fmt:parseNumber type="number" integerOnly="true" value="${(j.index + 1) / max + 1}" />页,
								共<fmt:formatNumber type="number" value="${fn:length(order.orderGoodsList) / max + 0.49}" pattern="#,###,###,###"></fmt:formatNumber>页
							</p>
						</div>
						<p style="font-size: 18pt; text-align:left;margin-top: 10pt;">本订单商品共${order.kind_num}sku,${order.all_num}件</p>
					</li>
				</c:if>
				<li class="goodsMsg">
					<span style="width: 6%;">${j.index+1}</span>
					<span style="width: 20%; margin-left: 20pt; margin-right: 10pt;">${goods.barcode}</span>
					<span style="width: 25%; margin-right:10pt; font-size: 10px;">${goods.goods_name}</span>
					<span style="width: 12%; text-align:left; margin-right: 20pt;word-break:break-all; font-size: 13px;">${goods.location_kw_barcode}</span>
					<span style="width: 6%;  text-align: right; margin-left: 10pt;">${goods.goods_number}</span>
				</li>
				<c:if test="${(j.index + 1) % max == 0 || fn:length(order.orderGoodsList) == j.index+1}">
						<c:if test="${fn:length(order.orderGoodsList) != j.index+1}">
							<div style="page-break-after: always;"></div>
						</c:if>
					</c:if>
			</c:forEach>
		</ul>
		<c:if test="${fn:length(orderInfoList) != (i.index+1)}">	
			  <div STYLE="page-break-after: always;"></div>	
		</c:if>		
		</c:forEach>
</body>
</html>