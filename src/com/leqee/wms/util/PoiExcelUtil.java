package com.leqee.wms.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PoiExcelUtil {

	/**
	 * 读取Excel文件，支持.xls和.xlsx2种版本
	 * @return List<Object><br/>
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static List<Object> readExcel(String file) throws FileNotFoundException, IOException{
		Workbook workbook = null;
		Sheet sheet = null;
		Row row = null;
		Cell cell = null;
		if(file.indexOf("xlsx") != -1){
			workbook = new XSSFWorkbook(new FileInputStream(file));
		}else if(file.indexOf("xls") != -1){
			workbook = new HSSFWorkbook(new FileInputStream(file));
		}else{
			//System.out.println("该文件不是excel文件！");
			return null;
		}
		sheet = workbook.getSheetAt(0);
		//获取sheet的总行数
		int rowsNum = sheet.getLastRowNum();
		//获取sheet的总列数
		int colsNum = sheet.getRow(0).getLastCellNum();

		String cellValue ;
		//获取row
		for(int i = 0 ; i < rowsNum ; i++){
			row = sheet.getRow(i);
			//System.out.print("行号： " + row.getRowNum());
			for(int j = 0 ; j < colsNum ; j++){
				cell = row.getCell(j);
				//将单元格设置为String，取的时候再转
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cellValue = cell.getStringCellValue();
				//System.out.print("\t" + cellValue);
			}
			//System.out.println();
		}
		return null;
	}

	
	/**
	 * 创建Excel文件,支持.xls和.xlsx2种类型<br/>
	 * 示例：file = "d:\\helloworld.xls"
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * 
	 * Map<String, Object> excelMap :
	 * 	String sheetType
	 * 	Integer sheetNum 
	 *  List<Map<String,Object>> sheetList 
	 *  	String sheetName
	 *  	List<Map<String,Object>> list 
	 *  		.... 
	 */
	public static void createxlsExcel(Map<String, Object> excelMap,String file) throws FileNotFoundException, IOException{
		//数据可以没有，列一定要有
		if(excelMap == null || excelMap.size() == 0){
			return;
		}
		Workbook workbook = null;
		Row row = null;
		Cell cell = null;
		if(file.indexOf("xlsx") != -1){
			workbook = new XSSFWorkbook();
		}else if(file.indexOf("xls") != -1){
			workbook = new HSSFWorkbook();
		}else{
			//System.out.println("excel文件的后缀名是xls或xlsx！请检查！");
			return;
		}
		
		//列头样式
		Font fontColumn = workbook.createFont();
		CellStyle cellStyleColumn = workbook.createCellStyle();
		fontColumn.setFontHeightInPoints((short) 10);
		fontColumn.setColor(IndexedColors.BLACK.getIndex());
		fontColumn.setBoldweight(Font.BOLDWEIGHT_BOLD);
		cellStyleColumn.setFont(fontColumn);
		cellStyleColumn.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyleColumn.setBorderRight(CellStyle.BORDER_THIN);
		cellStyleColumn.setBorderTop(CellStyle.BORDER_THIN);
		cellStyleColumn.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyleColumn.setAlignment(CellStyle.ALIGN_CENTER);
		
		//内容样式
		Font fontContent = workbook.createFont();
		CellStyle cellStyleContent = workbook.createCellStyle();
		fontContent.setFontHeightInPoints((short) 10);
		fontContent.setColor(IndexedColors.BLACK.getIndex());
		fontContent.setBoldweight(Font.DEFAULT_CHARSET);
		cellStyleContent.setFont(fontContent);
		cellStyleContent.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyleContent.setBorderRight(CellStyle.BORDER_THIN);
		cellStyleContent.setBorderTop(CellStyle.BORDER_THIN);
		cellStyleContent.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyleContent.setAlignment(CellStyle.ALIGN_CENTER);

		String sheetType = String.valueOf(excelMap.get("sheetType"));
		Integer sheetNum = Integer.parseInt(String.valueOf(excelMap.get("sheetNum")));
		if(sheetNum.equals(0)){
			return;
		}
		List<Map<String,Object>> sheetList = (List<Map<String, Object>>) excelMap.get("sheetList");
		for (int i = 0; i < sheetNum; i++) {
			Map<String,Object> sheetMap = sheetList.get(i);
			String sheetName = String.valueOf(sheetMap.get("sheetName"));
			List<Map<String,Object>> list = (List<Map<String, Object>>) sheetMap.get("list");
			
			Sheet sheet = workbook.createSheet(sheetName);
			if(!WorkerUtil.isNullOrEmpty(list)){
				for (int j = 0; j < list.size(); j++) {
					Row columnRow = sheet.createRow(j);
					Map<String,Object> map = list.get(j);
//					sheet.setColumnWidth(第X列,宽度);
					if("reportPerformance".equalsIgnoreCase(sheetType)){
						//员工绩效报表
						cell = columnRow.createCell(0);
						cell.setCellValue(String.valueOf(map.get("type")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(1);
						cell.setCellValue(String.valueOf(map.get("roles")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(2);
						cell.setCellValue(String.valueOf(map.get("username")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(3);
						cell.setCellValue(String.valueOf(map.get("realname")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(4);
						cell.setCellValue(String.valueOf(map.get("customerName")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(5);
						cell.setCellValue(String.valueOf(map.get("orderTaskNum")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(6);
						cell.setCellValue(String.valueOf(map.get("skuNum")));
						cell.setCellStyle(cellStyleContent);
						
						if(!WorkerUtil.isNullOrEmpty(map.get("productNum"))){
							cell = columnRow.createCell(7);
							cell.setCellValue(String.valueOf(map.get("productNum")));
							cell.setCellStyle(cellStyleContent);
						}
					}else if ("productValidityExpired".equalsIgnoreCase(sheetType)) {
						//生产日期预警
						cell = columnRow.createCell(0);
						cell.setCellValue(String.valueOf(map.get("warehouse_name")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(1);
						cell.setCellValue(String.valueOf(map.get("location_barcode")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(2);
						cell.setCellValue(String.valueOf(map.get("name")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(3);
						cell.setCellValue(String.valueOf(map.get("barcode")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(4);
						cell.setCellValue(String.valueOf(map.get("product_name")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(5);
						cell.setCellValue(String.valueOf(map.get("qty_total")));
						cell.setCellStyle(cellStyleContent);
						
						
						cell = columnRow.createCell(6);
						cell.setCellValue(String.valueOf(map.get("batch_sn")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(7);
						cell.setCellValue(String.valueOf(map.get("v2")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(8);
						cell.setCellValue(String.valueOf(map.get("validity")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(9);
						cell.setCellValue(String.valueOf(map.get("validity_unit")));
						cell.setCellStyle(cellStyleContent);
						
						cell = columnRow.createCell(10);
						cell.setCellValue(String.valueOf(map.get("chinese_validity_status")));
						cell.setCellStyle(cellStyleContent);
						
						
					}
					
				}
				//如果值的类型为时间，可用此格式化时间
				//cellStyleContent.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd")); 
			}
		}
		OutputStream outputStream = new FileOutputStream(file);
		workbook.write(outputStream);
		outputStream.close();
	}
}
