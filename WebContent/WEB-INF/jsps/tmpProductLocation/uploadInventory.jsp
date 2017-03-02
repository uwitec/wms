<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>库存导入</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style type="text/css">
	.download {
		line-height: 30px;
		padding: 0 6px;
	}
	html, body {
		width: 100%;
		height: 100%;
	}
	.alert {
		display: none;
		position: fixed;
		width: 100%;
		height: 100%;
		z-index: 999;
		background-color: rgba(0,0,0,.5); 
		overflow: auto;
	}
	
	.alertMain {
		width:600px;
		margin:0 auto;
		background: #fff;
	}
	.alertHeader {
		border-bottom: 1px solid #999;
	}
	.alertContainer {
		display: block;
		padding: 20px 50px;
	}
	.alertTitle {
		height: 30px;
		line-height: 30px;
		text-align: center;
		margin: 0;
	}
	.margin {
		margin: 0 0 0 20px;
	}
	.lcItem {
		margin: 10px 0 0 0;
	}
	.BtnItem {
		text-align: center;
	}
	.lcItem .lcInput {
		width: 50px;
		margin: 0 10px 0 0;
	}
	.lcItem .sequence {
		width: 150px;
	}
	.cancel {
		margin: 0 0 0 50px;
	}
	.del {
		color: red;
		cursor: pointer;
	}
	.update {
		cursor: pointer;
	}
	/*.inputAll, .download {
		float: right;
	}*/
	#page-search {
		float: right;
	}
	.page-wrap {
		display: none;
	}
	.uploadfile {
		margin: 10px 0 0 0;
	}
	#batchTable {
		margin: 10px 0 0 0;
	}
	.inputMsg {
	}
	.file {
		position: relative;
		left: -70px;
		width: 66px;
		height: 30px;
		opacity: 0;
		cursor: pointer;
	}
	.fileName {
		position: relative;
		top: 5px;
		left: -65px;
	}
	</style>
</head>
<body>
	<div class="main-container">
	<form action="../inventory/uploadInventory" class="lq-form-inline">
		<div class="lq-col-3">
			<div class="lq-form-group">
				<label for="customer_id">选择货主:</label>
				<select name="customer_id" id="customer_id" class="lq-form-control">
					<option value="">--请选择--</option> 
					<c:forEach items="${customers}" var="customer">
						<option name="customer_id"
							class="${customer.customer_id}"
							<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
							value="${customer.customer_id}">${customer.name}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="lq-col-3">
			<div class="lq-form-group">
				<button type="submit" class="lq-btn lq-btn-sm lq-btn-primary search">&nbsp;&nbsp;查&nbsp;&nbsp;&nbsp;&nbsp;询&nbsp;&nbsp;</button>
			</div>
		</div>
	</form>
	<form action="../inventory/uploadInventoryFile" method="post" enctype="multipart/form-data">
		<div class="lq-row">
			<div class="lq-col-4">
				<div class="lq-form-group">
					<button class="lq-btn lq-btn-sm lq-btn-primary search">选取文件</button>
					<input class="file" type="file" name="uploadfile" />
					<span class="fileName"></span>
					<span class="inputMsg">
						<c:if test="${resMap.excel_note != '' and resMap.excel_note != null}">
							<c:out value="${resMap.excel_note}"></c:out>
						</c:if>
					</span>
				</div>
			</div>
			<div class="lq-col-4">
				<div class="lq-form-group">
					<button class="lq-btn lq-btn-sm lq-btn-primary">批量导入</button>
					<a class="download" href="../inventory/downloadInventory">模板下载</a>
				</div>
			</div>
		</div>
	</form> 
	 <table class="lq-table" id="batchTable">
		 <thead>
			<tr>
				<th>编号</th>
				<th>货主</th>
				<th>商品名称</th>
				<th>商品条码</th>
				<th>库位编码</th>
				<th>数量</th>
				<th>生产日期</th>
				<th>新旧状态</th>
				<th>串号</th>
				<th>转换状态</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>最后更新人</th>
				<th>最后更新时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${waitImportInventory}" varStatus="i" var="item">
				<tr align="center" class="list">
					<td>${i.index+1}</td>
					<td>${item.name}</td>
					<td>${item.product_name}</td>
					<td>${item.product_barcode}</td>
					<td>${item.location_barcode}</td>
					<td>${item.quantity}</td>
					<td>${item.validity}</td>
					<td>${item.status}</td>
					<td>${item.serial_number}</td>
					<td>${item.transfer_status}</td>
					<td>${item.created_user}</td>
					<td>${item.created_time}</td>
					<td>${item.last_updated_user}</td>
					<td>${item.last_updated_time}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript">
	$(function () {
		function getFileName(obj){  
			var fileName="";  
			if (typeof(fileName) != "undefined") {  
			    fileName = $(obj).val().split("\\").pop(); 
			}  
			return fileName;  
		} 
		$('.file').change(function () {
			$('.fileName').html(getFileName($(this)));
		});
	});
	</script>
</body>
</html>