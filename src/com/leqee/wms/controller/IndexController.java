package com.leqee.wms.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.biz.SysResourceBiz;
import com.leqee.wms.biz.SysUserBiz;
import com.leqee.wms.biz.WarehouseCustomerBiz;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.ReportDao;
import com.leqee.wms.dao.WarehouseLoadDao;
import com.leqee.wms.entity.SysResource;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.util.WorkerUtil;

@Controller
public class IndexController {
	Logger logger = Logger.getLogger(IndexController.class);
	private Cache<String, List<Map>> customerShopCache;
	private Cache<String, List<Map>> dashboardCache;
	
	@Autowired
	SysUserBiz sysUserBiz;
	@Autowired
	SysResourceBiz sysResourceBiz;
	@Autowired
	WarehouseCustomerBiz warehouseCustomerBiz;
	
	@Autowired
	WarehouseLoadDao warehouseLoadDao;
	
	private ReportDao reportDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setReportDaoSlave(SqlSession sqlSession) {
		this.reportDaoSlave = sqlSession.getMapper(ReportDao.class);
	}
	
	private OrderInfoDao orderInfoDao;
	@Resource(name = "sqlSessionSlave")
	public void setOrderInfoDao(SqlSession sqlSession) {
		this.orderInfoDao = sqlSession.getMapper(OrderInfoDao.class);
	}
	
	@RequestMapping("/")
    public String index(Model model) {
		Subject subject = SecurityUtils.getSubject();
		String username = (String)subject.getPrincipal();
		
		// 菜单列表
		List<SysResource> menus = new ArrayList<SysResource>();
		Set<String> permissions = sysUserBiz.findPermissions(username);
	    menus = sysResourceBiz.findMenus(permissions);
        model.addAttribute("menus", menus);
        
        return "index";
    }

	
	@RequestMapping(value="/welcome_ajax")
	@ResponseBody
    public List<Map> welcome_ajax(HttpServletRequest req) {
    	Map<String,Object> searchMap = new HashMap<String,Object>();
		String start_time = req.getParameter("start");
		String customer_id = req.getParameter("customer_id");
		String oms_shop_id = req.getParameter("oms_shop_id");
		String end_time = req.getParameter("end");
		
    	Subject subject = SecurityUtils.getSubject();
    	Session session = subject.getSession();
    	Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		List<WarehouseCustomer> customerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();

		Integer physicalWarehoudeId = warehouse.getPhysical_warehouse_id();
		searchMap.put("physicalWarehoudeId", physicalWarehoudeId);
		if(!WorkerUtil.isNullOrEmpty(start_time) && !"".equalsIgnoreCase(start_time)){
			searchMap.put("startTime", start_time);
		}else{
			start_time = DateUtils.getDateString(7, "yyyy-MM-dd", "");
			searchMap.put("startTime", start_time);
		}
		if(!WorkerUtil.isNullOrEmpty(end_time) && !"".equalsIgnoreCase(end_time)){
			searchMap.put("endTime", end_time );
		}else{
			end_time = "0";
		}
		List<Integer> customerIdList = new ArrayList<Integer>();
		if(!WorkerUtil.isNullOrEmpty(customer_id) && !"".equalsIgnoreCase(customer_id) && !"0".equalsIgnoreCase(customer_id)){
			customerIdList.add(Integer.parseInt(customer_id));
			if(!WorkerUtil.isNullOrEmpty(oms_shop_id)  && !"".equalsIgnoreCase(oms_shop_id) && !"0".equalsIgnoreCase(oms_shop_id)){
				searchMap.put("omsShopId", oms_shop_id);
			}else{
				oms_shop_id = "0";
			}
		}else{
			for (WarehouseCustomer customer : customerList) {
				customerIdList.add(customer.getCustomer_id());
			}
			oms_shop_id = "0";
		}
		searchMap.put("customerIdList", customerIdList);
		Collections.sort(customerIdList);
		CacheManager cacheManager = securityManager.getCacheManager();
		dashboardCache = cacheManager.getCache("dashboardCache");
		List<Map> orderStatusList = dashboardCache.get(customerIdList+"_"+oms_shop_id+"_"+physicalWarehoudeId+"_"+start_time+"_"+end_time);
		if (orderStatusList == null || orderStatusList.isEmpty() || orderStatusList.size() == 0) {
//			//System.out.println("read data");
			Map<String, Object> parmMap = new HashMap<String, Object>();
			parmMap.put("time", DateUtils.getDateString(0, 0, 0, 0, 10, 0));

			orderStatusList = reportDaoSlave.getOrderListByPayTime(searchMap);
			dashboardCache.put(customerIdList+"_"+oms_shop_id+"_"+physicalWarehoudeId+"_"+start_time+"_"+end_time, orderStatusList);
//		}else{
//			//System.out.println("read cache");
		}
        return orderStatusList;
    }
	
	
    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String welcome_get(HttpServletRequest req,Map<String, Object> model) {
    	Subject subject = SecurityUtils.getSubject();
    	Session session = subject.getSession();
		List<WarehouseCustomer> customerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		model.put("customers", customerList);//用于前端下拉框显示
		

		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		customerShopCache = cacheManager.getCache("customerShopCache");

		List<List<Map<String,Object>>> shopNameLists = new ArrayList<List<Map<String,Object>>>();

		for (WarehouseCustomer customer : customerList) {
			List<Map<String,Object>> shopNameList = new ArrayList<Map<String,Object>>();
			Map<String, Object> customerMap = new HashMap<String, Object>();
			Map<String, Object> shopNameMap = new HashMap<String, Object>();
			
			List<Map> shopName = customerShopCache.get(customer.getCustomer_id() + "");

			if (shopName == null || shopName.isEmpty() || shopName.size() == 0) {
				shopName = orderInfoDao.selectCustomerShopCache(customer.getCustomer_id());
				customerShopCache.put(customer.getCustomer_id() + "", shopName);
			}
			
			customerMap.put("customerId", customer.getCustomer_id());
			shopNameMap.put("shopName", shopName);
			shopNameList.add(customerMap);
			shopNameList.add(shopNameMap);
			shopNameLists.add(shopNameList);
		}
		model.put("shopNameLists", shopNameLists);//用于前端下拉框显示(二级店铺)
		
		String startDate = DateUtils.getDateString(7, "yyyy-MM-dd", "");
		model.put("start", startDate );
		model.put("customer_id", "0");
		
        return "welcome";
    }
    
    /**
     * @author Jarvis
     * @Description 建设中页面
     * 
     * */
    @RequestMapping("/inConstruction")
    public String inConstruction() {
        return "inConstruction";
    }
    
}
