<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.io.*,java.util.*" %>
<%@ page import="javax.servlet.*,java.text.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>打印盘点任务单</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style type="text/css">
    	.header {
    		margin: 0 0 20pt 0;
    	}
    	.title {
    		text-align: center;
    		font-size: 30px;
    		font-weight: bold;
    	}
    	.batchTaskImg {
    		width: 150pt;
    	}
    	.headRight {
    		margin: 0 0 0 80pt;
    	}
    	.thead {
    		display: table;
    		font-size: 0;
    	}
    	.thead li {
    		display: table-cell;
    		border: 1px solid #000;
    		margin: 0 0 0 -1px;
    		text-align: center;
    		font-size: 12px;
    		border-width: 1px 1px 1px 0px;
    	}
    	.thead li:first-child {
    		border-width: 1px 1px 1px 1px;
    	}
    	.tbody li {
    		display: table;
    		font-size: 0;
    		/*border-width: 1px 0px 0px 1px;*/
    	}
    	.tbody span {
    		display: table-cell;
    		border: 1px solid #000;
    		/*margin: -1px 0 0 -1px;*/
    		border-width: 0px 1px 1px 0px;
    		text-align: center;
    		font-size: 12px;
    	}
    	.tbody span:first-child {
    		border-width: 0px 1px 1px 1px;
    	}
    	.goodsName {
    		/*-webkit-text-size-adjust:none;
			font-size: 8px;*/
			transform: scale(0.8);
    	}
    	.sign {
    		position: relative;
    		top: 20pt;
    		right: 100pt;
    		width: 100%;
    		text-align: right;
    	}
    	.sign1 {
    		position: relative;
    		left: -350pt;
    	}
    </style>
</head>
<body onLoad="window.print()">
	<div>
		<c:set var="current" value="1"></c:set>
		<c:forEach items="${batchStockTaskList}" varStatus="i" var="batchStockTask">
			<div class="header lq-row">
				<p class="title">${batchStockTask.taskType}任务单</p>
				<div class="headLeft lq-col-5">
					<img class="batchTaskImg" src="../common/barcode/generate?barcode=${batchStockTask.batchTaskSn}">
				</div>
				<div class="headRight lq-col-5">
					<div class="lq-form-gronp">
						<label>页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码:</label>
						<span>${current}/${fn:length(batchStockTaskList)}</span>
					</div>
					<div class="lq-form-gronp">
						<label>任务总数:</label>
						<span>${batchStockTask.taskNum}</span>
					</div>
					<div class="lq-form-gronp">
						<label>盘点时间:</label>
						<span><%
								Date dNow = new Date( );
							    SimpleDateFormat ft = 
							    new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
							    out.print(ft.format(dNow));
							  %>
						</span>
					</div>
				</div>
			</div>
			<div class="table">
				<ul class="thead">
					<li style="width: 35pt;">盘点任务ID</li>
					<li style="width: 47pt;">盘点库位</li>
					<li style="width: 35pt;">货主</li>
					<li style="width: 40pt;">渠道</li>
					<li style="width: 73pt;">商品条码</li>
					<li style="width: 185pt;">商品名称</li>
					<li style="width: 40pt;">生产日期</li>
					<li style="width: 50pt;">批次号</li>
					<li style="width: 35pt;">${batchStockTask.mark}数量</li>
				</ul>
				<ul class="tbody">
					<c:forEach items="${batchStockTask.taskList}" varStatus="i" var="task">
						<li>
							<span style="width: 35pt;">${task.task_id}</span>
							<span class="loc" style="width: 47pt;">${task.location_barcode}</span>
							<span style="width: 35pt;">${task.name}</span>
							<span style="width: 40pt;">${task.warehouse_name}</span>
							<span style="width: 73pt;">${task.barcode}</span>
							<span style="width: 185pt;">${task.product_name}</span>
							<span style="width: 40pt;">
								<c:if test="${task.hide_batch_sn == '0'}">/</c:if>
								<c:if test="${task.hide_batch_sn == '1'}">${task.validity.substring(0,10)}</c:if>
							</span>
							<span style="width: 50pt;">${task.batch_sn}</span>
							<span style="width: 35pt;"></span>
						</li>
					</c:forEach>
				</ul>
				<div class="sign">
					<span class="sign1">盘点人：</span>
					<span class="sign2">监盘人：</span>
				</div>
			</div>
			<c:set var="current" value="${current+1}"></c:set>
			<div style="page-break-after: always;"></div>
		</c:forEach>
	</div>
	<script type="text/javascript" src="../static/js/jquery.min.js"></script>
    <script type="text/javascript" > 
        // 库位转换
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
    </script>
</body>
</html>