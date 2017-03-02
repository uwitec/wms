package com.leqee.wms.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.api.util.Tools;
import com.leqee.wms.biz.ScheduleJobBiz;
import com.leqee.wms.biz.SysUserBiz;
import com.leqee.wms.biz.WarehouseBiz;
import com.leqee.wms.biz.impl.BatchPickBizImpl;
import com.leqee.wms.dao.BatchPickDao;
import com.leqee.wms.dao.BatchPickTaskDao;
import com.leqee.wms.dao.ConfigCwMappingDao;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.ConfigPrintDispatchBillDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.RegionDao;
import com.leqee.wms.dao.ScheduleJobDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.SysUserDao;
import com.leqee.wms.dao.UserActionBatchPickDao;
import com.leqee.wms.dao.UserActionWarehouseLoadDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseLoadDao;
import com.leqee.wms.entity.BatchPickParam;
import com.leqee.wms.entity.ConfigCwMapping;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Region;
import com.leqee.wms.entity.ScheduleJob;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.Shop;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.util.WorkerUtil;

@Controller
@RequestMapping(value = "/batchPick")
// 指定根路径
public class BatchPickController {

	private Logger logger = Logger.getLogger(BatchPickController.class);

	//private Cache<String, List<String>> shopNameCache;
	private Cache<String, List<Shop>> shopNameFileCache;
	private Cache<String, String> isPhysicalOutSourceCache;
	@Autowired
	SysUserDao sysUserDao;

	@Autowired
	RegionDao regionDao;

	@Autowired
	ShippingDao shippingDao;

	@Autowired
	WarehouseLoadDao warehouseLoadDao;
	@Autowired
	ConfigCwMappingDao configCwMappingDao;
	@Autowired
	OrderInfoDao orderInfoDao;

	@Autowired
	OrderProcessDao orderProcessDao;
	@Autowired
	ConfigPrintDispatchBillDao configPrintDispatchBillDao;

	@Autowired
	UserActionWarehouseLoadDao userActionWarehouseLoadDao;

	@Autowired
	UserActionBatchPickDao userActionBatchPickDao;

	@Autowired
	BatchPickDao batchPickDao;

	@Autowired
	BatchPickTaskDao batchPickTaskDao;
	
	@Autowired
	private ScheduleJobDao scheduleJobDao;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	@Autowired
	ConfigDao configDao;
	@Autowired
	WarehouseBiz warehouseBiz;

	@Autowired
	SysUserBiz sysUserBiz;

	@Autowired
	BatchPickBizImpl batchPickBizImpl;
	@Autowired
	private ScheduleJobBiz scheduleJobBiz;

	@RequestMapping(value = "/show")
	public String showCheck(HttpServletRequest req,
			HashMap<String, Object> model) {

		logger.info("BatchPickController /batchPick/showCheck");

		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
		HashMap<String, Object> customerShops = new HashMap<String, Object>();
		for (WarehouseCustomer customer : customers) {
			int customer_id = customer.getCustomer_id();

			// List<WarehouseCustomer> customers = new
			// ArrayList<WarehouseCustomer>();
			// customers = sysUserBiz.findCustomers(username);

			String time = DateUtils
					.getDateString(30, "yyyy-MM-dd", " 00:00:00");

			Map<String, Object> parmMap = new HashMap<String, Object>();

			parmMap.put("customer_id", customer_id);
			parmMap.put("time", time);

		}

		// 返回逻辑仓库
		List<Warehouse> warehouseList = new ArrayList<Warehouse>();
		warehouseList = (List<Warehouse>) session
				.getAttribute("userLogicWarehouses");

		// 返回地址组
		List<Region> regionList = regionDao.selectAllProvince();

		// 返回快递组
		// List<Shipping> shippingList = shippingDao.selectAllShipping();

		// shopNameList = orderInfoDao.selectAllShopName(customer_id);
		// 返回物理仓库
		Warehouse warehouse = (Warehouse) session
						.getAttribute("currentPhysicalWarehouse");
		String job_group = "batchpick_"+warehouse.getPhysical_warehouse_id();
		
		List<ScheduleJob> scheduleJobList=getBatchPickPhysicalJobByJobGroup(job_group,customers);
		
		
		model.put("scheduleJobList", scheduleJobList);
		model.put("warehouseList", warehouseList);
		model.put("customers", customers);
		model.put("regionList", regionList);
		// model.put("shippingList", shippingList);
	//	model.put("customerShops", customerShops);
	//	model.put("start", DateUtils.getDateString(14, "yyyy-MM-dd", ""));
	//	model.put("end", DateUtils.getDateString(0, "yyyy-MM-dd", ""));
		return "/batchPick/showBatchPick";
	}
	
