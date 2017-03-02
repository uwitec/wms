package com.leqee.wms.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.biz.BatchPickBiz;
import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.ShipmentBiz;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.ShippingWarehouseMappingDao;
import com.leqee.wms.dao.ShippingYundaTrackingNumberDao;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.LockUtil;
import com.leqee.wms.util.WorkerUtil;


@Controller
@RequestMapping(value="/batchShipment")  //指定根路径
public class BatchShipmentController  {

	
	private Logger logger = Logger.getLogger(BatchShipmentController.class);

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
	OrderProcessDao orderProcessDao;
	@Autowired
	ShippingYundaTrackingNumberDao shippingYundaTrackingNumberDao;
	@Autowired
	ShippingWarehouseMappingDao shippingWarehouseMappingDao;
	
	/**
	 * 批量复核
	 */ 
	@RequestMapping(value="/batchRecheck") 
	public String batchRecheck( HttpServletRequest req , Map<String,Object> model ){
		return "/batchShipment/batchRecheck";    
	}
	
	/**
	 * 批量称重
	 */
	@RequestMapping(value="/batchWeigh") 
	public String batchWeigh( HttpServletRequest req , Map<String,Object> model ){
		
		return "/batchShipment/batchWeigh";    
	}
	
	// 批量称重(检查波次单号)
	@RequestMapping(value="checkBatchPickSn")
	@ResponseBody
	public Object checkBatchPickSn(HttpServletRequest req ){
		
		String batch_pick_sn = req.getParameter("batch_pick_sn").trim();
		Map<String,Object> resMap = shipmentBiz.getBatchPickIsRechecked(batch_pick_sn);
		return resMap;
		
	}
	
	// 批量称重(检查快递单号)
	@RequestMapping(value="checkBatchTrackingNumber")
	@ResponseBody
	public Object checkBatchTrackingNumber(HttpServletRequest req ){
		
		String batch_pick_sn = req.getParameter("batch_pick_sn").trim();
		String tracking_number = req.getParameter("tracking_number").trim();
		Map<String,Object> resMap = shipmentBiz.checkBatchTrackingNumber(batch_pick_sn,tracking_number);
		return resMap;
		
	}
	
