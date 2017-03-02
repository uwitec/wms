package com.leqee.wms.job;

import java.util.ArrayList;
import java.util.Calendar;
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

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.api.util.WorkerUtil;
import com.leqee.wms.biz.impl.BatchPickBizImpl;
import com.leqee.wms.dao.BatchPickDao;
import com.leqee.wms.dao.BatchPickTaskDao;
import com.leqee.wms.dao.ConfigCwMappingDao;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.ConfigPrintDispatchBillDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ScheduleJobDao;
import com.leqee.wms.dao.ShopDao;
import com.leqee.wms.dao.UserActionBatchPickDao;
import com.leqee.wms.dao.UserActionOrderDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.ConfigCwMapping;
import com.leqee.wms.entity.ConfigPrintDispatch;
import com.leqee.wms.entity.SameDetails;
import com.leqee.wms.entity.ScheduleJob;
import com.leqee.wms.entity.Shop;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.schedule.job.TaskUtils;
import com.leqee.wms.util.SequenceUtil;

@Service
public class BatchPickJob {

	private Cache<String, List<ConfigCwMapping>> configCwMappingCache;
	private Cache<String, List<Shop>> shopNameFileCache;
	private Cache<String, String> isPhysicalOutSourceCache;
	private Cache<String, WarehouseCustomer> customerCache;
	private Cache<String, String> isBatchByShipmentCache;
	
	@Autowired
	BatchPickBizImpl batchPickBizImpl;
	@Autowired
	BatchPickTaskDao batchPickTaskDao;
	@Autowired
	UserActionBatchPickDao userActionBatchPickDao;
	@Autowired
	UserActionOrderDao userActionOrderDao;
	@Autowired
	OrderInfoDao orderInfoDao;

	@Autowired
	OrderProcessDao orderProcessDao;

	@Autowired
	BatchPickDao batchPickDao;

	@Autowired
	WarehouseDao warehouseDao;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	@Autowired
	ConfigPrintDispatchBillDao configPrintDispatchBillDao;
	
	@Autowired
	ConfigDao configDao;
	@Autowired
	ShopDao shopDao;
	@Autowired
	ConfigCwMappingDao configCwMappingDao;
	@Autowired
	private ScheduleJobDao scheduleJobDao;
	/** 日志对象 */
	private Logger logger = Logger.getLogger(BatchPickJob.class);

