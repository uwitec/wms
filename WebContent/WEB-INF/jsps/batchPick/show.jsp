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
<title>设置波次条件页面</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script type="text/javascript"
	src="../static/js/zapatec/utils/zapatec.js"></script>
<script type="text/javascript"
	src="../static/js/zapatec/zpcal/src/calendar.js"></script>
<script type="text/javascript"
	src="../static/js/zapatec/zpcal/lang/calendar-en.js"></script>
<link rel="stylesheet"
	href="../static/js/zapatec/zpcal/themes/winter.css" />

<style type="text/css">
.search td {
	font-size: 13px;
}

.table_bt_batchpick_show_1 {
	width: 33%;
	margin-bottom: 0px;
	background-color: blue;
	height: 25px
}

.table_bt_batchpick_show_2 , .table_bt_batchpick_show_3{
	width: 33%;
	margin-bottom: 0px;
	background-color: white;
	height: 25px
}

tr.list1 {
   display:true;
}

tr.list2,tr.list3 {
   display:none;
}
</style>
<script type="text/javascript">
	$("document").ready(
			function() {

				$(".table_bt_batchpick_show_1").click(function() {

					$(this).siblings('button').css("background-color","white");
					$(this).css("background-color","blue");
					$('tr.list2').hide();
					$('tr.list3').hide();
					$('tr.list1').show();

					
				})
				$(".table_bt_batchpick_show_2").click(function() {

					$(this).siblings('button').css("background-color","white");
					$(this).css("background-color","blue");
					$('tr.list1').hide();
					$('tr.list3').hide();
					$('tr.list2').show();
					
				})
				
				$(".table_bt_batchpick_show_3").click(function() {

					$(this).siblings('button').css("background-color","white");
					$(this).css("background-color","blue");
					$('tr.list2').hide();
					$('tr.list1').hide();
					$('tr.list3').show();
					
				})
				
				
				$("#btn1").click(function() {

					$(".btnCheckBox").attr("checked", true);//全选
					$("#checkValue").val(3);

				})

				$("#btn2").click(function() {

					$(".btnCheckBox").removeAttr("checked");//取消全选
					$("#checkValue").val(0);
				})

				$(".btnCheckBox").click(
						function() {

							if ($("#checkbox1").attr("checked")
									&& $("#checkbox2").attr("checked")) {
								$("#checkValue").val(3);
							} else if ($("#checkbox1").attr("checked")) {
								$("#checkValue").val(1);
							} else if ($("#checkbox2").attr("checked")) {
								$("#checkValue").val(2);
							} else {
								$("#checkValue").val(0);
							}

						})

			})
