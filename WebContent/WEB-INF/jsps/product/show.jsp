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
	<title>商品基本信息维护</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style type="text/css">
		html, body {
			height: 100%;
		}
		.headerItem {
			display: inline-block;
		}
		.headerIpt {
			width: 200px;
		}
		.delete {
			color: red;
			margin: 0 0 0 10px;
		}
		.update {
			color: blue;
		}
		.update, .delete {
			cursor: pointer;
			text-decoration: underline; 
			font-weight: bold;
		}
		.create {
			margin: 0 0 0 10px;
		}
		.alert {
			display: none;
			position: fixed;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background: rgba(0, 0, 0, 0.5);
			z-index: 999;
			overflow: hidden;
		}
		.alertContainer {
			width: 500px;
			padding: 20px 50px;
			margin: 0px auto;
			background: #fff;
			overflow-y: scroll;
		}
		.alertItem {
			margin: 0 0 10px 0;
		}
		.alertBtns {
			text-align: center;
			margin: 20px 0 0 0;
		}
		.alertItem .alertIptL {
			width: 180px;
		}
		.alertItem .alertIptS {
			width: 50px;
		}
		.alertItem label {
			width: 6em;
			text-align: right;
		}
		.save {
			margin: 0 50px 0 0;
		}
		.required {
			position: relative;
			top: 10px;
			font-size: 28px;
			color: #f00;
		}
		.download {
			position: relative;
			top: 5px;
			left: 10px;
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
		.inputMsg {
			position: relative;
			top: 5px;
			left: 20px;
		}
		/*分页*/
		#page-search {
			float: right;
		}
		.page-wrap {
			display: none;
		}
		.totalPage {
			position: relative;
			top: 5px;
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
	    .noDataTip {
	    	display: none;
	    	text-align: center;
	    	font-size: 30px;
	    	color: #666;
	    	margin: 30px 0 0 0;
	    }
	</style>
</head>
<body>
	<div id="alert">
		<div class="cover"></div>
		<div class="alertMain">
			<div class="alertContain">
				<div class="alertText">
					
				</div>
			</div>
			<div class="alertBtn">
				<button id="close" class="oneBtn">确定</button>
			</div>
		</div>
	</div>
	<div class="alert">
		<div class="lq-form-inline alertContainer">
			<div class="alertItem">
				<label>货主:</label>
				<span class="alertCustomerId"></span>
			</div>
			<div class="alertItem">
				<label>商品条码:</label>
				<span class="alertBarcode"></span>
			</div>
			<div class="alertItem">
				<label>商品名称:</label>
				<span class="alertGoodsName"></span>
			</div>
			<div class="alertItem">
				<label>是否高价:</label>
				<span class="alertIsHighPrice"></span>
			</div>
			<div class="alertItem">
				<label>单品毛重:</label>
				<input type="text" name="weight" class="lq-form-control repIpt saveValue alertIptL" />
				<span>(kg)</span>
			</div>
			<div class="alertItem">
				<label>长:</label>
				<input type="text" name="length" class="lq-form-control repIpt saveValue alertIptL" />
				<span>(cm)</span>
			</div>
			<div class="alertItem">
				<label>宽:</label>
				<input type="text" name="width" class="lq-form-control repIpt saveValue alertIptL" />
				<span>(cm)</span>
			</div>
			<div class="alertItem">
				<label>高:</label>
				<input type="text" name="height" class="lq-form-control repIpt saveValue alertIptL" />
				<span>(cm)</span>
			</div>
			<div class="alertItem">
				<label>箱规:</label>
				<input id="spec" type="text" name="spec" class="lq-form-control repIpt saveValue alertIptS" />
				<label style="width:auto;">满足箱拣货区最大数量:</label>
				<input type="text" name="box_pick_start_number" class="lq-form-control repIpt saveValue alertIptS" />
			</div>
			<div class="alertItem">
				<label>托规(Ti):</label>
				<input type="text" name="ti" class="lq-form-control repIpt saveValue alertIptS" />
				<label>托规(Hi):</label>
				<input type="text" name="hi" class="lq-form-control repIpt saveValue alertIptS" />
			</div>
			<div class="alertItem">
				<label>保质期管理:</label>
				<select class="lq-form-control saveValue alertIptS" name="is_maintain_guarantee">
					<option value="Y">是</option>
					<option value="N">否</option>
				</select>
			</div>
			<div class="alertItem">
				<label>收货规则:</label>
				<select class="lq-form-control saveValue" name="receive_rule">
					<option value="0"></option>
					<option value="0.5">不超过1/2保质期</option>
					<option value="0.3">不超过1/3保质期</option>
				</select>
			</div>
			<div class="alertItem">
				<label>补货规则:</label>
				<select class="lq-form-control saveValue" name="replenishment_rule">
					<option value="0"></option>
					<option value="0.5">不超过1/2保质期</option>
					<option value="0.3">不超过1/3保质期</option>
				</select>
			</div>
			<div class="alertItem">
				<label>出库规则:</label>
				<select class="lq-form-control saveValue" name="stock_allocation_rule">
					<option value="0"></option>
					<option value="0.5">不超过1/2保质期</option>
					<option value="0.3">不超过1/3保质期</option>
				</select>
			</div>
			<div class="alertBtns">
				<button class="lq-btn lq-btn-sm lq-btn-primary save">保存</button>
				<button class="lq-btn lq-btn-sm lq-btn-primary cancel">取消</button>
			</div>
		</div>
	</div>
	<div class="main-container">
		<div class="header lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-2">
					<div class="lq-form-group">
						<label for="customerId">货主:</label>
						<select id="customerId" class="lq-form-control">
							<option value="">--请选择--</option> 
							<c:forEach items="${customers}" var="customer">
								<option name="customer_id" value="${customer.customer_id}">${customer.name}
								</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="lq-col-2">
					<div class="lq-form-group">
						<label for="specmark">箱规</label>
						<select id="specmark" class="lq-form-control">
							<option value="">不限</option>
							<option value="N">未维护箱规</option>
						</select>
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="barcode">商品条码:</label>
						<input id="barcode" type="text" name="" class="lq-form-control" value="" />
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="productName">商品名称:</label>
						<input id="productName" type="text" name="" class="lq-form-control" value="" />
					</div>
				</div>
				<div class="lq-col-2">
					<div class="lq-form-group">
						<button class="lq-btn lq-btn-sm lq-btn-primary search">查询</button>
						<input type="button" id="exportProducts" name="type" value="导出" style="display:none" class="lq-btn lq-btn-sm lq-btn-primary">
					</div>
				</div>
			</div>
			<form id="upload" action="../product/upload" method="post" enctype="multipart/form-data">
				<div class="lq-row">
					<div class="lq-col-4">
						<div class="lq-form-group">
							<button class="lq-btn lq-btn-sm lq-btn-primary search">选取文件</button>
							<input class="file" type="file" name="uploadfile" />
							<span class="fileName"></span>
						</div>
					</div>
					<div class="lq-col-4">
						<div class="lq-form-group">
							<button class="lq-btn lq-btn-sm lq-btn-primary">批量导入</button>
							<a class="download" href="../product/downloadTemplate">模板下载</a>
						</div>
					</div>
				</div>
			</form>
		</div>
		<table class="lq-table">
			<thead>
				<th>货主</th>
				<th>商品条码</th>
				<th>商品名称</th>
				<th>是否高价</th>
				<th>单品毛重(kg)</th>
				<th>长(cm)</th>
				<th>宽(cm)</th>
				<th>高(cm)</th>
				<th>箱规</th>
				<th>满足箱拣货区最大数量(箱)</th>
				<th>托规(Hi)</th>
				<th>托规(Ti)</th>
				<th>保质期管理</th>
				<th>收货规则</th>
				<th>补货规则</th>
				<th>库存分配规则</th>
				<th>操作</th>
			</thead>
			<tbody>
				
			</tbody>
		</table>
		<div class="noDataTip">未查到符合条件的数据!</div>
		<div class="page-wrap">
			<div class="lq-row">
				<nav id="page-search">
					<ul class="lq-page-control">
						<li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>
						<!-- <c:forEach var="item" varStatus="i" begin="1" end="${page.totalPage}"> -->
							<!-- <li class="page"><a href="javascript:void(0);"></a></li> -->
						<!-- </c:forEach> -->
						<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li>
					</ul>
				</nav>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/alertPlugin.js"></script>
	<script type="text/javascript">
		$(function () {
			// 分页
			var TOTAL_PAGE = 0,
				START_PAGE = 1,
				MAX_PAGE = 10,
				END_PAGE = 10,
				CURRENT_PAGE = 1,
				PAGE_DATA = {},
				PAGE = {};
			function pageConstructor(currentPage,startPage,endPage) {
				var currentPage = currentPage,
					startPage = startPage,
				    endPage = endPage;
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
						pageHtml += '<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li></ul></nav></div>';
					}
					$(".page-wrap").html(pageHtml);
				} else {
					$(".page-wrap").html("").fadeOut(300);
				}
			}
			$(document).on("click",".lq-page-control li",function(){
				var that = $(this);
				if (!that.hasClass("prev") && !that.hasClass("next")) {
					if (!that.hasClass("active")) {
						var index = parseInt($(this).find("a").text());
						CURRENT_PAGE = index;
						$.ajax({
							url: '../product/list22',
							type : "post",
							dataType : "json",
							data : {
								currentPage : index,
								pageSize : 10,
								customer_id: $('#customerId').val(),
								barcode: $('#barcode').val(),
								product_name: $('#productName').val(),
								spec:$('#specmark').val()
							},
							beforeSend : function(){
								that.addClass("active").siblings().removeClass("active");
							},
							success : function(res) {
								console.log(res);
								showTable(res);
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
			// 查询
			$('.search').click(search);
			function search () {
				console.log($('#productName').val())
				$.ajax({
					url: '../product/list22',
					type: 'post',
					dataType: 'json',
					data: {
						customer_id: $('#customerId').val(),
						barcode: $('#barcode').val(),
						product_name: $('#productName').val(),
						spec:$('#specmark').val()
					},
					success: function (res) {
						console.log(res);
						$('.noDataTip').fadeOut(300);
						if (res.productList.length == 0) {
							$('.noDataTip').fadeIn(300);
						}
						showTable(res);
						// 分页
						PAGE_DATA = res.page;
						TOTAL_PAGE = parseInt(PAGE_DATA.totalPage);
						if (TOTAL_PAGE < MAX_PAGE) {
							END_PAGE = TOTAL_PAGE;
						} else {
							END_PAGE = MAX_PAGE;
						}
						CURRENT_PAGE = PAGE_DATA.currentPage;
						pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
						$('nav').after('<span class="totalPage">共'+PAGE_DATA.totalPage+'页,'+PAGE_DATA.totalCount+'条商品信息</span>');
						document.getElementById("exportProducts").style.display = "inline-block";
					}
				});
			}
			
			$('#exportProducts').click(function(){
				var data ="";
				var customer_id = $('#customerId').val();
				var barcode = $('#barcode').val();
				var product_name = $('#productName').val();
				var spec = $('#specmark').val();
				if(customer_id != null && customer_id != '') data += ("&customer_id="+customer_id);
				if(barcode != '' && barcode != null) data += ("&barcode="+barcode);
				if(product_name != '' && product_name != null) data += ("&product_name="+product_name);
				if(spec != '' && spec != null) data += ("&spec="+spec);
				window.location = "../product/exportProducts?data"+data;
			});
			
			function showTable (res) {
				var value = res.productList;
				var tdHtml = '';
				for (var i in value) {
					tdHtml += '<tr><td class="tableCustomerId" value='+value[i].customer_id+'>'+value[i].name+'</td><td>'+value[i].barcode+'</td><td>'+value[i].product_name+'</td><td>'+value[i].is_high_price+'</td><td>'+value[i].weight+'</td><td>'+value[i].length+'</td><td>'+value[i].width+'</td><td>'+value[i].height+'</td><td>'+value[i].spec+'</td><td>'+value[i].box_pick_start_number+'</td><td>'+value[i].ti+'</td><td>'+value[i].hi+'</td><td>'+value[i].is_maintain_guarantee+'</td><td>'+value[i].receive_rule+'</td><td>'+value[i].replenishment_rule+'</td><td>'+value[i].stock_allocation_rule+'</td><td class="update"><a class="lq-btn lq-btn-sm lq-btn-primary">编辑</a></td></tr>';
				}
				$('tbody').html(tdHtml);
			}
			// 文件上传
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
			// 编辑
			var dateFix = {};
			$(document).on('click', '.update', function () {
				var height = $(window).height();
				$('.alertContainer').css('height', height);
				$('.alert').show();
				$('body').css('overflow', 'hidden');
				var deleteTr = $(event.target).parents('tr');
				var operateIndex = deleteTr.index();
				console.log($('.tableCustomerId').eq(operateIndex).attr('value'), deleteTr.find('td').eq(1).text())
				$.ajax({
					url: '../product/list',
					type: 'post',
					dataType: 'json',
					data: {
						customer_id: $('.tableCustomerId').eq(operateIndex).attr('value'),
						barcode: deleteTr.find('td').eq(1).text()
					},
					success: function (res) {
						console.log(res);
						var value = res.productList[0];
						$('.alertCustomerId').html(value.name);
						$('.alertBarcode').html(value.barcode);
						$('.alertGoodsName').html(value.product_name);
						if (value.is_high_price == 'N') {
							$('.alertIsHighPrice').html('否');
						} else if (value.is_high_price == 'Y') {
							$('.alertIsHighPrice').html('是');
						} else {
							$('.alertIsHighPrice').html('');
						}
						var iptvalue = [value.weight, value.length, value.width, value.height, value.spec, value.box_pick_start_number, value.ti, value.hi, value.is_maintain_guarantee, value.receive_rule, value.replenishment_rule, value.stock_allocation_rule];
						console.log(iptvalue);
						$('.saveValue').each(function (i, v) {
							$(v).val(iptvalue[i]);
						});
						dataFix = {
							customer_id: value.customer_id,
							barcode: value.barcode,
							product_name: value.product_name
						}
					}
				});
			});
			// 取消
			$(document).on('click', '.cancel', function () {
				$('.alert').hide();
				$('body').css('overflow-y', 'scroll');
				clearData();
			});
			// 清楚数据 
			function clearData () {
				$('.saveValue').each(function(index, el) {
					if (index != 0) {
						$('.saveValue').eq(index).val('');
					}
				});
			}
			// 保存
			var isSave = true;
			var reg = /^[0-9]\d*$/;
			$(document).on('click', '.save', function () {
				if (!Number($('#spec').val()) || Number($('#spec').val()) <= 0) {
					isSave = false;
					$('#spec').focus();
					alert('请填写正整数的箱规！');
				} else {
					isSave = true;
				}
				console.log(isSave);
				if (isSave) {
					var data = {};
					$('.saveValue').each(function (i, v) {
						data[$(v).attr('name')] = $(v).val();
					});
					data = $.extend(true, data, dataFix);
					console.log(data);
					$.ajax({
						url: '../product/update',
						type: 'post',
						dataType: 'json',
						data: data,
						success: function (res) {
							console.log(res);
							$('.alert').hide();
							$('body').css('overflow-y', 'scroll');
							search();
						}
					});
				}
			});
			// 批量导入
			if ('${resMap.excel_note}') {
				$('#alert').show();
				$('#alert').AlertPlugin({
					width: 360,
					height: 220,
					htmlText: '<div><c:out value="${resMap.excel_note}"></c:out></div>',
					btnCount: 2
				});
			}
			$('.trueBtn').click(function () {
				$('#alert').hide();
			});
			$('.falseBtn').click(function () {
				$('#alert').hide();
			});
		});
	</script>
</body>
</html>