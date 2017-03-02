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
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>热敏资源管理</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <style>
    #body-sale-search .lq-form-inline label {
    	width: 5.4em;
    }

    #btn-sale-search {
    	margin-left: 15px;
		top: 40px;
    }

    #page-search {
    	float: right;
    }
    
    #loading {
    	font-size: 32px;
    	color: #fff;
    }
    .noDataTip {
    	/*display: none;*/
    	text-align: center;
    	font-size: 30px;
    	color: #666;
    	margin: 30px 0 0 0;
    }
    </style>
</head>
<body id="body-sale-search">
	<div class="main-container">
		<form method="post" id="form">
		<h3>热敏资源号段使用量（中通，汇通）</h3>
		<table class="lq-table" id="batchTable" style="width:70%;margin:0 0 0 0 ;">
		 	<thead>
				<tr>
					<th width="160px;">热敏账号</th>
					<th>快递方式</th>
					<th>物理仓</th>
					<th>逻辑仓</th>
					<th>未使用量</th>
					<th>待回传量</th>
					<th>回传失败</th>
				</tr>
			</thead> 
			<tbody>
				<c:forEach items="${thermalRepositoryList}" varStatus="i" var="thermal">
					<tr align="center" class="list" >
						<td><input type="hidden" value="${thermal.app_id}"/>${thermal.app_key}</td>
						<td>${thermal.shipping_name}</td>
						<td>${thermal.physical_warehouse_name}</td>
						<td>${thermal.group_warehouse_name}</td>
						<td>${thermal.unused}</td>
						<td>${thermal.waitBack}</td>
						<td>${thermal.error_done}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<br/><br/> 
		<h3>热敏仓库快递</h3>
		<table class="lq-table" id="batchTable" style="width:70%;margin:0 0 0 0 ;">
		 	<thead>
				<tr>
					<th width="160px;">逻辑仓</th>
					<th>快递方式</th>
				</tr>
			</thead> 
			<tbody>
				<c:forEach items="${thermalWarehouseShippingList}" varStatus="i" var="ws">
					<tr align="center" class="list" >
						<td><input type="hidden" value="${ws.warehouse_id}"/>${ws.warehouse_name}</td>
						<td>${ws.shipping_name_str}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</form>
	</div>
</body>
</html>