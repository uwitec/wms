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
	<title>手动生成补货任务</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
	<style>
	.customer-list {
		display: block;
		float: left;
		overflow: hidden;
		margin-bottom: 10px;
	}
	.customer-list li {
		float: left;
	}
	.checkbox-wrap {
		display: block;
		margin:1px 5px;
	}
	#startTrigger,#endTrigger {
		margin-right: 20px;
	}
	#manul-table {
		display: none;
	}
	#page-search {
    	float: right;
    }
	</style>
</head>
<body id="body-manul">
	<div class="main-container">
		<div class="lq-row">
			<div class="lq-col-12">
				<div class="lq-tab-wrap">
					<ul class="lq-tab-nav">
						<li class="active">参数设置</li>
						<li>补货任务列表</li>
					</ul>
					<div class="lq-tab-content-wrap">
						<div class="lq-tab-content active">
							<div class="lq-row">
								<p style="float:left;margin:0;">货主:</p>
								<ul class="customer-list">
									<li>
										<label class="checkbox-wrap"><input type="checkbox" class="customer all" name="customer" value="0"><span>不限</span></label>
									</li>
									<c:forEach items="${customers}" var="customer">
										<li>
											<label class="checkbox-wrap"><input type="checkbox" class="customer" name="customer" value="${customer.customer_id}"><span>${customer.name}</span></label>
										</li>
									</c:forEach>
								</ul>
							</div>
							<form class="lq-form-inline">
								<div class="lq-row">
									<div class="lq-form-group">
										<lable for="box_piece">选择库区:</lable>
										<select name="box_piece" id="box_piece" class="lq-form-control">
											<option value="BOX_PIECE">不限</option>
											<option value="PIECE">件拣货区</option>
											<option value="BOX">箱拣货区</option>
										</select>
									</div>
								</div>
								<div class="lq-row">
									<div class="lq-form-group">
										<lable>补货类型:</lable>
										<select name="task_level" id="task_level" class="lq-form-control">
											<option value="1">一般补货</option>
										</select>
									</div>
								</div>
								<div class="lq-row">
									<div class="lq-form-group">
										<lable for="barcode">商品条码:</lable>
										<input type="text" id="barcode" class="lq-form-control" readonly=true>
									</div>
								</div>
								<div class="lq-row">
									<div class="lq-form-group">
										<lable for="goods_name">商品名称:</lable>
										<input type="text" id="goods_name" class="lq-form-control" readonly="true">
									</div>
								</div>
								<div class="lq-row">
									<div class="lq-form-group">
										<button class="lq-btn lq-btn-primary" id="btn-create">
											<i class="fa fa-check"></i>生成补货任务
										</button>
									</div>
									<div class="speclist"></div>
								</div>
							</form>
						</div>
						<div class="lq-tab-content">
							<form class="lq-form-inline lq-row">
								<div class="lq-form-group lq-col-12">
									<label for="start">开始时间:</label>
									<input type="text" id="start" class="lq-form-control" value="${start}">
									<button id="startTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
										<i class="fa fa-calendar"></i>
									</button> 
									<label for="end">结束时间:</label>
									<input type="text" id="end" class="lq-form-control" value="${end}">
									<button id="endTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
										<i class="fa fa-calendar"></i>
									</button>
									<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-search">
										<i class="fa fa-search"></i>查询
									</button>
								</div>
							</form>
							<table class="lq-table" id="manul-table">
								<thead>
									<tr>
										<th>货主</th>
										<th>库区</th>
										<th>补货类型</th>
										<th>商品条码</th>
										<th>商品名称</th>
										<th>生成时间</th>
										<th>已生成任务</th>
										<th>任务状态</th>
										<th>操作人</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
							<div class="page-wrap"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="../static/js/zapatec/utils/zapatec.js"></script>
    <script type="text/javascript" src="../static/js/zapatec/zpcal/src/calendar.js"></script>
    <script type="text/javascript" src="../static/js/zapatec/zpcal/lang/calendar-en.js"></script>
	<script type="text/javascript">
		Zapatec.Calendar.setup({
			weekNumbers       : false,
			electric          : false,
			inputField        : "start",
			button            : "startTrigger",
			ifFormat          : "%Y-%m-%d",
			daFormat          : "%Y-%m-%d"
		});
	
		Zapatec.Calendar.setup({
			weekNumbers       : false,
			electric          : false,
			inputField        : "end",
			button            : "endTrigger",
			ifFormat          : "%Y-%m-%d",
			daFormat          : "%Y-%m-%d"
		});
	</script>
	<script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script>
    $(document).ready(function(){
    	var TOTAL_PAGE = 0, START_PAGE = 1, MAX_PAGE = 15, END_PAGE = 15, CURRENT_PAGE = 1, PAGE_DATA = {};

    	$(".lq-tab-nav li").on("click",function(){
    		var index = $(this).index();
    		$(this).addClass("active").siblings().removeClass("active");
    		$(".lq-tab-content").eq(index).addClass("active").siblings().removeClass("active");
    	});

    	$(".customer").on("change",function(){
    		window.setTimeout(function(){
    			if ($(".customer:checked").length == 1 && !$(".customer:checked").hasClass("all")) {
    				$("#barcode").attr("readonly",false);
    			} else {
    				$("#barcode").attr("readonly",true);
    			}

    			$("#barcode").val("");
    			$("#goods_name").val("");
    		},50);
    	});

    	$(".customer.all").on("change",function(){
    		var all = $(this).prop("checked");
    		if (all) {
    			$(".customer").not(".all").prop("checked",true);
    		} else {
    			$(".customer").not(".all").prop("checked",false);
    		}
    	});

    	$("#barcode").on("blur",function(){
    		var barcode = $(this).val(), that = $(this), customer_id = "";
    		for (var i=0;i<$(".customer").length;i++) {
    			if ($(".customer").eq(i).prop("checked")) {
    				customer_id = $(".customer").eq(i).val();
    			}
    		}
    		var data = {
    			barcode : barcode,
    			customer_id : customer_id
    		}
    		// console.log(data);
    		if (barcode) {
    			$.ajax({
					url : "../product/getNameByBarcode",
					type : "get",
					dataType : "json",
					data : data,
					beforeSend : function(){
						that.prop("readonly",true);
					},
					success : function(data) {
						that.prop("readonly",false);
						console.log(data);
						if (data.result == "success") {
							$("#goods_name").val(data.note);
						} else {
							that.val("");
							alert(data.note);
						}
					},
					error : function(error) {
						that.prop("readonly",false);
						alert("getNameByBarcode error");
					}
				});
    		}
    	});

    	$("#btn-create").on("click",function(e){
    		e.preventDefault();
    		if ($(".customer:checked").length == 0) {
    			alert("请选择货主");
    		} else {
    			var that = $(this),
	    			all = $(".customer.all").prop("checked"), other = $(".customer").not(".all");
	    		var createData = {
	    			customer_id_list : "",
	    			box_piece : $("#box_piece").val(),
	    			task_level : $("#task_level").val(),
	    			barcode : $("#barcode").val()
	    		};
	    		if (all) {
	    			createData.customer_id_list = [];
	    			createData.customer_id_list.push("0");
	    			createData.customer_id_list = createData.customer_id_list.join(",");
	    		} else {
	    			createData.customer_id_list = [];
	    			for (var i=0;i<other.length;i++) {
	    				if (other.eq(i).prop("checked")) {
	    					createData.customer_id_list.push(other.eq(i).val());
	    				}
	    			}
	    			createData.customer_id_list = createData.customer_id_list.join(",");
	    		}
	    		console.log(createData);
	    		$.ajax({
					url : "../replenishment/saveManulReplenish",
					type : "get",
					dataType : "json",
					data : createData,
					beforeSend : function(){
						that.prop("disabled",true);
					},
					success : function(data) {
						that.prop("disabled",false);
						console.log(data);
						if (data.result == "success") {
							$(".customer").prop("checked",false);
							$("#barcode").val("");
							$("#goods_name").val("");
							$("#box_piece").find("option").eq(0).attr("selected",true);
						
							if (data.specList.length) {
								var speclist = "";
								for (var i in data.specList) {
									speclist += '<p>'+data.specList[i]+'</p>';
								}
								$(".speclist").html(speclist);
							}
						}
						alert(data.note);
					},
					error : function(error) {
						that.prop("disabled",false);
						alert("saveManulReplenish error");
					}
				});
    		}
    	});

    	$("#btn-search").on("click",function(e){
    		e.preventDefault();
    		var that = $(this);
			var searchData = {
				start : $("#start").val(),
				end : $("#end").val()
			};
			//console.log(saleSearchData);
			$.ajax({
				url : "../replenishment/selectManulReplenish",
				type : "get",
				dataType : "json",
				data : searchData,
				beforeSend : function(){
					that.prop("disabled",true);
				},
				success : function(data) {
					that.prop("disabled",false);
					console.log(data);
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
				},
				error : function(error) {
					that.prop("disabled",false);
					alert("selectManulReplenish error");
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
					tableHtml += '<tr><td>' + data[key].customerName + '</td><td>'
							  + data[key].boxPiece + '</td><td>'
							  + data[key].taskLevel + '</td><td>'
							  + data[key].barcode + '</td><td>'
							  + data[key].productName + '</td><td>'
							  + data[key].created_time + '</td><td>'
							  + data[key].completeNum + '</td><td>'
							  + data[key].status + '</td><td>'
							  + data[key].created_user + '</td></tr>';
				}
				$("#manul-table tbody").html(tableHtml);
				$("#manul-table").fadeIn(300);
			} else {
				$("#manul-table tbody").html("");
				$("#manul-table").fadeOut(300);
			}
		}

		$("#body-manul").on("click",".lq-page-control li",function(){
			var that = $(this);
			if (!that.hasClass("prev") && !that.hasClass("next")) {
				if (!that.hasClass("active")) {
					var index = parseInt($(this).find("a").text());
					CURRENT_PAGE = index;
					var data = {
						currentPage : index,
						pageSize : MAX_PAGE,
						start : $("#start").val(),
						end : $("#end").val()
					};
					$.ajax({
						url : "../replenishment/selectManulReplenish",
						type : "get",
						dataType : "json",
						data : data,
						beforeSend : function(){
							that.addClass("active").siblings().removeClass("active");
						},
						success : function(data) {
							tableConstructor(data.list);
						},
						error : function(error) {
							alert("selectManulReplenish error");
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
    });
    </script>
</body>
</html>