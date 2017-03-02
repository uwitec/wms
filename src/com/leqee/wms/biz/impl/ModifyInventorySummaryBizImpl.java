package com.leqee.wms.biz.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.ModifyInventorySummaryBiz;
import com.leqee.wms.dao.InventorySummaryDao;
import com.leqee.wms.dao.InventorySummaryDetailDao;
import com.leqee.wms.dao.ReserveOrderDao;
import com.leqee.wms.entity.InventorySummary;
import com.leqee.wms.entity.InventorySummaryDetail;
import com.leqee.wms.entity.InventorySyncModifyInfo;
import com.leqee.wms.service.impl.ReserveOrdersServiceImpl;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ModifyInventorySummaryBizImpl implements ModifyInventorySummaryBiz {

	Logger logger = Logger.getLogger(ModifyInventorySummaryBizImpl.class);

	
	@Autowired
	InventorySummaryDao inventorySummaryDao;
	
	@Autowired
	ReserveOrderDao reserveOrderDao;
	
	@Autowired
	InventorySummaryDetailDao inventorySummaryDetailDao;
	
	@Override
	public void modifyStockQuantiry() {
		  
		List<InventorySyncModifyInfo> inventorySyncModifyInfoList =  inventorySummaryDao.getStockQuantityErrorRecord();
	    for (InventorySyncModifyInfo inventorySyncModifyInfo : inventorySyncModifyInfoList) {
	    	
	    	modifyTheErrorStockInventoryRecord(inventorySyncModifyInfo);
	    		    	
	    }
	    
	    List<InventorySyncModifyInfo> inventorySyncModifyInfoDetailList =inventorySummaryDao.getErrorSrockDetailQuantityRecord();

	    for (InventorySyncModifyInfo inventorySyncModifyInfo : inventorySyncModifyInfoDetailList) {
	    	
	    	modifyTheErrorStockDEtailInventoryRecord(inventorySyncModifyInfo);
		}
	 
	}
	
	public List<InventorySyncModifyInfo> getStockQuantityErrorRecord(){
		
		return inventorySummaryDao.getStockQuantityErrorRecord();
	}
	
	public  List<InventorySyncModifyInfo> getErrorSrockDetailQuantityRecord(){
		return inventorySummaryDao.getErrorSrockDetailQuantityRecord();
	}
	
	

	public void modifyTheErrorStockDEtailInventoryRecord(
			InventorySyncModifyInfo inventorySyncModifyInfo) {
        
		int itemNum = inventorySyncModifyInfo.getItem_sum();
	    int stockQuantiry = inventorySyncModifyInfo.getStock_quantity();
	    int diff = stockQuantiry - itemNum;
	    Integer updateLine = inventorySummaryDao.modifyErrorStockDetailQuantity(inventorySyncModifyInfo.getInventory_summary_detail_id(),diff);
	    if(WorkerUtil.isNullOrEmpty(updateLine) || updateLine != 1){
	    	logger.error("modifyTheErrorStockInventoryRecord updateLine:"+updateLine+" Inventory_summary_detail_id:"+inventorySyncModifyInfo.getInventory_summary_detail_id());
	    	throw new RuntimeException("modifyTheErrorStockInventoryRecord fail"+" Inventory_summary_detail_id:"+inventorySyncModifyInfo.getInventory_summary_detail_id());
	    }
	}

	public void modifyTheErrorStockInventoryRecord(InventorySyncModifyInfo inventorySyncModifyInfo) {
		
		
		//维护inventory_summary表中的实际库存
		//暂时不锁
//		InventorySummary inventorySummary = reserveOrderDao.lockInventorySummaryForReserve(inventorySyncModifyInfo.getInventory_summary_id());
		logger.info(inventorySyncModifyInfo.getProduct_id()+":"+inventorySyncModifyInfo.getWarehouse_id()+":"+inventorySyncModifyInfo.getStatus_id());
		
		InventorySyncModifyInfo modifyInfo =inventorySummaryDao.getErrorSrockQuantityRecord(inventorySyncModifyInfo.getProduct_id(),inventorySyncModifyInfo.getWarehouse_id(),inventorySyncModifyInfo.getStatus_id());
	    int itemNum = modifyInfo.getItem_sum();
	    int stockQuantiry = modifyInfo.getStock_quantity();
	    int diff = stockQuantiry - itemNum;
	   
	    logger.info("itemNum:"+itemNum+" stockQuantiry"+stockQuantiry+" diff"+diff);
	    
	    Integer updateLine = inventorySummaryDao.modifyErrorStockQuantity(inventorySyncModifyInfo.getInventory_summary_id(),diff);
	    if(WorkerUtil.isNullOrEmpty(updateLine)){
	    	logger.info("modifyTheErrorStockInventoryRecord updateLine:"+updateLine+" Inventory_summary_id:"+inventorySyncModifyInfo.getInventory_summary_id());
	    	throw new RuntimeException("modifyTheErrorStockInventoryRecord fail"+" Inventory_summary_id:"+inventorySyncModifyInfo.getInventory_summary_id());
	    }
	    
	}

	@Override
	public void modifyReserveQuantiry() {
		List<InventorySyncModifyInfo> inventorySyncModifyInfoList =  inventorySummaryDao.getAvailibleQuantityErrorRecord();
	    for (InventorySyncModifyInfo inventorySyncModifyInfo : inventorySyncModifyInfoList) {
	    	
	    	modifyTheErrorAvailibleInventoryRecord(inventorySyncModifyInfo);
	    		    	
	    }
		
	}
	
	
	public List<InventorySyncModifyInfo>  getAvailibleQuantityErrorRecord() {
		return inventorySummaryDao.getAvailibleQuantityErrorRecord();
	}

	public void modifyTheErrorAvailibleInventoryRecord(InventorySyncModifyInfo inventorySyncModifyInfo) {
                
//		InventorySummary inventorySummary = reserveOrderDao.lockInventorySummaryForReserve(inventorySyncModifyInfo.getInventory_summary_id());
		
		InventorySummary inventorySummary = reserveOrderDao.getInventorySummaryById(inventorySyncModifyInfo.getInventory_summary_id());
		Integer  reserveNum =inventorySummaryDao.getReserveNumNotDelivery(inventorySummary.getProduct_id(),inventorySummary.getWarehouse_id(),inventorySummary.getStatus_id());
	    if(WorkerUtil.isNullOrEmpty(reserveNum)){
	    	reserveNum = 0;
	    }	
		Integer stockQuantity = inventorySummary.getStock_quantity();
		
		Integer availibleNum = inventorySummary.getAvailable_to_reserved();
		
		Integer diff = availibleNum + reserveNum - stockQuantity;
		
		logger.info("inventory_summer_id:"+inventorySyncModifyInfo.getInventory_summary_id()+"stockQuantity:"+stockQuantity+"availibleNum:"+availibleNum+"diff:"+diff);
	    
	    inventorySummaryDao.modifyErrorAvailibleQuantity(inventorySyncModifyInfo.getInventory_summary_id(),diff);
	    
	}

	public Map<String, Integer> getProductNumForReserverByCustomerId(Integer customerId) {
		
		List<Map> productNumList = inventorySummaryDao.getProductNumForReserverByCustomerId(customerId);
		
		HashMap<String, Integer> productNumMap = new HashMap<String, Integer>();
	    for (Map map : productNumList) {
			
	    	String prodcut = (String) map.get("product");    	
	    	
	    	if(map.get("num") instanceof  BigInteger){
	    		BigInteger num = (BigInteger) map.get("num");
	    		productNumMap.put(prodcut, num.intValue());
	    	}
	    	else {
	    		Long num = (Long) map.get("num");
	    		productNumMap.put(prodcut, num.intValue());
			}
	    		    	
		}
		
	    logger.info(productNumMap.toString());	
		
		
		return productNumMap;
	}

	public Map<String, Integer> getProductNumForReserverByCustomerIdV5(Integer customer_id) {
		
		List<Map> productSummaryIdList = inventorySummaryDao.getProductNumForReserverByCustomerIdV5(customer_id);
		
		HashMap<String, Integer> productSummaryIdMap = new HashMap<String, Integer>();
	    
		for (Map map : productSummaryIdList) {
			
	    	String prodcut = (String) map.get("product");    	
	    	
	    	if(map.get("inventory_summary_id") instanceof  BigInteger){
	    		BigInteger inventory_summary_id = (BigInteger) map.get("inventory_summary_id");
	    		productSummaryIdMap.put(prodcut, inventory_summary_id.intValue());
	    	}
	    	else {
	    		Long inventory_summary_id = (Long) map.get("inventory_summary_id");
	    		productSummaryIdMap.put(prodcut, inventory_summary_id.intValue());
			}
	    		    	
		}
		
	    logger.info(productSummaryIdMap.toString());	
		
		return productSummaryIdMap;
	}

	public Map<String, Integer> getProductNumForReserverByCustomerIdV4(
			Integer customer_id) {
		
		List<Map> productNumList = inventorySummaryDao.getProductNumForReserverByCustomerIdV4(customer_id);
				
				HashMap<String, Integer> productNumMap = new HashMap<String, Integer>();
			    for (Map map : productNumList) {
					
			    	String prodcut = (String) map.get("product");    	
			    	
			    	if(map.get("num") instanceof  BigInteger){
			    		BigInteger num = (BigInteger) map.get("num");
			    		productNumMap.put(prodcut, num.intValue());
			    	}
			    	else {
			    		Integer num = (Integer) map.get("num");
			    		productNumMap.put(prodcut, num.intValue());
					}
			    		    	
				}
				
			    logger.info(productNumMap.toString());	
				
				
				return productNumMap;
			}

	

}
