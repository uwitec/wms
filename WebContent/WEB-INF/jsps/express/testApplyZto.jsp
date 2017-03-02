<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
	<title>测试获取面单号</title>
	<meta http-equiv="Content-Type" content="text/html;s charset=utf-8" />
	<base href="<%=basePath%>">
</head>

<body>
	<%
		String message = (String) request.getAttribute("message");
		if (message != "") {
	%>
	<div id="message" style="border: #7F9F00 2px solid; padding: 5px; text-align: center;">
		${message}</div>
	<%
		}
	%>

	<div align="center" >
		<h1>显示即完成，具体见数据库select count(*) from wms.shipping_tracking_number_repository</h1>
	</div>
</body>
</html>
