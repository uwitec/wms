package com.leqee.wms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.leqee.wms.api.util.Tools;
import com.leqee.wms.biz.CountTaskBiz;
import com.leqee.wms.biz.ReplenishmentBiz;
import com.leqee.wms.dao.ConfigCwMappingDao;
import com.leqee.wms.dao.CountTaskDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ScheduleQueueCountDao;
import com.leqee.wms.dao.SysUserDao;
import com.leqee.wms.dao.TaskImproveDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.ConfigCwMapping;
import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ScheduleQueueCount;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.TaskCount;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.LocationBarcodeTools;
import com.leqee.wms.util.LockUtil;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.ViewExcel;
import com.leqee.wms.util.WorkerUtil;


@Controller
@RequestMapping(value = "/countTask")
public class CountTaskController {

	private Logger logger = Logger.getLogger(CountTaskController.class);
	
	private Cache<String, List<ConfigCwMapping>> configCwMappingCache;
	@Autowired
	ScheduleQueueCountDao scheduleQueueCountDao;
	@Autowired
	CountTaskBiz countTaskBiz;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	
	@Autowired
	CountTaskDao countTaskDao;
	@Autowired
	SysUserDao sysUserDao;
	
	@Autowired
	TaskImproveDao taskImproveDao;
	@Autowired
	ProductLocationDao productLocationDao;
	@Autowired
	ConfigCwMappingDao configCwMappingDao;
	@Autowired
	WarehouseDao warehouseDao;
	@Autowired
	ReplenishmentBiz replenishmentBizImpl;
	/**
	 * 规则配置页面
	 */
	@RequestMapping(value="/jobIndex") 
	public String jobIndex( HttpServletRequest req , Map<String,Object> model ){
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
		
		//2、打印日志
		logger.info("/countTask  jobIndex");
		model.put("customers", customers);    
		
		return "/countTask/jobIndex";    
	}
	
	
	
	
	/**
	 * 配置工作
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/addJob") 
	@ResponseBody
	public Map<String, Object> addJob( HttpServletRequest req){
		
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		String created_user=((SysUser)session.getAttribute("currentUser")).getUsername();

		String location_type=req.getParameter("location_type");
		String from_location_barcode=req.getParameter("from_location_barcode").replaceAll("-", "");		
		String to_location_barcode=req.getParameter("to_location_barcode").replaceAll("-", "");
		
		String barcode=null==req.getParameter("barcode")?"":req.getParameter("barcode");
		int customer_id=req.getParameter("customer_id")==null?0:Integer.parseInt(req.getParameter("customer_id").toString());
		if((!"".equals(barcode))&&customer_id==0){
			resultMap.put("success", false);
			resultMap.put("message", "指定商品必需指定货主");
			return resultMap;
		}
		
		if((!LocationBarcodeTools.checkLoactionBarcode(from_location_barcode))&&(from_location_barcode.length()!=0)){
			resultMap.put("success", false);
			resultMap.put("message", "请输入正确的起始库位");
			return resultMap;
		}
		if((!LocationBarcodeTools.checkLoactionBarcode(to_location_barcode))&&(to_location_barcode.length()!=0)){
			resultMap.put("success", false);
			resultMap.put("message", "请输入正确的结束库位");
			return resultMap;
		}
		
		int hide_real_num=Integer.parseInt(req.getParameter("hide_real_num").toString());
		int hide_batch_sn=Integer.parseInt(req.getParameter("hide_batch_sn").toString());
		String task_type=req.getParameter("task_type");
		String count_sn=com.leqee.wms.util.WorkerUtil.generatorSequence(
				SequenceUtil.KEY_NAME_PDCODE, "P", true);
		
		ScheduleQueueCount sqc= new ScheduleQueueCount();
		sqc.setPhysical_warehouse_id(physical_warehouse_id);
		sqc.setCreated_user(created_user);
		sqc.setCreated_time(new Date());
		sqc.setLocation_type(location_type);
		sqc.setFrom_location_barcode(from_location_barcode);
		sqc.setTo_location_barcode(to_location_barcode);
		sqc.setTask_type(task_type);
		sqc.setCount_sn(count_sn);
		sqc.setStatus("INIT");
		sqc.setHide_batch_sn(hide_batch_sn);
		sqc.setHide_real_num(hide_real_num);
		sqc.setCustomer_id(customer_id);
		sqc.setBarcode(barcode);

		scheduleQueueCountDao.insert(sqc);
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,15);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		java.util.Date now = new java.util.Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期 
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		
		String nowString=dateFormat.format(c.getTime());
		List<ScheduleQueueCount> list=scheduleQueueCountDao.queryJobByPage(physical_warehouse_id,0,"",nowString,"",page);
		changeScheduleQueueCount(list);

		resultMap.put("list", list);
		resultMap.put("page", page);
		resultMap.put("success", true);
		return resultMap;   
	}
	
	/**
	 * 查询工作
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/queryJob") 
	@ResponseBody
	public Map<String, Object> queryJob( HttpServletRequest req){
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		String time=req.getParameter("time");
		String status=req.getParameter("status");
		int customer_id=req.getParameter("customer_id")==null?0:Integer.parseInt(req.getParameter("customer_id").toString());
		String task_type=req.getParameter("task_type");
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,15);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		
		
		List<ScheduleQueueCount> list=scheduleQueueCountDao.queryJobByPage(physical_warehouse_id,customer_id,task_type,time,status,page);
		
		changeScheduleQueueCount(list);
		
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("list", list);
		resultMap.put("page", page);
		resultMap.put("success", true);
		//2、打印日志
		logger.info("/countTask  queryJob");   
		
		return resultMap;   
	}
	
	/**
	 * 加上货主名称
	 * @author dlyao
	 * @param list
	 */
	private void changeScheduleQueueCount(List<ScheduleQueueCount> list) {
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers=warehouseCustomerDao.selectAll();
		for(ScheduleQueueCount sqc:list){
			if(sqc.getCustomer_id()==0){
				sqc.setCustomer_name("不限");
			}else{
				for(WarehouseCustomer wc:customers){
					if(wc.getCustomer_id()-sqc.getCustomer_id()==0){
						sqc.setCustomer_name(wc.getName());
						break;
					}
				}
			}
		}
		
	}

