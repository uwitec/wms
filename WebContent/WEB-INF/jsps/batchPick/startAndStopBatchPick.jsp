<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<title>生成波次</title>
<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">  
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
<link href="http://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet" />
<style>
.lq-form-group-nodisplay {
	display: none;
}
.header {
	margin: 8px 0 10px 0;
}
.autoWaveOnOff {
	position: relative;
	top: 3px;
	margin: 0;
}
#change_job_id {
	margin: -2px 0 0 0;
}
.search {

}
.btns {
	margin: 10px 0 5px 0;
}
.icon {
	width: 0;
	height: 0;
	border-top: 12px solid #ccc;
	border-left: 8px solid transparent;
	border-right: 8px solid transparent;
	position: relative;
	top: 14px;
	left: 10px;
}
.tableSelect {
	display: inline-block;
}
/*弹窗*/
html, body, #alert {
	width: 100%;
	height: 100%;
}
#alert {
	display: none;
	position: fixed;
	z-index: 999;
}
.cover {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: #000;
	opacity: 0.5;
}
.alertMain {
	position: absolute;
	top: 50%;
	left: 50%;
	background: #fff;
	border-radius: 5px;
}
.alertContain {
	display: table;
	box-sizing: border-box;
	width: 85%;
	height: 77%;
	margin: 0 auto;
}
.alertText {
	display: table-cell;
	vertical-align: middle;
	text-align: center;
	line-height: 18px;
}
.alertBtn {
	display: block;
	box-sizing: border-box;
	width: 100%;
	height: 23%;
	padding: 8px 0;
	border-top: 1px solid #5bc0de;
	cursor: pointer;
	text-align: center;
}
.btn {
	width: 50%;
	height: 100%;
	border: 0;
	float: left;
	/*color: #666;*/
	font-size: 14px;
	background: #fff;
	outline: #ccc;
	cursor: pointer;
}
.trueBtn {
	border-right: 1px solid #5bc0de;
	border-bottom-left-radius: 5px;
	color: #5cb85c;
}
.falseBtn {
	border-bottom-right-radius: 5px;
	color: #d9534f;
}
.oneBtn {
	height: 100%;
	border: 0;
	color: #5cb85c;
	font-size: 14px;
	background: #fff;
	outline: #ccc;
	cursor: pointer;
}

.noDataTip {
	display: none;
	text-align: center;
	font-size: 30px;
	color: #666;
	margin: 30px 0 0 0;
}

input[type=text]{
	height: 30px;
}
input[type=radio]{
	position: relative;
	top: -5px;
}

