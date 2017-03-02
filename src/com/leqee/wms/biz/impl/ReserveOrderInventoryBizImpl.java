package com.leqee.wms.biz.impl;

import com.leqee.wms.biz.ReserveOrderInventoryBiz;
import com.leqee.wms.dao.*;
import com.leqee.wms.entity.*;
import com.leqee.wms.util.WorkerUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

@Service
public class ReserveOrderInventoryBizImpl implements ReserveOrderInventoryBiz {

    Logger logger = Logger.getLogger(InventoryBizImpl.class);
    
    public static final int RESERVE_SUCCESS = 0;
    public static final int NOT_ENOUGH_AVAILABLE_NUMBER = 1;
    public static final int NOT_NEED_TO_RESERVE = 2;
    
    
	@Autowired
	ReserveOrderDao reserveOrderDao;
	
	@Autowired
	InventorySummaryDao inventorySummaryDao;
	
	@Autowired
	InventorySummaryDetailDao  inventorySummaryDetailDao;
	
	@Autowired
	InventorySyncRecordDao inventorySyncRecordDao;
	
	@Autowired
	OrderReserveDetailDao orderReserveDetailDao;
	
	@Autowired
	OrderInfoDao orderInfoDao;
	
	@Autowired
	OrderProcessDao orderProcessDao;
	
	@Autowired
	OrderGoodsDao orderGoodsDao;
	
	@Autowired
	OrderReserveRecordDao orderReserveRecordDao;
	
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	
	@Autowired
	InventoryDao inventoryDao;
	
	@Autowired
	OrderReserveInventoryMappingDao orderReserveInventoryMappingDao;
	
	
	public int getLastSyncInventoryItemDetailId(int customerId){
		return reserveOrderDao.getLastSyncInventoryItemDetailId(customerId);
	}
	
	public  List<InventorySyncItem> getIncrementInventoryItem(int inventoryItemDetailId,int customerId){
		return reserveOrderDao.getIncrementInventoryItem(inventoryItemDetailId, customerId);
	}
	
		
	public void maintainInventorySummaryAndDetail(InventorySyncItem inventorySyncItem) {
		
//		logger.info("maintainInventorySummaryAndDetail customerId:"+customerId);
//		
//        Integer inventoryItemDetailId = reserveOrderDao.getLastSyncInventoryItemDetailId(customerId);
//        List<InventorySyncItem> inventorySyncItemlist = null;
//        if(!WorkerUtil.isNullOrEmpty(inventoryItemDetailId)){
//            inventorySyncItemlist = reserveOrderDao.getIncrementInventoryItem(inventoryItemDetailId, customerId);
//		    logger.info("customerId:"+customerId+" inventoryItemDetailId:"+inventoryItemDetailId);
//        }else{
//			inventorySyncItemlist = reserveOrderDao.getIncrementInventoryItem(0, customerId);
//			logger.info("customerId:"+customerId+" inventoryItemDetailId:"+0);
//		}
//        for (InventorySyncItem inventorySyncItem : inventorySyncItemlist) {
			 logger.info("inventorySyncItem Product_id"+inventorySyncItem.getProduct_id()+" Warehouse_id"+inventorySyncItem.getWarehouse_id()+" Status"+inventorySyncItem.getStatus());
             InventorySummary inventorySummary = inventorySummaryDao.selectInventorySummary(inventorySyncItem.getProduct_id(),inventorySyncItem.getWarehouse_id(),inventorySyncItem.getStatus());
        	 
        	//用于处理新添加的商品 新商品最新的记录会是入库
        	if(WorkerUtil.isNullOrEmpty(inventorySummary)){
        		
        		logger.info("create a new inventory_summary record");
        		InventorySummary inventorySummaryNew = new InventorySummary();
        		inventorySummaryNew.setAvailable_to_reserved(inventorySyncItem.getChange_quantity());
        		inventorySummaryNew.setStock_quantity(inventorySyncItem.getChange_quantity());
        		inventorySummaryNew.setProduct_id(inventorySyncItem.getProduct_id());
        		inventorySummaryNew.setStatus_id(inventorySyncItem.getStatus());
        		inventorySummaryNew.setWarehouse_id(inventorySyncItem.getWarehouse_id());
        		inventorySummaryNew.setCreated_stamp(new Date());
        		inventorySummaryNew.setCreated_user("reserveSync");
        		inventorySummaryNew.setLast_updated_stamp(new Date());
        		inventorySummaryNew.setLast_updated_user("reserveSync");
        		inventorySummaryDao.insert(inventorySummaryNew);
        	}
        	else{
        		if(inventorySyncItem.getChange_quantity()<0){
            		inventorySummaryDao.updateStockQuantity(inventorySummary.getInventory_summary_id(),inventorySyncItem.getChange_quantity());
                    logger.info("inventorySummary : "+inventorySummary.getInventory_summary_id()+" deliveryNumber:"+inventorySyncItem.getChange_quantity());
        		}else{        		
        			inventorySummaryDao.updateStockAndAvailibleQuantity(inventorySummary.getInventory_summary_id(),inventorySyncItem.getChange_quantity(),inventorySyncItem.getChange_quantity());
                    logger.info("inventorySummary : "+inventorySummary.getInventory_summary_id()+" InNumber:"+inventorySyncItem.getChange_quantity());

        		}
        		
        	}
        	logger.info("Warehouse_id:"+inventorySyncItem.getWarehouse_id()+" Status"+inventorySyncItem.getStatus()+" Unit_cost"+inventorySyncItem.getUnit_cost()+""
    				+ " Provider_code"+inventorySyncItem.getProvider_code()+" Batch_sn"+inventorySyncItem.getBatch_sn());
        	InventorySummaryDetail inventorySummaryDetail = inventorySummaryDetailDao.selectInventorySummaryDetailInfo(inventorySyncItem.getProduct_id(),
        			inventorySyncItem.getWarehouse_id(),inventorySyncItem.getStatus(),inventorySyncItem.getUnit_cost(),
        			inventorySyncItem.getProvider_code(),inventorySyncItem.getBatch_sn());
        	if(WorkerUtil.isNullOrEmpty(inventorySummaryDetail)){
        		InventorySummaryDetail inventorySummaryDetailNew = new InventorySummaryDetail();
//        		inventorySummaryDetail.setAvailableToReserved(inventorySyncItem.getChange_quantity());
        		inventorySummaryDetailNew.setStock_quantity(inventorySyncItem.getChange_quantity());
        		inventorySummaryDetailNew.setBatch_sn(inventorySyncItem.getBatch_sn());
        		inventorySummaryDetailNew.setCreated_stamp(new Date());
        		inventorySummaryDetailNew.setCreated_user("reserveSync");
        		inventorySummaryDetailNew.setLast_updated_stamp(new Date());
        		inventorySummaryDetailNew.setLast_updated_user("reserveSync");
        		inventorySummaryDetailNew.setProduct_id(inventorySyncItem.getProduct_id());
        		inventorySummaryDetailNew.setWarehouse_id(inventorySyncItem.getWarehouse_id());
        		inventorySummaryDetailNew.setStatus_id(inventorySyncItem.getStatus());
        		inventorySummaryDetailNew.setUnit_price(inventorySyncItem.getUnit_cost());
        		inventorySummaryDetailNew.setProvider_code(inventorySyncItem.getProvider_code());
        		inventorySummaryDetailNew.setBatch_sn(inventorySyncItem.getBatch_sn());
        		inventorySummaryDetailNew.setCustomer_id(inventorySyncItem.getCustomer_id());
        		logger.info(inventorySummaryDetailNew.toString());
        	    inventorySummaryDetailDao.insert(inventorySummaryDetailNew);
        	    
        	}else{
        		inventorySummaryDetailDao.updateDetailStockQuantity(inventorySummaryDetail.getInventory_summary_detail_id(),inventorySyncItem.getChange_quantity());
        	}
//		}
        
//        if(!WorkerUtil.isNullOrEmpty(inventorySyncItemlist)) {
//        	logger.info("get the last inevntory_item_detail_id:"+inventorySyncItemlist.get(inventorySyncItemlist.size()-1).getInventory_item_detail_id());
//        	InventorySyncRecord inventorySyncRecord = new InventorySyncRecord();
//        	inventorySyncRecord.setCreated_stamp(new Date());
//        	inventorySyncRecord.setCustomer_id(customerId);
//        	inventorySyncRecord.setInventory_item_detail_id(inventorySyncItemlist.get(inventorySyncItemlist.size()-1).getInventory_item_detail_id());
//        	inventorySyncRecordDao.insert(inventorySyncRecord);
//        }
		
	}



