<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>缓存管理</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link href="${pageContext.request.contextPath}/static/css/autocomplete.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery/jquery.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery/jquery.ajaxQueue.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/autocomplete.js"></script>
	
	
	 <style>
    #body-order-search .lq-form-inline label {
    	width: 5.4em;
    }

    #btn-cache-search {
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
		$(document).ready(function(){
			$("#btn-cache-search").click(function(){
				var that = $(this);
				var cache_name = $('#cache_name').val().trim();
				$.ajax({
					url : "${pageContext.request.contextPath}/cache/search",
					type : "post",
					dataType : "json",
					data:"cache_name="+cache_name,
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
					},
					success : function(data) {
						window.setTimeout(function(){
							$(".mw-loading").fadeOut(300);
							console.log(data.keys);
							tableConstructor(data);
						},300);
					},
					error : function(error) {
						window.setTimeout(function(){
							$(".mw-loading").fadeOut(300);
							alert("操作失败，请刷新!");
							console.log(error);
						},300);
					}
				});
			});
		});
		
		function tableConstructor(data) {
			//console.log(data);
			if (data.keys.length) {
				var tableHtml = '';
				for(var i=0; i< data.keys.length; i++){
					tableHtml += '<tr><td>' + data.keys[i] + '</td><td><a href="${pageContext.request.contextPath}/cache/'+data.cache_name+'/'+data.keys[i]+'/delete" onclick="return confirm(\'sure?\');"';
					tableHtml += '>删除</a></td></tr>';
				}
				//alert(tableHtml);
				$("#sale-list-table tbody").html(tableHtml);
				$("#sale-list-table").fadeIn(300);
			} else {
				var tableHtml = '<tr><td colspan="2">未找到缓存数据</td></tr>';
				$("#sale-list-table tbody").html(tableHtml);
				$("#sale-list-table").fadeIn(300);
			}
		}
		
	</script>
</head>
<body>
	<div class="modal-wrap mw-loading">
		<div class="modal-content mc-loading">
			<i class="fa fa-spinner fa-spin" id="loading"></i>
		</div>
	</div>
	<div class="main-container">
		<div  class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="cache_name">缓存名称:</label>
<!-- 						<input type="text" name="cache_name" id="cache_name" class="lq-form-control"> -->
						<select name="cache_name" id="cache_name" class="lq-form-control">
							<!-- <option label="passwordRetryCache" value="passwordRetryCache" >passwordRetryCache</option>
							<option label="authorizationCache" value="authorizationCache">authorizationCache</option>
							<option label="authenticationCache" value="authenticationCache">authenticationCache</option>
							<option label="shopNameCache" value="shopNameCache">shopNameCache</option>
							<option label="dashboardCache" value="dashboardCache">dashboardCache</option> -->
							<c:forEach items="${cacheList}" var="name">
							    <option label=${name} value=${name}>${name}</option>
					        </c:forEach>
						</select> 
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<button type="button" id="btn-cache-search" class="lq-btn lq-btn-sm lq-btn-primary">
							<i class="fa fa-search"></i>查询
						</button>
					</div>  
				</div>
			</div>
		</div>
		
		<table class="lq-table" id="sale-list-table">
		 	<thead>
				<tr>
					<th>Cache Key</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>
</body>
</html>