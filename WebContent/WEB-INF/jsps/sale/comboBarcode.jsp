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
	.content {
		text-align: center;
	}
    </style>
</head>
<body onLoad="window.print()">
	<c:forEach var="i" begin="1" end="${preNumber}">
		<div class="content">
			<div class="label" style="font-size: 12pt;text-align:left;margin:15pt 0 6pt 30pt;">套餐名称:${barcodeName}</div>
			<img src="../common/barcode/generate?barcode=${comboBarcode}" >
		</div>
		<c:if test="${i != preNumber}">
			<div STYLE="page-break-after: always;">
			</div>
		</c:if>
	</c:forEach>
</body>
</html>