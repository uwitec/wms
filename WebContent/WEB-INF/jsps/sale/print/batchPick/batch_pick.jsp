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
<title>打印波次单</title>
   
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">

<style type="text/css">
	div#left {
	  position: relative;
	  top: 17pt;
	}
	div#right {
	  position: relative;
	}
    #body-sale-search .lq-form-inline label {
    	width: 5.4em;
    }
    #sale-list-table td {
    	padding: 5px 0;
    }
    .pageCurrent {
    	position: absolute;
    	top: 10pt;
    	right: 20pt;
 		font-size: 14pt;
    }
    .content td {
    	font-size: 8pt;
    }
</style>
</head>
<body onload="window.print()"><!-- onLoad="window.print()" -->
	<span>打印波次单</span>
	<c:if test="${result=='failure'}">
		<p style="margin: 10pt 0;font-size: 15px;">
			${note}
		</p>
	</c:if>
	
	<c:forEach items="${mapList}" varStatus="i" var="map">
		<c:set value="1" var="page"/>
		<div id="print" style="text-align: center;margin-top: 10px;position: relative;">
			<img src="../common/barcode/generate?barcode=${map.sn}">
			<div class="pageCurrent">
				当前第${page} 页,总共<fmt:formatNumber type="number" value="${fn:length(map.pickOrderGoods) / 10 + 0.49}" pattern="#,###,###,###"></fmt:formatNumber> 页
			</div>
		</div>
		<div id="header" style="margin-left: 10px;">
			<div id="left">
				<span>波次单号：</span>
				<span>${map.sn}</span>
			</div>
			
			<div id="right" style="text-align: right;margin-right: 10px;">
				<span>sku：</span>
				<span>${map.kindNum}</span>
				
				<span>商品总数：</span>
				<span>${map.allNum}</span>
				
				<span>订单总数：</span>
				<span>${map.order_num}</span>
			</div>
		</div>
		
			<c:if test="${map.batch_process_type == 'BATCH'}">
				<span>组合方式：</span>
				<span>${map.contain_way}</span>
			</c:if>
			<table class="lq-table" id="sale-list-table">
				<thead >
					<th style="width: 50pt;">库位</th>
					<th style="width: 70pt;">商品条码</th>
					<th style="width: 180pt;">商品名称</th>
					<th style="width: 50pt;">渠道</th>

					<th style="width: 70pt;">商品批次号</th>
					<th style="width: 60pt;">生产日期</th>								
					<th style="width: 35pt;">数量</th>
					<c:if test="${map.batch_process_type== 'BATCH'}">
						<th>箱容</th>
						<th>参考箱数</th>
					</c:if>
					<c:if test="${map.batch_process_type == '' || map.batch_process_type == 'NORMAL'}">
						<th style="width: 50pt;">备注</th>
					</c:if>
				</thead>
				<tbody>
				<c:forEach items="${map.pickOrderGoods}" varStatus="j" var="good">	
					<tr class="content">
						<td class="loc" sytle="font-size: 6pt;">${good.location_kw_barcode}</td>
						<td>${good.barcode}</td>
						<td style="font-size: 6pt;">${good.goods_name}</td>
						<td>${map.warehouseName}</td>
						<td>${good.batch_sn}</td>
						<td class="time" style="padding-right: 2.05rem;">${good.validity}</td>
						<td style=" text-align: center;">${good.goods_number}</td>
						<c:if test="${map.batch_process_type == 'BATCH'}">
							<td>${good.spec}</td>
							<td>
								<c:if test="${good.spec !='' and good.spec !=0 and good.spec != null }">
									<fmt:formatNumber type="number" pattern="0.00" maxFractionDigits="2" value="${good.goods_number/good.spec}" />
								</c:if>
							</td>
						</c:if>
						<c:if test="${map.batch_process_type == '' || map.batch_process_type == 'NORMAL'}">
							<td style=" text-align: center;">${good.note}</td>
						</c:if>
					</tr>
					<c:if test="${(j.index + 1)%10==0&&(j.index+1)!=fn:length(map.pickOrderGoods)}">
						</tbody>
						</table>
						<div style="page-break-after: always;">
						<c:set value="${page+1}" var="page"/>
						</div>
						<br/><br/>
							<div id="print" style="text-align: center;margin-top: 10px;position:relative;">
								<img src="../common/barcode/generate?barcode=${map.sn}">
								<div class="pageCurrent">
									当前第${page} 页,总共<fmt:formatNumber type="number" value="${fn:length(map.pickOrderGoods) / 10 + 0.49}" pattern="#,###,###,###"></fmt:formatNumber> 页
								</div>
							</div>
							<div id="header" style="margin-left: 10px;">
								<div id="left">
									<span>波次单号：</span>
									<span>${map.sn}</span>
								</div>
								<div id="right" style="text-align: right;margin-right: 10px;">
									<span>sku：</span>
									<span>${map.kindNum}</span>
									
									<span>商品总数：</span>
									<span>${map.allNum}</span>
									
									<span>订单总数：</span>
									<span>${map.order_num}</span>
								</div>
							</div>
							<table class="lq-table" id="sale-list-table">
								<thead >
									<th style="width: 50pt;">库位</th>
									<th style="width: 70pt;">商品条码</th>
									<th style="width: 180pt;">商品名称</th>
									<th style="width: 50pt;">渠道</th>
									<th style="width: 70pt;">商品批次号</th>
									<th style="width: 60pt;">生产日期</th>								
									<th style="width: 35pt;">数量</th>
									<c:if test="${map.batch_process_type== 'BATCH'}">
										<th>箱容</th>
										<th>参考箱数</th>
									</c:if>
									<c:if test="${map.batch_process_type == '' || map.batch_process_type == 'NORMAL'}">
										<th style="width: 50pt;">备注</th>
									</c:if>
								</thead>
							<tbody>
					</c:if>
				</c:forEach>
				</tbody>
			</table>
		<c:if test="${fn:length(mapList) != (i.index+1)}">
			  <div STYLE="page-break-after: always;">
				</div>
		</c:if>
	</c:forEach>
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
			$(".time").each(function(){
				$(this).text($(this).text().substring(0,10));
			})
		});
	</script>
</body>
</html>