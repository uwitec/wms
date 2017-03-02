package com.leqee.wms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.biz.BatchPickBiz;
import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.OrderPrepackBiz;
import com.leqee.wms.biz.ShipmentBiz;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderPrepackDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.ShippingYundaTrackingNumberDao;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.LockUtil;
import com.leqee.wms.util.ViewExcel;
import com.leqee.wms.util.WorkerUtil;


@Controller
@RequestMapping(value="/sale")  //指定根路径
public class SaleController  {

	private Cache<String, List<Map>> customerShopCache;
	private Cache<String, String> isDouble11Cache;
	private Logger logger = Logger.getLogger(SaleController.class);

	@Autowired
	OrderBiz orderBiz;
	@Autowired
	InventoryBiz inventoryBiz;
	@Autowired
	BatchPickBiz batchPickBiz;
	@Autowired
	ShipmentBiz shipmentBiz;
	@Autowired
	ShipmentDao shipmentDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	ShippingYundaTrackingNumberDao shippingYundaTrackingNumberDao;
	@Autowired 
	OrderPrepackBiz orderPrepackBiz;
	@Autowired 
	ProductDao productDao;
	@Autowired
	ConfigDao configDao;
	
	ShippingDao shippingDao;
	@Resource(name = "sqlSessionSlave")
	public void setShippingDao(SqlSession sqlSession) {
		this.shippingDao = sqlSession.getMapper(ShippingDao.class);
	}
	/**
	 * 销售出库页面
	 */
	@RequestMapping(value="/deliveryUI") 
	public String deliveryUI( HttpServletRequest req , Map<String,Object> model ){
		//1、获取客户端参数
		String username =  req.getParameter("username") ;
		String msg =  req.getParameter("msg") ;
		
		//2、打印日志
		logger.info(username);
		model.put("username", username );    
		model.put("msg", msg );    
		
		return "/sale/deliveryUI";    
	}
	
