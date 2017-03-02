<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>绑定运单号</title>
<link href="<%=request.getContextPath() %>/static/css/default.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath() %>/static/css/autocomplete.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="../static/js/jquery/jquery.js"></script>
<script type="text/javascript" src="../static/js/jquery/jquery.ajaxQueue.js"></script>
<script type="text/javascript" src="../static/js/autocomplete.js"></script>
<script type="text/javascript" src="../static/js/zapatec/utils/zapatec.js"></script>
<script type="text/javascript" src="../static/js/zapatec/zpcal/src/calendar.js"></script>
<script type="text/javascript" src="../static/js/zapatec/zpcal/lang/calendar-en.js"></script>
<link rel="stylesheet" href="../static/js/zapatec/zpcal/themes/winter.css" />

  <style type="text/css">
    <!--{literal}-->
    .tip {
        color: #888;
    }
    .require {
        color: #F00;
    }
    .loading {
        background: #F1F1F1 url('misc/indicator.gif') right center no-repeat;
    }
    <!--{/literal}-->
  </style>
  
  <script type="text/javascript">
    // <![CDATA[
    
    var ORDER_SN   = '{foreach from=$order_list item=order_item name=order_list1}{$order_item.order_sn}{if !$smarty.foreach.order_list1.last},{/if}{/foreach}';  // 查询条码
    var ORDER_ID   = '{$order.order_id}';  // 打印面单链接用
    var PARTY_ID   = '{$order.party_id}';  // 订单的组织
    var SHIP_STTS  = '{$order.shipping_status}';  // 主订单的配送状态，根据该状态来确定鼠标焦点的定位位置
    var CARRIER_ID = '{$shipment.CARRIER_ID}';  // 检查快递的格式
    var SHIPPING_ID = '{$order.shipping_id}';  // 检查快递的格式
    var TRACK_NUM  = '{$shipment.TRACKING_NUMBER}';  // 面单号，已经波次过的，需要检查
    var admin_name = '{$smarty.session.admin_name}';
    
    // {literal}
    
    $(document).ready(function(){
        // 绑定滑动效果
        $('legend.expand').bind('click', function(event){
            $(this).next().slideToggle('normal');
        });
        
        // 绑定加载出库单事件
        $('#load').bind('click', load);
        $('#order_id').bind('keyup', listen).focus();

        // 确定页面载入时鼠标焦点的定位
        if ($.trim($('#order_id').val()) != '') {
            $('#order_id').select();
        }
        // 等待输入发货单号 
        else {
            $('#order_id').focus();
        }
                		
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
        switch (event.keyCode) {
            case KEY.RETURN:
            case KEY.CTRL:
                load();
                event.preventDefault();
                break;
        }
    }
    
    /**
     * 载入订单商品和信息
     */
    function load() 
    {
        var order_id = $.trim($('#order_id').val());
        if (order_id == '') {
            alert('请先输入订单号');
            return; 
        }
        applyMailno("add_unique_shipment",order_id);
        
    }
    
    function applyMailno(act,order_id){
    	$.ajax({
	        beforeSend:function(){
	            $("#add_tracking_number_btn").attr('disabled', 'disabled');
	        },
	        
	        type: 'POST',
			url: '../express/ajax',  
			data : "act="+act+"&order_id="+order_id,
			dataType : 'json',
	        async: false,
	        error: function(){alert("查询异常，请联系ERP");},
	        success: function(data){
	        	if(data.result=="success" && data.trackingNumber!=""){ 
	        		alert(data.shippingId +" ; "+ data.trackingNumber);
	        		print_url = '../express/print?shippingId='+data.shippingId+"&orderId="+order_id+"&trackingNumber="+data.trackingNumber;
	        		pprint(print_url, 'hidden');
	        	}else{
	        		alert(data.msg);
	        	}
	        },
	        complete:function(){
	        	$("#add_tracking_number_btn").removeAttr('disabled');
	        }
	    });
    }
   
    /**
     * 打印
     */
    function pprint(url, mode)
    {
        if (mode == 'hidden') {
            var iframe = document.getElementById('_pprint_');
            if (!iframe) {
                var obj = document.createElement("iframe");
                obj.frameborder = 0;
                obj.width = 0;
                obj.height = 0;
                obj.id = '_pprint_';
                var iframe = document.body.appendChild(obj);
            }
            iframe.src = url;
            return iframe;
        } else {
            return window.open(url, "PrintWindow", "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,width=800,height=920,left=10,top=240");          
        }
    }
    
    /**
     * 打印
     */
    function hidden_print(url) {
        window.print_frame.location.href = url;
    }
    
    
    /**
	 * Record PRint Action
	 * @param  {[type]} ORDER_ID [description]
	 * @param  {[type]} bill_no  [description]
	 * @return {[type]}          [description]
	 */
	function sync_record_print(type,order_sn,tracking_number){
		$.ajax({
            async:false,
            type: 'POST',
            url: 'ajax.php?act=add_print_record_for_carrier_bill',
            data: 'type='+type+'&order_sn=' + order_sn + '&tracking_number=' + tracking_number,
            dataType: 'json',
            error: function() {
                alert('ajax请求错误,Record Bill Print 失败:' + tracking_number); 
            },
            success: function(data) {
                if(data['error']) {
                    alert(data['error']);
                } else if(data == true) {
                    result = true;
                } 
            }
        });
	}
	 
    // {/literal}
    // ]]>
  </script>
  
</head>
<body>
	<%
		String message = (String) request.getAttribute("message");
		if (message != "") {
	%>
	<div id="message" style="border: #7F9F00 2px solid; padding: 5px; text-align: center;">
		${message}</div>
	<%
		}
	%>

</br></br>
  
<fieldset style="-moz-border-radius:6px;padding:10px;">
<legend><span style="font-weight:bold; font-size:18px; color:#2A1FFF;">&nbsp;追加热敏面单-单个版&nbsp;</span></legend>
<form method="post" id="form"> 

    <div style="clear:both;"> 
    <h3 style="color:#09F;">配送信息</h3>

    <table cellpadding="5" cellspacing="1">
        <tr>
        <td width="120">扫描orderId</td>
        <td>
            <input type="text" id="order_id" value="${orderId}" size="39" /> &nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" id="load" value="加载" />
            <input type="button" id='add_tracking_number_btn' value="追加面单" disabled="disabled"/>
        </td>
        </tr>
        <% String orderId = (String) request.getAttribute("orderId");
		if (orderId != "") {  %>
        <c:forEach items="${orderInfoMap}" var="map" varStatus="status">
		<c:forEach items="${map}" var="data">
		<tr>
		<td>${data.key}</td>
		<td>${data.value}</td>
		</tr>
		</c:forEach>
		</c:forEach>
		<%} %>
    </table>
  </div>
</form>

<br /><br />

</fieldset>
<iframe name="print_frame" width="0" height="0" frameborder="0" src="about:blank" ></iframe>
</body>
</html>

