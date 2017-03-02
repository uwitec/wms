<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>	
<title>套餐查询页面</title>
<style type="text/css">
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
</style>
</head>
<body>
	<div class="modal-wrap mw-loading">
        <div class="modal-content mc-loading">
            <i class="fa fa-spinner fa-spin" id="loading"></i>
        </div>
    </div>
    <div class="main-container">
    	<div class="lq-form-inline">
    		<div class="lq-row">
    			<div class="lq-col-4">
                    <div class="lq-form-group">
                    	<label for="no">套餐条码:</label>
						<input type="text" id="no" name="no" class="lq-form-control" required="required">
						<button type="button" id="submit" class="lq-btn lq-btn-sm lq-btn-primary">
							查询
						</button>
					</div>
				</div>
    		</div>
    	</div>
    	<div class="lq-row" id="prepack-info">
    		<div class="lq-col-8">
    			<table class="lq-table">
    				<thead>
    					<th>货主</th>
    					<th>套餐条码</th>
    					<th>套餐名称</th>
    					<th>缺货数量</th>
    					<th>可用数量</th>
    					<th>申请数量</th>
    					<th>已完成数量</th>
    				</thead>
    				<tbody></tbody>
    			</table>
    		</div>
    	</div>
    </div>
 <script type="text/javascript">
 	function showTable(data){
 		var tdHtml="";
 		var valiable=(data.neednumber>0?0:Math.abs(parseInt(data.neednumber)));
 		var lake=(data.neednumber>0?data.neednumber:0);
		tdHtml+='<tr>';
		tdHtml+='<td>'+data.name+'</td>';
		tdHtml+='<td>'+data.barcode+'</td>';
		tdHtml+='<td>'+data.product_name+'</td>';
		tdHtml+='<td>'+lake+'</td>';
		tdHtml+='<td>'+valiable+'</td>';
		tdHtml+='<td>'+data.shenqingnumber+'</td>';
		tdHtml+='<td>'+data.actualnumber+'</td>';
		tdHtml+='</tr>';
 		$("tbody").html(tdHtml);
 	}
 	$(document).ready(function(){
 		$("#submit").click(function(e){
 			e.preventDefault();
 			var barcode=$("#no").val();
 			var that=$(this);
 			$.ajax({
 				url: '../sale/prepackQuery',
 				type: 'post',
 				data: {
 					barcode:barcode
 				},
 				beforeSend: function(){
                    $(".mw-loading").fadeIn(300);
                    that.attr("disabled",true);
 				},
 				success: function(res){
 					console.log(res);
 					$(".mw-loading").fadeOut(300);
 					that.removeAttr("disabled");
 					if(res.success) {
 						showTable(res.prepackMap);
 					}else{
 						alert(res.note);
 					}
 				
 						
 				},
 				error: function(err){
 					console.log(err);
 					$(".mw-loading").fadeOut(300);
 					that.removeAttr("disabled");
 				}
 			})
 		})
 	})
 </script>
</body>
</html>