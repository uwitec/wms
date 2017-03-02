<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>乐其WMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <script type="text/javascript" >
    	var _topWin = window;  
	    while (_topWin != _topWin.parent.window) {
	         _topWin = _topWin.parent.window;  
	    }  
	    if (window != _topWin)_topWin.document.location.href = "${pageContext.request.contextPath}/";  
    </script>
</head>
<body id="login-body">
	<div id="login-header">
		<img src="${pageContext.request.contextPath}/static/images/logo-shadow.png" alt="leqee" id="logo">
	</div>
	<form action="" method="post" id="form-login">
		<div class="form-header">
			<img src="${pageContext.request.contextPath}/static/images/wms-title.png" id="wms-title">
		</div>
		<div class="lq-alert lq-alert-danger login-error">
			<p><i class="fa fa-warning"></i><span class="lq-alert-content">${error}</span></p>
		</div>
		<div class="lq-form-group">
	    	<input type="text" placeholder="Username" errorTitle="用户名" autofocus="true" name="username" id="username" class="lq-form-control bg required" value="<shiro:principal/>">
	    	<i class="fa fa-user input-bkg"></i>
		</div>
		<div class="lq-form-group">
	    	<input type="password" placeholder="Password" errorTitle="密码" name="password" id="password" class="lq-form-control bg required">
	    	<i class="fa fa-lock input-bkg"></i>
		</div>
		<div class="lq-form-group">
			<select name="physical_warehouse_id" id="physical_warehouse_id"
				class="lq-form-control">
				<c:forEach items="${physicalWarehouseList}" var="warehouse">
					<option value="${warehouse.warehouse_id}">
						${warehouse.warehouse_name}
					</option>
				</c:forEach>
			</select>
		</div>
        <div class="lq-form-group">
        	<!--     自动登录：<input type="checkbox" name="rememberMe" value="true"><br/> -->
	        <input type="submit" class="lq-btn lq-btn-primary" id="btn-login" value="登录">
        </div>
	</form>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript">
	    $(document).ready(function(){
	    	var jspError = "${error}",
	    	    $loginError = $(".login-error");

	    	if (jspError) {
	    		$loginError.fadeIn(300);
	    	}

	    	$("#form-login").submit(function(){
	    		var $required = $(".required");
	    		if ($required.length > 0) {
	    			for (i=0;i<$required.length;i++) {
		    			if ($required.eq(i).val() == "") {
		    				$loginError.find(".lq-alert-content").text($required.eq(i).attr("errorTitle") + "不能为空").end().hide().fadeIn(300);
		    				return false;
		    			}
		    		}
	    		}
	    	});
	    });
	</script>
</body>
</html>