	private List<ScheduleJob> getBatchPickPhysicalJobByJobGroup(String job_group, List<WarehouseCustomer> customers) {
		List<ScheduleJob> scheduleJobList=scheduleJobDao.getBatchPickPhysicalJobByJobGroup(job_group);
		customers=warehouseCustomerDao.selectAll();
		for(ScheduleJob sjob:scheduleJobList){
			if(sjob.getJobStatus().equals("0")){
				sjob.setJobStatus("停止");
			}else{
				sjob.setJobStatus("运行");
			}
			
			BatchPickParam bpp=new BatchPickParam();
			String[] param= sjob.getParamNameValue().replace("param=", "").split(":");

			
			bpp.setSize(param[6]);
			bpp.setMinSize(param[0]);
			bpp.setMaxSize(param[1]);
			bpp.setMinWeight(param[2]);
			bpp.setMaxWeight(param[3]);
			bpp.setLevel(param[4]);
			bpp.setTime(param[7]);
			bpp.setCreateTime(DateUtils.getStringTime(sjob.getCreateTime()));
			
			if(sjob.getJobName().split("_").length<3){
				sjob.setJobName("全仓库");
			}else{
				bpp.setRunTimeStart(param[9]);
				bpp.setRunTimeEnd(param[10]);
				for(WarehouseCustomer whc:customers){
					if(whc.getCustomer_id()-Integer.parseInt(sjob.getJobName().split("_")[2])==0){
						sjob.setJobName(whc.getName());
						break;
					}
				}
			}
			


			sjob.setBatchPickParam(bpp);
		}
		return scheduleJobList;
	}

//	/**
//	 * 进入手动波次页面
//	 * @param req
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping(value = "/stopAndStartBatchPick")
//	public String stopAndStartBatchPick(HttpServletRequest req,
//			HashMap<String, Object> model) {
//
//		logger.info("BatchPickController /batchPick/showCheck");
//
//		// 返回店铺名称
//		Subject subject = SecurityUtils.getSubject();
//		Session session = subject.getSession();
//		
//		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
//		customers = (List<WarehouseCustomer>) session
//				.getAttribute("userCustomers");
//		HashMap<String, Object> customerShops = new HashMap<String, Object>();
//		for (WarehouseCustomer customer : customers) {
//			int customer_id = customer.getCustomer_id();
//
//			// List<WarehouseCustomer> customers = new
//			// ArrayList<WarehouseCustomer>();
//			// customers = sysUserBiz.findCustomers(username);
//
//			String time = DateUtils
//					.getDateString(30, "yyyy-MM-dd", " 00:00:00");
//
//			Map<String, Object> parmMap = new HashMap<String, Object>();
//
//			parmMap.put("customer_id", customer_id);
//			parmMap.put("time", time);
//
//			// com.leqee.wms.shiro.SpringCacheManagerWrapper cacheManager=new
//			// com.leqee.wms.shiro.SpringCacheManagerWrapper();
//			RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
//					.getSecurityManager();
//			CacheManager cacheManager = securityManager.getCacheManager();
//			shopNameCache = cacheManager.getCache("shopNameCache");
//			List<String> shopNameList = shopNameCache.get(customer_id + "");
//
//			if (shopNameList == null || shopNameList.isEmpty()
//					|| shopNameList.size() == 0) {
//				shopNameList = orderInfoDao.selectShopNameNullCache(parmMap);
//				shopNameCache.put(customer_id + "", shopNameList);
//			}
//			customerShops.put(customer_id + "", shopNameList);
//		}
//
//		// 返回物理仓库
//		Warehouse warehouse = (Warehouse) session
//				.getAttribute("currentPhysicalWarehouse");
//		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
//				+ "";
//		
//		// 返回逻辑仓库
//		List<Warehouse> warehouseList = new ArrayList<Warehouse>();
//		warehouseList = (List<Warehouse>) session
//				.getAttribute("userLogicWarehouses");
//
//		// 返回地址组
//		List<Region> regionList = regionDao.selectAllProvince();
//
//		// 返回快递组
//		List<Shipping> shippingList = shippingDao.selectAllShipping();
//
//		String batchpickjob="batchpick_"+physical_warehouse_id;
//		
//		String  status =  scheduleJobDao.getStatusByPhysicalWarehouseId(batchpickjob);
//		
//		if(WorkerUtil.isNullOrEmpty(status)){
//			status="0";
//		}
//		//返回此仓库今日批件单数目
//		
//		int bpnum=0;
//		bpnum=batchPickBizImpl.getbatchPickNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
//		model.put("bpnum", bpnum);
//		
//		//返回此仓库今天的订单数目
//		int opnum=0;
//		opnum=batchPickBizImpl.getOrderNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
//		model.put("opnum", opnum);
//		
//		int snum=0;
//        snum=batchPickBizImpl.getOrdersInPhysicalWareHouseTodayForBp(warehouse.getPhysical_warehouse_id());
//        model.put("snum", snum);
//		
//		//shopNameList = orderInfoDao.selectAllShopName(customer_id);
//
//		model.put("warehouseList", warehouseList);
//		model.put("customers", customers);
//		model.put("regionList", regionList);
//		model.put("shippingList", shippingList);
//		model.put("customerShops", customerShops);
//		model.put("status", status);
//	//	model.put("start", DateUtils.getDateString(14, "yyyy-MM-dd", ""));
//	//	model.put("end", DateUtils.getDateString(0, "yyyy-MM-dd", ""));
//		return "/batchPick/startAndStopBatchPick";
//	}

	
//	@PostConstruct
//	public void init() throws Exception {
//
//		List<WarehouseCustomer> list2=new ArrayList<WarehouseCustomer>();
//		list2=warehouseCustomerDao.selectAllInit();
//		
//		
//		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
//				.getSecurityManager();
//		CacheManager cacheManager = securityManager.getCacheManager();
//		shopNameFileCache = cacheManager.getCache("shopNameFileCache");
//		
//		for(WarehouseCustomer customer:list2){
//			
//			List<Shop> shopList = shopNameFileCache.get(customer.getCustomer_id() + "");
//
//			int customer_id = customer.getCustomer_id();
//
//
//			String time = DateUtils
//					.getDateString(30, "yyyy-MM-dd", " 00:00:00");
//
//			Map<String, Object> parmMap = new HashMap<String, Object>();
//
//			parmMap.put("customer_id", customer_id);
//			parmMap.put("time", time);
//			
//			if (shopList == null || shopList.isEmpty()
//					|| shopList.size() == 0) {
//				shopList = orderInfoDao.selectShopNameNullCacheV2(parmMap);
//				
//				for(Shop shop:shopList){
//					if(null==shop.getFile_name()||"".equalsIgnoreCase(shop.getFile_name())){
//						shop.setFile_name(customer.getDispatch_bill_file_name());
//					}
//				}
//				
//				shopNameFileCache.put(customer.getCustomer_id() + "", shopList);
//			}
//		}
//		
//		
//	}
	
