package com.leqee.wms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.biz.BatchTaskBiz;
import com.leqee.wms.biz.ReplenishmentBiz;
import com.leqee.wms.biz.ScheduleQueueReplenishmentBiz;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.SysUserDao;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;


/**
 * 补货相关逻辑
 * @author hzhang1
 * @date 2016-6-21
 * @version 1.0.0
 */
@Controller
@RequestMapping(value="/replenishment")
public class ReplenishmentController {

	private Logger logger = Logger.getLogger(ReplenishmentController.class);
	
	@Autowired
	ReplenishmentBiz replenishmentBiz;
	@Autowired
	ProductDao productDao;
	@Autowired
	ScheduleQueueReplenishmentBiz scheduleQueueReplenishmentBiz;
	@Autowired
	SysUserDao sysUserDao;
	@Autowired
	BatchTaskBiz batchTaskBiz;
	
	/**
	 * 补货任务显示页面 
	 * @author hzhang1 
	 * @date 2016-06-21
	 * @param req model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/show")
	public String showReplenishment(HttpServletRequest req , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
		String start_time = req.getParameter("start_time");
		
		if(!WorkerUtil.isNullOrEmpty(start_time)){
			model.put("start_time", start_time );
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf.format(new Date(System.currentTimeMillis()));
			model.put("start_time", startDate );
		}
		
		model.put("warehouseList", warehouseList );
        model.put("customers", customers);
		return "/replenishment/show";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/task_insert")
	public String showInsertReplenishment(HttpServletRequest req , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		String start_time = req.getParameter("start_time");
		
		if(!WorkerUtil.isNullOrEmpty(start_time)){
			model.put("start_time", start_time );
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf.format(new Date(System.currentTimeMillis()));
			model.put("start_time", startDate );
		}
		
        model.put("customers", customers);
		return "/replenishment/task_insert";
	}
	
	/**
	 * 校验库位
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/checkLocationBarcode")
	@ResponseBody
	public Object checkLocationBarcode(HttpServletRequest req){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		String location_barcode = req.getParameter("location_barcode");
		Map<String,Object> resMap = new HashMap<String,Object>();
		if(WorkerUtil.isNullOrEmpty(replenishmentBiz.checkLocationBarcode(currentPhysicalWarehouse.getPhysical_warehouse_id(),location_barcode.replace("-", "")))){
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "该库位不存在");
		}else{
			resMap.put("result", Response.SUCCESS);
			resMap.put("note", "该库位校验成功");
		}
		return resMap;
		
	}
	
	
	/**
	 * ajax调用接口返回补货任务
	 * @author hzhang1 
	 * @date 2016-06-21
	 * @param req model
	 * @return
	 */
	@RequestMapping(value="/list")
	@ResponseBody
	public Object showReplenishmentList(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		
		// 1.获取前端传递的参数
		String batch_pick_sn = req.getParameter("batch_pick_sn");
		String customer_id = req.getParameter("customer_id");
		String warehouse_id = req.getParameter("warehouse_id");
		String location_type = req.getParameter("location_type");
		String task_level = req.getParameter("task_level");
		String print_status = req.getParameter("print_status");
		String task_status = req.getParameter("task_status");
		String batch_task_sn = req.getParameter("batch_task_sn");
		String start_time = req.getParameter("start_time");
		String task_id = req.getParameter("task_id");
		
		
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map<String,Object> searchMap = new HashMap<String,Object>();
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		searchMap.put("task_type", "REPLENISHMENT");
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,15);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		if(!WorkerUtil.isNullOrEmpty(start_time)) searchMap.put("start_time", start_time);
		if(!WorkerUtil.isNullOrEmpty(task_level)) searchMap.put("task_level", task_level);
		if(!WorkerUtil.isNullOrEmpty(task_status)) searchMap.put("task_status", task_status);
		if(!WorkerUtil.isNullOrEmpty(warehouse_id)) searchMap.put("warehouse_id", warehouse_id);
		
