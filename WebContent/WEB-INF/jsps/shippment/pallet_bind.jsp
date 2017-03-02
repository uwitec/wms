<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>码托绑定</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
  <style type="text/css">
    input{
        height:1.5em;
    }
    .note{
        display:none;margin-bottom:20px;
    }
    .th{
        display:inline-block;font-weight: bold;background-color: #3295DD; color: white; height: 30px;line-height:30px;width:200px;text-align:center;float:left;background:#3295DD;border:1px solid #A9A9A9;color:#fff;font-size:16px;letter-spacing:2px;border-bottom:0px;
    }
    #table table{
        border-collapse: collapse;border:none;width:404px;
    }
    #table td{
        border: solid #A9A9A9 1px;width:150px;height:25px;text-align:center;
     }
    #table tr{width:300px;}
    .note-pre{
        font-weight: bold;font-size: 14px;margin-left:30px;
    }
    #form label {
        width: 7em;
        text-align: right;
    }
  </style>

</head>
<body>
<div class="main-container">
    <form method="post" class="lq-form-inline" id="form"> 
        <div class="lq-form-group">
            <label for='pallet_no'>扫描码托条码:</label>
            <input type="text" id="pallet_no" value="" size="39" class="lq-form-control" />
        </div>
        <div class="lq-form-group">
            <label for="tracking_no">扫描运单号:</label>
            <input type="text" id="tracking_no" value="" size="39" class="lq-form-control" />
        </div>
    </form>
    <div id="table_note">
        <div class="note" >
            <span class="note-pre">码托条码:</span><span class="pallet_td"></span>
            <span class="note-pre">快递方式:</span><span class="ship_td"></span>
            <span class="note-pre">绑定数量:</span><span class="num_td"></span>
        </div>
        <div>
            <div class="table_th" style="overflow:hidden;display:none;">
                <span class="th">快递单号</span>
                <span class="th">状态</span>
            </div>
            <div id="table" style=""></div>
        </div>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
  <script type="text/javascript">

    $(document).ready(function(){
        // 绑定滑动效果
        $('legend.expand').bind('click', function(event){
            $(this).next().slideToggle('normal');
        });
        // 确定页面载入时鼠标焦点的定位
        $('#pallet_no').focus();  // 定位到收货容器扫描框
        // 绑定工牌号扫描事件
        $('#pallet_no').bind('keyup', listen);
        // 绑定波次单扫描事件
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
        if(input_id == 'pallet_no') {
            pallet_no_scan(event,barcode,input_id);
        } else if(input_id == 'tracking_no') {
            var pallet_no = $.trim($("#pallet_no").val());
            tracking_no_scan(event,barcode,pallet_no,input_id);
        }
    } 
}
   //码托检测
   var flag_from_pallet = true;
   function pallet_no_scan(event,barcode,input_id){
        var pallet_sn = barcode;
        var $note = $('#'+input_id+'_note');
        if (!flag_from_pallet) {
            var tip = "当前操作还在进行...请稍等";
            return; 
        }
        flag_from_pallet = false;
        var tip = "正在验证中...请稍等";
        //检查码托
        var res_check = check_pallet_sn(pallet_sn);
        if(res_check){
             if($("#pallet_no_note").html()!=""){
                $("#pallet_no_note").html("");
             }
             $("input:eq(1)").focus();
        }else{
            $("input:eq(0)").val("");
             flag_from_pallet = true;
             return false;
        } 
        
        flag_from_pallet = true;
        
   }
 //ajax 码托检测
    function check_pallet_sn(pallet_sn){
         //alert('开始ajax请求');
         if(pallet_sn ==''){
             alert("码托为空！");
             $("#table").html("");
             return false;
         }
         var result = [];
         $.ajax({
             mode: 'abort',
             async : false,
             type: 'POST',
             dataType: 'json',
             url : 'checkPalletSn?', 
             data: 'pallet_sn=' + pallet_sn,
             success: function(data) {
                 if(data.success){
                    //console.log(data);
                    $(".pallet_td").html("");
                    $(".ship_td").html("");
                    $("#table").html("");
                    result['success'] =  data.success;
                    if(data.num!=0){
                        autocreate(data.num,data.ship);
                        $(".note").css("display","block");
                        $(".pallet_td").html(pallet_sn);
                        $(".ship_td").html(data.way);
                        $(".num_td").html(data.num);
                    } else{
                        $(".note").css("display","none");
                        $(".table_th").css("display","none");
                    }
                 }else{
                     result = false;
                     alert(data.error);
                     $("#table").html("");
                     $(".note").css("display","none");
                     $(".table_th").css("display","none");
                     $("input").val("");
                     $("#pallet_no_note").html("");
                 } 
             },
             error: function(data) {
                 result = false;
                 alert('ajax请求错误, 请重新扫描条码:' + pallet_sn); 
                 $(".note").css("display","none");
                 $("#table").html("");
             }
         });
         return result;
    }
    

 //创建table表格