	/**
	 * 进入手动波次页面
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/stopAndStartBatchPick")
	public String stopAndStartBatchPick(HttpServletRequest req,
			HashMap<String, Object> model) {

		logger.info("BatchPickController /batchPick/showCheck");

		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
		HashMap<String, Object> customerShops = new HashMap<String, Object>();
		
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		shopNameFileCache = cacheManager.getCache("shopNameFileCache");
		
		for (WarehouseCustomer customer : customers) {
			
			int customer_id = customer.getCustomer_id();

			String time = DateUtils
					.getDateString(30, "yyyy-MM-dd", " 00:00:00");

			Map<String, Object> parmMap = new HashMap<String, Object>();

			parmMap.put("customer_id", customer_id);
			parmMap.put("time", time);

			
			
			List<Shop> shopList = shopNameFileCache.get(customer_id + "");

			if (shopList == null || shopList.isEmpty()
					|| shopList.size() == 0) {
				shopList = orderInfoDao.selectShopNameNullCacheV3(parmMap);
				
				for(Shop shop:shopList){
					if(null==shop.getFile_name()||"".equalsIgnoreCase(shop.getFile_name())){
						shop.setFile_name(customer.getDispatch_bill_file_name());
					}
				}
				
				shopNameFileCache.put(customer_id + "", shopList);
			}
			customerShops.put(customer_id + "", shopList);
		}

		// 返回物理仓库
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
				+ "";
		
		// 返回逻辑仓库
		List<Warehouse> warehouseList = new ArrayList<Warehouse>();
		warehouseList = (List<Warehouse>) session
				.getAttribute("userLogicWarehouses");

		
		List<ConfigCwMapping> list=  configCwMappingDao.getConfigCwMappingListByPhysicalWarehouseId(physical_warehouse_id);
		
		// 返回地址组
		List<Region> regionList = regionDao.selectAllProvince();

		// 返回快递组
		List<Shipping> shippingList = shippingDao.selectAllShipping();

		String batchpickjob="batchpick_"+physical_warehouse_id;
		
		String  status =  scheduleJobDao.getStatusByPhysicalWarehouseId(batchpickjob);
		
		if(WorkerUtil.isNullOrEmpty(status)){
			status="0";
		}
		//返回此仓库今日批件单数目
		
		int bpnum=0;
		bpnum=batchPickBizImpl.getbatchPickNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
		model.put("bpnum", bpnum);
		
		//返回此仓库今天的订单数目
		int opnum=0;
		opnum=batchPickBizImpl.getOrderNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
		model.put("opnum", opnum);
		
		int snum=0;
        snum=batchPickBizImpl.getOrdersInPhysicalWareHouseTodayForBp(warehouse.getPhysical_warehouse_id());
        model.put("snum", snum);
		
		//shopNameList = orderInfoDao.selectAllShopName(customer_id);

        isPhysicalOutSourceCache = cacheManager.getCache("isPhysicalOutSourceCache");
		
		String isOutSource=isPhysicalOutSourceCache.get(physical_warehouse_id+"");
		
		if(null==isOutSource){
			isOutSource = configDao.getConfigValueByFrezen(warehouse.getPhysical_warehouse_id(), 0, "IS_OUTSOURCE_PHYSICAL");
			isPhysicalOutSourceCache.put(physical_warehouse_id, isOutSource);
		}
        
		model.put("isOutSource", isOutSource);
		
        model.put("start", DateUtils
				.getDateString(7, "yyyy-MM-dd", ""));
        
        model.put("list", list);
		model.put("warehouseList", warehouseList);
		model.put("customers", customers);
		model.put("regionList", regionList);
		model.put("shippingList", shippingList);
		model.put("customerShops", customerShops);
		model.put("status", status);
		return "/batchPick/startAndStopBatchPick";
	}
	
	/***
	 * 添加通用波次任务
	 * @author dlyao
	 * @param 
	 */
	@RequestMapping(value = "/addJob")
	@ResponseBody
	public Object addJob(HttpServletRequest req,
			HashMap<String, Object> model) {
		logger.info("BatchPickController /batchPick/addJob");
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
				+ "";
        
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		isPhysicalOutSourceCache = cacheManager.getCache("isPhysicalOutSourceCache");
		
		String isOutSource=isPhysicalOutSourceCache.get(physical_warehouse_id+"");
		
		if(null==isOutSource){
			isOutSource = configDao.getConfigValueByFrezen(warehouse.getPhysical_warehouse_id(), 0, "IS_OUTSOURCE_PHYSICAL");
			isPhysicalOutSourceCache.put(physical_warehouse_id, isOutSource);
		}
		
		
		int size = Integer.parseInt(req.getParameter("size"));
		int minSize = Integer.parseInt(req.getParameter("size"));
		int maxSize = Integer.parseInt(req.getParameter("maxSize"));
		int minWeight = Integer.parseInt(req.getParameter("minWeight"));
		int maxWeight = Integer.parseInt(req.getParameter("maxWeight"));
		int time = Integer.parseInt(req.getParameter("time"));
		int level = Integer.parseInt(req.getParameter("level"));
		
		if("1".equalsIgnoreCase(isOutSource)){
			level=2;
		}
		
		java.util.Date now = new java.util.Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		int minute=c.get(Calendar.MINUTE);
		
		while(minute>=time){
			minute-=time;
		}
		ScheduleJob scheduleJobOld=scheduleJobDao.getBatchPickPhysicalJob("batchpick_"+physical_warehouse_id);
		if(!com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(scheduleJobOld)){
			try {
				scheduleJobBiz.deleteJob(scheduleJobOld);
				//scheduleJobBiz.changeStatus(scheduleJobOld.getJobId(), "stop");
				scheduleJobDao.deleteByPrimaryKey(scheduleJobOld.getJobId());
			} catch (SchedulerException e) {
				logger.debug("delete Old Schedule job batchpick_"+physical_warehouse_id+ e.getMessage());
			}
		}
		int second=c.get(Calendar.SECOND);
		String cron ="";
		if(time>5){
			cron = ""+second + " "+ minute+"/" + time + " * * * ?";
		}
		else{
			cron = ""+second + " "+ minute+" */"+time+ " * * ?";
		}
		ScheduleJob scheduleJob = new ScheduleJob();
		scheduleJob.setJobName("batchpick_"+physical_warehouse_id);
		scheduleJob.setJobGroup("batchpick_"+physical_warehouse_id);
		scheduleJob.setIsConcurrent("1");  //0同步  1异步
		scheduleJob.setJobStatus("0");
		scheduleJob.setDescription("batchpick_"+physical_warehouse_id);
		scheduleJob.setSpringId("batchPickJob");
		scheduleJob.setBeanClass("com.leqee.wms.job.BatchPickJob");
		scheduleJob.setCronExpression(cron);
		scheduleJob.setMethodName("createBatchPick");
		scheduleJob.setCreateTime(new Date());
		scheduleJob.setIs_shield(1);
		scheduleJob.setParamNameValue(new StringBuffer("param=")
				.append(minSize).append(":").append(maxSize)
				.append(":").append(minWeight).append(":")
				.append(maxWeight).append(":").append(level)
				.append(":")
				.append(physical_warehouse_id).append(":")
				.append(size).append(":")
				.append(time).toString());
		
		scheduleJobBiz.addTask(scheduleJob);
		try {
			//System.out.println(scheduleJob.getJobId()+"");
			scheduleJobBiz.changeStatus(scheduleJob.getJobId(), "start");
		} catch (SchedulerException e) {
			resultMap.put("message", "任务启动失败");
			return resultMap;
		}
		resultMap.put("message", "设置成功");

        String job_group = "batchpick_"+warehouse.getPhysical_warehouse_id();
        List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
        List<ScheduleJob> scheduleJobList=getBatchPickPhysicalJobByJobGroup(job_group,customers);
        resultMap.put("scheduleJobList", scheduleJobList);
		return resultMap;
	}
	
	
	
