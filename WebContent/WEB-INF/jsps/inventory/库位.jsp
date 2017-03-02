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
	<title>库位查询</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style type="text/css">
	.download {
		line-height: 30px;
		padding: 0 6px;
	}
	</style>
</head>
<body>
	<div class="main-container">
		<form class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label>库位类型:</label>
						<select class="lq-form-control">
							<option value="">拣货区</option>
							<option value="">存储区</option>
							<option value="">中转区</option>
							<option value="">收货区</option>
						</select>
					</div>
					<div class="lq-form-group">
						<div class="lq-form-group">
							<label>库位类型:</label>
							<input type="text" class="lq-form-control" />
						</div>
					</div>
				</div>
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label>库位状态:</label>
						<select class="lq-form-control">
							<option value="">空</option>
							<option value="">非空</option>
							<option value="">不限</option>
						</select>
					</div>
				</div>
				<div class="lq-col-4">
					<div class="lq-form-group">
						<button type="button" class="lq-btn lq-btn-sm lq-btn-primary search">&nbsp;&nbsp;查&nbsp;&nbsp;&nbsp;&nbsp;询&nbsp;&nbsp;</button>
						<button type="button" class="lq-btn lq-btn-sm lq-btn-primary search">新增库位</button>
					</div>
					<div class="lq-form-group">
						<button type="button" class="lq-btn lq-btn-sm lq-btn-primary search">批量导入</button>
						<a class="download" href="">模板下载</a>
					</div>
				</div>
			</div>
		</form>
		<table class="lq-table">
			<thead>
				<th>库位类型</th>
				<th>库位条码</th>
				<th>货主</th>
				<th>库位状态</th>
				<th>创建者</th>
				<th>创建时间</th>
				<th>修改者</th>
				<th>修改时间</th>
				<th>操作</th>
			</thead>
			<tbody>
				
			</tbody>
		</table>
	</div>
</body>
</html>