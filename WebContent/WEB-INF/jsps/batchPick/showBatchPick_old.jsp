<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<title>生成波次单</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/static/css/global.css">
<link rel="stylesheet"
	href="../static/js/zapatec/zpcal/themes/winter.css" />
<link
	href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css"
	rel="stylesheet">
<style>
tr.list1 {
	display: table-row;
}

tr.list2,tr.list3 {
	display: none;
}

#tab-bp {
	padding: 10px;
}

#batchTable_showsd {
	margin-bottom: 10px;
}
/*弹框*/
.alertShow {
	position: absolute;
	top: 0px;
	width: 100%;
	height: 100%;
	z-index: 1000;
}
html, body {
	height: 100%;
}
.cover {
	width: 100%;
	height: 100%;
	background-color: #000;
	opacity: 0.5;
	filter: alpha(opacity=50);
}
.alertBox {
	position: absolute;
	top: 50%;
	left: 50%;
	width: 360px;
	height: 220px;
	margin: -110px 0 0 -180px;
	background: #fff;
	border-radius: 5px;
}
.alertIpt {
	margin: 40px auto;
	text-align: center;
}
.iptText {
	display: inline-block;
    width: 60%;
    height: 30px;
	background-color: #fff;
    background-image: none;
    border: 1px solid #ccc;
    border-radius: 4px;
    color: #555;
    font-size: 1rem;
    line-height: 1.42857;
    padding:2px 6px;
    margin: 10px 0;
}
.alertBtn {
	position: absolute;
	bottom: 0;
	width: 100%;
	padding: 8px 0;
	border-top: 1px solid #5bc0de;
	cursor: pointer;
}
.btn {
	left: 0;
	width: 50%;
	height: 33px;
	border: 0;
	float: left;
	/*color: #666;*/
	font-size: 14px;
	background: #fff;
	outline: #ccc;
	cursor: pointer;
}
/*.btn:visited {
	color: #333;
}
.btn:active {
	color: #333;
}*/
.trueBtn {
	border-right: 1px solid #5bc0de;
	border-bottom-left-radius: 5px;
	color: #5cb85c;
}
.falseBtn {
	border-bottom-right-radius: 5px;
	color: #d9534f;
}
.alertP {
	margin: 70px auto;
	text-align: center;
	font-size: 16px;
}
.oneBtn {
	width: 100%;
	height: 49px;
	color: #5cb85c;
	outline: #ccc;
	background: #fff;
	border: 0;
	border-top: 1px solid #5bc0de;
	border-bottom-left-radius: 5px;
	border-bottom-right-radius: 5px;
	cursor: pointer;
}
#creat_batch_pick_div {
	display: none;
}
#closeDiv {
	display: none;
}
</style>
</head>
<body id="body-batch-pick">
	<!-- 弹框 -->
	<div class="alertShow" id="creat_batch_pick_div" >
		<div class="cover"></div>
		<div class="alertBox">
			<div class="alertIpt">
				<div class="itemIpt">
					<label for="order_num">订单总数:</label>
					<input type="text"  id="order_num" name="order_num" class="iptText">
				</div>
				<div class="itemIpt">
					<label for="batch_num">波次单数:</label>
					<input type="text" id="batch_num" name="batch_num" class="iptText">
				</div>
			</div>
			<div class="alertBtn">
				<button class="btn trueBtn" id="creat_batch_pick_submit">确定</button>
				<button class="btn falseBtn" id="creat_batch_pick_close">取消</button>
			</div>
		</div>
	</div>
	<div id="closeDiv" class="alertShow">
		<div class="cover"></div>
		<div class="alertBox">
			<p class="alertP"></p>
			<button id="close" class="oneBtn alertBtn">确定</button>
		</div>
	</div>

	<div class="main-container">
		<div class="bp-search-container">
			<form method="post" class="lq-form-inline">
				<div class="lq-row">
					<div class="lq-col-3">
						<div class="lq-form-group">
							<label for="customer_id" style="width: 6.4em;">选择货主:</label>
							<select name="customer_id" id="customer_id"
								class="lq-form-control">
								<c:forEach items="${customers}" var="customer">
									<option name="customer_id"
										class="${customer.customer_id}"
										<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
										value="${customer.customer_id}">${customer.name}</option>
								</c:forEach>
							</select>
						</div>

						<div class="lq-form-group">
							<label for="warehouse_id" style="width: 6.4em;">选择逻辑仓库:</label> <select
								name="warehouse_id" id="warehouse_id" class="lq-form-control">
								<option value="all">不限</option>
								<c:forEach items="${warehouseList}" var="warehouse">
									<option name="warehouse_id"
										class="${warehouse.physical_warehouse_id}"
										<c:if test="${warehouse_id==warehouse.warehouse_id}">selected="true"</c:if>
										value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
								</c:forEach>
							</select>
						</div>
					</div>

					<div class="lq-col-3">
						<div class="lq-form-group">
							<label for="specialOrderId" style="width: 5.4em;">指定订单号:</label>
							<input type="text" id="specialOrderId" name="specialOrderId"
								class="lq-form-control">
						</div>
						<!-- delete@20160425 by dlyao 
						<div class="lq-form-group">
							<label for="shipping_id" style="width: 5.4em;">选择快递:</label> <select
								name="shipping_id" id="shipping_id" class="lq-form-control">
								<option value="">--请选择--</option>
								<c:forEach items="${shippingList}" var="shipping">
									<option name="shipping_id"
										<c:if test="${shipping_id==shipping.shipping_id}">selected="true"</c:if>
										value="${shipping.shipping_id}">${shipping.shipping_name}</option>
								</c:forEach>
							</select>
						</div>
						 -->
						<div class="lq-form-group">
							<label for="region_id" style="width: 5.4em;">选择区域:</label> <select
								name="region_id" id="region_id" class="lq-form-control">
								<option value="">--请选择--</option>
								<c:forEach items="${regionList}" var="region">
									<option name="region_id"
										<c:if test="${region_id==region.region_id}">selected="true"</c:if>
										value="${region.region_id}">${region.region_name}</option>
								</c:forEach>
							</select>
						</div>
					</div>

					<div class="lq-col-6">
						<div class="lq-form-group">
							<label for="" style="width: 4.4em;">订单时间:</label> 
								<input type="text" id="start" name="start" value="${start}" size="10" class="time lq-form-control">
							<button id="startTrigger"
								class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
								<i class="fa fa-calendar"></i>
							</button>
							<span>至</span> <input type="text" id="end" name="end"
								value="${end}" size="10" class="time lq-form-control"
								readonly="true">
							<button id="endTrigger"
								class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
								<i class="fa fa-calendar"></i>
							</button>
						</div>
						<!-- delete@20160425 by dlyao remove to lq-col-3
						<div class="lq-form-group">
							<label for="region_id" style="width: 4.4em;">选择区域:</label> <select
								name="region_id" id="region_id" class="lq-form-control">
								<option value="">--请选择--</option>
								<c:forEach items="${regionList}" var="region">
									<option name="region_id"
										<c:if test="${region_id==region.region_id}">selected="true"</c:if>
										value="${region.region_id}">${region.region_name}</option>
								</c:forEach>
							</select>
						-->
							<button id="submit" class="lq-btn lq-btn-sm lq-btn-primary">
								<i class="fa fa-search"></i>查询
							</button>
						</div>
					</div>
				</div>

				<div class="lq-row">
					<div class="lq-col-12">
						<div class="lq-form-group" id="customerShop">
							<label for="">订单商品类型:</label>
							<!-- <input type="button" id="btn1" value="全选" onclick="btn1()">  -->
							<!-- <input type="button" id="btn2" value="清空" onclick="btn2()">  -->

							<c:forEach var="customerShop" items="${customerShops}">   
                               <c:forEach items="${customerShop.value}" var="shopName">
                                <c:if test="${!empty shopName}">
								<input type="checkbox" class="btnCheckBox ${customerShop.key}" value="${shopName}"><span class="${customerShop.key}">${shopName}</span>
								</c:if>
								</c:forEach>  
                            </c:forEach> 

						</div>
					</div>
				</div>
			</form>
		</div>
	</div>


	<!-- 创建任务-->

	<!-- <div id="creat_batch_pick_div"
		style="display: none; position: absolute; z-index: 2; left: 25%; width: 40%; background-color: gray">
		<div
			style="background-color: white; margin-top: -20px; margin-bottom: 30px">
			<p style="text-align: center; font-size: 20px">生成波次单</p>
		</div>

		<table>
			<tr>
				<td>&nbsp;&nbsp;订单总数:</td>
				<td><input type="text" id="order_num" name="order_num"></td>
			</tr>

			<tr>
				<td>&nbsp;&nbsp;波次单数:</td>
				<td><input type="text" id="batch_num" name="batch_num"></td>
			</tr>


		</table>
		<button id="creat_batch_pick_submit"
			style="margin-left: 23%; margin-bottom: 20px">确认</button>
		<button id="creat_batch_pick_close"
			style="margin-left: 43%; margin-bottom: 20px">关闭</button>
	</div> -->

	<!-- 提示框 -->
	<!-- <div id="closeDiv"
		style="display: none; position: absolute; z-index: 3; left: 30%; width: 30%; background-color: gray">
		<p style="margin-left: 4%"></p>
		<button id="close" style="margin-left: 43%; margin-bottom: 20px">
			确认</button>
	</div>
 -->


	<div class="lq-tab-wrap" id="tab-bp">
		<ul class="lq-tab-nav">
			<li class="table_bt_batchpick_show_1 active">商品相同，数目相同</li>
			<li class="table_bt_batchpick_show_2">商品相同，数目不同</li>
			<li class="table_bt_batchpick_show_3">商品不同</li>
		</ul>
		<div class="lq-tab-content">
			<form method="post" id="form">
				<table class="lq-table" id="batchTable_showsd">
					<thead>
						<tr>
							<th>goods_name</th>
							<th>sku_sum</th>
							<th>product_key</th>
							<th>product_num</th>
							<th style="width: 100%;">订单归总信息</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				<button id="submit_create" class="lq-btn lq-btn-primary">
					<i class="fa fa-shopping-basket"></i>波次
				</button>
			</form>
		</div>
	</div>
	</div>
	<script type="text/javascript"
		src="../static/js/zapatec/utils/zapatec.js"></script>
	<script type="text/javascript"
		src="../static/js/zapatec/zpcal/src/calendar.js"></script>
	<script type="text/javascript"
		src="../static/js/zapatec/zpcal/lang/calendar-en.js"></script>
	<script type="text/javascript">
		Zapatec.Calendar.setup({
			weekNumbers : false,
			electric : false,
			inputField : "start",
			button : "startTrigger",
			ifFormat : "%Y-%m-%d",
			daFormat : "%Y-%m-%d"
		});

		Zapatec.Calendar.setup({
			weekNumbers : false,
			electric : false,
			inputField : "end",
			button : "endTrigger",
			ifFormat : "%Y-%m-%d",
			daFormat : "%Y-%m-%d"
		});
	</script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript">
		$("document").ready(
				function() {

					$("#batchTable_showsd thead tr th:lt(4)").css("display",
							"none");
					$("#batchTable_showsd tbody tr td:lt(4)").css("display",
							"none");

					$(".btnCheckBox[value='']").css("display", "none");

					$(".table_bt_batchpick_show_1").click(
							function() {
								$(this).addClass("active").siblings()
										.removeClass("active");

								// $(this).siblings('button').css("background-color", "white");
								// $(this).css("background-color", "blue");
								$('tr.list2').hide();
								$('tr.list3').hide();
								$('tr.list1').show();

							});
					$(".table_bt_batchpick_show_2").click(
							function() {
								$(this).addClass("active").siblings()
										.removeClass("active");
								// $(this).siblings('button').css("background-color", "white");
								// $(this).css("background-color", "blue");
								$('tr.list1').hide();
								$('tr.list3').hide();
								$('tr.list2').show();

							});

					$(".table_bt_batchpick_show_3").click(
							function() {
								$(this).addClass("active").siblings()
										.removeClass("active");
								// $(this).siblings('button').css("background-color", "white");
								// $(this).css("background-color", "blue");
								$('tr.list2').hide();
								$('tr.list1').hide();
								$('tr.list3').show();

							});

					$("#btn1").click(function() {

						$(".btnCheckBox").attr("checked", true);//全选

					});

					$("#btn2").click(function() {

						$(".btnCheckBox").removeAttr("checked");//取消全选

					});

					$("#close").click(function() {

						$("#closeDiv").css("display", "none");

					});

					$("#creat_batch_pick_close").click(function() {

						$("#creat_batch_pick_div").css("display", "none");

					});
					
					var c = $("select#customer_id option:eq(0)")
							.attr("class");
					$("#customerShop input").removeAttr("checked","checked");
					$("#customerShop input,span").css("display", "none");
					var c2 = "#customerShop input." + c;
					var c3 = "#customerShop span." + c;

					$(c2).css("display", "inline-block");
					$(c2).attr("checked","checked");
					$(c3).css("display", "inline-block");

					$("select#customer_id").change(
							function() {
								var p2 = $(this).children('option:selected')
										.attr("class");
								var s = "#customerShop input." + p2;
								var s2 = "#customerShop span." + p2;
								$("#customerShop input").removeAttr("checked","checked");
								$("#customerShop input,span").css("display",
										"none");
								$(s).attr("checked","checked");
								$(s).css("display", "inline-block");
								$(s2).css("display", "inline-block");

							});

				});

		function findAllShopNames() {
			var str = "";
			$(".btnCheckBox:checked").each(function() {
				if (this.value != '') {
					str += this.value + ",";
				}
			});
			//alert(str);
			return str;

		}

		function tableConstructor1(data) {
			//console.log(data);
			if (data.length) {
				var tableHtml = '';
				for ( var key in data) {

					tableHtml += '<tr  align="center" class="list1"><td>'
							+ data[key].goods_name
							+ '</td><td>'
							+ data[key].sku_sum
							+ '</td><td>'
							+ data[key].product_key
							+ '</td><td>'
							+ data[key].product_num
							+ '</td><td><input type="radio" name="radio" value='+data[key].product_key+";"+data[key].product_num+'>'
							+ data[key].shows_batchpick + '</td></tr>';

				}

				$("#batchTable_showsd tbody").html(tableHtml);

				$("#batchTable_showsd").fadeIn(300);
				$("#batchTable_showsd tbody tr td:lt(4)")
						.css("display", "none");
			} else {
				$("#batchTable_showsd tbody").html("");
				$("#batchTable_showsd").fadeIn(300);
			}
		}

		function tableConstructor2(data) {
			//console.log(data);
			if (data.length) {
				var tableHtml = '';
				for ( var key in data) {

					tableHtml += '<tr  align="center" class="list2"><td><a href ="../sale/detail?order_id='
							+ data[key].goods_name
							+ '">'
							+ data[key].goods_name
							+ '</a></td><td>'
							+ data[key].sku_sum
							+ '</td><td>'
							+ data[key].product_key
							+ '</td><td>'
							+ data[key].product_num
							+ '</td><td><input type="radio" name="radio" value='+data[key].product_key+";"+data[key].product_num+'>'
							+ data[key].shows_batchpick + '</td></tr>';

				}
				$("#batchTable_showsd tbody").html(
						$("#batchTable_showsd tbody").html() + tableHtml);
				$("#batchTable_showsd tbody tr td:lt(4)")
						.css("display", "none");
				//$("#batchTable_showsd").fadeIn(300);
			} else {
				//$("#batchTable_showsd tbody").html("");
				$("#batchTable_showsd").fadeIn(300);
			}
		}

		function tableConstructor3(data) {
			//console.log(data);
			if (data.length) {
				var tableHtml = '';
				for ( var key in data) {
					tableHtml += '<tr  align="center" class="list3"><td><a href ="../sale/detail?order_id='
							+ data[key].goods_name
							+ '">'
							+ data[key].goods_name
							+ '</a></td><td>'
							+ data[key].sku_sum
							+ '</td><td>'
							+ data[key].product_key
							+ '</td><td>'
							+ data[key].product_num
							+ '</td><td><input type="radio" name="radio" value='+data[key].product_key+";"+data[key].product_num+'>'
							+ data[key].shows_batchpick + '</td></tr>';

				}
				$("#batchTable_showsd tbody").html(
						$("#batchTable_showsd tbody").html() + tableHtml);
				$("#batchTable_showsd tbody tr td:lt(4)")
						.css("display", "none");
				//$("#batchTable_showsd").fadeIn(300);
			} else {
				//$("#batchTable_showsd tbody").html("");
				$("#batchTable_showsd").fadeIn(300);
			}
		}

		//******ajax_1  for  query  same orders
		$(document)
				.ready(
						function() {
							var inNum=100000;
							$("#submit")
									.click(
											function(e) {
												e.preventDefault();
												var shopnames = findAllShopNames();
												var data = {
													shopnames : shopnames,
													start : $("#start").val(),
													end : $("#end").val(),
													customer_id : $(
															"#customer_id")
															.val(),
													warehouse_id : $(
															"#warehouse_id")
															.val(),
													specialOrderId : $(
															"#specialOrderId")
															.val(),
													//shipping_id : $(
													//		"#shipping_id")
													//		.val(),
													region_id : $("#region_id")
															.val(),

												};
												//console.log(saleSearchData);
												$
														.ajax({
															url : "${pageContext.request.contextPath}/batchPick/search2",
															type : "post",
															dataType : "json",
															contentType : "application/x-www-form-urlencoded; charset=utf-8",
															data : data,

															success : function(
																	data) {

																if (data.errorkey == 'errorkey') {

																	$(
																			'#closeDiv')
																			.css(
																					"display",
																					"block");
																	$(
																			'#closeDiv p')
																			.html(
																					data.message);
																} else {
																	tableConstructor1(data.sdList1);
																	tableConstructor2(data.sdList2);
																	tableConstructor3(data.sdList3);
																	$(
																			"#batchTable_showsd tbody tr td:lt(4)")
																			.css(
																					"display",
																					"none");

																	$(
																			".table_bt_batchpick_show_1")
																			.addClass(
																					"active")
																			.siblings()
																			.removeClass(
																					"active");

																	$(
																			'tr.list2')
																			.hide();
																	$(
																			'tr.list3')
																			.hide();
																	$(
																			'tr.list1')
																			.show();
																	$(
																			"#batchTable_showsd tbody tr")
																			.find(
																					"td:lt(4)")
																			.css(
																					"display",
																					"none");
																}

															},
															error : function() {
																alert("ajax1 error");

															}
														});
											});

						});

		//*****ajax_2   for check warehouse  load
		$(document)
				.ready(
						function() {
							$("#submit_create")
									.click(
											function(e) {
												e.preventDefault();
												if ($('#batchTable_showsd tbody tr td input[type="radio"][name="radio"]:checked').length == 0) {
													$('#closeDiv').css(
															"display", "block");
													$('#closeDiv p').html(
															"请选需要波次的选项");
													//console.log($('#closeDiv p'));
												} else {

													var data = {
														warehouse_id : $(
																"#warehouse_id")
																.val(),
																customer_id : $(
																"#customer_id")
																.val(),

													};

													$
															.ajax({
																url : "${pageContext.request.contextPath}/batchPick/checkWarehouseLoad",
																type : "post",
																dataType : "json",
																contentType : "application/x-www-form-urlencoded; charset=utf-8",
																data : data,

																success : function(
																		data) {
																//还剩的可发单量
																	inNum=data.inNum;

																//未到发货单量80%
																	if (data.mark) {
																		
																																		
																		$('#creat_batch_pick_div')
																				.css(
																						"display",
																						"block");
																		$("#order_num").val($('#batchTable_showsd tbody tr td input[type="radio"][name="radio"]:checked')
																								.parent()
																								.parent()
																								.find('td:eq(1)')
																								.html());
																		
																	}
                                                                    //未到100%
																	else if (data.mark2) {
																		$('#closeDiv').css("display","block");
																		$('#closeDiv div p').html(data.message);
																		
																		$('#creat_batch_pick_div').css(
																				"display",
																				"block");
																$("#order_num")
																		.val(
																				$('#batchTable_showsd tbody tr td input[type="radio"][name="radio"]:checked')
																						.parent()
																						.parent()
																						.find('td:eq(1)')
																						.html());

																		//$("#batchTable_showsd tbody tr td:lt(4)").css("display","none");

																	} 
																    //达到发货单量
																	else {
																		$('#closeDiv').css("display","block");
																		$('#closeDiv div p')
																				.html(data.message);
																	}

																},
																error : function() {
																	alert("ajax2 error");

																}
															});

												}

											});

							//****ajax_4  for create batchpick  task

							$("#creat_batch_pick_submit").click(function(e) {
												$("#creat_batch_pick_div").css(
														"display", "none");
												e.preventDefault();
												var shopnames = findAllShopNames();
												if (isNaN($("#batch_num").val())||isNaN($("#order_num").val())||parseInt($("#order_num").val()) > parseInt($('#batchTable_showsd tbody tr td input[type="radio"][name="radio"]:checked')
														.parent().parent().find('td:eq(1)').html())
														|| parseInt($("#order_num").val()) <= 0
														|| parseInt($("#batch_num").val()) <= 0
														|| parseInt($("#batch_num").val()) > parseInt($("#order_num").val())||parseInt($("#order_num").val())>inNum||parseInt($("#order_num").val())/parseInt($("#batch_num").val())>30) 
												{
													$('#closeDiv').css(
															"display", "block");
													$('#closeDiv p').html(
															"请重新设置波次条件");
													return ;
												} else {
													var data = {
															customer_id : $(
															"#customer_id")
															.val(),
														warehouse_id : $(
																"#warehouse_id")
																.val(), //12306
														specialOrderId : $(
																"#specialOrderId")
																.val(),
													//	shipping_id : $(
													//			"#shipping_id")
													//			.val(),
														region_id : $(
																"#region_id")
																.val(),
														order_num : $(
																"#order_num")
																.val(),
														batch_num : $(
																"#batch_num")
																.val(),
														start : $("#start")
																.val(),
														end : $("#end").val(),
														shopnames : shopnames,

														sku_num : $(
																'#batchTable_showsd tbody tr td input[type="radio"][name="radio"]:checked')
																.parent()
																.parent()
																.find(
																		'td:eq(1)')
																.html(),

														product_key : $(
																'#batchTable_showsd tbody tr td input[type="radio"][name="radio"]:checked')
																.parent()
																.parent()
																.find(
																		'td:eq(2)')
																.html(),

														product_num : $(
																'#batchTable_showsd tbody tr td input[type="radio"][name="radio"]:checked')
																.parent()
																.parent()
																.find(
																		'td:eq(3)')
																.html(),
														type : $(
																'#batchTable_showsd tbody tr td input[type="radio"][name="radio"]:checked')
																.parent()
																.parent()
																.attr("class"),

													};

													$
															.ajax({
																url : "${pageContext.request.contextPath}/batchPick/createBatchPickTask",
																type : "post",
																dataType : "json",
																contentType : "application/x-www-form-urlencoded; charset=utf-8",
																data : data,

																success : function(
																		data) {
																	if (data.mark) {

																		console
																				.log(data.sdList1);
																		//刷新数据
																		tableConstructor1(data.sdList1);

																		console
																				.log(data.sdList2);
																		tableConstructor2(data.sdList2);

																		console
																				.log(data.sdList3);
																		tableConstructor3(data.sdList3);

																		$(
																				".table_bt_batchpick_show_1")
																				.addClass(
																						"active")
																				.siblings()
																				.removeClass(
																						"active");
																		$(
																				'tr.list2')
																				.hide();
																		$(
																				'tr.list3')
																				.hide();
																		$(
																				'tr.list1')
																				.show();
																		$(
																				"#batchTable_showsd tbody tr")
																				.find("td:lt(4)")
																				.css("display",
																						"none");

																		//刷新数据
																		$('#closeDiv')
																				.css(
																						"display",
																						"block");
																		$('#closeDiv div p')
																				.html(
																						data.successMessage);

																	} else {

																		$(
																				'#closeDiv')
																				.css(
																						"display",
																						"block");
																		$(
																				'#closeDiv p')
																				.html(
																						data.errorMessage);

																	}

																},
																error : function() {
																	alert("ajax4 error");

																}
															});
												}

											});

						});
	</script>
</body>
</html>