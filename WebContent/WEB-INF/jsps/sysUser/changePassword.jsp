<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>密码修改</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/user.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/bootstrap.min.css">
    <style type="text/css">
    .newBtn {
        margin: 0 0 0 28px;
    }
    </style>
</head>
<body>
<div class="main-container">
    <form method="post" id="form-edit" class="lq-form-inline">
        <div class="lq-form-group">
            <label for="newPassword">新密码:</label>
            <input type="text" id="newPassword" name="newPassword"/>
            <input type="submit" class="lq-btn lq-btn-sm lq-btn-primary newBtn" value="${op}">
        </div>
    </form>
</div>
</body>
</html>