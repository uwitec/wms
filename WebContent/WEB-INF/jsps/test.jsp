<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>标签</title>
<link href="<%=request.getContextPath() %>/static/css/default.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/autocomplete.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="../static/js/jquery/jquery.js"></script>
<script type="text/javascript" src="../static/js/jquery/jquery.ajaxQueue.js"></script>
<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
</head>
<body>


		
		<div style="clear:both;">
			<img src="../common/barcode/generate?barcode=C2016031400001">
			<br/>
			<!-- 
			<img src="../purchase/barcode?barcode=2016031400001">
			 -->
			<br/>
			<table class="bWindow" style="margin-top:10px;" id="batchTable">
				<tr>
					<td width="25%">商品名称：</td><td>AAA</td>
					<td width="25%">数量</td><td>100</td>
				</tr>
				<tr>
					<td>生产日期：</td><td>2016-03-14</td>
					<td>商品状态：</td><td>良品</td>
				</tr>
				<tr>
					<td>打印时间：</td><td>2016-03-14 20:0:00</td>
					<td>验收员：</td><td>leqee</td>
				</tr>
			</table>
		</div>

</body>
</html>