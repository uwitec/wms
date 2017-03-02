<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>采购订单商品明细</title>
<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/global.css" rel="stylesheet" type="text/css">
<style>
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
  	width: 120px;
  	margin-left: 10px;
}
input[type="text"]{
  	height: 30px;
}
.form-input{
	width: 60px;
}
.form_date{
	width: 90px;
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
.guidance{
	text-align: center;
	margin: 18px 0px;
}
.nav{
	-moz-border-radius: 20px; /* Firefox */
  	-webkit-border-radius: 20px; /* Safari 和 Chrome */
  	border-radius: 20px; /* Opera 10.5+, 以及使用了IE-CSS3的IE浏览器 */
  	margin: 0;
}
#print{
  width: 0px;
  height: 0px;
  border: 0px;
}
.delete {
	cursor: pointer;
}
</style>

</head>
<body>
	<div class="main-container">
	<iframe id="print" src=""></iframe>
	<div style="clear:both;margin: 0px;">
		<div class="lq-panel lq-panel-default">
			<div class="lq-panel-heading" style="text-align: center;font-size: large;">
				<i class="fa fa-tasks"></i>
				采购订单${oms_order_sn}</span>商品明细&nbsp;&nbsp;
				<c:if test="${order_status!='FULFILLED'}">
					<input type="button" class="btn reset nav" id="reset" value="重置"></input>
				</c:if>
			</div>
		</div>
	</div>
	
	<div class="col-sm-6 guidance">
		<c:if test="${order_status!='FULFILLED'&& order_status!='ABORTED'}">
			<input type="submit" class="btn btn-info start_inspect nav" id="start_inspect" value="开始验收"></input><span>&nbsp;&nbsp;》》》&nbsp;</span>
	        <input type="submit" class="btn nav" id="print_receipt"value="打印验收单"></input><span>&nbsp;&nbsp;》》》&nbsp;</span>
	        <input type="submit" class="btn nav" id="print_tag"value="打印标签"></input><span>&nbsp;&nbsp;》》》&nbsp;</span>
	        <!-- <input type="submit" class="btn nav" id="inspection"  value="验收完成"></input><span>&nbsp;&nbsp;》》》&nbsp;</span>  -->
	        <input type="submit" class="btn print_inventory nav" id="print_inventory" value="打印入库单"></input>
	        <input type="hidden" id="order_id" value="${order_id}"></input>
	        <input type="hidden" id="oms_order_sn" value="${oms_order_sn}"></input>
	        <input type="hidden" id="warehouse_id" value="${warehouse_id}"></input>
	        <input type="hidden" id="customer_id" value="${customer_id}">
	        <input type="hidden" id="arrive_time" value="${arrive_time}">
        </c:if>
	</div>
	<div>
		<span><B>供应商:</B>&nbsp;&nbsp;${provider_name}</span>&nbsp;&nbsp;&nbsp;&nbsp;
		<B>订单状态:</B>&nbsp;&nbsp;
		<span id="order_status">
			<c:choose>
				<c:when test="${order_status=='ACCEPT'}">
			        未处理
			    </c:when>
			    <c:when test="${order_status=='IN_PROCESS'}">
			        处理中
			    </c:when>
			    <c:when test="${order_status=='ON_SHELF'}">
			        部分上架
			    </c:when>
			    <c:when test="${order_status=='FULFILLED'}">
			        已完结
			    </c:when>
			      <c:when test="${order_status=='CANCEL'}">
			        已取消
			    </c:when>
			   	<c:when test="${order_status=='ABORTED'}">
			        已中止
			    </c:when>	
			</c:choose>
		</span>
	</div>
	
	<c:if test="${fn:length(orderGoodsList) > 0}">
	<div style="margin-top:10px;">
		<span style="font-weight:bold; font-size:15px; color:#393439; padding: 6px 12px; background:#EEEEEE;">&nbsp;待收货列表&nbsp;</span>
	</div> 
	
	<div class="form">
		<table class="lq-table" style="margin-top:10px;" id="batchTable">
			<thead>
			<tr>
				<th>商品条码</th>
				<th>商品名称</th>
				<th>长宽高</th>		
				<th>采购数量</th>
				<th>待收货数量</th>
				<th>到货数量</th>
				<th>所需托盘数</th>
				<th>Ti</th>
				<th>Hi</th>
				<th>良品</th>
				<th>不良品</th>
				<th>生产/过期日期</th>
				<th>串号</th>
				<th>批次号</th>
				<th>标签条码</th>
				<c:if test="${order_status == 'ON_SHELF' || order_status == 'FULFILLED'}">
					<th>库位条码</th>
				</c:if>
			</tr>
			</thead>
			<c:forEach items="${orderGoodsList}" varStatus="i" var="order"> 
			<tr align="center" class="list list_${i.index}">
				<input type="hidden" class="order_id" value="${order.order_id}">
				<input type="hidden" class="barcode_copy" value="${order.barcode}">
				<input type="hidden" class="order_goods_id_copy" value="${order.order_goods_id}">
				<input type="hidden" class="goods_name_copy" value="${order.goods_name}">
				<input type="hidden" class="physical_warehouse_id_copy" value="${order.physical_warehouse_id}">
				<input type="hidden" class="purchase_number" value="${order.goods_number}">
			<c:if test="${order.row_num > 0}">
				<input type="hidden" name="index_id" class="index_id" value="${i.index}">
				<input type="hidden" class="product_id" value="${order.product_id}">
				<input type="hidden" name="order_id_" value="${order.order_id}">	
				<input type="hidden"  class="is_serial" name="is_serial" value="${order.is_serial}"/>
				<input type="hidden"  class="is_maintain_warranty" name="is_maintain_warranty" value="${order.is_maintain_warranty}"/>
				<input type="hidden" class="is_maintain_batch_sn" name="is_maintain_batch_sn"  value="${order.is_maintain_batch_sn}">
							
				<td class="fixed" rowspan="${order.row_num}">${order.barcode}</td>
				<td class="fixed" rowspan="${order.row_num}">${order.goods_name}</td>
				<td class="fixed" rowspan="${order.row_num}">${order.length}*${order.width}*${order.height}</td>			
				<td class="fixed" rowspan="${order.row_num}">${order.goods_number}</td>
				<td class="fixed" rowspan="${order.row_num}">${order.not_accept_goods_number}</td>
				<td class="fixed" rowspan="${order.row_num}">
					<input class="form-control form-input arrive_number" id="arrive_number" type="text" value="${order.arrive_number}"/>
				</td>
				<td class="fixed" rowspan="${order.row_num}">
					<input class="form-control form-input tray_number" id="tray_number"  type="text" value="${order.tray_number}"/>
				</td>
				<td class="fixed" rowspan="${order.row_num}">
					<input class="form-control form-input dui_die_length" id="dui_die_length"  type="text" value="${order.ti}"/>
				</td>
				<td class="fixed" rowspan="${order.row_num}">
					<input class="form-control form-input dui_die_width" id="dui_die_width"  type="text" value="${order.hi}"/>
				</td>
				</c:if>

				<td class="change">
					<input class="form-control form-input normal_number" id="normal_number" name="purchaseProductAccDetailList[${i.index}].purchaseProductList[0].normal_number" type="text" group="${order.order_goods_id}"
						<c:choose>
							<c:when test="${order.status_id=='NORMAL'}">
						        value="${order.quantity}"
						    </c:when>
						    <c:when test="${order.status_id=='DEFECTIVE'}">
						        disabled="disabled"
						    </c:when>      
						</c:choose> 
					onfocus="disableDefectiveNumber(event)" 
					onblur="ableDefectiveNumber(event)"/>
				</td>
				<td class="change">
					<input class="form-control form-input defective_number" id="defective_number" name="purchaseProductAccDetailList[${i.index}].purchaseProductList[0].defective_number" type="text" group="${order.order_goods_id}"
						<c:choose>
							<c:when test="${order.status_id=='NORMAL'}">
						        disabled="disabled"
						    </c:when>
						    <c:when test="${order.status_id=='DEFECTIVE'}">
						        value="${order.quantity}"
						    </c:when>      
						</c:choose>
					onfocus="disableNomalNumber(event)" 
					onblur="ableNomalNumber(event)"/>
				</td>
				<c:if test="${order.is_maintain_warranty == 'Y'}">
					<td class="change">
						<input class="form-control form-input form_date validity_" id="validity" name="purchaseProductAccDetailList[${i.index}].purchaseProductList[0].validity" type="text" 
							<c:choose>
								<c:when test="${order.is_serial=='N'}">
							        value="${order.validity}"
							    </c:when>
							    <c:when test="${order.is_serial=='Y'}">
							        disabled="disabled"
							    </c:when>      
							</c:choose>
						onfocus="disableSeria(event)" 
						onblur="ableSeria(event)"/>
					</td>
				</c:if>
				<c:if test="${order.is_maintain_warranty == 'N'}">
					<td class="change">
						-
						<input type="hidden" class="form-control form-input form_date validity_" id="validity" value="" />
					</td>
				</c:if>
				<c:if test="${order.is_serial == 'Y'}">
					<td class="change">
						<input class="form-control form-input serial_no_" id="serial_no" name="purchaseProductAccDetailList[${i.index}].purchaseProductList[0].serial_no" type="text" 
							        value="${order.serial_no}"
						onfocus="disableValidity(event)" 
						onblur="ableValidity(event)"/>
					</td>
				</c:if>
				<c:if test="${order.is_serial == 'N'}">
					<td class="change">
						-
						<input class="form-control form-input serial_no_" id="serial_no"  type="hidden" value="" />
			
					</td>
				</c:if>
				<c:if test="${order.is_maintain_batch_sn == 'Y'}">
					<td class="change">
						<input class="form-control form-input batch_sn_" id="batch_sn" name="purchaseProductAccDetailList[${i.index}].purchaseProductList[0].batch_sn" type="text" 
							        value="${order.batch_sn}"
						onfocus="disableValidity(event)" 
						onblur="ableValidity(event)"/>
					</td>
				</c:if>
				<c:if test="${order.is_maintain_batch_sn == 'D'}">
					<td class="change">
						-
						<input class="form-control form-input batch_sn_" id="batch_sn"  type="hidden" value="" />
					</td>
				</c:if>
				<c:if test="${order.is_maintain_batch_sn == 'O'}">
					<td class="change">
						默认
						<input class="form-control form-input batch_sn_" id="batch_sn"  type="hidden" value="" />
					</td>
				</c:if>
				<c:if test="${order.is_maintain_batch_sn == 'N'}">
					<td class="change">
						-
						<input class="form-control form-input batch_sn_" id="batch_sn"  type="hidden" value="" />
			
					</td>
				</c:if>
				<td class="change tag">
					${order.location_barcode}
				</td>
				<c:if test="${order_status == 'ON_SHELF' || order_status == 'FULFILLED'}">
					<td class="td_location_barcode">${order.location_kw_barcode}</td>
				</c:if>
				<c:if test="${order_status == 'ACCEPT' || order_status == 'IN_PROCESS' || order_status == 'ON_SHELF'}">
					<c:if test="${order.location_barcode == '' || order.location_barcode == null }">
						<c:if test="${order.row_num > 0}">
						<td style="display:none" class="fixed" rowspan="${order.row_num}"><button  class="btn add">添加标签</button></td>
						</c:if>	
					</c:if>
				</c:if>
			</tr>
			</c:forEach>
		</table>
	</div>
	</c:if>
	<br/>
	<c:if test="${fn:length(orderGoodsList2) > 0}">
		<div style="margin-top:10px;">
			<span style="font-weight:bold; font-size:15px; color:#393439; padding: 6px 12px; background:#EEEEEE;">&nbsp;已收货列表&nbsp;</span>
		</div> 
		<div class="form2">
			<table class="lq-table" style="margin-top:10px;" >
				<thead>
					<tr>
						<th>商品条码</th>
						<th>商品名称</th>
						<th>长宽高</th>		
						<th>采购数量</th>
						<th>所需托盘数</th>
						<th>已收货数量</th>
						<th>Ti</th>
						<th>Hi</th>
						<th>良品</th>
						<th>不良品</th>
						<th>生产日期</th>
						<th>串号</th>
						<th>批次号</th>
						<th>标签条码</th>
						<th>库位条码</th>
					</tr>
				</thead>
				<c:forEach items="${orderGoodsList2}" varStatus="i" var="good">
					<tr align="center" class="list2">
					<c:if test="${good.row_num > 0}">
						<td rowspan="${good.row_num}">${good.barcode}</td>
						<td rowspan="${good.row_num}">${good.goods_name}</td>
						<td rowspan="${good.row_num}">${good.length}*${good.width}*${good.height}</td>
						<td rowspan="${good.row_num}">${good.goods_number}</td>
						<td rowspan="${good.row_num}">${good.tray_number}</td>
					</c:if>
						<td>${good.quantity}</td>
						<td>${good.ti}</td>
						<td>${good.hi}</td>
						<td>
							<c:if test="${good.status_id == 'NORMAL'}">
								${good.quantity}
							</c:if>
						</td>
						<td>
							<c:if test="${good.status_id == 'DEFECTIVE'}">
								${good.quantity}
							</c:if>
						</td>
						<td>${fn:substring(good.validity,0,10)}</td>
						<td>${good.serial_number}</td>
						<td>${good.batch_sn}</td>
						<td class="accept_location_barcode">${good.location_barcode}</td>
						<td>${good.location_kw_barcode}</td>
					</tr> 
				</c:forEach>
			</table>
		</div>
	</c:if>
	</div>