	/***
	 * 添加通用波次任务
	 * @author dlyao
	 * @param 
	 */
	@RequestMapping(value = "/addSingleJob")
	@ResponseBody
	public Object addSingleJob(HttpServletRequest req,
			HashMap<String, Object> model) {
		logger.info("BatchPickController /batchPick/addSingleJob");
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
				+ "";

		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		isPhysicalOutSourceCache = cacheManager.getCache("isPhysicalOutSourceCache");
		
		String isOutSource=isPhysicalOutSourceCache.get(physical_warehouse_id+"");
		
		if(null==isOutSource){
			isOutSource = configDao.getConfigValueByFrezen(warehouse.getPhysical_warehouse_id(), 0, "IS_OUTSOURCE_PHYSICAL");
			isPhysicalOutSourceCache.put(physical_warehouse_id, isOutSource);
		}
		
		int size = Integer.parseInt(req.getParameter("size"));
		int minSize = Integer.parseInt(req.getParameter("size"));
		int maxSize = Integer.parseInt(req.getParameter("maxSize"));
		int minWeight = Integer.parseInt(req.getParameter("minWeight"));
		int maxWeight = Integer.parseInt(req.getParameter("maxWeight"));
		int time = Integer.parseInt(req.getParameter("time"));
		int level = Integer.parseInt(req.getParameter("level"));
		

		if("1".equalsIgnoreCase(isOutSource)){
			level=2;
		}
		
		int start = Integer.parseInt(req.getParameter("start"));
		int end = Integer.parseInt(req.getParameter("end"));
		int customer_id = Integer.parseInt(req.getParameter("customer_id"));
		java.util.Date now = new java.util.Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		int date=c.get(Calendar.DATE);
		int month=c.get(Calendar.MONTH)+1;
		int year=c.get(Calendar.YEAR);
		int minute=c.get(Calendar.MINUTE);
		
		while(minute>=time){
			minute-=time;
		}
		
		ScheduleJob scheduleJobOld=scheduleJobDao.getBatchPickPhysicalJob("batchpick_"+physical_warehouse_id+"_"+customer_id);
		if(!com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(scheduleJobOld)){
			try {
				scheduleJobBiz.deleteJob(scheduleJobOld);
				
				//scheduleJobBiz.changeStatus(scheduleJobOld.getJobId(), "stop");
				scheduleJobDao.deleteByPrimaryKey(scheduleJobOld.getJobId());
			} catch (SchedulerException e) {
				logger.debug("delete Old Schedule job batchpick_"+physical_warehouse_id+ e.getMessage());
			}
		}
		
		int second=c.get(Calendar.SECOND);String cron ="";
		if(time>5){
			cron = ""+second + " "+ minute+"/" + time + " * * * ?";
		}
		else{
			cron = ""+second + " "+ minute+" */"+time+ " * * ?";
		}
		//String cron = ""+second + " "+ minute+"/" + time + " " + start+"-"+end+ " "+date+" "+ month+" ?";//+  year;
		ScheduleJob scheduleJob = new ScheduleJob();
		scheduleJob.setJobName("batchpick_"+physical_warehouse_id+"_"+customer_id);
		scheduleJob.setJobGroup("batchpick_"+physical_warehouse_id);
		scheduleJob.setIsConcurrent("1");  //0同步  1异步
		scheduleJob.setJobStatus("0");
		scheduleJob.setDescription(""+customer_id);
		scheduleJob.setSpringId("batchPickJob");
		scheduleJob.setBeanClass("com.leqee.wms.job.BatchPickJob");
		scheduleJob.setCronExpression(cron);
		scheduleJob.setMethodName("createSingleBatchPick");
		scheduleJob.setCreateTime(new Date());
		scheduleJob.setIs_shield(1);
		scheduleJob.setParamNameValue(new StringBuffer("param=")
				.append(minSize).append(":").append(maxSize)
				.append(":").append(minWeight).append(":")
				.append(maxWeight).append(":").append(level)
				.append(":")
				.append(physical_warehouse_id).append(":")
				.append(size).append(":")
				.append(time).append(":")
				.append(customer_id).append(":")
				.append(start).append(":")
				.append(end).toString());
		
		scheduleJobBiz.addTask(scheduleJob);
		try {
			//System.out.println(scheduleJob.getJobId()+"");
			scheduleJobBiz.changeStatus(scheduleJob.getJobId(), "start");
		} catch (SchedulerException e) {
			resultMap.put("message", "任务启动失败");
			return resultMap;
		}
		resultMap.put("message", "设置成功");
		//System.out.println("456456");
        String job_group = "batchpick_"+warehouse.getPhysical_warehouse_id();
        List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
        List<ScheduleJob> scheduleJobList=getBatchPickPhysicalJobByJobGroup(job_group,customers);
        resultMap.put("scheduleJobList", scheduleJobList);
		return resultMap;
	}


