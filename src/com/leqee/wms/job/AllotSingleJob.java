package com.leqee.wms.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.api.util.WorkerUtil;
import com.leqee.wms.biz.ReplenishmentBiz;
import com.leqee.wms.biz.impl.BatchPickBizImpl;
import com.leqee.wms.dao.ConfigCwMappingDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ReplenishmentDao;
import com.leqee.wms.dao.ReplenishmentUrgentDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.ConfigCwMapping;
import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.Product;
import com.leqee.wms.schedule.job.TaskUtils;

@Service
public class AllotSingleJob {

	private Logger logger = Logger.getLogger(AllotSingleJob.class);
	private Cache<String, List<ConfigCwMapping>> configCwMappingCache;
	@Autowired
	WarehouseDao warehouseDao;
	@Autowired
	ProductDao productDao;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	@Autowired
	ProductLocationDao productLocationDao;
	@Autowired
	ReplenishmentDao replenishmentDao;
	@Autowired
	ReplenishmentUrgentDao replenishmentUrgentDao;
	@Autowired
	BatchPickBizImpl batchPickBizImpl;
	@Autowired
	ReplenishmentBiz replenishmentBizImpl;
	@Autowired
	OrderInfoDao  orderInfoDao;
	@Autowired
	ConfigCwMappingDao configCwMappingDao;
	

	

	/**
	 * toC波此单
	 * @param paramNameValue
	 */
	public void allot2CInventory(String paramNameValue) {
		logger.info("AllotSingleJob allot2CInventory start;"+paramNameValue);
		String methodName = "AllotSingleJob->allot2CInventory";
		try {
			List<Integer> batchPickList = new ArrayList<Integer>();

			Map<String, Integer> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Integer physical_warehouse_id1 = serviceParamsMap.get("physical_warehouse_id"); //默认为0
			Integer group_id = serviceParamsMap.get("groupId"); //默认为0
			Integer customer_id = serviceParamsMap.get("customerId"); //默认为0
			
			List<Integer> list=new ArrayList<Integer>();
			List<Integer> list2=new ArrayList<Integer>();
			
			RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
					.getSecurityManager();
			CacheManager cacheManager = securityManager.getCacheManager();
            configCwMappingCache = cacheManager.getCache("configCwMappingCache");
			
			
			if(physical_warehouse_id1-0==0){
				list=warehouseDao.selectPhysicalWarehouseIdList();
			}else{
				list.add(physical_warehouse_id1);
			}
			
			if(group_id!=0){
				list2=warehouseCustomerDao.selectCustomerIdListByGroupId(group_id);
			}
			else if(customer_id-0==0){
				list2=warehouseCustomerDao.selectAllId();
			}
			else{
				list2.add(customer_id);
			}
			
			//pack任务
			List<Integer> empty0 = new ArrayList<Integer>();
			List<Map> empty1 = new ArrayList<Map>();
			Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
			Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
			List<Product> empty5 = new ArrayList<Product>();
			
			for(Integer physical_warehouse_id : list){
				for (Integer customerId : list2) {

					List<ConfigCwMapping> configCwMappingList=configCwMappingCache.get(physical_warehouse_id+"_"+customerId);
					
					if(null==configCwMappingList){
						configCwMappingList = configCwMappingDao.getConfigCwMappingList(physical_warehouse_id, customerId);
						configCwMappingCache.put(physical_warehouse_id+"_"+ customerId, configCwMappingList);
					}
					if(WorkerUtil.isNullOrEmpty(configCwMappingList)){
						continue ;
					}else{
						
						for(ConfigCwMapping cw:configCwMappingList){
							int warehouse_id=cw.getWarehouse_id();
							logger.info("AllotSingleJob allot2CInventory toC start;customerId="+customerId+",warehouse_id="+warehouse_id);
							
							// 普通波此单
							
							batchPickList = batchPickBizImpl.getNeedAllotSaleBatchPickV2(
									physical_warehouse_id, customerId,
									"('E','N')", 1,warehouse_id); // 1普通波此单
							
							if (!WorkerUtil.isNullOrEmpty(batchPickList)) {
								
								//使用同类型的参数
								replenishmentBizImpl.lockReplenishJobByWarehouseCustomer( 0, "",
										"Allot11", batchPickList, physical_warehouse_id,
										customerId, empty1,
										empty2, empty0, empty4,
										0, empty5,0,0,warehouse_id);
							}

							logger.info("AllotSingleJob allot2CInventory toC end;customerId="+customerId+",warehouse_id="+cw.getWarehouse_id());
						}
						
						
						
					}
					
					
				}
			}

		} catch (Exception e) {
			logger.error(methodName + "_error exception=" + e.getMessage(), e);
		}
		logger.info("AllotSingleJob allot2CInventory end;"+paramNameValue);
	}
	
