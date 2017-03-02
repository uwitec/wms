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
    #form-searchjob {
		margin-bottom: 15px;
	}
	#searchjob-table {
		margin-top: 15px;
	}
	#page-search {
    	float: right;
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
<body id="body-searchjob">
	<div class="main-container">
		<div class="lq-form-inline" id="form-searchjob">
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
						<label for="count_sn">盘点任务号:</label>
						<input type="text" id="count_sn" name="count_sn" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="task_id">盘点任务ID:</label>
						<input type="text" id="task_id" name="task_id" class="lq-form-control">
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="barcode">商品条码:</label>
						<input type="text" id="barcode" name="barcode" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="product_name">商品名称:</label>
						<input type="text" id="product_name" name="product_name" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="status">任务状态:</label>
						<select name="status" id="status" class="lq-form-control">
							<option value="">不限</option>
							<option value="INIT">初始化</option>
							<option value="CANCEL">已取消</option>
							<option value="FULFILLED">已完成</option>
							<option value="ON_FIRST">初盘中</option>
							<option value="OVER_FIRST">初盘已回单</option>
							<option value="ON_SECOND">复盘中</option>
							<option value="OVER_SECOND">复盘已回单</option>
							<option value="ON_THIRD">三盘中</option>
						</select>
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="task_type">盘点类型:</label>
						<select name="task_type" id="task_type" class="lq-form-control">
							<option value="NORMAL">标准盘点</option>
							<option value="CYCLE">循环盘点</option>
							<option value="DYNAMIC">动态盘点</option>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="time">申请时间:</label>
						<input type="text" readonly="true" id="time" name="time" value="${time}" size="12" class="lq-form-control">
						<button id="timeTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
							<i class="fa fa-calendar"></i>
						</button> 
					</div>
					<div class="lq-form-group">
						<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-search">
							<i class="fa fa-search"></i>查询
						</button>
					</div>
				</div>
			</div>  
		</div>
		<table class="lq-table" id="searchjob-table">
		 	<thead>
				<tr>
					<th>盘点任务号</th>
					<th>盘点任务ID</th>
					<th>盘点类型</th>
					<th>货主</th>
					<th>商品条码</th>
					<th>商品名称</th>
					<th>盘点库位</th>
					<th>生产日期</th>
					<th>批次号</th>
					<th>商品属性</th>
					<th>理论数量</th>
					<th>初盘数量</th>
					<th>二盘数量</th>
					<th>三盘数量</th>
					<th>差异数据</th>
					<th>任务状态</th>
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
	</script>
	<script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			var TOTAL_PAGE = 0, START_PAGE = 1, MAX_PAGE = 15, END_PAGE = 20, CURRENT_PAGE = 1, PAGE_DATA = {};

	    	$("#btn-search").on("click",function(e){
	    		e.preventDefault();
	    		var trigger = $(this);
	    		ajaxQueryJob(trigger);
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
			
			function ajaxQueryJob (trigger) {
				var searchData = {
	    	    	customer_id : $("#customer_id").val(),
	    	    	barcode : $("#barcode").val(),
	    	    	product_name : $("#product_name").val(),
	    	    	task_type : $("#task_type").val(),
	    	    	count_sn : $("#count_sn").val(),
	    	    	task_id : ($("#task_id").val() == "") ? 0 : $("#task_id").val(),
	    	    	time : $("#time").val(),
	    	    	status : $("#status").val()
	    		};
	    		console.log(searchData);
	    		$.ajax({
					url : "../countTask/query",
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
						alert("../countTask/query error");
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

			$("#body-searchjob").on("click",".lq-page-control li",function(){
				var that = $(this);
				if (!that.hasClass("prev") && !that.hasClass("next")) {
					if (!that.hasClass("active")) {
						var index = parseInt($(this).find("a").text());
						CURRENT_PAGE = index;
						var searchData = {
							currentPage : index,
							pageSize : MAX_PAGE,
							customer_id : $("#customer_id").val(),
			    	    	barcode : $("#barcode").val(),
			    	    	product_name : $("#product_name").val(),
			    	    	task_type : $("#task_type").val(),
			    	    	count_sn : $("#count_sn").val(),
			    	    	task_id : ($("#task_id").val() == "") ? 0 : $("#task_id").val(),
			    	    	time : $("#time").val(),
			    	    	status : $("#status").val()
						};
						$.ajax({
							url : "../countTask/query",
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
								alert("../countTask/query error");
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

			function tableConstructor(data) {
				//console.log(data);
				if (data.length) {
					var tableHtml = '';
					for (var key in data) {
						// 根据状态显示表单信息
						// tabMsgArr = ['', '/', data[key].num_first],
						var 	stateArr = ['未开始', '已取消', '初盘中', '初盘已回单', '复盘中', '复盘已回单', '中盘中', '已完成'],
							num_first, num_second, num_third, num_dif;
						function first (state) {
							if (state == stateArr[0] || state == stateArr[1] || state == stateArr[2]) {
								num_first = '';
							} else{
								num_first = data[key].num_first;
							}
							return num_first;
						}
						function second (state) {
							if (state == stateArr[0] || state == stateArr[1] || state == stateArr[2] || (state == stateArr[7] && data[key].mark == 1)) {
								num_second = '/';
							} else if (state == stateArr[3] || state == stateArr[4]) {
								num_second = '';
							} else {
								num_second = data[key].num_second;
							}
							return num_second;
						}
						function third (state) {
							if (state == stateArr[5] || state == stateArr[6]) {
								num_third = '';
							} else if (state == stateArr[7] && data[key].mark == 3) {
								num_third = data[key].num_third;
							} else {
								num_third = '/';
							}
							return num_third;
						}
						function diff (state) {
							if (state == stateArr[0] || state == stateArr[1] || state == stateArr[2]) {
								num_dif = '';
							} else{
								num_dif = data[key].num_dif;
							}
							return num_dif;
						}
						tableHtml += '<tr><td>' + data[key].count_sn + '</td><td>'
						+ data[key].task_id + '</td><td>'
						+ data[key].task_type + '</td><td>'
						+ data[key].customer_name + '</td><td>'
						+ data[key].barcode + '</td><td>'
						+ data[key].product_name + '</td><td class="td_location_barcode">'
						+ data[key].location_barcode + '</td><td>'
						+ (data[key].hide_batch_sn == 0 ? '/' : data[key].validity.substring(0,10)) + '</td><td>'
						+ data[key].batch_sn + '</td><td>'
						+ data[key].product_status + '</td><td>'
						+ data[key].num_real + '</td><td>'
						+ first(data[key].status) + '</td><td>'
						+ second(data[key].status) + '</td><td>'
						+ third(data[key].status) + '</td><td>'
						+ diff(data[key].status) + '</td><td>'
						+ data[key].status + '</td></tr>';
					}
					$("#searchjob-table tbody").html(tableHtml);
					for (var i in $(".td_location_barcode")) {
						$(".td_location_barcode").eq(i).text(insert_flg($(".td_location_barcode").eq(i).text()));
					}
				} else {
					$("#searchjob-table tbody").html("");
				}
			}			
		});
	</script>
</body>
</html>