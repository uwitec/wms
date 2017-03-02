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
    <title>库内加工单</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">      
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
   
    <style>
	#body-sale-search .main-container{
		padding-bottom:0;
	}
	#main-container .lq-form-inline label {
		font-size: 12px;
	}

	.moreLine {
		width: 100%;
	}
	.moreLineItem {
		display: inline-block;
	}


    #page-search {
    	float: right;
    }
    
	 #print{
	  width: 0px;
	  height: 0px;
	  border: 0px;
	}
    .noDataTip {
    	text-align: center;
    	font-size: 30px;
    	color: #666;
    	margin: 30px 0 0 0;
    }
    .lb_firstd {
    	display: block;
    	min-width:36px;
    }
    .lb_firstd span {
    	position: relative;
    	top: 3px;
    	left: 3px;
    }
    #body-sale-search .lq-form-group input[type=text]{
    	width:123px;
    }
    input[type=text]{
    	height:30px;
    }
    </style>
    <script type="text/javascript">
    $(document).ready(function(){
    	
    });
    
    /*****************全选*******************/
    function select_all(node, type)
    {
        node = node ? node : document ;
        $(node).find("input[name='check_batch_pick[]']:enabled").each(function(i){
    		this.checked = true;
    	});
    }

    /*****************反选*****************/
    function select_reverse(node)
    {
    	node = node ? node : document ;
    	$(node).find("input[name='check_batch_pick[]']:enabled").each(function(i){
    		this.checked = !this.checked;
    	});
    }

    
    function check_submit(node)
    {
    	$("#batch_print_pick").attr("disabled","disabled");
    	node = node ? node : document ;
    	item = $(node).find(':checkbox:checked');
    	console.log(item);
    	if (!item || item==undefined || item.length<1) {
    		alert('没有选中项');
    		$("#batch_print_pick").removeAttr("disabled");
    		return false;
    	}
    	var type = $('#task_type').val();
    	var orderList=new Array();
    	$("input[name='check_batch_pick[]']:checked").each(function(){
    		var orderId=$(this).closest("tr").find("td:last-child").find("input[type='hidden']").val();
    		//console.log(orderId);
    		orderList.push(orderId);
    	});
    	src="../product/printPrepack?type="+type+"&orderIdList="+orderList.toString().substring(0,orderList.toString().length);
    	console.log(src);
    	$("#print").attr("src",src);
    	$(".mw-loading").fadeOut(100);
    }
   
    </script>
