<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<title>盘点设置查询</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
    <style type="text/css">
    #form-job-search {
		margin-bottom: 15px;
	}
	#job-table {
		margin-top: 15px;
	}
	#page-search {
    	float: right;
    }
    .modal-wrap.mw-apply {
    	background-color: rgba(0,0,0,.5);
    	/*display: block;*/
    }
    .mc-apply {
    	width: 500px;
    	padding: 10px;
    	background-color: #fff;
    	position: absolute;
    	top: 100px;
    	left: 50%;
    	margin-left: -250px;
    	border-radius: 3px;
    }
    .btn-start-job {
    	display: block;
    	margin: 5px 0;
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
<body id="body-jobindex">
	<div class="modal-wrap mw-apply">
		<div class="modal-content mc-apply">
			<div class="lq-tab-wrap">
				<ul class="lq-tab-nav">
					<li class="active">标准盘点</li>
					<li>循环盘点</li>
					<li>动态盘点</li>
				</ul>
				<div class="lq-tab-content-wrap">
					<div class="lq-tab-content active lq-form-inline">
						<div class="lq-form-group">
							<label>
								<input type="checkbox" id="apply_hide_real_num" value="0" checked=true>
								<span>隐藏理论库存、盘盈/盘亏数量</span>
							</label>
							<label>
								<input type="checkbox" id="apply_hide_batch_sn" value="0" checked=true>
								<span>忽略批次属性</span>
							</label>
						</div>
						<div class="lq-form-group">
							<label for="apply_customer_id">　　货主:</label>
							<select name="apply_customer_id" id="apply_customer_id" class="lq-form-control">
								<option value="0">不限</option> 
								<c:forEach items="${customers}" var="customer">
									<option name="customer_id"
										class="${customer.customer_id}"
										<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
										value="${customer.customer_id}">${customer.name}</option>
								</c:forEach>
							</select>
						</div>
						<!-- <div class="lq-form-group">
							<label for="apply_start_time">开始时间:</label>
							<input type="text" id="apply_start_time" class="lq-form-control" readonly=true>
							<button id="astTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
								<i class="fa fa-calendar"></i>
							</button>
						</div>
						<div class="lq-form-group">
							<label for="apply_start_time">结束时间:</label>
							<input type="text" id="apply_end_time" class="lq-form-control" readonly=true>
							<button id="aetTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
								<i class="fa fa-calendar"></i>
							</button>
						</div> -->
						<div class="lq-form-group">
							<label for="apply_location_type">　　库区:</label>
							<select name="apply_location_type" id="apply_location_type" class="lq-form-control">
								<option value="PICKANDSTOCK">拣货区+存储区</option>
								<option value="PICK_LOCATION">拣货区</option>
								<option value="STOCK_LOCATION">存储区</option>
								<option value="DEFECTIVE_LOACTION">二手区</option>
								<option value="PACKBOX_LOCATION">耗材区</option>
								<option value="PACKBOX_PICK_LOCATION">耗材拣货区</option>
								<option value="QUALITY_CHECK_LOCATION">质检区</option>
								<option value="RETURN_LOCATION">RETURN区</option>
							</select>
						</div>
						<div class="lq-form-group">
							<label for="apply_barcode">商品条码:</label>
							<input type="text" id="apply_barcode" class="lq-form-control" disabled=true>
						</div>
						<div class="lq-form-group">
							<label for="apply_goods_name">商品名称:</label>
							<input type="text" id="apply_goods_name" class="lq-form-control" readonly=true>
						</div>
						<div class="lq-form-group">
							<label for="apply_from_location_barcode">起始库位:</label>
							<input type="text" id="apply_from_location_barcode" class="lq-form-control">
						</div>
						<div class="lq-form-group">
							<label for="apply_to_location_barcode">结束库位:</label>
							<input type="text" id="apply_to_location_barcode" class="lq-form-control">
						</div>
						<div class="lq-form-group">
							<button class="lq-btn lq-btn-primary" id="btn-normal-apply">
								<i class="fa fa-check"></i>生成盘点任务
							</button>
						</div>
					</div>
					<div class="lq-tab-content lq-form-inline">
						循环盘点
					</div>
					<div class="lq-tab-content lq-form-inline">
						动态盘点
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="main-container">
		<div class="lq-form-inline" id="form-job-search">
			<div class="lq-row">
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="customer_id">货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
							<option value="0">不限</option> 
							<c:forEach items="${customers}" var="customer">
								<option name="customer_id"
									class="${customer.customer_id}"
									<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
									value="${customer.customer_id}">${customer.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="status">状态:</label>
						<select name="status" id="status" class="lq-form-control">
							<option value="">不限</option>
							<option value="INIT">未开始</option>
							<option value="FULFILLED">已完成</option>
							<option value="CANCEL">已取消</option>
						</select>
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="task_type">盘点类型:</label>
						<select name="task_type" id="task_type" class="lq-form-control">
							<option value="NORMAL">标准盘点</option>
							<option value="">循环盘点</option>
							<option value="">动态盘点</option>
						</select>
					</div>
					<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-search">
						<i class="fa fa-search"></i>查询
					</button>
					<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-open-apply">
						<i class="fa fa-hand-paper-o"></i>申请盘点任务
					</button>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="time">生成时间:</label>
						<input type="text" readonly="true" id="time" name="time" value="${time}" size="12" class="lq-form-control">
						<button id="timeTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
							<i class="fa fa-calendar"></i>
						</button> 
					</div>
				</div>
			</div>  
		</div>
		<table class="lq-table" id="job-table">
		 	<thead>
				<tr>
					<th>盘点任务号</th>
					<th>货主</th>
					<th>库区</th>
					<th>库位</th>
					<th>隐藏理论库存、差异数量</th>
					<th>忽略批次属性</th>
					<th>当前生成任务数量</th>
					<th>当前状态</th>
					<th>操作人</th>
					<th>申请时间</th>
					<th>操作</th>
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
			inputField        : "time",
			button            : "timeTrigger",
			ifFormat          : "%Y-%m-%d",
			daFormat          : "%Y-%m-%d"
		});
		// Zapatec.Calendar.setup({
		// 	weekNumbers       : false,
		// 	electric          : false,
		// 	inputField        : "apply_start_time",
		// 	button            : "astTrigger",
		// 	ifFormat          : "%Y-%m-%d",
		// 	daFormat          : "%Y-%m-%d"
		// });
		// Zapatec.Calendar.setup({
		// 	weekNumbers       : false,
		// 	electric          : false,
		// 	inputField        : "apply_end_time",
		// 	button            : "aetTrigger",
		// 	ifFormat          : "%Y-%m-%d",
		// 	daFormat          : "%Y-%m-%d"
		// });
	</script>
	<script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
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

			var TOTAL_PAGE = 0, START_PAGE = 1, MAX_PAGE = 15, END_PAGE = 15, CURRENT_PAGE = 1, PAGE_DATA = {};

	    	$(".modal-wrap").on("click",function(e){
				if ($(e.target).hasClass("modal-wrap")) {
					$(this).fadeOut();
				}
			});

			$(".lq-tab-nav li").on("click",function(){
				var index = $(this).index();
				$(this).addClass("active").siblings().removeClass("active");
				$(".lq-tab-content").eq(index).addClass("active").siblings().removeClass("active");
			});

	    	$("#btn-search").on("click",function(e){
	    		e.preventDefault();
	    		var trigger = $(this);
	    		ajaxQueryJob(trigger);
	    	});
			
			function ajaxQueryJob (trigger) {
				var searchData = {
	    	    	time : $("#time").val(),
	    	    	status : $("#status").val(),
	    	    	customer_id : $("#customer_id").val(),
	    	    	task_type : $("#task_type").val()
	    		};
	    		console.log(searchData);
	    		$.ajax({
					url : "../countTask/queryJob",
					type : "get",
					dataType : "json",
					data : searchData,
					beforeSend : function(){
						if (trigger) {
							trigger.prop("disabled",true);
						}
					},
					success : function(data) {
						if (trigger) {
							trigger.prop("disabled",false);
						}
						console.log(data);
						if (data.success) {
							$('.noDataTip').hide();
							if (!data.list.length) {
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
							tableConstructor(data.list);
						} else {
							alert(data.message);
						}
					},
					error : function(error) {
						if (trigger) {
							trigger.prop("disabled",false);
						}
						alert("../countTask/queryJob error");
					}
				});
			}

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

			$("#body-jobindex").on("click",".lq-page-control li",function(){
				var that = $(this);
				if (!that.hasClass("prev") && !that.hasClass("next")) {
					if (!that.hasClass("active")) {
						var index = parseInt($(this).find("a").text());
						CURRENT_PAGE = index;
						var searchData = {
							currentPage : index,
							pageSize : MAX_PAGE,
							time : $("#time").val(),
			    	    	status : $("#status").val(),
			    	    	customer_id : $("#customer_id").val(),
			    	    	task_type : $("#task_type").val()
						};
						$.ajax({
							url : "../countTask/queryJob",
							type : "get",
							dataType : "json",
							data : searchData,
							beforeSend : function(){
								that.addClass("active").siblings().removeClass("active");
							},
							success : function(data) {
								if (data.success) {
									tableConstructor(data.list);
								} else {
									alert(data.message);
								}
							},
							error : function(error) {
								alert("../countTask/queryJob error");
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
			
			function changeTimeFormat(d){
				var date = new Date (d) ,
				    time = date.toString().split(' ')[4];
				return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() +" "+ time;
			}
 
			function tableConstructor(data) {
				//console.log(data);
				if (data.length) {
					var tableHtml = '';
					for (var key in data) {
						tableHtml += '<tr queue_id="'+ data[key].queue_id +'"><td>'
								+ data[key].count_sn + '</td><td>' + data[key].customer_name + '</td><td>'
								+ getLocationChinese(data[key].location_type) + '</td><td>'
								+ insert_flg(data[key].from_location_barcode) + ' / ' + insert_flg(data[key].to_location_barcode) + '</td><td>'
								+ (data[key].hide_real_num == 0 ? '是' : '否') + '</td><td>'
								+ (data[key].hide_batch_sn == 0 ? '是' : '否') + '</td><td class="td_num">'
								+ data[key].num + '</td><td class="td_status">';
							if (data[key].status == "INIT") {
								tableHtml += '未开始';
							}
							if (data[key].status == "CANCEL") {
								tableHtml += '已取消';
							}
							if (data[key].status == "FULFILLED") {
								tableHtml += '已完成';
							}
							tableHtml += '</td><td>'
								+ data[key].created_user + '</td><td>'
								+ changeTimeFormat(data[key].created_time) + '</td><td>';
							if (data[key].status == "INIT") {
								tableHtml += '<button class="btn-start-job lq-btn lq-btn-sm lq-btn-primary"><i class="fa fa-check"></i>开始执行</button>';
							}
							if (data[key].status == "INIT" || data[key].status == "FULFILLED") {
								tableHtml += '<button class="btn-cancel-job lq-btn lq-btn-sm lq-btn-danger"><i class="fa fa-times"></i>取&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;消</button>';
							}
							tableHtml += '</td></tr>';
					}
					$("#job-table tbody").html(tableHtml);
				} else {
					$("#job-table tbody").html("");
				}
			}

			$("#btn-open-apply").on("click",function(){
				$(".mw-apply").fadeIn();
			});

			$("#apply_customer_id").on("change",function(){
				if ($(this).val() == "0") {
					$("#apply_barcode").val("").attr("disabled",true);
				} else {
					$("#apply_barcode").attr("disabled",false);
				}
			});

			$("#apply_barcode").on("keyup",function(e){
				e.preventDefault();
				if (e.which == 13 || e.which == 17) {
					var that = $(this), 
						data = {
							customer_id : $("#apply_customer_id").val(),
							barcode : that.val()
						};
	                $.ajax({
						url : "../replenishmentQuery/getNameByBarcode",
						type : "get",
						dataType : "json",
						data : data,
						beforeSend : function(){
							that.prop("disabled",true);
						},
						success : function(data) {
							that.prop("disabled",false);
							// console.log(data);
							if (data.result == 'success') {
								$("#apply_goods_name").val(data.note);
							} else {
								that.val("").focus();
								$("#apply_goods_name").val("");
								alert(data.note);
							}
						},
						error : function(error) {
							that.prop("disabled",false);
							alert("../replenishmentQuery/getNameByBarcode error");
						}
					});	
	            }
			});

			$(document).on("click","#btn-normal-apply",function(){
				var that = $(this);
				var normalData = {
					task_type : "NORMAL",
					hide_real_num : 0,
					hide_batch_sn : 0,
					customer_id : $("#apply_customer_id").val(),
					location_type : $("#apply_location_type").val(),
					barcode : $("#apply_barcode").val(),
					from_location_barcode : $("#apply_from_location_barcode").val(),
					to_location_barcode : $("#apply_to_location_barcode").val()
				};
				if (! $("#apply_hide_real_num").prop("checked")) {
					normalData.hide_real_num = 1;
				}
				if (! $("#apply_hide_batch_sn").prop("checked")) {
					normalData.hide_batch_sn = 1;
				}	
				console.log(normalData);	
				$.ajax({
					url : "../countTask/addJob",
					type : "get",
					dataType : "json",
					data : normalData,
					beforeSend : function(){
						that.prop("disabled",true);
					},
					success : function(data) {
						that.prop("disabled",false);
						console.log(data);
						if (data.success) {
							$(".mw-apply").fadeOut();
							ajaxQueryJob();
							alert("申请成功");
						} else {
							alert(data.message);
						}
					},
					error : function(error) {
						that.prop("disabled",false);
						alert("../countTask/addJob error");
					}
				});	
			});

			$(document).on("click",".btn-start-job",function(){
				var that = $(this),
					startData = {
						queue_id : that.parents("tr").attr("queue_id")
					};
				$.ajax({
					url : "../countTask/startJob",
					type : "get",
					dataType : "json",
					data : startData,
					beforeSend : function(){
						that.prop("disabled",true);
					},
					success : function(data) {
						that.prop("disabled",false);
						console.log(data);
						if (data.success) {
							alert("开始成功");
							that.hide();
							that.parents("tr").find(".td_status").text("已完成");
							that.parents("tr").find(".td_num").text(data.num);
						} else {
							alert(data.message);
						}
					},
					error : function(error) {
						that.prop("disabled",false);
						alert("../countTask/startJob error");
					}
				});	
			});

			$(document).on("click",".btn-cancel-job",function(){
				var that = $(this),
					cancelData = {
						queue_id : that.parents("tr").attr("queue_id")
					};
				$.ajax({
					url : "../countTask/cancelJob",
					type : "get",
					dataType : "json",
					data : cancelData,
					beforeSend : function(){
						that.prop("disabled",true);
					},
					success : function(data) {
						that.prop("disabled",false);
						console.log(data);
						if (data.success) {
							alert("取消成功");
							that.hide();
							that.parents("tr").find(".td_status").text("已取消");
						} else {
							alert(data.message);
						}
					},
					error : function(error) {
						that.prop("disabled",false);
						alert("../countTask/cancelJob error");
					}
				});	
			});

			// 获取库区中文名
			function getLocationChinese (loc) {

				var locEArr = ['PICKANDSTOCK', 'PICK_LOCATION', 'STOCK_LOCATION', 'DEFECTIVE_LOACTION', 'PACKBOX_LOCATION','PACKBOX_PICK_LOCATION', 'RETURN_LOCATION','VARIANCE_MINUS_LOCATION','VARIANCE_ADD_LOCATION','PIECE_PICK_LOCATION','BOX_PICK_LOCATION'],
					locCArr = ['拣货区+存储区', '拣货区', '存储区', '二手区', '耗材区','耗材拣货区' ,'退货区','初始化盘亏','初始化盘盈','件拣货区','箱拣货区'],

					index;
				for( var i in locEArr) {
					if (locEArr[i] == loc.toUpperCase()) {
						index = i
					} 
				}
				return locCArr[index];
			}
		});
	</script>
</body>
</html>