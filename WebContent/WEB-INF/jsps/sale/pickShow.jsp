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
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>打印波次单</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <style>
/*     #body-sale-search .lq-form-inline label {
    	width: 5.4em;
    } */

    #btn-sale-search {
    }

    #page-search {
    	float: right;
    }
	 #print{
	  width: 0px;
	  height: 0px;
	  border: 0px;
	}
    .noDataTip {

    	text-align: center;
    	font-size: 30px;
    	color: #666;
    	margin: 30px 0 0 0;
    }
    .totalPage {
    	position: relative;
    	top: 5px;
    	margin: 5px;
    }
    
    input[type=text]{
    	height: 30px;
    }
  	#body-sale-search .lq-btn[disabled]{
    	background-color:#337ab7
    }
    </style>
    <script type="text/javascript">
    $(document).ready(function(){
    	
    });
    
    
    function print_pick_tag(obj){
    	$(".mw-loading").fadeIn(300);
    	var print_batch_pick_id= $(obj).closest('tr.list').find('.print_batch_pick_id').attr('value');
    	//var print_batch_pick_sn= $(obj).closest('tr.list').find('.print_batch_pick_sn').attr('value');
		src="../sale/print_pick_tag?batch_pick_id="+print_batch_pick_id;
  		$('#print').attr('src',src); 
  		$(".mw-loading").fadeOut(100);
    }
    
    function print_pick(obj){
    	$(".mw-loading").fadeIn(300);
    	var print_batch_pick_id= $(obj).closest('tr.list').find('.print_batch_pick_id').attr('value');
    	var print_batch_pick_sn= $(obj).closest('tr.list').find('.print_batch_pick_sn').attr('value');
    	var batch_pick = print_batch_pick_id+'&'+print_batch_pick_sn;
		src="../sale/print_pick?batch_pick="+batch_pick;
  		$('#print').attr('src',src); 
  		$(".mw-loading").fadeOut(100);
    }
    
    function print_delivery(obj){
		var that = $(this);
		
		var print_batch_pick_sn= $(obj).closest('tr.list').find('.print_batch_pick_sn').attr('value');
		//alert(print_batch_pick_sn);
		var saleSearchData = {
			batch_pick_id : print_batch_pick_sn
		};
		$.ajax({
			url : "../sale/search_orders",
			type : "get",
			dataType : "json",
			data : saleSearchData,
			beforeSend : function(){
				$(".mw-loading").fadeIn(300);
				//that.prop("disabled",true);
			},
			success : function(data) {
				window.setTimeout(function(){
					console.log(data)
					$(".mw-loading").fadeOut(300);
					//that.prop("disabled",false);
					console.log(data.pickOrderList);
					var tagList = "";
					var dataList = data.pickOrderList;
					for (var key in dataList){
						console.log(dataList);
						tagList += dataList[key].order_id;
						tagList += ",";
					}
					tagList = tagList.substring(0,tagList.lastIndexOf(','));
					console.log(tagList);
					if(tagList.length){
						src="../sale/print_card?order_id="+tagList;
		    			$('#print').attr('src',src); 
					}
				},300);
			},
			error : function(error) {
				window.setTimeout(function(){
					$(".mw-loading").fadeOut(300);
					//that.prop("disabled",false);
					alert("操作失败，请刷新!");
					console.log(error);
				},300);
				that.prop("disabled",false);
			}
		});
	}
    
    function ToUrl(pageNo)   
	{   
    	var customer_id = $('#customer_id').val();
    	var warehouse_id = $('#warehouse_id').val();
    	var batch_pick_sn = $('#batch_pick_sn').val();
    	var start = $('#start').val();
    	var end = $('#end').val();
    	var print_status = $('#print_status').val();
    	var pick_status = $('#pick_status').val();
    	var reserve_status = $('#reserve_status').val();
    	var is_batch = $('#is_batch').val();
    	
    	var data ="";
    	if(customer_id != '' && customer_id != null){
    		data += ("&customer_id="+customer_id);
    	}
    	if(warehouse_id != '' && warehouse_id != null){
    		data += ("&warehouse_id="+warehouse_id);
    	}
		
    	if(batch_pick_sn != '' && batch_pick_sn != null){
    		data += ("&batch_pick_sn="+batch_pick_sn);
    	}
		
    	if(start != '' && start != null){
    		data += ("&start="+start);
    	}
    	
    	if(end != '' && end != null){
    		data += ("&end="+end);
    	}
    	
    	if(print_status != '' && print_status != null){
    		data += ("&print_status="+print_status);
    	}
    	
    	if(pick_status != '' && pick_status != null){
    		data += ("&pick_status="+pick_status);
    	}
    	
    	if(reserve_status != '' && reserve_status != null){
    		data += ("&reserve_status="+reserve_status);
    	}
    	
    	if(is_batch != '' && is_batch != null){
    		data += ("&is_batch="+is_batch);
    	}
    	
		window.location.href= "../sale/pickShow?currentPage="+pageNo+"&pageSize=15"+data;
	}  
    /*****************全选*******************/
    function select_all(node, type)
    {
        node = node ? node : document ;
        $(node).find("input[name='check_batch_pick[]']:enabled").each(function(i){
    		this.checked = true;
    	});
    }

    /*****************反选*****************/
    function select_reverse(node)
    {
    	node = node ? node : document ;
    	$(node).find("input[name='check_batch_pick[]']:enabled").each(function(i){
    		this.checked = !this.checked;
    	});
    }

    /*****************清空*******************/
    function select_none(node, type)
    {
        node = node ? node : document ;
        $(node).find("input[name='check_batch_pick[]']:enabled").each(function(i){
    		this.checked = false;
    	});
    }
    
    function check_submit(node)
    {
    	node = node ? node : document ;
    	item = $(node).find(':checkbox:checked');
    	console.log(item);
    	if (!item || item==undefined || item.length<1) {
    		alert('没有选中项');
    		return false;
    	}
    	var fileName=$("input[name='check_batch_pick[]']:checked").eq(0).next("#fileName").val();
    	var orderList= new Array();
    	var isSame=true;
    	console.log(fileName);
    	$("input[name='check_batch_pick[]']:checked").each(function(){ 
    		var batch_pick_id = $(this).closest('tr.list').find('.check_batch_pick_id').attr('value');
            orderList.push($(this).val()+'_'+batch_pick_id);
            if(fileName!=$(this).next("#fileName").val()) isSame=false;
    	})
    	src="../sale/print_pick?batch_pick="+orderList;
    	console.log(src);
    	if(!isSame){
        	if(confirm("选中的波次单中存在不同的发货单，确定使用第一个波次单的发货单模板进行打印吗？")){
          		$('#print').attr('src',src); 
          		$(".mw-loading").fadeOut(100);
          		$(".batch_print_pick").attr('disabled', 'disabled');
        		$(".batch_print_delivery").removeAttr('disabled');   		
        	}   		
    	}else{
      		$('#print').attr('src',src); 
      		$(".mw-loading").fadeOut(100);
      		$(".batch_print_pick").attr('disabled', 'disabled');
    		$(".batch_print_delivery").removeAttr('disabled');   		
    	}
    }
    function check_submit2(node)
    {
    	node = node ? node : document ;
    	item = $(node).find(':checkbox:checked');
    	console.log(item);
    	if (!item || item==undefined || item.length<1) {
    		alert('没有选中项');
    		return false;
    	}
    	 
    	var orderList= new Array();
    	$("input[name='check_batch_pick[]']:checked").each(function(){ 
    		var batch_pick_id = $(this).closest('tr.list').find('.check_batch_pick_id').attr('value');
            orderList.push(batch_pick_id);
            //orderList.push($(this).val()+'_'+batch_pick_id);
            console.log(orderList)
    	})
    	
    	$.ajax({
			url : "../sale/search_orders",
			type : "post",
			dataType : "json",
			data : JSON.stringify(orderList),
			contentType:"application/json; charset=utf-8",
			beforeSend : function(){
				$(".mw-loading").fadeIn(300);
				//that.prop("disabled",true);
			},
			success : function(data) {
				window.setTimeout(function(){
					$(".mw-loading").fadeOut(300);
					//that.prop("disabled",false);
					console.log(data.pickOrderList);
					var dataList = data.pickOrderList;
					var dataListLen = dataList.length;
					var src = "../sale/print_card_v2?batch_pick_id=";
					if(dataListLen){
						for (var i = 0; i < orderList.length; i++) {
							src += orderList[i];
							if (orderList.length - i >= 2) {
								src += "_";
							}
			    		} 
			    		$('#print').attr('src',src);
			    		console.log(src)
					}
					$(".batch_print_delivery").attr('disabled', 'disabled');
				},300);
			},
			error : function(error) {
				window.setTimeout(function(){
					$(".mw-loading").fadeOut(300);
					//that.prop("disabled",false);
					alert("操作失败，请刷新!");
					console.log(error);
				},300);
				that.prop("disabled",false);
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
    }
    </script>
</head>
<iframe id="print" src=""></iframe>
<body id="body-sale-search">
	
	<div class="modal-wrap mw-loading">
		<div class="modal-content mc-loading">
			<i class="fa fa-spinner fa-spin" id="loading"></i>
		</div>
	</div>
	<div class="main-container">
		<form method="post" action="../sale/pickShow" class="lq-form-inline">
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
						<label for="batch_pick_sn">波次单号:</label>
						<input type="text" name="batch_pick_sn" value="${batch_pick_sn}" id="batch_pick_sn" style="width:150px" class="lq-form-control">
					</div>
				</div>
				<div class="lq-col-2">
					<div class="lq-form-group">
						<label for="print_status">打印状态:</label>
						<select name="print_status" id="print_status" class="lq-form-control"> 
							
							<option name="print_status" 
					      	<c:if test="${'N'== print_status}">selected="true"</c:if>
					      	value="N">未打印</option>
					      	
					      	<option name="print_status" 
					     	 <c:if test="${'Y'== print_status}">selected="true"</c:if>
					      	value="Y">已打印</option> 
						</select> 
					</div>
					<div class="lq-form-group">
						<label for="pick_status">波次状态:</label>
						<select name="pick_status" id="pick_status" class="lq-form-control"> 
							<option value="">--请选择--</option> 
							
							<option name="pick_status" 
					      	<c:if test="${'INIT'== pick_status}">selected="true"</c:if>
					      	value="INIT">未分配</option>
					      	
					      	<option name="pick_status" 
					     	 <c:if test="${'BINDED'== pick_status}">selected="true"</c:if>
					      	value="BINDED">已分配</option> 
						</select> 
					</div>
				</div>
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="start">生成时间:</label>
						<input class="form-control form_datetime lq-form-control" id="start" type="text" name="start" value="${start}" size="12" readonly/>
					</div>
					<div class="lq-form-group">
						<label for="end">结束时间:</label>
						<input class="form-control form_datetime lq-form-control" id="end" type="text" name="end" value="${end}" size="12" readonly/>
					</div>
				</div>
				
				<div class="lq-col-2">
					<div class="lq-form-group">
						<label for="reserve_status" style="text-indent: 1rem;">分配状态:</label>
						<select name="reserve_status" id="reserve_status" class="lq-form-control"> 
					      	<option name="reserve_status" <c:if test="${'Y'== reserve_status}">selected="true"</c:if>
					      	value="Y">已分配</option> 
							<option name="reserve_status"  <c:if test="${'N'== reserve_status}">selected="true"</c:if>
					      	value="N">未分配</option>
					      	<option name="reserve_status"  <c:if test="${'E'== reserve_status}">selected="true"</c:if>
					      	value="E">分配失败</option>
						</select> 
					</div> 
					<div class="lq-form-group">
						<label for="is_batch">是否批量单:</label>
						<select name="is_batch" id="is_batch" class="lq-form-control">
							<option value="">--请选择--</option> 
							<option name="is_batch"  <c:if test="${'N'== is_batch}">selected="true"</c:if>
					      	value="N">否</option> 
					      	<option name="is_batch" <c:if test="${'Y'== is_batch}">selected="true"</c:if>
					      	value="Y">是</option> 
						</select> 
					</div> 
				</div>
				<div class="lq-col-2">
				
<%-- 					<div class="lq-form-group">
						<label for="warehosue_id">渠道:</label>
						<select name="warehouse_id" id="warehouse_id" style="width: 65%;">
							<option value="">不限</option>
							<c:forEach items="${warehouseList}" var="warehouse">
									<option value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
							</c:forEach>
						</select>						
					</div> --%>
					<div class="lq-form-group">
						<label for="warehosue_id">渠道:</label>
						<select name="warehouse_id" id="warehouse_id" style="width: 65%;">
							<option value="">不限</option>
							<c:forEach items="${warehouseList}" var="warehouse">
									<option value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
							</c:forEach>
						</select>						
					</div>
					<div class="lq-form-group">
						<button type="submit" id="btn-sale-search" class="lq-btn lq-btn-sm lq-btn-primary">
							<i class="fa fa-search"></i>查询
						</button>
						<input type="hidden" name="act" value="search">
					</div>
				</div>
			</div>         	
		</form>
	
		<div>
		<form method="post" id="form">
		<c:if test="${print_status == 'N' and 'Y'== reserve_status}">
			<div style="margin: 10px 0 10px 0; clear:both;"> 
	      		 <input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="全选" onclick="select_all('#batchTable');" /> &nbsp;
	     			<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="清空" onclick="select_none('#batchTable');" /> &nbsp;
	     			<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="反选" onclick="select_reverse('#batchTable');" /> &nbsp;
	        		<input class="lq-btn lq-btn-sm lq-btn-primary batch_print_pick" type="button" id="batch_print_pick" value="打印选中波次单" onclick="check_submit('#form')" />
	        		<input class="lq-btn lq-btn-sm lq-btn-primary batch_print_delivery"  type="button" id="batch_print_delivery" value="打印选中发货单" onclick="check_submit2('#form')" disabled />
	        		<input type="hidden" name="act" value="batch_print_pick" />
	      	</div>
      	</c:if>
		<table class="lq-table" id="batchTable">
		 	<thead>
				<tr>
					<th class="Check">选择</th>
					<th>波次单号</th>
					<th>货主</th>
					<th>渠道</th>
					<th>订单数量</th>
					<th>生成波次时间</th>
					<th>首次打印时间</th>
					<th>是否批量单</th>
					<th>发货单模板</th>
				</tr>
			</thead>
			<tbody class="tbody"></tbody>
			</table>
			<div class="noDataTip">未查到符合条件的数据!</div>
		</table>
		</form>
		</div>
		<div class="page-wrap">
			<div class="lq-row">
				<nav id="page-search" style="display:none">
					<ul class="lq-page-control">
						<li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>
						 <c:forEach var="item" varStatus="i" begin="1" end="${page.totalPage}">
							 <li class="page"><a href="javascript:void(0);"></a></li>
						 </c:forEach>
						<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li>
					</ul>
				</nav>
			</div>
	</div>			
	</div>
	<script type="text/javascript" src="../static/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../static/js/bootstrap-datetimepicker.min.js"></script>	
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/v3.js"></script>
	<script type="text/javascript">
	$('.form_datetime').datetimepicker({
 	    //language:  'fr',
 	    language: 'zh',
 	    weekStart: 1,
 	    todayBtn:  1,
 		autoclose: 1,
 		todayHighlight: 1,
 		startView: 2,
 		forceParse: 0,
 	    showMeridian: 1,
 	    minView: 'month',
 	    format: 'yyyy-mm-dd'
 	});
  	</script>
	<script type="text/javascript">

		//点查询对页面初始化
		function InitTable(){
			$("tbody.tbody").html("");
			$("ul.lq-page-control").remove();
		}
		//AJAX使用查询
		var TOTAL_PAGE = 0, START_PAGE = 1, MAX_PAGE = 10, END_PAGE = 10, CURRENT_PAGE = 1, PAGE_DATA = {}, TOTAL_ORDER = 0;
		
		$("#btn-sale-search").click(function(e){
			e.preventDefault();
			InitTable();
			var data={};
			data={
					customer_id:$("#customer_id").val(),
					print_status:$("#print_status").val(),
					start:$("#start").val(),
					end: $("#end").val(),
					reserve_status: $("#reserve_status").val(),
					batch_pick_sn:$("#batch_pick_sn").val(),
					pick_status: $("#pick_status").val(),
					is_batch: $("#is_batch").val(),
					warehouse_id:$("#warehouse_id").val()
				/* 	warehouse_id:$("#warehouse_id").val() */
			}

			$.ajax({
				url: '../sale/pickShowV2',
				type:'post',
				dataType:'json',
				data: data,
				beforeSend : function(){
					$(".mw-loading").fadeIn(300);
					//that.prop("disabled",true);
				},
				success:function(res){
					$(".mw-loading").fadeOut(300);
					console.log(res);
					$("#batch_print_pick").removeAttr("disabled");
					if(res.pickOrderList.length>0){
						
						$(".noDataTip").fadeOut(300);
						PAGE_DATA = res.page;
						TOTAL_PAGE = parseInt(res.page.totalPage);
						TOTAL_ORDER = res.page.totalCount;
						START_PAGE = 1;
						if (TOTAL_PAGE < MAX_PAGE) {
							END_PAGE = TOTAL_PAGE;
						} else {
							END_PAGE = MAX_PAGE;
						}
						CURRENT_PAGE = res.page.currentPage;
						pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
						showTable(res);
					}else {
						$(".noDataTip").fadeIn(300);
					}
				},
				error: function(err){
					alert(err);
				}
			})
		})
		//生成波次时间转化
		function str2Date(str){
			var dateTime=new Date(str);
			var year=dateTime.getFullYear();
			var month=dateTime.getMonth()+1;
			var day=dateTime.getDate();
			if(parseInt(month)<10) month="0"+month;
			if(parseInt(day)<10) day="0"+day;
			return year+'-'+month+"-"+day+" "+dateTime.toString().split(' ')[4];
		}
		function showTable(res){
			var tdHtml='';
			var print_status=res.print_status;
			var reserve_status=res.reserve_status;
			var data=res.pickOrderList;
			$(".Check").css("display","none");
			for(var i in data){
				tdHtml+='<tr class="list">';
				if(print_status == "N"&&reserve_status == "Y"){
					$(".Check").css("display","block");
					tdHtml+='<td><input type="checkbox" name="check_batch_pick[]" value="'+data[i].batch_pick_sn+'" class="check" /><input type="hidden" id="fileName" value="'+data[i].file_name+'">';
					tdHtml+='<input type="hidden" name="check_batch_pick_id" class="check_batch_pick_id"  value="'+data[i].batch_pick_id+'" /></td>';
				}
				tdHtml+='<td><a target="_blank" href="../sale/search_orders2?batch_pick_id='+data[i].batch_pick_id+'&batch_pick_sn='+data[i].batch_pick_sn+'">'+data[i].batch_pick_sn+'</a></td>';
				tdHtml+='<td>'+data[i].name+'</td>';
				tdHtml+='<td>'+data[i].warehouse_name+'</td>';
				tdHtml+='<td>'+data[i].order_num+'</td>';
				/* tdHtml+='<td>'+data[i].warehouse_name+'</td>'; */
				tdHtml+='<td>'+str2Date(data[i].created_time)+'</td>';
				if(data[i].first_print_time!=undefined){
					tdHtml+='<td>'+str2Date(data[i].first_print_time)+'</td>';
				}else{
					tdHtml+='<td>-</td>'
				}
				if(data[i].batch_process_type=='BATCH'){
					tdHtml+='<td><input type="hidden" name="print_batch_pick_id" class="print_batch_pick_id" value="'+data[i].batch_pick_id+'" />';
					tdHtml+='<input type="hidden" name="print_batch_pick_sn" class="print_batch_pick_sn" value="'+data[i].batch_pick_sn+'" />';
					tdHtml+='<input type="button" value="打印拣货标签" onclick="print_pick_tag(this)" class="lq-btn lq-btn-sm lq-btn-primary" /></td>';
				}else if(data[i].batch_process_type!='BATCH'){
					tdHtml+='<td>-</td>'
				}
				tdHtml+='<td>'+data[i].file_name+'</td>';
			}
			$("tbody.tbody").html(tdHtml); 
		}
		
		//分页
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
					pageHtml += '<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li>';
				}
				pageHtml += '<span class="totalPage" >共 <span>'+TOTAL_PAGE+'</span> 页, '+TOTAL_ORDER+' 个订单</span></ul></nav></div>';
				$(".page-wrap").html(pageHtml);
			} else {
				$(".page-wrap").html("").fadeOut(300);
			}
		}
		
		$("#body-sale-search").on("click",".lq-page-control li",function(e){
			e.preventDefault();
			var that = $(this);
			var data={};

			if (!that.hasClass("prev") && !that.hasClass("next")) {
				if (!that.hasClass("active")) {
					var index = parseInt($(this).find("a").text());
					CURRENT_PAGE = index;
					data={
							currentPage : index,
							pageSize : 15,
							customer_id:$("#customer_id").val(),
							print_status:$("#print_status").val(),
							start:$("#start").val(),
							end: $("#end").val(),
							reserve_status: $("#reserve_status").val(),
							batch_pick_sn:$("#batch_pick_sn").val(),
							pick_status: $("#pick_status").val(),
							is_batch: $("#is_batch").val(),
							warehouse_id:$("#warehouse_id").val()
							/* warehouse_id:$("#warehouse_id").val() */
					}
					$.ajax({
						url : "../sale/pickShowV2",
						type : "get",
						dataType : "json",
						data : data,
						beforeSend : function(){
							$(".mw-loading").fadeIn(300);
							that.addClass("active").siblings().removeClass("active");
						},
						success : function(res) {
							$(".mw-loading").fadeOut(300);
							showTable(res);
						},
						error : function(error) {
							$(".mw-loading").fadeOut(300);
							alert("accept_ajax接口调用失败");
							console.log(error);
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
				pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
			}
		});

	</script>
</body>
</html>