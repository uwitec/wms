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
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>乐其WMS - 订单详情页面</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style>
	.order-title {
		color: #337ab7;
	}
	</style>
</head>
<body>
	<div class="main-container">
		<div class="lq-row">
			<div class="lq-col-8">
				<div class="lq-panel lq-panel-primary" id="orderinfo-container">
					<div class="lq-panel-heading">
						<h2><i class="fa fa-th"></i>订单信息</h2>
					</div>
					<div class="lq-panel-body">
						<c:forEach items="${orderDetial}" varStatus="i" var="order">
						<div class="lq-row">
							<div class="lq-col-4">
								<p>平台: <span>${order.order_source}</span></p>
								<p>订单号: <span>${order.order_id}
									<c:if test="${order.is2B == 'Y'}">
										(toB)
									</c:if>
									<c:if test="${order.is2B == 'X'}">
										(toX)
									</c:if>
								</span></p>
								<p>OMS订单号: <span>${order.oms_order_sn}</span></p>
								<p>店铺: <span>${order.shop_name}</span></p>
							</div>
							<div class="lq-col-4">
								<p>订单状态: <span>${order.order_status}</span></p>
								<p>快递方式: <span>${order.shipping_name}</span></p>
								<p>发货仓库: <span>${order.warehouse_name}</span></p>
								<p>预定状态: 
								<span>
									<c:if test="${order.is_reserved == 'Y'}">
										预定成功
									</c:if>
									<c:if test="${order.is_reserved == 'N'}">
										未预定
									</c:if>
									<c:if test="${order.is_reserved == 'E'}">
										预定失败
									</c:if>
									<c:if test="${order.order_status == '取消'}">
										已取消
									</c:if>
									<c:if test="${order.order_status != '取消' and order.is_reserved == 'F'}">
										已发货
									</c:if>
								</span></p>
							</div>
							<div class="lq-col-4">
								<p>收件人: <span>${order.receive_name}</span></p>
								<p>联系方式:
								<c:if test="${order.phone_number !='' && order.phone_number != null}">
									${order.phone_number} /
								</c:if> 
								${order.mobile_number}</span></p>
								<p>收货地址: <span>${order.province_name} ${order.city_name} ${order.district_name} ${order.shipping_address}</span></p>
							</div>
						</div>
						</c:forEach>
					</div>
				</div>

				<div class="lq-panel lq-panel-primary" id="goods-container">
					<div class="lq-panel-heading">
						<h2><i class="fa fa-shopping-bag"></i>商品明细</h2>
					</div>
					<div class="lq-panel-body">	
					<c:forEach items="${orderGoodsDetial}" varStatus="i" var="good">
					    <p class="order-title">
					    	<i class="fa fa-paperclip"></i>
					    	<strong>快递单号: <span>${good.tracking_number}</span></strong>
					    	<strong>重量: <span> 
					    	<c:if test="${good.shipping_wms_weight == '0.0000'}"> 
					    		未称重
					    	</c:if>
					    	<c:if test="${good.shipping_wms_weight != '0.0000'}">
					    		${good.shipping_wms_weight}kg
					    	</c:if>
					    	
					    	</span></strong>
					    </p>
						<table class="lq-table">
							<thead>
								<tr>
									<th>商品名称</th>
									<th>商品条码</th>
									<th>商品数量</th>
									<th>库存总量</th>
									<th>库存可用量</th>
									<th>单价</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach items="${good.order_goods}" varStatus="i" var="g">
							<c:if test="${g.goods_barcode != 'WMS888888888'}">
								<tr>
									<td>${g.goods_name}</td>
									<td>${g.goods_barcode}</td>
									<td>${g.goods_number}</td>
									<td>${g.quantity}</td>
									<td>${g.available_to_reserved}</td>
									<td><fmt:formatNumber type="number" pattern="0.00" maxFractionDigits="2" value="${g.goods_price}" /></td>
								</tr>
							</c:if>
							</c:forEach>
							</tbody>
						</table>
					</c:forEach>
					</div>
				</div>
			</div>	
			<div class="lq-col-4">
				<div class="lq-panel lq-panel-primary" id="action-container">
					<div class="lq-panel-heading">
						<h2><i class="fa fa-table"></i>操作记录</h2>
					</div>
					<table class="lq-table">
						<thead>
							<tr>
								<th>订单状态</th>
								<th>操作人</th>
								<th>订单时间</th>
								<th>备注</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${orderActionDetial}" varStatus="i" var="action">
								<tr>
									<td>${action.order_status}</td>
									<td>${action.created_user}</td>
									<td>${action.created_time}</td>
									<td>${action.action_note}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
	    </div>
	</div>
</body>
</html>