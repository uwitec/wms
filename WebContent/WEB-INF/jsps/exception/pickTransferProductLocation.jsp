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
		
		$(document).on("click","#updatePlId",function(e){
			e.preventDefault();
			var tr=$(this).parent().parent().find("td");
			var task_id=tr.eq(0).text();
			var customer_id=tr.eq(1).text();
			var goods_number=tr.eq(3).text();
			var pl_id=tr.eq(4).text();
			var new_pl_id=$(this).prev().val();
			
			if(new_pl_id == null || new_pl_id == ""){
				alert("请维护新的pl_id");
				return false;
			}
			$.ajax({
				url : "../exception/transferProductLocation",
				type : "post",
				dataType : "json",
				data : {
					"task_id" : task_id,
					"customer_id" : customer_id,
					"goods_number" : goods_number,
					"pl_id" : pl_id,
					"new_pl_id" : new_pl_id
				},
				success : function(data) {
					if(data.result.result == 'success')
						alert("更新成功");
					else 
						alert(data.note);
					//location.reload();
				},
				error : function(error) {
					that.prop("disabled",false);
				}
			});
		})
		
		$(document).on("click","#deleteOrder",function(e){
			e.preventDefault();
			var tr=$(this).parent().parent().find("td");
			var order_id=tr.eq(0).text();
			var batch_pick_id=tr.eq(2).text();
			
			if(order_id == null || order_id == ""){
				alert("请维护新的pl_id");
				return false;
			}
			alert(order_id+batch_pick_id);
			$.ajax({
				url : "../exception/deleteOrder",
				type : "post",
				dataType : "json",
				data : {
					"order_id" : order_id,
					"batch_pick_id" : batch_pick_id,
				},
				success : function(data) {
					if(data.result.result == 'success')
						alert("更新成功");
					else 
						alert(data.note);
					//location.reload();
				},
				error : function(error) {
					that.prop("disabled",false);
				}
			});
		})
		
		$(document).ready(function(){
			
		  	$("#batch_pick_sn").on("keyup",function(e){
                e.preventDefault();
                if (e.which == 13 || e.which == 17) {
                    calldoQueryOrdersApi($(this));
                }
            });
			  
		  	function calldoQueryOrdersApi (element) {
				var batch_pick_sn = $('#batch_pick_sn').val();
				
				if( batch_pick_sn == ""){
					alert("波次单号不能为空！");
					return false;
				}
				
				$.ajax({
					url : "../exception/showTransferProductLocation",
					type : "post",
					dataType : "json",
					data : {
						"batch_pick_sn" : batch_pick_sn
					},
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
						//that.prop("disabled",true);
					},
					success : function(data) {
						window.setTimeout(function(){
							$(".mw-loading").fadeOut(300);
							tableConstructor(data);
						},300);
					},
					error : function(error) {
						window.setTimeout(function(){
							$(".mw-loading").fadeOut(300);
							//that.prop("disabled",false);
							alert("操作失败，请刷新!");
							console.log(error);
						},300);
						that.prop("disabled",false);
					}
				});
		  	}
		  	
			$("#btn-order-search").click(function(){
				var that = $(this);
				var batch_pick_sn = $('#batch_pick_sn').val();
				
				if( batch_pick_sn == ""){
					alert("波次单号不能为空！");
					return false;
				}
				$.ajax({
					url : "../exception/showTransferProductLocation",
					type : "post",
					dataType : "json",
					data : {
						"batch_pick_sn" : batch_pick_sn
					},
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
						//that.prop("disabled",true);
					},
					success : function(data) {
						window.setTimeout(function(){
							$(".mw-loading").fadeOut(300);
							tableConstructor(data);
						},300);
					},
					error : function(error) {
						window.setTimeout(function(){
							$(".mw-loading").fadeOut(300);
							//that.prop("disabled",false);
							alert("操作失败，请刷新!");
							console.log(error);
						},300);
						that.prop("disabled",false);
					}
				});
		  		
			});

		});
		
		
		function tableConstructor(data) {
			var data1 = data.pickGoodsList;
			var data2 = data.pickOrderList;
			if (data1.length) {
				var tableHtml = '';
				for (var key in data1) {
					tableHtml += '<tr align="center" class="list"><td class="task_id">' + data1[key].task_id + '</td>';
					tableHtml += '<td>' + data1[key].customer_id + '</td>';
					tableHtml += '<td>' + data1[key].product_id + '</td>';
					tableHtml += '<td>' + data1[key].goods_number + '</td>';
					tableHtml += '<td class="pl_id">' + data1[key].pl_id + '</td>';
					tableHtml += '<td>' + data1[key].qty_total + '</td>';
					tableHtml += '<td>' + data1[key].qty_available + '</td>';
					if(data1[key].goods_number > data1[key].qty_total){
						tableHtml += '<td><input type="text" class="new_pl_id" class="lq-form-control" />';
						tableHtml += '<input type="button" id="updatePlId" class="lq-btn lq-btn-sm lq-btn-primary" value="修改" /></td></tr>';
					}else{
						tableHtml += '<td></td></tr>';
					}
				}
				for (var key in data2) {
					tableHtml += '<tr align="center" class="list"><td class="order_id">' + data2[key].order_id + '</td>';
					tableHtml += '<td>' + data2[key].status + '</td>';
					tableHtml += '<td class="batch_pick_id">' + data2[key].batch_pick_id + '</td>';
					tableHtml += '<td><input type="button" id="deleteOrder" class="lq-btn lq-btn-sm lq-btn-primary" value="踢出订单" /></td></tr>';
				}
				//alert(tableHtml);
				$("#sale-list-table tbody").html(tableHtml);
				$("#sale-list-table").fadeIn(300);
			} else {
				var tableHtml = '<tr><td colspan="8">未找到订单数据</td></tr>';
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
						<label for="batch_pick_sn">波次单号:</label>
						<input type="text"  name="batch_pick_sn" id="batch_pick_sn" required="required" class="lq-form-control">
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
	 	<thead>
			<tr>
				<th>任务ID</th>
				<th>货主ID</th>
				<th>商品ID</th>
				<th>所需数量</th>
				<th>plId</th>
				<th>库存总量</th>
				<th>库存可用量</th>
				<th>新plId</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
	</div>
	
	
</body>
</html>