    @Override
	public void reserveGTOrderByOrderId(int orderId) {
                 
			logger.info("reserveGTOrderByOrderId orderId:"+orderId);
			String logPrefix  = "reserveGTOrderByOrderId order_id:"+orderId;
//			List<OrderReserveDetail> orderReserveDetailList = orderReserveDetailDao.selectOrderReserveDetailByOrderId(orderId);
//			if(!WorkerUtil.isNullOrEmpty(orderReserveDetailList)){
//				 logger.info("reserveOrderByOrderId orderReserveDetailList is not null delete");
//				 for (OrderReserveDetail orderReserveDetail : orderReserveDetailList) {
//					 InventorySummary inventorySummary =inventorySummaryDao.selectInventorySummary(orderReserveDetail.getProduct_id(), 
//							 orderReserveDetail.getWarehouse_id(), orderReserveDetail.getStatus());
//					 if(!WorkerUtil.isNullOrEmpty(inventorySummary) && orderReserveDetail.getReserved_number() > 0 && orderReserveDetail.getStatus() == "Y"){
//						Integer updateLine = inventorySummaryDao.updateAvailibleNumberForReserve(inventorySummary.getInventory_summary_id(), -orderReserveDetail.getReserved_number());
//						if (WorkerUtil.isNullOrEmpty(updateLine)) {
//							logger.info("update inventory_summary for delete fail order_id:"+orderId);
//							throw new RuntimeException("delete orderReserveDetail fail order_id:"+orderId);
//						 }
//					 }
//				 
//				 }
//				 Integer deleteLine = orderReserveDetailDao.deleteOrderReserveDetailByOrderId(orderId);
//				 if (WorkerUtil.isNullOrEmpty(deleteLine)) {
//					logger.info("delete orderReserveDetail fail order_id:"+orderId);
//					throw new RuntimeException("delete orderReserveDetail fail order_id:"+orderId);
//				 }
//			}
			
			List<OrderGoods> orderGoods = orderGoodsDao.selectByOrderId(orderId);
			if(WorkerUtil.isNullOrEmpty(orderGoods)){
				logger.info("the order_id not having order_goods record order_id:"+orderId);
				throw new RuntimeException("order_id:"+orderId+" no order goods");
			}
			
            for (OrderGoods goods : orderGoods) {
				
				if(WorkerUtil.isNullOrEmpty(goods.getProduct_id()) || WorkerUtil.isNullOrEmpty(goods.getWarehouse_id()) ||
						WorkerUtil.isNullOrEmpty(goods.getStatus_id()) || WorkerUtil.isNullOrEmpty(goods.getGoods_number())  ){
					throw new RuntimeException("查库存时订单商品信息不完整");
				}
				logger.info("prodcuct:"+goods.getProduct_id()+" Warehouse_id:"+goods.getWarehouse_id()+" Status_id:"+goods.getStatus_id()+" Goods_number:"+goods.getGoods_number());

				reserveGTOrderInventoryByOrderGoods(goods);
				
				OrderReserveDetail orderReserveDetail = new OrderReserveDetail();
				orderReserveDetail.setCreated_time(new Date());
				orderReserveDetail.setGoods_number(goods.getGoods_number());
				orderReserveDetail.setInventory_status(goods.getStatus_id());
				orderReserveDetail.setOrder_id(orderId);
				orderReserveDetail.setProduct_id(goods.getProduct_id());
				orderReserveDetail.setReserved_number(goods.getGoods_number());
				orderReserveDetail.setStatus("Y");
				orderReserveDetail.setWarehouse_id(goods.getWarehouse_id());
				orderReserveDetail.setReserved_time(new Date());
				orderReserveDetail.setOrder_goods_id(goods.getOrder_goods_id());
				Integer insertLine =  orderReserveDetailDao.insert(orderReserveDetail);
				
				logger.info("insert order_reserve_detail order_goods_id:"+goods.getOrder_goods_id()+" insert line :"+insertLine);
				if(WorkerUtil.isNullOrEmpty(insertLine)){
					throw new RuntimeException("insert order_reserve_detail fail! order_goods_id:"+goods.getOrder_goods_id());
				}
			}
			
	
            Integer orderUpdateLine = orderInfoDao.updateOrderInfoForReserve(orderId,"Y");			
			if (WorkerUtil.isNullOrEmpty(orderUpdateLine) || orderUpdateLine != 1) {
				throw new RuntimeException("update order_info status fail! order_id:"+orderId);
			}
			
			
			Integer orderProcessUpdateLine = orderProcessDao.updateReserveStatus("Y",orderId);			
			if (WorkerUtil.isNullOrEmpty(orderProcessUpdateLine) || orderProcessUpdateLine != 1) {
				throw new RuntimeException("update order_process status fail! order_id:"+orderId);
			}
	}



    
	public void reserveGTOrderInventoryByOrderGoods(OrderGoods goods) {
        
		 InventorySummary inventorySummary = inventorySummaryDao.selectInventorySummary(goods.getProduct_id(), goods.getWarehouse_id(), goods.getStatus_id());
		 if (WorkerUtil.isNullOrEmpty(inventorySummary)) {
			logger.info("reserveGTOrderInventoryByOrderGoods get inventory summary fail order_goods_id:"+goods.getOrder_goods_id());
			throw new RuntimeException("get inventory summary fail order_goods_id:"+goods.getOrder_goods_id());
		 }
		 logger.info("reserveGTOrderInventoryByOrderGoodsId inventorySummary:"+inventorySummary.getInventory_summary_id());
		
		 OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(goods.getOrder_id());
		 if (WorkerUtil.isNullOrEmpty(orderInfo)) {
				logger.info("reserveGTOrderInventoryByOrderGoods get orderInfo fail order_id:"+goods.getOrder_id());
				throw new RuntimeException("get orderInfo fail order_id:"+goods.getOrder_id());
		 }
		 
		 logger.info("inventorySummaryDetail sql Product_id"+goods.getProduct_id()+" Warehouse_id"+goods.getWarehouse_id()+" Status"+ goods.getStatus_id()+" Goods_price"+
				 goods.getGoods_price()+" Provider_code"+orderInfo.getProvider_code()+" Batch_sn"+goods.getBatch_sn());
		 
		 String batsh_sn = goods.getBatch_sn();
		 if(batsh_sn == null){
			 batsh_sn ="";
		 }
		
		 InventorySummaryDetail inventorySummaryDetail = inventorySummaryDetailDao.selectInventorySummaryDetailInfo(goods.getProduct_id(),
				 goods.getWarehouse_id(), goods.getStatus_id(),
				 goods.getGoods_price(), orderInfo.getProvider_code(), batsh_sn);
		 
		 if (WorkerUtil.isNullOrEmpty(inventorySummaryDetail)) {
				logger.info("reserveGTOrderInventoryByOrderGoods get  inventorySummaryDetail fail order_goods_id:"+goods.getOrder_goods_id());
				throw new RuntimeException("get inventorySummaryDetail fail order_goods_id:"+goods.getOrder_goods_id());
			 }
		 
		 //得到需要预定的商品数
		 int req = goods.getGoods_number();
		 if(req <= 0){
			 logger.info("reserveGTOrderInventoryByOrderGoods order_goods number is wrong order_goods_id:"+goods.getOrder_goods_id());
		     throw new RuntimeException("reserveGTOrderInventoryByOrderGoods order_goods number is wrong order_goods_id:"+goods.getOrder_goods_id());
		 }
		 
		 
		 List<InventoryGoodsFreeze>  inventoryGoodsFreezeList =  inventoryDao.selectInventoryProductFreeze(goods.getProduct_id(),
				 goods.getWarehouse_id());
		 
		 int freezeNum = 0;
		 if (!WorkerUtil.isNullOrEmpty(inventoryGoodsFreezeList)) {
			 for (InventoryGoodsFreeze inventoryGoodsFreeze : inventoryGoodsFreezeList) {
				 freezeNum += inventoryGoodsFreeze.getReserve_number();
			 }
		 }		 
		 //减去冻结的商品库存
		 int atp = inventorySummary.getAvailable_to_reserved() - freezeNum;
		 if (atp<=0) {
			 logger.info("reserveGTOrderInventoryByOrderGoods atp -freezeNum <= 0 order_goods_id:"+goods.getOrder_goods_id());
		     throw new RuntimeException("reserveGTOrderInventoryByOrderGoods atp -freezeNum <= 0 order_goods_id:"+goods.getOrder_goods_id());
		 }
		 int atpDetail = inventorySummaryDetail.getStock_quantity();
		 if(atp>atpDetail){
			 atp = atpDetail;
		 }
		 if(atp>=req){
			 Integer updateLine = inventorySummaryDao.updateAvailibleNumberForReserve(inventorySummary.getInventory_summary_id(),req);	
			 if(WorkerUtil.isNullOrEmpty(updateLine) && updateLine!=1){
				 throw new RuntimeException("update inventorySummary status fail!"); 
			 }
		 }
		 else if (atp<req) {
			 Integer updateLine2 = inventorySummaryDao.updateAvailibleNumberForReserve(inventorySummary.getInventory_summary_id(), atp);	
			 if(WorkerUtil.isNullOrEmpty(updateLine2) && updateLine2!=1){
				 throw new RuntimeException("update inventorySummary status fail!"); 
			 }
		 }
		
		
	}

	//已经废弃
	public int reserveGTOrderInventoryByOrderGoodsId(OrderReserveDetail orderReserveDetail) {
		
		 
		 InventorySummary inventorySummary = inventorySummaryDao.selectInventorySummary(orderReserveDetail.getProduct_id(), orderReserveDetail.getWarehouse_id(), orderReserveDetail.getInventory_status());
		 logger.info("reserveGTOrderInventoryByOrderGoodsId inventorySummary:"+inventorySummary.getInventory_summary_id());
		 OrderGoods orderGoods = orderGoodsDao.selectByPrimaryKey(orderReserveDetail.getOrder_goods_id());
		 OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(orderGoods.getOrder_id());
		 
		 logger.info("inventorySummaryDetail sql Product_id"+orderReserveDetail.getProduct_id()+" Warehouse_id"+orderReserveDetail.getWarehouse_id()+" Status"+ orderReserveDetail.getInventory_status()+" Goods_price"+
				     orderGoods.getGoods_price()+" Provider_code"+orderInfo.getProvider_code()+" Batch_sn"+orderGoods.getBatch_sn());
		
		 InventorySummaryDetail inventorySummaryDetail = inventorySummaryDetailDao.selectInventorySummaryDetailInfo(orderReserveDetail.getProduct_id(),
				 orderReserveDetail.getWarehouse_id(), orderReserveDetail.getInventory_status(),
				 orderGoods.getGoods_price(), orderInfo.getProvider_code(), orderGoods.getBatch_sn());
		 
		 if(WorkerUtil.isNullOrEmpty(inventorySummary) || WorkerUtil.isNullOrEmpty(inventorySummaryDetail) ){
			 logger.error("wms:reserveOrderInventoryByOrderGoodsId error cant't get the inventory_summary");
			 throw new RuntimeException("cant't get the inventory_summary");
		 }
		 //得到需要预定的商品数
		 int req = orderReserveDetail.getGoods_number()- orderReserveDetail.getReserved_number();
		 if(req <= 0){
			return NOT_NEED_TO_RESERVE;
		 }
		 
		 
		 List<InventoryGoodsFreeze>  inventoryGoodsFreezeList =  inventoryDao.selectInventoryProductFreeze(orderReserveDetail.getProduct_id(),
				 orderReserveDetail.getWarehouse_id());
		 
		 int freezeNum = 0;
		 for (InventoryGoodsFreeze inventoryGoodsFreeze : inventoryGoodsFreezeList) {
			 freezeNum += inventoryGoodsFreeze.getReserve_number();
		 }
		 //减去冻结的商品库存
		 int atp = inventorySummary.getAvailable_to_reserved() - freezeNum;
		 int atpDetail = inventorySummaryDetail.getStock_quantity();
		 if(atp>atpDetail){
			 atp = atpDetail;
		 }
		 if(atp>=req){
			 //锁定该条inventory_summary记录
//			 reserveOrderDao.lockInventorySummaryForReserve(inventorySummary.getInventory_summary_id());
			 Integer updateLine = inventorySummaryDao.updateAvailibleNumberForReserve(inventorySummary.getInventory_summary_id(),req);	
			 if(WorkerUtil.isNullOrEmpty(updateLine) && updateLine!=1){
				 throw new RuntimeException("update inventorySummary status fail!"); 
			 }
			 orderReserveDetailDao.updateReserveDetailInfo(orderReserveDetail.getOrder_goods_id(),req,"Y");
		 }
		 else if (atp<req) {
//			 reserveOrderDao.lockInventorySummaryForReserve(inventorySummary.getInventory_summary_id());
//			 inventorySummary.setAvailable_to_reserved(inventorySummary.getAvailable_to_reserved()-atp); 
			 Integer updateLine2 = inventorySummaryDao.updateAvailibleNumberForReserve(inventorySummary.getInventory_summary_id(), atp);	
			 if(WorkerUtil.isNullOrEmpty(updateLine2) && updateLine2!=1){
				 throw new RuntimeException("update inventorySummary status fail!"); 
			 }
			 orderReserveDetailDao.updateReserveDetailInfo(orderReserveDetail.getOrder_goods_id(),atp,"Y");

		 }
	 
		 return RESERVE_SUCCESS;

	}



    /**
     *  预定订单  
     * @param order_id
     * @param indicate 
     */
   
