<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
	<title>查询订单商品</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
  	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
  	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
  	<style>
    .mw-loading {
        background-color: transparent;
    }

    .mc-loading {
        position: absolute;
        top: 20%;
        left: 50%;
        width: 100px;
        height: 100px;
        margin-left: -50px;
        text-align: center;
        padding-top: 25px;
        background-color: rgba(0,0,0,.5);
        border-radius: 5px;
    }
  
    #loading {
        font-size: 32px;
        color: #fff;
    }

    #order-info-content, #tracking-info-content ,#order-info-content2{
    	display: none;
    	margin:15px 0;
    }
    
	#kickOut {
    	display: none;
    }
    .btns {
    	margin: 10px 0 5px 0;
    }
	.noDataTip {
	display: none;
	text-align: center;
	font-size: 30px;
	color: #666;
	margin: 30px 0 0 0;
	}
	
	input[type="text"],
	select {
  	height: 30px;
  	position: relative;
  	top: 5px;
	}	
  	</style>
</head>
<body>
	<div class="modal-wrap mw-loading">
        <div class="modal-content mc-loading">
            <i class="fa fa-spinner fa-spin" id="loading"></i>
        </div>
    </div>
    <div class="main-container">
		<table>
			<tr>
				<td>选择货主:</td>
				<td>
	       			<div class="lq-form-group">
						<select name="customer_id" id="customer_id" class="lq-form-control">
							<c:forEach items="${customers}" var="customer">
								<option name="customer_id" value="${customer.customer_id}">${customer.name}
								</option>
							</c:forEach>
						</select>
	       			</div>					
				</td>
			</tr>
			<tr>
				<td>选择渠道:</td>
				<td>
	       			<div class="lq-form-group">
						<select name="warehouse_id" id="warehouse_id" class="lq-form-control">
							<c:forEach items="${warehouseList}" var="warehouse">
								<option name="warehouse_id" value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
							</c:forEach>
						</select>
	       			</div>					
				</td>
			</tr>
			<tr>
				<td>商品状态:</td>
				<td>
					<select name="status" id="status" class="lq-form-control">
							<option value="NORMAL">全新</option>
							<option value="DEFECTIVE">二手</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>输入库位:</td>
				<td>
					<input type="text" id="location_barcode">
				</td>
			</tr>				
			<tr>
				<td>商品条码:</td>
				<td>
					<input type="text" id="barcode">
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<button id="submit" class="lq-btn lq-btn-primary lq-btn-sm" >录入</button>
				</td>
			</tr>
		</table>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>	
	<script type="text/javascript">
	$(document).ready(function(){
		///查询
		$("#submit").click(function(e){
			e.preventDefault();
			that=$(this);
			that.attr("disabled",true);
			var customer_id=$("#customer_id").val();
			var status=$("#status").val();
			var location_barcode=$("#location_barcode").val();
			var barcode=$("#barcode").val();
			var num_real="0";//$("#num_real").val();
			
			var warehouse_id=$("#warehouse_id").val();
			if(location_barcode==""||barcode==""||num_real==""){
				alert("所填信息不能为空!");
				return ;
			}
			$.ajax({
				url: '${pageContext.request.contextPath}/exception/doTaskCountByHand',
				dataType: 'json',
				type: 'post',
				data: {
					customer_id: customer_id,
					status:status,
					location_barcode:location_barcode,
					barcode:barcode,
					num_real:num_real,
					warehouse_id: warehouse_id
				},
				success: function(res) {
					console.log(res);
					if(res.success) {
						alert("录入成功!");
						
					}else {
						alert(res.message);
					}
					
					$("#barcode").val("");
					$("#location_barcode").val("");
					
					that.removeAttr("disabled");
				},
				error: function(err){
					console.log(err);
				}
			})
		});
		
	})
	</script>
</body>
</html>
