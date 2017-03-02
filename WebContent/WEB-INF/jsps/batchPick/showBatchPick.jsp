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
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<title>生成波次单</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style>
	.lq-form-group-nodisplay {
		display: none;
	}
	.lq-tab {
		display: inline-block;
		font-size: 0;
		border: 1px solid #ccc;
		border-radius: 5px;
		margin: 0 0 20px 0;
	}
	.lq-tab-btn {
		display: inline-block;
		font-size: 14px;
		padding: 7px 20px;
		border: none;
		background: #f2f2f2;
	}
	.lq-tab-btn1 {
		
	}
	.lq-tab-btn2 {
		
	}
	.active {
		color: #fff;
		background: #337ab7;
	}
	/*弹窗*/
	html, body, #alert {
		width: 100%;
		height: 100%;
	}
	#alert {
		display: none;
		position: fixed;
		z-index: 999;
	}
	.cover {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background: #000;
		opacity: 0.5;
	}
	.alertMain {
		position: absolute;
		top: 50%;
		left: 50%;
		background: #fff;
		border-radius: 5px;
	}
	.alertContain {
		display: table;
		box-sizing: border-box;
		width: 85%;
		height: 77%;
		margin: 0 auto;
	}
	.alertText {
		display: table-cell;
		vertical-align: middle;
		text-align: center;
		line-height: 18px;
	}
	/*.alertText * {
		line-height: 18px;
	}*/
	.alertBtn {
		display: block;
		box-sizing: border-box;
		width: 100%;
		height: 23%;
		padding: 8px 0;
		border-top: 1px solid #5bc0de;
		cursor: pointer;
		text-align: center;
	}
	.btn {
		width: 50%;
		height: 100%;
		border: 0;
		float: left;
		/*color: #666;*/
		font-size: 14px;
		background: #fff;
		outline: #ccc;
		cursor: pointer;
	}
	.trueBtn {
		border-right: 1px solid #5bc0de;
		border-bottom-left-radius: 5px;
		color: #5cb85c;
	}
	.falseBtn {
		border-bottom-right-radius: 5px;
		color: #d9534f;
	}
	.oneBtn {
		height: 100%;
		border: 0;
		color: #5cb85c;
		font-size: 14px;
		background: #fff;
		outline: #ccc;
		cursor: pointer;
	}
	</style>