	public void reserveOrderByOrderId(Integer order_id, String indicate) {
		
		 
        Date startTime = new Date();
		String logPrefix = "reserveOrderByOrderId order_id:"+order_id;
		logger.info(logPrefix + " service start: " + startTime);
		//判断订单里是否有预打包商品
		List<OrderGoods> orderGoods = new ArrayList<OrderGoods>();
//		boolean flag = false;
//		List<OrderGoods> checkOrderGoods = orderGoodsDao.selectByOrderId(order_id);
//		for (OrderGoods checkGoods : checkOrderGoods) {
//			if(OrderGoods.ORDER_GOODS_TYPE_PREPACKAGE.equals(checkGoods.getOrder_goods_type())){
//				flag = true;
//			}
//		}
			if("sale".equals(indicate)){
				orderGoods = orderGoodsDao.selectByOrderIdV1(order_id);
			}else {
				orderGoods= orderGoodsDao.selectByOrderId(order_id);
			}
			
			//判断结束
			if(WorkerUtil.isNullOrEmpty(orderGoods)){
				logger.info("the order_id not having order_goods record order_id:"+order_id);
				throw new RuntimeException("order_id:"+order_id+" no order goods");
			}
			
			for (OrderGoods goods : orderGoods) {
				
				if(WorkerUtil.isNullOrEmpty(goods.getProduct_id()) || WorkerUtil.isNullOrEmpty(goods.getWarehouse_id()) ||
						WorkerUtil.isNullOrEmpty(goods.getStatus_id()) || WorkerUtil.isNullOrEmpty(goods.getGoods_number())  ){
					throw new RuntimeException("查库存时订单商品信息不完整");
				}
				logger.info("prodcuct:"+goods.getProduct_id()+" Warehouse_id:"+goods.getWarehouse_id()+" Status_id:"+goods.getStatus_id()+" Goods_number:"+goods.getGoods_number());
				//指定依云业务组 if(goods.getCustomer_id() == 5){
				String condition = getDeliveryGoodsCondition(goods,indicate);
				String isSerail = reserveOrderDao.checkProductIsSerial(goods.getProduct_id());
				//不预定到inventoryitem
				if(isSerail.equals("N")){
				    logger.info("reserveOrderByOrderId isSerail is N orderGoodsId:"+goods.getOrder_goods_id());		
					HashMap<Integer, Integer> inventoryItemList = null;				
					inventoryItemList = getAvailaleInventoryListForDeliver(goods.getProduct_id(),goods.getWarehouse_id(),
							goods.getStatus_id(),goods.getGoods_number(),condition, indicate);

					if(WorkerUtil.isNullOrEmpty(inventoryItemList)) {
						logger.info("reserveOrderByOrderId inventoryItemList is null orderGoodsId:"+goods.getOrder_goods_id());						
						throw new RuntimeException("reserveOrderByOrderId inventoryItemList is null orderGoodsId:"+goods.getOrder_goods_id());
					}
					
					Iterator<Integer> inventoryItemIds = inventoryItemList.keySet().iterator();
					while(inventoryItemIds.hasNext()) {
						Integer inventoryItemId = inventoryItemIds.next();
						Integer quantity = inventoryItemList.get(inventoryItemId);
						
						logger.info("reserveOrderByOrderId orderGoodsId:"+goods.getOrder_goods_id()+
								" inventoryItemId:"+inventoryItemId.toString()+" quantity:"+quantity);

						OrderReserveInventoryMapping orderReserveInventoryMapping = new OrderReserveInventoryMapping();
						if(!"sale".equals(indicate)){
							orderReserveInventoryMapping.setOrder_goods_id(goods.getOrder_goods_id());
							orderReserveInventoryMapping.setQuantity(quantity);
							orderReserveInventoryMapping.setOms_order_goods_sn(goods.getOms_order_goods_sn());
							orderReserveInventoryMapping.setInventory_item_id(inventoryItemId);
							orderReserveInventoryMapping.setOrder_id(goods.getOrder_id());
							orderReserveInventoryMapping.setOrder_goods_oms_id(0);
							orderReserveInventoryMapping.setProduct_id(goods.getProduct_id());
						}else {
							orderReserveInventoryMapping.setOrder_goods_id(0);
							orderReserveInventoryMapping.setQuantity(quantity);
							orderReserveInventoryMapping.setOms_order_goods_sn(goods.getOms_order_goods_sn());
							orderReserveInventoryMapping.setInventory_item_id(inventoryItemId);
							orderReserveInventoryMapping.setOrder_id(goods.getOrder_id());
							orderReserveInventoryMapping.setOrder_goods_oms_id(goods.getOrder_goods_id());
							orderReserveInventoryMapping.setProduct_id(goods.getProduct_id());
						}
						Integer insertLine = orderReserveInventoryMappingDao.insert(orderReserveInventoryMapping);
						if (WorkerUtil.isNullOrEmpty(insertLine)) {
							throw new RuntimeException("insert order_reserve_inventory_mapping fail! order_goods_id:"+goods.getOrder_goods_id());
						}
					}
				}
				//}
				
				Integer updateLine = inventorySummaryDao.updateInventorySummaryForReserve(goods.getProduct_id(),goods.getWarehouse_id(),goods.getStatus_id(),goods.getGoods_number());
                if(WorkerUtil.isNullOrEmpty(updateLine) || updateLine != 1){
                	logger.info(logPrefix+" update inventory summary fail order_goods_id:"+goods.getOrder_goods_id()+" updateLine:"+updateLine);
                	throw new RuntimeException("update inventory summary fail!");
                }

				OrderReserveDetail orderReserveDetail = new OrderReserveDetail();
				orderReserveDetail.setCreated_time(new Date());
				orderReserveDetail.setGoods_number(goods.getGoods_number());
				orderReserveDetail.setInventory_status(goods.getStatus_id());
				orderReserveDetail.setOrder_id(order_id);
				orderReserveDetail.setProduct_id(goods.getProduct_id());
				orderReserveDetail.setReserved_number(goods.getGoods_number());
				orderReserveDetail.setStatus("Y");
				orderReserveDetail.setWarehouse_id(goods.getWarehouse_id());
				orderReserveDetail.setReserved_time(new Date());
				if(!"sale".equals(indicate)){
					orderReserveDetail.setOrder_goods_id(goods.getOrder_goods_id());
					orderReserveDetail.setOrder_goods_oms_id(0);
				}else {
					orderReserveDetail.setOrder_goods_oms_id(goods.getOrder_goods_id());
					orderReserveDetail.setOrder_goods_id(0);
				}
				Integer insertLine =  orderReserveDetailDao.insert(orderReserveDetail);
				logger.info("insert order_reserve_detail order_goods_id:"+goods.getOrder_goods_id()+" insert line :"+insertLine);
				if(WorkerUtil.isNullOrEmpty(insertLine)){
					throw new RuntimeException("insert order_reserve_detail fail! order_goods_id:"+goods.getOrder_goods_id());					
				}
			}
			
			
			Integer orderUpdateLine = orderInfoDao.updateOrderInfoForReserve(order_id,"Y");
			
			if (WorkerUtil.isNullOrEmpty(orderUpdateLine) || orderUpdateLine != 1) {
				throw new RuntimeException("update order_info status fail! order_id:"+order_id);
			}			
			Integer orderProcessUpdateLine = orderProcessDao.updateReserveStatus("Y",order_id);			
			if (WorkerUtil.isNullOrEmpty(orderProcessUpdateLine) || orderProcessUpdateLine != 1) {
				throw new RuntimeException("update order_process status fail! order_id:"+order_id);
			}
//			updateOrderReserveStatusByOrderId(order_id);
			
			
		Date endTime = new Date();
		double elapsed = endTime.getTime() - startTime.getTime();
		logger.info(logPrefix + " Finished, runtime:" + elapsed / 1000 + "s");
		
	}
	
	


    private HashMap<Integer, Integer> getAvailaleInventoryListForDeliver(
			Integer product_id, Integer warehouse_id, String status_id,
			Integer goods_number, String condition,String indicate) {
		
    	HashMap<Integer, Integer> inventoryItemList = new HashMap<Integer, Integer>();
    	List<InventoryAvailaleReserve> itemList =  new ArrayList<InventoryAvailaleReserve>();
		itemList = reserveOrderDao.getAvailaleInventoryListForDeliver(product_id, warehouse_id, status_id,condition);
		if(WorkerUtil.isNullOrEmpty(itemList)) {
			logger.info("getAvailaleInventoryListForDeliver is null ");
			return null;
		}
		
		Integer req = goods_number;
		
		for (InventoryAvailaleReserve inventoryAvailaleReserve : itemList) {
			
			Integer availableToReservedQuantity = inventoryAvailaleReserve.getQuantity()
					- inventoryAvailaleReserve.getReserved_quantity();

			logger.info("getAvailaleInventoryListForDeliver inventory_item_id:"+ 
					inventoryAvailaleReserve.getInventory_item_id() + " quantity:"+
					inventoryAvailaleReserve.getQuantity()+" reserved_quantity:"+inventoryAvailaleReserve.getReserved_quantity()+
			      " available_to_reserved_quantity:"+availableToReservedQuantity );
			
			if(availableToReservedQuantity <= 0) {
				continue;
			}
			
			//根据一个个item吸纳库存
			if(req >= availableToReservedQuantity) {
				inventoryItemList.put(inventoryAvailaleReserve.getInventory_item_id(), availableToReservedQuantity);
			} else {
				inventoryItemList.put(inventoryAvailaleReserve.getInventory_item_id(), req);
			}
			
			req = req - availableToReservedQuantity;
			if(req <= 0) {
				break;
			}
			
		}
		//库存不足
		if(req > 0) {
			logger.info("getAvailaleInventoryListForDeliver is not enough productId:"+product_id + " warehouse_id:"+ warehouse_id +" status_id"+status_id+" goods_number:"+goods_number);
		    return null;
		}
		
		return inventoryItemList;
	}

	/**
     * 
     * @param goods
     * @param indicate
     * @return 根据订单类型   拼接不同的查询条件
     */
    private String getDeliveryGoodsCondition(OrderGoods goods, String indicate) {  		
   		 String condition = " ";
//   		 if(indicate.equals("gt")){
//   			 
//   			 //供应商出库的操作 需考虑 批次号 供应商 采购单价 
//   			 OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(goods.getOrder_id());
//   			 if(!WorkerUtil.isNullOrEmpty(goods.getBatch_sn())){
//   				 condition += " and ii.batch_sn = '"+goods.getBatch_sn()+"'";
//   			 } 
//   			 if(!WorkerUtil.isNullOrEmpty(orderInfo.getProvider_code())){
//   				 condition += " and ii.provider_code = "+orderInfo.getProvider_code();
//   			 } 
//   			 if(!WorkerUtil.isNullOrEmpty(goods.getGoods_price())){
//   				 condition += " and ii.unit_cost = '"+goods.getGoods_price()+"'";
//   			 }    			 
//   		 }else
//   			 if(indicate.equals("v")) { 			
//   			//对于现在的 -v逻辑  批次号业务组-v需要考虑批次号  非批次号业务组走销售订单一样的流程 
//   			 if(!WorkerUtil.isNullOrEmpty(goods.getBatch_sn())){
//  				 condition += " and ii.batch_sn = '"+goods.getBatch_sn()+"'";
//  			 } 
//   			 
//   		 }else{
//   			 condition += ""; 
//   		 }
   		
   		 logger.info("getDeliveryGoodsCondition condition: "+condition);
   		
   		return condition;
	}

	@Override
	public void updateOrderReserveStatusByOrderId(Integer orderId) {
		
        logger.info("updateOrderReserveStatusByOrderId:"+orderId);
		List<OrderReserveDetail> orderReserveDetailList = orderReserveDetailDao.selectOrderReserveDetailByOrderId(orderId);
		logger.info("orderReserveDetailList size:"+orderReserveDetailList.size());
		if(WorkerUtil.isNullOrEmpty(orderReserveDetailList)){
			logger.info("orderReserveDetailList is empty,the order don't init the reserve detail");
			throw new RuntimeException("orderReserveDetailList is null");
		}
//		for (OrderReserveDetail orderReserveDetail : orderReserveDetailList) {
//			if (orderReserveDetail.getStatus().equals("N")) {
//				throw new RuntimeException("exit reserve detail not resered");
//			}
//		}
//		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(orderId);
//		logger.info("updateOrderReserveStatusByOrderId orderInfo:"+orderInfo.getOrder_id());
//		
//		OrderProcess orderProcess = orderProcessDao.selectByPrimaryKey(orderId);
//		logger.info("updateOrderReserveStatusByOrderId orderProcess:"+orderProcess.getOrder_id());
		
		orderInfoDao.updateOrderInfoForReserve(orderId,"Y");
		
//		orderProcess.setRecheck_status("Y");
//		orderProcess.setReserve_time(new Date());
		orderProcessDao.updateReserveStatus("Y",orderId);
		
//		throw new RuntimeException("HHHHHHHHHHHHHH");
	}



