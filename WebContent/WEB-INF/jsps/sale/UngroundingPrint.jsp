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
	body,html {
		width: 100%;
		padding: 0;
		margin: 0;
	}
    </style>
</head>
<body onLoad="window.print()">
	<c:if test="${result == 'success'}">
	<c:forEach items="${printList}" varStatus="i" var="result1">
		<div class="one-row">
			<div class="label">标签条码:</div>
			<img src="../common/barcode/generate?barcode=${result1.location_barcode}" class="barcode">
		</div>
		<div class="one-row">
			<div class="label">商品条码:</div>
			<img src="../common/barcode/generate?barcode=${result1.barcode}" class="barcode">
		</div>
		<div class="one-row">
			<p>商品名称:${result1.product_name}</p>
		</div>
		<div class="one-row">
			<p>预打包时间:${result1.create_time}</p>
		</div>
		<div class="one-row">
			<p>数量:${result1.total_number}</p>
		</div>
		 
		<div class="one-row">
			<p>包装员:${result1.username}</p>
		</div>
		<c:if test="${(i.index + 1) % 1 == 0}">
		    <c:set var="current" value="${current+1}"></c:set>
			<c:if test="${fn:length(printList) != i.index+1}">
				<div style="page-break-after: always;"></div>
			</c:if>
		</c:if>
	</c:forEach>
		
	</c:if>
	<c:if test="${result != 'success'}">
		<p>${note}</p>
	</c:if>
</body>
</html>