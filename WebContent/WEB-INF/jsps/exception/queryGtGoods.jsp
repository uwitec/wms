<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
	<title>查询订单商品</title>
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

    #order-info-content, #tracking-info-content ,#order-info-content2{
    	display: none;
    	margin:15px 0;
    }
    
	#kickOut {
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
        					<th>商品名称</th>
        					<th>商品条码</th>
        					<th>商品数目</th>
        					<th>箱规</th>
        					<th>详情</th>
        					<th>件拣货区数量</th>
        					<th>箱拣货区数量</th>
        					<th>存储区数量</th>
        					<th>退货区数量</th>
        					<th>退货良品暂存区数量</th>
        					<th>耗材区数量</th>
        					<th>二手区数量</th>
        				</tr>
        			</thead>
                    <tbody class="table1">
                        
                    </tbody>
        		</table>
        	</div>
    	</div>
    	<div class="lq-row" id="tracking-info-content">
    		<div class="lq-col-6">
        		<table class="lq-table">
        			<thead>
        				<tr>
        					<th>商品名称</th>
        					<th>商品条码</th>
        					<th>库位</th>
        					<th>库区</th>
        					<th>可用数量</th>
        					<th>生产日期</th>
        				</tr>
        			</thead>
                    <tbody class="table2">
                        
                    </tbody>
        		</table>
        	</div>
    	</div>
    	
    	<div class="lq-row" id="order-info-content2">
    		<div class="lq-col-6">
        		<table class="lq-table">
        			<thead>
        				<tr>
        					<th>商品名称</th>
        					<th>商品条码</th>
        					<th>订单号</th>
        					<th>商品数量</th>
        					<th>需上架数量</th>
        				</tr>
        			</thead>
                    <tbody class="table3">
                        
                    </tbody>
        		</table>
        	</div>
    	</div>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>	
	<script type="text/javascript">
		$(document).ready(function() {
            var batch_pick_id=0;
            var pstr='';
            $("#no").on("keyup",function(e){
                e.preventDefault();
                if (e.which == 13 || e.which == 17) {
                    calldoQueryOrdersApi($(this));
                }
            });

            function calldoQueryOrdersApi (element) {
                var data = {
                	oms_order_sn : $("#no").val()
                };
                var that = element;
                $.ajax({
                    url : "${pageContext.request.contextPath}/exception/doQueryGtGoods",
                    type : "post",
                    dataType : "json",
                    contentType : "application/x-www-form-urlencoded; charset=utf-8",
                    data : data,
                    beforeSend : function() {
                        $(".mw-loading").fadeIn(300);
                        that.attr("disabled",true);
                    },
                    success : function(data) {
                        console.log(data);
                        $(".mw-loading").fadeOut(300);
                        that.attr("disabled",false);
                        if (data.success) {
                            alert(data.message);
                            // table1
                            var table1 = '';
                            var pickOrderGS = data.pickOrderGoodsSum;
                            for (var i in pickOrderGS) {
                            	
                                table1 += '<tr><td>'+pickOrderGS[i].goods_name+'</td><td>'+pickOrderGS[i].barcode+'</td><td>'+pickOrderGS[i].goods_number+'</td><td>'+pickOrderGS[i].spec+'</td><td>'+pickOrderGS[i].messageString+'</td><td>'+pickOrderGS[i].avaliable4+'</td><td>'+pickOrderGS[i].avaliable5+'</td><td>'+pickOrderGS[i].avaliable6+'</td><td>'+pickOrderGS[i].avaliable7+'</td><td>'+pickOrderGS[i].avaliable8+'</td><td>'+pickOrderGS[i].avaliable9+'</td><td>'+pickOrderGS[i].avaliable10+'</td></tr>';
                            }
                            $('.table1').html(table1);
                            $('#order-info-content').show();
                            // table2
                            var productLL = data.productLocationList;
                            var table2 = '';
                            for (var j in productLL) {
                                table2 += '<tr><td>'+productLL[j].product_name+'</td><td>'+productLL[j].barcode+'</td><td>'+productLL[j].location_barcode+'</td><td>'+productLL[j].location_type+'</td><td>'+productLL[j].qty_available+'</td><td>'+productLL[j].validity+'</td></tr>';
                            }
                            $('.table2').html(table2);
                            $('#tracking-info-content').show();
                            
                            var noPLorderList = data.noPlOrderList;
                            var table3 = '';
                            for (var j in noPLorderList) {
                                table3 += '<tr><td>'+noPLorderList[j].product_name+'</td><td>'+noPLorderList[j].barcode+'</td><td>'+noPLorderList[j].order_id+'</td><td>'+noPLorderList[j].goods_number+'</td><td>'+noPLorderList[j].diff+'</td></tr>';
                            }
                            $('.table3').html(table3);
                            $('#order-info-content2').show();
                        } else {
                            alert(data.message);
                        }
                    },
                    error : function() {
                        $(".mw-loading").fadeOut(300);
                        that.attr("disabled",false);
                        console.log("查询失败");
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