</script>
</head>
<body>
	<fieldset style="-moz-border-radius: 6px; padding: 10px;">
		<legend>
			<span style="font-weight: bold; font-size: 18px; color: #2A1FFF;">&nbsp;设置波次条件&nbsp;</span>
		</legend>

		<div style="clear: both;">
			<div style="clear: both;">
				<form method="post" action="../batchPick/search">
					<h3 style="color: #09F;">设置波次条件</h3>
					<table class="search">
						<tr>
							<td>&nbsp;&nbsp;选择仓库:</td>
							<td><select name="warehouse_id">
									<option value="">--请选择--</option>
									<c:forEach items="${warehouseList}" var="warehouse">
										<option name="warehouse_id"
											<c:if test="${warehouse_id==warehouse.warehouse_id}">selected="true"</c:if>
											value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
									</c:forEach>
							</select></td>

						</tr>

						<tr>
							<td>&nbsp;&nbsp;指定订单号:</td>
							<td><input type="text" id="specialOrderId"
								name="specialOrderId"></td>
						</tr>

						<tr>
							<td>&nbsp;&nbsp;选择快递:</td>
							<td><select name="shipping_id">
									<option value="">--请选择--</option>
									<c:forEach items="${shippingList}" var="shipping">
										<option name="shipping_id"
											<c:if test="${shipping_id==warehouse.shipping_id}">selected="true"</c:if>
											value="${shipping.shipping_id}">${shipping.shipping_name}</option>
									</c:forEach>
							</select></td>

						</tr>
						<tr>
							<td width="90">&nbsp;&nbsp;订单时间:</td>
							<td><input type="text" id="start" name="start"
								value="${start}" size="10" class="time" /> <input type="button"
								id="startTrigger" value="日历" class="cal" /></td>
							<td width="90">&nbsp;&nbsp;至:</td>
							<td><input type="text" id="end" name="end" value="${end}"
								size="10" class="time" /> <input type="button" id="endTrigger"
								value="日历" class="cal" /> <label>1231231234123</label></td>


						</tr>
						<tr>
							<td>&nbsp;&nbsp;选择区域:</td>
							<td><select name="region_name">
									<option value="">--请选择--</option>
									<c:forEach items="${regionList}" var="region">
										<option name="region_name"
											<c:if test="${region_name==region.region_name}">selected="true"</c:if>
											value="${region.region_name}">${region.region_name}</option>
									</c:forEach>
							</select></td>
						</tr>
						<tr>
							<td>&nbsp;&nbsp;订单商品类型:</td>
							<td><input type="button" id="btn1" value="全选"
								onclick="btn1()"> <input type="button" id="btn2"
								value="清空" onclick="btn2()"> <input type="checkbox"
								id="checkbox1" class="btnCheckBox" value="checkbox1"> 单品
								<input type="checkbox" id="checkbox2" class="btnCheckBox"
								value="checkbox2"> 多品 <input type="text" id="checkValue"
								name="checkValue"></td>
						</tr>
					</table>

					<input type="submit">
				</form>
			</div>
			<div style="margin-bottom: -10px">
				<button class="table_bt_batchpick_show_1">商品相同，数目相同</button>
				<button class="table_bt_batchpick_show_2">商品相同，数目不同</button>
				<button class="table_bt_batchpick_show_3">商品不同</button>
			</div>
			<div>
				<form method="post" id="form">
					<table class="bWindow" style="margin-top: 10px;" id="batchTable">
						<tr style="background-color: #999999; color: white">
							<th>index</th>
							<th>goods_name</th>
							<th>sku_sum</th>
							<th>product_key</th>
							<th>product_num</th>
							<th>order_ids</th>
						</tr>
						<c:forEach items="${sdList3}" varStatus="i" var="sd">
							<tr align="center" class="list3">
								<td><a
									href="../batchPick/detail?order_id=${sd.product_key}">${i.index}c</a></td>
								<td>${sd.goods_name}</td>
								<td>${sd.sku_sum}</td>
								<td>${sd.product_key}</td>
								<td>${sd.product_num}</td>
								<td>${sd.order_ids}</td>
							</tr>
						</c:forEach>

						<c:forEach items="${sdList2}" varStatus="i" var="sd">
							<tr align="center" class="list2">
								<td><a
									href="../batchPick/detail?order_id=${sd.product_key}">${i.index}b</a></td>
								<td>${sd.goods_name}</td>
								<td>${sd.sku_sum}</td>
								<td>${sd.product_key}</td>
								<td>${sd.product_num}</td>
								<td>${sd.order_ids}</td>
							</tr>
						</c:forEach>

						<c:forEach items="${sdList1}" varStatus="i" var="sd">
							<tr align="center" class="list1">
								<td><a
									href="../batchPick/detail?order_id=${sd.product_key}">${i.index}a</a></td>
								<td>${sd.goods_name}</td>
								<td>${sd.sku_sum}</td>
								<td>${sd.product_key}</td>
								<td>${sd.product_num}</td>
								<td>${sd.order_ids}</td>
							</tr>
						</c:forEach>
						</form>
						</div>
			</div>
	</fieldset>

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
</body>
</html>