        if(!WorkerUtil.isNullOrEmpty(batch_pick_sn)){
        	searchMap.put("batch_pick_sn", batch_pick_sn);
        	List<Map> replenishmentTaskList = replenishmentBiz.getReplenishmentTaskByBatchPickSn(searchMap);

    		resMap.put("result", Response.SUCCESS);
    		resMap.put("note", Response.SUCCESS);
        	
    		resMap.put("page", page);
    		resMap.put("replenishmentTaskList", replenishmentTaskList);
        	
		}
        else{
        	if(!WorkerUtil.isNullOrEmpty(task_id)) {
    			searchMap.put("task_id", task_id);
    		}else{
    			if(!WorkerUtil.isNullOrEmpty(customer_id)) {
    				searchMap.put("customer_id", customer_id);
    			}else{
    				searchMap.put("customers", customers);
    			}
    			if(!WorkerUtil.isNullOrEmpty(location_type)) searchMap.put("location_type", location_type);
    			if(!WorkerUtil.isNullOrEmpty(print_status) && "Y".equals(print_status)) searchMap.put("print_status", print_status);
    			if(!WorkerUtil.isNullOrEmpty(print_status) && "N".equals(print_status)) searchMap.put("print_status_n", print_status);
    			if(!WorkerUtil.isNullOrEmpty(batch_task_sn)) searchMap.put("batch_task_sn", batch_task_sn);
    		}
    		
    		resMap.put("result", Response.SUCCESS);
    		resMap.put("note", Response.SUCCESS);
    		resMap.put("page", page);
    		resMap.put("replenishmentTaskList", replenishmentBiz.getReplenishmentTask(searchMap));
        }
        