    @Override
	public int reserveOrderInventoryByOrderGoodsId(OrderReserveDetail orderReserveDetail) {
		

//		 InventorySummary inventorySummary = inventorySummaryDao.selectInventorySummary(orderReserveDetail.getProduct_id(), orderReserveDetail.getWarehouse_id(), orderReserveDetail.getInventory_status());
//		 
//	     inventorySummaryDao.updateAvailibleNumberForReserve(inventorySummary.getInventory_summary_id(), req);

			   
			 
 return 1;
		 
		 
	}





    @Override
	public void finishedOrderInventoryReservation(Integer orderId) {
		    
		  Integer updateReserveLine = orderReserveDetailDao.updateReserveStatusByOrderid(orderId,"F");
		  if(WorkerUtil.isNullOrEmpty(updateReserveLine)){
			  throw new RuntimeException("finishedOrderInventoryReservation update resserve_detail fail order_id:"+orderId);
		  }
		  Integer updateProcessLine = orderProcessDao.updateReserveStatus("F",orderId);
		  if(WorkerUtil.isNullOrEmpty(updateProcessLine) || updateProcessLine !=1){
			  throw new RuntimeException("finishedOrderInventoryReservation update order_process fail order_id:"+orderId);
		  }
		  Integer updateOrderLine  =orderInfoDao.updateOrderInfoForReserve(orderId, "F");
		  if(WorkerUtil.isNullOrEmpty(updateOrderLine) || updateOrderLine !=1){
			  throw new RuntimeException("finishedOrderInventoryReservation update order_info fail order_id:"+orderId);
		  }
	}




	@Override
	public List<Integer> getAllCustomerIdForReserve(String con) {
		
		return warehouseCustomerDao.selectAllCustomerId(con);
	}

	public Integer insertInventorySyncRecord(
			InventorySyncRecord inventorySyncRecord) {
		return inventorySyncRecordDao.insert(inventorySyncRecord);
		
	}

	public Map<Integer,Map> getReservedOrderIdForGT(Integer customerId, String condition) {
				
	 List<Map> orderIdListForGT = reserveOrderDao.getReservedOrderIdForGT(customerId,condition);
	 Map<Integer,Map> orderMapGT = new HashMap<Integer, Map>();	
     
	 if(!WorkerUtil.isNullOrEmpty(orderIdListForGT)){
		
			for (Map map : orderIdListForGT) {
				
				Integer order_id = (Integer) map.get("order_id");
				String product =  (String) map.get("product");				
				Long gnum = (Long) map.get("gnum");
				
				logger.info(" order_id:"+order_id+" product:"+product+" gnum:"+gnum);
				
				if (orderMapGT.containsKey(order_id)) {
					Map exitMap =orderMapGT.get(order_id); 
					exitMap.put(product, gnum.intValue());
				}
				else{
					HashMap<String, Integer> productMap = new HashMap<String, Integer>();
					productMap.put(product, gnum.intValue());
					orderMapGT.put(order_id, productMap);
				}		
				
			}
	 }	
		
		return  orderMapGT;
	}

	public Integer getLastReservedOrderId(Integer customerId) {

		return  reserveOrderDao.getLastReservedOrderId(customerId);
	}

	public List<Integer> getReservedOrderIdForSaleAfter(Integer customerId,
			Integer lastResevedOrderId) {
		return reserveOrderDao.getReservedOrderIdForSaleAfter(customerId,lastResevedOrderId);
	}

	public List<Integer> getReservedOrderIdForSaleBefore(Integer customerId,
			Integer lastResevedOrderId) {
		return  reserveOrderDao.getReservedOrderIdForSaleBefore(customerId, lastResevedOrderId);
	}

	public void insertOrderReserveRecord(OrderReserveRecord orderReserveRecord) {
		orderReserveRecordDao.insert(orderReserveRecord); 		
	}

	public List<Map> getOrderIdForReserve(Integer customerId, String nextOrders, String condition) {
        
		HashMap<String, Object> paramsHashMap = new HashMap<String, Object>();
		paramsHashMap.put("customerId", customerId);
		paramsHashMap.put("nextOrders", nextOrders);
		paramsHashMap.put("condition", condition);
		return reserveOrderDao.getOrderIdForReserve(paramsHashMap);
	}
	
	public List<Map> getPrepackOrderIdForReserve(Integer customerId, String nextOrders, String condition) {
        
		HashMap<String, Object> paramsHashMap = new HashMap<String, Object>();
		paramsHashMap.put("customerId", customerId);
		paramsHashMap.put("nextOrders", nextOrders);
		paramsHashMap.put("condition", condition);
		return reserveOrderDao.getPrepackOrderIdForReserve(paramsHashMap);
	}
	
	@Override
	public void reserveOrderByOrderId(Map orderMap) {
		// TODO Auto-generated method stub
		
	}

	public Integer getLastSyncDetailId() {
		return reserveOrderDao.getLastSyncDetailId();
	}

	public Integer getMaxIncreasedDetailId() {

		return reserveOrderDao.getMaxIncreasedDetailId();
	}

	public  void getIncrementInventoryItems(
			Integer inventoryItemDetailId, Integer maxIncreasedDetailId) {
		
		Date startTime = new Date();
		String logPrefix = "getIncrementInventoryItems";
		logger.info(logPrefix + " service start: " + startTime);

		//这里处理入库的订单  实际库存 与 可预订库存 都加
		List<InventorySyncItem> SyncItemList= reserveOrderDao.getIncrementInventoryItems(inventoryItemDetailId,maxIncreasedDetailId);
		if(WorkerUtil.isNullOrEmpty(SyncItemList)){
			logger.info("getIncrementInventoryItems SyncItemList is null");
		}else{
			for (InventorySyncItem inventorySyncItem : SyncItemList) {
			   
				logger.info("inventorySyncItem Product_id"+inventorySyncItem.getProduct_id()+" Warehouse_id"+inventorySyncItem.getWarehouse_id()+" Status"+inventorySyncItem.getStatus());
	            InventorySummary inventorySummary = inventorySummaryDao.selectInventorySummary(inventorySyncItem.getProduct_id(),inventorySyncItem.getWarehouse_id(),inventorySyncItem.getStatus());
	            
	            if(WorkerUtil.isNullOrEmpty(inventorySummary)){           	
	            	
	        		logger.info("create a new inventory_summary record");
	        		InventorySummary inventorySummaryNew = new InventorySummary();
	        		inventorySummaryNew.setAvailable_to_reserved(inventorySyncItem.getChange_quantity());
	        		inventorySummaryNew.setStock_quantity(inventorySyncItem.getChange_quantity());
	        		inventorySummaryNew.setProduct_id(inventorySyncItem.getProduct_id());
	        		inventorySummaryNew.setStatus_id(inventorySyncItem.getStatus());
	        		inventorySummaryNew.setWarehouse_id(inventorySyncItem.getWarehouse_id());
	        		inventorySummaryNew.setCustomer_id(inventorySyncItem.getCustomer_id());
	        		inventorySummaryNew.setCreated_stamp(new Date());
	        		inventorySummaryNew.setCreated_user("reserveSync");
	        		inventorySummaryNew.setLast_updated_stamp(new Date());
	        		inventorySummaryNew.setLast_updated_user("reserveSync");
	        		Integer imLine = inventorySummaryDao.insert(inventorySummaryNew);
	        		
	        		//因为希望程序不会因为某条记录的问题 而一直处于不能进入下一步  所以不抛出异常
	        		//异常问题导致的库存问题 交给 异常处理的程序执行
	        		if(WorkerUtil.isNullOrEmpty(imLine)){
	//        			throw new RuntimeException("insert inventory summary fail!");
	        		    logger.error("insert inventory summary fail!");
	        		    logger.error("product_id:"+inventorySyncItem.getProduct_id()+" warehouse_id:"+inventorySyncItem.getWarehouse_id()+" status_id:"+inventorySyncItem.getStatus());
	        		}
	        	}
	        	else{
	
	        			logger.info("inventorySummary : "+inventorySummary.getInventory_summary_id()+" InNumber:"+inventorySyncItem.getChange_quantity());
	        			Integer updateLine2 = inventorySummaryDao.updateStockAndAvailibleQuantity(inventorySummary.getInventory_summary_id(),inventorySyncItem.getChange_quantity(),inventorySyncItem.getChange_quantity());
	            		
	        			//因为希望程序不会因为某条记录的问题 而一直处于不能进入下一步  所以不抛出异常   异常问题导致的库存问题 交给 异常处理的调度执行           		
	        			if(WorkerUtil.isNullOrEmpty(updateLine2) || updateLine2!=1){
	            			logger.error(logPrefix+" update inventory summary fail! Inventory_summary_id:"+inventorySummary.getInventory_summary_id());
	//            			throw new RuntimeException("update inventory summary fail!");
	            		}
	        		
	        	}
			}
		}
		//这里处理出库的订单 实际库存减去 可预订库存不变
		List<InventorySyncItem> SyncItemListMinus= reserveOrderDao.getMinusInventoryItems(inventoryItemDetailId,maxIncreasedDetailId);
		if(WorkerUtil.isNullOrEmpty(SyncItemListMinus)){
			logger.info("getIncrementInventoryItems SyncItemListMinus is null");
		}else {
			for (InventorySyncItem inventorySyncItem : SyncItemListMinus) {
				
				logger.info("inventorySyncItem Product_id"+inventorySyncItem.getProduct_id()+" Warehouse_id"+inventorySyncItem.getWarehouse_id()+" Status"+inventorySyncItem.getStatus());
	            InventorySummary inventorySummary = inventorySummaryDao.selectInventorySummary(inventorySyncItem.getProduct_id(),inventorySyncItem.getWarehouse_id(),inventorySyncItem.getStatus());
	            
	            //这里其实属于异常 情况  但是为了让程序正常执行  这里不抛出异常  交给错误处理程序执行
	            if(WorkerUtil.isNullOrEmpty(inventorySummary)){  
	            	logger.error("inventoryItemDetailId:"+inventoryItemDetailId+" maxIncreasedDetailId:"+maxIncreasedDetailId);
	            	logger.error("minus can't get the inventory_summary product:"+inventorySyncItem.getProduct_id()+" warehourse_id:"+inventorySyncItem.getWarehouse_id()+" status_id:"+inventorySyncItem.getStatus());
	            }
	            
	    		Integer updateLine = inventorySummaryDao.updateStockQuantity(inventorySummary.getInventory_summary_id(),inventorySyncItem.getChange_quantity());
	    		
	    		//因为希望程序不会因为某条记录的问题 而一直处于不能进入下一步  所以不抛出异常   异常问题导致的库存问题 交给 异常处理的调度执行           
	    		if(WorkerUtil.isNullOrEmpty(updateLine) || updateLine!=1){
	    			logger.error("inventoryItemDetailId:"+inventoryItemDetailId+" maxIncreasedDetailId:"+maxIncreasedDetailId);
	    			logger.error(logPrefix+" update inventory summary fail! Inventory_summary_id:"+inventorySummary.getInventory_summary_id());
	//    			throw new RuntimeException("update inventory summary fail!");
	    		}
            
			}
			
		}
		
		/*
		 List<InventorySyncItem> SyncItemDetailList= reserveOrderDao.getIncrementInventoryItemsDetail(inventoryItemDetailId,maxIncreasedDetailId);
		 
		 for (InventorySyncItem inventorySyncItem : SyncItemDetailList) {
	 
			 logger.info("Warehouse_id:"+inventorySyncItem.getWarehouse_id()+" Status"+inventorySyncItem.getStatus()+" Unit_cost"+inventorySyncItem.getUnit_cost()+""
	 				+ " Provider_code"+inventorySyncItem.getProvider_code()+" Batch_sn"+inventorySyncItem.getBatch_sn());
	     	 
			 String batch_sn = inventorySyncItem.getBatch_sn();
			 if (WorkerUtil.isNullOrEmpty(batch_sn)) {
				 batch_sn ="";
			 }
			 
			 InventorySummaryDetail inventorySummaryDetail = inventorySummaryDetailDao.selectInventorySummaryDetailInfo(inventorySyncItem.getProduct_id(),
	     			inventorySyncItem.getWarehouse_id(),inventorySyncItem.getStatus(),inventorySyncItem.getUnit_cost(),
	     			inventorySyncItem.getProvider_code(),batch_sn);
			 
	     	 if(WorkerUtil.isNullOrEmpty(inventorySummaryDetail)){
	     		InventorySummaryDetail inventorySummaryDetailNew = new InventorySummaryDetail();
	     		inventorySummaryDetailNew.setStock_quantity(inventorySyncItem.getChange_quantity());
	     		inventorySummaryDetailNew.setCreated_stamp(new Date());
	     		inventorySummaryDetailNew.setCreated_user("reserveSync");
	     		inventorySummaryDetailNew.setLast_updated_stamp(new Date());
	     		inventorySummaryDetailNew.setLast_updated_user("reserveSync");
	     		inventorySummaryDetailNew.setProduct_id(inventorySyncItem.getProduct_id());
	     		inventorySummaryDetailNew.setWarehouse_id(inventorySyncItem.getWarehouse_id());
	     		inventorySummaryDetailNew.setStatus_id(inventorySyncItem.getStatus());
	     		inventorySummaryDetailNew.setUnit_price(inventorySyncItem.getUnit_cost());
	     		inventorySummaryDetailNew.setProvider_code(inventorySyncItem.getProvider_code());
	     		inventorySummaryDetailNew.setBatch_sn(batch_sn);
	     		inventorySummaryDetailNew.setCustomer_id(inventorySyncItem.getCustomer_id());
	     		
	     		logger.info(inventorySummaryDetailNew.toString());
	     	    Integer insertline = inventorySummaryDetailDao.insert(inventorySummaryDetailNew);
	     	    if (WorkerUtil.isNullOrEmpty(insertline)) {
					logger.info("insert inventorySummaryDetail falil");
//					throw new RuntimeException("insert inventorySummaryDetail falil");
				}
	     	    
	     	}else{
	     		 Integer updateline = inventorySummaryDetailDao.updateDetailStockQuantity(inventorySummaryDetail.getInventory_summary_detail_id(),inventorySyncItem.getChange_quantity());
	     		 if (WorkerUtil.isNullOrEmpty(updateline)) {
	 				logger.info("insert inventorySummaryDetail falil");
//	 				throw new RuntimeException("update inventorySummaryDetail falil");
	 			}	     	
	     	}
    	
	     }
		 */
		 
		 //对于同步到哪一个 item_detail_id严格控制
//		 if(!WorkerUtil.isNullOrEmpty(SyncItemList)) {
	        	logger.info("get the last inevntory_item_detail_id:"+maxIncreasedDetailId);
	        	InventorySyncRecord inventorySyncRecord = new InventorySyncRecord();
	        	inventorySyncRecord.setCreated_stamp(new Date());
	        	inventorySyncRecord.setCustomer_id(0);
	        	inventorySyncRecord.setInventory_item_detail_id(maxIncreasedDetailId);
	        	Integer insertLine = inventorySyncRecordDao.insert(inventorySyncRecord);
	        	if (WorkerUtil.isNullOrEmpty(insertLine)) {
		 				logger.info("insert inventorySummaryDetail falil");
		 				throw new RuntimeException("update inventorySyncRecord falil");
		 		}	
//	        }
		 
		 Date endTime = new Date();
		 double elapsed = endTime.getTime() - startTime.getTime();
		 logger.info(logPrefix + " Finished, runtime:" + elapsed / 1000 + "s");
	}

