<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
	<title>测试获取中通大头笔信息</title>
	<meta http-equiv="Content-Type" content="text/html;s charset=utf-8" />
	<base href="<%=basePath%>">
</head>

<body>

	<div align="center" >
		<h1>显示即完成，具体见数据库wms.shipping_zto_mark</h1>
	</div>

</body>
</html>
