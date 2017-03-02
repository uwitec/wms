<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>转快递</title>
    <c:set var="path" value="${pageContext.request.contextPath}" scope="page"></c:set>
    <script type="text/javascript">
    var basePath='${path}';
    </script>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link href="${path}/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="${path}/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="${path}/static/css/global.css">

    <style type="text/css">
    	.moreLineItem {
    		display: inline-block;
    	}
    	.col-sm-2 {
    		display: inline-block;
    	}
    	input[type=text] {
    		height: 30px;
    	}
    	.main-container {
    		padding: 20px;
    	}
    	#chart {
    		width: 600px;
    		height: 500px;
    		margin: 40px auto;
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
    </style>
</head>
<body>
	<div class="modal-wrap mw-loading">
        <div class="modal-content mc-loading">
            <i class="fa fa-spinner fa-spin" id="loading"></i>
        </div>
    </div>
	<div class="main-container">
		<form class="lq-form-inline" role="form" id="form-select" onsubmit="return false;" method = "post">
		  <div class="row-fluid">
			<div class="span4">
				<div class="lq-form-group">
					<label for="customer_id">选择货主:</label>
					<select name="customer_id" id="customer_id" class="lq-form-control">
						<option  value="0">--不限--</option>
						<c:forEach items="${customers}" var="customer">
							<option
								<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
								value="${customer.customer_id}">${customer.name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="span4">
				<div class="lq-form-group">
					<div class="lq-form-group">
						<label for="oms_shop_id">选择店铺:</label>
							<select name="oms_shop_id" id="oms_shop_id" class="lq-form-control">
								<option class="0" value="0">--不限--</option>
								<c:forEach var="customerShop" items="${shopNameLists}">
									<c:forEach items="${customerShop[1].shopName}" var="shopName">
										<c:if test="${!empty customerShop[1].shopName}">
											<option
											<c:if test="${oms_shop_id==shopName.oms_shop_id}">selected="true"</c:if>
											 class="${customerShop[0].customerId}" value="${shopName.oms_shop_id}">${shopName.shop_name}</option>
										</c:if>
									</c:forEach>
								</c:forEach>
							</select>
					</div>
				</div>
			</div>
			<div class="span4">
				<div class="lq-form-group">
					<div class="lq-form-group">
						<label for="shipping_id">原快递:</label>
							<select name="shipping_id" id="shipping_id" class="lq-form-control">
								<option value="0">--不限--</option>
								<c:forEach var="ship" items="${shippinglist}">
									<option value="${ship.shipping_id}">${ship.shipping_name }</option>
								</c:forEach>
							</select>
					</div>
				</div>
			</div>
		  </div>
		  <div class="row-fluid">
		  	<div class="span4">
				<div class="lq-form-group">
					<label for="warehouse_id">选择渠道:</label>
					<select name="warehouse_id" id="warehouse_id" class="lq-form-control">
						<option value="0">--请选择--</option> 
						<c:forEach items="${warehouseList}" var="warehouse">
							<option name="warehouse_id"
								class="${warehouse.warehouse_id}"
								<c:if test="${warehouse_id==warehouse.warehouse_id}">selected="true"</c:if>
									value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="span4">
				<div class="lq-form-group buyTime moreLineItem">
				 	<label class="col-sm-1 control-label" for="start">订单时间:</label> 
			      	<div class="col-sm-2">
			         	<input class="form-control form_datetime" id="start" type="text" name="start" value="${start}" size="16"/>
			      	</div>
			      	<label class="col-sm-1 control-label" for="end">至</label>
			      	<div class="col-sm-2">
			         	<input class="form-control form_datetime" id="end" type="text" name="end" value="${end}" size="16"/>
			      	</div>

			      	<button type="button" class="lq-btn lq-btn-sm lq-btn-primary" id="searchorder">
			      		查询
			      	</button>
			    </div>
			</div>
		  </div>
		  <div class="row-fluid">
			<div class="span10">
				<div class="lq-form-group">
					<label for="target_shipping_id">目标快递:</label>
						<select name="target_shipping_id" id="target_shipping_id" class="lq-form-control">
							<c:forEach var="ship" items="${targetShippinglist}">
								<option value="${ship.shipping_id}">${ship.shipping_name }</option>
							</c:forEach>
						</select>
					<button type="button" class="lq-btn lq-btn-sm lq-btn-primary" id="confirmupdate">确认修改快递</button>
				</div>
			</div>
		  </div>
		  <div class="row-fluid">
			<div class="span6">
				<div class="lq-form-group">
					<button type="button" class="lq-btn lq-btn-sm lq-btn-primary" id="chooseall">全选</button>
					<button type="button" class="lq-btn lq-btn-sm lq-btn-primary" id="chooseopposite">反选</button>
				</div>
			</div>
		  </div>
		</form>
		<div class="row-fluid">
			<table class="lq-table" id="orderlist">
				<thead>
		        	<tr>
		        		<th class="cancel">选择</th>
			            <th>货主</th>
			            <th>wms订单号</th>
			            <th>快递方式</th>
			            <th>渠道</th>
			            <th>订单状态</th>
			            <th>订单时间</th>
		        	</tr>
		        </thead>
		        <tbody></tbody>
			</table>
			<div class="noDataTip">未查到符合条件的数据!</div>
    		<div class="page-wrap"></div>
		</div>
	</div>
<script type="text/javascript" src="${path}/static/js/jquery.min.js"></script>
<script type="text/javascript" src="${path}/static/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${path}/static/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
 	var TOTAL_PAGE = 0, START_PAGE = 1, MAX_PAGE = 10, END_PAGE = 10, CURRENT_PAGE = 1, PAGE_DATA = {}, TOTAL_ORDER = 0;
			
			var PAGE = {

			};
	$("#oms_shop_id").find("option:gt(0)").not($("."+$("#customer_id").val())).css("display","none");

	/* console.log("ShopName:"+$("#shopName").val()); */
	$("#customer_id").on("change",function(){
		$("#oms_shop_id").find("option:gt(0)").css("display","block").removeAttr("selected");
		var customerId=$("#customer_id").val();
		$("#oms_shop_id").find("option:gt(0)").not($("."+customerId)).css("display","none");
		$("#oms_shop_id").find("option").eq(0).attr("selected",true);
		
	});
	
	$(".form-control").on("focus",function(){
		$("th.prev span").text("<");
		$("th.next span").text(">");
	});
	
///////////////时间控件////////////////////

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
///////////查询//////////

 $("#searchorder").click(function(e){
	var data={
			customer_id: $("#customer_id").val()||'',
			warehouse_id: $("#warehouse_id").val()||0,
			shop_id:$("#oms_shop_id").val()||0,
			shipping_id:$("#shipping_id").val()||0,
			start: $("#start").val()||'',
			end: $("#end").val()||''
	};
	ajaxform(basePath+"/sale/getShipOrders",data,1,10);
});
	function pageConstructor(currentPage,startPage,endPage) {
				//alert("currentPage:"+currentPage+",startPage:"+startPage+",endPage:"+endPage);
				if (TOTAL_PAGE) {
					$(".page-wrap").html("").fadeIn(300);
					var pageHtml = '<div class="lq-row"><nav id="page-search"><ul class="lq-page-control">';
					if (startPage !== 1) {
						pageHtml +='<li class="prev"><a href="javascript:;" aria-label="Previous" alt="Previous">&laquo;</a></li>';
					}
					for (var i=startPage;i<startPage+endPage&&i<=TOTAL_PAGE;i++) {
						pageHtml += '<li';
						if (i == currentPage) {
							pageHtml += ' class="active"';
						}
						pageHtml += '><a href="javascript:;">' + i + '</a></li>';
					}
					if (startPage+endPage-1 !== TOTAL_PAGE) {
						pageHtml += '<li class="next"><a href="javascript:;" aria-label="Next" alt="Next">&raquo;</a></li>';
					}
					pageHtml += '<span class="totalPage">共 <span>'+TOTAL_PAGE+'</span> 页, '+TOTAL_ORDER+' 个订单</span></ul></nav></div>';
					$(".page-wrap").html(pageHtml);
				} else {
					$(".page-wrap").html("").fadeOut(300);
				}
			}
	function tableConstructor(data) {
		$("#orderlist tbody").html("");
		if (data.length) {
			$('.noDataTip').fadeOut(300);
			var tableHtml = '';
			for (var i in data) {
				$("#orderlist tbody").append("<tr><td>"
					+'<input type="checkbox" name="selectedorder" value="'+data[i].order_id+'"/>'+"</td><td>"
					+data[i].customer_name+"</td><td>"
					+data[i].order_id+"</td><td>"
					+(data[i].shipping_name||'')+"</td><td>"
					+data[i].warehouse_name+"</td><td>"
					+statusFormat(data[i].order_status)+"</td><td>"
					+data[i].order_time+"</td></tr>");
			}
		} else {
			$('.noDataTip').fadeIn(300);
		}
		$("#form").show();
	}
	function statusFormat(status){
		switch(status){
		   case "ACCEPT":return "仓库接单（未处理）";
		   case "BATCH_PICK":return "生成波次单";
		   case "PICKING":return "拣货中";
		   case "RECHECKED":return "已复核";
		   case "WEIGHED":return "已称重";
		   case "DELIVERED":return "已发货";
		   case "IN_PROCESS":return "处理中";
		   case "ON_SHELF":return "待上架";
		   case "FULFILLED":return "已完成";
		   case "EXCEPTION":return "异常";
		   case "CANCEL":return "已取消";
		}
	}
	$("#chooseall").click(function(){
		$("input[name='selectedorder']").prop("checked",true);
	});
	$("#chooseopposite").click(function(){
		var checked = $("input[name='selectedorder']:checked");
		var unchecked = $("input[name='selectedorder']:not(:checked)");
		checked.prop("checked",false);
		unchecked.prop("checked",true);
	});
	$("#confirmupdate").click(function(){
		var checked = $("input[name='selectedorder']:checked");
		var target_shipping_id = $("#target_shipping_id").val();
		if(!target_shipping_id){
			return alert("未选择目标快递");
		}
		if(!checked.length){
			return alert("请至少选择一个订单");
		}
		var ids=new Array();
		checked.each(function(){
			ids.push($(this).val());
		});
		$.post(basePath+"/sale/commitchangeship",{shippingId:target_shipping_id,id:ids},function(res){
			if(res.code==0){
				alert("操作成功");
				ajaxform($(".page-wrap").data("url"), $(".page-wrap").data("formdata"),1,10);
			}else{
				alert(res.msg);
			}
		},'json');
	});
	$(document).on("click",".lq-page-control li",function(){
		var that = $(this);
		console.log(that);
		if (!that.hasClass("prev") && !that.hasClass("next")) {
			if (!that.hasClass("active")) {
				var index = $(this).find("a").text();
				if(!index){
					return;
				}
				var data = {
					currentPage : index,
					pageSize : 100,
					customer_id: $("#customer_id").val()||'',
					shop_id:$("#oms_shop_id").val()||0,
					shipping_id:$("#shipping_id").val()||0,
					start: $("#start").val()||'',
					end: $("#end").val()||''
				};
				ajaxform(basePath+"/sale/getShipOrders",data,that);
			}
		}else if (that.hasClass("next")) {
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
	function ajaxform(url,data,startPage,endPage){
		$(".page-wrap").data("url",url);
		$(".page-wrap").data("formdata",data);
		$.ajax({
			url : url,
			type : "get",
			dataType : "json",
			data : data,
			beforeSend : function(){
				$(".mw-loading").fadeIn(300);
			},
			success : function(data) {
				window.setTimeout(function(){
					$(".mw-loading").fadeOut(300);
					TOTAL_PAGE = parseInt(data.page.totalPage);
					TOTAL_ORDER = parseInt(data.page.totalCount);
					if(startPage&&endPage){
						pageConstructor(data.page.currentPage,startPage,endPage);
					}else{
						startPage.addClass("active").siblings().removeClass("active");
					}
					tableConstructor(data.orderList);
				},300);
			},
			error : function(error) {
				window.setTimeout(function(){
					$(".mw-loading").fadeOut(300);
					console.log(error);
				},300);
			}
		});
	}
});
</script>
</body>
</html>