	public void createSingleBatchPick(String param) {

		logger.info("BatchPickJob createSingleBatchPick start; param="+param);
		
		String[] params = param.split(":");
		int minSize = Integer.parseInt(params[0].replace("param=", ""));
		int maxSize = Integer.parseInt(params[1]);
		int minWeight = Integer.parseInt(params[2]);
		int maxWeight = Integer.parseInt(params[3]);
		int level = Integer.parseInt(params[4]);
		int physical_warehouse_id = Integer.parseInt(params[5]);
		int size = Integer.parseInt(params[6]);
		int customer_id = Integer.parseInt(params[8]);
		Map<String, Object> parmMap = new HashMap<String, Object>();

		logger.info("BatchPickJob createSingleBatchPick start; physical_warehouse_id="+physical_warehouse_id+",customer_id"+customer_id);
		
		// 用来接收查询的订单
		List<Integer> orderInfoList1 = new ArrayList<Integer>();
		List<Integer> orderInfoList2 = new ArrayList<Integer>();

		// 用来接收完全相同的订单
		List<SameDetails> sdList1 = new ArrayList<SameDetails>(); // 完全相同

		parmMap.put("physical_warehouse_id", physical_warehouse_id);
		parmMap.put("minSize", minSize);

		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		
		shopNameFileCache = cacheManager.getCache("shopNameFileCache");
		
		

		parmMap.put("time", DateUtils.getDateString(0, 1, 0, 0, 0, 0));
		parmMap.put("customer_id", customer_id);
		
		WarehouseCustomer customer=warehouseCustomerDao.selectByCustomerId(customer_id);
		
		List<Shop> shopList = shopNameFileCache.get(customer_id + "");
		
		if (shopList == null || shopList.isEmpty()
				|| shopList.size() == 0) {
			shopList = orderInfoDao.selectShopNameNullCacheV3(parmMap);
			
			for(Shop shop:shopList){
				if(null==shop.getFile_name()||"".equalsIgnoreCase(shop.getFile_name())){
					shop.setFile_name(customer.getDispatch_bill_file_name());
				}
			}
			
			shopNameFileCache.put(customer_id + "", shopList);
		}
	
		Map<String,List<String>> shopFileOmsShopIdMap = new HashMap<String,List<String>>();
		
		if(!WorkerUtil.isNullOrEmpty(shopList)){
			for(Shop shop:shopList){
				if(null==shopFileOmsShopIdMap.get(shop.getFile_name())){
					List<String> listTemp=new ArrayList<String>();
					listTemp.add(shop.getOms_shop_id());
					shopFileOmsShopIdMap.put(shop.getFile_name(), listTemp);
				}else{
					List<String> listTemp=shopFileOmsShopIdMap.get(shop.getFile_name());
					listTemp.add(shop.getOms_shop_id());
				}
			}
		}
		

			isPhysicalOutSourceCache = cacheManager.getCache("isPhysicalOutSourceCache");
			
			String isOutSource=isPhysicalOutSourceCache.get(physical_warehouse_id+"");
			
			if(null==isOutSource){
				isOutSource = configDao.getConfigValueByFrezen(physical_warehouse_id, 0, "IS_OUTSOURCE_PHYSICAL");
				isPhysicalOutSourceCache.put(physical_warehouse_id+"", isOutSource);
			}
			
			isBatchByShipmentCache = cacheManager.getCache("isBatchByShipmentCache");
			
			String isBatchByShipment=isBatchByShipmentCache.get(physical_warehouse_id+"_"+customer_id);
			
			if(null==isBatchByShipment){
				isBatchByShipment = configDao.getConfigValueByFrezen(physical_warehouse_id, customer_id, "IS_BATCH_BY_SHIPMENT");
				isBatchByShipmentCache.put(physical_warehouse_id+"_"+ customer_id, isBatchByShipment);
			}
			
			configCwMappingCache = cacheManager.getCache("configCwMappingCache");
			
			List<ConfigCwMapping> configCwMappingList=configCwMappingCache.get(physical_warehouse_id+"_"+customer_id);
			
			if(null==configCwMappingList){
				configCwMappingList = configCwMappingDao.getConfigCwMappingList(physical_warehouse_id, customer_id);
				configCwMappingCache.put(physical_warehouse_id+"_"+ customer_id, configCwMappingList);
			}
			if(WorkerUtil.isNullOrEmpty(configCwMappingList)){
				return ;
			}else{
				for(ConfigCwMapping cw:configCwMappingList){
					for (List<String> li : shopFileOmsShopIdMap.values()) {
						
						logger.info("BatchPickJob createSingleBatchPick level 1, physical_warehouse_id="+physical_warehouse_id+",customer_id"+customer_id+",warehouse_id"+cw.getWarehouse_id());

						List<String> bpList=new ArrayList<String>();
						
						// 第一级 高优先级订单
						if(!"1".equalsIgnoreCase(isOutSource)){
							orderInfoList1 = orderInfoDao.getOrderLevel3New(
									physical_warehouse_id, customer_id,cw.getWarehouse_id(), li,"N");

							
							bpList=getBpList(orderInfoList1,size);
							
							//@change by dlyao for warehouse's 20160816
							//batchPickBizImpl.createLevel3BatchPick(orderInfoList1,physical_warehouse_id,size,bpList);
							batchPickBizImpl.createLevel3NewBatchPick(orderInfoList1,physical_warehouse_id,size,bpList,cw.getWarehouse_id());
						}
						
						
						// 查询条件 parmMap需要填充参数 等级二和等级三 可共用
						parmMap.put("oms_shop_id", li);
						parmMap.put("end", DateUtils.getDateString(0, 0, 0, 0, 10, 0));
						parmMap.put("start", DateUtils.getDateString(0, 0, 10, 0, 0, 0));
						parmMap.put("warehouse_id", cw.getWarehouse_id());
						
						// 第二级 相同商品相同数目订单
					
						if(level>=2){
							logger.info("BatchPickJob createSingleBatchPick level 2, isBatchByShipment = "+isBatchByShipment+", physical_warehouse_id="+physical_warehouse_id+",customer_id"+customer_id);
							if(!"1".equals(isBatchByShipment)){
								sdList1 = batchPickBizImpl.batchPickAllSame(parmMap);
								for (SameDetails sd : sdList1) {
									if (sd.getSku_sum() >= size) {
										// 商品种类信息
										String product_key = sd.getProduct_key();

										// 每种商品的件数
										String product_num = sd.getProduct_num();
										parmMap.put("product_key", product_key);
										parmMap.put("product_num", product_num);
										parmMap.put("type", "list1");
										parmMap.put("order_num", sd.getSku_sum());
										parmMap.put("issf", "notsf");

										orderInfoList2 = batchPickBizImpl
												.batchPickAllOrderListByParm(parmMap);
										
										

										if (orderInfoList2.size() >= minSize&&sd.getSerial_nums()==0) {
											
											bpList=getBpList(orderInfoList2,maxSize);
											
											batchPickBizImpl.createBatchBatchPick(
													orderInfoList2, maxSize,physical_warehouse_id,bpList,minSize,cw.getWarehouse_id());
										} else if (orderInfoList2.size() >= size&&sd.getSerial_nums()==0) {
											bpList=getBpList(orderInfoList2,size);
											batchPickBizImpl.createBatchBatchPick(
													orderInfoList2, maxSize,physical_warehouse_id,bpList,size,cw.getWarehouse_id());
//											batchPickBizImpl
//													.createLevel3BatchPick(orderInfoList2,physical_warehouse_id,size,bpList);
										}
										
										parmMap.put("issf", "sf");

										orderInfoList2 = batchPickBizImpl
												.batchPickAllOrderListByParm(parmMap);

										if (orderInfoList2.size() >= minSize&&sd.getSerial_nums()==0) {
											
											bpList=getBpList(orderInfoList2,maxSize);
											batchPickBizImpl.createBatchBatchPick(
													orderInfoList2, maxSize,physical_warehouse_id,bpList,minSize,cw.getWarehouse_id());
										} else if (orderInfoList2.size() >= size&&sd.getSerial_nums()==0) {

											bpList=getBpList(orderInfoList2,maxSize);
											batchPickBizImpl.createBatchBatchPick(
													orderInfoList2, maxSize,physical_warehouse_id,bpList,size,cw.getWarehouse_id());
//											batchPickBizImpl
//													.createLevel3BatchPick(orderInfoList2,physical_warehouse_id,size,bpList);
										}
									}

								}
							}else{
								parmMap.put("size", size);
								sdList1 = orderInfoDao.selectOrderInfoListForBatchPickByShipment(parmMap);
								
								for (SameDetails sd : sdList1) {
									
									String product_key = sd.getProduct_key();
									// 每种商品的件数
									String product_num = sd.getProduct_num();
									parmMap.put("product_key", product_key);
									parmMap.put("product_num", product_num);
									parmMap.put("shipping_id", sd.getShipping_id());
									parmMap.put("type", "list1");
									parmMap.put("order_num", sd.getSku_sum());

									orderInfoList2 = orderInfoDao.batchPickAllOrderListByParmByShipment(parmMap);
									
									if (orderInfoList2.size() >= minSize&&sd.getSerial_nums()==0) {
										
										bpList=getBpList(orderInfoList2,maxSize);
										
										batchPickBizImpl.createBatchBatchPick(
												orderInfoList2, maxSize,physical_warehouse_id,bpList,size,cw.getWarehouse_id());
									}
								
							}
							
						}
					}	

						if(level>=3){
							logger.info("BatchPickJob createSingleBatchPick level 3, physical_warehouse_id="+physical_warehouse_id+",customer_id"+customer_id);
							// 第三级 相同商品数目不同的其他订单
							sdList1 = batchPickBizImpl.batchPickAllSameV3(parmMap);

							for (SameDetails sd : sdList1) {

								// 商品种类信息
								String product_key = sd.getProduct_key();

								parmMap.put("product_key", product_key);
								parmMap.put("type", "list2");
								parmMap.put("order_num", "");

								orderInfoList2 = batchPickBizImpl
										.batchPickAllOrderListByParmV3(parmMap);
								if (orderInfoList2.size() >= size) {
									bpList=getBpList(orderInfoList2,size);
									batchPickBizImpl.createLevel3BatchPick(orderInfoList2,physical_warehouse_id,size,bpList,cw.getWarehouse_id());
								}

							}
						}
						

						// 商品不同 但处于同一个通道 库区 品牌

						// 杂单
						if(level>=4){
							logger.info("BatchPickJob createSingleBatchPick level 4,, physical_warehouse_id="+physical_warehouse_id+",customer_id"+customer_id);
							parmMap.put("is2B", "('N','W')");
							orderInfoList2 = batchPickBizImpl
									.batchPickAllLastOrderListByParm(parmMap);
							if (orderInfoList2.size() >= size) {

								bpList=getBpList(orderInfoList2,size);
								batchPickBizImpl
										.createLevel3BatchPick(orderInfoList2,physical_warehouse_id,size,bpList,cw.getWarehouse_id());
							}
					
						}
						
						//外包仓不生成toB toX波次
						if(!"1".equalsIgnoreCase(isOutSource)){
							logger.info("BatchPickJob createSingleBatchPick toB, physical_warehouse_id="+physical_warehouse_id+",customer_id"+customer_id);
							parmMap.put("is2B", "('Y')");
							orderInfoList2 = batchPickBizImpl
									.batchPickAllLastOrderListByParm(parmMap);
							bpList=getBpList(orderInfoList2,1);
							batchPickBizImpl
							.createToBBatchPick(orderInfoList2,physical_warehouse_id,1,bpList,"TOB",cw.getWarehouse_id());
							
							logger.info("BatchPickJob createSingleBatchPick toX, physical_warehouse_id="+physical_warehouse_id+",customer_id"+customer_id);
							parmMap.put("is2B", "('X')");
							orderInfoList2 = batchPickBizImpl
									.batchPickAllLastOrderListByParm(parmMap);
							bpList=getBpList(orderInfoList2,1);
							batchPickBizImpl
							.createToBBatchPick(orderInfoList2,physical_warehouse_id,1,bpList,"TOX",cw.getWarehouse_id());
						}
					}

				}
			}
			
			
		logger.info("BatchPickJob createSingleBatchPick end; param="+param);
	
	}
	
