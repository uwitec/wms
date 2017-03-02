package com.leqee.wms.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.dao.ReplenishmentDao;
import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ScheduleQueueReplenishment;
import com.leqee.wms.biz.ReplenishmentBiz;
import com.leqee.wms.biz.ScheduleQueueReplenishmentBiz;
import com.leqee.wms.response.Response;
import com.leqee.wms.schedule.job.TaskUtils;
import com.leqee.wms.util.WorkerUtil;

@Service
public class GeneralReplenishJob extends CommonJob{

	private Logger logger = Logger.getLogger(GeneralReplenishJob.class);
	@Autowired
	ScheduleQueueReplenishmentBiz scheduleQueueReplenishmentBiz;
	@Autowired
	ReplenishmentBiz replenishmentBiz;
	@Autowired
	ReplenishmentDao replenishmentDao;
	
	//手动补货规则设置-->补货任务创建
	//可配置 每次执行设置条数
	public void manulReplenishTask(String paramNameValue){
		String methodName = "job:GeneralReplenishJob->manulReplenishTask";
		Map<String, String> paramNameValueMap = TaskUtils.getParamNameValueMap(paramNameValue);
		Integer quantity = paramNameValueMap.get("quantity") == null ?5 : Integer.parseInt(paramNameValueMap.get("quantity")) ;
		logger.info(methodName+ "_start paramNameValue=" + quantity);
		List<ScheduleQueueReplenishment> list = scheduleQueueReplenishmentBiz.selectManulPieceReplenish(quantity);
		if(!WorkerUtil.isNullOrEmpty(list)){
			List<Integer> empty0 = new ArrayList<Integer>();
			List<Map> empty1 = new ArrayList<Map>();
			Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
			Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
			List<Product> empty5 = new ArrayList<Product>();
			
			for (ScheduleQueueReplenishment scheduleQueueReplenishment : list) {
				String taskIdStrAdd = "";
				Integer physicalWarehouseId = scheduleQueueReplenishment.getPhysical_warehouse_id();
				Integer customerId = scheduleQueueReplenishment.getCustomer_id();
				Integer queueId = scheduleQueueReplenishment.getQueue_id();
				List<Integer> customerIdList = new ArrayList<Integer>();
				if(customerId.equals(0)){
					customerIdList = replenishmentBiz.selectGeneralCustomerByPhysicalWarehouseId(physicalWarehouseId);
				}
				if(customerId.equals(0) && !WorkerUtil.isNullOrEmpty(customerIdList)){
					for (Integer customerIdx : customerIdList) {
						String taskIdStr = ""; 
						try{
							taskIdStr = replenishmentBiz.lockReplenishJobByWarehouseCustomer(scheduleQueueReplenishment.getProduct_id(),
								scheduleQueueReplenishment.getBox_piece(),"General",empty0,physicalWarehouseId,customerIdx,
								empty1,empty2,empty0,empty4,0,empty5,0,0,0
								);
							if(taskIdStr=="LOCKING"){
								taskIdStr = "";
							}
						}catch (Exception e) {
							logger.info("manulReplenishTask error:"+e.getMessage());
						}
						taskIdStrAdd = taskIdStrAdd + taskIdStr;
					}
				}else if(!customerId.equals(0)){
					try{
						taskIdStrAdd = replenishmentBiz.lockReplenishJobByWarehouseCustomer(scheduleQueueReplenishment.getProduct_id(),
							scheduleQueueReplenishment.getBox_piece(),"General",empty0,physicalWarehouseId,customerId,
							empty1,empty2,empty0,empty4,0,empty5,0,0,0
							);
					}catch (Exception e) {
						logger.info("manulReplenishTask error:"+e.getMessage());
					}
				}

				if(queueId!=0 && !taskIdStrAdd.equals("LOCKING")){
					Integer taskCount = 0;
					if (taskIdStrAdd.indexOf(",")!=-1){  
						String[] str1 = taskIdStrAdd.split(",");  
						taskCount = str1.length; 
			        }  
					this.updateScheduleQueueReplenish(queueId,"COMPLETE",taskIdStrAdd,taskCount);
				}
				
			}
		}
		logger.info(methodName+ "_end paramNameValue=" + quantity);
	}
	
	//自动调度补货规则-->定时执行某物理仓&货主（不填则全匹配）
	public void autoReplenishTask(String paramNameValue){
		String methodName = "job:GeneralReplenishJob->autoReplenishTask";
		Map<String, String> paramNameValueMap = TaskUtils.getParamNameValueMap(paramNameValue);
		if(WorkerUtil.isNullOrEmpty(paramNameValueMap.get("physicalWarehouseId"))){
			logger.info(methodName+"exception :physicalWarehouseId is empty!");
			throw new RuntimeException(methodName+"exception :physicalWarehouseId is empty!");
		}
		Integer physicalWarehouseId = Integer.parseInt(paramNameValueMap.get("physicalWarehouseId"));
		List<Integer> customerIdList = new ArrayList<Integer>();
		if(WorkerUtil.isNullOrEmpty(paramNameValueMap.get("customerId"))){
			//配置过 补货规则的货主
			customerIdList = replenishmentBiz.selectGeneralCustomerByPhysicalWarehouseId(physicalWarehouseId);
		}else{
			customerIdList.add(Integer.parseInt(String.valueOf(paramNameValueMap.get("customerId"))));
		}
		logger.info(methodName+ "_start paramNameValue=" + paramNameValue);
		if(!WorkerUtil.isNullOrEmpty(customerIdList)){
			List<Integer> empty0 = new ArrayList<Integer>();
			List<Map> empty1 = new ArrayList<Map>();
			Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
			Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
			List<Product> empty5 = new ArrayList<Product>();
			for (Integer customerId : customerIdList) {
				logger.info("customerId="+customerId);
				try{
					replenishmentBiz.lockReplenishJobByWarehouseCustomer(0,"BOX_PIECE","General",empty0,
						physicalWarehouseId,customerId,
						empty1,empty2,empty0,empty4,0,empty5,0,0,0);
				}catch (Exception e) {
					logger.info("autoReplenishTask error:"+e.getMessage());
				}
			}
		}
		logger.info(methodName+ "_end paramNameValue=" + paramNameValue);
	}
	/**
	 * 手动设置一般补货规则更新任务  完成情况
	 */
	private void updateScheduleQueueReplenish(Integer queueId, String status,String taskIdStr, Integer taskCount) {
		replenishmentDao.updateScheduleQueueReplenish(queueId,status,taskIdStr,taskCount);
	}

}