	// 批量称重(批量更新同一波次包裹重量)
	@RequestMapping(value="updateBatchWeight")
	@ResponseBody
	public Object updateBatchWeight(HttpServletRequest req ){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		String batch_pick_sn = req.getParameter("batch_pick_sn").trim();
		String tracking_number = req.getParameter("tracking_number").trim();
		String weight = req.getParameter("weight").trim();
		Map<String, Object> resMap = new HashMap<String,Object>();
		try {
			resMap = shipmentBiz.updateBatchWeight(batch_pick_sn, tracking_number, weight,sysUser.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
			resMap.put("result", "failure");
			resMap.put("note", "批量称重更新失败！");
			resMap.put("info", "批量称重更新失败！");
		}
		return resMap;
	}
	
	/**
	 * 自动绑定码托
	 */
	@RequestMapping(value="/batchBindMT") 
	public String batchBindMT( HttpServletRequest req , Map<String,Object> model ){
		//1、获取客户端参数
		String username =  req.getParameter("username") ;
		String msg =  req.getParameter("msg") ;
		
		//2、打印日志
		logger.info(username);
		model.put("username", username );    
		model.put("msg", msg );    
		
		return "/batchShipment/batchBindMT";    
	}
	
	/**
	 * (批量复核)加载商品
	 * */
	@RequestMapping(value="/loadBatchPickSn")
	@ResponseBody
	public Map<String, Object> loadBatchPickSn(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String batchPickSn = req.getParameter("batch_pick_sn");
		if(WorkerUtil.isNullOrEmpty(batchPickSn)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "（批量复核）没有接收到波次单号");
			return resultMap;
		}else{
			ReentrantLock lock = LockUtil.getReentrantLock("loadBatchPickSn_" + batchPickSn);
			lock.lock();
			try{
				//用于杂单波次走批量单流程
				//于16年10月10日作废 -- 杂单波次不允许转回批量波次
//				shipmentBiz.checkBatchPickForRecheck(batchPickSn); //如果不正常，则 扔异常
				
				//16年12月21日增加：批量波次中若存在需要扫描批次号，需要根据配置判断可操作性
				shipmentBiz.checkRecheckBatchForPickSn(batchPickSn);
				
				//16年10月10日增加 ： batch_pick状态为SHIPMENT_Y|BATCH_RECHECK 才可操作
				shipmentBiz.checkBatchPickFlowStatus(batchPickSn,"BATCH_RECHECK");
				
				//新增加载快递方式总类
				List<String> shipList = orderProcessDao.getShippingList(batchPickSn);
				Integer shipNumber =shipList.size();
				
				//1. 查询波次单包含所有订单 需要 申请面单订单信息 ， 最新发货单数量
				Map<String,Object> orderWaitApplyTnMap = shipmentBiz.getAllOrderShipmentInfoByBatchOrderSn(batchPickSn);
				Integer maxShipmentCount = Integer.parseInt(String.valueOf(orderWaitApplyTnMap.get("max_shipment_count")));
				Integer batchOrderNum = Integer.parseInt(String.valueOf(orderWaitApplyTnMap.get("batch_order_num")));
				List<Integer> orderIdList = (List<Integer>)orderWaitApplyTnMap.get("order_id_list");
				
				if(!WorkerUtil.isNullOrEmpty(orderIdList)){
					List<String> errorOrderInfo = new ArrayList<String>();
					//add cancel order apply					
					List<Integer> cancelOrderIdList = orderProcessDao.getAllCancelOrderByList(orderIdList); // 取消订单
					if(!WorkerUtil.isNullOrEmpty(cancelOrderIdList)){
						// 每个订单分别createShipment + addTrackingNumber
						for (Integer orderId : cancelOrderIdList) {
							try{
								shipmentBiz.applyShipmentTnForCancelOrder(orderId);
							}catch (RuntimeException e) {
								errorOrderInfo.add(e.getMessage());
							}
						}
					}
					
					List<String> shippingCodeBatch = shippingWarehouseMappingDao.searchShippingCodeBatch();
	    			if(!WorkerUtil.isNullOrEmpty(shippingCodeBatch)){
		    			for (String shippingCode : shippingCodeBatch) {
		    				List<Map<String,Object>> ztoShippingAppOrders = orderBiz.getShippingAppOrders(orderIdList,shippingCode);
							if(!WorkerUtil.isNullOrEmpty(ztoShippingAppOrders)){
								try{
									shipmentBiz.batchApplyShipmentTn(ztoShippingAppOrders,shippingCode);
								}catch (Exception e) {
									errorOrderInfo.add(e.getMessage());
								}
							}
						}
	    			}
					List<Integer> otherOrderIds = orderBiz.getSingleOrderIdByOrderIds(orderIdList); // 其他快递
					logger.info("begin other:"+otherOrderIds.size());
					if(!WorkerUtil.isNullOrEmpty(otherOrderIds)){
						// 每个订单分别createShipment + addTrackingNumber
						for (Integer orderId : otherOrderIds) {
							try{
								Map<String,Object> result = shipmentBiz.applyShipmentTn(orderId,"BATCH");
								if(Response.FAILURE.equals(String.valueOf(result.get("result")))){
									errorOrderInfo.add(String.valueOf(result.get("note")));
								}
							}catch (RuntimeException e) {
								errorOrderInfo.add(e.getMessage());
							}
						}
					}
					logger.info("end other:"+otherOrderIds.size());
					if(errorOrderInfo.size()>0){
						String errorStr = "";
						for (String string : errorOrderInfo) {
							errorStr = errorStr + string;
						}
						resultMap.put("result", Response.FAILURE);
						resultMap.put("note", errorStr);
						return resultMap;
					}
				}
				resultMap = shipmentBiz.loadOrderGoodsForBatchPick(batchPickSn);
				resultMap.put("batch_order_num", batchOrderNum);
				resultMap.put("max_shipment_count", maxShipmentCount);
				resultMap.put("batch_pick_sn", batchPickSn);
				resultMap.put("shipNumber", shipNumber);
				resultMap.put("shipList", shipList);
			}catch(Exception e){
				logger.info("（批量复核）加载商品发生错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（批量复核）加载商品发生错误："+ e.getMessage());
			}
			finally{
				// 释放锁
				lock.unlock();
			}
		}
		return resultMap;
	}
	
	
	/**
	 * (批量复核)商品扫描 Ajax调用
	 * @param batch_pick_id
	 * @param order_goods_id
	 * @param max_shipment_count
	 * */
	@RequestMapping(value="/batchGoodScan")
	@ResponseBody
	public Map<String, Object> goodScan(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		Integer batchPickId = WorkerUtil.isNullOrEmpty(req.getParameter("batch_pick_id")) ? null : Integer.valueOf(req.getParameter("batch_pick_id"));
		Integer orderGoodsId = WorkerUtil.isNullOrEmpty(req.getParameter("order_goods_id")) ? null : Integer.valueOf(req.getParameter("order_goods_id"));
		Integer maxShipmentCount = WorkerUtil.isNullOrEmpty(req.getParameter("max_shipment_count")) ? null : Integer.valueOf(req.getParameter("max_shipment_count"));
		
		try {
			resultMap = orderBiz.getBatchSaleOrderGoodsScanResult(batchPickId, orderGoodsId, maxShipmentCount);
		} catch (Exception e) {
			logger.error("（批量复核）商品条码扫描失败!错误:" + e.getMessage());
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "（批量复核）商品条码扫描失败!错误:" + e.getMessage() );
		}
		
