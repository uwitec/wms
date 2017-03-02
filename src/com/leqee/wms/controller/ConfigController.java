package com.leqee.wms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.api.biz.impl.InventoryItemApiBizImpl;
import com.leqee.wms.dao.ConfigMailDao;
import com.leqee.wms.entity.ConfigMail;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;

@Controller
@RequestMapping(value="/config")
public class ConfigController {

	@Autowired
	InventoryItemApiBizImpl inventoryItemApiBizImpl;
	@Autowired
	ConfigMailDao configMailDao;
	
	    // 商品基本信息页面
		@RequestMapping(value="/show")
		public String show( HttpServletRequest req , Map<String,Object> model ){
			Subject subject = SecurityUtils.getSubject();
	        Session session = subject.getSession();
	        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
	        
	        List<ConfigMail> configmails=configMailDao.getAllConfigMailTypeAndNames();
	        model.put("configmails", configmails);
	        model.put("customers", customers);
			return "/config/config";
		}
		
		 // 商品基本信息页面
		@RequestMapping(value="/setMail")
		@ResponseBody
		public Object showMail( HttpServletRequest req , Map<String,Object> model ){
			
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			
			Subject subject = SecurityUtils.getSubject();
	        Session session = subject.getSession();
	        Warehouse warehouse = (Warehouse) session
					.getAttribute("currentPhysicalWarehouse");
			int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
	        String mail=req.getParameter("mail");
	        int customer_id=req.getParameter("customer_id")==null?0:Integer.parseInt(req.getParameter("customer_id").toString());
	        String type=req.getParameter("type");
	        int physical=req.getParameter("physical")==null?1:Integer.parseInt(req.getParameter("physical").toString());//如果是1 则为全仓库
	        if(physical==1){
	        	physical_warehouse_id=0;
	        }
	        
	        Map m=configMailDao.getCountConfigs(physical_warehouse_id,customer_id,type);
	       
	        if(m.get("count").toString().equals("1")){
	        	configMailDao.update(mail,physical_warehouse_id,customer_id,type);
	        	resultMap.put("message", "更新成功");
	        }else{
	        	configMailDao.insert(mail,physical_warehouse_id,customer_id,type);
	        	resultMap.put("message", "添加成功");
	        }
			return resultMap;
		}
		
		// 商品基本信息页面
		@RequestMapping(value="/test")
		public String test( HttpServletRequest req , Map<String,Object> model ){
			Subject subject = SecurityUtils.getSubject();
	        Session session = subject.getSession();
	        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
	        
	        inventoryItemApiBizImpl.getNeedFreezeProductLocation2(1, 9);
			return "/config/config";
		}
}
