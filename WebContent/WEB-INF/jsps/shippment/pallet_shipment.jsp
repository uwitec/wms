<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>发货交接</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style type="text/css">
		#alert_msg {
			text-align: center;
	        width: 300px;
	        height: 150px;
	        position: absolute;
	        z-index: 10;
	        top: 22%;
	        left: 50%;
	        margin-left: -150px;
	        background-color: white;
	        border-radius: 10px;
	        box-shadow: 0 5px 15px rgba(0,0,0,.5);
		}
		#alert_msg p{
	        font-size: 20px;
	        font-weight: bold;
	        line-height: 150px;
	    }
	    #load_pallet_no {
	    	margin: 0 0 0 10px;
	    }
    </style> 
</head>
<body>
	<div class="main-container">
		<form action="deliveryPallet?" id="form" method ="get" class="lq-form-inline" >
			<div class="lq-form-group">
			<c:if test="${error != null}" >
				<div class="lq-alert lq-alert-danger">
					<i class="fa fa-exclamation-triangle"></i>
					<span class="lq-alert-content">${error},${message}</span>
				</div>
			</c:if>	
			</div>
			<div class="lq-form-group">
				<label for="pallet_no">扫描码托条码:</label>
				<input type="text" id="pallet_no" value="${pallet_no}" size="39" class="lq-form-control" />
				<input type="button" id="load_pallet_no" class="lq-btn lq-btn-primary lq-btn-sm" value="加载" />
				 <c:if test="${can_ship != null}" > 
					<input type="submit" class="lq-btn lq-btn-primary lq-btn-sm" name="submit" value="发货" id="deliever"/>
				 </c:if> 
			</div>
			<c:if test="${pallet_no != null}" >
				<div class="lq-form-group">
					<span>码托快递：</span>
					<span>${pallet_shipping_name}</span>
				</div>
				<div class="lq-form-group">
					<span>可发货运单数：</span>
					<span>${ok_num}</span>
				</div>
				<div class="lq-form-group">
					<span>运单号：</span>
					<span>${delivery_numbers}</span>
				</div>
			</c:if>
			<div class="lq-form-group">		
				<c:if test="${has_shipped != null}">
					<div id="alert_msg">
					    <p>该码托之前已被发货！</p>
					</div>
				</c:if>
				<input type="hidden" name="pallet_no" value="${pallet_no}" /> 
			</div>
			<c:if test="${shipped != null}" >
				<div id="alert_msg">
				    <p>交接成功，已发货！</p>
				</div>
			</c:if>
		</form>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript">
	//          
	// {literal}
	function closeAlert () {
		if ($('#alert_msg').css('display')) {
			setTimeout(function () {
				window.location.href = 'palletShipment';
			}, 3000);
		}
	}
	closeAlert();
	$(document).ready(function() {
		
		$("#form").submit(function(){
			$("#deliever").attr("disabled",true);
		})
		
		// 绑定滑动效果
		$('legend.expand').bind('click', function(event) {
			$(this).next().slideToggle('normal');
		});

		// 绑定加载码托编码
		$('#load_pallet_no').bind('click', load_pallet_no);
		$('#pallet_no').bind('keyup', listen_pallet_no).focus();

		$('#pallet_no').focus();
	});

	var KEY = {
		RETURN : 13, // 回车
		CTRL : 17, // CTRL
		TAB : 9
	};

	/**
	 * 码托编号监听
	 */
	function listen_pallet_no(event) {
		switch (event.keyCode) {
		case KEY.RETURN:
		case KEY.CTRL:
			load_pallet_no();
			event.preventDefault();
			break;
		}
		
	}
	/**
	 * 载入码托对应运单信息
	 */
	function load_pallet_no() {
		var pallet_no = $.trim($('#pallet_no').val());
		if (pallet_no == '') {
			alert('请先扫描码托编号');
			return;
		}

		location.href = "loadPallet?pallet_no=" + pallet_no;
	}

	// 检测输入的数字
	function check_number_format(number) {
		var reg = /(^\d+$)/;
		if (!number.match(reg)) {
			return false;
		} else {
			return true;
		}
	}

	// {/literal}
	</script>
</body>
</html>
