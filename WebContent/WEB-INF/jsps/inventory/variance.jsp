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
<title>库存调整</title>
<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<style type="text/css">
	/*.searchBtn {
		margin: 0 0 0 20px;
	}*/
	.page-wrap {
		float: right;
	}
    .noDataTip {
    	/*display: none;*/
    	text-align: center;
    	font-size: 30px;
    	color: #666;
    	margin: 30px 0 0 0;
    }
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script type="text/javascript" src="../static/js/zapatec/utils/zapatec.js"></script>
<script type="text/javascript" src="../static/js/zapatec/zpcal/src/calendar.js"></script>
<script type="text/javascript" src="../static/js/zapatec/zpcal/lang/calendar-en.js"></script>
<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />

<script type="text/javascript">
function checksubmit(obj){
  	$(obj).attr("disabled",true);
  	var order_goods_id= $(obj).closest('tr.list').find('.order_goods_id').attr('value');
  	//alert(order_goods_id);
  	
  	 $.ajax({
         type: 'post',
   	   	 url: '../inventory/adjust',  
         data: {
            	 'orderGoodsId':order_goods_id,
            	 'serialNumber':"",
         },
      	 dataType:'json',
         success: function(data) { 
        	 console.log(data);
             alert(data.note);
             location.reload();
      		//$(obj).closest('tr.list').remove();
         },
 		 error:function(data){
 			  console.log(data);
 			  alert("失败！");
 		 }
      });
}

function checkdelete(obj){
  	$(obj).attr("disabled",true);
  	var order_goods_id= $(obj).closest('tr.list').find('.order_goods_id').attr('value');
  	var order_id= $(obj).closest('tr.list').find('.order_id').attr('value');
  	//alert(order_goods_id);
  	 $.ajax({
         type: 'post',
   	   	 url: '../inventory/adjust_delete',  
         data: {
            	 'orderGoodsId':order_goods_id,
            	 'serialNumber':"",
            	 'orderId':order_id,
         },
      	 dataType:'json',
         success: function(data) { 
        	 console.log(data);
             alert(data.note);
             location.reload();
      		//$(obj).closest('tr.list').remove();
         },
 		 error:function(data){
 			  console.log(data);
 			  alert("失败！");
 		 }
      });
}

/*****************全选*******************/
function select_all(node, type)
{
    node = node ? node : document ;
    $(node).find("input[name='check_variance[]']:enabled").each(function(i){
		this.checked = true;
	});
}

/*****************反选*****************/
function select_reverse(node)
{
	node = node ? node : document ;
	$(node).find("input[name='check_variance[]']:enabled").each(function(i){
		this.checked = !this.checked;
	});
}

/*****************清空*******************/
function select_none(node, type)
{
    node = node ? node : document ;
    $(node).find("input[name='check_variance[]']:enabled").each(function(i){
		this.checked = false;
	});
}

function check_submit(node)
{
	node = node ? node : document ;
	item = $(node).find(':checkbox:checked');
	console.log(item);
	if (!item || item==undefined || item.length<1) {
		alert('没有选中项');
		return false;
	}
	
	var orderList= new Array();
	 $("input[name='check_variance[]']:checked").each(function(){ 
         if($(this).attr("checked")){
        	 
             //var goods_name = $(this).closest('tr.list').find('.goods_name').attr('value');
             
             orderList.push("{'orderGoodsId':"+$(this).val()+",'serialNumber':''}");
         }
	 })
	
	 $.ajax({
         type: 'post',
   	   	 url: '../inventory/adjust2',  
         data: JSON.stringify(orderList),
      	 dataType:'json',
      	 contentType:"application/json",
         success: function(data) { 
        	 console.log(data);
             alert('成功！');
         },
 		 error:function(data){
 			  console.log(data);
 			  alert("失败！");
 		 }
      });

}

