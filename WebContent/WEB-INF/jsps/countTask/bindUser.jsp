<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>分配任务</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link href="https://cdn.bootcss.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
</head>
<body>
    <div class="main-container">
        <form method="post" class="lq-form-inline">
		    <div class="lq-form-group"> 
				<select name="type" id="type" class="lq-form-control">
					<option value="1" selected="true">波次单</option>
					<option value="2">盘点任务单</option>
				</select>
			</div>
				
            <div class="lq-form-group">
                <label for="employee_no">工牌号:</label>
                <input type="text" class="lq-form-control" id="employee_no" name="employee_no">
            </div>
            <div class="lq-form-group">
                <label for="batch_task">任务编号:</label>
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
                employee_no_scan(event,barcode,input_id);
            } else if(input_id == 'batch_task') {
                var employee_no = $.trim($("#employee_no").val());
                batch_task_scan(event,barcode,employee_no,input_id);
            }
        } 
        else {
            var note = '#'+input_id+'_note';
            $(note).val("条码不能为空");
        }
    }

    /**
    * 工牌号检测
    */
    var flag_from_employee = true;
    function employee_no_scan(event,barcode,input_id){
        var batch_employee_sn = barcode;
        var $note = $('#'+input_id+'_note');
        if (!flag_from_employee) {
            var tip = "当前操作还在进行...请稍等";
            return; 
        }
        flag_from_employee = false;
        var tip = "正在验证中...请稍等";
        //检查工牌号
        var res_check = check_batch_employee_sn(batch_employee_sn,barcode);
        if(res_check){
             $note.html("姓名：" + res_check["employee_name"] + "&nbsp;&nbsp;&nbsp;&nbsp;工牌号：" + res_check["employee_no"] );
             if($("#batch_task_note").html()!=""){
                $("#batch_task_note").html("");
             }
             $("input:eq(1)").focus();
        }else{
             $note.val('');
             flag_from_employee = true;
             return false;
        } 
        
        flag_from_employee = true;
        
    }

    //ajax 工牌号检测
    function check_batch_employee_sn(batch_employee_sn,barcode){
         //alert('开始ajax请求');
         if(batch_employee_sn ==''){
             alert("工牌号为空！");
             return false;
         }
         var result = [];
         $.ajax({
             mode: 'abort',
             async : false,
             type: 'POST',
             dataType: 'json',
             url : 'checkEmployeeSn?', 
             data: 'batch_employee_sn=' + batch_employee_sn,
             success: function(data) {
                 if(data.success){
                    result['success'] =  data.success;
                    result['employee_name'] = data['employee_name'];
                    result['employee_no'] = data['employee_no'];
                 }else{
                     result = false;
                     alert(data.error);
                     $("input").val("");
                     $("#employee_no_note").html("");
                 } 
             },
             error: function(data) {
                 result = false;
                 alert('ajax请求错误, 请重新扫描条码:' + barcode); 
             }
         });
         return result;
    }

    //波次单检测
    var flag_batch_task = true;
    function batch_task_scan(event,barcode,employee_no,input_id){
        var batch_task_sn = barcode;
        var employee_no = employee_no;
        // alert(KEY_MAP[event.keyCode]);
        var $note = $('#'+input_id+'_note');
        if (!flag_batch_task) {
            var tip = "当前操作还在进行...请稍等";
            return; 
        }
        flag_batch_pick = false;
        var tip = "正在验证中...请稍等";
        //检查工牌号
        var res_check = check_batch_task_sn(batch_task_sn,employee_no,barcode);
        
        if(res_check){
             $note.text("工牌号与盘点任务单号绑定成功！");
             $("input").val("");
             $("input:eq(0)").focus();
             $("#employee_no_note").html("");
             $("#employee_no_note").html("");

        }else{
             $note.val('');
             flag_batch_task = true;
             return false;
        } 
        
        flag_batch_task = true;

    }

    //ajax 波次单检测,如果存在则更新
    function check_batch_task_sn(batch_task_sn,employee_no,barcode){
         if(employee_no ==''){
             alert("工牌号不能为空！");
              $("input:eq(0)").focus();
              $("input").val("");
             return false;
         }
         if(batch_task_sn ==''){
             alert("盘点单号为空！");
             return false;
         }
         var url='bindUser?';
         if($('#type').val()=='1'){
        	 url='../batchPick/bindUser?';
         }
         var result = "";
         $.ajax({
             mode: 'abort',
             async : false,
             type: 'POST',
             dataType: 'json',
             //url : 'bindUser?', 
             url :url,
             data:{'batch_task_sn':batch_task_sn,'employee_no':employee_no,'batch_pick_sn':batch_task_sn} ,
             success: function(data) {
                 if(data.success){
                     result = data.success;
                 }else{
                     result = false;
                     alert(data.error);
                     if(data.error_id == 1){//工牌号开始存在，在绑定过程中，被删除
                        $("input").val("");
                        $("input:eq(0)").focus();
                        $("#employee_no_note").html("");
                     } else if(data.error_id == 2){//波次单号不存在！
                        $("input:eq(1)").val("");
                        $("#batch_task_note").html("");
                        $("input:eq(1)").focus();
                     }else if(data.error_id == 3){//工牌号存在&&波次单号存在,波次号对应选项有值,但是工牌号没有被删除
                        $("input").val("");
                        $("#employee_no_note").html("");
                        $("input:eq(0)").focus();
                     }
                 } 
             },
             error: function() {
                 result = false;
                  $("input:eq(1)").val("");
                 alert('ajax请求错误, 请重新扫描条码:' + barcode); 
             }
         });
         return result;
    }
    </script>
</body>
</html>
