<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
    <title>批量单补打面单</title>
    <link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <style>
    #print{
        border: 0px;
        width: 0px;
        height: 0px;
    }
    input[type="text"] {
    	height: 30px;
    }
    .pageIn {
    	display: none;
    }
    .note {
    	font-size: 14px;
    }
    .warning {
    	color: #f00; 
    	display:none;
    }
    </style>
</head>
<body>
    <iframe  src=""  id="print"></iframe>
    <div class="main-container">
        <div class="lq-form-inline">
            <div class="lq-row">
                <div class="lq-col-6">
                    <div class="lq-form-group">
                        <label for="batch_pick_sn">扫描波次单号:</label>
                        <input type="text" name="batch_pick_sn" id="batch_pick_sn" class="lq-form-control">
                        <span id="packNum" class="note"></span>
                    </div>
                    <div class="pageIn">
	                    <div class="lq-form-group">
	                    	<label for="start">开始打印页码:</label>
	                    	<input type="text" id="start_num"  class="lq-form-control" onkeyup="this.value=this.value.replace(/[^\d]/g,'') " placeholder="不小于1的整数" >
	                    	<span class="warning"></span>
	                    </div>
	                    <div class="lq-form-group">
	                    	<label for="start">结束打印页码:</label>
	                    	<input type="text" id="end_num"  class="lq-form-control" onkeyup="this.value=this.value.replace(/[^\d]/g,'') " placeholder="不能超过总快递面单数">
	                    	<span class="warning"></span>
	                    </div>
	                    <div class="lq-forn-group">
	                    	<button class="lq-btn lq-btn-primary" id="printer">打印</button>
	                    </div>
                    </div>                 
                </div>
            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
    <script>
    $(document).ready(function(){
        //扫描波次单号
        var END_PAGE,BATCH_PAICK_ID;
        $("#batch_pick_sn").focus().on("keyup",function(e){
            e.preventDefault();
            if (e.which == 13 || e.which == 17) {
                var that = $(this),
                	batch_pick_sn = $.trim(that.val());
                $.ajax({
                    url : 'reLoadBatchPickSnForRePrint',
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
                            $(".pageIn").fadeIn(300);
                            END_PAGE=data.packNum;
                            BATCH_PAICK_ID=data.batchPickId;
                            $("#packNum").text("总快递面单数为:"+data.packNum);
                            
                        } else {
                            alert(data.note);
                            that.focus().val("");
                            $(".pageIn").hide();
                        }
                    },
                    error : function (error) {
                        that.attr('disabled',false);
                        console.log('接口调取失败');
                    }
                });
                
            }
        });
        
        

        $("#start_num").blur(function(){
        	var start=$(this).val();
        	var warning=$(this).next("span");
        	var oldWarning=warning.text();
        	console.log(oldWarning);
			if(start==""){
				warning.text("起始页码不能为空");
				warning.fadeIn(300);
				$(this).focus();
			}else if(parseInt(start)<1){
				warning.text("起始页码不小于1");
				$(this).focus();
				warning.fadeIn(300);
			}else {
				warning.fadeOut(100);
				warning.text(oldWarning);
			}
        	
        })
        
        //结束框输入校验
        $("#end_num").blur(function(){
        	var end=$(this).val();
        	var warning=$(this).next("span");
			if(end==""){
				warning.text("结束页码不能为空");
				warning.fadeIn(300);
				$(this).focus();
				
			}else if(parseInt(end)>parseInt(END_PAGE)){
				warning.text("结束页码不能超过总页码");
				warning.fadeIn(300);
				$(this).focus();
				
			}else {
				warning.fadeOut(100);
				warning.text("");
			}
        })
        
        //打印按钮
        $("#printer").click(function(){
        	var start_num=$("#start_num").val();
        	var end_num=$("#end_num").val();
        	if(start_num&&end_num&&start_num>=1&&end_num<=parseInt(END_PAGE)){
	        	if(parseInt(start_num)<=parseInt(end_num)){
		            src="batchPrint?batch_pick_id="+BATCH_PAICK_ID+"&start_num="+start_num+"&end_num="+end_num;
		            $('#print').attr('src',src); 
		            $("#batch_pick_sn").focus().val("");
		            $(".pageIn").hide();
		            $("#packNum").text("");
		            $("input[type=text]").val("");
	        	}else{
	        		alert("起始页码不能大于结束页码");
	        	}
        	}else{
        		 $("#end_num").trigger("blur");
        		 $("#start_num").trigger("blur");
        	}
        })
        
    });
    </script>
</body>
</html>