	/***
	 * 添加通用波次任务
	 * @author dlyao
	 * @param 
	 */
	@RequestMapping(value = "/changeBatchPickStatus")
	@ResponseBody
	public Object changeBatchPickStatus(HttpServletRequest req,
			HashMap<String, Object> model) {
		logger.info("BatchPickController /batchPick/changeBatchPickStatus");
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
				+ "";

		String status0 = req.getParameter("status");
		String status;
		if(status0.equals("0")){
			status="stop";
		}else{
			status="start";
		}
		List<ScheduleJob> scheduleJobList=scheduleJobDao.getBatchPickPhysicalJobByJobGroup("batchpick_"+physical_warehouse_id);
		if(!com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(scheduleJobList)){
		try {
			    for(ScheduleJob  sjob:scheduleJobList){
			    	scheduleJobBiz.changeStatus(sjob.getJobId(), status);
			    }
				
			} catch (SchedulerException e) {
				logger.debug("stop Schedule job batchpick_"+physical_warehouse_id+ " error; "+ e.getMessage());
				resultMap.put("message", "任务启动失败");
			}
		}
		
		int bpnum=0;
		bpnum=batchPickBizImpl.getbatchPickNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
		resultMap.put("bpnum", bpnum);
		
		//返回此仓库今天的订单数目
		int opnum=0;
		opnum=batchPickBizImpl.getOrderNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
		resultMap.put("opnum", opnum);
		
		int snum=0;
        snum=batchPickBizImpl.getOrdersInPhysicalWareHouseTodayForBp(warehouse.getPhysical_warehouse_id());
		resultMap.put("snum", snum);
		resultMap.put("message", "设置成功");
		resultMap.put("status", status0);
		return resultMap;
	}
	
	
	/***
	 * 添加通用波次任务
	 * @author dlyao
	 * @param 
	 */
	@RequestMapping(value = "/deleteJob")
	@ResponseBody
	public Object deleteJob(HttpServletRequest req,
			HashMap<String, Object> model) {
		logger.info("BatchPickController /batchPick/deleteJob");
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		long job_id = Long.parseLong(req.getParameter("job_id"));
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
				+ "";
	    String job_group = "batchpick_"+warehouse.getPhysical_warehouse_id();
        List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
		try {
			ScheduleJob scheduleJob=scheduleJobBiz.getTaskById(job_id);
			//scheduleJobBiz.changeStatus(job_id, "stop");
			scheduleJobBiz.deleteJob(scheduleJob);
		} catch (SchedulerException e) {
			logger.debug("delete Schedule job error,jonId= "+job_id+"   eMessage:      " + e.getMessage());
			resultMap.put("message", "删除失败");
			resultMap.put("scheduleJobList", null);
			return resultMap;
		}
		scheduleJobDao.deleteByPrimaryKey(job_id);
		List<ScheduleJob> scheduleJobList=getBatchPickPhysicalJobByJobGroup(job_group,customers);
        resultMap.put("scheduleJobList", scheduleJobList);
        resultMap.put("message", "删除成功");
		return resultMap;
	}



