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
  	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
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
    .btns {
    	margin: 10px 0 5px 0;
    }
	.noDataTip {
	display: none;
	text-align: center;
	font-size: 30px;
	color: #666;
	margin: 30px 0 0 0;
	}
  	</style>
  	<script type="text/javascript">
		//全选
		function select_all(node, type)
	    {
	        node = node ? node : document ;
	        $(node).find("input[name='check_batch_pick[]']:enabled").each(function(i){
	    		this.checked = true;
	    	});
	    }
		
		//反选
		function select_reverse(node,type)
		{
			node=node?node:document;
			$(node).find("input[name='check_batch_pick[]']:enabled").each(function(){
				this.checked=!this.checked;
			})
		}
		
		//清空
		function select_none(node,type)
		{
			node=node?node:document;
			$(node).find("input[name='check_batch_pick[]']:enabled").each(function(){
				this.checked=false;
			})
		}
  	</script>
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
        		<div class="lq-col-2">
        			<div class="lq-form-group">
        				<label for="customer_id">选择货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
							<c:forEach items="${customers}" var="customer">
								<option name="customer_id" value="${customer.customer_id}">${customer.name}
								</option>
							</c:forEach>
						</select>
        			</div>
        		</div>
				<div class="lq-col-4">
                    <div class="lq-form-group">
						<button type="button" id="submit" class="lq-btn lq-btn-sm lq-btn-primary">查询</button>&nbsp;
						<button type="button" id="kick" class="lq-btn lq-btn-sm lq-btn-primary" style="display: none;">踢出</button>
					</div>
				</div>
		    </div>
	    	<div class="btns">
	      		<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="全选" onclick="select_all('#batchTable');" /> &nbsp;
	     		<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="清空" onclick="select_none('#batchTable');" /> &nbsp;
	     		<input class="lq-btn lq-btn-sm lq-btn-primary" type="button" value="反选" onclick="select_reverse('#batchTable');" /> &nbsp;
			</div>
		</div>
		<table class="lq-table" id="batchTable">
			<thead>
				<th>选择</th>
				<th>波次单号</th>
			</thead>
			<tbody>
			</tbody>
		</table>
		<div class="noDataTip">未查到符合条件的数据!</div>
		
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>	
	<script type="text/javascript">
	$(document).ready(function(){
		///查询
		$("#submit").click(function(e){
			e.preventDefault();
			var customer_id=$("#customer_id").val();
			$.ajax({
				url: '${pageContext.request.contextPath}/exception/getAllBatchPickSn',
				dataType: 'json',
				type: 'post',
				data: {
					customer_id: customer_id
				},
                beforeSend : function() {
                    $(".mw-loading").fadeIn(300);
                    $(this).attr("disabled",true);
                },
				success: function(res) {
					console.log(res);
					$(".mw-loading").fadeOut(300);
					var items=res.list;
					var table='';
					for(var i in items){
						table+='<tr>'+
								'<td><input type="checkbox" name="check_batch_pick[]" value="${order.batch_pick_sn}" class="check" />'+
								'<input type="hidden" name="check_batch_pick_id" class="check_batch_pick_id"  value="'+items[i].batch_pick_id+'" /></td>'+
								'<td>'+items[i].batch_pick_sn+'</td>'+
								'</tr>'
					}
					$("#batchTable tbody").html(table);

					if(items.length<1){
						$(".noDataTip").show();
						$("#kick").css("display","none");
						
					}else {
						$(".noDataTip").css("display","none");
						$("#kick").show();
					}
				},
				error: function(err){
					console.log(err);
				}
			})
		});
		
		$("#kick").click(function(e){
			e.preventDefault();
			var batch_pick_ids="";
			var customer_id=$("#customer_id").val();
			$("#batchTable").find("input[name='check_batch_pick[]']").each(function(){
				if(this.checked) {
					batch_pick_ids+=$(this).next().val()+",";
				}
			})
			batch_pick_ids=batch_pick_ids.substring(0,batch_pick_ids.length-1);
			console.log(batch_pick_ids);
			if(batch_pick_ids.length<1){
				alert("未选择需要踢出的波次单!");
				return ;
			}
			$.ajax({
				url: '${pageContext.request.contextPath}/exception/doBatchKickOut',
				dataType: 'json',
				type: 'post',
				data: {
					customer_id: customer_id,
					batch_pick_ids:batch_pick_ids
				},
				success: function(res){
					console.log(res);
					alert("踢出成功!");
					$("#submit").trigger("click");
				},
				error: function(err){
					alert(err);
				}
			})
		})
	})
	////////////////////////////////////////////////////////////////////////////
/* 		$(document).ready(function() {
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
                            	
                                table1 += '<tr><td>'+pickOrderGS[i].goods_name+'</td><td>'+pickOrderGS[i].barcode+'</td><td>'+pickOrderGS[i].goods_number+'</td><td>'+pickOrderGS[i].spec+'</td><td>'+pickOrderGS[i].messageString+'</td><td>'+pickOrderGS[i].avaliable4+'</td><td>'+pickOrderGS[i].avaliable5+'</td><td>'+pickOrderGS[i].avaliable6+'</td><td>'+pickOrderGS[i].avaliable7+'</td><td>'+pickOrderGS[i].avaliable8+'</td></tr>';
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

		}); */
	</script>
</body>
</html>
