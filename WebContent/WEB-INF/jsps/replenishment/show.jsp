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
	<title>打印补货任务单</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
	<style>
	#form-task-list {
		margin-bottom: 15px;
	}
	#form-task-list label {
		width: 5.5em;
	}
	#task-table {
		margin-top: 15px;
	}
	#page-search {
    	float: right;
    }
    .modal-wrap.mw-edit, .modal-wrap.mw-cancel, .modal-wrap.mw-username {
    	background-color: rgba(0,0,0,.5);
    	/*display: block;*/
    }
    /*.modal-wrap.mw-username{
    	display: block;
    }*/
    .mc-edit {
    	width: 360px;
    	padding: 10px 20px;
    	background-color: #fff;
    	position: absolute;
    	top: 100px;
    	left: 50%;
    	margin-left: -180px;
    	border-radius: 3px;
    }
    .mc-cancel,.mc-username {
    	width: 220px;
    	padding: 10px 20px 5px 20px;
    	background-color: #fff;
    	position: absolute;
    	top: 100px;
    	left: 50%;
    	margin-left: -180px;
    	border-radius: 3px;
    	text-align: center;
    }
    .mc-edit label {
    	width: 5em;
    }
    #btn-edit-save {
    	margin-left: 80px;
    	margin-right: 50px;
    }
    #btn-cancel-submit,#btn-username-submit {
    	display: block;
    	width: 165px;
    	margin:15px auto;
    }
    .btn-edit-task,.btn-finish-task {
    	margin:0 5px;
    }
    .btn-finish-task {
    	display: none;
    }
    #print{
        border: 0px;
        width: 0px;
        height: 0px;
    }
    .noDataTip {
    	display: none;
    	text-align: center;
    	font-size: 30px;
    	color: #666;
    	margin: 30px 0 0 0;
    }
	</style>
