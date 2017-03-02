<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>快递交接单</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />

	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript" src="../static/js/zapatec/utils/zapatec.js"></script>
	<script type="text/javascript" src="../static/js/zapatec/zpcal/src/calendar.js"></script>
	<script type="text/javascript" src="../static/js/zapatec/zpcal/lang/calendar-en.js"></script>
	
	<style>
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
	    
	    #btn-sale-search {
	    	margin: 0 10px;
	    }
	    #page-search {
	    	float: right;
	    }
	    
			.tip {
			color: #888;
		}
	.require {
			color: #F00;
		}
	.loading {
			background: #F1F1F1 url('misc/indicator.gif') right center no-repeat;
		}
	.strik {
	        background-color: #ECEE9F;
	    }
	.cal{
	 	color: white;
	 }
	 .search{
	 	border: 1px solid ;
	 }
	 .time{
	 	width: 89px;
	 }
	 .sel{
	 	width: 132px;
	 }
	 .list:nth-child(2n+1){
	 	background-color: rgba(192,192,192,0.3);
	 }
	 .order{
	 	width: 100%;
	 }
	 .search_button{
	 	width: 90px;
	 	border: 1px solid  #999;
	 	background-color: #3295DD;
	 	color: white;
	 	margin-left: 12px;

	 }
	 #print_button{
	 	text-decoration: none;
	 	/*text-decoration: none;
	 	color: black;
	 	border: 1px solid rgb(192, 192, 192);
	 	padding: 5px 10px;
	 	border-radius: 4px;
	 	box-shadow: 1px 1px 1px rgb(192, 192, 192);*/
	 }
	 .print_button {

	 }
	 .print_head{
	 	font-weight: bold;
	 }
	 .tracking{
	 	/*width: 15%;*/
	 	/*border: 1px solid black;*/
	 	text-align: center;
	 	/*padding: 8.5px;*/
	 	/*margin:;*/
	 }
	 .pallet{
	 	/*display: inline;*/
	 	line-height: 30px;
	 }

	 #print_foot_left{	
	 	position: relative;
	 	float: left;
	 	display: inline-block;
	 }
	 #print_foot_right{
	 	position: relative;
	 	float: right;
	 	margin-right: 80px;
	 	display: inline-block;
	 }
	 .footer{
	 	height: 30px;
	 	line-height: 30px;
	 }
    .noDataTip {
    	/*display: none;*/
    	text-align: center;
    	font-size: 30px;
    	color: #666;
    	margin: 30px 0 0 0;
    }
    .noDataTip {
    	/*display: none;*/
    	text-align: center;
    	font-size: 30px;
    	color: #666;
    	margin: 30px 0 0 0;
    }    
	</style>
</head>
<body id="shippmentList">
	<div class="modal-wrap mw-loading">
		<div class="modal-content mc-loading">
			<i class="fa fa-spinner fa-spin" id="loading"></i>
		</div>
	</div>
	<div id="print_div" hidden>
		<h1 class="head_title" align="center">快递交接单</h1>
		<p>快递方式:<span id="shipping_type" class="print_head"></span>&nbsp;&nbsp;面单总数:<span id="total" class="print_head"></span>&nbsp;&nbsp;发货日期:<span id="print_time" class="print_head"></span></p>
<%-- 			<c:forEach items="${palletShipmentList}" varStatus="i" var="order">
				<span class="pallet" id="${order.pallet_no}" hidden>
					<c:forEach var="name" items="${order.tracking_number_list}">
					    <span style="color:black;" class="tracking tracking_item"><c:out value="${name}" />&nbsp;&nbsp;&nbsp;&nbsp;
					    </span>
					</c:forEach>
				</span>
			</c:forEach> --%>
			<div id="tracking_number_list"></div>
		<p class="footer"><span id="print_foot_left">仓库发货人:</span><span id="print_foot_right">揽件人：</span></p>
	</div>

	<div class="main-container">
		<!-- <iframe id="print" src=""></iframe> -->
		<div class="lq-row lq-form-inline">
			<div class="lq-col-4">
				<div class="lq-form-group">
					<label for="delivery_start_time">开始时间:</label>
					<input type="text" id="delivery_start_time" name="delivery_start_time" value="${delivery_start_time}" size="10" class="lq-form-control"/>
				</div>
				<div class="lq-form-group">
					<label for="delivery_end_time">结束时间:</label>
					<input type="text" id="delivery_end_time" name="delivery_end_time" value="${delivery_end_time}" size="10" class="lq-form-control"/>					
				</div>
			</div>
			
			<div class="lq-col-4">
				<div class="lq-form-group">
					<span class="important">*</span>
					<label for="shipping_name">快递方式:</label>
					<select name="shipping_id" id="shipping_id" class="lq-form-control"> 
						  <c:forEach items="${shippingList}" var="shipping"> 
						      <option name="shipping_id" 
						      <c:if test="${shipping_id==shipping.shipping_id}">selected="true"</c:if>
						      value="${shipping.shipping_id}">${shipping.shipping_name}</option> 
						 </c:forEach> 
					</select>
				</div>
				<div class="lq-form-group">
					<label for="pallet_no">码托条码:</label>
					<input class="lq-form-control" type="text" name="pallet_no"  value="${pallet_no}" id="pallet_no"/>