function autocreate(num,shipment_id){
    var table=document.createElement("table");
     table.setAttribute("border","1");
     table.setAttribute("background","red");
     //获取行数值
     var line= num;
    //获取列数值
     var list=2;
    for(var i=0;i<line;i++){
        //创建tr
        var tr=document.createElement("tr");
    
        //创建td
        var td=document.createElement("td");
        td.innerHTML=shipment_id[i].tracking_number;
        tr.appendChild(td);
        var td=document.createElement("td");
        td.innerHTML="已绑定";
        tr.appendChild(td);
        table.appendChild(tr); 
    }
    document.getElementById("table").appendChild(table);
    $(".table_th").css("display","block");
}

    //运单号检测
   var flag_tracking_no = true;
   function tracking_no_scan(event,barcode,pallet_no,input_id){
        var tracking_no = barcode;
        var pallet_no = pallet_no;
        // alert(KEY_MAP[event.keyCode]);
        var $note = $('#'+input_id+'_note');
        if (!flag_tracking_no) {
            var tip = "当前操作还在进行...请稍等";
            return; 
        }
        flag_tracking_no = false;
        var tip = "正在验证中...请稍等";
        //检查工牌号
        var res_check = check_tracking_sn(tracking_no,pallet_no);
        if(res_check){
             $("input:eq(1)").val("");
 //            $("input:eq(0)").focus();

        }else{
             $note.val('');
             flag_tracking_no = true;
             return false;
        } 
        
        flag_tracking_no = true;

   }
    //ajax 运单号检测,如果存在则更新
    function check_tracking_sn(tracking_no,pallet_no){
          if(pallet_no ==''){
             alert("码托条码不能为空！");
              $("input:eq(0)").focus();
              $("input").val("");
             return false;
         }
         if(tracking_no ==''){
             alert("运单号为空！");
             return false;
         }
         var result = "";
         $.ajax({
             mode: 'abort',
             async : false,
             type: 'POST',
             dataType: 'json',
             url : 'checkTrackingSn?', 
             data:{'tracking_no':tracking_no,'pallet_no':pallet_no} ,
             success: function(data) {
                 if(data.success){
                    $(".pallet_td").html("");
                    $(".ship_td").html("");
                    $("#table").html("");
                    result = data.success;
                    autocreate(data.num,data.ship);
                    $(".note").css("display","block");
                    $(".pallet_td").html(pallet_no);
                    $(".ship_td").html(data.way);
                    $(".num_td").html(data.num);
                    $("input:eq(1)").focus();
                 }else{
                     result = false;
                     alert(data.error);
                     if(data.error_id == 1){//码托开始存在，在绑定过程中，被删除
                        $("input").val("");
                        $("input:eq(0)").focus();
                     } else if(data.error_id == 2){//运单号已发货或已未复核
                        $("input:eq(1)").val("");
                        $("input:eq(1)").focus();
                     }else if(data.error_id == 3){  //运单号不存在
                        $("input:eq(1)").val("");
                        $("input:eq(1)").focus();
                     }
                     else if(data.error_id == 4){  //未称重
                         $("input:eq(1)").val("");
                         $("input:eq(1)").focus();
                      }
                 } 
             },
             error: function() {
                 result = false;
                  $("input:eq(1)").val("");
                 alert('ajax请求错误, 请重新扫描条码:'); 
             }
         });
         return result;
    }
    
    </script>
</body>
</html>