function ToUrl(pageNo)   
{   
	var order_sn = $('#order_sn').val();
	var sku_code = $('#sku_code').val();
	var start = $('#start').val();
	var end = $('#end').val();
	var status = $('#status').val();
	
	var data ="";
	if(order_sn != '' && order_sn != null){
		data += ("&order_sn="+order_sn);
	}
	
	if(sku_code != '' && sku_code != null){
		data += ("&sku_code="+sku_code);
	}
	
	if(start != '' && start != null){
		data += ("&start="+start);
	}
	
	if(end != '' && end != null){
		data += ("&end="+end);
	}
	
	if(status != '' && status != null){
		data += ("&status="+status);
	}
	
	window.location.href= "../inventory/variance?currentPage="+pageNo+"&pageSize=10"+data;
}  
</script>

</head>
<body>
	<div class="main-container">
			<form method="post" action="../inventory/variance" class="lq-form-inline">
				<div class="lq-row">
					<div class="lq-col-3">
							<div class="lq-form-group">
							<label for="customer_id">选择货主:</label>
							<select name="customer_id" id="customer_id" class="lq-form-control">
								<option value="">--请选择--</option> 
								<c:forEach items="${customers}" var="customer">
									<option name="customer_id"
										class="${customer.customer_id}"
										<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
										value="${customer.customer_id}">${customer.name}</option>
								</c:forEach>
							</select>
						</div>
						<div class="lq-form-group">
						        <label>订单编号:</label>
					          	<input type="text" name="order_sn" value="${order_sn}" id="order_sn" class="lq-form-control"/>
					     </div>
					</div>
					
					<div class="lq-col-3">
						<div class="lq-form-group">
						        <label>开始时间:</label>
						        <input type="text" id="start" name="start" value="${start}" size="10" class="lq-form-control"/>
						        <button id="startTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
									<i class="fa fa-calendar"></i>
								</button> 
						</div>    	
						<div class="lq-form-group">
						        <label>结束时间:</label>
					            <input type="text" id="end" name="end" value="${end}" size="10" class="lq-form-control"/>
					            <button id="endTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
									<i class="fa fa-calendar"></i>
								</button>
						 </div>
					</div>
						    
					<div class="lq-col-3">
						<div class="lq-form-group">
						<label>仓库名称:</label>
		          		<select name="warehouse_id" id="warehouse_id" class="lq-form-control"> 
							<option value="">--请选择--</option> 
							<c:forEach items="${warehouseList}" var="warehouse"> 
							    <option name="warehouse_id" 
							    <c:if test="${warehouse_id==warehouse.warehouse_id}">selected="true"</c:if>
							    value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option> 
							</c:forEach> 
						</select>
					</div> 
						<div class="lq-form-group">
					          	<label>商家编码:</label>
					          	<input type="text" name="sku_code" value="${sku_code}" id="sku_code" class="lq-form-control"/>
					     </div>
				    </div>
				    <div class="lq-col-3">
				    	<div class="lq-form-group">      	
						        <label>是否出库:</label>
					        	<select name="status" class="lq-form-control"> 
									<option value="">--请选择--</option> 
								      <option name="staus" 
								      <c:if test="${'Y'== status}">selected="true"</c:if>
								      value="Y">已调整</option> 
								      
								      <option name="status" 
								      <c:if test="${'N'== status}">selected="true"</c:if>
								      value="N">未调整</option>
								</select> 
						</div>
				      	<input type="submit" value="查询"  class="lq-btn lq-btn-sm lq-btn-primary searchBtn"/>
		            	<input type="hidden" name="act" value="search" />
				    </div>
			</form>
			
			<div>
				 <form method="post" id="form">
					
			      	<div style="margin-top:10px; clear:both;"> 
			      		 <input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="全选" onclick="select_all('#batchTable');" /> &nbsp;&nbsp; 
		      			<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="清空" onclick="select_none('#batchTable');" /> &nbsp;&nbsp;
		      			<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="反选" onclick="select_reverse('#batchTable');" /> &nbsp;&nbsp;
			        	<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="调整选中订单库存" onclick="check_submit('#form')" />
			        	<input type="hidden" name="act" value="batch_variance" />
			      	</div>
				 	<table class="lq-table" style="margin-top:10px;" id="batchTable">
					<thead>
						<tr>
						<th>选择</th>
						<th>商品名</th>
						<th>商家编码</th>
						<th>库存状态</th>
						<th>仓库名称</th>
						<th>申请调整数量</th>
						<th>批次号</th>
						<th>订单</th>
						<th>申请时间</th>
						<th>类型</th>
						<th>已出库</th>
						<th>操作</th>
						</tr>
					</thead>
					<c:if test="${fn:length(varianceOrderList) == '0'}">
					</table>
						<div class="noDataTip">未查到符合条件的数据!</div>
					</c:if> 
					<c:forEach items="${varianceOrderList}" varStatus="i" var="order">
						<tr align="center" class="list">
							<td>
								<c:if test="${order.out_num == 0 && order.order_status == 'ACCEPT'}">
									<input type="checkbox" name="check_variance[]" value="${order.order_goods_id}" class="check" />
								</c:if>
								<input type="hidden" name="order_id" class="order_id"  value="${order.order_id}" />
								<input type="hidden" name="goods_name" class="goods_name"  value="${order.goods_name}" />
							</td>
							<td>${order.goods_name}</td>
							<td>${order.sku_code}</td>
							<td>
								<c:if test="${order.status_id == 'NORMAL'}">
									全新
								</c:if>
								<c:if test="${order.status_id == 'DEFECTIVE'}">
									二手
								</c:if>
							</td>
							<td>${order.warehouse_name}</td>
							<td>${order.goods_number}</td>
							<td>${order.batch_sn}</td>
							<td>${order.oms_order_sn}</td>
							<td>${order.order_time}</td>
							<td>
								<c:if test="${order.order_type == 'VARIANCE_ADD'}">
									盘盈
								</c:if>
								<c:if test="${order.order_type == 'VARIANCE_MINUS'}">
									盘亏
								</c:if>
							</td>
							<td>${order.out_num}</td>
							<td>
								<input type="hidden" name="order_goods_id" class="order_goods_id" value="${order.order_goods_id}" />
								
								<c:if test="${order.out_num == 0 && order.order_status == 'ACCEPT'}">
									<input type="button" value="调整" onclick="checksubmit(this)" class="lq-btn lq-btn-sm lq-btn-primary" />
									<input type="button" value="删除" onclick="checkdelete(this)" class="lq-btn lq-btn-sm lq-btn-primary" />
								</c:if>
								<c:if test="${order.out_num != 0}">
									已调整
								</c:if>
								<c:if test="${order.out_num == 0 && order.order_status == 'FULFILLED' }">
									已删除
								</c:if>
							
							</td>
						</tr>
					</c:forEach>
					</table>
					<br/>
				 </form>
			</div>
			
			<c:if test="${page.totalPage>1}">
				<div class="page-wrap">
					<div class="lq-row">
						<nav id="page-search">
							<ul class="lq-page-control">
								<li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>
								<c:forEach var="item" varStatus="i" begin="1" end="${page.totalPage}">
									<li <c:if test="${i.index==page.currentPage}"> 
									class="active"</c:if>><a href="javascript:void(0);" 
									onclick="ToUrl('${i.index}')">${i.index}</a></li>
								</c:forEach>
								<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li>
							</ul>
						</nav>
					</div>
				</div>
			</c:if>
		</div>
	
	<script type="text/javascript">
		Zapatec.Calendar.setup({
			weekNumbers       : false,
			electric          : false,
			inputField        : "start",
			button            : "startTrigger",
			ifFormat          : "%Y-%m-%d",
			daFormat          : "%Y-%m-%d"
		});
	
		Zapatec.Calendar.setup({
			weekNumbers       : false,
			electric          : false,
			inputField        : "end",
			button            : "endTrigger",
			ifFormat          : "%Y-%m-%d",
			daFormat          : "%Y-%m-%d"
		});
	</script>
</body>
</html>