	/**
	 * //2toB波此单
	 * @param paramNameValue
	 */
	public void allot2BInventory(String paramNameValue) {
		logger.info("AllotSingleJob allot2BInventory start;"+paramNameValue);
		String methodName = "AllotSingleJob->allot2BInventory";
		try {
			List<Integer> batchPickList = new ArrayList<Integer>();

			Map<String, Integer> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Integer physical_warehouse_id1 = serviceParamsMap.get("physical_warehouse_id") ; //默认为0
			Integer group_id = serviceParamsMap.get("groupId") ; //默认为0
			Integer customer_id = serviceParamsMap.get("customerId"); //默认为0
			
			List<Integer> list=new ArrayList<Integer>();
			List<Integer> list2=new ArrayList<Integer>();
			
			RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
					.getSecurityManager();
			CacheManager cacheManager = securityManager.getCacheManager();
            configCwMappingCache = cacheManager.getCache("configCwMappingCache");
			
			if(physical_warehouse_id1-0==0){
				list=warehouseDao.selectPhysicalWarehouseIdList();
			}else{
				list.add(physical_warehouse_id1);
			}
			
			if(group_id!=0){
				list2=warehouseCustomerDao.selectCustomerIdListByGroupId(group_id);
			}
			else if(customer_id-0==0){
				list2=warehouseCustomerDao.selectAllId();
			}
			else{
				list2.add(customer_id);
			}
			
			//pack任务
			List<Integer> empty0 = new ArrayList<Integer>();
			List<Map> empty1 = new ArrayList<Map>();
			Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
			Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
			List<Product> empty5 = new ArrayList<Product>();
			
			for(Integer physical_warehouse_id : list){
				for (Integer customerId : list2) {

					List<ConfigCwMapping> configCwMappingList=configCwMappingCache.get(physical_warehouse_id+"_"+customerId);
					
					if(null==configCwMappingList){
						configCwMappingList = configCwMappingDao.getConfigCwMappingList(physical_warehouse_id, customerId);
						configCwMappingCache.put(physical_warehouse_id+"_"+ customerId, configCwMappingList);
					}
					if(WorkerUtil.isNullOrEmpty(configCwMappingList)){
						continue ;
					}else{
						
						for(ConfigCwMapping cw:configCwMappingList){
							int warehouse_id=cw.getWarehouse_id();
							logger.info("AllotSingleJob allot2BInventory toB start;customerId="+customerId+",warehouse_id="+warehouse_id);
							
							
							batchPickList = batchPickBizImpl.getNeedAllotSaleBatchPickV2(
									physical_warehouse_id, customerId,
									"('E','N')", 2,warehouse_id); //2toB波此单
							
							if (!WorkerUtil.isNullOrEmpty(batchPickList)) {
								
								replenishmentBizImpl.lockReplenishJobByWarehouseCustomer( 0, "",
										"Allot_2B11", batchPickList, physical_warehouse_id,
										customerId, empty1,
										empty2, empty0, empty4,
										0, empty5,0,0,warehouse_id);
							}

							logger.info("AllotSingleJob allot2BInventory toB end;customerId="+customerId+",warehouse_id="+warehouse_id);
						}
					}
				}
			}
		}
		 catch (Exception e) {
			logger.error(methodName + "_error exception=" + e.getMessage(), e);
		}
		logger.info("AllotSingleJob allot2BInventory end;"+paramNameValue);
	}
	
