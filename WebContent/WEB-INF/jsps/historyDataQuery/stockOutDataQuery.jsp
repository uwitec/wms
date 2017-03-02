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
    <title>出库资料查询</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/v3.js"></script>
    <style>
	
	input[type=text]{
		height: 30px;
	}
	
    #page-search {
    	float: right;
    }
    
    .totalPage {
		float: right;
		position: relative;
		top: 15px;
		margin-left: 8px;
		color: #333;
		font-weight: 600;
    }
    
    .searchBtn {
    	margin: 0 20px 0 0;
    }
    input#start,input#end{
    	width: 35%;
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
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="customer_id">选择货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
							<option value="0">--请选择--</option> 
							<c:forEach items="${warehouseCustomerList}" var="customer">
								<option name="customer_id"
									<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
									value="${customer.customer_id}">${customer.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="batch_sn" style="text-indent:16px;">批次号:</label> 
						<input type="text" class="lq-form-control" id="batch_sn">
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="oms_order_sn">oms订单号:</label>
						<input type="text" name="oms_order_sn" id="oms_order_sn" class="lq-form-control" placeholder="请输入订单号">
					</div>
					<div class="lq-form-group">
						<label for="order_id" >wms订单号:</label>
						<input type="text" name="shop_order_sn"  id="order_id" class="lq-form-control" placeholder="请输入订单号">
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="barcode">商品条码:</label>
						<input type="text" name="barcode" id="barcode" class="lq-form-control">
					</div>
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
					</div>
				</div>
			</div>
			<div class="lq-row">
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="start">出库时间:</label>
						<input class="form-control form_datetime" id="start" type="text" name="start" value="${start_time}" size="16"/>
						<label for="end">至</label>
						<input class="form-control form_datetime" id="end" type="text" name="end" value="${end_time}" size="16"/>
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<button type="submit" id="btn-sale-search" class="lq-btn lq-btn-sm lq-btn-primary searchBtn">
							<i class="fa fa-search"></i>查询
						</button>
						<input type="button" id="exportOrders" name="type" value="导出" class="lq-btn lq-btn-sm lq-btn-primary">				
					</div>				
				</div>
			</div>  	
		</form>
		<div style="clear:both; font-size:14px;font-weight: 700;" id="skuOutNum">
			
		</div>
		<table class="lq-table" id="sale-list-table">
		 	<thead>
				<tr>
					<th>货主</th>
					<th>oms订单号</th>
					<th>wms订单号</th>
					<th>店铺名称</th>
					<th>商品条码</th>
					<th>商品名称</th>
					<th>渠道</th>
					<th>出库数量</th>
					<th>批次号</th>
					<th>出库时间</th>
				</tr>
			</thead>
			<tbody class="tbody">
			</tbody>
		</table>
		<div class="noDataTip">未查到符合条件的数据!</div>
		<div class="page-wrap"></div>
		
	</div>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/showPage.js"></script>
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
			var dt=new Date(new Date().getTime()-3*24*3600*1000);
			var dt_Now=new Date();
			var year=dt.getFullYear();
			var month=dt.getMonth()+1;
			var day=dt.getDate();
			var hour=dt.getHours();
			
			var month_now=(dt_Now.getMonth()+1)>10?(dt_Now.getMonth()+1):("0"+(dt_Now.getMonth()+1));
			var day_now=dt_Now.getDate()>10?dt_Now.getDate():("0"+dt_Now.getDate());
			var hour_now=dt_Now.getHours()>10?dt_Now.getHours():("0"+dt_Now.getHours());
			if(month<10) month="0"+month;
			if(day<10) day="0"+day;
			if(hour<10) hour="0"+hour;
			
			$("#start").val(year+"-"+month+"-"+day+" "+hour+":00:00");
			$("#end").val(dt_Now.getFullYear()+"-"+month_now+"-"+day_now+" "+hour_now+":00:00");
			
		}
		//Load();
	</script>
	<script>
		$(document).ready(function(){
			var PAGE_TOTAL=0,COUNT_TOTAL=0;
			$("#btn-sale-search").on("click",function(e){
				e.preventDefault();
				var that = $(this);
				var flag=true;
				var shop_name,customer_id;
				$(this).attr("disabled",true);
				$("#exportOrders").removeAttr("disabled");
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
				
				var start=$.trim($("#start").val());
				var end=$.trim($("#end").val());
				var oms_order_sn=$.trim($("#oms_order_sn").val());
				var order_id=$.trim($("#order_id").val());
				var barcode=$.trim($("#barcode").val());
				var batch_sn=$.trim($("#batch_sn").val());
				var customer_id=$.trim($("#customer_id").val());
				var warehouse_id=$.trim($("#warehouse_id").val());
				if(end!=""){
					var inteval=new Date(end).getTime()-new Date(start).getTime();
					var threeDay=(3*24+1)*3600*1000;
					if(inteval>threeDay){
						if(oms_order_sn==""&&order_id==""&&barcode==""&&batch_sn==""){
							alert("(oms订单号、wms订单号、商品条码、批次号必填一个!");
							$(this).removeAttr("disabled");
							return false;
						}
					}
						
					if(inteval<0){
						alert("结束时间不能小于开始时间！");
						$(this).removeAttr("disabled");
						return false;
					}
				}
				
				
				var data={
					customer_id: customer_id,
					warehouse_id: warehouse_id,
					start_time: start,
					end_time: end,
					oms_order_sn:oms_order_sn,
					order_id:order_id,
					batch_sn: batch_sn,
					barcode:barcode
				}
				
				$.ajax({
					url:'../history_query/stockOutSearch',
					type:'post',
					dataType:'json',
					data:data,
					beforeSend: function(){
						$(".mw-loading").fadeIn(300);
					},
					success: function(res){
						$(".mw-loading").fadeOut(300);
						that.removeAttr("disabled");
						if(res.result){
							if(res.stockOutList.length>0){
								$(".noDataTip").fadeOut(300);

								PAGE_DATA = res.page;
								TOTAL_PAGE = parseInt(res.page.totalPage);
								TOTAL_ORDER = res.page.totalCount;
								START_PAGE = 1;	
								if (TOTAL_PAGE < MAX_PAGE) {
									END_PAGE = TOTAL_PAGE;
								} else {
									END_PAGE = MAX_PAGE;
								}
								CURRENT_PAGE = res.page.currentPage;
								pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
		 						tableConstructor(res.stockOutList);
		 						PAGE_TOTAL=res.page.totalPage;
		 						COUNT_TOTAL=res.page.totalCount
		 						$(".page-wrap .lq-row").prepend('<p class="totalPage">共<span class="page">'+PAGE_TOTAL+'</span>页,<span class="record">'+COUNT_TOTAL+'</span>记录</p>');
		 						if($.trim($("#barcode").val())){
			 						var skuOutNum=res.skuOutNum;
			 						$("#skuOutNum").html('<p>商品出库总数:'+skuOutNum+'</p>').fadeIn(300);		 							
		 						}else{
		 							$("#skuOutNum").html("").fadeOut(300);	
		 						}

							}else{
								$("tbody.tbody").html("");
								$(".page-wrap").html("");
								$(".noDataTip").fadeIn(300);
							}
							
						}else{
							alert(res.note);
						}
						console.log(res);
					},
					error:function(err){
						$(".mw-loading").fadeOut(300);
						that.removeAttr("disabled");
						alert(err);
					}
				})
				
			});
							
			$("#body-sale-search").on("click",".lq-page-control li",function(e){
				e.preventDefault();
				var start=$.trim($("#start").val());
				var end=$.trim($("#end").val());
				var oms_order_sn=$.trim($("#oms_order_sn").val());
				var order_id=$.trim($("#order_id").val());
				var barcode=$.trim($("#barcode").val());
				var batch_sn=$.trim($("#batch_sn").val());
				var customer_id=$.trim($("#customer_id").val());
				var warehouse_id=$.trim($("#warehouse_id").val());
				var url="../history_query/stockOutSearch";
				var param={
						pageSize : 20,
						customer_id: customer_id,
						warehouse_id: warehouse_id,
						start_time: start,
						end_time: end,
						oms_order_sn:oms_order_sn,
						order_id:order_id,
						batch_sn: batch_sn,
						barcode:barcode
				}
				clickPage($(this),url,param,tableConstructor);
				$(".totalPage").remove();
				$(".page-wrap .lq-row").prepend('<p class="totalPage">共<span class="page">'+PAGE_TOTAL+'</span>页,<span class="record">'+COUNT_TOTAL+'</span>记录</p>');
			});
			function getBatchSn(str){
					if(str)
						return str;
					else 
						return "-";
			}		
	
			function tableConstructor(data){
				var tdHtml='';
				$("tbody.tbody").html("");
			
				for(var i in data){
					tdHtml+='<tr>';
					tdHtml+='<td>'+data[i].name+'</td>';
					tdHtml+='<td>'+data[i].oms_order_sn+'</td>';
					tdHtml+='<td>'+data[i].order_id+'</td>';
					tdHtml+='<td>'+data[i].shop_name+'</td>';
					tdHtml+='<td>'+data[i].barcode+'</td>';
					tdHtml+='<td>'+data[i].product_name+'</td>';
					tdHtml+='<td>'+data[i].warehouse_name+'</td>';
					tdHtml+='<td>'+data[i].number+'</td>';
					tdHtml+='<td>'+getBatchSn(data[i].batch_sn)+'</td>';
					tdHtml+='<td>'+str2Date(data[i].inventory_out_time)+'</td>';
					tdHtml+='</tr>';					
				}
				$("tbody.tbody").html(tdHtml);
			}
			
			//导出功能
			$("#exportOrders").click(function(){
				$(this).attr("disabled",true);
				var start_time=$.trim($("#start").val());
				var end_time=$.trim($("#end").val());
				var oms_order_sn=$.trim($("#oms_order_sn").val());
				var order_id=$.trim($("#order_id").val());
				var barcode=$.trim($("#barcode").val());
				var batch_sn=$.trim($("#batch_sn").val());
				var customer_id=$.trim($("#customer_id").val());
				var warehouse_id=$.trim($("#warehouse_id").val());
				
				var data="";
				data="&cutomer_id="+customer_id+"&warehouse_id="+warehouse_id+"&oms_order_sn="+oms_order_sn+"&barcode="+barcode+"&batch_sn="+batch_sn+"&start_time="+start_time+"&end_time="+end_time+"&order_id="+order_id;
				var url="../history_query/stockOutExport?"+data;
				console.log(url);
				window.location=url;
			})
			

		});
	</script>
</body>
</html>