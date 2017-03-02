<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>用户列表-新增</title>
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/user.css?t=201603211808">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/bootstrap-treeview.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/global.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap-treeview.js"></script>
    <style type="text/css">
	    html{
	    	font-size: 14px;
	    }
	    body{
	    	margin: 0;
	    	padding: 0;
	    	overflow: auto;
	    	height:100%;
	    }
    	.module{
	    	margin: 20px;
	    	overflow: hidden;
    	}
    	h3.module_title{
			border-left: 5px solid rgb(68,186,224);
			text-indent: 15px;
			margin-bottom: 20px;   	
    	}
    	.form-group input[type=text],
    	.form-group input[type=password],
    	.form-group select{
    		width: 48%;
    	}
    	.form-group h4{
    		margin-left: 2.6rem;
    	}
		#form-sysUser-edit input[type="checkbox"],
		#form-sysUser-edit input[type="radio"]{
 			-webkit-appearance:none;
			appearance:none;
			display: inline-block;
			width: 20px;
			height: 20px;
			border: 1px solid rgb(200,200,200);
			background: #fff;
			position: relative;
			cursor: pointer;
		}
		#form-sysUser-edit label {
			font-weight: normal;
		}
		#form-sysUser-edit input[type=text],
		#form-sysUser-edit select {
			height: 30px;
		}
		
		input[type=radio]{
			border-radius: 100%;
		}
		.glyphicon-ok:before{
			display: inline-block;
			height: 20px;
			width: 20px;
			text-align: center;
			position:absolute;
			top: 0;
			left:0;
		}
		.glyphicon-ok-sign:before {
			display: inline-block;
			height: 100%;
			width: 100%;
			position: absolute;
			top: -1px;
			left: 0;
			text-align:cnter;
			font-size: 20px;
		}
		label.checkItem{
			position: relative;
			top: -5px; 
			width:6rem;
			text-align: right;
		}
		.form-group label {
			position: relative;
			top: 0.35rem;
		}
		.form-group>div.lq-col-1{
			margin-left: 2rem;
		}
		.form-group h4 .change{
			display: inline-block;
			margin-left: 1.5rem;
			color: rgb(68,186,224);
			font-size: 14px;
			cursor: pointer;
		}
		.form-group .resultShow{
			padding: 8px 3rem;
			height: 35px;
			line-height: 1.4;
			font-weight: 700;
			color: #333;
		}
		.popUp{
			background: rgba(0,0,0,0.5);
			z-index: 999;
			width: 100%;
			height: 10000%;
			position: absolute;
			display: none;
		}
		.popUp .customerlist {
			height: 70%;
			padding-bottom: 10px;
			border: none;
			border-bottom: 1px solid #dbdbdb;
			overflow:auto;
		}
		.popUp .customers{
			width: 410px;
			height: 400px;
			overflow: auto;
			padding: 8px;
			box-shadow: 1px 1px 5px rgba(0,0,0,0.7),
						-1px -1px 5px rgba(0,0,0,0.7);
			position: fixed;
			top: 100px;
			left: 50%;
			margin-left: -15%;
			background: #fff;
			color: #333;
			border-radius: 5px;
			
		}
		h3.popUpTitle {
			border-bottom: 1px solid #dbdbdb;
			position: relative;
			padding: 5px 0;
		}
		h3.popUpTitle span{
			height: 20px;
			width: 20px;
			color: #ddd;
			border: 1px solid #dbdbdb;
			box-shadow: 1px 1px 2px rgba(0,0,0,0.3);
			border-radius: 100%;
			text-align:center;
			position: absolute;
			top: 6px;
			right: 10px;
			cursor: pointer;
			line-height: 1.4;
			font-size: 14px;
		}
		.module .resourceList{
			border: 1px solid #dbdbdb;
			height: 600px;
			overflow: hidden;
			position: relative;
		}
		.resourceList .leftTree{
			float: left;
			width: 30%;
			border: none;
			border-right: 1px solid #ddd;
			height: 100%;
			overflow:auto;
		}
		.resourceList .rightShow{
			height: 100%;
			width: auto;
			background: #eee;
			overflow: auto;
		}
		#list .selectedItem {
			padding: 15px;
			position: relative;
			border: 1px solid #dbdbdb;
			width: 80%;
			margin: 0px 20px 10px;
			border-radius: 5px;
			background:#fff;
			top: 20px;
		}
		#list .selectedItem p.content:before {
			content:'已选择的权限列表：';
			font-weight: 700;
			color: #333;
		}
		.newBtn {
			float: right;
		}
		.result {
			padding-left: 2.6rem;
			margin-top: 10px;
			padding-right:2rem;
		}
		
		.dot{
			border: 2px dotted rgb(68,186,224);
			padding: 5px;
			margin:5px;
			display:inline-block;
			cursor:pointer;
			position:relative;
		}
		.dot i{
			position: absolute;
			right: -6px;
			top: -6px;
			color:#fff;
			border:1px solid #f33;
			border-radius:100%;
			font-weight:normal;
			background:#f00;
			
			font-size:10px;
			display:none;
		}
		.dot:hover i{
			display:inline;
		}
		input[name="password"]::after{
			content:'*';
			font-size: 16px;
			color:#f00;
			font-weight:600;
			
		}
    </style>
