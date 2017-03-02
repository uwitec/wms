<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>	
<title>补打印套餐条码和上架标签</title>
<style type="text/css">
    #printComboBarcode,#printGrounding{
        border: 0px;
        width: 0px;
        height: 0px;
    }
</style>
</head>
<body>
    <iframe  src=""  id="printComboBarcode"></iframe>
    <iframe  src=""  id="printGrounding"></iframe>
    <div class="lq-form-inline">
		<div class="lq-row">
			<div class="lq-col-4">
				<div class="lq-form-group">
					<label for="barcode">套餐条码:</label>
					<input type="text" id="barcode" value="" class="lq-form-control"/>
				</div>
				<div class="lq-form-group">
					<label for="conbom_num" style="padding-left:30px;">数量:</label>
					<input type="text" id="conbom_num" value="" class="lq-form-control"/>
				</div>
				<div class="lq-form-group" style="text-align:center;">
					<button type="button" id="printCombo" class="lq-btn lq-btn-sm lq-btn-primary" style="margin-right:40px;">
						打印套餐条码
					</button>
				</div>
			</div>
			<div class="lq-col-4">
				<div class="lq-form-group">
					<label for="task">加工任务单:</label>
					<input type="text" id="task" value="" class="lq-form-control"/>
					<button type="button" id="printTask" class="lq-btn lq-btn-sm lq-btn-primary">
						打印上架标签
					</button>					
				</div>
		
			</div>
		</div>
	</div>
<script type="text/javascript" >
	$(document).ready(function(){
		$("#printCombo").click(function(){
			var num=$("#conbom_num").val();
			var barcode=$("#barcode").val();
			if(num==""||barcode==""){
				alert("套餐条码和数量不能为空!");
				return ;
			}
			var src="printbarcode?comboBarcode=" + barcode + "&preNumber=" + num;
			$('#printComboBarcode').attr('src',src);
		})
		
		$("#printTask").click(function(){
			var taskId=$("#task").val();
			if(taskId==""){
				alert("加工任务单号不能为空!");
				return;
			}
			var src="printlocation_barcode?order_sn=" + taskId;
			 $('#printGrounding').attr('src',src);  
			 
			 
			 
			 
			 
		})
	})
</script>
</body>
</html>