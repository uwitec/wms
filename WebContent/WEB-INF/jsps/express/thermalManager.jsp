<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html>
<head>
<title>热敏资源管理</title>
<meta http-equiv="Content-Type" content="text/html;s charset=utf-8" />
<base href="<%=basePath%>">
<style type="text/css">
	table.detail_table {
		border: 1px solid gray;
		border-collapse:collapse;
	}
	table.detail_table td {
		border: 1px solid gray;
		border-collapse:collapse;
		padding: 4px;
		text-align: center;
	}
	table.detail_table th {
		border: 1px solid gray;
		border-collapse:collapse;
		padding: 4px;
		font-size: 15px;
		text-align: center;
	}
</style>
</head>

<body>
	<div>
	<h3>系统热敏资源（中通）</h3>
		<c:if test="${result=='failure'}"> ${note} </c:if>
		<c:if test="${result=='success'}">
			<table class="detail_table">
				<tr>
					<th>快递方式</th>
					<th>逻辑仓库</th>
					<th>未使用</th>
					<th>使用中</th>
					<th>系统已用</th>
					<th>告知快递公司</th>
					<th>等待回传</th>
					<th>操作</th>
				</tr>
				<c:forEach items="${info}" varStatus="i" var="thermalInfo">
					<tr>
						<td>${thermalInfo.shipping_name}</td>
						<td>${thermalInfo.warehouse_names}</td>
						<td>${thermalInfo.N}</td>
						<td>${thermalInfo.R}</td>
						<td>${thermalInfo.Y}</td>
						<td>${thermalInfo.F}</td>
						<td>${thermalInfo.E}</td>
						<td>
						<input type="button" value="系统拉取" onclick=""/>
						<input type="hidden" name="shipping_app_id" value="${thermalInfo.shipping_app_id}" />
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</div>
	<br/>
	<div>
		<h2>当前系统“热敏”仓库快递组合</h2>
		<!-- 插入/更新 -->
		<div>
		<h3>更改设定</h3>
	<!-- 1. 支持物理仓批量操作
		2.热敏改为非热敏，无特殊要求
		3.非热敏改为热敏，需核实账号密码信息，多个账号不允许批量物理操作 
	 -->
		</div>
		<c:if test="${warehouseShippingThermal!=null}">
		<table class="detail_table">
		<tr>
		<th>快递方式</th>
		<th>系统逻辑仓</th>
		</tr>
		<c:forEach items="${warehouseShippingThermal}" var="thermal">
		<tr>
		<td>${thermal.shipping_name}</td>
		<td>${thermal.warehouse_name}</td>
		</tr>
		</c:forEach>
		</table>
		</c:if>
	</div>
	
</body>
</html>
