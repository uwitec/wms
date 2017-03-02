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
<title>显示采购单</title>
<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<style>
body{
	  font-family: "微软雅黑";
}
.main-container {
	min-width: 1020px;
}
.search td{
	font-size: 13px;
}
.col-sm-2 {
  	display: inline-block;
}
.col-xs-2 {
  	display: inline-block;
}
.grad {
	background: -webkit-linear-gradient(rgba(253,254,253,0), rgba(243,243,243,1));
	background: -o-linear-gradient(rgba(253,254,253,0), rgba(243,243,243,1));
	background: -moz-linear-gradient(rgba(253,254,253,0), rgba(243,243,243,1));
	background: linear-gradient(rgba(253,254,253,0), rgba(243,243,243,1));
    border-bottom: 1px solid rgb(229, 229, 229);
  	border-top: 1px solid rgb(229,229,229);
  	text-align: center;
}
.form-horizontal .control-label {
   	float: none; 
  	text-align: left;
  	display: inline-block;
  	width: 7%;
  	margin-left: 10px;
}
input[type="text"]{
  	height: 30px;
}
.form-input{
	width: 160px;
}
.form_datetime{
	width: 160px;
}
.form-select{
	width: 160px;
}
.form-group {
  	margin-top: 5px;
}
.form-group-top{
	margin-top: 18px;
}
.moreLine {
	width: 100%;
}
.moreLineItem {
	display: inline-block;
}
.searchBtn {
	margin: 0 0 0 40px;
}
.dTime {
	margin: 0 0 0 30px;
}


#page-search {
	float: right;
}

</style>
</head>
<body id="body-purchase-accept">
	<div class="modal-wrap mw-loading">
        <div class="modal-content mc-loading">
            <i class="fa fa-spinner fa-spin" id="loading"></i>
        </div>
    </div>
	<div class="main-container">
	<form class="lq-form-inline" role="form" id="form-purchase-accept">
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
				<label for="order_sn">采购单号:</label>
				<input class="lq-form-control" id="order_sn" name="order_sn" type="text" value="${order_sn}"/>
			</div>
		</div>
		
		<div class="lq-col-3">
		<!-- 
			<div class="lq-form-group">
				<label for="warehouseList">收货仓库:</label>
				<select id="warehouseList" name="warehouseList" class="form-control form-select"> 
					<option value="">--请选择--</option> 
					  	<c:forEach items="${warehouseList}" var="warehouse"> 
					      	<option name="warehouse_id" 
					      	<c:if test="${warehouse_id==warehouse.warehouse_id}">selected="true"</c:if>
					      	value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option> 
					  	</c:forEach> 
				</select>
			</div>
		 -->	
			<div class="lq-form-group">
				<label class="col-sm-1 control-label" for="order_status">订单状态:</label>
				<select id="order_status" name="order_status" class="form-control form-select"> 
					<option value="">--请选择--</option>
					<option <c:if test="${order_status=='ACCEPT'}">selected="true"</c:if> value="ACCEPT">未处理</option>
					<option <c:if test="${order_status=='IN_PROCESS'}">selected="true"</c:if> value="IN_PROCESS">处理中</option>
					<option <c:if test="${order_status=='ON_SHELF'}">selected="true"</c:if> value="ON_SHELF">部分上架</option>
					<option <c:if test="${order_status=='FULFILLED'}">selected="true"</c:if> value="FULFILLED">已完结</option>
					<option <c:if test="${order_status=='CANCEL'}">selected="true"</c:if> value="CANCEL">已取消</option>
					<option <c:if test="${order_status=='ABORTED '}">selected="true"</c:if> value="ABORTED">已中止</option>
				</select> 
			</div>
			
			<div class="lq-form-group moreLineItem">
				<label for="order_sn2">出库单号:</label>
	         	<input class="lq-form-control" id="order_sn2" name="order_sn2" type="text" value="${order_sn2}"/>
			</div>
		</div>
		<div class="lq-col-3">
			<div class="lq-form-group">
				<label for="batch_order_sn">批次号:</label>
				<input class="lq-form-control" id="batch_order_sn" name="batch_order_sn" type="text" value="${batch_order_sn}" />
			</div>
			
			<div class="lq-form-group">
				<label for="provider_name">供应商:</label>
	         	<input class="lq-form-control" id="provider_name" name="provider_name" type="text" value="${provider_name}" />
			</div>
		</div>
		<div class="lq-col-3">
			<label for="warehosue_id">渠道:</label>
			<select name="warehouse_id" id="warehouse_id" style="width: 65%;">
				<option value="">不限</option>
				<c:forEach items="${warehouseList}" var="warehouse">
						<option value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
				</c:forEach>
			</select>
		</div>
		<div class="moreLine">
			<div class="buyTime moreLineItem">
				<label class="col-sm-1 control-label" for="start">下单时间:</label>
		      	<div class="col-sm-2">
		         	<input class="form-control form_datetime" id="start" type="text" name="start" value="${start}" size="16"/>
		      	</div>
		      	<label class="col-sm-1 control-label" for="end">至</label>
		      	<div class="col-sm-2">
		         	<input class="form-control form_datetime" id="end" type="text" name="end" value="${end}" size="16"/>
		      	</div>
				<label for="arrave_time" class="dTime">到货时间:</label>
	         	<input class="form-control form_datetime" id="arrive_time_start" name="arrive_time_start" type="text" value="${arrive_time_start}"/>
				
				<label for="arrave_time">至</label>
	         	<input class="form-control form_datetime" id="arrive_time_end" name="arrive_time_end" type="text" value="${arrive_time_end}"/>
		      	<button class="lq-btn lq-btn-sm lq-btn-primary searchBtn">
		      		查询
		      	</button>
		    	<input class="form-control"  name="act" type="hidden" value="search"/>
		    </div>
		</div>
	</form>
	
	<table class="lq-table" style="margin-top:10px;" id="batchTable">
		<thead>
			<tr>
				<th>批次号</th>
				<th>采购订单号</th>
				<th>货主</th>
				<th>渠道</th>
				<th>采购时间</th>
				<th>商品</th>			
				<th>收货仓库</th>
				<th>预期到货时间</th>
				<th>供应商名称</th>
				<th>订单状态</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
	<div class="noDataTip">未查到符合条件的数据!</div>
	<div class="page-wrap"></div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script type="text/javascript" src="../static/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../static/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/v3.js"></script>
