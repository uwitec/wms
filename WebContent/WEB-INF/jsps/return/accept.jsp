<%@page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<title>退货入库</title>
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link type="text/css" rel="stylesheet" href="../static/css/v3.css">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">  	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<link rel="stylesheet" type="text/css" href="../static/css/accept.css">
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript" src="../static/js/v3.js"></script>	
	<style type="text/css">
		.service_box h2 a {
			text-decoration: none;
		}
		#to-page-wrap,.pageShow {
			float: right;
			margin:0 !important;
		}
		#to-page-wrap {
			position: relative;
			top: 10px;
		}
		#currentPage {
			width: 50px;
			text-align: center;
		}
		
		.mulInput {
			height: 30px;
			margin-bottom: 3px;
		}
		.dateProduce,.batch{
			max-width: 120px;
		}
		input[type=text].lq-form-control {
			height: 30px;
		}
		.nowNum{
			margin-top: 8px;
			width: 2rem;
		}
		input[type="radio"]{
			position: relative;
			top: -3px;
		}
		select.headerSelect{
			width: 130px;
			position: relative;
			top: 5px;
		}
	</style>
</head>
<body>
	<div class="headWrap">
		<div id="header">
			<div class="tab" style="display: inline-block; margin: 0 0 0 4px;">
				<a href="../return/accept?order_status=ACCEPT" class="tabBtn waitingPro">待处理(<span> ${countUnfulfilledReturnOrderInfo} </span>)</a>
				<a href="../return/accept?order_status=FULFILLED" class="tabBtn alreadyPutIn visited">已入库</a>
			</div>
			<form class="search" id="form1" method="get" onsubmit="return check();">
				<input type="hidden" name="act" value="search" /> 
				<select name="search_type" class="headerSelect">
					<option label="WMS订单号" value="order_id" <c:if test="${conditionMap.search_type == 'order_id'}">selected="selected"</c:if>>WMS订单号</option>
					<option label="OMS订单号" value="oms_order_sn" <c:if test="${conditionMap.search_type == 'oms_order_sn'}">selected="selected"</c:if>>OMS订单号</option>
					<option label="收货人" value="receive_name" <c:if test="${conditionMap.search_type == 'receive_name'}">selected="selected"</c:if>>收货人</option>
					<option label="收货人电话" value="phone_number" <c:if test="${conditionMap.search_type == 'phone_number'}">selected="selected"</c:if>>收货人电话</option>
					<option label="寄回快递运单号" value="tms_shipping_no" <c:if test="${conditionMap.search_type == 'tms_shipping_no'}">selected="selected"</c:if>>寄回快递运单号</option>
				</select> 
				<input class="headerIpt" type="text" name="search_text" id="search_text"
					placeholder="请输入搜索内容，例如订单号、快递单号..." <c:if test="${conditionMap.search_text != null}">value="${conditionMap.search_text}"</c:if>/> 
				<!-- <input type="submit" value="" title="搜索" alt="搜索" class="lq-btn lq-btn-sm lq-btn-primary" /> -->
				<button type="submit" id="btn-sale-search" class="lq-btn lq-btn-sm lq-btn-primary headerBtn">
					<i class="fa fa-search"></i>查询
				</button>
			</form>
		</div>
		<div class="clear"></div>
	</div>
	<div class="wrap">
		<c:forEach items="${orderInfoList}" varStatus="i" var="orderInfo">

		<div class="service_box lq-form-inline">
			<h2>
				<span>订单号：<a>${orderInfo.order_id} </a></span>&nbsp;&nbsp;货主：${orderInfo.customer_name} &nbsp;&nbsp;售后类型：退货申请&nbsp;&nbsp;&nbsp;退货申请时间：<fmt:formatDate value="${orderInfo.order_time}" pattern="yyyy/MM/dd  HH:mm:ss" />&nbsp;&nbsp;&nbsp;仓库接单时间：<fmt:formatDate value="${orderInfo.created_time}" pattern="yyyy/MM/dd  HH:mm:ss" /> &nbsp;&nbsp;OMS订单号：${orderInfo.oms_order_sn}
			</h2>
			<table cellpadding="0" cellspacing="0" class="user lq-table">
				<tr>
					<th>用户名</th>
					<th>收货人</th>
				</tr>
				<tr>
					<td>${orderInfo.buyer_name}</td>
					<td>${orderInfo.receive_name}</td>
				</tr>
			</table>
			<div style="margin: 7px">
				<span>受理（物理）仓库：</span> <b>${orderInfo.physical_warehouse_name}</b>
				<span>受理（逻辑）仓库：</span> <b>${orderInfo.warehouse_name}</b>
			</div>

			<div style="clear: both; margin: 15px auto;"></div>
			<span style="display: none">${orderInfo.order_id}</span> 
			<label for="forScan">扫描商品条码:</label> 
			<input type="text" id="forScan" class="scanBox lq-form-control"> 
			<span class="tips">该商品为串号控制商品呀，请扫描串号~</span>
			<form action="../return/inStock" method="post" class="form_submit">
				<table cellpadding="0" cellspacing="0" class="goodsBychen1 lq-table">
					<tr>
						<th>商品名称</th>
						<th>商品条码</th>
						<th>可退数量</th>
						<th>本次退回数量</th>
						<th>商品条码/串号</th>
						<th>生产日期</th>
						<th>批次号</th>
						<th>新旧情况</th>
					</tr>
					<c:if test="${orderInfo.order_status != 'FULFILLED' }">
					<c:forEach items="${orderInfo.orderGoodsList}" varStatus="j" var="orderGoods">
						<tr class="goodsList">
							<td style='text-align: center; padding-left: 10px; color: blue;'>${orderGoods.goods_name}</td>
							<td class="barcode1">${orderGoods.barcode}</td>
							<td class="amount">${orderGoods.goods_number}</td>
							<td><input type="text" class="nowNum" value="0"
								{if $goods.is_serial}disabled="true" {/if} readonly="true" />
								<input type="hidden" id="oldValue">
							</td>
							<td class="barcodeOrSerial"><p class="barcode"></p></td>
							<td class="validity"></td><!-- 标 -->
							<td class="batchSn" ></td>
							<td class="newOld" style="min-width:125px;"></td>
						</tr>
					</c:forEach>
						<tr>
							<td colspan="8" style="text-align: right; padding-right: 10px;">
								<div class='check_goods_result'	style='border-top: 1px solid #ccc;'>
									<input type="submit" value="验货入库" class="examine_ok button_1 lq-btn lq-btn-sm lq-btn-primary">
								</div> <input type="hidden" name="act" value="update" /> <input
								type="hidden" name="back_url" value="{$back_url}" /> <input
								type="hidden" name="inner_check" value="pass" /> <input
								type="hidden" name="orderId" value="${orderInfo.order_id}" />
							</td>
						</tr>
						</c:if>
						<c:if test="${orderInfo.order_status == 'FULFILLED' }">
							<c:forEach items="${orderInfoFulfilledList}" varStatus="k" var="Goods">
								<tr>
									<td style='text-align: center; padding-left: 10px; color: blue;'>${Goods.goods_name}</td>
									<td class="barcode1">${Goods.barcode}</td>
									<td class="amount">${Goods.goods_number}</td>
									<td>
										<input type="text" class="nowNum" style="padding:3px;" value="${Goods.goods_number}"{if $goods.is_serial}disabled="true" {/if} readonly="true" />
										<input type="hidden" id="oldValue">
									</td>
									<td class="barcodeOrSerial"><p class="barcode">${Goods.barcode}</p></td>
									<td class="validity fulFilledValidity" data="${Goods.validity}" td-data="${Goods.stringValidity}" style="min-width:130px;">${Goods.stringValidity}</td>
									<td class="batchSn fullFilledBatchSn" data="${Goods.batch_sn}" style="min-width:120px;">${Goods.stringBatchSn}</td>
									<td class="newOld fullFilled" data="${Goods.status}" style="min-width:90px;">${Goods.stringStatus}</td>																
								</tr>
							</c:forEach>
								<tr>
									<td colspan="8" style="text-align: right; padding-right: 10px;">
										<strong style="color: red; font-size: 14px; margin-right: 10px;">已全部入库</strong>
									</td>
								</tr>
						</c:if>
					</table>
				
			</form>

		</div>
		</c:forEach>

		<c:if test="${page.totalPage != 0 }">
		<!-- Pagination -->
		<div class="lq-row" style="width:960px;margin:0 auto;">
			<div class="lq-form-inline" id="to-page-wrap">
				<span>跳转到第</span>
				<input type="text" id="currentPage" value="1" class="lq-form-control">
				<span>页</span>
				<button class="lq-btn lq-btn-sm lq-btn-primary" id="btn-to-page">GO</button>
				<span>总共<span>${page.totalPage}</span>页</span>
			</div>
			<div class="pageShow">
				<nav id="page-search">
					<ul class="lq-page-control">
						<c:if test='${page.totalPage <=5}'> 
						<c:forEach var="i" begin="1" end="${page.totalPage}" step="1">
							<li <c:if test="${page.currentPage == i}"> class="active"</c:if>>
								<a href="../return/accept?currentPage=${i}&pageSize=5<c:forEach var='item' items='${conditionMap}'>&${item.key}=${item.value}</c:forEach>">${i}</a>
							</li>
						</c:forEach>
						</c:if>
						<c:if test='${page.totalPage >5}'>
						<c:forEach var="i" begin="1" end="5" step="1">
							<li <c:if test="${page.currentPage == i}"> class="active"</c:if>>
								<a href="../return/accept?currentPage=${i}&pageSize=5<c:forEach var='item' items='${conditionMap}'>&${item.key}=${item.value}</c:forEach>">${i}</a>
							</li>
						</c:forEach>
						</c:if>
					</ul>
				</nav>
			</div>
			
		</div>
		</c:if>

		<div id="popDiv" style="display: none;">
			<div class="popw">
				<span class="closeMask">&times;</span>
				<h2>添加备注：</h2>
				<div class="popContent">
					<p>
						<textarea name="track_result"></textarea>
					</p>
					<input type="hidden" name="act" value="remark" /> <input
						type="hidden" name="service_id" value="0" />
					<p>
						<input type="button" value="提交" class="sub button_1" /><input
							type="button" value="取消" class='closeMask button_2' />
					</p>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		$(document).ready(function(){
			$(".fulFilledValidity").each(function(){
				var validity=$(this).attr("data");
				var val=$(this).text();
				var amount=$(this).prev().prev().find("input").val();
				if(val==""&&validity!=""){
					$(this).text(validity.substring(0,10)+" * "+amount);
				}
				if(val!=""){
					var arr=val.split(" ");
					var str="";
					var i=0;
					for(;i<arr.length;i=i+2){
						if(arr[i]){
							var num=arr[i+1].split("*")[1];
							var dt=arr[i];
							str+=dt+" * "+num+"\n";
						}
					}
				//	console.log(arr);
					$(this).text(str);
				}
			})
			
			$(".fullFilledBatchSn").each(function(){
				var batch_sn=$(this).attr("data");
				var val=$(this).text();
				var amount=$(this).prev().prev().prev().find("input").val();
				if(val==""&&batch_sn!=""){
					$(this).text(batch_sn+" * "+amount);
				}
				if(val!=""){
					var str=$(this).split(" ").join("\n");
					$(this).text(str);
				}
			})
			
			$(".fullFilled").each(function(){
				var status=$(this).attr("data");
				var amount=$(this).prev().prev().prev().prev().find("input").val();
				status=(status=="NORMAL")?"全新":"二手";
				$(this).text(status+" * "+amount);
			})
		})
	</script>

	<script type="text/javascript">	

		$("#btn-to-page").on("click",function(e){
			e.preventDefault();
			var pageData = '${conditionMap}',url = '';
			pageData = pageData.replace(/\}/g, "");
			pageData = pageData.replace(/\{/g, "");
			pageData = pageData.replace(/\s/g, "");
			pageData = pageData.replace(/,/g, "&");
			console.log(pageData);
			url = '../return/accept?currentPage='+$("#currentPage").val()+'&pageSize=5&'+pageData;
			window.location.href = url;
		});
		
		function getUrl () {
			var arrUrl = window.location.search.split('=');
			lastUrlArr = arrUrl[arrUrl.length - 1];
			if (lastUrlArr == 'FULFILLED') {
				$('.tabBtn'). removeClass('visited');
				$('.alreadyPutIn').addClass('visited');
			} else {
				$('.tabBtn'). removeClass('visited');
				$('.waitingPro').addClass('visited');
			}
		}
		getUrl();
		function isPageSearch () {
			if ($('.service_box').length == 0) {
				$('#page-search').hide();
			}
		}
		isPageSearch();
        function check(){
        	var search_text = this.document.getElementById("search_text").value;
        	if(search_text.trim() == ""){
        		alert("请输入需要查找的内容！");
        		return false;
        	}
        	return true;
        }
        
        String.prototype.trim = function()  
        {  
            return this.replace(/(^\s*)|(\s*$)/g, "");  
        }

        $(document).ready(function(){
        	var msg = "${msg}";  // 不加双引号会报错
        	// 弹框信息提示
        	if (msg != null && msg != ""){
        		alert(msg);
        	}
        	
            $(".service_box").eq(0).css("margin-top","70px");
            var $scanBox = $(".scanBox");
            var $nowNum = $(".nowNum");
            var radioSeq = 0;   // 作为radio单选标签控件的编号使用
            var existSN = new Array();
            var $examine_ok = $(".examine_ok");
            var $examine_refuse = $(".examine_refuse");
            $("#rightSideWrap ul li").click(function(){
                if(! $(this).hasClass("shown")){
                    $(this).addClass("shown").siblings().removeClass("shown");
                }
                var liIndex = $("#rightSideWrap ul li").index(this);
                $(".n").eq(liIndex).fadeIn(400).siblings().hide();
            });
            $("#hideLi").click(function(){
                $(".n").slideUp();
            });
            
            
            $("#searchBarcode").keyup(function(e){
                var data = "key=" + $(this).val();
                if(e.which == 13){
                    $.ajax({
                        type:"POST",
                        dataType:'json',
                        url:"ajax.php?act=get_barcode",
                        data:data,
                        beforeSend:function(){
                            $("#searchBar img").fadeIn(200);
                        },
                        success:function(data){
                            var $nameNumber = data.goodslist.length;
                            var $nameList = $("#nameList");
                            var $styleList = $("#styleList");
                            $styleList.html("").hide();
                            $nameList.html("");
                            $("#searchBar img").fadeOut(200);
                            if($nameNumber == 0){
                                $nameList.append("<p>搜不到呀,换个关键词再试试~</p>").fadeIn(200);
                            }else{
                                for(var i=0;i<$nameNumber;i++){
                                    $nameList.append("<p>"+data.goodslist[i].name+"</p>").fadeIn(200);
                                }
                                var $np = $("#nameList p");
                                $np.click(function(){
                                    var aname = $(this).text();
                                    var goodsIndex = $np.index(this);
                                    // alert(goodsIndex);
                                    $("#searchBarcode").val(aname);
                                    $nameList.html("").fadeOut(200);
                                    var styleLen = data.goodslist[goodsIndex].style_list.length;
                                    // alert(styleLen);
                                    for(var i=0;i<styleLen;i++){
                                        $styleList.append("<p>"+data.goodslist[goodsIndex].style_list[i]+"</p>");
                                    }
                                    $styleList.fadeIn(200);      
                                });
                            }
                        },
                        error:function(){
                            alert("操作失败，请刷新!");
                        }
                    });
                }
            });
            
            function dateFormate(str){
            	var a = /^(\d{4})-(\d{2})-(\d{2})$/;
            	if(!a.test(str)){
            		alert("时间格式不正确")
            		return false;
            	}else
            		return true;
            }


            $examine_ok.click(function(){
            	var index = $examine_ok.index(this);
            	var dateErr=false;
            	$examine_ok.eq(index).attr('disabled', true);
            	$examine_refuse.eq(index).attr('disabled', true);
            	$('#cancel_wait').attr('disabled', true);
                var $nowNum = $(this).parent().parent().parent().parent().find(".nowNum");
                $(this).parent().parent().parent().parent().find(".dateProduce").each(function(){
                	if($(this).attr("readonly")!="readonly"){
                		if(!dateFormate($.trim($(this).val()))){
                    		$(this).focus();
                    		$examine_ok.eq(index).removeAttr('disabled');
                    		dateErr=true;
                    		return false;               			
                		}else{
                		var dt=new Date($(this).val()).getTime();
                		var dt_now=new Date().getTime();
                		var t=dt-dt_now;
                		if(t>0){
                    		$(this).focus();
                    		alert("生产日期不能超过当前日期");
                    		$examine_ok.eq(index).removeAttr('disabled');
                    		dateErr=true;
                    		return false;		
                		}
                	}
                	}
                })
                $(this).parent().parent().parent().parent().find(".batch").each(function(){
                	var isEnabled=$(this).attr("readonly");
                	if(!isEnabled){
                		
                		if($(this).val()==""){
                			alert("请维护批次号!");
                    		$examine_ok.eq(index).removeAttr('disabled');
                    		dateErr=true;
                    		return false;
                		}else{
                			var batch_id_reg = new RegExp("^[A-Za-z0-9_-]+$");
                			if(!batch_id_reg.test($.trim($(this).val()))){
                				alert("请正确维护批次号（数字、字母、-、_）!");
    	                		$examine_ok.eq(index).removeAttr('disabled');
	                    		dateErr=true;
	                    		return false;
                			}
                		}
                	}
                })
   				if(dateErr) return false;
                
                var len = $nowNum.length;
                for(var i=0;i<len;i++){
                    var am = Number($nowNum.eq(i).parent().prev().text());
                    var nn = Number($nowNum.eq(i).val()); 
                    if(isNaN(nn) || parseInt(nn) != nn || nn < 0){
                    	alert("请输入合法的商品数量（正整数）");
                    	$examine_ok.eq(index).attr('disabled', false);
                    	$examine_refuse.eq(index).attr('disabled', false);
                	    $('#cancel_wait').attr('disabled', false);
                    	return false;
                    }else if(am > nn){
                        alert("对不起，请扫描所有的商品！");
                        $examine_ok.eq(index).attr('disabled', false);
                        $examine_refuse.eq(index).attr('disabled', false);
                	    $('#cancel_wait').attr('disabled', false);
                        return false;
                    }else if(am < nn){
                    	alert("本次退回数量超过可退数量！请重新输入");
                    	$examine_ok.eq(index).attr('disabled', false);
                    	$examine_refuse.eq(index).attr('disabled', false);
                	    $('#cancel_wait').attr('disabled', false);
                    	return false;
                    }
                }
                if(confirm("确定入库吗？")){
                    $(this).parents().find('input[name="inner_check"]').val('pass');
                    $(this).parents().filter(".form_submit").trigger("submit");
                    return true;
                }else{
                    $examine_ok.eq(index).attr('disabled', false);
                    $examine_refuse.eq(index).attr('disabled', false);
                	$('#cancel_wait').attr('disabled', false);
                    return false;
                };
            }); 
            $examine_refuse.click(function(){
            	//$(this).parents().find('input[name="inner_check"]').val('refused');
            	index = $examine_refuse.index(this);
                $examine_ok.eq(index).attr('disabled', true);
            	$examine_refuse.eq(index).attr('disabled', true);
            	$('#cancel_wait').attr('disabled', true);
            	alert('这是验货拒绝哦！');         	          
	            if(confirm('确定拒绝吗？')){
	            	$(this).parents().find('input[name="inner_check"]').val('refused');
	            	$(this).parents().filter(".form_submit").trigger("submit");
	                return true;
	            }else{
		            $examine_ok.eq(index).attr('disabled', false);
	            	$examine_refuse.eq(index).attr('disabled', false);
	            	$('#cancel_wait').attr('disabled', false);
	            	return false;
	            };

            }); 
            
            $scanBox.keyup(function(e){
                if(e.which == 17 || e.which == 13){
                    var scanIndex = $(".scanBox").index(this);
                    var thisVal = $(this).val();
                    var orderId = $(".scanBox").eq(scanIndex).prev().prev().text();
                    var data = "key="+thisVal+"&orderId="+orderId;
                    $(".tips").hide();
                    $.ajax({
                        type:"post",
                        dataType:'json',
                        url:"../return/goodScan",
                        data:data,
                        beforeSend:function(){
                            $(".scanBox").eq(scanIndex).next().addClass("loadTips").text("正在拼了命加载...").fadeIn(400);
                        },
                        success:function(data){
                        	console.log(data);
                        	if(data.keyStatus == 0){
                        		$(".scanBox").eq(scanIndex).next().removeClass("loadTips").text("条码（串号）输入错误（该商品可能不属于此订单）！请重新扫描！");
                        	}else{
                        		var is_full = true;
                        		for(var i=0;i<data.goodIndex.length;i++){
	                            	var goodsInd = data.goodIndex[i];
	                            	var nowgoodsList = $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd);
		                            var goodsNumNow = parseInt(nowgoodsList.find(".nowNum").val());
		                            var availableNum = parseInt(nowgoodsList.find(".amount").text());
	                               	var validity="";
	                               	var batch_sn="";	
	                               	var validity_enable=true;
	                               	var batchsn_enable=true;                               	   
		                            if(goodsNumNow < availableNum) {
		                               var keyStatus = data.keyStatus;   // 标记用户输入的条码（串号）的查找结果    0:未找到用户输入的条码（串号） 
		                               									//1:找到了用户输入的条码   2:找到了用户输入的串号   3:用户输入了商品条码，但该商品为串号控制商品
      									$(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".nowNum").keyup(function(event){
	                                        	if($(this).val() == ""){
	                                        		$(this).val("0");
	                                        	}
	                                        	var reg = /^\d+$/;
	                                        	var n = $(this).val();
		                                        if(n == 0){
		                                        	$(this).attr("readonly",true);
		                                        	//当数量变为0时候初始化
		                                        	 $(this).parent().parent().find(".newOld").html("");
		                                        	 $(this).parent().parent().find(".validity").html("");
		                                        	 $(this).parent().parent().find(".batchSn").html("");
		                                        	 oldValue=0;
		                                        }
		                                        var an = $(this).parent().prev().text();
		                                        var b = parseInt(n) <= parseInt(an);
			                                    validity=$(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find("input.dateProduce").eq($(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find("input.dateProduce").size()-1).val();
			                                    batch_sn=$(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find("input.batch").eq($(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find("input.batch").size()-1).val();                                     
		                                        if( reg.test(n) && b){
		                                        	var oldNum=$(this).next().val();
		                                        	var add=parseInt(n) -parseInt(oldNum);
		                                        	$(this).next().val(n)
		                                        	if(add>0){
		                                        		//增加Radio组合
			                                          	for(var k=0;k<add;k++){
			                                                $(this).parent().parent().find(".newOld")
			                                                .append("<div class='radioPanel'><input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].isSerial' value='N'/>"
			                                                	   +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].productId' value='" + data.productId + "'/>"
			      		                                		   +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].barcode' value='" + data.key + "'/>"
			      		                                		   +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].orderGoodsId' value='" + data.orderGoodsIdList[i] + "'/>"
			    		                                		   +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].serialNumber' value=''/>"
			    		                                		   +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].num' value='1'/>"
			                                                	   +"<input type='radio' name='returnProductAccDetails[" + radioSeq.toString() + "].status' value='NORMAL' checked='checked'>全新"
			                                                	   +"<input type='radio' name='returnProductAccDetails[" + radioSeq.toString() + "].status' value='DEFECTIVE'>二手"
			                                                	   +"</div>");
			                                        		//增加生产日期维护框
		                                        			if(validity_enable)
		                                        				$(this).parent().parent().find(".validity")
		                                        				.append('<input type="text" class="form_datetime form-control lq-form-control mulInput dateProduce" name="returnProductAccDetails[' + radioSeq.toString() + '].validity" value="'+validity+'">');
		                                        			else
		                                        				$(this).parent().parent().find(".validity")
		                                        				.append('<input type="text" class="form_datetime form-control lq-form-control mulInput dateProduce" name="returnProductAccDetails[' + radioSeq.toString() + '].validity" value="" readonly>');		                                        				
		                                        		 	
		                                        			//绑定时间控件
		                                        			$('.form_datetime').datetimepicker({
		                                        		 	    //language:  'fr',
		                                        		 	    language: 'zh',
		                                        		 	    weekStart: 1,
		                                        		 	    todayBtn:  1,
		                                        		 		autoclose: 1,
		                                        		 		todayHighlight: 1,
		                                        		 		startView: 2,
		                                        		 		forceParse: 0,
		                                        		 	    showMeridian: 1,
		                                        		 	    minView: 'month',
		                                        		 	    format: 'yyyy-mm-dd'
		                                        		 	});
		                                        		    $(".form-control").on("focus",function(){
		                                        		    	$("th.prev span").text("<");
		                                        		    	$("th.next span").text(">");
		                                        		    });
		                                        			//增加批次号维护框
		                                        			if(batchsn_enable)
		                                        				$(this).parent().parent().find(".batchSn")
		                                        				.append('<input type="text" class="lq-form-control mulInput batch" name="returnProductAccDetails[' + radioSeq.toString() + '].batch_sn" value="'+batch_sn+'" >');
		                                        			else
		                                        				$(this).parent().parent().find(".batchSn")
		                                        				.append('<input type="text" class="lq-form-control mulInput batch" name="returnProductAccDetails[' + radioSeq.toString() + '].batch_sn" value="" readonly>');		
			                                                
			                                                
			                                                radioSeq++;
			                                          	}

		                                        	}else{
		                                        		//移除生产日期维护框
		                                        		add=Math.abs(add);
		                                        		var validity_total=$(this).parent().next().next().find("input").size();
		                                        		var removeIdx=validity_total-add-1;
		                                        		$(this).parent().next().next().find("input:gt("+removeIdx+")").each(function(){
		                                        			$(this).remove();
		                                        		})
		                                        		
		                                        		//移出批次号
		                                        		$(this).parent().next().next().next().find("input:gt("+removeIdx+")").each(function(){
		                                        			$(this).remove();
		                                        		})
		                                        		
		                                        		//移除radioButton
 		                                        		$(this).parent().next().next().next().next().find("div.radioPanel:gt("+removeIdx+")").each(function(){
		                                        			$(this).remove();
		                                        		})
		                                        		
		                                       	 }
		                                        }else{
		                                        	$(this).val(an)
		                                            alert("请输入小于等于可退数量的正整数");
		                                         
		                                        }
		                                   });
		                                   		                               
		                               if(keyStatus == 1){  
		                                   $(".tips").fadeOut();
		                               	   var newNum = parseInt(goodsNumNow) + 1;

		                               	   nowgoodsList.find(".nowNum").attr("readonly",false);		

		                                   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".nowNum").val(newNum);  // 本次已退商品数目加一
		                                   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find("#oldValue").val(newNum);//保存旧值
		                               	   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".barcode")  // 刷新刚扫描进的商品条码
		                                   .text(data.key+"（条码）");
		                                   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".newOld")
		                                   .append("<div class='radioPanel'><input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].isSerial' value='N'/>"
		                                		  +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].productId' value='" + data.productId + "'/>"
		                                		  +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].barcode' value='" + data.key + "'/>"
		                                		  +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].orderGoodsId' value='" + data.orderGoodsIdList[i] + "'/>"
		                                		  +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].serialNumber' value=''/>"
		                                		  +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].num' value='1'/>"
		                                		  +"<input type='radio' name='returnProductAccDetails[" + radioSeq.toString() + "].status' value='NORMAL' checked='checked'>全新"
		                                		  +"<input type='radio' name='returnProductAccDetails[" + radioSeq.toString() + "].status' value='DEFECTIVE'>二手"
		                                		  +"</div>");
		                               	   //判断是否需要维护生产日期和批次号判断字段为validityFlag和batchSnFlag
		                               	   //validityFlag:true需要维护;false:不需要维护
		                               	   //batchSnFlag:'1'表示需要手动输入维护，'2'表示需要跟生产日期一样,'3'表示需要系统给定默认值1970
		                               	   //validityFlag:true需要维护;false:不需要维护
		                               	   //batchSnFlag:0代表不维护  '1'表示需要手动输入维护，
		                                   if(data.validityFlag){
		                                	   validity_enable=true;
		                                	   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".validity")
		                                	   .append('<input type="text" name="returnProductAccDetails['+radioSeq.toString()+'].validity"  class="form_datetime form-control lq-form-control mulInput dateProduce" value="'+validity+'"/>');
                                   			$('.form_datetime').datetimepicker({
                                		 	    //language:  'fr',
                                		 	    language: 'zh',
                                		 	    weekStart: 1,
                                		 	    todayBtn:  1,
                                		 		autoclose: 1,
                                		 		todayHighlight: 1,
                                		 		startView: 2,
                                		 		forceParse: 0,
                                		 	    showMeridian: 1,
                                		 	    minView: 'month',
                                		 	    format: 'yyyy-mm-dd'
                                		 	});
                                   		    $(".form-control").on("focus",function(){
                                   		    	$("th.prev span").text("<");
                                   		    	$("th.next span").text(">");
                                   		    });
		                                   }else{
		                                	   validity_enable=false;
		                                	   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".validity")
		                                	   .append('<input type="text" name="returnProductAccDetails['+radioSeq.toString()+'].validity" class=" form_datetime form-control lq-form-control mulInput dateProduce" value="" readonly/>');		                                	   
		                                   }
		                                   
		                                   if(data.batchSnFlag==1||data.batchSnFlag=="1"){
		                                	   batchsn_enable=true;
		                                	   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".batchSn")
		                                	   .append('<input type="text" name="returnProductAccDetails['+radioSeq.toString()+'].batch_sn" class="lq-form-control mulInput batch" value="'+batch_sn+'"/>')
		                                   }else if(data.batchSnFlag==0||data.batchSnFlag=="0"){
		                                	   batchsn_enable=false;
		                                	   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".batchSn")
		                                	   .append('<input type="text" name="returnProductAccDetails['+radioSeq.toString()+'].batch_sn" class="lq-form-control mulInput batch" value="" readonly/>');	                                	   
		                                   }	                                   
		                                   $(".scanBox").eq(scanIndex).val("");		                                   
		                                   radioSeq++;
		                               }else if(keyStatus == 2){  // 找到了用户输入的串号 
		                                    if($.inArray(data.key,existSN) == -1){
		                                           existSN.push(data.key);
		                                           $(".tips").fadeOut();
		                                           var newNum = parseInt(goodsNumNow) + 1;
		                                           $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".nowNum").val(newNum);  // 本次已退商品数目加一
		                                           
		                                           $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".barcodeOrSerial")  // 添加刚扫描进的商品串号
		                                           .append("<p>" + data.key + "（串号）</p>");
		                                           $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".newOld")
		                                           .append("<div class='radioPanel'><input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].isSerial' value='Y'/>"
		                                        		  +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].productId' value='" + data.productId + "'/>"
		                                        		  +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].serialNumber' value='" + data.key + "'/>"
		                                        		  +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].orderGoodsId' value='" + data.orderGoodsIdList[i] + "'/>"
		                                        		  +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].barcode' value=''/>"
		                                        		  +"<input type='hidden' name='returnProductAccDetails[" + radioSeq.toString() + "].num' value='1'/>"
		                                        		  +"<input type='radio' name='returnProductAccDetails[" + radioSeq.toString() + "].status' value='NORMAL' checked='checked'>全新"
		                                        		  +"<input type='radio' name='returnProductAccDetails[" + radioSeq.toString() + "].status' value='DEFECTIVE'>二手"
		                                        		  +"</div>");
		                                           
		    	                                   if(data.validityFlag){
				                                	   validity_enable=true;
				                                	   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".validity")
				                                	   .append('<input type="text" name="returnProductAccDetails['+radioSeq.toString()+'].validity"  class="form_datetime form-control lq-form-control mulInput dateProduce" value="'+validity+'"/>');
				                                   }else{
				                                	   validity_enable=false;
				                                	   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".validity")
				                                	   .append('<input type="text" name="returnProductAccDetails['+radioSeq.toString()+'].validity" class=" form_datetime form-control lq-form-control mulInput dateProduce" value="1970-01-01" readonly/>');		                                	   
				                                   }
				                                   
				                                   if(data.batchSnFlag==1||data.batchSnFlag=="1"){
				                                	   batchsn_enable=true;
				                                	   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".batchSn")
				                                	   .append('<input type="text" name="returnProductAccDetails['+radioSeq.toString()+'].batch_sn" class="lq-form-control mulInput batch" value="'+batch_sn+'"/>')
				                                   }else if(data.batchSnFlag==2||data.batchSnFlag=="2"){
				                                	   batchsn_enable=true;
				                                	   var vt=$(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".dateProduce").val();
				                                	   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".batchSn")
				                                	   .append('<input type="text" name="returnProductAccDetails['+radioSeq.toString()+'].batch_sn" class="lq-form-control mulInput batch" value="'+vt+'" readonly/>');	                                	   
				                                   }else if(data.batchSnFlag==0||data.batchSnFlag=="0"){
				                                	   
				                                   }
				                                   else{
				                                	   batchsn_enable=false;
				                                	   $(".scanBox").eq(scanIndex).next().next().find(".goodsList").eq(goodsInd).find(".batchSn")
				                                	   .append('<input type="text" name="returnProductAccDetails['+radioSeq.toString()+'].batch_sn" class="lq-form-control mulInput batch" value="1970-01-01" readonly/>');	            
				                                   }		                                           
		                                           $(".scanBox").eq(scanIndex).val("");
		                                           radioSeq++;
		                                    }else{
		                                        $(".scanBox").eq(scanIndex).next().removeClass("loadTips").text("该串号已经输入过了呀~");  
		                                    }
		                               }else if(keyStatus == 3){  // 用户输入了正确的商品条码，但该商品为串号控制商品,应该输入商品的串号
		                               	   $(".scanBox").eq(scanIndex).next().removeClass("loadTips").text("该商品为串号控制商品,应该输入商品的串号");
		                               }else if(keyStatus == 4){  // 用户输入了正确的串号，可是该串号的商品已经在库存里面了
		                               	   $(".scanBox").eq(scanIndex).next().removeClass("loadTips").text("该串号的商品已经在库存里面了，请扫描其他商品入库");
		                               }else{  // 未找到用户输入的条码（串号）
		                                   $(".scanBox").eq(scanIndex).next().removeClass("loadTips").text("输入错误（该商品可能不属于此订单）呀,请重新扫描~");
		                               }
		                               is_full = false;
		                               break;
		                            }
	                            }
		                        if(is_full){
		                        	$(".scanBox").eq(scanIndex).next().removeClass("loadTips").text("已达可退数量上限呀~");
		                        }
                        	}
                        },
                        error:function(){
                            alert("操作失败，请刷新!!");
                        }
                    });
                }
            }); 
            
        });
        </script>
</body>
</html>