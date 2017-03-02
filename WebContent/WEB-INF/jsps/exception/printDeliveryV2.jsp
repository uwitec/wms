<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>补打印面单</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	
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
	table#sale-list-table {
	  width: 50%;
	}
    </style>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#btn-order-search").click(function(){
				var that = $(this);
 				var order_id =$.trim($('#order_id').val());
 				var tracking_number =$.trim($('#tracking_number').val());
				
				if( order_id == "" && tracking_number == ""){
					alert("订单号或面单号不能同时为空！");
					return false;
				}
				orderId2Number = order_id + '_' + tracking_number;
				
				
		  		$.ajax({
					url : "../exception/print_delivery",
					type : "post",
					dataType : "json",
					data : {
						'orderId2Number':orderId2Number
					},
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
					},
					success : function(data) {
						console.log(data);
						window.setTimeout(function(){
							$(".mw-loading").fadeOut(300);
							if(data.result == 'success'){
								tableConstructor(data);
							}else
								alert(data.note);
						},300);
					},
					error : function(error) {
						window.setTimeout(function(){
							$(".mw-loading").fadeOut(300);
							alert("操作失败，请刷新!");
							console.log(error);
						},300);
						that.prop("disabled",false);
					}
				});
		  		
			});
			
		});
		
		function batch_print(){
			var order_id = $('#order_id_').val();
			src="../sale/print?order_id="+order_id;
	  		$('#print').attr('src',src); 
	  		$(".mw-loading").fadeOut(100);
		}
		
		function tableConstructor(data) {
			order_id = data.order_id;
			sum = data.sum;
			shipping_id = data.shipping_id;
			var shipping_name = data.shipping_name;
			data = data.print_info;
			
			
			//data = data.shipment_tracking_numbers;
			//if (data.length) {
				var tableHtml = '<div style="margin: 10px;"><span>订单号：'+order_id+'&nbsp;&nbsp;包裹数量：'+sum+'</span><input type="hidden" id="order_id_" value="'+order_id+'"/>';
				if(sum > 1)
					tableHtml += "<input type='button' style='width:inherit;' class='lq-form-control' onclick='batch_print()' value='批量打印'/>";
				tableHtml += '</div><br/><tr><th>快递方式</th><th>快递单号</th><th>操作</th></tr>';
				for (var key in data) {
					var tracking_number = data[key].tracking_number;
					var orderId2Number = order_id + '_' + tracking_number;
					tableHtml += '<tr><td>' + shipping_name + '</td>';
					tableHtml += '<td>' + tracking_number + '</td>';
					tableHtml +='<td><a href="javascript:void(0);" onclick=ToUrl(';
					tableHtml += "'../exception/print?orderId2Number="+orderId2Number+"')>打印快递面单</a></td></tr> ";
				}
				//alert(tableHtml);
				$("#sale-list-table tbody").html(tableHtml);
				$("#sale-list-table").fadeIn(300);
				/*
			} else {
				var tableHtml = '<tr><td colspan="2">未找到订单数据</td></tr>';
				$("#sale-list-table tbody").html(tableHtml);
				$("#sale-list-table").fadeIn(300);
			}*/
		}
		
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
		<div  class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="batch_pick_sn">订单号:</label>
						<input type="text" name="order_id" value="${order_id}" id="order_id" class="lq-form-control">
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="batch_pick_sn">快递单号:</label>
						<input type="text" name="tracking_number" value="${tracking_number}" id="tracking_number" class="lq-form-control">
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
		
		<table class="lq-table" id="sale-list-table">
			<tbody>
			</tbody>
		</table>
	</div>
</body>
</html>