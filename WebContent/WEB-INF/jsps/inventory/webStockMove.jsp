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
<title>库存移动</title>
<link href="<%=request.getContextPath() %>/static/css/autocomplete.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />
<script type="text/javascript" src="../static/js/jquery/jquery.js"></script>
<script type="text/javascript" src="../static/js/jquery/jquery.ajaxQueue.js"></script>
<script type="text/javascript" src="../static/js/autocomplete.js"></script>
<style>
input[type="text"]{
  	height: 30px;
  	position: relative;
  	top: 5px;
}
body{
	  font-family: "微软雅黑";
}
td {
  width: 80px;
  height: 40px;
  font-size: 14px;
}
input[type="button"] {
	margin: 10px 90px 0 0;
}
    .noDataTip {
    	/*display: none;*/
    	text-align: center;
    	font-size: 30px;
    	color: #666;
    	margin: 30px 0 0 0;
    }
       #page-search {
    	float: right;
    }
        .lb_firstd {
    	display: block;
    	min-width:36px;
    }
    .lb_firstd span {
    	position: relative;
    	top: 3px;
    	left: 5px;
    }
</style>
<script type="text/javascript">
$(document).ready(function(){
	 	
		$('#tag_code').bind('keyup', listen_tag).focus();

});

var KEY = {
      RETURN: 13,  // 回车
      CTRL: 17,    // CTRL
      TAB: 9
  };

function listen_tag(event) 
{
    switch (event.keyCode) {
        case KEY.RETURN:
        case KEY.CTRL:
            load_tag_info();
            var tag_code= $('#tag_code').val();
            event.preventDefault();
            break;
    }
}

function load_tag_info(){
    // $('#loadOrder').click(function(){
		var tag_code=$.trim($('#tag_code').val());

		//var customer_id= $('#customer_id').val();
		$.ajax({
		    type:'post',
		    dataType:'json',
		    url:'../inventory/stockMoveSourceLocationCheck',
		    data:{
		        'from_location_barcode':tag_code
		    },
		    error:function(){
		        console.log('error');
		    },
		    success:function(data){
		    	console.log(data);
		    	console.log(data.success);
		    	if(data.success==false)
		    		alert(data.error);
		    	else {
		    		$("#inventory_type").val(data.locationTypeName);
		    		$("#goods_type").val(data.plSize);
		    		$("#group").removeAttr("disabled");
		    		showtables(data);
		    		if($("#goods_type").val()=="1") {
		    			$("tbody.list tr").eq(0).find("td").eq(0).find("input[name=check_batch_pick[]]").attr("checked","checked");
		    		}
		    	    var move_num=$("#move_amount").val('');
		    	    var to_location_barcode=$("#target_barcode").val('');
		    	}
		    }
		});
 }
   
 function showtables(data){
	 var value=data.list;
	 var tdHtml='';
	 var no;
	 $('tbody.list').html('');

	 for(var i in value) {
		 no=parseInt(i,10)+1;
		 tdHtml+='<tr>';
		 tdHtml+='<td><label class="lb_firstd"><input type="radio" name="check_batch_pick[]" class="index"><span>'+no+'</span></label><input type="hidden" id="pl_id" value="'+value[i].pl_id+'"></td>';
		 tdHtml+='<td>'+value[i].barcode+'</td>';
		 tdHtml+='<td>'+value[i].qty_total+'</td>';	
		 tdHtml+='<td>'+value[i].validity+'</td>';
		 tdHtml+='<td>'+value[i].batch_sn+'</td>';
		 tdHtml+='<td>'+value[i].warehouse_name+'</td>';
		 tdHtml+='<td id="available">'+value[i].qty_available+'</td>';
		 tdHtml+="</tr>";
	 }
	
	 $('tbody.list').html(tdHtml);
	 
 }
    
 
 
function checksubmit(){
	
	$("#group").attr('disabled', 'disabled');
	var from_location_barcode=$('#tag_code').val();
    var move_num=$("#move_amount").val();
    var to_location_barcode=$("#target_barcode").val();
    var pl_id= $("input[name='check_batch_pick[]']:checked").parent().next("#pl_id").val();
    var available=$("input[name='check_batch_pick[]']:checked").parent().parent().parent().find("#available").text();
    if( $("input[name='check_batch_pick[]']:checked").length==0) {
    	alert("并未选择商品!");
    	$("#group").removeAttr("disabled");
    	return ;
    }
   if(parseInt($("#move_amount").val())>parseInt(available))
	{
		console.log("可用库存:"+available);
		console.log("移库数量:"+$("#move_amount").val());
	   	alert("移动数量不能大于可用库存量");
	   	$("#group").removeAttr("disabled");
	   	return;
	}
	console.log(pl_id);
	$.ajax({
		url: '../inventory/stockMove',  
		data : {
            'from_location_barcode': from_location_barcode,
            'move_num':move_num,
            'to_location_barcode':to_location_barcode,
            'pl_id':pl_id
        },
		dataType : 'json',
		success : function(data){
			console.log(data);
			if(data.success == false){
				alert(data.error);
				$("#group").removeAttr("disabled");
			}
			else {
				alert("移库成功");
			}
				
			
		},
		error : function(data){
			alert('移库失败');
			$("#group").removeAttr("disabled");
		}
	});
}
</script>
</head>
<body>

	<div style="clear:both;margin: 10px;">
		<table>
			<tr>
				<td>库位条码：</td>
				<td><input type="text" id="tag_code" value="${tag_code}" /></td>
			</tr>
			<tr>
				<td>库位类型：</td>
				<td><input type="text" id="inventory_type"  readonly/>
					<!-- <input type="text" id="number" value="${goods_number}" /> -->
				</td>
			</tr>
			<tr>
				<td>商品种类：</td>
				<td><input type="text" id="goods_type"  readonly/></td>
			</tr>
		</table>
		<div style="max-height: 420px; overflow:auto;">
		<table class="lq-table" id="batchTable">
			<thead>
				<th>编号</th>
				<th>商品条码</th>
				<th>库存数量</th>
				<th>生产日期</th>
				<th>批次号</th>
				<th>渠道</th>
				<th>可用库存</th>
			</thead>
			<tbody class="list">
				
			</tbody>
		</table>
		</div>
		<div style="clear:both;margin: 15px;"></div>
		<table>
			<tr>
				<td>移动数量：</td>
				<td><input type="text" id="move_amount" /></td>
			</tr>
			<tr>
				<td>目标库位：</td>
				<td><input type="text" id="target_barcode"/>
					<!-- <input type="text" id="number" value="${goods_number}" /> -->
				</td>
			</tr>
			<tr>
				<td></td>
				<td><input type="button" id="group" onclick="checksubmit()" class="lq-btn lq-btn-primary lq-btn-sm" value="确认" /></td>
			</tr>
		</table>
</body>
</html>