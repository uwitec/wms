<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8"/>
    <title>角色列表 - 新增</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/user.css?t=201603211901">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/bootstrap-treeview.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/bootstrap-treeview.js"></script>
    <style>
        ul.ztree {margin-top: 10px;border: 1px solid #617775;background: #f0f6e4;width:220px;height:200px;overflow-y:scroll;overflow-x:auto;}
        .newBtn {
/*            position: relative;
           left: 15%; */
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
		.resourceList{
			border: 1px solid #dbdbdb;
			height: 700px;
			overflow: hidden;
			position: relative;
		}
		.resourceList .leftTree{
			float: left;
			width: 40%;
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
		label{
			font-weight: normal;
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
		
		.lq-form-group label{
			display: inline;
			padding: 3px;
			margin-right: 10px;
			width: 100px;
		}
		.lq-form-group input[type=text] {
			width: 30%;
			display:inline-block;
		}
    </style>

</head>
<body>

    <form:form method="post" commandName="sysRole" id="form-sysRole-edit">
        <form:hidden path="id"/>
        <form:hidden path="available"/>
		<div class="lq-col-12" style="margin: 10px;">
			<div class="lq-form-group">
	            <form:label path="name" style="margin-left:20px;">角色名</form:label>
	            <form:input path="name" id="name" class="lq-form-control"/>
	            
	            <form:label path="description" style="margin-left: 15px;">角色描述</form:label>
	            <form:input path="description" id="description" class="lq-form-control"/>	
	            
	            <input type="submit" class="lq-btn lq-btn-sm lq-btn-primary newBtn" name="${op}" value="${op}"/>		
			</div>
			<div class="lq-form-group">
		       	<div class="module row">
					<h3 style="font-size: 14px;">拥有的资源列表</h3>
					<div class="resourceList">
						<div class="leftTree">
							<div id="tree"></div>
						</div>
						<div class="rightShow">
							<div id="list"></div>
						</div>
					</div>
				</div>			
			</div>			
		</div>
 		<input type="hidden" name="resourceName" id="resourceName" >
 		<input type="hidden" name="resource_ids" id="resource_ids">

    </form:form>
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
	    $("#form-sysRole-edit").submit(function(){
    		var resource_ids="";
    		var resource_names="";
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
	        var flag = 1 , error = "";
	        if ($("#name").val() == "") {
	            flag = 0;
	            error = "角色名不能为空";
	        }
	        if ($("#description").val() == "") {
	            flag = 0;
	            error = "角色描述不能为空";
	        }
	        if (flag) {
	            return true;
	        } else {
	            alert(error);
	            return false;
	        }
	    });    
    </script>

</body>
</html>