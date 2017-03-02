<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>乐其WMS</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">

    <style type="text/css">
    	.moreLineItem {
    		display: inline-block;
    	}
    	.col-sm-2 {
    		display: inline-block;
    	}
    	input[type=text] {
    		height: 30px;
    	}
    	.main-container {
    		padding: 20px;
    	}
    	#chart {
    		width: 600px;
    		height: 500px;
    		margin: 40px auto;
    	}
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
    </style>
</head>
<body>
	<div class="modal-wrap mw-loading">
        <div class="modal-content mc-loading">
            <i class="fa fa-spinner fa-spin" id="loading"></i>
        </div>
    </div>
     <input id="list" type="hidden" value="${orderStatusList}"> 
    
    <table class="lq-table" id="batchTable" style="width:70%;margin:0 0 0 0 ; display: none;">
		 	<!--  shopNameLists -->
			<tbody>
				<c:forEach items="${orderStatusList}" varStatus="i" var="orders">
					<tr align="center" class="list" >
						<td>${orders.order_status}</td>
						<td>${orders.orderStatus}</td>
						<td>${orders.cou}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
     
	<div class="main-container">
		<form class="lq-form-inline" role="form" id="form-select" method = "post">
			<div class="lq-col-3">
				<div class="lq-form-group">
					<label for="customer_id">选择货主:</label>
					<select name="customer_id" id="customer_id" class="lq-form-control">
						<option name="customer_id"  value="0">--不限--</option>
						<c:forEach items="${customers}" var="customer">
							<option name="customer_id"
								<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
								value="${customer.customer_id}">${customer.name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="lq-col-3">
				<div class="lq-form-group">
					<div class="lq-form-group">
						<label for="shop_id">选择店铺:</label>
							<select name="oms_shop_id" id="oms_shop_id" class="lq-form-control">
								<option name="oms_shop_id" class="0" value="0">--不限--</option>
								<c:forEach var="customerShop" items="${shopNameLists}">
									<c:forEach items="${customerShop[1].shopName}" var="shopName">
										<c:if test="${!empty customerShop[1].shopName}">
											<option name="oms_shop_id" 
											<c:if test="${oms_shop_id==shopName.oms_shop_id}">selected="true"</c:if>
											 class="${customerShop[0].customerId}" value="${shopName.oms_shop_id}">${shopName.shop_name}</option>
										</c:if>
									</c:forEach>
								</c:forEach>
							</select>
					</div>
				</div>
			</div>
			<div class="lq-col-6">
				<div class="buyTime moreLineItem">
				 	<label class="col-sm-1 control-label" for="start">订单时间</label> 
					<%-- <select name="time_type" id="time_type" class="lq-form-control">
						<option value="order_time" <c:if test="${time_type == 'order_time'}">selected="selected"</c:if>>订单时间</option>
						<option value="pay_time" <c:if test="${time_type == 'pay_time'}">selected="selected"</c:if>>付款时间</option>
					</select> --%>
					
			      	<div class="col-sm-2">
			         	<input class="form-control form_datetime" id="start" type="text" name="start" value="${start}" size="16"/>
			      	</div>
			      	<label class="col-sm-1 control-label" for="end">至</label>
			      	<div class="col-sm-2">
			         	<input class="form-control form_datetime" id="end" type="text" name="end" value="${end}" size="16"/>
			      	</div>

			      	<button class="lq-btn lq-btn-sm lq-btn-primary searchBtn">
			      		查询
			      	</button>
			    	<input class="form-control"  name="act" type="hidden" value="search"/>
			    </div>
			</div>
		</form>
		<div id="chart_pie">
			<div style="text-align: left; font-size: 20px; margin-top: 50px; ">
				<p>总共订单量为:<span id="total"></span></p>
				<p>未发货量为:<span id="toDel"></span></p>
				<p>已发货量为:<span id="delivered"></span></p>
			</div>
			<div id="chart"></div>
		</div>

	</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/highcharts/highcharts.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/highcharts/highcharts-3d.js"></script>
<script type="text/javascript">
$(document).ready(function(){
 	
	$("#oms_shop_id").find("option:gt(0)").not($("."+$("#customer_id").val())).css("display","none");

	/* console.log("ShopName:"+$("#shopName").val()); */
	$("#customer_id").on("change",function(){
		$("#oms_shop_id").find("option:gt(0)").css("display","block").removeAttr("selected");
		var customerId=$("#customer_id").val();
		$("#oms_shop_id").find("option:gt(0)").not($("."+customerId)).css("display","none");
		$("#oms_shop_id").find("option").eq(0).attr("selected",true);
		
	})
	
	$(".form-control").on("focus",function(){
		$("th.prev span").text("<");
		$("th.next span").text(">");
	});
	
	
/* 	function Obj(order_status,orderStatus,cou)
	{
		this.order_status=order_status;
		this.orderStatus=orderStatus;
		this.cou=cou;
		this.amount=parseInt(cou);
		this.status=orderStatus;
	}
	
	function transToJson()
	{
		var arr=new Array();
		$("#batchTable tr").each(function(i,value){
			arr[i]=new Obj($(this).find("td").eq(0).text(),$(this).find("td").eq(1).text(),$(this).find("td").eq(2).text());
		})
		return arr;
	}
	
	function showChart() {
		pieChart(transToJson());
		var batchPick=0;
		var fulFilled=0,delivered=0;
		var cancel=0;
		var picking=0;
		var accept=0;
		var rechecked=0;
		var weighted=0;
		var Sum,toDel,Delivered;
		$("#batchTable tr").each(function(){
			var status=$(this).find("td").eq(0).text();
			var amount=parseInt($(this).find("td").eq(2).text());
			switch(status){
			case 'BATCH_PICK': batchPick=amount;break;
			case 'FULFILLED': fulFilled=amount;break;
			case 'DELIVERED' : delivered=amount;break;
			case 'CANCEL'	 : cancel=amount;break;
			case 'PICKING'	 : picking=amount;break;
			case 'ACCEPT'    : accept=amount;break;
			case 'RECHECKED' : rechecked=amount;break;
			case 'WEIGHED'  : weighted=amount;break;
			}
		});
		
		Sum=batchPick+fulFilled+delivered+cancel+picking+accept+rechecked+weighted;
		toDel=Sum-fulFilled-delivered-cancel;
		Delivered=fulFilled+delivered;
		
		$("#total").text(Sum);
		$("#toDel").text(toDel);
		$("#delivered").text(Delivered);

	} */

///////////////时间控件////////////////////

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
/* $('.form_date').datetimepicker({
	format:'yyyy-mm-dd',
    language:  'zh',
    weekStart: 1,
    todayBtn:  1,
	autoclose: 1,
	todayHighlight: 1,
	startView: 2,
	minView: 2,
	forceParse: 0
});
$('.form_time').datetimepicker({
    language:  'zh',
    weekStart: 1,
    todayBtn:  1,
	autoclose: 1,
	todayHighlight: 1,
	startView: 1,
	minView: 0,
	maxView: 1,
	forceParse: 0
}); */
//订单总量、未发货量、已发货量统计///
function Statistics(data){
	var batchPick=0;
	var fulFilled=0,delivered=0;
	var cancel=0;
	var picking=0;
	var accept=0;
	var rechecked=0;
	var weighted=0;
	var Sum,toDel,Delivered;	
	for(var i in data){
		amount=data[i].cou;
		switch(data[i].order_status){
		case 'BATCH_PICK': batchPick=amount;break;
		case 'FULFILLED': fulFilled=amount;break;
		case 'DELIVERED' : delivered=amount;break;
		case 'CANCEL'	 : cancel=amount;break;
		case 'PICKING'	 : picking=amount;break;
		case 'ACCEPT'    : accept=amount;break;
		case 'RECHECKED' : rechecked=amount;break;
		case 'WEIGHED'  : weighted=amount;break;
		}
	}
	
	Sum=batchPick+fulFilled+delivered+cancel+picking+accept+rechecked+weighted;
	toDel=Sum-fulFilled-delivered-cancel;
	Delivered=fulFilled+delivered;
	
	$("#total").text(Sum);
	$("#toDel").text(toDel);
	$("#delivered").text(Delivered);
}


///////////查询//////////

 $(".searchBtn").on("click",function(e){
	e.preventDefault();
	var that=$(this);
	var customer_id=$("#customer_id option:selected").val();
	var start=$("#start").val();
	var end=$("#end").val();
	var oms_shop_id=$("#oms_shop_id").val();
	/* var data="customer_id="+customer_id+"&start="+start+"&end="+end; */
	var data={
			customer_id: customer_id,
			start: start,
			end: end,
			oms_shop_id:oms_shop_id
	}
	console.log(data);
	$.ajax({
		type: 'post',
		dataType:'json',
		url: './welcome_ajax',
		data:data,
		beforeSend: function(){
            $(".mw-loading").fadeIn(300);
       
		},
		success: function(data) {
			$(".mw-loading").fadeOut(300);
		     that.removeAttr("disabled");
			console.log(data);
			pieChart(data);
			Statistics(data);
		},
		error: function(err){
			$(".mw-loading").fadeOut(300);
			console.log(err);
		}
	})

}) 

	$(".searchBtn").trigger("click");


////////////饼状图显示////////

function pieChart(datas){
    
	for(var i=0;i<datas.length;i++)
	{
		datas[i].y=datas[i].cou;
	}
	
	var chart= {
			type: 'pie',
			options3d: {
				enabled: false, //显示为2d饼图
				alpha: 45,
				beta: 0
			}
		};
	
	var title= {
			 text: ''
	 };
	
	var tooltip={
	
	 			formatter:function(){
					return '数量:'+this.y+'<br/>'+'百分比:'+Highcharts.numberFormat(this.percentage,1)+'%';
				} 
	 };
	
	var plotOptions = {
		      pie: {
		          allowPointSelect: true,
		          cursor: 'pointer',
		          depth: 35,
		          dataLabels: {
		             enabled: true,
		             format: '{point.orderStatus}'
		          },
		        showInLegend: true,
		        colors:['#87CEEB', '#90ED7D', '#A52A2A', '#F7A35C', '#4B0082', '#F15C80', '#F0E68C','#7E003E', '#058DC7'],
		        events: {
		        	click: function(e){
		        		console.log(e.point.order_status);
		        	}
		        }
		      }
		};
	
	 var legend = {
		      layout: 'vertical',
		      align: 'left',
		      verticalAlign: 'middle',
		      borderWidth: 0,
		      labelFormatter: function() {
		      	return this.orderStatus+"("+this.cou+")"
		      }
		  };
	 
	 var series= [{
	     type: 'pie',
	        name: '订单量',
	        data: datas
		}]; 
	 
	 var credits={
	      		enabled: false
	     };
		
	 var json = {};   
	 json.chart = chart; 
	 json.title = title;       
	 json.tooltip = tooltip; 
	 json.plotOptions = plotOptions; 
	 json.series = series;
	 json.credits=credits;
	 json.legend = legend;
	 $('#chart').highcharts(json);
}
})
</script>
</body>
</html>