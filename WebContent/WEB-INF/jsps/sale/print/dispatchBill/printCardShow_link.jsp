<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>打印发货单</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link href="<%=request.getContextPath() %>/static/css/autocomplete.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="../static/js/jquery/jquery.js"></script>
	<script type="text/javascript" src="../static/js/jquery/jquery.ajaxQueue.js"></script>
	<script type="text/javascript" src="../static/js/autocomplete.js"></script>
	
	
	 <style>
    #body-order-search .lq-form-inline label {
    	width: 5.4em;
    }

    #btn-order-search {
    	margin-left: 15px;
    }
    .mw-loading {
    	background-color: transparent;
    }

    .mc-loading {
    	position: absolute;
    	top: 20%;
    	left: 50%;
    	width: 100px;
    	height: 100px;
    	margin-left: -50px;
    	text-align: center;
    	padding-top: 25px;
    	background-color: rgba(0,0,0,.5);
    	border-radius: 5px;
    }
  
    #loading {
    	font-size: 32px;
    	color: #fff;
    }
    #print{
	  width: 0px;
	  height: 0px;
	  border: 0px;
	}
    </style>
	<script type="text/javascript">
		
		function ToUrl(x)   
		{   
  			 $('#print').attr('src',x); 
		}  
	</script>
</head>
<body>
<iframe id="print" src=""></iframe>
	<div class="modal-wrap mw-loading">
		<div class="modal-content mc-loading">
			<i class="fa fa-spinner fa-spin" id="loading"></i>
		</div>
	</div>
	<div class="main-container">
		
		<div id="head">
			<span>波次单号：</span>
			<span>${batch_pick_sn}</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<c:forEach items="${pickOrderList}" varStatus="i" begin="0" end ="0" var="order">
					<span>打印次数：</span>
					<span>${order.print_count}</span>
			</c:forEach>
		</div>
		<table class="lq-table" id="sale-list-table">
		 	<thead>
				<tr>
					<th>订单号</th>
<!-- 					<th>操作</th> -->
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${pickOrderList}" varStatus="i" var="order">
					<tr>
						<td><a target="_blank" href ="../sale/detail?order_id=${order.order_id}"/>${order.order_id}</td>
<%-- 						<c:if test="${order.order_status == 'PICKING'}">
							<td><a href="javascript:void(0);" onclick="ToUrl('../sale/print_card?order_id=${order.order_id}&batch_pick_sn=${batch_pick_sn}&batch_pick_id=${batch_pick_id}')">打印发货单</a></td>
						</c:if>
						<c:if test="${order.order_status == 'CANCEL'}">
							<td>此订单已取消</td>
						</c:if>
						<c:if test="${order.order_status != 'CANCEL' && order.order_status != 'PICKING'}">
							<td>此订单非拣货状态</td>
						</c:if> --%>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>