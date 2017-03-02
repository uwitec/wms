package com.leqee.wms.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.leqee.wms.biz.ProductBiz;
import com.leqee.wms.biz.ReplenishmentBiz;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.OrderPrepackDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.ViewExcel;
import com.leqee.wms.util.WorkerUtil;

/**
 * 商品维护相关逻辑
 * @author hzhang1
 * @date 2016-6-21
 * @version 1.0.0
 */
@Controller
@RequestMapping(value="/product")
public class ProductController {

	private Logger logger  = Logger.getLogger(ProductController.class);
	private Cache<String, String> isPhysicalOutSourceCache;
	
	@Autowired
	ProductBiz productBiz;
	@Autowired
	ProductDao productDao;
	
	@Autowired
	ReplenishmentBiz replenishmentBizImpl;
	@Autowired
	OrderPrepackDao orderPrepackDao;
	@Autowired
	ConfigDao configDao;
	
	// 商品预打包
	@RequestMapping(value="/prepack")
	@SuppressWarnings("unchecked")
	public String showPrepack( HttpServletRequest req , Map<String,Object> model ){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        String start = req.getParameter("start");
        
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        Integer physical_warehouse_id = currentPhysicalWarehouse.getPhysical_warehouse_id();
         
 		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
 				.getSecurityManager();
 		CacheManager cacheManager = securityManager.getCacheManager();
 		isPhysicalOutSourceCache = cacheManager.getCache("isPhysicalOutSourceCache");
 		
 		String isOutSource=isPhysicalOutSourceCache.get(physical_warehouse_id.toString());
 		if(null==isOutSource){
 			isOutSource = configDao.getConfigValueByFrezen(physical_warehouse_id, 0, "IS_OUTSOURCE_PHYSICAL");
 			isPhysicalOutSourceCache.put(physical_warehouse_id.toString(), isOutSource);
 		}
     		
        if(WorkerUtil.isNullOrEmpty(start)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf.format(new Date(System.currentTimeMillis()));
			model.put("start", startDate );
		}
        model.put("warehouseList", warehouseList );
        model.put("isOutWarehouse", isOutSource );
        model.put("customers", customers);
		return "/prepack/show";
	}
	
	
	// 商品预打包展示数据
	@RequestMapping(value="/prepackList")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object prepackList( HttpServletRequest req){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		Integer physical_warehouse_id = currentPhysicalWarehouse.getPhysical_warehouse_id();
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
 				.getSecurityManager();
 		CacheManager cacheManager = securityManager.getCacheManager();
 		isPhysicalOutSourceCache = cacheManager.getCache("isPhysicalOutSourceCache");
 		
 		String isOutSource=isPhysicalOutSourceCache.get(physical_warehouse_id.toString());
 		if(null==isOutSource){
 			isOutSource = configDao.getConfigValueByFrezen(physical_warehouse_id, 0, "IS_OUTSOURCE_PHYSICAL");
 			isPhysicalOutSourceCache.put(physical_warehouse_id.toString(), isOutSource);
 		}
 		
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
        if(!WorkerUtil.isNullOrEmpty(req.getParameter("customer_id"))) 
        	searchMap.put("customer_id", req.getParameter("customer_id"));
        else 
        	searchMap.put("customers", customers);
        if(!WorkerUtil.isNullOrEmpty(req.getParameter("oms_task_sn"))) searchMap.put("oms_task_sn", req.getParameter("oms_task_sn"));
        if(!WorkerUtil.isNullOrEmpty(req.getParameter("order_sn"))) searchMap.put("order_sn", req.getParameter("order_sn"));
        if(!WorkerUtil.isNullOrEmpty(req.getParameter("type"))) searchMap.put("type", req.getParameter("type"));
        if(!WorkerUtil.isNullOrEmpty(req.getParameter("start"))) searchMap.put("start", req.getParameter("start"));
        if(!WorkerUtil.isNullOrEmpty(req.getParameter("end"))) searchMap.put("end", req.getParameter("end"));
        if(!WorkerUtil.isNullOrEmpty(req.getParameter("status"))) searchMap.put("status", req.getParameter("status"));
        if(!WorkerUtil.isNullOrEmpty(req.getParameter("barcode"))) searchMap.put("barcode", req.getParameter("barcode"));
        if(!WorkerUtil.isNullOrEmpty(req.getParameter("warehouse_id"))) searchMap.put("warehouse_id", req.getParameter("warehouse_id"));
        
        PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		resMap.put("isOutWarehouse", isOutSource );
        resMap.put("prepackGoodsList",  productBiz.getPrePackProductList(searchMap));
        resMap.put("page", page);
		return resMap;
	}
	
