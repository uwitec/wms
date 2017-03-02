<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Leqee WMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/normalize.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/index.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/jquery.mCustomScrollbar.min.css">
	<link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
</head>
<body>
	<div id="main-wrap">
		<div id="header-wrap">
			<div id="logo-wrap">
				<img src="${pageContext.request.contextPath}/static/images/logo-shadow.png" alt="Leqee" id="logo">
			</div>
			<ul id="header-nav">
				<li><span>${sessionScope.currentUser.realname}(<shiro:principal/>)</span></li>
				<li><span><a href="${pageContext.request.contextPath}/sysUser/${sessionScope.currentUser.id}/personalSetting" target="content-frame">个人设置</a></span></li>
				<li><span><a href="${pageContext.request.contextPath}/sysUser/${sessionScope.currentUser.id}/modifyPassword" target="content-frame">密码修改</a></span></li>
				<li><span><a href="${pageContext.request.contextPath}/logout2">退出</a></span></li>
			</ul>
			<div id="party-select">
					<span id="party-name-wrap">
						<span id="party-name">当前仓库:&nbsp;&nbsp;&nbsp;${sessionScope.currentPhysicalWarehouse.warehouse_name}</span>
					</span>
			</div>
		</div>
		<div id="menu-wrap">
			<ul class="multilevel-menu" id="left-menu">
				<c:forEach items="${menus}" var="m">
				<li class="parent-level">
					<p class="li-title parent-li-title">
						${m.name}<i class="fa fa-angle-right ar"></i>
					</p>
				    <ul class="child-menu first-child-menu">
					    <c:forEach items="${m.childSysResourceList}" var="n">
					    <p class="li-title first-child last-child">
							<a href="${pageContext.request.contextPath}/${n.url}" target="content-frame">${n.name}</a>
						</p>
					    </c:forEach>
				    </ul>
				<li>
				</c:forEach>
			</ul>
		</div>
		<div id="content-wrap">
			<iframe src="${pageContext.request.contextPath}/welcome" width="100%" height="100%" frameborder="0" id="content_frame" name="content-frame"></iframe>
		</div>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.mCustomScrollbar.concat.min.js"></script>
	<script>
		$(document).ready(function(){
			$("#content-wrap").height($("#content-wrap").height()-45);
			$("#menu-wrap").height($("#menu-wrap").height()-50);
	        $("#menu-wrap").mCustomScrollbar({
	        	theme: "minimal-dark",
	        	scrollInertia: 100
	        });

	        $(".multilevel-menu .li-title").on("click",function(e){
				e.stopPropagation();
				if ($(this).hasClass("last-child")) {
					$(this).addClass("active").siblings().removeClass("active");
				}else {
					$(this).toggleClass("active").siblings(".child-menu").eq(0).stop().slideToggle(350)
					.end()
					.parents(".parent-level").siblings().find(".child-menu").stop().slideUp(350);
				}
				$(this).parents(".parent-level").siblings().find(".li-title").removeClass("active");
			});
	        
		});
	</script>

</body>
</html>