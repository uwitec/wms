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
	<title>库位查询</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style type="text/css">
	.download {
		line-height: 30px;
		padding: 0 6px;
	}
	html, body {
		width: 100%;
		height: 100%;
	}
	.alert {
		display: none;
		position: fixed;
		width: 100%;
		height: 100%;
		z-index: 999;
		background-color: rgba(0,0,0,.5); 
		overflow: auto;
	}
	
	.alertMain {
		width:610px;
		margin:0 auto;
		background: #fff;
	}
	.alertHeader {
		border-bottom: 1px solid #999;
	}
	.alertContainer {
		display: block;
		padding: 20px 50px;
	}
	.alertTitle {
		height: 30px;
		line-height: 30px;
		text-align: center;
		margin: 0;
	}
	.margin {
		margin: 0 0 0 20px;
	}
	.lcItem {
		margin: 10px 0 0 0;
	}
	.BtnItem {
		text-align: center;
	}
	.lcItem .lcInput {
		width: 50px;
		margin: 0 10px 0 0;
	}
	.lcItem .sequence {
		width: 150px;
	}
	.cancel {
		margin: 0 0 0 50px;
	}
	.del {
		color: red;
		cursor: pointer;
	}
	.update {
		cursor: pointer;
	}
	/*.inputAll, .download {
		float: right;
	}*/
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
	<div class="alert">
		<div class="alertMain">
			<div class="alertHeader">
				<h3 class="alertTitle">新增库位</h3>
			</div>
			<form class="lq-form-inline alertContainer">
				<div class="lcItem">
					<i class="important">*</i>
					<label for="lcCodeNew">库位编码:</label>
					<input id="lcCodeNew" type="text" class="lq-form-control">
					<i class="important margin">*</i>
					<label for="lcTypeNew">库位类型:</label>
					<select id="lcTypeNew" class="lq-form-control" style="width:28%;">
			     		<c:forEach items="${location_type_map}" var="map">
			     			<option value="${map.key}">${map.value}</option>
			     		</c:forEach>
					</select>
					
				</div>
				<div class="lcItem">
					<label for="lcArea">库区:</label>
					<input id="lcArea" type="text" class="lq-form-control lcInput disabled">
					<label for="lcPai">排:</label>
					<input id="lcPai" type="text" class="lq-form-control lcInput disabled">
					<label for="lcLie">列:</label>
					<input id="lcLie" type="text" class="lq-form-control lcInput disabled">
					<label for="lcCeng">层:</label>
					<input id="lcCeng" type="text" class="lq-form-control lcInput disabled">
				</div>
				<div class="lcItem">
					<label for="lcWarehouse">仓库:</label>
					<input id="lcWarehouse" type="text" class="lq-form-control" readonly="readonly">
					<label for="lcCustomer" class="margin"><i class="important customerList" style="display:none;">*</i> 货主:</label>
					<select name="customers_id" id="lcCustomer" class="lq-form-control"></select> 
				</div>
				<div class="lcItem">
					<label for="putaway">上架顺序:</label>
					<input id="putaway" type="text" class="lq-form-control sequence">
					<label for="orderpick" class="margin">拣货顺序:</label>
					<input id="orderpick" type="text" class="lq-form-control sequence">
				</div>
				<div class="lcItem">
					<label for="lcStatusNew">库位状态:</label>
					<select id="lcStatusNew" class="lq-form-control" disabled="true">
						<option value="Y" selected="true">空</option>
						<option value="N">非空</option>
					</select>
					<label for="lcIDNew" class="margin">库位ID:</label>
					<input id="lcIDNew" type="text" class="lq-form-control" readonly="readonly">
				</div>
				<div class="lcItem">
					<label for="lcPriceLevel">价值级别:</label>
					<select id="lcPriceLevel" class="lq-form-control">
						<option>A</option>
						<option>B</option>
						<option>C</option>
					</select>
					<label for="lcLoopLevel" class="margin">循环级别:</label>
					<select id="lcLoopLevel" class="lq-form-control">
						<option>A</option>
						<option>B</option>
						<option>C</option>
					</select>
				</div>
				<div class="lcItem">
					<label for="is_mixupsGoods">允许混放商品:</label>
					<select id="is_mixupsGoods" class="lq-form-control">
						<option value="0">0</option>
						<option value="1">1</option>
					</select>
					<label class="margin" for="is_mixupsBatch">允许混放批次:</label>
					<select id="is_mixupsBatch" class="lq-form-control">
						<option value="0">0</option>
						<option value="1">1</option>
					</select>
					<label class="margin" for="is_count">是否计算库容:</label>
					<select id="is_count" class="lq-form-control">
						<option value="0">0</option>
						<option value="1">1</option>
					</select>
				</div>
				<div class="lcItem">
					<label for="stockNum">可放托盘数量:</label>
					<input id="stockNum" type="text" class="lq-form-control">
				</div>
				<div class="lcItem">
					<label for="long">长:</label>
					<input id="long" type="text" class="lq-form-control lcInput">
					<label for="width">宽:</label>
					<input id="width" type="text" class="lq-form-control lcInput">
					<label for="height">高:</label>
					<input id="height" type="text" class="lq-form-control lcInput">
				</div>
				<div class="lcItem">
					<label for="volume">体积:</label>
					<input id="volume" type="text" class="lq-form-control lcInput">
					<label for="weight">重量:</label>
					<input id="weight" type="text" class="lq-form-control lcInput">
				</div>
				<div class="lcItem">
					<label for="X">坐标(X):</label>
					<input id="X" type="text" class="lq-form-control lcInput">
					<label for="Y">坐标(Y):</label>
					<input id="Y" type="text" class="lq-form-control lcInput">
					<label for="Z">坐标(Z):</label>
					<input id="Z" type="text" class="lq-form-control lcInput">
				</div>
				<div class="lcItem">
					<label for="numLimit">数量限制:</label>
					<input id="numLimit" type="text" class="lq-form-control lcInput">
					<label for="ignoreLPN">忽略LPN:</label>
					<input id="ignoreLPN" type="text" class="lq-form-control lcInput">
				</div>
				<div class="lcItem BtnItem">
					<button type="button" class="lq-btn lq-btn-sm lq-btn-primary save">保存</button>
					<button type="button" class="lq-btn lq-btn-sm lq-btn-primary cancel">取消</button>
				</div>
			</form>
		</div>
	</div>
	<div class="main-container">
		<form class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label>库位类型:</label>
 		          		<select class="lq-form-control locationType"> 
							<option value="">--请选择--</option> 
				     		<c:forEach items="${location_type_map}" var="map">
				     			<option value="${map.key}">${map.value}</option>
				     		</c:forEach>
				     	</select>						
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<div class="lq-form-group">
							<label>库位条码:</label>
							<input type="text" class="lq-form-control locationCode" value="${location_barcode}" />
						</div>
					</div>
				</div>
				<div class="lq-col-2">
					<div class="lq-form-group">
						<label>库位状态:</label>
			        	<select name="status" id="status" class="lq-form-control locationStatus"> 
			        		<option value="">不限</option>
			        		<option value="N">非空</option>
			        		<option value="Y">空</option>
						</select> 
					</div>
				</div>
				<div class="lq-col-2">
					<div class="lq-form-group">
						<label>是否弃用:</label>
			        	<select name="is_delete" id="is_delete" class="lq-form-control is_delete"> 
			        		<option value="N">否</option>
			        		<option value="Y">是</option>
						</select> 
					</div>
				</div>
				<div class="lq-col-2">
					<div class="lq-form-group">
						<button type="button" class="lq-btn lq-btn-sm lq-btn-primary search">&nbsp;&nbsp;查&nbsp;&nbsp;&nbsp;&nbsp;询&nbsp;&nbsp;</button>
						<button type="button" class="lq-btn lq-btn-sm lq-btn-primary newLocation">新增库位</button>
						<input type="button" id="exportLocation" name="type" value="导出" style="display:none" class="lq-btn lq-btn-sm lq-btn-primary">
					</div>
					
				</div>
			</div>
		
		</form>
			<form action="../inventory/uploadLocationFile" method="post" enctype="multipart/form-data">
				<!-- <div>
					<c:out value="${resMap.excel_note}"></c:out>
				</div> -->
				<div class="row">
					<div class="lq-col-10">
						<div class="lq-form-group">
							<input type="file" class="uploadfile" style="width: 190px;background-color: rgb(238, 238, 238);" name="uploadfile"/>
							<span class="inputMsg">
								<c:if test="${resMap.excel_note != '' and resMap.excel_note != null}">
									<c:out value="${resMap.excel_note}"></c:out>
								</c:if>
							</span>
						</div>
					</div>
					<div class="lq-col-2">
						<div class="lq-form-group">
							<button type="submit" class="lq-btn lq-btn-sm lq-btn-primary inputAll">批量导入</button>
							<a class="download" href="../inventory/downloadLocation">模板下载</a>
						</div>
					</div>
				</div>
			</form> 
		 <table class="lq-table" id="batchTable">
		 <thead>
			<tr>
				<th>编号</th>
				<th>库位编码</th>
				<th>库位类型</th>
				<th>库位状态</th>
				<th>创建者</th>
				<th>创建时间</th>
				<th>修改者</th>
				<th>修改时间</th>
				<th colspan="2">操作</th>
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
			var TOTAL_PAGE = 0,
				START_PAGE = 1,
				MAX_PAGE = 10,
				END_PAGE = 10,
				CURRENT_PAGE = 1,
				PAGE_DATA = {},
				PAGE = {};
			// 查询
			$('.search').on('click', function () {
				console.log($('.locationType').val())
				$('.search').attr('disabled', true);
				search();
			});
			$('#exportLocation').click(function(){
				var data ="";
				var locationType = $('.locationType').val();
				var locationStatus = $('.locationStatus').val();
				var location_barcode = $('.locationCode').val();
				var is_delete = $('.is_delete').val();
				if(locationType != null && locationType != '') data += ("&locationType="+locationType);
				if(locationStatus != '' && locationStatus != null) data += ("&locationStatus="+locationStatus);
				if(location_barcode != '' && location_barcode != null) data += ("&location_barcode="+location_barcode);
				if(is_delete != '' && is_delete != null) data += ("&is_delete="+is_delete);
				window.location = "../inventory/exportLocation?data"+data;
			});
			function search () {
				$.ajax({
					url: '../inventory/locationNew',
					type: 'post',
					dataType: 'json',
					data: {
						locationType: $('.locationType').val(),
						locationStatus: $('.locationStatus').val(),
						location_barcode: $('.locationCode').val(),
						is_delete: $('.is_delete').val()
					},
					success: function (res) {
						console.log(res);
						var val = res.locationList;
						showTable(res);
						if (res.locationList.length == 0) {
							$('.noDataTip').fadeIn(300);
							$('.search').attr('disabled', false);
						}
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
						document.getElementById("exportLocation").style.display = "inline-block";
					}
				});
			}
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
			// 生成table
			function showTable (res) {
				var val = res.locationList;
				$('.noDataTip').hide();
				$('.search').attr('disabled', false);
				var trHtml = '';
				var trNum = res.locationList.length;
				var date = '',
					date1 = '',
					time = '',
					time1 = '';
				var is_delete = $('.is_delete').val();
				for (var i = 0; i < trNum; i ++) {
					date = new Date(val[i].created_time);
					date1 = new Date(val[i].last_updated_time);
					time = date.toString().split(' ')[4];
					time1 = date1.toString().split(' ')[4];
					trHtml += '<tr><td>'+(i+1)+'</td><td>'+insert_flg(val[i].location_barcode)+'</td>';
					trHtml+='<td>'+val[i].location_type_name+'</td>';
					
					trHtml += '<td>'+val[i].location_status+'</td><td>'+val[i].created_user+'</td><td>'+date.getFullYear()+'-'+(date.getMonth() + 1)+'-'+date.getDate()+' '+time+'</td><td>'+val[i].last_updated_user+'</td><td>'+date1.getFullYear()+'-'+(date1.getMonth() + 1)+'-'+date1.getDate()+' '+time1+'</td>';
					if(is_delete == 'N'){
						trHtml += '<td><span class="del">弃用</span></td><td><span class="update">编辑</span></td></tr>';
					}else{
						trHtml += '<td><span class="rec">恢复</span></td></tr>';
					}
				}
				$('tbody').html(trHtml);
				
				// 弃用
				$('.del').on('click', function () {
					console.log($(this).parent().parent().index())
					var trIndex = $(this).parent().parent().index();
					console.log(val[trIndex].location_id);
					$.ajax({
						url: '../inventory/locationDeleteNew',
						type: 'post',
						dataType: 'json',
						data: {
							location_id: val[trIndex].location_id
						},
						success: function (res) {
							console.log(res);
							if (res.result == 'failure') {
								alert("弃用失败，"+res.note);
								return false;
							} else {
								alert("弃用成功");
								$('tbody tr').eq(trIndex).remove();
							}
						}
					});
				});
				// 恢复
				$('.rec').on('click', function () {
					console.log($(this).parent().parent().index())
					var trIndex = $(this).parent().parent().index();
					console.log(val[trIndex].location_id);
					$.ajax({
						url: '../inventory/locationRecoverNew',
						type: 'post',
						dataType: 'json',
						data: {
							location_id: val[trIndex].location_id
						},
						success: function (res) {
							console.log(res);
							if (res.result == 'failure') {
								alert(res.note);
								return false;
							} else {
								alert("恢复成功");
								$('tbody tr').eq(trIndex).remove();
							}
						}
					});
				});
				// 编辑
				$('.update').on('click', function () {
					$('.alert').show();
					var customersName = '';
					var trIndex = $(this).parent().parent().index();
					$('.alertTitle').html('编辑库位');
					$('.save').data('saveType', 'update');
					$('#lcCodeNew').attr('disabled', true);
					$('.disabled').attr('disabled', true);
					// 显示默认信息
					$('#lcCodeNew').val(val[trIndex].location_barcode);
					$.each($('#lcTypeNew option'), function(index, value) {
						if ($(value).val() == val[trIndex].location_type) {
							$('#lcTypeNew option').eq(index).prop('selected', true);
						}
					});
					$('#lcArea').val(val[trIndex].partition_id);
					$('#lcPai').val(val[trIndex].aisle);
					$('#lcLie').val(val[trIndex].bay);
					$('#lcCeng').val(val[trIndex].lev);
					$('#putaway').val(val[trIndex].putaway_seq);
					$('#orderpick').val(val[trIndex].pick_seq);
					// 仓库
					$('#lcWarehouse').val(res.currentPhysicalWarehouse.warehouse_name);
					// 货主
					customersName = '<option value="">--请选择--</option>';
					for (var i = 0; i < res.customers.length; i++) {
						
						if (res.customers[i].customer_id == val[trIndex].customer_id) {
							customersName += '<option selected="true" value='+res.customers[i].customer_id+'>'+res.customers[i].name+'</option>';
						}else{
							customersName += '<option value='+res.customers[i].customer_id+'>'+res.customers[i].name+'</option>';
						}
					}
					$('#lcCustomer').html(customersName);
					$('#lcStatusNew').val(val[trIndex].is_empty); // 库位状态
					$('#lcIDNew').val(val[trIndex].location_id);
					$('#lcPriceLevel').val(val[trIndex].price_class);
					$('#lcLoopLevel').val(val[trIndex].circle_class);
					$('#is_mixupsGoods').val(val[trIndex].can_mix_product);
					$('#is_mixupsBatch').val(val[trIndex].can_mix_batch);
					$('#is_count').val(val[trIndex].not_auto_recmd);
					$('#stockNum').val(val[trIndex].max_lpn_qty);
					$('#long').val(val[trIndex].length);
					$('#width').val(val[trIndex].width);
					$('#height').val(val[trIndex].height);
					$('#volume').val(val[trIndex].volume);
					$('#weight').val(val[trIndex].weight);
					$('#X').val(val[trIndex].axis_x);
					$('#Y').val(val[trIndex].axis_y);
					$('#Z').val(val[trIndex].axis_z);
					$('#numLimit').val(val[trIndex].max_prod_qty);
					$('#ignoreLPN').val(val[trIndex].ignore_lpn);
					if($("#lcTypeNew").val()=="PACKBOX_PICK_LOCATION"){
						$(".customerList").show();
					
						if($("#lcCustomer").find("option").size()>1){
							$("#lcCustomer").find("option").eq(0).removeAttr("selected").hide();
						}
					}else{
						$(".customerList").hide();
						if($("#lcCustomer").find("option").size()>1){
							$("#lcCustomer").find("option").eq(0).show();;
						}					
					}
				});
			}
			
			$("#lcTypeNew").on("change",function(){
				if($(this).val()=="PACKBOX_PICK_LOCATION"){
					$(".customerList").show();
				
					if($("#lcCustomer").find("option").size()>1){
						$("#lcCustomer").find("option").eq(0).removeAttr("selected").hide();
						$("#lcCustomer").find("option").eq(1).attr("selected",true);
						$("#lcCustomer").val($("#lcCustomer").find("option").eq(1).val());
					}
				}else{
					$(".customerList").hide();
					if($("#lcCustomer").find("option").size()>1){
						$("#lcCustomer").find("option").eq(0).attr("selected",true).show();;
					}					
				}
			})
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
						CURRENT_PAGE = index;
						$.ajax({
							url : "../inventory/locationNew",
							type : "get",
							dataType : "json",
							data : {
								currentPage : index,
								pageSize : 10,
								locationType: $('.locationType').val(),
								locationStatus: $('.locationStatus').val(),
								location_barcode: $('.locationCode').val(),
								is_delete: $('.is_delete').val()
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
			// 库位信息
			function lcMsg () {
				var data = {
					location_barcode: $('#lcCodeNew').val(),
					location_type: $('#lcTypeNew').val(),
					partition_id: $('#lcArea').val(),
					aisle: $('#lcPai').val(),
					bay: $('#lcLie').val(),
					lev: $('#lcCeng').val(),
					physical_warehouse_id: $('#').val(), // 仓库
					customer_id: $('#lcCustomer').val(),
					putaway_seq: $('#putaway').val(),
					pick_seq: $('#orderpick').val(),
					//is_empty: $('#lcStatusNew').val(),
					location_id: $('#lcIDNew').val(),
					price_class: $('#lcPriceLevel').val(),
					circle_class: $('#lcLoopLevel').val(),
					can_mix_product: $('#is_mixupsGoods').val(),
					can_mix_batch: $('#is_mixupsBatch').val(),
					not_auto_recmd: $('#is_count').val(),
					max_lpn_qty: $('#stockNum').val(),
					length: $('#long').val(), 
					width: $('#width').val(),  // 宽
					height: $('#height').val(),  
					volume: $('#volume').val(),  // 体积
					weight: $('#weight').val(),  // 重量
					axis_x: $('#X').val(),
					axis_y: $('#Y').val(),
					axis_z: $('#Z').val(),
					max_prod_qty: $('#numLimit').val(),
					ignore_lpn: $('#ignoreLPN').val(),
					pick_seq:$('#orderpick').val(),
					putaway_seq: $("#putaway").val()
				};
				return data;
			}
			// 新增库位
			$('.newLocation').click(function () {
				$('.alert').show();
				$('.alertTitle').html('新增库位');
				$('.save').data('saveType', 'new');
				$('.alert').find('input[type="text"]').val('');
				$('.alert').find('select option:first-child').prop('selected', true);
				$('#lcCodeNew').attr('disabled', false);
				$('.disabled').attr('disabled', false);
				$.ajax({
					url: '../inventory/getLocationInfo',
					type: 'post',
					dataType: 'json',
					data: '',
					success: function (res) {
						console.log(res);
						$('#lcWarehouse').val(res.currentPhysicalWarehouse.warehouse_name);
						// 货主
						var customersName = '<option value="">--请选择--</option>';
						for (var i = 0; i < res.customers.length; i++) {
							customersName += '<option value='+res.customers[i].customer_id+'>'+res.customers[i].name+'</option>';
						}
						$('#lcCustomer').html(customersName);
					}
				});
			});
			// 保存
			$(document).on('click','.save', function () {
				var reg = /^[1-9]\d*$/;
				if (!$('#lcCodeNew').val() || !$('#lcTypeNew').val()) {
					alert('请填写库位编码和库位类型');
					return false;
				} else if ($('.sequence').val() && !reg.test($('.sequence').val())) {
					alert('上架顺序和拣货顺序必须为数字');
					return false;
				} 
				else 
					if (('PACKBOX_PICK_LOCATION'==$('#lcTypeNew').val()) && (''== $('#lcCustomer').val())) {
					alert('耗材拣货区必需指定货主');
					return false;
				}
				
				else {
					console.log($('.save').data('saveType'));
					if ($('.save').data('saveType') == 'new') {
						$.ajax({
							url: '../inventory/locationInsertNew',
							type: 'post',
							dataType: 'json',
							data: lcMsg(),
							success: function (res) {
								console.log(res);
								alert(res.note);
								if (res.result == 'success') {
									$('.alert').hide();
									search();
								} 
							}
						});
					} else {
						$.ajax({
							url: '../inventory/locationUpdateNew',
							type: 'post',
							dataType: 'json',
							data: lcMsg(),
							success: function (res) {
								console.log(res);
								alert(res.note);
								if (res.result == 'success') {
									$('.alert').hide();
									search();
								} 
							}
						});
					}
				}
			});
			// 取消
			$('.cancel').click(function () {
				$('.alert').hide();
				$(".customerList").css("display","none");
			});
			// 批量导入
			// function inputAll() {
			// 	if ("${resMap.result}" == "success") {
			// 		var resMapNote = "${resMap.excel_note}";
			// 		alert(resMapNote);
			// 	}
			// }
			// $('.inputAll').on('click', function () {
			// 	var resMapNote = "${resMap.excel_note}";
			// 	alert(resMapNote);
			// });
		});
	</script>
</body>
</html>