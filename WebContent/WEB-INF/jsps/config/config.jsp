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
	<title>配置告警邮件</title>
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


</head>

<body>
	<div class="main-container">
		<div class="container" div="first_div">
			<form id="loadsform" method="post" class="lq-form-inline">
			    <div class="lq-form-group">
							<label for="apply_physical_type">　　告警范围:</label>
							<select name="apply_physical_type" id="apply_physical_type" class="lq-form-control">
								<option value="0">当前仓库</option>
								<option value="1">全仓库</option>
							</select>
				</div>
				<!-- 
				<div class="lq-form-group">
							<label for="apply_type">　　告警类型:</label>
							<select name="apply_type" id="apply_type" class="lq-form-control">
								<option value="INVENTORY_NOT_ENOUGH">耗材不足告警</option>
								<option value="QUANTITYEXCEPTIONLOCATION">拣货区</option>
								<option value="PRODUCTSPECNULL">未维护箱规告警</option>
								<option value="PRODUCTEXCEPTIONLOCATION">库位库存异常告警</option>
								<option value="THERMAL_MAILNOS_NOT_ENOUGH">面单号不足告警</option>
							</select>
				</div>
				 -->
				<div class="lq-form-group">
							<label for="apply_type">　　货主:</label>
							<select name="apply_type" id="apply_type" class="lq-form-control">
								<c:forEach items="${configmails}" var="configmail">
									<option name="type"
										class="${configmail.type}"
										<c:if test="${type==configmail.type}">selected="true"</c:if>
										value="${configmail.type}">${configmail.name}</option>
								</c:forEach>
							</select>
				</div>
				<div class="lq-form-group">
							<label for="apply_customer_id">　　货主:</label>
							<select name="apply_customer_id" id="apply_customer_id" class="lq-form-control">
								<option value="0">不限</option> 
								<c:forEach items="${customers}" var="customer">
									<option name="customer_id"
										class="${customer.customer_id}"
										<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
										value="${customer.customer_id}">${customer.name}</option>
								</c:forEach>
							</select>
				</div>
				<div class="lq-form-group">
					<label for=mail>邮箱</label>
					<input id="mail" type="text" name="mail" class="lq-form-control" value="${mail}"/>
				</div>
				<div class="lq-form-group">
					<button id="submit" class="lq-btn lq-btn-sm lq-btn-primary">设置</button>
				</div>
			</form>
		</div>
	</div>
	
	
	<script type="text/javascript">
	$(document).ready(function() {
		$("#submit").click(function(e) {
											e.preventDefault();
												var mail = $("#mail").val();
												
												
													var data = {
															type : $("#apply_type").val(),
															customer_id : $("#apply_customer_id").val(),
															physical:$("#apply_physical_type").val(),
															mail:mail
															
														};
														//console.log(saleSearchData);
														$.ajax({
																	url : "${pageContext.request.contextPath}/config/setMail",
																	type : "post",
																	dataType : "json",
																	data : data,

																	success : function(data) {
																		
																		
																		alert(data.message);
																		
																	},
																	error : function() {
																		alert("提交失败");

																	}
																});

												});
												
					});
</script>
</body>
</html>