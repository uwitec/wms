<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>取消未上架查询</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/autocomplete.css" />
	<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">  
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style type="text/css">
		.searchBtn {
			margin: 2px 0 0 5px;
		}
		.item {
			display: inline-block;
			margin: 0 20px 0 0;
		}
		.noDataTip {
	    	display: none;
	    	text-align: center;
	    	font-size: 30px;
	    	color: #666;
	    	margin: 30px 0 0 0;
	    }
		.check {
			margin-right: 10px;
			display: inline-block;
		}
		.btns {
			margin: 10px 0;
		}
		input[type=text]{
			height:30px;
			position:relative;
			top: 3px;
		}
		input[type=text].form_datetime {
			width: 15%;
		}
	</style>
</head>
<body>
	<div class="main-container">
		<input type="hidden" id="orderIdList"/>
		<div class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="customer_id">选择货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
			                <option value="" >--请选择--</option>
		                   	<c:forEach items="${warehouseCustomerList}" var="customer"> 
			                   	<option <c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
							    value="${customer.customer_id}">${customer.name}</option>
							</c:forEach> 
			            </select>
					</div>
					<div class="lq-form-group">
						<label for="location_barcode">库位条码:</label>
						<input type="text" id="location_barcode" class="lq-form-control"/>
					</div>					
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="barcode">商品条码:</label>
						<input type="text" id="barcode" class="lq-form-control" />
					</div>
					<div class="lq-form-group">
						<label for="location_barcode">&nbsp;&nbsp;&nbsp; 订单号:</label>
						<input type="text" id="order_id" class="lq-form-control"/>
					</div>						
				</div>
				<div class="lq-col-6">
					<div class="lq-form-group">
						<label>订单时间:</label>
<%-- 						<input type="text" id="created_time" class="lq-form-control" value="${created_time}" />
						<button id="from_date_trigger" name="from_date_trigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon" >
	    					<i class="fa fa-calendar"></i>
	    				</button> --%>
						<input class="form-control form_datetime lq-form-control" id="created_time" type="text" name="created_time" value="${created_time}" size="12"/>	    				
						 至
