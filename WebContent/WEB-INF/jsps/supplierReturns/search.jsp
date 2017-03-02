<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>供应商退货</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/autocomplete.css" />
	<!-- Loading Calendar JavaScript files -->
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/zapatec.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/calendar.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/calendar-en.js"></script>
	<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
	<style type="text/css">
		#print{
		  width: 0px;
		  height: 0px;
		  border: 0px;
		}
		form {
			margin: 0 0 20px 0;
		}
		.searchBtn {
			margin: 0 0 0 10px;
		}
		#page-search {
			float: right;
		}
		.page-wrap {
			display: none;
		}
		.uploadfile {
			margin: 10px 0 0 0;
		}
		#batchTable {
			margin: 10px 0 0 0;
		}
		.inputMsg {
		}
	    .noDataTip {
	    	display: none;
	    	text-align: center;
	    	font-size: 30px;
	    	color: #666;
	    	margin: 30px 0 0 0;
	    }
	</style>
</head>
<body>
 	<iframe id="print" src=""></iframe>
	<div class="main-container">
		<form class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="customer_id">选择货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
							<option value="">--请选择--</option> 
							<c:forEach items="${customerList}" var="customer">
								<option name="customer_id"
									class="${customer.customer_id}"
									<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
									value="${customer.customer_id}">${customer.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="OMSOrderSN">OMS订单号:</label>
						<input type="text" name="" class="lq-form-control" id="OMSOrderSN">
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="orderStatus">订单状态:</label>
						<select class="lq-form-control" id="orderStatus">
							<option value="All" <c:if test="${'All'== orderStatus}">selected="true"</c:if>>不限</option>
							<option value="ACCEPT" <c:if test="${'ACCEPT'== orderStatus}">selected="true"</c:if>>未处理</option>
							<option value="RESERVED" <c:if test="${'RESERVED'== orderStatus}">selected="true"</c:if>>分配成功</option>
							<option value="FULFILLED" <c:if test="${'FULFILLED'== orderStatus}">selected="true"</c:if>>已退还</option>
							<option value="CANCEL" <c:if test="${'CANCEL'== orderStatus}">selected="true"</c:if>>已取消</option>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="barcode">商品条码:</label>
						<input type="text" name="" class="lq-form-control" id="barcode">
					</div>
				</div>
				<div class="lq-col-5">
					<div class="lq-form-group">
	    				<label for="">创建时间:</label>
	    				<input type="text" id='startTime' name='startTime' value="${startTime}" class="lq-form-control" />
	    				<button id="from_date_trigger" name="from_date_trigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon" >
	    					<i class="fa fa-calendar"></i>
	    				</button>
		    		</div>
		    		<div class="lq-form-group">
    					<label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
	               		<input type="text" id='endTime' name='endTime' value="${endTime}"  class="lq-form-control"/>
	               		<button type="button" id="to_date_trigger" name="to_date_trigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon" >
	               			<i class="fa fa-calendar"></i>
	               		</button>
			  		</div>
				</div>
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="warehouse_id">选择渠道:</label>
						<select name="warehouse_id" id="warehouse_id" class="lq-form-control">
							<option value="">--请选择--</option> 
							<c:forEach items="${warehouseList}" var="warehouse">
								<option name="warehouse_id"
									class="${warehouse.warehouse_id}"
									<c:if test="${warehouse_id==warehouse.warehouse_id}">selected="true"</c:if>
									value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
							</c:forEach>
						</select>
		               	<button type="button" id="btn-sale-search" class="searchBtn lq-btn lq-btn-sm lq-btn-primary">查询</button>
					</div>
				</div>
			</div>
		</form>
		<div class="lq-form-group">
			<button type="button" id="" class="lq-btn lq-btn-sm lq-btn-primary checkAll">全选</button>
			<button type="button" id="" class="lq-btn lq-btn-sm lq-btn-primary clearAll">清空</button>
			<button type="button" id="" class="lq-btn lq-btn-sm lq-btn-primary checkNot">反选</button>
			<button type="button" id="printBtn" class="lq-btn lq-btn-sm lq-btn-primary" disabled="true">打印选中出库单</button>
		</div>
		<table class="lq-table">
			<thead>
				<tr>
					<th>选择</th>
					<th>OMS订单号</th>
					<th>订单状态</th>
					<th>货主</th>
					<th>订单时间</th>
					<th>商品条码</th>
					<th>商品名称</th>
					<th>库存状态</th>
					<th>渠道</th>
					<th>申请出库数</th>
					<th>已出库数</th>
				</tr>
			</thead>
			<tbody>

			</tbody>
		</table>
		<div class="noDataTip">未查到符合条件的数据!</div>
		<div class="page-wrap">
			<div class="lq-row">
				<nav id="page-search">
					<ul class="lq-page-control">
						<li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>
						<!-- <c:forEach var="item" varStatus="i" begin="1" end="${page.totalPage}"> -->
							<!-- <li class="page"><a href="javascript:void(0);"></a></li> -->
						<!-- </c:forEach> -->
						<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li>
					</ul>
				</nav>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript">
		Zapatec.Calendar.setup({
	  	    weekNumbers       : false,
	  	    electric          : false,
	  	    inputField        : "startTime",
	  	    button            : "from_date_trigger",
	  	    ifFormat          : "%Y-%m-%d",
	  	    daFormat          : "%Y-%m-%d"
	      });
	    Zapatec.Calendar.setup({
	  	    weekNumbers       : false,
	  	    electric          : false,
	  	    inputField        : "end_time",
	  	    button            : "to_date_trigger",
	  	    ifFormat          : "%Y-%m-%d",
	  	    daFormat          : "%Y-%m-%d"
	     });
		$(function () {
			var TOTAL_PAGE = 0,
				START_PAGE = 1,
				MAX_PAGE = 10,
				END_PAGE = 10,
				CURRENT_PAGE = 1,
				PAGE_DATA = {},
				PAGE = {},
				sendData = {};
			// 查询
			$('.searchBtn').click(function () {
				console.log($('#orderStatus').val());
				var isOK = true;
				// oms_order_sn 和 start_time 至少一项必填
				if (!$('#OMSOrderSN').val() && !$('#startTime').val()) {
					isOK = false;
				}
				if (isOK) {
					sendData = {
						customer_id: $('#customer_id').val(),
						barcode: $.trim($('#barcode').val()),
						order_status: $('#orderStatus').val(),
						start_time: $('#startTime').val(),
						end_time: $('#endTime').val(),
						oms_order_sn: $.trim($('#OMSOrderSN').val()),
						warehouse_id: $('#warehouse_id').val()
					};
					$.ajax({
						url: '../supplierReturn/supplierReturnSearch',
						type: 'post',
						dataType: 'json',
						data: sendData,
						success: function (res) {
							console.log(res);
							$('.noDataTip').hide();
							if (!res.returnGoodsList.length) {
								$('.noDataTip').show();
							}
							showTable (res);
							// 分页
							PAGE_DATA = res.page;
							TOTAL_PAGE = parseInt(PAGE_DATA.totalPage);
							TOTAL_ORDER = PAGE_DATA.totalCount;
							START_PAGE = 1;
							if (TOTAL_PAGE < MAX_PAGE) {
								END_PAGE = TOTAL_PAGE;
							} else {
								END_PAGE = MAX_PAGE;
							}
							CURRENT_PAGE = PAGE_DATA.currentPage;
							pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
							}
					});
				}
			});
			function showTable (res) {
				data = res.returnGoodsList;
				var listLen = data.length;
				var trHtml = '';
				for (var i = 0; i < listLen; i++) {
					var goodsList = data[i].goodsList;
					var goodsLen = goodsList.length;
					if (data[i].order_status == '分配成功') {
						var isCheckbox = '<input type="checkbox" class="checkbox" value='+data[i].order_id+' />'
						var order_status_RESERVED = '<a href="../supplierReturn/supplierReturnEdit?order_id='+data[i].order_id+'">'+data[i].order_status+'</a>';
					} else {
						var isCheckbox = '';
						var order_status_RESERVED = data[i].order_status;
					}
					trHtml += '<tr><td rowspan="'+goodsLen+'">'+isCheckbox+'</td><td rowspan="'+goodsLen+'">'+data[i].oms_order_sn+'</td><td class="order_status" rowspan="'+goodsLen+'" data-value='+data[i].order_status+'>'+order_status_RESERVED+'</td>';
					for (var j = 0; j < goodsLen; j++) {
						trHtml += '<td>'+goodsList[j].customer_name+'</td><td>'+goodsList[j].order_time+'</td><td>'+goodsList[j].barcode+'</td><td>'+goodsList[j].product_name+'</td><td>'+goodsList[j].goods_status+'</td><td>'+goodsList[j].warehouse_name+'</td><td>'+goodsList[j].goods_number+'</td><td>'+goodsList[j].out_num+'</td></tr>';
					}
				}
				$('tbody').html(trHtml);
				$('input[type="checkbox"]').prop('checked', function () {
					$('#printBtn').attr('disabled', false);
				});
			}
			// 分页
            function pageConstructor(currentPage,startPage,endPage) {
				var currentPage = currentPage,
					startPage = startPage,
				    endPage = endPage;
				if (TOTAL_PAGE) {
					$(".page-wrap").html("").fadeIn(300);
					var pageHtml = '<div class="lq-row"><nav id="page-search"><ul class="lq-page-control">';
					if (startPage !== 1) {
						pageHtml +='<li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>';
					}
					for (var i=startPage;i<startPage+endPage;i++) {
						pageHtml += '<li';
						if (i == currentPage) {
							pageHtml += ' class="active"';
						}
						pageHtml += '><a href="#">' + i + '</a></li>';
					}
					if (startPage+endPage-1 !== TOTAL_PAGE) {
						pageHtml += '<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li></ul></nav></div>';
					}
					
					$(".page-wrap").html(pageHtml);
				} else {
					$(".page-wrap").html("").fadeOut(300);
				}
			}
			$(document).on("click",".lq-page-control li",function(){
				var that = $(this);
				if (!that.hasClass("prev") && !that.hasClass("next")) {
					if (!that.hasClass("active")) {
						var index = parseInt($(this).find("a").text());
						sendData = {
							customer_id: $('#customer_id').val(),
							warehouse_id: $('#warehouse_name').val(),
							order_status: $('#orderStatus').val(),
							start_time: $('#startTime').val(),
							end_time: $('#endTime').val(),
							oms_order_sn: $.trim($('#OMSOrderSN').val())
						};
						CURRENT_PAGE = index;
						sendData = $.extend({}, {currentPage: index, pageSize: 10}, sendData);
						$.ajax({
							url : "../supplierReturn/supplierReturnSearch",
							type : "get",
							dataType : "json",
							data : sendData,
							beforeSend : function(){
								that.addClass("active").siblings().removeClass("active");
							},
							success : function(res) {
								console.log(res);
								showTable(res);
							}
						});
					}
				} else if (that.hasClass("next")) {
					START_PAGE = parseInt(that.prev().find('a').text()) + 1;
					var REST_PAGE = TOTAL_PAGE - parseInt(that.prev().find('a').text()),
					    END_PAGE = 0;
					if (REST_PAGE >= 0 && REST_PAGE <= MAX_PAGE) {
						END_PAGE = REST_PAGE;
						pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE); 
					} else if (REST_PAGE > MAX_PAGE) {
						END_PAGE = MAX_PAGE;
						pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
					}
					
				} else if (that.hasClass("prev")) {
					var END_PAGE = MAX_PAGE;
					START_PAGE = parseInt(that.next().find('a').text()) - MAX_PAGE;
					//alert("END_PAGE:"+END_PAGE+",START_PAGE:"+START_PAGE);
					pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
				}
			});
			// 全选	
			var checkNum = 0;
			$('.checkAll').on('click', function () {
				$.each($('input[type="checkbox"]'), function(index, val) {
					if ($(val).prop('checked')) {
						checkNum = 1;
					} else {
						checkNum = 0;
						return false;
					}
				});
				if (checkNum % 2 == 0) {
					$('input[type="checkbox"]').prop('checked', true);
				} else {
					$('input[type="checkbox"]').prop('checked', false);
				}
				checkNum ++;
			});
			// 反选
			$('.checkNot').on('click', function () {
				$.each($('input[type="checkbox"]'), function(index, val) {
					if ($(val).prop('checked')) {
						$(val).prop('checked', false);
					} else {
						$(val).prop('checked', true);
					}
				});
			});
			// 清空
			$('.clearAll').on('click', function () {
				$.each($('input[type="checkbox"]'), function(index, val) {
					$('input[type="checkbox"]').prop('checked', false);
				});
			});
			// 打印
			$('#printBtn').on('click', function () {
				var orderIds = '';
				$.each($('input[type="checkbox"]'), function(index, val) {
					if ($(val).prop('checked')) {
						orderIds += $('.checkbox').eq(index).val() + ',';
					}
				});
				orderIds = orderIds.substring(0, orderIds.length - 1);
				console.log(orderIds);
				if (orderIds) {
					src="../supplierReturn/supplierReturnPrint?order_ids=" + orderIds;
					$('#print').attr('src',src);
				} else {
					alert('请选择');
				}
			});
		});
	</script>
</body>
</html>


