/**
 * 任务管理
 */
package com.leqee.wms.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.dao.ExpressDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ReportDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.service.ExpressService;
import com.leqee.wms.util.LockUtil;
import com.leqee.wms.util.WorkerUtil;

@Controller
@RequestMapping(value = "/search")
public class SearchController {

	private Cache<String, List<Map>> monitorCache;
	private Logger logger = Logger.getLogger(SearchController.class);
	
	@Autowired
	ShippingDao shippingDao;
	@Autowired 
	OrderProcessDao orderProcessDao;
	@Autowired
	ExpressService expressService;
	
	private ReportDao reportDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setReportDaoSlave(SqlSession sqlSession) {
		this.reportDaoSlave = sqlSession.getMapper(ReportDao.class);
	}
	
	private ExpressDao expressDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setExpressDaoSlave(SqlSession sqlSession) {
	  this.expressDaoSlave = sqlSession.getMapper(ExpressDao.class);
	}
	
	//1. 包裹状态查询
	@RequestMapping(value="/shipmentStatusSearch") 
	public String shipmentStatusSearch( HttpServletRequest req , Map<String,Object> model ){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		List<Map<String,Object>> shippingList = shippingDao.searchAllElecShipping(physical_warehouse_id);

		Map<String,Object> searchMap = new HashMap<String,Object>();
		String customer_id = req.getParameter("customer_id");
		String shipping_code = req.getParameter("shipping_code");
		String start_time = req.getParameter("start");
		String end_time = req.getParameter("end");
		String status = req.getParameter("status");
		String order_id = req.getParameter("order_id");
		String pallet_no = req.getParameter("pallet_no");
		String tracking_number = req.getParameter("tracking_number");
		
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customer_id", customer_id);
			model.put("customer_id", customer_id);
		}
		searchMap.put("physical_warehouse_id", physical_warehouse_id);
		if(!WorkerUtil.isNullOrEmpty(shipping_code)){
			searchMap.put("shipping_code", shipping_code);
			model.put("shipping_code", shipping_code);
		}
				
		if(!WorkerUtil.isNullOrEmpty(start_time)){
			searchMap.put("startTime", start_time);
			model.put("start", start_time);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf.format(new Date(System.currentTimeMillis()));
			searchMap.put("startTime", startDate);
			model.put("start", startDate );
		}
				
		if(!WorkerUtil.isNullOrEmpty(end_time)){
			searchMap.put("endTime", end_time);
			model.put("end", end_time);
		}
				
		if(!WorkerUtil.isNullOrEmpty(status)){
			searchMap.put("status", status);
			model.put("status", status);
		}
		if(!WorkerUtil.isNullOrEmpty(order_id)){
			searchMap.put("order_id", order_id);
			model.put("order_id", order_id);
		}
		if(!WorkerUtil.isNullOrEmpty(tracking_number)){
			searchMap.put("tracking_number", tracking_number);
			model.put("tracking_number", tracking_number);
		}
		if(!WorkerUtil.isNullOrEmpty(pallet_no)){
			searchMap.put("pallet_no", pallet_no);
			model.put("pallet_no", pallet_no);
		}
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		
		List<Map> shipmentStatusList = orderProcessDao.getshipmentStatusListByPage(searchMap);
		Integer shipmentNum = orderProcessDao.getShipmentStatusNum(searchMap);
		
