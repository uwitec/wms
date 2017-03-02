<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>打印波次单</title>
</head>
<body onLoad="window.print()">
  <c:forEach items="${model}" var="m" varStatus="i">
  <div style="margin:10px;">
    ${m.shipping_name}
  	<img src="../common/barcode/generate?barcode=${m.mt_code}&text=0" style="display:block;width:227.6pt;height:113.8pt;">
  	${m.mt_code}
  </div>
  <c:if test="${fn:length(model) != (i.index+1)}">
      <div STYLE="page-break-after: always;">
      </div>
  </c:if>
  </c:forEach>
</body>
</html>