	// 打印预打包
	@RequestMapping(value="/printPrepack")
	@SuppressWarnings("unchecked")
	public String printPrepack( HttpServletRequest req , Map<String,Object> model ){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		String temp = req.getParameter("orderIdList");
		String type = req.getParameter("type");
		if(WorkerUtil.isNullOrEmpty(temp))
			return null;
		String orderArr[] = temp.split(",");
		List<String> orderIdList = new ArrayList<String>();
		for(int i = 0 ;i<orderArr.length;i++){
			orderIdList.add(orderArr[i]);
		}
		
		try {
			Map resMap = productBiz.printPrePackProduct(orderIdList,type,currentPhysicalWarehouse.getPhysical_warehouse_id(),sysUser.getUsername());
			model.put("printPrepackGoodsList", (List<Map>) resMap.get("printPrepackGoodsList"));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String printTime = sdf.format(new Date());
			model.put("print_time", printTime);
			model.put("type", type);
			model.put("print_user", sysUser.getRealname());
		} catch (Exception e) {
			model.put("printPrepackGoodsList", null);
			logger.error("打印预打包任务单失败：", e);
		}
		
		return "/prepack/print";
	}
	
	
	@RequestMapping(value="/buPrintTask")
	@SuppressWarnings("unchecked")
	public String buPrintTask( HttpServletRequest req , Map<String,Object> model ){
		return "/prepack/buPrintTasksn";
	}
	
	// 补打印预打包
	@RequestMapping(value="/buPrintPrepack")
	@SuppressWarnings("unchecked")
	public String buPrintPrepack( HttpServletRequest req , Map<String,Object> model ){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		String oms_task_sn = req.getParameter("oms_task_sn").trim();
		OrderPrepack orderPrepack = orderPrepackDao.selectOrderPrepackByOmsTaskSn(oms_task_sn);
		if(WorkerUtil.isNullOrEmpty(orderPrepack) || WorkerUtil.isNullOrEmpty(oms_task_sn))
			return null;
		String type = orderPrepack.getPack_type();
		if(OrderPrepack.ORDER_PREPACK_STATUS_RESERVED.equals(orderPrepack.getStatus()) ||
				OrderPrepack.ORDER_PREPACK_STATUS_IN_PROCESS.equals(orderPrepack.getStatus())){
			
			List<String> orderIdList = new ArrayList<String>();
			orderIdList.add(orderPrepack.getOrder_id().toString());
			
			try {
				Map resMap = productBiz.printPrePackProduct(orderIdList,type,currentPhysicalWarehouse.getPhysical_warehouse_id(),sysUser.getUsername());
				model.put("printPrepackGoodsList", (List<Map>) resMap.get("printPrepackGoodsList"));
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String printTime = sdf.format(new Date());
				model.put("print_time", printTime);
				model.put("type", type);
				model.put("print_user", sysUser.getRealname());
			} catch (Exception e) {
				model.put("printPrepackGoodsList", null);
				logger.error("打印预打包任务单失败：", e);
			}
		}
		return "/prepack/print";
	}
	
