<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Error</title>
 	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
<style>
input[type="text"]{
  	height: 30px;
}
body{
	  font-family: "微软雅黑";
}
td {
  width: 80px;
  height: 45px;
}
</style>
</head>
<body>

	<div style="clear:both;margin: 10px;">
		<div class="lq-panel lq-panel-default">
			<div class="lq-panel-heading">
				<i class="fa fa-tasks"></i>
				请检查业务组是否正常切换
			</div>
		</div>
	</div>
	
</body>
</html>