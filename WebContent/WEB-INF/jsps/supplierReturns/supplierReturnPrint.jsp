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
	<title>打印供应商退货出库单</title>
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
    		width: 100%;
    	}
    	.thead li {
    		display: table-cell;
    		border: 1px solid #000;
    		text-align: center;
    		font-size: 12px;
    	}
    	.tbody li {
    		display: table;
    		font-size: 0;
    		width : 100%;
    	}
    	.tbody span {
    		display: table-cell;
    		font-size: 10px;
    		-webkit-text-size-adjust:none;
    		border: 1px solid #000;
    		text-align: center;
    	}
    </style>
</head>
<body onLoad="window.print()" >
	<div>
			<div class="header lq-row">
				<p class="title">供应商退货出库单</p>
			</div>
			<c:set var="current" value="1"></c:set>
			<c:forEach items="${goodsReturnPrintList}" varStatus="i" var="goodsReturnPrint">
			<div>${current}&nbsp;货主：&nbsp;${goodsReturnPrint.customer_name}&nbsp;
				订单号：${goodsReturnPrint.order_id}&nbsp;供应商：${goodsReturnPrint.provider_name}</div>
			<div class="table">
				<ul class="thead">
					<li style="width:10%;">库位条码</li>
					<li style="width:15%;">商品条码</li>
					<li style="width:45%;">商品名称</li>
					<li style="width:20%;">生产日期</li>
					<li style="width:10%;">数量</li>
				</ul>
				<ul class="tbody">
					<c:forEach items="${goodsReturnPrint.goodsReturnPrintInfo}" var="info">
						<li>
							<span class="loc" style="width:10%;">${info.location_barcode}</span>
							<span style="width:15%;">${info.barcode}</span>
							<span style="width:45%;">${info.product_name}</span>
							<span style="width:20%;">${info.validity}</span>
							<span style="width:10%;">${info.quantity}</span>
						</li>
					</c:forEach>
				</ul>
			</div>
			<c:set var="current" value="${current+1}"></c:set>
			<div><br/></div>
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