<!-- 					<button type="submit" id="btn-sale-search" class="lq-btn lq-btn-sm lq-btn-primary">
						<i class="fa fa-search"></i>查询
					</button> -->
				</div>
			</div>
			
 			<div class="lq-col-4">
 				<div class="lq-form-group">
					<label for="print">打印状态:</label>
					<select name="print_count" id="print_count" class="lq-form-control"> 
						<option value="0">未打印</option>
						<option value="1">已打印</option>
					</select> 					
 				</div>
				<div class="lq-form-group">
					<button id="btn-sale-search" class="lq-btn lq-btn-sm lq-btn-primary">
						<i class="fa fa-search"></i>查询
					</button>
					<input type="hidden" name="act" value="search">
				</div>
			</div>		
		</div>
		<form id="form">
		  	<div style="margin:10px 0; clear:both;"> 
	      		<input type="button" value="全选" onclick="select_all('#batchTable')" class="lq-btn lq-btn-sm lq-btn-primary" />
	      		<input type="button" value="清空" onclick="select_none('#batchTable')" class="lq-btn lq-btn-sm lq-btn-primary" />
	      		<input type="button" value="反选" onclick="select_reverse('#batchTable')" class="lq-btn lq-btn-sm lq-btn-primary" />
	      		<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="打印" id="printBtn"  />
	      	</div>
	      	
	      	<p><span id='count'></span></p>
	      	
	      	<table id="batchTable" class="lq-table">
	      		<thead>
	    			<th class="cancel">选择</th>
		            <th style="width: 30%;">码托条码</th>
		            <th>快递方式</th>
		            <th>物理仓</th>
		            <th>运单总数</th>
		            <th>发货时间</th>
	      		</thead>
	      		<tbody></tbody>
	      	</table>
	      	<div class="noDataTip">未查到符合条件的数据!</div>
      	</form>
		<div class="page-wrap">
			<div class="lq-row">
				<nav id="page-search">
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
<script type="text/javascript">
	Zapatec.Calendar.setup({
		weekNumbers       : false,
		electric          : false,
		inputField        : "delivery_start_time",
		button            : "delivery_start_time",
		ifFormat          : "%Y-%m-%d",
		daFormat          : "%Y-%m-%d"
	});
	Zapatec.Calendar.setup({
		weekNumbers       : false,
		electric          : false,
		inputField        : "delivery_end_time",
		button            : "delivery_end_time",
		ifFormat          : "%Y-%m-%d",
		daFormat          : "%Y-%m-%d"
	});
	

	
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
	//获取当前时间
 	function getTimeNow(){
		var timeNow=new Date();
		return timeNow.getFullYear()+"-"+(timeNow.getMonth()+1)+"-"+timeNow.getDate();
	} 
	//码托条码输入
	$(function(){
		$('#pallet_no').on('click', listen_pallet_no);
        $('#pallet_no').on('keyup', listen_pallet_no).focus(); 
    	//进入页面，开始时间选择当前时间
		var timeNow=new Date();
    	$("#delivery_start_time").val(getTimeNow());
	})
	
	var KEY = {
        RETURN: 13,  // 回车
        CTRL: 17,    // CTRL
        TAB: 9
    };
	
	//码托条码监听
	function listen_pallet_no(event) 
    {
        switch (event.keyCode) {
            case KEY.RETURN:
            case KEY.CTRL:
                load_staff_number();
                event.preventDefault();
                break;
        }
    }
	
	//载入码托条码信息
	function load_shipment_id() 
    {
        var pallet_no = $.trim($('#pallet_no').val());
        if (pallet_no == '')
            return; 
    }
	
	//打印
	$("#printBtn").click(function(){
		var palletList= "";
		$("input[name='check_batch_pick[]']:checked").each(function(){ 
			palletList+=','+($(this).val());
		});
		if(palletList.length<=0) {alert("未中任何项!"); return;}
		console.log(palletList.substring(1));
 		$.ajax({
			url:'../shippment/print',
			type: 'post',
			dataType: 'json',
			data: {
				mt_code:palletList.substring(1)
			},
			success: function(res){
				console.log(res);
				if(res.result=="success"){
					printme("#form");
				}else{
					alert("未选中选项");
					return;
				}
			},
			error: function(err){
				alert("ajax Eror");
			}
		}) 
	})
	
	//打印
 	function printme(node){
		var pallet_no=[];//存放码托条码
 		var shipping_type='';//存放快递方式
 		var total=0;//存储快递单总数
 		var tracking_number = [];//获取所有快递单号码
 		var item='';
 		
 		var shipping_type=$("td.shippingmentName").eq(0).text();
 		node = node ? node : document ;
		item = $(node).find(':checkbox:checked');
		if (!item || item==undefined || item.length<1) {
			alert('没有选中项');
			return false;
		}
    	
		var palletList= new Array();
		 $("input[name='check_batch_pick[]']:checked").each(function(){ 
			 pallet_no.push($(this).val());
             var tracking_number = $(this).closest('tr.list').find('.tracking_number').attr('value');
             palletList.push("{'palletNo':"+$(this).val()+",'tracking_number':"+tracking_number+"}");

             total += Number( $(this).closest('tr.list').find('.total_tracking').val());
		 })
		 console.log(pallet_no);
         $('#total').text(total).addClass('print_head');
         $('#shipping_type').text(shipping_type).addClass('print_head');
         console.log(total);
         console.log(shipping_type);
         $('#div1').show();
         for(var i=0;i<pallet_no.length;i++){
         	$('#'+pallet_no[i]).show();
         	$('#'+pallet_no[i]).children().show();
         	$('#'+pallet_no[i]).children().css('display','inline-block');
         }         
         var time = getTimeNow();
		 $('#print_time').text(time);
		 var size = 0;
		 size = $('.tracking_item').size();
         $('.tracking_item').each(function(index,element){
         	if((index+1) % 128 == 0){//正常情况下一行6个，一页60行，此处设置为28行
          		$(this).after('<p class="footer"><span id="print_foot_left">仓库发货人：</span>'+'<span id="print_foot_right">揽件人：</span></p><div style="page-break-after: always;"></div><h1 class="head_title">快递交接单</h1>'+
				'<p>快递方式：<span class="print_head">'+shipping_type+'</span>&nbsp;&nbsp;面单总数：<span class="print_head">'+total+'</span>'+'&nbsp;&nbsp;发货日期：<span class="print_head">'+time+'</span></p>'); 
         	}
         });
     	document.body.innerHTML=document.getElementById('print_div').innerHTML;
		window.print();
		document.location.reload();
		
		
	} 
	//构造静态打印页面
	function productPage(data){
		var spanHtml="";
		for(var j in data){
			spanHtml+='<span style="color:black;" class="tracking tracking_item">'+data[j]+'</span>&nbsp;&nbsp;&nbsp;&nbsp;';
		}
		return spanHtml;
	}
	
	function Init(){
		$("tbody").html("");
		$("#page-search").hide();
	}
	
	//查询
	var TOTAL_PAGE = 0, START_PAGE = 1, MAX_PAGE = 10, END_PAGE = 10, CURRENT_PAGE = 1, PAGE_DATA = {}, TOTAL_ORDER = 0;

	$("#btn-sale-search").click(function(e){
		e.preventDefault();
		Init();
		$.ajax({
			url:'../shippment/queryPalletShipmentList',
			dataType:'json',
			type:'post',
			beforeSend:function(){
				$(".mw-loading").fadeIn(300);
			},
			data: {
				delivery_start_time:$("#delivery_start_time").val(),
				delivery_end_time: $("#delivery_end_time").val(),
				shipping_id: $("#shipping_id").val(),
				pallet_no: $("#pallet_no").val(),
			    print_count:$("#print_count").val(), 
				act: "search"
			},
			success: function(res){
				console.log(res);
			
				$(".mw-loading").fadeOut(300);
				if(res.palletShipmentList.length>0){
					$(".noDataTip").fadeOut(300);
					PAGE_DATA = res.page;
					TOTAL_PAGE = parseInt(res.page.totalPage);
					START_PAGE = 1;
					if (TOTAL_PAGE < MAX_PAGE) {
						END_PAGE = TOTAL_PAGE;
					} else {
						END_PAGE = MAX_PAGE;
					}
					CURRENT_PAGE = res.page.currentPage;
					$('#count').text("筛选条件下共有"+res.count+"个运单");
					pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
					showTable(res.palletShipmentList,res.physicalWarehouse.warehouse_name);
					
					/* showTable(res.palletShipmentList); */
					var list="";
					for(var k in res.palletShipmentList ) {
						list+='<span class="pallet" id="'+res.palletShipmentList[k].pallet_no+'" hidden>'+productPage(res.palletShipmentList[k].tracking_number_list)+'</span>';
					}
					$("#tracking_number_list").html(list);
				}
				else{
					$(".noDataTip").fadeIn(300);
				}
					
			},
			error:function(err){
				console.log(err);
				alert(err);
				$(".mw-loading").fadeOut(300);
			}
		})
	})

	//时间转化
	function trans2Date(str){
		var nowDate=new Date(str);
		var year=nowDate.getFullYear();
		var month=nowDate.getMonth()+1;
		var day=nowDate.getDate();
		var nowTime="";
		if(parseInt(month)<10) month="0"+month;
		if(parseInt(day)<10) day="0"+day;
		nowTime=year+'-'+month+'-'+day+' '+nowDate.toString().split(' ')[4];
		return nowTime;
	}
	
	
	//显示表格
	function showTable(data,physicalWarehouse){
		var tdHtml="";
		var shippingmentName=$("#shipping_id option:selected").text();
		for(var i in data){
			tdHtml+='<tr class="list">';
			tdHtml+='<td><input type="checkbox" name="check_batch_pick[]" value="'+data[i].pallet_no+'" class="check" />';
			tdHtml+='<input type="hidden" name="check_batch_pick_id" class="check_batch_pick_id"  value="'+data[i].pallet_no+'" />';
			tdHtml+='<input type="hidden" name="tracking_number" class="tracking_number" value="'+data[i].tracking_number_list+'" />';
			tdHtml+='<input type="hidden" name="pallet_no" class="pallet_no" value="'+data[i].pallet_no+'" />';
			tdHtml+='<input type="hidden" name="shipping_type" class="shipping_type" value="'+data[i].shipping_name+'" />';
			tdHtml+='<input type="hidden" name="total_tracking" class="total_tracking" value="'+data[i].tracking_number_count+'" /></td>';
			tdHtml+='<td>'+data[i].pallet_no+'</td>';
			tdHtml+='<td class="shippingmentName">'+shippingmentName+'</td>';
			tdHtml+='<td>'+physicalWarehouse+'</td>';
			tdHtml+='<td>'+data[i].tracking_number_count+'</td>';
			tdHtml+='<td>'+trans2Date(data[i].shipped_time)+'</td>';
			tdHtml+='</tr>';
		}
		$("tbody").html(tdHtml);
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
				pageHtml += '<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li></ul></nav></div>';
			}
			
			$(".page-wrap").html(pageHtml);
		} else {
			$(".page-wrap").html("").fadeOut(300);
		}
	}
	
	$("#shippmentList").on("click",".lq-page-control li",function(e){
		e.preventDefault();
		var that = $(this);
		var data={};
		if (!that.hasClass("prev") && !that.hasClass("next")) {
			if (!that.hasClass("active")) {
				var index = parseInt($(this).find("a").text());
				CURRENT_PAGE = index;
				data={
						currentPage : index,
						pageSize : 20,
						delivery_start_time:$("#delivery_start_time").val(),
						delivery_end_time: $("#delivery_end_time").val(),
						shipping_id: $("#shipping_id").val(),
						pallet_no: $("#pallet_no").val(),
					    print_count:$("#print_count").val(),
						act: "search"
				}
				$.ajax({
					url : "../shippment/queryPalletShipmentList",
					type : "get",
					dataType : "json",
					data : data,
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
						that.addClass("active").siblings().removeClass("active");
					},
					success : function(res) {
						console.log(res);
						$(".mw-loading").fadeOut(300);
						showTable(res.palletShipmentList,res.physicalWarehouse.warehouse_name);
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