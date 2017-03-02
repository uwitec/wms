<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@page import="java.util.logging.Formatter"%>
<%@page import="org.apache.velocity.runtime.directive.Foreach"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>  
<!DOCTYPE html>
<html>
 <head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <title>供应商退货</title>
  <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/autocomplete.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
  <!-- Loading Calendar JavaScript files -->
  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/zapatec.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/calendar.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/calendar-en.js"></script>
   <link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
	<style type="text/css">
	.endDate {
		display: inline-block;
		margin: 10px 0 0 78px;
	}
	.searchBtn {
		display: inline-block;
		margin: 10px;
	}
	#product_name {
		margin: 0 0 0 1em;
	}
	#to_date {
		margin: 0 0 0 -0.6em;
	}
	#print{
	  width: 0px;
	  height: 0px;
	  border: 0px;
	}
	#page-search {
		float: right;
	}
	.noDataTip {
    	display: none;
    	text-align: center;
    	font-size: 30px;
    	color: #666;
    	margin: 30px 0 0 0;
    }
	</style>
  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/autocomplete.js"></script>
  <script type="text/javascript" >
  function chkDateformat(){
      var start_date = document.getElementById('from_date');
      var end_date = document.getElementById('to_date');

      pattern_date = /^\d{4}-(0?[1-9]|1[0-2])-(0?[1-9]|[1-2]\d|3[0-1])$/;
      if(start_date.value != ''){
   	   if(!pattern_date.test(start_date.value)){
       	   alert('起始输入的时间格式不对，请重新输入。');
       	   start_date.select();
       	   return false;
   	   }
      }

      if(end_date.value != ''){
   	   if(!pattern_date.test(end_date.value)){
       	   alert('结束输入的时间格式不对，请重新输入。');
       	   end_date.select();
       	   return false;
   	   }
      }
      if(start_date.value == '' && end_date.value == ''   )
       {
    	  alert('不能全部都是空');
    	  return false;
       }
   }
  /*****************全选*******************/
  function select_all(node, type)
  {
      node = node ? node : document ;
      $(node).find("input[name='check_order_id[]']:enabled").each(function(i){
  		this.checked = true;
  	});
  }

  /*****************反选*****************/
  function select_reverse(node)
  {
  	node = node ? node : document ;
  	$(node).find("input[name='check_order_id[]']:enabled").each(function(i){
  		this.checked = !this.checked;
  	});
  }

  /*****************清空*******************/
  function select_none(node, type)
  {
      node = node ? node : document ;
      $(node).find("input[name='check_order_id[]']:enabled").each(function(i){
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
  	$("input[name='check_order_id[]']:checked").each(function(){ 
          orderList.push($(this).val());
  	})
  	src="../supplierReturn/goodsReturnPrint?batchOrder="+orderList;
	$('#print').attr('src',src); 
  }
  
  </script>
  
 </head>
 <iframe id="print" src=""></iframe>
 <body id="body-sale-search">
 	<div class="main-container">
	    <form method="post" action="../supplierReturn/goodsReturn" class="lq-form-inline">
		    <div class="lq-row">
	    		<div class="lq-col-3">
	    			<div class="lq-form-group">
	    				<label for="customer_id">选择货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
			                <option value="" >--请选择--</option>
			                   <c:forEach items="${warehouseCustomerList}" var="warehouseCustomer"> 
							    <option 
							    <c:if test="${customer_id==warehouseCustomer.customer_id}">selected="true"</c:if>
							    value="${warehouseCustomer.customer_id}">${warehouseCustomer.name}</option> 
							</c:forEach> 
			            </select>
	    			</div>
	    			<div class="lq-form-group">
	    				<label for="product_name">商品名:</label>
						<input type="text" id='product_name' name='product_name' value="${product_name}" class="lq-form-control" />
	    			</div>
	    			
	    		</div>
	    		<div class="lq-col-3">
	    			<div class="lq-form-group">
	    				<label for="warehousename">退货仓库:</label>
	    				<select name="warehouse_id" id="warehouse_id" class="lq-form-control">
			                <option value="" >全部</option>
			                   <c:forEach items="${warehouseList}" var="warehouse"> 
							    <option 
							    <c:if test="${warehouse_id==warehouse.warehouse_id}">selected="true"</c:if>
							    value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option> 
							</c:forEach> 
			            </select>
	    			</div>
	    			<div class="lq-form-group">
	    				<label for="">操作状态:</label>
	    				<select id="order_status" name="order_status" class="lq-form-control">
		                	<option value="ACCEPT" <c:if test="${'ACCEPT'== order_status}">selected="true"</c:if>>未处理</option>
		                  	<option value="RESERVED" <c:if test="${'RESERVED'== order_status}">selected="true"</c:if>>分配成功</option>
		                  	<option value="FULFILLED" <c:if test="${'FULFILLED'== order_status}">selected="true"</c:if>>已退还</option>
		                  	<option value="CANCEL" <c:if test="${'CANCEL'== order_status}">selected="true"</c:if>>已取消</option>
		                </select>
	    			</div> 
	    		</div>
	    		<div class="lq-col-6">
	    			<div class="lq-form-group">
	    				<div>
		    				<label for="">创建时间:</label>
		    				<input type="text" id='created_time' name='created_time' value="${created_time}" class="lq-form-control" />
		    				<button id="from_date_trigger" name="from_date_trigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon" >
		    					<i class="fa fa-calendar"></i>
		    				</button>
		    				</div>
	    				<div class="endDate">
		               		<input type="text" id='delivery_time' name='delivery_time' value="${delivery_time}"  class="lq-form-control"/>
		               		<button type="button" id="to_date_trigger" name="to_date_trigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon" >
		               			<i class="fa fa-calendar"></i>
		               		</button>
		               	</div>
		               	<button type="submit" id="btn-sale-search" class="searchBtn lq-btn lq-btn-sm lq-btn-primary">
							<i class="fa fa-search"></i>查询
			  			</button>
	    			</div>
	    		</div>
	    	</div>
	    </form>
	    <div>
	    <form method="post" id="form">
		    <div style="margin: 10px 0 10px 0; clear:both;"> 
	      		 <input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="全选" onclick="select_all('#returngoods-table');" /> &nbsp;
	   			<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="清空" onclick="select_none('#returngoods-table');" /> &nbsp;
	   			<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="反选" onclick="select_reverse('#returngoods-table');" /> &nbsp;
	       		<input class="lq-btn lq-btn-sm lq-btn-primary batch_print_order" type="button" id="batch_print_order" value="打印选中出库单" onclick="check_submit('#form')" />
	       		<input type="hidden" name="act" value="batch_print_order" />
	     	</div>
	<table id = 'returngoods-table' class="lq-table">
      	<thead>
			<tr>
				<th>选择</th>
				<th style="width:106px; min-width: 92px;">货主</th>
				<th style="width:106px; min-width: 92px;">订单号</th>
				<th style="width:106px; min-width: 92px;">操作状态</th>
				<th style="width:106px; min-width: 92px;">商品条码</th>
				<th style="width:106px; min-width: 380px;">商品名</th>
				<th style="width:106px; min-width: 62px;">箱规</th>
				<th style="width:106px; min-width: 92px;">货物批次</th>
				<th style="width:106px; min-width: 130px;">仓库</th>
				<th style="width:106px; min-width: 92px;">库存状态</th>
				<th style="width:106px; min-width: 92px;">下单出库数</th>
				<th style="width:106px; min-width: 62px;">已退数</th>
				<th style="width:106px; min-width: 92px;">调整价格</th>
				<th style="width:106px; min-width: 130px;">申请时间</th>
				<th style="width:106px; min-width: 130px;">出库时间</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
    </table>
    <div class="noDataTip">未查到符合条件的数据!</div>
    <div class="page-wrap"></div>
    </form>
</div>
  <script type="text/javascript">
    Zapatec.Calendar.setup({
  	    weekNumbers       : false,
  	    electric          : false,
  	    inputField        : "created_time",
  	    button            : "from_date_trigger",
  	    ifFormat          : "%Y-%m-%d",
  	    daFormat          : "%Y-%m-%d"
      });
    Zapatec.Calendar.setup({
  	    weekNumbers       : false,
  	    electric          : false,
  	    inputField        : "delivery_time",
  	    button            : "to_date_trigger",
  	    ifFormat          : "%Y-%m-%d",
  	    daFormat          : "%Y-%m-%d"
     });
  </script>
  <script>
		  
		$(document).ready(function(){
			// $("#form").hide();
			
			$("#btn-sale-search").on("click",function(e){
				e.preventDefault();
				var that = $(this);
				var goodReturnData = {
					customer_id : $("#customer_id").val(),
					product_name : $("#product_name").val(),
					created_time : $("#created_time").val(),
					delivery_time : $("#delivery_time").val(),
					order_status : $("#order_status").val(),
					warehouse_id : $("#warehouse_id").val()
				};
				//console.log($("#product_name").val());
				$.ajax({
					url : "../supplierReturn/searchReturn",
					type : "get",
					dataType : "json",
					data : goodReturnData,
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
						that.prop("disabled",true);
					},
					success : function(data) {
						window.setTimeout(function(){
							console.log(data);
							$(".mw-loading").fadeOut(300);
							that.prop("disabled",false);
							pageConstructor(data.page);
							tableConstructor(data.returnGoodslist);
						},300);
					},
					error : function(error) {
						window.setTimeout(function(){
							$(".mw-loading").fadeOut(300);
							that.prop("disabled",false);
							alert("操作失败，请刷新!");
							console.log(error);
						},300);
						that.prop("disabled",false);
					}
				});
			});

			function pageConstructor(data) {
				//console.log(data);
				if (data.totalPage) {
					$(".page-wrap").html("").fadeIn(300);
					var pageHtml = '<div class="lq-row"><nav id="page-search"><ul class="lq-page-control"><li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>';
					for (var i=1;i<data.totalPage+1;i++) {
						pageHtml += '<li';
						if (i == data.currentPage) {
							pageHtml += ' class="active"';
						}
						pageHtml += '><a href="#">' + i + '</a></li>'
					}
					pageHtml += '<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li></ul></nav></div>';
					//console.log("pageHtml:"+pageHtml);
					$(".page-wrap").html(pageHtml);
				} else {
					$(".page-wrap").html("").fadeOut(300);
				}
			}

			function tableConstructor(data) {
				console.log(data);
				if (data.length) {
					$('.noDataTip').fadeOut(300);
					var tableHtml = '';
					for (var key in data) {
						if(data[key].order_status == 'RESERVED'){
							tableHtml +='<tr><td><input type="checkbox" name="check_order_id[]" value="'+data[key].order_id+'" class="check" /></td><td>';
						}else{
							tableHtml += '<tr><td></td><td>';
						}
						
						tableHtml += data[key].name+'</td><td>'
								  + data[key].order_id+'</td><td>';
						if(data[key].order_status == 'RESERVED' ) {       
							tableHtml +='<a id="33" href="goodsEdit?order_goods_id='+data[key].order_goods_id+'">分配成功</a></td><td>';
						}else if(data[key].order_status == 'FULFILLED'){
							tableHtml +='已退还'+'</td><td>';
						}else if(data[key].order_status == 'ACCEPT'){
							tableHtml +='未处理'+'</td><td>';
						}else if(data[key].order_status == 'CANCEL'){
							tableHtml +='已取消'+'</td><td>';
						}else{
							tableHtml +='</td><td>';
						}
						tableHtml += data[key].barcode + '</td><td>'
						          + data[key].product_name + '</td><td>'
						          + data[key].spec + '</td><td>'
						          + data[key].batch_sn + '</td><td>'
						          + data[key].warehouse_name + '</td><td>'
						          + data[key].goods_status + '</td><td>'
						          + data[key].goods_number + '</td><td>'
						          + data[key].deliver_num + '</td><td>'
						          + data[key].goods_price + '</td><td>'
						          + data[key].created_time + '</td><td>'
						          + data[key].delivery_time +'</td></tr>';
					}
					$("#returngoods-table tbody").html(tableHtml);
					$("#returngoods-table").fadeIn(300);
				} else {
					$("#returngoods-table tbody").html("");
					// $("#returngoods-table").hide();
					$('.noDataTip').fadeIn(300);
				}
				$("#form").show();
			}

			$("#body-sale-search").on("click",".lq-page-control li",function(){
				var that = $(this);
				console.log(that);
				if (!that.hasClass("prev") && !that.hasClass("next")) {
					if (!that.hasClass("active")) {
						var index = $(this).index();
						console.log("index:"+index);
						var data = {
							currentPage : index,
							pageSize : 10,
							customer_id : $("#customer_id").val(),
							product_name : $("#product_name").val(),
							created_time : $("#created_time").val(),
							delivery_time : $("#delivery_time").val(),
							order_status : $("#order_status").val(),
							warehouse_id : $("#warehouse_id").val()
						}
						$.ajax({
							url : "../supplierReturn/searchReturn",
							type : "get",
							dataType : "json",
							data : data,
							beforeSend : function(){
								$(".mw-loading").fadeIn(300);
								that.addClass("active").siblings().removeClass("active");
							},
							success : function(data) {
								window.setTimeout(function(){
									$(".mw-loading").fadeOut(300);
									tableConstructor(data.returnGoodslist);
								},300);
							},
							error : function(error) {
								window.setTimeout(function(){
									$(".mw-loading").fadeOut(300);
									alert("s");
									console.log(error);
								},300);
							}
						});
					}
				}
			});
		});
	</script>
	</div>
 </body>
</html>


