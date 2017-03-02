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
    <title>订单监控</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <style>
/*     #body-sale-search .lq-form-inline label {
    	width: 5.4em;
    } */

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
    .moreLineItem {
    	display: inline-block;
    }
    input[type=text] {
    	height: 30px;
    }
    .col-sm-2 {
    	display: inline-block;
    }
    
    .toDeal {
    	background: #C24557;
    	color: #F7FDFD;
    	border: none;
    }
    .reserved{
    	background: #017BBE;
    	color: #fff;
    }
    .picking {
    	background: #f2ec98;
    	color: #724629;
    }
    .rechecked,.weighed,.picking,.toDeal,.reserved{
    	background: #e02128;
    	color: #fff;
    }
    .weighed{
    }
    </style>
    <script type="text/javascript">
    function ToUrl(pageNo)   
	{   
    	var start = $('#start').val();
    	var end = $('#end').val();
    	
    	var data ="";
		
    	if(start != '' && start != null){
    		data += ("start="+start);
    	}else{
    		alert("起始时间必须填写！");
    	}
    	
    	if(end != '' && end != null ){
    		data += ("&end="+end);
    	}
    	
		window.location.href= "../search/orderTaskToDoSearch?"+data;
	}  
  
    </script>
</head>
<body id="body-sale-search">
	<div class="main-container">
		<form method="post" action="../search/orderTaskToDoSearch" class="lq-form-inline">
			<div class="lq-col-6" style="margin: 20px;">
				<div class="buyTime moreLineItem">
					<label class="col-sm-1 control-label" for="start">订单时间：</label> 
					<%-- <select name="time_type" id="time_type" class="lq-form-control">
						<option value="order_time" <c:if test="${time_type == 'order_time'}">selected="selected"</c:if>>订单时间</option>
						<option value="pay_time" <c:if test="${time_type == 'pay_time'}">selected="selected"</c:if>>付款时间</option>
					</select> --%>
					
			      	<div class="col-sm-2">
			         	<input class="form-control form_datetime" id="start" type="text" name="start" value="${start}" size="16"/>
			      	</div>
			      	<label class="col-sm-1 control-label" for="end">至</label>
			      	<div class="col-sm-2">
			         	<input class="form-control form_datetime" id="end" type="text" name="end" value="${end}" size="16"/>
			      	</div>

			      	<button class="lq-btn lq-btn-sm lq-btn-primary searchBtn">
			      		查询
			      	</button>
			    	<input class="form-control"  name="act" type="hidden" value="search"/>
			    </div>
			</div>
		</form>
		<div style="clear:both"></div>
		<form method="post" id="form">
		
		<c:if test="${not empty totalMap}">
		<c:if test="${not empty replenishmentList}">
		<div>
		<table class="lq-table"  style="width:40%;margin-bottom:10px;">
		 	<thead>
				<tr>
					<th width="160px;">货主</th>
					<th>一般补货任务</th>
					<th>紧急补货任务</th>
				</tr>
			</thead> 
			<tbody>
				<c:forEach items="${replenishmentList}" varStatus="i" var="task">
					<tr align="center" class="list" >
						<td>${task.name}</td>
						<td>${task.general_rep}</td>
						<td>${task.hurry_rep}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</div>
		</c:if>
		<br/>
		<table class="lq-table" id="batchTable" style="width:90%;margin:0 0 0 0 ;">
		 	<thead>
				<tr>
					<th width="160px;">货主</th>
					<th>总订单量</th>
					<th>总发货量</th>
					<th>当日已发货</th>
					<th>已取消</th>
					<th>未处理</th>
					<th>已分配</th>
					<th>拣货中</th>
					<th>已复核</th>
					<th>已称重</th>
					<th>未发货量</th>
				</tr>
			</thead> 
			<tbody>
				<tr align="center" class="list" style="background: yellow;">
					<td>总计</td>
					<td>${totalMap.ALL_ORDER}</td>
					<td>${totalMap.ALL_FULFILLED}</td>
					<td>${totalMap.FULFILLED}</td>
					<td>${totalMap.CANCEL}</td>
					<td class="toDeal">${totalMap.ACCPET}</td>
					<td class="reserved">${totalMap.BATCH_PICK}</td>
					<td class="picking">${totalMap.PICKING}</td>
					<td class="rechecked">${totalMap.RECHECKED}</td>
					<td class="weighed">${totalMap.WEIGHED}</td>
					<td>${totalMap.TODO}</td>
				</tr>
				<c:forEach items="${orderToDoList}" varStatus="i" var="orders">
					<tr align="center" class="list" >
						<td>${orders.name}</td>
						<td>${orders.ALL_ORDER}</td>
						<td>${orders.ALL_FULFILLED}</td>
						<td>${orders.FULFILLED}</td>
						<td>${orders.CANCEL}</td>
						<td class="toDeal">${orders.ACCPET}</td>
						<td class="reserved">${orders.BATCH_PICK}</td>
						<td class="picking">${orders.PICKING}</td>
						<td class="rechecked">${orders.RECHECKED}</td>
						<td class="weighed">${orders.WEIGHED}</td>
						<td>${orders.TODO}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</c:if>
		<c:if test="${empty totalMap}">
		<br/>
			<div>当前查询拥挤排队中，请稍后再试！</div>
		</c:if>
		</form>
		
		</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript">
	$(".form-control").on("focus",function(){
		$("th.prev span").text("<");
		$("th.next span").text(">");
	});

 	$('.form_datetime').datetimepicker({
 	    //language:  'fr',
 	    language: 'zh',
 	    weekStart: 1,
 	    todayBtn:  1,
 		autoclose: 1,
 		todayHighlight: 1,
 		startView: 2,
 		forceParse: 0,
 	    showMeridian: 1,
 	    minView: 'month',
 	    format: 'yyyy-mm-dd'
 	});
 	$('.form_date').datetimepicker({
 		format:'yyyy-mm-dd',
 	    language:  'zh',
 	    weekStart: 1,
 	    todayBtn:  1,
 		autoclose: 1,
 		todayHighlight: 1,
 		startView: 2,
 		minView: 2,
 		forceParse: 0
 	});
 	$('.form_time').datetimepicker({
 	    language:  'zh',
 	    weekStart: 1,
 	    todayBtn:  1,
 		autoclose: 1,
 		todayHighlight: 1,
 		startView: 1,
 		minView: 0,
 		maxView: 1,
 		forceParse: 0
 	});
	</script>
</body>
</html>