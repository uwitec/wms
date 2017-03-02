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
	<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
	<title>自动绑定码托</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
  	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
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

    #mt-table {
    	display: none;
    	margin:15px 0;
    }

    #print{
        border: 0px;
        width: 0px;
        height: 0px;
    }
    .errorMsg {
        font-size: 16px;
        font-weight: 600;
    }
    .tables {
        width: 500px;
    }
  	</style>
</head>
<body>
    <iframe id="print"></iframe>
	<div class="modal-wrap mw-loading">
        <div class="modal-content mc-loading">
            <i class="fa fa-spinner fa-spin" id="loading"></i>
        </div>
    </div>
    <div class="main-container">
        <!--<form class="lq-form-inline">-->
        	<div class="lq-form-inline">
				<div>
                    <div class="lq-form-group">
                        <label for="no">扫描波次号:</label>
						<input type="text" id="no" name="no" id="no" class="lq-form-control" required="required">
						<!-- <button type="button" id="submit" class="lq-btn lq-btn-sm lq-btn-primary">
							查询
						</button> -->
                        <button type="button" id="btn-print" class="lq-btn lq-btn-sm lq-btn-primary" disabled=true>
                            打印
                        </button>
					</div>
				</div>
		    </div>
		<!--</form>-->
		<div class="tables">
    		<table class="lq-table" id="mt-table">
    			<thead>
    				<tr>
    					<th>码托条码</th>
    					<th>快递方式</th>
    					<th>数量</th>
    				</tr>
    			</thead>
    			<tbody>
    			</tbody>
    		</table>
            <div class="newTable">
                
            </div>
    	</div>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>	
	<script type="text/javascript">
		$(document).ready(function() {
			
			//扫描订单号
	        $("#no").focus().on("keyup",function(e){
	            e.preventDefault();
	            if (e.which == 13 || e.which == 17) {
	                loadBatchPickSn($.trim($(this).val()));
	            }
	        });
	        //根据订单号获取绑定和未绑定的商品信息
	        function loadBatchPickSn(no) {
			
				var data = {
					no : $("#no").val()
				};
                var that = $(this);
				$.ajax({
					url : "${pageContext.request.contextPath}/batchShipment/queryAllShipment",
					type : "post",
					dataType : "json",
					contentType : "application/x-www-form-urlencoded; charset=utf-8",
					data : data,
                    beforeSend : function () {
                        $(".mw-loading").fadeIn(300);
                        that.attr("disabled",true);
                    },
					success : function(data) {
                        $(".mw-loading").fadeOut(300);
                        that.attr("disabled",false);
                        $('.newTable').after('');
						console.log(data);
						if (data.result == "success") {
                            var html = "";
                            for (var i=0;i<data.list.length;i++) {
                                html += "<tr><td class='mtCode'>"
                                + data.list[i].MTCode + "</td><td class='shippingName'>"
                                + data.list[i].shipping_name + "</td><td>"
                                + data.list[i].count + "</td></tr>";
                            }
                            $("#mt-table tbody").html(html);
                            $("#mt-table").fadeIn(300);
                            $("#btn-print").attr("disabled",false);
                            if(data.message){
                            	alert(data.message);
                                var tableHtml = '<table class="lq-table errorOrder"><thead><th>已取消的订单号</th><th>快递单号</th></thead><tbody class="errorOrderHtml"></tbody></table>';
                                $('.newTable').html(tableHtml);
                                var trHtml = '';
                                for (var i = 0; i < data.errorOrderList.length; i ++) {
                                    trHtml += '<tr><td>'+data.errorOrderList[i].order_id+'</td><td>'+data.errorOrderList[i].tracking_Numbers+'</td></tr>';
                                }
                                $('.errorOrderHtml').html(trHtml);
                                $('.errorOrder').before('<p class="errorMsg">'+data.errortrackingNums+'</p>');
                            }
						} else {
							alert(data.message);
						}
					},
					error : function() {
                        $(".mw-loading").fadeOut(300);
                        that.attr("disabled",false);
						console.log("doQueryOrders接口调用失败");
					}
				});
			}
            
            $("#btn-print").on("click",function(e){
                e.preventDefault();
                var mt_code = [], shipping_name = [];
                $(".mtCode").each(function(){
                	mt_code.push($(this).text());
                });
                $(".shippingName").each(function(){
                	shipping_name.push($(this).text());
                });
                console.log(mt_code.join(","));
                console.log(shipping_name.join(","));
                src = "../batchShipment/printAllShipment?mt_code="+mt_code.join(",")+"&shipping_name="+encodeURI(encodeURI(shipping_name.join(",")));
                $('#print').attr('src',src);
            });
		});
	</script>
</body>
</html>