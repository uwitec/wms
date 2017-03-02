
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>库存冻结</title>
<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<link href="<%=request.getContextPath() %>/static/css/autocomplete.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script type="text/javascript" src="../static/js/autocomplete.js"></script>
<script type="text/javascript" src="../static/js/zapatec/utils/zapatec.js"></script>
<script type="text/javascript" src="../static/js/zapatec/zpcal/src/calendar.js"></script>
<script type="text/javascript" src="../static/js/zapatec/zpcal/lang/calendar-en.js"></script>
<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />

<style type="text/css">
	.modal-wrap{
    width:100%;
    height: 100%;
    position: fixed;
    top: 0;
    left: 0;
    background-color: rgba(0,0,0,.5);
    z-index: 999;
    display: none;
}
.modal-content{
    margin:100px auto 0 auto;
    width: 450px;
    max-height:400px;
	overflow:none;
    padding:20px 30px;
    position: relative;
    background-color:white;
    border-radius: 10px;
}
.modal-close {
    font-size: 30px;
    width: 36px;
    height: 36px;
    display: block;
    position: fixed;
    top:100px;
    right:35%;
    cursor: pointer;
    z-index: -1;
}
.page-wrap {
	position: relative;
	top: -10px;
	left: 990px;
}
.noDataTip {
	/*display: none;*/
	text-align: center;
	font-size: 30px;
	color: #666;
	margin: 30px 0 0 0;
}
</style>

