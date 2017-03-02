package com.leqee.wms.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.api.util.WorkerUtil;
import com.leqee.wms.biz.HardwareBiz;
import com.leqee.wms.biz.SysResourceBiz;
import com.leqee.wms.biz.SysUserBiz;
import com.leqee.wms.biz.WarehouseBiz;
import com.leqee.wms.dao.InventorySummaryDao;
import com.leqee.wms.dao.OrderReserveDetailDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.InventorySummary;
import com.leqee.wms.entity.OrderReserveDetail;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.exception.LackWarehousePermissionException;
import com.leqee.wms.shiro.MyFormAuthenticationFilter;

@Controller
@RequestMapping(value="/inventorySummerQuery")
public class inventorySummerQueryController {
	Logger logger = Logger.getLogger(inventorySummerQueryController.class);
	@Autowired 
	InventorySummaryDao inventorySummaryDao;
	@Autowired 
	WarehouseDao warehouseDao;
	@Autowired
	OrderReserveDetailDao orderReserveDetailDao;
	private final static Integer pageSize = 20 ;

	@RequestMapping(value = "/query")
	public String inventorySummerQuery(HttpServletRequest req,Integer product_id,Integer warehouse_id,String status,Integer customer_id,Integer pageNo) {
		String order_id = req.getParameter("order_id");
		pageNo =(pageNo==null? 1:pageNo);
		Integer lastPageNo = pageNo*pageSize;
		Integer initPageNo = (pageNo-1)*pageSize ;
		Map<String, Object> searchMap = new  HashMap<String, Object>();
		searchMap.put("product_id", product_id);
		searchMap.put("warehouse_id", warehouse_id);
		searchMap.put("status", status);
		searchMap.put("customer_id", customer_id);
		searchMap.put("lastPageNo", 20);
		searchMap.put("initPageNo", initPageNo);
		
		// 返回物理仓库列表
		List<Warehouse> warehouseList = warehouseDao.selectAllWarehouseList();
		req.setAttribute("warehouseList", warehouseList);
		//Warehouse warehouse = warehouseDao.selectByWarehouseName(warehouse_name);
		List<InventorySummary> inventorySummaryList = inventorySummaryDao.selectInventorySummaryByMap(searchMap);
		if(!WorkerUtil.isNullOrEmpty(inventorySummaryList)){
			req.setAttribute("inventorySummaryList", inventorySummaryList);
		}
		Map<String, Object> orderReserveMap = new  HashMap<String, Object>();
		orderReserveMap.put("product_id", product_id);
		orderReserveMap.put("warehouse_id", warehouse_id);
		orderReserveMap.put("order_id", order_id);
		orderReserveMap.put("lastPageNo", 20);
		orderReserveMap.put("initPageNo", initPageNo);
		List<OrderReserveDetail> orderReserveDetailList = orderReserveDetailDao.selectOrderReserveDetail(orderReserveMap);
		if(!WorkerUtil.isNullOrEmpty(orderReserveDetailList)){
			req.setAttribute("orderReserveDetailList", orderReserveDetailList);
		}
		req.setAttribute("pageNo", pageNo);
		return "/inventorySummerQuery/query";
	}

	

}
