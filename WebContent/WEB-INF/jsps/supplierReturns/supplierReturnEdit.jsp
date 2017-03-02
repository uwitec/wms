<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>供应商退货</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style type="text/css">
		label {
			display: inline-block;
			width: 7em;
			text-align: right;
			vertical-align: top;
		}
		span {
			font-weight: bold;
		}
		.table {
			display: inline-block;
			width: auto;
		}
		.ipt {
			width: 100px;
		}
		.btns {
			position: relative;
			left: 293px;
		}
		.close {
			margin: 0 0 0 50px;
		}
	    .alertTip {
	    	display: none;
	    	position: absolute;
	    	top: 0;
	    	left: 0;
	    	right: 0;
	    	bottom: 0;
	    	margin: auto;
	    	width: 300px;
	    	height: 150px;
	    	background-color: #fff;
	    	z-index: 999;
	    	box-shadow: 0 0 20px #000;
	    	text-align: center;
	    	line-height: 150px;
	    	border-radius: 5px;
	    }
	</style>
</head>
<body>
 	<div class="alertTip">
 		<h1>供应商退货出库成功</h1>
 	</div>
	<div class="main-container">
		<div class="lq-form-group">
			<label>OMS订单号:</label>
			<span class="OMSOrderSN">${goodsReturnList[0].oms_order_sn}</span>
		</div>
		<div class="lq-form-group">
			<label>供应商:</label>
			<span class="supplier">${goodsReturnList[0].provider_name}</span>
		</div>
		<div class="lq-form-group">
			<label>货主:</label>
			<span class="customer">${goodsReturnList[0].customer_name}</span>
		</div>
		<div class="lq-form-group">
			<label>退货仓库:</label>
			<span class="RGWarehouse">${goodsReturnList[0].warehouse_name}</span>
		</div>
		<div class="lq-form-group">
			<label>退货商品:</label>
			<table class="lq-table table">
				<thead>
					<tr>
						<th>库位条码</th>
						<th>申请出库数</th>
						<th>商品条码</th>
						<th>商品名称</th>
						<th>生产日期</th>
						<th>库存状态</th>
					</tr>
				</thead>
				<tbody data-order="${goodsReturnList[0].order_id}">
					<c:forEach items="${goodsReturnList[0].goodsReturnPrintInfo}" var="goodsReturn">
						<tr data-task="${goodsReturn.task_id}">
							<td class="loc">${goodsReturn.location_barcode}</td>
							<td class="quantity" data-quantity="${goodsReturn.quantity}"><input type="text" class="lq-form-control ipt" value="${goodsReturn.quantity}" /></td>
							<td>${goodsReturn.barcode}</td>
							<td>${goodsReturn.product_name}</td>
							<td>${goodsReturn.validity}</td>
							<td>${goodsReturn.goods_status}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<div class="btns">
			<button class="lq-btn lq-btn-sm lq-btn-primary RGOut">退货出库</button>
			<button class="lq-btn lq-btn-sm lq-btn-primary close">取消</button>
		</div>
	</div>
</body>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript">
		$(function () {
			$('.RGOut').click(function () {
				$(this).attr("disabled","disabled");
				var task_out_qty = '',
					isRGOut = false;
					sum = 0;
				$.each($('tbody tr'), function(index, val) {
					task_out_qty += $(val).data('task') + '_' + $(val).find('.quantity').data('quantity') + '_' + $(val).find('.ipt').val() + ',';
					sum += Number($(val).find('.ipt').val());
					if ($(val).find('.ipt').val() >= 0 && $(val).find('.ipt').val() <= $(val).find('.quantity').data('quantity')) {
						isRGOut = true;
					} else {
						alert('申请退货数必须在0~' + $(val).find('.quantity').data('quantity'));
						isRGOut = false;
					}
				}); 
				if (sum == 0) {
					alert('出库数量总和不可为0');
					isRGOut = false;
				}
				task_out_qty = task_out_qty.substring(0, task_out_qty.length-1);
				console.log($('tbody').data('order'));
				console.log(task_out_qty);
				if (isRGOut) {
					$.ajax({
						url: '../supplierReturn/supplierReturnOut',
						type: 'post',
						dataType: 'json',
						data: {
							order_id: $('tbody').data('order'),
							task_out_qty: task_out_qty
						},
						success: function (res) {
							console.log(res);
							if (res.result == 'success') { 
								$('.alertTip').fadeIn(300);
								setTimeout(function () {
									$('.alertTip').fadeOut(300);
									location.href = '../supplierReturn/goodsReturn';
								}, 3000);
								$(this).removeAttr("disabled");
							} else {
								alert(res.note);
							}
						}
					});
				}
			});
			$('.close').click(function () {
				$(this).attr("disabled","disabled");
				location.href = '../supplierReturn/goodsReturn';
			});
			// 库位转换
	        function insert_flg(str){
	            if (str && str != "") {
	                var newstr="";
	                var before = str.substring(0,3), after = str.substring(3,7);
	                newstr = before + "-" + after;
	                str = newstr;
	                var before = str.substring(0,6), after = str.substring(6,8);
	                newstr = before + "-" + after;
	                str = newstr;
	            } else {
	                newstr = "";
	            }
	            return newstr;
	        }
	        $(".loc").each(function(){
				$(this).text(insert_flg($(this).text()));
			});
		});
	</script>
</html>