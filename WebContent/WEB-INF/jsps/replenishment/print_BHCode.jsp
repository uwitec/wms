<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<title>打印补货标签</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/static/css/global.css">
<style type="text/css">
/* .order_liTab {
	float: left;
	margin-bottom: -1px;
	margin-top: 20px;
}

.order_liTab li {
	width: 110px;
	height: 21px;
	padding-top: 11px;
	background: url(images/order_liBg_1.gif) 0 5px no-repeat;
	float: left;
	text-align: center;
	margin-left: -1px;
	cursor: pointer;
	list-style: none;
}

.order_liTab li.on {
	background: url(images/order_liOn.png) no-repeat;
	font-weight: bold;
	font-size: 14px;
	margin-bottom: -1px;
	position: relative;
	cursor: default;
	color: #000;
}

.ddan {
	width: 900px;
	float: left;
}
 */
#print {
	width: 0px;
	height: 0px;
	border: 0px;
}

</style>
</head>

<body>
	<div class="main-container">
		<iframe id="print" src=""></iframe>
		<div class="main-div" id="barcode_grouding">
			<form method="post" class="lq-form-inline">
				<div class="lq-form-group">
					<label for="barcode_tray">数量:</label> 
					<input type="text" id="barcode_tray" name="barcode_tray" value="5"
						onkeyup="value=value.replace(/[^\d]/g,'')"
						onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))"
						class="lq-form-control"> 
					<input type='hidden' id='GN_TRAY_LIST' name="GN_TRAY_LIST" value="">
					<input type='button' id="IprintGNs_tray" value='打印' class="lq-btn lq-btn-primary lq-btn-sm">
				</div>
					
			</form>
		</div>
	</div>
	<iframe name="print_frame" width="0" height="0" frameborder="0" src="about:blank"></iframe>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript" src="../static/js/sinri_print_iframe.js"></script>
	<script type="text/javascript">
		// 打印条码
		$(document).ready(function() {
			$('#IprintGNs_tray').click(function() {
				$barcode_tray = $.trim($('#barcode_tray').val());//打印数量
				if (!check_input_number($barcode_tray)) {//检查数量是否为数字
					alert("输入内容不是数字！");
					return false;
				}
				$grouding_barcodes = get_tray_barcodes($barcode_tray); //获取所有的条码编号
				if ($grouding_barcodes == ''){
					alert("获取补货标签条码失败！");
					return false;
				}
				src = "../replenishment/print?code=" + $grouding_barcodes;
				$('#print').attr('src', src); 
			})
		});
		
		function check_input_number(number) {
			if (isNaN(number.trim())) {
				alert("请输入数字:" + number);
				return false;
			}

			if (number.trim() == '') {
				alert("请输入一个数字:" + number);
				return false;
			}
			return true;
		}
		
		function get_tray_barcodes(number) {
			var result = "";
			$.ajax({
				async : false,
				dataType : 'json',
				type : 'post',
				url : 'getBHBarcodes?',
				data : 'number=' + number,
				error : function() {
				},
				success : function(data) {
					if (!data.success) {
						alert(data.error);
					} else {
						result = data.result;
					}
				}
			});
			return result;
		}
		
	</script>
</body>
</html>