</head>
<body id="body-task-list">
	<iframe  src=""  id="print"></iframe>
	<div class="modal-wrap mw-edit">
		<div class="modal-content mc-edit lq-form-inline">
			<p class="lq-form-group"><label>货主:</label><span id="edit-custome"></span></p>
			<p class="lq-form-group"><label>补货类型:</label><span id="edit-type"></span></p>
			<p class="lq-form-group"><label>商品条码:</label><span id="edit-barcode"></span></p>
			<p class="lq-form-group"><label>商品名称:</label><span id="edit-goods-name"></span></p>
			<p class="lq-form-group"><label>源库位:</label><span id="edit-from"></span></p>
			<div class="lq-form-group">
				<label for="edit-to">目标库位:</label>
				<input type="text" class="lq-form-control" id="edit-to">
			</div>
			<div class="lq-form-group">
				<label for="edit-number">补货数量:</label>
				<input type="text" class="lq-form-control" id="edit-number" value="" readonly=true>
			</div>
			<input type="hidden" id="edit-task-id">
			<div class="lq-form-group">
				<button class="lq-btn lq-btn-primary" id="btn-edit-save">保存</button>
				<button class="lq-btn lq-btn-primary" id="btn-edit-cancel">取消</button>
			</div>
		</div>
	</div>
	<div class="modal-wrap mw-cancel">
		<div class="modal-content mc-cancel lq-form-inline">
			<div class="lq-form-group">
				<label for="cancel_reason">取消原因:</label>
				<select name="cancel_reason" id="cancel_reason" class="lq-form-control">
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
	<div class="modal-wrap mw-username">
		<div class="modal-content mc-username lq-form-inline">
			<div class="lq-form-group">
				<label for="user_name">工牌号:</label>
				<input type="text" class="lq-form-control" id="user_name">
			</div>
			<div class="lq-form-group">
				<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-username-submit" disabled=true>确认</button>
			</div>
		</div>
	</div>
	<div class="main-container">
		<div class="lq-form-inline" id="form-task-list">
			<div class="lq-row">
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="customer_id">货主:</label>
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
					<div class="lq-form-group">
						<label for="print_status">打印状态:</label>
						<select name="print_status" id="print_status" class="lq-form-control">
							<option value="">不限</option>
							<option value="Y">已打印</option>
							<option value="N">未打印</option>
						</select>
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="location_type">库区:</label>
						<select name="location_type" id="location_type" class="lq-form-control">
							<option value="">不限</option>
							<option value="PIECE_PICK_LOCATION">件拣货区</option>
							<option value="BOX_PICK_LOCATION">箱拣货区</option>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="task_status">任务状态:</label>
						<select name="task_status" id="task_status" class="lq-form-control">
							<option value="">不限</option>
							<option value="INIT">未处理</option>
							<option value="BINDED">已分配</option>
							<option value="IN_PROCESS">执行中</option>
							<option value="UNSHELVE">执行中(已下架)</option>
							<option value="CANCEL">已取消</option>
							<option value="N_FULFILLED">未完成</option>
							<option value="FULFILLED">已完成</option>
						</select>
					</div> 					
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="task_level">补货类型:</label>
						<select name="task_level" id="task_level" class="lq-form-control">
							<option value="3">紧急补货</option>
							<option value="1">一般补货</option>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="start_time">生成时间:</label>
						<input type="text" readonly="true" id="start_time" name="start_time" value="${start_time}" size="12" class="lq-form-control">
						<button id="startTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
							<i class="fa fa-calendar"></i>
						</button> 
					</div>
				</div>
				<div class="lq-col-3">
					
					<!-- <div class="lq-form-group">
						<label for="batch_task_sn">补货批次号:</label>
						<input type="text" class="lq-form-control" name="batch_task_sn" id="batch_task_sn">
					</div> -->
					<div class="lq-form-group">
						<label for="batch_pick_sn">波次单号:</label>
						<input type="text" class="lq-form-control" name="batch_pick_sn" id="batch_pick_sn">
				    </div>
					<div class="lq-form-group">
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
				<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-print">打印选中补货任务单</button>
			</div>  
		</div> 
		<table class="lq-table" id="task-table">
		 	<thead>
				<tr>
					<th>编号</th>
					<th>货主</th>
					<th>商品条码</th>
					<th>商品名称</th>
					<th>理论补货数量</th>
					<th>补货状态</th>
					<th>生成时间</th>
					<th>源库位</th>
					<th>目标库位</th>
					<th>补货批次号</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
		<div class="noDataTip">未查到符合条件的数据!</div>
		<div class="page-wrap"></div>
	</div>
	<script type="text/javascript" src="../static/js/zapatec/utils/zapatec.js"></script>
    <script type="text/javascript" src="../static/js/zapatec/zpcal/src/calendar.js"></script>
    <script type="text/javascript" src="../static/js/zapatec/zpcal/lang/calendar-en.js"></script>
	<script type="text/javascript">
		Zapatec.Calendar.setup({
			weekNumbers       : false,
			electric          : false,
			inputField        : "start_time",
			button            : "startTrigger",
			ifFormat          : "%Y-%m-%d",
			daFormat          : "%Y-%m-%d"
		});
	</script>
	<script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script>
    $(document).ready(function(){
    	var TOTAL_PAGE = 0, START_PAGE = 1, MAX_PAGE = 15, END_PAGE = 15, CURRENT_PAGE = 1, PAGE_DATA = {};

    	$(".modal-wrap").on("click",function(e){
			if ($(e.target).hasClass("modal-wrap")) {
				$(this).fadeOut();
			}
		});

    	$("#btn-search").on("click",function(e){
    		e.preventDefault();
    		var that = $(this),
    	    seachData = {
    	    	customer_id:$("#customer_id").val(),
    	    	location_type:$("#location_type").val(),
    	    	task_level:$("#task_level").val(),
    	    	start_time:$("#start_time").val(),
    	    	print_status:$("#print_status").val(),
    	    	task_status:$("#task_status").val(),
    	    	batch_pick_sn:$("#batch_pick_sn").val()
    	    	// batch_task_sn:$("#batch_task_sn").val()
    		};
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
						$('.noDataTip').fadeOut(300);
						if (!data.replenishmentTaskList.length) {
							$('.noDataTip').fadeIn(300);
						}
						PAGE_DATA = data.page;
						TOTAL_PAGE = parseInt(data.page.totalPage);
						START_PAGE = 1;
						if (TOTAL_PAGE < MAX_PAGE) {
							END_PAGE = TOTAL_PAGE;
						} else {
							END_PAGE = MAX_PAGE;
						}
						CURRENT_PAGE = data.page.currentPage;
						pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
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
    	});

    	function pageConstructor(currentPage,startPage,endPage) {
			var currentPage = currentPage,
				startPage = startPage,
			    endPage = endPage;
			//alert("startPage:"+startPage+",endPage:"+endPage);
			if (TOTAL_PAGE) {
				$(".page-wrap").html("").fadeIn(300);
				var pageHtml = '<div class="lq-row"><nav id="page-search"><ul class="lq-page-control">';
				if (startPage !== 1) {
					pageHtml +='<li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>';
				}
				for (var i=startPage;i<startPage+endPage;i++) {
					pageHtml += '<li';
					if (i == currentPage) {
						pageHtml += ' class="active"';
					}
					pageHtml += '><a href="#">' + i + '</a></li>';
				}
				if (startPage+endPage-1 !== TOTAL_PAGE) {
					pageHtml += '<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li>';
				}
				pageHtml += '</ul></nav></div>';
				$(".page-wrap").html(pageHtml);
			} else {
				$(".page-wrap").html("").fadeOut(300);
			}
		}

		function tableConstructor(data) {
			//console.log(data);
			if (data.length) {
				var tableHtml = '';
				for (var key in data) {
					tableHtml += '<tr class="task" taskId="'+data[key].task_id+'" goodsBarcode="'+data[key].barcode+'" goodsName="'+data[key].product_name+'" quantity="'+data[key].quantity+'"><td>';
					if (data[key].task_status == "未处理") {
						tableHtml += '<label><input type="checkbox" class="index"><span></span></label>';
					}
					tableHtml += '</td><td class="td_name">'
							  + data[key].name + '</td><td class="td_barcode">'
							  + data[key].barcode + '</td><td class="td_product_name">'
							  + data[key].product_name + '</td><td class="td_quantity">'
							  + data[key].quantity + '</td><td>'
							  + data[key].task_status + '</td><td class="td_created_time">'
							  + data[key].created_time + '</td><td class="td_location_barcode">'
							  + insert_flg(data[key].location_barcode) + '</td><td class="td_to_location_barcode">'
							  + insert_flg(data[key].to_location_barcode) + '</td><td>'
							  + data[key].batch_task_sn + '</td></tr>';
							  // + '<td class="td_act">';
					// if (data[key].task_status != "已完成" && data[key].task_status != "已取消") {
					// 	tableHtml += '<button class="btn-edit-task lq-btn lq-btn-sm lq-btn-primary"><i class="fa fa-edit"></i>编辑</button><button class="btn-finish-task lq-btn lq-btn-sm lq-btn-primary"><i class="fa fa-check"></i>完成</button><button class="btn-cancel-task lq-btn lq-btn-sm lq-btn-danger"><i class="fa fa-times"></i>取消</button>';
					// }
					// tableHtml += '<<td class="td_cancel_reason">'
					// 		  + data[key].cancel_reason + '</td>';
				}
				$("#task-table tbody").html(tableHtml);
				// $("#task-table").fadeIn(300);
			} else {
				$("#task-table tbody").html("");
				// $("#task-table").fadeOut(300);
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

		$("#body-task-list").on("click",".lq-page-control li",function(){
			var that = $(this);
			if (!that.hasClass("prev") && !that.hasClass("next")) {
				if (!that.hasClass("active")) {
					var index = parseInt($(this).find("a").text());
					CURRENT_PAGE = index;
					var searchData = {
						currentPage : index,
						pageSize : MAX_PAGE,
						customer_id:$("#customer_id").val(),
		    	    	location_type:$("#location_type").val(),
		    	    	task_level:$("#task_level").val(),
		    	    	start_time:$("#start_time").val(),
		    	    	print_status:$("#print_status").val(),
		    	    	batch_pick_sn:$("#batch_pick_sn").val(),
		    	    	task_status:$("#task_status").val()
		    	    	// batch_task_sn:$("#batch_task_sn").val()
					};
					$.ajax({
						url : "../replenishment/list",
						type : "get",
						dataType : "json",
						data : searchData,
						beforeSend : function(){
							that.addClass("active").siblings().removeClass("active");
						},
						success : function(data) {
							tableConstructor(data.replenishmentTaskList);
						},
						error : function(error) {
							alert("../replenishment/list error");
						}
					});
				}
			} else if (that.hasClass("next")) {
				START_PAGE = parseInt(that.prev().find('a').text()) + 1;
				var REST_PAGE = TOTAL_PAGE - parseInt(that.prev().find('a').text()),
				    END_PAGE = 0;
				if (REST_PAGE >= 0 && REST_PAGE <= MAX_PAGE) {
					END_PAGE = REST_PAGE;
					pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE); 
				} else if (REST_PAGE > MAX_PAGE) {
					END_PAGE = MAX_PAGE;
					pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
				}
				
			} else if (that.hasClass("prev")) {
				var END_PAGE = MAX_PAGE;
				START_PAGE = parseInt(that.next().find('a').text()) - MAX_PAGE;
				//alert("END_PAGE:"+END_PAGE+",START_PAGE:"+START_PAGE);
				pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
			}
		});

		$(document).on("click",".btn-edit-task",function(){
			var thisTd = $(this).parent();
			thisTd.parent("tr").addClass("editing").siblings().removeClass("editing");
			$(".mw-edit").fadeIn();
			$("#edit-task-id").val(thisTd.parent("tr").attr("taskId"));
			$("#edit-custome").text(thisTd.siblings(".td_name").text());
			$("#edit-type").text(thisTd.siblings(".td_task_level").text());
			$("#edit-barcode").text(thisTd.parent("tr").attr("goodsBarcode"));
			$("#edit-goods-name").text(thisTd.parent("tr").attr("goodsName"));
			$("#edit-from").text(thisTd.siblings(".td_location_barcode").text());
			$("#edit-to").val(thisTd.siblings(".td_to_location_barcode").text());
			$("#edit-number").val(thisTd.parent("tr").attr("quantity"));
		});

		$(document).on("click","#btn-edit-save",function(){
			var that = $(this),
				saveData = {
					task_id : $("#edit-task-id").val(),
					to_location_barcode : $("#edit-to").val(),
					from_location_barcode : $("#edit-from").text()
				};
			$.ajax({
				url : "../replenishment/updateReplenishment",
				type : "get",
				dataType : "json",
				data : saveData,
				beforeSend : function(){
					that.prop("disabled",true);
				},
				success : function(data) {
					that.prop("disabled",false);
					console.log(data);
					if (data.result == "success") {
						$(".mw-edit").fadeOut();
						$(".editing").find(".td_to_location_barcode").text(saveData.to_location_barcode);
						$(".editing").find(".btn-edit-task").hide();
						$(".editing").find(".btn-finish-task").show();
					} else {
						alert(data.note);
					}
				},
				error : function(error) {
					that.prop("disabled",false);
					alert("../replenishment/updateReplenishment error");
				}
			});
		});

		$(document).on("click",".btn-finish-task",function(){
			var that = $(this);
			if (that.parents("tr").find(".td_to_location_barcode").text()) {
				var finishData = {
					type : "finish",
					task_id : that.parents("tr").attr("taskId")
				};
				// console.log(finishData);
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
							alert("完成成功");
							that.hide();
							that.next().hide();
							that.parents("tr").find(".td_task_status").text("已完成");
						} else {
							alert(data.note);
						}
					},
					error : function(error) {
						that.prop("disabled",false);
						alert("../replenishment/finishOrCancel error");
					}
				});
			} else {
				alert("没有目标库位");
			}
		});

		$(document).on("click","#btn-edit-cancel",function(){
			$(".mw-edit").fadeOut();
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
				task_id : $("#cancel-task-id").val(),
				cancelReason : $("#cancel_reason").val()
			}
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
						$(".editing").find(".btn-edit-task").hide();
						$(".editing").find(".btn-cancel-task").hide();
						$(".editing").find(".btn-finish-task").hide();
						$(".editing").find(".td_task_status").text("已取消");
						$(".editing").find(".td_cancel_reason").text($("#cancel_reason").find("option:selected").text());
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

		$("#btn-print").on("click",function(){
			$(".mw-username").fadeIn(function(){
				$("#user_name").focus().val("");
				$("#btn-username-submit").prop("disabled",true);
			});
		});

		$("#user_name").on("keyup",function(e){
			e.preventDefault();
			var that = $(this), data = {
				user_name : that.val()
			};
            if (e.which == 13 || e.which == 17) {
                $.ajax({
					url : "../replenishment/checkUser",
					type : "get",
					dataType : "json",
					data : data,
					beforeSend : function(){
						that.prop("readonly",true);
					},
					success : function(data) {
						that.prop("readonly",false);
						//console.log(data);
						if (data.result == "success") {
							$("#btn-username-submit").prop("disabled",false);
						} else {
							$("#btn-username-submit").prop("disabled",true);
							alert(data.note);
							that.val("");
						}
					},
					error : function(error) {
						that.prop("readonly",false);
						alert("../replenishment/checkUser error");
					}
				});
            }
		});

		$("#btn-username-submit").on("click",function(){
			var user_name = $("#user_name").val();
			if (user_name) {
				var task_id_list = [], checkboxes = $(".index");
				for (var i=0;i<checkboxes.length;i++) {
					if (checkboxes.eq(i).prop("checked")) {
						checkboxes.eq(i).addClass("printed");
						task_id_list.push(checkboxes.eq(i).parents("tr").attr("taskid"));
					}
				}
				task_id_list = task_id_list.join(","); 
				src="../replenishment/printReplenishment?task_id_list="+task_id_list+"&user_name="+user_name;
	            $('#print').attr('src',src); 
	            $(".printed").remove();
	            $(".mw-username").fadeOut();
			} else {
				alert("请输入工牌号");
			}
		});
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
    });
    </script>
</body>
</html>