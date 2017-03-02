package com.leqee.wms.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.biz.LocationBiz;
import com.leqee.wms.biz.ProductBiz;
import com.leqee.wms.biz.ReplenishmentBiz;
import com.leqee.wms.biz.ReplenishmentUrgentBiz;
import com.leqee.wms.biz.WarehouseCustomerBiz;
import com.leqee.wms.biz.impl.InventoryBizImpl;
import com.leqee.wms.dao.LocationDao;
import com.leqee.wms.dao.OrderGoodsDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ReplenishmentDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.ConfigReplenishmentUrgent;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.WorkerUtil;
/**
 * 补货规则入口
 * @author hchen1
 *
 */

@Controller
@RequestMapping(value="/replenishmentQuery")  //指定根路径
public class ReplenishmentQueryController  {
	@Autowired
	ReplenishmentBiz replenishmentBiz;
	@Autowired
	LocationBiz locationBiz;
	@Autowired
	ProductBiz productBiz;
	@Autowired
	WarehouseCustomerBiz warehouseCustomerBiz;
	@Autowired
	ReplenishmentUrgentBiz replenishmentUrgentBiz;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	@Autowired
	ProductDao productDao;
	
	private Logger logger = Logger.getLogger(ReplenishmentQueryController.class);
	private final static Integer pageSize = 15 ;
/**
 * 补货规则查询
 * @param model
 * @param request
 * @return
 */
	
	@RequestMapping(value="/replenishment")
	
	public String replenishmentQuery(Map<String,Object> model,HttpServletRequest request){
		
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String barcode = request.getParameter("barcode");
		String customer_id = request.getParameter("customer_id");
		String product_name = request.getParameter("product_name");
	
		List<WarehouseCustomer> warehouseCustomerList = warehouseCustomerBiz.findAll();
		
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customer_id", customer_id);
		}

		if(!WorkerUtil.isNullOrEmpty(product_name)){
			searchMap.put("product_name", product_name);
		}
		