		return resultMap;
	}
	
	
	/**
	 * (批量复核)绑定耗材
	 * */
	@RequestMapping(value="/batchBindConsume")
	@ResponseBody
	public Map<String, Object> bindConsume(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		Integer batchPickId = WorkerUtil.isNullOrEmpty(req.getParameter("batch_pick_id")) ? null : Integer.valueOf(req.getParameter("batch_pick_id"));
		String barcode = WorkerUtil.isNullOrEmpty(req.getParameter("barcode")) ? null : req.getParameter("barcode");
		Integer maxShipmentCount = WorkerUtil.isNullOrEmpty(req.getParameter("max_shipment_count")) ? null : Integer.valueOf(req.getParameter("max_shipment_count"));
		
		if(WorkerUtil.isNullOrEmpty(batchPickId) || WorkerUtil.isNullOrEmpty(barcode) || WorkerUtil.isNullOrEmpty(maxShipmentCount)){
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "波次号，耗材编码 未成功获取");
			return resultMap;
		}else{
			try{
				resultMap = shipmentBiz.batchBindConsume(batchPickId,maxShipmentCount,barcode);
			}catch(Exception e){
				logger.error("（批量复核）绑定耗材发生异常，异常信息:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);      
				resultMap.put("note","绑定耗材发生异常，错误信息："+e.getMessage());
			}	
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
		String batchPickSn = req.getParameter("batch_pick_sn");
		if(WorkerUtil.isNullOrEmpty(batchPickSn)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "没有接收到清空信息！");
			return resultMap;
		}else if(!WorkerUtil.isNullOrEmpty(batchPickSn)){// 输入 波次单号
			try{
				resultMap = shipmentBiz.cleanBatchPickOrderTns(batchPickSn);
			}catch(Exception e){
				logger.info("（复核）撤销复核失败！错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（复核）撤销复核失败！错误："+ e.getMessage());
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
	 * 根据波次号 批量打印
	 */
	@RequestMapping(value="batchPrint")
	public String batchPrint(HttpServletRequest req , Map<String,Object> model){
		
		String batchPickId =req.getParameter("batch_pick_id");
		Integer startNum = 1;
		Integer endNum = 0;
		if(WorkerUtil.isNullOrEmpty(batchPickId)){
			return "failure";
		}else if(!WorkerUtil.isNullOrEmpty(req.getParameter("start_num"))){
			startNum = Integer.parseInt(String.valueOf(req.getParameter("start_num")));
			endNum = Integer.parseInt(String.valueOf(req.getParameter("end_num")));
		}
		Map printInfo = shipmentBiz.batchSelectPrintInfo(batchPickId,startNum,endNum);
		
		
		model.put("print_info", printInfo);
		model.put("order_count", printInfo.get("order_count"));
		model.put("packNum", printInfo.get("packNum"));
		model.put("batchNumSize", printInfo.get("batchNumSize"));
		List<Map<String,Object>> orderInfoList = (List<Map<String,Object>>)printInfo.get("order_info_list");
		String shippingCode = String.valueOf(orderInfoList.get(0).get("shipping_code"));
		
		if("SF".equals(shippingCode) || "SFLY".equals(shippingCode)){
			return "/batchShipment/print/expressBill/batchSf";
		}
		
		return "/batchShipment/print/expressBill/printBill";
	}
	
	/**
	 * 大后门【打印快递面单之后自动完成出库等操作流程】
	 */ 
	@RequestMapping(value="/batchTrickRecheck") 
	public String batchTrickRecheck( HttpServletRequest req , Map<String,Object> model ){
		return "/batchShipment/batchTrickRecheck";    
	}
	
	/**
	 * 大后门【打印快递面单之后自动完成出库等操作流程】判断可打印性
	 * */
	@RequestMapping(value="/loadTrickBatchPickSn")
	@ResponseBody
	public Map<String, Object> loadTrickBatchPickSn(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String batchPickSn = req.getParameter("batch_pick_sn");
		if(WorkerUtil.isNullOrEmpty(batchPickSn)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "（打印快递面单）没有接收到波次单号");
			return resultMap;
		}else{
			ReentrantLock lock = LockUtil.getReentrantLock("loadBatchPickSn_" + batchPickSn);
			lock.lock();
			try{
				Integer batchPickId = shipmentBiz.loadTrickBatchPickSn(batchPickSn);
				resultMap.put("result", Response.SUCCESS);
				resultMap.put("batch_pick_id", batchPickId);
			}catch(Exception e){
				logger.info("（打印快递面单）加载波次发生错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（打印快递面单）加载波次发生错误："+ e.getMessage());
			}
			finally{
				// 释放锁
				lock.unlock();
			}
		}
		return resultMap;
	}
	
	/**
	 * 大后门【打印快递面单之后自动完成出库等操作流程】
	 */ 
	@RequestMapping(value="/batchTrickRecheckReprint") 
	public String batchTrickRecheckReprint( HttpServletRequest req , Map<String,Object> model ){
		return "/batchShipment/batchTrickRecheckReprint";    
	}
	
	/**
	 * 补打印【打印快递面单之后自动完成出库等操作流程】判断可打印性
	 * */
	@RequestMapping(value="/reLoadTrickBatchPickSn")
	@ResponseBody
	public Map<String, Object> reLoadTrickBatchPickSn(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String batchPickSn = req.getParameter("batch_pick_sn");
		if(WorkerUtil.isNullOrEmpty(batchPickSn)) {
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "（补打印快递面单）没有接收到波次单号");
			return resultMap;
		}else{
			ReentrantLock lock = LockUtil.getReentrantLock("loadBatchPickSn_" + batchPickSn);
			lock.lock();
			try{
				Integer batchPickId = shipmentBiz.reLoadTrickBatchPickSn(batchPickSn);
				resultMap.put("result", Response.SUCCESS);
				resultMap.put("batch_pick_id", batchPickId);
			}catch(Exception e){
				logger.info("（补打印快递面单）加载波次发生错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（补打印快递面单）加载波次发生错误："+ e.getMessage());
			}
			finally{
				// 释放锁
				lock.unlock();
			}
		}
		return resultMap;
	}

	/**
	 * 补打印波次快递面单 
	 */ 
	@RequestMapping(value="/batchRecheckReprintByPick") 
	public String batchPickSnForRePrint( HttpServletRequest req , Map<String,Object> model ){
		return "/batchShipment/batchRecheckReprintByPick";    
	}
	
	/**
	 * 补打印波次快递面单 - 判断是否可打印
	 * */
	@RequestMapping(value="/reLoadBatchPickSnForRePrint")
	@ResponseBody
	public Map<String, Object> reLoadBatchPickSnForRePrint(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String batchPickSn = req.getParameter("batch_pick_sn");
		if(WorkerUtil.isNullOrEmpty(batchPickSn)) { 
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "（补打印快递面单）没有接收到波次单号");
			return resultMap;
		}else{
			try{
				resultMap = shipmentBiz.reLoadBatchPickSnForRePrint(batchPickSn);
			}catch(Exception e){
				logger.info("（补打印快递面单）加载波次发生错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（补打印快递面单）加载波次发生错误："+ e.getMessage());
			}
		}
		return resultMap;
	}
	/**
	 * 批量绑定码拖
	 * @author dlyao
	 * 
	 * */
	@RequestMapping(value="/queryAllShipment")
	@ResponseBody
	public Map<String, Object> queryAllShipment(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String batch_pick_sn=req.getParameter("no");
		
		resultMap=shipmentBiz.queryAllShipment(batch_pick_sn);
		
		
		return resultMap;
		
	}
	
	/**
	 * 批量打印码拖条码
	 * @author dlyao
	 * 
	 * */
	@RequestMapping(value="/printAllShipment")
	public String printAllShipment(HttpServletRequest req,Map<String,Object> model){
	
		String mt_code=req.getParameter("mt_code");
        List<String> li = Arrays.asList(mt_code.split(","));
		String shipping_name="";
		try {
			shipping_name = URLDecoder.decode(req.getParameter("shipping_name"),"utf-8");
			
			
		} catch (UnsupportedEncodingException e) {

			logger.debug(e.getMessage());
			model.put("mt_code", li);
			return "/batchShipment/print/mtcode";
		}
		List<String> li2 = Arrays.asList(shipping_name.split(","));
		List<Map<String,String>> li3=new ArrayList<Map<String,String>>();
		for(int i=0;i<li.size();i++){
			Map<String,String> m= new HashMap<String,String>();
			m.put("mt_code", li.get(i));
			m.put("shipping_name", li2.get(i));
			li3.add(m);
		}
		
		
		model.put("model", li3);
		
		return "/batchShipment/print/mtcode";
		
	}
	
	
	/**
	 * 根据批量波次查询面单序号 
	 */ 
	@RequestMapping(value="/batchSearchTnsSequence") 
	public String batchSearchTnsSequence( HttpServletRequest req , Map<String,Object> model ){
		return "/batchShipment/batchSearchTnsSequence";    
	}
	
	/**
	 * 批量波次查询面单序号 
	 */ 
	@RequestMapping(value="/searchTnsSequence") 
	@ResponseBody
	public Map<String, Object> searchTnsSequence(HttpServletRequest req){
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取前端传递的参数
		String batchPickSn = req.getParameter("batch_pick_sn");
		if(WorkerUtil.isNullOrEmpty(batchPickSn)) { 
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "（批量波次查询面单序号）没有接收到波次单号");
			return resultMap;
		}else{
			try{
				resultMap = shipmentBiz.searchTnsSequence(batchPickSn);
			}catch(Exception e){
				logger.info("（批量波次查询面单序号）错误:" + e.getMessage());
				resultMap.put("result",Response.FAILURE);
				resultMap.put("note", "（批量波次查询面单序号）错误："+ e.getMessage());
			}
		}
		return resultMap;
	}
	
}