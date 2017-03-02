<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
    <title>批量复核</title>
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

    #unbind-wrap, #bind-wrap {
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

    .mw-consume {
        /*display: block;*/
        z-index: 900;
        background-color: rgba(0,0,0,.5);
    }

    .mc-consume {
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

    #btn-bind-consume {
        display: block;
        margin:10px auto;
    }

    #print{
        border: 0px;
        width: 0px;
        height: 0px;
    }
    div.tracking{
    	display: none;
    	font-size: 14px;
    }
    </style>
</head>
<body>
    <audio id="ad-success" preload>
        <source src="${pageContext.request.contextPath}/static/audio/success.ogg" type="audio/ogg">
        <source src="${pageContext.request.contextPath}/static/audio/success.mp3" type="audio/mpeg">
    </audio> 
    <audio id="ad-error" preload>
        <source src="${pageContext.request.contextPath}/static/audio/error.ogg" type="audio/ogg">
        <source src="${pageContext.request.contextPath}/static/audio/error.mp3" type="audio/mpeg">
    </audio> 
    <iframe  src=""  id="print"></iframe>
    <!-- <div class="modal-wrap mw-loading">
        <div class="modal-content mc-loading">
            <i class="fa fa-spinner fa-spin" id="loading"></i>
        </div>
    </div> -->
    <div class="modal-wrap mw-consume">
        <div class="modal-content mc-consume">
            <div class="lq-form-inline">
                <div class="lq-form-group">
                    <label for="consume_id">扫描耗材条码:</label>
                    <input type="text" name="consume_id" id="consume_id" class="lq-form-control">
                </div>
                <div class="lq-form-group">
                    <label for="consume_num">耗材数量:</label>
                    <input type="text" name="consume_num" id="consume_num" class="lq-form-control" readonly="readonly">
                </div>
                <button id="btn-bind-consume" class="lq-btn lq-btn lq-btn-primary">    
                    确定
                </button>
            </div>
        </div>
    </div>
    <div class="main-container">
        <div class="lq-form-inline">
            <div class="lq-row">
                <div class="lq-col-5">
                    <div class="lq-form-group">
                        <label for="batch_pick_sn">扫描波次号:</label>
                        <input type="text" name="batch_pick_sn" id="batch_pick_sn" class="lq-form-control">
                        <button id="btn-add-shipment" class="lq-btn lq-btn-sm lq-btn-primary" disabled=true>    
                            追加面单(快捷键:ALT)
                        </button>
                        <button id="btn-cancel" class="lq-btn lq-btn-sm lq-btn-primary" disabled=true>   
                            取消复核(快捷键:↓)
                        </button>
                    </div>
                </div>
            </div>
        </div>        
        <div class="tracking">
        	<p>快递总数:<span class="total_tracking" style="margin-right:15px;"></span>快递方式:<span class="tracking_type"></span></p>
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
        var BATCH_PICK_ID = "", BATCH_PICK_SN = "", BATCH_ORDER_NUM = "", MAX_SHIPMENT_COUNT = "";

        var BINDED_GOODS = [], UNBIND_GOODS = [];

        //扫描订单号
        $("#batch_pick_sn").focus().on("keyup",function(e){
            e.preventDefault();
            if (e.which == 13 || e.which == 17) {
                loadBatchPickSn($.trim($(this).val()));
            }
        });

        //根据订单号获取绑定和未绑定的商品信息
        function loadBatchPickSn(batch_pick_sn) {
            var $batchPickSn = $("#batch_pick_sn"),$barcode = $("#barcode");
            $.ajax({
                type: 'post',
                dataType: 'json',
                url: 'loadBatchPickSn',
                data: {
                    batch_pick_sn:batch_pick_sn
                },
                beforeSend : function() {
                    // $(".mw-loading").fadeIn(300);
                    $batchPickSn.attr("disabled",true);
                    $barcode.attr("disabled",true);
                },
                success : function(data) {
                    $barcode.attr("disabled",false);
                    // $(".mw-loading").fadeOut(300);
                    console.log(data);
                    if (data.result == "success") {
                        BATCH_PICK_ID = data.batch_pick_id;
                        BATCH_PICK_SN = data.batch_pick_sn;
                        BATCH_ORDER_NUM = data.batch_order_num;
                        MAX_SHIPMENT_COUNT = data.max_shipment_count;
                        
                    	//如果成功返回数据则显示快递方式和快递数量
                    	var total=data.shipNumber;
                    	var ship="";
                    	for(var i in data.shipList){
                    		ship+=data.shipList[i]+",";
                    	}
                    	$("span.total_tracking").text(total);
                    	$("span.tracking_type").text(ship);
                    	$(".tracking").fadeIn(300);
                        
                        
                        
                        $("#consume_num").val(BATCH_ORDER_NUM);
                        $("#btn-cancel").attr("disabled",false);
                        if (data.unbind_goods_info.length) {
                            unbindGoodsTableConstructor(data.unbind_goods_info);
                            $("#unbind-wrap").show();
                            $("#barcode").focus();
                        } else {
                            $("#unbind-table tbody").html("");
                            $("#unbind-wrap").hide();
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
                        $batchPickSn.attr("disabled",false).val("").focus();
                        alert(data.note);
                    }
                },
                error : function(error) {
                    $barcode.attr("disabled",false);
                    // $(".mw-loading").fadeOut(300);
                    $batchPickSn.attr("disabled",false);
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
                var that = $(this), data = {
                    batch_pick_sn : BATCH_PICK_SN
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
                html += '<tr class="unbind-goods-info" is_serial="'
                     + data[i].is_serial + '" order_goods_id="'
                     + data[i].order_goods_id + '" order_id="'
                     + data[i].order_id + '" order_status="'
                     + data[i].order_status + '" shipping_id="'
                     + data[i].shipping_id + '" warehouse_id="'
                     + data[i].warehouse_id+ '"><td class="goods_barcode">'
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
                html += '<div class="one-package"><table class="lq-table bind-table"><thead><tr><th class="th_goods_barcode">商品条码</th><th class="th_goods_name">商品名称</th><th class="th_goods_number">数量</th></tr></thead><tbody>';
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
            var batch_pick_id = BATCH_PICK_ID;
            var max_shipment_count = MAX_SHIPMENT_COUNT;           
            var barcodes = [];
            var serials = [];
            var map = {};
            var adSuccess = $("#ad-success")[0];
            var adError = $("#ad-error")[0];

            //获取所有的商品条码
            $(".unbind-goods-info").each(function(){
                var data_serial = $(this).attr("is_serial");//获得串号信息
                var data_barcode = $(this).find(".goods_barcode").text();//获得barcode条码信息
                var data_goods_id = $(this).attr("order_goods_id");//获得order_goods_id
                map[data_barcode] = [data_goods_id,data_serial];
                serials.push(data_serial);//所有未绑定的串号信息
                barcodes.push(data_barcode);//所有未绑定的商品条码
            });

            var in_barcodes = $.inArray(barcode,barcodes);//扫描的商品条码在数组中
            if (in_barcodes>=0) {//存在于数组中,说明扫描进来的数据是商品编码
                var barcode_ajax = barcodes[in_barcodes];//要传递的goods_id对应的条形码
                
                var order_goods_id=map[barcode_ajax]['0'];//这个值要AJAX传递给后端
                
                var is_serial=map[barcode_ajax]['1'];
                if (is_serial=='N') {//非串号商品
                    callOrderGoodsApi(order_goods_id,batch_pick_id,max_shipment_count);
                } else {
                    adError.play();
                    alert('该商品为串号商品，请扫描串号');
                }
            } else {  
                adError.play(); 
                alert('商品条码有误，请确认后再输入！');
            }
        }

        //扫描商品条码，传递shipment_id和order_goods_id,以及serial_number
        function callOrderGoodsApi(order_goods_id,batch_pick_id,max_shipment_count) {
            var $barcode = $("#barcode");
            var adSuccess = $("#ad-success")[0];
            var adError = $("#ad-error")[0];
            $.ajax({
                url:'batchGoodScan',
                dataType:'json',
                type:'post',
                data:{
                    'order_goods_id':order_goods_id,
                    'batch_pick_id':batch_pick_id,
                    'max_shipment_count':max_shipment_count
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
                        console.log("商品扫描接口调取成功");
                        adSuccess.play(); 
                        loadBatchPickSn(BATCH_PICK_SN);
                    }else{
                        adError.play();
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
        $("#btn-bind-consume").on("click",function(e){
            e.preventDefault();
            var consumeId = $("#consume_id").val();
            var batch_pick_id = BATCH_PICK_ID;
            var max_shipment_count = MAX_SHIPMENT_COUNT;
            var that = $(this);
            if (consumeId == "") {
                alert("耗材条码不能为空");
                $("#consume_id").focus();
            } else {
                $.ajax({
                    type:'post',
                    dataType:'json',
                    url:'batchBindConsume',
                    data:{
                        'barcode':  consumeId,
                        'batch_pick_id' : batch_pick_id,
                        'max_shipment_count' : max_shipment_count
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
                            var $currentPackage = $(".one-package").eq(0);
                            var consumeHtml = "";
                            console.log(data.barcode);
                            if( data.barcode!="WMS888888888"){
	                            consumeHtml += '<tr class="shipment-goods-info shipment-consume-info"><td class="goods_barcode">'
	                             + data.barcode + '</td><td class="goods_name">'
	                             + data.goods_name + '</td><td class="goods_number">'
	                             + data.goods_number + '</td></tr>';
                            }
                            $currentPackage.find(".bind-table tbody").prepend(consumeHtml);
                            $("#btn-add-shipment").attr("disabled",true);
                            $(".mw-consume").fadeOut(300);
                            if ($("#unbind-table tbody tr").length == 0) {
                                destroyPage();
                                src="batchPrint?batch_pick_id="+BATCH_PICK_ID;
                                $('#print').attr('src',src); 
                            } else {
                                loadBatchPickSn(BATCH_PICK_SN);
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
        });

        function destroyPage () {
            console.log("destroyPage run");
            $("#batch_pick_sn").attr("disabled",false).val("").focus();
            $("#btn-add-shipment").attr("disabled",true);
            $("#btn-cancel").attr("disabled",true);
            $("#bind-wrap").hide();
            $("#unbind-wrap").hide();
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
