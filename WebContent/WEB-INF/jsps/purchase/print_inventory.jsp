<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ page import="java.text.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>打印入库单</title>

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
<body onLoad="window.focus();window.print()">
	<div style="clear:both;" id="all">
			<h2>采购入库单</h2>
			<div style="clear:both;">
				<div id="left">
					<img src="../common/barcode/generate?barcode=${oms_order_sn}">
				</div>
				<div id="right">
					页码：1
					<br/>
					打印时间:<%=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()) %>
					<br/>
					到货时间:${arrive_time}
				</div>
			</div>
			<br/><br/><br/>
			<div style="padding: 10px;">
				跟单人：${action_user}&nbsp;&nbsp;采购单号：${oms_order_sn}&nbsp;&nbsp;供应商：${provider_name}&nbsp;&nbsp;供应商出库单号
			</div>
			
			<table border="1" cellspacing="0" style="margin-top:10px;" id="batchTable">
				<tr>
					<th>编号</th>
					<th>商家编码</th>
					<th>商品名称</th>
					<th>总采购数量</th>
					<th>到货数量</th>
					<th>良品</th>
					<th>不良品</th>
					<th>生产日期</th>
					<th>批次号</th>
					<th>箱数</th>
					<th>备注</th>
				</tr>

				<c:forEach items="${orderGoodsList}" varStatus="i" var="order"> 
					<tr align="center" class="list">
						<td>${i.index+1}</td>
						<td>${order.sku_code}</td>
						<td>${order.goods_name}</td>
						<td>${order.goods_number}</td>
						<td>${order.quantity}</td>
						<td>
							<c:if test="${order.status_id == 'NORMAL'}">
								Y
							</c:if>
						</td>
						<td>
							<c:if test="${order.status_id == 'DEFECTIVE'}">
								Y
							</c:if>
						</td>
						<td>${order.validity}</td>
						<td>${order.batch_sn}</td>
						<td>
							<fmt:formatNumber type="number" pattern="0.00" maxFractionDigits="2" value="${order.quantity/order.spec}" />
						</td>
						<td>${order.note}</td>			
					</tr>
				</c:forEach>
		</table>
	</div>
</body>
</html>