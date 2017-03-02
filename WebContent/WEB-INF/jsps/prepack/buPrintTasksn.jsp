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
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>乐其WMS - 打印波次单库存不够转移库位库存</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
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
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript">
		
		$(document).ready(function(){
			
		  	$("#oms_task_sn").on("keyup",function(e){
                e.preventDefault();
                if (e.which == 13 || e.which == 17) {
                    calldoQueryOrdersApi($(this));
                }
            });
			  
		  	function calldoQueryOrdersApi (element) {
				var oms_task_sn = $('#oms_task_sn').val();
				
				if( oms_task_sn == ""){
					alert("oms任务单号不能为空！");
					return false;
				}
				src="../product/buPrintPrepack?oms_task_sn="+oms_task_sn;
		  		$('#print').attr('src',src); 
		  		$(".mw-loading").fadeOut(100);
		  	}
		  	
			$("#btn-order-search").click(function(){
				var that = $(this);
				var oms_task_sn = $('#oms_task_sn').val();
				
				if( oms_task_sn == ""){
					alert("oms任务单号不能为空！");
					return false;
				}
				src="../product/buPrintPrepack?oms_task_sn="+oms_task_sn;
		  		$('#print').attr('src',src); 
		  		$(".mw-loading").fadeOut(100);
			});

		});
		
	
	</script>
</head>
<iframe id="print" src=""></iframe>
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
						<label for="oms_task_sn">oms任务单号:</label>
						<input type="text"  name="oms_task_sn" id="oms_task_sn" required="required" class="lq-form-control">
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<button type="button" id="btn-order-search" class="lq-btn lq-btn-sm lq-btn-primary">
							<i class="fa fa-search"></i>查询
						</button>
						<input type="hidden" name="act" value="search">
					</div>  
				</div>
			</div>
		</div>
		
	</div>
	
	
</body>
</html>