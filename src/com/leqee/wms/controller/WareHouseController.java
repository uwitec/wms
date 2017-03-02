package com.leqee.wms.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.biz.WarehouseBiz;
import com.leqee.wms.biz.impl.BatchPickBizImpl;
import com.leqee.wms.dao.BatchPickTaskDao;
import com.leqee.wms.dao.UserActionWarehouseLoadDao;
import com.leqee.wms.dao.WarehouseLoadDao;
import com.leqee.wms.entity.UserActionWarehouseLoad;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseLoad;

@Controller
@RequestMapping(value = "warehouse")
public class WareHouseController {

	private Logger logger = Logger.getLogger(WareHouseController.class);
	@Autowired
	WarehouseBiz warehouseBiz;
	@Autowired
	BatchPickBizImpl batchPickBizImpl;
	@Autowired
	UserActionWarehouseLoadDao userActionWarehouseLoadDao;
	
	@Autowired
	BatchPickTaskDao batchPickTaskDao;
	@Autowired
	WarehouseLoadDao warehouseLoadDao;

	/**
	 * @author dlyao
	 * @CreatedDate 2016.02.22 设置仓库负荷
	 * */
	@RequestMapping(value = "/warehouseLoad")
	public String wareHouseLoad(HttpServletRequest req,
			Map<String, Object> model) {

		logger.info("WareHouseController /warehouse/warehouseLoad");

		java.util.Date now = new java.util.Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期

		String time = dateFormat.format(c.getTime()) + " 00:00:00";

		Map<String, Object> parmMap = new HashMap<String, Object>();

		
		parmMap.put("time", time);
		
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		
		Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		
		int ordersInTask = 0;
		ordersInTask=batchPickBizImpl.getOrderNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
		
		int loads=0;
		List<WarehouseLoad> warehouseLoadList = warehouseLoadDao.findWarehouseLoadList();
		for(WarehouseLoad wl:warehouseLoadList)
		{
			if(wl.getPhysical_warehouse_id()-warehouse.getPhysical_warehouse_id()==0)
			{
				loads=wl.getLoads();
			}
		}
		model.put("loads", loads);
		model.put("nums", ordersInTask);
		
		//return "/warehouse/setwarehouseShow";
		return "/warehouse/setwarehouse";
	}

	/**
	 * @author dlyao
	 * @CreatedDate 2016.02.22 设置仓库负荷
	 * */
	@RequestMapping(value = "/setWarehouseLoad",method = RequestMethod.POST)
	@ResponseBody
	public Object setWareHouseLoad(HttpServletRequest req,
			Map<String, Object> model) {

		logger.info("WareHouseController /warehouse/setWarehouseLoad");
		
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
				
		Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		int loads = Integer.parseInt(req.getParameter("loads"));

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("physical_warehouse_id", physical_warehouse_id);
		map.put("loads", loads);
		map.put("time", DateUtils.getDateString(0, "yyyy-MM-dd", " 00:00:00"));

		warehouseLoadDao.insertUserActionWarehouseLoad(map);

		String actionUser = (String) SecurityUtils.getSubject().getPrincipal();

		// 插入订单操作日志记录
		UserActionWarehouseLoad userActionWarehouseLoad = new UserActionWarehouseLoad();

		userActionWarehouseLoad.setPhysical_warehouse_id(physical_warehouse_id);
		userActionWarehouseLoad.setAction_type("CHANGED");
		userActionWarehouseLoad.setAction_note("CHANGED TO " + loads);
		userActionWarehouseLoad.setCreate_user(actionUser);
		userActionWarehouseLoadDao
				.insertUserActionWarehouseLoadRecord(userActionWarehouseLoad);

		
		HashMap<String, Object> resMap = new HashMap<String, Object>();
		List<WarehouseLoad> warehouseLoadList = warehouseLoadDao.findWarehouseLoadList(); 
		
		for(WarehouseLoad warehouseLoad:warehouseLoadList)
		{
			int ordersInTask = 0;
			map.put("physical_warehouse_id", warehouseLoad.getPhysical_warehouse_id());
				ordersInTask = batchPickBizImpl.getOrderNumInPhySicalWareHouseToday(warehouseLoad.getPhysical_warehouse_id());

			warehouseLoad.setNums(ordersInTask);
		}
		
		resMap.put("message", "设置成功");
		resMap.put("warehouseLoadList", warehouseLoadList);
		return resMap;
	}

}