</head>
<body>
    <form:form method="post" commandName="sysUser" id="form-sysUser-edit" class="lq-form-inline" style="height:100%;width:100%;position:relative;" >
        <form:hidden path="id"/>
        <form:hidden path="salt"/>
        <form:hidden path="available"/>
        <form:hidden path="created_time"/>
        <form:hidden path="last_login_time"/>
        <c:if test="${op ne '新增'}">
            <form:hidden path="password"/>
        </c:if>
        <input type="hidden" name="resource_ids" id="resource_ids"/>
        <input type="hidden" name="_roleIds" id="_roleIds" value="1"/>
        <input type="hidden" name="_customerIds" id="_customerIds" value="1"/>
        <input type="hidden" name="warehouseNames" id="warehouseNames">
        <input type="hidden" name="warehouse_ids" id="warehouse_id">
        <input type="hidden" name="resourceName" id="resourceName">
        
        
        
        <!-- 货主列表弹出窗  -->
	 	<div class="popUp" id="popup_customer">
			<div class="customers row">
				<h3 class="popUpTitle">货主列表<button class="lq-btn lq-btn-sm lq-btn-primary checkAll" style="position:relative;top:-5px;left:65%;">全选</button></h3>
				<div class="customerlist customer_list checkboxs">
					<c:forEach items="${customerList}" varStatus="j" var="customer" >
						<div class="lq-col-4">
							<label for="${customer.customer_id}" class="checkItem" style="display: inline-block; top:0; ">${customer.name}</label>
							<input type="checkbox" id="${customer.customer_id}" check="${customer.checked}"  name="customerIds" value="${customer.customer_id}" class="use_check customer_check glyphicon"/>
						</div>
					</c:forEach>
				</div>
				<button class="lq-btn lq-btn-sm lq-btn-primary cancel" id="cancel" style="position:relative;top:10px;left: 20%;">取消</button>
				<button class="lq-btn lq-btn-sm lq-btn-primary confirm" id="confirm" data="customer_list" style="position:relative;top:15px;left: 60%;">确认</button>
			</div>
		</div>
		<!-- 角色列表弹出窗  -->
	 	<div class="popUp" id="popup_role">
			<div class="customers row">
				<h3 class="popUpTitle">角色列表<button class="lq-btn lq-btn-sm lq-btn-primary checkAll" style="position:relative;top:-5px;left:65%;">全选</button></h3>
				<div class="customerlist role_list checkboxs">
		     		<c:forEach items="${sysRoleList}" varStatus="i" var="role">
						<div class="lq-col-4">
							<label for="${role.id}" class="checkItem">${role.name}</label>
							<input type="checkbox" id="${role.id}" check="${role.checked}" name="roleIds" value="${role.id}" class=" role_check glyphicon">
						</div>
		     		</c:forEach>
				</div>
				<button class="lq-btn lq-btn-sm lq-btn-primary cancel" id="cancel" style="position:relative;top:15px;left: 20%;">取消</button>
				<button class="lq-btn lq-btn-sm lq-btn-primary confirm" id="confirm" data="role_list" style="position:relative;top:15px;left: 60%;">确认</button>
			</div>
		</div>		
		<!--  -->
        <div class="module row">
        	<h3 class="module_title">基础信息</h3>
        	<div class="lq-col-3">
        		<div class="form-group">
        			<form:label path="username" for="username">用户名:</form:label>
        			<form:input path="username"  name="username" id="username" class="lq-form-control"  readonly="${op ne '新增'}"/>
	                <c:if test="${op eq '新增' }">
	              	  <span style="color:#f00; font-size: 16px; font-weight:600;">*</span>
	                </c:if>        			
        		</div>
				<div class="form-group">
					<form:label path="email" for="Email" style="text-indent:1rem">邮箱:</form:label>
					<form:input path="email" class="lq-form-control" id="Email" name="email"/>
				</div>        		
        	</div>
        	<div class="lq-col-3">
				<div class="form-group">
	                <form:label path="realname">真实姓名:</form:label>
	                <form:input path="realname" id="realname" class="lq-form-control"/>
	                <c:if test="${op eq '新增' }">
	              	  <span style="color:#f00; font-size: 16px; font-weight:600;">*</span>
	                </c:if>
	                
				</div>
				<div class="form-group">
					<form:label path="department_id" for="Department">分组(部门):</form:label>
					<form:select path="department_id" id="Department"  class="lq-form-control" >
						<c:forEach items="${departmentList}" var="department">
							<option value="${department.department_id}" <c:if test="${department.department_id==sysUser.department_id}">selected="true"</c:if>>${department.department_name}</option>
						</c:forEach>
					</form:select>
				</div>						
        	</div>
			<div class="lq-col-3">
	        	<c:if test="${op eq '新增'}">
	  				<div class="form-group">
						<form:label path="password" for="password" style="text-indent:2rem">密码:</form:label>
						<form:input path="password"  type="password" class="lq-form-control" id="password" name="password"/>
						<span style="color:#f00;">*</span>
					</div>
				</c:if> 				
				<div class="form-group">
					<label  for="ipConfig" style="text-indent: 1rem;"> IP权限:</label>
					<select id="ipConfig" name="ip_type" class="form-control" style="font-size: 0.89rem;"> 
 						<option name="ip_type" value="COMPANY" <c:if test="${'COMPANY'==sysUser.ip_type}">selected="true"</c:if>>公司IP</option>
						<option name="ip_type" value="ANYWHERE" <c:if test="${'ANYWHERE'==sysUser.ip_type}">selected="true"</c:if>>任何IP</option>
					</select>
				</div>
			</div>        	
        </div>
        <div class="module row">
      	  <h3 class="module_title">角色与权限</h3>
	   	    <div class="form-group row">
				<h4 class="group_title">角色<span id="change" class="change" data="role">更改</span></h4>
				<div id="role_list" class="result">
				已选择的角色权限:
				</div>
	     	</div>
			<div class="form-group row">
				<h4 class="group_title">货主权限<span id="change" class="change" data="customer">更改</span></h4>
				<div id="customer_list" class="result">
				已选择的货主权限:
				</div>
			</div>
			
			<div class="form-group row">
				<h4 class="group_title">仓库权限</h4>
				<div class="result warehouse">