	/**
	 * 全仓库波次
	 * @param param
	 */
	public void createBatchPick(String param) {

		logger.info("BatchPickJob createBatchPick start; param="+param);
		
		String[] params = param.split(":");
		int minSize = Integer.parseInt(params[0].replace("param=", ""));
		int maxSize = Integer.parseInt(params[1]);
		int minWeight = Integer.parseInt(params[2]);
		int maxWeight = Integer.parseInt(params[3]);
		int level = Integer.parseInt(params[4]);
		int physical_warehouse_id = Integer.parseInt(params[5]);
		int size = Integer.parseInt(params[6]);
		Map<String, Object> parmMap = new HashMap<String, Object>();
		String job_group = "batchpick_" + physical_warehouse_id;
		List<ScheduleJob> scheduleJobList = scheduleJobDao
				.selectTaskJob(job_group);

		// 用来接收查询的订单
		List<Integer> orderInfoList1 = new ArrayList<Integer>();
		List<Integer> orderInfoList2 = new ArrayList<Integer>();

		// 用来接收完全相同的订单
		List<SameDetails> sdList1 = new ArrayList<SameDetails>(); // 完全相同

		parmMap.put("physical_warehouse_id", physical_warehouse_id);
		parmMap.put("minSize", minSize);

		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		
		shopNameFileCache = cacheManager.getCache("shopNameFileCache");
		
		List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>(); // 货主列表
		customers = warehouseCustomerDao.selectAllInit();

		parmMap.put("time", DateUtils.getDateString(0, 1, 0, 0, 0, 0));

		
		
		

		
		for (WarehouseCustomer whc : customers) {
			boolean mark = true;
			parmMap.put("customer_id", whc.getCustomer_id().intValue());
			for (ScheduleJob sj : scheduleJobList) {
				
				if (Integer.parseInt(sj.getDescription()) - whc
						.getCustomer_id()==0) {
					mark = false;
					if(sj.getJobStatus().equals("0")){
						mark = true;
					}
					if(Integer.parseInt(sj.getParamNameValue().split(":")[10])<DateUtils.getNowHours()){
						mark = true;
					}
				}
			}

			if (mark) {
				
				List<Shop> shopList = shopNameFileCache.get(whc.getCustomer_id().intValue() + "");

				if (shopList == null || shopList.isEmpty()
						|| shopList.size() == 0) {
					shopList = orderInfoDao.selectShopNameNullCacheV3(parmMap);
					
					for(Shop shop:shopList){
						if(null==shop.getFile_name()||"".equalsIgnoreCase(shop.getFile_name())){
							shop.setFile_name(whc.getDispatch_bill_file_name());
						}
					}
					
					shopNameFileCache.put(whc.getCustomer_id().intValue() + "", shopList);
				}
			
				Map<String,List<String>> shopFileOmsShopIdMap = new HashMap<String,List<String>>();
				
				if(!WorkerUtil.isNullOrEmpty(shopList)){
					for(Shop shop:shopList){
						if(null==shopFileOmsShopIdMap.get(shop.getFile_name())){
							List<String> listTemp=new ArrayList<String>();
							listTemp.add(shop.getOms_shop_id());
							shopFileOmsShopIdMap.put(shop.getFile_name(), listTemp);
						}else{
							List<String> listTemp=shopFileOmsShopIdMap.get(shop.getFile_name());
							listTemp.add(shop.getOms_shop_id());
						}
					}
				}
				

				configCwMappingCache = cacheManager.getCache("configCwMappingCache");
				
				List<ConfigCwMapping> configCwMappingList=configCwMappingCache.get(physical_warehouse_id+"_"+whc.getCustomer_id());
				
				if(null==configCwMappingList){
					configCwMappingList = configCwMappingDao.getConfigCwMappingList(physical_warehouse_id, whc.getCustomer_id());
					configCwMappingCache.put(physical_warehouse_id+"_"+ whc.getCustomer_id(), configCwMappingList);
				}
				
					isPhysicalOutSourceCache = cacheManager.getCache("isPhysicalOutSourceCache");
					
					String isOutSource=isPhysicalOutSourceCache.get(physical_warehouse_id+"");
					
					if(null==isOutSource){
						isOutSource = configDao.getConfigValueByFrezen(physical_warehouse_id, 0, "IS_OUTSOURCE_PHYSICAL");
						isPhysicalOutSourceCache.put(physical_warehouse_id+"", isOutSource);
					}
					
					isBatchByShipmentCache = cacheManager.getCache("isBatchByShipmentCache");
					String isBatchByShipment=isBatchByShipmentCache.get(physical_warehouse_id+"_"+whc.getCustomer_id().intValue());
					
					if(null==isBatchByShipment){
						isBatchByShipment = configDao.getConfigValueByFrezen(physical_warehouse_id, whc.getCustomer_id().intValue(), "IS_BATCH_BY_SHIPMENT");
						isBatchByShipmentCache.put(physical_warehouse_id+"_"+ whc.getCustomer_id().intValue(), isBatchByShipment);
					}
					
					if(WorkerUtil.isNullOrEmpty(configCwMappingList)){
						continue ;
					}else{
						
						
						for (ConfigCwMapping cw:configCwMappingList){
							for (List<String> li : shopFileOmsShopIdMap.values()) {
								// 第一级 高优先级订单
							
								List<String> bpList=new ArrayList<String>();
								
								if(!"1".equalsIgnoreCase(isOutSource)){
									logger.info("BatchPickJob createBatchPick level 1;customerid="+whc.getCustomer_id());
									orderInfoList1 = orderInfoDao.getOrderLevel3New(
											physical_warehouse_id,
											whc.getCustomer_id(),cw.getWarehouse_id(), li,"N");

									
									bpList=getBpList(orderInfoList1,size);
									batchPickBizImpl.createLevel3NewBatchPick(orderInfoList1,physical_warehouse_id,size,bpList,cw.getWarehouse_id());
								}
								

								// 查询条件 parmMap需要填充参数 等级二和等级三 可共用
								parmMap.put("oms_shop_id", li);
								parmMap.put("end", DateUtils.getDateString(0, 0, 0, 0, 10, 0));
								parmMap.put("start", DateUtils.getDateString(0, 0, 10, 0, 0, 0));
								parmMap.put("warehouse_id", cw.getWarehouse_id());
								
								
							if(level>=2){
								    logger.info("BatchPickJob createBatchPick level 2;customerid="+whc.getCustomer_id()+", isBatchByShipment = "+isBatchByShipment);
									// 第二级 相同商品相同数目订单
								    if(!"1".equals(isBatchByShipment)){
								    	sdList1 = batchPickBizImpl.batchPickAllSame(parmMap);
										for (SameDetails sd : sdList1) {
											if (sd.getSku_sum() >= size) {
												// 商品种类信息
												String product_key = sd.getProduct_key();

												// 每种商品的件数
												String product_num = sd.getProduct_num();
												parmMap.put("product_key", product_key);
												parmMap.put("product_num", product_num);
												parmMap.put("type", "list1");
												parmMap.put("order_num", sd.getSku_sum());
												//普通快递
												parmMap.put("issf", "notsf");
												orderInfoList2 = batchPickBizImpl
														.batchPickAllOrderListByParm(parmMap);
												if (orderInfoList2.size() >= minSize&&sd.getSerial_nums()==0) {

													bpList=getBpList(orderInfoList2,maxSize);
													batchPickBizImpl.createBatchBatchPick(
															orderInfoList2, maxSize,physical_warehouse_id,bpList,minSize,cw.getWarehouse_id());
												} else if (orderInfoList2.size() >= size&&sd.getSerial_nums()==0) {

													bpList=getBpList(orderInfoList2,size);
													batchPickBizImpl.createBatchBatchPick(
															orderInfoList2, maxSize,physical_warehouse_id,bpList,size,cw.getWarehouse_id());
//													batchPickBizImpl
//															.createLevel3BatchPick(orderInfoList2,physical_warehouse_id,size,bpList);
												}
												
												//顺丰
												parmMap.put("issf", "sf");
												orderInfoList2 = batchPickBizImpl
														.batchPickAllOrderListByParm(parmMap);
												if (orderInfoList2.size() >= minSize&&sd.getSerial_nums()==0) {

													bpList=getBpList(orderInfoList2,maxSize);
													batchPickBizImpl.createBatchBatchPick(
															orderInfoList2, maxSize,physical_warehouse_id,bpList,minSize,cw.getWarehouse_id());
												} else if (orderInfoList2.size() >= size&&sd.getSerial_nums()==0) {

													bpList=getBpList(orderInfoList2,size);
													batchPickBizImpl.createBatchBatchPick(
															orderInfoList2, maxSize,physical_warehouse_id,bpList,size,cw.getWarehouse_id());
													
//													batchPickBizImpl
//															.createLevel3BatchPick(orderInfoList2,physical_warehouse_id,size,bpList);
												}
											}
										}
								    }else{

										parmMap.put("size", size);
										sdList1 = orderInfoDao.selectOrderInfoListForBatchPickByShipment(parmMap);
										
										for (SameDetails sd : sdList1) {
											
											String product_key = sd.getProduct_key();
											// 每种商品的件数
											String product_num = sd.getProduct_num();
											parmMap.put("product_key", product_key);
											parmMap.put("product_num", product_num);
											parmMap.put("shipping_id", sd.getShipping_id());
											parmMap.put("type", "list1");
											parmMap.put("order_num", sd.getSku_sum());

											orderInfoList2 = orderInfoDao.batchPickAllOrderListByParmByShipment(parmMap);
											
											if (orderInfoList2.size() >= minSize&&sd.getSerial_nums()==0) {
												
												bpList=getBpList(orderInfoList2,maxSize);
												
												batchPickBizImpl.createBatchBatchPick(
														orderInfoList2, maxSize,physical_warehouse_id,bpList,size,cw.getWarehouse_id());
											}
										
									}
								    }
								    
									
								}
								
								if(level>=3){
									logger.info("BatchPickJob createBatchPick level 3;customerid="+whc.getCustomer_id());
									// 第三级 相同商品数目不同的其他订单
									sdList1 = batchPickBizImpl.batchPickAllSameV3(parmMap);
								
									for (SameDetails sd : sdList1) {
								
										// 商品种类信息
										String product_key = sd.getProduct_key();
								
										parmMap.put("product_key", product_key);
										parmMap.put("type", "list2");
										parmMap.put("order_num", "");
								
										orderInfoList2 = batchPickBizImpl
												.batchPickAllOrderListByParmV3(parmMap);
										if (orderInfoList2.size() >= size) {

											bpList=getBpList(orderInfoList2,size);
											batchPickBizImpl
													.createLevel3BatchPick(orderInfoList2,physical_warehouse_id,size,bpList,cw.getWarehouse_id());
										}
								
									}
								}
								

								// 商品不同 但处于同一个通道 库区 品牌

								// 杂单
								if(level>=4){
									logger.info("BatchPickJob createBatchPick level 4;customerid="+whc.getCustomer_id());
									parmMap.put("is2B", "('N','W')");
									orderInfoList2 = batchPickBizImpl
											.batchPickAllLastOrderListByParm(parmMap);
									if (orderInfoList2.size() >= size) {

										bpList=getBpList(orderInfoList2,size);
										batchPickBizImpl
												.createLevel3BatchPick(orderInfoList2,physical_warehouse_id,size,bpList,cw.getWarehouse_id());
									}
							
								}
								//外包仓不生成toB toX波次
								if(!"1".equalsIgnoreCase(isOutSource)){
									logger.info("BatchPickJob createBatchPick toB;customerid="+whc.getCustomer_id());
									parmMap.put("is2B", "('Y')");
									orderInfoList2 = batchPickBizImpl
											.batchPickAllLastOrderListByParm(parmMap);
									bpList=getBpList(orderInfoList2,1);
									batchPickBizImpl
									.createToBBatchPick(orderInfoList2,physical_warehouse_id,1,bpList,"TOB",cw.getWarehouse_id());
									
									logger.info("BatchPickJob createBatchPick toB;customerid="+whc.getCustomer_id());
									parmMap.put("is2B", "('X')");
									orderInfoList2 = batchPickBizImpl
											.batchPickAllLastOrderListByParm(parmMap);
									bpList=getBpList(orderInfoList2,1);
									batchPickBizImpl
									.createToBBatchPick(orderInfoList2,physical_warehouse_id,1,bpList,"TOX",cw.getWarehouse_id());
									
								}
							}
						}
						
					}
				}
			

		}
		logger.info("BatchPickJob createBatchPick end; param="+param);
	}

