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
    <title>订单查询</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style>
	#print{
	        border: 0px;
	        width: 0px;
	        height: 0px;
	    }
    </style>
</head>
<body id="body-sale-search">
	<!-- iframe打印面单 -->
    <iframe  src=""  id="print"></iframe>
    <input type="text" id="id">
    <button id="dayin">打印</button>


	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script>
		$(document).ready(function(){
			$("#dayin").on("click",function(e){
				e.preventDefault();
				var orderId = $.trim($('#id').val());
				src = "print?order_id="+orderId;
				$('#print').attr('src',src);
			});
		});
	</script>
</body>
</html>