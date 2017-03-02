<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
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
<title>批量称重</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
  	<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">    
 	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<style type="text/css">
	body{
		font-size:14px;
		position:relative;
	}

	.popup{
	          position:absolute;
	          width: 300px;
	          height: 200px;
	          border:1px solid #dedede;
	          border-radius: 5px;
	          box-shadow: 1px 1px 2px rgba(10,10,10,0.4),
	                      -1px -1px 2px rgba(10,10,10,0.4);
	          top: 120px;
	          left:50%;
	          margin-left: -150px;
	          padding: 7px;
	      	  display: none; 
	}
	input.lq-form-control{
		width: 60%;
		height: 25px;
	}
	label {
		width: 3rem;
		height: 25px;
		display:inline-block;
		position: relative;
		top: -3px;
	}
	h3{
		text-align:center;
	}
	.lq-form-group {
		line-height:1.4;
		text-align:center;
	}
	button.cancel {
		margin: 0 100px 0 0;
	}
	</style>

<script type="text/javascript">

	$(document).ready(function(){
	    // 确定页面载入时鼠标焦点的定位
	    $('#batch_pick_sn').focus();  // 定位到收货容器扫描框
	    $('#batch_pick_sn').bind('keyup', listen);
	    $('#tracking_number').bind('keyup', listen);
        if(window.navigator.userAgent.indexOf("Chrome")!=-1)
        	setApp();
	});
	
	//第一次进入称重页面时候需要配置连接ChromeAPP所需要的AppID和波特率
	function setApp(){
		if(window.localStorage){
			var localStorage=window.localStorage;
			if(!(localStorage["eid"]&&localStorage["bitrate"])){
				var pop=$(".popup");
				pop.fadeIn(300);
				$("#eid").focus();
			}
		}else{
			alert("当前浏览器不支持localStorage,请更新当前Chrome浏览器版本!");
		}
	}	
	
	var KEY = {
		RETURN : 13, // 回车
		CTRL : 17, // CTRL
		TAB : 9
	};
	
	function listen(event){
        var that = $(this);
        switch (event.keyCode) {
            case KEY.RETURN:
            case KEY.CTRL:
                scan(event,that);
                event.preventDefault();
                break;
        }
    }
	  
	function scan(event,that){ 
	      var input = that,
	      barcode = $.trim(input.val()),
	      input_id = input.attr('id');
	      if (barcode != '') {
	          if(input_id == 'batch_pick_sn') {
	        	  check_batch_pick_sn(event);
	          } else if(input_id == 'tracking_number') {
	              check_tracking_number(event);
	          }
	      } 
	}


	
	function check_batch_pick_sn(event) {
		var batch_pick_sn = $('#batch_pick_sn').val();
		var res_check = check_batch_pick_sn_v2(batch_pick_sn);
		if(res_check){
			$("#tracking_number").focus();
		}else{
			$("#batch_pick_sn").val("");
		}
	}
	
	// 检测该波次内的所有的订单状态都处于已复核
	function check_batch_pick_sn_v2(batch_pick_sn){
		var result = true;
		$.ajax({
			mode: 'abort',
            async : false,
            type: 'POST',
            dataType: 'json',
            url : '../batchShipment/checkBatchPickSn', 
            data: {
            	'batch_pick_sn':batch_pick_sn
            },
            success: function(data) {
            	if(data.result == 'failure'){
            		alert(data.note);
            		$("#note").html(data.note);
            		result = false;
            	}else if(data.result == 'info'){
            		alert(data.note);
            		$("#note").html(data.note);
            	}
            },
            error: function(data) {
                result = false;
                alert('ajax请求错误, 请重新扫描条码:' + pallet_sn); 
                $(".note").css("display","none");
                $("#table").html("");
            }
        });
		return result;
	}
	
	
	// 称重面单号验证
	function check_tracking_number(event) {
		
		var batch_pick_sn = $('#batch_pick_sn').val();
		if(batch_pick_sn == '' || batch_pick_sn == null){
			alert('请先填写波次单号!');
			return false;
		}
		
		eventObj = event == null ? window.event : event;
		keyCode = eventObj.which == null ? event.keyCode : eventObj.keyCode;
		if (keyCode == KEY.RETURN || keyCode == KEY.CTRL) {
			get_weight(keyCode);
		}
	}
	//称重数据校验
	function weightData(weight){
		var tracking_number = document.getElementById("tracking_number").value;
		var batch_pick_sn=document.getElementById("batch_pick_sn").value;
		tracking_number = tracking_number.replace(/^\s+|\s+$/g, "");
		batch_pick_sn = batch_pick_sn.replace(/^\s+|\s+$/g, "");
		if (weight) {
			var check_weight = check_weight_number(weight); // 判断字符正确性
			if (!check_weight) {
				alert("请输入正确的数字！不要有其他字符");
				return false;
			}
			if (weight > 999) {//
				alert('重量超过999kg，超出最大设置，请检查下是否是称重的重量！');
				return false;
			}
			if(weight==0){
				alert("包裹重量不允许为0！请重新操作！");
				return false;
			}
			if(weight>=50){
				if(confirm("重量为50Kg，是否需要重新称重?")){
					return false;
				}
			}
			ul_append_child(batch_pick_sn,tracking_number, weight);
			document.getElementById("tracking_number").value = '';
		}
	}
	// 获取输入框 数据
    function get_weight(keyCode) {
		var tracking_number =  $('#tracking_number').val();
		var batch_pick_sn = $('#batch_pick_sn').val();
		tracking_number = tracking_number.replace(/^\s+|\s+$/g, "");
		// return键就不管了，免得触发键和仓库键盘操作冲突
		if (tracking_number == '') {
			if (keyCode != KEY.RETURN) {
				alert('请输入快递单号');
			}
			return false;
		}

		var note = check_order_status(batch_pick_sn,tracking_number);
		if (note != '') {
			alert('该快递单号:' + tracking_number +note+'\n请删除该快递单号再扫描下一个');
			$("#note").html('该快递单号:' + tracking_number +note+'\n请删除该快递单号再扫描下一个');
			//$('#batch_pick_sn').val("");
			$('#tracking_number').val("");
			$('#tracking_number').focus();
			return false;
		} 
		
		var leqee_weight = get_realtime_weight();
		if (leqee_weight != undefined) {
			var weight = prompt("输入称重结果", leqee_weight);
			weightData(weight);
		}else if(window.navigator.userAgent.indexOf("Chrome")!=-1){
			var eid=window.localStorage["eid"];
			var bit=window.localStorage["bitrate"];

		     chrome.runtime.sendMessage(eid,{bitrate:bit},function(res){
		    	 try{
		            if(res.error){
		              alert(res.error);
		            }else{
		    			//leqee_weight=linkToApp(eid,bitrate);
		    			var weight=prompt("输入称重结果", res.data);
		    			weightData(weight);
		            }
		    	 }catch(e){
		    		 alert("确保插件处于开启状态，检查插件id与配置的的插件id是否一样");
		    		 $(".popup").fadeIn(300);
		    		 $("#eid").val(eid);
		    		 $("#bitrate").val(bit);
		    		 
		    	 }
		       })	
		}else {
			alert("称重结果显示异常，请删除已扫描运单号，重新扫描！\n注意检查下火狐的版本，版本不要超过12.0！\n官网下载：http://firefox.com.cn/download/  在这个官网页面下载12.0版本的火狐即可！");
		}

	}
	
  	// 扫描运单号时检查订单状态，运单号是否在波次单内
	// 检查订单状态，取消和没有订单对应的订单不能称重 
	function check_order_status(batch_pick_sn,tracking_number) {
		var note = "";
		$.ajax({
			async : false,
			type : 'POST',
			url : '../batchShipment/checkBatchTrackingNumber',
			data : {
				'batch_pick_sn':batch_pick_sn,
				'tracking_number' : tracking_number
			},
			dataType : 'json',
			error : function() {
				note = '扫描运单号时检查订单状态失败:' + tracking_number;
			},
			success : function(data) {
				if(data.result=='failure'){
					note = data.note;
				}
			}
		});
		return note;
	}
  
	// 得到实时的重量 ytchen
	function get_realtime_weight() {
		var leqee_weight = get_clipboard_data();
		var i = 0;
		while (leqee_weight == undefined) {
			leqee_weight = get_clipboard_data();
			if (leqee_weight != undefined) {
				break;
			}
			if (i >= 20) {
				break;
			}
			i++;
		}
		return leqee_weight;
	}
	// 剪贴板数据 ytchen
	function get_clipboard_data() {
		if (window.clipboardData) {
			return (window.clipboardData.getData('text'));
		} else {
			if (window.netscape) {
				var i = 0;
				try {
					netscape.security.PrivilegeManager
							.enablePrivilege("UniversalXPConnect");
					var clip = Components.classes["@mozilla.org/widget/clipboard;1"]
							.createInstance(Components.interfaces.nsIClipboard);
					if (!clip) {
						return;
					}
					var trans = Components.classes["@mozilla.org/widget/transferable;1"]
							.createInstance(Components.interfaces.nsITransferable);
					if (!trans) {
						return;
					}
					trans.addDataFlavor("text/unicode");
					clip.getData(trans, clip.kGlobalClipboard);
					var str = new Object();
					var len = new Object();
					trans.getTransferData("text/unicode", str, len);
				} catch (e) {
					if (i < 10) {
						setTimeout("get_clipboard_data();", '100');
						i++;
						return;
					}
				}
				if (str) {
					if (Components.interfaces.nsISupportsWString) {
						str = str.value
								.QueryInterface(Components.interfaces.nsISupportsWString);
					} else {
						if (Components.interfaces.nsISupportsString) {
							str = str.value
									.QueryInterface(Components.interfaces.nsISupportsString);
						} else {
							str = null;
						}
					}
				}
				if (str) {
					return (str.data.substring(0, len.value / 2));
				}
			}
		}
		return null;
	}
	
	// 检测输入的重量 ytchen
	function check_weight_number(weight) {
		var reg = /(^\d+\.?\d+$)|(^\d+$)/;
		if (!weight.match(reg)) {
			return false;
		} else {
			return true;
		}
	}
	//称重校验 ytchen
	function check_weighing(tracking_number, weight) {
		// 暂时不需要
		return 0;
		var weighing_check;
		$.ajax({
			async : false,
			type : 'POST',
			url : 'checkWeighing',
			data : 'tracking_number=' + tracking_number + '&weight=' + weight,
			dataType : 'json',
			error : function() {
				alert('扫描运单号时称重校验失败:' + tracking_number);
				return false
			},
			success : function(data) {
				weighing_check = data.weighing_check;
			}
		});
		return weighing_check;
	}
	
	//称重即记录重量
	function ul_append_child(batch_pick_sn,tracking_number, weight) {
		$.ajax({
			async : false,
			type : 'POST',
			url : '../batchShipment/updateBatchWeight',
			data : {
				'batch_pick_sn' : batch_pick_sn,
				'tracking_number' : tracking_number ,
				'weight': weight
			},
			dataType : 'json',
			error : function() {
				alert('请重新扫描快递单号:' + tracking_number);
			},
			success : function(data) {
				if(data.info == 'success'){
					alert("该批次称重已完成");
					$("#note").html("该批次称重已完成");
					$('#batch_pick_sn').val("");
					$('#tracking_number').val("");
					$('#batch_pick_sn').focus();
				}else{
					alert(data.info);
					$("#note").html(data.info);
					$('#tracking_number').val("");
					$('#tracking_number').focus();
				}
			}
		});
	}

	
