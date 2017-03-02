package com.leqee.wms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

import com.leqee.wms.api.util.Tools;
import com.leqee.wms.biz.CountTaskBiz;
import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.ReplenishmentBiz;
import com.leqee.wms.biz.ShipmentBiz;
import com.leqee.wms.dao.BatchPickDao;
import com.leqee.wms.dao.ConfigCwMappingDao;
import com.leqee.wms.dao.CountTaskDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderPrepackDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.ConfigCwMapping;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.LocationBarcodeTools;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;


@Controller
@RequestMapping(value="/exception")  //指定根路径
public class ExceptionController  {

	
	private Logger logger = Logger.getLogger(ExceptionController.class);

	
	
	@Autowired
	OrderBiz orderBiz;
	@Autowired
	ShipmentBiz shipmentBiz;
	@Autowired
	ReplenishmentBiz replenishmentBiz;
	@Autowired
	CountTaskDao countTaskDao;
	@Autowired
	BatchPickDao batchPickDao;
	@Autowired
	ProductLocationDao productLocationDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderProcessDao orderProcessDao;
	@Autowired
	InventoryBiz inventoryBiz;
	@Autowired
	CountTaskBiz countTaskBiz;
	@Autowired 
	ShippingDao shippingDao;
	@Autowired
	OrderPrepackDao orderPrepackDao;
	@Autowired
	ProductDao productDao;
	
	@Autowired
	ConfigCwMappingDao configCwMappingDao;
	/**
	 * 删除面单
	 */
	@RequestMapping(value="/deleteTrackingNumber") 
	public String deleteTrackingNumber( HttpServletRequest req , Map<String,Object> model ){
		//1、获取客户端参数
		String username =  req.getParameter("username") ;
		String msg =  req.getParameter("msg") ;
		
		//2、打印日志
		logger.info(username);
		model.put("username", username );    
		model.put("msg", msg );    
		
		return "/exception/deleteTrackingNumber";    
	}
	
