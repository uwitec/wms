<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>库存查询</title>
<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script type="text/javascript" src="../static/js/zapatec/utils/zapatec.js"></script>
<script type="text/javascript" src="../static/js/zapatec/zpcal/src/calendar.js"></script>
<script type="text/javascript" src="../static/js/zapatec/zpcal/lang/calendar-en.js"></script>
<style>
	#body-sale-search .lq-form-inline label {
    	width: 5.4em;
    }

    #btn-sale-search {
    	margin-left: 15px;
    }
    .noDataTip {
    	/*display: none;*/
    	text-align: center;
    	font-size: 30px;
    	color: #666;
    	margin: 30px 0 0 0;
    }
	
	
	.validity {
		border: none;
		height: 100%;
		width: 100%;
	}

    #page-search {
    	float: right;
    	display: none;
    }
    .isDouble11 {
    	dislplay:none;
    }
    .batchSn {
    	border: none;
    	padding: 7px 3px;
    }
</style>

<script>
var oldValidity;
console.log("Init customer_id:"+$("#custonerID").val());
function dateCheck(d){
	console.log("newValue:"+d);
	if(d.length==10){
		var month=parseInt(d.substring(5,7));
		var day= parseInt(d.substring(8,10));
		if(month>12||month<1){
			alert("输入的月份范围为(1~12)");
			return false;
		}
		if(day<1||day>31) {
			alert("输入的日期范围为(1~31)");
			return false;
		}
		if(d==oldValidity) {
			return false;
		}
		return true;
	}else{
		alert("输入生产日期有误，正确格式为(yyyy-mm-dd)");
	}
}
function dateChanged(value,pl_id,id) {
	/*  console.log("oldValue:"+oldValidity); */
	 var read=$("#"+id).attr("readonly");
	 if(dateCheck(value)){
		 if(read=="readonly") {
			 $("#"+id).val(oldValidity);
			 return ;
		 }
		 if(value!=oldValidity){
			if(confirm("确认修改？")){
				var selectedVal=new Date(value).getTime();
				var today=new Date().getTime();
				if(selectedVal>today){
					console.log("selectedVal:"+selectedVal);
					console.log("today:"+today);
					alert("生产日期不能超过当前日期!");
					$("#"+id).val(oldValidity);
				}else{
				
				 $.ajax({
					 url: '../inventory/editValidity',
					 dataType: 'json',
					 type: 'POST',
					 data: {
						 pl_id:pl_id,
						 validity: value
					 },
					 success: function(data){ 
						 $("#"+id).attr("readonly","true");
						 oldValidity=value;
						 console.log(data);
						 if(data.resule=="success"){
							 alert(data.note);
						 }
					 },
					 error: function(err) {
						 console.log(err);
						 alert("修改日期出错");
						 $("#"+id).val(oldValidity);
					 }
				 })
				}
			 }else {
					$("#"+id).val(oldValidity);
				}
		}
	 }			
}
$(document).ready(function(){
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

	//AJAX查询
	var TOTAL_PAGE = 0, START_PAGE = 1, MAX_PAGE = 10, END_PAGE = 10, CURRENT_PAGE = 1, PAGE_DATA = {}, TOTAL_ORDER = 0;
	
	$("#search").click(function(e){
		e.preventDefault();
		var data={
				customer_id: $("#customer_id").val(),
				location_type: $("#location_type").val(),
				is_show_urikitamono: $("#is_show_urikitamono").val(),
				goods_name: $("#goods_name").val(),
				barcode: $("#barcode").val(),
				is_exception: $("#is_exception").val(),
				from_location_barcode: $("#from_location_barcode").val(),
				to_location_barcode: $("#to_location_barcode").val(),
				status_id: $("#status_id").val(),
				batch_sn:$("#batch_number").val(),
				warehouse_id:$("#warehouse_id").val()
		};

		
		$.ajax({
			url: '../inventory/searchV2',
			dataType:'json',
			type: 'post',
			data: data,
			beforeSend: function(){
				$(".mw-loading").fadeIn(300);
			},
			success: function(res){
				console.log(res);
				$(".mw-loading").fadeOut(300);
				if(res.inventoryGoods.length>0){
					$(".noDataTip").fadeOut(300);
					PAGE_DATA = res.page;
					TOTAL_PAGE = parseInt(res.page.totalPage);
					START_PAGE = 1;
					if (TOTAL_PAGE < MAX_PAGE) {
						END_PAGE = TOTAL_PAGE;
					} else {
						END_PAGE = MAX_PAGE;
					}
					CURRENT_PAGE = res.page.currentPage;
					pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
					 console.log("isDouble:"+res.isDouble11);
					if(res.isDouble11!=1&&res.isDouble11!=null){
						$("#btns").show();
					}else{
						$("#btns").hide();
					}
					showTable(res.inventoryGoods);
					$("#page-search").show();
				}else{
					$("tbody").html("");
					$(".noDataTip").fadeIn(300);
					$("#page-search").css("display","none");
				}
			},
			error: function(err){
				$(".mw-loading").fadeOut(300);
				alert(err);
			}
		})
	})
	
		//分页
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
	
	$("#body-search").on("click",".lq-page-control li",function(e){
		e.preventDefault();
		var that = $(this);
		if (!that.hasClass("prev") && !that.hasClass("next")) {
			if (!that.hasClass("active")) {
				var index = parseInt($(this).find("a").text());
				CURRENT_PAGE = index;
				var data = $("#form-purchase-accept").serialize();
				data+="&currentPage="+index+"&pageSize=10";
				$.ajax({
					url : '../inventory/searchV2',
					type : "get",
					dataType : "json",
					data : {
						currentPage : index,
						pageSize : 20,
						customer_id: $("#customer_id").val(),
						location_type: $("#location_type").val(),
						is_show_urikitamono: $("#is_show_urikitamono").val(),
						goods_name: $("#goods_name").val(),
						barcode: $("#barcode").val(),
						is_exception: $("#is_exception").val(),
						from_location_barcode: $("#from_location_barcode").val(),
						to_location_barcode: $("#to_location_barcode").val(),
						status_id: $("#status_id").val(),
						warehouse_id:$("#warehouse_id").val()
					},
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
						that.addClass("active").siblings().removeClass("active");
					},
					success : function(data) {
						$(".mw-loading").fadeOut(300);
						showTable(data.inventoryGoods);
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
	
	function en2Status(en){
		var str="";
		switch(en){
		case "NORMAL": str="正常";break;
		case "WARNING": str="临期预警";break;
		case "UNSALABLE": str="禁发";break;
		case "EXPIRED": str="过期";break;
		default: str="-";break;
		}
		return str;
	}
	
	function str2Date(str){
		var date=new Date(str);
		var month=date.getMonth()+1;
		var year=date.getFullYear();
		var day=date.getDate();
 		if(parseInt(month)<10) month="0"+month;
		if(parseInt(day)<10) day="0"+day;
		return year+"-"+month+"-"+day;
	}
	 function getDeadLine(validity,day,unit){
		var available=getAvailable(day, unit);
		var deadLine=new Date(new Date(validity).getTime()+available*24*3600*1000);
		var year=deadLine.getFullYear();
		var month=deadLine.getMonth()+1;
		var day=deadLine.getDate();
		month=(month<10)?("0"+month):month;
		day=(day<10)?("0"+day):day;
		return year+"-"+month+"-"+day;
	 }
	 
	 function getAvailable(day,unit){
		 var available;
		 switch(unit){
			 case "MONTH": available=day*30;break;
			 case "DAY": available=day;break;
			 case "YEAR": available=day*365;break;
			 default: available=day;break;
		 }
		 return available;
	 }
	
	 
	function showTable(data){
		var tdHtml='';
		for(var i in data){
			tdHtml+='<tr class="list">';
			tdHtml+='<td class="td_location_barcode">'+data[i].location_barcode+'</td>';
			tdHtml+='<td>'+data[i].location_type_name+'</td>';
			tdHtml+='<td>'+((data[i].product_location_status=="NORMAL")?"否":"是")+'</td>';
			tdHtml+='<td>'+data[i].name+'</td>';
			tdHtml+='<td>'+data[i].barcode+'</td>';
			tdHtml+='<td>'+data[i].product_name+'</td>';
			tdHtml+='<td>'+data[i].warehouse_name+'</td>';
			tdHtml+='<td><shiro:lacksPermission name="validity:modify">'+str2Date(data[i].validity)+'</shiro:lacksPermission>';
			tdHtml+='<shiro:hasPermission name="validity:modify"><input type="text" value="'+str2Date(data[i].validity)+'" class="validity" id="validity'+i+'" readonly onchange="dateChanged(this.value,'+data[i].pl_id+',this.id);"/></shiro:hasPermission>';
			tdHtml+='<td>'+getDeadLine(data[i].validity,data[i].validity_,data[i].validity_unit)+'</td>'
			tdHtml+='<td>'+getAvailable(data[i].validity_,data[i].validity_unit)+'</td>';
			tdHtml+='<td>'+en2Status(data[i].validity_status)+'</td>';
			tdHtml+='<input type="hidden" value="'+data[i].pl_id+'" id="plID">';
			tdHtml+='<td><shiro:lacksPermission name="batchsn:modify">'+data[i].batch_sn+'</shiro:lacksPermission>';
			tdHtml+='<shiro:hasPermission name="batchsn:modify"><input type="hidden" value="'+data[i].pl_id+'" id="pl_id"><input type="text" value="'+data[i].batch_sn+'" class="batchSn" id="batchSN'+i+'" readonly></shiro:hasPermission></td>';
			tdHtml+='<td>'+data[i].serial_number+'</td>';
			tdHtml+='<td>'+((data[i].status=="NORMAL")?"全新":"二手")+'</td>';
			tdHtml+='<td>'+data[i].qty_total+'</td>';
			tdHtml+='<td>'+data[i].qty_available+'</td>';
			tdHtml+='<td>'+data[i].qty_freeze+'</td>';
			tdHtml+='<td>'+(parseInt(data[i].qty_total)-parseInt(data[i].qty_available)-parseInt(data[i].qty_freeze))+'</td>';
			tdHtml+='</tr>';
		}
		
		$("tbody").html(tdHtml);
		

		
		$(".validity").each(function(key,value){
			
			$(this).on("dblclick",function(){
				oldValidity=$(this).val();
				$(this).removeAttr("readonly").focus();
				var index="validity"+key;
				selectedInput=index;
		 		Zapatec.Calendar.setup({
					weekNumbers       : false,
					electric          : false,
					inputField        :	index,
					ifFormat          : "%Y-%m-%d",
					daFormat          : "%Y-%m-%d"
				});
			})
		})
		
		$(".batchSn").each(function(){
			$(this).on("dblclick",function(){
				$(this).removeAttr("readonly").focus();
			})
		})
		
		$(".batchSn").each(function(){
			var that=$(this);
			var oldValue=that.val();
			var pl_Id=$(this).prev().val();
			
			that.change(function(){
				if(that.val().length>32){
					alert("批次号太长,无法修改");
					return false;
				}
				
				if(confirm("确认修改批次号?")){
					if(that.val()!=oldValue&&that.val()!=""){
						var batch_id_reg = new RegExp("^[A-Za-z0-9_-]+$");
						if(!batch_id_reg.test($.trim(that.val()))){
							alert("请正确维护批次号（数字、字母、-、_）!");
							that.val(oldValue);
							that.blur();
							that.attr("readonly",true);	
						}else{
							$.ajax({
								url: '../inventory/editBatchSn',
								type:'post',
								dataType: 'json',
								data: {
									batch_sn:that.val(),
									pl_id: pl_Id
								},
								success: function(res){
									if(res.resule=="success"){
										alert(res.note);
										that.blur();
										that.attr("readonly",true);
										oldValue=that.val();
									}else{
										alert(res.note);
									}
										
									
									
									console.log(res);
								},
								error: function(err){
									alert(err);
								}
							})
						}
					}else if(that.val()==""){
						alert("修改的批次号不能为空!");
						that.val(oldValue);
						that.blur();
						that.attr("readonly",true);						
					}else {

					}
				}
			})
		})
		
		
		$(".td_location_barcode").each(function(){
			if($(this).text()!="undefined"){
				$(this).text(insert_flg($(this).text()));
			}else{
				$(this).text("-")
					.css("text-align","center");
			}
		});
	}
	
	$('#exportInventory').click(function(){
		
		var data ="";
		var customer_id = $('#customer_id').val();
		var barcode = $('#barcode').val();
		var goods_name = $('#goods_name').val();
		var location_type = $('#location_type').val();
		var status_id = $('#status_id').val();
		var from_location_barcode = $('#from_location_barcode').val();
		var to_location_barcode = $('#to_location_barcode').val();
		var is_show_urikitamono = $('#is_show_urikitamono').val();
		var is_exception = $('#is_exception').val();
		
		if(customer_id != '' && customer_id != null){
			data += ("&customer_id="+customer_id);
		}
		if(barcode != '' && barcode != null){
			data += ("&barcode="+barcode);
		}
		if(goods_name != '' && goods_name != null){
			data += ("&goods_name="+goods_name);
		}
		if(location_type != '' && location_type != null){
			data += ("&location_type="+location_type);
		}
		if(status_id != '' && status_id != null){
			data += ("&status_id="+status_id);
		}
		if(from_location_barcode != '' && from_location_barcode != null){
			data += ("&from_location_barcode="+from_location_barcode);
		}
		if(to_location_barcode != '' && to_location_barcode != null){
			data += ("&to_location_barcode="+to_location_barcode);
		}
		if(is_show_urikitamono != '' && is_show_urikitamono != null){
			data += ("&is_show_urikitamono="+is_show_urikitamono);
		}
		if(is_exception != '' && is_exception != null){
			data += ("&is_exception="+is_exception);
		}
			
		//alert(data);
		//return false
		window.location = "../inventory/exportInventory?data"+data;
	})
	
	$('#exportPackBox').click(function(){
		var data ="";
		var customer_id = $('#customer_id').val();
		if(customer_id != '' && customer_id!=null) data += ("&customer_id="+customer_id);
		window.location = "../inventory/exportPackBox?data="+data;
	})
});


</script>
</head>
<body id="body-search">
	<div class="modal-wrap mw-loading">
		<div class="modal-content mc-loading">
			<i class="fa fa-spinner fa-spin" id="loading"></i>
		</div>
	</div>
	<div class="main-container">
		<form method="post" action="../inventory/search" class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="customer_id">选择货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
							<option selected="true"
									value="-1">--请选择--</option> 
							<c:forEach items="${customers}" var="customer">
								<option name="customer_id"
									class="${customer.customer_id}"
									<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
									value="${customer.customer_id}">${customer.name}</option>
							</c:forEach>
						</select>
						<%-- <input type="hidden" id="custonerID" value="${customer_id }"> --%>
					</div>
					
					<div class="lq-form-group">
						<label>库区类型:</label>
 		          		<select name="location_type" id="location_type" class="lq-form-control"> 
							<option value="">--请选择--</option> 
				     		<c:forEach items="${location_type_map}" var="map">
				     			<option value="${map.key}">${map.value}</option>
				     		</c:forEach>
				     	</select>
					</div> 
					
					<div class="lq-form-group"> 
					    <label>是否有货:</label>
						    <select name="is_show_urikitamono" id="is_show_urikitamono" class="lq-form-control">
						    <option value="Y" <c:if test="${is_show_urikitamono == 'Y'}">selected="true"</c:if>>是</option>
						    <option value="N" <c:if test="${is_show_urikitamono == 'N'}">selected="true"</c:if>>否</option>
					    </select>
					 </div>
				</div>
				
				<div class="lq-col-3">
					<div class="lq-form-group"> 
						<label>商品名称:</label>
						<input id ="goods_name" name="goods_name"  value="${goods_name}" class="lq-form-control">
					</div> 
					<div class="lq-form-group">  
					  	<label>商品条码:</label>
					    <input id ="barcode" name="barcode" value="${barcode}" class="lq-form-control">
					</div>
					<div class="lq-form-group"> 
					    <label>是否异常:</label>
						    <select name="is_exception" id="is_exception" class="lq-form-control">
						    <option value="">请选择</option>
						    <option value="Y" <c:if test="${is_exception == 'Y'}">selected="true"</c:if>>是</option>
						    <option value="N" <c:if test="${is_exception == 'N'}">selected="true"</c:if>>否</option>
					    </select>
					 </div>
				</div>
					
				<div class="lq-col-3">
					<div class="lq-form-group">  
					  	<label style="text-indent:-20px;">From库位:</label>
					    <input id ="from_location_barcode" name="from_location_barcode" value="${from_location_barcode}" class="lq-form-control">
					</div>
					
					<div class="lq-form-group">  
					  	<label>To库位:</label>
					    <input id ="to_location_barcode" name="to_location_barcode" value="${to_location_barcode}" class="lq-form-control">
					</div>
					
					<div class="lq-form-group">
						<label>批次号:</label>
						<input id="batch_number" name="batch_number" value="" class="lq-form-control">
					</div>
				</div>		
							    
				<div class="lq-col-3">
					<div class="lq-form-group"> 
						<label>全新:</label>
					    <select name="status_id" id="status_id" class="lq-form-control">
					    	<option value="">所有</option>
					    	<option value="NORMAL" <c:if test="${status_id == 'NORMAL'}">selected="true"</c:if>>全新</option>
					    	<option value="DEFECTIVE" <c:if test="${status_id == 'DEFECTIVE'}">selected="true"</c:if>>二手</option>
					    </select>
				     </div>
				     <div class="lq-form-group">
				     	<label for="warehosue_id">渠道:</label>
				     	<select name="warehouse_id" id="warehouse_id" class="lq-form-control">
				     		<option value="">不限</option>
				     		<c:forEach items="${warehouseList}" var="warehouse">
				     			<option value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
				     		</c:forEach>
				     	</select>
				     </div>	
				     <div class="lq-form-group"> 
			        	<input type="submit" name="act" value="搜索"  class="lq-btn lq-btn-sm lq-btn-primary" id="search"/>
					 </div>
		           	 <div class="lq-form-group" id="btns" style="display:none;">
						<input type="button" id="exportInventory" name="type" value="导出库存清单" class="lq-btn lq-btn-sm lq-btn-primary">
						<input type="button" id="exportPackBox" name="type" value="导出耗材库存" class="lq-btn lq-btn-sm lq-btn-primary">
				     </div>					 
				</div>
			</div>
		</form>
			
		<table class="lq-table" id="batchTable" style="font-size:10px">
			<thead>
				<tr>
					<th style="min-width:82px;">库位条码</th>
					<th>库区</th>
					<th>是否登记异常</th>
					<th>货主</th>
					<th>商品条码</th>
					<th>商品名称</th>
					<th>渠道</th>
					<th style="min-width:82px;">生产日期</th>
					<th style="min-width: 82px;">到期日期</th>
					<th>保质期（天）</th>
					<th>效期状态</th>
					<th style="min-width:82px;">批次号</th>
					<th>串号</th>
					<th>商品状态</th>
					<th>库存总量</th>
					<th>可用库存量</th>
					<th>已冻结库存量</th>
					<th>已分配库存量</th>
				</tr>
			</thead>
			<tbody></tbody>
			<c:if test="${fn:length(inventoryGoods) == '0'}">
			</table>
				<div class="noDataTip">未查到符合条件的数据!</div>
			</c:if> 
		</table>
	</div>
	<div class="page-wrap"></div>
</body>
</html>