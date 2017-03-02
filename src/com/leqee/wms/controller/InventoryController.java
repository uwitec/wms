package com.leqee.wms.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.WarehouseBiz;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.ViewExcel;
import com.leqee.wms.util.WorkerUtil;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


/**
 * 此controller实现的是库存管理
 * 主要包括：库存查询，库存调整，库存冻结，库存移动
 * @author hzhang1
 * @date 2016-3-7
 * @version 1.0.0
 */

@Controller
@RequestMapping(value="inventory")
public class InventoryController {
	
	private Cache<String, String> isDouble11Cache;
	
	@Autowired
	InventoryBiz inventoryBiz;
	
	@Autowired
	WarehouseDao warehouseDao;
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	OrderBiz orderBiz;
	
	@Autowired
	WarehouseBiz warehouseBiz;
	
	@Autowired
	InventoryDao inventoryDao;
	
	@Autowired
	ConfigDao configDao;
	
	private Logger logger = Logger.getLogger(InventoryController.class);
	
	
	@RequestMapping(value="exportPackBox")
	public ModelAndView exportPackBox(HttpServletRequest req , Map<String,Object> model){
		 ViewExcel viewExcel = new ViewExcel();    
		 
		 Map<String,Object> searchMap = new HashMap<String,Object>();
		 String customer_id = req.getParameter("customer_id");
		 if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customer_id", customer_id);
			model.put("customer_id", customer_id);
		}else{
		    return new ModelAndView(viewExcel, model);  
		}
		 model.put("list", inventoryDao.getPackBoxInventory(searchMap));
		 model.put("type", "exportPackBox");
	     return new ModelAndView(viewExcel, model);  
	}
	
	@RequestMapping(value="exportInventory")
	public ModelAndView exportInventory(HttpServletRequest req , Map<String,Object> model){
		
		ViewExcel viewExcel = new ViewExcel();    
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        List<Integer> warehouseIdList= new ArrayList<Integer>();
        
		// 1.接收参数
        String customer_id = WorkerUtil.isNullOrEmpty(req.getParameter("customer_id"))?null:req.getParameter("customer_id");
        String barcode = WorkerUtil.isNullOrEmpty(req.getParameter("barcode"))?null:req.getParameter("barcode");
        String batch_sn = WorkerUtil.isNullOrEmpty(req.getParameter("batch_sn"))?null:req.getParameter("batch_sn");
        String goods_name = WorkerUtil.isNullOrEmpty(req.getParameter("goods_name"))?null:req.getParameter("goods_name");
		String status_id = WorkerUtil.isNullOrEmpty(req.getParameter("status_id"))?null:req.getParameter("status_id");
		String location_type = WorkerUtil.isNullOrEmpty(req.getParameter("location_type"))?null:req.getParameter("location_type");
		String from_location_barcode = WorkerUtil.isNullOrEmpty(req.getParameter("from_location_barcode"))?null:req.getParameter("from_location_barcode").toString().replace("-", "");
		String to_location_barcode = WorkerUtil.isNullOrEmpty(req.getParameter("to_location_barcode"))?null:req.getParameter("to_location_barcode").toString().replace("-", "");
		String is_show_urikitamono = WorkerUtil.isNullOrEmpty(req.getParameter("is_show_urikitamono"))?null:req.getParameter("is_show_urikitamono");
		String is_exception = WorkerUtil.isNullOrEmpty(req.getParameter("is_exception"))?null:req.getParameter("is_exception");
		
		model.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		if(!WorkerUtil.isNullOrEmpty(barcode)) {
			model.put("barcode", barcode);
			
			if(!WorkerUtil.isNullOrEmpty(location_type)){
				model.put("location_type", location_type);
			}
			if(!WorkerUtil.isNullOrEmpty(batch_sn)){
				model.put("batch_sn", batch_sn);
			}
		}else{
			if(!WorkerUtil.isNullOrEmpty(customer_id)){
				if(!"-1".equals(customer_id))
					model.put("customer_id", customer_id);
				else{
					if(WorkerUtil.isNullOrEmpty(customers))
						customers.add(null);
					model.put("customers", customers);
				}
			}
			
			if(!WorkerUtil.isNullOrEmpty(batch_sn)){
				model.put("batch_sn", batch_sn);
			}
			if(!WorkerUtil.isNullOrEmpty(barcode)) model.put("sku_code", barcode);
			if(!WorkerUtil.isNullOrEmpty(goods_name)) model.put("goods_name", goods_name);
//			if(!WorkerUtil.isNullOrEmpty(barcode)) model.put("barcode", barcode);
			if(!WorkerUtil.isNullOrEmpty(status_id)) model.put("status_id", status_id);
			if(!WorkerUtil.isNullOrEmpty(location_type)) model.put("location_type", location_type);
			if(!WorkerUtil.isNullOrEmpty(is_show_urikitamono)) model.put("is_show_urikitamono", is_show_urikitamono);
			if(!WorkerUtil.isNullOrEmpty(is_exception)) model.put("is_exception", is_exception);
			
			if(WorkerUtil.isNullOrEmpty(from_location_barcode) && !WorkerUtil.isNullOrEmpty(to_location_barcode)){
				model.put("location_barcode", to_location_barcode);
				model.put("to_location_barcode", to_location_barcode);
			}else if(!WorkerUtil.isNullOrEmpty(from_location_barcode) && WorkerUtil.isNullOrEmpty(to_location_barcode)){
				model.put("location_barcode", from_location_barcode);
				model.put("from_location_barcode", from_location_barcode);
			}else if(!WorkerUtil.isNullOrEmpty(from_location_barcode) && !WorkerUtil.isNullOrEmpty(to_location_barcode)){
				model.put("from_location_barcode", from_location_barcode);
				model.put("to_location_barcode", to_location_barcode);
			}
		}
		// 2.调用biz层获取数据
		List<Map> inventoryGoodsList = inventoryBiz.getInventoryGoods(model);
		
		model.put("list", inventoryGoodsList);
		model.put("warehouse_name", currentPhysicalWarehouse.getWarehouse_name());
		model.put("type", "exportInventory");
	    return new ModelAndView(viewExcel, model);  
	}
	
	
	/**
	 * 库存查询接口
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="search")
	public String show(HttpServletRequest req , Map<String,Object> model){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        List<Integer> warehouseIdList= new ArrayList<Integer>();
        
		// 1.接收参数
        String customer_id = WorkerUtil.isNullOrEmpty(req.getParameter("customer_id"))?null:req.getParameter("customer_id");
        String warehouse_id = WorkerUtil.isNullOrEmpty(req.getParameter("warehouse_id"))?null:req.getParameter("warehouse_id");
        String barcode = WorkerUtil.isNullOrEmpty(req.getParameter("barcode"))?null:req.getParameter("barcode");
        String batch_sn = WorkerUtil.isNullOrEmpty(req.getParameter("batch_sn"))?null:req.getParameter("batch_sn");
        String goods_name = WorkerUtil.isNullOrEmpty(req.getParameter("goods_name"))?null:req.getParameter("goods_name");
		String status_id = WorkerUtil.isNullOrEmpty(req.getParameter("status_id"))?null:req.getParameter("status_id");
		String location_type = WorkerUtil.isNullOrEmpty(req.getParameter("location_type"))?"":req.getParameter("location_type");
		String from_location_barcode = WorkerUtil.isNullOrEmpty(req.getParameter("from_location_barcode"))?null:req.getParameter("from_location_barcode").toString().replace("-", "");
		String to_location_barcode = WorkerUtil.isNullOrEmpty(req.getParameter("to_location_barcode"))?null:req.getParameter("to_location_barcode").toString().replace("-", "");
		String is_show_urikitamono = WorkerUtil.isNullOrEmpty(req.getParameter("is_show_urikitamono"))?null:req.getParameter("is_show_urikitamono");
		String is_exception = WorkerUtil.isNullOrEmpty(req.getParameter("is_exception"))?null:req.getParameter("is_exception");
		
		model.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		
		if(!WorkerUtil.isNullOrEmpty(barcode)) {
			model.put("barcode", barcode);
			
			if(!WorkerUtil.isNullOrEmpty(location_type)){
				model.put("location_type", location_type);
			}
			if(!WorkerUtil.isNullOrEmpty(batch_sn)){
				model.put("batch_sn", batch_sn);
			}
		}else{
			if(!WorkerUtil.isNullOrEmpty(customer_id)){
				if(!"-1".equals(customer_id))
					model.put("customer_id", customer_id);
				else{
					if(WorkerUtil.isNullOrEmpty(customers))
						customers.add(null);
					model.put("customers", customers);
				}
			}else{
				model.put("warehouseList", warehouseList);
				model.put("customers", customers);
				model.put("location_type_map", Location.LOCATION_TYPE_MAP);
				model.put("customer_id",  !WorkerUtil.isNullOrEmpty(customers) && !WorkerUtil.isNullOrEmpty(customers.get(0).getCustomer_id())?customers.get(0).getCustomer_id():0);
				return "inventory/search";
			}
			
			
			if(!WorkerUtil.isNullOrEmpty(goods_name)) model.put("goods_name", goods_name);
			//if(!WorkerUtil.isNullOrEmpty(barcode)) model.put("barcode", barcode);
			if(!WorkerUtil.isNullOrEmpty(status_id)) model.put("status_id", status_id);
			if(!WorkerUtil.isNullOrEmpty(location_type)){
				model.put("location_type", location_type);
			}
			if(!WorkerUtil.isNullOrEmpty(batch_sn)){
				model.put("batch_sn", batch_sn);
			}
			if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
				model.put("warehouse_id", warehouse_id);
			}
			
			if(!WorkerUtil.isNullOrEmpty(is_show_urikitamono)) model.put("is_show_urikitamono", is_show_urikitamono);
			if(!WorkerUtil.isNullOrEmpty(is_exception)) model.put("is_exception", is_exception);
			
			if(WorkerUtil.isNullOrEmpty(from_location_barcode) && !WorkerUtil.isNullOrEmpty(to_location_barcode)){
				model.put("location_barcode", to_location_barcode);
				model.put("to_location_barcode", to_location_barcode);
			}else if(!WorkerUtil.isNullOrEmpty(from_location_barcode) && WorkerUtil.isNullOrEmpty(to_location_barcode)){
				model.put("location_barcode", from_location_barcode);
				model.put("from_location_barcode", from_location_barcode);
			}else if(!WorkerUtil.isNullOrEmpty(from_location_barcode) && !WorkerUtil.isNullOrEmpty(to_location_barcode)){
				model.put("from_location_barcode", from_location_barcode);
				model.put("to_location_barcode", to_location_barcode);
			}
		}
		// 2.调用biz层获取数据
		List<Map> inventoryGoodsList = inventoryBiz.getInventoryGoods(model);
		
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		isDouble11Cache = cacheManager.getCache("isDouble11Cache");
		String isDouble11=isDouble11Cache.get(currentPhysicalWarehouse.getPhysical_warehouse_id().toString());
		if(isDouble11 == null){
			isDouble11 = configDao.getConfigValueByFrezen(currentPhysicalWarehouse.getPhysical_warehouse_id(), 0, "IS_DOUBLE_11");
			isDouble11Cache.put(currentPhysicalWarehouse.getPhysical_warehouse_id().toString(), isDouble11);
		}
		// 3.返回数据
		model.put("isDouble11", isDouble11);
		model.put("inventoryGoods", inventoryGoodsList);
		model.put("customers", customers);
		model.put("customer_id", customer_id);
		model.put("warehouseList", warehouseList );
		model.put("location_type_map", Location.LOCATION_TYPE_MAP);
		return "inventory/search";
		
	}
	
	@RequestMapping(value="searchV2")
	@ResponseBody
	public Object showV2(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Map<String,Object> model = new HashMap<String,Object>();
        Map<String,Object> resMap = new HashMap<String,Object>();
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        List<Integer> warehouseIdList= new ArrayList<Integer>();
        
		// 1.接收参数
        String customer_id = WorkerUtil.isNullOrEmpty(req.getParameter("customer_id"))?null:req.getParameter("customer_id");
        String warehouse_id = WorkerUtil.isNullOrEmpty(req.getParameter("warehouse_id"))?null:req.getParameter("warehouse_id");
        String barcode = WorkerUtil.isNullOrEmpty(req.getParameter("barcode"))?null:req.getParameter("barcode");
        String batch_sn = WorkerUtil.isNullOrEmpty(req.getParameter("batch_sn"))?null:req.getParameter("batch_sn");
        String goods_name = WorkerUtil.isNullOrEmpty(req.getParameter("goods_name"))?null:req.getParameter("goods_name");
		String status_id = WorkerUtil.isNullOrEmpty(req.getParameter("status_id"))?null:req.getParameter("status_id");
		String location_type = WorkerUtil.isNullOrEmpty(req.getParameter("location_type"))?null:req.getParameter("location_type");
		String from_location_barcode = WorkerUtil.isNullOrEmpty(req.getParameter("from_location_barcode"))?null:req.getParameter("from_location_barcode").toString().replace("-", "");
		String to_location_barcode = WorkerUtil.isNullOrEmpty(req.getParameter("to_location_barcode"))?null:req.getParameter("to_location_barcode").toString().replace("-", "");
		String is_show_urikitamono = WorkerUtil.isNullOrEmpty(req.getParameter("is_show_urikitamono"))?null:req.getParameter("is_show_urikitamono");
		String is_exception = WorkerUtil.isNullOrEmpty(req.getParameter("is_exception"))?null:req.getParameter("is_exception");
		
		model.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		
		if(!WorkerUtil.isNullOrEmpty(barcode)) {
			model.put("barcode", barcode);
			
			if(!WorkerUtil.isNullOrEmpty(location_type)){
				model.put("location_type", location_type);
			}
			if(!WorkerUtil.isNullOrEmpty(batch_sn)){
				model.put("batch_sn", batch_sn);
			}
		}else{
			if(!WorkerUtil.isNullOrEmpty(customer_id)){
				if(!"-1".equals(customer_id))
					model.put("customer_id", customer_id);
				else{
					if(WorkerUtil.isNullOrEmpty(customers))
						customers.add(null);
					model.put("customers", customers);
				}
			}else{
				model.put("warehouseList", warehouseList);
				if(WorkerUtil.isNullOrEmpty(customers))
					customers.add(null);
				model.put("customers", customers);
				model.put("customer_id", !WorkerUtil.isNullOrEmpty(customers.get(0).getCustomer_id())?customers.get(0).getCustomer_id():0);
				return "inventory/search";
			}
			
			
			if(!WorkerUtil.isNullOrEmpty(goods_name)) model.put("goods_name", goods_name);
			//if(!WorkerUtil.isNullOrEmpty(barcode)) model.put("barcode", barcode);
			if(!WorkerUtil.isNullOrEmpty(status_id)) model.put("status_id", status_id);
			if(!WorkerUtil.isNullOrEmpty(location_type)){
				model.put("location_type", location_type);
			}
			if(!WorkerUtil.isNullOrEmpty(batch_sn)){
				model.put("batch_sn", batch_sn);
			}
			if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
				model.put("warehouse_id", warehouse_id);
			}
			
			if(!WorkerUtil.isNullOrEmpty(is_show_urikitamono)) model.put("is_show_urikitamono", is_show_urikitamono);
			if(!WorkerUtil.isNullOrEmpty(is_exception)) model.put("is_exception", is_exception);
			
			if(WorkerUtil.isNullOrEmpty(from_location_barcode) && !WorkerUtil.isNullOrEmpty(to_location_barcode)){
				model.put("location_barcode", to_location_barcode);
				model.put("to_location_barcode", to_location_barcode);
			}else if(!WorkerUtil.isNullOrEmpty(from_location_barcode) && WorkerUtil.isNullOrEmpty(to_location_barcode)){
				model.put("location_barcode", from_location_barcode);
				model.put("from_location_barcode", from_location_barcode);
			}else if(!WorkerUtil.isNullOrEmpty(from_location_barcode) && !WorkerUtil.isNullOrEmpty(to_location_barcode)){
				model.put("from_location_barcode", from_location_barcode);
				model.put("to_location_barcode", to_location_barcode);
			}
		}
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter();
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		model.put("page", page);
		
		// 2.调用biz层获取数据
		List<Map> inventoryGoodsList = inventoryBiz.getInventoryGoods(model);
		
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		isDouble11Cache = cacheManager.getCache("isDouble11Cache");
		String isDouble11=isDouble11Cache.get(currentPhysicalWarehouse.getPhysical_warehouse_id().toString());
		if(isDouble11 == null){
			isDouble11 = configDao.getConfigValueByFrezen(currentPhysicalWarehouse.getPhysical_warehouse_id(), 0, "IS_DOUBLE_11");
			isDouble11Cache.put(currentPhysicalWarehouse.getPhysical_warehouse_id().toString(), isDouble11);
		}
		// 3.返回数据
		resMap.put("isDouble11", isDouble11);
		resMap.put("inventoryGoods", inventoryGoodsList);
		resMap.put("customers", customers);
		resMap.put("customer_id", customer_id);
		resMap.put("page", page);
		return resMap;
		
	}
	
	@RequiresPermissions("validity:modify:*")
	@RequestMapping(value="editValidity")
	@ResponseBody
	public Object editValidity(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		Map<String,Object> resMap = new HashMap<String,Object>();
		Integer plId = Integer.parseInt(req.getParameter("pl_id"));
		String validity = req.getParameter("validity");
		try {
			resMap = inventoryBiz.editProductLocationValidity(plId, validity,sysUser.getUsername());
		} catch (Exception e) {
			resMap.put("resule", Response.FAILURE);
			resMap.put("note", e.getMessage());
		}
		return resMap;
	}
	
	@RequiresPermissions("batchsn:modify:*")
	@RequestMapping(value="editBatchSn")
	@ResponseBody
	public Object editBatchSn(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		Map<String,Object> resMap = new HashMap<String,Object>();
		Integer plId = Integer.parseInt(req.getParameter("pl_id"));
		String batchSn = req.getParameter("batch_sn");
		try {
			resMap = inventoryBiz.editProductLocationBatchSn(plId, batchSn,sysUser.getUsername());
		} catch (Exception e) {
			resMap.put("resule", Response.FAILURE);
			resMap.put("note", e.getMessage());
		}
		return resMap;
	}
	
	/**
	 * 库存调整入口
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/variance")
	public String list( HttpServletRequest req , Map<String,Object> model ){
		
		// 1.获得session
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 2.获取当前货主
        //WarehouseCustomer customer = (WarehouseCustomer) session.getAttribute("currentCustomer");
        // 获取货主
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        List<Integer> warehouseIdList = new ArrayList<Integer>();
        
		// 1.获取请求参数
		String start_time = req.getParameter("start");
		String end_time = req.getParameter("end");
		String order_sn = req.getParameter("order_sn");
		String sku_code = req.getParameter("sku_code");
		String status = req.getParameter("status");
		String customer_id = req.getParameter("customer_id");
		String warehouse_id = req.getParameter("warehouse_id");
		
		// 2.组装条件参数
		Map<String,Object> searchMap = new HashMap<String,Object>();
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
			searchMap.put("customer_id", customer_id);
			model.put("customer_id", customer_id);
		}
		
		if(!WorkerUtil.isNullOrEmpty(start_time)){
			searchMap.put("start_time", start_time);
			model.put("start", start_time);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf.format(new Date(System.currentTimeMillis() - 3600000 *240 ));
			searchMap.put("start_time", startDate);
			model.put("start", startDate);
		}
		
		if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
			warehouseIdList.add(Integer.valueOf(warehouse_id));
			//searchMap.put("warehouse_id", warehouse_id);
			model.put("warehouse_id", warehouse_id);
		}else{
	        for (Warehouse warehouse : warehouseList) {
	        	warehouseIdList.add(warehouse.getWarehouse_id());
			}
		}
		searchMap.put("warehouseIdList", warehouseIdList);
		
		if(!WorkerUtil.isNullOrEmpty(end_time)){
			searchMap.put("end_time", end_time);
			model.put("end", end_time);
		}
		
		if(!WorkerUtil.isNullOrEmpty(order_sn)){
			searchMap.put("order_sn", order_sn);
			model.put("order_sn", order_sn);
		}
		
		if(!WorkerUtil.isNullOrEmpty(sku_code)){
			searchMap.put("sku_code", sku_code);
			model.put("sku_code", sku_code);
		}
		
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter();
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		
		// 3.调用biz层得到数据
		List<Map> varianceOrderList= orderBiz.getVarianceOrder(searchMap);
		List<Map> newvarianceOrderList = new ArrayList<Map>();
		if(!WorkerUtil.isNullOrEmpty(status) && "Y".equals(status)){
			for(Map map :varianceOrderList){
				if(Integer.parseInt(map.get("out_num").toString()) != 0){
					newvarianceOrderList.add(map);
				}
			}
			varianceOrderList = newvarianceOrderList;
			model.put("status", status);
		}else if(!WorkerUtil.isNullOrEmpty(status) && "N".equals(status)){
			for(Map map :varianceOrderList){
				if(Integer.parseInt(map.get("out_num").toString()) == 0){
					newvarianceOrderList.add(map);
				}
			}
			varianceOrderList = newvarianceOrderList;
			model.put("status", status);
		}

		// 4.返回前端页面
		model.put("varianceOrderList", varianceOrderList ); 
		model.put("warehouseList", warehouseList);
		model.put("customers", customers);
		model.put("page", page ); 
		return "inventory/variance";
	}
	
	/**
	 * 调整库存接口
	 * @param orderGoodsId
	 * @param serialNumber
	 * @return
	 */
	@RequestMapping(value="/adjust")
	@ResponseBody
	public Object adjust(@RequestParam("orderGoodsId") Integer orderGoodsId,
			@RequestParam("serialNumber") String serialNumber) {
		
		logger.info("orderGoodsId:"+orderGoodsId+" serialNumber:"+serialNumber);
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		try {
			resMap = inventoryBiz.deliverInventoryViranceOrderInventory(orderGoodsId,serialNumber,sysUser.getUsername());
		} catch (Exception e) {
			resMap.put("result", "failure");
			resMap.put("note", "调整库存出错");
			e.printStackTrace();
		}
		return resMap;
	}
	
	@RequestMapping(value="/adjust_delete")
	@ResponseBody
	public Object adjust_delete(@RequestParam("orderGoodsId") Integer orderGoodsId,
			@RequestParam("serialNumber") String serialNumber,@RequestParam("orderId") Integer orderId) {
		
		logger.info("orderGoodsId:"+orderGoodsId+" serialNumber:"+serialNumber);
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		try {
			resMap = inventoryBiz.deleteViranceOrder(orderId,sysUser.getUsername());
		} catch (Exception e) {
			resMap.put("result", "failure");
			resMap.put("note", "调整库存出错");
			e.printStackTrace();
		}
		return resMap;
	}
	
	@RequestMapping(value="/adjust2")
	@ResponseBody
	public Object adjust2(@RequestBody String[] orderList,HttpServletRequest request) {
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		Map<String,Object> resMap = new HashMap<String,Object>();
		List<Map> resList = new ArrayList<Map>();
		
		logger.info(orderList);
		for(String order:orderList){
			JSONObject orderJson  = JSONObject.fromObject(order);
			Integer orderGoodsId = Integer.parseInt(orderJson.getString("orderGoodsId"));
			String serialNumber = orderJson.getString("serialNumber");
			resMap = inventoryBiz.deliverInventoryViranceOrderInventory(orderGoodsId,serialNumber,sysUser.getUsername());
			resList.add(resMap);
		}
		
		return resList;
	}
	
	
	/**
	 * 库存冻结入口
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="freeze")
	public String freeze(HttpServletRequest req , Map<String,Object> model){
		
		// 1.获得session
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        // 获取逻辑仓
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        // 3.获取货主
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        List<Integer> warehouseIdList = new ArrayList<Integer>();
        
		String warehouseId = req.getParameter("warehouse_id");
		String skuCode = req.getParameter("sku_code");
		String goodsName = req.getParameter("goods_name");
		String barcode = req.getParameter("barcode");
		String customer_id = req.getParameter("customer_id");
		
		
		Map<String,Object> searchMap = new HashMap<String,Object>();
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customerId", customer_id);
			model.put("customer_id", customer_id );
		}
		if(!WorkerUtil.isNullOrEmpty(warehouseId)){
			warehouseIdList.add(Integer.valueOf(warehouseId));
			//searchMap.put("warehouseId", warehouseId);
			model.put("warehouse_id", warehouseId);
		}else{
	        for (Warehouse warehouse : warehouseList) {
	        	warehouseIdList.add(warehouse.getWarehouse_id());
			}
		}
		searchMap.put("warehouseIdList", warehouseIdList);

		if(!WorkerUtil.isNullOrEmpty(skuCode)){
			searchMap.put("skuCode", skuCode);
			model.put("sku_code", skuCode);
		}
		
		if(!WorkerUtil.isNullOrEmpty(goodsName)){
			searchMap.put("goodsName", goodsName);
			model.put("goods_name", goodsName);
		}
		
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
			model.put("barcode", barcode);
		}
		
		// 2.分页信息设置
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter();
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		
		List<Map> freezeGoods = inventoryBiz.getFreezeGoods(searchMap);
		
		model.put("freezeGoods", freezeGoods);
		model.put("warehouseList", warehouseList);
		model.put("customers", customers);
		model.put("page", page);
		return "inventory/freeze";
		
	}
	
	
	/**
	 * 搜素商品（模糊搜索）
	 * @param req
	 * @return
	 */
	@RequestMapping(value="search_goods")
	@ResponseBody
	public Object search_goods(HttpServletRequest req ){
		String name = req.getParameter("q");
		
		List<Product> productList = productDao.selectProductList(name);
		
		return productList;
	}
	
	/**
	 * 库存冻结的插入 & 更新
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="ajax")
	@ResponseBody
	public Object ajax(HttpServletRequest req , Map<String,Object> model){
		
		String act = req.getParameter("act");
		Integer ori_warehouse_id = Integer.parseInt(req.getParameter("ori_warehouse_id"));
		Integer warehouse_id = Integer.parseInt(req.getParameter("warehouse_id"));
		Integer reserve_number = Integer.parseInt(req.getParameter("reserve_number"));
		String temp = req.getParameter("freeze_reason");
		String freeze_reason = "";
		try {
			freeze_reason = new String(temp.getBytes("iso8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Freeze reason error:", e);
		}
		Integer mapping_id = Integer.parseInt(req.getParameter("mapping_id"));
		Integer customer_id = Integer.parseInt(req.getParameter("customer_id"));
		Integer product_id = Integer.parseInt(req.getParameter("product_id"));
		
		logger.info("act:" + act + " ori_warehouse_id:" + ori_warehouse_id
				+ " reserve_number:" + reserve_number + " freeze_reason:"
				+ freeze_reason + " mapping_id:" + mapping_id + " customer_id:"
				+ customer_id + " product_id:" + product_id);

		Map<String, Object> resMap = new HashMap<String, Object>();

		// 1.调用biz层方法
		Map<String, Object> returnMap = inventoryBiz.dealFreeze(act,
				warehouse_id,ori_warehouse_id, reserve_number, freeze_reason, mapping_id,
				customer_id, product_id);

		// 2.根据返回结果判断
		if("success".equals(returnMap.get("result").toString())){
			resMap.put("result", "success");
			resMap.put("note", "success");
		}else if("failure".equals(returnMap.get("result").toString())){
			return returnMap;
		}
		
		return resMap;
	}

	
	
	// 容器 & 库位管理
	@RequestMapping(value="location")
	public String location(HttpServletRequest req , Map<String,Object> model){
		
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 获取物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        List<Warehouse> warehouseList = new ArrayList<Warehouse>();
        warehouseList.add(currentPhysicalWarehouse);
        
		Map<String,Object> searchMap = new HashMap<String,Object>();
		
		String status = req.getParameter("status");
		String locationCode = req.getParameter("location_code");
		
		searchMap.put("physicalWarehouseId", currentPhysicalWarehouse.getPhysical_warehouse_id());
		model.put("warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		
		if(!WorkerUtil.isNullOrEmpty(status)){
			if("Y".equals(status)){
				searchMap.put("y_status", "Y");
			}
			if("N".equals(status)){
				searchMap.put("n_status", "N");
			}
			model.put("status", status);
		}
		if(!WorkerUtil.isNullOrEmpty(locationCode)){
			searchMap.put("locationCode", locationCode);
			model.put("location_code", locationCode);
		}
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter();
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		
		List<Map> locationList = inventoryBiz.getLocation(searchMap);
		model.put("page", page);
		model.put("locationList", locationList);
		model.put("warehouseList", warehouseList);
		model.put("location_type_map", Location.LOCATION_TYPE_MAP);
		return "inventory/location";
	}
	
//	@RequestMapping(value="locationV2")
//	@ResponseBody
//	public Object locationV2(HttpServletRequest req){
//		
//		
//		Subject subject = SecurityUtils.getSubject();
//        Session session = subject.getSession();
//        Map<String,Object> resMap = new HashMap<String,Object>();
//        
//        
//        // 获取物理仓
//        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
//        List<Warehouse> warehouseList = new ArrayList<Warehouse>();
//        warehouseList.add(currentPhysicalWarehouse);
//        
//		Map<String,Object> searchMap = new HashMap<String,Object>();
//		
//		String status = req.getParameter("status");
//		String locationCode = req.getParameter("location_code");
//		
//		searchMap.put("physicalWarehouseId", currentPhysicalWarehouse.getPhysical_warehouse_id());
//		resMap.put("warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
//		
//		if(!WorkerUtil.isNullOrEmpty(status)){
//			if("Y".equals(status)){
//				searchMap.put("y_status", "Y");
//			}
//			if("N".equals(status)){
//				searchMap.put("n_status", "N");
//			}
//			resMap.put("status", status);
//		}
//		if(!WorkerUtil.isNullOrEmpty(locationCode)){
//			searchMap.put("locationCode", locationCode.toUpperCase().replace("-", ""));
//			resMap.put("location_code", locationCode.toUpperCase().replace("-", ""));
//		}
//		
//		PageParameter page = null;
//		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
//			page = new PageParameter();
//		}else{
//			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
//		}
//		searchMap.put("page", page);
//		
//		List<Map> locationList = inventoryBiz.getLocation(searchMap);
//		resMap.put("page", page);
//		resMap.put("locationList", locationList);
//		resMap.put("warehouseList", warehouseList);
//		return resMap;
//	}
	
	
	
	
	/********************************************************************************************
	 * 库位管理二期
	 * @author hzhang1
	 * 2016-05-14
	 ********************************************************************************************/
	@RequestMapping(value="getLocationInfo")
	@ResponseBody
	public Object getLocationInfo(HttpServletRequest req){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		resMap.put("currentPhysicalWarehouse", currentPhysicalWarehouse);
		resMap.put("customers", customers);
		return resMap;
	}
	
	@RequestMapping(value="locationNew")
	@ResponseBody
	public Object locationNew(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map<String,Object> searchMap = new HashMap<String,Object>();
		
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		
        searchMap.put("physicalWarehouseId", currentPhysicalWarehouse.getPhysical_warehouse_id());
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("location_barcode"))){
			searchMap.put("location_code", req.getParameter("location_barcode").toString().toUpperCase().replace("-", ""));
		}
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("locationType"))){
			searchMap.put("location_type", req.getParameter("locationType"));
			resMap.put("locationType", req.getParameter("locationType"));
		}
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("locationStatus"))){
			if("Y".equals(req.getParameter("locationStatus"))){
				searchMap.put("y_status", "Y");
			}
			if("N".equals(req.getParameter("locationStatus"))){
				searchMap.put("n_status", "N");
			}
			resMap.put("locationStatus", req.getParameter("locationStatus"));
		}
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("is_delete"))){
			searchMap.put("is_delete", req.getParameter("is_delete"));
			resMap.put("is_delete", req.getParameter("is_delete"));
		}
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter();
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		searchMap.put("type", "search");
		
		List<Map> locationNewList = inventoryBiz.getLocationV2(searchMap);
		resMap.put("locationList", locationNewList);
		resMap.put("currentPhysicalWarehouse", currentPhysicalWarehouse);
		resMap.put("customers", customers);
		resMap.put("page", page);
		return resMap;
	}
	
	@RequestMapping(value="locationInsertNew")
	@ResponseBody
	public Object locationInsertNew(Location location){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		
		location.setPhysical_warehouse_id(currentPhysicalWarehouse.getPhysical_warehouse_id());
		location.setCreated_user(sysUser.getUsername());
		location.setLast_updated_user(sysUser.getUsername());
		location.setIs_empty("Y");
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map returnMap = inventoryBiz.insertLocationV2(location,"insert");
		return returnMap;
		
	}
	
	
	@RequestMapping(value="locationUpdateNew")
	@ResponseBody
	public Object locationUpdateNew(Location location){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		location.setPhysical_warehouse_id(currentPhysicalWarehouse.getPhysical_warehouse_id());
		//location.setIs_delete("N");
		location.setLast_updated_user(sysUser.getUsername());
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map returnMap = inventoryBiz.insertLocationV2(location,"update");
		return returnMap;
		
	}
	
	@RequestMapping(value="locationDeleteNew")
	@ResponseBody
	public Object locationDeleteNew(HttpServletRequest req){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		Integer loc＿id = null;
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("location_id"))){
			loc＿id = Integer.parseInt(req.getParameter("location_id"));
		}
		resMap = inventoryBiz.deleteLocationV2(loc＿id);
		return resMap;
	}
	
	@RequestMapping(value="locationRecoverNew")
	@ResponseBody
	public Object locationRecoverNew(HttpServletRequest req){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		Integer loc＿id = null;
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("location_id"))){
			loc＿id = Integer.parseInt(req.getParameter("location_id"));
		}
		resMap = inventoryBiz.recoverLocationV2(loc＿id);
		return resMap;
	}
	
	@RequestMapping(value="exportLocation")
	@SuppressWarnings("unchecked")
	public ModelAndView exportLocation(HttpServletRequest req , Map<String,Object> model){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		ViewExcel viewExcel = new ViewExcel(); 
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 获取当前物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
        // 获取货主
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
		// 拥有的仓库权限列表
        Map<String,Object> searchMap = new HashMap<String,Object>();
        searchMap.put("physicalWarehouseId", currentPhysicalWarehouse.getPhysical_warehouse_id());
        if(!WorkerUtil.isNullOrEmpty(req.getParameter("location_barcode"))){
			searchMap.put("location_code", req.getParameter("location_barcode").toString().toUpperCase().replace("-", ""));
		}
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("locationType"))){
			searchMap.put("location_type", req.getParameter("locationType"));
			resMap.put("locationType", req.getParameter("locationType"));
		}
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("locationStatus"))){
			if("Y".equals(req.getParameter("locationStatus"))){
				searchMap.put("y_status", "Y");
			}
			if("N".equals(req.getParameter("locationStatus"))){
				searchMap.put("n_status", "N");
			}
			resMap.put("locationStatus", req.getParameter("locationStatus"));
		}
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("is_delete"))){
			searchMap.put("is_delete", req.getParameter("is_delete"));
			resMap.put("is_delete", req.getParameter("is_delete"));
		}
		
		searchMap.put("type", "export");
		
		// 2.查找订单
		List<Map> locationNewList = inventoryBiz.getLocationV2(searchMap);
		model.put("type", "exportLocation");
		model.put("locationList", locationNewList);
		
		// 3.返回订单结果
		return new ModelAndView(viewExcel, model);  
	}
	
	@RequestMapping(value="downloadLocation")
	public ModelAndView downloadLocation(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
        Workbook wb = new HSSFWorkbook(); // 创建excel工作簿
        Sheet sheet = wb.createSheet("template"); // 创建第一个sheet（页），并命名
        Row row = sheet.createRow((short) 0); // 创建第一行

        CellStyle cellStyle=wb.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex()); // 前景色
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);  
        
        Cell cell = row.createCell(0); // 创建列（每行里的单元格）
        cell.setCellValue("物理仓");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("货主");
        cell = row.createCell(2);
        cell.setCellValue("库位编码");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("区域");
        cell = row.createCell(4);
        cell.setCellValue("库位类型");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("循环级别");
        cell = row.createCell(6);
        cell.setCellValue("价值级别");
        cell = row.createCell(7);
        cell.setCellValue("是否允许混放商品");
        cell = row.createCell(8);
        cell.setCellValue("是否允许混放批次");
        cell = row.createCell(9);
        cell.setCellValue("是否在计算库容包括在内");
        cell = row.createCell(10);
        cell.setCellValue("可放托盘数量");
        cell = row.createCell(11);
        cell.setCellValue("体积");
        cell = row.createCell(12);
        cell.setCellValue("重量");
        cell = row.createCell(13);
        cell.setCellValue("长");
        cell = row.createCell(14);
        cell.setCellValue("宽");
        cell = row.createCell(15);
        cell.setCellValue("高");
        cell = row.createCell(16);
        cell.setCellValue("坐标（X）");
        cell = row.createCell(17);
        cell.setCellValue("坐标（Y）");
        cell = row.createCell(18);
        cell.setCellValue("坐标（Z）");
        cell = row.createCell(19);
        cell.setCellValue("数量限制");
        cell = row.createCell(20);
        cell.setCellValue("忽略LPN");
        cell = row.createCell(21);
        cell.setCellValue("上架顺序");
        cell = row.createCell(22);
        cell.setCellValue("拣货顺序");
        
        String typeList[] = {"存储区","件拣货区","箱拣货区","Return区","二手区","耗材区","质检区"};
        String yOrN[] = {"1","0"};
        // 加载下拉列表内容
 		DVConstraint constraint = DVConstraint.createExplicitListConstraint(typeList);
 		// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
 		CellRangeAddressList regions = new CellRangeAddressList(1,500,4,4);
 		// 数据有效性对象
 		HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
 		sheet.addValidationData(data_validation_list);
        
 		constraint = DVConstraint.createExplicitListConstraint(yOrN);
 		regions = new CellRangeAddressList(1,500,7,9);
 		data_validation_list = new HSSFDataValidation(regions, constraint);
 		sheet.addValidationData(data_validation_list);
 		
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
		response.setHeader("Content-Disposition", "attachment;filename=" + new String(("库位导入模板.xls").getBytes(), "iso-8859-1"));
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
	
	@RequestMapping(value="uploadLocationFile")
	//@ResponseBody
	public String uploadLocationFile(@RequestParam("uploadfile") CommonsMultipartFile file,HttpServletRequest request, Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
        List<Warehouse> physicalWarehouses = warehouseBiz.findAllPhysical();
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
        Map<String,Object> physicalWarehouseMap = new HashMap<String,Object>();
        Map<String,Object> customersMap = new HashMap<String,Object>();
        
        for(Warehouse w:physicalWarehouses){
        	physicalWarehouseMap.put(w.getWarehouse_name(),w.getWarehouse_id().toString());
        }
        
        for(WarehouseCustomer wc:customers){
        	customersMap.put(wc.getName(), wc.getCustomer_id().toString());
        }
        
        
        Map<String,Object> resMap = new HashMap<String,Object>();
		if (!file.isEmpty()) {
			String type = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
			
			boolean isE2007 = false;	//判断是否是excel2007格式
			if(type.endsWith("xlsx"))
				isE2007 = true;
			
            try {
				resMap= inventoryBiz.uploadLocationV2(file.getInputStream(),isE2007,sysUser.getUsername(),physicalWarehouseMap,customersMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		model.put("resMap", resMap);
		return "inventory/location";
	}
	
	

	/****************************************
	 * 以下接口针对的是库存导入 
	 * by hzhang1
	 ****************************************/
	@RequestMapping(value="uploadInventory")
	public String uploadInventory(HttpServletRequest req, Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		String customerId = req.getParameter("customer_id");
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		if(WorkerUtil.isNullOrEmpty(customerId)){
			model.put("waitImportInventory", inventoryDao.selectImportProductLocation(null, currentPhysicalWarehouse.getPhysical_warehouse_id(), null));
		}else{
			model.put("customer_id", customerId);
			model.put("waitImportInventory", inventoryDao.selectImportProductLocation(Integer.parseInt(customerId.toString()), currentPhysicalWarehouse.getPhysical_warehouse_id(), null));
		}
		model.put("currentPhysicalWarehouse", currentPhysicalWarehouse);
		model.put("customers", customers);
		return "tmpProductLocation/uploadInventory";
	}
	
	// 库存导入模板下载
	@RequestMapping(value="downloadInventory")
	public ModelAndView downloadInventory(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
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
        cell.setCellValue("物理仓名称");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("商品条码");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("库位条码");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue("数量");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("生产日期");
        cell = row.createCell(6);
        cell.setCellValue("过保日期");
        
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
        List<Warehouse> physicalWarehouses = warehouseBiz.findAllPhysical();
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
        String physicalWarehouseList = "";
        String customersList = "";
        
        for(Warehouse w:physicalWarehouses){
        	physicalWarehouseList += (w.getWarehouse_name()+",");
        }
        
        for(WarehouseCustomer wc:customers){
        	customersList += (wc.getName()+",");
        }
        // 加载下拉列表内容
 		DVConstraint constraint = DVConstraint.createExplicitListConstraint(physicalWarehouseList.substring(0, physicalWarehouseList.length()-1).split(","));
 		// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
 		CellRangeAddressList regions = new CellRangeAddressList(1,500,1,1);
 		// 数据有效性对象
 		HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
 		sheet.addValidationData(data_validation_list);
        
 		constraint = DVConstraint.createExplicitListConstraint(customersList.substring(0, customersList.length()-1).split(","));
 		regions = new CellRangeAddressList(1,500,0,0);
 		data_validation_list = new HSSFDataValidation(regions, constraint);
 		sheet.addValidationData(data_validation_list);
 		
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
		response.setHeader("Content-Disposition", "attachment;filename=" + new String(("库存导入模板.xls").getBytes(), "iso-8859-1"));
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
	
	// 导入库存信息
	@RequestMapping(value="uploadInventoryFile")
	public String uploadInventoryFile(@RequestParam("uploadfile") CommonsMultipartFile file,HttpServletRequest request, Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
        List<Warehouse> physicalWarehouses = warehouseBiz.findAllPhysical();
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
        Map<String,Object> physicalWarehouseMap = new HashMap<String,Object>();
        Map<String,Object> customersMap = new HashMap<String,Object>();
        
        for(Warehouse w:physicalWarehouses){
        	physicalWarehouseMap.put(w.getWarehouse_name(),w.getWarehouse_id().toString());
        }
        
        for(WarehouseCustomer wc:customers){
        	customersMap.put(wc.getName(), wc.getCustomer_id().toString());
        }
        
        
        Map<String,Object> resMap = new HashMap<String,Object>();
		if (!file.isEmpty()) {
			String type = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
			
			boolean isE2007 = false;	//判断是否是excel2007格式
			if(type.endsWith("xlsx"))
				isE2007 = true;
			
            try {
				resMap= inventoryBiz.uploadInventory(file.getInputStream(),isE2007,sysUser.getUsername(),physicalWarehouseMap,customersMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		model.put("resMap", resMap);
		return "tmpProductLocation/uploadInventory";
	}
	
	/**
	 * hchen1
	 * 获取取消的订单
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="transitionLocation")
	public String goodsReturn(HttpServletRequest request , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Warehouse> logicWarehouses = (List<Warehouse>)session.getAttribute("userLogicWarehouses");
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");

		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			model.put("created_time", createdTime);
		}else{
			model.put("created_time", DateUtils.getDateString(7, "yyyy-MM-dd", ""));
		}

		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			model.put("delivery_time", deliveryTime);
		}else{
			model.put("delivery_time", DateUtils.getDateString(-1, "yyyy-MM-dd", ""));
		}
		
		model.put("warehouseCustomerList", warehouseCustomerList);
		model.put("logicWarehouses", logicWarehouses);
		// 2.返回订单
		return "inventory/transitionLocation";
	}
	
	
	@RequestMapping(value="/searchTransitionLocation")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object searchReturn(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 1.获取从前端传来的数据
		String customerId = request.getParameter("customer_id");
		String barcode = request.getParameter("barcode");
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");
		String orderId = request.getParameter("orderId");
		String warehouse_id = request.getParameter("warehouse_id");
		String customerString ="(";
		
		if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
			searchMap.put("warehouse_id", warehouse_id);
		}
		
		if(!WorkerUtil.isNullOrEmpty(customerId)){
			searchMap.put("customer_id", customerId);
		}else {
			List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
			for (WarehouseCustomer warehouseCustomer : warehouseCustomerList) {
				customerString  = customerString+warehouseCustomer.getCustomer_id()+",";
			}
			customerString = customerString.substring(0, customerString.length()-1);
			customerString  = customerString+")";
			logger.info(customerString);
			searchMap.put("customerString", customerString);
		}
		if(!WorkerUtil.isNullOrEmpty(orderId)){
			searchMap.put("orderId", orderId);
		}
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		if(!WorkerUtil.isNullOrEmpty(warehouse)){
			searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		}
		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			if("2016-08-02".compareTo(createdTime)>0){
				searchMap.put("created_time", "2016-08-02");
			}else {
				searchMap.put("created_time", createdTime);
			}
		}else{
			searchMap.put("created_time", DateUtils.getDateString(7, "yyyy-MM-dd", ""));
		}

		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			searchMap.put("delivery_time", deliveryTime);
		}else{
			searchMap.put("delivery_time", DateUtils.getDateString(0, "yyyy-MM-dd", ""));
		}

		// 2.查询订单
		try {
			resultMap = inventoryBiz.getOrderCancelList(searchMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	
	/**
	 * RF移库之库存移动
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/webStockMove") 
	public String webStockMove( HttpServletRequest req , Map<String,Object> model ){
		return "/inventory/webStockMove";    
	}
	
	/**
	 * RF移库之库存移动 -- 源库位检查
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/stockMoveSourceLocationCheck")
	@ResponseBody
	public Object stockMoveSourceLocationCheck(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		String locationBarcode = req.getParameter("from_location_barcode").toString();
		logger.info("RF移库之库存移动 ,fromLocationBarcode:"+locationBarcode);
		resMap = inventoryBiz.checkStockMoveSourceLocation(locationBarcode,currentPhysicalWarehouse.getPhysical_warehouse_id());
		return resMap;
	}
	
	/**
	 * RF移库之库存移动 -- 目标库位检查 & 完成移库
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/stockMove")
	@ResponseBody
	public Object stockMove(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		String fromLocationBarcode = req.getParameter("from_location_barcode").toString();
		String toLocationBarcode = req.getParameter("to_location_barcode").toString();
		Integer plId = Integer.parseInt(String.valueOf(req.getParameter("pl_id")));
		Integer moveNum = Integer.parseInt(String.valueOf(req.getParameter("move_num")));
		logger.info("RF移库之库存移动 ,fromLocationBarcode:"+fromLocationBarcode
				+", toLocationBarcode:"+toLocationBarcode+", plId:"+plId+", moveNum:"+moveNum);
		try{
			resMap = inventoryBiz.stockMove(fromLocationBarcode,toLocationBarcode,plId,moveNum,currentPhysicalWarehouse.getPhysical_warehouse_id());
		}catch (Exception e) {
			resMap.put("success", false);
			resMap.put("error", e.getMessage());
		}
		return resMap;
	}
}
