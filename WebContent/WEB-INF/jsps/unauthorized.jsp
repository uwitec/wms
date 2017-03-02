<%@page import="java.util.Map"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true"%>
<%@page import="java.io.PrintStream"%> 
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>没有权限</title>
    <style>.error{color:red;}</style>
</head>
<body>

<div class="error">
	<% 
		String exceptionMessage = exception.getMessage();
		String lackedPermission = exceptionMessage.substring(exceptionMessage.indexOf("[")+1, exceptionMessage.indexOf("]"));
		Map<String,String> permissionResourceMap = (Map<String,String>)session.getAttribute("permissionResourceMap");
		String lackedPermissionName = permissionResourceMap.get(lackedPermission);
	%>
	对不起，您没有此权限: 【<%=lackedPermissionName %>】，请联系管理员<br/>
</div>
</body>
</html>