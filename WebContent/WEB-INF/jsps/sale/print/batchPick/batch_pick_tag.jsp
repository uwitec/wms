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
<title>打印波次标签</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">

<style type="text/css">
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
    #body-sale-search .lq-form-inline label {
    	width: 5.4em;
    }
</style>
</head>
<body onLoad="window.print()"><!-- onLoad="window.print()" -->
	

		<div style="clear:both;margin:10px;">
		
					<c:forEach items="${pickOrderGoods}" varStatus="i" var="good">
						
						<table class="lq-table" id="sale-list-table">
						<tr>
							<td colspan="2">
								<div id="print" style="text-align: center;margin-top: 10px;">
									<img src="../common/barcode/generate?barcode=${good.batchCode}">
								</div>
							</td>
						</tr>
						
						<tr>
							<td colspan="2">
								<div id="print" style="text-align: center;margin-top: 10px;">
									<img src="../common/barcode/generate?barcode=${good.barcode}">
								</div>
							</td>
						</tr>
						
						<tr>
							<td>商品名称：</td>
							<td>${good.goods_name}</td>
						</tr>
						
						<tr>
							<td>库位</td>
							<td class="loc">${good.location_kw_barcode}</td>
						</tr>
						
						<tr>
							<td>商品数量：</td>
							<td>${good.goods_number}</td>
						</tr>
						
						<!--  
							<c:if test="${kinds > 1}">
								<tr>
									<td>组合方式</td>
									<td></td>
								</tr>
								
								<tr>
									<td>组合数量</td>
									<td></td>
								</tr>
							</c:if>
						-->
						</table>
						<c:if test="${fn:length(pickOrderGoods) != (i.index+1)}">
							  <div STYLE="page-break-after: always;">
								</div>
						</c:if>
					</c:forEach>
			<br/>
		</div>
		<script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
		<script type="text/javascript">
			$(function () {
				function insert_flg(str){
				    var newstr="";
				    var before = str.substring(0,3), after = str.substring(3,7);
				    newstr = before + "-" + after;
				    str = newstr;
				    var before = str.substring(0,6), after = str.substring(6,8);
				    newstr = before + "-" + after;
				    str = newstr;
				    return newstr;
				}
				$(".loc").each(function(){
					$(this).text(insert_flg($(this).text()));
				});
			});
		</script>
</body>
</html>