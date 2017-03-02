<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
	<title>问题单查询</title>
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

    #order-info-content, #tracking-info-content {
    	display: none;
    	margin:15px 0;
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
        		<div class="lq-col-1">
				    <div class="lq-form-group"> 
						<select name="type" id="type" class="lq-form-control">
							<option value="order_id" selected="true">订单号</option>
							<option value="tracking_number">快递单号</option>
						</select>
					</div>
				</div>
				<div class="lq-col-4">
                    <div class="lq-form-group">
						<input type="text" id="no" name="no" class="lq-form-control" required="required">
						<button type="button" id="submit" class="lq-btn lq-btn-sm lq-btn-primary">
							查询
						</button>
					</div>
				</div>
		    </div>
		</div>

		<div class="lq-row" id="order-info-content">
    		<div class="lq-col-6">
        		<table class="lq-table">
        			<thead>
        				<tr>
        					<th>波次单号</th>
        					<th>订单数量</th>
        					<th>分配状态</th>
        					<th>绑定工牌号</th>
        					<th>生成波次时间</th>
        				</tr>
        			</thead>
        			<tbody>
        				<tr>
        					<td class="oi-batch-pick-sn"></td>
        					<td class="oi-count"></td>
        					<td class="oi-status"></td>
        					<td class="oi-username"></td>
        					<td class="oi-created-time"></td>
        				</tr>
        			</tbody>
        		</table>
        	</div>
    	</div>

    	<div class="lq-row" id="tracking-info-content">
    		<div class="lq-col-6">
        		<table class="lq-table">
        			<thead>
        				<tr>
        					<th>货主</th>
        					<th>订单号</th>
        					<th>订单状态</th>
        					<th>快递方式</th>
        					<th>快递单号</th>
        					<th>波次单号</th>
        					<th>码拖条码</th>
        				</tr>
        			</thead>
        			<tbody>
        				<tr>
        					<td class="ti-customer"></td>
        					<td class="ti-order-id"></td>
        					<td class="ti-order-status"></td>
        					<td class="ti-shipping-name"></td>
        					<td class="ti-tracking-number"></td>
        					<td class="ti-batchpick-number"></td>
        					<td class="ti-mt-number"></td>
        				</tr>
        			</tbody>
        		</table>
        	</div>
    	</div>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>	
	<script type="text/javascript">
		$(document).ready(function() {
			var TYPE = "order_id";

			$("#type").on("change",function(){
	    		TYPE = $(this).val();
	    		$("#no").val("");
	    	});

            $("#no").on("keyup",function(e){
                e.preventDefault();
                if (e.which == 13 || e.which == 17) {
                    calldoQueryOrdersApi($(this));
                }
            });

            function calldoQueryOrdersApi (element) {
                var data = {
                    type: $("#type").val(),
                    no : $("#no").val()
                };
                var that = element;
                $.ajax({
                    url : "${pageContext.request.contextPath}/exception/doQueryOrders",
                    type : "post",
                    dataType : "json",
                    contentType : "application/x-www-form-urlencoded; charset=utf-8",
                    data : data,
                    beforeSend : function() {
                        $(".mw-loading").fadeIn(300);
                        that.attr("disabled",true);
                        $("#tracking-info-content").fadeOut(300);
                        $("#order-info-content").fadeOut(300);
                    },
                    success : function(data) {
                        console.log(data);
                        $(".mw-loading").fadeOut(300);
                        that.attr("disabled",false);
                        if (data.result == "success") {
                            // if (data.)
                            if (TYPE == "order_id") {
                                $(".oi-batch-pick-sn").text(data.batch_pick_sn);
                                $(".oi-count").text(data.count);
                                $(".oi-status").text(data.status);
                                $(".oi-username").text(data.username);
                                $(".oi-created-time").text(data.created_time);
                                $("#order-info-content").fadeIn(300);
                            } else if (TYPE == "tracking_number") {
                                $(".ti-customer").text(data.customer);
                                $(".ti-order-id").text(data.order_id);
                                $(".ti-order-status").text(data.order_status);
                                $(".ti-shipping-name").text(data.shipping_name);
                                $(".ti-tracking-number").text(data.tracking_number);
                                $(".ti-batchpick-number").text(data.batch_pick_sn);
                                $(".ti-mt-number").text(data.mtcode);
                                $("#tracking-info-content").fadeIn(300);
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

			$("#submit").click(function(e) {
				e.preventDefault();
				calldoQueryOrdersApi($(this));
			});
		});
	</script>
</body>
</html>