<%-- 						<input type="text" id="delivery_time" class="lq-form-control" value="${delivery_time}" />
						<button type="button" id="to_date_trigger" name="to_date_trigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon" >
	               			<i class="fa fa-calendar"></i>
	               		</button> --%>
	               		<input class="form-control form_datetime lq-form-control" id="delivery_time" type="text" name="delivery_time" value="${delivery_time}" size="16"/>	    				
						<button type="button" id="" class="searchBtn lq-btn lq-btn-sm lq-btn-primary">查询</button>
					</div>
					<div class="lq-form-group">
						<label for="warehouse_id" style="text-indent:2rem;">渠道：</label>
						<select name="warehouse_id" id="warehouse_id" style="margin-left:-10px;">
							<c:forEach items="${logicWarehouses}" var="warehouse">
								<option name="warehouse_id" value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
							</c:forEach>
						</select>
						<shiro:hasPermission name="exceptionTransitionLocation:cancel">
							<button type="button" id="cancel" class="lq-btn lq-btn-sm lq-btn-primary" style="margin-left:10px;">批量上架</button>
						 </shiro:hasPermission> 
					</div>
				</div>			
			</div>
		</div>
		<div class="lq-form-group">
			<div class="item">
				<label>订单状态:</label>
				<span class="orderStatus">已取消</span>
			</div>
			<div class="item">
				<label>订单总数:</label>
				<span class="orderSum"></span>
			</div>
			<div class="item">
				<label>商品种类:</label>
				<span class="goodskinds"></span>
			</div>
			<div class="item">
				<label>商品总数:</label>
				<span class="goodsSum"></span>
			</div>
		</div>
		<div class="btns">
      		<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="全选" onclick="select_all('#batchTable');" /> &nbsp;
     		<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="清空" onclick="select_none('#batchTable');" /> &nbsp;
     		<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="反选" onclick="select_reverse('#batchTable');" /> &nbsp;
		</div>	
		<table class="lq-table" id="batchTable">
			<thead>
				<tr>
					<th>货主</th>
					<th>渠道</th>
					<th>订单号</th>
					<th>商品条码</th>
					<th>商品名称</th>
					<th>商品数量</th>
				</tr>
			</thead>
			<tbody class="tbody">
				
			</tbody>
		</table>
		<div class="noDataTip">未查到符合条件的数据!</div>
	</div>
	<script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>  
	<script type="text/javascript" src="../static/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../static/js/bootstrap-datetimepicker.min.js"></script>	
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/v3.js"></script>
 	 <script type="text/javascript">
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
  	</script>
	<script type="text/javascript">
		//全选
		function select_all(node, type)
	    {
	        node = node ? node : document ;
	        $(node).find("input[name='check_batch_pick[]']:enabled").each(function(i){
	    		this.checked = true;
	    	});
	    }
		
		//反选
		function select_reverse(node,type)
		{
			node=node?node:document;
			$(node).find("input[name='check_batch_pick[]']:enabled").each(function(){
				this.checked=!this.checked;
			})
		}
		
		//清空
		function select_none(node,type)
		{
			node=node?node:document;
			$(node).find("input[name='check_batch_pick[]']:enabled").each(function(){
				this.checked=false;
			})
		}
		
		
	    //取消未上架//
	    $("#cancel").click(function(e){
	    	e.preventDefault();
	    	$(this).attr("disabled",true);
	    	var locationBarcode=$("#location_barcode").val();
	    	var orderIds="";
	    	var warehouse_id=$("#warehouse_id").val();
	    	if(locationBarcode=="") {
	    		alert("库位条码不能为空!");
	    		$(this).removeAttr("disabled");
	    		return;
	    	}else {
	    		$("input[name='check_batch_pick[]']").each(function(){
	    			
	    			if(this.checked){
		    			var orderId=$(this).parent().next().next().text();
		    			orderIds+=orderId+",";
	    			}
	    		});
	    		orderIds=orderIds.substring(0,orderIds.length-1);
	    		console.log(orderIds);
	    		if(orderIds=="") {
	    			alert("还未勾选条目!");
	    			$(this).removeAttr("disabled");
	    			return;
	    		}
	    		$.ajax({
	    			url: '../RF/web_grounding_allsubmit',
	    			type: 'post',
	    			dataType: 'json',
	    			data: {
	    				locationBarcode : locationBarcode.split('-').join(''),
	    				orderIdList : orderIds
	    			},
					success: function(res) {
						console.log(res);
						if(res.result=="failure"){
							$("#cancel").removeAttr("disabled");
							alert(res.note);

						}else{
							alert("操作成功!");
						}
					},
					error: function(err) {
						alert(err);
					}
	    		})
	    	}
	    	
	    })

		$(function () {
			// 查询
			$('.searchBtn').click(function () {
				$("#cancel").removeAttr("disabled");
				var orderId=$("#order_id").val();
/* 				 if(orderId==""){
			    		alert("订单号不能为空!");
			    		$(this).removeAttr("disabled");
			    		return;
			    	} */
				$.ajax({
					url: '../inventory/searchTransitionLocation',
					type: 'post',
					dataType: 'json',
					data: {
						customer_id: $('#customer_id').val(),
						created_time: $('#created_time').val(),
						barcode: $('#barcode').val(),
						delivery_time: $('#delivery_time').val(),
						orderId: orderId,
						warehouse_id:$("#warehouse_id").val()
					},
					success: function (res) {
						console.log(res);
						if(res.message) {
							alert(res.message);
							$('tbody.tbody').html('');
							$('.orderSum').html('');
							$('.goodskinds').html('');
							$('.goodsSum').html('');							
							$('.noDataTip').show();
							return;
						}
						$('.noDataTip').hide();
						$('tbody.tbody').html('');
						$('.orderSum').html('');
						$('.goodskinds').html('');
						$('.goodsSum').html('');
						if (res.goodsTotalNumberInteger == 0 || !res.goodsTotalNumberInteger) {
							$('.noDataTip').show();
						} else if (res.goodsTotalNumberInteger) {
							$('.orderSum').html(res.orderIdNumber);
							$('.goodskinds').html(res.goodsCategory);
							$('.goodsSum').html(res.goodsTotalNumberInteger);
							var trHtml = '',
								tableData = res.newOrderCancelList,
								tableDataLen = tableData.length,
								goodsData = {},
								goodsDataLen = 0;
							for (var i = 0; i < tableDataLen; i++) {
								goodsData = tableData[i].maplist,
								goodsDataLen = goodsData.length;
								trHtml += '<tr><td rowspan="'+goodsDataLen+'" style="position: relative;"  valign="middle"><input type="checkbox" name="check_batch_pick[]" class="check" style="position:relative; top:-3px;" />'+tableData[i].name+'</td><td rowspan="'+goodsDataLen+'">'+$("#warehouse_id option:selected").text()+'</td><td rowspan="'+goodsDataLen+'">'+tableData[i].order_id+'</td>';
								for (var j = 0; j < goodsDataLen; j++) {
									trHtml += '<td>'+goodsData[j].barcode+'</td><td>'+goodsData[j].product_name+'</td><td>'+goodsData[j].diff+'</td></tr>';
								}
							}
							$('tbody.tbody').html(trHtml);
						}
					}
				});
			});
		});
	</script>
</body>
</html>