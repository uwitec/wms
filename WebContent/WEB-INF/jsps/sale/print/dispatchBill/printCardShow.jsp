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
    <title>打印发货单</title>
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
			$("#btn-order-search").click(function(){
				var that = $(this);
				var batch_pick_sn = $('#batch_pick_id').val();
				var order_id = $('#order_id').val();
				
				if( batch_pick_sn == "" && order_id == ""){
					alert("波次单号和订单号不能同时为空！");
					return false;
				}
				/*
				if(batch_pick_sn == '' || batch_pick_sn == null){
					alert("请填写波次单！");
					return false;
				}*/
				var orderList= new Array();
				orderList.push('F'+batch_pick_sn+'_'+order_id);
				//console.log(saleSearchData);
				$.ajax({
					url : "../sale/search_orders",
					type : "post",
					dataType : "json",
					data : JSON.stringify(orderList),
					contentType:"application/json; charset=utf-8",
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
						//that.prop("disabled",true);
					},
					success : function(data) {
						window.setTimeout(function(){
							$(".mw-loading").fadeOut(300);
							//that.prop("disabled",false);
							console.log(data.pickOrderList);
							var tagList = "";
							var dataList = data.pickOrderList;
							var batch_pick_id2 = "",batch_pick_sn2 = "";
							for (var key in dataList){
								console.log(dataList);
								tagList += dataList[key].order_id;
								tagList += ",";
								batch_pick_sn2 = dataList[key].batch_pick_sn;
								batch_pick_id2 = dataList[key].batch_pick_id;
							}
							tagList = tagList.substring(0,tagList.lastIndexOf(','));
							tableConstructor(data.pickOrderList);
							if(tagList.length){
								if(batch_pick_sn == ""){
									src="../sale/print_card?order_id="+tagList+"&batch_pick_sn="+batch_pick_sn2;
								}else{
									src="../sale/print_card?batch_pick_id="+batch_pick_id2+"&batch_pick_sn="+batch_pick_sn2;
								}
				    			$('#print').attr('src',src); 
							}
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
			//console.log(data);
			if (data.length) {
				var tableHtml = '';
				for (var key in data) {
					var order_id = data[key].order_id;
					var batch_pick_id = data[key].batch_pick_id;
					var batch_pick_sn = data[key].batch_pick_sn;
					var order_status = data[key].order_status;
					tableHtml += '<tr><td>' + data[key].order_id + '</td>';
					if(order_status != 'CANCEL'){
						tableHtml +='<td><a href="javascript:void(0);" onclick=ToUrl(';
						tableHtml += "'../sale/print_card?order_id="+order_id+"&batch_pick_id="+batch_pick_id+"&batch_pick_sn="+batch_pick_sn+"')>打印发货单</a></td></tr> ";
					}else if(order_status == 'CANCEL'){
						tableHtml += '<td>此订单已取消</td></tr>';
					}else{
						tableHtml += '<td>此订单非生成波次单状态</td></tr>';
					}
				}
				//alert(tableHtml);
				$("#sale-list-table tbody").html(tableHtml);
				$("#sale-list-table").fadeIn(300);
			} else {
				var tableHtml = '<tr><td colspan="2">未找到订单数据</td></tr>';
				$("#sale-list-table tbody").html(tableHtml);
				$("#sale-list-table").fadeIn(300);
			}
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
						<label for="batch_pick_id">波次单号:</label>
						<input type="text" name="batch_pick_id" value="${batch_pick_id}" id="batch_pick_id" class="lq-form-control">
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="order_id">订单号:</label>
						<input type="text" name="order_id" value="${order_id}" id="order_id" class="lq-form-control">
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
					<th>订单号</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>
</body>
</html>