</style>
</head>
<body>
	<div id="alert">
		<div class="cover"></div>
		<div class="alertMain">
			<div class="alertContain">
				<div class="alertText">
					
				</div>
			</div>
			<div class="alertBtn">
				<button id="close" class="oneBtn">确定</button>
			</div>
		</div>
	</div>
	<div class="modal-wrap mw-loading">
		<div class="modal-content mc-loading">
			<i class="fa fa-spinner fa-spin" id="loading"></i>
		</div>
	</div>
	<div class="main-container">
		<form class="lq-form-inline">
			<div class="header lq-form-group">
				<label>自动波次:</label> 
				<input class="autoWaveOnOff" id="autoWaveOn" type="radio" name="autoWave" value="1">
				<label for="autoWaveOn">开启</label>
				<input class="autoWaveOnOff" id="autoWaveOff" type="radio" name="autoWave" value="0">
				<label for="autoWaveOff">关闭</label>
				<button id="change_job_id"  type="button" class="lq-btn lq-btn-sm lq-btn-primary">提交</button>
			</div>
			<!-- 开启时显示 -->
			<div class="lq-form-group autoWaveOnText">
				<p>今日已处理订单数<span>${opnum}</span>,共生成波次单<span>${bpnum}</span>,今日剩余未生成波次的订单数<span>${snum}</span></p>
			</div>
			<!-- 关闭时显示 -->
			<div class="autoWaveOffText">
				<div class="lq-row">
					<div class="lq-col-2">
						<div class="lq-form-group">
							<label for="customer_id">选择货主:</label> 
							<select name="customer_id" id="customer_id" class="lq-form-control">
								<c:forEach items="${customers}" var="customer">
									<option name="customer_id" value="${customer.customer_id}">${customer.name}
									</option>
								</c:forEach>
							</select>
						</div>
						<div class="lq-form-group">
							<label for="region_id">选择区域:</label> 
							<select
								name="region_id" id="region_id" class="lq-form-control">
								<option value="">--请选择--</option>
								<c:forEach items="${regionList}" var="region">
									<option name="region_id"
										value="${region.region_id}">${region.region_name}</option>
								</c:forEach>
							</select>
						</div>
						
					</div>
					<div class="lq-col-3">
						<div class="lq-form-group">
							<label for="shop_name" style="text-indent:13px;">选择店铺:</label> 
							<select name="shop_name" id="shop_name" class="lq-form-control">
								<option name="shop_name" value="" data="">--不限--</option>
								<c:forEach var="customerShop" items="${customerShops}">
									<c:forEach items="${customerShop.value}" var="shop">
										<c:if test="${!empty shop}">
											<option name="${shop.file_name} " class="${customerShop.key}" data="${ shop.oms_shop_id }" value="${shop.shop_name}">${shop.shop_name}</option>
										</c:if>
									</c:forEach>
								</c:forEach>
								<!-- <input type="hidden" id="customerShops" value="${customerShops}"> -->
							</select>
						</div> 
						<div class="lq-form-group">
							<label for="level">订单优先级:</label> 
							<select name="level" id="level" class="lq-form-control">
								<option value="0">不限</option>
								<option value="3">高级</option>
								<option value="2">中级</option>
								<option value="1">低级</option>	
							</select>
						</div>

					</div>
					<div class="lq-col-3">
						<div class="lq-form-group">
							<label for="start">订单时间:</label>
<%-- 							<input type="text" readonly="true" id="start" name="start" value="${start}" size="12" class="lq-form-control">
							<button id="startTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
								<i class="fa fa-calendar"></i>
							</button>  --%>
							<input class="form-control form_datetime lq-form-control" id="start" type="text" name="start" value="${start}" size="12" readonly/>
						</div>
						<div class="lq-form-gorup">
							 <label for="delivery_time" style="text-indent:48px;">至</label>
<%-- 							<input type="text" readonly="true" id="end" name="end" value="${end}" size="12" class="lq-form-control"/>
							<button id="endTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
								<i class="fa fa-calendar"></i>
							</button> --%>
							<input class="form-control form_datetime lq-form-control" id="end" type="text" name="end" value="${end}" size="12" readonly/>
						</div>
						
					</div>
					<div class="lq-col-2" style="margin-left:-40px;">

						<div class="lq-form-group">
							<label for="level">拣选面是否有货:</label> 
							<select name="pick" id="pick" class="lq-form-control">
								<option value="0" selected="true">不限</option>
								<option value="1">是</option>
							</select>							
						</div>
						<div class="lq-form-group">
							<label for="mark" style="text-indent: 47px;">订单类型</label> 
							<select name="mark" id="mark" class="lq-form-control">
								<option value="10">不限</option>
								<option value=0>正常订单</option>
								<option value=1>缺货订单</option>
							</select>
						</div>
					</div>
					<div class="lq-col-2">
						<div class="lq-form-group">
							<label for="shipping_id">快递方式</label> 
							<select name="shipping_id" id="shipping_id" class="tableSelect lq-form-control">
								<option  value="">可选择</option>
								<c:forEach items="${shippingList}" var="shipping">
									<option name="shipping_id"
										<c:if test="${shipping_id==shipping.shipping_id}">selected="true"</c:if>
										value="${shipping.shipping_id}">${shipping.shipping_name}</option>
								</c:forEach>
							</select>							
						</div>
						<div class="lq-form-group">
							<label for="warehouseList" style="text-indent: 2rem;">渠道</label>
							<select name="warehouseList" id="warehouseList" class="tableSelect lq-form-control">
								<c:forEach items="${list}" var="warehouse">
									<option value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
								</c:forEach>
							</select>
						</div>
					</div>
				</div>
				<div class="btns">
					<button type="button" class="lq-btn lq-btn-sm lq-btn-primary checkAll">全选</button>
					<button type="button" class="lq-btn lq-btn-sm lq-btn-primary checkNot">反选</button>
					<button type="button" class="lq-btn lq-btn-sm lq-btn-primary createBP" disabled="true">生成波次</button>
					<button type="button" class="lq-btn lq-btn-sm lq-btn-primary search" style="position:relative; left:69%">查询</button>
				</div>
				<table class="lq-table">
					<thead>
						<th style="width: 70px;">编号</th>
						<th>订单号</th>
						<th>店铺名称</th>
						<th style="width: 180px;position:relative;">订单时间</th>
						<th >快递方式</th>
						<th>发货区域</th>
						<th>是否有货</th>
					</thead>
					<tbody class="tbody"></tbody>
				</table>
				<div class="noDataTip">未查到符合条件的数据!</div>
			</div>
		</form>
		<%-- <input type="hidden" id="isOutSource" value="${isOutSource}"> --%>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/alertPlugin.js"></script>
	<script type="text/javascript" src="../static/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../static/js/bootstrap-datetimepicker.min.js"></script>	
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/v3.js"></script>
	<script type="text/javascript">
