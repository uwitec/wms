<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>称重</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
  <link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
 <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>

<style type="text/css">
<!--
.STYLE1 {color: #FF0000}
li{list-style:none;font-size:14px;line-height:200%;}
table, td,tr{
    border:1px solid #B7B7B7;
    border-collapse:collapse;
}
-->
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
.mw-consume {
    z-index: 900;
    background-color: rgba(0,0,0,.5);
}

.mc-consume,.mc-transfercode {
    position: absolute;
    top: 200px;
    left: 50%;
    width: 360px;
    margin-left: -180px;
    padding:20px;
    background-color: #fff;
    box-shadow: 0 0 5px #333;
    border-radius: 3px;
}
.mc-consume .lq-form-control {
	height:35px;
	width: 80%;
	margin-left:10%;
}
.mc-consume button{
	margin-top: -10px;
}
</style>

<script type="text/javascript">

    var type = '{$type}';
    var appData;
    var total = 0;
    var isWeighPackBox="";
    window.onload = function() {
        document.getElementById('tracking_number').select();
        document.getElementById('tracking_number').focus();
        isWeighPackBox="${isWeighPackBox}";
        if(window.navigator.userAgent.indexOf("Chrome")!=-1)
        	setApp();
    }
	//获取chrome APP上的称重数据
	function linkToApp(eid,bit){
		// var eid = 'dmdbbmdjpggkokhedkhcnbhpiecggfeo';

	}

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
	
	// 多个运单号只展示最后1个
	function check_remove_child(tracking_number, weight) {
		var ul = document.getElementById("list");
		var li = document.getElementsByTagName('li');
		for ( var i = 0; i < li.length; i++) {
			if (li[i].innerHTML.indexOf(tracking_number) > 0) {
				ul.removeChild(li[i]);
				break;
			}
		}
	}

	
	function remove_ul(li_value) {
		var ul = document.getElementById("list");
		var li = document.getElementsByTagName('li');
		for ( var i = 0; i < li.length; i++) {
			if (li[i].innerHTML == li_value) {
				ul.removeChild(li[i]);
			}
		}
	}

	var KEY = {
		RETURN : 13, // 回车
		CTRL : 17, // CTRL
		TAB : 9
	};
	
	var TRACKING_NUM,WEIGHT;

	// 响应 输入框数据 ytchen
	function change_tracking_number(event) {
		eventObj = event == null ? window.event : event;
		keyCode = eventObj.which == null ? event.keyCode : eventObj.keyCode;

		if (keyCode == KEY.RETURN || keyCode == KEY.CTRL) {
			get_weight(keyCode);
		}
	
	}
	//称重数据校验
	function weightData(weight){
		var tracking_number = document.getElementById("tracking_number").value;
		console.log("外层tacking_num"+tracking_number);
		tracking_number = tracking_number.replace(/^\s+|\s+$/g, "");
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
				if(confirm("重量超过50Kg，是否重新称重")){
					return false;
				}
			}
			WEIGHT=weight;
			if(isWeighPackBox=="Y"){
				
				$(".mw-consume").fadeIn(300,function(){
					$("#barcode").val("");
					$("#barcode").focus();
				});
			}else{
				ul_append_child(tracking_number, weight,"");
			}
			
			document.getElementById("tracking_number").value ='';
		}
	}
	$(document).on("keyup","#barcode",function(e){
			if(e.keyCode==KEY.RETURN||e.keyCode==KEY.CTRL){
				if($(this).val()!=""){
					var barcode=$.trim($(this).val());
					console.log("内层tacking_num"+TRACKING_NUM);
					ul_append_child(TRACKING_NUM, WEIGHT,barcode);
				}else{
					alert("耗材条码不能为空!");
					$(this).focus();
					
				}
			}
	})
	// 获取输入框 数据 ytchen
    function get_weight(keyCode) {
		var tracking_number = document.getElementById("tracking_number").value;
		tracking_number = tracking_number.replace(/^\s+|\s+$/g, "");
		if(tracking_number!="")
			TRACKING_NUM=tracking_number;
		else
			TRACKING_NUM=TRACKING_NUM;
		// return键就不管了，免得触发键和仓库键盘操作冲突
		if (tracking_number == '') {
			if (keyCode != KEY.RETURN) {
				alert('请输入快递单号');
			}
			return false;
		}

 		var note = check_order_status(tracking_number);
		if (note != '') {
			alert('该快递单号:' + tracking_number + note+'\n请删除该快递单号再扫描下一个');
			return false;
		} 
		
		var leqee_weight = get_realtime_weight();
		if (leqee_weight != undefined) {
			var weight = prompt("输入称重结果", leqee_weight);
			weightData(weight);
		} else if(window.navigator.userAgent.indexOf("Chrome")!=-1) { //保留上方使用IE浏览器称重的部分，当使用Chrome浏览器是进入该模块
			var eid=window.localStorage["eid"];
			var bit=window.localStorage["bitrate"];
		     chrome.runtime.sendMessage(eid,{bitrate:bit},function(res){
		    	 try{
		            if(res.error){
		              alert(res.error);
		            }else{
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
		
			
		}else{
			alert("称重结果显示异常，请删除已扫描运单号，重新扫描！\n注意检查下火狐的版本，版本不要超过12.0！\n官网下载：http://firefox.com.cn/download/  在这个官网页面下载12.0版本的火狐即可！");
		}

	}
	
  	//扫描运单号时检查订单状态 ytchen  
	// 检查订单状态，取消和没有订单对应的订单不能称重 
	function check_order_status(tracking_number) {
		var note;
		$.ajax({
			async : false,
			type : 'POST',
			url : 'checkOrderStatus?',
			data : 'tracking_number=' + tracking_number,
			dataType : 'json',
			error : function() {
				alert('扫描运单号时检查订单状态失败:' + tracking_number);
				return false
			},
			success : function(data) {
				if(data.result=='failure'){
					note = data.note; //错误消息
				}else{
					note = '';
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
	function check_weighing(tracking_number, weight,barcode) {
		// 暂时不需要
		return 0;
		var weighing_check;
		$.ajax({
			async : false,
			type : 'POST',
			url : 'checkWeighing',
			data : 'tracking_number=' + tracking_number + '&weight=' + weight+'&barcode='+barcode,
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
	
	function ul_append_child(tracking_number, weight,barcode) {
		//称重即记录重量
		$.ajax({
			async : false,
			type : 'POST',
			url : 'updateLeqeeWeight?',
			data : 'tracking_number=' + tracking_number + '&weight='+ weight+'&barcode='+barcode,
			dataType : 'json',
			error : function() {
				alert('请重新扫描快递单号:' + tracking_number);
			},
			success : function(data) {

				if(data.note!=""&&data.note){
					alert(data.note);
					$("#barcode").val("");
					return false;
				}					

				$(".mw-consume ").fadeOut(300);
				var ul = document.getElementById("list");
				var li = document.createElement('li');
				var li_value = document.createTextNode("快递单号:" + tracking_number + " 重量:" + weight+"kg");
				check_remove_child(tracking_number, weight); // 多个运单号只展示最后1个 
				li.appendChild(li_value);
				ul.appendChild(li);
				$("#tracking_number").focus();
				li.onclick = function() {
					var result = confirm("删除记录:" + li.innerHTML);
					if (result) {
						remove_ul(li.innerHTML);
					} else {
						// 增加修改重量功能  
						var result = confirm("是否修改重量：\n" + li.innerHTML);
						if (result) {
							var update_weight = prompt("输入修改的重量", weight);
							var check_weight = check_weight_number(update_weight); // 判断字符正确性 
							if (!check_weight) {
								alert("请输入正确的数字！不要有其他字符");
								return false;
							}
							if (update_weight > 999) {
								alert('重量超过999kg，超出最大设置，请检查下是否是称重的重量！');
								return false;
							}
							check_weighing(tracking_number, update_weight,barcode); // 称重校验  
						} else {
							return false;
						}
					}
				};
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

    <div class="modal-wrap mw-consume">
        <div class="modal-content mc-consume">
        <p style="width:100%;text-align:center;margin:10px auto;">绑定耗材</p>
            <input type="text" name="barcode" id="barcode" class="lq-form-control">
        </div>
    </div>
	
	<div class="main-container">
		<!-- <h3>包裹称重</h3>  -->
		<form id="form" action="" method="post" >
			<ul id="list"></ul>
			快递单号：<br />
			<textarea name="tracking_number" cols="25" rows="8"
				id="tracking_number" onkeyup="change_tracking_number(event)"></textarea>
			<p id="number"></p>
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