		return resMap;
	}
	
	
	
	@RequestMapping(value="/checkUser")
	@ResponseBody
	public Object checkUser(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
        String userName = req.getParameter("user_name");
        SysUser sysUser = sysUserDao.selectByUsername(userName);
		if(WorkerUtil.isNullOrEmpty(sysUser)){
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "工牌不存在！");
		}else{
			resMap.put("result", Response.SUCCESS);
			resMap.put("note", Response.SUCCESS);
		}
		return resMap;
	}
	
	/**
	 * 打印补货任务 
	 * @author hzhang1 
	 * @date 2016-06-22
	 * @param req model
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/printReplenishment")
	public String printReplenishment(HttpServletRequest req , Map<String,Object> model){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		String temp = req.getParameter("task_id_list");
		String userName = req.getParameter("user_name");
		Map<String,Object> returnMap = new HashMap<String,Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			returnMap = replenishmentBiz.printReplenishmentTask(temp.trim(),userName);
			model.put("printReplenishmentTaskList", (List<Map>)returnMap.get("printReplenishmentTaskList"));
			model.put("bhCode", returnMap.get("bhCode").toString());
			model.put("printTime", sdf.format(new Date()));
			model.put("actionUser", userName);
			model.put("result", Response.SUCCESS);
			model.put("note", "成功打印补货任务");
		} catch (Exception e) {
			logger.error("printReplenishment error:", e);
			model.put("result", Response.FAILURE);
			model.put("note", e.getMessage());
			model.put("bhCode", "");
			model.put("printTime", sdf.format(new Date()));
			model.put("actionUser", userName);
			model.put("printReplenishmentTaskList",null);
		}
		return "/replenishment/print_replenishment";
	}
	
	
	/**
	 * 更新补货任务 
	 * @author hzhang1 
	 * @date 2016-06-22
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/updateReplenishment")
	@ResponseBody
	public Object updateReplenishment(HttpServletRequest req){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
        String taskId = req.getParameter("task_id");
        String fromLocationBarcode = req.getParameter("from_location_barcode");
        String toLocationBarcode = req.getParameter("to_location_barcode");
		String quantity = req.getParameter("quantity");
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		try {
			resMap = replenishmentBiz.updateReplenishment(taskId,fromLocationBarcode,toLocationBarcode,quantity,currentPhysicalWarehouse.getPhysical_warehouse_id(),sysUser.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "更新补货任务失败，原因："+e.getMessage());
		}
		return resMap;
	}

	
	/**
	 * 完结补货任务 (完成 or取消)
	 * @author hzhang1 
	 * @date 2016-06-22
	 * @param req model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/finishOrCancel")
	@ResponseBody
	public Object finishOrCancel(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		Map<String,Object> resMap = new HashMap<String,Object>();
		String type = req.getParameter("type");
		String taskIdList[] = req.getParameter("task_list").toString().split(",");
		logger.info("完结补货任务，此次操作类型为："+type+",taskIdList:"+taskIdList.toString()+",size:"+taskIdList.length);
		if("finish".equals(type)){
			try {
				for(String temp : taskIdList){
					String arr[] = temp.split("_");
					resMap = replenishmentBiz.updateReplenishment(arr[0],arr[1].toUpperCase().replace("-", ""),arr[2].toUpperCase().replace("-", ""),arr[3],currentPhysicalWarehouse.getPhysical_warehouse_id(),sysUser.getUsername());
					if("failure".equals(resMap.get("result").toString())){
						throw new RuntimeException("更新补货任务失败"+resMap.get("note").toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("完结补货任务失败", e);
				resMap.put("result", Response.FAILURE);
				resMap.put("note", "完成补货任务失败，原因："+e.getMessage());
			}
		}else if("cancel".equals(type)){
			String cancelReason = req.getParameter("cancel_reason");
			try {
				for(String temp : taskIdList){
					String tempArr[] = temp.split("_");
					String taskId = tempArr[0];
					String fromLocationBarcode = tempArr[1].toUpperCase().trim();
					resMap = replenishmentBiz.cancelReplenishmentTask(taskId,fromLocationBarcode.toUpperCase().replace("-", ""),cancelReason,sysUser.getUsername());
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("取消补货任务失败", e);
				resMap.put("result", Response.FAILURE);
				resMap.put("note", "取消补货任务失败，原因："+e.getMessage());
			}
		}
		return resMap;
	}
	
	
	
	
	
	
	
	/**************************************************************************************************
	 * 分割线,以下接口是针对RF扫描枪的补货
	 * by hzhang1 2016-06-22
	 * ************************************************************************************************/
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getCustomers")
	@ResponseBody
	public Object getCustomers(HttpServletRequest req){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        Map<String,Object> resMap = new HashMap<String,Object>();
        if(WorkerUtil.isNullOrEmpty(customers)){
        	resMap.put("result", Response.FAILURE);
        	resMap.put("success", Boolean.FALSE);
			resMap.put("note", "获取货主列表失败");
			resMap.put("customers", null);
        }else{
        	resMap.put("result", Response.SUCCESS);
        	resMap.put("success", Boolean.TRUE);
			resMap.put("note", "获取货主列表成功");
			resMap.put("customers", customers);
        }
        return resMap;
	}
	
	// 1.RF获取补货任务(每次只获取到一个任务)
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getReplenishmentTask")
	@ResponseBody
	public Object getReplenishmentTask(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		
		String customerId = req.getParameter("customer_id");
		if(WorkerUtil.isNullOrEmpty(customerId)){
			customerId = "";
		}
		String taskLevel = req.getParameter("task_level");
		Map<String,Object> resMap = new HashMap<String,Object>();
		resMap = replenishmentBiz.getReplenishmentTaskFromRF(customerId,taskLevel,currentPhysicalWarehouse.getPhysical_warehouse_id(),"REPLENISHMENT");
		logger.info("RF获取补货任务，customerId："+customerId+",获取到的补货任务："+resMap.toString());
		return resMap;
		
	}
	
	// 2.RF补货下架扫描库位获取商品信息
	@SuppressWarnings("unchecked")
	@RequestMapping(value="getOffShelfReplenishmentInfo")
	@ResponseBody
	public Object getOffShelfReplenishmentInfo(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		String fromLocationBarcode = req.getParameter("from_location_barcode");
		String taskId = req.getParameter("task_id");
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		resMap = replenishmentBiz.getReplenishmentProductFromRF(fromLocationBarcode,currentPhysicalWarehouse.getPhysical_warehouse_id(),"REPLENISHMENT",taskId,sysUser.getUsername());
		logger.info("RF补货下架扫描库位获取商品信息，taskId："+taskId+",获取到的库位商品："+resMap.toString());
		return resMap;
	}
	
	
	// 3.RF补货下架
	@SuppressWarnings("unchecked")
	@RequestMapping(value="offShelfReplenishmentTask")
	@ResponseBody
	public Object offShelfReplenishmentTask(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		
		String locationBarcode = WorkerUtil.isNullOrEmpty(req.getParameter("from_location_barcode"))?"":req.getParameter("from_location_barcode").toString();
		String toLocationBarcode = WorkerUtil.isNullOrEmpty(req.getParameter("to_location_barcode"))?"":req.getParameter("to_location_barcode").toString();
		String productId = WorkerUtil.isNullOrEmpty(req.getParameter("product_id"))?"":req.getParameter("product_id").toString();
		String status = WorkerUtil.isNullOrEmpty(req.getParameter("status"))?"":req.getParameter("status").toString();
		String validity = WorkerUtil.isNullOrEmpty(req.getParameter("validity"))?"":req.getParameter("validity").toString();
		String batchSn = WorkerUtil.isNullOrEmpty(req.getParameter("batch_sn"))?"":req.getParameter("batch_sn").toString();
		String serialNumber = WorkerUtil.isNullOrEmpty(req.getParameter("serial_number"))?"":req.getParameter("serial_number").toString();
		String taskId = req.getParameter("task_id");
		Integer warehouseId = Integer.parseInt(req.getParameter("warehouse_id").toString());
		Integer quantity = Integer.parseInt(req.getParameter("quantity").toString());
		
		logger.info("RF补货下架，locationBarcode:" + locationBarcode + "toLocationBarcode:"+toLocationBarcode+",productId:"
				+ productId + ",status:" + status + ",validity:" + validity
				+ ",batchSn:" + batchSn + ",serialNumber:" + serialNumber
				+ ",taskId:" + taskId + ",quantity:" + quantity);
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		try {
			resMap = replenishmentBiz.offShelfReplenishmentFromRF(taskId,warehouseId,locationBarcode,toLocationBarcode,
					productId, status, validity, batchSn, serialNumber,
					currentPhysicalWarehouse.getPhysical_warehouse_id(),quantity,
					sysUser.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
			resMap.put("result", Response.FAILURE);
			resMap.put("success", Boolean.FALSE);
			resMap.put("note", "RF补货下架失败"+e.getMessage());
		}
		return resMap;
	}
	
	// 4.RF补货下架时任务取消
	@SuppressWarnings("unchecked")
	@RequestMapping(value="offShelfReplenishmentTaskCancel")
	@ResponseBody
	public Object offShelfReplenishmentTaskCancel(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		Map<String,Object> resMap = new HashMap<String,Object>();
		String cancelReason = req.getParameter("cancel_reason");
		String taskId = req.getParameter("task_id");
		String fromLocationBarcode = req.getParameter("from_location_barcode");
		logger.info("RF补货下架时取消任务,taskId:"+taskId+",cancelReason:"+cancelReason+",fromLocationBarcode:"+fromLocationBarcode);
		try {
			resMap = replenishmentBiz.cancelReplenishmentTask(taskId,fromLocationBarcode.replace("-", ""),cancelReason,sysUser.getUsername());
		} catch (Exception e) {
			resMap.put("result", Response.FAILURE);
			resMap.put("success", Boolean.FALSE);
			resMap.put("note", "取消补货任务失败!");
		}
		return resMap;
	}
	
	// 5.RF补货上架扫描补货标签得到信息
	@SuppressWarnings("unchecked")
	@RequestMapping(value="getOnShelfReplenishmentInfo")
	@ResponseBody
	public Object getOnShelfReplenishmentInfo(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		String bhCode = req.getParameter("bh_code");
		resMap = replenishmentBiz.getReplenishmentInfoByCodeFromRF(bhCode,currentPhysicalWarehouse.getPhysical_warehouse_id());
		return resMap;
	}
	
	// 6.RF补货上架
	@SuppressWarnings("unchecked")
	@RequestMapping(value="onShelfReplenishmentTask")
	@ResponseBody
	public Object onShelfReplenishmentTask(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		String bhCode = WorkerUtil.isNullOrEmpty(req.getParameter("bh_code"))?"":req.getParameter("bh_code").toString();
		String barcode = req.getParameter("barcode");
		Integer quantity = Integer.parseInt(req.getParameter("quantity").toString());
		String toLocationBarcode = req.getParameter("to_location_barcode");
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		try {
			resMap = replenishmentBiz.onShelfReplenishmentFromRF(bhCode,barcode,quantity,toLocationBarcode,currentPhysicalWarehouse.getPhysical_warehouse_id(),sysUser.getUsername(),sysUser.getId());
		} catch (Exception e) {
			e.printStackTrace();
			resMap.put("result", Response.FAILURE);
			resMap.put("success", Boolean.FALSE);
			resMap.put("note", "RF补货上架失败"+e.getMessage());
		}
		return resMap;
	}
	
	/**
	 * 打印补货标签 
	 * by ytchen 16.06.23
	 */
	@RequestMapping(value = "/printBH")
	public String printBH(HttpServletRequest req, HashMap<String, Object> model) {
		return "/replenishment/print_BHCode";
	}
	
	/**
	 * 获取补货标签条码
	 * by ytchen 16.06.23
	 */
	@RequestMapping(value = "/getBHBarcodes")
	@ResponseBody
	public Map getTrayBarcodes(HttpServletRequest req) {
		logger.info("replenishment/getBHBarcodes");
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		Integer number = Integer.parseInt(req.getParameter("number").trim());
		if (WorkerUtil.isNullOrEmpty(number)) {
			resultMap.put("error", "number is null or empty");
			logger.info("replenishment getBHBarcodes number is null or empty");
			return resultMap;
		}
		logger.info("replenishment getBHBarcodes params number:" + number);

		// 获取条码
		resultMap = replenishmentBiz.getTrayBarcodeForBH(number);

		return resultMap;
	}
	
	/**
	 * 打印补货标签条码
	 * by ytchen 16.06.23
	 */
	@RequestMapping(value = "print")
	public String print(HttpServletRequest req, HashMap<String, Object> model) {
		String BHCode = req.getParameter("code");
		String code[] = BHCode.split(",");
		List<String> codeList = new ArrayList<String>();
		for (String str : code) {
			codeList.add(str);
		}
		model.put("codeList", codeList);
		return "/replenishment/print";
	}
	
	/**
	 * 手动生成补货任务 
	 * by ytchen 16.06.23
	 */
	@RequestMapping(value = "/manulSetReplenishTask")
	public String manulSetReplenishTask(HttpServletRequest req, HashMap<String, Object> model) {
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowDate = sdf.format(new Date(System.currentTimeMillis()));
		model.put("start", nowDate );
		model.put("end", nowDate);
		model.put("customers", customers);
		return "/replenishment/manulSetReplenishTask";
	}
	
	/**
	 * 保存手动补货任务
	 * by ytchen 16.06.23
	 */
	@RequestMapping(value = "/saveManulReplenish")
	@ResponseBody
	public Map saveManulReplenish(HttpServletRequest req) {
		logger.info("replenishment/saveManulReplenish");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		/*
		 * 前端传值 
		 */
		String customerIdStr = req.getParameter("customer_id_list");// 0 or 1,2 or 3 
		String[] customerIdArr = customerIdStr.split(","); // 若为0，则需要遍历所有 设置过补货规则的 物理仓最低大于0的 货主
		if(customerIdArr.length == 0){
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "请选择货主！");
			return resultMap;
		}
		List<Integer> customerIdList = new ArrayList<Integer>();
		for (String customerId : customerIdArr) {
			// 全选  意味着 所有货主
			customerIdList.add(Integer.parseInt(customerId));
		}
		
		//库区
		String boxPiece = req.getParameter("box_piece");
		//商品条码
		String barcode = req.getParameter("barcode");
		Integer productId = 0;
		if(!WorkerUtil.isNullOrEmpty(barcode) && customerIdArr.length==1 && !customerIdStr.equals(0)){
			//初步判断 barcode的准确性
			Product product = productDao.selectProductByBarcodeCustomer(barcode,customerIdList.get(0));
			if(WorkerUtil.isNullOrEmpty(productId)){
				resultMap.put("result", Response.FAILURE);
				resultMap.put("note", "根据商品条码“"+barcode+"”与指定货主没有匹配到合适的商品信息！");
				return resultMap;
			}else{
				productId = product.getProduct_id();
			}
		}else if(!WorkerUtil.isNullOrEmpty(barcode)){
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "指定商品条码时，货主必须唯一选择！");
			return resultMap;
		}
		
		//物理仓
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse warehouse = (Warehouse)session.getAttribute("currentPhysicalWarehouse");
        Integer physicalWarehouseId = warehouse.getPhysical_warehouse_id();
        //当前用户
        String localUser = (String)SecurityUtils.getSubject().getPrincipal();
        //补货类型
		Integer taskLevel = Integer.parseInt(req.getParameter("task_level").trim());
		
		resultMap = scheduleQueueReplenishmentBiz.batchInsertScheduleQueueReplenishment(physicalWarehouseId,customerIdList,boxPiece,taskLevel,productId,localUser);
		if(Response.SUCCESS.equals(resultMap.get("result"))){
			//提示每个货主都分别有多少个箱规未维护
			List<String> specList = new ArrayList<String>();
			for(Integer customerIdx : customerIdList){
				Map<String,Object> map = productDao.selectCountUnCheckSpec(customerIdx);
				if(!WorkerUtil.isNullOrEmpty(map) && Integer.parseInt(String.valueOf(map.get("cou")))!=0) {
					specList.add(map.get("name")+"有"+map.get("cou")+"个商品未维护箱规！");
				}
			}
			resultMap.put("specList", specList);
		}
		return resultMap;
	}

	/**
	 * 根据起始时间查询补货任务
	 * by ytchen 16.06.26
	 */
	@RequestMapping(value = "/selectManulReplenish")
	@ResponseBody
	public Map<String, Object> selectManulReplenish(HttpServletRequest req) {
		logger.info("replenishment/selectManulReplenish");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		/*
		 * 前端传值 
		 */
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String start_time = req.getParameter("start");
		String end_time = req.getParameter("end");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowDate = sdf.format(new Date(System.currentTimeMillis()));
		if(!WorkerUtil.isNullOrEmpty(start_time)){
			searchMap.put("startTime", start_time+" 00:00:00");
		}
		if(!WorkerUtil.isNullOrEmpty(end_time)){
			searchMap.put("endTime", end_time+" 23:59:59");
		}else{
			searchMap.put("endTime", nowDate+" 23:59:59");
		}
		
		//物理仓
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse warehouse = (Warehouse)session.getAttribute("currentPhysicalWarehouse");
        Integer physicalWarehouseId = warehouse.getPhysical_warehouse_id();
        searchMap.put("physicalWarehouseId", physicalWarehouseId);
        PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,15);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		
		List<Map> list = scheduleQueueReplenishmentBiz.selectManulReplenishByPage(searchMap);
		
		resultMap.put("page", page);
		resultMap.put("list", list);
		return resultMap;
	}

	
	/**
	 * 分配补货任务
	 * by ytchen 16.07.02
	 */
	@RequestMapping(value = "/assignBatchTask")
	public String assignBatchTask(HttpServletRequest req, HashMap<String, Object> model) {
		// 获取传递过来的条件参数
		return "/replenishment/assignBatchTask";
	}
	/**
	 * 检分配补货任务 -- 查工牌号
	 * by ytchen
	 */
	@RequestMapping(value = "/checkEmployeeNo")
	@ResponseBody
	public Map checkEmployeeNo(HttpServletRequest req) {
		logger.info("ReplenishmentController /replenishment/checkEmployeeNo");
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		String employee_no = (String) req.getParameter("employee_no");
		if (WorkerUtil.isNullOrEmpty(employee_no)) {
			resultMap.put("success", false);
			resultMap.put("note", "请先扫描工牌号！");
			return resultMap;
		}

		SysUser sysuser = sysUserDao.selectByUsername(employee_no);
		if (WorkerUtil.isNullOrEmpty(sysuser)) {
			resultMap.put("success", false);
			resultMap.put("note", "根据输入工牌号“"+employee_no+"”未找到用户信息！");
			resultMap.put("note_id", 1);
			return resultMap;
		}
		resultMap.put("success", true);
		resultMap.put("employee_name", sysuser.getRealname());
		resultMap.put("employee_no", sysuser.getUsername());
		return resultMap;
	}
	
	/**
	 * 分配补货任务 -- 分配
	 * by ytchen 2016.07.02
	 */
	@RequestMapping(value = "/bindBatchTaskUser")
	@ResponseBody
	public Map<String, Object> bindBatchTaskUser(HttpServletRequest req) {
		logger.info("ReplenishmentController /replenishment/bindBatchTaskUser");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String batch_task_sn = (String) req.getParameter("batch_task_sn");
		String employee_no = (String) req.getParameter("employee_no");

		if (WorkerUtil.isNullOrEmpty(batch_task_sn)) {
			resultMap.put("success", false);
			resultMap.put("note", "请先扫描波次单号！");
			return resultMap;
		}

		SysUser sysuser = sysUserDao.selectByUsername(employee_no);

		if (WorkerUtil.isNullOrEmpty(sysuser)) {
			resultMap.put("success", false);
			resultMap.put("note", "根据输入工牌号“"+employee_no+"”未找到用户信息！");
			resultMap.put("note_id", 1);
			return resultMap;
		}

		resultMap = batchTaskBiz.bindBatchTaskForWeb(sysuser.getId(),sysuser.getUsername(), batch_task_sn);
		return resultMap;
	}
}