</head>
<iframe id="print" src=""></iframe>
<body id="body-sale-search" style="padding: 0 10px;">
	<div class="modal-wrap mw-loading">
		<div class="modal-content mc-loading">
			<i class="fa fa-spinner fa-spin" id="loading"></i>
		</div>
	</div>
	<div class="main-container">
		<form method="post" action="../product/prepackList" class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="customer_id" style="text-indent:1rem;">选择货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
							<option value="">不限</option> 
							<c:forEach items="${customers}" var="customer">
								<option name="customer_id"
									class="${customer.customer_id}"
									<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
									value="${customer.customer_id}">${customer.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="task_status">任务单状态:</label>
						<%-- <input type="text" name="task_status" value="${task_status}" id="task_status" class="lq-form-control"> --%>
						<select name="task_status" id="task_status" class="lq-form-control">
							<option name="task_status" <c:if test="${status=='INIT' }">selected="true"</c:if> value="INIT" >未处理</option>
							<option name="task_status" <c:if test="${status=='RESERVED'} ">selected="true"</c:if> value="RESERVED" >已分配</option>
							<option name="task_status" <c:if test="${status=='RESERVE_FAILED' }">selected="true"</c:if> value="RESERVE_FAILED" >分配失败</option>
							<option name="task_status" <c:if test="${status=='CANCEL'} ">selected="true"</c:if> value="CANCEL" >已取消</option>
							<option name="task_status" <c:if test="${status=='FULFILLED'} ">selected="true"</c:if> value="FULFILLED" >已完成</option>
							<option name="task_status" <c:if test="${status=='IN_PROCESS' }">selected="true"</c:if> value="IN_PROCESS" >处理中</option>
							<option name="task_status" <c:if test="${status=='' }">selected="true"</c:if> value="" >不限</option>
							<option name="task_status" <c:if test="${status=='PART_FULFILLED' }">selected="true"</c:if> value="PART_FULFILLED" >部分完成</option>
						</select>
					</div>
					<div class="lq-form-group">
				     	<label for="warehosue_id" style="text-indent:3rem;">渠道:</label>
				     	<select name="warehouse_id" id="warehouse_id" class="lq-form-control">
				     		<option value="">不限</option>
				     		<c:forEach items="${warehouseList}" var="warehouse">
				     			<option value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
				     		</c:forEach>
				     	</select>						
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="oms_task_sn">OMS任务单号:</label>
						<input type="text" name="oms_task_sn" value="${oms_task_sn}" id="oms_task_sn" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="order_sn">WMS任务单号:</label>
						<input type="text" name="order_sn" value="${order_sn}" id="order_sn" class="lq-form-control">
					</div>
				</div>
				<div class="lq-col-3">
						<div class="lq-form-group">
							<label for="start">任务单时间:</label>
<%-- 							<input type="text" readonly="true" id="start" name="start" value="${start}" size="12" class="lq-form-control">
							<button id="startTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
								<i class="fa fa-calendar"></i>
							</button> --%>
							<input class="form-control form_datetime lq-form-control" id="start" type="text" name="start" value="${start}" size="12" readonly/>							
						</div>
						<div class="lq-form-group">
							<label for="end" style="padding-left:60px;">至</label>
<%-- 							<input type="text" readonly="true" id="end" name="end" value="${end}" size="12" class="lq-form-control"/>
							<button id="endTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
								<i class="fa fa-calendar"></i>
							</button> --%>
							<input class="form-control form_datetime lq-form-control" id="end" type="text" name="end" value="${end}" size="12" readonly/>							
						</div>
				</div>
				
 				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="task_type">任务类型:</label>
						<select name="task_type" id="task_type" class="lq-form-control"> 
					      	<option name="task_type" <c:if test="${'BOX_CLOSED'== type}">selected="true"</c:if>
					      	value="BOX_CLOSED">预打包(全打包)</option> 
					      	<option name="task_type" <c:if test="${'BOX_OPEN'== type}">selected="true"</c:if>
					      	value="BOX_OPEN">预打包(半打包)</option> 
							<option name="task_type"  <c:if test="${'UNPACK'== type}">selected="true"</c:if>
					      	value="UNPACK">拆分套餐</option>
						</select> 
					</div> 
					<div class="lq-form-group">
						<label for="order_sn">套餐条码:</label>
						<input type="text" name="barcode" value="${barcode}" id="barcode" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<button  id="btn-sale-search" class="lq-btn lq-btn-sm lq-btn-primary search">
							<i class="fa fa-search"></i>查询
						</button>					
					</div>
				</div>
			</div>
		</form>
	</div>
		<div style="clear:both;"></div>
		<div>
		<form method="post" id="form">
			<div style="margin: 10px 0 10px 0; clear:both;" id="buttons"> 
	      		 <input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="全选" onclick="select_all('#batchTable');" /> &nbsp;
	     			<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="反选" onclick="select_reverse('#batchTable');" /> &nbsp;
	        		<input class="lq-btn lq-btn-sm lq-btn-primary batch_print_pick" type="button" id="batch_print_pick" value="打印加工单" onclick="check_submit('#form')" />
	        		<input type="hidden" name="act" value="batch_print_pick" />
	      	</div>
		<table class="lq-table" id="batchTable">
		 	<thead>
				<tr>
					<th>编号</th>
					<th>货主</th>			
					<th>OMS任务单号</th>
					<th>WMS任务单号</th>
					<th>任务类型</th>
					<th>任务单时间</th>
					<th>任务单状态</th>					
					<th>套餐名称</th>
					<th>渠道</th>		
					<th>套餐条码</th>
					<th>预打包需求量</th>
					<th>实际打包数量</th>
					<th>组件商品</th>
					<th>组件商品数量</th>
					<th>活动开始时间</th>
					<th>活动结束时间</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody class="tbody">
				
			</tbody>
 			<c:if test="${fn:length(prepackGoodsList) == '0'}">
			</table>
				<div class="noDataTip">未查到符合条件的数据!</div>
			</c:if>

		</table>
		</form>
		</div>
		
		<div class="page-wrap">
			<div class="lq-row">
				<nav id="page-search">
					<ul class="lq-page-control">
						<li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>
						 <c:forEach var="item" varStatus="i" begin="1" end="${page.totalPage}">
							 <li class="page"><a href="javascript:void(0);"></a></li>
						 </c:forEach>
						<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li>
					</ul>
				</nav>
			</div>
		</div>
	</div>
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
		$(function () {
			// 分页
			var TOTAL_PAGE = 0,
				START_PAGE = 1,
				MAX_PAGE = 10,
				END_PAGE = 10,
				CURRENT_PAGE = 1,
				PAGE_DATA = {},
				PAGE = {};
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
						CURRENT_PAGE = index;
						$.ajax({
							url: '../product/prepackList',
							type : "post",
							dataType : "json",
							data : {
								currentPage : index,
								pageSize : 20,
								customer_id: $('#customer_id').val(),
								oms_task_sn: $('#oms_task_sn').val(),
								order_sn: $('#order_sn').val(),
								type: $('#task_type').val(),
								barcode: $('#barcode').val(),
								status: $('#task_status').val(),
								start: $('#start').val(),
								end: $('#end').val(),
								warehouse_id:$("#warehouse_id").val()
							},
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
			// 查询
			$(".search").click(function(e){
				$("#batch_print_pick").removeAttr("disabled");
				e.preventDefault();
				console.log($('#task_status').val());
				$('tbody.tbody').html("");
				search();
				//只有处理中时全选，反选，打印按钮才出现
				var status=$("#task_status").val();
				console.log(status);
				if(status=="RESERVED") {
		/* 			$("#buttons").find("input[type=button]").removeAttr("disabled"); */
					
					$("#buttons").show();
				}else {
					/* $("#buttons").find("input[type=button]").attr("disabled","disabled"); */
					
					$("#buttons").hide();
				}
			})
			
			function search () {
				
				$.ajax({
					url: '../product/prepackList',
					type: 'post',
					dataType: 'json',
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
					},
					data: {
						customer_id: $('#customer_id').val(),
						oms_task_sn: $('#oms_task_sn').val(),
						order_sn: $('#order_sn').val(),
						type: $('#task_type').val(),
						barcode: $('#barcode').val(),
						status: $('#task_status').val(),
						start: $('#start').val(),
						end: $('#end').val(),
						warehouse_id:$("#warehouse_id").val()
					},
					success: function (res) {
						console.log(res);
						$(".mw-loading").fadeOut(300);
 						$('.noDataTip').fadeOut(300);
 					
 						if (res.prepackGoodsList.length == 0) {
							$('.noDataTip').fadeIn(300);
						} 
						showTable(res); 
						// 分页
						PAGE_DATA = res.page;
						TOTAL_PAGE = parseInt(PAGE_DATA.totalPage);
						if (TOTAL_PAGE < MAX_PAGE) {
							END_PAGE = TOTAL_PAGE;
						} else {
							END_PAGE = MAX_PAGE;
						}
						CURRENT_PAGE = PAGE_DATA.currentPage;
						pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
						$('nav').after('<span class="totalPage">共'+PAGE_DATA.totalPage+'页,'+PAGE_DATA.totalCount+'条加工任务单</span>');
					}
				});
			}
			
			function transform(status_num) {
				var status="";
				switch(status_num) {
				case "INIT": status="未处理";break;
				case "RESERVED": status="已分配";break;
				case "RESERVE_FAILED": status="分配失败";break;
				case "CANCEL": status="已取消";break;
				case "FULFILLED": status="已完成";break;
				case "IN_PROCESS": status="处理中";break;
				case "PART_FULFILLED": status="部分完成";break;
				case "": status="不限";break;
				case "BOX_CLOSED": status="全打包";break;
				case "BOX_OPEN": status="半打包";break;
				case "UNPACK": status="拆分套餐";break;
				default: status="-";break;
				}
				return status;
			}
			
			function showTable (res) {
				var value = res.prepackGoodsList;
				console.log(value);
				var no;
				var tdHtml = '';
				for (var i in value) {
					//console.log(value[i].row_num);
					no=parseInt(i,10)+1;
					tdHtml+="<tr>";
  					if(value[i].row_num==0)
					{
						tdHtml+='<td style="display:none;"><label class="lb_firstd"><input type="checkbox" name="check_batch_pick[]" class="index"><span>'+no+'</span></label></td>';
					}
					if(value[i].row_num>0)
					{
						tdHtml+='<td rowspan="'+value[i].row_num+'"><label class="lb_firstd"><input type="checkbox" name="check_batch_pick[]" class="index"><span></span></label></td>';
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+value[i].name+'</td>';
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+value[i].oms_task_sn+'</td>';	
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+value[i].order_sn+'</td>';	
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+transform(value[i].type)+'</td>';	
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+value[i].created_time_+'</td>';	
						tdHtml+='<td id="order_id_'+value[i].order_id+'" rowspan="'+value[i].row_num+'">'+transform(value[i].status)+'</td>';	
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+value[i].product_name+'</td>';	
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+value[i].warehouse_name+'</td>';
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+value[i].barcode+'</td>';	
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+value[i].qty_need+'</td>';	
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+value[i].qty_actual+'</td>';
					}
					tdHtml+='<td >'+value[i].barcode2+'</td>';
					tdHtml+='<td >'+(value[i].number*value[i].qty_need)+'</td>';
					if(value[i].row_num>0){
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+value[i].activity_start_time_+'</td>';	
						tdHtml+='<td rowspan="'+value[i].row_num+'">'+value[i].activity_end_time_+'</td>';	
						tdHtml+='<td rowspan="'+value[i].row_num+'"><input type="hidden" value="'+value[i].order_id+'" id="orderID">';
						tdHtml+='<input type="hidden" value="'+value[i].customer_id+'" id="customerID">';
						tdHtml+='<button class="lq-btn lq-btn-sm lq-btn-primary" name="complete" >完结</button>';
						if(res.isOutWarehouse=="0"){
							tdHtml+='<input type="hidden" value="'+value[i].warehouse_id+'" id="warehouse_id"><button class="lq-btn lq-btn-sm lq-btn-primary" name="distribute" style="margin: 5px 0;" >库存分配</button></td>';
						}
							
					}
					tdHtml+="</tr>";
				}
				$('tbody.tbody').html(tdHtml);
				
				//$("#batchTable").rowspan(4);
				$("button[name='complete']").removeAttr("disabled");
				
				//判断合并的行数，重新编号
				var row=0;
 				$("tbody tr").each(function(i,val){
  					var row_span=$(this).find("td").eq(0).attr("rowspan");
 					if(row_span>1){
 						$(this).find("td").eq(0).find("span").text(i+1-row);
 						row=row+parseInt(row_span,10)-1;
 					}
 					else if(row_span==1){
 						$(this).find("td").eq(0).find("span").text(i+1-row);
 						
 					} 				
				})
				//对checkbox、完结按钮的可用情况的判断
				$("tbody tr").each(function(){
 					if($(this).find("td").eq(6).text()!="部分完成"&&$(this).find("td").eq(6).text()!="处理中")
						$(this).find("td:last-child").find("button[name='complete']").attr("disabled","disabled");
					if($(this).find("td").eq(6).text()!="已分配"){
						$(this).find("td").eq(0).find("input[type='checkbox']").css("display","none");
					}
					if($(this).find("td").eq(6).text()!="未处理"&&$(this).find("td").eq(6).text()!="分配失败")
						$(this).find("td:last-child").find("button[name='distribute']").attr("disabled","disabled");
				})
				

				
			}
		});
		//完结
		$(document).on("click","button[name=complete]",function(e){
			
			if(confirm("确认完结吗?")){
				$(this).attr("disabled","disabled");
				var orderID=$(this).parent().find("#orderID").val();
				e.preventDefault();
				console.log(orderID);
				if(orderID!=""){
					$.ajax({
						url:'../product/endPrepack',
						dataType: 'json',
						type: 'post',
						data: {
							order_id:orderID
						},
						success: function(res){
							console.log(res);
							if(res.result=='failure')
								alert(res.note);
							else
								alert("操作成功");
							//$(this).attr("disabled","disabled");
						},
						error: function(res){
							console.log(res);
							alert("出现错误!!");
							$(this).removeAttr("disabled");
						}
					})
				}
			}else{ return false;}

		})
		
		//库存 分配
		$(document).on("click","button[name=distribute]",function(e){
			e.preventDefault();
			$(this).attr("disabled","disabled");
			var type=$("#task_type").val();
			if(type == 'BOX_OPEN' || type == 'BOX_CLOSED'){
				type = 'PACK';
			}
			var order_id=$(this).parent().find("#orderID").val();
			var customer_id=$(this).parent().find("#customerID").val();
			var warehouse_id=$(this).prev("#warehouse_id").val();
			console.log("type:"+type+" order_id:"+order_id+" customer_id:"+customer_id);
			$.ajax({
				url:'../product/AllotByUser',
				dataType: 'json',
				type: 'post',
				data: {
					type: type,
					order_id: order_id,
					customer_id: customer_id,
					warehouse_id: warehouse_id
				},
				success: function(res){
					console.log(res);
					if(res.result=="success"){
						alert("分配成功!");
						console.log($(this).parent().parent().find("td").eq(6).text());
						console.log($("#order_id_"+order_id).text());
						$("#order_id_"+order_id).text("已分配");
					}else{
						alert("分配失败!");
						$(this).removeAttr("disabled");
						$("#order_id_"+order_id).text("分配失败");
					}
				},
				error: function(err){
					alert(err);
				}
			})
		})
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