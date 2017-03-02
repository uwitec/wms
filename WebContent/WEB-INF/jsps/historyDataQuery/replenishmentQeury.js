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
<title>库存查询</title>
<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/js/zapatec/zpcal/themes/winter.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<style>
	#body-sale-search .lq-form-inline label {
    	width: 5.4em;
    }

    #btn-sale-search {
    	margin-left: 15px;
    }
    .noDataTip {
    	/*display: none;*/
    	text-align: center;
    	font-size: 30px;
    	color: #666;
    	margin: 30px 0 0 0;
    }
	
	
	.validity {
		border: none;
		height: 100%;
		width: 100%;
	}
</style>


</head>
<body>
<script>
function dateCheck(d){
	console.log(d);
	if(d.length==10){
		var month=parseInt(d.substring(5,7));
		var day= parseInt(d.substring(8,10));
		if(month>12||month<1){
			alert("输入的月份范围为(1~12)");
			return false;
		}
		if(day<1||day>31) {
			alert("输入的日期范围为(1~31)");
			return false;
		}
		return true;
	}else{
		alert("输入生产日期有误，正确格式为(yyyy-mm-dd)");
	}
}


 function dateChanged(value,pl_id,id) {
	 console.log(oldValidity);
	 console.log("pl_id:"+pl_id);
	 var read=$("#"+id).attr("readonly");
	 if(dateCheck(value)){
	 if(read=="readonly") {
		 $("#"+id).val(oldValidity);
		 return ;
	 }
	 if(value!=oldValidity){
		if(confirm("确认修改？")){
			 oldValidity=value;
			 $.ajax({
				 url: '../inventory/editValidity',
				 dataType: 'json',
				 type: 'POST',
				 data: {
					 pl_id:pl_id,
					 validity: value
				 },
				 success: function(data){
					 console.log(data.note);
					 alert(data.note);
					 $("#"+id).attr("readonly","true");
				 },
				 error: function(err) {
					 console.log(err);
					 alert("修改日期出错");
				 }
			 })
		 }
	}else {
		$("#"+id).val(oldValidity);
	}
	 }
	
 }
</script>
	<div class="main-container">
		<form method="post" action="../inventory/search" class="lq-form-inline">
			<div class="lq-row">
				<div class="lq-col-3">
					<div class="lq-form-group">
						<label for="customer_id">选择货主:</label>
						<select name="customer_id" id="customer_id" class="lq-form-control">
							<option <c:if test="${customer_id=='-1'}">selected="true"</c:if>
									value="-1">--请选择--</option> 
							<c:forEach items="${customers}" var="customer">
								<option name="customer_id"
									class="${customer.customer_id}"
									<c:if test="${customer_id==customer.customer_id}">selected="true"</c:if>
									value="${customer.customer_id}">${customer.name}</option>
							</c:forEach>
						</select>
					</div>
					<div class="lq-form-group">
						<label for="start">选择日期:</label>
						<input type="text" readonly="true" id="end" name="end" value="${end}" size="12" class="lq-form-control"/>
						<button id="startTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
							<i class="fa fa-calendar"></i>
						</button>
					</div> 
					<div class="lq-form-group">
						<label for="end" style="display:inline-block;width:61px;text-align:right;">至</label>
						<input type="text" readonly="true" id="end" name="end" value="${end}" size="12" class="lq-form-control"/>
						<button id="endTrigger" class="cal lq-btn lq-btn-sm lq-btn-default lq-btn-icon">
							<i class="fa fa-calendar"></i>
						</button>
					</div> 
				</div>
				
				<div class="lq-col-3">
					<div class="lq-form-group"> 
						<label style="text-indent: 27px;">工号:</label>
						<input id ="job_number" name="job_number"  class="lq-form-control">
					</div> 
					<div class="lq-form-group">  
					  	<label>下架储位:</label>
					    <input id ="order_id" name="order_id" class="lq-form-control">
					</div>
					<div class="lq-form-group"> 
					    <label>上架储位:</label>	
					    <input id ="batch_number" name="batch_number" class="lq-form-control">
					 </div>
				</div>
					
				<div class="lq-col-3">
					<div class="lq-form-group">  
					  	<label>商品编码:</label>
					    <input id ="barcode" name="barcode" class="lq-form-control">
					</div>
					<div class="lq-form-group"> 
					    <label>补货类型:</label>	
					  	<select name="replenishment_type" id="replenishment_type" class="lq-form-control">
						    <option value="urgency" <c:if test="">selected="true"</c:if>>紧急补货</option>
						    <option value="ordinary" <c:if test="">selected="true"</c:if>>一般补货</option>
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
					<th>商品编码</th>
					<th>商品名称</th>
					<th>上架数量</th>
					<th>补货日期</th>
					<th>补货时间</th>
					<th>下架储位</th>
					<th>上架储位</th>
					<th>补货类型</th>
					<th>工号</th>
					<th>姓名</th>
				</tr>
			</thead>
			<c:if test="${fn:length(inventoryGoods) == '0'}">
			</table>
				<div class="noDataTip">未查到符合条件的数据!</div>
			</c:if> 
		</table>
	</div>
	<script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>  
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/zapatec/utils/zapatec.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/zapatec/zpcal/src/calendar.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/zapatec/zpcal/lang/calendar-en.js"></script>
	<script type="text/javascript">
		Zapatec.Calendar.setup({
			weekNumbers       : false,
			electric          : false,
			inputField        : "start",
			button            : "startTrigger",
			ifFormat          : "%Y-%m-%d",
			daFormat          : "%Y-%m-%d"
		});
	
		Zapatec.Calendar.setup({
			weekNumbers       : false,
			electric          : false,
			inputField        : "end",
			button            : "endTrigger",
			ifFormat          : "%Y-%m-%d",
			daFormat          : "%Y-%m-%d"
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
			

		});
	</script>
</body>
</html>