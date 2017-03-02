package com.leqee.wms.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.midi.MidiDevice.Info;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.biz.impl.InventoryBizImpl;
import com.leqee.wms.biz.impl.ModifyInventorySummaryBizImpl;
import com.leqee.wms.biz.impl.ReserveOrderInventoryBizImpl;
import com.leqee.wms.dao.InventorySummaryDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.entity.InventorySummary;
import com.leqee.wms.entity.InventorySyncItem;
import com.leqee.wms.entity.InventorySyncModifyInfo;
import com.leqee.wms.entity.InventorySyncRecord;
import com.leqee.wms.entity.OrderReserveDetail;
import com.leqee.wms.entity.OrderReserveRecord;
import com.leqee.wms.util.LockUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ReserveOrdersServiceImpl {
   
	Logger logger = Logger.getLogger(ReserveOrdersServiceImpl.class);
	private final static HashMap<String, ReentrantLock> reentrantLocks = new HashMap<String, ReentrantLock>();
	private final static byte[] createReentrantSyncLock = new byte[0];
	
	@Autowired
	ReserveOrderInventoryBizImpl reserveOrderInventoryBizImpl;
	
	@Autowired
	ModifyInventorySummaryBizImpl modifyInventorySummaryBizImpl;
	
	@Autowired
	InventorySummaryDao inventorySummaryDao;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	
	/**
	 *  订单预定
	 */
	public void reservceOrders(Integer groupId,
			Integer customerId, Integer days, String endDate) {
		
		Date startTime = new Date();
		String logPrefix = "reservceOrders";
		logger.info(logPrefix + " service start: " + startTime);
		
		/*  delete by dlyao 1523 start*/
		//维护inventory_summary and Detail 库存记录  维护库存的程序不嫩并发
//		try {
//			maintainInventorySummaryAndDetail();
//		} catch (Exception e) {
//			logger.error("reservceOrders  fail",e);
//		}
		/*  delete by dlyao 1523 end*/
		
		String condition = "";
		//构造订单的查询条件	
		if(!WorkerUtil.isNullOrEmpty(endDate)){
			condition += " and op.created_time < '"+endDate+"'";
		}
		if(!WorkerUtil.isNullOrEmpty(days) && days!= 0){
			condition += " and op.created_time>=date_add(now(),interval - "+days+" day) ";
		}	
		if(!WorkerUtil.isNullOrEmpty(customerId) && customerId.intValue()!=0 ){
			condition += " and op.customer_id = "+customerId;
		}
       
		List<Integer> customerIdList=new ArrayList<Integer>();
		
		if(groupId-0!=0){
			customerIdList=warehouseCustomerDao.selectCustomerIdListByGroupId(groupId);
		}
		else if(customerId-0==0){
			customerIdList=warehouseCustomerDao.selectAllId();
		}
		else{
			customerIdList.add(customerId);
		}

		if(!WorkerUtil.isNullOrEmpty(customerIdList)){
			for (Integer customer : customerIdList) {
				
				Lock lock = ReserveOrdersServiceImpl.getReentrantLock(customer+"_reserve");
				logger.info("reservceOrders customerId:"+customer+" get lock");
				lock.lock();
				logger.info("reservceOrders customerId:"+customer+" condition:"+condition);
				try {
					/*  add by dlyao 1523 start*/
					//
					doCalculateInventorySummary(customer, "", logPrefix);
					/*  add by dlyao 1523 end*/
					reserveOrderByCustomerId(customer,condition);
				} catch (Exception e) {
					logger.error("reservceOrders customerId:"+customer,e);
				}finally{
					if (lock != null) {
						lock.unlock();
					}
					logger.info("reservceOrders customerId:"+customer+" release lock");
				}

			}
		}else{
			 logger.info("the groupid no mapping customer_id groupId:"+groupId);
		}
		
		
		Date endTime = new Date();
		double elapsed = endTime.getTime() - startTime.getTime();
		logger.info(logPrefix + " Finished, runtime:" + elapsed / 1000 + "s");
		
	}
	
	/**
	 * 根据customer_id 预定订单
	 * @param customerId
	 * @param condition 
	 */
	public void reserveOrderByCustomerId(Integer customerId, String condition) {
       
        logger.info("reserveOrder customerId:"+customerId);
        
        Date startTime = new Date();
		String logPrefix = "reservceOrders suctomer_id:"+customerId;
		logger.info(logPrefix + " service start: " + startTime);
		
		if(WorkerUtil.isNullOrEmpty(customerId)){
			logger.info("customerId is null:"+customerId); 
			return;
		}
		
		//获取该customer下 用户的库存记录
		Map<String, Integer> productNumMap = modifyInventorySummaryBizImpl.getProductNumForReserverByCustomerId(customerId);
		logger.info("productNumMap detail:"+productNumMap.toString());
		
        //处理供应商退货订单预定
		Map<Integer,Map> orderMapGT = reserveOrderInventoryBizImpl.getReservedOrderIdForGT(customerId,condition);
		logger.info("reserOrdersForOut orderMapGT begin 开始预定  size: "+orderMapGT.size());						
		Integer succeseNumGt = reserveOrdersReal(productNumMap,orderMapGT,"gt");
		logger.info("reserOrdersForOut orderMapGT end successNumGt:"+succeseNumGt);
		//处理-gt订单的预定结束
		
		//处理-v订单
		Map<Integer,Map> orderMapV = reserveOrderInventoryBizImpl.getReservedOrderIdForV(customerId,condition);
		logger.info("reserOrdersForOut orderMapV begin 开始预定  size: "+orderMapV.size());						
		Integer succeseNumV = reserveOrdersReal(productNumMap,orderMapV,"v");
		logger.info("reserOrdersForOut orderMapV end successNumGt:"+succeseNumV);
		//处理-v订单结束
		
		
		//处理缺货订单		
		Map<Integer,Map> orderNotEnoughMap = reserveOrderInventoryBizImpl.getOrderNotEnoughAndHasInstorageByCustomerId(customerId,condition);
		if(WorkerUtil.isNullOrEmpty(orderNotEnoughMap)){
			logger.info("不存在缺货的订单"+customerId);
		}else {
			logger.info("reserOrdersForOut orderNotEnoughMap begin 开始预定  size: "+orderNotEnoughMap.size());
		}
		Integer succeseNumE = reserveOrdersReal(productNumMap,orderNotEnoughMap,"sale");
		logger.info("reserOrdersForOut orderNotEnoughMap end succeseNumE:"+succeseNumE);	
		
		
		//预定-sale订单
	    //记录预定的
	    int reserve_num = 0;
	    String nextOrders ="";
	    Boolean is_continue = true;
	    List<Map> orderIdList = null;
	    Integer max_order_id = 0;
	    //获取需要预定的订单信息 并且转换成相应的结构
	    logger.info("reserOrdersForOut Sale begin 开始预定 ");
	    while(reserve_num < 2500 && is_continue ==true ){	    
			orderIdList= reserveOrderInventoryBizImpl.getOrderIdForReserve(customerId,nextOrders,condition);		
			if (orderIdList.size() < 1500) {
				is_continue =false;
			}else{
				//为下一次需要预定订单做准备				
			    max_order_id = (Integer) orderIdList.get(orderIdList.size()-1).get("order_id");
				nextOrders = " and order_id >"+max_order_id;
			}			
			logger.info(logPrefix+" orderIdList size "+orderIdList.size());
			if(!WorkerUtil.isNullOrEmpty(orderIdList)){								
				Map<Integer,Map> orderMap = new HashMap<Integer, Map>();	
				for (Map map : orderIdList) {					
					Integer order_id = (Integer) map.get("order_id");
					String product =  (String) map.get("product");
					Integer gnum = Integer.parseInt(map.get("gnum").toString()) ;
					
					logger.info(logPrefix+" order_id:"+order_id+" product:"+product+" gnum:"+gnum);					
					if (orderMap.containsKey(order_id)) {
						Map exitMap =orderMap.get(order_id); 
						exitMap.put(product, gnum.intValue());
					}
					else{
						HashMap<String, Integer> productMap = new HashMap<String, Integer>();
						productMap.put(product, gnum.intValue());
						orderMap.put(order_id, productMap);
					}							
				}								
				 for (Entry<Integer, Map> entry : orderMap.entrySet()) {					
					 Integer order_id = entry.getKey();
					 Map<String, Integer> gMap = (Map<String, Integer>) entry.getValue();
					
					 Boolean is_enough = true;
					 for (String key : gMap.keySet()) {
						   logger.info("order_id:"+order_id+" key= "+ key + " and value= " + gMap.get(key));
						  
						   logger.info(" productNum"+productNumMap.get(key) +" gnum"+ gMap.get(key) );
						   
						   if(!productNumMap.containsKey(key)){
							   logger.info("productNumMap don't contain the product order_id:"+order_id);
							   is_enough = false;
							   break;
						   }					   
						   if( productNumMap.get(key) < gMap.get(key) ){
							   is_enough = false;
							   logger.info("productNumMap NOT enough  order_id:"+order_id);
							   //标记
							   try {
								   reserveOrderInventoryBizImpl.updateOrderProcessForReserve(order_id);
							   } catch (Exception e) {
								   logger.info(e.getMessage());
								   logger.info(e.getStackTrace().toString());
							   }							  
							   break;
						   }			   
				     }					 
				   if(is_enough == true){						
					try {
						 reserveOrderInventoryBizImpl.reserveOrderByOrderId(order_id,"sale");
						//减去map中对应的库存
						 for (String key : gMap.keySet()) { 						   
							 productNumMap.put(key, productNumMap.get(key) - gMap.get(key))	;	   
					     }
					    
						 logger.info(productNumMap.toString());
						 reserve_num ++;
					} catch (Exception e) {
						logger.error(e.getMessage());
						logger.error(e.getStackTrace().toString());
						e.printStackTrace();
					}					
					if(reserve_num >= 2500){
						is_continue = false;
						break;
					}	 
			     }
			  }
						
			 }else{
				logger.info("orderIdListForSale orders 0");
				is_continue = false;
			}
	    }
	    logger.info("reserOrdersForOut Sale end 开始预定  size:"+reserve_num);
		
		//预定雀巢的8个订单
//		 int prepack_num = 0;
//	     String nextOrders1 ="";
//	     Boolean is_continue1 = true;
//	     List<Map> prepackOrderIdList = null;
//	     Integer max_order_id1 = 0;
//		 logger.info("reserOrdersForOut prepack begin 开始预定 ");
//		    while(prepack_num < 2500 && is_continue1 ==true ){	    
//		    	prepackOrderIdList= reserveOrderInventoryBizImpl.getPrepackOrderIdForReserve(customerId,nextOrders1,condition);		
//				if (prepackOrderIdList.size() < 1500) {
//					is_continue1 =false;
//				}else{
//					//为下一次需要预定订单做准备				
//				    max_order_id1 = (Integer) prepackOrderIdList.get(prepackOrderIdList.size()-1).get("order_id");
//					nextOrders1 = " and order_id >"+max_order_id1;
//				}			
//				logger.info(logPrefix+" prepackOrderIdList size "+prepackOrderIdList.size());
//				if(!WorkerUtil.isNullOrEmpty(prepackOrderIdList)){								
//					Map<Integer,Map> orderMap = new HashMap<Integer, Map>();	
//					for (Map map : prepackOrderIdList) {					
//						Integer order_id = (Integer) map.get("order_id");
//						String product =  (String) map.get("product");
//						Integer gnum = Integer.parseInt(map.get("gnum").toString()) ;
//						
//						logger.info(logPrefix+" order_id:"+order_id+" product:"+product+" gnum:"+gnum);					
//						if (orderMap.containsKey(order_id)) {
//							Map exitMap =orderMap.get(order_id); 
//							exitMap.put(product, gnum.intValue());
//						}
//						else{
//							HashMap<String, Integer> productMap = new HashMap<String, Integer>();
//							productMap.put(product, gnum.intValue());
//							orderMap.put(order_id, productMap);
//						}							
//					}								
//					 for (Entry<Integer, Map> entry : orderMap.entrySet()) {					
//						 Integer order_id = entry.getKey();
//						 Map<String, Integer> gMap = (Map<String, Integer>) entry.getValue();
//						
//						 Boolean is_enough = true;
//						 for (String key : gMap.keySet()) {
//							   logger.info("order_id:"+order_id+" key= "+ key + " and value= " + gMap.get(key));
//							  
//							   logger.info(" productNum"+productNumMap.get(key) +" gnum"+ gMap.get(key) );
//							   
//							   if(!productNumMap.containsKey(key)){
//								   logger.info("productNumMap don't contain the product order_id:"+order_id);
//								   is_enough = false;
//								   break;
//							   }					   
//							   if( productNumMap.get(key) < gMap.get(key) ){
//								   is_enough = false;
//								   logger.info("productNumMap NOT enough  order_id:"+order_id);
//								   //标记
////								   try {
////									   reserveOrderInventoryBizImpl.updateOrderProcessForReserve(order_id);
////								   } catch (Exception e) {
////									   logger.info(e.getMessage());
////									   logger.info(e.getStackTrace().toString());
////								   }							  
//								   break;
//							   }			   
//					     }					 
//					   if(is_enough == true){						
//						try {
//							 reserveOrderInventoryBizImpl.reserveOrderByOrderId(order_id,"quechao");
//							//减去map中对应的库存
//							 for (String key : gMap.keySet()) { 						   
//								 productNumMap.put(key, productNumMap.get(key) - gMap.get(key))	;	   
//						     }
//						    
//							 logger.info(productNumMap.toString());
//							 prepack_num ++;
//						} catch (Exception e) {
//							logger.error(e.getMessage());
//							logger.error(e.getStackTrace().toString());
//							e.printStackTrace();
//						}					
//						if(prepack_num >= 2500){
//							is_continue1 = false;
//							break;
//						}	 
//				     }
//				  }
//							
//				 }else{
//					logger.info("orderIdListForPrepack orders 0");
//					is_continue1 = false;
//				}
//		    }
//		    logger.info("reserOrdersForOut prepack end 开始预定  size:"+prepack_num);
		
			Date endTime = new Date();
			double elapsed = endTime.getTime() - startTime.getTime();
			logger.info(logPrefix + " Finished, runtime:" + elapsed / 1000 + "s");
	}
	
	
	
	
    /**
     * 
     * @param productNumMap
     * @param orderMap
     * @param string
     * @return 很久map中库存记录开始预定  并且这个函数 不会对缺货订单 进行标记
     */
	private Integer reserveOrdersReal(Map<String, Integer> productNumMap,
			Map<Integer, Map> orderMap, String indicate) {
		
		Integer succeseNum=0;
		if(!WorkerUtil.isNullOrEmpty(orderMap)){
			 for (Entry<Integer, Map> entry : orderMap.entrySet()) {
					
				 Integer order_id = entry.getKey();
				 Map<String, Integer> gMap = (Map<String, Integer>) entry.getValue();
				
				 Boolean is_enough = true;
				 for (String key : gMap.keySet()) {
					   logger.info("order_id:"+order_id+" key= "+ key + " and value= " + gMap.get(key));
					  
					   logger.info(" productNum"+productNumMap.get(key) +" gnum"+ gMap.get(key) );
					   //判断订单中的商品是否有库存
					   if(!productNumMap.containsKey(key)){
						   logger.info("productNumMap don't contain the product order_id:"+order_id);
						   is_enough = false;
						   break;
					   }
					   //判断库存是否足够
					   if( productNumMap.get(key) < gMap.get(key) ){
						   is_enough = false;
						   break;
					   }			   
			     }
				 
			   if(is_enough == true){
					
				try {
                     //订单预定的入库
					 reserveOrderInventoryBizImpl.reserveOrderByOrderId(order_id,indicate);
					//减去map中对应的库存
					 for (String key : gMap.keySet()) { 						   
						 productNumMap.put(key, productNumMap.get(key) - gMap.get(key))	;	   
				     }
					 succeseNum++;
					 logger.info(productNumMap.toString());
				} catch (Exception e) {
					logger.error(e.getMessage());
					logger.error(e.getStackTrace().toString());
					e.printStackTrace();
				}						 
		     }
		  }
		
		}else{
			logger.info("orderMap orders 0");			
		}
		
		return succeseNum;
	}

	/**
	 * 维护inventory_summary和inventory_summary_detail的库存变动
	 */
	 
	public  void maintainInventorySummaryAndDetail(){
			Date startTime = new Date();
			String logPrefix = "maintainInventorySummaryAndDetail";
			logger.info(logPrefix + " service start: " + startTime);
			
	  synchronized(ReserveOrdersServiceImpl.class){		
		  
			Integer inventoryItemDetailId = reserveOrderInventoryBizImpl.getLastSyncDetailId();
			logger.info(logPrefix + " inventoryItemDetailId start: " + inventoryItemDetailId);
			
			Integer maxIncreasedDetailId = reserveOrderInventoryBizImpl.getMaxIncreasedDetailId();
			logger.info(logPrefix + " maxIncreasedDetailId: " + maxIncreasedDetailId);
			
			if(WorkerUtil.isNullOrEmpty(inventoryItemDetailId)){
				logger.info(logPrefix+" there is no inventoryItemDetailId ");
				return;
	        }
			
			if(WorkerUtil.isNullOrEmpty(maxIncreasedDetailId) ){
				logger.info(logPrefix+"  maxIncreasedDetailId is null ");
				return;
			}
			
			if(inventoryItemDetailId.intValue() == maxIncreasedDetailId.intValue()){
				logger.info(logPrefix+" there is no increasing item ");
				return;
			}
			
			
			logger.info(logPrefix+" inventoryItemDetailId:"+inventoryItemDetailId+" maxIncreasedDetailId"+maxIncreasedDetailId);
			
			try {
				reserveOrderInventoryBizImpl.getIncrementInventoryItems(inventoryItemDetailId, maxIncreasedDetailId);
			} catch (Exception e) {
				logger.info(e.getMessage());
				logger.info(e.getStackTrace().toString());
			}		 
	   }	
	  
			Date endTime = new Date();
			double elapsed = endTime.getTime() - startTime.getTime();
			logger.info(logPrefix + " Finished, runtime:" + elapsed / 1000 + "s");
	 
	}
	
	//fei
	public void maintainInventorySummaryAndDetailByCustomerId(Integer customerId) {
		
        logger.info("maintainInventorySummaryAndDetail customerId:"+customerId);
		
        Integer inventoryItemDetailId = reserveOrderInventoryBizImpl.getLastSyncInventoryItemDetailId(customerId);
        List<InventorySyncItem> inventorySyncItemlist = null;
        if(!WorkerUtil.isNullOrEmpty(inventoryItemDetailId)){
            inventorySyncItemlist = reserveOrderInventoryBizImpl.getIncrementInventoryItem(inventoryItemDetailId, customerId);
		    logger.info("customerId:"+customerId+" inventoryItemDetailId:"+inventoryItemDetailId);
        }else{
			inventorySyncItemlist = reserveOrderInventoryBizImpl.getIncrementInventoryItem(0, customerId);
			logger.info("customerId:"+customerId+" inventoryItemDetailId:"+0);
		}
        
        for (InventorySyncItem inventorySyncItem : inventorySyncItemlist) {
        	reserveOrderInventoryBizImpl.maintainInventorySummaryAndDetail(inventorySyncItem);
        }
        
        if(!WorkerUtil.isNullOrEmpty(inventorySyncItemlist)) {
        	logger.info("get the last inevntory_item_detail_id:"+inventorySyncItemlist.get(inventorySyncItemlist.size()-1).getInventory_item_detail_id());
        	InventorySyncRecord inventorySyncRecord = new InventorySyncRecord();
        	inventorySyncRecord.setCreated_stamp(new Date());
        	inventorySyncRecord.setCustomer_id(customerId);
        	inventorySyncRecord.setInventory_item_detail_id(inventorySyncItemlist.get(inventorySyncItemlist.size()-1).getInventory_item_detail_id());
        	reserveOrderInventoryBizImpl.insertInventorySyncRecord(inventorySyncRecord);
        }
        
        
	}
	
	/**
	 * 修复错误的库存
	 */
	public void  modifyErrorStockAndAvailityQuantiry(){
		
		Date startTime = new Date();
		String logPrefix = "modifyErrorStockAndAvailityQuantiry";
		logger.info(logPrefix + " service start: " + startTime);
		
		
		//在修改库存开始之前 确保订单出库后 预定状态都为F
        List<Integer> reserveOrderIds = reserveOrderInventoryBizImpl.getDeliverOrderIdForFinishReserve();
        
		if(!WorkerUtil.isNullOrEmpty(reserveOrderIds)){
			logger.info(logPrefix+" there are some order having delivery but not changing the reserve status to F");
			return;
		}
        
        
		List<InventorySyncModifyInfo> inventorySyncModifyInfoList =  modifyInventorySummaryBizImpl.getStockQuantityErrorRecord();
		for (InventorySyncModifyInfo inventorySyncModifyInfo : inventorySyncModifyInfoList) {
	    	try {
	    		modifyInventorySummaryBizImpl.modifyTheErrorStockInventoryRecord(inventorySyncModifyInfo);
			} catch (Exception e) {
				logger.error(e.getMessage());
				logger.error(e.getStackTrace().toString());
				e.printStackTrace();
			}
	    	
	    		    	
	    }
	   
		 inventorySyncModifyInfoList =  modifyInventorySummaryBizImpl.getAvailibleQuantityErrorRecord();
		    for (InventorySyncModifyInfo inventorySyncModifyInfo : inventorySyncModifyInfoList) {
		    	try {
			    	modifyInventorySummaryBizImpl.modifyTheErrorAvailibleInventoryRecord(inventorySyncModifyInfo);
				} catch (Exception e) {
					logger.error(e.getMessage());
					logger.error(e.getStackTrace().toString());
					e.printStackTrace();
				}
		    		    	
		    }
		
//	    inventorySyncModifyInfoList =  modifyInventorySummaryBizImpl.getAvailibleQuantityErrorRecord();
//	    if(WorkerUtil.isNullOrEmpty(inventorySyncModifyInfoList)){
//	    	logger.info("没有可调整的预定总表时,也需要进行一次库存核对,以防止异常数据发生");
//	    	List<InventorySyncModifyInfo> inventorySyncModifyInfoList1 =inventorySummaryDao.getAvailibleQuantityErrorRecordV1();
//	    	for (InventorySyncModifyInfo inventorySyncModifyInfo : inventorySyncModifyInfoList1) {
//		    	try {
//			    	modifyInventorySummaryBizImpl.modifyTheErrorAvailibleInventoryRecord(inventorySyncModifyInfo);
//				} catch (Exception e) {
//					logger.error(e.getMessage());
//					logger.error(e.getStackTrace().toString());
//					e.printStackTrace();
//				}
//		    		    	
//		    }
//	    }else{
//			    for (InventorySyncModifyInfo inventorySyncModifyInfo : inventorySyncModifyInfoList) {
//			    	try {
//				    	modifyInventorySummaryBizImpl.modifyTheErrorAvailibleInventoryRecord(inventorySyncModifyInfo);
//					} catch (Exception e) {
//						logger.error(e.getMessage());
//						logger.error(e.getStackTrace().toString());
//						e.printStackTrace();
//					}
//			    		    	
//			    }
//	    }
		
		
//		
		Date endTime = new Date();
		double elapsed = endTime.getTime() - startTime.getTime();
		logger.info(logPrefix + " Finished, runtime:" + elapsed / 1000 + "s");
	
		
	}
	
	/**
	 * 发货之后将订单预定状态改为F
	 */
	public void FinishedOrderInventoryReservation() {
		Date startTime = new Date();
		String logPrefix = "FinishedOrderInventoryReservation";
		logger.info(logPrefix + " service start: " + startTime);
		
		boolean doAgain = true;
		
		while(doAgain){
		        List<Integer> reserveOrderIds = reserveOrderInventoryBizImpl.getDeliverOrderIdForFinishReserve();
				
				if(WorkerUtil.isNullOrEmpty(reserveOrderIds)){
					logger.info("FreserveOrderIds FreserveOrderIds is  null or empty");
					doAgain = false;
					break;
				}
				logger.info("finishReserveOrder size:"+reserveOrderIds.size());		
				for (Integer orderId : reserveOrderIds) {					
					try {
						reserveOrderInventoryBizImpl.finishedOrderInventoryReservation(orderId);
					} catch (Exception e) {
						logger.info(e.getMessage());
						logger.info(e.getStackTrace().toString());
						e.printStackTrace();
					}
					
				}
		}
		
		Date endTime = new Date();
		double elapsed = endTime.getTime() - startTime.getTime();
		logger.info(logPrefix + " Finished, runtime:" + elapsed / 1000 + "s");
		
	}
	
	/**
	 * 获取lockKey对应的排它锁，没有时会创建一个
	 * @param lockKey
	 * @return
	 */
	public static ReentrantLock getReentrantLock(String lockKey ) {
		String key = lockKey ;
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(key.getBytes());
			byte[] result = md.digest();
			key = new String(result, 0, 16);
		} catch (java.security.NoSuchAlgorithmException e) {
			key = lockKey ;
		}

		ReentrantLock lock = reentrantLocks.get(key);
		if (lock == null) {
			//创建锁时需要加锁
			synchronized (createReentrantSyncLock) {
				lock = reentrantLocks.get(key);
				if (lock == null) {
					lock = new ReentrantLock();
					reentrantLocks.put(key, lock);
				}
			}
		}
		return lock;
	}

	public String reservceOrdersByDlYao(Integer customer_id, Integer days,
				String endDate) {
			
			String queueStr = "";
			ReentrantLock lock = LockUtil.getReentrantLock("customerId_" + customer_id);
			
			//当前线程其他方法已经获得此锁，以非重入方式使用此锁
			if(lock.isHeldByCurrentThread()){
				return queueStr;
			}
			
			lock.lock();
			try {
				queueStr=doReserveJobByCustomerId(customer_id, queueStr ,endDate,days);
				
				
			} catch (Exception e) {
				logger.info("lock message : "+e.getMessage(),e);
				if(lock.isLocked()){
					queueStr = "LOCKING";
				}else{
					throw new RuntimeException(e.getMessage());
				}
	
			}  finally {
				// 释放锁
				lock.unlock();
			}
			return queueStr;
			
		}

	/**
	 * 每个货主预定
	 * @param customer_id
	 * @param queueStr
	 * @param endDate
	 * @param days 
	 * @return
	 */
	private String doReserveJobByCustomerId(Integer customer_id,
			String queueStr,String endDate, Integer days) {
		String logPrefix = "doReserveJobByCustomerId";
		logger.info(logPrefix + " service start: customer_id"+customer_id);	
  
		queueStr=doCalculateInventorySummary(customer_id, queueStr, logPrefix);
		
		doReserveWorkByCustomerId(customer_id,
				queueStr,endDate,days);

		logger.info(logPrefix + " Finished customer_id"+customer_id);
		return queueStr;
	}

	/**
	 * 按货主  各种类型订单预定
	 * @param customer_id
	 * @param queueStr
	 * @param endDate
	 * @param days 
	 */
	private void doReserveWorkByCustomerId(Integer customer_id,
			String queueStr, String endDate, Integer days) {
		String condition = "";
		//构造订单的查询条件	
		if(!WorkerUtil.isNullOrEmpty(endDate)){
			condition += " and op.created_time < '"+endDate+"'";
		}
		if(!WorkerUtil.isNullOrEmpty(days) && days!= 0){
			String time = DateUtils.getDateString(days, "yyyy-MM-dd HH:mm:ss");
			condition += " and op.created_time>= '" + time+ "' ";
		}	
		if(!WorkerUtil.isNullOrEmpty(customer_id) && customer_id.intValue()!=0 ){
			condition += " and op.customer_id = "+customer_id;
		}
		
		Integer succeseNum = 0;
		//处理    供应商退货订单&&盘亏订单      预定
		//获取该customer下 库存的库存记录
		Map<String, Integer> productNumMap = modifyInventorySummaryBizImpl.getProductNumForReserverByCustomerIdV4(customer_id);
		
		Map<String, Integer> productSummaryIdMap = modifyInventorySummaryBizImpl.getProductNumForReserverByCustomerIdV5(customer_id);
		
		logger.info("productNumMap detail:"+productNumMap.toString());
		
        //处理    供应商退货订单&&盘亏订单      预定
		Map<Integer,List<Map>> orderMap = reserveOrderInventoryBizImpl.getReservedOrderIdForGTAndVarianceMinus(customer_id,condition);

		logger.info("reserveOrdersRealForGTAndVarianceMinus orderMap begin 开始预定  size: "+orderMap.size());	
		
		if(!WorkerUtil.isNullOrEmpty(orderMap)){
			succeseNum = reserveOrdersRealForGTAndVarianceMinus(productNumMap,productSummaryIdMap,orderMap,customer_id);
		}
		
		logger.info("reserveOrdersRealForGTAndVarianceMinus orderMap end successNum:"+succeseNum);
		//处理    供应商退货订单&&盘亏订单      预定结束
		
		
		
		//获取该customer下 库存的库存记录
		productNumMap = modifyInventorySummaryBizImpl.getProductNumForReserverByCustomerIdV4(customer_id);
		
		productSummaryIdMap = modifyInventorySummaryBizImpl.getProductNumForReserverByCustomerIdV5(customer_id);
		
		logger.info("productNumMap detail:"+productNumMap.toString());
		
        //处理    销售订单      预定 不缺货订单  N
		orderMap = reserveOrderInventoryBizImpl.getReservedOrderIdForSaleOrder(customer_id,condition,"N");

		logger.info("reserveOrderInventoryBizImpl orderMap begin N 开始预定  size: "+orderMap.size());	
		
		if(!WorkerUtil.isNullOrEmpty(orderMap)){
			succeseNum = reserveOrdersRealForSaleOrder(productNumMap,productSummaryIdMap,orderMap,customer_id);
		}
		
		logger.info("reserveOrderInventoryBizImpl orderMap end successNum:"+succeseNum);
		//处理    销售订单      预定结束

		
		//获取该customer下 库存的库存记录  之前缺货  E
		productNumMap = modifyInventorySummaryBizImpl.getProductNumForReserverByCustomerIdV4(customer_id);
		
		productSummaryIdMap = modifyInventorySummaryBizImpl.getProductNumForReserverByCustomerIdV5(customer_id);
		
		logger.info("productNumMap detail:"+productNumMap.toString());
		
        //处理    销售订单      预定
		orderMap = reserveOrderInventoryBizImpl.getReservedOrderIdForSaleOrder(customer_id,condition,"E");

		logger.info("reserveOrderInventoryBizImpl orderMap E begin 开始预定  size: "+orderMap.size());	
		
		if(!WorkerUtil.isNullOrEmpty(orderMap)){
			succeseNum = reserveOrdersRealForSaleOrder(productNumMap,productSummaryIdMap,orderMap,customer_id);
		}
		
		logger.info("reserveOrderInventoryBizImpl orderMap E end successNum:"+succeseNum);
		//处理    销售订单      预定结束
		
		
	}

	private Integer reserveOrdersRealForSaleOrder(
			Map<String, Integer> productNumMap,
			Map<String, Integer> productSummaryIdMap,
			Map<Integer, List<Map>> orderMap, Integer customer_id) {
		Integer succeseNum=0;
		//订单预定的入库
		succeseNum= reserveOrderInventoryBizImpl.reserveOrdersRealForSaleOrder(productNumMap,productSummaryIdMap,orderMap,customer_id);
				
		return succeseNum;
	}

	/**
	 * GT && VARIANCE MINUS  实际预定
	 * @param productNumMap
	 * @param productSummaryIdMap 
	 * @param orderMap
	 * @param customer_id 
	 * @return
	 */
	private Integer reserveOrdersRealForGTAndVarianceMinus(
			Map<String, Integer> productNumMap, Map<String, Integer> productSummaryIdMap, Map<Integer, List<Map>> orderMap, Integer customer_id) {

		Integer succeseNum=0;
		//订单预定的入库
		succeseNum= reserveOrderInventoryBizImpl.reserveOrdersRealForGTAndVarianceMinus(productNumMap,productSummaryIdMap,orderMap,customer_id);
		
		return succeseNum;
	
	}

	/**
	 * 每个货主进行统计
	 * @param customer_id
	 * @param queueStr
	 * @param logPrefix
	 * @return
	 */
	private String doCalculateInventorySummary(Integer customer_id,
			String queueStr, String logPrefix) {
		//inventory_item_detail 此货主的历史最大记录
		Integer inventoryItemDetailId = reserveOrderInventoryBizImpl.getLastSyncDetailIdByCustomerId(customer_id);
		logger.info(logPrefix + " inventoryItemDetailId start: " + inventoryItemDetailId +"customer_id"+customer_id);
		
		//inventory_item_detail 此货主的最大记录
		Integer maxIncreasedDetailId = reserveOrderInventoryBizImpl.getMaxIncreasedDetailIdByCustomerId(customer_id);
		logger.info(logPrefix + " maxIncreasedDetailId: " + maxIncreasedDetailId+"customer_id"+customer_id);
		
		if(WorkerUtil.isNullOrEmpty(inventoryItemDetailId)){
			inventoryItemDetailId=0;
//			logger.info(logPrefix+"  inventoryItemDetailId is null ");
//			return queueStr;
		}
		
		if(WorkerUtil.isNullOrEmpty(maxIncreasedDetailId) ){
			logger.info(logPrefix+"  maxIncreasedDetailId is null "+"customer_id"+customer_id);
			return queueStr;
		}
		
		if(inventoryItemDetailId.intValue() == maxIncreasedDetailId.intValue()){
			logger.info(logPrefix+" there is no increasing item "+"customer_id"+customer_id);
			return queueStr;
		}
		
		logger.info(logPrefix+" inventoryItemDetailId:"+inventoryItemDetailId+" maxIncreasedDetailId"+maxIncreasedDetailId+"customer_id"+customer_id);				
		
		reserveOrderInventoryBizImpl.getIncrementInventoryItemsByCustomerId(inventoryItemDetailId, maxIncreasedDetailId,customer_id);
		
		return "OK";
	}
		
	
}
