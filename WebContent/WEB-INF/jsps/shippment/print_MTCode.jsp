<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>打印码托条码</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<style type="text/css">
	.order_liTab{float:left;margin-bottom:-1px;margin-top:20px;}
	.order_liTab li{width:110px;height:21px;padding-top:11px;background:url(images/order_liBg_1.gif) 0 5px no-repeat;float:left;text-align:center;margin-left:-1px;cursor:pointer;list-style:none;}
	.order_liTab li.on{background:url(images/order_liOn.png) no-repeat;font-weight:bold;font-size:14px;margin-bottom:-1px;position:relative;cursor:default;color:#000;}
	.order_liTab li a{color:#fff;}
	.order_liTab li.on a{color:#000;}

	.ddan{
	width:900px;
	float:left;
	}
	#print{
	  width: 0px;
	  height: 0px;
	  border: 0px;
	}
	#IgetGNs_tray {
		margin: 0 0 0 20px;
	}
	</style>
</head>

<body>
<div class="main-container">
	<iframe id="print" src=""></iframe>
	<script type="text/javascript">	
	  window.setTimeout(function(){
	    $('#message').slideUp("slow");
	  }, 4000);

	</script>

	<!-- 打印码托 MT201602290001-->
	<div class="main-div" id="barcode_grouding"> <!-- style="float:left" -->
	<form method="post" class="lq-form-inline">
		<div class="lq-form-group">
			<label for="barcode_tray">数量:</label>
			<input type="text"  id="barcode_tray" name="barcode_tray" value="5" onkeyup="value=value.replace(/[^\d]/g,'')" onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))"  class="lq-form-control">
			<input type='button' id="IgetGNs_tray" onclick='if(check_barcode_tray_number())I_GNs_TRAY(1);' value='列出打印条码' class="lq-btn lq-btn-primary lq-btn-sm">
			<input type='button' id="IprintGNs_tray" disabled="disabled"  value='打印' class="lq-btn lq-btn-primary lq-btn-sm">
		</div>
		<div class="lq-form-group">
			<label for="GN_TRAY_LIST">码托条码打印信息:</label>
			<input type='hidden' id='GN_TRAY_LIST' name="GN_TRAY_LIST" value="{$GN_LIST_STR}">
			<input readOnly="readOnly" style="width:80%;" class="lq-form-control" type='text' id='GN_TRAY_LIST_SHOW' name="GN_TRAY_LIST_SHOW" value="就绪。">
		</div>
	</form>
	</div>
