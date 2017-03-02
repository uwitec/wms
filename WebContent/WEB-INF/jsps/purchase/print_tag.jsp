<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ page import="java.text.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>打印标签</title>
<link href="<%=request.getContextPath() %>/static/css/default.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/autocomplete.css" rel="stylesheet" type="text/css">

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
		<c:forEach items="${tagList}" varStatus="i" var="tag">
		
			<img src="../common/barcode/generate?barcode=${tag.location_barcode}">
			<br/>
			<img src="../common/barcode/generate?barcode=${tag.barcode}">
			<br/><br/>
			<table class="bWindow" style="margin-top:10px;" id="batchTable">
					
					<tr>
						<td width="25%">商品名称：</td><td>${tag.goods_name}</td>
						<td width="25%">数量</td><td>
							<c:if test="${tag.normal_number != ''}">
								${tag.normal_number}
							</c:if>
							<c:if test="${tag.defective_number != ''}">
								${tag.defective_number}
							</c:if>
						</td>
					</tr>
					<tr>
						<td>生产日期：</td><td>${tag.validity}</td>
						<td>商品状态：</td><td>
							<c:if test="${tag.normal_number != ''}">
								良品
							</c:if>
							<c:if test="${tag.defective_number != ''}">
								不良品
							</c:if>
						</td>
					</tr>
					<tr>
						<td>打印时间：</td><td><%=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()) %></td>
						<td>验收员：</td><td>${tag.action_user}</td>
					</tr>
					<tr>
						<td>批次号:</td><td>${tag.batch_sn}</td>
						<td></td><td></td>
					</tr>
					<!-- 
					<tr>
						<td>到货时间：</td>
						<td  colspan="3">
							${tag.arrive_time}
						</td>
					</tr>
					 -->
			</table>
			<c:if test="${fn:length(tagList) != (i.index+1)}">
				  <div STYLE="page-break-after: always;">
					</div>
			</c:if>
		</c:forEach>
		</div>
</body>
</html>