<%-- 					<c:forEach items="${warehouseList}" varStatus="i" var="warehouse">
					<c:if test="${warehouse.warehouse_type=='PHYSICAL'}">
						<div class="lq-col-2">
							<label for="${warehouse.warehouse_id}" class="checkItem" style="width:8rem;">${warehouse.warehouse_name}</label>
							<input type="radio"  check="${warehouse.checked}" id="${warehouse.warehouse_id}" value="${warehouse.warehouse_id}" class="glyphicon">
						</div>
					</c:if>
					</c:forEach> --%>
					<c:forEach items="${warehouseList}" varStatus="i" var="warehouse">
					<c:if test="${warehouse.warehouse_type=='PHYSICAL'}">
						<div class="lq-col-2">
							<label for="${warehouse.warehouse_id}" class="checkItem" style="width:8rem;">${warehouse.warehouse_name}</label>
							<input type="checkbox"  check="${warehouse.checked}" id="${warehouse.warehouse_id}" value="${warehouse.warehouse_id}" class="glyphicon">
						</div>
					</c:if>
					</c:forEach>					
				</div>
			</div>				 	
        </div>
        
       	<div class="module row">
			<h3 class="module_title">资源列表<input type="submit" class="lq-btn lq-btn-sm lq-btn-primary newBtn" style="position:fixed; bottom:3%;right:6%;transform:scale(1.5);z-index: 999;" name="${op}" value="${op}"/></h3>
			
			<div class="resourceList">
				<div class="leftTree">
					<div id="tree"></div>
				</div>
				<div class="rightShow">
					<div id="list"></div>
				</div>
			</div>
		</div>
    </form:form>
    <script type="text/javascript">
    	function ShowResult(str){
    		var tdHtml;
    		var dt;
    		if(str=="customer_list"){
    			 tdHtml="已选择的货主权限:";
    			 dt="customer";
    		}
    		else{
    			 tdHtml="已选择的角色权限:";
    			 dt="role";
    		}
    			
    		$("."+str+".checkboxs input[type=checkbox]").each(function(){
     			if($(this).prop("checked")){
    				tdHtml+='<span class="dot" data="'+dt+'"><i class="re glyphicon glyphicon-remove"></i>'+$(this).prev().text()+'</span>';
    			} 
    		})
    		
    		$("#"+str).html(tdHtml);  
	    	$("span.dot i").on("click",function(){
	    		$(this).parent().remove();
	    		var content=$(this).parent().text();
	    		$("#popup_"+$(this).parent().attr("data")).find("label").each(function(){
	    			if(content==$(this)[0].textContent){
	    				$(this).next().removeAttr("checked").removeClass("glyphicon-ok");
	    			}
	    		})
	    	})
    	}
    	
