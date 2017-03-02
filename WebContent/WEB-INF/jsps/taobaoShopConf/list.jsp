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
	<title>淘宝店铺管理</title>
	<meta http-equiv="Content-Type" content="text/html;s charset=utf-8" />
	<base href="<%=basePath%>">
</head>

<body>

	<div align="center" >
		<h1>Who want show this? ${username}</h1>
		<h2>${msg}</h2>
	</div>
	<div align="center" >
		<table>
	
			<tr >
				<th >店铺名称</th>
				<th >支付方式</th>
				<th >店铺类型</th>
				<th >applicationKey</th>
				<th >是否更新库存</th>
				<th >操作</th>
			</tr>
			<c:forEach items="${taobaoShopConfList}" varStatus="i" var="taobaoShopConf">
				<tr >
					<td>${taobaoShopConf.nick}</td>
					<td>${taobaoShopConf.pay_name}(${taobaoShopConf.pay_id})</td>
					<td>${taobaoShopConf.shop_type}</td>
					<td>${taobaoShopConf.application_key}</td>
					<td>
						<c:if test="${taobaoShopConf.is_stock_update == 'N'}">
							否
						</c:if>
						<c:if test="${taobaoShopConf.is_stock_update == 'Y'}">
							是
						</c:if>
					</td>
					<td >
						<a href="www.baidu.com" >编辑</a>
						<a href="${pageContext.request.contextPath}/taobaoShopConf/delete/${taobaoShopConf.taobao_shop_conf_id}" onclick="return confirm('你确定要删除该条记录吗？')" >删除</a>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>

</body>
</html>
