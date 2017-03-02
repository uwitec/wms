package com.leqee.wms.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.api.util.Tools;
import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.ScheduleJobBiz;
import com.leqee.wms.biz.SysUserBiz;
import com.leqee.wms.biz.WarehouseBiz;
import com.leqee.wms.biz.impl.BatchPickBizImpl;
import com.leqee.wms.dao.BatchPickDao;
import com.leqee.wms.dao.BatchPickTaskDao;
import com.leqee.wms.dao.ConfigPrintDispatchBillDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.RegionDao;
import com.leqee.wms.dao.ScheduleJobDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.SysUserDao;
import com.leqee.wms.dao.UserActionBatchPickDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.UserActionWarehouseLoadDao;
import com.leqee.wms.dao.WarehouseLoadDao;
import com.leqee.wms.entity.BatchPickParam;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Region;
import com.leqee.wms.entity.ScheduleJob;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.util.ViewExcel;
import com.leqee.wms.util.WorkerUtil;

@Controller
@RequestMapping(value = "/history_query")
// 指定根路径
public class HistoryQueryController {

	private Logger logger = Logger.getLogger(HistoryQueryController.class);
	@Autowired
	InventoryBiz inventoryBiz;
	private InventoryDao inventoryDaoSlave;
    @Resource(name = "sqlSessionSlave")
	public void setInventoryDaoSlave(SqlSession sqlSession) {
	  this.inventoryDaoSlave = sqlSession.getMapper(InventoryDao.class);
	}
	/**
	 * hchen1
	 * 移库查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="move")
	public String move(HttpServletRequest request , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");

		
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");

		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			model.put("created_time", createdTime);
		}else{
			model.put("created_time", DateUtils.getDateString(3, "yyyy-MM-dd", ""));
		}

		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			model.put("delivery_time", deliveryTime);
		}else{
			model.put("delivery_time", DateUtils.getDateString(-1, "yyyy-MM-dd", ""));
		}
		
		model.put("warehouseCustomerList", warehouseCustomerList);
		model.put("warehouseList", warehouseList);
		// 2.返回订单
		return "historyDataQuery/moveInventory";
	}
	
	
	@RequestMapping(value="/moveQuery")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object moveQuery(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 1.获取从前端传来的数据
		String customerId = request.getParameter("customer_id");
		String created_user = request.getParameter("created_user");
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");
		String barcode = request.getParameter("barcode");
		String in_location_barcode = request.getParameter("in_location_barcode");
		String out_location_barcode = request.getParameter("out_location_barcode");
		String warehouse_id =request.getParameter("warehouse_id");
		if (!WorkerUtil.isNullOrEmpty(warehouse_id)) {
			searchMap.put("warehouse_id", warehouse_id);
		}
		if(!WorkerUtil.isNullOrEmpty(customerId)){
			searchMap.put("customer_id", customerId);
		}
		if(!WorkerUtil.isNullOrEmpty(created_user)){
			searchMap.put("created_user", created_user);
		}
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		if(!WorkerUtil.isNullOrEmpty(warehouse)){
			searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		}
		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			searchMap.put("created_time", createdTime);
		}
		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			searchMap.put("delivery_time", deliveryTime);
		}
		if (!WorkerUtil.isNullOrEmpty(in_location_barcode)) {
			searchMap.put("in_location_barcode", in_location_barcode);
		}
		if (!WorkerUtil.isNullOrEmpty(out_location_barcode)) {
			searchMap.put("out_location_barcode", out_location_barcode);
		}
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(request.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(request.getParameter("currentPage")),Integer.valueOf(request.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		// 2.查询订单
		try {
			resultMap = inventoryBiz.getMoveList(searchMap);
			resultMap.put("success",true);
			resultMap.put("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	/**
	 * 移库导出
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="exportmove")
	public ModelAndView exportMove(HttpServletRequest request , Map<String,Object> model){
		 ViewExcel viewExcel = new ViewExcel();    
		 Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession();
			Warehouse warehouse = (Warehouse) session
					.getAttribute("currentPhysicalWarehouse");
			Map<String,Object> searchMap = new HashMap<String,Object>();
			// 1.获取从前端传来的数据
			String customerId = request.getParameter("customer_id");
			String created_user = request.getParameter("created_user");
			String createdTime = request.getParameter("created_time");
			String deliveryTime = request.getParameter("delivery_time");
			String barcode = request.getParameter("barcode");
			String in_location_barcode = request.getParameter("in_location_barcode");
			String out_location_barcode = request.getParameter("out_location_barcode");
			if(!WorkerUtil.isNullOrEmpty(customerId)){
				searchMap.put("customer_id", customerId);
			}
			if(!WorkerUtil.isNullOrEmpty(created_user)){
				searchMap.put("created_user", created_user);
			}
			if(!WorkerUtil.isNullOrEmpty(barcode)){
				searchMap.put("barcode", barcode);
			}
			if(!WorkerUtil.isNullOrEmpty(warehouse)){
				searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
			}
			if(!WorkerUtil.isNullOrEmpty(createdTime)){
				searchMap.put("created_time", createdTime);
			}
			if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
				searchMap.put("delivery_time", deliveryTime);
			}
			if (!WorkerUtil.isNullOrEmpty(in_location_barcode)) {
				searchMap.put("in_location_barcode", in_location_barcode);
			}
			if (!WorkerUtil.isNullOrEmpty(out_location_barcode)) {
				searchMap.put("out_location_barcode", out_location_barcode);
			}
			
		 model.put("moveList", inventoryDaoSlave.selectMoveList(searchMap));
		 model.put("type", "exportmove");
	     return new ModelAndView(viewExcel, model);  
	}
	
	
	
	/**
	 * hchen1
	 * 复核查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="recheck")
	public String recheck(HttpServletRequest request , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");
		

		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			model.put("created_time", createdTime);
		}else{
			model.put("created_time", DateUtils.getDateString(3, "yyyy-MM-dd", ""));
		}

		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			model.put("delivery_time", deliveryTime);
		}else{
			model.put("delivery_time", DateUtils.getDateString(-1, "yyyy-MM-dd", ""));
		}
		
		model.put("warehouseCustomerList", warehouseCustomerList);
		model.put("warehouseList", warehouseList);
		// 2.返回订单
		return "historyDataQuery/recheckDataQuery";
	}
	
	
	@RequestMapping(value="/recheckQuery")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object recheckQuery(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 1.获取从前端传来的数据
		String customerId = request.getParameter("customer_id");
		String created_user = request.getParameter("created_user");
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");
		String barcode = request.getParameter("barcode");
		String batch_pick_id = request.getParameter("batch_pick_id");
		String order_id = request.getParameter("order_id");
		String oms_order_sn = request.getParameter("oms_order_sn");
		String warehouse_id =request.getParameter("warehouse_id");
		if (!WorkerUtil.isNullOrEmpty(warehouse_id)) {
			searchMap.put("warehouse_id", warehouse_id);
		}
		if (!WorkerUtil.isNullOrEmpty(oms_order_sn)||!WorkerUtil.isNullOrEmpty(order_id)||!WorkerUtil.isNullOrEmpty(batch_pick_id)) {
			searchMap.put("oms_order_sn", oms_order_sn);
			searchMap.put("batch_pick_id", batch_pick_id);
			searchMap.put("order_id", order_id);
		}else {
			
		
		if(!WorkerUtil.isNullOrEmpty(customerId)){
			searchMap.put("customer_id", customerId);
		}
		if(!WorkerUtil.isNullOrEmpty(created_user)){
			searchMap.put("created_user", created_user);
		}
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		if(!WorkerUtil.isNullOrEmpty(warehouse)){
			searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		}
		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			searchMap.put("created_time", createdTime);
		}
		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			searchMap.put("delivery_time", deliveryTime);
		}
		}
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(request.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(request.getParameter("currentPage")),Integer.valueOf(request.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		// 2.查询订单
		try {
			resultMap = inventoryBiz.getRecheckList(searchMap);
			resultMap.put("success",true);
			resultMap.put("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	/**
	 * 复核导出
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="exportrecheck")
	public ModelAndView exportRecheck(HttpServletRequest request , Map<String,Object> model){
		 ViewExcel viewExcel = new ViewExcel();    
		 Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession();
			Warehouse warehouse = (Warehouse) session
					.getAttribute("currentPhysicalWarehouse");
			Map<String,Object> searchMap = new HashMap<String,Object>();
			// 1.获取从前端传来的数据
			String customerId = request.getParameter("customer_id");
			String created_user = request.getParameter("created_user");
			String createdTime = request.getParameter("created_time");
			String deliveryTime = request.getParameter("delivery_time");
			String barcode = request.getParameter("barcode");
			String batch_pick_id = request.getParameter("batch_pick_id");
			String order_id = request.getParameter("order_id");
			if(!WorkerUtil.isNullOrEmpty(customerId)){
				searchMap.put("customer_id", customerId);
			}
			if(!WorkerUtil.isNullOrEmpty(created_user)){
				searchMap.put("created_user", created_user);
			}
			if(!WorkerUtil.isNullOrEmpty(barcode)){
				searchMap.put("barcode", barcode);
			}
			if(!WorkerUtil.isNullOrEmpty(warehouse)){
				searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
			}
			if(!WorkerUtil.isNullOrEmpty(createdTime)){
				searchMap.put("created_time", createdTime);
			}
			if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
				searchMap.put("delivery_time", deliveryTime);
			}
			if (!WorkerUtil.isNullOrEmpty(batch_pick_id)) {
				searchMap.put("batch_pick_id", batch_pick_id);
			}
			if (!WorkerUtil.isNullOrEmpty(order_id)) {
				searchMap.put("order_id", order_id);
			}
		 model.put("recheckList", inventoryDaoSlave.selectRecheckList(searchMap));
		 model.put("type", "exportrecheck");
	     return new ModelAndView(viewExcel, model);  
	}
	
	
	/**
	 * hchen1
	 * 补货查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="bh")
	public String Bh(HttpServletRequest request , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
		
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");

		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			model.put("created_time", createdTime);
		}else{
			model.put("created_time", DateUtils.getDateString(3, "yyyy-MM-dd", ""));
		}

		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			model.put("delivery_time", deliveryTime);
		}else{
			model.put("delivery_time", DateUtils.getDateString(-1, "yyyy-MM-dd", ""));
		}
		
		model.put("warehouseCustomerList", warehouseCustomerList);
		model.put("warehouseList", warehouseList);

		// 2.返回订单
		return "historyDataQuery/replenishmentQeury";
	}
	
	
	@RequestMapping(value="/bhQuery")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object bhQuery(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 1.获取从前端传来的数据
		String customerId = request.getParameter("customer_id");
		String created_user = request.getParameter("created_user");
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");
		String barcode = request.getParameter("barcode");
		String in_location_barcode = request.getParameter("in_location_barcode");
		String out_location_barcode = request.getParameter("out_location_barcode");
		String bhType =  request.getParameter("bhType");
		String warehouse_id =request.getParameter("warehouse_id");
		if (!WorkerUtil.isNullOrEmpty(warehouse_id)) {
			searchMap.put("warehouse_id", warehouse_id);
		}
		if(!WorkerUtil.isNullOrEmpty(customerId)){
			searchMap.put("customer_id", customerId);
		}
		if(!WorkerUtil.isNullOrEmpty(created_user)){
			searchMap.put("created_user", created_user);
		}
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		if(!WorkerUtil.isNullOrEmpty(warehouse)){
			searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		}
		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			searchMap.put("created_time", createdTime);
		}
		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			searchMap.put("delivery_time", deliveryTime);
		}
		if (!WorkerUtil.isNullOrEmpty(in_location_barcode)) {
			searchMap.put("in_location_barcode", in_location_barcode);
		}
		if (!WorkerUtil.isNullOrEmpty(out_location_barcode)) {
			searchMap.put("out_location_barcode", out_location_barcode);
		}
		if (!WorkerUtil.isNullOrEmpty(bhType)) {
			searchMap.put("bhType", bhType);
		}
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(request.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(request.getParameter("currentPage")),Integer.valueOf(request.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		// 2.查询订单
		try {
			resultMap = inventoryBiz.getBhList(searchMap);
			resultMap.put("success",true);
			resultMap.put("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	/**
	 * 补货导出
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="exportbh")
	public ModelAndView exportBh(HttpServletRequest request , Map<String,Object> model){
		 ViewExcel viewExcel = new ViewExcel();    
		 Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession();
			Warehouse warehouse = (Warehouse) session
					.getAttribute("currentPhysicalWarehouse");
			Map<String,Object> searchMap = new HashMap<String,Object>();
			// 1.获取从前端传来的数据
			String customerId = request.getParameter("customer_id");
			String created_user = request.getParameter("created_user");
			String createdTime = request.getParameter("created_time");
			String deliveryTime = request.getParameter("delivery_time");
			String barcode = request.getParameter("barcode");
			String in_location_barcode = request.getParameter("in_location_barcode");
			String out_location_barcode = request.getParameter("out_location_barcode");
			String bhType =  request.getParameter("bhType");
			if(!WorkerUtil.isNullOrEmpty(customerId)){
				searchMap.put("customer_id", customerId);
			}
			if(!WorkerUtil.isNullOrEmpty(created_user)){
				searchMap.put("created_user", created_user);
			}
			if(!WorkerUtil.isNullOrEmpty(barcode)){
				searchMap.put("barcode", barcode);
			}
			if(!WorkerUtil.isNullOrEmpty(warehouse)){
				searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
			}
			if(!WorkerUtil.isNullOrEmpty(createdTime)){
				searchMap.put("created_time", createdTime);
			}
			if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
				searchMap.put("delivery_time", deliveryTime);
			}
			if (!WorkerUtil.isNullOrEmpty(in_location_barcode)) {
				searchMap.put("in_location_barcode", in_location_barcode);
			}
			if (!WorkerUtil.isNullOrEmpty(out_location_barcode)) {
				searchMap.put("out_location_barcode", out_location_barcode);
			}
			if (!WorkerUtil.isNullOrEmpty(bhType)) {
				searchMap.put("bhType", bhType);
			}
		 model.put("bhList", inventoryDaoSlave.selectBhList(searchMap));
		 model.put("type", "exportbh");
	     return new ModelAndView(viewExcel, model);  
	}
	
	
	/**
	 * hchen1
	 * 拣货查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="pick")
	public String pick(HttpServletRequest request , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");

		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			model.put("created_time", createdTime);
		}else{
			model.put("created_time", DateUtils.getDateString(3, "yyyy-MM-dd", ""));
		}

		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			model.put("delivery_time", deliveryTime);
		}else{
			model.put("delivery_time", DateUtils.getDateString(-1, "yyyy-MM-dd", ""));
		}
		
		model.put("warehouseList", warehouseList);
		model.put("warehouseCustomerList", warehouseCustomerList);
		// 2.返回订单
		return "historyDataQuery/pickDataQuery";
	}
	
	
	@RequestMapping(value="/pickQuery")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object pickQuery(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 1.获取从前端传来的数据
		String customerId = request.getParameter("customer_id");
		String created_user = request.getParameter("created_user");
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");
		String barcode = request.getParameter("barcode");
		String batch_pick_id = request.getParameter("batch_pick_id");
		String warehouse_id =request.getParameter("warehouse_id");
		if (!WorkerUtil.isNullOrEmpty(warehouse_id)) {
			searchMap.put("warehouse_id", warehouse_id);
		}
		if (!WorkerUtil.isNullOrEmpty(batch_pick_id)) {
			searchMap.put("batch_pick_id", batch_pick_id);
		}else {
			
		if(!WorkerUtil.isNullOrEmpty(customerId)){
			searchMap.put("customer_id", customerId);
		}
		if(!WorkerUtil.isNullOrEmpty(created_user)){
			searchMap.put("created_user", created_user);
		}
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		if(!WorkerUtil.isNullOrEmpty(warehouse)){
			searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		}
		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			searchMap.put("created_time", createdTime);
		}
		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			searchMap.put("delivery_time", deliveryTime);
		}
		}
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(request.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(request.getParameter("currentPage")),Integer.valueOf(request.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		// 2.查询订单
		try {
			resultMap = inventoryBiz.getPickList(searchMap);
			resultMap.put("success",true);
			resultMap.put("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}

	/**
	 * 拣货资料导出
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="exportpick")
	public ModelAndView exportPick(HttpServletRequest request , Map<String,Object> model){
		 ViewExcel viewExcel = new ViewExcel();    
		 Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession();
			Warehouse warehouse = (Warehouse) session
					.getAttribute("currentPhysicalWarehouse");
			Map<String,Object> searchMap = new HashMap<String,Object>();
			// 1.获取从前端传来的数据
			String customerId = request.getParameter("customer_id");
			String created_user = request.getParameter("created_user");
			String createdTime = request.getParameter("created_time");
			String deliveryTime = request.getParameter("delivery_time");
			String barcode = request.getParameter("barcode");
			String batch_pick_id = request.getParameter("batch_pick_id");
			if(!WorkerUtil.isNullOrEmpty(customerId)){
				searchMap.put("customer_id", customerId);
			}
			if(!WorkerUtil.isNullOrEmpty(created_user)){
				searchMap.put("created_user", created_user);
			}
			if(!WorkerUtil.isNullOrEmpty(barcode)){
				searchMap.put("barcode", barcode);
			}
			if(!WorkerUtil.isNullOrEmpty(warehouse)){
				searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
			}
			if(!WorkerUtil.isNullOrEmpty(createdTime)){
				searchMap.put("created_time", createdTime);
			}
			if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
				searchMap.put("delivery_time", deliveryTime);
			}
			if (!WorkerUtil.isNullOrEmpty(batch_pick_id)) {
				searchMap.put("batch_pick_id", batch_pick_id);
			}
		 model.put("pickList", inventoryDaoSlave.selectPickList(searchMap));
		 model.put("type", "exportpick");
	     return new ModelAndView(viewExcel, model);  
	}
	
	/**
	 * hchen1
	 * 库存调整查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="maintain")
	public String maintain(HttpServletRequest request , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");

		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			model.put("created_time", createdTime);
		}else{
			model.put("created_time", DateUtils.getDateString(3, "yyyy-MM-dd", ""));
		}

		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			model.put("delivery_time", deliveryTime);
		}else{
			model.put("delivery_time", DateUtils.getDateString(-1, "yyyy-MM-dd", ""));
		}
		
		model.put("warehouseCustomerList", warehouseCustomerList);
		model.put("warehouseList", warehouseList);
		// 2.返回订单
		return "historyDataQuery/inventoryDataQuery";
	}
	
	
	@RequestMapping(value="/maintainQuery")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object maintainQuery(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 1.获取从前端传来的数据
		String customerId = request.getParameter("customer_id");
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");
		String barcode = request.getParameter("barcode");
		String maintain_type = request.getParameter("maintain_type");
		String warehouse_id =request.getParameter("warehouse_id");
		if (!WorkerUtil.isNullOrEmpty(warehouse_id)) {
			searchMap.put("warehouse_id", warehouse_id);
		}
		if(!WorkerUtil.isNullOrEmpty(customerId)){
			searchMap.put("customer_id", customerId);
		}
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			searchMap.put("created_time", createdTime);
		}
		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			searchMap.put("delivery_time", deliveryTime);
		}
		if(!WorkerUtil.isNullOrEmpty(warehouse)){
			searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		}
		if (!WorkerUtil.isNullOrEmpty(maintain_type)) {
			searchMap.put("maintain_type", maintain_type);
		}
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(request.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(request.getParameter("currentPage")),Integer.valueOf(request.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		// 2.查询订单
		try {
			resultMap = inventoryBiz.getMaintainList(searchMap);
			resultMap.put("success",true);
			resultMap.put("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	/**
	 * 库存调整导出
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="exportmaintain")
	public ModelAndView exportMaintain(HttpServletRequest request , Map<String,Object> model){
		 ViewExcel viewExcel = new ViewExcel();    
		 Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession();
			Warehouse warehouse = (Warehouse) session
					.getAttribute("currentPhysicalWarehouse");
			Map<String,Object> searchMap = new HashMap<String,Object>();
			// 1.获取从前端传来的数据
			String customerId = request.getParameter("customer_id");
			String createdTime = request.getParameter("created_time");
			String deliveryTime = request.getParameter("delivery_time");
			String barcode = request.getParameter("barcode");
			String maintain_type = request.getParameter("maintain_type");
			if(!WorkerUtil.isNullOrEmpty(customerId)){
				searchMap.put("customer_id", customerId);
			}
			if(!WorkerUtil.isNullOrEmpty(barcode)){
				searchMap.put("barcode", barcode);
			}
			if(!WorkerUtil.isNullOrEmpty(createdTime)){
				searchMap.put("created_time", createdTime);
			}
			if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
				searchMap.put("delivery_time", deliveryTime);
			}
			if(!WorkerUtil.isNullOrEmpty(warehouse)){
				searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
			}
			if (!WorkerUtil.isNullOrEmpty(maintain_type)) {
				searchMap.put("maintain_type", maintain_type);
			}
		 model.put("maintainList", inventoryDaoSlave.selectMaintainList(searchMap));
		 model.put("type", "exportmaintain");
	     return new ModelAndView(viewExcel, model);  
	}
	
	
	/**
	 * hchen1
	 * 库存入库查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="purchase")
	public String purchase(HttpServletRequest request , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");

		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			model.put("created_time", createdTime);
		}else{
			model.put("created_time", DateUtils.getDateString(3, "yyyy-MM-dd", ""));
		}

		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			model.put("delivery_time", deliveryTime);
		}else{
			model.put("delivery_time", DateUtils.getDateString(-1, "yyyy-MM-dd", ""));
		}
		
		model.put("warehouseCustomerList", warehouseCustomerList);
		model.put("warehouseList", warehouseList);
		// 2.返回订单
		return "historyDataQuery/acceptHistoryQuery";
	}
	
	
	@RequestMapping(value="/purchaseQuery")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object purchaseQuery(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 1.获取从前端传来的数据
		String customerId = request.getParameter("customer_id");
		String created_user = request.getParameter("created_user");
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");
		String barcode = request.getParameter("barcode");
		String batch_sn = request.getParameter("batch_sn");
		String product_batch_sn = request.getParameter("product_batch_sn");
		String order_id = request.getParameter("order_id");
		String order_type = request.getParameter("order_type");
		String order_status = request.getParameter("order_status");
		String oms_order_sn = request.getParameter("oms_order_sn");
		String warehouse_id =request.getParameter("warehouse_id");
		if (!WorkerUtil.isNullOrEmpty(warehouse_id)) {
			searchMap.put("warehouse_id", warehouse_id);
		}
		if (!WorkerUtil.isNullOrEmpty(oms_order_sn)|| !WorkerUtil.isNullOrEmpty(order_id)) {
			searchMap.put("oms_order_sn", oms_order_sn);
			searchMap.put("order_id", order_id);
		}else {
			
		if(!WorkerUtil.isNullOrEmpty(customerId)){
			searchMap.put("customer_id", customerId);
		}
		if(!WorkerUtil.isNullOrEmpty(created_user)){
			searchMap.put("created_user", created_user);
		}
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		if(!WorkerUtil.isNullOrEmpty(warehouse)){
			searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		}
		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			searchMap.put("created_time", createdTime);
		}
		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			searchMap.put("delivery_time", deliveryTime);
		}
		if (!WorkerUtil.isNullOrEmpty(batch_sn)) {
			searchMap.put("batch_sn", batch_sn);
		}
		if(!WorkerUtil.isNullOrEmpty(product_batch_sn)){
			searchMap.put("product_batch_sn", product_batch_sn);
		}
		if (!WorkerUtil.isNullOrEmpty(order_type)) {
			searchMap.put("order_type", order_type);
		}
		if (!WorkerUtil.isNullOrEmpty(order_status)) {
			searchMap.put("order_status", order_status);
		}
		}
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(request.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(request.getParameter("currentPage")),Integer.valueOf(request.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		// 2.查询订单
		try {
			resultMap = inventoryBiz.getRukuList(searchMap);
			resultMap.put("success",true);
			resultMap.put("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	/**
	 * 入库资料导出
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="exportpurchase")
	public ModelAndView exportPurchase(HttpServletRequest request , Map<String,Object> model){
		 ViewExcel viewExcel = new ViewExcel();    
		 Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession();
			Warehouse warehouse = (Warehouse) session
					.getAttribute("currentPhysicalWarehouse");
			Map<String,Object> searchMap = new HashMap<String,Object>();
			// 1.获取从前端传来的数据
			String customerId = request.getParameter("customer_id");
			String created_user = request.getParameter("created_user");
			String createdTime = request.getParameter("created_time");
			String deliveryTime = request.getParameter("delivery_time");
			String barcode = request.getParameter("barcode");
			String batch_sn = request.getParameter("batch_sn");
			String order_id = request.getParameter("order_id");
			String order_type = request.getParameter("order_type");
			String product_batch_sn = request.getParameter("product_batch_sn");
			String order_status = request.getParameter("order_status");
			if(!WorkerUtil.isNullOrEmpty(customerId)){
				searchMap.put("customer_id", customerId);
			}
			if(!WorkerUtil.isNullOrEmpty(product_batch_sn)){
				searchMap.put("product_batch_sn", product_batch_sn);
			}
			if(!WorkerUtil.isNullOrEmpty(created_user)){
				searchMap.put("created_user", created_user);
			}
			if(!WorkerUtil.isNullOrEmpty(barcode)){
				searchMap.put("barcode", barcode);
			}
			if(!WorkerUtil.isNullOrEmpty(warehouse)){
				searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
			}
			if(!WorkerUtil.isNullOrEmpty(createdTime)){
				searchMap.put("created_time", createdTime);
			}
			if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
				searchMap.put("delivery_time", deliveryTime);
			}
			if (!WorkerUtil.isNullOrEmpty(batch_sn)) {
				searchMap.put("batch_sn", batch_sn);
			}
			if (!WorkerUtil.isNullOrEmpty(order_id)) {
				searchMap.put("order_id", order_id);
			}
			if (!WorkerUtil.isNullOrEmpty(order_type)) {
				searchMap.put("order_type", order_type);
			}
			if (!WorkerUtil.isNullOrEmpty(order_status)) {
				searchMap.put("order_status", order_status);
			}
		 model.put("purchaseList", inventoryDaoSlave.selectRukuList(searchMap));
		 model.put("type", "exportpurchase");
	     return new ModelAndView(viewExcel, model);  
	}

	/**
	 * hchen1
	 * 上架入库查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="grounding")
	public String grounding(HttpServletRequest request , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");

		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			model.put("created_time", createdTime);
		}else{
			model.put("created_time", DateUtils.getDateString(3, "yyyy-MM-dd", ""));
		}

		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			model.put("delivery_time", deliveryTime);
		}else{
			model.put("delivery_time", DateUtils.getDateString(-1, "yyyy-MM-dd", ""));
		}
		
		model.put("warehouseCustomerList", warehouseCustomerList);
		model.put("warehouseList", warehouseList);
		// 2.返回订单
		return "historyDataQuery/onshelfDataQuery";
	}
	
	
	@RequestMapping(value="/groundingQuery")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object groundingQuery(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 1.获取从前端传来的数据
		String customerId = request.getParameter("customer_id");
		String created_user = request.getParameter("created_user");
		String createdTime = request.getParameter("created_time");
		String deliveryTime = request.getParameter("delivery_time");
		String barcode = request.getParameter("barcode");
		String order_id = request.getParameter("order_id");
		String order_type = request.getParameter("order_type");
		String location_barcode = request.getParameter("location_barcode");
		String order_status = request.getParameter("order_status");
		String oms_order_sn = request.getParameter("oms_order_sn");
		String product_batch_sn = request.getParameter("product_batch_sn");
		String warehouse_id =request.getParameter("warehouse_id");
		if (!WorkerUtil.isNullOrEmpty(warehouse_id)) {
			searchMap.put("warehouse_id", warehouse_id);
		}
		if (!WorkerUtil.isNullOrEmpty(oms_order_sn)|| !WorkerUtil.isNullOrEmpty(order_id)) {
			searchMap.put("oms_order_sn", oms_order_sn);
			searchMap.put("order_id", order_id);
		}else {
			
		if (!WorkerUtil.isNullOrEmpty(product_batch_sn)) {
			searchMap.put("product_batch_sn", product_batch_sn);
		}
		if(!WorkerUtil.isNullOrEmpty(customerId)){
			searchMap.put("customer_id", customerId);
		}
		if(!WorkerUtil.isNullOrEmpty(created_user)){
			searchMap.put("created_user", created_user);
		}
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		if(!WorkerUtil.isNullOrEmpty(warehouse)){
			searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		}
		if(!WorkerUtil.isNullOrEmpty(createdTime)){
			searchMap.put("created_time", createdTime);
		}
		if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
			searchMap.put("delivery_time", deliveryTime);
		}
	
		if (!WorkerUtil.isNullOrEmpty(order_type)) {
			searchMap.put("order_type", order_type);
		}
		if (!WorkerUtil.isNullOrEmpty(location_barcode)) {
			searchMap.put("location_barcode", location_barcode);
		}
		if (!WorkerUtil.isNullOrEmpty(order_status)) {
			searchMap.put("order_status", order_status);
		}
	    }
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(request.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(request.getParameter("currentPage")),Integer.valueOf(request.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		// 2.查询订单
		try {
			resultMap = inventoryBiz.getGroundingList(searchMap);
			resultMap.put("success",true);
			resultMap.put("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	/**
	 * 上架资料导出
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="exportgrounding")
	public ModelAndView exportGrounding(HttpServletRequest request , Map<String,Object> model){
		 ViewExcel viewExcel = new ViewExcel();    
		 Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession();
			Warehouse warehouse = (Warehouse) session
					.getAttribute("currentPhysicalWarehouse");
			Map<String,Object> searchMap = new HashMap<String,Object>();
			List<Map<String, Object>> GroundingList = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> GroundingAllList = new ArrayList<Map<String,Object>>();
			// 1.获取从前端传来的数据
			String customerId = request.getParameter("customer_id");
			String created_user = request.getParameter("created_user");
			String createdTime = request.getParameter("created_time");
			String deliveryTime = request.getParameter("delivery_time");
			String barcode = request.getParameter("barcode");
			String order_id = request.getParameter("order_id");
			String order_type = request.getParameter("order_type");
			String location_barcode = request.getParameter("location_barcode");
			String order_status = request.getParameter("order_status");
			if(!WorkerUtil.isNullOrEmpty(customerId)){
				searchMap.put("customer_id", customerId);
			}
			if(!WorkerUtil.isNullOrEmpty(created_user)){
				searchMap.put("created_user", created_user);
			}
			if(!WorkerUtil.isNullOrEmpty(barcode)){
				searchMap.put("barcode", barcode);
			}
			if(!WorkerUtil.isNullOrEmpty(warehouse)){
				searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
			}
			if(!WorkerUtil.isNullOrEmpty(createdTime)){
				searchMap.put("created_time", createdTime);
			}
			if (!WorkerUtil.isNullOrEmpty(deliveryTime)) {
				searchMap.put("delivery_time", deliveryTime);
			}
			if (!WorkerUtil.isNullOrEmpty(order_id)) {
				searchMap.put("order_id", order_id);
			}
			if (!WorkerUtil.isNullOrEmpty(order_type)) {
				searchMap.put("order_type", order_type);
			}
			if (!WorkerUtil.isNullOrEmpty(location_barcode)) {
				searchMap.put("location_barcode", location_barcode);
			}
			if (!WorkerUtil.isNullOrEmpty(order_status)) {
				searchMap.put("order_status", order_status);
			}
			
			GroundingAllList = inventoryDaoSlave.selectGroundingPurchaseList(searchMap);

			model.put("groundingList", GroundingAllList);
			model.put("type", "exportgrounding");
		    return new ModelAndView(viewExcel, model);  
	}


	/**
	 * 出库资料查询 by ytchen 16.12.23
	 * @param request
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="stockOut")
	public String batchSnOut(HttpServletRequest request , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Warehouse> warehouse_list = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
		String minute_second = DateUtils.getDateString(3, "mm:ss", "");
		String hour = DateUtils.getDateString(3, "HH", "");
		if(!"00:00".equalsIgnoreCase(minute_second)){
			hour = String.valueOf((Integer.parseInt(hour)+1));
		}
		model.put("start_time", DateUtils.getDateString(3, "yyyy-MM-dd HH:00:00", ""));
		model.put("end_time", DateUtils.getDateString(0, "yyyy-MM-dd ", "")+hour+":00:00");
		model.put("warehouseCustomerList", warehouseCustomerList);
		model.put("warehouseList",warehouse_list);
		// 2.返回基础信息
		return "historyDataQuery/stockOutDataQuery";
	}
	
	@RequestMapping(value="/stockOutSearch")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object stockOutSearch(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 1.获取从前端传来的数据
		String customer_id = request.getParameter("customer_id");
		String warehouse_id = request.getParameter("warehouse_id");
		String oms_order_sn = request.getParameter("oms_order_sn");
		String barcode = request.getParameter("barcode");
		String batch_sn = request.getParameter("batch_sn");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		
		List<Integer> customerIdList = new ArrayList<Integer>();
		if(!WorkerUtil.isNullOrEmpty(customer_id) && !"".equalsIgnoreCase(customer_id) && !"0".equalsIgnoreCase(customer_id)){
			customerIdList.add(Integer.parseInt(customer_id));
		}else{
			for (WarehouseCustomer customer : warehouseCustomerList) {
				customerIdList.add(customer.getCustomer_id());
			}
		}
		searchMap.put("customerIdList", customerIdList);
		
		if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
			searchMap.put("warehouse_id", warehouse_id);
		}
		
		if(!WorkerUtil.isNullOrEmpty(oms_order_sn)){
			searchMap.put("oms_order_sn", oms_order_sn);
		}
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}
		searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		
		if(!WorkerUtil.isNullOrEmpty(batch_sn)){
			searchMap.put("batch_sn", batch_sn);
		}
		if (!WorkerUtil.isNullOrEmpty(request.getParameter("order_id"))) {
			searchMap.put("order_id", Integer.parseInt(String.valueOf(request.getParameter("order_id"))));
		}
		if (!WorkerUtil.isNullOrEmpty(start_time)) {
			searchMap.put("start_time", start_time);
		}
		if (!WorkerUtil.isNullOrEmpty(end_time)) {
			searchMap.put("end_time", end_time);
		}
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(request.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(request.getParameter("currentPage")),Integer.valueOf(request.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		// 2.查询订单
		try {
			resultMap = inventoryBiz.getStockOutList(searchMap);
			resultMap.put("success",true);
			resultMap.put("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	@RequestMapping(value="stockOutExport")
	public ModelAndView stockOutExport(HttpServletRequest request, Map<String, Object> model) {
		ViewExcel viewExcel = new ViewExcel();

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		Map<String, Object> searchMap = new HashMap<String, Object>();
		// 1.获取从前端传来的数据
		String customer_id = request.getParameter("customer_id");
		String warehouse_id = request.getParameter("warehouse_id");
		String oms_order_sn = request.getParameter("oms_order_sn");
		String barcode = request.getParameter("barcode");
		String batch_sn = request.getParameter("batch_sn");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");

		List<Integer> customerIdList = new ArrayList<Integer>();
		if (!WorkerUtil.isNullOrEmpty(customer_id) && !"".equalsIgnoreCase(customer_id)
				&& !"0".equalsIgnoreCase(customer_id)) {
			customerIdList.add(Integer.parseInt(customer_id));
		} else {
			for (WarehouseCustomer customer : warehouseCustomerList) {
				customerIdList.add(customer.getCustomer_id());
			}
		}
		searchMap.put("customerIdList", customerIdList);
		
		if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
			searchMap.put("warehouse_id", warehouse_id);
		}

		if (!WorkerUtil.isNullOrEmpty(oms_order_sn)) {
			searchMap.put("oms_order_sn", oms_order_sn);
		}
		if (!WorkerUtil.isNullOrEmpty(barcode)) {
			searchMap.put("barcode", barcode);
		}
		searchMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());

		if (!WorkerUtil.isNullOrEmpty(batch_sn)) {
			searchMap.put("batch_sn", batch_sn);
		}
		if (!WorkerUtil.isNullOrEmpty(request.getParameter("order_id"))) {
			searchMap.put("order_id", Integer.parseInt(String.valueOf(request.getParameter("order_id"))));
		}
		if (!WorkerUtil.isNullOrEmpty(start_time)) {
			searchMap.put("start_time", start_time);
		}
		if (!WorkerUtil.isNullOrEmpty(end_time)) {
			searchMap.put("end_time", end_time);
		}
		List<Map<String, Object>> stockOutList = inventoryBiz.getStockOutListForExport(searchMap);
		if (stockOutList.size() > 50000) {
			model.put("message", "导出数据超过50000行，请先选择更具体的查询条件");
		} else if(stockOutList.size()==0){
			model.put("message", "导出数据为空，请适当调整查询条件");
		}else{
			model.put("stockOutList", stockOutList);
		}
		model.put("type", "stockOutExport");
		return new ModelAndView(viewExcel, model);
	}



}