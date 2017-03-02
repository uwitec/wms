<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>紧急补货规则</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style type="text/css">
		.title {
			font-weight: bold;
		}
		input[type="checkbox"] {
			position: relative;
			top: 2px;
			display: inline-block;
		}
		.middle {
			margin: 20px 0;
		}
		.middle .select {
			display: inline-block;
			width: auto;
		}
		.btns {
			margin: 10px 50px;
		}
		.save {
			margin: 0 30px 0 0;
		}
		td p {
			display: inline-block;
			font-weight: bold;
			text-decoration: underline;
			cursor: pointer;
		}
		.update {
			color: blue;
			margin: 0 10px 0 0;
		}
		.delete {
			color: red;
		}
		td .tdSelect {
			display: inline-block;
			width: auto;
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
	<div class="main-container">
		<div class="header">
			<span class="title">货主:</span>
			<label for="null"><input type="checkbox" id="null" name="customerId" value="" checked="true" />不限</label>
			<c:forEach items="${warehouseCustomerList}" var="customer">
			<label for="${customer.customer_id}"><input type="checkbox" id="${customer.customer_id}" name="customerId" value="${customer.customer_id}" class="customerId" checked="true" />${customer.name}</label>
			</c:forEach>
		</div>
		<div class="middle">
			<span class="title">补货数量:</span>
			<select class="lq-form-control select">
				<option value="1">订单需求量</option>
				<option value="2">Max[订单需求量，最高存量]</option>
				<option value="3">订单需求量+最高存量</option>
			</select>
			<div class="btns">
				<button class="lq-btn lq-btn-sm lq-btn-primary save">保存</button>
				<button class="lq-btn lq-btn-sm lq-btn-primary cancel">取消</button>
			</div>
		</div>
		<div class="footer">
			<span class="title">紧急补货规则列表</span>
			<table class="lq-table">
				<thead>
					<th>货主</th>
					<th>补货数量</th>
					<th>设置时间</th>
					<th>操作人</th>
					<th>操作</th>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/alertPlugin.js"></script>
	<script type="text/javascript">
		$(function () {
			function search () {
				$.ajax({
					url: '../replenishmentQuery/configReplenishmentUrgentquery',
					type: 'post',
					dataType: 'json',
					data: {

					},
					success: function (res) {
						console.log(res);
						showTable(res);
					}
				});
			}
			search();
			function showTable (res) {
				var trHtml = '';
				var date = '';
				var time = '';
				for (var i in res.searchResultList) {
					date = new Date(res.searchResultList[i].created_time);
					time = date.toString().split(' ')[4];
					trHtml += '<tr><td class="tdCustomerId" value="'+res.searchResultList[i].customer_id+'">'+res.searchResultList[i].name+'</td><td><select class="lq-form-control tdSelect" disabled><option value="1">订单需求量</option><option value="2">Max[订单需求量，最高存量]</option><option value="3">订单需求量+最高存量</option></select></td><td>'+date.getFullYear()+'-'+(date.getMonth() + 1)+'-'+date.getDate()+' '+time+'</td><td>'+res.searchResultList[i].created_user+'</td><td><a class="update lq-btn lq-btn-sm lq-btn-primary">编辑</a><a class="delete lq-btn lq-btn-sm lq-btn-danger">撤销</a></td></tr>';
				}
				$('tbody').html(trHtml);
				for (var i in res.searchResultList) {
					$('.tdSelect').eq(i).val(res.searchResultList[i].replenishment_condition);
				}
			}
			$('.customerId').change(function () {
				$('#null').prop('checked', false);
			});
			$('#null').change(function () {
				$('.customerId').prop('checked', $(this).prop('checked'));
			});
			// 取消
			$('.cancel').click(function () {
				$('#null').prop('checked', true);
				$('.customerId').prop('checked', false);
				$('.select').val('1');
			});
			// 保存
			$('.save').click(function () {
				var checkedCustomerId = '';
				$(this).attr('disabled', true);
				$.each($('input[type="checkbox"]:checked'), function(index, val) {
					checkedCustomerId += $(val).val() + ',';
				});
				checkedCustomerId = checkedCustomerId.substring(0, checkedCustomerId.length - 1);
				checkedCustomerId = checkedCustomerId.substring(0, 1) == "," ? '' : checkedCustomerId
				console.log(checkedCustomerId);
				$.ajax({
					url: '../replenishmentQuery/addUrgentRule',
					type: 'post',
					dataType: 'json',
					data: {
						customer_id: checkedCustomerId,
						replenishment_condition: $('.select').val()
					},
					success: function (res) {
						console.log(res);
						if (res.result == 'success') {
							alert('保存成功');
							search();
						}
						$('.save').attr('disabled', false);
					}
				});
			});     
			// 删除
			var deleteTr = '';
			$(document).on('click', '.delete', function(event) {
				 deleteTr = $(event.target).parents('tr');
				console.log(deleteTr.find('.tdCustomerId').attr('value'))
				$('#alert').show();
				$('#alert').AlertPlugin({
					width: 360,
					height: 220,
					htmlText: '<div style="font-size:20px">确定要删除吗？</div>',
					btnCount: 2
				});
			});
			$(document).on('click', '.trueBtn', function () {
				$.ajax({
					url: '../replenishmentQuery/deleteUrgentRule',
					type: 'post',
					dataType: 'json',
					data: {
						'customer_id': deleteTr.find('.tdCustomerId').attr('value')
					},
					success: function (res) {
						console.log(res);
						$('#alert').hide();
						alert(res.note);
						if (res.result == 'success') {
							deleteTr.remove();
						}
					}
				});
			});
			$(document).on('click', '.falseBtn', function () {
				$('#alert').hide();
			});
			// 编辑
			var isUpdate = false;
			$(document).on('click', '.update', function () {
				var thisIndex = $(this).parents('tr').index();
				if (isUpdate) {
					console.log($('.tdCustomerId').eq(thisIndex).attr('value'), $('.tdSelect').eq(thisIndex).val())
					$.ajax({
						url: '../replenishmentQuery/addUrgentRule',
						type: 'post',
						dataType: 'json',
						data: {
							customer_id: $('.tdCustomerId').eq(thisIndex).attr('value'),
							replenishment_condition: $('.tdSelect').eq(thisIndex).val()
						},
						success: function (res) {
							console.log(res);
							if (res.result == 'success') {
								alert('保存成功');
								search();
							}
						}
					});
					$('.tdSelect').eq(thisIndex).attr('disabled', true);
					$(this).html('编辑');
				} else {
					$('.tdSelect').eq(thisIndex).attr('disabled', false);
					$(this).html('保存');
				}
				isUpdate = !isUpdate;
			});
		});
	</script>
</body>
</html>