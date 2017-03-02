<%@ page language="java" import="java.util.*" pageEncoding="utf8" isELIgnored="false"  %>
<!doctype html>
<html>
<head>
<meta charset="UTF-8" />


<%
	String path = request.getContextPath();
	String rootPath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ "/";
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	request.setAttribute("basePath", basePath);
	request.setAttribute("rootPath", rootPath);
	pageContext.setAttribute("newLineChar", "\n");
%>
<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery/jquery.js"></script>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>  
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<style type="text/css">
input{
  	height: 30px;
    margin: 10px;
	border-radius:6px;
	padding: 4px;
  	border: solid 1px #E5E5E5;
	font: normal 13px/100% Verdana, Tahoma, sans-serif;
	box-shadow: rgba(0, 0, 0, 0.1) 0px 0px 8px;
}
.datagrid-mask {
	background: #ccc;
}

.datagrid-mask-msg {
	border-color: #95B8E7;
}

.datagrid-mask-msg {
	background: #ffffff url('../../images/loading.gif') no-repeat scroll 5px
		center;
}

.datagrid-mask {
	position: absolute;
	left: 0;
	top: 0;
	width: 100%;
	height: 100%;
	opacity: 0.3;
	filter: alpha(opacity = 30);
	display: none;
}

.datagrid-mask-msg {
	position: absolute;
	top: 50%;
	margin-top: -20px;
	padding: 12px 5px 10px 30px;
	width: auto;
	height: 16px;
	border-width: 2px;
	border-style: solid;
	display: none;
}

.list_table {
	border: 1px solid #CCCCCC;
	border-collapse: collapse;
	color: #333333;
	margin: 5px;
	border-spacing: 0;
	  font-size: 14px;
}

.list_table tbody td {
	border-top: 1px solid #CCCCCC;
	text-align: center;
}

.list_table td {
	
}



.list_table tbody tr:hover th,.list_table tbody tr:hover td {
	background: #EEF0F2;
}

.list_table thead tr {
	border-bottom: 1px solid #CCCCCC;
	border-right: 1px solid #CCCCCC;
}
</style>
<style type="text/css">
	.bgray {
		padding: 15px;
	}
	.bgray a.lq-btn {
		margin-bottom: 3px;
	}
	.bgray table tr td{
		text-align: left;
		vertical-align: middle;
		line-height: 1.4;
	}
	.bgray table tr:nth-child(even){
		background: #eee;
	}
	.bgray a{
		text-decoration: none;
	}
	.bgray input[type=text]{
		width: 90%;
	}
	.bgray select{
		cursor:pointer;
	}
</style>
</head>

<title>wms调度配置管理</title>
<body class="bgray">

	<div style="clear:both;margin: 0px;">
		<div class="lq-panel lq-panel-default">
			<div class="lq-panel-heading" style="text-align: center;font-size: large;">
				<i class="fa fa-tasks"></i>
				wms调度配置管理
			</div>
		</div>
	</div>

	<form id="addForm" method="post">
		<table class="list_table lq-table">
			<thead>
				<tr>
					<th style="width:90px;">操作</th>
					<th>Id</th>
					<th>group</th>
					<th style="min-width: 80px;">name</th>
					<th style="min-width: 80px;">状态</th>
					<th style="min-width:112px;">
						<select name="taskType" id="taskType" class="lq-form-control">
						</select>
					</th>
					<th style="min-width: 105px;">cron表达式</th>
					<th style="min-width: 100px;">描述</th>
					<th style="min-width: 60px;">同步</th>
					<th>spring id</th>
					<th>方法名</th>
					<th>参数名值对</th>
				</tr>
			</thead>
			<tbody class="tbody">
