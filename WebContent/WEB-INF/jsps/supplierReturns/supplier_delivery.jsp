<%@page import="java.util.logging.Formatter"%>
<%@page import="org.apache.velocity.runtime.directive.Foreach"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>

<html>
 <head>
  <meta charset='utf-8' />
  <title>供应商退货</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
  <link href="<%=request.getContextPath() %>/static/css/bootstrap.min.css" rel="stylesheet" type="text/css">
  <script type="text/javascript" src="../static/js/jquery.min.js"></script>
  <script type="text/javascript" >  

   // 串号扫描
   function sn_scan(event){
       switch (event.keyCode) {
		    case 13:
		    case 17:
              var in_sn_input = document.getElementById('sninput');
              var in_sn = in_sn_input.value.trim();
              var desc_in_sn = document.getElementById(in_sn);
              
              if(desc_in_sn == undefined){
              	  in_sn_input.select() ;
              }else{
                  desc_in_sn.value = in_sn;
                  in_sn_input.value = '';
                  in_sn_input.focus();
              }
       }
   }
   
   function autoinput(count) {
        var sn_inputs = $('input[name="serial_number"]');
        count = Math.min(sn_inputs.length, count);
        for (var i=0; i<count; i++ ) {
            sn_inputs[i].value = sn_inputs[i].id;
        }
   }

   function getGoodsSn(){
	   var $button = $('#return_goods_btn');
	   waiting($button);
	   
	   var invItemTypeId = document.getElementById('hid_invItemTypeId').value ; 
	   if('SERIALIZED' == invItemTypeId){
	       var goods_sn_input =  document.getElementsByName('serial_number');
	       var json = '[';
	       var index = 0 ;
	       for(var i=0; i<goods_sn_input.length; i++){
	    	   if(goods_sn_input[i].value != ''){
	    		    // 检查没没有瞎提交
                   if(goods_sn_input[i].value != goods_sn_input[i].id){
                	    alert('你自己填写的串号【' + goods_sn_input[i].value + '】， 写错了地方。');
                	    goods_sn_input[i].select();
                	    waiting($button, 'en');
                	    return false ;
                    }
                   // 提交的数据
                   if(index == 0){
                	    json += '{"erp_goods_sn":"'+ goods_sn_input[i].id + '"}';
                    } else {
                	    json += ', {"erp_goods_sn":"'+ goods_sn_input[i].id + '"} ';
                   }

        	      index++;
               }
           }
           json += ']';
           if(0 == index){
               // 还没有填商品序列号
               alert('需要出库的串号 还没有扫进来。');
               waiting($button, 'en');
               return false;
           }
           
           document.getElementById('hid_serial_number').value = json ;
	   }
	   else{
		   var return_amount = document.getElementById('hid_returnOrderAmount').value;
		   var input_amount = document.getElementById('returnOrderAmount').value;
		   if(parseInt(input_amount) <= 0 || parseInt(input_amount) > parseInt(return_amount)){
                alert('你填入的退货数量 不对，');
                waiting($button, 'en');
                return false ;
		   }
	   }
   }
   
   /**
    * 切换按钮的等待状态
    */
   function waiting($button, status){
	   if (status == 'en'){
		   $button.attr('disabled', false);
	   } else {
		   $button.attr('disabled', true);
	   }
   }

   // 取消操作
   function cancellatonFunc(){
	   var order_goods_id = document.getElementById('hid_supRetReqId').value;
	   document.getElementById('hid_supRetReqId').value = '';
	   document.getElementById('hid_serial_number').value = '';
	   document.getElementById('act').value = '';

	   location.href = 'goodsReturn';
   }
   
  </script>
  <style type="text/css">
  	.lb-table{border:1px solid black; width:130px;}
  	.lb-table tr td{border:1px solid black; text-align: center;}
	.lb-table tr th{border:1px solid black;}
  </style>
 </head>
 <body>
   <c:if test="${error != null}" ><div style="color: red;font-size: 20px;">${error}<br> ${message}</div></c:if>
   <fieldset style="-moz-border-radius:6px;padding:10px;">
      <legend><span style="font-weight:bold; font-size:18px; color:#2A1FFF;">&nbsp;供应商退货&nbsp;</span></legend>
      <br/>

    <form method="post" action="goodsOut" id="RForm" name="RForm" >
      <input type="hidden" id="hid_supRetReqId" name="hid_supRetReqId" value=<%=request.getAttribute("order_goods_id")%> ></input>  
      <input type="hidden" id="hid_serial_number" name="hid_serial_number" />  
      <input type="hidden" id="hid_invItemTypeId" name="hid_invItemTypeId" value=<%=request.getAttribute("isserial")%>></input>  
      <input type="hidden" id="act" name="act" value="supplier_return"/>    
      <div style="clear:both;">
    <table cellpadding="5" cellspacing="1">
       <input type="hidden" readonly="readonly"  name="product_id" value=<%=request.getAttribute("product_id")%> ></input>
       <input type="hidden"  readonly="readonly"  name="order_id" value=<%=request.getAttribute("order_Id")%> ></input>
       <input type="hidden"  readonly="readonly"  name="order_goods_id" value=<%=request.getAttribute("order_goods_id")%> ></input>
       <input type="hidden" readonly="readonly"   name="provider_code" value=<%=request.getAttribute("provider_code")%> ></input>
       <input type="hidden" readonly="readonly"   name="batch_sn" value=<%=request.getAttribute("batch_sn")%> ></input>
       <input type="hidden"  readonly="readonly"  name="unit_price" value=<%=request.getAttribute("unit_price")%> ></input>
       <input type="hidden" readonly="readonly"  name="status" value=<%=request.getAttribute("status")%> ></input>
       <input type="hidden"  readonly="readonly"  name="warehouse_id" value=<%=request.getAttribute("warehouse_id")%> ></input>
       <input type="hidden"  readonly="readonly"  name="customer_id" value=<%=request.getAttribute("customer_id")%> ></input>
    <tr>
        <td>商品名：</td>
        <td style="font-weight: bold;"><%=request.getAttribute("goods_name") %></td>
    </tr>
    <tr>
        <td width="120">商品库存类型 ：</td>
        <td style="letter-spacing: 1px; font-weight: bold;"><%=request.getAttribute("status_desc")%></td>
    </tr>  
      
    <tr>
        <td width="120">商品出库仓库 ：</td>
        <td style="letter-spacing: 1px; font-weight: bold;"><%=request.getAttribute("warehouse_name")%></td>
    </tr>
    <tr>
        <td width="120">商品单价 ：</td>
        <td style="letter-spacing: 1px; font-weight: bold;"><%=request.getAttribute("unit_price")%></td>
    </tr>
     <tr>
        <td width="120">批次号 ：</td>
        <td style="letter-spacing: 1px; font-weight: bold;"><%=request.getAttribute("batch_sn")%> </td>
    </tr>
    <tr>
        <td width="120">退还供应商 ：</td>
        <td style="letter-spacing: 1px; font-weight: bold;"><%=request.getAttribute("provider_name")%></td>
    </tr>
    
    <% 
       String isserial = (String)request.getAttribute("isserial");
       if("NON-SERIALIZED".equals(isserial)){
     %>
    <tr>    
        <td>退货数量 ：
        <input type="hidden" id="hid_returnOrderAmount" name="hid_returnOrderAmount" value=<%=request.getAttribute("amount")%> />
        </td>
        <td>
        <input type="hidden" id="amount" name="amount" readonly="readonly"  value=<%=request.getAttribute("maxOutNum")%> onkeyup="this.value=this.value.replace(/[^\d]/g,'') " onafterpaste="this.value=this.value.replace(/[^\d]/g,'') "/> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 最大可退货数量 ： <%=request.getAttribute("maxOutNum")%>
        <table class="lb-table">
        <tr><th>库位条码</th><th>数量</th></tr>
        <c:forEach items="${locationList}" var="info">
        <tr><td class="loc">${info.location_barcode}</td><td>${info.quantity}</td></tr>
        </c:forEach>
        </table>
        
        </td> 
    </tr> 
	 <%
	   }else
	{%>
	 <tr>    
        <td>可退货串号商品数量 ：<%=request.getAttribute("maxOutNum")%></td>
    </tr> 
    <tr>    
        <td>请输入串号，多个用逗号隔开<input type="text" style="width:300px;height:40px" id="serialnums" name="serialnums"></input></td> 
    </tr> 
	
       
        </table>
        </td>
    </tr>
         
	<%}%>
    