</script>
</head>
<body>
	<!--弹出框用来配置eid和波特率  -->
	<div class="popup">
		<h3>配置称重信息</h3>
		<div class="lq-form-group">
			<label for="eid">插件ID</label>
			<input type="text" id="eid" class="lq-form-control">
		</div>
		<div class="lq-form-group">
			<label for="bitrate">波特率</label>
			<input type="number" id="bitrate" class="lq-form-control" value="9600">
		</div>
		<div class="lq-form-group">
			<button class="lq-btn lq-btn-danger cancel" id="cancel">取消</button>
			<button class="lq-btn lq-btn-primary" id="save">保存</button>		
		</div>
	</div>
	<div class="main-container" style="margin:20px">
		<form id="form" action="" method="post" >
			<div  class="lq-form-inline">
				<div class="lq-row">
					<div class="lq-col-3">
						<div class="lq-form-group">
							<label for="batch_pick_sn">波次单号:</label>
							<input type="text" name="batch_pick_sn" value="" id="batch_pick_sn"  class="lq-form-control">
						</div>
						<div class="lq-form-group">
							<label for="tracking_number">快递单号:</label>
							<input type="text" name="tracking_number" value="" id="tracking_number" class="lq-form-control">
						</div>
					</div>
				</div>
			</div>
			 <div class="lead" style="color:red" id="note"></div>
		</form>
	</div>
<script>
$(document).ready(function(){
	//用户自定义设置eid和bitrate
	$("#save").click(function(e){
		e.preventDefault();
		if(window.localStorage){
			var localStorage=window.localStorage;
			var eid=$("#eid").val();
			var bitrate=$("#bitrate").val();
			if(eid==""){
				alert("插件ID不能为空!");
				$("#eid").focus();
			}else if(bitrate==""){
				alert("波特率不能为空!")
				$("#bitrate").focus();
			}else{
				localStorage["eid"]=eid;
				localStorage["bitrate"]=bitrate;
				alert("配置成功!");
				$(".popup").fadeOut(300);
				$("#tracking_number").focus();
			}
		}else{
			alert("当前浏览器不支持localStorage,请更新当前Chrome浏览器版本!");
		}
	})
	
	$("#cancel").click(function(){
		$(".popup").fadeOut(300);
	})
})
</script>	
</body>
</html>