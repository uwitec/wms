<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<title>补货任务信息录入</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style>
	#form-task-list {
		margin-bottom: 15px;
	}
	#form-task-list label {
		width: 5.5em;
	}
	#task-table-wrap {
		position: relative;
		margin-top: 15px;
		display: none;
	}
	.bhinfo {
		position: absolute;
		right: 0;
		top: -52px;
	}
	.bhtype {
		margin-right: 20px;
	}
    .modal-wrap.mw-cancel, .modal-wrap.mw-batch-cancel {
    	background-color: rgba(0,0,0,.5);
    	/*display: block;*/
    }
    .mc-cancel, .mc-batch-cancel {
    	width: 220px;
    	padding: 15px 20px 15px 20px;
    	background-color: #fff;
    	position: absolute;
    	top: 100px;
    	left: 50%;
    	margin-left: -180px;
    	border-radius: 3px;
    	text-align: center;
    }
    #cancel_reason, #btn-cancel-submit, #batch_cancel_reason, #btn-batch-cancel-submit {
    	display: block;
    	margin:5px auto;
    	width: 180px;
    }
    #btn-save {
    	display: block;
    	margin:15px auto;
    }
	</style>
</head>
<body id="body-task-list">
	<div class="modal-wrap mw-cancel">
		<div class="modal-content mc-cancel lq-form-inline">
			<div class="lq-form-group">
				<label for="cancel_reason">取消原因:</label>
				<select name="cancel_reason" id="cancel_reason" class="lq-form-control">
					<option value="4">无理由取消</option>
					<option value="2">数量异常</option>
					<option value="3">质量异常</option>
				</select>
				<input type="hidden" id="cancel-task-id">
			</div>
			<div class="lq-form-group">
				<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-cancel-submit">确认</button>
			</div>
		</div>
	</div>
	<div class="modal-wrap mw-batch-cancel">
		<div class="modal-content mc-batch-cancel lq-form-inline">
			<div class="lq-form-group">
				<label for="batch_cancel_reason">取消原因:</label>
				<select name="batch_cancel_reason" id="batch_cancel_reason" class="lq-form-control">
					<option value="4">无理由取消</option>
					<option value="2">数量异常</option>
					<option value="3">质量异常</option>
				</select>
			</div>
			<div class="lq-form-group">
				<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-batch-cancel-submit">确认</button>
			</div>
		</div>
	</div>
	<div class="main-container">
		<div class="lq-form-inline" id="form-task-list">
			<div class="lq-row">
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="batch_task_sn">补货批次号:</label>
						<input type="text" class="lq-form-control" name="batch_task_sn" id="batch_task_sn">
						<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-search">
							<i class="fa fa-search">查询</i>
						</button>
					</div>
				</div>
			</div>  
			<div class="lq-row">
				<div class="lq-col-12">
					<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-all">全选</button>
					<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-reverse">反选</button>
					<button class="lq-btn lq-btn-sm lq-btn-danger" id="btn-batch-cancel">
						<i class="fa fa-times"></i>批量取消
					</button>
				</div>  
			</div>  	
		</div>
		<div id="task-table-wrap">
			<p class="bhinfo">补货类型:<span class="bhtype"></span> 补货员:<span class="bhuser"></span></p>
			<table class="lq-table" id="task-table">
			 	<thead>
					<tr>
						<th>编号</th>
						<th style="min-width: 6.5rem;">源库位</th>
						<th>商品名称</th>
						<th>渠道</th>
						<th style="min-width: 6.5rem;">商品批次号</th>
						<th style="min-width: 6.5rem;">生产日期</th>
						<th style="min-width: 8rem;">理论补货数量</th>
						<th>实际补货数量</th>
						<th>目标库位</th>
						<th>操作</th>
						<th style="min-width: 5.5rem;">取消原因</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="lq-row">
				<button class="lq-btn lq-btn-primary" id="btn-save">
					<i class="fa fa-check"></i>保存
				</button>
			</div>
		</div>
	</div>
	<script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script>
    $(document).ready(function(){
    	$(".modal-wrap").on("click",function(e){
			if ($(e.target).hasClass("modal-wrap")) {
				$(this).fadeOut();
			}
		});

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

		function callListApi (element) {
			var that = element,
    	    seachData = {
    	    	batch_task_sn:$("#batch_task_sn").val()
    		};
    		if (seachData.batch_task_sn) {
    			$.ajax({
					url : "../replenishment/list",
					type : "get",
					dataType : "json",
					data : seachData,
					beforeSend : function(){
						that.prop("disabled",true);
					},
					success : function(data) {
						that.prop("disabled",false);
						console.log(data);
						if (data.result == "success") {
							tableConstructor(data.replenishmentTaskList);
						} else {
							alert(data.note);
						}
					},
					error : function(error) {
						that.prop("disabled",false);
						alert("../replenishment/list error");
					}
				});
    		} else {
    			alert("补货批次号不能为空");
    		}
		}

    	$("#btn-search").on("click",function(e){
    		e.preventDefault();
    		callListApi($(this));
    	});

    	$("#batch_task_sn").on("keyup",function(e){
            e.preventDefault();
            if (e.which == 13 || e.which == 17) {
                callListApi($(this));
            }
        });

        $(document).on("keyup",".to_location_barcode",function(e){
        	if (e.which == 17) {
                e.preventDefault();
                var that = $(this),
                	currentCheckbox =  that.parents("tr").find(".index"),
                	nextField = that.parents("tr").next().find(".to_location_barcode"),
                	data = {
                		location_barcode : that.val()
                	};

                $.ajax({
					url : "../replenishment/checkLocationBarcode",
					type : "get",
					dataType : "json",
					data : data,
					beforeSend : function(){
						that.prop("disabled",true);
					},
					success : function(data) {
						that.prop("disabled",false);
						console.log(data);
						if (data.result == "success") {
							if (currentCheckbox.length) {
			                	currentCheckbox.prop("checked",true);
			                }
			                if (nextField.length) {
			                	nextField.focus();
			                }
						} else {
							that.focus();
							alert(data.note);
						}
					},
					error : function(error) {
						that.prop("disabled",false);
						alert("../replenishment/checkLocationBarcode error");
					}
				});
            }
        });
		function str2Date(str){
			var dt=new Date(str);
			var year=dt.getFullYear();
			var month=dt.getMonth()+1;
			var day=dt.getDate();
			if(month<10) month="0"+month;
			if(day<10) day="0"+day;
			return year+"-"+month+"-"+day;
		}
		function tableConstructor(data) {
			//console.log(data);
			if (data.length) {
				var tableHtml = '';
				for (var key in data) {
					tableHtml += '<tr class="task" taskId="'+data[key].task_id+'" goodsBarcode="'+data[key].barcode+'" goodsName="'+data[key].product_name+'" quantity="'+data[key].quantity+'"><td class="td_index">';
					if (data[key].task_status != "已完成" && data[key].task_status != "已取消") {
						tableHtml += '<label><input type="checkbox" class="index"><span></span></label>';
					}
					tableHtml += '</td><td class="td_location_barcode">'
							  + insert_flg(data[key].location_barcode) + '</td><td class="td_product_name">'
							  + data[key].product_name + '</td><td class="td_warehouse_name">'+data[key].warehouse_name+'</td><td class="td_batch_sn">'+data[key].batch_sn+'</td><td class="td_validity">'+str2Date(data[key].validity)+'</td><td class="td_quantity">'
							  + data[key].quantity + '</td><td class="td_real_quantity"><input type="text" class="lq-form-control real_quantity" readonly=true value="'
							  + data[key].quantity + '"></td><td class="td_to_location_barcode"><input type="text" class="lq-form-control to_location_barcode" value="'
							  + insert_flg(data[key].to_location_barcode) + '"></td><td class="td_act">';
					if (data[key].task_status != "已完成" && data[key].task_status != "已取消") {
						tableHtml += '<button class="btn-cancel-task lq-btn lq-btn-sm lq-btn-danger"><i class="fa fa-times"></i>取消</button>';
					}
					tableHtml += '</td><td class="td_cancel_reason">'
							  + data[key].cancel_reason + '</td></tr>';
				}
				$("#task-table tbody").html(tableHtml);
				$(".bhtype").text(data[0].task_level);
				$(".bhuser").text(data[0].last_updated_user);
				$("#task-table-wrap").fadeIn(300);
			} else {
				$("#task-table tbody").html("");
				$(".bhtype").text("");
				$(".bhuser").text("");
				$("#task-table-wrap").fadeOut(300);
			}
		}

		$("#btn-all").on("click",function(){
			var checkbox = $(".index");
			for (var i in checkbox) {
				checkbox.eq(i).prop("checked",true);
			}
		});

		$("#btn-reverse").on("click",function(){
			var checkbox = $(".index");
			for (var i in checkbox) {
				if (checkbox.eq(i).prop("checked")) {
					checkbox.eq(i).prop("checked",false);
				} else {
					checkbox.eq(i).prop("checked",true);
				}
			}
		});

		$(document).on("click","#btn-save",function(){
			var that = $(this);
			var finishData = {
				type : "finish",
				task_list : ""
			};
			var task_list = [], checkboxes = $(".index");
			for (var i=0;i<checkboxes.length;i++) {
				if (checkboxes.eq(i).prop("checked")) {
					task_list.push(checkboxes.eq(i).parents("tr").attr("taskid")+"_"+checkboxes.eq(i).parents("tr").find(".td_location_barcode").text()+"_"+checkboxes.eq(i).parents("tr").find(".to_location_barcode").val()+"_"+checkboxes.eq(i).parents("tr").find(".real_quantity").val());
				}
			}
			task_list = task_list.join(",");
			finishData.task_list = task_list;
			console.log(finishData);
			if (task_list) {
				$.ajax({
					url : "../replenishment/finishOrCancel",
					type : "get",
					dataType : "json",
					data : finishData,
					beforeSend : function(){
						that.prop("disabled",true);
					},
					success : function(data) {
						that.prop("disabled",false);
						console.log(data);
						if (data.result == "success") {
							alert("保存成功");
						} else {
							alert(data.note);
						}
						location.reload();
					},
					error : function(error) {
						that.prop("disabled",false);
						alert("../replenishment/finishOrCancel error");
					}
				});
			} else {
				alert("请勾选具体条目进行操作");
			}
		});

		$("#btn-batch-cancel").on("click",function(){
			var checked = false, checkboxes = $(".index");
			for (var i=0;i<checkboxes.length;i++) {
				if (checkboxes.eq(i).prop("checked")) {
					checked = true;
				}
			}
			if (checked) {
				$(".mw-batch-cancel").fadeIn();
			} else {
				alert("请勾选具体条目进行操作");
			}
		});

		$(document).on("click","#btn-batch-cancel-submit",function(){
			var that = $(this);
			var cancelData = {
				type : "cancel",
				task_list : "",
				cancel_reason : $("#batch_cancel_reason").val()
			};
			var task_list = [], checkboxes = $(".index");
			for (var i=0;i<checkboxes.length;i++) {
				if (checkboxes.eq(i).prop("checked")) {
					checkboxes.eq(i).parents("tr").addClass("canceled");
					task_list.push(checkboxes.eq(i).parents("tr").attr("taskid")+"_"+checkboxes.eq(i).parents("tr").find(".td_location_barcode").text());
				}
			}
			task_list = task_list.join(",");
			cancelData.task_list = task_list;
			console.log(cancelData);
			// if (task_list) {
				$.ajax({
					url : "../replenishment/finishOrCancel",
					type : "get",
					dataType : "json",
					data : cancelData,
					beforeSend : function(){
						that.prop("disabled",true);
					},
					success : function(data) {
						that.prop("disabled",false);
						console.log(data);
						if (data.result == "success") {
							alert("取消成功");
							$(".mw-cancel").fadeOut();
							$(".canceled").find(".btn-cancel-task").hide();
							$(".canceled").find(".td_cancel_reason").text($("#batch_cancel_reason").find("option:selected").text());
							$(".canceled").find(".td_index").text("");
						} else {
							alert(data.note);
						}
					},
					error : function(error) {
						that.prop("disabled",false);
						alert("../replenishment/finishOrCancel error");
					}
				});
			// } else {
			// 	alert("请勾选具体条目进行操作");
			// }
		});

		$(document).on("click",".btn-cancel-task",function(){
			$(".mw-cancel").fadeIn();
			$(this).parents("tr").addClass("editing").siblings().removeClass("editing");
			$("#cancel-task-id").val($(this).parents("tr").attr("taskId"));
		});

		$(document).on("click","#btn-cancel-submit",function(){
			var that = $(this);
			var cancelData = {
				type : "cancel",
				task_list : $("#cancel-task-id").val()+"_"+$(".editing").find(".td_location_barcode").text(),
				cancel_reason : $("#cancel_reason").val()
			}
			console.log(cancelData);
			$.ajax({
				url : "../replenishment/finishOrCancel",
				type : "get",
				dataType : "json",
				data : cancelData,
				beforeSend : function(){
					that.prop("disabled",true);
				},
				success : function(data) {
					that.prop("disabled",false);
					console.log(data);
					if (data.result == "success") {
						alert("取消成功");
						$(".mw-cancel").fadeOut();
						$(".editing").find(".btn-cancel-task").hide();
						$(".editing").find(".td_cancel_reason").text($("#cancel_reason").find("option:selected").text());
						$(".editing").find(".td_index").text("");
					} else {
						alert(data.note);
					}
				},
				error : function(error) {
					that.prop("disabled",false);
					alert("../replenishment/finishOrCancel error");
				}
			});
		});
    });
    </script>
</body>
</html>