	/**
	 * （复核）判断订单是否为批量单
	 */
	@RequestMapping(value="/checkOrderForRecheck")
	@ResponseBody
	public Map<String, Object> checkOrderForRecheck(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String orderId = req.getParameter("order_id");
		if(WorkerUtil.isNullOrEmpty(orderId)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "没有接收到订单号信息");
			return resultMap;
		}else{
			ReentrantLock lock = LockUtil.getReentrantLock("RecheckLoadOrder_orderId_" + orderId);
			lock.lock();
			try{
				resultMap = shipmentBiz.checkOrderForRecheck(Integer.parseInt(orderId));
			}catch(NumberFormatException e){
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（复核）判断订单波次属性错误："+ "扫描订单号并非数字格式");
			}catch(Exception e){
				logger.info("（复核）判断订单波次属性错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（复核）判断订单波次属性错误："+ e.getMessage());
			}finally{
				// 释放锁
				lock.unlock();
			}	
		}
		return resultMap;
	}
	
	
	/**
	 * (复核)加载商品
	 * */
	@RequestMapping(value="/loadOrder")
	@ResponseBody
	public Map<String, Object> loadOrder(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String orderId = req.getParameter("order_id");
		if(WorkerUtil.isNullOrEmpty(orderId)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "没有接收到订单号信息");
			return resultMap;
		}else{
			ReentrantLock lock = LockUtil.getReentrantLock("RecheckLoadOrder_orderId_" + orderId);
			lock.lock();
			try{
				resultMap = shipmentBiz.loadGoodsForOrder(Integer.parseInt(orderId));
			}catch(NumberFormatException e){
				logger.info("（复核）加载商品发生异常，异常信息:"+e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（复核）加载商品错误："+ "扫描订单号并非数字格式");
			}catch(Exception e){
				logger.info("（复核）加载商品发生异常，异常信息:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（复核）加载商品错误："+ e.getMessage());
			}finally{
				// 释放锁
				lock.unlock();
			}	
		}
		return resultMap;
	}
	
	
	/**
	 * (复核)商品扫描 Ajax调用
	 * @param shipment_id
	 * @param order_goods_id
	 * @param serial_number
	 * */
	@RequestMapping(value="/goodScan")
	@ResponseBody
	public Map<String, Object> goodScan(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		Integer shipmentId = WorkerUtil.isNullOrEmpty(req.getParameter("shipment_id")) ? null : Integer.valueOf(req.getParameter("shipment_id"));
		Integer orderGoodsId = WorkerUtil.isNullOrEmpty(req.getParameter("order_goods_id")) ? null : Integer.valueOf(req.getParameter("order_goods_id"));
		String serialNumber = WorkerUtil.isNullOrEmpty(req.getParameter("serial_number")) ? null : req.getParameter("serial_number");
		String batchSn = WorkerUtil.isNullOrEmpty(req.getParameter("batch_sn"))? null: req.getParameter("batch_sn");
		Integer time = WorkerUtil.isNullOrEmpty(req.getParameter("time"))? null: Integer.valueOf(req.getParameter("time"));
		
		try {
			resultMap = orderBiz.getSaleOrderGoodsScanResult(shipmentId, orderGoodsId, serialNumber, batchSn, time);
		} catch (Exception e) {
			logger.error("（复核）商品条码/串号扫描失败!异常信息:" + e.getMessage() );
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "（复核）商品条码/串号扫描失败!异常信息:" + e.getMessage() );
		}
		
		return resultMap;
	}
	
	/**
	 * (复核)物流码扫描存储
	 * @param order_goods_id
	 * @param transfer_code
	 * */
	@RequestMapping(value="/transferCodeScan")
	@ResponseBody
	public Map<String, Object> transferCodeScan(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		Integer orderGoodsId = WorkerUtil.isNullOrEmpty(req.getParameter("order_goods_id")) ? null : Integer.valueOf(req.getParameter("order_goods_id"));
		String transferCode = WorkerUtil.isNullOrEmpty(req.getParameter("transfer_code")) ? null : req.getParameter("transfer_code");
		
		try {
			resultMap = orderBiz.saveTransferCodeForOrder(orderGoodsId, transferCode);
		} catch (Exception e) {
			logger.error("（复核）商品物流码扫描失败!异常信息:" + e.getMessage() );
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "（复核）商品物流码扫描失败!异常信息:" + e.getMessage() );
		}
		
		return resultMap;
	}
	
	/**
	 * (复核)撤销复核记录
	 * */
	@RequestMapping(value="/cleanOrderTns")
	@ResponseBody
	public Map<String, Object> cleanOrderTns(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String orderId = req.getParameter("order_id");
		if(WorkerUtil.isNullOrEmpty(orderId)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "没有接收到清空信息！");
			return resultMap;
		}else if(!WorkerUtil.isNullOrEmpty(orderId)){// 输入 订单号
			try{
				resultMap = shipmentBiz.cleanOrderTns(Integer.parseInt(orderId));
			}catch(NumberFormatException e){
				logger.info("（复核）撤销复核失败！错误:"+e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（复核）撤销复核失败！错误："+ "订单号并非数字格式");
			}catch(Exception e){
				logger.info("（复核）撤销复核失败！错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（复核）撤销复核失败！错误："+ e.getMessage());
			}	
		}
		return resultMap;
	}
	
	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.22
	 * 模拟创建波次单调度
	 * */
	@RequestMapping(value="/batchPickSchedule")
	public String batchPickSchedule( HttpServletRequest req , Map<String,Object> model ){
		Integer customerId = Integer.valueOf(65558);
		Integer warehouseId = Integer.valueOf(12306);
		String actionUser = "system";
		
		Response response = new Response();
		try {
			response = batchPickBiz.batchPick(customerId, warehouseId, actionUser);
			if(Response.OK.equals(response.getCode())){
				logger.info("SUCCESS: " + response.getMsg());
			}else{
				logger.info("FAILED: " + response.getMsg());
			}
		} catch (Exception e) {
			logger.error("创建波次单发生异常！",e);
		}
		
		return "redirect:/sale/deliveryUI";
	}
	
	/**
	 * (复核)绑定耗材
	 * */
	@RequestMapping(value="/bindConsume")
	@ResponseBody
	public Map<String, Object> bindConsume(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String shipmentId = req.getParameter("shipment_id");
		String barcode = req.getParameter("barcode");
		String orderId = req.getParameter("order_id");
		if(WorkerUtil.isNullOrEmpty(shipmentId) || WorkerUtil.isNullOrEmpty(barcode) || WorkerUtil.isNullOrEmpty(orderId)){
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "快递单号，耗材编码与订单号 未成功获取");
			return resultMap;
		}else{
			try{
				resultMap = shipmentBiz.bindConsume(Integer.parseInt(shipmentId),barcode,Integer.parseInt(orderId));
			}catch(NumberFormatException e){
				logger.error("（复核）绑定耗材发生异常，异常信息:" + e.getMessage() );
				resultMap.put("result",Response.FAILURE);      
				resultMap.put("note","绑定耗材发生异常，错误信息："+"输入信息并非数字类型，请检查！");
			}catch(Exception e){
				logger.error("（复核）绑定耗材发生异常，异常信息:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);      
				resultMap.put("note","绑定耗材发生异常，错误信息："+e.getMessage());
			}	
		}
		
		return resultMap;
	}
	
	/**
	 * (复核)追加面单
	 * */
	@RequestMapping(value="/addTrackingNumber")
	@ResponseBody
	public Map<String, Object> addTrackingNumber(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String orderId = req.getParameter("order_id");
		if(WorkerUtil.isNullOrEmpty(orderId)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "没有接收到订单号信息");
			return resultMap;
		}else{
			try{
				resultMap = shipmentBiz.addTrackingNumber(Integer.parseInt(orderId));
			}catch(NumberFormatException e){
				logger.info("（复核）追加面单异常，异常信息:"+e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "追加面单错误："+ "扫描订单号并非数字格式");
			}catch(Exception e){
				logger.error("（复核）追加面单发生异常，异常信息:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note","追加面单发生异常，请重试！"+e.getMessage());
			}	
		}
		return resultMap;
	}
	

	/**
	 * @author ytchen
	 * @CreatedDate 2016.03.14
	 * 根据订单选择打印格式链接
	 */
	@RequestMapping(value="print")
	public String print(HttpServletRequest req , Map<String,Object> model){
		
		String strOrderId =req.getParameter("order_id");
		Integer orderId = 0;
		if(!WorkerUtil.isNullOrEmpty(strOrderId)){
			orderId = Integer.parseInt(strOrderId);
		}else{
			return "failure";
		}
		Map printInfo = shipmentBiz.selectPrintInfo(orderId,"");
		List<Map<String,Object>> orderInfoList = (List<Map<String,Object>>)printInfo.get("order_info_list");
		String shippingCode = String.valueOf(orderInfoList.get(0).get("shipping_code"));
		model.put("print_info", printInfo);
		if("SF".equals(shippingCode) || "SFLY".equals(shippingCode)){
			return "/sale/print/expressBill/sf";
		}else if("YTO".equals(shippingCode)){
			return "/sale/print/expressBill/yto";
		}else if("HT".equals(shippingCode)){
			return "/sale/print/expressBill/ht";
		}else if("YD".equals(shippingCode)){
			return "/sale/print/expressBill/yd";
		}else if("YD_BQ".equals(shippingCode)){
			return "/sale/print/expressBill/yd_bq";
		}else if("ZTO".equals(shippingCode)){
			return "/sale/print/expressBill/zto";
		}else if("LEQEE_ZT".equals(shippingCode)){
			return "/sale/print/expressBill/leqee_zt";
		}else if("STO".equals(shippingCode)){
			return "/sale/print/expressBill/sto";
		}else if("DEPPON".equals(shippingCode)){
			return "/sale/print/expressBill/deppon";
		}else if("TTKDEX".equals(shippingCode)){
			return "/sale/print/expressBill/ttkdex";
		}else if("CHINAPOST".equals(shippingCode)){
			return "/sale/print/expressBill/chinapost";
		}else if("ZJS".equals(shippingCode)){
			return "/sale/print/expressBill/zjs";
		}else if("KJKD".equals(shippingCode)){
			return "/sale/print/expressBill/kjkd";
		}else if("QFKD".equals(shippingCode)){
			return "/sale/print/expressBill/qfkd";
		}else if("EMS".equals(shippingCode)){
			return "/sale/print/expressBill/ems";
		}else{
			return "";
		}
	}
	
	
	/**
	 * 打印波次单入口
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="pickShow")
	public String pickShow(HttpServletRequest req , Map<String,Object> model){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        
        // 获取物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String customer_id = req.getParameter("customer_id");
		String batch_pick_sn = req.getParameter("batch_pick_sn");
		String start_time = req.getParameter("start");
		String end_time = req.getParameter("end");
		String print_status = req.getParameter("print_status");
		String pick_status = req.getParameter("pick_status");
		String reserve_status = req.getParameter("reserve_status");
		String is_batch = req.getParameter("is_batch");
		
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customer_id", customer_id);
			model.put("customer_id", customer_id);
		}
		
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		if(!WorkerUtil.isNullOrEmpty(batch_pick_sn)){
			searchMap.put("batchPickSn", batch_pick_sn);
			model.put("batch_pick_sn", batch_pick_sn);
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
		
		if(!WorkerUtil.isNullOrEmpty(print_status)){
			if("Y".equals(print_status)){
				searchMap.put("printStatus_Y", "Y");
			}else if("N".equals(print_status)){
				searchMap.put("printStatus_N", "N");
			}
			model.put("print_status", print_status);
		}else{
			searchMap.put("printStatus_N", "N");
			model.put("print_status", "N");
		}
		
		if(!WorkerUtil.isNullOrEmpty(pick_status)){
			searchMap.put("pickStatus", pick_status);
			model.put("pick_status", pick_status);
		}
		
		if(!WorkerUtil.isNullOrEmpty(reserve_status)){
			searchMap.put("reserveStatus", reserve_status);
			model.put("reserve_status", reserve_status);
		}else{
			searchMap.put("reserveStatus", "Y");
			model.put("reserve_status", "Y");
		}
		
		if(!WorkerUtil.isNullOrEmpty(is_batch)){
			searchMap.put("is_batch", is_batch);
			model.put("is_batch", is_batch);
		}
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,15);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		
		List<Map> pickOrderList = orderBiz.getPickOrderList(searchMap);
		
		model.put("page", page);
		model.put("customers", customers);
		model.put("pickOrderList", pickOrderList);
		model.put("warehouseList", warehouseList );
		return "sale/pickShow";
	}
	
	@RequestMapping(value="pickShowV2")
	@ResponseBody
	public Object pickShowV2(HttpServletRequest req){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        Map<String,Object> resMap = new HashMap<String,Object>();
        
        // 获取物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String customer_id = req.getParameter("customer_id");
		String warehouse_id = req.getParameter("warehouse_id");
		String batch_pick_sn = req.getParameter("batch_pick_sn");
		String start_time = req.getParameter("start");
		String end_time = req.getParameter("end");
		String print_status = req.getParameter("print_status");
		String pick_status = req.getParameter("pick_status");
		String reserve_status = req.getParameter("reserve_status");
		String is_batch = req.getParameter("is_batch");
		
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customer_id", customer_id);
			resMap.put("customer_id", customer_id);
		}else{
			if(WorkerUtil.isNullOrEmpty(customers))
				customers.add(null);
			searchMap.put("customers", customers);
		}
		
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		if(!WorkerUtil.isNullOrEmpty(batch_pick_sn)){
			searchMap.put("batchPickSn", batch_pick_sn);
			resMap.put("batch_pick_sn", batch_pick_sn);
		}
		
		if(!WorkerUtil.isNullOrEmpty(start_time)){
			searchMap.put("startTime", start_time);
			resMap.put("start", start_time);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf.format(new Date(System.currentTimeMillis()));
			searchMap.put("startTime", startDate);
			resMap.put("start", startDate );
		}
		
		if(!WorkerUtil.isNullOrEmpty(end_time)){
			searchMap.put("endTime", end_time);
			resMap.put("end", end_time);
		}
		
		if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
			searchMap.put("warehouse_id", warehouse_id);
			resMap.put("warehouse_id", warehouse_id);
		}
		
		if(!WorkerUtil.isNullOrEmpty(print_status)){
			if("Y".equals(print_status)){
				searchMap.put("printStatus_Y", "Y");
			}else if("N".equals(print_status)){
				searchMap.put("printStatus_N", "N");
			}
			resMap.put("print_status", print_status);
		}else{
			searchMap.put("printStatus_N", "N");
			resMap.put("print_status", "N");
		}
		
		if(!WorkerUtil.isNullOrEmpty(pick_status)){
			searchMap.put("pickStatus", pick_status);
			resMap.put("pick_status", pick_status);
		}
		
		if(!WorkerUtil.isNullOrEmpty(reserve_status)){
			searchMap.put("reserveStatus", reserve_status);
			resMap.put("reserve_status", reserve_status);
		}
		
		if(!WorkerUtil.isNullOrEmpty(is_batch)){
			searchMap.put("is_batch", is_batch);
			resMap.put("is_batch", is_batch);
		}
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,15);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		
		List<Map> pickOrderList = orderBiz.getPickOrderList(searchMap);
		
		resMap.put("page", page);
		resMap.put("customers", customers);
		resMap.put("pickOrderList", pickOrderList);
		return resMap;
	}
	
	@RequestMapping(value="print_pick")
	//@ResponseBody
	public String print_pick(HttpServletRequest req, Map<String,Object> model){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 当前用户
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		String batchPick = req.getParameter("batch_pick").trim();
		
		String arr[] = batchPick.split(",");
		List<Map> mapList = new ArrayList<Map>();
		try {
//			for(String str : arr){
//				String sn2Id[] = str.split("_");
//				Map<String,Object> resMap = new HashMap<String,Object>();
//				Map<String,Object> returnMap = orderBiz.dealPickOrder(sn2Id[1],sysUser.getUsername());
//				
//				List<Map> pickOrderGoods = (List<Map>) returnMap.get("pickOrderGoods");
//				//List<Map> pickOrderList = (List<Map>) returnMap.get("pickOrderList");
//
//				resMap.put("sn", sn2Id[0]);
//				resMap.put("pickOrderGoods", pickOrderGoods);
//				resMap.put("kinds",returnMap.get("kindNum"));
//				resMap.put("allNum", returnMap.get("allNum"));
//				resMap.put("batch_process_type", returnMap.get("batch_process_type"));
//				resMap.put("contain_way", returnMap.get("contain_way"));
//				resMap.put("order_num", returnMap.get("order_num"));
//				//resMap.put("pickOrderList", pickOrderList);
//				mapList.add(resMap);
//			}
			Map<String,Object> returnMap = orderBiz.dealPickOrder(arr,sysUser.getUsername());
			model.put("result", "success");
			model.put("note", "success");
			model.put("mapList", (List<Map>)returnMap.get("mapList"));
		} catch (Exception e) {
			e.printStackTrace();
			model.put("result", "failure");
			model.put("note", "打印波次单失败，失败原因："+e.getMessage());
			model.put("mapList", null);
		}
		return "/sale/print/batchPick/batch_pick";
	}
	
	
	@RequestMapping(value="print_pick_tag")
	public String print_pick_tag(HttpServletRequest req, Map<String,Object> model){
		
		String batchPickId = req.getParameter("batch_pick_id").trim();
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map<String,Object> returnMap = orderBiz.printBatchPickTag(batchPickId);
		
		List<Map> pickOrderGoods = (List<Map>) returnMap.get("pickOrderGoods");
		model.put("pickOrderGoods", pickOrderGoods);
		model.put("kinds",pickOrderGoods.size());
		return "/sale/print/batchPick/batch_pick_tag";
	}
	
	
	// 打印发货单
	@RequestMapping(value="printCardShow")
	public String printCard(HttpServletRequest req){
		return "/sale/print/dispatchBill/printCardShow";
	}
	
	@RequestMapping(value="search_orders")
	@ResponseBody
	public Object search_orders(@RequestBody String[] batchPickSnList){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		// 1.获取前端传来的batch_pick_id
		//String batchPickSn = req.getParameter("batch_pick_sn_list").trim();
		//String batchPickId = req.getParameter("batch_pick_id").trim();
		
		// 2.搜索订单
		List<Map> pickOrderList = new ArrayList<Map>();
		for(int i=0;i<batchPickSnList.length;i++){
			pickOrderList.addAll(orderBiz.getBatchPickOrderList(batchPickSnList[i]));
		}
		resMap.put("pickOrderList", pickOrderList);
		return resMap;
	}
	
	@RequestMapping(value="search_orders2")
	public String search_orders2(HttpServletRequest req, Map<String,Object> model){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		// 1.获取前端传来的batch_pick_id
		String batchPickId = req.getParameter("batch_pick_id").trim();
		
		// 2.搜索订单
		
		List<Map> pickOrderList = orderBiz.getBatchPickOrderList(batchPickId);
		
		model.put("batch_pick_id", batchPickId);
		model.put("batch_pick_sn", req.getParameter("batch_pick_sn").trim());
		model.put("pickOrderList", pickOrderList);
		return "sale/print/dispatchBill/printCardShow_link";
	}
	
	@RequestMapping(value="print_card")
	public String print_card(HttpServletRequest req , Map<String,Object> model){
		
		
		String orderId = req.getParameter("order_id");
		String batch_pick_sn = req.getParameter("batch_pick_sn");
		String batchPickId = req.getParameter("batch_pick_id");
		
		
		String fileName = "";
		List<Map> orderInfoList = new ArrayList<Map>();
		List<Map> orderInfoList2 = new ArrayList<Map>();
		if(!WorkerUtil.isNullOrEmpty(batchPickId)){
		String batchPickIdArr[] = batchPickId.split("_");
			Map resMap = orderBiz.getPrintDispatchInfo(batchPickIdArr);
			orderInfoList = (List<Map>)resMap.get("orderInfoList");
			fileName = resMap.get("fileName").toString();
		}
		if(!WorkerUtil.isNullOrEmpty(orderId)){
			String orderIdArr[] = orderId.split(",");
			List<String> orderList = new ArrayList<String>();
			Collections.addAll(orderList, orderIdArr);
			for(Map map:orderInfoList){
				if(orderList.contains(map.get("order_id").toString())){
					orderInfoList2.add(map);
				}
			}
			model.put("orderInfoList", orderInfoList2);
		}else{
			model.put("orderInfoList", orderInfoList);
		}
		return "/sale/print/dispatchBill/"+fileName;
	}
	
	
	@RequestMapping(value="print_card_v2")
	public String print_card_v2(HttpServletRequest req , Map<String,Object> model){
		
		String batchPickIdList = req.getParameter("batch_pick_id");
		String batchPickIdArr[] = batchPickIdList.split("_");
		List<Map> orderInfoList = new ArrayList<Map>();
		String fileName = "";
		Map resMap = orderBiz.getPrintDispatchInfo(batchPickIdArr);
		orderInfoList = (List<Map>)resMap.get("orderInfoList");
		fileName = resMap.get("fileName").toString();
		model.put("orderInfoList", orderInfoList);
		return "/sale/print/dispatchBill/"+fileName;
	}
	/***************************************************************
	 * 销售订单历史查询
	 * @author hzhang1
	 * @date 2016-03-16
	 ****************************************************************/
	private Cache<String, List<String>> shopNameCache;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="search")
	public String search(HttpServletRequest req , Map<String,Object> model){
		
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 获取当前物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
        // 获取货主
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
        // 拥有的逻辑仓库
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        
		
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String order_id = req.getParameter("order_id");
		String shop_order_sn = req.getParameter("shop_order_sn");
		String start_time = req.getParameter("start");
		String end_time = req.getParameter("end");
		String order_status = req.getParameter("order_status");
		String warehouse_id = req.getParameter("warehouse_id");
		String customer_id = req.getParameter("customer_id");
		
		
		if(!WorkerUtil.isNullOrEmpty(start_time)){
			searchMap.put("start_time", start_time);
			model.put("start", start_time);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf.format(new Date(System.currentTimeMillis()));
			searchMap.put("start_time", startDate);
			model.put("start", startDate);
		}
		
		
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		HashMap<String, Object> customerShops = new HashMap<String, Object>();
		CacheManager cacheManager = securityManager.getCacheManager();
		customerShopCache = cacheManager.getCache("customerShopCache");
		List<List<Map<String,Object>>> shopNameLists = new ArrayList<List<Map<String,Object>>>();
		for (WarehouseCustomer customer : customers) {
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
		model.put("customerShops", shopNameLists);
		model.put("customers", customers);
		model.put("warehouseList", warehouseList);
		return "sale/orders";
	}
	
	
	@RequestMapping(value="search_ajax")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object searchAjax(HttpServletRequest req ){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 获取当前物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
        // 获取货主
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
		// 拥有的仓库权限列表
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        List<Integer> warehouseIdList = new ArrayList<Integer>();
        List<String> orderStatusList = new ArrayList<String>();
        List<Map> saleOrderList = new ArrayList<Map>();
        
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String order_id = req.getParameter("order_id").trim();
		String shop_order_sn = req.getParameter("shop_order_sn").trim();
		//String time_type = req.getParameter("time_type");
		String start_time = req.getParameter("start");
		String end_time = req.getParameter("end");
		String order_status = req.getParameter("order_status");
		String warehouse_id = req.getParameter("warehouse_id");
		String customer_id = req.getParameter("customer_id");
		String batch_pick_sn = req.getParameter("batch_pick_sn");
		String is_reserved = req.getParameter("is_reserved");
		String oms_shop_id=req.getParameter("shop_id");
		
		// 2.分页信息设置
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,12);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		searchMap.put("type", "search");
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		if(!WorkerUtil.isNullOrEmpty(order_id)){
			searchMap.put("order_id", order_id);
			saleOrderList = orderBiz.getSaleOrderList(searchMap,"order_id");
		}else if(!WorkerUtil.isNullOrEmpty(batch_pick_sn)){
			searchMap.put("batch_pick_sn", batch_pick_sn);
			saleOrderList = orderBiz.getSaleOrderList(searchMap,"batch_pick_sn");
		}else if(!WorkerUtil.isNullOrEmpty(shop_order_sn)){
			searchMap.put("shop_order_sn", shop_order_sn);
			saleOrderList = orderBiz.getSaleOrderList(searchMap,"shop_order_sn");
		}else{
			//searchMap.put("time_type", time_type);
			if(!WorkerUtil.isNullOrEmpty(customer_id)){
				searchMap.put("customer_id", customer_id);
			}else{
				if(WorkerUtil.isNullOrEmpty(customers))
					customers.add(null);
				searchMap.put("customers", customers);
			}
			if(!WorkerUtil.isNullOrEmpty(start_time)){
				searchMap.put("start_time", start_time);
			}else{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String startDate = sdf.format(new Date(System.currentTimeMillis()));
				searchMap.put("start_time", startDate);
			}
			
			if(!WorkerUtil.isNullOrEmpty(end_time)){
				searchMap.put("end_time", end_time);
			}
			
			if(!WorkerUtil.isNullOrEmpty(oms_shop_id)){
				searchMap.put("oms_shop_id", oms_shop_id);
			}
			
			if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
				searchMap.put("warehouse_id", warehouse_id);
			}
			
			if(!WorkerUtil.isNullOrEmpty(is_reserved) && is_reserved.equals("Y")){
				searchMap.put("is_reserved", "Y");
			}else if(!WorkerUtil.isNullOrEmpty(is_reserved) && is_reserved.equals("N")){
				searchMap.put("is_reserved_n", "N");
			}
			
			if(!WorkerUtil.isNullOrEmpty(order_status)){
				//searchMap.put("order_status", order_status);
				if(OrderInfo.ORDER_STATUS_DELEVERED.equals(order_status) || "FULFILLED".equals(order_status)){
					orderStatusList.add(OrderInfo.ORDER_STATUS_DELEVERED);
					orderStatusList.add("FULFILLED");
				}else{
					orderStatusList.add(order_status);
				}
				searchMap.put("orderStatusList", orderStatusList);
			}
		
			if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
				warehouseIdList.add(Integer.valueOf(warehouse_id));
			}else{
		        for (Warehouse warehouse : warehouseList) {
		        	warehouseIdList.add(warehouse.getWarehouse_id());
				}
			}
			searchMap.put("warehouseIdList", warehouseIdList);
			saleOrderList = orderBiz.getSaleOrderList(searchMap,"general");
		}
		
		
		
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		isDouble11Cache = cacheManager.getCache("isDouble11Cache");
		String isDouble11=isDouble11Cache.get(currentPhysicalWarehouse.getPhysical_warehouse_id()+"");
		if(isDouble11 == null){
			isDouble11 = configDao.getConfigValueByFrezen(currentPhysicalWarehouse.getPhysical_warehouse_id(), 0, "IS_DOUBLE_11");
			isDouble11Cache.put(currentPhysicalWarehouse.getPhysical_warehouse_id().toString(), isDouble11);
		}
		
		// 3.查找订单
		resMap.put("isDouble11", isDouble11);
		resMap.put("page", page);
		resMap.put("saleOrderList", saleOrderList);
		resMap.put("warehouseList", warehouseList);
		resMap.put("customers", customers);
		
		// 4.返回订单结果
		return resMap;
	}
	
	@RequestMapping(value="exportOrders")
	@SuppressWarnings("unchecked")
	public ModelAndView exportOrders(HttpServletRequest req , Map<String,Object> model){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		ViewExcel viewExcel = new ViewExcel(); 
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 获取当前物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
        // 获取货主
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
		// 拥有的仓库权限列表
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        List<Integer> warehouseIdList = new ArrayList<Integer>();
        List<String> orderStatusList = new ArrayList<String>();
        
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		String order_id = req.getParameter("order_id");
		String shop_order_sn = req.getParameter("shop_order_sn");
		//String time_type = req.getParameter("time_type");
		String start_time = req.getParameter("start");
		String end_time = req.getParameter("end");
		String order_status = req.getParameter("order_status");
		String warehouse_id = req.getParameter("warehouse_id");
		String customer_id = req.getParameter("customer_id");
		String batch_pick_sn = req.getParameter("batch_pick_sn");
		String is_reserved = req.getParameter("is_reserved");
		String oms_shop_id=req.getParameter("shop_id");
		
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		
		if(!WorkerUtil.isNullOrEmpty(order_id)){
			searchMap.put("order_id", order_id);
		}else if(!WorkerUtil.isNullOrEmpty(batch_pick_sn)){
			searchMap.put("batch_pick_sn", batch_pick_sn);
		}else if(!WorkerUtil.isNullOrEmpty(shop_order_sn)){
			searchMap.put("shop_order_sn", shop_order_sn);
		}else{
			//searchMap.put("time_type", time_type);
			if(!WorkerUtil.isNullOrEmpty(is_reserved) && is_reserved.equals("Y")){
				searchMap.put("is_reserved", "Y");
			}else if(!WorkerUtil.isNullOrEmpty(is_reserved) && is_reserved.equals("N")){
				searchMap.put("is_reserved_n", "N");
			}
			if(!WorkerUtil.isNullOrEmpty(customer_id)){
				searchMap.put("customer_id", customer_id);
			}else{
				if(WorkerUtil.isNullOrEmpty(customers))
					customers.add(null);
				searchMap.put("customers", customers);
			}
			if(!WorkerUtil.isNullOrEmpty(start_time)){
				searchMap.put("start_time", start_time);
			}else{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String startDate = sdf.format(new Date(System.currentTimeMillis()));
				searchMap.put("start_time", startDate);
			}
			
			if(!WorkerUtil.isNullOrEmpty(end_time)){
				searchMap.put("end_time", end_time);
			}
			
			if(!WorkerUtil.isNullOrEmpty(oms_shop_id)){
				searchMap.put("oms_shop_id", oms_shop_id);
			}
			
			if(!WorkerUtil.isNullOrEmpty(order_status)){
				//searchMap.put("order_status", order_status);
				if(OrderInfo.ORDER_STATUS_DELEVERED.equals(order_status) || "FULFILLED".equals(order_status)){
					orderStatusList.add(OrderInfo.ORDER_STATUS_DELEVERED);
					orderStatusList.add("FULFILLED");
				}else{
					orderStatusList.add(order_status);
				}
				searchMap.put("orderStatusList", orderStatusList);
			}
		
			if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
				warehouseIdList.add(Integer.valueOf(warehouse_id));
			}else{
		        for (Warehouse warehouse : warehouseList) {
		        	warehouseIdList.add(warehouse.getWarehouse_id());
				}
			}
			searchMap.put("warehouseIdList", warehouseIdList);
		}
		searchMap.put("type", "export");
		
		// 2.查找订单
		List<Map> saleOrderList = orderBiz.getSaleOrderList(searchMap,"general");
		model.put("type", "exportSaleOrders");
		model.put("saleOrderList", saleOrderList);
		
		// 3.返回订单结果
		return new ModelAndView(viewExcel, model);  
	}
	
	@RequestMapping(value="detail")
	public String detail(HttpServletRequest req , Map<String,Object> model){
		// 1.获取从前端传来的数据
		Map<String,Object> searchMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 获取当前物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		String orderId = req.getParameter("order_id");
		
		if(!WorkerUtil.isNullOrEmpty(orderId)){
			searchMap.put("orderId", orderId);
			model.put("order_id", orderId);
		}
		
		
		// 2.查找订单
		OrderInfo orderInfo = orderBiz.getOrderInfoByOrderId(Integer.valueOf(orderId));
		if(!currentPhysicalWarehouse.getPhysical_warehouse_id().equals(orderInfo.getPhysical_warehouse_id())){
			return "sale/detail";
		}
		//Map<String,Object> saleOrderInfoMap = orderBiz.getSaleOrderInfoById(searchMap);
		
		List<Map> orderDetail = orderBiz.getSaleOrderById(searchMap);
		List<Map> orderGoodsDetial = orderBiz.getSaleOrderGoodsById(orderInfo);
		List<Map> orderActionDetial = orderBiz.getSaleOrderActionById(searchMap);
		
		logger.info("orderDetail：" + orderDetail.size()
				+ " orderGoodsDetial:" + orderGoodsDetial.size()
				+ " orderActionDetial:" + orderActionDetial.size());
		model.put("order_id", orderId);
		model.put("orderDetial", orderDetail);
		model.put("orderGoodsDetial", orderGoodsDetial);
		model.put("orderActionDetial",orderActionDetial );
		
		// 3.返回订单
		return "sale/detail";
	}
	
	/**
	 * hchen1
	 * 加工复核页面
	 */
	@RequestMapping(value="/packCheckSurface") 
	public String packCheckSurface( HttpServletRequest req , Map<String,Object> model ){
		//1、获取客户端参数
		String username =  req.getParameter("username") ;
		String msg =  req.getParameter("msg") ;
		
		//2、打印日志
		logger.info(username);
		model.put("username", username );    
		model.put("msg", msg );    
		
		return "/sale/packcheckGoods";    
	}
	
	/**
	 * hchen1
	 * 预打包查询页面
	 */
	@RequestMapping(value="/prepack") 
	public String prepack( HttpServletRequest req , Map<String,Object> model ){
		//1、获取客户端参数
		String username =  req.getParameter("username") ;
		String msg =  req.getParameter("msg") ;
		
		//2、打印日志
		logger.info(username);
		model.put("username", username );    
		model.put("msg", msg );    
		
		return "/sale/prepackQuery";    
	}
	
	
	@RequestMapping(value="/prepackQuery") 
	@ResponseBody
	public Map<String, Object> prepackQuery( HttpServletRequest req , Map<String,Object> model ){
		//1、获取客户端参数
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String barcode = req.getParameter("barcode");
		
		//查询预打包商品信息
		resultMap = orderPrepackBiz.getPrepackByBarcode(barcode,warehouse.getPhysical_warehouse_id());
		
		return resultMap ;    
	}
	
	
	/**
	 * hchen1
	 * 加工复核页面
	 */
	@RequestMapping(value="/unPackCheckSurface") 
	public String unPackCheckSurface( HttpServletRequest req , Map<String,Object> model ){
		//1、获取客户端参数
		String username =  req.getParameter("username") ;
		String msg =  req.getParameter("msg") ;
		
		//2、打印日志
		logger.info(username);
		model.put("username", username );    
		model.put("msg", msg );    
		
		return "/sale/unPackcheckGoods";    
	}
	
	/**
	 * (复核)加载信息
	 * */
	@RequestMapping(value="/checkLoadOrder")
	@ResponseBody
	public Map<String, Object> checkLoadOrder(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String order_sn = req.getParameter("order_sn");
		String type =req.getParameter("type");
		if(WorkerUtil.isNullOrEmpty(type)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "加工单类型为空");
			return resultMap;
		}
		if(WorkerUtil.isNullOrEmpty(order_sn)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "没有接收到订单号信息");
			return resultMap;
		}
		try{
			resultMap = orderPrepackBiz.loadGoodsForOrder(order_sn,type);
			logger.info("信息加载成功");
		}catch(Exception e){
			logger.info("（复核）加载商品发生异常，异常信息:" + e.getMessage());
			e.printStackTrace();
			resultMap.put("result",Response.FAILURE);
			resultMap.put("note", "（复核）加载商品错误："+ e.getMessage());
		}	
		
		return resultMap;
	}
	
	/**
	 *确认接口
	 * @param KEY
	 * @param check_order_id
	 * */
	@RequestMapping(value="/confirm")
	@ResponseBody
	public Map<String, Object> confirm(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		
		Integer check_order_id = WorkerUtil.isNullOrEmpty(req.getParameter("order_id")) ? null : Integer.valueOf(req.getParameter("order_id"));
		String packbox_product_barcode = WorkerUtil.isNullOrEmpty(req.getParameter("packbox_product_barcode")) ? null : req.getParameter("packbox_product_barcode");
		//List<Map<String, Object>> checkInitGoodsList = req.getParameter("checkInitGoodsList");
		
		try {
			resultMap = orderPrepackBiz.getCheckOrderGoodsConfirm(check_order_id, packbox_product_barcode);
			logger.info("信息确认成功");
		} catch (Exception e) {
			logger.error("（复核）商品条码确认失败!异常信息:" + e.getMessage() );
			e.printStackTrace();
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "（复核）商品条码确认失败!异常信息:" + e.getMessage() );
		}
		
		return resultMap;
	}
	

	/**
	 *上架打印标签接口
	 * @param KEY
	 * @param check_order_id
	 * */
	@RequestMapping(value="/grounding_print")
	public String groundingPrint(HttpServletRequest req,Map<String,Object> model){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		
		Integer order_id = WorkerUtil.isNullOrEmpty(req.getParameter("order_id")) ? null : Integer.valueOf(req.getParameter("order_id"));
		Integer packbox_product_id = WorkerUtil.isNullOrEmpty(req.getParameter("packbox_product_id")) ? null : Integer.valueOf(req.getParameter("packbox_product_id"));
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Integer preNumber = Integer.valueOf(req.getParameter("preNumber"));
        Integer prePackageNumber   = Integer.valueOf(req.getParameter("prePackageNumber"));  
        // 1.获取当前用户及当前货主
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		
		try {
			resultMap = orderPrepackBiz.groundingPrint(order_id, packbox_product_id,sysUser.getUsername(),preNumber,prePackageNumber);
		} catch (Exception e) {
			logger.error("（复核）上架失败!异常信息:" + e.getMessage() );
			e.printStackTrace();
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "（复核）上架失败!异常信息:" + e.getMessage() );
		}
		model.putAll( resultMap);
		return  "/sale/groundingPrint";
	}
	
	
	/**
	 *拆解上架打印标签接口
	 * @param KEY
	 * @param check_order_id
	 * */
	@RequestMapping(value="/Ungrounding_print")
	public String UngroundingPrint(HttpServletRequest req, Map<String,Object> model){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		
		Integer order_id = WorkerUtil.isNullOrEmpty(req.getParameter("order_id")) ? null : Integer.valueOf(req.getParameter("order_id"));
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Integer preNumber = Integer.valueOf(req.getParameter("preNumber"));
        Integer prePackageNumber   = Integer.valueOf(req.getParameter("prePackageNumber"));
        
        // 1.获取当前用户及当前货主
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		
		try {
			resultMap = orderPrepackBiz.UngroundingPrint(order_id,sysUser.getUsername(),preNumber,prePackageNumber);
			
		} catch (Exception e) {
			logger.error("（复核）上架失败!异常信息:" + e.getMessage() );
			e.printStackTrace();
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "（复核）上架失败!异常信息:" + e.getMessage() );
		}
		model.put("printList", resultMap.get("printList"));
		model.putAll(resultMap);
		return "/sale/UngroundingPrint";
	}
	
	/**
	 * @author hchen
	 * @CreatedDate 2016.03.14
	 * 根据订单选择打印格式链接
	 */
	@RequestMapping(value="/printbarcode")
	public String printBarcode(HttpServletRequest req , Map<String,Object> model){
		
		String comboBarcode =req.getParameter("comboBarcode");
		
		Integer preNumber = Integer.valueOf(req.getParameter("preNumber"));
		logger.info("comboBarcode:"+comboBarcode);
		if(WorkerUtil.isNullOrEmpty(comboBarcode)){
			model.put("error", "商品条码为空");
		}else {
			String barcodeName = productDao.selectProductNameByBarcode(comboBarcode);
			model.put("success", Response.OK);
			model.put("comboBarcode", comboBarcode);
			model.put("barcodeName", barcodeName);
			model.put("preNumber", preNumber);
		}
		
		
		return "/sale/comboBarcode";
	}
	
	@RequestMapping(value="/changeship")
	public String changeShipmentPage(HttpServletRequest req,Map<String, Object> model){
		Subject subject = SecurityUtils.getSubject();
    	Session session = subject.getSession();
		List<WarehouseCustomer> customerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Warehouse> warehouse_list = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
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
				//System.out.println("customer.getCustomer_id ： "+customer.getCustomer_id());
				shopName = orderInfoDao.selectCustomerShopCache(customer.getCustomer_id());
				customerShopCache.put(customer.getCustomer_id() + "", shopName);
			}
			
			customerMap.put("customerId", customer.getCustomer_id());
			shopNameMap.put("shopName", shopName);
			shopNameList.add(customerMap);
			shopNameList.add(shopNameMap);
			shopNameLists.add(shopNameList);
		}
		Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		
		model.put("shopNameLists", shopNameLists);//用于前端下拉框显示(二级店铺)
		model.put("shippinglist", shippingDao.selectAllShipping());
		model.put("targetShippinglist", shippingDao.searchElecShipping(warehouse.getWarehouse_id()));
		model.put("warehouseList",warehouse_list);
		String startDate = DateUtils.getDateString(1, "yyyy-MM-dd", "");
		model.put("start", startDate );
		model.put("customer_id", "0");
		return "sale/change_ship";
	}
	
	@RequestMapping(value="/getShipOrders")
	@ResponseBody
	public Map getShipOrders(HttpServletRequest request){
		Map<String,Object> searchMap = new HashMap<String,Object>();
		// 1.获取从前端传来的数据
		String customerId = request.getParameter("customer_id");
		String warehouseId = request.getParameter("warehouse_id");
		String shopId = request.getParameter("shop_id");
		String shippingId = request.getParameter("shipping_id");
		String startTime = request.getParameter("start");
		String endTime = request.getParameter("end");
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> customerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Integer> customerIdList = new ArrayList<Integer>();
		Map<Integer,String> customermap = new HashMap<Integer,String>();
		for (WarehouseCustomer customer : customerList) {
			customermap.put(customer.getCustomer_id(),customer.getName());
		}
		if(!StringUtils.isBlank(customerId)&&!"0".equals(customerId)){
			customerIdList.add(Integer.valueOf(customerId));
			searchMap.put("customerIdList", customerIdList);
		}else{
			for (WarehouseCustomer customer : customerList) {
	        	customerIdList.add(customer.getCustomer_id());
			}
			searchMap.put("customerIdList", customerIdList);
		}

		if(!StringUtils.isBlank(warehouseId)&&!"0".equals(warehouseId)){
			searchMap.put("warehouseId", warehouseId);
		}
		if(!StringUtils.isBlank(shopId)&&!"0".equals(shopId)){
			searchMap.put("shopId", shopId);
		}
		if(!StringUtils.isBlank(shippingId)&&!"0".equals(shippingId)){
			searchMap.put("shippingId", shippingId);
		}
		if(!StringUtils.isBlank(startTime)){
			searchMap.put("startTime", startTime);
		}else if (!StringUtils.isBlank(endTime)) {
			searchMap.put("endTime", endTime);
			searchMap.put("startTime", DateUtils.getDateString(10, "yyyy-MM-dd",endTime));
		}
		searchMap.put("orderStatus", OrderInfo.ORDER_STATUS_ACCEPT);
		
		Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		searchMap.put("warehouseid", warehouse.getWarehouse_id());
		
		// 2.分页信息设置
		PageParameter page = new PageParameter();
		if (!StringUtils.isBlank(request.getParameter("currentPage"))) {
			page.setCurrentPage(Integer.valueOf(request
					.getParameter("currentPage")));
		}
		if (!StringUtils.isBlank(request.getParameter("pageSize"))) {
			page.setPageSize(Integer.valueOf(request
					.getParameter("pageSize")));
		}else{
			page.setPageSize(100);
		}
		searchMap.put("page", page);
		
		List<Map<String,Object>> orderList = orderBiz.getOrdersByShip(searchMap);
		SimpleDateFormat dateFormats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期
		List<Shipping> shiplist = shippingDao.selectAllShipping();
		Map<Integer,String> shipmap = new HashMap<Integer,String>();
		for(Shipping s:shiplist){
			shipmap.put(s.getShipping_id(), s.getShipping_name());
		}
		for(Map<String, Object> item:orderList){
			if(item.get("order_time") instanceof Date){
				item.put("order_time", dateFormats.format((Date)item.get("order_time")));
			}
			item.put("customer_name", customermap.get((Integer)item.get("customer_id")));
			item.put("shipping_name", shipmap.get((Integer)item.get("shipping_id")));
		}
		searchMap.put("orderList",orderList);
		return searchMap;
	}
	@RequestMapping(value="/commitchangeship")
	@ResponseBody
	public Map commitchangeship(HttpServletRequest request,@RequestParam(value="id[]")Integer[] ids,Integer shippingId){
		Map res = new HashMap();
		res.put("code", 0);
		if(ids==null||ids.length<=0||shippingId==null||shippingId<=0){
			res.put("code", -1);
			res.put("msg", "参数不正确");
			return res;
		}
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> customerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		List<Integer> customerIdList = new ArrayList<Integer>();
		for (WarehouseCustomer customer : customerList) {
        	customerIdList.add(customer.getCustomer_id());
		}
		Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		List<Map<String, Object>> shiplist = shippingDao.searchElecShipping(warehouse.getWarehouse_id());
		boolean isValidShip=false;
		for(Map<String, Object> item:shiplist){
			if(shippingId.equals(Integer.valueOf(item.get("shipping_id").toString()))){
				isValidShip=true;
				break;
			}
		}
		if(!isValidShip){
			res.put("code", -1);
			res.put("msg", "该目标快递不在当前物理仓可选快递内。");
			return res;
		}
		SysUser user = (SysUser)session.getAttribute("currentUser");
		String msg = orderBiz.batchChangeShip(customerIdList,ids,shippingId,user.getUsername());
		if(!StringUtils.isBlank(msg)){
			res.put("code", -1);
			res.put("msg", msg);
		}
		return res;
	}
	
	
	
	
}