<script type="text/javascript">
$(document).ready(function(){

	var TOTAL_PAGE = 0, START_PAGE = 1, MAX_PAGE = 10, END_PAGE = 10, CURRENT_PAGE = 1, PAGE_DATA = {};

	//查询采购单
	$(".searchBtn").on("click",function(e){
		e.preventDefault();
		var data = $("#form-purchase-accept").serialize(),that = $(this);
		//console.log(data);
		$.ajax({
			type: 'post',
			url: '../purchase/accept_ajax',  
			data: data,
			dataType:'json',
			beforeSend : function() {
                $(".mw-loading").fadeIn(300);
                that.attr("disabled",true);
            },
			success: function(data) { 
				$(".mw-loading").fadeOut(300);
                that.attr("disabled",false);
				$('.noDataTip').fadeOut(300);
				console.log(data);
				PAGE_DATA = data.page;
				TOTAL_PAGE = parseInt(data.page.totalPage);
				START_PAGE = 1;
				if (TOTAL_PAGE < MAX_PAGE) {
					END_PAGE = TOTAL_PAGE;
				} else {
					END_PAGE = MAX_PAGE;
				}
				CURRENT_PAGE = data.page.currentPage;
				pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
				tableConstructor(data.purchaseOrderList);
				if (!data.purchaseOrderList.length) {
					$(".mw-loading").fadeOut(300);
                	that.attr("disabled",false);
					$('.noDataTip').fadeIn(300);
					$("#batchTable tbody").html("");
				}
			},
			error:function(data){
				$(".mw-loading").fadeOut(300);
                that.attr("disabled",false);
				alert("accept_ajax接口调用失败");
			}
		});
	});

	//构造采购单列表
	function tableConstructor (data) {
		//console.log(data);
		var tableHtml = '';
		for (var i in data) {
			if (data[i].change_quantity == "") {
				tableHtml += '<tr align="center" class="list"   title="还未入库">';
			} else if(data[i].goods_number > data[i].change_quantity){
				tableHtml += '<tr align="center" class="list" style="background:rgb(219, 219, 242);"  title="部分入库">';
			}else {
				tableHtml += '<tr align="center" class="list"  title="全部入库">';
			}
			
			if (data[i].batch_sn_num > 0) {
				tableHtml += '<td rowspan="'+data[i].batch_sn_num+'">';
				if (data[i].batch_order_sn == '' || data[i].batch_order_sn == null) {
					tableHtml += '-';
				} else {
					tableHtml += '<a target="_blank" href="../purchase/edit_batch_sn?batch_order_sn='
					+ data[i].batch_order_sn + '">' + data[i].batch_order_sn + '</a></td>';
				}
			}
			if (data[i].row_num > 0) {
				tableHtml += '<td rowspan="'+data[i].row_num+'"><a target="_blank" href="../purchase/edit?oms_order_sn='+data[i].oms_order_sn+'">'+data[i].oms_order_sn+'</a></td>';
			}

			tableHtml += '<td>'+data[i].name+'</td>';
			tableHtml += '<td>'+data[i].warehouse_name+'</td>';

			function toTime (time) {
				var date = new Date(time);
				return date.getFullYear()+'-'+(date.getMonth() + 1)+'-'+date.getDate()+' '+date.toString().split(' ')[4];
			}
			tableHtml += '<td>'+data[i].order_time+'</td>';
			tableHtml += '<td>'+data[i].goods_name+'</td>';
			tableHtml += '<td>'+data[i].warehouse_name+'</td>';
			tableHtml += '<td>'+data[i].arrival_time+'</td>';
			tableHtml += '<td>'+data[i].provider_name+'</td>';
			if (data[i].order_status == "ACCEPT") {
				tableHtml += '<td>未处理</td></tr>';
			}
			if (data[i].order_status == "IN_PROCESS") {
				tableHtml += '<td>处理中</td></tr>';
			}
			if (data[i].order_status == "ON_SHELF") {
				tableHtml += '<td>部分上架</td></tr>';
			}
			if (data[i].order_status == "FULFILLED") {
				tableHtml += '<td>已完结</td></tr>';
			}
			if (data[i].order_status == "CANCEL") {
				tableHtml += '<td>已取消</td></tr>';
			}
			if(data[i].order_status=="ABORTED"){
				tableHtml += '<td>已中止</td></tr>';
			}
		}
		$("#batchTable tbody").html(tableHtml);
	}

	//分页
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
				pageHtml += '<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li></ul></nav></div>';
			}
			
			$(".page-wrap").html(pageHtml);
		} else {
			$(".page-wrap").html("").fadeOut(300);
		}
	}

	$("#body-purchase-accept").on("click",".lq-page-control li",function(e){
		e.preventDefault();
		var that = $(this);
		if (!that.hasClass("prev") && !that.hasClass("next")) {
			if (!that.hasClass("active")) {
				var index = parseInt($(this).find("a").text());
				CURRENT_PAGE = index;
				var data = $("#form-purchase-accept").serialize();
				data+="&currentPage="+index+"&pageSize=10";
				$.ajax({
					url : "../purchase/accept_ajax",
					type : "get",
					dataType : "json",
					data : data,
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
						that.addClass("active").siblings().removeClass("active");
					},
					success : function(data) {
						$(".mw-loading").fadeOut(300);
						tableConstructor(data.purchaseOrderList);
					},
					error : function(error) {
						$(".mw-loading").fadeOut(300);
						alert("accept_ajax接口调用失败");
						console.log(error);
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
			pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
		}
	});

	$('.form_datetime').datetimepicker({
        language:  'zh',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		forceParse: 0,
        showMeridian: 1
    });;
});
</script>
</body>
</html>