/* 	console.log($("#customerShops").val()); */
	
	//当前仓库为外包仓时候，禁用手动生成波次
	//console.log("isOutSource:"+${isOutSource});
	
 	if(${isOutSource}=="1") {
		$(".btns").hide();
	}else{
		$(".btns").show();
	} 
	
	
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
		$(function() {
			// 初始自动波次状态
			function initStatus () {
				console.log('${status}');
				$('.autoWaveOnOff[value="' + ${status} + '"]').attr('checked', true);
				if ('${status}' == 0) {
					$('.autoWaveOffText').css('display', 'block');
					$('.autoWaveOnText').css('display', 'none');
				} else {
					$('.autoWaveOnText').nextAll().css('display', 'none');
					$('.autoWaveOnText').css('display', 'block');
				}
			}
			
			initStatus();
/* 			var sort='pay_time';
			var sort2='desc'; */
			$("#change_job_id").click(function(e) {
				e.preventDefault();
				console.log($('input[name="autoWave"]:checked').val());
				var status = $('input[name="autoWave"]:checked').val() || 0;
				var data = {
                	status:status,
				};
				$.ajax({
					url: "${pageContext.request.contextPath}/batchPick/changeBatchPickStatus",
					type: "post",
					dataType: "json",
					contentType : "application/x-www-form-urlencoded; charset=utf-8",
					data: data,
					success: function(data) {
						console.log(data);
						if (data.message == '设置成功') {
							$('#alert').css('display', 'block');
							$('#alert').AlertPlugin({
								width: 360,
								height: 220,
								htmlText: '<div style="font-size:20px">'+data.message+'</div>',
								btnCount: 1
							});
							if (status == 0) {
								$('.autoWaveOffText').css('display', 'block');
								$('.autoWaveOnText').css('display', 'none');
							} else {
								$('.autoWaveOnText').nextAll().css('display', 'none');
								$('.autoWaveOnText').css('display', 'block');
							}
						} else {
							return false;
						}
					},
					error: function() {
						console.log("changeBatchPickStatus error");
					}
				});
			});
			// 查询
			$('.search').on('click',function(){
				 showTable();
			});
			function getFilename(shopName){
				var file=";"
				$("#shop_name option").each(function(){
					console.log($(this).attr("name"));
					if($(this).val()==shopName){
						file=$(this).attr("name");
						return false;
					}
				})
				return file;
			}
			
			// 生成波次
			$('.createBP').on('click', function () {
				var orderIds = '';
				var flag=true;
				var customerId="";
				var filename="";
				var thisCustomer;
				var thisFilename;
				var difFilename=false;
				$.each($('input[type="checkbox"]'), function(index, val) {
					if ($(val).prop('checked')) {
						orderIds += $('.orderId').eq(index).html() + ',';
						thisCustomer=$(this).next("input").val();
						thisFilename=getFilename($(this).parents("tr").find("td").eq(2).text());
						console.log(thisCustomer);
						console.log(thisFilename)
						if(customerId!=""){
							if(customerId!=thisCustomer||filename!=thisFilename){
								if(filename!=thisFilename){
									difFilename=true;
								}
								flag=false;
								return false;
							}
						}
						customerId=thisCustomer;
						filename=thisFilename;
					}
				});
				if(!flag){
					if(difFilename)
						alert("发货单不同不能生成波次!");
					else
						alert("货主不同不能生成波次!");
					return ;
				}
				var data = {
						customer_id: $('#customer_id').val(),
						shop_name: $('#shop_name').val(),
						region_id: $('#region_id').val(),
						shipping_id: $('#shipping_id').val(),
						pick: $("#pick").val(),
						/* sort: sort,  */ // 按照哪个字段排序（订单时间、支付时间）
						/* sort2: sort2,  */ //顺序: asc;逆序：desc
						mark: $('#mark').val(),
						level: $('#level').val(),
						currentPage: 1,
						pageSize: 12,
						start: $("#start").val(),
						end: $("#end").val(),
						currentPage: 1,
						pageSize: 12,
						order_ids: orderIds,
						oms_shop_id: $("#shop_name option:selected").attr("data"),
						
						warehouse_id:$("#warehouseList").val()
				};
				$.ajax({
					url: '${pageContext.request.contextPath}/batchPick/batchforquery',
					type: 'post',
					dataType: 'json',
					data: data,
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
					},
					success: function (res) {
						console.log(res);
						$(".mw-loading").fadeOut(300);
						var trHtml = '';
						var trNum = res.orderInfoList.length;
						var val = res.orderInfoList;
						console.log(val);
						var date = '';
						var time = '';
						res.shippingList.splice(0, 0, '');
						for (var i = 0; i < trNum; i ++) {
							date = new Date(val[i].order_time);
							time = date.toString().split(' ')[4];
							
							/* date2 = new Date(val[i].pay_time);
							time2 = date.toString().split(' ')[4]; */
							//trHtml += '<tr><td><input type="checkbox" />'+(i+1)+'</td><td class="orderId">'+val[i].order_id+'</td><td>'+val[i].shop_name+'</td><td>'+date.getFullYear()+'-'+(date.getMonth() + 1)+'-'+date.getDate()+' '+time+'</td><td>'+res.shippingList[val[i].shipping_id].shipping_name+'</td><td>'+val[i].province_name+'</td></tr>';
							trHtml += '<tr><td><input type="checkbox" />'+(i+1)+'<input type="hidden" class="customer" value="'+val[i].customer_id+'"></td><td class="orderId">'+val[i].order_id+'</td><td>'+val[i].shop_name+'</td><td>'+date.getFullYear()+'-'+(date.getMonth() + 1)+'-'+date.getDate()+' '+time+'</td><td>'+res.shippingMap[val[i].shipping_id].shipping_name+'</td><td>'+val[i].province_name+'</td><td>'+figure2Ch(val[i].canAllot)+'</td></tr>';
						}
						$('.tbody').html(trHtml);
					}
				});	
			});
			
			
			function figure2Ch(num){
				var ch="";
				switch(num){
				case 0: ch="未判断";break;
				case 1: ch="可以分配";break;
				case 2: ch="不能分配";
				}
				return ch;
			}
			function showTable () {	
	/* 			console.log(sort+","+sort2); */
				$('.search').attr('disabled', true);
				if($("#start").val()==""){
					$(".search").removeAttr("disabled");
					alert("开始时间不能为空!");
					
					return ;
				}
				var data = {
					customer_id: $('#customer_id').val(),
					shop_name: $('#shop_name').val(),
					region_id: $('#region_id').val(),
					shipping_id: $('#shipping_id').val(),
					pick: $("#pick").val(),
					/* sort: sort,  */ // 按照哪个字段排序（订单时间、支付时间）
					/* sort2: sort2,  */ //顺序: asc;逆序：desc
					mark: $('#mark').val(),
					level: $('#level').val(),
					currentPage: 1,
					pageSize: 12,
					start: $("#start").val(),
					end: $("#end").val(),
					oms_shop_id: $("#shop_name option:selected").attr("data"),
					
					warehouse_id:$("#warehouseList").val()
				};
				console.log($("#shop_name option:selected").attr("data"));
				$.ajax({
					url: '${pageContext.request.contextPath}/batchPick/queryV2',
					type: 'post',
					dataType: 'json',
					data: data,
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
					},
					success: function (res) {
						$(".mw-loading").fadeOut(300);
						$('.noDataTip').fadeOut(300);
						console.log(res.orderInfoList.length);
						if (res.orderInfoList.length != 0) {
							$('.tbody').show();
							var trHtml = '';
							var trNum = res.orderInfoList.length;
							var val = res.orderInfoList;
							console.log(val);
							var date = '';
							var time = '';
							res.shippingList.splice(0, 0, '');
							for (var i = 0; i < trNum; i ++) {
								date = new Date(val[i].order_time);
								time = date.toString().split(' ')[4];
								
								/* date2 = new Date(val[i].pay_time);
								time2 = date.toString().split(' ')[4]; */
								trHtml += '<tr><td><input type="checkbox" />'+(i+1)+'<input type="hidden" class="customer" value="'+val[i].customer_id+'"></td><td class="orderId">'+val[i].order_id+'</td><td class="shopname">'+val[i].shop_name+'</td><td>'+date.getFullYear()+'-'+(date.getMonth() + 1)+'-'+date.getDate()+' '+time+'</td><td>'+res.shippingMap[val[i].shipping_id].shipping_name+'</td><td>'+val[i].province_name+'</td><td>'+figure2Ch(val[i].canAllot)+'</td></tr>';

								//trHtml += '<tr><td><input type="checkbox" />'+(i+1)+'</td><td class="orderId">'+val[i].order_id+'</td><td>'+val[i].shop_name+'</td><td>'+date.getFullYear()+'-'+(date.getMonth() + 1)+'-'+date.getDate()+' '+time+'</td><td>'+res.shippingMap[val[i].shipping_id].shipping_name+'</td><td>'+val[i].province_name+'</td></tr>';
							
								//trHtml += '<tr><td><input type="checkbox" />'+(i+1)+'</td><td class="orderId">'+val[i].order_id+'</td><td>'+val[i].shop_name+'</td><td>'+date.getFullYear()+'-'+(date.getMonth() + 1)+'-'+date.getDate()+' '+time+'</td><td>'+(res.shippingList[val[i].shipping_id].shipping_name || res.shippingList[val[i].shipping_id])+'</td><td>'+val[i].province_name+'</td></tr>';
							}
							$('.tbody').html(trHtml);
							$('.createBP').attr('disabled', false);
							$('.search').attr('disabled', false);
						} else {
							$('.tbody').hide();
							$('.noDataTip').fadeIn(300);
							$('.search').attr('disabled', false);
						}
					}
				});
			}
			// 通过选择货主来选择店铺
			$('#customer_id').on('change', function () {
 				$('#shop_name').find('option').css('display', 'block').removeAttr("selected");
				var customerId = $(this).val();
				if(customerId!="0"){
				$('#shop_name').find('option:gt(0)').not($('.' + customerId)).css('display', 'none');
				 console.log("列表长度:"+$("."+customerId).length);
				 $("#shop_name").find("option").eq(0).attr("selected",true);
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
		});
	</script>
</body>
</html>