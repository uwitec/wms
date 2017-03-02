<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>码托解绑</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style type="text/css">
    input{
        height:1.5em;
    }
    </style>
</head>
<body>
    <div class="main-container">
        <form method="post" id="form" class="lq-form-inline"> 
            <div class="lq-form-group">
                <label for="tracking_no">扫描运单号:</label>
                <input type="text" id="tracking_no" value="" size="39" class="lq-form-control" />
            </div>
        </form>
    </div>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script type="text/javascript">
    $(document).ready(function(){
        // 绑定滑动效果
        $('legend.expand').bind('click', function(event){
            $(this).next().slideToggle('normal');
        });
        // 确定页面载入时鼠标焦点的定位
        $('#tracking_no').focus();  // 定位到收货容器扫描框
        // 解绑波次单扫描事件
        $('#tracking_no').bind('keyup', listen);

    });
     var KEY = {
        RETURN: 13,  // 回车
        CTRL: 17,    // CTRL
        TAB: 9
    };
    /**
     * 监听
     */
    function listen(event) 
    {
        var that = $(this);
        switch (event.keyCode) {
            case KEY.RETURN:
            case KEY.CTRL:
                scan(event,that);
                event.preventDefault();
                break;
        }
    }
    
/**
 * 扫描动作
 */
function scan(event,that)
{ 
    var input = that,
    barcode = $.trim(input.val()),
    input_id = input.attr('id');
    if (barcode != '') {
        if(input_id == 'tracking_no') {
            tracking_no_scan(event,barcode);
        }
    } 
}
   
 
    
    //运单号检测
   var flag_tracking_no = true;
   function tracking_no_scan(event,barcode){
        var tracking_no = barcode;
        // alert(KEY_MAP[event.keyCode]);
        if (!flag_tracking_no) {
            var tip = "当前操作还在进行...请稍等";
            return; 
        }
        flag_tracking_no = false;
        var tip = "正在验证中...请稍等";
        //检查工牌号
        var res_check = check_tracking_sn(tracking_no);
        if(res_check){
             $("input").val("");
             $("input:eq(0)").focus();
        }else{
             flag_tracking_no = true;
             return false;
        } 
        
        flag_tracking_no = true;

   }
    //ajax 运单号检测,如果存在则更新
    function check_tracking_sn(tracking_no){      
         if(tracking_no ==''){
             alert("运单号不能为空！");
             return false;
         }
         var result = "";
         $.ajax({
             mode: 'abort',
             async : false,
             type: 'POST',
             dataType: 'json',
             url : 'checkTrackingUnbind?', 
             data:{'tracking_no':tracking_no} ,
             success: function(data) {
                 if(data.success){
                     result = data.success;
                      alert("解绑成功！");
                 }else{
                     result = false;
                     alert(data.error);
                    $("input").val("");
                    $("input:eq(0)").focus();
                 } 
             },
             error: function() {
                 result = false;
                  $("input:eq(1)").val("");
                 alert('ajax请求错误, 请重新扫描运单号:'); 
             }
         });
         return result;
    }

    </script>
</body>
</html>

