<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>WEB分配补货任务单</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
</head>
<body>
    <div class="main-container">
        <form method="post" class="lq-form-inline">
            <div class="lq-form-group">
                <label for="employee_no">工牌号:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                <input type="text" class="lq-form-control" id="employee_no" name="employee_no">
            </div>
            <div class="lq-form-group">
                <label for="batch_task">补货批次号:</label>
                <input type="text" class="lq-form-control" id="batch_task" name="batch_task">
            </div>
        </form>

        <div class="lead"  id="employee_no_note"></div>
        <div class="lead" id="batch_task_note"></div>
    </div>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script type="text/javascript">
    $(document).ready(function(){
        // 绑定滑动效果
        $('legend.expand').bind('click', function(event){
            $(this).next().slideToggle('normal');
        });
        // 确定页面载入时鼠标焦点的定位
        $('#employee_no').focus();  // 定位到收货容器扫描框
        // 绑定工牌号扫描事件
        $('#employee_no').bind('keyup', listen);
        // 绑定波次单扫描事件
        $('#batch_task').bind('keyup', listen);

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
            if(input_id == 'employee_no') {
                employee_no_scan(barcode);
            } else if(input_id == 'batch_task') {
                var employee_no = $.trim($("#employee_no").val());
                batch_task_scan(barcode,employee_no);
            }
        }else {
            var note = '#'+input_id+'_note';
            $(note).val("输入内容不能为空");
        }
    }

    /**
    * 工牌号检测
    */
    function employee_no_scan(employee_no){
        $.ajax({
            mode: 'abort',
            async : false,
            type: 'POST',
            dataType: 'json',
            url : 'checkEmployeeNo?', 
            data: 'employee_no=' + employee_no,
            success: function(data) {
                if(data.success){
                	$('#employee_no_note').html("姓名：" + data.employee_name+ "&nbsp;&nbsp;&nbsp;&nbsp;工牌号：" + data.employee_no);
                    $("#batch_pick_note").html("");
                    $("input:eq(1)").focus();
                }else{
                    alert(data.note);
                    $("input").val("");
                    $("#employee_no_note").html("");
                } 
            },
            error: function(data) {
            	$('#employee_no_note').val('');
                alert('请求错误, 请重新扫描:' + employee_no); 
                return false;
            }
        });
    }


    //波次单检测
    function batch_task_scan(batch_task,employee_no){
    	if(employee_no ==''){
            alert("工牌号不能为空！");
             $("input:eq(0)").focus();
             $("input").val("");
            return false;
        }
        if(batch_task ==''){
            alert("补货批次号不能为空！");
            return false;
        }
        $.ajax({
            mode: 'abort',
            async : false,
            type: 'POST',
            dataType: 'json',
            url : 'bindBatchTaskUser?', 
            data:{'batch_task_sn':batch_task,'employee_no':employee_no} ,
            success: function(data) {
                if(data.success){
                	$("#batch_task_note").text("工牌号与补货批次号绑定成功！");
                    $("input").val("");
                    $("input:eq(0)").focus();
                    $("#employee_no_note").html("");
                }else{
                    alert(data.note);
                    if(data.note_id == 1 || data.note_id ==3){
                    	//工牌号开始存在，在绑定过程中，被删除
                    	//工牌号存在&&补货批次号存在,补货批次号对应选项有值,但是工牌号没有被删除
                       $("input").val("");
                       $("#employee_no_note").html("");
                       $("input:eq(0)").focus();
                    } else if(data.note_id == 2){//补货批次号不存在！
                       $("input:eq(1)").val("");
                       $("#batch_task_note").html("");
                       $("input:eq(1)").focus();
                    }
                } 
            },
            error: function() {
                $("input:eq(1)").val("");
                alert('请求错误, 请重新扫描:' + batch_task); 
                return false;
            }
        });
    }
    </script>
</body>
</html>