		model.put("warehouseCustomerList",warehouseCustomerList);
		return  "/replenishment/replenishment_rule_setting";
	}
	/**
	 * 根据商品条码查找商品名称
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/getNameByBarcode")
	@ResponseBody
	public Map<String, Object> getNameByBarcode( HttpServletRequest req){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String barcode = req.getParameter("barcode");
		if(WorkerUtil.isNullOrEmpty(barcode)){
			resultMap.put("result",Response.FAILURE);
			resultMap.put("note", "商品条码为空，不能加载商品信息");
			return resultMap;
		}
		Integer customerId = Integer.parseInt(req.getParameter("customer_id"));
		if(WorkerUtil.isNullOrEmpty(customerId)){
			resultMap.put("result",Response.FAILURE);
			resultMap.put("note", "货主为空，不能加载商品信息");
			return resultMap;
		}
		Product product = productDao.selectProductByBarcodeCustomer(barcode,customerId);
		if(!WorkerUtil.isNullOrEmpty(product)){
			resultMap.put("result",Response.SUCCESS);
			resultMap.put("note", product.getProduct_name());
		}else{
			resultMap.put("result",Response.FAILURE);
			resultMap.put("note", "根据商品条码"+barcode+"没有找到对应商品信息");
		}
		return resultMap;
	}
	/**
	 * ajax 调用返回数据
	 */
	@RequestMapping(value="/query")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object searchReturn(HttpServletRequest request) {
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Map<String,Object> rerurnMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		String barcode = request.getParameter("barcode");
		String customer_id = request.getParameter("customer_id");
		String product_name = request.getParameter("product_name");
		List<WarehouseCustomer> warehouseCustomerList = warehouseCustomerBiz.findAll();
		String productName = productBiz.selectProductNameByBarcodeCustomer(barcode,customer_id);
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customer_id", customer_id);
		}
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		if(!WorkerUtil.isNullOrEmpty(product_name)){
			searchMap.put("product_name", product_name);
		}
		if(!WorkerUtil.isNullOrEmpty(currentPhysicalWarehouse.getPhysical_warehouse_id())){
			searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		}
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(request.getParameter("currentPage"))){
			page = new PageParameter(1,15);
		}else{
			page = new PageParameter(Integer.valueOf(request.getParameter("currentPage")),Integer.valueOf(request.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		List<Map<String, Object>> searchResultList = replenishmentBiz.selectByMap(searchMap);
		rerurnMap.put("searchResultList", searchResultList);
		rerurnMap.put("warehouseCustomerList", warehouseCustomerList);
		rerurnMap.put("productName", productName);
		rerurnMap.put("customer_id", customer_id);
		rerurnMap.put("page", page);
		return rerurnMap;
	}
	

	/**
	 * 补货规则添加
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/addRule")
	@ResponseBody
	public Object addRule(HttpServletRequest request,Map<String,Object> model) {
		synchronized (this){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		Map<String,Object> resMap = new HashMap<String,Object>();
		Integer customer_id = Integer.parseInt(request.getParameter("customer_id"));
		String product_name = request.getParameter("product_name");
		String barcode = request.getParameter("barcode");
		String operateState =request.getParameter("operateState");
		String from_piece_location_barcode = request.getParameter("from_piece_location_barcode").toUpperCase();
		String to_piece_location_barcode = request.getParameter("to_piece_location_barcode").toUpperCase();
		if(!WorkerUtil.isNullOrEmpty(from_piece_location_barcode) && !WorkerUtil.isNullOrEmpty(to_piece_location_barcode)){
		if(from_piece_location_barcode.compareTo(to_piece_location_barcode)>0)
		{
			WorkerUtil.swap(from_piece_location_barcode, to_piece_location_barcode);
		}
		}
		String piece_location_max_quantity = request.getParameter("piece_location_max_quantity");
		String piece_location_min_quantity = request.getParameter("piece_location_min_quantity");
		String from_box_location_barcode = request.getParameter("from_box_location_barcode").toUpperCase();
		String to_box_location_barcode = request.getParameter("to_box_location_barcode").toUpperCase();
		if(!WorkerUtil.isNullOrEmpty(from_box_location_barcode) && !WorkerUtil.isNullOrEmpty(to_box_location_barcode)){
		if(from_box_location_barcode.compareTo(to_box_location_barcode)>0)
		{
			WorkerUtil.swap(from_box_location_barcode, to_box_location_barcode);
		}
		}
		String str = from_piece_location_barcode+to_piece_location_barcode+from_box_location_barcode+to_box_location_barcode;
		if(str.contains("-")){
			from_piece_location_barcode=WorkerUtil.transferLocationBarcode(from_piece_location_barcode);
			to_piece_location_barcode=WorkerUtil.transferLocationBarcode(to_piece_location_barcode);
			from_box_location_barcode=WorkerUtil.transferLocationBarcode(from_box_location_barcode);
			to_box_location_barcode=WorkerUtil.transferLocationBarcode(to_box_location_barcode);
		}
		String box_location_max_quantity = request.getParameter("box_location_max_quantity");
		String box_location_min_quantity = request.getParameter("box_location_min_quantity");
		Integer product_id = productDao.selectProductIdByBarcodeCustomer(barcode,customer_id);
		Map<String, Object> queryMap = new HashMap<String, Object>();
		Map<String, Object> searchMap = new HashMap<String, Object>();
		Map<String,Object> updateReplenishmentMap = new HashMap<String,Object>();
		queryMap.put("customer_id", customer_id);
		queryMap.put("product_id", product_id);
		queryMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			updateReplenishmentMap.put("customer_id", customer_id);
		}
		if(!WorkerUtil.isNullOrEmpty(product_id)){
			updateReplenishmentMap.put("product_id", product_id);
		}else {
			resMap.put("result", "failure");
			resMap.put("note", "该商品条码不存在");
			return resMap;
		}
		if(!WorkerUtil.isNullOrEmpty(product_name)){
			updateReplenishmentMap.put("product_name", product_name);
		}
		if(!WorkerUtil.isNullOrEmpty(piece_location_max_quantity)){
			updateReplenishmentMap.put("piece_location_max_quantity", piece_location_max_quantity);
		}
		if(!WorkerUtil.isNullOrEmpty(piece_location_min_quantity)){
			updateReplenishmentMap.put("piece_location_min_quantity", piece_location_min_quantity);
		}
		if(!WorkerUtil.isNullOrEmpty(box_location_max_quantity)){
			updateReplenishmentMap.put("box_location_max_quantity", box_location_max_quantity);
		}
		if(!WorkerUtil.isNullOrEmpty(box_location_min_quantity)){
			updateReplenishmentMap.put("box_location_min_quantity", box_location_min_quantity);
		}
		if(!WorkerUtil.isNullOrEmpty(currentPhysicalWarehouse.getPhysical_warehouse_id())){
			updateReplenishmentMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		}
		searchMap.put("from_piece_location_barcode", from_piece_location_barcode);
		searchMap.put("to_piece_location_barcode", to_piece_location_barcode);
		searchMap.put("from_box_location_barcode", from_box_location_barcode);
		searchMap.put("to_box_location_barcode", to_box_location_barcode);
		searchMap.put("customer_id", Integer.valueOf(customer_id));
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		searchMap.put("created_user", sysUser.getUsername());
		searchMap.put("last_updated_user", sysUser.getUsername());
		
		try {
			if("0".equals(operateState)){
			if (!WorkerUtil.isNullOrEmpty(replenishmentBiz.selectReplenishmentIsExist(queryMap))) {
				resMap.put("result",Response.FAILURE);
				resMap.put("note", "该补货规则已经存在,不允许重复插入");
				return resMap;
			}else{
				resMap = replenishmentBiz.insertConfigReplenishmentByMap(updateReplenishmentMap,searchMap);
			}
		}else{
			 resMap = replenishmentBiz.updateReplenishmentByUpdateReplenishmentMap(updateReplenishmentMap,searchMap);
		 	}
		} catch (Exception e) {
			resMap.put("result", "failure");
			resMap.put("note", "插入失败");
			e.printStackTrace();
		}
		
		return resMap;
		}
	}
	
	
	/**
	 * 补货规则删除
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/deleteRule")
	@ResponseBody
	public Object deleteRule(HttpServletRequest request,Map<String,Object> model) {
		String customer_id = request.getParameter("customer_id");
		String barcode = request.getParameter("barcode");
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		Map<String,Object> resMap = new HashMap<String,Object>();
		

		try {
			 Integer product_id = productBiz.selectProductIdByBarcodeCustomerId(barcode,Integer.valueOf(customer_id));
			 replenishmentBiz.deleteReplenishment(customer_id, product_id,currentPhysicalWarehouse.getPhysical_warehouse_id());
			 resMap.put("result", "success");
			 resMap.put("note", "删除成功");
		} catch (Exception e) {
			resMap.put("result", "failure");
			resMap.put("note", "删除失败");
			e.printStackTrace();
		}
		return resMap;
	}
	
	
	/**
	 * 补货规则模板下载
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="downloadReplenishment")
	public ModelAndView downloadLocation(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
        Workbook wb = new HSSFWorkbook(); // 创建excel工作簿
        Sheet sheet = wb.createSheet("template"); // 创建第一个sheet（页），并命名
        Row row = sheet.createRow((short) 0); // 创建第一行

        CellStyle cellStyle=wb.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex()); // 前景色
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);  
        
        Cell cell = row.createCell(0); // 创建列（每行里的单元格）
        cell.setCellValue("货主");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("商品条码");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("商品名称");
        cell = row.createCell(3);
        cell.setCellValue("件拣货区(from)");
        cell = row.createCell(4);
        cell.setCellValue("件拣货区(to)");
        cell = row.createCell(5);
        cell.setCellValue("最高存量");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(6);
        cell.setCellValue("最低存量");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(7);
        cell.setCellValue("箱拣货区(from)");
        cell = row.createCell(8);
        cell.setCellValue("箱拣货区(to)");
        cell = row.createCell(9);
        cell.setCellValue("最高存量");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(10);
        cell.setCellValue("最低存量");
        cell.setCellStyle(cellStyle);
 		
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            wb.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);

        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment;filename=" + new String(("补货规则导入模板.xls").getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);

            byte[] buff = new byte[2048];
            int bytesRead;

            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }

        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        return null;
	}
	/**
	 * 补货规则模板导入
	 * @param file
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="uploadLocationFile")
	public String uploadLocationFile(@RequestParam("uploadfile") CommonsMultipartFile file,HttpServletRequest request,Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        List<Map<String, Object>>  piliangdaoruList  = new ArrayList<Map<String,Object>>(); 
        List<WarehouseCustomer> warehouseCustomerList = warehouseCustomerBiz.findAll();
		if (!file.isEmpty()) {
			String type = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
			
			boolean isE2007 = false;	//判断是否是excel2007格式
			if(type.endsWith("xlsx"))
				isE2007 = true;
			
            try {
            	piliangdaoruList= replenishmentBiz.uploadLocationV2(file.getInputStream(),isE2007,sysUser.getUsername());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		model.put("piliangdaoruList", piliangdaoruList);
		model.put("warehouseCustomerList", warehouseCustomerList);
		return "/replenishment/replenishment_rule_setting";
	}
	/**
	 * 紧急补货规则
	 */
	@RequestMapping(value="/configReplenishmentUrgent")
	
	public String configReplenishmentUrgent(Map<String,Object> model,HttpServletRequest request){
		
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String barcode = request.getParameter("barcode");
		String customer_id = request.getParameter("customer_id");
		String product_name = request.getParameter("product_name");
		List<WarehouseCustomer> warehouseCustomerList1 =new ArrayList<WarehouseCustomer>();
		List<WarehouseCustomer> warehouseCustomerList = warehouseCustomerBiz.findAll();
		for (WarehouseCustomer warehouseCustomer : warehouseCustomerList) {
			if(warehouseCustomer.getCustomer_id()!=2){
				warehouseCustomerList1.add(warehouseCustomer);
			}
		}
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customer_id", customer_id);
		}
		if(!WorkerUtil.isNullOrEmpty(product_name)){
			searchMap.put("product_name", product_name);
		}
		
		model.put("warehouseCustomerList",warehouseCustomerList1);
		return  "/replenishmentUrgent/replenishmentUrgent";
	}
	/**
	 * 紧急补货规则查询
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/configReplenishmentUrgentquery")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object configReplenishmentUrgentSearch(HttpServletRequest request) {
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Map<String,Object> rerurnMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		List<WarehouseCustomer> warehouseCustomerList1 =new ArrayList<WarehouseCustomer>();
		
		List<WarehouseCustomer> warehouseCustomerList = warehouseCustomerBiz.findAll();
		for (WarehouseCustomer warehouseCustomer : warehouseCustomerList) {
			if(warehouseCustomer.getCustomer_id()!=2){
				warehouseCustomerList1.add(warehouseCustomer);
			}
		}
		if(!WorkerUtil.isNullOrEmpty(currentPhysicalWarehouse.getPhysical_warehouse_id())){
			searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		}
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(request.getParameter("currentPage"))){
			page = new PageParameter(1,15);
		}else{
			page = new PageParameter(Integer.valueOf(request.getParameter("currentPage")),Integer.valueOf(request.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		List<Map<String, Object>> searchResultList = replenishmentUrgentBiz.selectByMap(searchMap);
		rerurnMap.put("searchResultList", searchResultList);
		rerurnMap.put("warehouseCustomerList", warehouseCustomerList1);
		rerurnMap.put("page", page);
		return rerurnMap;
	}
	
	/**
	 * 补货规则添加
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/deleteUrgentRule")
	@ResponseBody
	public Object deleteUrgentRule(HttpServletRequest request,Map<String,Object> model) {
		String barcode = request.getParameter("barcode");
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		Map<String,Object> resMap = new HashMap<String,Object>();
		String customer_id = request.getParameter("customer_id");

		try {
			replenishmentUrgentBiz.deleteReplenishmentUrgent(Integer.valueOf(customer_id), currentPhysicalWarehouse.getPhysical_warehouse_id());
			 resMap.put("result", "success");
			 resMap.put("note", "删除成功");
		} catch (Exception e) {
			resMap.put("result", "failure");
			resMap.put("note", "删除失败");
			e.printStackTrace();
		}
		return resMap;
	}
	
	
	/**
	 * 补货规则添加
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/addUrgentRule")
	@ResponseBody
	public Object addUrgentRule(HttpServletRequest request,Map<String,Object> model) {
		
		List<Integer> customerIdList = new ArrayList<Integer>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		List<WarehouseCustomer> warehouseCustomerList = warehouseCustomerBiz.findAll();
		String replenishment_condition = request.getParameter("replenishment_condition");
		Map<String,Object> resMap = new HashMap<String,Object>();
		if("".equals(request.getParameter("customer_id"))){
			for (WarehouseCustomer warehouseCustomer : warehouseCustomerList) {
				customerIdList.add(warehouseCustomer.getCustomer_id());
			}
		}else{
			String [] customer_id =  request.getParameter("customer_id").split(",");
			 for (int i = 0 ; i <customer_id.length ; i++ ) {
				 customerIdList.add(Integer.valueOf(customer_id[i]));
			    } 
		}
		//List<Integer> customerList = warehouseCustomerDao.selectCustomerIdByCustomerName(customerIdList);
		Map<String, Object> queryMap = new HashMap<String, Object>();
		Map<String, Object> searchMap = new HashMap<String, Object>();
		Map<String,Object> updateReplenishmentMap = new HashMap<String,Object>();
		queryMap.put("customerList", customerIdList);
		queryMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
//		if(!WorkerUtil.isNullOrEmpty(customerIdList)){
//			updateReplenishmentMap.put("customerList", customerIdList);
//		}
		if(!WorkerUtil.isNullOrEmpty(replenishment_condition)){
			updateReplenishmentMap.put("replenishment_condition", replenishment_condition);
		}
		if(!WorkerUtil.isNullOrEmpty(currentPhysicalWarehouse.getPhysical_warehouse_id())){
			updateReplenishmentMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		}
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		searchMap.put("created_user", sysUser.getUsername());
		searchMap.put("last_updated_user", sysUser.getUsername());
		
		try {
			if (!WorkerUtil.isNullOrEmpty(customerIdList)) {
				for(Integer customer : customerIdList ){
					if(!WorkerUtil.isNullOrEmpty(replenishmentUrgentBiz.selectReplenishmentUrgentIsExist(customer, currentPhysicalWarehouse.getPhysical_warehouse_id()))){
						updateReplenishmentMap.put("customer_id", customer);
						resMap = replenishmentUrgentBiz.updateReplenishmentUrgentByUpdateReplenishmentMap(updateReplenishmentMap,searchMap);
					}else{
						updateReplenishmentMap.put("customer_id", customer);
						resMap = replenishmentUrgentBiz.insertConfigReplenishmentUrgentByMap(updateReplenishmentMap, searchMap);
					}
				}
				
			}
		} catch (Exception e) {
			resMap.put("result", "failure");
			resMap.put("note", "插入失败");
			e.printStackTrace();
		}
		
		return resMap;
		}
	
}