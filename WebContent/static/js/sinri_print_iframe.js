/**
	All Hail Sinri Edogawa !
	First Draft since 20130807
	To deal with the javascript codes to print
	ljni@i9i8.com
**/

/**
 * 打印区内容填充
 * IF the page of the url hold the contents to print when load... then it prints as if you commanded it.
 * YOU NEED IT:
 * <iframe name="print_frame" width="0" height="0" frameborder="0" src="about:blank" ></iframe>
 */
function hidden_print(url) {
   	window.print_frame.location.href = url;
}

/**
* A Function to deal with print
* @param url a page to print
* @param mode if 'hidden' create iframe, Then set the url to it (IF the page of the url hold the contents to print when load... then it prints as if you commanded it.) ELSE print url directly
**/
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
* 给一个主订单号打印参数，返回相应的打印页地址。
* 如果sugu为1，返回地址为立即打印的打印页地址
**/
function get_bill_url(order_id,sugu){
	var url = getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_shipping_order3.php?order_id=' + order_id;
	if(sugu==1) url=url + '&print=1';
	return url;
}
/**
 * 补寄发票批量打印，给订单号打印参数，返回相应的页地址
 * 如果sugu为1，返回地址为立即打印的打印页地址
 */
//-----------------------------------------------------------------------------------------------------------------
function get_invoice_url(id,sugu){
	var url = getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/invoice_manage/print_invoice_temple.php?id=' + id;
	//var url ='print_invoice_temple.php?id=' + id;
	if(sugu==1) url=url + '&print=1';
	return url;
}
//-----------------------------------------------------------------------------------------------------------------

/**
* 根据一个main_order_id打印这货的面单
* 经过深入调研和围观，这里只管印出来。
* 什么数据库操作这个脚本里就不管了。
**/ 
function print_shipment(order_id){
	//var print_url = getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_shipping_order3.php?order_id=' + order_id + '&print=1';
	var print_url=get_bill_url(order_id,1);
	pprint(print_url);
}

/**
根据一个mian_order_id数组打印这堆货的面单
**/ 
function batch_print_shipment(order_ids){
	var url = getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_shipping_orders.php?print=1&order_id=' + order_ids.join(',');
	pprint(url);
}

/**
* 根据一个发货单号打发货单
* @author Sinri Edogawa ljni@i9i8.com
* @verion Hajime 20130802 UNTESTED
**/
function print_dispatch(shipment_id){
	//pprint('shipment_print.php?print=1&shipment_id='+shipment_id,'hidden');
	pprint(getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+"/admin/shipment_print_for_batch_pick_new.php?print=1&shipment_id="+shipment_id);
}