	/***
	 * 查询散单
	 * @author dlyao
	 * @param 
	 */
	@RequestMapping(value = "/query")
	@ResponseBody
	public Object query(HttpServletRequest req,
			HashMap<String, Object> model) {
		logger.info("BatchPickController /batchPick/changeBatchPickStatus");
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
				+ "";

//		String status0 = req.getParameter("status");
//		String status;
//		if(status0.equals("0")){
//			status="stop";
//		}else{
//			status="start";
//		}
		int customer_id=Integer.parseInt(req.getParameter("customer_id"));
		String shop_name=req.getParameter("shop_name");
		String region_id = req.getParameter("region_id");
		String shipping_id = req.getParameter("shipping_id");
		int level= Integer.parseInt(req.getParameter("level"));
		String sort=req.getParameter("sort");
		String sort2=req.getParameter("sort2");
		// 筛选条件3_2：逻辑仓库
		String warehouse_id = req.getParameter("warehouse_id");
		if (warehouse_id.equals("all")) {
			warehouse_id = "";
		}
		
		if (shipping_id.equals("可选择")) {
			shipping_id = "";
		}
		int mark= Integer.parseInt(req.getParameter("mark"));
		Map<String, Object> parmMap = new HashMap<String, Object>();
		parmMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		parmMap.put("warehouse_id", warehouse_id);
		parmMap.put("customer_id", customer_id);
		parmMap.put("shop_name", shop_name);
		parmMap.put("region_id", region_id);
		parmMap.put("shipping_id", shipping_id);
		parmMap.put("level", level);
		parmMap.put("sort", sort);
		parmMap.put("sort2", sort2);
		parmMap.put("mark", mark);
		// 2.分页信息设置
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,12);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		
		parmMap.put("page", page);
		
		List<OrderInfo> orderInfoList = batchPickBizImpl.getSingleOrderByShopByPage(parmMap);
		
		int bpnum=0;
		bpnum=batchPickBizImpl.getbatchPickNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
		resultMap.put("bpnum", bpnum);
		
		//返回此仓库今天的订单数目
		int opnum=0;
		opnum=batchPickBizImpl.getOrderNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
		resultMap.put("opnum", opnum);
		
		int snum=0;
        snum=batchPickBizImpl.getOrdersInPhysicalWareHouseTodayForBp(warehouse.getPhysical_warehouse_id());
		resultMap.put("snum", snum);
		
		// 返回快递组
		List<Shipping> shippingList = shippingDao.selectAllShipping();
		
		Map<Integer,Shipping> shippingMap=new HashMap<Integer,Shipping>();

