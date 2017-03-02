<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>移库资料查询</title>
<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css"> 
<style>
	#body-sale-search .lq-form-inline label {
    	width: 5.4em;
    }

    #btn-sale-search {
    	margin-left: 15px;
    }

	.validity {
		border: none;
		height: 100%;
		width: 100%;
	}
	
	 #page-search {
    	float: right;
    }

    .totalPage {
    	position: relative;
    	top: 5px;
    	margin-left: 5px;
    }
    
    input[type=text]{
    	height: 30px;
    }
    input#start,#end{
    	width: 40%;
    }
    .lq-form-group{
    	height: 30px;
    }
</style>


</head>
<body id="body-move">
	<div class="modal-wrap mw-loading">
		<div class="modal-content mc-loading">
			<i class="fa fa-spinner fa-spin" id="loading"></i>
		</div>
	</div>
	<div class="main-container">
		<form method="post"  class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="customer_id">选择货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
							<option <c:if test="${customer_id=='-1'}">selected="true"</c:if>
									value="">--请选择--</option> 
							<c:forEach items="${warehouseCustomerList}" var="customer">
								<option name="customer_id"
									class="${customer.customer_id}"
									<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
									value="${customer.customer_id}">${customer.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="start">选择日期:</label>
						<input class="form-control form_datetime lq-form-control" id="start" type="text" name="start" value="${created_time}" size="12"/>
					</div> 
					<div class="lq-form-group">
						<label for="end" style="display:inline-block;width:61px;text-align:right;">至</label>
						<input class="form-control form_datetime lq-form-control" id="end" type="text" name="end" value="${delivery_time}" size="12"/>
					</div> 
				</div>
				
				<div class="lq-col-3">
					<div class="lq-form-group"> 
						<label style="text-indent: 27px;">工号:</label>
						<input id ="job_number" name="job_number"  class="lq-form-control">
					</div> 
					<div class="lq-form-group">  
					  	<label>移出储位:</label>
					    <input id ="out_location_barcode" name="out_location_barcode" class="lq-form-control">
					</div>
					<div class="lq-form-group"> 
					    <label>移入储位:</label>	
					    <input id ="in_location_barcode" name="in_location_barcode" class="lq-form-control">
					 </div>
				</div>
					
				<div class="lq-col-3">
					<div class="lq-form-group">  
					  	<label>商品编码:</label>
					    <input id ="barcode" name="barcode" class="lq-form-control">
					</div>
					<div class="lq-form-group">
						<label for="warehouse_id" style="text-indent: 1.8rem;">渠道:</label>
						<select name="warehouse_id" id="warehouse_id" style="">
							<option value="">不限</option>
							<c:forEach items="${warehouseList}" var="warehouse">
								<option name="warehouse_id" value="${warehouse.warehouse_id}">${warehouse.warehouse_name}</option>
							</c:forEach>
						</select>				
					</div>
				</div>		
							    
				<div class="lq-col-3">
		           	 <div class="lq-form-group">
						<input type="button" id="search" name="type" value="查询" class="lq-btn lq-btn-sm lq-btn-primary">
				     </div>
				     <div class="lq-form-group">
				     	<input type="button" id="export" name="type" value="导出" class="lq-btn lq-btn-sm lq-btn-primary">
				     </div>
				</div>
			</div>
		</form>
			
		<table class="lq-table" id="batchTable" style="font-size:10px">
			<thead>
				<tr>
					<th>货主</th>
					<th>渠道</th>
					<th>商品编码</th>
					<th>商品名称</th>
					<th>商品批次号</th>
					<th>移库数量</th>
					<th>移库时间</th>
					<th>移出储位</th>
					<th>移入储位</th>
					<th>工号</th>
					<th>姓名</th>
				</tr>
			</thead>
			<tbody class="tbody">
			
			</tbody>
			<c:if test="${fn:length(inventoryGoods) == '0'}">
			</table>
				<div class="noDataTip">未查到符合条件的数据!</div>
			</c:if> 

		</table>
		</div>
		<div class="page-wrap">
			<div class="lq-row">
				<nav id="page-search">
					<ul class="lq-page-control">
						<li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>
						 <c:forEach var="item" varStatus="i" begin="1" end="${page.totalPage}">
							 <li class="page"><a href="javascript:void(0);"></a></li>
						 </c:forEach>
						<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li>
					</ul>
				</nav>
			</div>
		</div>	
	<script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>  
	<script type="text/javascript" src="../static/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../static/js/bootstrap-datetimepicker.min.js"></script>	
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/v3.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/showPage.js"></script>
	<script type="text/javascript">
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
	</script>
	
	
	  
	<script type="text/javascript">
		$(document).ready(function(){
			function insert_flg(str){
			    var newstr="";
			    var before = str.substring(0,3), after = str.substring(3,7);
			    newstr = before + "-" + after;
			    str = newstr;
			    var before = str.substring(0,6), after = str.substring(6,8);
			    newstr = before + "-" + after;
			    str = newstr;
			    return newstr;
			}
			$(".td_location_barcode").each(function(){
				$(this).text(insert_flg($(this).text()));
			});
			
			
			$("#search").click(function(e){
				e.preventDefault();
				$(this).attr("disabled",true);
				$("#export").removeAttr("disabled");
				var that=$(this);
				var customer_id=$("#customer_id").val();
				var create_user=$("#job_number").val();
				var created_time=$("#start").val();
				var delivery_time=$("#end").val();
				var barcode=$("#barcode").val();
				var in_location_barcode=$("#in_location_barcode").val().split("-").join("");
				var out_location_barcode=$("#out_location_barcode").val().split("-").join("");
				var warehouse_id=$.trim($("#warehouse_id").val());
				console.log(out_location_barcode);
				$.ajax({
					url: '../history_query/moveQuery',
					type: 'post',
					dataType: 'json',
					data: {
						customer_id:customer_id,
						created_user:create_user,
						created_time:created_time,
						delivery_time:delivery_time,
						barcode:barcode,
						in_location_barcode:in_location_barcode,
						out_location_barcode:out_location_barcode,
						warehouse_id:warehouse_id
					},
					beforeSend : function(){
						$(".mw-loading").fadeIn(300);
					},
					success: function(data){
						$(".mw-loading").fadeOut(300);
						$('.noDataTip').fadeOut(300);
						that.removeAttr("disabled");
						console.log(data);
						if(!$.isEmptyObject(data)){
							PAGE_DATA = data.page;
							TOTAL_PAGE = parseInt(data.page.totalPage);
							TOTAL_ORDER = data.page.totalCount;
							START_PAGE = 1;
							if (TOTAL_PAGE < MAX_PAGE) {
								END_PAGE = TOTAL_PAGE;
							} else {
								END_PAGE = MAX_PAGE;
							}
							CURRENT_PAGE = data.page.currentPage;
							pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
	 						tableConstructor(data.moveList);
							if (!data.moveList.length) {
								$(".mw-loading").fadeOut(300);
								$('.noDataTip').fadeIn(300);
							}
						}
					},
					error: function(err){
						alert(err);
						console.log(err);
					}
				})
			})
			
	function str2Date(str) {
		var date=new Date(str);
		var month=date.getUTCMonth()+1;
		var year=date.getFullYear();
		var day=date.getUTCDate();
		return year+"-"+month+"-"+day+" "+date.toString().split(' ')[4];
	}		
	
	function tableConstructor(data){
		var thHtml='';
		console.log(data);
		for(var i in data){
			thHtml+='<tr>';
			thHtml+='<td>'+data[i].name+'</td>';
			thHtml+='<td>'+data[i].warehouse_name+'</td>';
			thHtml+='<td>'+data[i].barcode+'</td>';
			thHtml+='<td>'+data[i].product_name+'</td>';
			if(data[i].batch_sn!="")
				thHtml+='<td>'+data[i].batch_sn+'</td>';
			else
				thHtml+='<td>-</td>';
			thHtml+='<td>'+data[i].quantity+'</td>';
			thHtml+='<td>'+str2Date(data[i].last_updated_time)+'</td>';
			thHtml+='<td>'+data[i].out_location_barcode+'</td>';
			thHtml+='<td>'+data[i].in_location_barcode+'</td>';
			thHtml+='<td>'+data[i].username+'</td>';
			thHtml+='<td>'+data[i].realname+'</td>';
			thHtml+='</tr>';
		}
		$("tbody.tbody").html(thHtml);
	}
			
			
	$("#body-move").on("click",".lq-page-control li",function(e){
				e.preventDefault();
				var that = $(this);
				var customer_id=$("#customer_id").val();
				var create_user=$("#job_number").val();
				var created_time=$("#start").val();
				var delivery_time=$("#end").val();
				var barcode=$("#barcode").val();
				var in_location_barcode=$("#in_location_barcode").val().split("-").join("");
				var out_location_barcode=$("#out_location_barcode").val().split("-").join("");
				var warehouse_id=$.trim($("#warehouse_id").val());
				var url="../history_query/moveQuery";
				var param={
						pageSize : 20,
						customer_id:customer_id,
						created_user:create_user,
						created_time:created_time,
						delivery_time:delivery_time,
						barcode:barcode,
						in_location_barcode:in_location_barcode,
						out_location_barcode:out_location_barcode,
						warehouse_id:warehouse_id
				}
				clickPage($(this),url,param,tableConstructor);
			});
	
	
		$("#export").click(function(e){
			$(this).attr("disabled",true);
			var customer_id=$("#customer_id").val();
			var create_user=$("#job_number").val();
			var created_time=$("#start").val();
			var delivery_time=$("#end").val();
			var barcode=$("#barcode").val();
			var in_location_barcode=$("#in_location_barcode").val();
			var out_location_barcode=$("#out_location_barcode").val();
			var data='';
			data+=('&customer_id='+customer_id+'&created_user='+create_user+'&created_time='+created_time+'&delivery_time='+delivery_time+'&barcode='+barcode+'&in_location_barcode='+in_location_barcode+'&out_location_barcode='+out_location_barcode);
			var url="../history_query/exportmove?data"+data;
			console.log(url);
			window.location=url;

		})

		});
	</script>
</body>
</html>