	/**
	 * 执行工作
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/startJob") 
	@ResponseBody
	public Map<String, Object> startJob( HttpServletRequest req){
		
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		int queue_id = Integer.parseInt(req.getParameter("queue_id").toString());

		ReentrantLock lock = LockUtil.getReentrantLock("pandian_queue_id_" + queue_id);

		if(lock.tryLock()){ //tryLock()方法是有返回值的，它表示用来尝试获取锁，如果获取成功，则返回true，如果获取失败（即锁已被其他线程获取），则返回false	
			try{
				Subject subject = SecurityUtils.getSubject();
				Session session = subject.getSession();

				Warehouse warehouse = (Warehouse) session
						.getAttribute("currentPhysicalWarehouse");
				int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
				
				ScheduleQueueCount sqc = scheduleQueueCountDao
						.getScheduleQueueCountByQueueId(queue_id);
				if (com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(sqc)) {
					resultMap.put("success", false);
					resultMap.put("message", "该盘点工作已经被删除");
					  
				}
				else if("CANCEL".equalsIgnoreCase(sqc.getStatus())){
					resultMap.put("success", false);
					resultMap.put("message", "该盘点工作已经取消");
				}
				else if(!"INIT".equalsIgnoreCase(sqc.getStatus())){
					resultMap.put("success", false);
					resultMap.put("message", "该盘点工作已经操作过了");
				}
				else{
					resultMap=countTaskBiz.doStartJob(sqc);
				}
				
				
				PageParameter page = null;
				if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
					page = new PageParameter(1,15);
				}else{
					page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
				}

				resultMap.put("page", page);
			
				return resultMap;    
			}			
			catch (Exception e) {
				logger.info("lock message : "+e.getMessage());
			throw new RuntimeException(e.getMessage());
		}  
		finally {
			// 释放锁
			lock.unlock();
		}	
		}	
			
		return resultMap;
	}
	
	
	/**
	 * 取消工作
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/cancelJob") 
	@ResponseBody
	public Map<String, Object> cancelJob( HttpServletRequest req){
		
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		int queue_id = Integer.parseInt(req.getParameter("queue_id").toString());

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		ScheduleQueueCount sqc = scheduleQueueCountDao
				.getScheduleQueueCountByQueueId(queue_id);
		if (com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(sqc)) {
			resultMap.put("success", false);
			resultMap.put("message", "该盘点工作已经被删除");
			return resultMap;        
		}
		resultMap=countTaskBiz.doCancelJob(sqc);
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,15);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
//		java.util.Date now = new java.util.Date();
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期 
//		Calendar c = Calendar.getInstance();
//		c.setTime(now);
//		
//		String nowString=dateFormat.format(c.getTime());
//		List<ScheduleQueueCount> list=scheduleQueueCountDao.queryJobByPage(physical_warehouse_id,0,"",nowString,"",page);
//		changeScheduleQueueCount(list);
//		resultMap.put("list",list);
		resultMap.put("page", page);
		
		return resultMap;    
	}
	
	/**
	 * 进入查询盘点任务页面
	 */
	@RequestMapping(value="/queryCountTask") 
	public String queryConutTask( HttpServletRequest req , Map<String,Object> model ){
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
		
		//2、打印日志
		logger.info("/countTask  queryConutTask");
		model.put("customers", customers);    
		
		return "/countTask/queryCountTask";    
	}
	
