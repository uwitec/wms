package com.leqee.wms.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.response.Response;
import com.leqee.wms.service.InventoryService;

@Service
public class InventoryJob extends CommonJob {
	private Logger logger = Logger.getLogger(InventoryJob.class);
	
	@Autowired
	InventoryService inventoryService;
	@Autowired
	WarehouseDao warehouseDao;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;

	/**
	 * 正常销售订单出库流程
	 * @author hzhang1
	 * @param paramNameValue
	 */
	public void saleInventoryOut(String paramNameValue){
		String methodName = "job:InventoryJob->saleInventoryOut";
		try{
			logger.info(methodName+ "_start paramNameValue=" + paramNameValue);
			Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Response resp =  inventoryService.saleInventoryOut( (Integer)serviceParamsMap.get("groupId") , 
							(Integer)serviceParamsMap.get("customerId"), (Double)serviceParamsMap.get("hours") , (String)serviceParamsMap.get("endDate"));
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage());
		}
		
	}
	
	/**
	 * 大后门批量单直接出库
	 * @author hzhang1
	 * @param paramNameValue
	 */
	public void saleInventoryOutFor1111(String paramNameValue){
		String methodName = "job:InventoryJob->saleInventoryOut";
		try{
			logger.info(methodName+ "_start paramNameValue=" + paramNameValue);
			Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Response resp =  inventoryService.saleInventoryOutFor1111( (Integer)serviceParamsMap.get("groupId") , 
							(Integer)serviceParamsMap.get("customerId"), (Double)serviceParamsMap.get("hours") , (String)serviceParamsMap.get("endDate"));
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage());
		}
		
	}
	
	/**
	 * 耗材出库  by hzhang1
	 * 兼容于销售，-v，-gt订单
	 * @param paramNameValue
	 */
	public void packBoxInventoryOut(String paramNameValue){
		String methodName = "job:InventoryJob->packBoxInventoryOut";
		try{
			logger.info(methodName+ "_start paramNameValue=" + paramNameValue);
			Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Response resp =  inventoryService.packBoxInventoryOut( (Integer)serviceParamsMap.get("groupId") , 
							(Integer)serviceParamsMap.get("customerId"), (Double)serviceParamsMap.get("hours") , (String)serviceParamsMap.get("endDate"));
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage());
		}
		
	}
	
	
	public void packBoxInventoryOutV2(String paramNameValue){
		String methodName = "job:InventoryJob->packBoxInventoryOutV2";
		try{
			logger.info(methodName+ "_start paramNameValue=" + paramNameValue);
			Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Response resp =  inventoryService.packBoxInventoryOutV2( (Integer)serviceParamsMap.get("groupId") , 
							(Integer)serviceParamsMap.get("customerId"), (Double)serviceParamsMap.get("hours") , (String)serviceParamsMap.get("endDate"));
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage());
		}
		
	}
	
	/**
	 * 耗材出库  by dlyao
	 * 预打包消耗掉的耗材
	 * @param paramNameValue
	 */
	public void packBoxInventoryOutForPrePack(String paramNameValue){
		String methodName = "job:InventoryJob->packBoxInventoryOutForPrePack";
		
			logger.info(methodName+ "_start paramNameValue=" + paramNameValue);
			Map<String, Object> serviceParamsMap = getServiceParamsMapByDlYao(paramNameValue);
			
			List<Integer> list=new ArrayList<Integer>();
			List<Integer> list2=new ArrayList<Integer>();
			int physical_warehouse_id= (Integer)serviceParamsMap.get("physical_warehouse_id");
			int groupId=(Integer)serviceParamsMap.get("groupId");
			int customer_id=(Integer)serviceParamsMap.get("customerId");
			int days=(Integer)serviceParamsMap.get("days");
			
			java.util.Date now = new java.util.Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期 
			Calendar c = Calendar.getInstance();
			c.setTime(now);

			//c.set(Calendar.DATE, c.get(Calendar.DATE)-days);//修改天数  0表示今天  1表示昨天  -1表示明天  
			c.set(Calendar.DATE, c.get(Calendar.DATE)-days);
			String start=dateFormat.format(c.getTime())+" 00:00:00";
			
			if(physical_warehouse_id-0==0){
				list=warehouseDao.selectPhysicalWarehouseIdList();
			}else{
				list.add(physical_warehouse_id);
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
			
			for(int p_id:list){
				for(int c_id:list2){
					try{
						Response resp =  inventoryService.packBoxInventoryOutForPrePack(p_id, c_id,start);
						logger.info(methodName+ "_end resp=" + resp.toString());
					}catch(Exception e){
						logger.error(methodName +"_error exception=" + e.getMessage());
					}
					
				}
			}
			
		
	}
	
	/**
	 * 仓库内部导入product_location库存
	 * by hzhang1
	 * @param paramNameValue
	 */
	public void importProductLocation(String paramNameValue){
		String methodName = "job:InventoryJob->importProductLocation";
		try{
			logger.info(methodName+ "_start paramNameValue=" + paramNameValue);
			Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Response resp =  inventoryService.importProductLocation(
							(Integer)serviceParamsMap.get("customerId"), (Integer)serviceParamsMap.get("physicalWarehouseId"));
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
		
	}
	
	public void autoPurchaseAccept(String paramNameValue){
		String methodName = "job:InventoryJob->autoPurchaseAccept";
		try{
			logger.info(methodName+ "_start paramNameValue=" + paramNameValue);
			Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Response resp =  inventoryService.autoPurchaseAccept(
							(Integer)serviceParamsMap.get("customerId"), (Integer)serviceParamsMap.get("physicalWarehouseId"),
							(Double)serviceParamsMap.get("hours") , (String)serviceParamsMap.get("endDate"));
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
	}
	
	
	// 测试采购单自动收货上架
	public void testAutoPurchaseAcceptAndGrouding(String paramNameValue){
		String methodName = "job:InventoryJob->testAutoPurchaseAcceptAndGrouding";
		try{
			logger.info(methodName+ "_start paramNameValue=" + paramNameValue);
			Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Response resp =  inventoryService.testAutoPurchaseAcceptAndGrouding((Integer)serviceParamsMap.get("groupId"),
							(Integer)serviceParamsMap.get("customerId"), (Integer)serviceParamsMap.get("physicalWarehouseId"),
							(Double)serviceParamsMap.get("hours") , (String)serviceParamsMap.get("endDate"));
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
	}
}
