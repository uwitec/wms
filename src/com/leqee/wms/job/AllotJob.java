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
import com.leqee.wms.entity.ConfigReplenishmentUrgent;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.schedule.job.TaskUtils;

@Service
public class AllotJob {

	private Logger logger = Logger.getLogger(AllotJob.class);
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
	
	public void allotInventory(String paramNameValue) {
		logger.info("AllotJob allotInventory start;"+paramNameValue);
		String methodName = "AllotJob->allotInventory";
		try {
			List<Integer> batchPickList = new ArrayList<Integer>();
			List<Integer> batchPickList1 = new ArrayList<Integer>();

			Map<String, Integer> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Integer physical_warehouse_id1=serviceParamsMap.get("physical_warehouse_id");
			Integer groupId=serviceParamsMap.get("groupId");
			Integer customer_id=serviceParamsMap.get("customerId");

			List<Integer> list=new ArrayList<Integer>();
			List<Integer> list2=new ArrayList<Integer>();
			
			if(physical_warehouse_id1-0==0){
				list=warehouseDao.selectPhysicalWarehouseIdList();
			}else{
				list.add(physical_warehouse_id1);
			}
			
			if(groupId!=0){
				list2=warehouseCustomerDao.selectCustomerIdListByGroupId(groupId);
			}
			else if(customer_id-0==0){
				list2=warehouseCustomerDao.selectAllId();
			}
			else{
				list2.add(customer_id);
			}
			
			RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
					.getSecurityManager();
			CacheManager cacheManager = securityManager.getCacheManager();
            configCwMappingCache = cacheManager.getCache("configCwMappingCache");

			
			for(Integer physical_warehouse_id:list){
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
							
							logger.info("AllotJob allotInventory toX start;customerId="+customerId+",warehouse_id="+warehouse_id);
							
							// toX波此单
							batchPickList = batchPickBizImpl.getNeedAllotSaleBatchPick(
									physical_warehouse_id, customerId,
									"E", 3,warehouse_id); // 3toX波此单
							batchPickList1 = batchPickBizImpl
									.getNeedAllotSaleBatchPick(physical_warehouse_id,
											customerId, "N", 3,warehouse_id); // 3toX波此单
							batchPickList.addAll(batchPickList1);
							allotBatchPick(batchPickList, physical_warehouse_id, customerId, "Allot_2X",warehouse_id);
							
							logger.info("AllotJob allotInventory toX end;customerId="+customerId+",warehouse_id="+warehouse_id);
							
							// -gt
							
							List<Integer> gtOrderIdList = new ArrayList<Integer>();
							gtOrderIdList=orderInfoDao.getGtOrderIdList("NORMAL",physical_warehouse_id,customerId,warehouse_id);
							allotGtOrders(gtOrderIdList, physical_warehouse_id,
									customerId, "gt_normal",warehouse_id);
							logger.info("AllotJob allotInventory -gt normal end;customerId="+customerId);
							//gt订单 二手
							gtOrderIdList=orderInfoDao.getGtOrderIdList("DEFECTIVE",physical_warehouse_id,customerId,warehouse_id);
							allotGtOrders(gtOrderIdList,physical_warehouse_id,
									customerId, "gt_defective",warehouse_id);
		
							logger.info("AllotJob allotInventory -gt defective end;customerId="+customerId+",warehouse_id="+warehouse_id);
							
							
							
							// 普通波此单
							batchPickList = batchPickBizImpl.getNeedAllotSaleBatchPick(
									physical_warehouse_id, customerId,
									"E", 1,warehouse_id); // 1普通波此单
							batchPickList1 = batchPickBizImpl
									.getNeedAllotSaleBatchPick(physical_warehouse_id,
											customerId, "N", 1,warehouse_id); // 1普通波此单
		
							batchPickList.addAll(batchPickList1);
							allotBatchPick(batchPickList, physical_warehouse_id, customerId, "Allot",warehouse_id);
		
							logger.info("AllotJob allotInventory toB start;customerId="+customerId+",warehouse_id="+warehouse_id);
							
							// toB波此单
							batchPickList = batchPickBizImpl.getNeedAllotSaleBatchPick(
									physical_warehouse_id, customerId,
									"E", 2,warehouse_id); // 2toB波此单
							batchPickList1 = batchPickBizImpl
									.getNeedAllotSaleBatchPick(physical_warehouse_id,
											customerId, "N", 2,warehouse_id); // 2toB波此单
							batchPickList.addAll(batchPickList1);
							allotBatchPick(batchPickList, physical_warehouse_id, customerId, "Allot_2B",warehouse_id);
							
							logger.info("AllotJob allotInventory toB end;customerId="+customerId+",warehouse_id="+warehouse_id);
						}
					}

				}
			
			}
				

		} catch (Exception e) {
			logger.error(methodName + "_error exception=" + e.getMessage(), e);
		}
		logger.info("AllotJob allotInventory end;"+paramNameValue);
	}

	private Map<String, Integer> getServiceParamsMap(String paramNameValue) {
		Map<String, String> paramNameValueMap = TaskUtils.getParamNameValueMap(paramNameValue);
		Map<String, Integer> serviceParamsMap = new HashMap<String, Integer>();
		
		Integer physical_warehouse_id = paramNameValueMap.get("physical_warehouse_id") == null ? 0 : Integer.parseInt(paramNameValueMap.get("physical_warehouse_id")) ; //默认为0
		Integer groupId = paramNameValueMap.get("groupId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("groupId")) ; //默认为0
		Integer customerId = paramNameValueMap.get("customerId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("customerId")); //默认为0
		
		serviceParamsMap.put("groupId", groupId);
		serviceParamsMap.put("customerId", customerId);
		serviceParamsMap.put("physical_warehouse_id", physical_warehouse_id);
		
		return serviceParamsMap;
	}

	/**
	 * 处理 -gt订单
	 * @param gtOrderIdList
	 * @param physical_warehouse_id
	 * @param customer_id
	 * @param jobType
	 * @param warehouse_id 
	 */
	private void allotGtOrders(List<Integer> gtOrderIdList,
			Integer physical_warehouse_id, Integer customer_id, String jobType, int warehouse_id) {
		if(!WorkerUtil.isNullOrEmpty(gtOrderIdList)){
			//pack任务
			List<Integer> empty0 = new ArrayList<Integer>();
			List<Map> empty1 = new ArrayList<Map>();
			Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
			Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
			List<Product> empty5 = new ArrayList<Product>();
			replenishmentBizImpl.lockReplenishJobByWarehouseCustomer(0, "",
						jobType, gtOrderIdList, physical_warehouse_id,
						customer_id, empty1,
						empty2, empty0, empty4,
						0, empty5,0,0,warehouse_id);
			}
		}

	private void allotBatchPick(List<Integer> batchPickList, Integer physical_warehouse_id,
			Integer customerId, String jobType, int warehouse_id) {
		if (!WorkerUtil.isNullOrEmpty(batchPickList)) {

			List<Integer> empty0 = new ArrayList<Integer>();
			List<Map> empty1 = new ArrayList<Map>();
			Map<String, List<Map>> empty2 = new HashMap<String, List<Map>>();
			Map<String, ConfigReplenishment> empty4 = new HashMap<String, ConfigReplenishment>();
			List<Product> empty5 = new ArrayList<Product>();

			replenishmentBizImpl.lockReplenishJobByWarehouseCustomer( 0, "",
					jobType, batchPickList, physical_warehouse_id,
					customerId, empty1,
					empty2, empty0, empty4,
					0, empty5,0,0,warehouse_id);

		}
	}

}