	/**
	 * 查询盘点任务
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/query") 
	@ResponseBody
	public Map<String, Object> query( HttpServletRequest req){
		
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		int customer_id=req.getParameter("customer_id")==null?0:Integer.parseInt(req.getParameter("customer_id").toString());
		String barcode=req.getParameter("barcode");
		String product_name=req.getParameter("product_name");
		String task_type=req.getParameter("task_type");
		
		String count_sn=req.getParameter("count_sn");
		int task_id=req.getParameter("task_id")==null?0:Integer.parseInt(req.getParameter("task_id").toString());
		String time=req.getParameter("time");
		String status=req.getParameter("status");
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,15);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		
		List<TaskCount> list = countTaskDao.getTaskCountByPage(physical_warehouse_id,customer_id,barcode,product_name,task_type,count_sn,task_id,time,status,page);
		changeTaskCount(list);


		resultMap.put("success",true);
		resultMap.put("list",list);
		resultMap.put("page", page);
		
		return resultMap;    
	}
	
	/**
	 * 1. 打印盘点任务单 ，进入页面
	 * by ytchen
	 */
	@RequestMapping(value="/stockCheck") 
	public String stockCheck( HttpServletRequest req , Map<String,Object> model ){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		model.put("physicalWarehouseId", currentPhysicalWarehouse.getPhysical_warehouse_id());    
		return "/countTask/stockCheck";     
	}

	/**
	 * 2. 输入盘点任务号，点击“确认”
	 * by ytchen
	 */
	@RequestMapping(value="/searchCountSn")
	@ResponseBody
	public Map<String, Object> searchCountSn(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String countSn = req.getParameter("countSn");
		Integer physicalWarehouseId = Integer.parseInt(req.getParameter("physicalWarehouseId"));
		if(WorkerUtil.isNullOrEmpty(countSn)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "请输入盘点任务号！");
			return resultMap;
		}else{
			try{
				resultMap = countTaskBiz.searchCountSnForPrint(countSn,physicalWarehouseId);
			}catch(Exception e){
				logger.info("搜索盘点任务发生异常，异常信息:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "搜索盘点任务错误："+ e.getMessage());
			}	
		}
		return resultMap;
	}
	
