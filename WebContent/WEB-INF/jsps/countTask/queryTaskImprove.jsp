<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
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
	<title>生成调整单</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style type="text/css">
		.searchBtn {
			margin: 0 0 0 10px;
		}
		#reviewAll {
			margin: 0 0 0 30px;
		}
		#page-search {
			float: right;
		}
		.page-wrap {
			display: none;
		}
		.uploadfile {
			margin: 10px 0 0 0;
		}
		#batchTable {
			margin: 10px 0 0 0;
		}
		.inputMsg {
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
	<div class="main-container">
		<form class="lq-form-inline">
			<div class="lq-form-group">
				<label for="customer_id">选择货主:</label>
				<select name="customer_id" id="customer_id" class="lq-form-control">
	                <option value="" >--请选择--</option>
                   	<c:forEach items="${customers}" var="customer"> 
	                   	<option <c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
					    value="${customer.customer_id}">${customer.name}</option>
					</c:forEach> 
	            </select>
	            <button type="button" id="" class="searchBtn lq-btn lq-btn-sm lq-btn-primary">查询</button>
	        	<shiro:hasPermission name="sys:work:create">
	               <button type="button" id="reviewAll" class="lq-btn lq-btn-sm lq-btn-primary">审核</button>
	             </shiro:hasPermission>
	        </div>
	        <div class="lq-form-group">
				<div class="btns">
					<button type="button" class="lq-btn lq-btn-sm lq-btn-primary checkAll">全选</button>
					<button type="button" class="lq-btn lq-btn-sm lq-btn-primary checkNot">反选</button>
				</div>
	        </div>
		</form>
		<table class="lq-table">
			<thead>
				<tr>
					<th>勾选</th>
					<th>渠道</th>
					<th>商品条码</th>
					<th>商品名称</th>
					<th>商品状态</th>
					<th>差异数据</th>
				</tr>
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
	<script type="text/javascript">
	var warehouseMap={};
		$(function () {
			var TOTAL_PAGE = 0,
				START_PAGE = 1,
				MAX_PAGE = 10,
				END_PAGE = 10,
				CURRENT_PAGE = 1,
				PAGE_DATA = {},
				PAGE = {},
				sendData = {};
			var customer_id = 0;

			
			// 查询
			$('.searchBtn').click(search);
			function search () {
				$.ajax({
					url: '../countTask/queryProductLocationImprove',
					type: 'post',
					dataType: 'json',
					data: {
						customer_id: $('#customer_id').val()
					},
					success: function (res) {
						console.log(res);
						if(res.warehouseList){
							for(var i in res.warehouseList){
								warehouseMap[res.warehouseList[i].warehouse_id]=res.warehouseList[i].warehouse_name;
							}
						}
						console.log(warehouseMap);
						
						$('.noDataTip').hide();
						if (!res.list.length) {
							$('.noDataTip').show();
						}
						showTable (res);
						// 分页
						PAGE_DATA = res.page;
						TOTAL_PAGE = parseInt(PAGE_DATA.totalPage);
						TOTAL_ORDER = PAGE_DATA.totalCount;
						START_PAGE = 1;
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
			customer_id = $('#customer_id').val();
			//状态转化方法
			function statusTransform(str)
			{
				var status;
				switch(str)
				{
				case "DEFECTIVE": status="二手";break;
				case "NORMAL"	: status="全新";break;
				case "EXCEPTION_QUALITY": status="质量问题（冻结）";break;
				}
				return status;
			}
			function showTable (res) {
				var data = res.list,
					dataLen = data.length,
					trHtml = '';
				for (var i = 0; i < dataLen; i++) {
					trHtml += '<tr data-product_id="'+data[i].product_id+'" data-warehouse_id="'+data[i].warehouse_id+'"><td><input type="checkbox" /></td><td>'+warehouseMap[data[i].warehouse_id]+'</td><td>'+data[i].barcode+'</td><td>'+data[i].product_name+'</td><td class="status">'+statusTransform(data[i].status)+'<input type="hidden" id="status" value="'+data[i].status+'"></td><td class="qty_total">'+data[i].qty_total+'</td></tr>'
				}
				$('tbody').html(trHtml);
			}
			// 分页
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
						sendData = {
							customer_id: $('#customer_id').val()
						};
						CURRENT_PAGE = index;
						sendData = $.extend({}, {currentPage: index, pageSize: 20}, {customer_id: $('#customer_id').val()});
						$.ajax({
							url : "../countTask/queryProductLocationImprove",
							type : "get",
							dataType : "json",
							data : sendData,
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
			// 全部审核
			$('#reviewAll').click(function () {
				$(this).attr('disabled', true);
				var reviewInfo = '';
				var isChecked = true;
				var flag=false;
				$.each($('input[type="checkbox"]'), function(index, val) {
					if ($(val).prop('checked')) {
						var thisTr = $('tr').eq(index + 1);
						var qty=parseInt(thisTr.find('.qty_total').text());
						console.log(thisTr.find('#status').val());
						reviewInfo += thisTr.data('product_id') + ':' + thisTr.find('#status').val() + ':' + thisTr.find('.qty_total').text() +':'+thisTr.data('warehouse_id')+ ',';
						if(qty<=-100){
							console.log("盘亏数量为:"+qty);
							flag=true;
						}
						
					}
				});
				reviewInfo = reviewInfo.substring(0, reviewInfo.length - 1);
				if (!reviewInfo.length) {
					isChecked = false;
				}
				console.log(isChecked, reviewInfo.length);
				if (isChecked) {
					if(flag){
						if(confirm("存在盘亏数量大于100的商品，确认审核?")){
							$.ajax({
								url: '../countTask/createProductLocationChange',
								type: 'post',
								dataType: 'json',
								data: {
									customer_id: $('#customer_id').val(),
									param: reviewInfo
								},
								success: function (res) {
									console.log(res);
									if (res.result = 'true') {
										alert('审核成功');
									}
									$('#reviewAll').attr('disabled', false);
									search();
								}
							});
						}else{
							$('#reviewAll').removeAttr('disabled');
							return;
						}
					}else{
						$.ajax({
							url: '../countTask/createProductLocationChange',
							type: 'post',
							dataType: 'json',
							data: {
								customer_id: $('#customer_id').val(),
								param: reviewInfo
							},
							success: function (res) {
								console.log(res);
								if (res.result = 'true') {
									alert('审核成功');
									window.location.reload();
								}
								$('#reviewAll').attr('disabled', false);
								search();
							}
						});
					}

				} else {
					alert('请勾选');
					$('#reviewAll').attr('disabled', false);
				}
			});
			// 全选	
			var checkNum = 0;
			$('.checkAll').on('click', function () {
				$.each($('input[type="checkbox"]'), function(index, val) {
					if ($(val).prop('checked')) {
						checkNum = 1;
					} else {
						checkNum = 0;
						return false;
					}
				});
				if (checkNum % 2 == 0) {
					$('input[type="checkbox"]').prop('checked', true);
				} else {
					$('input[type="checkbox"]').prop('checked', false);
				}
				checkNum ++;
			});
			// 反选
			$('.checkNot').on('click', function () {
				$.each($('input[type="checkbox"]'), function(index, val) {
					if ($(val).prop('checked')) {
						$(val).prop('checked', false);
					} else {
						$(val).prop('checked', true);
					}
				});
			});
		});
	</script>
</body>
</html>