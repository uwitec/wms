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
    <title>订单查询</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/v3.js"></script>
    <style>
    #body-sale-search .lq-form-inline label {
    	width: 5.4em;
    }
	
	input[type=text]{
		height: 30px;
	}
	
    #page-search {
    	float: right;
    }
    
    .totalPage {
    	position: relative;
    	top: 5px;
    	margin-left: 5px;
    }
    .searchBtn {
    	margin: 0 20px 0 0;
    }
    input#start,input#end{
    	width: 40%;
    }
   	.lq-form-group select{
   		width: 45%;
   	}
    </style>
</head>
<body id="body-sale-search">
	<div class="modal-wrap mw-loading">
		<div class="modal-content mc-loading">
			<i class="fa fa-spinner fa-spin" id="loading"></i>
		</div>
	</div>
	<div class="main-container">
		<form method="post" action="../sale/search" class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="customer_id">选择货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
							<option value="0">--请选择--</option> 
							<c:forEach items="${customers}" var="customer">
								<option name="customer_id"
									<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
									value="${customer.customer_id}">${customer.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="shop_name">选择店铺:</label> 
						
 						<select name="shop_name" id="shop_name" class="lq-form-control">
							<option value="0" class="0">--请选择--</option> 
 							<c:forEach var="customerShop" items="${customerShops}">
								<c:forEach items="${customerShop[1].shopName}" var="shopName">
									<c:if test="${!empty shopName}">
										<option name="shop_name" class="${customerShop[0].customerId}" value="${shopName.oms_shop_id}">${shopName.shop_name}</option>
									</c:if>
								</c:forEach>
							</c:forEach> 
						</select>
						<%-- <input type="hidden" id="shops" value="${customerShops[0][0].customerId}"> --%>
					</div>
					<div class="lq-form-group">
						<label for="order_status">订单状态:</label>
						<select name="order_status" id="order_status" class="lq-form-control"> 
							<option value="">--请选择--</option> 
					      	<option name="order_status" 
					     	 <c:if test="${'ACCEPT'== order_status}">selected="true"</c:if> value="ACCEPT">未处理</option> 
					      
							<option name="order_status" 
					      	<c:if test="${'BATCH_PICK'== order_status}">selected="true"</c:if> value="BATCH_PICK">已分配

					      	<option name="order_status" 
					      	<c:if test="${'PICKING'== order_status}">selected="true"</c:if> value="PICKING">拣货中</option>
							
							<option name="order_status" 
					      	<c:if test="${'RECHECKED'== order_status}">selected="true"</c:if> value="RECHECKED">已复核</option>
						
							<option name="order_status" 
					      	<c:if test="${'WEIGHED'== order_status}">selected="true"</c:if> value="WEIGHED">已称重</option>
					      	
							<option name="order_status" 
					      	<c:if test="${'DELIVERED'== order_status}">selected="true"</c:if> value="DELIVERED">已发货</option>
					   
					      	<option name="order_status" 
					      	<c:if test="${'CANCEL'== order_status}">selected="true"</c:if> value="CANCEL">已取消</option>
						</select> 
					</div>
					<div class="lq-form-group">
						<label for="warehosue_id" style="text-indent:2rem;">渠道:</label>
						<select name="warehouse_id" id="warehouse_id">
							<option value="">不限</option>
							<c:forEach items="${warehouseList}" var="warehouse">
									<option value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="order_id">订单号:</label>
						<input type="text" name="order_id" value="${order_id}" id="order_id" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="shop_order_sn" style="width:100px;margin-left:-21px;">OMS订单号:</label>
						<input type="text" name="shop_order_sn" value="${shop_order_sn}" id="shop_order_sn" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="batch_pick_sn">波次单号:</label>
						<input type="text" name="batch_pick_sn" value="${batch_pick_sn}" id="batch_pick_sn" class="lq-form-control">
					</div>
				</div>
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="start">订单时间:</label>
						<input class="form-control form_datetime" id="start" type="text" name="start" value="${start}" size="16"/>
					</div>
					<div class="lq-form-group">
						<label for="end">结束时间:</label>
						<input class="form-control form_datetime" id="end" type="text" name="end" value="${end}" size="16"/>
					</div>
					<div class="lq-form-group">
						<label for="is_reserved">预定状态:</label>
						<select name="is_reserved" id="is_reserved" class="lq-form-control">
							<option value="Y">已预订</option>
							<option value="N">未预定</option>
						</select>
					</div>
					<div class="lq-form-group">
						<button type="submit" id="btn-sale-search" class="lq-btn lq-btn-sm lq-btn-primary searchBtn">
							<i class="fa fa-search"></i>查询
						</button>
						<input type="hidden" name="act" value="search">
						<input type="button" id="exportOrders" name="type" value="导出" style="display:none" class="lq-btn lq-btn-sm lq-btn-primary">				
					</div>
				</div>
			</div>         	
		</form>
		<table class="lq-table" id="sale-list-table">
		 	<thead>
				<tr>
					<th style="width:5%">货主</th>
					<th style="width:10%">渠道</th>
					<th style="width:8%">订单号</th>
					<th style="width:15%">OMS订单号</th>
					<th style="width:18%">订单时间</th>
					<th style="width:8%">订单状态</th>
					<th style="width:15%">波次单号</th>
					<th style="width:18%">最后更新时间</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
		<div class="noDataTip">未查到符合条件的数据!</div>
		<div class="page-wrap"></div>
	</div>
    <script type="text/javascript" src="../static/js/zapatec/utils/zapatec.js"></script>
  	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/alertPlugin.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap-datetimepicker.min.js"></script>
	<script>
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
	 	    minView: 'day',
	 	    format: 'yyyy-mm-dd hh:00:00'
	 	});
		$(".form-control").on("focus",function(){
			$("th.prev span").text("<");
			$("th.next span").text(">");
		});
		function Load(){
			var dt=new Date();
			var year=dt.getFullYear();
			var month=dt.getMonth()+1;
			var day=dt.getDate();
			var hour=dt.getHours();
			if(month<10) month="0"+month;
			if(day<10) day="0"+day;
			if(hour<10) hour="0"+hour;
			$("#start").val(year+"-"+month+"-"+day+" "+hour+":00:00");
		}
		Load();
	</script>
	<script>
		console.log("shops:"+$("#shops").val());
		$(document).ready(function(){
			
			var TOTAL_PAGE = 0, START_PAGE = 1, MAX_PAGE = 10, END_PAGE = 10, CURRENT_PAGE = 1, PAGE_DATA = {}, TOTAL_ORDER = 0;
			
			var PAGE = {

			};
			
			$("#customer_id").on("change",function(){
				$('#shop_name').find('option:gt(0)').css('display', 'block').removeAttr("selected");
				var customerId = $(this).val();
				if(customerId=="0"){
					$('#shop_name').find('option:gt(0)').css('display', 'block');
				}else{
					$('#shop_name').find('option:gt(0)').not($('.' + customerId)).css('display', 'none');
				}
				$('#shop_name').find('option').eq(0).attr("selected",true);
			})

			$('#exportOrders').click(function(){
				var data ="";
				
				var shop_name,customer_id;
				if($("#customer_id").val()=="0") 
					customer_id="";
				else
					customer_id=$('#customer_id').val();
				
				if($("#shop_name").val()=="0") 
					shop_name="";
				else
					shop_name=$('#shop_name').val();
			
				
				var order_id = $("#order_id").val();
				var shop_order_sn = $("#shop_order_sn").val();
				//var time_type = $("#time_type").val();
				var start = $("#start").val();
				var end = $("#end").val();
				var order_status = $("#order_status").val();
	/* 			var customer_id = $('#customer_id').val(); */
				var batch_pick_sn = $('#batch_pick_sn').val();
				var warehouse_id = $("#warehouse_id").val();
				var is_reserved = $('#is_reserved').val();
/* 				var shop_name = $('#shop_name').val(); */
				if(order_id != null && order_id != '') data += ("&order_id="+order_id);
				if(shop_order_sn != '' && shop_order_sn != null) data += ("&shop_order_sn="+shop_order_sn);
				//if(time_type != '' && time_type != null) data += ("&time_type="+time_type);
				if(start != '' && start != null) data += ("&start="+start);
				if(end != '' && end != null) data += ("&end="+end);
				if(order_status != '' && order_status != null) data += ("&order_status="+order_status);
				if(customer_id != '' && customer_id!=null) data +=("&customer_id="+customer_id);
				if(warehouse_id != '' && warehouse_id!=null) data += ("&warehouse_id="+warehouse_id);
				if(batch_pick_sn != '' && batch_pick_sn!=null) data += ("&batch_pick_sn="+batch_pick_sn);
				if(warehouse_id != '' && warehouse_id!=null) data += ("&warehouse_id="+warehouse_id);
				if(is_reserved != '' && is_reserved!=null) data += ("&is_reserved="+is_reserved);
				if(shop_name != '' && shop_name!=null) data += ("&shop_id="+shop_name);
				window.location = "../sale/exportOrders?data"+data;
			});
			
			$("#btn-sale-search").on("click",function(e){
				e.preventDefault();
				var that = $(this);
				var flag=true;
				var shop_name,customer_id;
				$(".form-control").each(function(){
					var dateTime=$(this).val();
					if($(this).index<0||dateTime!=""){
						if(!validate(dateTime)){
							$(this).focus();
							flag=false;
							return false;
						}
					}
					if($(this).index==0&&dateTime==""){
						alert("开始时间不能为空!")
						$(this).focus();
						flag=false;
						return false;
					}
				})
				if(!flag) return false;
				
				var warehouseId=$("#warehouse_id").val();
				
				if($("#customer_id").val()=="0") 
					customer_id="";
				else
					customer_id=$('#customer_id').val();
				
				if($("#shop_name").val()=="0") 
					shop_name="";
				else
					shop_name=$('#shop_name').val();
				
				var saleSearchData = {
					order_id : $("#order_id").val(),
					shop_order_sn : $("#shop_order_sn").val(),
					//time_type : $("#time_type").val(),
					start : $("#start").val(),
					end : $("#end").val(),
					order_status : $("#order_status").val(),
					warehouse_id : $("#warehouse_id").val(),
					customer_id : customer_id,
					batch_pick_sn : $('#batch_pick_sn').val(),
					is_reserved : $('#is_reserved').val(),
					shop_id : shop_name,
					warehouse_id:warehouseId
				};
				//console.log(saleSearchData);
				$.ajax({
					url : "../sale/search_ajax",
					type : "post",
					dataType : "json",
					data : saleSearchData,
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
						that.prop("disabled",true);
					},
					success : function(data) {
						console.log(data)
						function settimeoutSuccess () {
							$(".mw-loading").fadeOut(300);
							that.prop("disabled",false);
							//console.log(data.page);
							PAGE_DATA = data.page;
							TOTAL_PAGE = parseInt(data.page.totalPage);
							TOTAL_ORDER = data.page.totalCount;
							START_PAGE = 1;
							if (TOTAL_PAGE < MAX_PAGE) {
								END_PAGE = TOTAL_PAGE;
							} else {
								END_PAGE = MAX_PAGE;
							}
							CURRENT_PAGE = data.page.currentPage;
							console.log(START_PAGE,END_PAGE)
							pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
							tableConstructor(data.saleOrderList);
							console.log(data.isDouble11);
							if(data.isDouble11!=1&&data.isDouble11!=null)
								document.getElementById("exportOrders").style.display = "inline-block";
						
						}
						settimeoutSuccess();
						// window.setTimeout(function () {settimeoutSuccess()},300);
						if (!data.saleOrderList.length) {
							$(".mw-loading").fadeOut(300);
							that.prop("disabled",false);
							$('.noDataTip').fadeIn(300);
						}
					},
					error : function(jqXHR, textStatus, errorThrown) {
						window.setTimeout(function(){
							$(".mw-loading").fadeOut(300);
							that.prop("disabled",false);
							alert("操作失败，请刷新");
							console.log(jqXHR);
							console.log(textStatus);
							console.log(errorThrown);
						},300);
						that.prop("disabled",false);
					}
				});
			});

			function pageConstructor(currentPage,startPage,endPage) {
				var currentPage = currentPage,
					startPage = startPage,
				    endPage = endPage;
				//alert("startPage:"+startPage+",endPage:"+endPage);
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
						pageHtml += '<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li>';
					}
					pageHtml += '<span class="totalPage">共 <span>'+TOTAL_PAGE+'</span> 页, '+TOTAL_ORDER+' 个订单</span></ul></nav></div>';
					$(".page-wrap").html(pageHtml);
				} else {
					$(".page-wrap").html("").fadeOut(300);
				}
			}

			function tableConstructor(data) {
				//console.log(data);
				$('.noDataTip').fadeOut(300);
				if (data.length) {
					var tableHtml = '';
					for (var key in data) {
						var order_status = data[key].order_status;
						tableHtml += '<tr><td>'+ data[key].name + '</td><td>'+data[key].warehouse_name+'</td><td><a target="_blank" href ="../sale/detail?order_id=' + data[key].order_id + '">'+data[key].order_id +'</a></td><td>'
						          + data[key].oms_order_sn + '</td><td>'
						          + data[key].order_time + '</td><td>';
						if(order_status == 'ACCEPT'){
							tableHtml +=  '未处理</td><td>';
						}else if(order_status == 'BATCH_PICK'){
							tableHtml += '已分配</td><td>';
						}else if(order_status == 'PICKING'){
							tableHtml +=  '拣货中</td><td>';
						}else if(order_status == 'RECHECKED'){
							tableHtml += '已复核</td><td>';
						}else if(order_status == 'WEIGHED'){
							tableHtml += '已称重</td><td>';
						}else if(order_status == 'DELIVERED'){
							tableHtml += '已发货</td><td>';
						}else if(order_status == 'FULFILLED'){
							tableHtml += '已发货</td><td>';
						}else if(order_status == 'CANCEL'){
							tableHtml += '已取消</td><td>';
						}else{
							tableHtml += '其他状态</td><td>';
						}
						if(data[key].batch_pick_sn =="" || data[key].batch_pick_sn == null){
							tableHtml += '-</td><td>';
						}else{
							tableHtml += data[key].batch_pick_sn + '</td><td>';
						}
						tableHtml +=  data[key].last_updated_time+'</td></tr>';
					}
					$("#sale-list-table tbody").html(tableHtml);
				} else {
					$("#sale-list-table tbody").html("");
				}
			}

			$("#body-sale-search").on("click",".lq-page-control li",function(){
				var that = $(this);
				if (!that.hasClass("prev") && !that.hasClass("next")) {
					if (!that.hasClass("active")) {
						var index = parseInt($(this).find("a").text());
						CURRENT_PAGE = index;
						var warehouse_id=$("#warehosue_id").val();
						var shop_name,customer_id;
						if($("#customer_id").val()=="0") 
							customer_id="";
						else
							customer_id=$('#customer_id').val();
						
						if($("#shop_name").val()=="0") 
							shop_name="";
						else
							shop_name=$('#shop_name').val();

						var data = {
							currentPage : index,
							pageSize : 12,
							order_id : $("#order_id").val(),
							shop_order_sn : $("#shop_order_sn").val(),
							warehouse_id : $("#warehouse_id").val(),
							//time_type : $("#time_type").val(),
							start : $("#start").val(),
							end : $("#end").val(),
							order_status : $("#order_status").val(),
							customer_id : customer_id,
							batch_pick_sn : $('#batch_pick_sn').val(),
							is_reserved : $('#is_reserved').val(),
							shop_id : shop_name,
							warehouse_id:warehouse_id
						};
						$.ajax({
							url : "../sale/search_ajax",
							type : "post",
							dataType : "json",
							data : data,
							beforeSend : function(){
								$(".mw-loading").fadeIn(300);
								that.addClass("active").siblings().removeClass("active");
							},
							success : function(data) {
								function tableSuccess () {
									$(".mw-loading").fadeOut(300);
									tableConstructor(data.saleOrderList);
								}
								window.setTimeout(function(){tableSuccess()},300);
							},
							error : function(error) {
								window.setTimeout(function(){
									$(".mw-loading").fadeOut(300);
									alert("ajax error");
									console.log(error);
								},300);
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
		});
	</script>
</body>
</html>