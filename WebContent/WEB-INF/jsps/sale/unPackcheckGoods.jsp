<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
    <title>拆解单复核</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style>
    #printGrounding{
        border: 0px;
        width: 0px;
        height: 0px;
    }
    .lq-table-title {
        font-size: 14px;
        font-weight: bold;
        color: #337ab7;
    }

    .th_goods_barcode {
        width: 20%;
    }

    .th_goods_name {
        width: 70%;
    }

    .th_goods_number {
        width: 10%;
    }
    .common-info {
    	margin-right: 15px;
    	font-weight: bold;
    }
    #prepack-num-wrap {
    	margin:15px 0;
    }
    .all-info-wrap {
        display: none;
    }
    </style>
</head>
<body> 
    <iframe  src=""  id="printGrounding"></iframe>
    <div class="main-container">
        <div class="lq-form-inline">
            <div class="lq-row">
                <div class="lq-col-5">
                    <div class="lq-form-group">
                        <label for="order_sn">扫描任务单号:</label>
                        <input type="text" name="order_sn" id="order_sn" class="lq-form-control">
                    </div>
                </div>
            </div>
        </div>
        <div class="all-info-wrap">
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
	                <div class="lq-col-12" id="common-info-wrap">
	                    <span class="common-info">套餐条码:<span id="comboBarcode"></span></span>
	                    <span class="common-info">套餐名称:<span id="comboName"></span></span>
	                    <span class="common-info">需拆分数量:<span id="prePackageNumber"></span></span>
	                    <span class="common-info">组合方式:<span id="unionType"></span></span>
	                </div>
	            </div>
	            <div class="lq-row">
	                <div class="lq-col-6">
	                    <p class="lq-table-title">待复核商品信息</p>
	                    <table class="lq-table" id="unbind-table">
	                        <thead>
	                            <tr>
	                                <th class="th_goods_barcode">套餐条码</th>
	                                <th class="th_goods_name">套餐名称</th>
	                                <th class="th_goods_number">数量</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        </tbody>
	                    </table>
	                </div>
	            </div>
	        </div>
	        <div id="bind-wrap">
	            <div class="lq-row">
	                <div class="lq-col-6">
	                    <p class="lq-table-title">已复核商品信息</p>
	                    <table class="lq-table" id="bind-table">
	                        <thead>
	                            <tr>
	                                <th class="th_goods_barcode">套餐条码</th>
	                                <th class="th_goods_name">套餐名称</th>
	                                <th class="th_goods_number">数量</th>
	                            </tr>
	                        </thead>
	                        <tbody>
	                        </tbody>
	                    </table>
	                </div>
	            </div>
	        </div>
	        <div class="lq-form-inline" id="prepack-num-wrap">
	            <div class="lq-row">
	                <div class="lq-col-3">
	                    <div class="lq-form-group">
	                        <label for="inputPrePackageNumber">输入拆分数量:</label>
	                        <input type="text" name="inputPrePackageNumber" id="inputPrePackageNumber" class="lq-form-control">
	                    </div>
	                </div>
	            </div>
	        </div>
	        <div class="lq-form-inline">
	            <div class="lq-row">
	                <div class="lq-col-6">
	                    <button class="lq-btn lq-btn-primary" id="btn-print" disabled=true>
	                    	打印上架标签
	                    </button>
	                </div>
	            </div>
	        </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script>
    $(document).ready(function(){
        //一些全局变量
        var PACKBOX_PRODUCT_ID = '', COMBOBARCODE = '', ORDER_ID = '';
    	//扫描任务单号
        $("#order_sn").focus().on("keyup",function(e){
            e.preventDefault();
            if (e.which == 13 || e.which == 17) {
            	var that = $(this),
            	data = {
            		order_sn : $.trim(that.val()),
                    type : 1
            	};
                $.ajax({
	                type: 'post',
	                dataType: 'json',
	                url: 'checkLoadOrder',
	                data: data,
	                beforeSend : function() {
	                    that.attr("disabled",true);
	                },
	                success : function(data) {
	                    console.log(data);
                        if (data.result == 'success') {
                            $(".all-info-wrap").fadeIn(300);
                            $("#barcode").focus().val("");
                            COMBOBARCODE = data.comboBarcode;
                            ORDER_ID = data.order_id;
                            $("#comboBarcode").text(data.comboBarcode);
                            $("#comboName").text(data.comboName);
                            $("#prePackageNumber").text(data.prePackageNumber);
                            $("#unionType").text(data.unionType);
                            //构造待复核商品列表
                            var waitcheckListHtml = '';
                            for (var i in data.waitcheckList) {
                                waitcheckListHtml += '<tr><td class="td_goods_barcode">'
                                + data.waitcheckList[i].barcode + '</td><td class="td_goods_name">'
                                + data.waitcheckList[i].product_name + '</td><td class="td_goods_number">'
                                + data.waitcheckList[i].number + '</td></tr>';
                            }
                            $("#unbind-table tbody").html(waitcheckListHtml);
                            //构造已复核商品列表
                            var finishcheckListHtml = '';
                            for (var i in data.finishcheckList) {
                                finishcheckListHtml += '<tr><td class="td_goods_barcode">'
                                + data.finishcheckList[i].barcode + '</td><td class="td_goods_name">'
                                + data.finishcheckList[i].product_name+ '</td><td class="td_goods_number">'
                                + '0' + '</td></tr>';
                            }
                            $("#bind-table tbody").html(finishcheckListHtml);
                        } else {
                            that.attr("disabled",false).focus().val("");
                            alert(data.note);
                        }
	                },
	                error : function(error) {
	                    that.attr("disabled",false);
	                    console.log("checkLoadOrder接口调用失败");
	                }
	            });
            }
        });

        //扫描商品 （不与后台交互）
    	$("#barcode").on("keyup",function(e){
            e.preventDefault();
            if (e.which == 13 || e.which == 17) {
                if ($("#unbind-table tbody tr").length) {
                    scanBarcode($.trim($(this).val()));
                } else {
                    alert('已经没有待扫描的商品');
                }
                $(this).val("");
            }
        });

        function scanBarcode (barcode) {
        	//计算未扫描的商品总数量
        	var totalNumberUnbind = 0, ifInUnbindTable = false;
        	$("#unbind-table .td_goods_number").each(function(){
        		totalNumberUnbind += parseInt($(this).text());
        	});

        	//递减未扫描商品列表
        	$("#unbind-table .td_goods_barcode").each(function(){
        		var that = $(this);
        		if (that.text() == barcode) {
                    ifInUnbindTable = true;
        			if (parseInt(that.siblings(".td_goods_number").text()) - 1 != 0) {
        				that.siblings(".td_goods_number").text(parseInt(that.siblings(".td_goods_number").text()) - 1);
        				totalNumberUnbind = totalNumberUnbind - 1;
        			} else {
        				that.parents("tr").remove();
        				totalNumberUnbind = totalNumberUnbind - 1;
        				if (totalNumberUnbind == 0) {
    						$("#unbind-table").fadeOut(300);
                            $("#inputPrePackageNumber").focus().val("");
    						$("#btn-print").prop("disabled",false);
        				}
        			}
        			//递增未扫描商品列表
        			$("#bind-table .td_goods_barcode").each(function(){
		        		var that = $(this);
		        		if (that.text() == barcode) {
		        			that.siblings(".td_goods_number").text(parseInt(that.siblings(".td_goods_number").text()) + 1);
                            return false;
		        		}
		        	});
                    return false;
        		}
        	});
            
            if (!ifInUnbindTable) {
                alert('该商品不在待扫描列表中');
            }
        }

        //是否为正整数 
        function isPositiveNum (s) { 
            var re = /^[0-9]*[1-9][0-9]*$/;  
            return re.test(s); 
        } 

        //打印上架标签
        $("#btn-print").on("click",function(e){
        	e.preventDefault();
            if (isPositiveNum($("#inputPrePackageNumber").val())) {
                var prePackageNumber = parseInt($("#prePackageNumber").text()),
                    inputPrePackageNumber = parseInt($("#inputPrePackageNumber").val());
                if (inputPrePackageNumber > prePackageNumber) {
                    alert('数量超过拆分需求数量，请重新输入！');
                    
                } else if (inputPrePackageNumber < prePackageNumber) {
                    if (confirm('数量小于需拆分数量，是否确认？')) {
                        var src="Ungrounding_print?order_id=" + ORDER_ID + "&packbox_product_id=" + PACKBOX_PRODUCT_ID + "&preNumber=" + $("#inputPrePackageNumber").val() + "&prePackageNumber=" + $("#prePackageNumber").text();
                        console.log(src);
                        $('#printGrounding').attr('src',src);
                        $(this).attr("disabled","disabled");
                    }
                } else if (inputPrePackageNumber == prePackageNumber) {
                    var src="Ungrounding_print?order_id=" + ORDER_ID + "&packbox_product_id=" + PACKBOX_PRODUCT_ID + "&preNumber=" + $("#inputPrePackageNumber").val() + "&prePackageNumber=" + $("#prePackageNumber").text();
                    console.log(src);
                    $('#printGrounding').attr('src',src);
                    $(this).attr("disabled","disabled");
                }
            } else {
                alert('请输入正整数');
            }
        });
     
    });
    </script>
</body>
</html>
