<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
    <title>批量波次查询面单序号</title>
    <link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style>
    #print{
        border: 0px;
        width: 0px;
        height: 0px;
    }
    input[type="text"] {
    	height: 30px;
    }
    .pageIn {
    	display: none;
    }
    .note {
    	font-size: 14px;
    }
    .warning {
    	color: #f00; 
    	display:none;
    }
    </style>
</head>
<body>
    <iframe  src=""  id="print"></iframe>
    <div class="main-container">
        <div class="lq-form-inline">
            <div class="lq-row">
                <div class="lq-col-6">
                    <div class="lq-form-group">
                        <label for="batch_pick_sn">扫描波次单号:</label>
                        <input type="text" name="batch_pick_sn" id="batch_pick_sn" class="lq-form-control">
                        <button class="lq-btn lq-btn-primary" id="search" style="margin: 0 10px;">查询</button>
                    </div>           
                </div>
            </div>
        </div>
        <table class="lq-table" id="batchTable" style="width:65%">
        	<thead>
        		<th>序号</th>
        		<th>订单号</th>
        		<th>快递运单号</th>
        	</thead>
        	<tbody>
        		
        	</tbody>
        </table>
    </div>
    <script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script>
    $(document).ready(function(){
 		$("#search").click(function(e){
 			e.preventDefault();
 			$("tbody").html(" ");
 			$.ajax({
 				url:'./searchTnsSequence',
 				dataType: 'json',
 				type: 'post',
 				data:{
 					batch_pick_sn:$("#batch_pick_sn").val()
 				},
 				success:function(res){
 					console.log(res);
 					if(res.result=="success"){
 						showTable(res.list);
 					}else{
 						alert(res.note);
 						$("#batch_pick_sn").val("").focus();
 					}
 				}
 			})
 		})
 		
 		function showTable(data){
 			var tbHtml="";
 			if(data.length>0){
 				for(var i in data){
 					tbHtml+='<tr>';
 					tbHtml+='<td>'+data[i].pm+'</td>';
 					tbHtml+='<td>'+data[i].order_id+'</td>';
 					tbHtml+='<td>'+data[i].tracking_number+'</td>';
 					tbHtml+='</tr>';
 				}
 				$("#batchTable tbody").html(tbHtml);
 			}
 		}
    	
    })

    </script>
</body>
</html>
