<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
    <title>复核/打印面单</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style>

    #unbind-wrap, #bind-wrap,.hidden-div {
        display: none;
    }

    #unbind-wrap {
        margin-top: 30px;
        margin-bottom: 30px;
    }

    .lq-table-title {
        font-size: 14px;
        font-weight: bold;
        color: #337ab7;
    }

    .th_goods_barcode {
        width: 15%;
    }

    .th_goods_name {
        width: 75%;
    }

    .th_goods_number {
        width: 10%;
    }

    .one-package {
        margin-bottom: 20px;
    }

    .one-package p {
        margin:5px 0;
        font-weight: bold;
        color: #333;
    }

    .one-package-tn {
        margin-right: 20px;
    }

    .mw-consume,.mw-transfer-code,.mw-batch-sn {
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

    #consume_id {
        display: block;
        width: 100%;
        margin:20px auto;
    }

    #btn-bind-consume {
        display: block;
        margin:10px auto;
    }

    #print{
        border: 0px;
        width: 0px;
        height: 0px;
    }

    </style>
</head>
<body>
    <audio id="ad-success" preload>
        <%-- <source src="${pageContext.request.contextPath}/static/audio/success.ogg" type="audio/ogg"> --%>
        <source src="${pageContext.request.contextPath}/static/audio/success.wav" type="audio/mpeg">
    </audio> 
    <audio id="ad-error" preload>
       <%--  <source src="${pageContext.request.contextPath}/static/audio/error.ogg" type="audio/ogg"> --%>
        <source src="${pageContext.request.contextPath}/static/audio/error.wav" type="audio/mpeg">
    </audio> 
    <iframe  src=""  id="print"></iframe>
    <!-- <div class="modal-wrap mw-loading">
        <div class="modal-content mc-loading">
            <i class="fa fa-spinner fa-spin" id="loading"></i>
        </div>
    </div> -->    
    <div class="modal-wrap mw-consume">
        <div class="modal-content mc-consume">
        <p style="width:100%;text-align:center;margin:10px auto;">绑定耗材</p>
            <input type="text" name="consume_id" id="consume_id" class="lq-form-control">
            <button id="btn-bind-consume" class="lq-btn lq-btn lq-btn-primary">    
                	绑定耗材
            </button>
        </div>
    </div>
    
    
    <!-- 扫描批次号弹出框 -->
    <div class="modal-wrap  mw-batch-sn" >
    	<div class="modal-content mc-consume" style="height: 120px;">
    		<label for="batch_sn">扫描批次号:</label>
            <input type="text" name="batch_sn" id="batch_sn" class="lq-form-control" style="display:inline;width: 70%;margin:25px auto;">
    	</div>
    </div> 
    
    <div class="modal-wrap mw-transfer-code">
        <div class="modal-content mc-transfercode">
        <p style="width:100%;text-align:center;margin:10px auto;">扫描物流码</p>
            <input type="text" name="transferCode" id="transferCode" class="lq-form-control">
        </div>
    </div>
    <div class="main-container">
        <div class="lq-form-inline">
            <div class="lq-row">
                <div class="lq-col-6">
                    <div class="lq-form-group">
                        <label for="order_id">扫描订单号:</label>
                        <input type="text" name="order_id" id="order_id" class="lq-form-control">
                        <button id="btn-add-shipment" class="lq-btn lq-btn-sm lq-btn-primary" disabled=true>    
                            追加面单(快捷键:ALT)
                        </button>
                        <button id="btn-cancel" class="lq-btn lq-btn-sm lq-btn-primary" disabled=true>   
                            取消复核(快捷键:↓)
                        </button>
                    </div>
                </div>
            </div>
             <div class="lq-row hidden-div">
                <div class="lq-col-5">
                    <p>总复核数：<span class="total_number"></span>　待复核数：<span class="unbind_number"></span></p>
                </div>
            </div>
        </div>
        <div id="unbind-wrap">
            <div class="lq-form-inline">
                <div class="lq-row">
                    <div class="lq-col-3">
                        <div class="lq-form-group">
                            <label for="barcode">扫描商品条码:</label>
                            <input type="text" name="barcode" id="barcode" class="lq-form-control">
                        </div>
                    </div>
                </div>
            </div>
            <div class="lq-row">
                <div class="lq-col-6">
                    <p class="lq-table-title">未绑定商品信息</p>
                    <table class="lq-table" id="unbind-table">
                        <thead>
                            <tr>
                                <th class="th_goods_barcode">商品条码</th>
                                <th class="th_goods_name">商品名称</th>
                                <th class="th_goods_number">数量</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
        <div id="bind-wrap">
            <div class="lq-row">
                <div class="lq-col-6">
                    <p class="lq-table-title">已绑定商品信息</p>
                    <div id="package-content">
                        <!-- <div class="one-package">
                            <p>
                                快递单号:<span class="one-package-tn"></span>
                                商品数量:<span class="one-package-gn"></span>
                            </p>
                            <table class="lq-table bind-table">
                                <thead>
                                    <tr>
                                        <th class="th_goods_barcode">商品条码</th>
                                        <th class="th_goods_name">商品名称</th>
                                        <th class="th_goods_number">数量</th>
                                    </tr>
                                </thead>
                                <tbody></tbody>
                            </table>
                        </div> -->
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script>
    $(document).ready(function(){
        //一些全局变量
        var ORDER_ID = "", SHIPMENT_ID = "", BIND_HEAD_CLASS = "",NEED_BATCH_SN="";
        
        var ORDER_GOODS_ID="",SHIPMENT_ID="",BATCH_SN="",IS_NEED_TRANSFER_CODE,IS_SERIAL;

        var BINDED_GOODS = [], UNBIND_GOODS = [],MAP={},BARCODE="",SCAN_TIME=0;

        //扫描物流码的全局变量
        var TC_ORDER_GOODS_ID = '', TC_SHIPMENT_ID = '';

        //扫描订单号
        $("#order_id").focus().on("keyup",function(e){
            e.preventDefault();
            if (e.which == 13 || e.which == 17) {
                var that = $(this);
                $.ajax({
                    url : 'checkOrderForRecheck',
                    dataType : 'json',
                    type : 'post',
                    data : {
                      order_id : $.trim(that.val())  
                    },
                    beforeSend : function () {
                        that.attr('disabled',true);
                    },
                    success : function (data) {
                        that.attr('disabled',false);
                        console.log(data);
                        if (data.result == 'success' && data.type == 'BATCH') {
                            if (confirm('此订单属于批量波次单，请确认使用散单复核')) {
                                loadOrderGoods($.trim(that.val()));
                            }
                        } else if(data.result == 'success'){
                        	loadOrderGoods($.trim(that.val()));
                        	
                        }else{
                            alert(data.note);
                        }
                    },
                    error : function (error) {
                        that.attr('disabled',false);
                        console.log('checkOrderForRecheck接口调取失败');
                    }
                });
            }
        });

        //根据订单号获取绑定和未绑定的商品信息
        function loadOrderGoods(order_id) {
            var $orderId = $("#order_id"),$barcode = $("#barcode");
            $.ajax({
                type: 'post',
                dataType: 'json',
                url: '../sale/loadOrder',
                data: {
                    order_id:order_id
                },
                beforeSend : function() {
                    // $(".mw-loading").fadeIn(300);
                    $orderId.attr("disabled",true);
                    $barcode.attr("disabled",true);
                },
                success : function(data) {
                    $barcode.attr("disabled",false);
                    // $(".mw-loading").fadeOut(300);
                    console.log(data);
                    if (data.result == "success") {
                        ORDER_ID = order_id;
                        SHIPMENT_ID = data.shipment_id;
                        NEED_BATCH_SN=data.need_batch_sn;
                        $("#btn-cancel").attr("disabled",false);  
                        $(".total_number").text(data.total_number);
                        $(".unbind_number").text(data.unbind_number);
                        if (data.unbind_goods_info.length) {
                            unbindGoodsTableConstructor(data.unbind_goods_info);
                            $(".hidden-div").show();
                            $("#unbind-wrap").show();
                            $("#barcode").focus();
                        } else {
                            $("#unbind-table tbody").html("");
                            $("#unbind-wrap").hide();
                            $(".hidden-div").hide();
                        }

                        if (data.bind_goods_info.length) {
                        	packageConstructor(data.bind_goods_info);
                            //如果最新包裹里有商品而且有商品未扫描 那就可以追加新的面单
                            if ($(".one-package").eq(0).find(".shipment-goods-info").length && data.unbind_goods_info.length) {
                                $("#btn-add-shipment").attr("disabled",false);
                            }
                            $("#bind-wrap").show();
                        }

                        if (data.unbind_goods_info.length == 0 && data.bind_goods_info.length) {
                            //此情况说明最新的包裹没有绑定耗材，需要自动弹出耗材绑定窗口
                            $(".mw-consume").fadeIn(300);
                            $("#consume_id").val("").focus();
                        }
                    } else {
                        $orderId.attr("disabled",false).val("").focus();
                        alert(data.note);
                    }
                },
                error : function(error) {
                    $barcode.attr("disabled",false);
                    // $(".mw-loading").fadeOut(300);
                    $orderId.attr("disabled",false);
                    console.log("loadOrder接口调用失败");
                }
            });
        }

        $("#btn-cancel").on("click",function(){
            cancelRecheck($(this));
        });
        //监听方向键:下
        $(document).keyup(function(event){ 
            event.preventDefault();
            var f = $("#btn-cancel").attr("disabled");
            if(event.keyCode == 40){ 
                if (f != "disabled") {
                    var el = $("#btn-cancel");
                    cancelRecheck(el);
                }
            } else { 
                return false;
            } 
        });

        function cancelRecheck (el) {
            if (confirm("确定取消复核吗？")) {
                var that = el, data = {
                    order_id : ORDER_ID
                };
                $.ajax({
                    url : 'cleanOrderTns',
                    dataType : 'json',
                    type : 'post',
                    data : data,
                    beforeSend : function () {
                        that.attr('disabled',true);
                    },
                    success : function (data) {
                        that.attr('disabled',false);
                        console.log(data);
                        if (data.result == 'success') {
                            alert("取消复核成功");
                            destroyPage();
                        } else {
                            alert(data.note);
                        }
                    },
                    error : function (error) {
                        that.attr('disabled',false);
                        console.log('撤销复核接口调取失败');
                    }
                });
            }
        }

        //构造未绑定（未扫描）商品列表
        function unbindGoodsTableConstructor(data) {
            var html = "";
            for(var i=0;i<data.length;i++){
                html += '<tr class="unbind-goods-info" is_need_transfer_code="'
                     + data[i].is_need_transfer_code + '" is_serial="'
                     + data[i].is_serial + '" order_goods_id="'
                     + data[i].order_goods_id + '" order_id="'
                     + data[i].order_id + '" order_status="'
                     + data[i].order_status + '" shipping_id="'
                     + data[i].shipping_id + '" warehouse_id="'
                     + data[i].warehouse_id+ '" is_maintain_batch_sn="'
                     + data[i].is_maintain_batch_sn+'"><td class="goods_barcode">'
                     + data[i].barcode + '</td><td class="goods_name">'
                     + data[i].goods_name+'</td><td class="goods_number">'
                     + data[i].unbind_number +'</td></tr>';
            }
            $("#unbind-table tbody").html(html);
        }

        //构造包裹列表
        function packageConstructor(data) {
            var html = "";
            for (var i=0;i<data.length;i++) {
                html += '<div class="one-package" data_shipment_id="'
                     + data[i].shipment_id + '"><p>快递单号:<span class="one-package-tn">'
                     + data[i].tracking_number + '</span>商品数量:<span class="one-package-gn">'
                     + data[i].tn_goods_total + '</span></p><table class="lq-table bind-table"><thead><tr><th class="th_goods_barcode">商品条码</th><th class="th_goods_name">商品名称</th><th class="th_goods_number">数量</th></tr></thead><tbody>';
                for (var j=0;j<data[i].shipment_goods_info.length;j++) {
                    html += '<tr class="shipment-goods-info" is_serial="'
                         + data[i].shipment_goods_info[j].is_serial + '" order_goods_id="'
                         + data[i].shipment_goods_info[j].order_goods_id + '" product_id="'
                         + data[i].shipment_goods_info[j].product_id + '" serial_number="'
                         + data[i].shipment_goods_info[j].serial_number + '" shipment_id="'
                         + data[i].shipment_goods_info[j].shipment_id + '" tracking_number="'
                         + data[i].shipment_goods_info[j].tracking_number +'"><td class="goods_barcode">'
                         + data[i].shipment_goods_info[j].barcode + '</td><td class="goods_name">'
                         + data[i].shipment_goods_info[j].goods_name + '</td><td class="goods_number">'
                         + data[i].shipment_goods_info[j].goods_number + '</td></tr>';
                }
                html += '</tbody></table></div>';
            }
            $("#package-content").html(html);
        }

        //扫描商品条码（串号）
        $("#barcode").on("keyup",function(e){
            e.preventDefault();
            if (e.which == 13 || e.which == 17) {
                if ($(this).val() == 'WMS000000000') {
                    var f = $("#btn-add-shipment").attr("disabled");
                    if (f != "disabled") {
                        addShipment();
                        $(this).val("");
                    } 
                } else {
                    scanBarcode($.trim($(this).val()));
                    $(this).val("");
                }
            }
        });

        function scanBarcode(barcode) {
            var shipment_id = SHIPMENT_ID;          
            var barcodes = [];
            var serials = [];
            var map = {};
            var adSuccess = $("#ad-success")[0];
            var adError = $("#ad-error")[0];
			BARCODE=barcode;
            //获取所有的商品条码
            $(".unbind-goods-info").each(function(){
                var data_serial = $(this).attr("is_serial");//获得串号信息
                var data_barcode = $(this).find(".goods_barcode").text();//获得barcode条码信息
                var data_goods_id = $(this).attr("order_goods_id");//获得order_goods_id
                var is_maintain_batch_sn=$(this).attr("is_maintain_batch_sn");
                map[data_barcode] = [data_goods_id,data_serial,is_maintain_batch_sn];
                serials.push(data_serial);//所有未绑定的串号信息
                barcodes.push(data_barcode);//所有未绑定的商品条码
            });

            var in_barcodes = $.inArray(barcode,barcodes);//扫描的商品条码在数组中
            if (in_barcodes>=0) {//存在于数组中,说明扫描进来的数据是商品编码
            	adSuccess.play();
                var barcode_ajax = barcodes[in_barcodes];//要传递的goods_id对应的条形码
                
                var order_goods_id=map[barcode_ajax]['0'];//这个值要AJAX传递给后端
                
                var is_serial=map[barcode_ajax]['1'];
                
                var is_maintain_batch_sn=map[barcode_ajax]['2'];
                if (is_serial=='N') {//非串号商品
                    //todo 物流码
                    $(".unbind-goods-info").each(function(){
                        var that = $(this);
                        if (that.find(".goods_barcode").text() == barcode) {
                        	if(that.attr("is_maintain_batch_sn")=="Y"&&NEED_BATCH_SN=="Y"){
                        		$(".mw-batch-sn").fadeIn(300,function(){
                        			$("#batch_sn").focus();
                        		})
                        		ORDER_GOODS_ID = order_goods_id;
                        		SHIPMENT_ID = shipment_id;
                        		
                        		if(that.attr("is_need_transfer_code") == "Y")
                        			IS_NEED_TRANSFER_CODE=true;
                        		else
                        			IS_NEED_TRANSFER_CODE=false;
                        }else{
          					
                            if (that.attr("is_need_transfer_code") == "Y") {
                                $(".mw-transfer-code").fadeIn(300,function(){
                                    $("#transferCode").focus();
                                });
                                TC_ORDER_GOODS_ID = order_goods_id;
                                TC_SHIPMENT_ID = shipment_id;
                            } else {
                                callOrderGoodsApi(order_goods_id,shipment_id,'','');
                            }	
                        }
                          return false; 
                        }
                    });
                } else {
                    adError.play();
                    alert('该商品为串号商品，请扫描串号');
                }
            } else {   
                var sum=0;
                for (var i=0;i<serials.length;i++) {
                    if (serials[i]=='N') {//非串号商品
                       sum++;
                    }
                }
                if (sum == serials.length) {//说明全部是非串号商品
                    //此时需要传递serial_number,shipment_id
                    console.log('全部是非串号');
                    adError.play();
                    alert('商品条码有误，请确认后再输入！');
                } else {
                    var serial_number = barcode;
                    if(is_maintain_batch_sn=="Y"&&NEED_BATCH_SN=="Y"){
                    	IS_SERIAL=true;
                		$(".mw-batch-sn").fadeIn(300,function(){
                			$("#batch_sn").focus();
                		})                    	
                    }else{
                    	IS_SERIAL=false;
                    	callOrderGoodsApi('',shipment_id,serial_number,'');
                    }
               }
            }
        }
		//扫描批次号
		$("#batch_sn").on("keyup",function(e){
			e.preventDefault();
			if(e.which==13||e.which==17){
				if($.trim($(this).val())==""){
					alert("请维护批次号!")
					$(this).focus();
				}else{
					BATCH_SN=$(this).val();
					var batch_id_reg = new RegExp("^[A-Za-z0-9_-]+$");
					if(!batch_id_reg.test(BATCH_SN)){
						alert("请正确维护批次号（数字、字母、-、_）!");
						return;
					}
	
					
					if(IS_NEED_TRANSFER_CODE){
                        $(".mw-transfer-code").fadeIn(300,function(){
                            $("#transferCode").focus();
                        });
                        TC_ORDER_GOODS_ID = ORDER_GOODS_ID;
                        TC_SHIPMENT_ID = SHIPMENT_ID;                        
					}else{
						if(IS_SERIAL)
							callOrderGoodsApi('',SHIPMENT_ID,ORDER_GOODS_ID,BATCH_SN);
						else
						 	callOrderGoodsApi(ORDER_GOODS_ID,SHIPMENT_ID,'',BATCH_SN);
					}
			
				}
			}
		})
        //扫描物流码
        $('#transferCode').on('keyup',function(e){
            e.preventDefault();
            if (e.which == 13 || e.which == 17) {
                var that = $(this),
                transferCodeData = {
                    order_goods_id : TC_ORDER_GOODS_ID,
                    transfer_code : that.val()
                };
                $.ajax({
                    url : 'transferCodeScan',
                    dataType : 'json',
                    type : 'post',
                    data : transferCodeData,
                    beforeSend : function () {
                        that.attr('readonly',true);
                    },
                    success : function (data) {
                        that.attr('readonly',false);
                        console.log(data);
                        alert(data.note);
                        if (data.result == 'SUCCESS') {
                            $(".mw-transfer-code").fadeOut(300);
                            callOrderGoodsApi(TC_ORDER_GOODS_ID,TC_SHIPMENT_ID,'',BATCH_SN);
                        } else {
                            alert(data.note);
                            that.focus().val("");
                        }
                    },
                    error : function (error) {
                        that.attr('readonly',false);
                        console.log('扫描物流码接口调取失败');
                    }
                });
            }
        });
		//判断扫描的批次号是否已经被提示过
		function isExist(batch_sn){
			var i=0;
			if(MAP[BARCODE]){
				for(;i<MAP[BARCODE].length;i++){
					if(MAP[BARCODE][i]==batch_sn)
						return true;
				}
				return false;
			}else{
				return false;
			}
		}
        //扫描商品条码，传递shipment_id和order_goods_id,以及serial_number
        function callOrderGoodsApi(order_goods_id,shipment_id,serial_number,batch_sn) {
            var $barcode = $("#barcode");
            var adSuccess = $("#ad-success")[0];
            var adError = $("#ad-error")[0];
			
            $.ajax({
                url:'../sale/goodScan',
                dataType:'json',
                type:'post',
                data:{
                    'order_goods_id':order_goods_id,
                    'shipment_id':shipment_id,
                    'serial_number':serial_number,
                    'batch_sn':batch_sn,
                    'time':SCAN_TIME
                },
                beforeSend : function() {
                    // $(".mw-loading").fadeIn(300);
                    $barcode.attr("disabled",true);
                },
                success : function(data) {
                    console.log(data);
                    // $(".mw-loading").fadeOut(300);
                    $barcode.attr("disabled",false);
                    if(data.result=='success'){
                    	SCAN_TIME=0;
        				$(".mw-batch-sn").fadeOut(300);
                        console.log("商品扫描接口调取成功"); 
                        adSuccess.play();    
                        loadOrderGoods(ORDER_ID);
                    }else{
                        adError.play();
                        $("#batch_sn").val("").focus();
                        SCAN_TIME=1;
                        alert(data.note);
                    }
                },
                error : function() {
                    var adError = $("#ad-error")[0];

                    adError.play();
                    // $(".mw-loading").fadeOut(300);
                    $barcode.attr("disabled",false);
                    console.log("商品扫描接口调取失败");
                }
            });
        }

        // function reDrawList(orderGoodsId) {
        //     var shipmentId = SHIPMENT_ID;
        //     var existBarcodes = [];
        //     var $currentPackage = $(".one-package[data_shipment_id='"+shipmentId+"']");
        //     var currentBarcode = $("#unbind-table tbody tr[order_goods_id='"+orderGoodsId+"']").find(".goods_barcode").text();
        //     $currentPackage.find(".bind-table tbody tr").each(function(){
        //        var barcode = $(this).find(".goods_barcode").text();
        //        existBarcodes.push(barcode);
        //     });
        //     console.log("currentBarcode:"+currentBarcode);
        //     console.log(existBarcodes);
        //     $("#unbind-table tbody tr[order_goods_id='"+orderGoodsId+"']").remove();
        //     if ($.inArray(currentBarcode,existBarcodes)) {
        //         var $currentTr = $currentPackage.find(".bind-table tbody tr[order_goods_id='"+orderGoodsId+"']");
        //         var $currentNumber = parseInt($currentTr.find(".goods_number").text());
        //         $currentTr.find(".goods_number").text($currentNumber+1);
        //     } else {
        //         var html = "";
        //         $currentPackage.find(".bind-table tbody").prepend(html);
        //     }
        // }

        //绑定耗材 
        $("#consume_id").on("keyup",function(e){
            e.preventDefault();
            if (e.which == 13 || e.which == 17) {
                callBindConsumeApi();
            }
        });
		$("#btn-bind-consume").on("click",function(e){
			e.preventDefault();
			callBindConsumeApi();
		})
        
        function callBindConsumeApi() {
            var consumeId = $("#consume_id").val();
            var shipmentId = SHIPMENT_ID;
            var orderId = ORDER_ID;
            var that = $(this);
            if (consumeId == "") {
                alert("耗材条码不能为空");
                $("#consume_id").focus();
            } else {
                $.ajax({
                    type:'post',
                    dataType:'json',
                    url:'bindConsume',
                    data:{
                        'barcode':  consumeId,
                        'shipment_id' : shipmentId,
                        'order_id' : orderId
                    },
                    beforeSend : function () {
                        // $(".mw-loading").fadeIn(300);
                        that.attr("disabled",true);
                    },
                    success : function (data){
                        console.log(data);
                        // $(".mw-loading").fadeOut(300);
                        that.attr("disabled",false);
                        if(data.result=='success'){
                            //向最新的包裹里添加一个耗材
                            var $currentPackage = $(".one-package[data_shipment_id='"+shipmentId+"']");
                            var consumeHtml = "";
                            if (data.barcode != "WMS888888888") {
	                            consumeHtml += '<tr class="shipment-goods-info shipment-consume-info" is_serial="' + data.is_serial + '" order_goods_id="'
	                             + data.order_goods_id + '" product_type="'
	                             + data.product_type + '"><td class="goods_barcode">'
	                             + data.barcode + '</td><td class="goods_name">'
	                             + data.goods_name + '</td><td class="goods_number">'
	                             + data.goods_number + '</td></tr>';
                            }
                             $currentPackage.find(".bind-table tbody").prepend(consumeHtml);
                             var newNumber = parseInt($currentPackage.find(".one-package-gn").text()) + 1;
                             $currentPackage.find(".one-package-gn").text(newNumber);
                            
                            $("#btn-add-shipment").attr("disabled",true);
                            $(".mw-consume").fadeOut(300);
                            if ($("#unbind-table tbody tr").length == 0) {
                                destroyPage();
                                src="print?order_id="+ORDER_ID;
                                $('#print').attr('src',src); 
                            } else {
                                loadOrderGoods(ORDER_ID);
                                //callAddTNApi();
                            }
                        }else{
                            $("#consume_id").val("").focus();
                            alert(data.note);  // 运单信息与耗材编码未成功获取
                        }
                        
                    },
                    error : function () {
                        // $(".mw-loading").fadeOut(300);
                        that.attr("disabled",false);
                        $(".mw-consume").fadeOut(300);
                        console.log("耗材绑定接口调用失败");
                    }
                });
            }
        }

        function destroyPage () {
            console.log("destroyPage run");
            $("#order_id").attr("disabled",false).val("").focus();
            $("#btn-add-shipment").attr("disabled",true);
            $("#btn-cancel").attr("disabled",true);
            $("#bind-wrap").hide();
            $("#unbind-wrap").hide();
            $(".hidden-div").hide();
        }

        function callAddTNApi() {
            $.ajax({
                type:'post',
                dataType:'json',
                url:'addTrackingNumber',
                data:{
                    'order_id': ORDER_ID
                },
                beforeSend : function () {
                    // $(".mw-loading").fadeIn(300);
                },
                success : function (data) {
                    // $(".mw-loading").fadeOut(300);
                    console.log(data);
                    if(data.result=='success'){
                        $('#add_shipment').attr('disabled', 'disabled');
                        SHIPMENT_ID = data.shipment_id;
                        loadOrderGoods(ORDER_ID);
                        $(".mw-consume").fadeOut(300);
                    }else{
                        alert(data.note);
                    }
                },
                error : function () {
                    // $(".mw-loading").fadeOut(300);
                    alert('追加面单接口调用失败');
                }
            });
        }

        //追加面单
        $("#btn-add-shipment").on("click",function(e){
            e.preventDefault();
            addShipment();
        });
        //追加面单触发按钮 : ALT
        $(document).keyup(function(event){ 
            event.preventDefault();
            var f = $("#btn-add-shipment").attr("disabled");
            if(event.keyCode == 18){ 
                if (f != "disabled") {
                    addShipment();
                }
            } else { 
                return false;
            } 
        });
        function addShipment () {
        	$("#barcode").attr("disabled",true);
            $(".mw-consume").fadeIn(300);
            $("#consume_id").val("").focus();
        }
    });
    </script>
</body>
</html>