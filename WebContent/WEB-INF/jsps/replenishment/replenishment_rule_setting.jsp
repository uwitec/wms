<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>补货规则设置</title>
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
		.create {
			margin: 0 0 0 10px;
		}
		.update {
			margin: 12px 10px 0 0;
		}
		.repAlert {
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
			padding: 50px;
			margin: 50px auto 0 auto;
			background: #fff;
		}
		.reqItem {
			position: relative; 
			margin: 0 0 10px 0;
		}
		.reqItem .repIpt {
			width: 100px;
		}
		.reqBtns {
			text-align: center;
			margin: 20px 0 0 0;
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
		.errorTip {
			display: none;
			position: absolute;
			top: -5px;
			left: 70px;
			font-size: 12px;
			color: red;
		}
		/*分页*/
		#page-search {
			float: right;
		}
		.page-wrap {
			display: none;
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
			width: 100%;
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
	<div class="repAlert">
		<div class="lq-form-inline alertContainer">
			<div class="reqItem">
				<label for="repCustomerId">货&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;主:</label>
				<select id="repCustomerId" class="lq-form-control saveValue" data-value="customer_id">
					<c:forEach items="${warehouseCustomerList}" var="customer">
						<option name="customer_id" value="${customer.customer_id}">${customer.name}
						</option>
					</c:forEach>
				</select>
				<span class="required">*</span>
			</div>
			<div class="reqItem">
				<i class="errorTip">asfsf</i>
				<label for="repBarcode">商品条码:</label>
				<input id="repBarcode" type="text" name="" class="lq-form-control saveValue"  data-value="barcode" required="required" />
				<span class="required">*</span>
			</div>
			<div class="reqItem">
				<label for="repProductName">商品名称:</label>
				<input id="repProductName" type="text" name="" class="lq-form-control saveValue"  data-value="product_name" disabled="true" />
			</div>
			<div class="reqItem">
				<label for="">件拣货区:</label>
				<input id="fromP" type="text" name="" class="need-trans lq-form-control repIpt saveValue pickArea"  data-value="from_piece_location_barcode" />
				<span>至</span>
				<input id="toP" type="text" name="" class="need-trans lq-form-control repIpt saveValue pickArea" data-value="to_piece_location_barcode" />
			</div>
			<div class="reqItem">
				<label for="">最低存量:</label>
				<input id="" type="text" name="" class="lq-form-control repIpt saveValue pieceMin" data-value="piece_location_min_quantity" required="required" />
				<span class="required">*</span>
				<label for="">最高存量:</label>
				<input id="" type="text" name="" class="lq-form-control repIpt saveValue pieceMax" data-value="piece_location_max_quantity" required="required" />
				<span class="required">*</span>
			</div>
			<div class="reqItem">
				<label for="">箱拣货区:</label>
				<input id="fromB" type="text" name="" class="need-trans lq-form-control repIpt saveValue pickArea" data-value="from_box_location_barcode" />
				<span>至</span>
				<input id="toB" type="text" name="" class="need-trans lq-form-control repIpt saveValue pickArea" data-value="to_box_location_barcode" />
			</div>
			<div class="reqItem">
				<label for="">最低存量:</label>
				<input id="" type="text" name="" class="lq-form-control repIpt saveValue boxMin" data-value="box_location_min_quantity" required="required" />
				<span class="required">*</span>
				<label for="">最高存量:</label>
				<input id="" type="text" name="" class="lq-form-control repIpt saveValue boxMax" data-value="box_location_max_quantity" required="required" />
				<span class="required">*</span>
			</div>
			<div class="reqBtns">
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
							<c:forEach items="${warehouseCustomerList}" var="customer">
								<option name="customer_id" value="${customer.customer_id}">${customer.name}
								</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="barcode">商品条码:</label>
						<input id="barcode" type="text" name="" class="lq-form-control" value="" />
					</div>
				</div>
				<div class="lq-col-4">
					<div class="lq-form-group">
						<label for="productName">商品名称:</label>
						<input id="productName" type="text" name="" class="lq-form-control" value="" />
					</div>
				</div>
				<div class="lq-col-2">
					<div class="lq-form-group">
						<button class="lq-btn lq-btn-sm lq-btn-primary search">查询</button>
						<button class="lq-btn lq-btn-sm lq-btn-primary create">+&nbsp;新增</button>
					</div>
				</div>
			</div>
			<form action="../replenishmentQuery/uploadLocationFile" method="post" enctype="multipart/form-data" onsubmit="return check_form()">
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
							<button id="putIn" class="lq-btn lq-btn-sm lq-btn-primary">批量导入</button>
							<a class="download" href="../replenishmentQuery/downloadReplenishment">模板下载</a>
							<span class="inputMsg">
								<c:if test="${resMap.excel_note != '' and resMap.excel_note != null}">
									<c:out value="${resMap.excel_note}"></c:out>
								</c:if>
							</span>
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
				<th>最低存量（件拣货区）</th>
				<th>最高存量（件拣货区）</th>
				<th>最低存量（箱拣货区）</th>
				<th>最高存量（箱拣货区）</th>
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
	<div class="test">
		<c:forEach items="${piliangdaoruList}" var="uploadTIP">
			<p name="excel_note" value="${uploadTIP.excel_note}">${customer.excel_note}</p>
		</c:forEach>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/alertPlugin.js"></script>
	<script type="text/javascript">
		$(function () {
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
			// 文件上传
			function getFileName(obj){  
				var fileName="";  
				if(typeof(fileName) != "undefined") {  
				    fileName = $(obj).val().split("\\").pop(); 
				}  
				return fileName;  
			} 
			$('.file').change(function () {
				$('.fileName').html(getFileName($(this)));
			})
			// 建表
			function showTable(res) {
				var value = res.searchResultList;
				var tdHtml = '';
				for (var i in value) {
					tdHtml += '<tr name="'+value[i].customer_id+'"><td>'+value[i].name+'</td><td class="barcodeTd">'+value[i].barcode+'</td><td>'+value[i].product_name+'</td><td>'+value[i].piece_location_min_quantity+'</td><td>'+value[i].piece_location_max_quantity+'</td><td>'+value[i].box_location_min_quantity+'</td><td>'+value[i].box_location_max_quantity+'</td><td><p class="update lq-btn lq-btn-sm lq-btn-primary">编辑</p><p class="delete lq-btn lq-btn-sm lq-btn-danger">撤销</p></td></tr>';
				}
				$('tbody').html(tdHtml);
				$('.search').attr('disabled', false);
			}
			// 查询
			function search () {
				$.ajax({
					url: '../replenishmentQuery/query',
					type: 'post',
					dataType: 'json',
					data: {
						customer_id: $('#customerId').val(),
						barcode: $('#barcode').val(),
						product_name: $('#productName').val()
					},
					success: function (res) {
						console.log(res);
						$('.noDataTip').fadeOut(300);
						if (!res.searchResultList.length) {
							$('.search').attr('disabled', false);
							$('.noDataTip').fadeIn(300);
							// alert('没有相应的补货规则');
						}
						showTable(res);
						// 分页
						PAGE_DATA = res.page;
						TOTAL_PAGE = parseInt(PAGE_DATA.totalPage);
						MAX_PAGE = PAGE_DATA.pageSize;
						if (TOTAL_PAGE < MAX_PAGE) {
							END_PAGE = TOTAL_PAGE;
						} else {
							END_PAGE = MAX_PAGE;
						}
						CURRENT_PAGE = PAGE_DATA.currentPage;
						pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
					}
				});
			}
			$('.search').click(function () {
				$('.search').attr('disabled', true);
				console.log($('#customerId').val(), $('#barcode').val(), $('#productName').val());
				search ();
			});
			// 新增
			$('.create').click(function () {
				$('.repAlert').data('type', '0');
				$('.repAlert').show();
			});
			// 输入商品条码时自动匹配商品名称
			$('#repBarcode').blur(function () {
				console.log($(this).val(), $('#repCustomerId').val())
				$.ajax({
					url: '../replenishmentQuery/getNameByBarcode',
					type: 'post',
					dataType: 'json',
					data: {
						barcode: $(this).val(),
						customer_id: $('#repCustomerId').val()
					},
					success: function (res) {
						console.log(res)
						if (res.result == 'success') {
							$('.errorTip').hide();
							$('#repProductName').val(res.note);
						} else {
							$('#repBarcode').val('');
							$('.errorTip').show().html(res.note);
						}
					}
				});
			});
			$('#repBarcode').focus(function () {
				$('.errorTip').hide();
			});
			// 取消
			$('.cancel').click(function () {
				$('.repAlert').hide();
				$('.errorTip').hide();
				clearData();
			});
			// 清楚数据 
			function clearData () {
				$('.saveValue').each(function(index, el) {
					if (index != 0) {
						$('.saveValue').eq(index).val('');
					}
				});
				$('#repCustomerId').attr('disabled', false);
				$('#repBarcode').attr('disabled', false);
			}
			// 保存
			$('.save').click(function () {
				// 校验
				var isSubmit = true;
				var isNumNull = true;
				var pickArea = true;
				$('.required').prev().each(function(index, el) {
					if (!$(el).val()) {  // 部分必填
						alert('请输入');
						isSubmit = false;
						return false;
					} else if (index > 1) {
						var reg = /^[0-9]\d*$/;
						if (!reg.test($(el).val())) {  // 存量是否为数字
							alert('请填写正确的存量');
							isSubmit = false;
							return false;
						} else if (parseInt($('.pieceMax').val()) < parseInt($('.pieceMin').val()) || parseInt($('.boxMax').val()) < parseInt($('.boxMin').val())) {
							alert('最低存量不能高于最高存量！');
							isSubmit = false;
							return false;
						} else if (parseInt($('.pieceMax').val()) && parseInt($('.boxMax').val())) {
							if (parseInt($('.pieceMax').val()) >= parseInt($('.boxMax').val())) {
								alert('箱拣货区的最高存量一定要大于件拣货区的最高存量！');
								isSubmit = false;
								return false;
							}
						}
					}
				});
				// 必须填写完整的件/箱拣货区
				$('.pickArea').each(function (i, v) {
					if (i == 2) {
						pickArea = true;
					}
					if ($(v).val()) {
						pickArea = !pickArea;
						isNumNull = pickArea;
					}
					if (i == 1 && !isNumNull) {
						alert('必须填写完整的件/箱拣货区!');
						return false;
					}
				});
				console.log(isSubmit, isNumNull);
				if (isSubmit && isNumNull) {
					var data = {};
					$('.saveValue').each(function(index, val) {
						data[$('.saveValue').eq(index).data('value')] = $(val).val();
					});
					data.operateState = $('.repAlert').data('type');
					$.ajax({
						url: '../replenishmentQuery/addRule',
						type: 'post',
						dataType: 'json',
						data: data,
						success: function (res) {
							console.log(res);
							if (res.error) {
								alert(res.error);
							} else {
								alert(res.note);
								if (res.result == 'success') {
									$('.repAlert').hide();
									clearData();
								}
								search ();
							}
						}
					});
				}
			});
			// 编辑
			$(document).on('click', '.update', function (event) {
				$('.repAlert').data('type', '1');
				$('.repAlert').show();
				$('#repCustomerId').attr('disabled', true);
				$('#repBarcode').attr('disabled', true);
				var deleteTr = $(event.target).parents('tr');
				var operateIndex = deleteTr.index();
				$.ajax({
					url: '../replenishmentQuery/query',
					type: 'post',
					dataType: 'json',
					data: {
						customer_id: $('#customerId').val(),
						barcode: $('#barcode').val(),
						product_name: $('#productName').val()
					},
					success: function (res) {
						console.log(res)
						var operateData = res.searchResultList[operateIndex];
						$('.saveValue').each(function(i, el) {
							$.each(operateData, function(index, val) {
								if (index == $(el).data('value')) {
									$(el).val(val);
								}
							});
						});
						$(".need-trans").each(function(){
							$(this).val(insert_flg($(this).val()));
						});
					}
				});
			});
			// 删除
			var deleteTr;
			$(document).on('click', '.delete', function (event) {
				deleteTr = $(event.target).parents('tr');
				console.log(deleteTr.find('.barcodeTd').text());
				$('#alert').show();
				$('#alert').AlertPlugin({
					width: 360,
					height: 220,
					htmlText: '<div style="font-size:20px">确定要删除吗？</div>',
					btnCount: 2
				});
			});
			$(document).on('click', '.trueBtn', function () {
				$.ajax({
					url: '../replenishmentQuery/deleteRule',
					type: 'post',
					dataType: 'json',
					data: {
						'customer_id': deleteTr.attr('name'),
						'barcode': deleteTr.find('.barcodeTd').text()
					},
					success: function (res) {
						console.log(res);
						$('#alert').hide();
						alert(res.note);
						if (res.result == 'success') {
							deleteTr.remove();
						}
					}
				});
			});
			$(document).on('click', '.falseBtn', function () {
				$('#alert').hide();
			});
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
						console.log(MAX_PAGE);
						$.ajax({
							url: '../replenishmentQuery/query',
							type: 'post',
							dataType: 'json',
							data: {
								currentPage : index,
								pageSize : MAX_PAGE,
								customer_id: $('#customerId').val(),
								barcode: $('#barcode').val(),
								product_name: $('#productName').val()
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
			// 批量导入
			var tip = '';
			var noteMsg = '';
			console.log('${piliangdaoruList}');
			if ('${piliangdaoruList}') {
				$('#alert').show();
				console.log('${piliangdaoruList[1].result}')
				if ('${piliangdaoruList[1].result}' == 'success') {
					noteMsg = '${piliangdaoruList[1].note}';
					$('#alert').AlertPlugin({
						width: 360,
						height: 220,
						htmlText: '<div style="font-size:20px">'+noteMsg+'</div>',
						btnCount: 1
					});
					$('.alertMain').css({
						'margin': '100px -180px',
						'top': 0
					});
				} else {
					noteMsg = '<c:forEach items="${piliangdaoruList}" varStatus="i" var="tip"><p class="tip">${tip.result}</p></c:forEach>';
					$('#alert').AlertPlugin({
						width: 1000,
						height: 'auto',
						htmlText: '<div class="tipMsg">'+noteMsg+'</div>',
						btnCount: 1
					});
					$('.alertMain').css({
						'margin': '0 -500px',
						'top': 0
					});
				}
				function windowSize () {
					var maxAlert = $(window).height();
					if ($('.alertMain').height() >= $(window).height()) {
						$('.tipMsg').css({
							'height': (maxAlert * 0.91),
							'overflow-y': 'scroll'
						});
					}
				}
				windowSize();	
				$(window).resize(function () {
					windowSize();
				});
			}
		});
		// 禁止连续两次批量导入
		function check_form () {
			$('#putIn').attr('disabled', true);
		}
	</script>
</body>
</html>