</div>
<iframe name="print_frame" width="0" height="0" frameborder="0" src="about:blank" ></iframe>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script type="text/javascript" src="../static/js/sinri_print_iframe.js"></script>
<script type="text/javascript">	
	
  function check_input_number(number) {
	  if (isNaN(number.trim())) {
	      alert("请输入数字:"+ number);
	      return false;
	  }
		
	  if (number.trim() == ''){
		  alert("请输入一个数字:"+ number);
		  return false;
	  }
	  return true;
  }
  /*
  //现在没有用
  function show_hide_ids(type){
      for(var i=0;i<BARCODE_TYPE.length;i++){
    	  if(type == BARCODE_TYPE[i]) {
    		  //$('#menu_barcode_'+type).addClass('on');
    	      $('#barcode_'+type).show();
    	  } else {
		      //$('#menu_barcode_'+BARCODE_TYPE[i]).removeClass('on');  
	    	  $('#barcode_'+BARCODE_TYPE[i]).hide();
    	  }
      }
  }

  function check_order_sn() {
  	alert("你妹的倒是给我干活啊！");
	  return true;
  }
  */
  
  /**
  找出可打印的字符
  **/
  function check_grouding_number() {
	$gouding_number = $('#grouding_number').val();//打印数量
	if(!check_input_number($gouding_number)) {//检查数量是否为数字
	 	return false;
	}
	var $gouding_barcodes = get_grouding_location_barcodes($gouding_number);//获取所有的条码编号
	document.getElementById('GN_LIST').value='';//将隐藏的列表设置为空
	document.getElementById('GN_LIST_SHOW').value='';//将显示的列表设置为空
	for(var i=0;i<$gouding_barcodes.length;i++) {
		//alert($gouding_barcodes[i]);
		document.getElementById('GN_LIST').value+=$gouding_barcodes[i];//将条码全部放进去
		if(i<$gouding_barcodes.length-1) document.getElementById('GN_LIST').value+=',';
		//if(i%10==9) document.getElementById('GN_LIST').value+='\r\n';
		if(i==0){
			document.getElementById('GN_LIST_SHOW').value+='自 '+$gouding_barcodes[i];
		} else if (i==$gouding_barcodes.length-1){
			document.getElementById('GN_LIST_SHOW').value+=' 到 '+$gouding_barcodes[i]+"("+Date()+") 待打印...";
		}
	}
	if($gouding_barcodes.length>0) return true;
	else return false;
	/*
	if($to_print){
  		// 打印该条码
		if($gouding_barcodes) {
			print_barcodes($gouding_barcodes);
		}
	}
	*/		
  } 
  // 码托  barcode_tray
 function check_barcode_tray_number() {
	$barcode_tray = $('#barcode_tray').val();//打印数量
	if(!check_input_number($barcode_tray)) {//检查数量是否为数字
	 	return false;
	}
	var $gouding_barcodes = get_tray_barcodes($barcode_tray);//获取所有的条码编号
	document.getElementById('GN_TRAY_LIST').value='';//将隐藏的列表设置为空
	document.getElementById('GN_TRAY_LIST_SHOW').value='';//将显示的列表设置为空
	for(var i=0;i<$gouding_barcodes.length;i++) {
		//alert($gouding_barcodes[i]);
		document.getElementById('GN_TRAY_LIST').value+=$gouding_barcodes[i];//将条码全部放进去
		if(i<$gouding_barcodes.length-1) document.getElementById('GN_TRAY_LIST').value+=',';
		//if(i%10==9) document.getElementById('GN_LIST').value+='\r\n';
		if(i==0){
			document.getElementById('GN_TRAY_LIST_SHOW').value+='自 '+$gouding_barcodes[i];
		} else if (i==$gouding_barcodes.length-1){
			document.getElementById('GN_TRAY_LIST_SHOW').value+=' 到 '+$gouding_barcodes[i]+"("+Date()+") 待打印...";
		}
	}
	if($gouding_barcodes.length>0) return true;
	else return false;
  }
   /**
  控制注册 码托 空条码和打印的入口
  **/
  function I_GNs_TRAY(work){
  	if (work==1){
  		document.getElementById("IprintGNs_tray").disabled=false;
		document.getElementById("IgetGNs_tray").disabled=true;
  	} else if (work==2){
  		document.getElementById("IprintGNs_tray").disabled=true;
		document.getElementById("IgetGNs_tray").disabled=false;
		document.getElementById('GN_TRAY_LIST').value='';
		document.getElementById('GN_TRAY_LIST_SHOW').value+=" ("+Date()+") 已打印。";
  	}
  }

  /**
  控制注册空条码和打印的入口
  **/
  function I_GNs(work){
  	if (work==1){
  		document.getElementById("IprintGNs").disabled=false;
		document.getElementById("IgetGNs").disabled=true;
  	} else if (work==2){
  		document.getElementById("IprintGNs").disabled=true;
		document.getElementById("IgetGNs").disabled=false;
		document.getElementById('GN_LIST').value='';
		document.getElementById('GN_LIST_SHOW').value+=" ("+Date()+") 已打印。";
  	}
  }

  /*
  function print_grouding_number() {
  	var url='print_barcode.php?sugu_print=1&type=grouding&barcodes=';
  	var codes=document.getElementById('GN_LIST').value;
  	hidden_print(url+codes);
  }
  */
  
  function get_grouding_location_barcodes($gouding_number) {
	  var result = "";
	  $.ajax({
		  async:false,
	      dataType:'json',
	      type:'post',
	      url:'ajax.php?act=get_grouding_location_barcodes',
	      data:'number='+$gouding_number,
	      error:function(){},
	      success:function(data) {
	    	  if(!data.success) {
	    		  alert(data.error);
	    	  } else {
	    		  result = data.res;//获取所有的条码编号
	    	  }
	      }
	  });
	  return result;
  }
  // 码托
  function get_tray_barcodes($gouding_number) {
	  var result = "";
	  $.ajax({
		  async:false,
	      dataType:'json',
	      type:'post',
	      url:'getTrayBarcodes?',
	      data:'number='+$gouding_number,
	      error:function(){},
	      success:function(data) {
	    	  if(!data.success) {
	    		  alert(data.error);
	    	  } else {
	    		  result = data.res;//获取所有的码托条码编号
	    	  }
	      }
	  });
	  return result;
  }
  // 打印条码入口
  function print_barcodes($gouding_barcodes) {
	  alert('yes');
  }
  
  // 打印条码
  $(document).ready(function(){
	  $('#IprintGNs_tray').click(function(){
			 var mt_code = $('#GN_TRAY_LIST').val();
			 src="../shippment/printV0?mt_code="+mt_code;
  			 $('#print').attr('src',src); 
	  })
  });
  
</script> 
</body>
</html>