	/**
	 * 3toX波此单 后进先出
	 * @param paramNameValue
	 */
	public void allot2XInventory(String paramNameValue) {
		logger.info("AllotSingleJob allot2XInventory start;"+paramNameValue);
		String methodName = "AllotSingleJob->allot2XInventory";
		try {
			List<Integer> batchPickList = new ArrayList<Integer>();
			Map<String, Integer> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Integer physical_warehouse_id1 = serviceParamsMap.get("physical_warehouse_id") ; //默认为0
			Integer group_id = serviceParamsMap.get("groupId") ; //默认为0
			Integer customer_id = serviceParamsMap.get("customerId"); //默认为0
			
			List<Integer> list=new ArrayList<Integer>();
			List<Integer> list2=new ArrayList<Integer>();
			
			RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
					.getSecurityManager();
			CacheManager cacheManager = securityManager.getCacheManager();
            configCwMappingCache = cacheManager.getCache("configCwMappingCache");
			
			
			if(physical_warehouse_id1-0==0){
				list=warehouseDao.selectPhysicalWarehouseIdList();
			}else{
				list.add(physical_warehouse_id1);
			}
			
			if(group_id!=0){
				list2=warehouseCustomerDao.selectCustomerIdListByGroupId(group_id);
			}
			else if(customer_id-0==0){
				list2=warehouseCustomerDao.selectAllId();
			}
			else{
				list2.add(customer_id);
			}
			
			
			List<Integer> empty0 = new ArrayList<Integer>();
			List<Map> empty1 = new ArrayList<Map>();
			Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
			Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
			List<Product> empty5 = new ArrayList<Product>();
			
			for(Integer physical_warehouse_id : list){
				for (Integer customerId : list2) {

					List<ConfigCwMapping> configCwMappingList=configCwMappingCache.get(physical_warehouse_id+"_"+customerId);
					
					if(null==configCwMappingList){
						configCwMappingList = configCwMappingDao.getConfigCwMappingList(physical_warehouse_id, customerId);
						configCwMappingCache.put(physical_warehouse_id+"_"+ customerId, configCwMappingList);
					}
					if(WorkerUtil.isNullOrEmpty(configCwMappingList)){
						continue ;
					}
					else{
						
						for(ConfigCwMapping cw:configCwMappingList){
							int warehouse_id=cw.getWarehouse_id();
					
							logger.info("AllotSingleJob allot2XInventory tox start;customerId="+customerId+",warehouse_id="+warehouse_id);
							
							// 普通波此单
							batchPickList = batchPickBizImpl.getNeedAllotSaleBatchPickV2(
									physical_warehouse_id, customerId,
									"('E','N')", 3,warehouse_id);// 3toX波此单 后进先出
							
							if (!WorkerUtil.isNullOrEmpty(batchPickList)) {
								
								replenishmentBizImpl.lockReplenishJobByWarehouseCustomer( 0, "",
										"Allot_2X", batchPickList, physical_warehouse_id,
										customerId, empty1,
										empty2, empty0, empty4,
										0, empty5,0,0,warehouse_id);
							}
		
							logger.info("AllotSingleJob allot2XInventory tox end;customerId="+customerId+",warehouse_id="+warehouse_id);
					
						}
				}
			}

		}
		}catch (Exception e) {
			logger.error(methodName + "_error exception=" + e.getMessage(), e);
		}
		logger.info("AllotSingleJob allot2XInventory end;"+paramNameValue);
	}
	
