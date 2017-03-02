<%@ page language="java" import="java.util.*" pageEncoding="utf8"
	isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>



<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>库存详情</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/zapatec.js"></script>
<script language="javascript" type="text/javascript"
	src="../static/My97DatePicker/WdatePicker.js"></script>
  <link href="styles/default.css" rel="stylesheet" type="text/css">

<style>
body {
	font-family: "微软雅黑";
	font-size: 14px;
}

table td, table th {
	border: solid 1px #B3EE3A;
	text-align: center;
	font-size: 12px;
}
table th{
	font-size:14px;
	color:white;
	background-color:#0099cc;
}
table{width:100%;}
button {
	background-color: #0099cc;
	color: white;
	padding: 5px 5px 5px 5px;
}
</style>
</head>
<body>
<div style="width:100%;height:50px;background-color:#0099cc;">
		<img src="../static/images/logo.png" style="float:left;margin-left:10px;"/>
</div>
<form action="../inventorySummerQuery/query" >
		<div style="width: 100%; height: 50px;margin-top:20px;padding-top:10px;">
			商品ID: <input type="text" value="${product_id}" name="product_id"/>
			<select name="warehouse_id" id="warehouse_id" class="lq-form-control"> 
			<option value="">--请选择--</option> 
			<c:forEach items="${warehouseList}" var="warehouse"> 
			<option name="warehouse_id"  value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option> 
			</c:forEach> 
			</select> 
			商品状态：<input type="text" id='status' name='status' value="${status}"  />
			<input type="submit" value="查询" />
		</div>
	</form>
<div style="width:100%; margin:0 auto;">
<table class="bWindow"style="text-align: center; margin-top: 10px;">
	<tr align="center">
		<th width="100">预定表id</th>
		<th width="100">商品状态</th>
	  	<th width="100">仓库ID</th>
	  	<th width="100">商品ID</th>
	  	<th width="100">业务组ID</th>
	  	<th width="100">实际总库存</th>
	  	<th width="100">可预订库存</th>
	  	<th width="100">预定开始日期</th>
	  	<th width="100">预定截止日期</th>
		
	</tr>
	<c:forEach var="inventorySummary" items="${inventorySummaryList}">
	<tr align="center">
		<td>${inventorySummary.inventory_summary_id}</td>
		<td>${inventorySummary.status_id}</td>
		<td>${inventorySummary.warehouse_id}</td>
		<td>${inventorySummary.product_id}</td>
		<td>${inventorySummary.customer_id}</td>
		<td>${inventorySummary.stock_quantity}</td>
		<td>${inventorySummary.available_to_reserved}</td>
		<td><fmt:formatDate value='${inventorySummary.created_stamp}'
			pattern='yyyy-MM-dd HH:mm:ss' /></td>
		<td><fmt:formatDate value='${inventorySummary.last_updated_stamp}'
			pattern='yyyy-MM-dd HH:mm:ss' /></td>
	</tr>
	
	</c:forEach>
	<tr align="center">
		<th width="100">订单商品id</th>
		<th width="100">订单号</th>
	  	<th width="100">仓库ID</th>
	  	<th width="100">商品ID</th>
	  	<th width="100">商品数量</th>
	  	<th width="100">预订数量</th>
	  	<th width="100">商品状态</th>
	  	<th width="100">预定状态</th>
	  	<th width="100">预定日期</th>
	  	<th width="100">创建日期</th>
		
	</tr>
	
	<c:forEach var="object" items="${orderReserveDetailList}">
	<tr align="center">
		<td>${object.order_goods_id}</td>
		<td>${object.order_id}</td>
		<td>${object.warehouse_id}</td>
		<td>${object.product_id}</td>
		<td>${object.goods_number}</td>
		<td>${object.reserved_number}</td>
		<td>${object.inventory_status}</td>
		<td>${object.status}</td>
		<td><fmt:formatDate value='${object.reserved_time}'
			pattern='yyyy-MM-dd HH:mm:ss' /></td>
		<td><fmt:formatDate value='${object.created_time}'
			pattern='yyyy-MM-dd HH:mm:ss' /></td>
	</tr>
 </c:forEach>
 
<table>
</div>

<div style="width: 100%; text-align: center; margin-top: 10px;">
		<c:if test="${pageNo > 1 }">
			<a href="../inventorySummerQuery/query?pageNo=${pageNo-1 }&&product_id=${product_id}&&warehouse_id=${warehouse_id}"><button>&lt;&lt;上一页</button></a>
		</c:if>
		<form action="../inventorySummerQuery/query" onSubmit="return beforeSubmit(this);">
		<input type="text" style="width:20px;" name="pageNo" id="pageNo" value="${pageNo}" />
		<input type="hidden" value="${product_id}" name="product_id"/>
		<input type="hidden" value="${warehouse_id}" name="warehouse_id"/>
		<input type="submit" value="go"/>
		</form>
		<a href="../inventorySummerQuery/query?pageNo=${pageNo+1 }&&product_id=${product_id}&&warehouse_id=${warehouse_id}"><button>&gt;&gt;下一页</button></a>
	</div>



</body>
</html>