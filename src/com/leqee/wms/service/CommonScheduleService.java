package com.leqee.wms.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.leqee.wms.biz.WarehouseCustomerBiz;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.util.WorkerUtil;


public abstract class CommonScheduleService {
	
	@Autowired
	WarehouseCustomerBiz warehouseCustomerBiz;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;

	/**
	 * 验证和初始化相关参数
	 * @param groupId
	 * @param customerId
	 * @param distributorId
	 * @param hours
	 * @param endDate
	 * @param customerIdList
	 * @param startDateTime
	 * @param endDateTime
	 * @param methodName
	 * @return
	 */
	protected Response validAndInitParams( Integer groupId , Integer customerId , 
			Double hours , String endDate, List<Integer> customerIdList, 
			Date startDateTime , Date endDateTime , String methodName , Logger logger ){
		
		// 非空和格式的基本验证
		if( groupId <= 0 && customerId <= 0 ){
			logger.info(methodName + "_error groupId、customerId不能都小于等于0, groupId=" + groupId +",customerId="+ customerId);
			return ResponseFactory.createErrorResponse( "输入参数错误" ,"100001" ,"groupId、customerId不能都小于等于0");
		}
		if( hours <= 0){
			logger.info(methodName + "_error hours不能小于等于0，hours=" + hours);
			return ResponseFactory.createErrorResponse( "输入参数错误" ,"100002" ,"hours不能都小于等于0,hours=" + hours);
		}
		
		// TODO 对象传递地址引用问题groupId、customerId也要考虑到，类似startDateTime处理
		// 初始化groupId、customerId并验证
		if(customerId > 0){
			WarehouseCustomer customer = warehouseCustomerBiz.findByCustomerId(customerId); 
			if(WorkerUtil.isNullOrEmpty(customer) || WorkerUtil.isNullOrEmpty(customer.getCustomer_id())){
				logger.error(methodName + "_error customerId没有对应的业务组，customerId=" + customerId);
				return ResponseFactory.createErrorResponse( "输入参数错误" ,"100003" ,"customerId没有对应的业务组或者业务组,customerId=" + customerId);
			}
			
			groupId = customer.getGroup_id();
			if(groupId <= 0){
				logger.error(methodName + "_error customerId没有对应的group，customerId=" + customerId);
				return ResponseFactory.createErrorResponse( "输入参数错误" ,"100004" ,"customerId没有对应的group,customerId=" + customerId);
			}
			customerIdList.add(customerId);
		}else{
			customerIdList.addAll(warehouseCustomerDao.selectCustomerIdListByGroupId(groupId));
			if(WorkerUtil.isNullOrEmpty(customerIdList)){
				logger.error(methodName + "_error groupId没有对应的party列表，groupId=" + groupId);
				return ResponseFactory.createErrorResponse( "输入参数错误" ,"100005" ,"groupId没有对应的party列表，groupId=" + groupId);
			}
		}
		
		
		// 初始化startDateTime和endDateTime
		if(WorkerUtil.isNullOrEmpty(endDate)){  
			//为空时取当前时间
			endDateTime.setTime(new Date().getTime());
		}else{
			try {
				endDateTime.setTime(WorkerUtil.parseDatetime(endDate).getTime()) ;
			} catch (ParseException e) {
				logger.error(methodName + "_error endDate参数格式错误，endDate=" + endDate, e);
				return ResponseFactory.createErrorResponse( "输入参数错误" ,"100006" ,"endDate参数格式错误，endDate=" + endDate + ",exception=" + e.getMessage());
			}
		}
		
		startDateTime.setTime( new Date(endDateTime.getTime()-(long)(hours*3600L*1000L)).getTime()) ;
		if(startDateTime.after(endDateTime)){
			logger.error(methodName + "_error 计算出的开始时间晚于结束时间，endDate=" + endDate+",hours="+ hours);
			return ResponseFactory.createErrorResponse( "输入参数错误" ,"100007" ,"计算出的开始时间晚于结束时间，endDate=" + endDate+",hours="+ hours);
		}
		
		return ResponseFactory.createOkResponse("success");
	}
	
	/**
	 * 验证和初始化相关参数
	 * @param groupId
	 * @param customerId
	 * @param distributorId
	 * @param hours
	 * @param endDate
	 * @param customerIdList
	 * @param startDateTime
	 * @param endDateTime
	 * @param methodName
	 * @return
	 */
	protected Response validAndInitParamsV2(Integer physicalWarehouseId, String batchPickGroupIds ,
			Double hours , String endDate, List<Integer> batchPickGroupIdList, 
			Date startDateTime , Date endDateTime , String methodName , Logger logger ){
		
		if(physicalWarehouseId <= 0 ){
			logger.info(methodName + "_error physicalWarehouseId不能小于等于0:" + physicalWarehouseId );
			return ResponseFactory.createErrorResponse( "输入参数错误" ,"100001" ,"physicalWarehouseId不能小于等于0");
		}
		logger.info("selectToApplyFirstShipmentBatchPickIdList:"+batchPickGroupIds);
		if(WorkerUtil.isNullOrEmpty(batchPickGroupIds) || "".equalsIgnoreCase(batchPickGroupIds)){
			logger.info(methodName + "_error batchPickGroupIds不能为空:" + batchPickGroupIds );
			return ResponseFactory.createErrorResponse( "输入参数错误" ,"100001" ,"batchPickGroupIds不能为空");
		}else{
			String[] arr = batchPickGroupIds.split(",");
			for (String batchPickGroupId : arr) {
				batchPickGroupIdList.add(Integer.parseInt(batchPickGroupId));
			}
		}
		if( hours <= 0){
			logger.info(methodName + "_error hours不能小于等于0，hours=" + hours);
			return ResponseFactory.createErrorResponse( "输入参数错误" ,"100002" ,"hours不能都小于等于0,hours=" + hours);
		}
		
		// 初始化startDateTime和endDateTime
		if(WorkerUtil.isNullOrEmpty(endDate)){  
			//为空时取当前时间
			endDateTime.setTime(new Date().getTime());
		}else{
			try {
				endDateTime.setTime(WorkerUtil.parseDatetime(endDate).getTime()) ;
			} catch (ParseException e) {
				logger.error(methodName + "_error endDate参数格式错误，endDate=" + endDate, e);
				return ResponseFactory.createErrorResponse( "输入参数错误" ,"100006" ,"endDate参数格式错误，endDate=" + endDate + ",exception=" + e.getMessage());
			}
		}
		
		startDateTime.setTime( new Date(endDateTime.getTime()-(long)(hours*3600L*1000L)).getTime()) ;
		if(startDateTime.after(endDateTime)){
			logger.error(methodName + "_error 计算出的开始时间晚于结束时间，endDate=" + endDate+",hours="+ hours);
			return ResponseFactory.createErrorResponse( "输入参数错误" ,"100007" ,"计算出的开始时间晚于结束时间，endDate=" + endDate+",hours="+ hours);
		}
		
		return ResponseFactory.createOkResponse("success");
	}
	
}
