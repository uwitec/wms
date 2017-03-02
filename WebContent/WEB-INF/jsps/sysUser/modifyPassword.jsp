<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>密码修改</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/user.css">
     <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
     <style type="text/css">
    .newBtn {
        margin: 10px 0 0 28px;
    }
    </style>
</head>
<body>
    <div class="main-container">
        <form method="post" id="form-modify-password" class="lq-form-inline">
            <div class="form-group">
                <label for="newPassword">新密码:</label>
                <input type="text" id="newPassword" name="newPassword" class="lq-form-control" />
            </div>
            <input type="submit" value="修改" class="lq-btn lq-btn-sm lq-btn-primary newBtn">
        </form>
    </div>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script>
        $(document).ready(function(){
            $("#form-modify-password").submit(function(){
                var flag = 1;
                if ($("#newPassword").val() == "") {
                    flag=0;
                }
                if (flag) {
                    return true;
                } else {
                    alert("密码不能为空");
                    return false;
                }
            });
        });
    </script>
</body>
</html>