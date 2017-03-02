<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>用户列表</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/sysUser.css">
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
</head>
<body>
<div id="sysUser-list-container">
    <c:if test="${not empty msg}">
        <div class="message">${msg}</div>
    </c:if>

    <shiro:hasPermission name="sys:user:create">
        <a href="${pageContext.request.contextPath}/sysUser/create" class="lq-btn lq-btn-primary" id="sysUser-btn-add"><i class="fa fa-plus"></i>用户新增</a>
    </shiro:hasPermission>

    <table class="lq-table">
        <thead>
            <tr>
                <th>用户名</th>
                <th>真实姓名</th>
                <th>部门</th>
                <th>邮箱</th>
                <th>加入时间</th>
                <th>上次登录时间</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${sysUserList}" var="sysUser">
            <c:if test="${sysUser.available eq true}">
                <tr>
                    <td>${sysUser.username}</td>
                    <td>${sysUser.realname}</td>
                    <td>${sysUser.department}</td>
                    <td>${sysUser.email}</td>
                    <td><fmt:formatDate value="${sysUser.created_time}" pattern="yyyy/MM/dd  HH:mm:ss" /></td>
                    <td><fmt:formatDate value="${sysUser.last_login_time}" pattern="yyyy/MM/dd  HH:mm:ss" /></td>
                    <td>
                        <shiro:hasPermission name="sys:user:update">
                            <a href="${pageContext.request.contextPath}/sysUser/${sysUser.id}/update" class="lq-btn lq-btn-sm lq-btn-success"><i class="fa fa-edit"></i>修改</a>
                        </shiro:hasPermission>

                        <shiro:hasPermission name="sys:user:delete">
                            <a href="${pageContext.request.contextPath}/sysUser/${sysUser.id}/delete" onclick="return confirm('确认删除吗？')" class="lq-btn lq-btn-sm lq-btn-danger"><i class="fa fa-close"></i>删除</a>
                        </shiro:hasPermission>

                        <shiro:hasPermission name="sys:user:update">
                            <a href="${pageContext.request.contextPath}/sysUser/${sysUser.id}/changePassword" class="lq-btn lq-btn-sm lq-btn-info"><i class="fa fa-cog"></i>重置密码</a>
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