<script type="text/javascript">
$(document).ready(function(){
	 $(".modal-close").click(function(){
	        $(this).parents(".modal-wrap").hide();
	    });
	 	$(".modal-wrap").click(function(event){
	        var target = $(event.target).attr("class");
	        if (target == "modal-wrap") {
	            $(".modal-wrap").hide();
	        }
	    });
	 	
	 	$('#goods_name2').autocomplete('../inventory/search_goods?method=ajax', {
 			dataType : 'json',
 			minChars: 0,
 			mustMatch: false,
 			formatItem : function(row, i, max, value, term) {
 				return(row.product_name);
 			},
 			formatResult : function(row) {
 				return(row.product_name);
 			}
 	    	}).result(function(event, row, formatted) {
 			 $('#goods_name2').val(row.product_name);
 			 $('#product_id2').val(row.product_id);
 			 $('#customer_id2').val(row.customer_id);
 		  }); 
});

	function show_add(){
		act = 'insert';
		show_dia();
	}
	
	function show_dia(){
		$('.modal-table-content').parents(".modal-wrap").fadeIn();
	}
	
	function checkupdate(obj){
		var mapping_id= $(obj).closest('tr.list').find('.mapping_id').attr('value');
		var warehouse_id= $(obj).closest('tr.list').find('.warehouse_id').attr('value');
		var product_id= $(obj).closest('tr.list').find('.product_id').attr('value');
		var reserve_number= $(obj).closest('tr.list').find('.reserve_number').attr('value');
		var freeze_reason= $(obj).closest('tr.list').find('.freeze_reason').attr('value');
		var goods_name= $(obj).closest('tr.list').find('.goods_name_').attr('value');
		var customer_id= $(obj).closest('tr.list').find('.customer_id').attr('value');
		
		$('#mapping_id2').val(mapping_id);
		
		$('#product_id2').val(product_id);
		
		$('#goods_name2').val(goods_name);
		$('#warehouseId2').val(warehouse_id);
		$('#ori_warehouse_id').val(warehouse_id);
		$('#reserve_number2').val(reserve_number);
		$('#freeze_reason2').val(freeze_reason);
		$('#customer_id2').val(customer_id);
		
		act = 'update';
		show_dia();
	}
	
	function checkdelete(obj){
		var mapping_id= $(obj).closest('tr.list').find('.mapping_id').attr('value');
		var warehouse_id= $(obj).closest('tr.list').find('.warehouse_id').attr('value');
		var product_id= $(obj).closest('tr.list').find('.product_id').attr('value');
		if(!confirm('确认删除？')){
			return;
		}
		var data = "act=delete&mapping_id="+mapping_id+"&warehouse_id="+warehouse_id+"&product_id="+product_id+"&ori_warehouse_id=1&reserve_number=0&freeze_reason=1&customer_id=1";
		$.ajax({
			url: '../inventory/ajax',  
			data : data,
			dataType : 'json',
			success : function(data){
				alert('删除成功');
				location.reload();
			},
			error : function(data){
				alert('删除失败，请联系ERP组');
			}
		});
	}
	function update_reserve(){
		var mapping_id =$('#mapping_id2').val();
		var warehouse_id =$('#warehouseId2').val();
		var ori_warehouse_id = $('#ori_warehouse_id').val();
		var product_id =$('#product_id2').val();
		var reserve_number = $('#reserve_number2').val();
		var customer_id = $('#customer_id2').val();
		var freeze_reason = $('#freeze_reason2').val();
		
		if(product_id == null || product_id == 0 || product_id == "undefined"){
			alert("请填写商品");
			return false;
		}
		
		if(warehouse_id == null || warehouse_id == 0 || warehouse_id == "undefined"){
			alert("请填写仓库");
			return false;
		}
		
		if(reserve_number == null || reserve_number == 0 || reserve_number == "undefined "){
			alert("请填写库存冻结数量");
			return false;
		}
		
		if(! /^\d+$/.test(reserve_number)){
			alert("冻结数量非数字");
			return false;
		}
		
		if(freeze_reason == null || freeze_reason == "" || freeze_reason =="undefined"){
			alert("请填写库存冻结原因");
			return false;
		}
		
		
		
		if(ori_warehouse_id == null || ori_warehouse_id == ""){
			ori_warehouse_id = 0;
		}
		if(mapping_id == null || mapping_id == ""){
			mapping_id = 0;
		}
		
		var data = "act="+act+"&mapping_id="+mapping_id+"&warehouse_id="+warehouse_id+"&product_id="+product_id+"&ori_warehouse_id="+ori_warehouse_id+"&reserve_number="+reserve_number+"&freeze_reason="+freeze_reason+"&customer_id="+customer_id;
		$.ajax({
			url: '../inventory/ajax',  
			data : data,
			dataType : 'json',
			success : function(data){
				if(data.result == 'failure')
					alert(data.note);
				location.reload();
			},
			error : function(data){
				alert('更新失败，请联系ERP组');
			}
		});
		$('.modal-table-content').parents(".modal-wrap").fadeOut();
	}
	function cancle(){
		$('.modal-table-content').parents(".modal-wrap").fadeOut();
	}
	function ToUrl(pageNo)   
	{   
		var customer_id = $('#customer_id').val();
		var goods_name = $('#goods_name').val();
		var sku_code = $('#sku_code').val();
		var warehouse_id = $('#warehouse_id').val();
		var barcode = $('#barcode').val();
		
		var data ="";
		if(customer_id != '' && customer_id != null){
			data += ("&customer_id="+customer_id);
		}
		
		if(goods_name != '' && goods_name != null){
			data += ("&goods_name="+goods_name);
		}
		
		if(sku_code != '' && sku_code != null){
			data += ("&sku_code="+sku_code);
		}
		
		if(warehouse_id != '' && warehouse_id != null){
			data += ("&warehouse_id="+warehouse_id);
		}
		
		if(barcode != '' && barcode != null){
			data += ("&barcode="+barcode);
		}
		
		window.location.href= "../inventory/freeze?currentPage="+pageNo+"&pageSize=10"+data;
	}  
