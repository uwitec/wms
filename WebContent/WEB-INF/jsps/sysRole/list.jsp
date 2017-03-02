<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>角色列表</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/sysUser.css">
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
</head>
<body>
<div id="sysRole-list-container">
<c:if test="${not empty msg}">
    <div class="message">${msg}</div>
</c:if>

<shiro:hasPermission name="sys:role:create">
    <a href="${pageContext.request.contextPath}/sysRole/create" class="lq-btn lq-btn-primary" id="sysRole-btn-add"><i class="fa fa-plus"></i>角色新增</a><br/>
</shiro:hasPermission>
<table class="lq-table">
    <thead>
        <tr>
            <th>角色名称</th>
            <th>角色描述</th>
            <th>操作</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${sysRoleList}" var="sysRole">
        <c:if test="${sysRole.available eq true}">
            <tr>
                <td>${sysRole.name}</td>
                <td>${sysRole.description}</td>
                <td>
                    <shiro:hasPermission name="sys:role:update">
                        <a href="${pageContext.request.contextPath}/sysRole/${sysRole.id}/update" class="lq-btn lq-btn-sm lq-btn-success">修改</a>
                    </shiro:hasPermission>

                    <shiro:hasPermission name="sys:role:delete">
                        <a href="${pageContext.request.contextPath}/sysRole/${sysRole.id}/delete" onclick="return confirm('确认删除吗？')" class="lq-btn lq-btn-sm lq-btn-danger">删除</a>
                    </shiro:hasPermission>
                </td>
            </tr>
        </c:if>
        </c:forEach>
    </tbody>
</table>
</div>
</body>
</html>