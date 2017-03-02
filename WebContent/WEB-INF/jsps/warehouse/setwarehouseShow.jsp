<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<title>设置仓库每日最大订单量</title>
	<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style type="text/css">
	.newForm {
		margin: 10px 0 0 0;
	}
    </style>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript" src="../static/js/jquery/jquery.ajaxQueue.js"></script>
	<script type="text/javascript" src="../static/js/zapatec/utils/zapatec.js"></script>


<script type="text/javascript">
	//$("document").ready(function() {
	//	$("#loadsform").validate({
	//		rules : {
	//			loads : {
	//				required : true,
	//				digits : true,
	//				range : [ 1, 100000 ]
	//			}
	//		},
	//		messages : {
	//			password : {
	//				required : "请输入仓库每日处理最大订单量",
	//				digits : "仓库每日处理最大订单量只能输入数字",
	//				range : jQuery.validator.format("请输入一个介于 {0} 和 {1} 之间的值")
	//			}
	//		}
	//	});
	//});

	
	function tableConstructor(data) {
		if (data.length) {
			var tableHtml = '';
			for ( var key in data) {

				tableHtml += '<tr  align="center"><td>'
						+ data[key].warehouse_name
						+ '</a></td><td class="need2Hide">'
						+ data[key].physical_warehouse_id
						+ '</a></td><td>'
						+ data[key].loads
						+ '</td><td>'
						+ data[key].nums
						+ '</td><td>'
						+ '<button class="lq-btn lq-btn-sm lq-btn-primary">修改</button></td></tr>';

			}

			$("#warehouseLoadList_show tbody").html(tableHtml);
			$(".need2Hide").css("display","none");
			$("#warehouseLoadList_show").fadeIn(300);
		} else {
			$("#warehouseLoadList_show tbody").html("");
			$("#batchTable_showsd").fadeIn(300);
		}
	}
	//点击修改  打开修改的页面
	$(document).ready(function() {
		
		$(".need2Hide").css("display","none");
		
		$("div#second_div button").live("click",function(){
			var id=$(this).parent().parent().find("td:eq(1)").html();
			$("#physical_warehouse_id2").val(id);
			$("div#third_div").css("display","block");
			$("div#third_div div:eq(0)").css("display","none");
			
		});
		
		//$("div#second_div button").click(function(){
		//	var id=$(this).parent().parent().find("td:eq(1)").html();
		//	$("#physical_warehouse_id2").val(id);
	//	$("div#third_div").css("display","block");
		//	$("div#third_div tr:eq(0)").css("display","none");
			
		//});
		
		$("#submit").click(function(e) {
											e.preventDefault();
												var num = $("#loads").val();
												var reg = new RegExp("^[0-9]*$");
												if(!reg.test(num)){
											        alert("请输入数字!");
											        return ;
											    }
												if (num<0||num>100000) {

													alert("请输入0-100000的数字");
													 return ;
												} else {
													var data = {
															loads : $("#loads").val(),
															physical_warehouse_id : $(
																	"#physical_warehouse_id")
																	.val()
														};
														//console.log(saleSearchData);
														$.ajax({
																	url : "${pageContext.request.contextPath}/warehouse/setWarehouseLoad",
																	type : "post",
																	dataType : "json",
																	data : data,

																	success : function(data) {
																		
																		tableConstructor(data.warehouseLoadList);
																		alert(data.message);
																		
																	},
																	error : function() {
																		alert("setWarehouseLoad error");

																	}
																});

												}});
		
		
		                //增加
						
		                
		                
		                $("#submit2").click(function(e) {
											    e.preventDefault();
												var num = $("#loads2").val();
												var reg = new RegExp("^[0-9]*$");
												if(!reg.test(num)){
											        alert("请输入数字!");
											        return ;
											    }
												if (num<0||num>100000) {

													alert("请输入0-100000的数字");
													return ;
												} else {
													var data = {
															loads : $("#loads2").val(),
															physical_warehouse_id : $(
																	"#physical_warehouse_id2")
																	.val()
														};
														//console.log(saleSearchData);
														$.ajax({
																	url : "${pageContext.request.contextPath}/warehouse/setWarehouseLoad",
																	type : "post",
																	dataType : "json",
																	data : data,

																	success : function(data) {
																		
																		tableConstructor(data.warehouseLoadList);
																		alert(data.message);
																		
																	},
																	error : function() {
																		alert("setWarehouseLoad error");

																	}
																});

												}});
						
					});
</script>
</head>

<body>
	<div class="main-container">
		<div class="container" div="first_div">
			<form id="loadsform" method="post" class="lq-form-inline">
				<div class="lq-form-group">
					<label for="physical_warehouse_id">选择仓库:</label>
					<select name="physical_warehouse_id" id="physical_warehouse_id" class="lq-form-control">
						<c:forEach items="${warehouseList}" var="warehouse">
							<option name="physical_warehouse_id"
								<c:if test="${physical_warehouse_id==warehouse.physical_warehouse_id}">selected="true"</c:if>
								value="${warehouse.physical_warehouse_id}">${warehouse.warehouse_name}
							</option>
						</c:forEach>
					</select>
				</div>
				<div class="lq-form-group">
					<label for="loads">最大处理订单量:</label>
					<input id="loads" type="text" name="loads" class="lq-form-control" />
				</div>
				<button id="submit" class="lq-btn lq-btn-sm lq-btn-primary">设置</button>
			</form>
		</div>
		<div class="container" id="second_div">
			<table id="warehouseLoadList_show" class="lq-table">
			    <thead>
					<tr>
					    <th>物理仓库名</th>
						<th class="need2Hide">物理仓库号</th>
						<th>日单量</th>
						<th>当前单量</th>
						<th>操作</th>
					</tr>
				</thead>
                <tbody>
                	<c:forEach items="${warehouseLoadList}" varStatus="i" var="warehouseLoad">
						<tr align="center">
							<td>${warehouseLoad.warehouse_name}</td>
							<td class="need2Hide">${warehouseLoad.physical_warehouse_id}</td>
							<td>${warehouseLoad.loads}</td>
							<td>${warehouseLoad.nums}</td>
							<td><button class="lq-btn lq-btn-sm lq-btn-primary">修改</button></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	
		<div class="container" id="third_div" style="display:none">
			<form class="lq-form-inline newForm">
				<div class="lq-form-group">
					<label for="physical_warehouse_id2">仓库号:</label>
					<input id="physical_warehouse_id2" type="text" name="physical_warehouse_id2" class="lq-form-control">
				</div>
				<div class="lq-form-group">
					<label for="loads2">最大处理订单量:</label>
					<input id="loads2" type="text" name="loads2" class="lq-form-control" />
				</div>
				<button id="submit2" class="lq-btn lq-btn-sm lq-btn-primary">提交修改</button>
			</form>
		</div>
	</div>
</body>
</html>