</table>
 

   <% 
      if("SERIALIZED".equals(isserial)){
    %>
    <div style="margin-left:52px; margin-top: 8px; ">
	      <label style="border-width: 1px;">串号扫描：&nbsp; &nbsp; &nbsp; </label>
	      <input style="width:200px;" id="sninput" name="sninput"  onkeyup="javascript:sn_scan(event);"/>
	      <input type="button" value="自动填写100个串号" onclick="javascript:autoinput(100);" /> 
    </div>
  <%
	  }else
  %>

	<div style="margin-left:150px; margin-top: 30px; margin-bottom: 25px;">
	      <input type="submit" id="return_goods_btn" class="lq-btn lq-btn-primary lq-btn-sm" value="退货出库" /> &nbsp; &nbsp; &nbsp; &nbsp;
	      <input type="button" id="cancellaton" class="lq-btn lq-btn-primary lq-btn-sm" name="cancellaton" value=" 取 消  "  onclick="javascript:cancellatonFunc();"/> 
	</div>

	</div>
	      
	</form>

   </fieldset> 
   <script type="text/javascript">
     // 库位转换
        function insert_flg(str){
            if (str && str != "") {
                var newstr="";
                var before = str.substring(0,3), after = str.substring(3,7);
                newstr = before + "-" + after;
                str = newstr;
                var before = str.substring(0,6), after = str.substring(6,8);
                newstr = before + "-" + after;
                str = newstr;
            } else {
                newstr = "";
            }
            return newstr;
        }
        $(".loc").each(function(){
      $(this).text(insert_flg($(this).text()));
    });
   </script>
 </body>
</html>