	public List<Integer> getDeliverOrderIdForFinishReserve() {
		return  reserveOrderDao.getDeliverOrderIdForFinishReserve();
	}

	//初始化库存不足的订单
	public void initNotEnoughStockOrder(Integer order_id) {
		
		List<OrderGoods> orderGoods = orderGoodsDao.selectByOrderId(order_id);
		
		if(WorkerUtil.isNullOrEmpty(orderGoods)){
			logger.info("the order_id not having order_goods record order_id:"+order_id);
			throw new RuntimeException("order_id:"+order_id+" no order goods");
		}
		
		for (OrderGoods goods : orderGoods) {
			
			if(WorkerUtil.isNullOrEmpty(goods.getProduct_id()) || WorkerUtil.isNullOrEmpty(goods.getWarehouse_id()) ||
					WorkerUtil.isNullOrEmpty(goods.getStatus_id()) || WorkerUtil.isNullOrEmpty(goods.getGoods_number())  ){
				throw new RuntimeException("查库存时订单商品信息不完整");
			}
			logger.info("prodcuct:"+goods.getProduct_id()+" Warehouse_id:"+goods.getWarehouse_id()+" Status_id:"+goods.getStatus_id()+" Goods_number:"+goods.getGoods_number());
			
			OrderReserveDetail orderReserveDetail = new OrderReserveDetail();
			orderReserveDetail.setCreated_time(new Date());
			orderReserveDetail.setGoods_number(goods.getGoods_number());
			orderReserveDetail.setInventory_status(goods.getStatus_id());
			orderReserveDetail.setOrder_id(order_id);
			orderReserveDetail.setProduct_id(goods.getProduct_id());
			orderReserveDetail.setReserved_number(goods.getGoods_number());
			orderReserveDetail.setStatus("E");
			orderReserveDetail.setWarehouse_id(goods.getWarehouse_id());
			orderReserveDetail.setReserved_time(new Date());
			orderReserveDetail.setOrder_goods_id(goods.getOrder_goods_id());
			Integer insertLine =  orderReserveDetailDao.insert(orderReserveDetail);
			
			logger.info("insert order_reserve_detail order_goods_id:"+goods.getOrder_goods_id()+" insert line :"+insertLine);
			if(WorkerUtil.isNullOrEmpty(insertLine)){
				throw new RuntimeException("insert order_reserve_detail fail! order_goods_id:"+goods.getOrder_goods_id());
			}
		}
		
	}