	/**
	 * (删除面单)加载
	 * */
	@RequestMapping(value="/loadOrderTns")
	@ResponseBody
	public Map<String, Object> loadOrderTns(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String orderId = req.getParameter("order_id");
		String trackingNumber = req.getParameter("tracking_number");
		String batchPickSn = req.getParameter("batch_pick_sn");
		if(WorkerUtil.isNullOrEmpty(orderId) && WorkerUtil.isNullOrEmpty(trackingNumber) && WorkerUtil.isNullOrEmpty(batchPickSn)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "没有接收到订单号|快递单号|波次单号");
			return resultMap;
		}else if(!WorkerUtil.isNullOrEmpty(trackingNumber)){ //输入 快递单号
			
			try{
				resultMap = shipmentBiz.loadGoodsForDeleteByTn(trackingNumber);
			}catch(Exception e){
				logger.info("删除面单加载失败！错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "删除面单加载失败！错误："+ e.getMessage());
			}
		}else if(!WorkerUtil.isNullOrEmpty(orderId)){// 输入 订单号
			
			try{
				resultMap = shipmentBiz.loadGoodsForDeleteByOrderId(Integer.parseInt(orderId));
			}catch(NumberFormatException e){
				logger.info("删除面单加载失败！错误:"+e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "删除面单加载失败！错误："+ "订单号并非数字格式");
			}catch(Exception e){
				logger.info("删除面单加载失败！错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "删除面单加载失败！错误："+ e.getMessage());
			}	
		}else if(!WorkerUtil.isNullOrEmpty(batchPickSn)){//波次单号
			try{
				resultMap = shipmentBiz.loadBatchInfosForDeleteByBatchPickSn(batchPickSn);
			}catch(Exception e){
				logger.info("删除面单加载失败！错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "删除面单加载失败！错误："+ e.getMessage());
			}
		}
		return resultMap;
	}
	/**
	 * (删除面单)清空复核记录
	 * */
	@RequestMapping(value="/cleanOrderTns")
	@ResponseBody
	public Map<String, Object> cleanOrderTns(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String orderId = req.getParameter("order_id");
		String batchPickSn = req.getParameter("batch_pick_sn");
		if(WorkerUtil.isNullOrEmpty(orderId) && WorkerUtil.isNullOrEmpty(batchPickSn)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "没有接收到清空信息！");
			return resultMap;
		}else if(!WorkerUtil.isNullOrEmpty(orderId)){// 输入 订单号
			try{
				resultMap = shipmentBiz.cleanOrderTns(Integer.parseInt(orderId));
			}catch(NumberFormatException e){
				logger.info("清空复核记录失败！错误:"+e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "清空复核记录失败！错误："+ "订单号并非数字格式");
			}catch(Exception e){
				logger.info("清空复核记录失败！错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "清空复核记录失败！错误："+ e.getMessage());
			}	
		}else if(!WorkerUtil.isNullOrEmpty(batchPickSn)){// 输入 波次单号
			try{
				resultMap = shipmentBiz.cleanBatchPickOrderTns(batchPickSn);
			}catch(Exception e){
				logger.info("清空复核记录失败！错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "清空复核记录失败！错误："+ e.getMessage());
			}	
		}
		return resultMap;
	}
	
	
	
	
	
	/**
	 * @author dlyao
	 * @20160518
	 * 
	 */
	@RequestMapping(value="/queryOrders") 
	public String queryOrders( HttpServletRequest req , Map<String,Object> model ){
		logger.debug("exception   queryOrders ");
		return "/exception/queryOrders";    
	}
	
	/**
	 * @author dlyao
	 * @20160518
	 * 
	 */
	@RequestMapping(value="/doQueryOrders") 
	@ResponseBody
	public Map<String, Object> doQueryOrders( HttpServletRequest req){
		logger.debug("exception   doQueryOrders ");
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String type=req.getParameter("type");
		String no = req.getParameter("no");
		if(type.equals("order_id")){
			resultMap=orderBiz.queryBatchPickByOrder_id(no);
		}else{
			resultMap=shipmentBiz.queryOrderByTrackingNo(no);
		}
		
		return resultMap;    
	}

	
	
	@RequestMapping(value="printPickV2")
	public String printPickV2(HttpServletRequest req){
		return "/exception/printPickV2";
	}
	
	@RequestMapping(value="print_pick")
	public String print_pick(HttpServletRequest req, Map<String,Object> model){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 当前用户
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		String batchPickSn = req.getParameter("batch_pick_sn").trim();
		BatchPick batchPick = orderBiz.getBatchPickBySn(batchPickSn);
		
		
		List<Map> mapList = new ArrayList<Map>();
		Map<String,Object> resMap = new HashMap<String,Object>();
		String arr[] = new String[1];
		arr[0] = "F_"+batchPick.getBatch_pick_id().toString();
		Map<String,Object> returnMap = orderBiz.dealPickOrder(arr,sysUser.getUsername());
		
		List<Map> pickOrderGoods = (List<Map>) returnMap.get("pickOrderGoods");
		//List<Map> pickOrderList = (List<Map>) returnMap.get("pickOrderList");

//		resMap.put("sn", batchPickSn);
//		resMap.put("pickOrderGoods", pickOrderGoods);
//		resMap.put("kinds",returnMap.get("kindNum"));
//		resMap.put("allNum", returnMap.get("allNum"));
//		resMap.put("batch_process_type", returnMap.get("batch_process_type"));
//		resMap.put("contain_way", returnMap.get("contain_way"));
//		resMap.put("order_num", returnMap.get("order_num"));
//		//resMap.put("pickOrderList", pickOrderList);
//		mapList.add(resMap);
		
		model.put("mapList",  (List<Map>)returnMap.get("mapList"));
		return "/sale/print/batchPick/batch_pick";
	}
	
	@RequestMapping(value="printDeliveryV2")
	public String printDeliveryV2(HttpServletRequest req){
		return "/exception/printDeliveryV2";
	}
	
	
	@RequestMapping(value="print_delivery")
	@ResponseBody
	public Object print_delivery(HttpServletRequest req, Map<String,Object> model){
		
		String orderId2Number = req.getParameter("orderId2Number");
		String arr[] = orderId2Number.split("_");
		String strOrderId = arr[0];
		String trackingNumber = "";
		if(arr.length > 1){
			trackingNumber = arr[1];
		}
		Integer orderId = 0;
		
		Map printInfo = null;
		try {
			if(!WorkerUtil.isNullOrEmpty(strOrderId)){
				orderId = Integer.parseInt(strOrderId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.put("result", "failure");
			model.put("note", "输入值格式有误("+strOrderId+")");
			return model;
		}
		String orderStatus = shipmentBiz.selectOrderStatusByOrderOrTrackingNumber(orderId,trackingNumber);
		List<String> list = new ArrayList<String>();
		list.add(OrderInfo.ORDER_STATUS_RECHECKED);
		list.add(OrderInfo.ORDER_STATUS_WEIGHED);
		list.add(OrderInfo.ORDER_STATUS_DELEVERED);
		list.add(OrderInfo.ORDER_STATUS_FULFILLED);
		if("".equalsIgnoreCase(orderStatus)){
			model.put("result", "failure");
			model.put("note", "根据输入内容无法判断订单状态，请检查是否有多余空格("+orderId2Number+")");
			return model;
		}else if(!list.contains(orderStatus)){
			model.put("result", "failure");
			model.put("note", "订单当前状态为"+OrderInfo.ORDER_STATUS_MAP.get(orderStatus)+",不允许补打印快递单");
			return model;
		}
		
		printInfo = shipmentBiz.selectPrintInfo(orderId,trackingNumber);
		
		List<Map<String,Object>> orderInfoList = (List<Map<String,Object>>)printInfo.get("order_info_list");
		if(!WorkerUtil.isNullOrEmpty(orderInfoList)){
			Integer shippingId = Integer.parseInt(orderInfoList.get(0).get("shipping_id").toString());
			model.put("print_info", orderInfoList);
			model.put("shipping_id", shippingId);
			model.put("shipping_name", shippingDao.selectByPrimaryKey(shippingId).getShipping_name());
			model.put("sum", orderInfoList.size());
			model.put("order_id", orderInfoList.get(0).get("order_id").toString());
			model.put("result", "success");
			model.put("note", "success");
		}else{
			model.put("result", "failure");
			model.put("note", "未找到快递单数据");
		}
		return model;
	}
	
	@RequestMapping(value="print")
	public String print(HttpServletRequest req, Map<String,Object> model){
		
		String orderId2Number = req.getParameter("orderId2Number");
		String arr [] = orderId2Number.split("_");
		String strOrderId = arr[0];
		String trackingNumber = arr[1];
		
		Integer orderId = 0;
		Map printInfo = null;
		try {
			if(!WorkerUtil.isNullOrEmpty(strOrderId)){
				orderId = Integer.parseInt(strOrderId);
			}
			printInfo = shipmentBiz.selectPrintInfo(orderId,trackingNumber);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			model.put("result", "failure");
			model.put("note", e.getMessage());
		}
		model.put("print_info", printInfo);
		model.put("result", "success");
		model.put("note", "success");
		
		List<Map<String,Object>> orderInfoList = (List<Map<String,Object>>)printInfo.get("order_info_list");
		String shippingCode = String.valueOf(orderInfoList.get(0).get("shipping_code"));
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
	 * 补打印补货单
	 */
	@RequestMapping(value="printReplenishment")
	public String printReplenishment(HttpServletRequest req){
		return "/exception/printReplenishment";
	}

	@RequestMapping(value="print_replenishment_V2")
	public String print_replenishment_V2(HttpServletRequest req, Map<String,Object> model){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		String batchTaskSn = req.getParameter("batch_task_sn");
		Map<String,Object> returnMap = new HashMap<String,Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			returnMap = replenishmentBiz.printReplenishmentTaskByBatchTaskSn(batchTaskSn.trim(),sysUser.getUsername(),sysUser.getId());
			model.put("printReplenishmentTaskList", (List<Map>)returnMap.get("printReplenishmentTaskList"));
			model.put("bhCode", batchTaskSn);
			model.put("printTime", sdf.format(new Date()));
			model.put("actionUser", sysUser.getUsername());
			model.put("result", Response.SUCCESS);
			model.put("note", "成功打印补货任务");
		} catch (Exception e) {
			logger.error("printReplenishment error:", e);
			model.put("result", Response.FAILURE);
			model.put("note", e.getMessage());
			model.put("bhCode", "");
			model.put("printTime", sdf.format(new Date()));
			model.put("actionUser", sysUser.getUsername());
			model.put("printReplenishmentTaskList",null);
		}
		return "/replenishment/print_replenishment";
	}
	
	

	/**
	 * @author dlyao
	 * @20160518
	 * 
	 */
	@RequestMapping(value="/printCountTask") 
	public String printCountTask( HttpServletRequest req , Map<String,Object> model ){
		logger.debug("exception   printCountTask start");
		return "/exception/printCountTask";    
	}
	
	/**
	 * @author dlyao
	 * @20160518
	 * 
	 */
	@RequestMapping(value="/doPrintCountTask") 
	public String doPrintCountTask( HttpServletRequest req , Map<String,Object> model ){
		logger.debug("exception   doPrintCountTask start");

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		int batchMark=0;
		String batch_task_sn = (String) req.getParameter("batch_task_sn");

		if(!batch_task_sn.equals("")){
			Pattern p=Pattern.compile("^[P][0-9]{12,16}[-][1-3][-][1-9][0-9]{0,3}$");
			if(!LocationBarcodeTools.checkString(batch_task_sn, p)){
				model.put("success", false);
				model.put("message", "请输入正确的盘点任务编号");
				return "/exception/printCountTask"; 
			}
			batchMark=Integer.parseInt(batch_task_sn.split("-")[1]);
		}
		model.put("batchMark", batchMark);
		
		String count_sn=countTaskDao.getCountSnByBatchTaskCount(batch_task_sn);
		if(com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(count_sn)){
			model.put("success", false);
			model.put("message", "输入的盘点任务编号有误");
			return "/exception/printCountTask"; 
		}
		
		int taskNum=countTaskDao.getTaskNumByCountSnMark(batchMark,count_sn);
		
		List<Map<String,Object>> batchStockTaskList = new ArrayList<Map<String,Object>>();
		Map<String,Object> batchStockTaskMap = new HashMap<String,Object>();
		List<Map<String,Object>> taskList = countTaskDao.searchCountSnForPrint(count_sn,physical_warehouse_id,batch_task_sn,batchMark);
		
		batchStockTaskMap.put("batchTaskSn",batch_task_sn);
		batchStockTaskMap.put("taskNum",taskNum);
		batchStockTaskMap.put("taskType",taskList.get(0).get("task_type"));
		batchStockTaskMap.put("taskList",taskList);
		batchStockTaskMap.put("mark", batchMark==1?"初盘":(batchMark==2?"复盘":"三盘"));
		batchStockTaskList.add(batchStockTaskMap);
		
		model.put("batchStockTaskList", batchStockTaskList);
		
		return "/countTask/printStockCheckTask";   
	}

	/**
	 * @author dlyao
	 * @20160815
	 * 查询波此单的商品和库存
	 */
	@RequestMapping(value="/queryGtGoods") 
	public String queryGtGoods( HttpServletRequest req , Map<String,Object> model ){
		logger.debug("exception   queryGtGoods start");
		return "/exception/queryGtGoods";    
	}
	
	
	/**
	 * @author dlyao
	 * @20160518
	 * 
	 */
	@RequestMapping(value="/doQueryGtGoods") 
	@ResponseBody
	public Map<String, Object> doQueryGtGoods( HttpServletRequest req){
		logger.debug("exception   doQueryGtGoods ");
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		
		
		String oms_order_sn =req.getParameter("oms_order_sn");
		int warehouse_id=orderInfoDao.getWarehouseIdBySn(oms_order_sn);
		List<Map> pickOrderGoodsSum =orderInfoDao.selectGtOrderGoodsListByOmsOrderSn(oms_order_sn);
		List<Integer> productIdList=new ArrayList<Integer>();
		
		
		for (Map m : pickOrderGoodsSum) {
			productIdList.add(Integer
					.parseInt(m.get("product_id").toString()));
		}

		if(WorkerUtil.isNullOrEmpty(productIdList)){
			
			List<ProductLocation> productLocationList =new ArrayList<ProductLocation>();
			List<Map> noPlOrderList=new ArrayList<Map>();
			resultMap.put("success", true);
			resultMap.put("message", "查询成功");
			resultMap.put("noPlOrderList", noPlOrderList);
			resultMap.put("pickOrderGoodsSum", pickOrderGoodsSum);
			resultMap.put("productLocationList", productLocationList);
			return resultMap;    
		}
		
		List<ProductLocation> productLocationList = productLocationDao.selectProductlocationListForGT(productIdList,physical_warehouse_id,warehouse_id);
		//全部数据
		Map<Integer,Integer> availableMap1=new HashMap<Integer,Integer>();
		//可分配区域数字
		Map<Integer,Integer> availableMap2=new HashMap<Integer,Integer>();

		

		Map<Integer,Integer> availableMap4=new HashMap<Integer,Integer>();//件
		Map<Integer,Integer> availableMap5=new HashMap<Integer,Integer>();//箱
		Map<Integer,Integer> availableMap6=new HashMap<Integer,Integer>();//存储
		Map<Integer,Integer> availableMap7=new HashMap<Integer,Integer>();//return
		Map<Integer,Integer> availableMap8=new HashMap<Integer,Integer>();//退货良品暂存
		
		Map<Integer,Integer> availableMap9=new HashMap<Integer,Integer>();//耗材
		Map<Integer,Integer> availableMap10=new HashMap<Integer,Integer>();//二手
		
		for(ProductLocation pl:productLocationList){
			if(null!=availableMap1.get(pl.getProduct_id())){
				availableMap1.put(pl.getProduct_id(), availableMap1.get(pl.getProduct_id())+pl.getQty_available());
			}else{
				availableMap1.put(pl.getProduct_id(),pl.getQty_available());
			}
			
			if(!pl.getLocation_type().equalsIgnoreCase("退货良品暂存区")){
				if(null!=availableMap2.get(pl.getProduct_id())){
					availableMap2.put(pl.getProduct_id(), availableMap2.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap2.put(pl.getProduct_id(),pl.getQty_available());
				}
			}
			
			if(pl.getLocation_type().equalsIgnoreCase("件拣货区")){
				if(null!=availableMap4.get(pl.getProduct_id())){
					availableMap4.put(pl.getProduct_id(), availableMap4.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap4.put(pl.getProduct_id(),pl.getQty_available());
				}
			}else if(pl.getLocation_type().equalsIgnoreCase("箱拣货区")){
				if(null!=availableMap5.get(pl.getProduct_id())){
					availableMap5.put(pl.getProduct_id(), availableMap5.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap5.put(pl.getProduct_id(),pl.getQty_available());
				}
			}else if(pl.getLocation_type().equalsIgnoreCase("存储区")){
				if(null!=availableMap6.get(pl.getProduct_id())){
					availableMap6.put(pl.getProduct_id(), availableMap6.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap6.put(pl.getProduct_id(),pl.getQty_available());
				}
			}else if(pl.getLocation_type().equalsIgnoreCase("退货区")){
				if(null!=availableMap7.get(pl.getProduct_id())){
					availableMap7.put(pl.getProduct_id(), availableMap7.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap7.put(pl.getProduct_id(),pl.getQty_available());
				}
			}else if(pl.getLocation_type().equalsIgnoreCase("耗材区")){
				if(null!=availableMap9.get(pl.getProduct_id())){
					availableMap9.put(pl.getProduct_id(), availableMap9.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap9.put(pl.getProduct_id(),pl.getQty_available());
				}
			}else if(pl.getLocation_type().equalsIgnoreCase("二手区")){
				if(null!=availableMap10.get(pl.getProduct_id())){
					availableMap10.put(pl.getProduct_id(), availableMap10.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap10.put(pl.getProduct_id(),pl.getQty_available());
				}
			}
			else{
				if(null!=availableMap8.get(pl.getProduct_id())){
					availableMap8.put(pl.getProduct_id(), availableMap8.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap8.put(pl.getProduct_id(),pl.getQty_available());
				}
			}
			
		}
		
		
		
		List<Integer> noPlProductIdList=new ArrayList<Integer>();
		
		for(Map m : pickOrderGoodsSum){
			Integer product_id=Integer.parseInt(m.get("product_id").toString());
			int goods_number=Integer.parseInt(m.get("goods_number").toString());
			int spec = null==m.get("spec").toString()?0:Integer.parseInt(m.get("spec").toString());
			int avaliable1=null==availableMap1.get(product_id)?0:availableMap1.get(product_id);
			int avaliable2=null==availableMap2.get(product_id)?0:availableMap2.get(product_id);
			
			int avaliable4=null==availableMap4.get(product_id)?0:availableMap4.get(product_id);
			int avaliable5=null==availableMap5.get(product_id)?0:availableMap5.get(product_id);
			int avaliable6=null==availableMap6.get(product_id)?0:availableMap6.get(product_id);
			int avaliable7=null==availableMap7.get(product_id)?0:availableMap7.get(product_id);
			int avaliable8=null==availableMap8.get(product_id)?0:availableMap8.get(product_id);
			
			int avaliable9=null==availableMap9.get(product_id)?0:availableMap9.get(product_id);
			int avaliable10=null==availableMap10.get(product_id)?0:availableMap10.get(product_id);
			
			m.put("avaliable4", avaliable4);
			m.put("avaliable5", avaliable5);
			m.put("avaliable6", avaliable6);
			m.put("avaliable7", avaliable7);
			m.put("avaliable8", avaliable8);
			m.put("avaliable9", avaliable9);
			m.put("avaliable10", avaliable10);
			
			if(spec==0){
				m.put("messageString", "此商品未维护箱规，请维护箱规");
				m.put("mark", 1);
			}
			else if(avaliable2>=goods_number){
				m.put("messageString", "去查看补货任务，如果有任务，请执行补货");
				//noPlProductIdList.add(product_id);
				m.put("mark", 1);
			}else if(avaliable1>=goods_number){
				m.put("messageString", "去退货良品暂存区找找，如果发现库存，请执行移库");
				//noPlProductIdList.add(product_id);
				m.put("mark", 2);
			}else{
				m.put("messageString", "去本页面右下面的表格查看有无取消订单商品尚未上架，如果有记录，请执行取消订单重新上架");
				noPlProductIdList.add(product_id);
				m.put("mark", 3);
			}
		}
		List<Map> noPlOrderList=new ArrayList<Map>();
		if(!WorkerUtil.isNullOrEmpty(noPlProductIdList)){
			noPlOrderList=orderInfoDao.getNoPlOrderListByProductIdList(noPlProductIdList,physical_warehouse_id,warehouse_id);
		}
		
		resultMap.put("success", true);
		resultMap.put("message", "查询成功");
		resultMap.put("pickOrderGoodsSum", pickOrderGoodsSum);
		resultMap.put("noPlOrderList", noPlOrderList);
		resultMap.put("productLocationList", productLocationList);
		return resultMap;    
	}
	
	
	/**
	 * @author dlyao
	 * @20160815
	 * 查询波此单的商品和库存
	 */
	@RequestMapping(value="/queryGoods") 
	public String queryGoods( HttpServletRequest req , Map<String,Object> model ){
		logger.debug("exception   queryGoods start");
		return "/exception/queryGoods";    
	}
	
	
	
	/**
	 * @author dlyao
	 * @20160518
	 * 
	 */
	@RequestMapping(value="/doQueryGoods") 
	@ResponseBody
	public Map<String, Object> doQueryGoods( HttpServletRequest req){
		logger.debug("exception   doQueryGoods ");
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		String batch_pick_sn =req.getParameter("batch_pick_sn");
		
		String reservedStatus = batchPickDao.selectReservedStatusBySn(batch_pick_sn);
		int warehouse_id = batchPickDao.selectWarehouseIdBySn(batch_pick_sn);
		
		List<Map> pickOrderGoodsSum =orderBiz.doQueryGoods(batch_pick_sn);
		List<Integer> productIdList=new ArrayList<Integer>();
		
		
		for (Map m : pickOrderGoodsSum) {
			productIdList.add(Integer
					.parseInt(m.get("product_id").toString()));
		}

		if(WorkerUtil.isNullOrEmpty(productIdList)){
			
			List<ProductLocation> productLocationList =new ArrayList<ProductLocation>();
			List<Map> noPlOrderList=new ArrayList<Map>();
			resultMap.put("success", true);
			resultMap.put("message", "查询成功");
			resultMap.put("noPlOrderList", noPlOrderList);
			resultMap.put("pickOrderGoodsSum", pickOrderGoodsSum);
			resultMap.put("productLocationList", productLocationList);
			return resultMap;    
		}
		
		List<ProductLocation> productLocationList = productLocationDao.selectProductlocationListForBatchPick(productIdList,physical_warehouse_id,warehouse_id);
		//全部数据
		Map<Integer,Integer> availableMap1=new HashMap<Integer,Integer>();
		//可分配区域数字
		Map<Integer,Integer> availableMap2=new HashMap<Integer,Integer>();
		//件拣和return区
		Map<Integer,Integer> availableMap3=new HashMap<Integer,Integer>();
		
		Map<Integer,Integer> availableMap4=new HashMap<Integer,Integer>();//件
		Map<Integer,Integer> availableMap5=new HashMap<Integer,Integer>();//箱
		Map<Integer,Integer> availableMap6=new HashMap<Integer,Integer>();//存储
		Map<Integer,Integer> availableMap7=new HashMap<Integer,Integer>();//return
		Map<Integer,Integer> availableMap8=new HashMap<Integer,Integer>();//退货良品暂存
		
		//取消订单
		List<Integer> cancelOrderIdList=orderProcessDao.getAllCancelOrderByBatchPickSn(batch_pick_sn);

		resultMap.put("cancelOrderIdList", cancelOrderIdList);

		
		for(ProductLocation pl:productLocationList){
			if(null!=availableMap1.get(pl.getProduct_id())){
				availableMap1.put(pl.getProduct_id(), availableMap1.get(pl.getProduct_id())+pl.getQty_available());
			}else{
				availableMap1.put(pl.getProduct_id(),pl.getQty_available());
			}
			
			if(!pl.getLocation_type().equalsIgnoreCase("退货良品暂存区")){
				if(null!=availableMap2.get(pl.getProduct_id())){
					availableMap2.put(pl.getProduct_id(), availableMap2.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap2.put(pl.getProduct_id(),pl.getQty_available());
				}
			}
			

			if(pl.getLocation_type().equalsIgnoreCase("件拣货区")||pl.getLocation_type().equalsIgnoreCase("退货区")){
				if(null!=availableMap3.get(pl.getProduct_id())){
					availableMap3.put(pl.getProduct_id(), availableMap3.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap3.put(pl.getProduct_id(),pl.getQty_available());
				}
			}
			
			if(pl.getLocation_type().equalsIgnoreCase("件拣货区")){
				if(null!=availableMap4.get(pl.getProduct_id())){
					availableMap4.put(pl.getProduct_id(), availableMap4.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap4.put(pl.getProduct_id(),pl.getQty_available());
				}
			}else if(pl.getLocation_type().equalsIgnoreCase("箱拣货区")){
				if(null!=availableMap5.get(pl.getProduct_id())){
					availableMap5.put(pl.getProduct_id(), availableMap5.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap5.put(pl.getProduct_id(),pl.getQty_available());
				}
			}else if(pl.getLocation_type().equalsIgnoreCase("存储区")){
				if(null!=availableMap6.get(pl.getProduct_id())){
					availableMap6.put(pl.getProduct_id(), availableMap6.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap6.put(pl.getProduct_id(),pl.getQty_available());
				}
			}else if(pl.getLocation_type().equalsIgnoreCase("退货区")){
				if(null!=availableMap7.get(pl.getProduct_id())){
					availableMap7.put(pl.getProduct_id(), availableMap7.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap7.put(pl.getProduct_id(),pl.getQty_available());
				}
			}else{
				if(null!=availableMap8.get(pl.getProduct_id())){
					availableMap8.put(pl.getProduct_id(), availableMap8.get(pl.getProduct_id())+pl.getQty_available());
				}else{
					availableMap8.put(pl.getProduct_id(),pl.getQty_available());
				}
			}
		}
		
		List<Integer> noPlProductIdList=new ArrayList<Integer>();
		
		for(Map m : pickOrderGoodsSum){
			Integer product_id=Integer.parseInt(m.get("product_id").toString());
			int goods_number=Integer.parseInt(m.get("goods_number").toString());
			int spec = null==m.get("spec").toString()?0:Integer.parseInt(m.get("spec").toString());
			int avaliable1=null==availableMap1.get(product_id)?0:availableMap1.get(product_id);
			int avaliable2=null==availableMap2.get(product_id)?0:availableMap2.get(product_id);
			int avaliable3=null==availableMap3.get(product_id)?0:availableMap3.get(product_id);
			
			int avaliable4=null==availableMap4.get(product_id)?0:availableMap4.get(product_id);
			int avaliable5=null==availableMap5.get(product_id)?0:availableMap5.get(product_id);
			int avaliable6=null==availableMap6.get(product_id)?0:availableMap6.get(product_id);
			int avaliable7=null==availableMap7.get(product_id)?0:availableMap7.get(product_id);
			int avaliable8=null==availableMap8.get(product_id)?0:availableMap8.get(product_id);
			
			m.put("avaliable4", avaliable4);
			m.put("avaliable5", avaliable5);
			m.put("avaliable6", avaliable6);
			m.put("avaliable7", avaliable7);
			m.put("avaliable8", avaliable8);
			
			if(spec==0){
				m.put("messageString", "此商品未维护箱规，请维护箱规");
				m.put("mark", 1);
			}
			else if(avaliable3>=goods_number){
				m.put("messageString", "可以分配");
				//noPlProductIdList.add(product_id);
				m.put("mark", 1);
			}
			else if(avaliable2>=goods_number){
				m.put("messageString", "去查看补货任务，如果有任务，请执行补货");
				//noPlProductIdList.add(product_id);
				m.put("mark", 1);
			}else if(avaliable1>=goods_number){
				m.put("messageString", "去退货良品暂存区找找，如果发现库存，请执行移库");
				//noPlProductIdList.add(product_id);
				m.put("mark", 2);
			}else{
				m.put("messageString", "去本页面最右下的表格查看有无取消订单商品尚未上架，如果有记录，请执行取消订单重新上架");
				noPlProductIdList.add(product_id);
				m.put("mark", 3);
			}
		}
		
		List<Map> noPlOrderList=new ArrayList<Map>();
		if(!WorkerUtil.isNullOrEmpty(noPlProductIdList)){
			noPlOrderList=orderInfoDao.getNoPlOrderListByProductIdList(noPlProductIdList,physical_warehouse_id,warehouse_id);
		}
		
		
		resultMap.put("success", true);
		
		resultMap.put("message", "查询成功");
		resultMap.put("reservedStatus", reservedStatus);
		resultMap.put("pickOrderGoodsSum", pickOrderGoodsSum);
		resultMap.put("noPlOrderList", noPlOrderList);
		resultMap.put("productLocationList", productLocationList);
		return resultMap;    
	}
	
	
	/**
	 * @author dlyao
	 * @20160518
	 * 
	 */
	@RequiresPermissions("sys:work:kickout")
	@RequestMapping(value="/doKickOut") 
	@ResponseBody
	public Map<String, Object> doKickOut( HttpServletRequest req){
		logger.debug("exception   doKickOut satrt ");
				
		return orderBiz.doKickOut(req);
	}

	
	
	/**
	 * @author dlyao
	 * @20160921
	 * 补打印采购收货标签
	 */
	@RequestMapping(value="/batchKickOut") 
	public String batchKickOut( HttpServletRequest req , Map<String,Object> model ){
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
		// 返回物理仓库
		Warehouse warehouse = (Warehouse) session
						.getAttribute("currentPhysicalWarehouse");
		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
						+ "";
		List<ConfigCwMapping> list=  configCwMappingDao.getConfigCwMappingListByPhysicalWarehouseId(physical_warehouse_id);
		
		model.put("list", list);
		model.put("customers", customers);
		return "/exception/batchKickOut";    
	}
	
	
	
	/**
	 * @author dlyao
	 * @20160921
	 * 补打印采购收货标签
	 */
	@RequestMapping(value="/doBatchKickOut") 
	@ResponseBody
	public Object doBatchKickOut( HttpServletRequest req){
		
		Map<String,Object> model=new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
		
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		String userName=sysUser.getUsername();
		
		String batch_pick_ids=req.getParameter("batch_pick_ids");
		List<String> batchPickIdStringList=Tools.changeStringToList(batch_pick_ids, 0);
		
		List<Integer> batchPickIdList=Tools.changeStringListToIntegerList(batchPickIdStringList);
		
		if(!WorkerUtil.isNullOrEmpty(batchPickIdList)){
			for(Integer bpId:batchPickIdList){
				orderBiz.doBatchKickOut(bpId,physical_warehouse_id,customers,userName);
			}
		}
		
		
		int customer_id=Integer.parseInt(req.getParameter("customer_id"));
		List<BatchPick> list = batchPickDao.selectAllEBatchPickByCustomerId(physical_warehouse_id,customer_id);
		
		model.put("customers", customers);
		model.put("list", list);
		return model;    
	}
	
	/**
	 * @author dlyao
	 * @20160921
	 * 补打印采购收货标签
	 */
	@RequestMapping(value="/getAllBatchPickSn") 
	@ResponseBody
	public Object getAllBatchPickSn( HttpServletRequest req){
		
		Map<String,Object> model=new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
		
		
		int customer_id=Integer.parseInt(req.getParameter("customer_id"));
		
		List<BatchPick> list = batchPickDao.selectAllEBatchPickByCustomerId(physical_warehouse_id,customer_id);
		
		model.put("customers", customers);
		model.put("list", list);
		return model;    
	}
	
	/**
	 * @author hzhang1
	 * @20160818
	 * 补打印采购收货标签
	 */
	@RequestMapping(value="/printPurchaseLable") 
	public String printPurchaseLable( HttpServletRequest req , Map<String,Object> model ){
		return "/exception/printPurchaseLable";    
	}
	

	/**
	 * @author hzhang1
	 * @20160818
	 * 补打印采购收货标签
	 */
	@RequestMapping(value="/purchaseLable") 
	public String purchaseLable( HttpServletRequest req , Map<String,Object> model ){
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		List<Map> tagList = new ArrayList<Map>();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physicalWarehouseId = warehouse.getPhysical_warehouse_id();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		String purchaseLable = req.getParameter("purchase_lable");
		Map<String,Object> resMap = inventoryBiz.getPurchaseLableInfo(purchaseLable, physicalWarehouseId);
		if(WorkerUtil.isNullOrEmpty(resMap)){
			return "";
		}
		resMap.put("action_user", sysUser.getRealname());
		String statusId = resMap.get("status_id").toString();
		if(statusId.equals("NORMAL")){
			resMap.put("normal_number", resMap.get("quantity"));
			resMap.put("defective_number", "");
		}else{
			resMap.put("normal_number", "");
			resMap.put("defective_number", resMap.get("quantity"));
		}
		tagList.add(resMap);
		model.put("tagList", tagList);
		return "/purchase/print_tag";    
	}
	
	/**
	 * @author dlyao
	 * @20160921
	 * 手动触发盘点任务
	 */
	@RequestMapping(value="/taskCountByHand") 
	public String taskCountByHand( HttpServletRequest req , Map<String,Object> model ){
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = (List<WarehouseCustomer>) session
				.getAttribute("userCustomers");
		
		
				
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
		
		model.put("list", list);
		model.put("warehouseList", warehouseList);
		model.put("customers", customers);
		return "/exception/taskCountByHand";    
	}
	
	/**
	 * @author dlyao
	 * @20160921
	 * 手动触发盘点任务
	 */
	@RequestMapping(value="/doTaskCountByHand") 
	@ResponseBody
	public Object doTaskCountByHand( HttpServletRequest req){
		
		Map<String,Object> model=new HashMap<String,Object>();
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		
		SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		String userName=sysUser.getUsername();
		
		String barcode = req.getParameter("barcode");
		
		String location_barcode=req.getParameter("location_barcode").replaceAll("-", "");
		Integer warehouse_id=Integer.parseInt(req.getParameter("warehouse_id").toString());
		
		String status=req.getParameter("status");
		
		Integer customer_id= Integer.parseInt(req.getParameter("customer_id"));
		
		int num_real = Integer.parseInt(req.getParameter("num_real"));
		
		String count_sn=com.leqee.wms.util.WorkerUtil.generatorSequence(
				SequenceUtil.KEY_NAME_PDCODE, "P", true);
		
		model=countTaskBiz.doTaskCountByHand(physical_warehouse_id,warehouse_id,barcode,location_barcode,customer_id,num_real,count_sn,userName,status);
		
		return model;    
	}
	
	
	/**
	 * 打印波次单库存不够转移库位库存
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/pickTransferProductLocation")
	public String pickTransferProductLocation(HttpServletRequest req , Map<String,Object> model){
		return "/exception/pickTransferProductLocation";
	}
	
	
	@RequestMapping(value="/showTransferProductLocation")
	@ResponseBody
	public Object showTransferProductLocation(HttpServletRequest req){
		String batchPickSn = req.getParameter("batch_pick_sn");
		Map<String,Object> resMap = new HashMap<String,Object>();
		List<Map> pickGoodsList = orderBiz.selectPickProductLocationByBatchPickSn(batchPickSn);
		List<Map> pickOrderList = orderBiz.selectPickOrdersByBatchPickSn(batchPickSn);
		resMap.put("pickGoodsList", pickGoodsList);
		resMap.put("pickOrderList", pickOrderList);
		return resMap;
	}
	
	
	@RequestMapping(value="/transferProductLocation")
	@ResponseBody
	public Object transferProductLocation(HttpServletRequest req){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		Integer taskId = Integer.parseInt(req.getParameter("task_id"));
		Integer customerId = Integer.parseInt(req.getParameter("customer_id"));
		Integer goodsNumber = Integer.parseInt(req.getParameter("goods_number"));
		Integer plId = Integer.parseInt(req.getParameter("pl_id"));
		Integer newPlId = Integer.parseInt(req.getParameter("new_pl_id"));
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		
		try {
			resMap = orderBiz.updatePickProductLocationByBatchPickSn(taskId, goodsNumber, plId, newPlId,customerId,warehouse.getPhysical_warehouse_id());
		} catch (Exception e) {
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "更新失败"+e.getMessage());
			e.printStackTrace();
		}
		return resMap;
	}
	
	@RequestMapping(value="/deleteOrder")
	@ResponseBody
	public Object deleteOrder(HttpServletRequest req){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		Integer orderId = Integer.parseInt(req.getParameter("order_id"));
		Integer batchPickId = Integer.parseInt(req.getParameter("batch_pick_id"));
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		
		try {
			resMap = orderBiz.deleteOrderByBatchPickSn(orderId, batchPickId,warehouse.getPhysical_warehouse_id());
		} catch (Exception e) {
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "更新失败"+e.getMessage());
			e.printStackTrace();
		}
		return resMap;
	}
	
	

	/**
	 * hchen1
	 * 补打加工单页面
	 */
	@RequestMapping(value="/printComboAndTask") 
	public String printComboAndTask( HttpServletRequest req , Map<String,Object> model ){
		//1、获取客户端参数
		String username =  req.getParameter("username") ;
		String msg =  req.getParameter("msg") ;
		
		//2、打印日志
		logger.info(username);
		model.put("username", username );    
		model.put("msg", msg );    
		
		return "/exception/printComboAndTask";    
	}
	
	
	/**
	 * @author hchen
	 * @CreatedDate 2016.03.14
	 * 补打加工任务单上架标签
	 */
	@RequestMapping(value="/printlocation_barcode")
	public String printLocationBarcode(HttpServletRequest req , Map<String,Object> model){
		
		String order_sn =req.getParameter("order_sn");
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        // 1.获取当前用户及当前货主
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String, Object>> orderPrepackMapList = new ArrayList<Map<String,Object>>();
        Map<String, Object> orderPrepack = orderPrepackDao.selectOrderPrepackByOrderSnV3(order_sn);
        if(OrderPrepack.ORDER_PREPACK_TYPE.equalsIgnoreCase(orderPrepack.get("type").toString())){
        	 orderPrepackMapList = orderPrepackDao.selectOrderPrepackByOrderSnV1(order_sn,Integer.parseInt(orderPrepack.get("order_id").toString()));
        }else {
        	orderPrepackMapList = orderPrepackDao.selectOrderPrepackByOrderSnV2(order_sn);
		}
        if(WorkerUtil.isNullOrEmpty(orderPrepackMapList)){
        	model.put("result", "failure");
    		model.put("note", "不存在的加工任务单号");
        }else{
        	model.put("result", "success");
        	model.put("orderPrepackMapList", orderPrepackMapList);
//        	model.put("location_barcode", orderPrepackMap.get("location_barcode"));
//    		model.put("barcode", orderPrepackMap.get("barcode"));
//    		model.put("product_name", orderPrepackMap.get("product_name"));
//    		model.put("total_number", orderPrepackMap.get("qty_actual"));
    		model.put("create_time", sdf.format(new Date()));
    		model.put("username", sysUser.getUsername());
        }
		
		
		return  "/sale/groundingPrintNew";
	}
	
	
	
	/**
	 * @author hchen
	 * @CreatedDate 2016.03.14
	 * 套餐条码补打
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
			if(WorkerUtil.isNullOrEmpty(barcodeName)){
				model.put("error", "不存在的商品条码");
			}else{
			model.put("success", Response.OK);
			model.put("comboBarcode", comboBarcode);
			model.put("barcodeName", barcodeName);
			model.put("preNumber", preNumber);
			}
		}
		
		
		return "/sale/comboBarcode";
	}
}