/*     	if("${op}"=="新增"){
    		$("input[type=radio]").eq(0).addClass("glyphicon-ok-sign").attr("checked",true);
    	} */
    	$(".cancel").on("click",function(e){
    		e.preventDefault();
    		$(".popUp").fadeOut(300,function(){
    			$("body").css({overflow:"auto"});
    		});
    		

    	})
    	$(".confirm").on("click",function(e){
    		e.preventDefault();
    		var o=$(this).attr("data");
    		ShowResult(o);
    		$(".popUp").fadeOut(300,function(){
    			$("body").css({overflow:"auto"});
    		});
    		

    	})
    	$(".change").on("click",function(){
    		$("#popup_"+$(this).attr("data")).fadeIn(300);
    		$("body").css({overflow:"hidden"});
		
    	})
    	
    	
    	$(".checkboxs input[type=checkbox]").on("change",function(){
    		if($(this).prop("checked"))
    			$(this).addClass("glyphicon-ok");
    		else
    			$(this).removeClass("glyphicon-ok");
    		
    	})
    	function getChecked(){
    		var hasChecked=0;
    		$(".warehouse input[type=checkbox]").each(function(){
    			
    			if($(this).hasClass("glyphicon-ok")){
    				hasChecked++;
    			}
    			console.log($(this).val()+":"+$(this).hasClass("glyphicon-ok"))
    		})
    		return hasChecked;
    	}
    	$(".warehouse input[type=checkbox]").on("click",function(){
/*     		if($(this).prop("checked"))
    			$(this).addClass("glyphicon-ok");
    		else
    			$(this).removeClass("glyphicon-ok"); */
    		var hasChecked=getChecked();
    			
    		if(!$(this).hasClass("glyphicon-ok")){
        		if(hasChecked>0){
        			alert("一个用户无法拥有多个仓库权限")
        		}else{
        			$(this).addClass("glyphicon-ok");
        		}  			
    		}else{
    			$(this).removeClass("glyphicon-ok");
    		}

    		
    	})
    	
    	$(".checkAll").on("click",function(e){
    		e.preventDefault();
    		$(this).parent().siblings().find("input[type=checkbox]").each(function(){
    			$(this).prop("checked",true);
    			$(this).addClass("glyphicon-ok");
    		})
    	})
    	function Init(){
    		$("input[type=checkbox].use_check").each(function(){
    			if($(this).attr("check")=="1"){
    				$(this).addClass("glyphicon-ok");
    				$(this).prop("checked",true);
    			}
    				
    		})
    		ShowResult("customer_list");
    		$("input[type=checkbox].role_check").each(function(){
    			if($(this).attr("check")=="1"){
    				$(this).addClass("glyphicon-ok");
    				$(this).prop("checked",true);
    			}
    		})
    		ShowResult("role_list");
    		
    		$(".warehouse input[type=checkbox]").each(function(){
    			if($(this).attr("check")=="1"){
    				$(this).addClass("glyphicon-ok");
    			}
    		})
    	}
    	Init();

    </script>
    <script type="text/javascript"> 
	function transData(a,idStr,pidStr,childrenStr){
		var r=[],hash={},id=idStr,pid=pidStr,children=childrenStr,
			i=0,j=0,len=a.length;
		if(len==0)
			return [];
		else{
			for(;i<len;i++){
				hash[a[i][id]]=a[i];
			}
			for(;j<len;j++){
				var aVal=a[j];
				var hashVP=hash[aVal[pid]];
				if(hashVP){
                    a[j].lv=hashVP.lv+1;
                    if(!hashVP[children]) hashVP[children] = []
                    hashVP[children].push(aVal); 
				}else{
					r.push(aVal);
				}
			}

			return r;
		}
	}
    function getTree(){
        var zNodes =[
                     <c:forEach items="${resourceList}" var="r">
                     <c:if test="${not r.rootNode}">
                     { id:${r.id}, pId:${r.parent_id},special:${r.manual},text:"${r.name}",state:{expanded:false,checked:${r.checked}}},
                     </c:if>
                     </c:forEach>
                 ];
       zNodes.shift();
        for(var i in zNodes){
        	if(zNodes[i].pId==1){
        		zNodes[i].lv=1;
        	}
        	if(zNodes[i].special&&zNodes[i].pId!=1){
        		zNodes[i].color="#f00";
        	}
        }
        return transData(zNodes,'id','pId','nodes');
    }
    
    </script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstraptree.js"></script>
    <script type="text/javascript">
    	$(".node-tree").on("click",function(){
    		if($(this).find("span.expand-icon")){
        		var node_id=$(this).data("nodeid");
        		$("#tree").treeview('getNode',node_id);		
    		}

    	})
    </script>
    <script type="text/javascript">
    	$("#form-sysUser-edit").submit(function(){
    		var resource_ids="";
    		var resource_names="";
    		var warehouse_ids="";
    		var warehouse_names="";
    		if($(".selectedItem").size()>0){
    			$(".selectedItem").each(function(){
    				$(this).find("span").each(function(){
    					var id=$(this).attr("data");
    					var name=$(this).text();
    					resource_ids+=id+",";
    					resource_names+=name+",";
    				})
    			})
    			console.log(resource_ids);
    			$("#resource_ids").val(resource_ids.substring(0,resource_ids.length-1));
    			$("#resourceName").val(resource_names.substring(0,resource_names.length-1));
    		}
    		$(".warehouse input[type=checkbox]").each(function(){
    			if($(this).hasClass("glyphicon-ok")){
    				try{
    					warehouse_names+=$(this).prev().text()+",";
    					warehouse_ids+=$(this).val()+",";
    				}catch(e){
    					return false;
    				}
    			}
    		})
    		$("#warehouseNames").val(warehouse_names.substring(0,warehouse_names.length-1));
    		$("#warehouse_id").val(warehouse_ids.substring(0,warehouse_ids.length-1));
    		if(warehouse_names==""||warehouse_ids==""){
    			alert("仓库权限至少勾选一个!!");
    			return false;
    		}
   		    var flag = 1,error = "";
            if ($("#username").val() == "" || $("#realname").val() == "") {
                flag = 0;
                error = "用户名和真实姓名不能为空";
            }
            if ($("#password").length > 0) {
                if ($("#password").val() == "") {
                    flag = 0;
                    error = "密码不能为空";
                }
            }
            
            if (flag) {
                return true;
            } else {
                alert(error);
                return false;
            }
    	})
    </script>

</body>
</html>