		for(Shipping s:shippingList){
			shippingMap.put(s.getShipping_id(), s);
		}
		
		
		resultMap.put("message", "查询成功");
		resultMap.put("shippingList", shippingList);
		resultMap.put("shippingMap", shippingMap);
		resultMap.put("page", page);
		resultMap.put("orderInfoList", orderInfoList);
		return resultMap;
	}
	
	/***
	 * 查询散单
	 * @author dlyao
	 * @param 
	 */
	@RequestMapping(value = "/queryV2")
	@ResponseBody
	public Object queryV2(HttpServletRequest req,
			HashMap<String, Object> model) {
		logger.info("BatchPickController /batchPick/changeBatchPickStatus");
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
				+ "";

		int customer_id=Integer.parseInt(req.getParameter("customer_id"));
		String oms_shop_id=req.getParameter("oms_shop_id");
		String region_id = req.getParameter("region_id");
		String shipping_id = req.getParameter("shipping_id");
		int mark= Integer.parseInt(req.getParameter("mark"));
		int level= Integer.parseInt(req.getParameter("level"));
		int pick= Integer.parseInt(req.getParameter("pick"));
		int warehouse_id= Integer.parseInt(req.getParameter("warehouse_id"));
		String start=req.getParameter("start");
		String end=req.getParameter("end");
		
		Map<String, Object> parmMap = new HashMap<String, Object>();
		parmMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		parmMap.put("customer_id", customer_id);
		parmMap.put("warehouse_id", warehouse_id);
		parmMap.put("oms_shop_id", oms_shop_id);
		parmMap.put("region_id", region_id);
		parmMap.put("shipping_id", shipping_id);
		parmMap.put("level", level);
		parmMap.put("mark", mark);
		parmMap.put("start", start);
		parmMap.put("end", end);
		
		// 2.分页信息设置
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,12);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		
		parmMap.put("page", page);
		
		List<OrderInfo> orderInfoList = batchPickBizImpl.getSingleOrderByShopByPageV2(parmMap,pick);
		
		int bpnum=0;
		bpnum=batchPickBizImpl.getbatchPickNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
		resultMap.put("bpnum", bpnum);
		
		//返回此仓库今天的订单数目
		int opnum=0;
		opnum=batchPickBizImpl.getOrderNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
		resultMap.put("opnum", opnum);
		
		int snum=0;
        snum=batchPickBizImpl.getOrdersInPhysicalWareHouseTodayForBp(warehouse.getPhysical_warehouse_id());
		resultMap.put("snum", snum);
		
		// 返回快递组
		List<Shipping> shippingList = shippingDao.selectAllShipping();
		
		Map<Integer,Shipping> shippingMap=new HashMap<Integer,Shipping>();

		for(Shipping s:shippingList){
			shippingMap.put(s.getShipping_id(), s);
		}
		
		
		resultMap.put("message", "查询成功");
		resultMap.put("shippingList", shippingList);
		resultMap.put("shippingMap", shippingMap);
		resultMap.put("page", page);
		resultMap.put("orderInfoList", orderInfoList);
		return resultMap;
	}
	
	
	
	
	/***
	 * 查询散单
	 * @author dlyao
	 * @param 
	 */
	@RequestMapping(value = "/batchforquery")
	@ResponseBody
	public Object batchforquery(HttpServletRequest req,
			HashMap<String, Object> model) {
		logger.info("BatchPickController /batchPick/changeBatchPickStatus");
		
		String order_ids=req.getParameter("order_ids");
		List<String> order_id_list=Tools.changeStringToList(order_ids, 1);
		
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
				+ "";
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		String userName=sysUser.getUsername();
		int warehouse_id= Integer.parseInt(req.getParameter("warehouse_id"));
		//散单创建批件单
		batchPickBizImpl.createOddBatchPick(order_id_list,warehouse.getPhysical_warehouse_id(),userName,warehouse_id);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		int customer_id=Integer.parseInt(req.getParameter("customer_id"));
		String oms_shop_id=req.getParameter("oms_shop_id");
		String region_id = req.getParameter("region_id");
		String shipping_id = req.getParameter("shipping_id");
		
		int mark= Integer.parseInt(req.getParameter("mark"));
		int level= Integer.parseInt(req.getParameter("level"));
		int pick= Integer.parseInt(req.getParameter("pick"));
		String start=req.getParameter("start");
		String end=req.getParameter("end");
		
		
		Map<String, Object> parmMap = new HashMap<String, Object>();
		
		parmMap.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		parmMap.put("customer_id", customer_id);
		parmMap.put("oms_shop_id", oms_shop_id);
		parmMap.put("region_id", region_id);
		parmMap.put("warehouse_id", warehouse_id);
		parmMap.put("shipping_id", shipping_id);
		parmMap.put("level", level);
		parmMap.put("mark", mark);
		parmMap.put("start", start);
		parmMap.put("end", end);
		
		// 2.分页信息设置
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,12);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		
		parmMap.put("page", page);
		
		List<OrderInfo> orderInfoList = batchPickBizImpl.getSingleOrderByShopByPageV2(parmMap,pick);
		
		int bpnum=0;
		bpnum=batchPickBizImpl.getbatchPickNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
		resultMap.put("bpnum", bpnum);
		
		//返回此仓库今天的订单数目
		int opnum=0;
		opnum=batchPickBizImpl.getOrderNumInPhySicalWareHouseToday(warehouse.getPhysical_warehouse_id());
		resultMap.put("opnum", opnum);

		int snum=0;
        snum=batchPickBizImpl.getOrdersInPhysicalWareHouseTodayForBp(warehouse.getPhysical_warehouse_id());
		resultMap.put("snum", snum);
		
		// 返回快递组
		List<Shipping> shippingList = shippingDao.selectAllShipping();
		Map<Integer,Shipping> shippingMap=new HashMap<Integer,Shipping>();

		for(Shipping s:shippingList){
			shippingMap.put(s.getShipping_id(), s);
		}
		
		resultMap.put("message", "成功生成波次单");
		resultMap.put("shippingList", shippingList);
		resultMap.put("shippingMap", shippingMap);
		resultMap.put("page", page);
		resultMap.put("orderInfoList", orderInfoList);
		return resultMap;
	}
	

	@RequiresPermissions("delivery:assignBatchPick:*")
	@RequestMapping(value = "/assignBatchPick")
	public String accept(HttpServletRequest req, HashMap<String, Object> model) {

		logger.info("BatchPickController /batchPick/bindUser");
		// 获取传递过来的条件参数
		return "/batchPick/bindUser";
	}
	
	
	

	@RequestMapping(value = "/checkEmployeeSn")
	@ResponseBody
	public Map checkEmployeeSn(HttpServletRequest req) {

		logger.info("BatchPickController /batchPick/checkEmployeeSn");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String batch_employee_sn = (String) req
				.getParameter("batch_employee_sn");

		if (WorkerUtil.isNullOrEmpty(batch_employee_sn)) {
			resultMap.put("error", "工牌号不存在！");
			resultMap.put("success", false);

			logger.info("BatchPickController checkEmployeeSn batch_employee_sn is null");
			return resultMap;
		}

		logger.info("BatchPickController checkEmployeeSn  batch_employee_sn:"
				+ batch_employee_sn);
		SysUser sysuser = sysUserDao.selectByUsername(batch_employee_sn);

		if (WorkerUtil.isNullOrEmpty(sysuser)) {

			resultMap.put("error", "工牌号不存在！");
			resultMap.put("success", false);
			resultMap.put("error_id", 1);

			logger.info("BatchPickController checkEmployeeSn can't get the sysuser");
			return resultMap;

		}

		resultMap.put("employee_name", sysuser.getRealname());
		resultMap.put("employee_no", sysuser.getUsername());
		resultMap.put("success", true);

		logger.info("BatchPickController checkEmployeeSn employee_name:"
				+ sysuser.getRealname() + " employee_no:"
				+ sysuser.getUsername());
		// 获取传递过来的条件参数

		return resultMap;
	}

	@RequestMapping(value = "/bindUser")
	@ResponseBody
	public Map bindUser(HttpServletRequest req) {

		logger.info("BatchPickController /batchPick/bindUser");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String batch_pick_sn = (String) req.getParameter("batch_pick_sn");
		String employee_no = (String) req.getParameter("employee_no");

		if (WorkerUtil.isNullOrEmpty(batch_pick_sn)) {

			resultMap.put("error", "波次单号不存在！");
			resultMap.put("success", false);

			logger.info("BatchPickController bindUser batch_pick_sn is null");
			return resultMap;
		}

		SysUser sysuser = sysUserDao.selectByUsername(employee_no);

		if (WorkerUtil.isNullOrEmpty(sysuser)) {
			resultMap.put("error", "工牌号不存在" + employee_no);
			resultMap.put("error_id", 1);
			resultMap.put("success", false);

			logger.info("BatchPickController bindUser employee_no is wrong");
			return resultMap;
		}

		resultMap = batchPickBizImpl.bindBatchPickforUser(sysuser.getId(),
				sysuser.getUsername(), batch_pick_sn);

		return resultMap;
	}

	/**
	 * 通过给定条件查出具备某种规则的批次
	 */
	@RequestMapping(value = "/search2")
	@ResponseBody
	public Object searchBatchPick2(HttpServletRequest req,
			HashMap<String, Object> model) {

		logger.info("BatchPickController /batchPick/searchBatchPick");

		// 检查个店铺发货单

		HashMap<String, Object> resMap1 = batchPickBizImpl
				.checkShopCollection(req);
		if (Integer.valueOf(resMap1.get("result").toString()) == 1) {
			resMap1.put("errorkey", "errorkey");
			resMap1.put("message", "不能同时选择不同发货单的店铺");
			return resMap1;

		}
		// 执行查询
		HashMap<String, Object> resMap = batchPickBizImpl.doSearchBatchPick2(
				req, model);
		return resMap;

	}

	/**
	 * 检查今天仓库的发货单量是否达到最大
	 */
	@RequestMapping(value = "/checkWarehouseLoad")
	@ResponseBody
	public Object checkWarehouseLoad(HttpServletRequest req,
			HashMap<String, Object> model) {

		logger.info("BatchPickController /batchPick/checkWarehouseLoad");

		HashMap<String, Object> resMap = batchPickBizImpl.checkWarehouseLoad(
				req, model);

		return resMap;
	}

	/**
	 * 建立波次任务
	 */
	@RequestMapping(value = "/createBatchPickTask")
	@ResponseBody
	public Object createBatchPickTask(HttpServletRequest req,
			HashMap<String, Object> model) {

		logger.info("BatchPickController /batchPick/createBatchPickTask");

		HashMap<String, Object> resMap = batchPickBizImpl.createBatchPickTask(
				req, model);

		return resMap;
	}

	/**
	 * 建立波次任务
	 */
	@RequestMapping(value = "/configBatchPick")
	@ResponseBody
	public Object configBatchPick(HttpServletRequest req,
			HashMap<String, Object> model) {

		logger.info("BatchPickController /batchPick/createBatchPickTask");

		HashMap<String, Object> resMap = batchPickBizImpl.configBatchPick(req,
				model);

		return resMap;
	}


}