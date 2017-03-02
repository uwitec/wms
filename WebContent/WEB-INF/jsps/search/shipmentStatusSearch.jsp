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
    <title>包裹状态查询</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <style>
    #body-sale-search .lq-form-inline label {
    	width: 5.4em;
    }

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
    </style>
    <script type="text/javascript">
    $(document).ready(function(){
    	
    });
    
    
    
    function ToUrl(pageNo)   
	{   
    	var customer_id = $('#customer_id').val();
    	var order_id = $.trim($('#order_id').val());
    	var status = $('#status').val();
    	var start = $('#start').val();
    	var end = $('#end').val();
    	var tracking_number = $.trim($('#tracking_number').val());
    	var pallet_no = $.trim($('#pallet_no').val());
    	var shipping_code = $('#shipping_code').val();
    	
    	var data ="";
    	if(customer_id != '' && customer_id != null){
    		data += ("&customer_id="+customer_id);
    	}
    	if(order_id != '' && order_id != null){
    		data += ("&order_id="+order_id);
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
    	if(tracking_number != '' && tracking_number != null){
    		data += ("&tracking_number="+tracking_number);
    	}
    	
    	if(pallet_no != '' && pallet_no != null){
    		data += ("&pallet_no="+pallet_no);
    	}
    	if(shipping_code != '' && shipping_code != null){
    		data += ("&shipping_code="+shipping_code);
    	}
    	
		window.location.href= "../search/shipmentStatusSearch?currentPage="+pageNo+"&pageSize=20"+data;
	}  
  
    </script>
</head>
<body id="body-sale-search">
	<div class="main-container">
		<form method="post" action="../search/shipmentStatusSearch" class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="customer_id">货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
							<option value="">不限</option> 
							<c:forEach items="${customerList}" var="customer">
								<option name="customer_id"
									class="${customer.customer_id}"
									<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
									value="${customer.customer_id}">${customer.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="order_id">订单号:</label>
						<input type="text" id="order_id" name="order_id" value="${order_id}" size="12" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="start">起始时间:</label>
						<input type="text" id="start" name="start" value="${start}" size="12" class="lq-form-control">
						<button id="startTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
							<i class="fa fa-calendar"></i>
						</button> 
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="status">状态:</label>
						<select name="status" id="status" class="lq-form-control">
							<option value="">不限</option> 
							<option value="UN_WEIGH" <c:if test="${status=='UN_WEIGH'}">selected="true"</c:if>>已复核，待称重</option>
							<option value="UN_MT" <c:if test="${status=='UN_MT'}">selected="true"</c:if>>已称重，待码托</option>
							<option value="UN_SHIP" <c:if test="${status=='UN_SHIP'}">selected="true"</c:if>>已码托，待发货</option>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="tracking_number">快递单号:</label>
						<input type="text" id="tracking_number" name="tracking_number" value="${tracking_number}" size="12" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="end">截止时间:</label>
						<input type="text" id="end" name="end" value="${end}" size="12" class="lq-form-control"/>
						<button id="endTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
							<i class="fa fa-calendar"></i>
						</button>
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="pallet_no">码托条码:</label>
						<input type="text" id="pallet_no" name="pallet_no" value="${pallet_no}" size="12" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="shipping_code">快递方式:</label>
						<select name="shipping_code" id="shipping_code" class="lq-form-control">
							<option value="">不限</option> 
							<c:forEach items="${shippingList}" var="shipping">
								<option name="shipping_code"
									class="${shipping.shipping_code}"
									<c:if test="${shipping_code==shipping.shipping_code}">selected="true"</c:if>
									value="${shipping.shipping_code}">${shipping.shipping_name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="lq-form-group">
						<button type="submit" id="btn-sale-search" class="lq-btn lq-btn-sm lq-btn-primary">
							<i class="fa fa-search"></i>查询
						</button>
						<input type="hidden" name="act" value="search">
					</div>  
				</div>
			</div>         	
		</form>
		<c:if test="${shipmentNum != 0}">
			<div><span>筛选条件下 有“${shipmentNum}”个包裹</span></div>
	
		<div>
		<form method="post" id="form">
		<table class="lq-table" id="batchTable">
		 	<thead>
				<tr>
					<th>货主</th>
					<th>订单号</th>
					<th>订单时间</th>
					<th>快递单号</th>
					<th>快递方式</th>
					<th>码托条码</th>
					<th>包裹状态</th>
					<th>操作时间</th>
				</tr>
			</thead> 
			<tbody>
				<c:forEach items="${shipmentStatusList}" varStatus="i" var="shipment">
					<tr align="center" class="list">
						<td>${shipment.name}</td>
						<td>${shipment.order_id}</td>
						<td>${shipment.order_time}</td>
						<td>${shipment.tracking_number}</td>
						<td>${shipment.shipping_name}</td>
						<td>${shipment.pallet_no}</td>
						<td>${shipment.status}</td>
						<td>${shipment.do_time}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</form>
		</div>
		</c:if>
		<c:if test="${shipmentNum == 0}">
			<div class="noDataTip" style="display:block;">未查到符合条件的数据!</div>
		</c:if>
		<c:if test="${page.totalPage != 0 }">
		<div class="page-wrap">
				<div class="lq-row">
					<nav id="page-search">
						<ul class="lq-page-control">
							<li class="prev"><a href="javascript:void(0);" 
								onclick="ToUrl('1')" aria-label="Previous" alt="Previous">&laquo;</a></li>
							<c:if test="${page.totalPage > 10}">
								<c:set var="maxPage" value="10"></c:set>
							</c:if>
							<c:if test="${page.totalPage < 10}">
								<c:set var="maxPage" value="${page.totalPage}"></c:set>
							</c:if>
							<c:forEach var="item" varStatus="i" begin="1" end="${maxPage}">
								<li <c:if test="${i.index==page.currentPage}"> 
								class="active"</c:if>><a href="javascript:void(0);" 
								onclick="ToUrl('${i.index}')">${i.index}</a></li>
							</c:forEach>
							<c:if test="${page.totalPage > 10}">
								<li>...</li>
								<li><a href="javascript:void(0);" onclick="ToUrl('${page.totalPage}')">${page.totalPage}</a></li>
							</c:if>
							<c:if test="${page.totalPage != page.currentPage}">
								<li class="next"><a href="javascript:void(0);" 
									onclick="ToUrl('${page.currentPage+1}')" aria-label="Next" alt="Next">&raquo;</a></li>
							</c:if>
						</ul>
					</nav>
				</div>
		</div>
		</c:if>
	</div>
	<script type="text/javascript" src="../static/js/zapatec/utils/zapatec.js"></script>
    <script type="text/javascript" src="../static/js/zapatec/zpcal/src/calendar.js"></script>
    <script type="text/javascript" src="../static/js/zapatec/zpcal/lang/calendar-en.js"></script>
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