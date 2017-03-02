<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>打印码托码</title>
<link href="<%=request.getContextPath() %>/static/css/default.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/autocomplete.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="../static/js/jquery/jquery.js"></script>
<script type="text/javascript" src="../static/js/jquery/jquery.ajaxQueue.js"></script>

<style type="text/css">
h2 {
  text-align: center;
}
div#all {
  margin: 20px;
}
div#left {
  width: 60%;
  float: left;
}
div#right {
  position: relative;
}
</style>
</head>
<body onLoad="window.print()">
		<div style="clear:both;">
		<c:forEach items="${codeList}" varStatus="i" var="code">
			<table class="bWindow" style="margin-top:10px;" id="batchTable">
					<tr><td>
						<img src="../common/barcode/generate?barcode=${code}">
					</td></tr>
					
			</table>
		<c:if test="${fn:length(codeList) != (i.index+1)}">
			<div STYLE="page-break-after: always;">
			</div>
		</c:if>
		</c:forEach>
		</div>
</body>
</html>