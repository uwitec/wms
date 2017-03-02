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
	<title>仓库发货单量</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style type="text/css">
	.newForm {
		margin: 10px 0 0 0;
	}
	#nums {
		margin: 0 0 0 2em;
	}
	#submit {
		position: relative;
		top: 10px;
		margin: 0 175px;
	}
    </style>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>


<script type="text/javascript">
	$(document).ready(function() {
		$("#submit").click(function(e) {
											e.preventDefault();
												var num = $("#loads").val();
												var reg = new RegExp("^[0-9]*$");
												if(!reg.test(num)){
											        alert("请输入数字!");
											        return ;
											    }
												if (num<0||num>1000000) {

													alert("请输入0-1000000的数字");
													return ;
												} else {
													var data = {
															loads : $("#loads").val(),
														};
														//console.log(saleSearchData);
														$.ajax({
																	url : "${pageContext.request.contextPath}/warehouse/setWarehouseLoad",
																	type : "post",
																	dataType : "json",
																	data : data,

																	success : function(data) {
																		
																		
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
					<label for="loads">最大处理订单量:</label>
					<input id="loads" type="text" name="loads" value="${loads}" class="lq-form-control" />
				</div>
				<div class="lq-form-group">
					<label for=nums>当前订单量:</label>
					<input id="nums" type="text" name="nums" class="lq-form-control" value="${nums}" readonly/>
				</div>
				<div class="lq-form-group">
					<button id="submit" class="lq-btn lq-btn-sm lq-btn-primary">设置</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>