	/**
	 * 自动刷新订单的优先级 在进入wms24小时是优先级从默认低级（1）到中级（2），48小时（2）从中级到高级（3）
	 */
	public void changeOrderStatus() {
		logger.info("BatchPickJob changeOrderStatus start;");
		
		int mid=orderInfoDao.selectMiddleLevelTime();
		int high=orderInfoDao.selectHighLevelTime();
		
		String time24String = DateUtils.getDateString(0, 0, 0, mid, 0, 0);
		String time48String = DateUtils.getDateString(0, 0, 0, high, 0, 0);

		orderInfoDao.updateOrderInfo24Hours(time24String);
		orderInfoDao.updateOrderInfo36Hours(time48String);
		logger.info("BatchPickJob changeOrderStatus end;");
	}
	
	/**
	 * 自动刷新订单的优先级 在进入wms24小时是优先级从默认低级（1）到中级（2），48小时（2）从中级到高级（3）
	 */
	public void changeOrderStatusByParam(String paramNameValue) {
		logger.info("BatchPickJob changeOrderStatusByParam start;");
		Map<String, String> paramNameValueMap =   TaskUtils.getParamNameValueMap(paramNameValue);
		
		int mid= paramNameValueMap.get("mid") == null ? 24 : Integer.parseInt(paramNameValueMap.get("mid")) ; //默认为0
		int high= paramNameValueMap.get("high") == null ? 48 : Integer.parseInt(paramNameValueMap.get("high")) ; //默认为0
		
		String time24String = DateUtils.getDateString(0, 0, 0, mid, 0, 0);
		String time48String = DateUtils.getDateString(0, 0, 0, high, 0, 0);

		orderInfoDao.updateOrderInfo24Hours(time24String);
		orderInfoDao.updateOrderInfo36Hours(time48String);
		logger.info("BatchPickJob changeOrderStatusByParam end;");
	}
	
	
	public void checkOutShopNameToShop(){
		
	}
	