</head>
<body>
	<div class="main-container">
		<div class="lq-tab">
			<button id="lq-tab-btn1" class="lq-tab-btn lq-tab-btn1 active">全局波次规则</button>
			<button id="lq-tab-btn2" class="lq-tab-btn lq-tab-btn2">单个波次规则</button>
		</div>
		<form class="lq-form-inline">
		
	        <div id="customer_div" class="lq-form-group lq-form-group-nodisplay">
				<label for="customer_id">选择货主:</label>
				<select name="customer_id" id="customer_id" class="lq-form-control">
					<c:forEach items="${customers}" var="customer">
						<option name="customer_id" class="${customer.customer_id}"
							<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
							value="${customer.customer_id}">${customer.name}
						</option>
					</c:forEach>
				</select>
			</div>
			<div class="lq-row">
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="time">选择波次频率:</label> 
						<select
							name="time" id="time" class="lq-form-control">
							<option value="10">10分钟</option>
							<option value="15" selected="true">15分钟</option>
							<option value="20">20分钟</option>
							<option value="30">30分钟</option>
							<option value="1">1小时</option>
							<option value="2">2小时</option>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="size">波次单订单数:</label>
						<input type="text" id="size" name="size" class="lq-form-control" required="required">
					</div>
				</div>
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="minSize">订单数量下限:</label>
						<input type="text" id="minSize" name="minSize" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="maxSize">订单数量上限:</label>
						<input type="text" id="maxSize" name="maxSize" class="lq-form-control" onblur="maxLimit(this.value)">
					</div>
				</div>
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="minWeight">订单重量下限:</label>
						<input type="text" id="minWeight" name="minWeight" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="maxWeight">订单重量上限:</label>
						<input type="text" id="maxWeight" name="maxWeight" class="lq-form-control">
					</div>
				</div>
			</div>
			<div class="lq-form-group">
				<label for="level">选择波次级别:</label> <select
					name="level" id="level" class="lq-form-control">
					<option value="1">紧急订单</option>
					<option value="2">紧急订单 ，批量订单</option>
					<option value="3"  selected="true">紧急订单 ， 批量订单 ，商品相同订单</option>
					<!-- <option value="4">跑紧急订单和商品相同订单和同库位订单</option> -->
					<option value="4">紧急订单，批量订单，商品相同订单，杂单</option>
				</select>
			</div>

            <div class="lq-form-group lq-form-group-nodisplay">
				<label>规则生效时间:</label>
				<select
					name="start" id="start" class="lq-form-control">
					<option value="0"   selected="true">0</option>
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
					<option value="7">7</option>
					<option value="8">8</option>
					<option value="9">9</option>
					<option value="10">10</option>
					<option value="11">11</option>
					<option value="12">12</option>
					<option value="13">13</option>
					<option value="14">14</option>
					<option value="15">15</option>
					<option value="16">16</option>
					<option value="17">17</option>
					<option value="18">18</option>
					<option value="19">19</option>
					<option value="20">20</option>
					<option value="21">21</option>
					<option value="22">22</option>
					<option value="23">23</option>
					
				</select>
				
				<span>-</span>
				<select
					name="end" id="end" class="lq-form-control">
					<option value="0">0</option>
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
					<option value="7">7</option>
					<option value="8">8</option>
					<option value="9">9</option>
					<option value="10">10</option>
					<option value="11">11</option>
					<option value="12">12</option>
					<option value="13">13</option>
					<option value="14">14</option>
					<option value="15">15</option>
					<option value="16">16</option>
					<option value="17">17</option>
					<option value="18">18</option>
					<option value="19">19</option>
					<option value="20">20</option>
					<option value="21">21</option>
					<option value="22">22</option>
					<option value="23"   selected="true">23</option>
					
				</select>
			</div>

			<button type="button" id="submit" class="lq-btn lq-btn-sm lq-btn-primary">保存</button>
			<button type="button" class="lq-btn lq-btn-sm lq-btn-primary" id="clear">清空</button>
		</form>
		
		
		<div class="lq-col-7">
			<table class="lq-table" id="table-batch">
				<thead>
					<tr>
						<!-- <th>波次任务序号</th> -->
						<th>波次任务编号</th>
						<th>波次任务名</th>
						<th>波次任务状态</th>
						<th>波次单订单数</th>
						<th>订单数量上限</th>
						<th>订单数量下限</th>
						<th>任务级别</th>
						<th>任务频率</th>
						<th>创建时间</th>
						<th>有效时间</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach  items="${scheduleJobList}"  var="item"  varStatus="status">
						<tr class="">
						<!--<td>${status.index+1 }</td> -->
							<td>${item.jobId}</td>
							<td>${item.jobName}</td>
							<td>${item.jobStatus}</td>
							<td>${item.batchPickParam.size}</td>
							<td>${item.batchPickParam.maxSize}</td>
							<td>${item.batchPickParam.minSize}</td>
							<td>${item.batchPickParam.level}</td>
							<td><c:if test="${item.batchPickParam.time>5}">${item.batchPickParam.time}分钟</c:if><c:if test="${item.batchPickParam.time<5}">${item.batchPickParam.time}小时</c:if></td>
							<td>${item.batchPickParam.createTime}</td>
							<td>${item.batchPickParam.runTimeStart}-${item.batchPickParam.runTimeEnd}</td>
							<td><button class="lq-btn lq-btn-sm lq-btn-primary deletejob">删除</button></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		
	</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script type="text/javascript">
