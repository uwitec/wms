package com.leqee.wms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.leqee.wms.biz.BatchPickBiz;
import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.OrderPrepackBiz;
import com.leqee.wms.biz.SuperBiz;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.ShippingYundaTrackingNumberDao;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.LockUtil;
import com.leqee.wms.util.ViewExcel;
import com.leqee.wms.util.WorkerUtil;


@Controller
@RequestMapping(value="/super")  //指定根路径
public class SuperController  {

	
	private Logger logger = Logger.getLogger(SaleController.class);

	@Autowired
	OrderBiz orderBiz;
	@Autowired
	InventoryBiz inventoryBiz;
	@Autowired
	BatchPickBiz batchPickBiz;
	@Autowired
	SuperBiz superBiz;
	@Autowired
	ShipmentDao shipmentDao;
	@Autowired
	ShippingYundaTrackingNumberDao shippingYundaTrackingNumberDao;
	@Autowired 
	OrderPrepackBiz orderPrepackBiz;
	
	/**
	 * 超级复核
	 */
	@RequestMapping(value="/superRecheck") 
	public String superRecheck( HttpServletRequest req , Map<String,Object> model ){
		return "/super/recheck";    
	}
	
	/**
	 * （超级复核）扫描订单号获取发货单张数
	 */
	@RequestMapping(value="/orderDispatchNum")
	@ResponseBody
	public Map<String, Object> orderDispatchNum(HttpServletRequest req){
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
				Integer orderPage = superBiz.getOrderDispatchNum(Integer.parseInt(orderId));
				resultMap.put("result", Response.SUCCESS);
				resultMap.put("number", orderPage);
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
	 * (超级复核)加载商品绑定信息
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
				resultMap = superBiz.loadGoodsForOrder(Integer.parseInt(orderId));
			}catch(NumberFormatException e){
				logger.info("（超级复核）加载商品发生异常，异常信息:"+e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（超级复核）加载商品错误："+ "扫描订单号并非数字格式");
			}catch(Exception e){
				logger.info("（超级复核）加载商品错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（超级复核）加载商品错误："+ e.getMessage());
			}finally{
				// 释放锁
				lock.unlock();
			}	
		}
		return resultMap;
	}
	
	/**
	 * (超级复核)绑定耗材
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
				resultMap = superBiz.bindConsume(Integer.parseInt(shipmentId),barcode,Integer.parseInt(orderId));
			}catch(NumberFormatException e){
				logger.error("（超级复核）绑定耗材发生异常，异常信息:" + e.getMessage() );
				resultMap.put("result",Response.FAILURE);      
				resultMap.put("note","绑定耗材错误："+"输入信息并非数字类型，请检查！");
			}catch(Exception e){
				logger.error("（超级复核）绑定耗材发生异常，异常信息:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);      
				resultMap.put("note","绑定耗材错误："+e.getMessage());
			}	
		}
		
		return resultMap;
	}
	

	/**
	 * (超级复核)打印快递面单
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
		Map printInfo = superBiz.selectPrintInfo(orderId,"");
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
	 * 面单线下维护
	 */
	@RequestMapping(value="/addOfflineTNS") 
	public String addOfflineTNS( HttpServletRequest req , Map<String,Object> model ){
		return "/super/addOfflineTNS";    
	}
	
	/**
	 * 面单线下维护（from-to）
	 */
	@RequestMapping(value="/addFromToTN")
	@ResponseBody
	public Map<String, Object> addFromToTN(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 获取前端传递的参数
		String physical_warehouse_id = req.getParameter("physical_warehouse_id");
		String shipping_code = req.getParameter("shipping_code");
		String from_tracking_number = req.getParameter("from_tracking_number");
		String to_tracking_number = req.getParameter("to_tracking_number");
		if(WorkerUtil.isNullOrEmpty(physical_warehouse_id) || 
				WorkerUtil.isNullOrEmpty(shipping_code) || 
				WorkerUtil.isNullOrEmpty(from_tracking_number) || 
				WorkerUtil.isNullOrEmpty(to_tracking_number)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "所有值均要填写");
			return resultMap;
		}else{
			try{
				Integer line = 
						superBiz.addFromToTN(Integer.parseInt(physical_warehouse_id),shipping_code,from_tracking_number,to_tracking_number);
				resultMap.put("result", Response.SUCCESS);
				resultMap.put("note", "成功插入数据"+line+"条！");
			}catch(Exception e){
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "线下维护面单错误信息："+ e.getMessage());
			}
		}
		return resultMap;
	}
	
	/**
	 * 面单线下导出
	 */
	@RequestMapping(value="exportTNsForBack")
	public ModelAndView exportTNsForBack(HttpServletRequest req , Map<String,Object> model){
		 ViewExcel viewExcel = new ViewExcel();    
		 String physical_warehouse_id = req.getParameter("physical_warehouse_id");
		 String shipping_code = req.getParameter("shipping_code");
		 logger.info("exportTNsForBack : "+physical_warehouse_id + " _ " +shipping_code);
		 try{
			 List<Map> list = superBiz.exportTNsForBack(Integer.parseInt(physical_warehouse_id),shipping_code);
			 model.put("list", list);
		 }catch(Exception e){
			 logger.info("exportTNsForBack_message:"+e.getMessage());
			 model.put("message", e.getMessage());
		 }
		 model.put("type", "exportTNsForBack");
	     return new ModelAndView(viewExcel, model);  
	}
	
}