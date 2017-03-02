<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
	<title>清空复核记录</title>
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

    #order-info-content {
    	margin:15px 0;
        display: none;
    }

    #tracking-info-content, #batchPick-info-content {
        display: none;
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
                <div class="lq-col-12">
                    <div class="lq-form-group">
                        <select name="search_type" id="search_type" class="lq-form-control">
                            <option value="order_id">订单号</option>
                            <option value="tracking_number">快递单号</option>
                            <option value="batch_pick_sn">批量单波次单号</option>
                        </select>
                        <input type="text" id="order_id" class="lq-form-control to-send">
                        <button id="btn-load" class="lq-btn lq-btn-sm lq-btn-primary">    
                            加载
                        </button>
                        <button id="btn-clear" class="lq-btn lq-btn-sm lq-btn-primary" disabled=true>    
                            清空复核记录
                        </button>
                    </div>    
                </div>
            </div>
        </div>

        <div id="info-content">
        	<div class="lq-row" id="order-info-content">
                <div class="lq-col-12">
                	<span>订单号:<span id="oi-order-id"></span>( <span id="oi-order-status"></span> )</span>
                	<span style="margin:0 20px;">包裹数量:<span id="oi-package-number"></span></span>
                	<span>快递单号:<span id="oi-tracking-number"></span></span>
                </div>
        	</div>
        	<div class="lq-row" id="tracking-info-content">
        		<div class="lq-col-12">
        			<span>快递单号:<span id="ti-tracking-number"></span></span>
        			<span style="margin:0 20px;">商品数量:<span id="ti-goods-number"></span></span>
	        		<table class="lq-table">
	        			<thead>
	        				<tr>
	        					<th>商品条码</th>
	        					<th>商品名称</th>
	        					<th>商品数量</th>
	        				</tr>
	        			</thead>
	        			<tbody></tbody>
	        		</table>
	        	</div>
        	</div>
            <div class="lq-row" id="batchPick-info-content">
                <div class="lq-col-12">
                    <span>订单数:<span class="orderSum"></span></span>
                    <span style="margin:0 20px;">快递包裹数:<span class="BPSum"></span></span>
                </div>
            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script>
    $(document).ready(function(){
    	var ORDER_ID = "" , TYPE = "order_id" , BATCH_PICK_SN = "";

    	$("#search_type").on("change",function(){
    		$(".to-send").attr("id",$(this).val()).val("");
    	});

    	$("#btn-load").on("click",function(e){
    		e.preventDefault();
    		var that = $(this),
    			$toSend = $(".to-send"),key = $toSend.attr("id"),sendData = {};
            TYPE = key;
    		sendData[TYPE] = $.trim($toSend.val());
    		$.ajax({
                type: 'post',
                dataType: 'json',
                url: 'loadOrderTns',
                data: sendData,
                beforeSend : function() {
                    $(".mw-loading").fadeIn(300);
                    that.attr("disabled",true);
                },
                success : function(data) {
                    $(".mw-loading").fadeOut(300);
                    that.attr("disabled",false);
                    console.log(data);
                    if (data.result == "success") {
                        if (TYPE == "order_id") {
                            ORDER_ID = data.order_id;
                            $("#oi-order-id").text(data.order_info.order_id);
                            $("#oi-order-status").text(data.order_info.order_status);
                            $("#oi-package-number").text(data.order_info.pack_num);
                            $("#oi-tracking-number").text(data.order_info.tracking_numbers);
                            $("#order-info-content").fadeIn(300);

                            $("#tracking-info-content").fadeOut(300);
                            $("#batchPick-info-content").fadeOut(300);
                            if (data.order_info.tracking_numbers) {
                                $("#btn-clear").attr("disabled",false);
                            }  
                        }
                        if (TYPE == "tracking_number") {
                            ORDER_ID = data.order_id;
                            $("#oi-order-id").text(data.order_info.order_id);
                            $("#oi-order-status").text(data.order_info.order_status);
                            $("#oi-package-number").text(data.order_info.pack_num);
                            $("#oi-tracking-number").text(data.order_info.tracking_numbers);
                            $("#order-info-content").fadeIn(300);

                            $("#batchPick-info-content").fadeOut(300);
                            $("#tracking-info-content").fadeIn(300);
                            $("#ti-tracking-number").text(data.tracking_number);
                            $("#ti-goods-number").text(data.tns_goods_number);
                            if (data.tns_goods_info.length) {
                                var tiHtml = "";
                                for (var i=0;i<data.tns_goods_info.length;i++) {
                                	if("WMS888888888" != data.tns_goods_info[i].barcode){
	                                    tiHtml += "<tr><td>"
	                                    + data.tns_goods_info[i].barcode + "</td><td>"
	                                    + data.tns_goods_info[i].goods_name + "</td><td>"
	                                    + data.tns_goods_info[i].goods_number + "</td></tr>";
                                	}
                                } 
                                $("#tracking-info-content table tbody").html(tiHtml);  
                            }
                            if (data.order_info.tracking_numbers) {
                                $("#btn-clear").attr("disabled",false);
                            }  
                        }
                        if (TYPE == "batch_pick_sn") {
                            BATCH_PICK_SN = data.batch_pick_sn;
                            $("#order-info-content").fadeOut(300);
                            $("#tracking-info-content").fadeOut(300);
                            $("#batchPick-info-content").fadeIn(300);
                            $('.orderSum').html(data.order_num);
                            $('.BPSum').html(data.shipping_num_str);
                            if (data.shipping_num_str) {
                                $("#btn-clear").attr("disabled",false);
                            }  
                        }
                    } else {
                        alert(data.note);
                    }
                },
                error : function(error) {
                    $(".mw-loading").fadeOut(300);
                    that.attr("disabled",false);
                    console.log("loadOrderTns接口调用失败");
                }
            });
    	});

    	$(document).on("click","#btn-clear",function(e){
    		e.preventDefault();
    		var that = $(this);
            if (TYPE == "order_id" || TYPE == "tracking_number") {
                var sendData = {
                    order_id : $.trim(ORDER_ID)
                };
            }
    		if (TYPE == "batch_pick_sn") {
                var sendData = {
                    batch_pick_sn : $.trim(BATCH_PICK_SN)
                };
            }
    		$.ajax({
                type: 'post',
                dataType: 'json',
                url: 'cleanOrderTns',
                data: sendData,
                beforeSend : function() {
                    $(".mw-loading").fadeIn(300);
                    that.attr("disabled",true);
                },
                success : function(data) {
                    $(".mw-loading").fadeOut(300);
                    console.log(data);
                    if (data.result == "success") {
                        alert("清空复核记录成功");
                        $(".to-send").val("");
                        $("#order-info-content").fadeOut(300);
                        $("#tracking-info-content").fadeOut(300);
                        $("#batchPick-info-content").fadeOut(300);
                    } else {
                        alert(data.note);
                    }
                },
                error : function(error) {
                    $(".mw-loading").fadeOut(300);
                    that.attr("disabled",false);
                    console.log("cleanOrderTns接口调用失败");
                }
            });
    	});
    });
    </script>
</body>
</html>