	// 完结预打包
	@RequestMapping(value="/endPrepack")
	@SuppressWarnings("unchecked")
	@ResponseBody
	public Object endPrepack( HttpServletRequest req , Map<String,Object> model ){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		Map<String,Object> resMap = new HashMap<String,Object>();
		String orderId = req.getParameter("order_id");
		try {
			resMap = productBiz.endPrePackProduct(Integer.parseInt(orderId),currentPhysicalWarehouse.getPhysical_warehouse_id());
		} catch (NumberFormatException e) {
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "完结预打包任务单失败"+e.getMessage());
			logger.error("完结预打包", e);
		}
		return resMap;
	}
		
	
	// 商品预打包上架页面
	@RequestMapping(value="/prepackGrouding")
	@SuppressWarnings("unchecked")
	public String prepackGrouding( HttpServletRequest req , Map<String,Object> model ){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		return "/prepack/grouding";
	}
		
	// 扫描预打包上架标签
	@RequestMapping(value="/scanPrepackGrouding")
	@ResponseBody
	public Object scanPrepackGrouding( HttpServletRequest req , Map<String,Object> model ){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        String location_barcode = req.getParameter("location_barcode");
        
        
        Map<String,Object> resMap = new HashMap<String,Object>();
        resMap = productBiz.scanPrepackGrouding(location_barcode,currentPhysicalWarehouse.getPhysical_warehouse_id());
		return resMap;
	}
	
	// 预打包上架
	@RequestMapping(value="/prepackSubmitGrouding")
	@ResponseBody
	public Object prepackSubmitGrouding( HttpServletRequest req , Map<String,Object> model ){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        String location_barcode = req.getParameter("location_barcode");
        String new_location_barcode = req.getParameter("new_location_barcode").toString().toUpperCase().replace("-", "");
        
        Map<String,Object> resMap = new HashMap<String,Object>();
        try {
			resMap = productBiz.prepackSubmitGrouding(location_barcode, currentPhysicalWarehouse.getPhysical_warehouse_id(), new_location_barcode,sysUser.getUsername());
		} catch (Exception e) {
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "上架预打包任务单失败"+e.getMessage());
			logger.error("prepackSubmitGrouding error", e);
		}
		return resMap;
	}
	
	
	// 商品基本信息页面
	@RequestMapping(value="/show")
	@SuppressWarnings("unchecked")
	public String showProduct( HttpServletRequest req , Map<String,Object> model ){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
        model.put("customers", customers);
		return "/product/show";
	}
	
	// ajax调用接口返回商品列表
	@RequestMapping(value="/list")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object showProductList( HttpServletRequest req ){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map<String,Object> searchMap = new HashMap<String,Object>();
		
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		
		String customer_id = req.getParameter("customer_id");
		String barcode = req.getParameter("barcode");
		String product_name = req.getParameter("product_name");
		
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customer_id", customer_id);
		}else{
			searchMap.put("customers", customers);
		}
		
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		if(!WorkerUtil.isNullOrEmpty(product_name)){
			searchMap.put("product_name", product_name);
		}
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter();
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		resMap.put("productList", productBiz.getProductList(searchMap));
		resMap.put("customers", customers);
		resMap.put("page", page);
		return resMap;
	}
	
	@RequestMapping(value="exportProducts")
	@SuppressWarnings("unchecked")
	public ModelAndView exportProducts(HttpServletRequest req , Map<String,Object> model){
		
		ViewExcel viewExcel = new ViewExcel(); 
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map<String,Object> searchMap = new HashMap<String,Object>();
		
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		
		String customer_id = req.getParameter("customer_id");
		String barcode = req.getParameter("barcode");
		String product_name = req.getParameter("product_name");
		String spec=req.getParameter("spec");
		
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customer_id", customer_id);
		}else{
			searchMap.put("customers", customers);
		}
		
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		if(!WorkerUtil.isNullOrEmpty(product_name)){
			searchMap.put("product_name", product_name);
		}
		searchMap.put("spec", spec);
		
		model.put("type", "exportSaleProducts");
		List<Map> productList = (List<Map>) productBiz.getProductListExport(searchMap);
		model.put("productList", productList);
		
		// 3.返回订单结果
		return new ModelAndView(viewExcel, model);  
	}
	    // ajax调用接口返回商品列表
		@RequestMapping(value="/list22")
		@ResponseBody
		@SuppressWarnings("unchecked")
		public Object showProductList22( HttpServletRequest req ){
			
			Subject subject = SecurityUtils.getSubject();
	        Session session = subject.getSession();
	        
			Map<String,Object> resMap = new HashMap<String,Object>();
			Map<String,Object> searchMap = new HashMap<String,Object>();
			
			List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
			
			String customer_id = req.getParameter("customer_id");
			String barcode = req.getParameter("barcode");
			String product_name = req.getParameter("product_name");
			String spec=req.getParameter("spec");
			
			if(!WorkerUtil.isNullOrEmpty(customer_id)){
				searchMap.put("customer_id", customer_id);
			}else{
				searchMap.put("customers", customers);
			}
			
			if(!WorkerUtil.isNullOrEmpty(barcode)){
				searchMap.put("barcode", barcode);
			}
			if(!WorkerUtil.isNullOrEmpty(product_name)){
				searchMap.put("product_name", product_name);
			}
			searchMap.put("spec", spec);
			
			PageParameter page = null;
			if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
				page = new PageParameter();
			}else{
				page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
			}
			searchMap.put("page", page);
			resMap.put("productList", productBiz.getProductList22(searchMap));
			resMap.put("customers", customers);
			resMap.put("page", page);
			return resMap;
		}
	
	// 更新商品基本信息
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/update")
	@ResponseBody
	public Object updatedProduct(Product product){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		try {
			resMap = productBiz.updateProduct(product);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新商品信息失败", e);
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "更新商品信息失败");
		}
		return resMap;
	}
	
	
	// 下载商品导入模板
	@RequestMapping(value="downloadTemplate")
	public ModelAndView downloadTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
        cell.setCellValue("单品毛重（kg）");
        cell = row.createCell(4);
        cell.setCellValue("长（cm）");
        cell = row.createCell(5);
        cell.setCellValue("宽（cm）");
        cell = row.createCell(6);
        cell.setCellValue("高（cm）");
        cell = row.createCell(7);
        cell.setCellValue("箱规");
        cell = row.createCell(8);
        cell.setCellValue("满足箱拣货区最大数量");
        cell = row.createCell(9);
        cell.setCellValue("托规（Hi）");
        cell = row.createCell(10);
        cell.setCellValue("托规（Ti）");
        cell = row.createCell(11);
        cell.setCellValue("保质期管理");
        cell = row.createCell(12);
        cell.setCellValue("收货规则");
        cell = row.createCell(13);
        cell.setCellValue("补货规则");
        cell = row.createCell(14);
        cell.setCellValue("库存分配规则");
        cell = row.createCell(15);
        cell.setCellValue("是否易碎");
        cell = row.createCell(16);
        cell.setCellValue("是否3C产品");
        cell = row.createCell(17);
        cell.setCellValue("是否需要串号维护");
        cell = row.createCell(18);
        cell.setCellValue("是否支持虚拟库存");
        cell = row.createCell(19);
        cell.setCellValue("虚拟库存");
        cell = row.createCell(20);
        
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
		response.setHeader("Content-Disposition", "attachment;filename=" + new String(("商品信息维护导入模板.xls").getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);

            byte[] buff = new byte[2048];
            int bytesRead;

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
	
	// 上传excel商品信息
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/upload")
	public String uploadProduct(@RequestParam("uploadfile") CommonsMultipartFile file,HttpServletRequest request, Map<String,Object> model){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		if (!file.isEmpty()) {
			String type = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
			
			boolean isE2007 = false;	//判断是否是excel2007格式
			if(type.endsWith("xlsx"))
				isE2007 = true;
			
            try {
				resMap= productBiz.uploadProduct(file.getInputStream(),isE2007,sysUser.getUsername());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		model.put("resMap", resMap);
		return "/product/show";
	}
	
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
	
	
	@RequestMapping(value="/AllotByUser")
	@ResponseBody
	public Map<String, Object> AllotByUser( HttpServletRequest req){
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		String type = req.getParameter("type");
		Integer order_id = Integer.parseInt(req.getParameter("order_id"));
		Integer customer_id = Integer.parseInt(req.getParameter("customer_id"));
		Integer warehouse_id = Integer.parseInt(req.getParameter("warehouse_id"));
		
		List<Integer> empty0 = new ArrayList<Integer>();
		List<Map> empty1 = new ArrayList<Map>();
		Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
		Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
		List<Product> empty5 = new ArrayList<Product>();
		
		String result=replenishmentBizImpl.lockReplenishJobByWarehouseCustomer(order_id,"",type,empty0,
				physical_warehouse_id,customer_id,
				empty1,empty2,empty0,empty4,0,empty5,0,0,warehouse_id);
		
		resultMap.put("result",result);
		
		return resultMap;
	}
	
}