	/**
	 * 预打包任务
	 * @param paramNameValue
	 */
	public void allot2PackInventory(String paramNameValue) {
		logger.info("AllotSingleJob allot2PackInventory start;"+paramNameValue);
		String methodName = "AllotSingleJob->allot2PackInventory";
		try {

			Map<String, Integer> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Integer physical_warehouse_id1 = serviceParamsMap.get("physical_warehouse_id") ; //默认为0
			Integer group_id = serviceParamsMap.get("groupId") ; //默认为0
			Integer customer_id = serviceParamsMap.get("customerId"); //默认为0
			
			List<Integer> list=new ArrayList<Integer>();
			List<Integer> list2=new ArrayList<Integer>();
			
			RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
					.getSecurityManager();
			CacheManager cacheManager = securityManager.getCacheManager();
            configCwMappingCache = cacheManager.getCache("configCwMappingCache");
			
			
			if(physical_warehouse_id1-0==0){
				list=warehouseDao.selectPhysicalWarehouseIdList();
			}else{
				list.add(physical_warehouse_id1);
			}
			
			if(group_id!=0){
				list2=warehouseCustomerDao.selectCustomerIdListByGroupId(group_id);
			}
			else if(customer_id-0==0){
				list2=warehouseCustomerDao.selectAllId();
			}
			else{
				list2.add(customer_id);
			}
			
			
			List<Integer> empty0 = new ArrayList<Integer>();
			List<Map> empty1 = new ArrayList<Map>();
			Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
			Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
			List<Product> empty5 = new ArrayList<Product>();
			
			for(Integer physical_warehouse_id : list){
				for (Integer customerId : list2) {

					
					List<ConfigCwMapping> configCwMappingList=configCwMappingCache.get(physical_warehouse_id+"_"+customerId);
					
					if(null==configCwMappingList){
						configCwMappingList = configCwMappingDao.getConfigCwMappingList(physical_warehouse_id, customerId);
						configCwMappingCache.put(physical_warehouse_id+"_"+ customerId, configCwMappingList);
					}
					if(WorkerUtil.isNullOrEmpty(configCwMappingList)){
						continue ;
					}
					else
					{
						for(ConfigCwMapping cw:configCwMappingList){
							int warehouse_id=cw.getWarehouse_id();
					
							logger.info("AllotSingleJob allot2PackInventory  start;customerId="+customerId+",warehouse_id="+warehouse_id);
							
							replenishmentBizImpl.lockReplenishJobByWarehouseCustomer(0,"","PACK",empty0,
									physical_warehouse_id,customerId,
									empty1,empty2,empty0,empty4,0,empty5,0,0,warehouse_id);
							replenishmentBizImpl.lockReplenishJobByWarehouseCustomer(0,"","UNPACK",empty0,
									physical_warehouse_id,customerId,
									empty1,empty2,empty0,empty4,0,empty5,0,0,warehouse_id);
		
							logger.info("AllotSingleJob allot2PackInventory  end;customerId="+customerId+",warehouse_id="+warehouse_id);
					
						}
					}	
				}
			}

		} catch (Exception e) {
			logger.error(methodName + "_error exception=" + e.getMessage(), e);
		}
		logger.info("AllotSingleJob allot2PackInventory end;"+paramNameValue);
	}
	
