package com.leqee.wms.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.schedule.job.TaskUtils;
import com.leqee.wms.service.impl.ReserveOrdersServiceImpl;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ReserveJob {
	
	private Logger logger = Logger.getLogger(ReserveJob.class);
  
	@Autowired
    ReserveOrdersServiceImpl reserveOrdersServiceImpl;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	
	@Autowired
	ConfigDao configDao;
	 
	public void reserveOrders() {
		Date begin = new Date();
		logger.info("reserveOrders-run start");	
		String paramNameValue = "groupId=0";
		Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
		//System.out.println(serviceParamsMap);
		try {
					
			reserveOrdersServiceImpl.reservceOrders((Integer)serviceParamsMap.get("groupId"), 
					(Integer)serviceParamsMap.get("customerId") ,
					(Integer)serviceParamsMap.get("days") , 
					(String)serviceParamsMap.get("endDate"));  
		} catch (Exception e) {
			logger.error("reserveOrders paramNameValue:"+paramNameValue,e);
		}
		
    	Date end = new Date();
    	logger.info("reserveOrders-run period:"+(end.getTime()-begin.getTime())/1000+"/s");
    }
	
	public void reserveOrders(String paramNameValue) {
		Date begin = new Date();
		logger.info("reserveOrders-run start paramNameValue:"+paramNameValue);
		Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
		try {
			reserveOrdersServiceImpl.reservceOrders((Integer)serviceParamsMap.get("groupId"), 
					(Integer)serviceParamsMap.get("customerId") ,
					(Integer)serviceParamsMap.get("days") , 
					(String)serviceParamsMap.get("endDate"));
		} catch (Exception e) {
			logger.error("reserveOrders paramNameValue:"+paramNameValue,e);
		}
    	
    	
    	Date end = new Date();
    	logger.info("reserveOrders-run end period:"+(end.getTime()-begin.getTime())/1000+"/s");
    }
	
	public void FinishedOrderInventoryReservation(){
		Date begin = new Date();
		logger.info("FinishedOrderInventoryReservation-run start");	 
		
		try {
			reserveOrdersServiceImpl.FinishedOrderInventoryReservation();
		} catch (Exception e) {
			logger.error("FinishedOrderInventoryReservation:",e);
		}
    	 	
    	Date end = new Date();
    	logger.info("FinishedOrderInventoryReservation-run period:"+(end.getTime()-begin.getTime())/1000+"/s");
		
	}
	
    public void modifyErrorStockAndAvailityQuantiry(){
    	Date begin = new Date();
		logger.info("modifyErrorStockAndAvailityQuantiry-run start");	
		try {
			reserveOrdersServiceImpl.modifyErrorStockAndAvailityQuantiry();
		} catch (Exception e) {
			logger.error("modifyErrorStockAndAvailityQuantiry:", e);
		}
    	
    	
    	Date end = new Date();
    	logger.info("modifyErrorStockAndAvailityQuantiry-run period:"+(end.getTime()-begin.getTime())/1000+"/s");
		
	}
    
	protected Map<String, Object> getServiceParamsMap( String paramNameValue ){
		
		Map<String, String> paramNameValueMap =   TaskUtils.getParamNameValueMap(paramNameValue);
		Map<String, Object> serviceParamsMap =   new HashMap<String, Object>();
		
		Integer groupId = paramNameValueMap.get("groupId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("groupId")) ; //默认为0
		Integer customerId = paramNameValueMap.get("customerId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("customerId")); //默认为0
		String endDate = paramNameValueMap.get("endDate") == null ? WorkerUtil.formatDatetime(new Date()) : paramNameValueMap.get("endDate");  //默认当前时间
		Integer days = paramNameValueMap.get("days") == null ? 20: Integer.parseInt(paramNameValueMap.get("days"));  //默认20天
		
		serviceParamsMap.put("groupId", groupId);
		serviceParamsMap.put("customerId", customerId);
		serviceParamsMap.put("endDate", endDate);
		serviceParamsMap.put("days", days);
		
		return serviceParamsMap;		
	}
	
	public void reserveOrdersByDlYao(String paramNameValue ) {
		Date begin = new Date();
		logger.info("reserveOrdersByDlYao-run start");	
		Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
		//System.out.println(serviceParamsMap);
		
		Integer groupId=(Integer)serviceParamsMap.get("groupId");
		Integer customerId=(Integer)serviceParamsMap.get("customerId");
		Integer days=(Integer)serviceParamsMap.get("days");
		String endDate=(String)serviceParamsMap.get("endDate");
		
		List<Integer> list=new ArrayList<Integer>();
		
		
				
		if(groupId-0!=0){
			list=warehouseCustomerDao.selectCustomerIdListByGroupId(groupId);
		}
		else if(customerId-0==0){
			list=warehouseCustomerDao.selectAllId();
		}
		else{
			list.add(customerId);
		}
		
		for(Integer customer_id:list){
			try {
				String reserveTimesString=configDao.getConfigValueByFrezen(0, customer_id, "reserveTimes");
				int reserveTimes=null==reserveTimesString?1:Integer.parseInt(reserveTimesString);
				
				for(int i=0;i<reserveTimes;i++){
					logger.info("reserveOrdersByDlYao-run customer_id"+customer_id+ "start,reserveTimes="+reserveTimes);	
					reserveOrdersServiceImpl.reservceOrdersByDlYao(customer_id,days,endDate); 
				}
			 
			} catch (Exception e) {
				logger.error("reserveOrders paramNameValue:"+paramNameValue,e);
			}
			
		}
		
		

		
    	Date end = new Date();
    	logger.info("reserveOrders-run period:"+(end.getTime()-begin.getTime())/1000+"/s");
    }
	
}