<%-- 				<c:forEach var="job" items="${scheduleJobList}">
					<tr>
						<td>
							<a href="javascript:;" onclick="updateCron('${job.jobId}')" class="lq-btn lq-btn-sm lq-btn-primary">更新cron</a><br/>
							<a href="javascript:;" onclick="updateParamNameValue('${job.jobId}')"  class="lq-btn lq-btn-sm lq-btn-primary">更新参数</a>
							<a href="javascript:;" onclick="deleteJob('${job.jobId}')"  class="lq-btn lq-btn-sm lq-btn-primary">删除调度</a>
							<a href="javascript:;" onclick="runAJobNow('${job.jobId}')"  class="lq-btn lq-btn-sm lq-btn-primary">手动触发</a>
						</td>					
						<td>${job.jobId }</td>
						<td>${job.jobGroup }</td>
						<td>${job.jobName }</td>
						<td>${job.jobStatus }<c:choose>
								<c:when test="${job.jobStatus=='1' }">
									<a href="javascript:;"
										onclick="changeJobStatus('${job.jobId}','stop')" class="lq-btn lq-btn-sm lq-btn-primary">停止</a>&nbsp;
								</c:when>
								<c:otherwise>
									<a href="javascript:;"
										onclick="changeJobStatus('${job.jobId}','start')" class="lq-btn lq-btn-sm lq-btn-primary">开启</a>&nbsp;
								</c:otherwise>
							</c:choose>
						</td>
						<td>${job.jobType }</td>
						<td>${job.cronExpression }</td>
						<td>${job.description }</td>
						<td>${job.isConcurrent }</td>
						<td>${job.springId }</td>
						<td>${job.methodName }</td>
						<td>${job.paramNameValue }</td>
					</tr>
				</c:forEach>
				<tr>
					<td>n</td>
					<td><input type="text" name="jobGroup" id="jobGroup"/></td>
					<td><input type="text" name="jobName" id="jobName"/></td>
					<td>0<input type="hidden" name="jobStatus" value="0"/></td>
					<td><input type="text" name="cronExpression" id="cronExpression"/></td>
					<td><input type="text" name="description" id="description"/></td>
					<td>
						<select name="isConcurrent" id="isConcurrent" class="lq-form-control">
								<option value="1">1</option>
								<option value="0">0</option>
						</select>
					</td>
					<td></td>
					<td><input type="text" name="springId" id="springId"/></td>
					<td><input type="text" name="methodName" id="methodName"/></td>
					<td><input type="text" name="paramNameValue" id="paramNameValue"/></td><br/>
					<td><input type="button" class="lq-btn lq-btn-sm lq-btn-primary" onclick="add()" value="保存" /></td>
				</tr> --%>
			</tbody>
		</table>
	</form>
	<script type="text/javascript">
		function Load(){
			$.ajax({
				url:'../scheduleJob/getType',
				type:'GET',
				dataType:'json',
				success:function(res){
					console.log(res);
					bindSelect(res.typeList);
					$("#taskType").trigger("change");
				},
				error:function(err){
					alert(err);
				}
			})
		}
		
		function bindSelect(selectList){
			if(selectList.length>0){
				var option="";
				for(var i in selectList)
				option+='<option value="'+selectList[i]+'">'+selectList[i]+'</option>';
				
			}
			$("#taskType").append(option);
		}
		
		$("#taskType").change(function(){
			var type=$(this).val();
			//console.log(data);
			console.log(type);
			$.ajax({
				url:'../scheduleJob/getListByType',
				type:'POST',
				dataType:'json',
				data:{
					jobType:type
				},
				success: function(res){
					showTable(res.scheduleJobList);
					console.log(res);
				},
				error: function(err){
					alert("调用ajax接口失败");
				}
			})
		})
		Load();
		
		function showTable(data){
			var tdHtml='';
			$("tbody.tbody").html("");
			for(var i in data){
				tdHtml+='<tr>';
				tdHtml+='<td><a href="javascript:;" onclick="updateCron('+data[i].jobId+')" class="lq-btn lq-btn-sm lq-btn-primary">更新cron</a><br/>';
				tdHtml+='<a href="javascript:;" onclick="updateParamNameValue('+data[i].jobId+')" class="lq-btn lq-btn-sm lq-btn-primary">更新参数</a>';
				tdHtml+='<a href="javascript:;" onclick="deleteJob('+data[i].jobId+')"  class="lq-btn lq-btn-sm lq-btn-primary">删除调度</a>';
				tdHtml+='<a href="javascript:;" onclick="runAJobNow('+data[i].jobId+')"  class="lq-btn lq-btn-sm lq-btn-primary">手动触发</a></td>';
				tdHtml+='<td>'+data[i].jobId+'</td>';	
				tdHtml+='<td>'+data[i].jobGroup+'</td>';
				tdHtml+='<td>'+data[i].jobName+'</td>';
				if(data[i].jobStatus=="1")
					tdHtml+='<td>'+data[i].jobStatus+'&nbsp;&nbsp;&nbsp;<a href="javascript:;" onclick="changeJobStatus('+data[i].jobId+',\'stop\')" class="lq-btn lq-btn-sm lq-btn-primary">停止</a></td>';
				else 
					tdHtml+='<td>'+data[i].jobStatus+'&nbsp;&nbsp;&nbsp;<a href="javascript:;" onclick="changeJobStatus('+data[i].jobId+',\'start\')" class="lq-btn lq-btn-sm lq-btn-primary">开启</a></td>';
				tdHtml+='<td>'+data[i].jobType+'</td>';
				tdHtml+='<td>'+data[i].cronExpression+'</td>';
				tdHtml+='<td>'+data[i].description+'</td>';
				tdHtml+='<td>'+data[i].isConcurrent+'</td>';
				tdHtml+='<td>'+data[i].springId+'</td>';
				tdHtml+='<td>'+data[i].methodName+'</td>';
				tdHtml+='<td>'+data[i].paramNameValue+'</td>';
				tdHtml+='</tr>';
			}
			var lastRow='<tr><td><input type="button" class="lq-btn lq-btn-sm lq-btn-primary" onclick="add()" value="保存" /></td>';
				lastRow+='<td>n</td>';
				lastRow+='<td><input type="text" name="jobGroup" id="jobGroup"/></td>';
				lastRow+='<td><input type="text" name="jobName" id="jobName"/></td>';
				lastRow+='<td>0<input type="hidden" name="jobStatus" value="0"/></td>';
				lastRow+='<td><input type="text" name="jobType"></td>';
				lastRow+='<td><input type="text" name="cronExpression" id="cronExpression"/></td>';
				lastRow+='<td><input type="text" name="description" id="description"/></td><td>';
				lastRow+='<select name="isConcurrent" id="isConcurrent" class="lq-form-control"><option value="1">1</option><option value="0">0</option></select></td>';
				lastRow+='<td><input type="text" name="springId" id="springId"/></td>';
				lastRow+='<td><input type="text" name="methodName" id="methodName"/></td>';
				lastRow+='<td><input type="text" name="paramNameValue" id="paramNameValue"/></td>';
				lastRow+='</tr>';
			
			var table=tdHtml+lastRow;
			$("tbody.tbody").html(table);
							
		}
		$(document).ready(function(){
			$('#addNow').click(function(){
				if ($.trim($('#jobGroup_1').val()) == '') {
					alert('Group不能为空！');
					$('#jobGroup_1').focus();
					return false;
				}
				if ($.trim($('#jobName_1').val()) == '') {
					alert('Name不能为空！');
					$('#jobName_1').focus();
					return false;
				}
				if ($.trim($('#springId_1').val()) == '') {
					alert('springId不能为空！');
					$('#springId_1').focus();
					return false;
				}
				if ($.trim($('#methodName_1').val()) == '') {
					alert('methodName不能为空！');
					$('#methodName_1').focus();
					return false;
				}
				$.ajax({
					type : "POST",
					dataType : "json",
					url : "${basePath}scheduleJob/runAJobNow2",
					data : {
						jobGroup:$('#jobGroup_1').val(),
						jobName:$('#jobName_1').val(),
						springId:$('#springId_1').val(),
						methodName:$('#methodName_1').val(),
						paramNameValue:$('#paramNameValue_1').val()
					},
					success : function(data) {
						if (data.msg) {
							alert(data.msg);
						}else{
							location.reload();
						}
					},
					error: function(data) {
		                alert('ajax请求错误'); 
		            }
				});//end-ajax
			});
		});
	</script>
	<script>
		function validateAdd() {
			if ($.trim($('#jobName').val()) == '') {
				alert('name不能为空！');
				$('#jobName').focus();
				return false;
			}
			if ($.trim($('#jobGroup').val()) == '') {
				alert('group不能为空！');
				$('#jobGroup').focus();
				return false;
			}
			if ($.trim($('#cronExpression').val()) == '') {
				alert('cron表达式不能为空！');
				$('#cronExpression').focus();
				return false;
			}
			if ($.trim($('#beanClass').val()) == '' && $.trim($('#springId').val()) == '') {
				$('#beanClass').focus();
				alert('类路径和spring id至少填写一个');
				return false;
			}
			if ($.trim($('#methodName').val()) == '') {
				$('#methodName').focus();
				alert('方法名不能为空！');
				return false;
			}
			return true;
		}
		function add() {
			if (validateAdd()) {
				showWaitMsg();
				$.ajax({
					type : "POST",
					async : false,
					dataType : "JSON",
					cache : false,
					url : "${basePath}scheduleJob/add",
					data : $("#addForm").serialize(),
					success : function(data) {
						hideWaitMsg();
						if (data.flag) {

							location.reload();
						} else {
							if(data.msg) 
								alert(data.msg);
						}
						location.reload();
					}//end-callback
				});//end-ajax
			}
		}
		function changeJobStatus(jobId, cmd) {
			showWaitMsg();
			$.ajax({
				type : "POST",
				async : false,
				dataType : "JSON",
				cache : false,
				url : "${basePath}scheduleJob/changeJobStatus",
				data : {
					jobId : jobId,
					cmd : cmd
				},
				success : function(data) {
					hideWaitMsg();
					if (data.flag) {
						$("#taskType").trigger("change");
					} else {
						if(data.msg)
							alert(data.msg);
					}
					location.reload();
				}//end-callback
			});//end-ajax
		}
		function updateCron(jobId) {
			var cron = prompt("输入cron表达式！", "")
			if (cron) {
				showWaitMsg();

				$.ajax({
					type : "POST",
					async : false,
					dataType : "JSON",
					cache : false,
					url : "${basePath}scheduleJob/updateCron",
					data : {
						jobId : jobId,
						cron : cron
					},
					success : function(data) {
						hideWaitMsg();
						if (data.flag) {
							location.reload();
						} else {
							if(data.msg)
								alert(data.msg);
						}
						location.reload();
					}//end-callback
				});//end-ajax
			}

		}
		function updateParamNameValue(jobId) {
			var paramNameValue = prompt("输入paramNameValue表达式！", "")
			if (paramNameValue) {
				showWaitMsg();

				$.ajax({
					type : "POST",
					async : false,
					dataType : "JSON",
					cache : false,
					url : "${basePath}scheduleJob/updateParamNameValue",
					data : {
						jobId : jobId,
						paramNameValue : paramNameValue
					},
					success : function(data) {
						hideWaitMsg();
						console.log(data);
						if (data.flag) {

							location.reload();
						} else {
							if(data.msg)
								alert(data.msg);
						}
						location.reload();
					}//end-callback
				});//end-ajax
			}

		}
		function deleteJob(jobId) {
			var r=confirm("将要删除该调度")
			if (r==false){
				return false;
			}
		
			$.ajax({
				type : "POST",
				async : false,
				dataType : "JSON",
				cache : false,
				url : "${basePath}scheduleJob/deleteJob",
				data : {
					jobId : jobId
				},
				success : function(data) {
					hideWaitMsg();
					if (data.flag) {
						location.reload();
					} else {
						if(data.msg)
							alert(data.msg);
					}
					location.reload();
				}//end-callback
			});//end-ajax
		}
		function runAJobNow(jobId) {
			
			showWaitMsg();

			$.ajax({
				type : "POST",
				async : false,
				dataType : "JSON",
				cache : false,
				url : "${basePath}scheduleJob/runAJobNow3",
				data : {
					jobId : jobId
				},
				success : function(data) {
					hideWaitMsg();
					alert("手动触发成功");

				}//end-callback
			});//end-ajax

		}
		function showWaitMsg(msg) {
			if (msg) {

			} else {
				msg = '正在处理，请稍候...';
			}
			var panelContainer = $("body");
			$("<div id='msg-background' class='datagrid-mask' style=\"display:block;z-index:10006;\"></div>").appendTo(panelContainer);
			var msgDiv = $("<div id='msg-board' class='datagrid-mask-msg' style=\"display:block;z-index:10007;left:50%\"></div>").html(msg).appendTo(
					panelContainer);
			msgDiv.css("marginLeft", -msgDiv.outerWidth() / 2);
		}
		function hideWaitMsg() {
			$('.datagrid-mask').remove();
			$('.datagrid-mask-msg').remove();
		}
	</script>
</body>
</html>




