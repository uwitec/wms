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
    <title>线下面单号段维护与导出</title>
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
<h2 style="color: red;">此页面谨慎操作，无后路可退！</h2>
<div style="margin: 10px 0 0 10px;">
<h3> 根据起止(纯数字)单号插入线下号段</h3>
    <span>物理仓ID：<input type="text" id="physical_warehouse_id"> </span> <br/><br/>
	<span> 快递CODE:<input type="text" id="shipping_code"></span> <br/><br/>
    <span>起始面单号（包含）:<input type="text" id="from_tracking_number"></span><br/><br/> 
	<span> 终止面单号（包含）:<input type="text" id="to_tracking_number"></span><br/><br/>
    <button id="addFromToTN">提交</button><br/>
</div>
<br/><br/>
<div style="margin: 10px 0 0 10px;">
<h3>线下导出号段更新回传</h3>
    <span>物理仓ID：<input type="text" id="physicalWarehouseId"> </span> <br/><br/>
   <span> 快递CODE:<input type="text" id="shippingCode"></span> <br/><br/>
    <button id="exportTNsForBack">导出</button><br/>
</div>

	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script>
		$(document).ready(function(){
			$("#addFromToTN").on("click",function(e){
				e.preventDefault();
				var physical_warehouse_id = $.trim($('#physical_warehouse_id').val());
				var shipping_code = $.trim($('#shipping_code').val());
				var from_tracking_number = $.trim($('#from_tracking_number').val());
				var to_tracking_number = $.trim($('#to_tracking_number').val());
				var searchData = "physical_warehouse_id="+physical_warehouse_id
				+"&shipping_code="+shipping_code
				+"&from_tracking_number="+from_tracking_number
				+"&to_tracking_number="+to_tracking_number;
				$.ajax({
					url : "addFromToTN",
					type : "get",
					dataType : "json",
					data : searchData,
					beforeSend : function () {
						$('#addFromToTN').attr('disabled',true);
	                },
					success : function(data) {
						console.log(data);
						alert(data.note);
						$('#physical_warehouse_id').val("");
						$('#shipping_code').val("");
						$('#from_tracking_number').val("");
						$('#to_tracking_number').val("");
						$('#addFromToTN').attr('disabled',false);
					},
					error : function(error) {
						alert("super/addFromToTN error");
					}
				});
			});
			
			$("#exportTNsForBack").on("click",function(e){
				e.preventDefault();
				var physical_warehouse_id = $.trim($('#physicalWarehouseId').val());
				var shipping_code = $.trim($('#shippingCode').val());
				var searchData = "physical_warehouse_id="+physical_warehouse_id
				+"&shipping_code="+shipping_code;
				$('#exportTNsForBack').attr('disabled',true);
				window.location = "exportTNsForBack?"+searchData;
			});
			
		});
	</script>
</body>
</html>