		model.put("page", page);
		model.put("shipmentNum", shipmentNum);
		model.put("shipmentStatusList", shipmentStatusList);
		model.put("shippingList", shippingList);
		model.put("customerList", warehouseCustomerList);
		return "/search/shipmentStatusSearch";    
	}

	//2. 任务追踪查询
	@RequestMapping(value="/orderTaskToDoSearch") 
	public String orderTaskToDoSearch(HttpServletRequest req , Map<String,Object> model ){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();

		Map<String,Object> searchMap = new HashMap<String,Object>();
		String start_time = req.getParameter("start");
		String end_time = req.getParameter("end");

		searchMap.put("physical_warehouse_id", physical_warehouse_id);
		String monitorCacheName = ""+physical_warehouse_id+"_";
		if(!WorkerUtil.isNullOrEmpty(start_time)){
			searchMap.put("startTime", start_time);
			model.put("start", start_time);
			monitorCacheName += start_time+"_";
		}else{
			String startDate = DateUtils.getDateString(7, "yyyy-MM-dd", "");
			searchMap.put("startTime", startDate);
			model.put("start", startDate );
			monitorCacheName += startDate+"_";
		}
		String todayDate = DateUtils.getDateString(0,"yyyy-MM-dd", "");
		searchMap.put("todayDate", todayDate);		
		if(!WorkerUtil.isNullOrEmpty(end_time)){
			searchMap.put("endTime", end_time+" 23:59:59");
			model.put("end", end_time);
			monitorCacheName += end_time;
		}else{
			monitorCacheName += "0";
		}

		Map<String,Object> total = new HashMap<String,Object>();
		ReentrantLock lock = LockUtil.getReentrantLock("monitorCache_" + monitorCacheName);
		if(lock.tryLock()){
			RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
					.getSecurityManager();
			CacheManager cacheManager = securityManager.getCacheManager();
			monitorCache = cacheManager.getCache("monitorCache");
			List<Map> orderToDoList = monitorCache.get(monitorCacheName);
			if (orderToDoList == null || orderToDoList.isEmpty() || orderToDoList.size() == 0) {
				orderToDoList = reportDaoSlave.getOrderToDoList(searchMap);
				monitorCache.put(monitorCacheName, orderToDoList);
			}
			
			Integer ALL_FULFILLED = 0;
			Integer ACCPET = 0;
			Integer BATCH_PICK = 0;
			Integer PICKING = 0;
			Integer RECHECKED = 0;
			Integer WEIGHED = 0;
			Integer FULFILLED = 0;
			Integer CANCEL = 0;
			Integer TODO = 0;
			Integer ALL_ORDER = 0;
			
			for (Map orderToDo : orderToDoList) {
				ALL_ORDER = ALL_ORDER +Integer.parseInt(String.valueOf(orderToDo.get("ALL_ORDER")));
				ALL_FULFILLED = ALL_FULFILLED+Integer.parseInt(String.valueOf(orderToDo.get("ALL_FULFILLED")));
				FULFILLED = FULFILLED+Integer.parseInt(String.valueOf(orderToDo.get("FULFILLED")));
				CANCEL = CANCEL+Integer.parseInt(String.valueOf(orderToDo.get("CANCEL")));
				TODO = TODO+Integer.parseInt(String.valueOf(orderToDo.get("TODO")));
				ACCPET = ACCPET+Integer.parseInt(String.valueOf(orderToDo.get("ACCPET")));
				BATCH_PICK = BATCH_PICK+Integer.parseInt(String.valueOf(orderToDo.get("BATCH_PICK")));
				PICKING = PICKING+Integer.parseInt(String.valueOf(orderToDo.get("PICKING")));
				RECHECKED = RECHECKED+Integer.parseInt(String.valueOf(orderToDo.get("RECHECKED")));
				WEIGHED = WEIGHED+Integer.parseInt(String.valueOf(orderToDo.get("WEIGHED")));
			}
			total.put("ALL_ORDER",ALL_ORDER );
			total.put("ALL_FULFILLED",ALL_FULFILLED );
			total.put("FULFILLED", FULFILLED);
			total.put("CANCEL", CANCEL);
			total.put("TODO", TODO);
			total.put("ACCPET",ACCPET );
			total.put("BATCH_PICK", BATCH_PICK);
			total.put("PICKING",PICKING );
			total.put("RECHECKED", RECHECKED);
			total.put("WEIGHED", WEIGHED);
			
			model.put("totalMap", total);
			model.put("orderToDoList", orderToDoList); 
			
			List<Map<String,Object>> replenishmentList = reportDaoSlave.getReplenishmentToDoList(searchMap);
			model.put("replenishmentList", replenishmentList); 
			lock.unlock();
		}else{
			model.put("totalMap", total);
		}
		
		return "/search/orderTaskToDoSearch";    
	}

	//3. 热敏资源管理
	@RequestMapping(value="/thermalRepository") 
	public String thermalRepository(HttpServletRequest req , Map<String,Object> model ){
		List<Map<String, Object>> thermalRepositoryList = expressService.getThermalRepositoryList();
		model.put("thermalRepositoryList", thermalRepositoryList);  //号段可用量
		
		List<Map<String,Object>> thermalWarehouseShippingList = expressDaoSlave.getThermalWarehouseShippingList();
		model.put("thermalWarehouseShippingList", thermalWarehouseShippingList);//仓库使用热敏快递方式列表
		return "/search/thermalRepository";    
	}
}
