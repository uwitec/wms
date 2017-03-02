<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>交接单查询</title>
	<link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath() %>/static/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">  
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">

	<style type="text/css">
		.searchBtn {
			margin: 0 0 0 10px;
		}
		#reviewAll {
			margin: 0 0 0 30px;
		}
		#page-search {
			float: right;
		}
		.page-wrap {
			display: none;
		}
		.uploadfile {
			margin: 10px 0 0 0;
		}
		#batchTable {
			margin: 10px 0 0 0;
		}
		.inputMsg {
		}
	    .totalPage {
	    	display:inline-block;
	    	margin: 15px ;
	    }
	    input[type=text]{
	    	height:30px;
	    }
	</style>
</head>
<body onload="onLoad()">
	<div class="modal-wrap mw-loading">
		<div class="modal-content mc-loading">
			<i class="fa fa-spinner fa-spin" id="loading"></i>
		</div>
	</div>
	<div class="main-container">
		<form class="lq-form-inline">
			<div class="lq-col-3">
				<div class="lq-form-group">
					<label for="customer_id">选择货主:</label>
					<select name="customer_id" id="customer_id" class="lq-form-control">
		                <option value="0" >--请选择--</option>
	                   	<c:forEach items="${customers}" var="customer"> 
		                   	<option <c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
						    value="${customer.customer_id}">${customer.name}</option>
						</c:forEach> 
		            </select>
		        </div>			
			</div>
			<div class="lq-col-6">
				<div class="lq-form-group">
					<label for="start">开始时间:</label>
<%-- 					<input type="text" readonly="true" id="start" name="start" value="${start}" size="12" class="lq-form-control">
					<button id="startTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
						<i class="fa fa-calendar"></i>
					</button>  --%>
					<input class="form-control form_datetime lq-form-control" id="start" type="text" name="start" value="${start}" size="12" readonly/>
					<!--  -->
					<label for="end">结束时间:</label>
<%-- 					<input type="text" readonly="true" id="end" name="end" value="${end}" size="12" class="lq-form-control"/>
					<button id="endTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
						<i class="fa fa-calendar"></i>
					</button> --%>
					<input class="form-control form_datetime lq-form-control" id="end" type="text" name="end" value="${end}" size="12" readonly/>
					<!--  -->
					<button type="submit" id="btn-search" class="lq-btn lq-btn-sm lq-btn-primary searchBtn">
						<i class="fa fa-search"></i>查询
					</button>
					<button type="submit" id="btn-export" class="lq-btn lq-btn-sm lq-btn-primary searchBtn">
						<i class="fa fa-search"></i>导出
					</button>	
				</div>		
			</div>
		</form>
		<div style="clear:both;"></div>
		<table class="lq-table">
			<thead>
				<tr>
					<th>编号</th>
					<th>渠道</th>
					<th>商品条码</th>
					<th>货主名称</th>
					<th>商品名称</th>
					<th>日期</th>
					<th>数量</th>
				</tr>
			</thead>
			<tbody class="tbody">

			</tbody>
		</table>
		<div class="noDataTip">未查到符合条件的数据!</div>
		<div class="page-wrap">
			<div class="lq-row">
				<nav id="page-search">
					<ul class="lq-page-control">
						<li class="prev"><a href="#" aria-label="Previous" alt="Previous">&laquo;</a></li>
						<li class="next"><a href="#" aria-label="Next" alt="Next">&raquo;</a></li>
					</ul>
				</nav>
			</div>
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
		//开始时间默认当前日期的一周之前
		function startDate(){
			var dateNow=new Date( new Date().getTime()-7*24*3600*1000);
			var year=dateNow.getFullYear();
			var month=dateNow.getMonth()+1;
			var day=dateNow.getDate();
			if(month<10) month="0"+month;
			if(day<10) day="0"+day;
			return year+"-"+month+"-"+day;
		}
		function onLoad(){
			$("#start").val(startDate());
		}
	</script> 
	<script type="text/javascript">
		initialParam();
	
		
		function showTable(data){
			var tdHtml="";
			for(var i in data){
				var no=parseInt(i)+1;
				tdHtml+="<tr>";
				tdHtml+="<td>"+no+"</td>";
				tdHtml+="<td>"+data[i].warehouse_name+"</td>";
				tdHtml+="<td>"+data[i].barcode+"</td>";
				tdHtml+='<td>'+data[i].name+'</td>';
				tdHtml+='<td>'+data[i].product_name+'</td>';
				tdHtml+='<td>'+str2Date(data[i].created_time)+'</td>';
				tdHtml+='<td>'+data[i].num+'</td>';
				tdHtml+="</tr>";
			}
			$("tbody.tbody").html(tdHtml);
		}
		
		$(document).on("click",".lq-page-control li",function(){
			var url="./doQueryHistoryTaskImprove";
			var param={
					pageSize : 50,
					customer_id:$("#customer_id").val(),
					start:$("#start").val(),
					end: $("#end").val()
			}
			clickPage($(this),url,param,showTable);
		});
		
		$("#btn-search").click(function(e){
			e.preventDefault();
			$("tbody.tbody").html("");
			$("#page-search").html("");
			$(".totalPage").text("");
			data={
					customer_id:$("#customer_id").val(),
					start:$("#start").val(),
					end: $("#end").val()
			}
			$.ajax({
				url:'./doQueryHistoryTaskImprove',
				type:'post',
				dataType:'json',
				data:data,
				beforeSend:function(){
					$(".mw-loading").fadeIn(300);
				},
				success:function(res){
					console.log(res);
					$(".mw-loading").fadeOut(300);
					if(res.list.length>0){
						$(".noDataTip").fadeOut(100);
						showTable(res.list);
						PAGE_DATA = res.page;
						TOTAL_PAGE = parseInt(PAGE_DATA.totalPage);
						if (TOTAL_PAGE < MAX_PAGE) {
							END_PAGE = TOTAL_PAGE;
						} else {
							END_PAGE = MAX_PAGE;
						}
						CURRENT_PAGE = PAGE_DATA.currentPage;
						pageConstructor(CURRENT_PAGE,START_PAGE,END_PAGE);
						$('nav').after('<span class="totalPage">共'+PAGE_DATA.totalPage+'页,'+PAGE_DATA.totalCount+'条数据</span>');
					}else{
						$(".noDataTip").fadeIn(100);
					}
				},
				error:function(err){
					$(".mw-loading").fadeOut(300);
					alert(err);
				}
			})
		})
		
		$("#btn-export").click(function(e){
			e.preventDefault();
			var url="./export?";
			var customer_id=$("#customer_id").val();
			var start=$("#start").val();
			var end=$("#end").val();
			url=url+"&customer_id="+customer_id+"&start="+start+"&end="+end;
			window.location=url;
		})

	</script>

</body>
</html>