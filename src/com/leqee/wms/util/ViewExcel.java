package com.leqee.wms.util;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.web.servlet.view.document.AbstractExcelView;
public class ViewExcel extends AbstractExcelView {
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,     
            HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		
	        String type = (String) model.get("type");
	        
	        if("exportTNsForBack".equals(type)){
	        	String message = "";
	        	String excelName = "快递回传数据.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    
		        // 产生Excel表头  
		        HSSFSheet sheet = workbook.createSheet("tnsForBackList");  
		        HSSFRow header = sheet.createRow(0); // 第0行  
	        	if(WorkerUtil.isNullOrEmpty(model.get("list"))){
	        		message = String.valueOf(model.get("message"));
	        		header.createCell((short) 0).setCellValue("导出信息"); 
	        		HSSFRow row = sheet.createRow(1);  
		            row.createCell((short) 0).setCellValue(message);  
	        	}else{
	        		List trackingNumberList = (List) model.get("list");
	        		// 产生标题列  
			        header.createCell((short) 0).setCellValue("快递单号");    
			        header.createCell((short) 1).setCellValue("收件人");  
			        header.createCell((short) 2).setCellValue("收件省");  
			        header.createCell((short) 3).setCellValue("收件市");  
			        header.createCell((short) 4).setCellValue("收件区");  
			        header.createCell((short) 5).setCellValue("收件详址");  
			        header.createCell((short) 6).setCellValue("收件电话"); 
			        header.createCell((short) 7).setCellValue("收件手机");   
			        header.createCell((short) 8).setCellValue("重量"); 
			        HSSFCellStyle cellStyle = workbook.createCellStyle();  
			        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd")); 
			        // 填充数据  
			        int rowNum = 1;  
			        for (Iterator iter = trackingNumberList.iterator(); iter.hasNext();) {  
			            Map element = (Map) iter.next();  
			            HSSFRow row = sheet.createRow(rowNum++);  
			            row.createCell((short) 0).setCellValue(element.get("tracking_number").toString());    
			            row.createCell((short) 1).setCellValue(element.get("receive_name").toString());  
			            row.createCell((short) 2).setCellValue(element.get("province_name").toString());  
			            row.createCell((short) 3).setCellValue(element.get("city_name").toString());  
			            row.createCell((short) 4).setCellValue(element.get("district_name").toString());  
			            row.createCell((short) 5).setCellValue(element.get("shipping_address").toString());  
			            row.createCell((short) 6).setCellValue(String.valueOf(element.get("mobile_number")));
			            row.createCell((short) 7).setCellValue(String.valueOf(element.get("phone_number")));
			            row.createCell((short) 8).setCellValue(String.valueOf(element.get("shipping_wms_weight")));
			        }  
			        
	        	}
		        
	        }else if("stockOutExport".equals(type)){
	        	String message = "";
	        	String excelName = "出库资料查询.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    
		        // 产生Excel表头  
		        HSSFSheet sheet = workbook.createSheet("stockOutExport");  
		        HSSFRow header = sheet.createRow(0); // 第0行  
	        	if(WorkerUtil.isNullOrEmpty(model.get("stockOutList"))){
	        		message = String.valueOf(model.get("message"));
	        		header.createCell((short) 0).setCellValue("导出信息"); 
	        		HSSFRow row = sheet.createRow(1);  
		            row.createCell((short) 0).setCellValue(message);  
	        	}else{
	        		List list = (List) model.get("stockOutList");
	        		// 产生标题列  
			        header.createCell((short) 0).setCellValue("货主");    
			        header.createCell((short) 1).setCellValue("oms订单号");  
			        header.createCell((short) 2).setCellValue("wms订单号");  
			        header.createCell((short) 3).setCellValue("店铺名称");  
			        header.createCell((short) 4).setCellValue("商品条码");  
			        header.createCell((short) 5).setCellValue("商品名称");
			        header.createCell((short) 6).setCellValue("渠道");
			        header.createCell((short) 7).setCellValue("出库数量"); 
			        header.createCell((short) 8).setCellValue("批次号");   
			        header.createCell((short) 9).setCellValue("出库时间"); 
			        HSSFCellStyle cellStyle = workbook.createCellStyle();  
			        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd")); 
			        // 填充数据  
			        int rowNum = 1;  
			        for (Iterator iter = list.iterator(); iter.hasNext();) {  
			            Map element = (Map) iter.next();  
			            HSSFRow row = sheet.createRow(rowNum++);  
			            row.createCell((short) 0).setCellValue(String.valueOf(element.get("name")));    
			            row.createCell((short) 1).setCellValue(String.valueOf(element.get("oms_order_sn")));  
			            row.createCell((short) 2).setCellValue(String.valueOf(element.get("order_id")));  
			            row.createCell((short) 3).setCellValue(String.valueOf(element.get("shop_name")));  
			            row.createCell((short) 4).setCellValue(String.valueOf(element.get("barcode")));  
			            row.createCell((short) 5).setCellValue(String.valueOf(element.get("product_name")));
			            row.createCell((short) 6).setCellValue(String.valueOf(element.get("warehouse_name")));  
			            row.createCell((short) 7).setCellValue(String.valueOf(element.get("number")));
			            row.createCell((short) 8).setCellValue(String.valueOf(element.get("batch_sn")));
			            row.createCell((short) 9).setCellValue(String.valueOf(element.get("inventory_out_time")));
			        }  
			        
	        	}
	        }
	        else if("exportHistoryTaskImprove".equals(type)){
	        	List inventoryList = (List) model.get("list");
	        	String excelName = "历史盘点调整数据.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    
		        // 产生Excel表头  
		        HSSFSheet sheet = workbook.createSheet("packBoxList");  
		        HSSFRow header = sheet.createRow(0); // 第0行  
		        // 产生标题列  
		        header.createCell((short) 0).setCellValue("货主名称");  
		        header.createCell((short) 1).setCellValue("渠道");   
		        header.createCell((short) 2).setCellValue("商品名称");  
		        header.createCell((short) 3).setCellValue("商品条码");    
		        header.createCell((short) 4).setCellValue("调整数量");  
		        header.createCell((short) 5).setCellValue("调整时间");  
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd HH:mm:ss"));  
		  
		        // 填充数据  
		        int rowNum = 1;  
		        for (Iterator iter = inventoryList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            HSSFRow row = sheet.createRow(rowNum++);  
		            row.createCell((short) 0).setCellValue(element.get("name").toString());  
		            row.createCell((short) 1).setCellValue(element.get("warehouse_name").toString());  
		            row.createCell((short) 2).setCellValue(element.get("product_name").toString());  
		            row.createCell((short) 3).setCellValue(element.get("barcode").toString());   
		            row.createCell((short) 4).setCellValue(element.get("num").toString());  
		            row.createCell((short) 5).setCellValue(element.get("created_time").toString());  
		        }  
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell((short) 0).setCellValue("TOTAL:"+(rowNum-1));  
	        }
	        else if("exportPackBox".equals(type)){
	        	List inventoryList = (List) model.get("list");
	        	String excelName = "耗材信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    
		        // 产生Excel表头  
		        HSSFSheet sheet = workbook.createSheet("packBoxList");  
		        HSSFRow header = sheet.createRow(0); // 第0行  
		        // 产生标题列  
		        header.createCell((short) 0).setCellValue("业务组织");  
		        header.createCell((short) 1).setCellValue("仓库名称");  
		        header.createCell((short) 2).setCellValue("商品名称");  
		        header.createCell((short) 3).setCellValue("商品条码");  
		        header.createCell((short) 4).setCellValue("库位");  
		        header.createCell((short) 5).setCellValue("状态");  
		        header.createCell((short) 6).setCellValue("ERP库存");  
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));  
		  
		        // 填充数据  
		        int rowNum = 1;  
		        for (Iterator iter = inventoryList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            HSSFRow row = sheet.createRow(rowNum++);  
		            row.createCell((short) 0).setCellValue(element.get("name").toString());  
		            row.createCell((short) 1).setCellValue(element.get("warehouse_name").toString());  
		            row.createCell((short) 2).setCellValue(element.get("product_name").toString());  
		            row.createCell((short) 3).setCellValue(element.get("barcode").toString());  
		            row.createCell((short) 4).setCellValue(element.get("location_barcode").toString());  
		            row.createCell((short) 5).setCellValue(element.get("status").toString());  
		            row.createCell((short) 6).setCellValue("***");  
		        }  
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell((short) 0).setCellValue("TOTAL:"+(rowNum-1));  
	        }else if("exportInventory".equals(type)){
	        	List inventoryList = (List) model.get("list");
	        	String warehouseName = model.get("warehouse_name").toString();
	        	String excelName = "库存信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    

		        // 产生Excel表头  
		        HSSFSheet sheet = workbook.createSheet("inventoryList");  
		        HSSFRow header = sheet.createRow(0); // 第0行  
		        // 产生标题列  
		        header.createCell((short) 0).setCellValue("库位条码");  
		        header.createCell((short) 1).setCellValue("货主");  
		        header.createCell((short) 2).setCellValue("商品条码");
		        header.createCell((short) 3).setCellValue("商品名称");  
		        header.createCell((short) 4).setCellValue("商品状态");
		        header.createCell((short) 5).setCellValue("商品保质期");  
		        header.createCell((short) 6).setCellValue("保质期单位");
		        header.createCell((short) 7).setCellValue("生产日期");
		        header.createCell((short) 8).setCellValue("效期状态");
		        header.createCell((short) 9).setCellValue("批次号");
		        header.createCell((short) 10).setCellValue("仓库名称");  
		        header.createCell((short) 11).setCellValue("库位总量");  
		        header.createCell((short) 12).setCellValue("可用库存");  
		        header.createCell((short) 13).setCellValue("不可用库存");
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));  
		  
		        // 填充数据  
		        int rowNum = 1;  
		        for (Iterator iter = inventoryList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            HSSFRow row = sheet.createRow(rowNum++);  
		            String validity_status = WorkerUtil.isNullOrEmpty(element.get("validity_status"))?"":element.get("validity_status").toString();
		            String qty_total = WorkerUtil.isNullOrEmpty(element.get("qty_total"))?"0":element.get("qty_total").toString();
		            String qty_available = WorkerUtil.isNullOrEmpty(element.get("qty_available"))?"0":element.get("qty_available").toString();
		            row.createCell((short) 0).setCellValue(element.containsKey("location_barcode")&&!WorkerUtil.isNullOrEmpty(element.get("location_barcode"))?element.get("location_barcode").toString():"-");  
		            row.createCell((short) 1).setCellValue(element.get("name").toString());  
		            row.createCell((short) 2).setCellValue(element.get("barcode").toString());  
		            row.createCell((short) 3).setCellValue(element.get("product_name").toString());  
		            row.createCell((short) 4).setCellValue(element.get("status").toString());  
		            row.createCell((short) 5).setCellValue(element.get("validity_").toString());  
		            if("NORMAL".equals(validity_status)){
		            	row.createCell((short) 6).setCellValue("正常");
		            }else if("WARNING".equals(validity_status)){
		            	row.createCell((short) 6).setCellValue("临期预警");
		            }else if("UNSALABLE".equals(validity_status)){
		            	row.createCell((short) 6).setCellValue("禁发");
		            }else if("EXPIRED".equals(validity_status)){
		            	row.createCell((short) 6).setCellValue("过期");
		            }else{
		            	row.createCell((short) 6).setCellValue("");
		            }
		            row.createCell((short) 7).setCellValue(element.get("validity").toString());
		            row.createCell((short) 8).setCellValue(element.get("validity_status").toString());
		            row.createCell((short) 9).setCellValue(element.get("batch_sn").toString());
		            row.createCell((short) 10).setCellValue(warehouseName);  
		            row.createCell((short) 11).setCellValue(qty_total);  
		            row.createCell((short) 12).setCellValue(qty_available);  
		            row.createCell((short) 13).setCellValue(Integer.parseInt(qty_total)-Integer.parseInt(qty_available));  
		        }  
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell((short) 0).setCellValue("TOTAL:"+(rowNum-1));  
	        }else if("exportSaleOrders".equals(type)){
	        	List saleOrderList = (List) model.get("saleOrderList");
	        	String excelName = "历史订单信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    

		        // 产生Excel表头  
		        HSSFSheet sheet = workbook.createSheet("saleOrderList");  
		        HSSFRow header = sheet.createRow(0); // 第0行  
		        // 产生标题列  
		        header.createCell((short) 0).setCellValue("货主");  
		        header.createCell((short) 1).setCellValue("订单号");  
		        header.createCell((short) 2).setCellValue("OMS订单号");
		        header.createCell((short) 3).setCellValue("订单状态");  
		        header.createCell((short) 4).setCellValue("订单优先级");  
		        header.createCell((short) 5).setCellValue("付款时间");  
		        header.createCell((short) 6).setCellValue("快递方式");
		        header.createCell((short) 7).setCellValue("发货仓库");  
		        header.createCell((short) 8).setCellValue("商品名称"); 
		        header.createCell((short) 9).setCellValue("商品条码");
		        header.createCell((short) 10).setCellValue("商品数量");
		        header.createCell((short) 11).setCellValue("波次单号");
		        header.createCell((short) 12).setCellValue("波次订单序号");
		        header.createCell((short) 13).setCellValue("最后操作时间");
		        header.createCell((short) 14).setCellValue("最后操作人");  
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));  
		  
		        // 填充数据  
		        int rowNum = 1;  
		        for (Iterator iter = saleOrderList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            HSSFRow row = sheet.createRow(rowNum++);  
		            row.createCell((short) 0).setCellValue(element.get("name").toString());  
		            row.createCell((short) 1).setCellValue(element.get("order_id").toString());  
		            row.createCell((short) 2).setCellValue(element.get("oms_order_sn").toString());  
		            String order_status = element.get("order_status").toString();
		            try {
						row.createCell((short) 3).setCellValue(ORDERSTATUSMAP.get(order_status).toString());
					} catch (Exception e) {
						row.createCell((short) 3).setCellValue(order_status);
					}  
		            row.createCell((short) 4).setCellValue(element.get("order_level").toString()); 
		            row.createCell((short) 5).setCellValue(element.get("pay_time").toString()); 
		            row.createCell((short) 6).setCellValue(element.get("shipping_name").toString());  
		            row.createCell((short) 7).setCellValue(element.get("warehouse_name").toString());  
		            row.createCell((short) 8).setCellValue(element.get("goods_name").toString());  
		            row.createCell((short) 9).setCellValue(element.get("barcode").toString());  
		            row.createCell((short) 10).setCellValue(element.get("goods_number").toString());  
		            row.createCell((short) 11).setCellValue(element.containsKey("batch_pick_sn")&&!WorkerUtil.isNullOrEmpty(element.get("batch_pick_sn"))?element.get("batch_pick_sn").toString():"-");  
		            row.createCell((short) 12).setCellValue(element.get("order_batch_sequence_number").toString());  
		            row.createCell((short) 13).setCellValue(element.get("last_updated_time").toString());  
		            row.createCell((short) 14).setCellValue(element.get("last_updated_user").toString());  
		        }  
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell((short) 0).setCellValue("TOTAL:"+(rowNum-1));  
	        }else if("exportSaleProducts".equals(type)){
	        	List saleOrderList = (List) model.get("productList");
	        	String excelName = "商品信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    

		        // 产生Excel表头  
		        HSSFSheet sheet = workbook.createSheet("productList");  
		        HSSFRow header = sheet.createRow(0); // 第0行  
		        // 产生标题列  
		        header.createCell((short) 0).setCellValue("货主");  
		        header.createCell((short) 1).setCellValue("商品条码");  
		        header.createCell((short) 2).setCellValue("商品名称");
		        //header.createCell((short) 3).setCellValue("是否高价");  
		        header.createCell((short) 3).setCellValue("单品毛重");
		        header.createCell((short) 4).setCellValue("长");  
		        header.createCell((short) 5).setCellValue("宽");  
		        header.createCell((short) 6).setCellValue("高");  
		        header.createCell((short) 7).setCellValue("箱规");
		        header.createCell((short) 8).setCellValue("满足箱拣货区最大数量");  
		        header.createCell((short) 9).setCellValue("托规Hi");
		        header.createCell((short) 10).setCellValue("托规Ti");  
		        header.createCell((short) 11).setCellValue("保质期管理");  
		        header.createCell((short) 12).setCellValue("收货规则");  
		        header.createCell((short) 13).setCellValue("补货规则");
		        header.createCell((short) 14).setCellValue("库存分配规则"); 
		        header.createCell((short) 15).setCellValue("是否易碎");  
		        header.createCell((short) 16).setCellValue("是否3C产品");  
		        header.createCell((short) 17).setCellValue("是否需要串号维护");
		        header.createCell((short) 18).setCellValue("是否支持虚拟库存"); 
		        header.createCell((short) 19).setCellValue("虚拟库存"); 

		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));  
		  
		        // 填充数据  
		        int rowNum = 1;  
		        for (Iterator iter = saleOrderList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            HSSFRow row = sheet.createRow(rowNum++);  
		            row.createCell((short) 0).setCellValue(element.get("name").toString());  
		            row.createCell((short) 1).setCellValue(element.get("barcode").toString());  
		            row.createCell((short) 2).setCellValue(element.get("product_name").toString());  
					//row.createCell((short) 3).setCellValue(element.containsKey("is_high_price")&&!WorkerUtil.isNullOrEmpty(element.get("is_high_price"))?element.get("is_high_price").toString():"-");
		            row.createCell((short) 3).setCellValue(element.containsKey("weight")&&!WorkerUtil.isNullOrEmpty(element.get("weight"))?element.get("weight").toString():"");  
		            row.createCell((short) 4).setCellValue(element.containsKey("length")&&!WorkerUtil.isNullOrEmpty(element.get("length"))?element.get("length").toString():"");  
		            row.createCell((short) 5).setCellValue(element.containsKey("width")&&!WorkerUtil.isNullOrEmpty(element.get("width"))?element.get("width").toString():"");  
		            row.createCell((short) 6).setCellValue(element.containsKey("height")&&!WorkerUtil.isNullOrEmpty(element.get("height"))?element.get("height").toString():"");  
		            row.createCell((short) 7).setCellValue(element.containsKey("spec")&&!WorkerUtil.isNullOrEmpty(element.get("spec"))?element.get("spec").toString():"");  
		            row.createCell((short) 8).setCellValue(element.containsKey("box_pick_start_number")&&!WorkerUtil.isNullOrEmpty(element.get("box_pick_start_number"))?element.get("box_pick_start_number").toString():"");  
		            row.createCell((short) 9).setCellValue(element.containsKey("ti")&&!WorkerUtil.isNullOrEmpty(element.get("ti"))?element.get("ti").toString():"");  
		            row.createCell((short) 10).setCellValue(element.containsKey("hi")&&!WorkerUtil.isNullOrEmpty(element.get("hi"))?element.get("hi").toString():"");  
		            row.createCell((short) 11).setCellValue(element.containsKey("is_maintain_guarantee")&&!WorkerUtil.isNullOrEmpty(element.get("is_maintain_guarantee"))?element.get("is_maintain_guarantee").toString():"");  
		            row.createCell((short) 12).setCellValue(element.containsKey("receive_rule")&&!WorkerUtil.isNullOrEmpty(element.get("receive_rule"))?element.get("receive_rule").toString():"");  
		            row.createCell((short) 13).setCellValue(element.containsKey("replenishment_rule")&&!WorkerUtil.isNullOrEmpty(element.get("replenishment_rule"))?element.get("replenishment_rule").toString():"");  
		            row.createCell((short) 14).setCellValue(element.containsKey("stock_allocation_rule")&&!WorkerUtil.isNullOrEmpty(element.get("stock_allocation_rule"))?element.get("stock_allocation_rule").toString():""); 
		           
		            row.createCell((short) 15).setCellValue(element.containsKey("is_fragile")&&!WorkerUtil.isNullOrEmpty(element.get("is_fragile"))?element.get("is_fragile").toString():"");  
		            row.createCell((short) 16).setCellValue(element.containsKey("is_three_c")&&!WorkerUtil.isNullOrEmpty(element.get("is_three_c"))?element.get("is_three_c").toString():"");  
		            row.createCell((short) 17).setCellValue(element.containsKey("is_maintain_virtual_stock")&&!WorkerUtil.isNullOrEmpty(element.get("is_maintain_virtual_stock"))?element.get("is_maintain_virtual_stock").toString():"");  
		            row.createCell((short) 18).setCellValue(element.containsKey("virtual_stock")&&!WorkerUtil.isNullOrEmpty(element.get("virtual_stock"))?element.get("virtual_stock").toString():"");  
		            row.createCell((short) 19).setCellValue(element.containsKey("is_need_transfer_code")&&!WorkerUtil.isNullOrEmpty(element.get("is_need_transfer_code"))?element.get("is_need_transfer_code").toString():"");  
		        }  
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell((short) 0).setCellValue("TOTAL:"+(rowNum-1));  
	        }else if("exportLocation".equals(type)){
	        	List saleOrderList = (List) model.get("locationList");
	        	String excelName = "库位信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    

		        // 产生Excel表头  
		        HSSFSheet sheet = workbook.createSheet("locationList");  
		        HSSFRow header = sheet.createRow(0); // 第0行  
		        // 产生标题列  
		        header.createCell((short) 0).setCellValue("物理仓");  
		        header.createCell((short) 1).setCellValue("货主");  
		        header.createCell((short) 2).setCellValue("库位编码");
		        header.createCell((short) 3).setCellValue("区域");
		        header.createCell((short) 4).setCellValue("库位类型");  
		        header.createCell((short) 5).setCellValue("循环级别");  
		        header.createCell((short) 6).setCellValue("价值级别");  
		        header.createCell((short) 7).setCellValue("是否允许混放商品");
		        header.createCell((short) 8).setCellValue("是否允许混放批次");  
		        header.createCell((short) 9).setCellValue("是否在计算库容包括在内");
		        header.createCell((short) 10).setCellValue("可放托盘数量");  
		        header.createCell((short) 11).setCellValue("体积");  
		        header.createCell((short) 12).setCellValue("重量");  
		        header.createCell((short) 13).setCellValue("长");
		        header.createCell((short) 14).setCellValue("宽"); 
		        header.createCell((short) 15).setCellValue("高");  
		        header.createCell((short) 16).setCellValue("坐标（X）");  
		        header.createCell((short) 17).setCellValue("坐标（Y）");
		        header.createCell((short) 18).setCellValue("坐标（Z）"); 
		        header.createCell((short) 19).setCellValue("数量限制"); 
		        header.createCell((short) 20).setCellValue("忽略LPN");  
		        header.createCell((short) 21).setCellValue("上架顺序");
		        header.createCell((short) 22).setCellValue("拣货顺序"); 
		        header.createCell((short) 23).setCellValue("是否弃用");
		        
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));  
		  
		        // 填充数据  
		        int rowNum = 1;  
		        for (Iterator iter = saleOrderList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            HSSFRow row = sheet.createRow(rowNum++);  
		            row.createCell((short) 0).setCellValue(element.get("warehouse_name").toString());  
		            row.createCell((short) 1).setCellValue(element.containsKey("name")&&!WorkerUtil.isNullOrEmpty(element.get("name"))?element.get("name").toString():"");  
		            row.createCell((short) 2).setCellValue(element.get("location_barcode").toString());  
		            row.createCell((short) 3).setCellValue(element.containsKey("area")&&!WorkerUtil.isNullOrEmpty(element.get("area"))?element.get("area").toString():"");  
		            row.createCell((short) 4).setCellValue(LOCATIONTYPEMAP.get(element.get("location_type").toString()).toString());  
		            row.createCell((short) 5).setCellValue(element.containsKey("circle_class")&&!WorkerUtil.isNullOrEmpty(element.get("circle_class"))?element.get("circle_class").toString():"");  
		            row.createCell((short) 6).setCellValue(element.containsKey("price_class")&&!WorkerUtil.isNullOrEmpty(element.get("price_class"))?element.get("price_class").toString():"");  
		            row.createCell((short) 7).setCellValue(element.containsKey("can_mix_product")&&!WorkerUtil.isNullOrEmpty(element.get("can_mix_product"))?element.get("can_mix_product").toString():"");  
		            row.createCell((short) 8).setCellValue(element.containsKey("can_mix_batch")&&!WorkerUtil.isNullOrEmpty(element.get("can_mix_batch"))?element.get("can_mix_batch").toString():"");  
		            row.createCell((short) 9).setCellValue(element.containsKey("not_auto_recmd")&&!WorkerUtil.isNullOrEmpty(element.get("not_auto_recmd"))?element.get("not_auto_recmd").toString():"");  
		            row.createCell((short) 10).setCellValue(element.containsKey("max_lpn_qty")&&!WorkerUtil.isNullOrEmpty(element.get("max_lpn_qty"))?element.get("max_lpn_qty").toString():"");  
		            row.createCell((short) 11).setCellValue(element.containsKey("volume")&&!WorkerUtil.isNullOrEmpty(element.get("volume"))?element.get("volume").toString():"");  
		            row.createCell((short) 12).setCellValue(element.containsKey("weight")&&!WorkerUtil.isNullOrEmpty(element.get("weight"))?element.get("weight").toString():"");  
		            row.createCell((short) 13).setCellValue(element.containsKey("length")&&!WorkerUtil.isNullOrEmpty(element.get("length"))?element.get("length").toString():"");  
		            row.createCell((short) 14).setCellValue(element.containsKey("width")&&!WorkerUtil.isNullOrEmpty(element.get("width"))?element.get("width").toString():""); 
		            row.createCell((short) 15).setCellValue(element.containsKey("height")&&!WorkerUtil.isNullOrEmpty(element.get("height"))?element.get("height").toString():"");  
		            row.createCell((short) 16).setCellValue(element.containsKey("axis_x")&&!WorkerUtil.isNullOrEmpty(element.get("axis_x"))?element.get("axis_x").toString():"");  
		            row.createCell((short) 17).setCellValue(element.containsKey("axis_y")&&!WorkerUtil.isNullOrEmpty(element.get("axis_y"))?element.get("axis_y").toString():"");  
		            row.createCell((short) 18).setCellValue(element.containsKey("axis_z")&&!WorkerUtil.isNullOrEmpty(element.get("axis_z"))?element.get("axis_z").toString():"");  
		            row.createCell((short) 19).setCellValue(element.containsKey("max_prod_qty")&&!WorkerUtil.isNullOrEmpty(element.get("max_prod_qty"))?element.get("max_prod_qty").toString():"");  
		            row.createCell((short) 20).setCellValue(element.containsKey("ignore_lpn")&&!WorkerUtil.isNullOrEmpty(element.get("ignore_lpn"))?element.get("ignore_lpn").toString():"");  
		            row.createCell((short) 21).setCellValue(element.containsKey("putaway_seq")&&!WorkerUtil.isNullOrEmpty(element.get("putaway_seq"))?element.get("putaway_seq").toString():"");  
		            row.createCell((short) 22).setCellValue(element.containsKey("pick_seq")&&!WorkerUtil.isNullOrEmpty(element.get("pick_seq"))?element.get("pick_seq").toString():"");  
		            row.createCell((short) 23).setCellValue(element.containsKey("is_delete")&&!WorkerUtil.isNullOrEmpty(element.get("is_delete"))?element.get("is_delete").toString():"");  
		      
		        }  
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell((short) 0).setCellValue("TOTAL:"+(rowNum-1));  
	        }else if("exportmove".equals(type)){
	        	List moveList = (List) model.get("moveList");
	        	String excelName = "移库资料查询信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    

				long dataIndex = 1;
	        	Integer aa=0;
	        	HSSFSheet sheet = workbook.createSheet("moveList");  
			    HSSFRow header = sheet.createRow(0); // 第0行
			    header.createCell((short) 0).setCellValue("货主");  
		        header.createCell((short) 1).setCellValue("商品编码");  
		        header.createCell((short) 2).setCellValue("商品名称");
		        header.createCell((short) 3).setCellValue("移库数量");
		        header.createCell((short) 4).setCellValue("移库时间");  
		        header.createCell((short) 5).setCellValue("移出储位");  
		        header.createCell((short) 6).setCellValue("移入储位");  
		        header.createCell((short) 7).setCellValue("工号");
		        header.createCell((short) 8).setCellValue("姓名");
		        header.createCell((short) 9).setCellValue("商品批次号"); 
		        // 填充数据  
		        int rowNum = 1;  
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd")); 
		        for (Iterator iter = moveList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            // 产生Excel表头  
		            if (dataIndex % 65000== 0) {
		            	aa++;
		            	sheet = workbook.createSheet("moveList"+aa);  
		            	header = sheet.createRow(0); // 第0行
				        // 产生标题列  
				        header.createCell((short) 0).setCellValue("货主");  
				        header.createCell((short) 1).setCellValue("商品编码");  
				        header.createCell((short) 2).setCellValue("商品名称");
				        header.createCell((short) 3).setCellValue("移库数量");
				        header.createCell((short) 4).setCellValue("移库时间");  
				        header.createCell((short) 5).setCellValue("移出储位");  
				        header.createCell((short) 6).setCellValue("移入储位");  
				        header.createCell((short) 7).setCellValue("工号");
				        header.createCell((short) 8).setCellValue("姓名"); 
				        header.createCell((short) 9).setCellValue("商品批次号"); 
				        rowNum=1;
		            }
		            HSSFRow row = sheet.createRow(rowNum++);
			        // 产生标题列  
		            row.createCell((short) 0).setCellValue(element.get("name").toString());  
		            row.createCell((short) 1).setCellValue(element.get("barcode").toString());  
		            row.createCell((short) 2).setCellValue(element.get("product_name").toString());  
		            row.createCell((short) 3).setCellValue(element.get("quantity").toString());  
		            row.createCell((short) 4).setCellValue(element.get("last_updated_time").toString());  
		            row.createCell((short) 5).setCellValue(element.get("in_location_barcode").toString());
		            row.createCell((short) 6).setCellValue(element.get("out_location_barcode").toString()); 
		            row.createCell((short) 7).setCellValue(element.containsKey("username")&&!WorkerUtil.isNullOrEmpty(element.get("username"))?element.get("username").toString():"");
		            row.createCell((short) 8).setCellValue(element.containsKey("realname")&&!WorkerUtil.isNullOrEmpty(element.get("realname"))?element.get("realname").toString():"");	
		            row.createCell((short) 9).setCellValue(element.containsKey("batch_sn")&&!WorkerUtil.isNullOrEmpty(element.get("batch_sn"))?element.get("batch_sn").toString():"-");

		            dataIndex++;
		        }
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell( 0).setCellValue("TOTAL:"+(rowNum-1));    
	        }else if("exportrecheck".equals(type)){
	        	List recheckList = (List) model.get("recheckList");
	        	String excelName = "复核资料查询信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));
	        	long dataIndex = 1;
	        	Integer aa=0;
	        	HSSFSheet sheet = workbook.createSheet("recheckList");  
			    HSSFRow header = sheet.createRow(0); // 第0行
			    header.createCell((short) 0).setCellValue("货主");  
		        header.createCell((short) 1).setCellValue("商品编码");  
		        header.createCell((short) 2).setCellValue("商品名称");
		        header.createCell((short) 3).setCellValue("复核数量");
		        header.createCell((short) 4).setCellValue("复核时间");  
		        header.createCell((short) 5).setCellValue("批拣单号");  
		        header.createCell((short) 6).setCellValue("订单号");  
		        header.createCell((short) 7).setCellValue("工号");
		        header.createCell((short) 8).setCellValue("姓名");  
		        // 填充数据  
		        int rowNum = 1;  
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd")); 
		        for (Iterator iter = recheckList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            // 产生Excel表头  
		            if (dataIndex % 65000== 0) {
		            	aa++;
		            	sheet = workbook.createSheet("recheckList"+aa);  
		            	header = sheet.createRow(0); // 第0行
				        // 产生标题列  
				        header.createCell((short) 0).setCellValue("货主");  
				        header.createCell((short) 1).setCellValue("商品编码");  
				        header.createCell((short) 2).setCellValue("商品名称");
				        header.createCell((short) 3).setCellValue("复核数量");
				        header.createCell((short) 4).setCellValue("复核时间");  
				        header.createCell((short) 5).setCellValue("批拣单号");  
				        header.createCell((short) 6).setCellValue("订单号");  
				        header.createCell((short) 7).setCellValue("工号");
				        header.createCell((short) 8).setCellValue("姓名"); 
				        rowNum=1;
		            }
		            HSSFRow row = sheet.createRow(rowNum++);
			        // 产生标题列  
		            row.createCell((short) 0).setCellValue(element.get("name").toString());  
		            row.createCell((short) 1).setCellValue(element.get("barcode").toString());  
		            row.createCell((short) 2).setCellValue(element.get("product_name").toString());  
		            row.createCell((short) 3).setCellValue(element.get("total").toString());  
		            row.createCell((short) 4).setCellValue(element.get("recheck_time").toString());  
		            row.createCell((short) 5).setCellValue(element.get("batch_pick_sn").toString());
		            row.createCell((short) 6).setCellValue(element.get("order_id").toString()); 
		            row.createCell((short) 7).setCellValue(element.containsKey("username")&&!WorkerUtil.isNullOrEmpty(element.get("username"))?element.get("username").toString():"");
		            row.createCell((short) 8).setCellValue(element.containsKey("realname")&&!WorkerUtil.isNullOrEmpty(element.get("realname"))?element.get("realname").toString():"");		         
		            dataIndex++;
		        }
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell( 0).setCellValue("TOTAL:"+(rowNum-1));  
		
	        }else if("exportbh".equals(type)){
	        	List bhList = (List) model.get("bhList");
	        	String excelName = "补货资料查询信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    

		        // 产生Excel表头  
				long dataIndex = 1;
	        	Integer aa=0;
	        	HSSFSheet sheet = workbook.createSheet("bhList");  
			    HSSFRow header = sheet.createRow(0); // 第0行
			    header.createCell((short) 0).setCellValue("货主");  
		        header.createCell((short) 1).setCellValue("商品编码");  
		        header.createCell((short) 2).setCellValue("商品名称");
		        header.createCell((short) 3).setCellValue("上架数量");
		        header.createCell((short) 4).setCellValue("补货时间");  
		        header.createCell((short) 5).setCellValue("下架储位");  
		        header.createCell((short) 6).setCellValue("上架储位");
		        header.createCell((short) 7).setCellValue("补货类型");
		        header.createCell((short) 8).setCellValue("工号");
		        header.createCell((short) 9).setCellValue("姓名");
		        header.createCell((short) 10).setCellValue("商品批次号");
		        // 填充数据  
		        int rowNum = 1;  
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd")); 
		        for (Iterator iter = bhList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            // 产生Excel表头  
		            if (dataIndex % 65000== 0) {
		            	aa++;
		            	sheet = workbook.createSheet("bhList"+aa);  
		            	header = sheet.createRow(0); // 第0行
				        // 产生标题列  
				        header.createCell((short) 0).setCellValue("货主");  
						header.createCell((short) 0).setCellValue("货主");  
				        header.createCell((short) 1).setCellValue("商品编码");  
				        header.createCell((short) 2).setCellValue("商品名称");
				        header.createCell((short) 3).setCellValue("上架数量");
				        header.createCell((short) 4).setCellValue("补货时间");  
				        header.createCell((short) 5).setCellValue("下架储位");  
				        header.createCell((short) 6).setCellValue("上架储位");
				        header.createCell((short) 7).setCellValue("补货类型");
				        header.createCell((short) 8).setCellValue("工号");
				        header.createCell((short) 9).setCellValue("姓名");  
				        header.createCell((short) 10).setCellValue("商品批次号");
				        rowNum=1;
		            }
		            HSSFRow row = sheet.createRow(rowNum++);
			        // 产生标题列  
		            row.createCell((short) 0).setCellValue(element.get("name").toString());  
		            row.createCell((short) 1).setCellValue(element.get("barcode").toString());  
		            row.createCell((short) 2).setCellValue(element.get("product_name").toString());  
		            row.createCell((short) 3).setCellValue(element.get("quantity").toString());  
		            row.createCell((short) 4).setCellValue(element.get("last_updated_time").toString());  
		            row.createCell((short) 5).setCellValue(element.get("out_location_barcode").toString());
		            row.createCell((short) 6).setCellValue(element.get("in_location_barcode").toString()); 
		            row.createCell((short) 7).setCellValue(element.get("task_level").toString()); 
		            row.createCell((short) 8).setCellValue(element.containsKey("username")&&!WorkerUtil.isNullOrEmpty(element.get("username"))?element.get("username").toString():"");
		            row.createCell((short) 9).setCellValue(element.containsKey("realname")&&!WorkerUtil.isNullOrEmpty(element.get("realname"))?element.get("realname").toString():"");	
		            row.createCell((short) 10).setCellValue(element.containsKey("batch_sn")&&!WorkerUtil.isNullOrEmpty(element.get("batch_sn"))?element.get("batch_sn").toString():"-");	         

		            dataIndex++;
		        }
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell( 0).setCellValue("TOTAL:"+(rowNum-1));   
	        }else if("exportmaintain".equals(type)){
	        	List maintainList = (List) model.get("maintainList");
	        	String excelName = "库存调整资料查询信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    

		        // 产生Excel表头  
				long dataIndex = 1;
	        	Integer aa=0;
	        	HSSFSheet sheet = workbook.createSheet("maintainList");  
			    HSSFRow header = sheet.createRow(0); // 第0行
			     header.createCell((short) 0).setCellValue("货主");  
		        header.createCell((short) 1).setCellValue("商品编码");  
		        header.createCell((short) 2).setCellValue("商品名称");
		        header.createCell((short) 3).setCellValue("调整类型");
		        header.createCell((short) 4).setCellValue("调整数量");  
		        header.createCell((short) 5).setCellValue("调整日期");  
		        header.createCell((short) 6).setCellValue("操作工号");    
		        // 填充数据  
		        int rowNum = 1;  
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd")); 
		        for (Iterator iter = maintainList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            // 产生Excel表头  
		            if (dataIndex % 65000== 0) {
		            	aa++;
		            	sheet = workbook.createSheet("maintainList"+aa);  
		            	header = sheet.createRow(0); // 第0行
				        // 产生标题列  
				        header.createCell((short) 0).setCellValue("货主");  
						header.createCell((short) 0).setCellValue("货主");  
				        header.createCell((short) 1).setCellValue("商品编码");  
				        header.createCell((short) 2).setCellValue("商品名称");
				        header.createCell((short) 3).setCellValue("调整类型");
				        header.createCell((short) 4).setCellValue("调整数量");  
				        header.createCell((short) 5).setCellValue("调整日期");  
				        header.createCell((short) 6).setCellValue("操作工号"); 
				        rowNum=1;
		            }
		            HSSFRow row = sheet.createRow(rowNum++);
			        // 产生标题列  
		            row.createCell((short) 0).setCellValue(element.get("name").toString());  
		            row.createCell((short) 1).setCellValue(element.get("barcode").toString());  
		            row.createCell((short) 2).setCellValue(element.get("product_name").toString());  
		            row.createCell((short) 3).setCellValue(element.get("order_type").toString());  
		            row.createCell((short) 4).setCellValue(element.get("total").toString());  
		            row.createCell((short) 5).setCellValue(element.get("last_update_time").toString());
		            row.createCell((short) 6).setCellValue(element.get("created_user").toString());	         
		            dataIndex++;
		        }
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell( 0).setCellValue("TOTAL:"+(rowNum-1));    
	        }else if("exportpurchase".equals(type)){
	        	List purchaseList = (List) model.get("purchaseList");
	        	String excelName = "入库资料查询信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    

		        // 产生Excel表头  
				long dataIndex = 1;
	        	Integer aa=0;
	        	HSSFSheet sheet = workbook.createSheet("purchaseList");  
			    HSSFRow header = sheet.createRow(0); // 第0行
			    header.createCell((short) 0).setCellValue("货主");  
		        header.createCell((short) 1).setCellValue("商品编码");  
		        header.createCell((short) 2).setCellValue("商品名称");
		        header.createCell((short) 3).setCellValue("入库数量");
		        header.createCell((short) 4).setCellValue("入库时间");  
		        header.createCell((short) 5).setCellValue("入库类型");  
		        header.createCell((short) 6).setCellValue("订单号");
		        header.createCell((short) 7).setCellValue("采购批次号");
		        header.createCell((short) 8).setCellValue("工号");
		        header.createCell((short) 9).setCellValue("姓名"); 
		        header.createCell((short) 10).setCellValue("商品批次号");
		        // 填充数据  
		        int rowNum = 1;  
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd")); 
		        for (Iterator iter = purchaseList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            // 产生Excel表头  
		            if (dataIndex % 65000== 0) {
		            	aa++;
		            	sheet = workbook.createSheet("purchaseList"+aa);  
		            	header = sheet.createRow(0); // 第0行
				        // 产生标题列  
				        header.createCell((short) 0).setCellValue("货主");  
						header.createCell((short) 0).setCellValue("货主");  
				        header.createCell((short) 1).setCellValue("商品编码");  
				        header.createCell((short) 2).setCellValue("商品名称");
				        header.createCell((short) 3).setCellValue("入库数量");
				        header.createCell((short) 4).setCellValue("入库时间");  
				        header.createCell((short) 5).setCellValue("入库类型");  
				        header.createCell((short) 6).setCellValue("订单号");
				        header.createCell((short) 7).setCellValue("采购批次号");
				        header.createCell((short) 8).setCellValue("工号");
				        header.createCell((short) 9).setCellValue("姓名");
				        header.createCell((short) 10).setCellValue("商品批次号");
				        rowNum=1;
		            }
		            HSSFRow row = sheet.createRow(rowNum++);
			        // 产生标题列  
		            row.createCell((short) 0).setCellValue(element.get("name").toString());  
		            row.createCell((short) 1).setCellValue(element.get("barcode").toString());  
		            row.createCell((short) 2).setCellValue(element.get("product_name").toString());
		            row.createCell((short) 3).setCellValue(element.get("total").toString());
		            row.createCell((short) 4).setCellValue(element.get("last_updated_time").toString());
		            row.createCell((short) 5).setCellValue(element.get("order_type").toString());  
		            row.createCell((short) 6).setCellValue(element.get("oms_order_sn").toString());
		            row.createCell((short) 7).setCellValue(element.containsKey("batch_order_sn")&&!WorkerUtil.isNullOrEmpty(element.get("batch_order_sn"))?element.get("batch_order_sn").toString():"-");
		            row.createCell((short) 8).setCellValue(element.containsKey("username")&&!WorkerUtil.isNullOrEmpty(element.get("username"))?element.get("username").toString():"");
		            row.createCell((short) 9).setCellValue(element.containsKey("realname")&&!WorkerUtil.isNullOrEmpty(element.get("realname"))?element.get("realname").toString():"");
		            row.createCell((short) 10).setCellValue(element.containsKey("batch_sn")&&!WorkerUtil.isNullOrEmpty(element.get("batch_sn"))?element.get("batch_sn").toString():"-");

		            dataIndex++;
		        }
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell( 0).setCellValue("TOTAL:"+(rowNum-1));    
	        }else if("exportgrounding".equals(type)){
	        	List groundingList = (List) model.get("groundingList");
	        	String excelName = "上架资料查询信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    

		        // 产生Excel表头  
				long dataIndex = 1;
	        	Integer aa=0;
	        	HSSFSheet sheet = workbook.createSheet("groundingList");  
			    HSSFRow header = sheet.createRow(0); // 第0行
			     header.createCell((short) 0).setCellValue("货主");  
		        header.createCell((short) 1).setCellValue("商品编码");  
		        header.createCell((short) 2).setCellValue("商品名称");
		        header.createCell((short) 3).setCellValue("上架数量");
		        header.createCell((short) 4).setCellValue("上架时间");  
		        header.createCell((short) 5).setCellValue("上架类型");
		        header.createCell((short) 5).setCellValue("上架库位"); 
		        header.createCell((short) 6).setCellValue("订单号");
		        header.createCell((short) 7).setCellValue("工号");
		        header.createCell((short) 8).setCellValue("姓名");
		        header.createCell((short) 9).setCellValue("商品批次号");
		        // 填充数据  
		        int rowNum = 1;  
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd")); 
		        for (Iterator iter = groundingList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            // 产生Excel表头  
		            if (dataIndex % 65000== 0) {
		            	aa++;
		            	sheet = workbook.createSheet("groundingList"+aa);  
		            	header = sheet.createRow(0); // 第0行
				        // 产生标题列  
				        header.createCell((short) 0).setCellValue("货主");  
				    header.createCell((short) 0).setCellValue("货主");  
			        header.createCell((short) 1).setCellValue("商品编码");  
			        header.createCell((short) 2).setCellValue("商品名称");
			        header.createCell((short) 3).setCellValue("上架数量");
			        header.createCell((short) 4).setCellValue("上架时间");  
			        header.createCell((short) 5).setCellValue("上架类型");
			        header.createCell((short) 5).setCellValue("上架库位"); 
			        header.createCell((short) 6).setCellValue("订单号");
			        header.createCell((short) 7).setCellValue("工号");
			        header.createCell((short) 8).setCellValue("姓名"); 
			        header.createCell((short) 9).setCellValue("商品批次号");
				        rowNum=1;
		            }
		            HSSFRow row = sheet.createRow(rowNum++);
			        // 产生标题列  
		            row.createCell((short) 0).setCellValue(element.get("name").toString());  
		            row.createCell((short) 1).setCellValue(element.get("barcode").toString());  
		            row.createCell((short) 2).setCellValue(element.get("product_name").toString());
		            row.createCell((short) 3).setCellValue(element.get("total").toString());
		            row.createCell((short) 4).setCellValue(element.get("last_updated_time").toString());
		            row.createCell((short) 5).setCellValue(element.get("order_type").toString()); 
		            row.createCell((short) 5).setCellValue(element.get("location_barcode").toString()); 
		            row.createCell((short) 6).setCellValue(element.get("oms_order_sn").toString());
		            row.createCell((short) 7).setCellValue(element.containsKey("created_user")&&!WorkerUtil.isNullOrEmpty(element.get("created_user"))?element.get("created_user").toString():"-");
		            row.createCell((short) 8).setCellValue(element.containsKey("realname")&&!WorkerUtil.isNullOrEmpty(element.get("realname"))?element.get("realname").toString():"");
		            row.createCell((short) 9).setCellValue(element.containsKey("batch_sn")&&!WorkerUtil.isNullOrEmpty(element.get("batch_sn"))?element.get("batch_sn").toString():"-");
		            dataIndex++;
		        }
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell( 0).setCellValue("TOTAL:"+(rowNum-1));  
	        }else if("exportpick".equals(type)){
	        	List pickList = (List) model.get("pickList");
	        	String excelName = "拣货资料查询信息.xls";  
	        	// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
	        	response.setContentType("APPLICATION/OCTET-STREAM");  
	        	response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    

		        // 产生Excel表头  
				long dataIndex = 1;
	        	Integer aa=0;
	        	HSSFSheet sheet = workbook.createSheet("pickList");  
			    HSSFRow header = sheet.createRow(0); // 第0行
			    header.createCell((short) 0).setCellValue("货主");  
		        header.createCell((short) 1).setCellValue("商品编码");  
		        header.createCell((short) 2).setCellValue("商品名称");
		        header.createCell((short) 3).setCellValue("拣货数量");
		        header.createCell((short) 4).setCellValue("拣货时间");  
		        header.createCell((short) 5).setCellValue("储位");  
		        header.createCell((short) 6).setCellValue("批拣单号");
		        header.createCell((short) 7).setCellValue("工号");
		        header.createCell((short) 8).setCellValue("姓名");      
		        // 填充数据  
		        int rowNum = 1;  
		        HSSFCellStyle cellStyle = workbook.createCellStyle();  
		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd")); 
		        for (Iterator iter = pickList.iterator(); iter.hasNext();) {  
		            Map element = (Map) iter.next();  
		            // 产生Excel表头  
		            if (dataIndex % 65000== 0) {
		            	aa++;
		            	sheet = workbook.createSheet("pickList"+aa);  
		            	header = sheet.createRow(0); // 第0行
				        // 产生标题列  
				        header.createCell((short) 0).setCellValue("货主");  
						header.createCell((short) 0).setCellValue("货主");  
				        header.createCell((short) 1).setCellValue("商品编码");  
				        header.createCell((short) 2).setCellValue("商品名称");
				        header.createCell((short) 3).setCellValue("拣货数量");
				        header.createCell((short) 4).setCellValue("拣货时间");  
				        header.createCell((short) 5).setCellValue("储位");  
				        header.createCell((short) 6).setCellValue("批拣单号");
				        header.createCell((short) 7).setCellValue("工号");
				        header.createCell((short) 8).setCellValue("姓名");  
				        rowNum=1;
		            }
		            HSSFRow row = sheet.createRow(rowNum++);
			        // 产生标题列  
		            row.createCell((short) 0).setCellValue(element.get("name").toString());  
		            row.createCell((short) 1).setCellValue(element.get("barcode").toString());  
		            row.createCell((short) 2).setCellValue(element.get("product_name").toString());
		            row.createCell((short) 3).setCellValue(element.get("total").toString());
		            row.createCell((short) 4).setCellValue(element.get("bind_time").toString());
		            row.createCell((short) 5).setCellValue("location_barcode");
		            row.createCell((short) 6).setCellValue(element.get("batch_pick_sn").toString());  
		            row.createCell((short) 7).setCellValue(element.containsKey("username")&&!WorkerUtil.isNullOrEmpty(element.get("username"))?element.get("username").toString():"");
		            row.createCell((short) 8).setCellValue(element.containsKey("realname")&&!WorkerUtil.isNullOrEmpty(element.get("realname"))?element.get("realname").toString():"");	    
		            dataIndex++;
		        }
		        // 列总和计算  
		        HSSFRow row = sheet.createRow(rowNum);  
		        row.createCell( 0).setCellValue("TOTAL:"+(rowNum-1));
	        }
	  
	}
	
	private static Map<String,Object> ORDERSTATUSMAP = null;
	static{
		ORDERSTATUSMAP = new HashMap<String,Object>();
		ORDERSTATUSMAP.put("ACCEPT", "未处理");
		ORDERSTATUSMAP.put("BATCH_PICK", "已分配");
		ORDERSTATUSMAP.put("PICKING", "拣货中");
		ORDERSTATUSMAP.put("RECHECKED", "已复核");
		ORDERSTATUSMAP.put("WEIGHED", "已称重");
		ORDERSTATUSMAP.put("DELIVERED", "已发货");
		ORDERSTATUSMAP.put("FULFILLED", "已出库");
		ORDERSTATUSMAP.put("CANCEL", "已取消");
	}
	
	private static Map<String, Object> LOCATIONTYPEMAP = null;
	static {
		LOCATIONTYPEMAP = new HashMap<String, Object>();
		LOCATIONTYPEMAP.put("STOCK_LOCATION","存储区");
		LOCATIONTYPEMAP.put("PIECE_PICK_LOCATION","件拣货区");
		LOCATIONTYPEMAP.put( "BOX_PICK_LOCATION","箱拣货区");
		LOCATIONTYPEMAP.put( "RETURN_LOCATION","Return区");
		LOCATIONTYPEMAP.put( "QUALITY_CHECK_LOCATION","质检区");
		LOCATIONTYPEMAP.put( "DEFECTIVE_LOCATION","二手区");
		LOCATIONTYPEMAP.put( "PACKBOX_LOCATION","耗材区");
	}

}