</script>
</head>
<body>
	<div class="main-container">
			<form method="post" action="../inventory/freeze" class="lq-form-inline">
				<div class="lq-row">
					<div class="lq-col-3">
						<div class="lq-form-group">
							<label for="customer_id">选择货主:</label>
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
						        <label>仓库名称:</label>
			          			<select name="warehouse_id" class="lq-form-control"> 
									<option value="">--请选择--</option> 
									  <c:forEach items="${warehouseList}" var="warehouse"> 
									      <option name="warehouse_id" 
									      <c:if test="${warehouse_id==warehouse.warehouse_id}">selected="true"</c:if>
									      value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option> 
									 </c:forEach> 
								</select> 	
						</div>
					</div>
					<div class="lq-col-3">
						<div class="lq-form-group">
								<label>商品名称:</label>
						        <input type="text" name="goods_name" value="${goods_name}" id="goods_name" class="lq-form-control"/>
						</div>
						<div class="lq-form-group">		
					          	<label>商品条码:</label>
						        <input type="text" name="barcode" value="${barcode}" id="barcode" class="lq-form-control"/>
					    </div>
					</div>
					<div class="lq-col-3">
					    <div class="lq-form-group">
					          	<label>商家编码:</label>
					          	<input type="text" name="sku_code" value="${sku_code}" id="sku_code" class="lq-form-control"/>
					    </div>
						<div class="lq-form-group">
						         <input type="submit" value="查询" class="lq-btn lq-btn-sm lq-btn-primary searchBtn"/>
							     <input type="hidden" name="act" value="search" />
							     <input type="button" value="添加" id="add" class="lq-btn lq-btn-sm lq-btn-primary" onclick="show_add();"></a>
						</div>
					</div>
				</div>
			</form>
		
		
		<div>
		 <form method="post" id="form">
		 <table class="lq-table" style="margin-top:10px;" id="batchTable">
			<thead>
				<tr>
				<th>序号</th>
				<th>商家编码</th>
				<th>商品名</th>
				<th>商品条码</th>
				<th>仓库名</th>
				<th>冻结数量</th>
				<th>冻结原因</th>
				<th>操作</th>
				</tr>
			</thead>
			<c:if test="${fn:length(freezeGoods) == '0'}">
			</table>
				<div class="noDataTip">未查到符合条件的数据!</div>
			</c:if> 
			<c:forEach items="${freezeGoods}" varStatus="i" var="good">
				<tr align="center" class="list">
					<td>${i.index+1}</td>
					<td>${good.sku_code}</td>
					<td>${good.product_name}</td>
					<td>${good.barcode}</td>
					<td>${good.warehouse_name}</td>
					<td>${good.reserve_number}</td>
					<td>${good.freeze_reason}</td>
					<td>
						<input type="hidden" name="mapping_id" class="mapping_id" value="${good.mapping_id}" />
						<input type="hidden" name="goods_name_" class="goods_name_" value="${good.product_name}" />
						<input type="hidden" name="warehouse_id" class="warehouse_id" value="${good.warehouse_id}" />
						<input type="hidden" name="product_id" class="product_id" value="${good.product_id}" />
						<input type="hidden" name="customer_id" class="customer_id" value="${good.customer_id}" />
						<input type="hidden" name="reserve_number" class="reserve_number" value="${good.reserve_number}" />
						<input type="hidden" name="freeze_reason" class="freeze_reason" value="${good.freeze_reason}" />
						<input type="button" value="调整" onclick="checkupdate(this)" class="lq-btn lq-btn-sm lq-btn-primary" />
						<input type="button" value="删除" onclick="checkdelete(this)" class="lq-btn lq-btn-sm lq-btn-primary" />
					</td>
				</tr>
			</c:forEach>
		</table>
		</form>
		</div>
		
		<c:if test="${page.totalPage>1}">	
				<div class="page-wrap">
					<div class="lq-row">
						<nav id="page-search">
							<ul class="lq-page-control">
								<li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>
								<c:forEach var="item" varStatus="i" begin="1" end="${page.totalPage}">
									<li <c:if test="${i.index==page.currentPage}"> 
									class="active"</c:if>><a href="javascript:void(0);" 
									onclick="ToUrl('${i.index}')">${i.index}</a></li>
								</c:forEach>
								<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li>
							</ul>
						</nav>
					</div>
				</div>
		</c:if>
	
	<div class="modal-wrap">
            <div class="modal-content">
                <div class="modal-close">
                    <i class="fa fa-times"></i>
                </div>
                <div class="modal-table-content">
                		<input type="hidden" id="product_id2"/>
                		<input type="hidden" id="mapping_id2"/>
                		<input type="hidden" id="customer_id2"/>
                		<input type="hidden" id="ori_warehouse_id"/>
						商品名：&nbsp;&nbsp;&nbsp;<input id="goods_name2" style="width:280px" /><br/>
						<br/>
						
						商品仓库：<select name="warehouseId2" id="warehouseId2"> 
										<option value="">--请选择--</option> 
											  <c:forEach items="${warehouseList}" var="warehouse"> 
											      <option name="warehouse_id" 
											      <c:if test="${warehouseId2==warehouse.warehouse_id}">selected="true"</c:if>
											      value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option> 
											 </c:forEach> 
									</select> 
						<br/><br/>
                		冻结数：&nbsp;&nbsp;&nbsp;<input type="text" id="reserve_number2"/><br/>
                		<br/>
                		冻结原因：<input type="text" id="freeze_reason2" style="width:280px"/><br/>
                		<br/>
						<div align="center"><input value="确定" type="button" class="btn btn-default" onclick="update_reserve()"/>
						&nbsp&nbsp<input value="取消" class="btn btn-default" type="button" onclick="cancle()" /></div>
                </div>
            </div>
      </div>
</body>
</html>