	public void changeOrderLevelToHigh(String paramNameValue) {
		logger.info("BatchPickJob changeOrderLevelToHigh start;");
		Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
		
		Integer physical_warehouse_id = serviceParamsMap.get("physical_warehouse_id") == null ? 0 : Integer.parseInt(serviceParamsMap.get("physical_warehouse_id").toString()) ; //默认为0
		Integer group_id = serviceParamsMap.get("groupId") == null ? 0 : Integer.parseInt(serviceParamsMap.get("groupId").toString()) ; //默认为0
		Integer customer_id = serviceParamsMap.get("customerId") == null? 0 : Integer.parseInt(serviceParamsMap.get("customerId").toString()); //默认为0
		Integer level = serviceParamsMap.get("level") == null? 3 : Integer.parseInt(serviceParamsMap.get("level").toString()); //默认为0
		List<Integer> list=new ArrayList<Integer>();
		List<Integer> list2=new ArrayList<Integer>();
		
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
		
		String start = serviceParamsMap.get("start").toString();
		String end = serviceParamsMap.get("end").toString();

		for(int p_id:list){
			for(int c_id:list2){
				orderInfoDao.updateOrderInfoLevelUp(p_id,c_id,level,start,end);
			}
		}
	}
	
	private Map<String, Object> getServiceParamsMap(String paramNameValue) {
		Map<String, String> paramNameValueMap =   TaskUtils.getParamNameValueMap(paramNameValue);
		Map<String, Object> serviceParamsMap =   new HashMap<String, Object>();
		
		Integer physical_warehouse_id = paramNameValueMap.get("physical_warehouse_id") == null ? 0 : Integer.parseInt(paramNameValueMap.get("physical_warehouse_id")) ; //默认为0
		Integer groupId = paramNameValueMap.get("groupId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("groupId")) ; //默认为0
		Integer customerId = paramNameValueMap.get("customerId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("customerId")); //默认为0
		Integer level = paramNameValueMap.get("level") == null ? 3 : Integer.parseInt(paramNameValueMap.get("level")); //默认为0
		String start = null==paramNameValueMap.get("start")?"":paramNameValueMap.get("start");
		String end = null==paramNameValueMap.get("end")?"":paramNameValueMap.get("end");
		
		
		serviceParamsMap.put("physical_warehouse_id", physical_warehouse_id);
		serviceParamsMap.put("groupId", groupId);
		serviceParamsMap.put("customerId", customerId);
		serviceParamsMap.put("level", level);
		serviceParamsMap.put("start", start);
		serviceParamsMap.put("end", end);
		
		return serviceParamsMap;		
	}
	
	/**
	 * 自动删除过时的任务，需要删除
	 * 请配置每天的凌晨时间执行  如"0 2 0  * * ?"执行
	 */
	public void deleteDeadSingleBatchPickJob() {
		logger.info("BatchPickJob deleteDeadSingleBatchPickJob start;");
		List<ScheduleJob> scheduleJobList = scheduleJobDao
				.selectDeadSingleBatchPickJob();

		java.util.Date now = new java.util.Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		int date=c.get(Calendar.DATE);
		int month=c.get(Calendar.MONTH)+1;
		//int year=c.get(Calendar.YEAR);
		
		for(ScheduleJob sjob:scheduleJobList){
			String cron=sjob.getCronExpression();
			String [] crons=cron.split(" ");
			if(month-Integer.parseInt(crons[4])>0||(month-Integer.parseInt(crons[4])==0&&date-Integer.parseInt(crons[3])>0)||(month-Integer.parseInt(crons[4])<-10)){
				//
				scheduleJobDao.deleteByPrimaryKey(sjob.getJobId());
			}
		}
		logger.info("BatchPickJob deleteDeadSingleBatchPickJob end;");
	}
	
	
	/**
	 * 维护shop表
	 */
	public  void  checkShoptable(String numString){
		
		int num = Integer.parseInt(numString);
		
		String time = DateUtils.getDateString(num, "yyyy-MM-dd", " 00:00:00");
		
		//订单表的所有的数据
		List<Shop> shopList=orderInfoDao.getShopListFromTableOrderInfo(time);
		
		List<Shop> shopListUpdate=new ArrayList<Shop>();
		shopListUpdate.addAll(shopList);
		
		//shop表已有数据
		List<Shop> shopList2 = shopDao.selectAll();
		
		//找到已有数据 需要更新
		shopListUpdate.retainAll(shopList2);
		
		//找到未维护数据  需要插入
		shopList.removeAll(shopList2);
		
		if(!WorkerUtil.isNullOrEmpty(shopList)){
			shopDao.batchInsert(shopList);
		}
		
		if(!WorkerUtil.isNullOrEmpty(shopListUpdate)){
			shopDao.batchUpdate(shopListUpdate);
		}
	}
	
	public List<String> getBpList(List<Integer> orderInfoList2, int maxSize){
		int bp_need=0;
		List<String> bpList=new ArrayList<String>();
		if(orderInfoList2.size()%maxSize==0){
			bp_need=orderInfoList2.size()/maxSize;
		}
		else{
			bp_need=orderInfoList2.size()/maxSize+1;
		}
		String bpcode="";
		for(int i=0;i<bp_need;i++){
			bpcode=com.leqee.wms.util.WorkerUtil.generatorSequence(
					SequenceUtil.KEY_NAME_BPCODE, "", true);
			bpList.add(bpcode);
		}
		return bpList;
	}

}
