package com.leqee.wms.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.dao.OrderPrepackDao;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.service.CommonScheduleService;
import com.leqee.wms.service.InventoryService;
import com.leqee.wms.util.LockUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class InventoryServiceImpl extends CommonScheduleService implements InventoryService  {
	private Logger logger = Logger.getLogger(InventoryServiceImpl.class);
	
	@Autowired
	OrderBiz orderBiz;
	@Autowired
	InventoryBiz inventoryBiz;
	@Autowired
	OrderPrepackDao orderPrepackDao;
	@Autowired
	ShipmentDao shipmentDao;
	
	


	@Override
	public Response saleInventoryOut(Integer groupId, Integer customerId,
			Double hours, String endDate) {
		// 初始化数据
		Response response = new Response();
		List<Integer> customerIdList = new ArrayList<Integer>();
		Date startDateTime = new Date();
		Date endDateTime = new Date();
		String methodName = "saleInventoryOut";

		// 验证输入参数
		response = validAndInitParams(groupId, customerId, hours,
				endDate, customerIdList, startDateTime, endDateTime, methodName,
				logger);
		Assert.notNull(response, "[验证输入参数出错]response不能为空");
		if (!Response.OK.equals(response.getCode())) {
			return response;
		}
		
		String log_prefix = methodName + "_groupId_" + groupId.toString() + "_customerId_" + customerId.toString();
		
		// 加锁
		LockUtil.lock(groupId, customerId, methodName);
		logger.info(log_prefix + " start, lock the lock");
		
		try{
			// 分业务组处理销售订单出库
			if(!WorkerUtil.isNullOrEmpty(customerIdList)){
				for (Integer cId : customerIdList) {
				    List<Integer> orderIdList = orderBiz.getToDeliverInvSaleOrderIdList(cId, startDateTime, endDateTime);
				    logger.info(log_prefix + " 单个货主出库开始, customerId:" + cId + " 此货主待出库销售订单数量:" + orderIdList.size());
				    
				    if(!WorkerUtil.isNullOrEmpty(orderIdList)) {
				    	// 订单分开处理，即使出现错误，也不影响后面的订单~
				    	for (Integer orderId : orderIdList) {
				    		logger.info(log_prefix + " 单个订单出库开始, orderId:" + orderId);
				    		try{  // 单个订单出现的异常及时捕获, 不影响后面的订单
				    			Response singleResponse = inventoryBiz.saleDeliverInventory(orderId, "system");
				    			if(!Response.OK.equals(singleResponse.getCode())){
				    				orderBiz.updateOrderProcessBatchTrickStatus(orderId, "FAILED");
				    				logger.error(log_prefix + " 单个订单出库发生错误, orderId:" + orderId + ", message:" + singleResponse.getMsg());
				    			}
				    		}catch(Exception e){
				    			orderBiz.updateOrderProcessBatchTrickStatus(orderId, "FAILED");
				    			logger.error(log_prefix + " 单个订单出库发生异常, orderId:" + orderId + ", message:" + e.getMessage());
				    		}
				    		logger.info(log_prefix + " 单个订单出库结束, orderId:" + orderId);
						}
				    }
				    logger.info(log_prefix + " 单个货主出库结束, customerId:" + cId);
				}
			}
			
		}catch(Exception e){
			response = ResponseFactory.createExceptionResponse(log_prefix + "_exception: " + e.getMessage());
			logger.error(log_prefix + "_exception: " + e.getMessage());
		}finally{
			// 释放锁
			LockUtil.unlock(groupId, customerId, methodName);
			logger.info(log_prefix + " end, unlock the lock");
		}
		
		return response;
	}
	
	
	@Override
	public Response saleInventoryOutFor1111(Integer groupId, Integer customerId,
			Double hours, String endDate) {
		// 初始化数据
		Response response = new Response();
		List<Integer> customerIdList = new ArrayList<Integer>();
		Date startDateTime = new Date();
		Date endDateTime = new Date();
		String methodName = "大后门saleInventoryOutFor1111";

		// 验证输入参数
		response = validAndInitParams(groupId, customerId, hours,
				endDate, customerIdList, startDateTime, endDateTime, methodName,
				logger);
		Assert.notNull(response, "[验证输入参数出错]response不能为空");
		if (!Response.OK.equals(response.getCode())) {
			return response;
		}
		
		String log_prefix = methodName + "_groupId_" + groupId.toString() + "_customerId_" + customerId.toString();
		
		// 加锁
		LockUtil.lock(groupId, customerId, methodName);
		logger.info(log_prefix + " start, lock the lock");
		
		try{
			// 分业务组处理销售订单出库
			if(!WorkerUtil.isNullOrEmpty(customerIdList)){
				for (Integer cId : customerIdList) {
					logger.info(log_prefix + " 单个货主出库开始, customerId:" + cId);
				    List<Integer> orderIdList = orderBiz.getToDeliverInvSaleOrderIdListFor1111(cId, startDateTime, endDateTime);
				    logger.info(log_prefix + " 此货主待出库销售订单数量:" + orderIdList.size());
				    
				    if(!WorkerUtil.isNullOrEmpty(orderIdList)) {
				    	// 订单分开处理，即使出现错误，也不影响后面的订单~
				    	for (Integer orderId : orderIdList) {
				    		logger.info(log_prefix + " 单个订单出库开始, orderId:" + orderId);
				    		try{  // 单个订单出现的异常及时捕获, 不影响后面的订单
				    			Response singleResponse = inventoryBiz.saleDeliverInventoryFor1111(orderId, "system");
				    			if(!Response.OK.equals(singleResponse.getCode())){
				    				orderBiz.updateOrderProcessBatchTrickStatus(orderId, "ERROR");
				    				logger.error(log_prefix + " 单个订单出库发生错误, orderId:" + orderId + ", message:" + singleResponse.getMsg());
				    			}
				    		}catch(Exception e){
				    			orderBiz.updateOrderProcessBatchTrickStatus(orderId, "ERROR");
				    			logger.error(log_prefix + " 单个订单出库发生异常, orderId:" + orderId + ", message:" + e.getMessage());
				    		}
				    		logger.info(log_prefix + " 单个订单出库结束, orderId:" + orderId);
						}
				    }
				    logger.info(log_prefix + " 单个货主出库结束, customerId:" + cId);
				}
			}
			
		}catch(Exception e){
			response = ResponseFactory.createExceptionResponse(log_prefix + "_exception: " + e.getMessage());
			logger.error(log_prefix + "_exception: " + e.getMessage());
		}finally{
			// 释放锁
			LockUtil.unlock(groupId, customerId, methodName);
			logger.info(log_prefix + " end, unlock the lock");
		}
		
		return response;
	}
	
	
	@Override
	// 耗材出库调度
	public Response packBoxInventoryOut(Integer groupId, Integer customerId,
			Double hours, String endDate) {
		// 初始化数据
		Response response = new Response();
		List<Integer> customerIdList = new ArrayList<Integer>();
		Date startDateTime = new Date();
		Date endDateTime = new Date();
		String methodName = "packBoxInventoryOut";

		// 验证输入参数 
		response = validAndInitParams(groupId, customerId, hours,
				endDate, customerIdList, startDateTime, endDateTime, methodName,
				logger);
		Assert.notNull(response, "[验证输入参数出错]response不能为空");
		if (!Response.OK.equals(response.getCode())) {
			return response;
		}
		
		String log_prefix = methodName + "_groupId_" + groupId.toString() + "_customerId_" + customerId.toString();
		
		// 加锁
		LockUtil.lock(groupId, customerId, methodName);
		logger.info(log_prefix + " start, lock the lock");
		
		try{
			// 分业务组处理耗材出库
			if(!WorkerUtil.isNullOrEmpty(customerIdList)){
				for (Integer cId : customerIdList) {
					
					logger.info(log_prefix + " 单个货主耗材出库开始, customerId:" + cId);
				    List<Integer> shipmentIdList = orderBiz.getToDeliverInvPackBoxShipmentIdList(cId, startDateTime, endDateTime);
				    logger.info(log_prefix + " 此货主待出库耗材订单数量:" + shipmentIdList.size());
				    
				    if(!WorkerUtil.isNullOrEmpty(shipmentIdList)) {
				    	// 订单分开处理，即使出现错误，也不影响后面的订单~
				    	for (Integer shipmentId : shipmentIdList) {
				    		logger.info(log_prefix + " 单个订单耗材出库开始, shipmentId:" + shipmentId);
				    		try{  // 单个订单出现的异常及时捕获, 不影响后面的订单
				    			Response singleResponse = inventoryBiz.packBoxDeliverInventory(shipmentId, "system");
				    			if(!Response.OK.equals(singleResponse.getCode())){
				    				shipmentDao.updatePackBoxStatusV2(shipmentId, 'F', "system");
				    				logger.error(log_prefix + " 单个订单耗材出库发生错误, orderId:" + shipmentId + ", message:" + singleResponse.getMsg());
				    			}
				    		}catch(Exception e){
				    			shipmentDao.updatePackBoxStatusV2(shipmentId, 'F', "system");
				    			logger.error(log_prefix + " 单个订单耗材出库发生异常, orderId:" + shipmentId + ", message:" + e.getMessage());
				    		}
				    		logger.info(log_prefix + " 单个订单耗材出库结束, orderId:" + shipmentId);
						}
				    }
				    logger.info(log_prefix + " 单个货主耗材出库结束, customerId:" + cId);
				}
			}
			
		}catch(Exception e){
			response = ResponseFactory.createExceptionResponse(log_prefix + "_exception: " + e.getMessage());
			logger.error(log_prefix + "_exception: " + e.getMessage(), e);
		}finally{
			// 释放锁
			LockUtil.unlock(groupId, customerId, methodName);
			logger.info(log_prefix + " end, unlock the lock");
		}
		
		return response;
	}
	
	@Override
	// 耗材出库调度
	public Response packBoxInventoryOutV2(Integer groupId, Integer customerId,
			Double hours, String endDate) {
		// 初始化数据
		Response response = new Response();
		List<Integer> customerIdList = new ArrayList<Integer>();
		Date startDateTime = new Date();
		Date endDateTime = new Date();
		String methodName = "packBoxInventoryOutV2";

		// 验证输入参数
		response = validAndInitParams(groupId, customerId, hours,
				endDate, customerIdList, startDateTime, endDateTime, methodName,
				logger);
		Assert.notNull(response, "[验证输入参数出错]response不能为空");
		if (!Response.OK.equals(response.getCode())) {
			return response;
		}
		
		String log_prefix = methodName + "_groupId_" + groupId.toString() + "_customerId_" + customerId.toString();
		
		// 加锁
		LockUtil.lock(groupId, customerId, methodName);
		logger.info(log_prefix + " start, lock the lock");
		
		try{
			// 分业务组处理耗材出库
			if(!WorkerUtil.isNullOrEmpty(customerIdList)){
				for (Integer cId : customerIdList) {
					
					logger.info(log_prefix + " 单个货主耗材出库开始, customerId:" + cId);
				    List<Integer> shipmentIdList = orderBiz.getToDeliverInvPackBoxShipmentIdListV2(cId, startDateTime, endDateTime);
				    logger.info(log_prefix + " 此货主待出库耗材订单数量:" + shipmentIdList.size());
				    
				    if(!WorkerUtil.isNullOrEmpty(shipmentIdList)) {
				    	// 订单分开处理，即使出现错误，也不影响后面的订单~
				    	for (Integer shipmentId : shipmentIdList) {
				    		logger.info(log_prefix + " 单个订单耗材出库开始, shipmentId:" + shipmentId);
				    		try{  // 单个订单出现的异常及时捕获, 不影响后面的订单
				    			Response singleResponse = inventoryBiz.packBoxDeliverInventory(shipmentId, "system");
				    			if(!Response.OK.equals(singleResponse.getCode())){
				    				logger.error(log_prefix + " 单个订单耗材出库发生错误, orderId:" + shipmentId + ", message:" + singleResponse.getMsg());
				    			}
				    		}catch(Exception e){
				    			logger.error(log_prefix + " 单个订单耗材出库发生异常, orderId:" + shipmentId + ", message:" + e.getMessage());
				    		}
				    		logger.info(log_prefix + " 单个订单耗材出库结束, orderId:" + shipmentId);
						}
				    }
				    logger.info(log_prefix + " 单个货主耗材出库结束, customerId:" + cId);
				}
			}
			
		}catch(Exception e){
			response = ResponseFactory.createExceptionResponse(log_prefix + "_exception: " + e.getMessage());
			logger.error(log_prefix + "_exception: " + e.getMessage(), e);
		}finally{
			// 释放锁
			LockUtil.unlock(groupId, customerId, methodName);
			logger.info(log_prefix + " end, unlock the lock");
		}
		
		return response;
	}
	
	/**
	 * 仓库内导入库位库存
	 * @param customerId
	 * @param physicalWarehouId
	 * @return
	 */
	public Response importProductLocation(Integer customerId , Integer physicalWarehouseId){
		Response response = null;
		try {
			Map resMap = inventoryBiz.importProductLocation(customerId, physicalWarehouseId);
			response = new Response(Response.OK,Response.SUCCESS+resMap.get("note").toString());
		} catch (Exception e) {
			logger.error("仓库内导入库位库存调度转换失败", e);
			response = new Response(Response.EXCEPTION,Response.FAILURE+e.getMessage());;
		}
		return response;
	}
	
	/**
	 * 调拨采购单自动入库
	 * @param customerId
	 * @param physicalWarehouId
	 * @return
	 */
	public Response autoPurchaseAccept(Integer customerId , Integer physicalWarehouseId,Double hours, String endDate){
		Response response = null;
		Date startDateTime = new Date();
		Date endDateTime = new Date();
		try {
			if(WorkerUtil.isNullOrEmpty(endDate)){  
				//为空时取当前时间
				endDateTime.setTime(new Date().getTime());
			}else{
				try {
					endDateTime.setTime(WorkerUtil.parseDatetime(endDate).getTime()) ;
				} catch (ParseException e) {
					return new Response(Response.FAILURE, "输入参数错误 exception=" + e.getMessage());
				}
			}
			
			startDateTime.setTime( new Date(endDateTime.getTime()-(long)(hours*3600L*1000L)).getTime()) ;
			
			List<Integer> orderIdList = orderBiz.getToAutoPurchaseAccept(customerId, physicalWarehouseId,startDateTime, endDateTime);
			for(Integer orderId : orderIdList){
				Map resMap = inventoryBiz.autoPurchaseAccept(orderId,customerId, physicalWarehouseId);
			}
			response = new Response(Response.OK,Response.SUCCESS);
		} catch (Exception e) {
			logger.error("调拨采购单自动入库失败", e);
			response = new Response(Response.EXCEPTION,Response.FAILURE+e.getMessage());;
		}
		return response;
	}


	public Response testAutoPurchaseAcceptAndGrouding(Integer groupId,Integer customerId , Integer physicalWarehouseId,Double hours, String endDate){
		Date startDateTime = new Date();
		Date endDateTime = new Date();
		Response response = new Response();
		List<Integer> customerIdList = new ArrayList<Integer>();
		String methodName = "testAutoPurchaseAcceptAndGrouding";

		// 验证输入参数
		response = validAndInitParams(groupId, customerId, hours,
				endDate, customerIdList, startDateTime, endDateTime, methodName,
				logger);
		Assert.notNull(response, "[验证输入参数出错]response不能为空");
		if (!Response.OK.equals(response.getCode())) {
			return response;
		}
		
		if(!WorkerUtil.isNullOrEmpty(customerIdList)){
			for (Integer cId : customerIdList) {
		
				List<Integer> orderIdList = orderBiz.getTestToAutoPurchaseAccept(cId, physicalWarehouseId,startDateTime, endDateTime);
				for(Integer orderId : orderIdList){
					try {
						Map resMap = inventoryBiz.testAutoPurchaseAccept(orderId,customerId, physicalWarehouseId);
					} catch (Exception e) {
						logger.error("调拨采购单自动入库失败", e);
						response = new Response(Response.EXCEPTION,Response.FAILURE+e.getMessage());;
					}
				}
				response = new Response(Response.OK,Response.SUCCESS);
			}
		}
		
		return response;
	}
	
	
	@Override
	public Response packBoxInventoryOutForPrePack(
			Integer physical_warehouse_id, Integer customer_id,
			 String start) {
		logger.info(" 单个货主预打包耗材出库开始,physical_warehouse_id="+physical_warehouse_id+" customerId:" + customer_id);
		
		Response result = ResponseFactory.createOkResponse("耗材出库成功!");
		result.setCode(Response.OK);
		
        List<Map> labelPrepackMap=new ArrayList<Map>();
        labelPrepackMap=orderPrepackDao.getNeedToOutPrepackMap(physical_warehouse_id,customer_id,start);
        if(WorkerUtil.isNullOrEmpty(labelPrepackMap)){
        	logger.info(" 单个货主预打包耗材出库结束, customerId:" + customer_id);
        	return result;
        }
		for(Map m:labelPrepackMap){
			
			int order_id=null==m.get("order_id")?0:Integer.parseInt(m.get("order_id").toString());
			String order_sn=null==m.get("order_sn")?"":m.get("order_sn").toString();
			int warehouse_id=null==m.get("warehouse_id")?0:Integer.parseInt(m.get("warehouse_id").toString());
			int label_prepack_id=null==m.get("label_prepack_id")?0:Integer.parseInt(m.get("label_prepack_id").toString());
			int packbox_product_id=null==m.get("packbox_product_id")?0:Integer.parseInt(m.get("packbox_product_id").toString());
			int packbox_need_out=null==m.get("packbox_need_out")?0:Integer.parseInt(m.get("packbox_need_out").toString());
			
			logger.info(" 单个预打包标签订单耗材出库开始, label_prepack_id:" + label_prepack_id);
		    		
		    		try{  // 单个订单出现的异常及时捕获, 不影响后面的订单
		    			Response singleResponse = inventoryBiz.PrePackBoxOut(physical_warehouse_id, customer_id,order_id,order_sn,label_prepack_id,packbox_product_id,packbox_need_out,warehouse_id);
		    			if(!Response.OK.equals(singleResponse.getCode())){
		    				logger.error(" 单个预打包耗材出库发生错误, label_prepack_id:" + label_prepack_id + ", message:" + singleResponse.getMsg());
		    			}
		    		}catch(Exception e){
		    			logger.info(" 单个预打包标签订单耗材出库出错, label_prepack_id:" +label_prepack_id +", message:" + e.getMessage());
		    		}
		    		logger.info(" 单个预打包标签订单耗材出库结束, label_prepack_id:" + label_prepack_id);
				}
		logger.info(" 单个货主预打包耗材出库结束, customerId:" + customer_id);
		   
		
		return result;
	}
	
	
}