	public void allotGtInventory(String paramNameValue) {
		logger.info("AllotSingleJob allotGtInventory start;"+paramNameValue);
		String methodName = "AllotSingleJob->allotGtInventory";
		try {

			Map<String, Integer> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Integer physical_warehouse_id1 = serviceParamsMap.get("physical_warehouse_id"); //默认为0
			Integer group_id = serviceParamsMap.get("groupId"); //默认为0
			Integer customer_id = serviceParamsMap.get("customerId"); //默认为0
			
			List<Integer> list=new ArrayList<Integer>();
			List<Integer> list2=new ArrayList<Integer>();
			
			RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
					.getSecurityManager();
			CacheManager cacheManager = securityManager.getCacheManager();
            configCwMappingCache = cacheManager.getCache("configCwMappingCache");
			
			
			if(physical_warehouse_id1-0==0){
				list=warehouseDao.selectPhysicalWarehouseIdList();
			}else{
				list.add(physical_warehouse_id1);
			}
			
			if(group_id!=0){
				list2=warehouseCustomerDao.selectCustomerIdListByGroupId(group_id);
			}
			else if(customer_id-0==0){
				list2=warehouseCustomerDao.selectAllId();
			}
			else{
				list2.add(customer_id);
			}
			
			List<Integer> empty0 = new ArrayList<Integer>();
			List<Map> empty1 = new ArrayList<Map>();
			Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
			Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
			List<Product> empty5 = new ArrayList<Product>();
			
			for(Integer physical_warehouse_id : list){
				for (Integer customerId : list2) {

					
					List<ConfigCwMapping> configCwMappingList=configCwMappingCache.get(physical_warehouse_id+"_"+customerId);
					
					if(null==configCwMappingList){
						configCwMappingList = configCwMappingDao.getConfigCwMappingList(physical_warehouse_id, customerId);
						configCwMappingCache.put(physical_warehouse_id+"_"+ customerId, configCwMappingList);
					}
					if(WorkerUtil.isNullOrEmpty(configCwMappingList)){
						continue ;
					}
					else
					{
						for(ConfigCwMapping cw:configCwMappingList){
							int warehouse_id=cw.getWarehouse_id();
					
					
							logger.info("AllotSingleJob allotGtInventory toX start;customerId="+customerId+",warehouse_id="+warehouse_id);
												
							// -gt					
							List<Integer> gtOrderIdList = new ArrayList<Integer>();
							gtOrderIdList=orderInfoDao.getGtOrderIdList("NORMAL",physical_warehouse_id,customerId,warehouse_id);
							
							if(!WorkerUtil.isNullOrEmpty(gtOrderIdList)){
								     
								replenishmentBizImpl.lockReplenishJobByWarehouseCustomer(0, "",
										"gt_normal11", gtOrderIdList, physical_warehouse_id,
										customerId, empty1,
											empty2, empty0, empty4,
											0, empty5,0,0,warehouse_id);
								}
							
							
							logger.info("AllotSingleJob allotGtInventory -gt normal end;customerId="+customerId+",warehouse_id="+warehouse_id);
							
							//gt订单 二手
							gtOrderIdList=orderInfoDao.getGtOrderIdList("DEFECTIVE",physical_warehouse_id,customerId,warehouse_id);
							if(!WorkerUtil.isNullOrEmpty(gtOrderIdList)){
								 
								replenishmentBizImpl.lockReplenishJobByWarehouseCustomer(0, "",
										"gt_defective11", gtOrderIdList, physical_warehouse_id,
										customerId, empty1,
											empty2, empty0, empty4,
											0, empty5,0,0,warehouse_id);
								}
							
		
							logger.info("AllotSingleJob allotGtInventory -gt defective end;customerId="+customerId+",warehouse_id="+warehouse_id);
						}
					}	
				}
			}
							
		} catch (Exception e) {
			logger.error(methodName + "_error exception=" + e.getMessage(), e);
		}
		logger.info("AllotSingleJob allotGtInventory end;"+paramNameValue);
	}
	
	
	private Map<String, Integer> getServiceParamsMap(String paramNameValue) {
		Map<String, String> paramNameValueMap = TaskUtils.getParamNameValueMap(paramNameValue);
		Map<String, Integer> serviceParamsMap = new HashMap<String, Integer>();
		
		Integer physical_warehouse_id = null==paramNameValueMap.get("physical_warehouse_id") ? 0 : Integer.parseInt(paramNameValueMap.get("physical_warehouse_id")) ; //默认为0
		Integer groupId =  null==paramNameValueMap.get("groupId")? 0 : Integer.parseInt(paramNameValueMap.get("groupId")) ; //默认为0
		Integer customerId = null==paramNameValueMap.get("customerId")? 0 : Integer.parseInt(paramNameValueMap.get("customerId")); //默认为0
		
		serviceParamsMap.put("groupId", groupId);
		serviceParamsMap.put("customerId", customerId);
		serviceParamsMap.put("physical_warehouse_id", physical_warehouse_id);
		
		return serviceParamsMap;
	}
}