	public Map<Integer, Map> getOrderNotEnoughAndHasInstorageByCustomerId(
			Integer customerId, String condition) {
		
	    if(WorkerUtil.isNullOrEmpty(customerId)){
	    	logger.info("customerId is null");
	    	return null;
	    }
	    
	    Map<Integer,Map> orderMap = new HashMap<Integer, Map>();	
	    
	    InventoryItemDetail inventoryItemDetail = inventoryDao.getLastedInstorageInventoryByCustomerId(customerId);
		
	    if(WorkerUtil.isNullOrEmpty(inventoryItemDetail)){
	    	logger.info("该customer_id不存在入库记录");
	    	return null;
	    }
	    
	    List<Map> orderNotEnoughList = reserveOrderDao.getOrderNotEnoughAndHasInstorageByCustomerId(customerId,inventoryItemDetail.getCreated_time(),condition);
	    
	    for (Map map : orderNotEnoughList) {
			
			Integer order_id = (Integer) map.get("order_id");
			String product =  (String) map.get("product");				
			Long gnum = (Long) map.get("gnum");
			
			logger.info(" order_id:"+order_id+" product:"+product+" gnum:"+gnum);
			
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
		return orderMap;
	}

	public void updateOrderProcessForReserve(Integer order_id) {
			
		orderProcessDao.updateReserveStatus("E", order_id);
	}

	public void cancelOrderInventoryReservation(Integer order_id){
		
		logger.info("cancelOrderInventoryReservation order_id:"+order_id);
//		
//		//判断订单里是否有预打包商品
//		boolean flag = false;
//		List<OrderGoods> checkOrderGoods = orderGoodsDao.selectByOrderId(order_id);
//		for (OrderGoods checkGoods : checkOrderGoods) {
//			if(OrderGoods.ORDER_GOODS_TYPE_PREPACKAGE.equals(checkGoods.getOrder_goods_type())){
//				flag = true;
//			}
//		}
		
		//获取订单已经预定成功的商品数量 取消返回
		//假设订单里有预打包商品就根据oms_order_goods进行取消
		List<OrderReserveDetail> orderReserveDetailList = orderReserveDetailDao.selectOrderReserveDetailByOrderId(order_id);
		if(!WorkerUtil.isNullOrEmpty(orderReserveDetailList)){
			
			 logger.info("cancelOrderInventoryReservation is not null delete");
			 for (OrderReserveDetail orderReserveDetail : orderReserveDetailList) {
				 
				 if(orderReserveDetail.getStatus().equals("Y")){
					 InventorySummary inventorySummary =inventorySummaryDao.selectInventorySummary(orderReserveDetail.getProduct_id(), 
							 orderReserveDetail.getWarehouse_id(), orderReserveDetail.getInventory_status());
					 if(WorkerUtil.isNullOrEmpty(inventorySummary)){
						 logger.info("can't get inventory_summary order_id:"+order_id);
						 throw new RuntimeException("can't get inventory_summary order_id:"+order_id);
					 }
					 if(!WorkerUtil.isNullOrEmpty(inventorySummary) && orderReserveDetail.getReserved_number() > 0){
						Integer updateLine = inventorySummaryDao.updateAvailibleNumberForReserve(inventorySummary.getInventory_summary_id(), -orderReserveDetail.getReserved_number());
						if (WorkerUtil.isNullOrEmpty(updateLine)) {
							logger.info("update inventory_summary for delete fail order_id:"+order_id);
							throw new RuntimeException("delete orderReserveDetail fail order_id:"+order_id);
						 }
					 }
				 }
			 
			 }
			 Integer deleteLine = orderReserveDetailDao.deleteOrderReserveDetailByOrderId(order_id);
			 if (WorkerUtil.isNullOrEmpty(deleteLine)) {
				logger.info("delete orderReserveDetail fail order_id:"+order_id);
				throw new RuntimeException("delete orderReserveDetail fail order_id:"+order_id);
			 }
		}
		
		Integer orderUpdateLine = orderInfoDao.updateOrderInfoForReserve(order_id,"F");
		
		if (WorkerUtil.isNullOrEmpty(orderUpdateLine) || orderUpdateLine != 1) {
			throw new RuntimeException("update order_info status fail! order_id:"+order_id);
		}
		
		
		Integer orderProcessUpdateLine = orderProcessDao.updateReserveStatus("F",order_id);
		
		if (WorkerUtil.isNullOrEmpty(orderProcessUpdateLine) || orderProcessUpdateLine != 1) {
			throw new RuntimeException("update order_process status fail! order_id:"+order_id);
		}

	}

	public Map<Integer, Map> getReservedOrderIdForV(Integer customerId,
			String condition) {
		 List<Map> orderIdListForV = reserveOrderDao.getReservedOrderIdForV(customerId,condition);
		 Map<Integer,Map> orderMapV = new HashMap<Integer, Map>();	
	     
		 if(!WorkerUtil.isNullOrEmpty(orderIdListForV)){
			
				for (Map map : orderIdListForV) {
					
					Integer order_id = (Integer) map.get("order_id");
					String product =  (String) map.get("product");				
					Long gnum = (Long) map.get("gnum");
					
					logger.info(" order_id:"+order_id+" product:"+product+" gnum:"+gnum);
					
					if (orderMapV.containsKey(order_id)) {
						Map exitMap =orderMapV.get(order_id); 
						exitMap.put(product, gnum.intValue());
					}
					else{
						HashMap<String, Integer> productMap = new HashMap<String, Integer>();
						productMap.put(product, gnum.intValue());
						orderMapV.put(order_id, productMap);
					}		
					
				}
		 }				
		 return  orderMapV;
	}

	public Integer getLastSyncDetailIdByCustomerId(Integer customer_id) {
		return reserveOrderDao.getLastSyncDetailIdByCustomerId(customer_id);
	}

	public Integer getMaxIncreasedDetailIdByCustomerId(Integer customer_id) {
		return reserveOrderDao.getMaxIncreasedDetailIdByCustomerId(customer_id);
	}

	public void getIncrementInventoryItemsByCustomerId(
			Integer inventoryItemDetailId, Integer maxIncreasedDetailId,
			Integer customer_id) {

		
		Date startTime = new Date();
		String logPrefix = "getIncrementInventoryItems";
		logger.info(logPrefix + " service start: " + startTime+" customer_id"+customer_id);

		
		//这里处理入库的订单  实际库存 与 可预订库存 都加  将入库的记录添加到inventorysummary记录里
		doAddIncrementInventorySummary(inventoryItemDetailId,
				maxIncreasedDetailId, customer_id);
		
		
		//这里处理出库的订单 实际库存减去 可预订库存不变
		doUpdateMinusInventorySummary(inventoryItemDetailId,
				maxIncreasedDetailId, customer_id);
		
		
	    logger.info("get the last inevntory_item_detail_id:"+maxIncreasedDetailId +" customer_id"+customer_id);
	    
	    //TODO update
	    InventorySyncRecord inventorySyncRecord = new InventorySyncRecord();
	    inventorySyncRecord.setCreated_stamp(new Date());
	    inventorySyncRecord.setCustomer_id(customer_id);
	    inventorySyncRecord.setInventory_item_detail_id(maxIncreasedDetailId);
	    Integer insertLine = inventorySyncRecordDao.insert(inventorySyncRecord);
	    
	    if (WorkerUtil.isNullOrEmpty(insertLine)) {
	 		logger.info("insert inventorySummaryDetail falil"+" customer_id"+customer_id);
	 		throw new RuntimeException("update inventorySyncRecord falil");
		}	

		logger.info(logPrefix + " Finished");
	
	}

	/**
	 * 这里处理出库的订单 实际库存减去 可预订库存不变
	 * @param inventoryItemDetailId    inventory_sync_record此货主最大值
	 * @param maxIncreasedDetailId     InventoryItemdetail 此货主最大值
	 * @param customer_id 货主
	 */
	private void doUpdateMinusInventorySummary(Integer inventoryItemDetailId,
			Integer maxIncreasedDetailId, Integer customer_id) {
		
		 //这里处理出库的订单 实际库存减去 可预订库存不变
		List<InventorySyncItem> SyncItemMinusList= reserveOrderDao.getMinusInventoryItemsByCustomerId(inventoryItemDetailId,maxIncreasedDetailId,customer_id);
		
		
		if(WorkerUtil.isNullOrEmpty(SyncItemMinusList)){
			logger.info("getIncrementInventoryItems SyncItemListMinus is null"+" customer_id"+customer_id);
		}else {
			
			//此货主增加的product_id集合
			List<Integer> productIdAddList=new ArrayList<Integer>();
			
			for(InventorySyncItem isi:SyncItemMinusList){
				if(!productIdAddList.contains(isi.getProduct_id())){
					productIdAddList.add(isi.getProduct_id());
				}
			}
			
			//此货主涉及到的(原本的记录，应该更新)
			List<InventorySummary> inventorySummaryList=inventorySummaryDao.selectInventorySummaryListByProductIdList(productIdAddList);
			
			Map<String,InventorySummary> inventorySummaryMap=new HashMap<String,InventorySummary>();
			

			for (InventorySummary is:inventorySummaryList){
				is.setStock_quantity(0);
				inventorySummaryMap.put(is.getWarehouse_id()+"_"+is.getProduct_id()+"_"+is.getStatus_id(), is);
			}
			
			List<InventorySummary> updateInventorySummaryList=new ArrayList<InventorySummary>();
			
			for (InventorySyncItem inventorySyncItem : SyncItemMinusList) {
				
				
				String key = inventorySyncItem.getWarehouse_id()+"_"+inventorySyncItem.getProduct_id()+"_"+inventorySyncItem.getStatus(); 
	            
				if(null==inventorySummaryMap.get(key)){           	
	            	
	        		logger.info("inventorySyncItemMinus  Product_id"+inventorySyncItem.getProduct_id()+" Warehouse_id"+inventorySyncItem.getWarehouse_id()+" Status"+inventorySyncItem.getStatus()+" can't find inventorysummary"+" customer_id"+customer_id);
	        		
	        	}
	        	else
	        	{
	        		InventorySummary inventorySummary = inventorySummaryMap.get(key);
	        		inventorySummary.setStock_quantity(inventorySyncItem.getChange_quantity());
	        		updateInventorySummaryList.add(inventorySummary);
	        		//记录变化量
//	        		InventorySummary inventorySummaryNew = new InventorySummary();
//	        		inventorySummaryNew.setInventory_summary_id(inventorySummary.getInventory_summary_id());
//	        		inventorySummaryNew.setStock_quantity(inventorySummary.getStock_quantity()+inventorySyncItem.getChange_quantity()); //变化量  是一个负数
//	        		updateInventorySummaryList.add(inventorySummaryNew);
	        		
	        	}
            
			}
			
			
			//更新已有的
			if(!WorkerUtil.isNullOrEmpty(updateInventorySummaryList)){
				inventorySummaryDao.updateMinusList(updateInventorySummaryList);
			}
			
		}
	}

	/**
	 * 将入库的记录添加到inventorysummary记录里
	 * @param inventoryItemDetailId    inventory_sync_record此货主最大值
	 * @param maxIncreasedDetailId     InventoryItemdetail 此货主最大值
	 * @param customer_id 货主
	 */
	private void doAddIncrementInventorySummary(Integer inventoryItemDetailId,
			Integer maxIncreasedDetailId, Integer customer_id) {
		//这里处理入库的订单  实际库存 与 可预订库存 都加
		List<InventorySyncItem> syncItemAddList= reserveOrderDao.getIncrementInventoryItemsByCustomerId(inventoryItemDetailId,maxIncreasedDetailId,customer_id);
		
		//此货主增加的product_id集合
		List<Integer> productIdAddList=new ArrayList<Integer>();
		
		
		if(WorkerUtil.isNullOrEmpty(syncItemAddList)){
			logger.info("getIncrementInventoryItems SyncItemList is null");
		}else{
			

			for(InventorySyncItem isi:syncItemAddList){
				if(!productIdAddList.contains(isi.getProduct_id())){
					productIdAddList.add(isi.getProduct_id());
				}
			}
			
			//此货主涉及到的(原本的记录，应该更新)
			List<InventorySummary> inventorySummaryList=inventorySummaryDao.selectInventorySummaryListByProductIdList(productIdAddList);
			
			Map<String,InventorySummary> inventorySummaryMap=new HashMap<String,InventorySummary>();
			
			for (InventorySummary is:inventorySummaryList){
				is.setStock_quantity(0);
				is.setAvailable_to_reserved(0);
				inventorySummaryMap.put(is.getWarehouse_id()+"_"+is.getProduct_id()+"_"+is.getStatus_id(), is);
			}
			
			List<InventorySummary> insertInventorySummaryList=new ArrayList<InventorySummary>();
			List<InventorySummary> updateInventorySummaryList=new ArrayList<InventorySummary>();
			
			for (InventorySyncItem inventorySyncItem : syncItemAddList) {
			   
				String key = inventorySyncItem.getWarehouse_id()+"_"+inventorySyncItem.getProduct_id()+"_"+inventorySyncItem.getStatus(); 
	            
				if(null==inventorySummaryMap.get(key)){           	
	            	
	        		logger.info("create a new inventory_summary record");
	        		InventorySummary inventorySummaryNew = new InventorySummary();
	        		inventorySummaryNew.setAvailable_to_reserved(inventorySyncItem.getChange_quantity());
	        		inventorySummaryNew.setStock_quantity(inventorySyncItem.getChange_quantity());
	        		inventorySummaryNew.setProduct_id(inventorySyncItem.getProduct_id());
	        		inventorySummaryNew.setStatus_id(inventorySyncItem.getStatus());
	        		inventorySummaryNew.setWarehouse_id(inventorySyncItem.getWarehouse_id());
	        		inventorySummaryNew.setCustomer_id(inventorySyncItem.getCustomer_id());
	        		inventorySummaryNew.setCreated_stamp(new Date());
	        		inventorySummaryNew.setCreated_user("reserveSync");
	        		inventorySummaryNew.setLast_updated_stamp(new Date());
	        		inventorySummaryNew.setLast_updated_user("reserveSync");
	        		
	        		insertInventorySummaryList.add(inventorySummaryNew);
	        		
	        	}
	        	else
	        	{
	        		InventorySummary inventorySummary = inventorySummaryMap.get(key);
	        		inventorySummary.setAvailable_to_reserved(inventorySyncItem.getChange_quantity());
	        		inventorySummary.setStock_quantity(inventorySyncItem.getChange_quantity());
	        		updateInventorySummaryList.add(inventorySummary);
	        		
	        		//记录变化量
//	        		InventorySummary inventorySummaryNew = new InventorySummary();
//	        		inventorySummaryNew.setInventory_summary_id(inventorySummary.getInventory_summary_id());
//	        		inventorySummaryNew.setStock_quantity(inventorySummary.getStock_quantity()+inventorySyncItem.getChange_quantity());
//	        		inventorySummaryNew.setAvailable_to_reserved(inventorySummary.getAvailable_to_reserved()+inventorySyncItem.getChange_quantity());
//	        		updateInventorySummaryList.add(inventorySummaryNew);
	        		
	        	}
			}
			
			//插入没有的记录
			if(!WorkerUtil.isNullOrEmpty(insertInventorySummaryList)){
				inventorySummaryDao.insertList(insertInventorySummaryList);
			}
			
			//更新已有的
			if(!WorkerUtil.isNullOrEmpty(updateInventorySummaryList)){
				inventorySummaryDao.updateAddList(updateInventorySummaryList);
			}
			
			
		}
	}

	public Map<Integer, List<Map>> getReservedOrderIdForGTAndVarianceMinus(
			Integer customer_id, String condition) {

		 List<Map> gTAndVarianceMinusOrderIdList = reserveOrderDao.getReservedOrderIdListForGTAndVarianceMinusByHzl(customer_id,condition);
		 
//		 List<Integer> gTOrVMinusOrderIdList = reserveOrderDao.getReservedOrderIdListForGTAndVarianceMinus(customer_id,condition);
//		 List<Map> gTAndVarianceMinusOrderIdList = new ArrayList<Map>();
//		 if(!WorkerUtil.isNullOrEmpty(gTOrVMinusOrderIdList)){
//			 gTAndVarianceMinusOrderIdList = reserveOrderDao.getReservedOrderIdForGTAndVarianceMinusV2(gTOrVMinusOrderIdList);
//		 }
		 
		 Map<Integer,List<Map>> orderMap = new HashMap<Integer, List<Map>>();	
	     
		 if(!WorkerUtil.isNullOrEmpty(gTAndVarianceMinusOrderIdList)){
			
				for (Map map : gTAndVarianceMinusOrderIdList) {
					
					Integer order_id = (Integer) map.get("order_id");
					String product =  (String) map.get("product");				
					Number gnum = (Number) map.get("gnum");
					
					logger.info(" order_id:"+order_id+" product:"+product+" gnum:"+gnum);
					
					if (orderMap.containsKey(order_id)) {
						List<Map> listTemp=orderMap.get(order_id);
						listTemp.add(map);
						orderMap.put(order_id,listTemp);
					}
					else{
						List<Map> listTemp=new ArrayList<Map>();
						listTemp.add(map);
						orderMap.put(order_id,listTemp);
					}		
					
				}
		 }	
			
		return  orderMap;
		
	}

	public Integer reserveOrdersRealForGTAndVarianceMinus(
			Map<String, Integer> productNumMap, Map<String, Integer> productSummaryIdMap, Map<Integer, List<Map>> orderMap, Integer customer_id) {

		Integer succeseNum=0;
		
		//List<Integer> orderIdList=(List<Integer>) orderMap.keySet();
		List<Integer> orderIdList=new ArrayList<Integer>();
		orderIdList.addAll(orderMap.keySet());
		//for  update orderProcess 拒绝其他操作  诸如取消
		List<OrderProcess> orderProcessList =orderProcessDao.selectOrdersForLock2Task(orderIdList);
		
		
		
		// 每个gt && -v minus订单的商品总数
		Map<Integer, List<Map>>  reserveOrderGoodMap = new HashMap<Integer, List<Map>>();
		// 2.统计商品总数
		List<Map> reserveOrderGoods = orderInfoDao.selectGtOrderGoodsListV5(orderIdList);
		//将该货主所有订单商品  按照订单号 组装成map
		chengeOrderGoodsToMap(reserveOrderGoodMap, reserveOrderGoods);
		

		//查询该货主 所有的inventory_item  quantity是扣除已预订的数量
		List<InventoryItem> inventoryItemList = inventoryDao.getInventoryItemsByCustomerId(customer_id);
		Map<String,List<InventoryItem>> inventoryItemMap=new HashMap<String,List<InventoryItem>>();
		//将查询的可预定库存记录  抓换成 product_id+"_"+warehouse_id+"_"+ 状态（normal:1|defective:0）
		changeInventoryListToMap(inventoryItemList, inventoryItemMap);
		
		
		Map<Integer,OrderProcess> orderProcessMap = new HashMap<Integer,OrderProcess>();
		
		for(OrderProcess op:orderProcessList){
			orderProcessMap.put(op.getOrder_id(), op);
		}
		
		//需要插入的
		List<OrderReserveInventoryMapping> orimInsertList= new ArrayList<OrderReserveInventoryMapping>();
		List<OrderReserveDetail> ordInsertList= new ArrayList<OrderReserveDetail>();
		List<InventorySummary> inventorySummaryUpdateList= new ArrayList<InventorySummary>();
		
		//记录订单号 用于更新是否预订状态
		List<Integer> orderYList=new ArrayList<Integer>();
		List<Integer> orderEList=new ArrayList<Integer>();
		
		if(!WorkerUtil.isNullOrEmpty(orderMap)){
			
			 for (Entry<Integer, List<Map>> entry : orderMap.entrySet()) {
					
				 Integer order_id = entry.getKey();
				 
				 if(!(OrderInfo.ORDER_STATUS_ACCEPT.equalsIgnoreCase(orderProcessMap.get(order_id).getStatus())
						 &&("N".equalsIgnoreCase(orderProcessMap.get(order_id).getReserve_status())
								 ||("E".equalsIgnoreCase(orderProcessMap.get(order_id).getReserve_status()))))){
					 continue;
				 }
				 
				 List<Map> list=entry.getValue();
				 
				 Boolean is_enough = true;
				 
				 for(Map m:list){
					 
					 //product_id+'_'+warehouse_id+'_'+if(og.status_id='NORMAL',1,0))
					 String product = m.get("product").toString();
					 Integer gnum = Integer.parseInt(m.get("gnum").toString());
					 Integer available=null==productNumMap.get(product)?0:productNumMap.get(product);
					 
					 if(gnum>available){
						 orderEList.add(order_id);
						 is_enough = false;
						   break;
					 }
					 
				 }
				 
				 if(is_enough == true){
					 orderYList.add(order_id);
					 List<Map> oglist = reserveOrderGoodMap.get(order_id);
					 
					 for(Map m:oglist){
					 						 
						 int order_goods_id = null==m.get("order_goods_id")?0:Integer.parseInt(m.get("order_goods_id").toString());
						 int product_id = null==m.get("product_id")?0:Integer.parseInt(m.get("product_id").toString());
						 int warehouse_id = null==m.get("warehouse_id")?0:Integer.parseInt(m.get("warehouse_id").toString());
						 String status = null==m.get("status_id")?"NORMAL":m.get("status_id").toString();
						 int statusAsInt = "NORMAL".equalsIgnoreCase(m.get("status_id").toString())?1:0;
						 int goods_number = null==m.get("goods_number")?0:Integer.parseInt(m.get("goods_number").toString());
						 String oms_order_goods_sn = null==m.get("oms_order_goods_sn")?"":m.get("oms_order_goods_sn").toString();
						 
						 //插入一条 order_reserve_detail
						 
						 List<InventoryItem> iiList = inventoryItemMap.get(product_id+"_"+warehouse_id+"_"+statusAsInt);
						 int sum=0;
						 for (InventoryItem ii:iiList){
							 //全部预定掉
							 if(ii.getQuantity()<goods_number-sum){
								
								 sum+=ii.getQuantity();
								 
								 //插入一条 order_reserve_inventory_mapping
								 OrderReserveInventoryMapping  orim = new OrderReserveInventoryMapping();
								 orim.setInventory_item_id(ii.getInventory_item_id());
								 orim.setProduct_id(product_id);
								 orim.setOrder_goods_id(order_goods_id);
								 orim.setOrder_goods_oms_id(0);
								 orim.setOrder_id(order_id);
								 orim.setQuantity(ii.getQuantity());
								 orim.setOms_order_goods_sn(oms_order_goods_sn);
								 orimInsertList.add(orim);
								 
								 ii.setQuantity(0);
								
							 }
							 //足够了  
							 else{
								//插入一条 order_reserve_inventory_mapping
								 OrderReserveInventoryMapping  orim = new OrderReserveInventoryMapping();
								 orim.setInventory_item_id(ii.getInventory_item_id());
								 orim.setProduct_id(product_id);
								 orim.setOrder_goods_id(order_goods_id);
								 orim.setOrder_goods_oms_id(0);
								 orim.setOrder_id(order_id);
								 orim.setQuantity(goods_number-sum);
								 orim.setOms_order_goods_sn(oms_order_goods_sn);
								 orimInsertList.add(orim);
								 
								 ii.setQuantity(ii.getQuantity()+sum-goods_number);
								 break;
							 }
						 }
						 //插入 order_reserve_detail
						 OrderReserveDetail ord=new OrderReserveDetail();
						 ord.setStatus("Y");
						 ord.setGoods_number(goods_number);
						 ord.setOrder_id(order_id);
						 ord.setOrder_goods_id(order_goods_id);
						 ord.setProduct_id(product_id);
						 ord.setWarehouse_id(warehouse_id);
						 ord.setReserved_number(goods_number);
						 ord.setOrder_goods_oms_id(0);
						 ord.setInventory_status(status);
						 ordInsertList.add(ord);
						 
						 
						 //记录 inventory_summary change
						 InventorySummary inventorySummaryNew=new InventorySummary();
						 inventorySummaryNew.setInventory_summary_id(productSummaryIdMap.get(product_id+"_"+warehouse_id+"_"+statusAsInt));
						 inventorySummaryNew.setAvailable_to_reserved(goods_number);
						 
						 inventorySummaryUpdateList.add(inventorySummaryNew);
						 
						 //减少可预定量
						 productNumMap.put(product_id+"_"+warehouse_id+"_"+statusAsInt, productNumMap.get(product_id+"_"+warehouse_id+"_"+statusAsInt) - goods_number);
						 
						 succeseNum++;
					 }

				 }

			  }
			 
			  
			  if(!WorkerUtil.isNullOrEmpty(orimInsertList)){
				  orderReserveInventoryMappingDao.batchInsert(orimInsertList);
			  }
			  
			  if(!WorkerUtil.isNullOrEmpty(ordInsertList)){
				  orderReserveDetailDao.batchInsert(ordInsertList);
			  }
			  
			  if(!WorkerUtil.isNullOrEmpty(inventorySummaryUpdateList)){
				   //要操作同一条记录先行归并
					Map<Integer,InventorySummary> updateIsMap =new HashMap<Integer,InventorySummary>();
					List<InventorySummary> updateIsList=new ArrayList<InventorySummary>();
					for(InventorySummary is:inventorySummaryUpdateList){
						//如果没有则加到记录中
						if(null==updateIsMap.get(is.getInventory_summary_id())){
							updateIsMap.put(is.getInventory_summary_id(), is);
							updateIsList.add(is);
						}else{
							InventorySummary is2=updateIsMap.get(is.getInventory_summary_id());
							is2.setAvailable_to_reserved(is2.getAvailable_to_reserved()+is.getAvailable_to_reserved());
						}
					}

					inventorySummaryDao.updateInventorySummaryReserveList(updateIsList);
			  }
			
			  if(!WorkerUtil.isNullOrEmpty(orderYList)){
				  orderInfoDao.updateReserveResultList(orderYList,"Y");
				  orderProcessDao.updateReserveResultList(orderYList,"Y");
			  }
			  
			  if(!WorkerUtil.isNullOrEmpty(orderEList)){
				  orderInfoDao.updateReserveResultList(orderEList,"E");
				  orderProcessDao.updateReserveResultList(orderEList,"E");
			  }
			  
			}
		    else{
				logger.info("orderMap orders 0");			
			}
			 

	
		
		return succeseNum;
	}
	
	private void chengeOrderGoodsToMap(
			Map<Integer, List<Map>> reserveOrderGoodMap,
			List<Map> reserveOrderGoods) {
		if(!WorkerUtil.isNullOrEmpty(reserveOrderGoods)){
			for (Map m : reserveOrderGoods){
				if(null==reserveOrderGoodMap.get(Integer.parseInt(m.get("order_id").toString()))){
					List<Map> rOrderGood = new ArrayList<Map>();
					rOrderGood.add(m);
					reserveOrderGoodMap.put(Integer.parseInt(m.get("order_id").toString()), rOrderGood);
				}
				else{
					List<Map> rOrderGood = reserveOrderGoodMap.get(Integer.parseInt(m.get("order_id").toString()));
					rOrderGood.add(m);
				}
			}
		}
		
	}

	private void changeInventoryListToMap(
			List<InventoryItem> inventoryItemList,
			Map<String, List<InventoryItem>> inventoryItemMap) {
		
		if(!WorkerUtil.isNullOrEmpty(inventoryItemList)){
			for (InventoryItem ii:inventoryItemList){
				StringBuffer keyBuffer = new StringBuffer("");
				keyBuffer.append(ii.getProduct_id()).append('_').append(ii.getWarehouse_id()).append('_');
				if("NORMAL".equalsIgnoreCase(ii.getStatus())){
					keyBuffer.append(1);
				}else{
					keyBuffer.append(0);
				}
				
				if(null==inventoryItemMap.get(keyBuffer.toString())){
					List<InventoryItem> inventoryItemListTemp=new ArrayList<InventoryItem>();
					inventoryItemListTemp.add(ii);
					inventoryItemMap.put(keyBuffer.toString(), inventoryItemListTemp);
				}else{
					List<InventoryItem> inventoryItemListTemp=inventoryItemMap.get(keyBuffer.toString());
					inventoryItemListTemp.add(ii);
				}
			}
		}
		
	}

	public Map<Integer, List<Map>> getReservedOrderIdForSaleOrder(
			Integer customer_id, String condition, String reserve_status) {
		
		List<Map> saleOrderIdListList = reserveOrderDao.getReservedOrderIdForSaleOrderByHzl(customer_id,condition,reserve_status);

//		 List<Integer> saleOrderIdList = reserveOrderDao.getReservedOrderIdListForSaleOrder(customer_id,condition);
//		 List<Map> saleOrderIdListList = new ArrayList<Map>();
//		 if(!WorkerUtil.isNullOrEmpty(saleOrderIdList)){
//			 saleOrderIdListList = reserveOrderDao.getReservedOrderIdForSaleOrderV2(saleOrderIdList);
//		 }
//		 
		 
		 Map<Integer,List<Map>> orderMap = new HashMap<Integer, List<Map>>();	
	     
		 if(!WorkerUtil.isNullOrEmpty(saleOrderIdListList)){
			
				for (Map map : saleOrderIdListList) {
					
					Integer order_id = (Integer) map.get("order_id");
					String product =  (String) map.get("product");				
					//Long gnum = (Long) map.get("gnum");
					
					logger.info(" order_id:"+order_id+" product:"+product + "reserve_status"+reserve_status);
					
					if (orderMap.containsKey(order_id)) {
						List<Map> listTemp=orderMap.get(order_id);
						listTemp.add(map);
						orderMap.put(order_id,listTemp);
					}
					else{
						List<Map> listTemp=new ArrayList<Map>();
						listTemp.add(map);
						orderMap.put(order_id,listTemp);
					}		
					
				}
		 }	
			
		return  orderMap;
		
	
	}

	public Integer reserveOrdersRealForSaleOrder(
			Map<String, Integer> productNumMap,
			Map<String, Integer> productSummaryIdMap,
			Map<Integer, List<Map>> orderMap, Integer customer_id) {


		Integer succeseNum=0;
		

		List<Integer> orderIdList=new ArrayList<Integer>();
		orderIdList.addAll(orderMap.keySet());
		//for  update orderProcess 拒绝其他操作  诸如取消
		List<OrderProcess> orderProcessList =orderProcessDao.selectOrdersForLock2Task(orderIdList);
		
		
		
		// 每个gt && -v minus订单的商品总数
		Map<Integer, List<Map>>  reserveOrderGoodMap = new HashMap<Integer, List<Map>>();
		// 2.统计商品总数
		List<Map> reserveOrderGoods = orderInfoDao.selectSaleOrderGoodsListV5(orderIdList);
		//将该货主所有订单商品  按照订单号 组装成map
		chengeOrderGoodsToMap(reserveOrderGoodMap, reserveOrderGoods);
		

		//查询该货主 所有的inventory_item  quantity是扣除已预订的数量
		List<InventoryItem> inventoryItemList = inventoryDao.getInventoryItemsByCustomerId(customer_id);
		Map<String,List<InventoryItem>> inventoryItemMap=new HashMap<String,List<InventoryItem>>();
		//将查询的可预定库存记录  抓换成 product_id+"_"+warehouse_id+"_"+ 状态（normal:1|defective:0）
		changeInventoryListToMap(inventoryItemList, inventoryItemMap);
		
		
		Map<Integer,OrderProcess> orderProcessMap = new HashMap<Integer,OrderProcess>();
		
		for(OrderProcess op:orderProcessList){
			orderProcessMap.put(op.getOrder_id(), op);
		}
		
		//需要插入的
		List<OrderReserveInventoryMapping> orimInsertList= new ArrayList<OrderReserveInventoryMapping>();
		List<OrderReserveDetail> ordInsertList= new ArrayList<OrderReserveDetail>();
		List<InventorySummary> inventorySummaryUpdateList= new ArrayList<InventorySummary>();
		
		//记录订单号 用于更新是否预订状态
		List<Integer> orderYList=new ArrayList<Integer>();
		List<Integer> orderEList=new ArrayList<Integer>();
		
		if(!WorkerUtil.isNullOrEmpty(orderMap)){
			
			 for (Entry<Integer, List<Map>> entry : orderMap.entrySet()) {
					
				 Integer order_id = entry.getKey();
				 logger.info("reserveOrdersRealForSaleOrder dlyaoAdd20161019  order_id:"+order_id);	
				 if(!(OrderInfo.ORDER_STATUS_ACCEPT.equalsIgnoreCase(orderProcessMap.get(order_id).getStatus())
						 &&("N".equalsIgnoreCase(orderProcessMap.get(order_id).getReserve_status())
								 ||("E".equalsIgnoreCase(orderProcessMap.get(order_id).getReserve_status()))))){
					 continue;
				 }
				 
				 List<Map> list=entry.getValue();
				 
				 Boolean is_enough = true;
				 
				 for(Map m:list){
					 
					 //product_id+'_'+warehouse_id+'_'+if(og.status_id='NORMAL',1,0))
					 String product = m.get("product").toString();
					 Integer gnum = Integer.parseInt(m.get("gnum").toString());
					 Integer available=null==productNumMap.get(product)?0:productNumMap.get(product);
					 
					 if(gnum>available){
						 orderEList.add(order_id);
						 is_enough = false;
						   break;
					 }
					 
				 }
				 
				 if(is_enough == true){

					 orderYList.add(order_id);
					 
					 List<Map> oglist = reserveOrderGoodMap.get(order_id);
					 
					 for(Map m:oglist){
					 						 
						 int order_goods_id = null==m.get("order_goods_id")?0:Integer.parseInt(m.get("order_goods_id").toString());
						 int product_id = null==m.get("product_id")?0:Integer.parseInt(m.get("product_id").toString());
						 int warehouse_id = null==m.get("warehouse_id")?0:Integer.parseInt(m.get("warehouse_id").toString());
						 String status = null==m.get("status_id")?"NORMAL":m.get("status_id").toString();
						 int statusAsInt = "NORMAL".equalsIgnoreCase(m.get("status_id").toString())?1:0;
						 int goods_number = null==m.get("goods_number")?0:Integer.parseInt(m.get("goods_number").toString());
						 String oms_order_goods_sn = null==m.get("oms_order_goods_sn")?"":m.get("oms_order_goods_sn").toString();
						 
						 //插入一条 order_reserve_detail
						 
						 List<InventoryItem> iiList = inventoryItemMap.get(product_id+"_"+warehouse_id+"_"+statusAsInt);
						 int sum=0;
						 for (InventoryItem ii:iiList){
							 //全部预定掉
							 if(ii.getQuantity()<goods_number-sum){
								
								 sum+=ii.getQuantity();
								 
								 //插入一条 order_reserve_inventory_mapping
								 OrderReserveInventoryMapping  orim = new OrderReserveInventoryMapping();
								 orim.setInventory_item_id(ii.getInventory_item_id());
								 orim.setProduct_id(product_id);
								 orim.setOrder_goods_id(0);
								 orim.setOrder_goods_oms_id(order_goods_id);
								 orim.setOrder_id(order_id);
								 orim.setQuantity(ii.getQuantity());
								 orim.setOms_order_goods_sn(oms_order_goods_sn);
								 orimInsertList.add(orim);
								 
								 ii.setQuantity(0);
								
							 }
							 //足够了  
							 else{
								//插入一条 order_reserve_inventory_mapping
								 OrderReserveInventoryMapping  orim = new OrderReserveInventoryMapping();
								 orim.setInventory_item_id(ii.getInventory_item_id());
								 orim.setProduct_id(product_id);
								 orim.setOrder_goods_id(0);
								 orim.setOrder_goods_oms_id(order_goods_id);
								 orim.setOrder_id(order_id);
								 orim.setQuantity(goods_number-sum);
								 orim.setOms_order_goods_sn(oms_order_goods_sn);
								 orimInsertList.add(orim);
								 
								 ii.setQuantity(ii.getQuantity()+sum-goods_number);
								 break;
							 }
						 }
						 //插入 order_reserve_detail
						 OrderReserveDetail ord=new OrderReserveDetail();
						 ord.setStatus("Y");
						 ord.setGoods_number(goods_number);
						 ord.setOrder_id(order_id);
						 ord.setOrder_goods_id(0);
						 ord.setOrder_goods_oms_id(order_goods_id);
						 ord.setProduct_id(product_id);
						 ord.setWarehouse_id(warehouse_id);
						 ord.setReserved_number(goods_number);						 
						 ord.setInventory_status(status);
						 ordInsertList.add(ord);
						 
						 
						 //记录 inventory_summary change
						 InventorySummary inventorySummaryNew=new InventorySummary();
						 inventorySummaryNew.setInventory_summary_id(productSummaryIdMap.get(product_id+"_"+warehouse_id+"_"+statusAsInt));
						 inventorySummaryNew.setAvailable_to_reserved(goods_number);
						 
						 inventorySummaryUpdateList.add(inventorySummaryNew);
						 
						 //减少可预定量
						 productNumMap.put(product_id+"_"+warehouse_id+"_"+statusAsInt, productNumMap.get(product_id+"_"+warehouse_id+"_"+statusAsInt) - goods_number);
						 
						 succeseNum++;
					 }

				 }

			  }
			 
			  
			  if(!WorkerUtil.isNullOrEmpty(orimInsertList)){
				  orderReserveInventoryMappingDao.batchInsert(orimInsertList);
			  }
			  
			  if(!WorkerUtil.isNullOrEmpty(ordInsertList)){
				  orderReserveDetailDao.batchInsert(ordInsertList);
			  }
			  
			  if(!WorkerUtil.isNullOrEmpty(inventorySummaryUpdateList)){
				   //要操作同一条记录先行归并
					Map<Integer,InventorySummary> updateIsMap =new HashMap<Integer,InventorySummary>();
					List<InventorySummary> updateIsList=new ArrayList<InventorySummary>();
					for(InventorySummary is:inventorySummaryUpdateList){
						//如果没有则加到记录中
						if(null==updateIsMap.get(is.getInventory_summary_id())){
							updateIsMap.put(is.getInventory_summary_id(), is);
							updateIsList.add(is);
						}else{
							InventorySummary is2=updateIsMap.get(is.getInventory_summary_id());
							is2.setAvailable_to_reserved(is2.getAvailable_to_reserved()+is.getAvailable_to_reserved());
						}
					}

					inventorySummaryDao.updateInventorySummaryReserveList(updateIsList);
			  }
			  
			  if(!WorkerUtil.isNullOrEmpty(orderYList)){
				  orderInfoDao.updateReserveResultList(orderYList,"Y");
				  orderProcessDao.updateReserveResultList(orderYList,"Y");
			  }
			  
			  if(!WorkerUtil.isNullOrEmpty(orderEList)){
				  orderInfoDao.updateReserveResultList(orderEList,"E");
				  orderProcessDao.updateReserveResultList(orderEList,"E");
			  }
			
			}
		    else{
				logger.info("orderMap orders 0");			
			}

		return succeseNum;
	
	}
 
	
}