<script type="text/javascript" src="../static/js/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="../static/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
	/////////////////表格第11列时间格式化/////////////////// 	
/* 	$("tbody tr.list2").each(function(val){
		console.log($(this).find("td").eq(10).text().length);
    	if($(this).find("td").eq(10).text().length>10){
    		$(this).find("td").eq(10).text($(this).find("td").eq(10).text().substring(0,10));
    	}else if($(this).find("td").eq(10).text().length==0) {    //当合并相同行后，第11列会变成第6列
    		if($(this).find("td").eq(5).text().length>10) {
    			$(this).find("td").eq(5).text($(this).find("td").eq(5).text().substring(0,10));
    		}
    	}
	})
 */
   	/////////////////表格第11列时间格式化/////////////////// 	
    	function insert_flg(str){
		    var newstr="";
		    var before = str.substring(0,3), after = str.substring(3,7);
		    newstr = before + "-" + after;
		    str = newstr;
		    var before = str.substring(0,6), after = str.substring(6,8);
		    newstr = before + "-" + after;
		    str = newstr;
		    return newstr;
		}
		$(".td_location_barcode").each(function(){
			$(this).text(insert_flg($(this).text()));
		});
			
    	var order_status = "${order_status}";
    	if(order_status == "ACCEPT"){
    		$("input").attr('disabled', 'disabled');
    		$(".add").attr('disabled', 'disabled');
    		$(".start_inspect").removeAttr('disabled');
    	}else if (order_status == "IN_PROCESS") {
    		$("input").attr('disabled', 'disabled');
    		$(".add").attr('disabled', 'disabled');
    		$("#print_receipt").addClass('btn-info');
    		$("#print_tag").addClass('btn-info');
    		var length = $("#batchTable tr").length;
    		if(length > 0){
	    		$("#print_receipt").removeAttr('disabled');
	    		$("#print_tag").removeAttr('disabled');
    		}
    		//$("#print_receipt").attr('disabled', 'disabled');
    		//$("#print_tag").attr('disabled', 'disabled');
    		//$("#inspection").attr('disabled', 'disabled');
    		$("#reset").removeAttr('disabled');
    		//$("#print_inventory").attr('disabled', 'disabled');
    		//$("#start_inspect").attr('disabled', 'disabled');
    	}else if (order_status == "ON_SHELF") {
    		//$(".add").attr('disabled', 'disabled');
    		$(".add").removeAttr('disabled');
    		$("#print_receipt").addClass('btn-info');
    		$("#print_tag").addClass('btn-info');
    		var length = $("#batchTable tr").length;
    		if(length > 0){
	    		$("#print_receipt").removeAttr('disabled');
	    		$("#print_tag").removeAttr('disabled');
    		}else{
    			$("#print_receipt").attr('disabled', 'disabled');
        		$("#print_tag").attr('disabled', 'disabled');
    		}
    		//$("#inspection").addClass('btn-info');
    		$("#print_inventory").addClass('btn-info');   		
    		$("#start_inspect").attr('disabled', 'disabled');
    		$("#print_inventory").removeAttr('disabled');
    		$("#reset").removeAttr('disabled');
    	}else if (order_status == "FULFILLED" || order_status == "CANCEL") {
    		$(".add").attr('disabled', 'disabled');
    		$("#print_receipt").addClass('btn-info');
    		$("#print_tag").addClass('btn-info');
    		//$("#inspection").addClass('btn-info');
    		$("#print_inventory").addClass('btn-info');

    		$("#start_inspect").attr('disabled', 'disabled');
    		$("#print_receipt").attr('disabled', 'disabled');
    		$("#print_tag").attr('disabled', 'disabled');
    		//$("#inspection").attr('disabled', 'disabled');
    		$("#print_inventory").attr('disabled', 'disabled');
    		$("#reset").attr('disabled', 'disabled');

    	}

    	$("#start_inspect").on('click', function(event) {
    		event.preventDefault();
    		var order_id = $("#order_id").val();
    		// console.log(order_id);
    		$.ajax({
		        type: 'post',
		  	   	url: '../purchase/start',  
		        data: {
		        	'order_id':order_id
		        },
		     	dataType:'json',
		         success: function(data) { 
		         	if(data.result == 'success'){
		         		$("#order_status").html('处理中');
		         	}
		         	$("#print_receipt").addClass('btn-info');
		    		$("#print_tag").addClass('btn-info');
		    		// $("#inspection").addClass('btn-info');
		    		$("#print_receipt").removeAttr('disabled');
		    		$("#print_tag").removeAttr('disabled');
		    		// $("#inspection").removeAttr('disabled');
		    		$("#print_inventory").attr('disabled', 'disabled');
		    		$("#start_inspect").attr('disabled', 'disabled');
		            alert('开始验货成功');
		         },
					error:function(data){
						// console.log(data);
						alert("开始验货失败");
					}
		     });

    	});
		
    	$("#reset").on('click', function(event) {
    		event.preventDefault();
    		var order_id = $("#order_id").val();
    		var tagList = '';
    		var tr = $(".list2").length;
    		for (var i = 0; i < tr; i++) {
    			var location_barcode = $(".list2").eq(i).find('.accept_location_barcode').html();
    			if(location_barcode!= "" && location_barcode!="undefined"){
    				tagList += location_barcode+",";
    		  	}
    		}
    		tagList = tagList.substring(0,tagList.lastIndexOf(','));
    		if(tagList == ""){
    			alert("请刷新页面后再重置");
    			return false;
    		}
    		$.ajax({
		        type: 'post',
		  	   	url: '../purchase/reset',  
		        data: {
		        	'order_id':order_id,
		        	'tag_list':tagList
		        },
		     	dataType:'json',
		        success: function(data) { 
		        	if(data.result == 'success'){
		        		/*
		         		$("#order_status").html('未处理');
		         		$("#print_receipt").addClass('btn-info');
			    		$("#print_tag").addClass('btn-info');
			    		$("#print_receipt").attr('disabled', 'disabled');
			    		$("#print_tag").attr('disabled', 'disabled');
			    		$("#print_inventory").attr('disabled', 'disabled');
			    		$("#start_inspect").attr('disabled', 'disabled');*/
			            alert('重置成功');
			            location.reload();
		         	}else{
		         		alert(data.note);
		         	}
		        },
				error:function(data){
					alert("重置失败");
				}
    		});
    	});
        function dateFormate(str){
        	var a = /^(\d{4})-(\d{2})-(\d{2})$/;
        	if(!a.test(str)){
        		return false;
        	}else
        		return true;
        }
    	
		$("#print_receipt").on('click', function(event) {
    		event.preventDefault();
    		$("input").removeAttr('disabled');
    		$(".add").removeAttr('disabled');
    		$("#print_inventory").attr('disabled', 'disabled');
    		//$("#inspection").attr('disabled', 'disabled');
		    $("#start_inspect").attr('disabled', 'disabled');
		    alert("打印验收单完成");
		    $("#print_receipt").attr('disabled', 'disabled');
		    $(".arrive_number").each(function(){
		    	$(this).val($(this).parent().prev().html());
		    });
    	});
    	$('.arrive_number').on('keyup', function () {
    		var trThis = $(this);
    		var trIndex = trThis.parent().parent().index();
    		if (parseInt(trThis.val()) > parseInt(trThis.parent().prev().text())) {
    			trThis.css('border', '1px solid red');
    			$('tr').eq(trIndex + 1).find('input[type="text"]').attr('disabled', true);
    			trThis.attr('disabled', false);
    		} else {
    			trThis.css('border', '1px solid #CCC');
    		}
    	});
    	if ('${order_status}' != 'ACCEPT') {
    		$('input[type="text"]').attr('disabled', 'disabled');
    	}
    	'${order_status}'
    	$("#print_tag").on('click', function(event) {
    		event.preventDefault();
    		var flag = true;
    		$("tr.list").each(function(){
    			var normal=$(this).find("#normal_number").val();
    			var defective=$(this).find("#defective_number").val();
    			var tag=$.trim( $(this).find(".tag").text().replace(/[\r\n]/g,""));
    			var batch_sn=$.trim($(this).find(".batch_sn_").val());
    			var isBatch=$.trim($(this).find(".batch_sn_").parent().text());
    			var validity=$(this).find(".validity_").val();
    			var row=parseInt($(this).index(),10)+1;
    			
    			//多商品情况下要打印单个商品的上架标签时候要校验tag是否已经生成
    			if(normal==""&&defective==""&&tag!="")
    			{	   				
    				alert("第"+row+"行的良品与不良品数量都为空!");
					flag = false;
					return false;
    			}
    			if(tag!=""){
        			if(batch_sn==""&&isBatch!="-"){
           				alert("第"+row+"行批次号为空!");
       					flag = false;
       					return false;
        			}else if(batch_sn.length>32){
           				alert("第"+row+"行批次号太长!");
       					flag = false;
       					return false;    				
        			}    				
    			}

    			if(!dateFormate(validity)&&validity!=""){
       				alert("第"+row+"行时间格式不正确!");
       				$(this).find(".validity_").removeAttr("disabled").focus();
   					flag = false;
   					return false;   				
    			}
    		})
    		if(!flag){
    			return false;
    		}
    		
    		var arrive_number = $(".arrive_number").val();
    		var purchase_number = $(".purchase_number").val();
    		var customer_id = $("#customer_id").val();
    		var arrive_time = $("#arrive_time").val();
    		var order_id = $('#order_id').val(); 
    		if(parseInt(purchase_number) < parseInt(arrive_number)){
    			alert("到货数量大于采购数量");
    			flag = false;
    			return false;
    		}
    		
    		//数据验证，验证到货数量不能为空值
    		$(".arrive_number").each(function() {
    			var obj = $(this).val();
    			var text = $.trim(obj);
    			// console.log(text);
    			if(text == ""){
					alert("到货数量不能为空");
					flag = false;
					return false;
    			}
    			if (! /^\d+$/.test(text)){
					alert("到货数量只能是数字");
					flag = false;
					return false;
    			}
    			
    		});

    		$(".dui_die_length").each(function() {
    			var obj = $(this).val();
    			var text = $.trim(obj);
    			if (text != "" && ! /^\d+$/.test(text)){
					alert("堆叠长只能是数字");
					flag = false;
					return false;
    			}
    			
    		});
    		$(".dui_die_width").each(function() {
    			var obj = $(this).val();
    			var text = $.trim(obj);
    			if (text != "" && ! /^\d+$/.test(text)){
					alert("堆叠宽只能是数字");
					flag = false;
					return false;
    			}
    			
    		});
    		
    		$(".tray_number").each(function() {
    			var obj = $(this).val();
    			var text = $.trim(obj);
    			if(text == ""){
					//alert("所需托盘数不能为空");
					//flag = false;
					//return false;
    			}
    			if (! /^\d+$/.test(text)){
					//alert("所需托盘数只能是数字");
					//flag = false;
					//return false;
    			}
    			
    		});
	        
    		if(!flag){
    			return false;
    		}
			
    		var tagList= "";
	        var tr = $(".list").length;
	        var temp = "";
	        var tagList1 = [];
	        tagList="{[";
			var last_arrive_number = "";
			var last_product_id = "";
			var last_tray_number = "";
			var last_dui_die_length = "";
			var last_dui_die_width = "";
			var last_is_maintain_warranty = "";
			var last_is_maintain_batch_sn = "";
			var pre_order_goods_id;
	        for (var i = 0; i < tr; i++) {
	        	  var tagInfo="";
		          var oms_order_sn = $('#oms_order_sn').val(); 
		          var warehouse_id = $('#warehouse_id').val(); 
		          var order_goods_id = $(".list").eq(i).find('.order_goods_id_copy').val();
		          var physical_warehouse_id = $(".list").eq(i).find('.physical_warehouse_id_copy').val(); 
		          var barcode = $(".list").eq(i).find('.barcode_copy').val();
		          var goods_name = $(".list").eq(i).find('.goods_name_copy').val().replace(/\'/g, "").replace(/&/g,""); //过滤商品名称中的单引号    
		          var order_goods_id = $(".list").eq(i).find('.order_goods_id_copy').val();
		          var product_id = $(".list").eq(i).find('.product_id').val();
		          var arrive_number = $(".list").eq(i).find('.arrive_number').val();
		          var tray_number = $(".list").eq(i).find('.tray_number').val();
		          var dui_die_length = $(".list").eq(i).find('.dui_die_length').val();
		          var dui_die_width = $(".list").eq(i).find('.dui_die_width').val();
		          var serial_no = $(".list").eq(i).find('.serial_no_').val();
		          serial_no=serial_no.replace(/，/ig,',');
		          var is_maintain_batch_sn = $(".list").eq(i).find('.is_maintain_batch_sn').val();
		          var batch_sn = $(".list").eq(i).find('.batch_sn_').val();
		          var is_serial = $(".list").eq(i).find('.is_serial').val();
		          var is_maintain_warranty = $(".list").eq(i).find('.is_maintain_warranty').val();
		          var tag=$.trim($(".list").eq(i).find(".tag").text().replace(/[\r\n]/g,""));
		          if(is_serial == 'Y' && (serial_no == '' || serial_no == null)){
		        	  alert("串号商品请维护串号");
		        	  return;
		          }
		          
		          if(is_maintain_batch_sn == 'Y'&&tag!=""){
    				  if(batch_sn == '' || batch_sn == null){
			        	  alert("请维护商品批次号");
			        	  return;
    				  }else{
    					  var batch_id_reg = new RegExp("^[A-Za-z0-9_-]+$");
    					  if(!batch_id_reg.test(batch_sn)) {
    						  alert("请正确维护批次号（数字、字母、-、_）!");
   								return;
   						  }
    				  }
		          }
		          
		          
		          if(product_id == "" || product_id == null){
		        	  product_id = last_product_id;
		          }
		          if(arrive_number == "" || arrive_number == null){
		        	  arrive_number = last_arrive_number;
		          }
		          if(tray_number == "" || tray_number == null){
		        	  tray_number = last_tray_number;
		          }
		          if(dui_die_length == "" || dui_die_length == null){
		        	  dui_die_length = last_dui_die_length;
		          }
		          if(dui_die_width == "" || dui_die_width == null){
		        	  dui_die_width = last_dui_die_width;
		          }
		          if(is_maintain_warranty == "" || is_maintain_warranty == null){
		        	  is_maintain_warranty = last_is_maintain_warranty;
		          }
		          if(is_maintain_batch_sn == "" || is_maintain_batch_sn == null){
		        	  is_maintain_batch_sn = last_is_maintain_batch_sn;
		          }
		          
		          var normal_number = $(".list").eq(i).find('.normal_number').val();
		          var defective_number = $(".list").eq(i).find('.defective_number').val();
		          var validity = $(".list").eq(i).find('.validity_').val();
		          var location_barcode = $(".list").eq(i).find('.tag').html();
		          last_arrive_number = arrive_number;
				  last_product_id = product_id;
				  last_tray_number = tray_number;
				  last_dui_die_length = dui_die_length;
				  last_dui_die_width = dui_die_width;
				  last_is_maintain_warranty = is_maintain_warranty;
				  last_is_maintain_batch_sn = is_maintain_batch_sn;
		          if (location_barcode == "") {
			            alert("便签条码不能为空");
			            return;
		          }
		          if (normal_number=="" && defective_number=="") {
			            //alert("良品和不良品不能同时为空");
			            continue;
			            //return;
		          };
		          if(is_maintain_warranty == 'Y' && (validity == '' || validity == null)){
		        	  alert("请维护生产日期");
		        	  return;
		          }
		          if(is_serial == 'Y' && (serial_no == '' || serial_no == null)){
		        	  alert("请维护串号");
		        	  return;
		          }
	    		  if(location_barcode== "" || location_barcode=="undefined"){
	    			  continue;
	    				//alert("便签条码不能为空");
	    				//return;
	    		  }
	    		  console.log("到货数量" + arrive_number);
		          if(pre_order_goods_id != order_goods_id){
	    				var total = 0;
	    				var temp = "input[group="+order_goods_id+"]";
	    				$(temp).each(function() {
	    					total += Number($(this).val());
	    					console.log("总数："+total);	
	    				});
	    				pre_order_goods_id = order_goods_id;
	    				// console.log("前一个编码："+pre_order_goods_id);
	    				if(arrive_number != total){
							alert("良品与不良品总和不等于到货数量！");
							return;
						}
	    		  }
		          
		          /*
	    		  if(pre_order_goods_id != order_goods_id){
	    				var total = 0;
	    				var temp = "input[group="+order_goods_id+"]";
	    				$(temp).each(function() {
	    					total += Number($(this).val());
	    					console.log("总数："+total);	
	    				});
	    				pre_order_goods_id = order_goods_id;
	    				// console.log("前一个编码："+pre_order_goods_id);
    					var arrive_number_total = 0;
    					$(".arrive_number").each(function(){
    						arrive_number_total += Number($(this).val());
    					});
						console.log("important total" + total);
						console.log("important arrive_number_total" + arrive_number_total);
	    				if(arrive_number_total != total){
							//alert("良品与不良品总和不等于到货数量！");
							//return;
						}
	    		  }*/
	    		  /*
	    		  if(dui_die != ''){
	    			  if (! /\d{1,}\*\d{1,}/.test(dui_die)){
	  					alert("堆叠格式不匹配，正确的格式形如：2*2");
	  					return;
	      			}
	    		  }*/
	    		  if(validity != '' || validity == null){
	    			  if(new Date() < Date.parse(validity)){
	    				  alert("生产日期大于当前日期，将以过期日期记录！");
		  				  //return;
	    			  }
	    		  }
		          temp = "{'customer_id':'"+customer_id+"','location_barcode':'"+location_barcode+"'"+
		          ",'normal_number':'"+normal_number+"'"+
		          ",'defective_number':'"+defective_number+"'"+
		          ",'validity':'"+validity+"'"+
		          ",'order_goods_id':'"+order_goods_id+"'"+
		          ",'product_id':'"+product_id+"'"+
		          ",'arrive_number':'"+arrive_number+"'"+
		          ",'tray_number':'"+tray_number+"'"+
		          ",'dui_die_length':'"+dui_die_length+"'"+
		          ",'dui_die_width':'"+dui_die_width+"'"+
		          ",'serial_no':'"+serial_no+"'"+
		          ",'physical_warehouse_id':'"+physical_warehouse_id+"'"+
		          ",'order_id':'"+order_id+"'"+
		          ",'oms_order_sn':'"+oms_order_sn+"'"+
		          ",'warehouse_id':'"+warehouse_id+"'"+
		          ",'goods_name':'"+goods_name+"'"+
		          ",'barcode':'"+barcode+"'"+
		          ",'batch_sn':'"+batch_sn+"'"+
		          ",'is_maintain_batch_sn':'"+is_maintain_batch_sn+"'"+
		          ",'arrive_time':'"+arrive_time+"'"+
		          "}";
		          tagInfo += temp;
		          tagInfo += ",";
		          tagList += tagInfo;
		          tagList1.push(temp);
	      }
	      tagList = tagList.substring(0,tagList.lastIndexOf(','));
	      tagList += "]}";
		  console.log(tagList);
		  console.log(tagList1);
		  $("#print_tag").attr('disabled', 'disabled');
		  $.ajax({
		        type: 'post',
		  	   	url: '../purchase/print_tag',  
		        data: JSON.stringify(tagList1),
		     	dataType:'json',
		     	contentType:"application/json; charset=utf-8",
		        success: function(data) { 
		         	console.log(data);
		         	if(data.result == 'success'){
			         	var list = tagList.replace(/#/g,"");
			            src="../purchase/print_tag2?tagList="+encodeURI(encodeURI(list));
	    				$('#print').attr('src',src); 
			            $("#print_tag").attr('disabled', 'disabled');
			            $("#inspection").addClass('btn-info');
			            $("#inspection").removeAttr('disabled');
			            $("#print_inventory").removeAttr('disabled');
		         	}else{
		         		alert(data.note);
		         	}

		        },
				error:function(data){
					alert("失败");
					$("#inspection").attr('disabled', 'disabled');
				}
		     });
    	 });

    	$('#print_inventory').on('click', function(event) {

    		event.preventDefault();
	        var tagList= "";
	        var tr = $(".list").length;
	        
	        var temp = "";
	        var oms_order_sn = "";
	        oms_order_sn = $('#oms_order_sn').val(); 
            src="../purchase/print_inventory?oms_order_sn="+oms_order_sn;
  				$('#print').attr('src',src); 

            $("#print_inventory").attr('disabled', 'disabled');
		    

    	});
    	
    	$("#print_receipt").on('click', function(event) {
    		event.preventDefault();
    		var oms_order_sn = $("#oms_order_sn").val();
    		src="../purchase/print_receipt?oms_order_sn="+oms_order_sn;
			$('#print').attr('src',src); 
    	});
    	
    	$('.form_date').datetimepicker({
			format:'yyyy-mm-dd',
	        language:  'fr',
	        weekStart: 1,
	        todayBtn:  1,
			autoclose: 1,
			todayHighlight: 1,
			startView: 2,
			minView: 2,
			forceParse: 0
    	});

    	$(document).on('click',".add", function (event,param) {
    		var index = $('.add').index(this);
    		if (param) {
    			var num = param.numFromTrayNumber;
    		} else {
    			var num = $('.tray_number').eq(index).val();
    		}
    		$.ajax({
		  	   	url: '../purchase/generate_tag', 
		        type: 'post', 
		     	dataType:'json',
		        data: {
		           	 'physical_warehouse_id': $('#physical_warehouse_id').val(),
		           	 'customer_id':$('#customer_id').val(),
		           	 'num':num
		         },
		        success: function(data) { 
		         	//console.log(data);
		         	// event.stopPropagation();
		    		var tr_list = event.target.closest('.list');
		    		//console.log("ok");
		    		//console.log($(tr_list));
		    		var rowspan = $(tr_list).children("td").eq(0).attr('rowspan');
		    		
		    		var index_id = $(tr_list).find('.index_id').val();
		    		var group = $(tr_list).find('.order_goods_id_copy').val();
		    		var barcode = $(tr_list).find('.barcode_copy').val();
		    		var order_goods_id = $(tr_list).find('.order_goods_id_copy').val();
		    		var goods_name = $(tr_list).find('.goods_name_copy').val();
		    		var physical_warehouse_id = $(tr_list).find('.physical_warehouse_id_copy').val();
		    		var is_serial = $(tr_list).find('.is_serial').val();
		    		var is_maintain_warranty = $(tr_list).find('.is_maintain_warranty').val();
		    		var is_maintain_batch_sn = $(tr_list).find('.is_maintain_batch_sn').val();
						// rowspan = $(event.target).parent().parent().find('#tray_number').val();
					var newRow = '';
					$(".newAdd_"+index).each(function(){
						$(this).remove();
						rowspan--;
					});
					for (var i = 0; i < data.locationBarcode.length - 1; i++) {
		    			newRow += '<tr align="center" class="list list_'+index+' newAdd_'+index+'">'+
				    		'<input type="hidden" class="barcode_copy" value="'+barcode+'">'+
							'<input type="hidden" class="order_goods_id_copy" value="'+order_goods_id+'">'+
							'<input type="hidden" class="goods_name_copy" value="'+goods_name+'">'+
							'<input type="hidden" class="physical_warehouse_id_copy" value="'+physical_warehouse_id+'">'+
							'<td class="change">'+
								'<input class="form-control form-input normal_number" id="normal_number" name="purchaseProductAccDetailList['+index_id+'].purchaseProductList['+rowspan+'].normal_number" type="text" value=""'+
								' group="'+group+'"'+
								' onfocus="disableDefectiveNumber(event)" onblur="ableDefectiveNumber(event)"/>'+
							'</td>'+
							'<td class="change">'+
								'<input class="form-control form-input defective_number" id="defective_number" name="purchaseProductAccDetailList['+index_id+'].purchaseProductList['+rowspan+'].defective_number" type="text" value=""'+' group="'+group+'"'+
								' onfocus="disableNomalNumber(event)" onblur="ableNomalNumber(event)"/>'+
							'</td>';
							
							if(is_maintain_warranty == 'Y'){
								newRow += ('<td class="change">'+
									'<input class="form-control form-input form_date validity_" id="" name="purchaseProductAccDetailList['+index_id+'].purchaseProductList['+rowspan+'].validity" type="text" value="" onfocus="disableSeria(event)" onblur="ableSeria(event)"/>'+'</td>');
							}else{
								newRow += ('<td class="change">'+
										'-<input type="hidden" class="form-control form-input form_date validity_" id="validity" value="" />'+
									'</td>');
							}
							
							if(is_serial == 'Y'){
								newRow += ('<td class="change">'+
									'<input class="form-control form-input serial_no_" id="serial_no" name="purchaseProductAccDetailList['+index_id+'].purchaseProductList['+rowspan+'].serial_no" type="text" value="" onfocus="disableValidity(event)" onblur="ableValidity(event)"/>'+
								'</td>');
							}else{
								newRow += ('<td class="change">'+
										'-<input class="form-control form-input serial_no_" id="serial_no"  type="hidden" value="" />'+
									'</td>');
							}
							
							if(is_maintain_batch_sn == 'Y'){
								newRow += ('<td class="change">'+
									'<input class="form-control form-input batch_sn_" id="batch_sn" name="purchaseProductAccDetailList['+index_id+'].purchaseProductList['+rowspan+'].batch_sn" type="text" value="" onfocus="disableValidity(event)" onblur="ableValidity(event)"/>'+
								'</td>');
							}else if(is_maintain_batch_sn == 'O'){
								newRow += ('<td class="change">'+
										'默认<input class="form-control form-input batch_sn_" id="batch_sn"  type="hidden" value="" />'+
									'</td>');
				    		}else if(is_maintain_batch_sn == 'D'){
								newRow += ('<td class="change">'+
										'-<input class="form-control form-input batch_sn_" id="batch_sn"  type="hidden" value="" />'+
									'</td>');
				    		}else{
								newRow += ('<td class="change">'+
										'-<input class="form-control form-input batch_sn_" id="batch_sn"  type="hidden" value="" />'+
									'</td>');
							}
							
							newRow +=  ('<td class="change tag">'+
								'<input class="form-control form-input tag_" name="purchaseProductAccDetailList['+index_id+'].purchaseProductList['+rowspan+'].tag" type="hidden" value="${order.goods_barcode}"/></td>'+
							'<td class="change"><span class="delete"><image src="../static/images/shanchu.png"></span></td>'+
						'</tr>');
						rowspan ++;
		    		}
					
					$(tr_list).after(newRow);
					$(tr_list).children(".fixed").attr('rowspan',rowspan);
					var td_tray_number = $(event.target).parent().parent().find('#tray_number').val(rowspan);

					var first_normal_number = $(".list_"+index).eq(0).find(".normal_number").eq(0).val();
					var last_normal_number = $(".list_"+index).eq(0).find(".arrive_number").val() - ($(".list_"+index).eq(0).find(".normal_number").val()) * ($(".list_"+index).eq(0).find(".tray_number").val() - 1);
					var last_defective_number = $(".list_"+index).eq(0).find(".arrive_number").val() - ($(".list_"+index).eq(0).find(".defective_number").val()) * ($(".list_"+index).eq(0).find(".tray_number").val() - 1);
					
					var first_defective_number = $(".list_"+index).eq(0).find(".defective_number").eq(0).val();
					var first_validity_ = $(".list_"+index).eq(0).find(".validity_").eq(0).val();

					$(".list_"+index).each(function(i){
						$(this).find(".tag").text(data.locationBarcode[i]);
						$(this).find(".normal_number").val(first_normal_number);
						$(this).find(".defective_number").val(first_defective_number);
						$(this).find(".validity_").val(first_validity_);
					});

					var normal_number_length = $(".list_"+index).find(".normal_number").length;
					
					if ($(".list_"+index).find(".normal_number").eq(0).val() != "") {
						$(".list_"+index).find(".normal_number").eq(normal_number_length - 1).val(last_normal_number);
					}

					var defective_number_length = $(".list_"+index).find(".defective_number").length;
					
					if ($(".list_"+index).find(".defective_number").eq(0).val() != "") {
						$(".list_"+index).find(".defective_number").eq(defective_number_length - 1).val(last_defective_number);
					}
					

					$('.form_date').datetimepicker({
						format:'yyyy-mm-dd',
				        language:  'fr',
				        weekStart: 1,
				        todayBtn:  1,
						autoclose: 1,
						todayHighlight: 1,
						startView: 2,
						minView: 2,
						forceParse: 0
			    	});	
		        },
				error:function(error){
					console.log(error);
					alert("获取便签失败！");
				}
		     });
    	});

		$(document).on('keyup','.tray_number',function(event){
			if (event.which == 13) {
				var index = $('.tray_number').index(this);
				var total = parseInt($(this).parent().prev().find(".arrive_number").val());
				if ($(".list_"+index).eq(0).find(".normal_number").eq(0).val()!="" || $(".list_"+index).eq(0).find(".defective_number").eq(0).val()!="") {
					
					var current = ($(this).val()) * ($(".list_"+index).eq(0).find(".normal_number").eq(0).val());
					var current_defective_number = ($(this).val()) * ($(".list_"+index).eq(0).find(".defective_number").eq(0).val());
					
					if (current <= total && current_defective_number <= total) {
						$(".add").eq(index).trigger("click",{
							numFromTrayNumber : $(this).val()
						});
					} else {
						alert("良品与不良品总和大于到货数量！");
					}
				} else {
					$(".add").eq(index).trigger("click",{
						numFromTrayNumber : $(this).val()
					});
				}
				
			}
		});

    	$("#inspection").on('click', function(event) {
    		event.preventDefault();
    		
    		var arrive_number = $(".arrive_number").val();
    		var purchase_number = $(".purchase_number").val();
    		if(parseInt(purchase_number) < parseInt(arrive_number)){
    			alert("到货数量大于采购数量");
    			return false;
    		}
    		
    		//数据验证，验证到货数量不能为空值
    		$(".arrive_number").each(function() {
    			var obj = $(this).val();
    			var text = $.trim(obj);
    			
    			if(text == ""){
					alert("到货数量不能为空");
					return false;
    			}
    			if (! /^\d+$/.test(text)){
					alert("到货数量只能是数字");
					return false;
    			}
    			
    		});

    		$(".tray_number").each(function() {
    			var obj = $(this).val();
    			var text = $.trim(obj);
    			if(text == ""){
					alert("所需托盘数不能为空");
					return false;
    			}
    			if (! /^\d+$/.test(text)){
					alert("所需托盘数只能是数字");
					return false;
    			}
    			
    		});
    		
    		alert("开始验收入库");

    		//数据验证,检验良品与不良品总和是否等于到货数量
    		var tr = $(".list").length;
    		var pre_order_goods_id;
    		var tagList= "";
    		for (var i = 0; i < tr; i++) {   			
    			var order_goods_id = $(".list").eq(i).find('.order_goods_id_copy').val();
    			var arrive_number = $(".list").eq(i).find('#arrive_number').val();
    			var normal_number = $(".list").eq(i).find('#normal_number').val();
    			var defective_number = $(".list").eq(i).find('#defective_number').val();
    			var validity = $(".list").eq(i).find('#validity').val();
    			var serial_no = $(".list").eq(i).find('#serial_no').val();
    			var tag_ = $(".list").eq(i).find('.tag').html();
    			
    			
    			if (tag_ == "" || tag_=="undefined" ) {
		            alert("便签条码不能为空");
		            return;
		        }
		        if (normal_number=="" && defective_number=="") {
		            alert("良品和不良品不能同时为空");
		            return;
	            };
	            if(normal_number=="" && defective_number==""){
    				alert("良品和不良品不能同时为空");
    				break;
    		    }
	            tagList += tag_ + ",";
    			// console.log("当前编码" + order_goods_id);
    			console.log("到货数量" + arrive_number);
    			if(pre_order_goods_id != order_goods_id){
    				var total = 0;
    				var temp = "input[group="+order_goods_id+"]";
    				$(temp).each(function() {
    					total += Number($(this).val());
    					console.log("总数："+total);	
    				});
    				pre_order_goods_id = order_goods_id;
    				// console.log("前一个编码："+pre_order_goods_id);
    				var arrive_number_total = 0;
    					$(".arrive_number").each(function(){
    						arrive_number_total += Number($(this).val());
    					});
					console.log("important total" + total);
					console.log("important arrive_number_total" + arrive_number_total);
    				if(arrive_number_total != total){
						alert("良品与不良品总和不等于到货数量！");
						break;
					}
    			}
    			// console.log("i:"+i);
    			if(i == tr-1){
    				tagList = tagList.substring(0,tagList.lastIndexOf(','));
    				var order_id = $('#order_id').val(); 
    				$.ajax({
    			        type: 'post',
    			  	   	url: '../purchase/finish',  
    			        data: {
    			        	'order_id':order_id,
    			        	'tag_list':tagList,
    			        },
    			     	dataType:'json',
    			         success: function(data) { 
    			            alert(data.note);
    			            location.reload();
    			         },
    					error:function(data){
    						// console.log(data);
    						alert("失败");
    					}
    			     });
					//$("#form").submit();
    			}
    		}


    		
    	});
    	// $(".achieveTag").live('click', function(event) {
    	// 	event.preventDefault();
    	// 	// event.stopPropagation();
    	// 	var td_tag = $(event.target).parent().siblings('.tag');
    	// 	var physical_warehouse_id = $("#physical_warehouse_id").val();
    	// 	var customer_id = $("#customer_id").val();
    	// 	var temp = $(td_tag).children().eq(0).val();
    	// 	if(temp == '' || temp == null){
	    // 		 $.ajax({
			  //       type: 'post',
			  // 	   	url: '../purchase/generate_tag',  
			  //       data: {
			  //          	 'physical_warehouse_id':physical_warehouse_id,
			  //          	 'customer_id':customer_id
			  //        },
			  //    	dataType:'json',
			  //        success: function(data) { 
			  //        	$(td_tag).children().eq(0).after(data.locationBarcode);
			  //        	$(td_tag).children().eq(0).val(data.locationBarcode);
			  //           alert('成功！');
			  //        },
					// 	error:function(data){
					// 		console.log(data);
					// 		alert("获取便签失败！");
					// 	}
			  //    });
    	// 	}else{
    	// 		alert("已生成标签条码，请勿重复生成");
    	// 	}
    	// });
    	$(".delete").live('click', function(event) {
    		event.preventDefault();
    		var listClass = $(this).parents(".list").attr("class");
    		var index = listClass.charAt(listClass.length - 1);
    		if(confirm("确定删除该标签么?")){
	   			var rowspan = $(this).parents(".list").siblings(".list_"+index).eq(0).find(".fixed").eq(0).attr('rowspan');
	    		rowspan--;
	    		//alert(rowspan);
				$(this).parents(".list").siblings(".list_"+index).eq(0).find(".fixed").attr('rowspan',rowspan);
				$(this).parents(".list").remove();
				$('.tray_number').eq(index).val(rowspan);	
    		}else{
    			return;
    		}
    		
    	});


    });

    function disableDefectiveNumber (event) {
    	console.log( $(event.target).val());
    	var defective=$(event.target).parent().parent().find('.defective_number');
    	var input_text=defective.val();
    	if(input_text!="")
    		$(event.target).attr('disabled', 'disabled');
    	else
       		 $(event.target).parent().parent().find('.defective_number').attr('disabled', 'disabled');
    }
    function ableDefectiveNumber (event) {
    	var input_text = event.target.value;
    	if(input_text == ""){
        $(event.target).parent().parent().find('.defective_number').removeAttr('disabled');
		}
    }
    function disableNomalNumber (event) {
    	var normal=$(event.target).parent().parent().find('.normal_number')
    	var  input_text = normal.val();
    	if(input_text!="")
    		$(event.target).attr('disabled', 'disabled');
    	else
          	$(event.target).parent().parent().find('.normal_number').attr('disabled', 'disabled');
    	
    		
    }
    function ableNomalNumber (event) {
    	var input_text = event.target.value;
    	if(input_text == ""){
        $(event.target).parent().parent().find('.normal_number').removeAttr('disabled');
		}
    } 

    function disableSeria (event) {
        $(event.target).parent().parent().find('.serial_no_').attr('disabled', 'disabled');
    }
    function ableSeria (event) {
    	var input_text = event.target.value;
    	if(input_text == ""){
        $(event.target).parent().parent().find('.serial_no_').removeAttr('disabled');
		}
    }
    function disableValidity (event) {
         $(event.target).parent().parent().find('.validity_').attr('disabled', 'disabled');
    }
    function ableValidity (event) {
    	var input_text = event.target.value;
    	if(input_text == ""){
        $(event.target).parent().parent().find('.validity_').removeAttr('disabled');
		}
    } 
</script>
</body>
</html>



