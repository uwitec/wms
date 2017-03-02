<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ page import="java.text.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>打印验收单</title>
<link href="<%=request.getContextPath() %>/static/css/default.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/autocomplete.css" rel="stylesheet" type="text/css">

<style type="text/css">
h2 {
  text-align: center;
}
div#all {
  margin: 20px;
}
div#left {
  width: 60%;
  float: left;
}
div#right {
  position: relative;
}
</style>
</head>
<body onLoad="window.print()">
	<c:forEach items="${mapList}" varStatus="i" var="map"> 
		<div style="clear:both;" id="all">
				<c:if test="${i.index == 0}">
					<h2>采购验收单</h2>
					<div style="clear:both;">
						<div id="left">
							<!-- <img src="../common/barcode/generate?barcode=${oms_order_sn}">&nbsp;&nbsp; -->
							<img src="../common/barcode/generate?barcode=${map.batch_order_sn}">
						</div>
						<div id="right">
							页码：${i.index+1}
							<br/>
							打印时间:<%=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()) %>
							<br/>
							到货时间:${map.arrive_time}
							<br/>
							跟单人：${actionUser}
						</div>
					</div>
				</c:if>
			<c:if test="${i.index % 3 == 0 && i.index != 0}">
				<div style="page-break-after: always;"></div>
			</c:if>
				<br/><br/><br/>
				<div>
					${i.index+1}.采购单号:${map.oms_order_sn}&nbsp;&nbsp;供应商:${map.provider_name}&nbsp;&nbsp;供应商出库单号
				</div>
				
				<form method="post" id="form">
					<table border="1" cellspacing="0" style="margin-top:10px;" id="batchTable">
						<c:forEach items="${map.orderGoodsList}" varStatus="j" var="order"> 
							
							<c:if test="${j.index == 0}">
								<div>
									商品条码:${order.barcode}&nbsp;&nbsp;商品名称:${order.goods_name}
								</div>
								<tr>
									<th>采购数量</th>
									<th>待收货数量</th>
									<th>到货数量</th>
									<th>良品</th>
									<th>不良品</th>
									<th>箱容</th>
									<th>参考箱数</th>
									<th>长宽高(cm)</th>
									<th>生产日期</th>
									<th>批次号</th>
									<th>堆叠</th>
									<th>托盘个数</th>
									<th>备注</th>
								</tr>
							</c:if>
			
							<c:if test="${j.index != 0 and order.barcode != last_barcode}">
								</table>
								<br>
								<div>
									商品条码:${order.barcode}&nbsp;&nbsp;商品名称:${order.goods_name}
								</div>
								<table border="1" cellspacing="0" style="margin-top:10px;" id="batchTable">
								<tr>
									<th>采购数量</th>
									<th>待收货数量</th>
									<th>到货数量</th>
									<th>良品</th>
									<th>不良品</th>
									<th>箱容</th>
									<th>参考箱数</th>
									<th>长宽高(cm)</th>
									<th>生产日期</th>
									<th>批次号</th>
									<th>堆叠</th>
									<th>托盘个数</th>
									<th>备注</th>
								</tr>
							</c:if>
							<tr align="center" class="list">
								<td>${order.goods_number}</td>
								<td>${order.not_accept_goods_number}</td>
								<td></td>
								<td>${order.normal_number}</td>
								<td>${order.defective_number}</td>
								<td></td>
								<td></td>
								<td>L:${order.length} W:${order.width} H:${order.height}</td>			
				
								<td></td>
								<td></td>
								<td>${order.tray_number}</td>
								<td></td>
							</tr>
							<c:set value="${order.barcode}" var="last_barcode"></c:set>
						</c:forEach>
					</table>
				</form>
		</div>
	</c:forEach>
</body>
</html>