function maxLimit(value){
	if(parseInt(value)>=500){
		console.log(value);
		$("#maxSize").val("500");
	}
}
$(document).ready(function() {
	$("#lq-tab-btn1").click(function() {
		$('#lq-tab-btn2').removeClass('active');
		$(this).addClass('active');
		$(".lq-form-group-nodisplay").css("display","none");
	});
	$("#lq-tab-btn2").click(function() {
		$('#lq-tab-btn1').removeClass('active');
		$(this).addClass('active');
		$(".lq-form-group-nodisplay").css("display","block");
	});
	function getCustomer_id() {
		if($(".lq-form-group-nodisplay").css("display")=="none"){
			return "";
		}
		return $("#customer_id").val();
	}
	
	function getStart() {
		if($(".lq-form-group-nodisplay").css("display")=="none"){
			return "";
		}
		return $("#start").val();
	}
	
	function getEnd() {
		if($(".lq-form-group-nodisplay").css("display")=="none"){
			return "";
		}
		return $("#end").val();
	}
	
	function getUrl() {
		if($(".lq-form-group-nodisplay").css("display")=="none"){
			return "${pageContext.request.contextPath}/batchPick/addJob";
		}
		return "${pageContext.request.contextPath}/batchPick/addSingleJob";
	}

	$(document).on('click','.deletejob',function(){
		var job_id=$(this).parent().parent().find('td:eq(0)').html();
		
		var data = {
				job_id: job_id,
			};
		$.ajax({
			url : "${pageContext.request.contextPath}/batchPick/deleteJob",
			type : "post",
			dataType : "json",
			contentType : "application/x-www-form-urlencoded; charset=utf-8",
			data : data,
			success : function(data) {
				refreshTable(data.scheduleJobList);
				alert(data.message);
			},
			error : function() {
				console.log("ajax2 error");
			}
		});
	});
	
	$("#submit").click(function(e) {
		e.preventDefault();
		var inputCheck = $('input[type="text"]').val();
		console.log($('input[type="text"]'))
		var reg = /^[0-9a]+$/;
		// 必填
		if (!$('#size').val()) {
			alert('波次单订单数不能为空！');
			return false;
		} else if (!$('#minSize').val()) {
			alert('订单数量下限不能为空！');
			return false;
		} else if (!$('#maxSize').val()) {
			alert('订单数量上限不能为空！');
			return false;
		}else if (parseInt($("#minSize").val())<parseInt($("#size").val())) {
			alert('批量单订单下限不能小于波次订单数！');
			return false;
		}
		else if (parseInt($("#maxSize").val())<parseInt($("#minSize").val())) {
			alert('批量单订单下限不能大于上限！');
			return false;
		}
		if ($('.active').html() == '单个波次规则') {
			if (!$('#start').val() || !$('#end').val()) {
				alert('请填写正确的规则生效时间！');
				return false;
			}
			var start=getStart();
			var end=getEnd();
			if(start>end){
				alert('请填写正确的规则生效时间,开始时间不能大于结束时间！');
				return false;
			}

			var date = new Date();
			if(date.getHours()-end>0){
				alert('请填写正确的规则生效时间,当前时间已经超过结束时间！');
				return false;
			}
		}
		if(!reg.test(inputCheck)){
			alert('请填写数字');
		}
		
		var customer_id=getCustomer_id();
		
		var url=getUrl();
		var data = {
			size: $("#size").val(),
			minSize: $("#minSize").val(),
			maxSize: $("#maxSize").val(),
			minWeight: 20 || $("#minWeight").val(),
			maxWeight: 100 || $("#maxWeight").val(),
			time:$("#time").val(),
			level:$("#level").val(),
			customer_id:customer_id,
			end:end,
			start:start
		};
		$.ajax({
			url : url,//"${pageContext.request.contextPath}/batchPick/addJob",
			type : "post",
			dataType : "json",
			contentType : "application/x-www-form-urlencoded; charset=utf-8",
			data : data,
			success : function(data) {
				console.log(data);
				refreshTable(data.scheduleJobList);
				alert(data.message);
			},
			error : function() {
				console.log("ajax2 error");
			}
		});
	});
	function refreshTable (data) {
		var html = "";
		if (data.length) {
			for (var i=0;i<data.length;i++) {
				
				html += "<tr><td>"
				+ data[i].jobId + "</td><td>"
				+ data[i].jobName + "</td><td>"
				+ data[i].jobStatus + "</td><td>"
				+ data[i].batchPickParam.size + "</td><td>"
				+ data[i].batchPickParam.maxSize + "</td><td>"
				+ data[i].batchPickParam.minSize + "</td><td>"
				+ data[i].batchPickParam.level + "</td><td>";
				if(data[i].batchPickParam.time>5){
					html +=data[i].batchPickParam.time + "分钟</td><td>";
				}
				else{
					html +=data[i].batchPickParam.time + "小时</td><td>";
				}
				html += data[i].batchPickParam.createTime + "</td><td>"
				+ data[i].batchPickParam.runTimeStart+'-'+data[i].batchPickParam.runTimeEnd + "</td><td><button class='lq-btn lq-btn-sm lq-btn-primary deletejob'>删除</button></td></tr>"
			}
		}
		$("#table-batch tbody").html(html);
	}
	// 清空
	$('#clear').on('click', function () {
		$('#size').val('');
		$('#minSize').val('');
		$('#maxSize').val('');
		$('#minWeight').val('');
		$('#maxWeight').val('');
	});
});

</script>
</body>
</html>