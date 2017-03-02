<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
<link href="<%=request.getContextPath() %>/static/css/default.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/autocomplete.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="../static/js/jquery/jquery.js"></script>
<style>
	h2 {
		text-align: center;
	}
	div#all {
		margin: 20px;
	}
	div#left {
		margin-left:34px; 
		float:left;
		height: auto;
		overflow:hidden;
	}
	div#right {
		margin-right:34px;
		float: right;
	}
	#info {

	}
	#info li {
		display:inline-block;
		list-style: none;
		padding: 5px;
	}
	#form {
		margin: 0 44px;
	}
	table {
		width: 100%;
	}
	table td {
		text-align: center;
		padding: 5px;
	}
	.remark{
		margin-top: 10pt;
		font-size: 14pt;
	}
</style>
</head>
<body onLoad="window.print()">
<c:forEach items="${printPrepackGoodsList}" varStatus="i" var="map">
	<c:set value="1" var="page"/>
 	<div style="clear:both;" id="all">
 	
 		<!--打印单名称 -->
		<c:if test="${type=='BOX_CLOSED'}">
			<h2>${map.name}套餐全打包任务单</h2>
		</c:if>
		<c:if test="${type=='BOX_OPEN'}">
			<h2>${map.name}套餐半打包任务单</h2>
		</c:if>
		<c:if test="${type=='UNPACK' }">
			<h2>${map.name}套餐拆分任务单</h2>
		</c:if>
		<!-- 打印单名称 -->
		
		<div style="clear:both;">
		
		<!--条码、页码、打印时间、打印者  -->
			<div id="left">
				<img src="../common/barcode/generate?barcode=${map.prePackCode}" alt="任务单号">
			</div>
			<div id="right">
				<p>
					<span>页码:</span>
					<span>${page}/<fmt:formatNumber type="number" value="${fn:length(map.printGoodsList) / 10 + 0.49}" pattern="#,###,###,###"></fmt:formatNumber></span>
					
				</p>
				<p>
					<span>打印员:</span>
					<span>${print_user}</span>
				</p>
				<p>
					<span>打印时间:</span>
					<span>${print_time}</span>
				</p>
			</div>
		<!--条码、页码、打印时间、打印者    -->
		
		<!--表格上方的信息显示  -->
			<div style="clear:both">
				<ul id="info">
					<li>套餐条码: ${map.tc_barcode}</li>
					<li>套餐名称: ${map.tc_name}</li>
					<c:if test="${type=='BOX_CLOSED'}">
						<li>全打包数量: ${map.qty_need}</li>
					</c:if>
					<c:if test="${type=='BOX_OPEN'}">
						<li>半打包数量: ${map.qty_need}</li>
					</c:if>
					<c:if test="${type=='UNPACK'}">
						<li>需拆分数量: ${map.qty_need}</li>
					</c:if>
					<li style="font-weight:bold;">耗材条码: ${map.hc_barcode}</li>
					<li style="font-weight:bold;">耗材名称: ${map.hc_name}</li>
					<li>组合方式: ${map.contain_way}</li>
				</ul>
			</div> 
		<!--表格上方的信息显示  -->
		
			<table border="1" cellspacing="0" style="margin-top:10px;" class="batchTable">
				<thead>
					<th>库位</th>
					<th>渠道</th>
					<c:if test="${type=='BOX_CLOSED' }">
						<th>组件商品条码</th>
						<th>组件商品名称</th>
					</c:if>
					<c:if test="${type=='BOX_OPEN' }">
						<th>组件商品条码</th>
						<th>组件商品名称</th>
					</c:if>
					<c:if test="${type=='UNPACK'}">
						<th>套餐条码</th>
						<th>套餐名称</th>
					</c:if>
					<th>数量</th>
				</thead>
				<tbody>
				<c:forEach items="${map.printGoodsList}" varStatus="j" var="good">
					<tr>
						<td class="locationBar">${good.location_barcode}</td>
						<td>${good.warehouse_name}</td>
						<td>${good.barcode2 }</td>
						<td>${good.product_name2 }</td>
						<td>${good.quantity }</td>
					</tr>
					<c:if test="${(j.index+1)%10==0 &&(j.index+1)!=fn:length(map.printGoodsList) }">
						</tbody>
						</table>
						<div style="page-break-after:always;"></div>
				 		 <c:set value="${page+1}" var="page"/>
				 			<div id="left">
								<img src="../common/barcode/generate?barcode=${map.prePackCode}" alt="任务单号">
							</div>
							<div id="right">
								<p>
									<span>页码:</span>
									<span>${page}/<fmt:formatNumber type="number" value="${fn:length(map.printGoodsList) / 10 + 0.49}" pattern="#,###,###,###"></fmt:formatNumber></span>
								</p>
								<p>
									<span>打印员:</span>
									<span>${print_user}</span>
								</p>
								<p>
									<span>打印时间:</span>
									<span>${print_time}</span>
								</p>
							</div>
							<div style="clear:both">
								<ul id="info">
									<li>套餐条码: ${map.tc_barcode}</li>
									<li>套餐名称: ${map.tc_name}</li>
									<c:if test="${type=='BOX_CLOSED'}">
										<li>全打包数量: ${map.qty_need}</li>
									</c:if>
									<c:if test="${type=='BOX_OPEN'}">
										<li>半打包数量: ${map.qty_need}</li>
									</c:if>
									<c:if test="${type=='UNPACK'}">
										<li>需拆分数量: ${map.qty_need}</li>
									</c:if>
									<li>组合方式: ${map.contain_way}</li>
								</ul>
							</div>
						<table border="1" cellspacing="0" style="margin-top:10px;" class="batchTable">
							<thead>
								<th>库位</th>
								<th>渠道</th>
								<c:if test="${type=='BOX_CLOSED' }">
									<th>组件商品条码</th>
									<th>组件商品名称</th>
								</c:if>
								<c:if test="${type=='BOX_OPEN' }">
									<th>组件商品条码</th>
									<th>组件商品名称</th>
								</c:if>
								<c:if test="${type=='UNPACK'}">
									<th>套餐条码</th>
									<th>套餐名称</th>
								</c:if>
								<th>数量</th>
							</thead>
							<tbody>
					</c:if>
				</c:forEach>
				</tbody>
			</table>
			<c:if test="${map.note != '' }">
				<div class="remark">备注: ${map.note}</div>
			</c:if>
			
		</div>
	</div>
	<c:if test="${(i.index+1)<fn:length(printPrepackGoodsList) }">
		<div style="page-break-after:always;"></div>
	</c:if>
</c:forEach>
<script>
	$(".batchTable tbody").find("td.locationBar").each(function(){
		var newstr="";
		var str= $(this).text();
	    var before = str.substring(0,3), after = str.substring(3,7);
	    newstr = before + "-" + after;
	    str = newstr;
	    var before = str.substring(0,6), after = str.substring(6,8);
	    newstr = before + "-" + after;
	    str = newstr;
		$(this).text(str);
	})
</script>
</body>
</html>