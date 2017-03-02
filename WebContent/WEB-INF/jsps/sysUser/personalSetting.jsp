<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>个人设置</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/user.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style>
        ul.ztree {margin-top: 10px;border: 1px solid #617775;background: #f0f6e4;width:220px;height:200px;overflow-y:scroll;overflow-x:auto;}
        .newBtn {
            margin: 10px 0 0 28px;
        }
        .form-group label {
            width: 8em;
        }
    </style>
</head>
<body>
    <div class="main-container">
        <form:form method="post" commandName="sysUser" id="form-personal-settings" class="lq-form-inline">
            <form:hidden path="id"/>
            <form:hidden path="salt"/>
            <form:hidden path="available"/>
            <form:hidden path="created_time"/>
            <form:hidden path="last_login_time"/>
            <form:hidden path="role_ids"/>
            <form:hidden path="resource_ids"/>
            <form:hidden path="customer_ids"/>
            <form:hidden path="warehouse_ids"/>
            <form:hidden path="password"/>
            
            <div class="form-group">
                <label><form:label path="username">用户名(工牌号):</form:label></label>
                <form:input path="username" readonly="true" class="lq-form-control"/>
            </div>

            <div class="form-group">
                <label><form:label path="realname">真实姓名:</form:label></label>
                <form:input path="realname" id="realname" class="lq-form-control"/>
            </div>
            
            <div class="form-group">
                <label><form:label path="email">邮箱:</form:label></label>
                <form:input path="email" class="lq-form-control"/>
            </div>
            
            <div class="form-group">
                <label><form:label path="department">分组（部门）:</form:label></label>
                <form:input path="department" class="lq-form-control"/>
            </div>
            
            <input type="submit" value="更新" class="lq-btn lq-btn-sm lq-btn-primary newBtn"/>

        </form:form>
    </div>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script>
        $(document).ready(function(){
            $("#form-personal-settings").submit(function(){
                var flag = 1;
                if ($("#realname").val() == "") {
                    flag=0;
                }
                if (flag) {
                    return true;
                } else {
                    alert("真实姓名不能为空");
                    return false;
                }
            });
        });
    </script>
</body>
</html>