/**
* 根据一个发货单号数组打印一叠发货单
* @author Sinri Edogawa ljni@i9i8.com
* @verion Hajime 20130802 UNTESTED
**/
function batch_print_dispatch(shipment_ids){
	//alert("Go to print "+shipments.join(','));
	pprint(getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/shipment_print_for_batch_pick_new.php?print=1&shipment_id=' + shipment_ids.join(','));
}

/** ! All Hail Sinri Edogawa **/

/**
找相对路径用的，也就是找到erp文件夹为止
**/
function SINRI_SET_PATH_DEEPTH(){
	//alert("!<?php echo $SINRI_ROOT_PATH; ?>!");
	//return 0;//For Release
	 return 1;//For Sinri USE as http://172.16.1.231/Erp/admin/shipment_listV5.php
	//return 2;//For cywang USE as http://172.16.1.231/Erp/xxx/admin/shipment_listV5.php
}
function getRootWebSitePath(deepth){
	if(!deepth){
    	deepth=0;
    }
	var _location = document.location.toString();
	
	var applicationNameIndex = _location.indexOf('/', _location.indexOf('://') + 3);
	var applicationName = _location.substring(0, applicationNameIndex) + '/';
	var sub=applicationName;
	var webFolderIndex = applicationNameIndex;
	for(var i=0;i<deepth;i++){
		var webFolderIndex = _location.indexOf('/', _location.indexOf(applicationName) + sub.length);
		sub = _location.substring(0, webFolderIndex) + '/';
	}
	applicationName=sub;
	var webFolderFullPath = _location.substring(0, webFolderIndex);
	return webFolderFullPath;
	//return _location+"\r\n"+applicationNameIndex+" "+applicationName+"\r\n"+webFolderIndex+" "+webFolderFullPath;
}
function getRootWebSitePath_bak(){
	var _location = document.location.toString();
	var applicationNameIndex = _location.indexOf('/', _location.indexOf('://') + 3);
	var applicationName = _location.substring(0, applicationNameIndex) + '/';
	var webFolderIndex = _location.indexOf('/', _location.indexOf(applicationName) + applicationName.length);
	var webFolderFullPath = _location.substring(0, webFolderIndex);
	return webFolderFullPath;
}
/**
码托
从HTML中找ID为GN_LIST的东西里的值作为barcodes，作为上架容器条码批量打印。
**/
function print_tray_numbers() {
	var url=getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_barcodes.php?sugu_print=1&type=tray&barcodes=';
	var codes=document.getElementById('GN_TRAY_LIST').value;
	//alert("接下来要打印上架容器条码 "+codes);
	hidden_print(url+codes);
	//alert("理论上打印上架容器条码 "+codes+" 已经完了");
	return true;
}

/**
从HTML中找ID为GN_LIST的东西里的值作为barcodes，作为上架容器条码批量打印。
要求支持hidden_print
有关条码打印机设置和纸型，请阅读《鲵辞-天问》。
**/
function print_grouding_numbers() {
	var url=getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_barcodes.php?sugu_print=1&type=grouding&barcodes=';
	var codes=document.getElementById('GN_LIST').value;
	//alert("接下来要打印上架容器条码 "+codes);
	hidden_print(url+codes);
	//alert("理论上打印上架容器条码 "+codes+" 已经完了");
	return true;
}

/**
返回非立即打印的单个上架容器条码的打印页面URL
有关条码打印机设置和纸型，请阅读《鲵辞-天问》。
**/
function BARCODE_PRINT_URL_GROUDING(barcode){
	var url=getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_barcode.php?sugu_print=0&type=grouding&barcode='+barcode;
	return url;
}

function print_barcodes_for_test(barcodes) {
	var url=getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_barcodes.php?sugu_print=1&type=grouding&barcodes='+barcodes;
	//alert("接下来要打印上架容器条码 "+codes);
	hidden_print(url);
	//alert("理论上打印上架容器条码 "+codes+" 已经完了");
	return true;
}

function print_batch_pick_with_bpsn(bpsn){
	var url=getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_batch_pick.php?sugu_print=1&sn='+bpsn;
	hidden_print(url);
	return url;
}

function print_goods_barcodes(barcode,num){
	var url=getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_barcodes.php?sugu_print=1&type=goods&barcode='+barcode+'&number='+num;
	//alert("接下来要打印上架容器条码 "+codes);
	hidden_print(url);
	//alert("理论上打印上架容器条码 "+codes+" 已经完了");
	return true;
}

function print_SN_barcodes(barcode,start,num){
	var url=getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_barcodes.php?sugu_print=1&type=sn&barcode='+barcode+'&number='+num+"&start="+start;
	//alert(url);
	hidden_print(url);
	return true;
}

function print_location_barcodes(barcode,num){
	var url=getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_barcodes.php?sugu_print=1&type=location&barcode='+barcode+'&number='+num;
	//alert("接下来要打印库位条码");
	hidden_print(url);
	//alert("理论上打印上架容器条码 "+codes+" 已经完了");
	return true;
}

function print_location_barcode_list(barcodes){
	var url=getRootWebSitePath(SINRI_SET_PATH_DEEPTH())+'/admin/print_barcodes.php?sugu_print=1&type=locations&barcodes='+barcodes;
	if(url.length>2000)alert("打印的内容过长，请换打印途径");
	hidden_print(url);
	//alert("理论上打印上架容器条码 "+codes+" 已经完了");
	return true;
}