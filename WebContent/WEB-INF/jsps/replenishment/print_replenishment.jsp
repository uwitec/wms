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
	<meta charset="utf-8">
	<title></title>
	<style>
	body,html {
		width: 100%;
		padding: 0;
		margin: 0;
		padding-top: 3pt;
		font-size: 12pt;
	}
	li {
		list-style: none;
	}
	.printReplenishmentTaskList,.header,.thead {
		position: relative;
		overflow: hidden;
		padding: 0;
		margin: 0 0 0 0;
		/*border:1px solid black;*/
	}
	.thead, .oneReplishment {
		display: table;
		border:1px solid black;
		border-width: 1px 0 0 1px;
	}
	.thead span,.oneReplishment span {
		display: table-cell;
		padding:10px 0;
		border:1px solid black;
		text-align: center;
		word-break: break-all;
		line-height:1.4
	}
	.thead span {
		border-width: 0 1px 0 0;
		font-size: 10pt;
		font-weight: bolder;
	}
	.oneReplishment span {
			font-weight: lighter;
			font-size: 8pt;
	}
	.oneReplishment span {
		border-width: 0 1px 1px 0;
	}
	.page {
		position: absolute;
		right: 0;
		top: -20px;
	}
	.time {
		position: absolute;
		right: 0;
		top: 0px;
	}
	.user {
		position: absolute;
		right: 0;
		top: 20px;
	}
	.bhlx {
		position: absolute;
		right: 0;
		top: 40px;
	}
    </style>
</head>
<body onLoad="window.print()">
	<ul class="printReplenishmentTaskList">
		<c:set var="current" value="1"></c:set>
		<c:forEach items="${printReplenishmentTaskList}" varStatus="i" var="replenishment">
		<c:set var="max" value="6"></c:set>
		<c:if test="${(i.index + 1) % max == 1}">
			<div class="header">
				<img src="../common/barcode/generate?barcode=${bhCode}">
				<p class="page"><span class="label">页码:</span><span>${current}</span> / <span><fmt:formatNumber type="number" pattern="0" maxFractionDigits="0" value="${fn:length(printReplenishmentTaskList) / max + 1}"/></span></p>
				<p class="time"><span class="label">打印时间:</span><span>${printTime}</span></p>
				<p class="user"><span class="label">操作人:</span><span>${actionUser}</span></p>
				<p class="bhlx"><span class="label">补货类型:</span><span>${replenishment.task_level}</span></p>
			</div>
			<ul class="thead">
				<span style="width:23pt">编号</span>
				<span style="width:30pt">货主</span>
				<span style="width:48pt">商品条码</span>
				<span style="width:120pt">商品名称</span>
				<span style="width:30pt;">渠道</span>
				<span style="width:60pt">商品批次号</span>
				<span style="width:60pt">生产日期</span>
				<span style="width:37pt">理论补货数</span>
				<span style="width:37pt">实际补货数</span>
				<span style="width:50pt">源库位</span>
				<span style="width:50pt">目标库位</span>
			</ul>
		</c:if>
		<li class="oneReplishment">
			<span style="width:23pt">${i.index+1}</span>
			<span style="width:30pt">${replenishment.name}</span>
			<span style="width:48pt">${replenishment.barcode}</span>
			<span style="width:120pt; font-size: 6pt;text-align:left;">${replenishment.product_name}</span>
			<span style="width:30pt;">${replenishment.warehouse_name}</span>
			<span style="width:60pt">${replenishment.batch_sn}</span>
			<span style="width:60pt" class="timeFormate">${replenishment.validity}</span>
			<span style="width:37pt">${replenishment.quantity}</span>
			<span style="width:37pt"></span>
			<span class="loc" style="width:50pt;font-size: 6pt;">${replenishment.location_barcode}</span>
			<span style="width: 50pt;"></span>
		</li>
		<c:if test="${(i.index + 1) % max == 0}">
		    <c:set var="current" value="${current+1}"></c:set>
			<c:if test="${fn:length(printReplenishmentTaskList) != i.index+1}">
				<div style="page-break-after: always;"></div>
			</c:if>
		</c:if>
	</c:forEach>
	</ul>
	<script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript">
		$(function () {
			function insert_flg(str){
				if (str && str != "") {
					var newstr="";
				    var before = str.substring(0,3), after = str.substring(3,7);
				    newstr = before + "-" + after;
				    str = newstr;
				    var before = str.substring(0,6), after = str.substring(6,8);
				    newstr = before + "-" + after;
				    str = newstr;
				} else {
					newstr = "";
				}
			    return newstr;
			}

			$(".loc").each(function(){
				$(this).text(insert_flg($(this).text()));
			});
			
			$(".timeFormate").each(function(){
				$(this).text($(this).text().substring(0,10))
			})
		});
	</script>
</body>
</html>