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
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>新品上架</title>
<link href="<%=request.getContextPath() %>/static/css/autocomplete.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
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
  height: 40px;
  font-size: 14px;
}
input[type="button"] {
	margin: 10px 90px 0 0;
}
</style>
<script>
$(document).ready(function(){
	$('#tag_code').bind('keyup', listen_tag).focus();
	
	var KEY = {
		      RETURN: 13,  // 回车
		      CTRL: 17,    // CTRL
		      TAB: 9
		  };
	
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
			var tag_code= $('#tag_code').val();
			var customer_id= $('#customer_id').val();
			$.ajax({
			    type:'post',
			    dataType:'json',
			    url:'../product/scanPrepackGrouding',
			    data:{
			        'location_barcode':tag_code
			    },
			    error:function(){
			        console.log('error');
			    },
			    success:function(data){
			    	console.log(data);

					if(data.result=='success'){
				    	$("#goods_name").val(data.product_name);
				    	$("#number").html(data.quantity);
				    	$("#tj_barcode").html(insert_flg(data.new_location_barcode));
				    	$("#warehouse_id").text(data.warehouse_name);
					}else{
						alert(data.note);
					}
/* 			    	if((data.map)[0].result == 'success'){
				    	 $('#goods_name').val((data.map)[0].goods_name);
						 $('#number').html((data.map)[0].goods_number);
						 $('#tj_barcode').html(insert_flg((data.map)[0].location_barcode));
						 $('#order_id').val((data.map)[0].order_id);
						 $('#order_goods_id').val((data.map)[0].order_goods_id);
						 $('#warehouse_id').val((data.map)[0].warehouse_id);
						 $('#physical_warehouse_id').val((data.map)[0].physical_warehouse_id);
						 $('#customer_id').val((data.map)[0].customer_id);
			    	}else{
			    		alert((data.map)[0].note);
			    	} */
			    },
			});
	 }
	

})
	function checksubmit(){
		
		$("#group").attr('disabled', 'disabled');
/* 		var tag_code= $('#tag_code').val();
		var order_id= $('#order_id').val();
		var order_goods_id= $('#order_goods_id').val();
		var warehouse_id= $('#warehouse_id').val();
		var customer_id= $('#customer_id').val();
		var physical_warehouse_id= $('#physical_warehouse_id').val();*/
		var goods_number= $('#goods_number').val();
		var number= $('#number').html(); 
		var location_barcode= $('#tag_code').val();
		var new_location_barcode=$("#location_barcode").val();
		if(number != goods_number){
			alert("商品数量不一致");
			$("#group").removeAttr('disabled');
			return false;
		}
		if(new_location_barcode == ""){
			alert("请维护库位信息");
			$("#group").removeAttr('disabled');
			return false;
		}
		
		
		$.ajax({
			url: '../product/prepackSubmitGrouding',
			type: 'post',
			data : {
	            'location_barcode':location_barcode,
				'new_location_barcode':new_location_barcode
	        },
			dataType : 'json',
			success : function(data){
				console.log(data);
				if(data.result == 'failure')
					alert(data.note);
				else
					alert('上架成功'); 
				
				location.reload();
			},
			error : function(data){
				alert('上架失败，请联系产品技术组');
				$("#group").removeAttr('disabled');
			}
		});
	}
</script>
</head>
<body>
		<div style="clear:both;margin: 10px;">
		<table>
			<tr>
				<td>标签条码：</td>
				<td><input type="text" id="tag_code" value="${tag_code}" /></td>
			</tr>
			<tr>
				<td>商品名称：</td>
				<td><input type="text" id="goods_name" value="${goods_name}" readonly/>
					<!-- <input type="text" id="number" value="${goods_number}" /> -->
					<span id="number"></span>
				</td>
			</tr>
			<tr>
				<td>商品数量：</td>
				<td><input type="text" id="goods_number" value="${goods_number}" /></td>
			</tr>
			<tr>
				<td>推荐库位：</td>
				<td>
					<!-- <input type="text" id="tj_barcode" value="${tj_barcode}" /> -->
					<span id="tj_barcode"></span>
				</td>
			</tr>
			<tr>
				<td style="text-indent: 2rem;">渠道：</td>
				<td><span id="warehouse_id"></span></td>
			</tr>
			<tr>
				<td>存放库位：</td>
				<td><input type="text" id="location_barcode" value="${location_barcode}" /></td>
			</tr>
			<tr>
				<td colspan="2" align="right">
				<input type="hidden" name="order_goods_id" id="order_goods_id" value="${order_goods_id}" />
				<input type="hidden" name="order_id" id="order_id" value="${order_id}" />
				<input type="hidden" name="customer_id" id="customer_id" value="${customer_id}" />
				<input type="hidden" name="warehouse_id" id="warehouse_id" value="${warehouse_id}" />
				<input type="hidden" name="physical_warehouse_id" id="physical_warehouse_id" value="${physical_warehouse_id}" />
				<input type="button" id="group" onclick="checksubmit()" class="lq-btn lq-btn-primary lq-btn-sm" value="上架" /></td>
			</tr>
		</table>
</body>
</html>