	/**
	 * 3. 点击“打印”
	 * by ytchen
	 */
	@RequestMapping(value="printBatchStockTask")
	public String printBatchStockTask(HttpServletRequest req , Map<String,Object> model){
		String countSn = req.getParameter("countSn");
		Integer taskNumPerPage = Integer.parseInt(req.getParameter("taskNumPerPage"));
		if(WorkerUtil.isNullOrEmpty(countSn) || WorkerUtil.isNullOrEmpty(taskNumPerPage)){
			return "failure";
		}
		List<Map<String,Object>> batchStockTaskList = countTaskBiz.createBatchTaskCount(countSn,taskNumPerPage); 
		model.put("batchStockTaskList", batchStockTaskList);
		return "/countTask/printStockCheckTask";
	}
	
	
	@RequestMapping(value = "/bindTaskCount")
	public String accept(HttpServletRequest req, HashMap<String, Object> model) {

		logger.info("CountTaskController /countTask/bindTaskCount");
		// 获取传递过来的条件参数
		return "/countTask/bindUser";
	}
	
	
	@RequestMapping(value = "/checkEmployeeSn")
	@ResponseBody
	public Map checkEmployeeSn(HttpServletRequest req) {

		logger.info("CountTaskController /countTask/checkEmployeeSn");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String batch_employee_sn = (String) req
				.getParameter("batch_employee_sn");

		if (WorkerUtil.isNullOrEmpty(batch_employee_sn)) {
			resultMap.put("error", "工牌号不存在！");
			resultMap.put("success", false);

			logger.info("CountTaskController checkEmployeeSn bind_employee_sn is null");
			return resultMap;
		}

		logger.info("CountTaskController checkEmployeeSn  bind_employee_sn:"
				+ batch_employee_sn);
		SysUser sysuser = sysUserDao.selectByUsername(batch_employee_sn);

		if (WorkerUtil.isNullOrEmpty(sysuser)) {

			resultMap.put("error", "工牌号不存在！");
			resultMap.put("success", false);
			resultMap.put("error_id", 1);

			logger.info("CountTaskController checkEmployeeSn can't get the sysuser");
			return resultMap;

		}

		resultMap.put("employee_name", sysuser.getRealname());
		resultMap.put("employee_no", sysuser.getUsername());
		resultMap.put("success", true);

		logger.info("CountTaskController checkEmployeeSn employee_name:"
				+ sysuser.getRealname() + " employee_no:"
				+ sysuser.getUsername());
		// 获取传递过来的条件参数

		return resultMap;
	}
	
	
	@RequestMapping(value = "/bindUser")
	@ResponseBody
	public Map bindUser(HttpServletRequest req) {

		logger.info("CountTaskController /countTask/bindUser");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String batch_task_sn = (String) req.getParameter("batch_task_sn");
		String employee_no = (String) req.getParameter("employee_no");

		if (WorkerUtil.isNullOrEmpty(batch_task_sn)) {

			resultMap.put("error", "盘点任务编号不存在！");
			resultMap.put("success", false);

			logger.info("CountTaskController bindUser batch_task_sn is null");
			return resultMap;
		}

		SysUser sysuser = sysUserDao.selectByUsername(employee_no);

		if (WorkerUtil.isNullOrEmpty(sysuser)) {
			resultMap.put("error", "工牌号不存在" + employee_no);
			resultMap.put("error_id", 1);
			resultMap.put("success", false);

			logger.info("CountTaskController bindUser employee_no is wrong");
			return resultMap;
		}
		resultMap=countTaskBiz.bindUser(sysuser.getId(),
				sysuser.getUsername(), batch_task_sn);

		return resultMap;
	}
	
	
	/**
	 * 盘点回单查询
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/queryBatchTaskCount")
	@ResponseBody
	public Map queryBatchTaskCount(HttpServletRequest req) {

		logger.info("CountTaskController /countTask/queryBatchTaskCount");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		int mark=0;
		String batch_task_sn = (String) req.getParameter("batch_task_sn");
		
		int task_id = req.getParameter("task_id").equals("")?0:Integer.parseInt(req.getParameter("task_id").toString());

		if(!batch_task_sn.equals("")){
			Pattern p=Pattern.compile("^[P][0-9]{12,16}[-][1-3][-][1-9][0-9]{0,3}$");
			if(!LocationBarcodeTools.checkString(batch_task_sn, p)){
				resultMap.put("success", false);
				resultMap.put("message", "请输入正确的盘点任务号");
				return resultMap;
			}
			mark=Integer.parseInt(batch_task_sn.split("-")[1]);
		}
		resultMap.put("mark", mark);
		List<TaskCount> list = countTaskDao.queryTaskCountByBatchCountSnTaskId(physical_warehouse_id,batch_task_sn,task_id,mark);
		
		int needImprove=countTaskDao.getNeedImprove(list.get(0).getTask_id());
		resultMap.put("needImprove", needImprove);
		if(!com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(list)){
			resultMap.put("task_type", list.get(0).getTask_type());
			resultMap.put("mark", list.get(0).getMark());
		}
		changeTaskCount(list);
		
		resultMap.put("success", true);
		resultMap.put("list", list);
		
		return resultMap;
	}
	
	
	private void changeTaskCount(List<TaskCount> list) {
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers=warehouseCustomerDao.selectAll();
		for(TaskCount tc:list){
			if(tc.getCustomer_id()==0){
				tc.setCustomer_name("不限");
			}else{
				for(WarehouseCustomer wc:customers){
					if(wc.getCustomer_id()-tc.getCustomer_id()==0){
						tc.setCustomer_name(wc.getName());
						break;
					}
				}
			}
		}
		
		
	}

	/**
	 * 盘点回单录入
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/countIn")
	@ResponseBody
	public Map countIn(HttpServletRequest req) {

		logger.info("CountTaskController /countTask/queryBatchTaskCount");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		
		String user_name=((SysUser)session.getAttribute("currentUser")).getUsername();
		
		int mark=req.getParameter("mark")==""?0:Integer.parseInt(req.getParameter("mark").toString());
		String batch_task_sn = (String) req.getParameter("batch_task_sn");
		int task_id = req.getParameter("task_id").equals("")?0:Integer.parseInt(req.getParameter("task_id").toString());
		String task_ids=req.getParameter("task_ids");
		String nums=(String) req.getParameter("nums");
		List<String> taskIdStringList=Tools.changeStringToList(task_ids, 0);
		List<Integer> taskIdList=Tools.changeStringListToIntegerList(taskIdStringList);
		List<String> numStringList=Tools.changeStringToList(nums, 0);
		List<Integer> numList=Tools.changeStringListToIntegerList(numStringList);
		
		List<TaskCount> list = countTaskDao.queryTaskCountByIdList(taskIdList);
		if(list.size()!=taskIdList.size()){
			resultMap.put("success", false);
			resultMap.put("message", "该盘点任务不在盘点中");
			return resultMap;
		}
		for(int i=0;i<list.size();i++){
			TaskCount tc=list.get(i);
			if(mark==1){
				tc.setNum_first(numList.get(i));
				if(tc.getNum_real()==tc.getNum_first()){
					tc.setStatus(TaskCount.STATUS_FULFILLED);
					tc.setNum_dif(0);
				}
				else{
					tc.setStatus(TaskCount.STATUS_OVER_FIRST);
					tc.setNum_dif(0);
				}
			}else if(mark==2){
				tc.setNum_second(numList.get(i));
				if(tc.getNum_second()==tc.getNum_first()){
					tc.setStatus(TaskCount.STATUS_FULFILLED);
					tc.setNum_dif(tc.getNum_second()-tc.getNum_real());
				}
				else{
					tc.setStatus(TaskCount.STATUS_OVER_SECOND);
				}
			}else if(mark==3){
				tc.setNum_third(numList.get(i));
				tc.setStatus(TaskCount.STATUS_FULFILLED);
				tc.setNum_dif(tc.getNum_third()-tc.getNum_real());
			}
			else{
				resultMap.put("success", false);
				resultMap.put("message", "该盘点任务不在盘点中");
				return resultMap;
			}
		}
		countTaskBiz.updateList(list,mark,user_name);

		List<TaskCount> list2 = countTaskDao.queryTaskCountByBatchCountSnTaskId(physical_warehouse_id,batch_task_sn,task_id,mark);
		changeTaskCount(list);
		
		int needImprove=countTaskDao.getNeedImprove(list.get(0).getTask_id());
		resultMap.put("needImprove", needImprove);
		resultMap.put("success", true);
		resultMap.put("list", list2);
		resultMap.put("mark", mark);
		
		return resultMap;
	}
	
	/**
	 * 确认盘点差异
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/createChangeTask")
	@ResponseBody
	public Map createChangeTask(HttpServletRequest req) {

		logger.info("CountTaskController /countTask/createChangeTask");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		
		String user_name=((SysUser)session.getAttribute("currentUser")).getUsername();

		int task_id = req.getParameter("task_id").equals("")?0:Integer.parseInt(req.getParameter("task_id").toString());
		
		int num =countTaskDao.getNotFulfilledTaskNums(task_id);
		
		if(num>0){
			resultMap.put("success", false);
			resultMap.put("message", "还有盘点单没有完成，不能调整");
			return resultMap;
		}
		
		List<TaskCount> list = countTaskDao.getFulfilledTaskByTaskId(task_id);
		
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers=warehouseCustomerDao.selectAll();

		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		
		
		for(WarehouseCustomer whc:customers){
			int customer_id=whc.getCustomer_id();
			
			configCwMappingCache = cacheManager.getCache("configCwMappingCache");
			
			List<ConfigCwMapping> configCwMappingList=configCwMappingCache.get(physical_warehouse_id+"_"+customer_id);
			
			if(null==configCwMappingList||configCwMappingList.isEmpty()){
				configCwMappingList = configCwMappingDao.getConfigCwMappingList(physical_warehouse_id, customer_id);
				configCwMappingCache.put(physical_warehouse_id+"_"+ customer_id, configCwMappingList);
			}
			if(WorkerUtil.isNullOrEmpty(configCwMappingList)){
				continue ;
			}else{
				for(ConfigCwMapping cw:configCwMappingList){
					
					
					List<TaskCount> list2 = new ArrayList<TaskCount>();
					List<Integer> productIdList = new ArrayList<Integer>();
					List<Integer> LocationIdList = new ArrayList<Integer>();
					for(TaskCount tc:list){
						if(tc.getCustomer_id()-whc.getCustomer_id()==0&&tc.getWarehouse_id()-cw.getWarehouse_id()==0){
							list2.add(tc);
							productIdList.add(tc.getProduct_id());
							LocationIdList.add(tc.getLocation_id());
						}
					}
					
					
					List<Map> empty1 = new ArrayList<Map>();
					Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
					Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
					List<Product> empty5 = new ArrayList<Product>();
					Map<Integer,List<TaskCount>> map=new HashMap<Integer,List<TaskCount>>();
					empty1.add(map);
					map.put(customer_id, list2);
					
					if(!com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(list2)){
						String result=replenishmentBizImpl.lockReplenishJobByWarehouseCustomer(0,
								user_name,"Improve",LocationIdList,physical_warehouse_id,customer_id,
								empty1,empty2,productIdList,empty4,0,empty5,0,0,cw.getWarehouse_id());
						if(result.equals("LOCKING")){
							resultMap.put("success", false);
							resultMap.put("message", "部分货主有其他任务正在进行，请稍后再试");
							return resultMap;
						}else if(result.equals("false")){
							resultMap.put("success", false);
							resultMap.put("message", "部分货主调整失败，请稍后再试");
							return resultMap;
						}
					}
					
				}
				}
			
			
		}	
		resultMap.put("success", true);
		resultMap.put("message", "调整成功");
		int needImprove=countTaskDao.getNeedImprove(task_id);
		resultMap.put("needImprove", needImprove);
		return resultMap;
	}
	
	/**
	 * 月末调整页面
	 * @author dlyao
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/queryTaskImprove")
	public String queryTaskImprove(HttpServletRequest req, HashMap<String, Object> model) {

		logger.info("CountTaskController /countTask/queryTaskImprove");
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
		
		List<Warehouse> warehouseList = warehouseDao.selectAllWarehouseList();
		
		
		model.put("customers", customers);
		// 获取传递过来的条件参数
		return "/countTask/queryTaskImprove";
	}
	
	/**
	 * 月末调整查询
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/queryProductLocationImprove")
	@ResponseBody
	public Map queryProductLocationImprove(HttpServletRequest req, HashMap<String, Object> model) {

		logger.info("CountTaskController /countTask/queryProductLocationImprove");
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		List<Warehouse> warehouseList = warehouseDao.selectLogicWarehousesByPhysicalWarehouseId(physical_warehouse_id);
		
		int customer_id=0;
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("customer_id"))){
			customer_id=Integer.parseInt(req.getParameter("customer_id").toString());
		}
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		List<ProductLocation> list=productLocationDao.selectAllByPage(physical_warehouse_id,customer_id,page);
//		List<ProductLocation> addList=productLocationDao.selectAllByPage(physical_warehouse_id,customer_id,"('VARIANCE_ADD_LOCATION')",page);
//		List<ProductLocation> minusList=productLocationDao.selectAllByPage(physical_warehouse_id,customer_id,"('VARIANCE_MINUS_LOCATION')",page);
//		Map<String,ProductLocation> addMap=new HashMap<String,ProductLocation>();
//		for(ProductLocation pl :addList){
//			addMap.put(pl.getProduct_id()+pl.getStatus(), pl);
//		}
//		for(ProductLocation pl :minusList){
//			String key = pl.getProduct_id()+pl.getStatus();
//			if(null==addMap.get(key)){
//				pl.setQty_total(0-pl.getQty_total());
//				addMap.put(key, pl);
//			}
//			else{
//				ProductLocation plnew= addMap.get(key);
//				plnew.setQty_total(plnew.getQty_total()-pl.getQty_total());
//			}
//		}
		resultMap.put("warehouseList", warehouseList);
		resultMap.put("result", true);
		resultMap.put("list", list);
		resultMap.put("page", page);
		
		// 获取传递过来的条件参数
		return resultMap;
	}
	
	
	/**
	 * 月末调整生成
	 * @param req
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:work:create")
	@RequestMapping(value = "/createProductLocationChange")
	@ResponseBody
	public Map createProductLocationChange(HttpServletRequest req, HashMap<String, Object> model) {

		logger.info("CountTaskController /countTask/createProductLocationChange");
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		String user_name=((SysUser)session.getAttribute("currentUser")).getUsername();
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		List<Warehouse> warehouseList = warehouseDao.selectLogicWarehousesByPhysicalWarehouseId(physical_warehouse_id);
		
		int customer_id=0;
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("customer_id"))){
			customer_id=Integer.parseInt(req.getParameter("customer_id").toString());
		}
		
		if(!WorkerUtil.isNullOrEmpty(warehouseList)){
            
			for(Warehouse wh:warehouseList){
				int warehouse_id=wh.getWarehouse_id().intValue();
				
				List<Integer> productIdList=new ArrayList<Integer>();
				
				List<ProductLocation> addList=new ArrayList<ProductLocation>();
				List<ProductLocation> addList2=new ArrayList<ProductLocation>();
				List<ProductLocation> minusList=new ArrayList<ProductLocation>();
				List<ProductLocation> minusList2=new ArrayList<ProductLocation>();
				

				String[] param = req.getParameter("param").toString().split(",");
				for(String s:param){
					
					ProductLocation pl=new ProductLocation();
					int wid=Integer.parseInt(s.split(":")[3]);
					if(warehouse_id-wid==0){
						pl.setProduct_id(Integer.parseInt(s.split(":")[0]));
						pl.setStatus(s.split(":")[1]);
						pl.setQty_total(Integer.parseInt(s.split(":")[2]));
						pl.setWarehouse_id(warehouse_id);
						if(pl.getQty_total()>0){
							addList.add(pl);
						}else if(pl.getQty_total()<0){
							pl.setQty_total(0-pl.getQty_total());
							minusList.add(pl);
						}
						productIdList.add(pl.getProduct_id());
					}
					
					
				}
				String task_improve_sn=WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_OTHER,"TI",true);
				
				if(!WorkerUtil.isNullOrEmpty(productIdList)){
						List<ProductLocation> listTemp=productLocationDao.selectAllVarinanceProductLocation(physical_warehouse_id,productIdList,warehouse_id);
						Map<String,Integer> mapTemp=new HashMap<String,Integer>();
					    for(ProductLocation pl:listTemp){
					    	mapTemp.put(pl.getProduct_id()+"", pl.getQty_total());
					    }
						
						for(ProductLocation pl:addList){
							//执行盘盈
							int a=pl.getQty_total();
							//需要盘盈
							int b=null==mapTemp.get(pl.getProduct_id()+"")?0:Integer.parseInt(mapTemp.get(pl.getProduct_id()+"").toString());
							//不去执行
							if(a-b>0){
								addList2.add(pl);
							}
							
						}
						addList.removeAll(addList2);
						for(ProductLocation pl:minusList){
							//执行盘亏数量
							int a=pl.getQty_total();
							//需要盘盈
							int b=null==mapTemp.get(pl.getProduct_id()+"")?0:Integer.parseInt(mapTemp.get(pl.getProduct_id()+"").toString());
							//不去执行
							if(a+b>0){
								minusList2.add(pl);
							}
							
						}
						minusList.removeAll(minusList2);
						countTaskBiz.doCreateProductLocationChange(productIdList,physical_warehouse_id,user_name,customer_id,addList,minusList,task_improve_sn,warehouse_id);
						
					}
				}							
		}
		
		
		List<ProductLocation> list=productLocationDao.selectAllByPage(physical_warehouse_id,customer_id,page);

		resultMap.put("warehouseList", warehouseList);
		resultMap.put("result", true);
		resultMap.put("list", list);
		resultMap.put("page", page);
		
		// 获取传递过来的条件参数
		return resultMap;
	}
	
	
	/**
	 * 查询task_improve记录页面
	 * @author dlyao
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/queryHistoryTaskImprove")
	public String queryHistoryTaskImprove(HttpServletRequest req, HashMap<String, Object> model) {

		logger.info("CountTaskController /countTask/queryHistoryTaskImprove");
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
		model.put("customers", customers);
		// 获取传递过来的条件参数
		return "/countTask/queryHistoryTaskImprove";
	}
	
	/**
	 * 查询task_improve记录
	 * @author dlyao
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/doQueryHistoryTaskImprove")
	@ResponseBody
	public Object doQueryHistoryTaskImprove(HttpServletRequest req) {

		logger.info("CountTaskController /countTask/doQueryHistoryTaskImprove");
		
		HashMap<String, Object> model = new HashMap<String, Object>();
		
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		int customer_id=null==req.getParameter("customer_id")?0:Integer.parseInt(req.getParameter("customer_id").toString());
		
		String start=req.getParameter("start");
		
		String end=req.getParameter("end");
		
		// 2.分页信息设置
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,50);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		
		List<Map> list = taskImproveDao.getHistoryTaskImproveByPage(physical_warehouse_id,customer_id,start,end,page);
		
		model.put("customers", customers);
		model.put("page", page);
		model.put("list", list);
		// 获取传递过来的条件参数
		return model;
	}
	
	@RequestMapping(value="/export")
	@SuppressWarnings("unchecked")
	public ModelAndView export(HttpServletRequest req , Map<String,Object> model){
		
		ViewExcel viewExcel = new ViewExcel();
		model.put("type", "exportHistoryTaskImprove");
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();		
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		int customer_id=null==req.getParameter("customer_id")?0:Integer.parseInt(req.getParameter("customer_id").toString());
		
		String start=req.getParameter("start");
		
		String end=req.getParameter("end");
		
		List<Map> list = taskImproveDao.getHistoryTaskImprove(physical_warehouse_id,customer_id,start,end);
		
		model.put("customers", customers);
		model.put("list", list);
		
		// 3.返回订单结果
		return new ModelAndView(viewExcel, model);  
	}
	
	
}
