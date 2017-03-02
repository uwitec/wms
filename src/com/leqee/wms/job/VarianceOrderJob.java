package com.leqee.wms.job;

import java.util.ArrayList;
import java.util.Date;
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
import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.dao.ConfigCwMappingDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.ConfigCwMapping;
import com.leqee.wms.entity.OrderInfo;

@Service
public class VarianceOrderJob  extends CommonJob {
	private Logger logger = Logger.getLogger(VarianceOrderJob.class);
	
	private Cache<String, List<ConfigCwMapping>> configCwMappingCache;
	
	@Autowired
	WarehouseDao warehouseDao;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	@Autowired
	OrderInfoDao  orderInfoDao;
	@Autowired
	ConfigCwMappingDao configCwMappingDao;
	
	@Autowired
	InventoryBiz inventoryBiz;
	public void varianceOrder(String paramNameValue){
		logger.info("reserveOrders-varianceOrder start paramNameValue:"+paramNameValue);
		Map<String, Object> serviceParamsMap = getServiceParamsMapByDlYao(paramNameValue);
		Integer physical_warehouse_id = serviceParamsMap.get("physical_warehouse_id") == null ? 0 : Integer.parseInt(serviceParamsMap.get("physical_warehouse_id").toString()) ; //默认为0
		Integer group_id = serviceParamsMap.get("groupId") == null ? 0 : Integer.parseInt(serviceParamsMap.get("groupId").toString()) ; //默认为0
		Integer customer_id = serviceParamsMap.get("customerId") == null? 0 : Integer.parseInt(serviceParamsMap.get("customerId").toString()); //默认为0
		
		List<Integer> list=new ArrayList<Integer>();
		List<Integer> list2=new ArrayList<Integer>();
		
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
        configCwMappingCache = cacheManager.getCache("configCwMappingCache");
		
		if(physical_warehouse_id-0==0){
			list=warehouseDao.selectPhysicalWarehouseIdList();
		}else{
			list.add(physical_warehouse_id);
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
		
		for(int p_id:list){
			for(int c_id:list2){
				
				List<ConfigCwMapping> configCwMappingList=configCwMappingCache.get(p_id+"_"+c_id);
				
				if(null==configCwMappingList){
					configCwMappingList = configCwMappingDao.getConfigCwMappingList(p_id, c_id);
					configCwMappingCache.put(p_id+"_"+c_id, configCwMappingList);
				}
				if(WorkerUtil.isNullOrEmpty(configCwMappingList)){
					continue ;
				}else{
					for(ConfigCwMapping cw:configCwMappingList){
						
						int warehouse_id=cw.getWarehouse_id();
					
						List<OrderInfo> varianceMinusOrderInfos=orderInfoDao.selectAllVarianceMinusOrderIdsForUpdate(p_id,c_id,"VARIANCE_MINUS","Y",warehouse_id);
						List<Integer> varianceMinusImproveOrderIds=new ArrayList<Integer>();
						
						if(null!=varianceMinusOrderInfos&&varianceMinusOrderInfos.size()>0){
							for(OrderInfo oi:varianceMinusOrderInfos){
								
								//表明不是全新变二手
								if(null==oi.getParent_order_id()||0==oi.getParent_order_id()){
									varianceMinusImproveOrderIds.add(oi.getOrder_id());
								}
							}
						}else{
							continue;
						}
						
						if(null!=varianceMinusImproveOrderIds&&varianceMinusImproveOrderIds.size()==0){
							continue;
						}
						
						List<Map> varianceMinusImproveOrderGoodsList=orderInfoDao.selectVarianceMinusImproveOrderGoodsMap(varianceMinusImproveOrderIds);
						Map<String,List<Map>> varianceMinusImproveOrderGoodsMap=new HashMap<String,List<Map>>();
						List<Integer> productIdList=new ArrayList<Integer>();
						List<Integer> orderIdList=new ArrayList<Integer>();
						List<Integer> orderGoodIdList=new ArrayList<Integer>();
						for(Map m:varianceMinusImproveOrderGoodsList){
							Integer order_id=Integer.parseInt(m.get("order_id").toString());
							if(!productIdList.contains(Integer.parseInt(m.get("product_id").toString()))){
								productIdList.add(Integer.parseInt(m.get("product_id").toString()));
							}
							
							if(!orderGoodIdList.contains(Integer.parseInt(m.get("order_goods_id").toString()))){
								orderGoodIdList.add(Integer.parseInt(m.get("order_goods_id").toString()));
							}
							
							if(!orderIdList.contains(order_id)){
								orderIdList.add(order_id);
							}
							
							if(null==varianceMinusImproveOrderGoodsMap.get(order_id+"")){
								List<Map> listTemp = new ArrayList<Map>();
								listTemp.add(m);
								varianceMinusImproveOrderGoodsMap.put(order_id+"", listTemp);
							}
							else{
								List<Map> listTemp = varianceMinusImproveOrderGoodsMap.get(order_id+"");
								listTemp.add(m);
							}
						}
						
						if(!WorkerUtil.isNullOrEmpty(orderIdList)){
							inventoryBiz.doVarianceMinus(p_id,c_id,varianceMinusImproveOrderGoodsMap,productIdList,orderIdList,orderGoodIdList,warehouse_id);
						}

					}
						
					}
					
				}
				
				
		}
		
	
		
	}
	
	
	public void varianceOrderAdd(String paramNameValue){
		logger.info("reserveOrders-run varianceOrderAdd paramNameValue:"+paramNameValue);
		Map<String, Object> serviceParamsMap = getServiceParamsMapByDlYao(paramNameValue);
		Integer physical_warehouse_id = serviceParamsMap.get("physical_warehouse_id") == null ? 0 : Integer.parseInt(serviceParamsMap.get("physical_warehouse_id").toString()) ; //默认为0
		Integer group_id = serviceParamsMap.get("groupId") == null ? 0 : Integer.parseInt(serviceParamsMap.get("groupId").toString()) ; //默认为0
		Integer customer_id = serviceParamsMap.get("customerId") == null? 0 : Integer.parseInt(serviceParamsMap.get("customerId").toString()); //默认为0
		
		List<Integer> list=new ArrayList<Integer>();
		List<Integer> list2=new ArrayList<Integer>();
		
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
        configCwMappingCache = cacheManager.getCache("configCwMappingCache");
		
		
		if(physical_warehouse_id-0==0){
			list=warehouseDao.selectPhysicalWarehouseIdList();
		}else{
			list.add(physical_warehouse_id);
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
		
		for(int p_id:list){
			for(int c_id:list2){
				
				List<ConfigCwMapping> configCwMappingList=configCwMappingCache.get(p_id+"_"+c_id);
				
				if(null==configCwMappingList){
					configCwMappingList = configCwMappingDao.getConfigCwMappingList(p_id, c_id);
					configCwMappingCache.put(p_id+"_"+c_id, configCwMappingList);
				}
				if(WorkerUtil.isNullOrEmpty(configCwMappingList)){
					continue ;
				}else{
					for(ConfigCwMapping cw:configCwMappingList){
						
						int warehouse_id=cw.getWarehouse_id();
				
						List<OrderInfo> varianceAddOrderInfos=orderInfoDao.selectAllVarianceMinusOrderIdsForUpdate(p_id,c_id,"VARIANCE_ADD","N",warehouse_id);
						List<Integer> varianceAddImproveOrderIds=new ArrayList<Integer>();
						
						if(null!=varianceAddOrderInfos&&varianceAddOrderInfos.size()>0){
							for(OrderInfo oi:varianceAddOrderInfos){
								if(null==oi.getParent_order_id()||0==oi.getParent_order_id()){
									varianceAddImproveOrderIds.add(oi.getOrder_id());
								}
							}
						}else{
							continue;
						}
						
						//表明不是全新变二手
						if(null!=varianceAddImproveOrderIds&&varianceAddImproveOrderIds.size()==0){
							continue;
						}
						
						List<Map> varianceAddImproveOrderGoodsList=orderInfoDao.selectVarianceMinusImproveOrderGoodsMap(varianceAddImproveOrderIds);
						Map<String,List<Map>> varianceAddImproveOrderGoodsMap=new HashMap<String,List<Map>>();
						List<Integer> productIdList=new ArrayList<Integer>();
						List<Integer> orderIdList=new ArrayList<Integer>();
						List<Integer> orderGoodIdList=new ArrayList<Integer>();
						for(Map m:varianceAddImproveOrderGoodsList){
							Integer order_id=Integer.parseInt(m.get("order_id").toString());
							if(!productIdList.contains(Integer.parseInt(m.get("product_id").toString()))){
								productIdList.add(Integer.parseInt(m.get("product_id").toString()));
							}
							
							if(!orderGoodIdList.contains(Integer.parseInt(m.get("order_goods_id").toString()))){
								orderGoodIdList.add(Integer.parseInt(m.get("order_goods_id").toString()));
							}
							
							if(!orderIdList.contains(order_id)){
								orderIdList.add(order_id);
							}
							
							if(null==varianceAddImproveOrderGoodsMap.get(order_id+"")){
								List<Map> listTemp = new ArrayList<Map>();
								listTemp.add(m);
								varianceAddImproveOrderGoodsMap.put(order_id+"", listTemp);
							}
							else{
								List<Map> listTemp = varianceAddImproveOrderGoodsMap.get(order_id+"");
								listTemp.add(m);
							}
						}
						
						if(!WorkerUtil.isNullOrEmpty(orderIdList)){
							inventoryBiz.doVarianceAdd(p_id,c_id,varianceAddImproveOrderGoodsMap,productIdList,orderIdList,orderGoodIdList,warehouse_id);
						}

					}
				}
		
			}
		}
		
	}
}
