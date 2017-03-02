<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
    <title>双11预案--补打快递面单</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style>
    #print{
        border: 0px;
        width: 0px;
        height: 0px;
    }
    </style>
</head>
<body>
    <iframe  src=""  id="print"></iframe>
    <div class="main-container">
        <div class="lq-form-inline">
            <div class="lq-row">
                <div class="lq-col-5">
                    <div class="lq-form-group">
                        <label for="batch_pick_sn">扫描波次单号:</label>
                        <input type="text" name="batch_pick_sn" id="batch_pick_sn" class="lq-form-control">
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script>
    $(document).ready(function(){
        //扫描波次单号
        $("#batch_pick_sn").focus().on("keyup",function(e){
            e.preventDefault();
            if (e.which == 13 || e.which == 17) {
                var that = $(this),
                	batch_pick_sn = $.trim(that.val());
                $.ajax({
                    url : 'reLoadTrickBatchPickSn',
                    dataType : 'json',
                    type : 'post',
                    data : {
                        batch_pick_sn : batch_pick_sn
                    },
                    beforeSend : function () {
                        that.attr('disabled',true);
                    },
                    success : function (data) {
                        that.attr('disabled',false);
                        console.log(data);
                        if (data.result == 'success') {
                            src="batchPrint?batch_pick_id="+data.batch_pick_id;
                            $('#print').attr('src',src); 
                            that.focus().val("");
                        } else {
                            alert(data.note);
                            that.focus().val("");
                        }
                    },
                    error : function (error) {
                        that.attr('disabled',false);
                        console.log('接口调取失败');
                    }
                });
                
            }
        });
    });
    </script>
</body>
</html>
