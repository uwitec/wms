<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>采购批次单</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>

<style type="text/css">
	div#all {
	  margin: 20px;
	}
	div#left {
	  width: 60%;
	  float: left;
	}
	div#right {
	  position: relative;
	}
	#print{
	  width: 0px;
	  height: 0px;
	  border: 0px;
	}
    #body-sale-search .lq-form-inline label {
    	width: 5.4em;
    }
</style>
<script type="text/javascript">
	/*****************全选*******************/
	function select_all(node, type)
	{
	    node = node ? node : document ;
	    $(node).find("input[name='check_order_sn[]']:enabled").each(function(i){
			this.checked = true;
		});
	}
	
	/*****************反选*****************/
	function select_reverse(node)
	{
		node = node ? node : document ;
		$(node).find("input[name='check_order_sn[]']:enabled").each(function(i){
			this.checked = !this.checked;
		});
	}
	
	/*****************清空*******************/
	function select_none(node, type)
	{
	    node = node ? node : document ;
	    $(node).find("input[name='check_order_sn[]']:enabled").each(function(i){
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
    	 
    	var orderList= "";
    	$("input[name='check_order_sn[]']:checked").each(function(){ 
    		var check_order_id = $(this).closest('tr.list').find('.check_order_id').attr('value');
            orderList += check_order_id+",";
    	})
    	orderList = orderList.substring(0,orderList.lastIndexOf(','));
    	$.ajax({
			url : "../purchase/start_batch",
			type : "get",
			dataType : "json",
			data : {
				'order_list':orderList
			},
			contentType:"application/json; charset=utf-8",
			beforeSend : function(){
				$(".mw-loading").fadeIn(300);
				//that.prop("disabled",true);
			},
			success : function(data) {
				window.setTimeout(function(){
					$(".mw-loading").fadeOut(300);
					alert(data.note);
					//location.reload();
					$(".print_accept").removeAttr('disabled');
				},300);
			},
			error : function(error) {
				window.setTimeout(function(){
					$(".mw-loading").fadeOut(300);
					alert("操作失败，请刷新!");
					console.log(error);
				},300);
				//that.prop("disabled",false);
			}
		});
    }
	
    function check_submit2(node)
    {
    	node = node ? node : document ;
    	item = $(node).find(':checkbox:checked');
    	console.log(item);
    	if (!item || item==undefined || item.length<1) {
    		alert('没有选中项');
    		return false;
    	}
    	 
    	var orderList= "";
    	$("input[name='check_order_sn[]']:checked").each(function(){ 
    		var check_order_sn = $(this).val();
            orderList += check_order_sn+",";
    	})
    	orderList = orderList.substring(0,orderList.lastIndexOf(','));
    	
    	src="../purchase/print_receipt?oms_order_sn="+orderList;
  		$('#print').attr('src',src); 
  		$(".mw-loading").fadeOut(100);
    }
</script>
</head>
<iframe id="print" src=""></iframe>
<body>

	<div style="clear:both;margin: 0px;">
		<div class="lq-panel lq-panel-default">
			<div class="lq-panel-heading" style="text-align: center;font-size: large;">
				<i class="fa fa-tasks"></i>
				采购批次号${batch_order_sn}</span>明细(含${num}个采购订单)&nbsp;&nbsp;
			</div>
		</div>
	</div>
	
	<div style="clear:both;margin:10px;">
		<form method="post" id="form">
		<table class="lq-table" id="batchTable">
			<thead>
				<tr>
					<th>选择</th>
					<th>采购单号</th>
					<th>订单状态</th>
					<th>货主名称</th>
					<th>采购时间</th>
					<th>商品名称</th>
					<th>仓库名称</th>
					<th>预计到货时间</th>
					<th>供应商名称</th>
				</tr>
			</thead>
			
			
			<tbody>
				<div style="margin: 10px 0 10px 0; clear:both;"> 
		      		 <input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="全选" onclick="select_all('#batchTable');" /> &nbsp;
		     			<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="清空" onclick="select_none('#batchTable');" /> &nbsp;
		     			<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="反选" onclick="select_reverse('#batchTable');" /> &nbsp;
		        		<input class="lq-btn lq-btn-sm lq-btn-primary start_accept" type="button" id="start_accept" value="开始验收" onclick="check_submit('#form')" />
		        		<input class="lq-btn lq-btn-sm lq-btn-primary print_accept" type="button" disabled id="print_accept" value="打印验收单" onclick="check_submit2('#form')" />
		      	</div>
				<c:forEach items="${purchase_order_info}" varStatus="i" var="order">
					<tr align="center" class="list">
						<td>
							<c:if test="${order.order_status == 'ACCEPT' }">
								<input type="checkbox" name="check_order_sn[]" value="${order.oms_order_sn}" class="check" />
								<input type="hidden" name="check_order_id" class="check_order_id"  value="${order.order_id}" />
							</c:if>
						</td>
						<td>
							<a target="_blank" href="../purchase/edit?oms_order_sn=${order.oms_order_sn}">${order.oms_order_sn}</a>
						</td>
						<td>
							<c:choose>
								<c:when test="${order.order_status=='ACCEPT'}">
							        未处理
							    </c:when>
							    <c:when test="${order.order_status=='IN_PROCESS'}">
							        处理中
							    </c:when>
							    <c:when test="${order.order_status=='ON_SHELF'}">
							        待上架
							    </c:when>
							    <c:when test="${order.order_status=='FULFILLED'}">
							        已上架
							    </c:when>	       
							</c:choose>
						</td>
						<td>${order.name}</td>
						<td>${order.order_time}</td>
						<td>${order.goods_name}</td>
						<td>${order.warehouse_name}</td>
						<td>${order.arrival_time}</td>
						<td>${order.provider_name}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</form>
	</div>
</body>
</html>