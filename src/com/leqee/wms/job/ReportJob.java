package com.leqee.wms.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.response.Response;
import com.leqee.wms.schedule.job.TaskUtils;
import com.leqee.wms.service.ReportService;
import com.leqee.wms.service.SlaveReportService;

@Service
public class ReportJob extends CommonJob {
	private Logger logger = Logger.getLogger(ReportJob.class);
	
	@Autowired
	ReportService reportService;
	@Autowired   
	SlaveReportService slaveReportService;
	@Autowired
	WarehouseDao warehouseDao;
	/**
	 * @author dlyao
	 * 
	 * 耗材不足  ————无法出库
	 */
	public void packboxNotEnough(){
		String methodName = "job:ReportJob->packboxNotEnough";
		try{
			logger.info(methodName+ "_start ");
			Response resp =  reportService.packboxNotEnough();
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
		
	}
	
	/**
	 * @author dlyao
	 * 
	 * 未维护箱规---->会导致无法预订库存  无法生成补货任务
	 */
	public void productSpecNull(){
		String methodName = "job:ReportJob->productSpecNull";
		try{
			logger.info(methodName+ "_start ");
			Response resp =  reportService.productSpecNull();
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
		
	}
	
	/**
	 * @author dlyao
	 * 被冻结和被标记异常的库位----->需要立刻执行
	 */
	public void productExceptionLocation(){
		String methodName = "job:ReportJob->productExceptionLocation";
		
		List<Integer> list=new ArrayList<Integer>();

		list=warehouseDao.selectPhysicalWarehouseIdList();
		for(Integer physical_warehouse_id:list){
			try{
				logger.info(methodName+ "_start ");
	
				Response resp =  reportService.productExceptionLocation(physical_warehouse_id);
				logger.info(methodName+ "_end resp=" + resp.toString());
							
			}catch(Exception e){
				logger.error(methodName +"_error exception=" + e.getMessage() , e);
			}
		}
	}
	
	
	/**
	 * @author dlyao
	 * 
	 * physical_warehouse_id
	 * 被冻结和被标记异常的库位----->需要立刻执行
	 */
	public void productExceptionLocationByPhysical(String paramNameValue){
		String methodName = "job:ReportJob->productExceptionLocation";
		Date begin = new Date();
		logger.info("reserveOrders-run start paramNameValue:"+paramNameValue);
		Map<String, Object> serviceParamsMap = getServiceParamsMapByDlYao(paramNameValue);
		try{
			logger.info(methodName+ "_start ");
			Response resp =  reportService.productExceptionLocation(
					(Integer)serviceParamsMap.get("physical_warehouse_id"));
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
		
	}
	
	/**
	 * @author dlyao
	 * 
	 * physical_warehouse_id
	 * 被冻结和被标记异常的库位----->需要立刻执行
	 */
	public void getSaleNums(String paramNameValue){
		String methodName = "job:ReportJob->productExceptionLocation";
		Date begin = new Date();
		logger.info("reserveOrders-run start paramNameValue:"+paramNameValue);
		Map<String, Object> serviceParamsMap = getServiceParamsMapByDlYao(paramNameValue);
		try{
			logger.info(methodName+ "_start ");
			Response resp =  reportService.getSaleNums(serviceParamsMap);
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
		
	}
	
	
	/**
	 * hchen1
	 * 库存比较报警
	 * @param paramNameValue
	 */
	public void maintainItem(String paramNameValue) {
		Date begin = new Date();
		logger.info("maintainInventoryItemAndProductLocation-run start paramNameValue:"+paramNameValue);
		Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
		try {
			Response resp =reportService.maintainInventoryItemAndProductLocation((Integer)serviceParamsMap.get("groupId"),
					(Integer)serviceParamsMap.get("physical_warehouse_id"),
					(Integer)serviceParamsMap.get("customerId")
					);
			logger.info(resp);
		} catch (Exception e) {
			logger.error("maintainInventoryItemAndProductLocation paramNameValue:"+paramNameValue,e);
		}
    	
    	
    	Date end = new Date();
    	logger.info("maintainInventoryItemAndProductLocation-run end period:"+(end.getTime()-begin.getTime())/1000+"/s");
    }
	
	public void productSumNotEnough(String paramNameValue) {
		Date begin = new Date();
		logger.info("productSumNotEnough-run start paramNameValue:"+paramNameValue);
		Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
		try {
			Response resp =reportService.productSumNotEnough((Integer)serviceParamsMap.get("groupId"),
					(Integer)serviceParamsMap.get("physical_warehouse_id"),
					(Integer)serviceParamsMap.get("customerId")
					);
			logger.info(resp);
		} catch (Exception e) {
			logger.error("productSumNotEnough paramNameValue:"+paramNameValue,e);
		}
    	
    	
    	Date end = new Date();
    	logger.info("productSumNotEnough-run end period:"+(end.getTime()-begin.getTime())/1000+"/s");
    }
	
	
	protected Map<String, Object> getServiceParamsMap( String paramNameValue ){
		
		Map<String, String> paramNameValueMap =   TaskUtils.getParamNameValueMap(paramNameValue);
		Map<String, Object> serviceParamsMap =   new HashMap<String, Object>();
		
		Integer physical_warehouse_id = paramNameValueMap.get("physical_warehouse_id") == null ? 1 : Integer.parseInt(paramNameValueMap.get("physical_warehouse_id")) ; //默认为0
		Integer groupId = paramNameValueMap.get("groupId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("groupId")) ; //默认为0
		Integer customerId = paramNameValueMap.get("customerId") == null ? 0 : Integer.parseInt(paramNameValueMap.get("customerId")); //默认为0
		
		
		serviceParamsMap.put("physical_warehouse_id", physical_warehouse_id);
		serviceParamsMap.put("groupId", groupId);
		serviceParamsMap.put("customerId", customerId);
		
		return serviceParamsMap;		
	}
	
	/**
	 * @author ytchen
	 * 
	 * 热敏资源不足2000
	 */
	public void thermalMailnosNotEnough(){
		String methodName = "job:ReportJob->thermalMailnosNotEnough";
		try{
			logger.info(methodName+ "_start ");
			Response resp =  reportService.thermalMailnosNotEnough();
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
		
	}
	
	/**
	 * 仓库员工绩效报表 by ytchen
	 * @param paramNameValue physical_warehosue_id
	 * @return
	 */
	public void performanceReport(String paramNameValue){
		
		Map<String, String> paramNameValueMap =   TaskUtils.getParamNameValueMap(paramNameValue);
		Integer physicalWarehouseId = paramNameValueMap.get("physical_warehouse_id") == null ? 0 : Integer.parseInt(paramNameValueMap.get("physical_warehouse_id")) ; //默认为0
		String type = paramNameValueMap.get("type") == null ? "day": paramNameValueMap.get("type") ; //默认为 day
		String methodName = "job:ReportJob->performanceReport("+physicalWarehouseId+")";
		try{
			logger.info(methodName+ "_start ");
			Response resp =  slaveReportService.performanceReport(physicalWarehouseId,type);
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
	}
	
	/**
	 * 补货任务完成情况报表 by ytchen
	 * @param paramNameValue physical_warehosue_id
	 * @return
	 */
	public void fulfilledReplenishmentReport(){
		
		String methodName = "job:ReportJob->fulfilledReplenishmentReport";
		try{
			logger.info(methodName+ "_start ");
			Response resp =  slaveReportService.fulfilledReplenishmentReport();
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
	}
	
	
	/**
	 * 有库存商品被删除报警 by ytchen
	 */
	public void stockGoodsDeletedReport(){
		String methodName = "job:ReportJob->stockGoodsDeletedReport";
		try{
			logger.info(methodName + "_start ");
			Response res = slaveReportService.selectStockGoodsDeleted();
			logger.info(methodName + "_end res="+res.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
	}
	

	/**
	 * 商品生产日期预警 by hchen
	 */
	
	public void productValidityExpiredReport(String paramNameValue) {
		Date begin = new Date();
		logger.info("productValidityExpiredReport-run start paramNameValue:"+paramNameValue);
		Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
		try {
			Response resp =reportService.productValidityExpiredReport((Integer)serviceParamsMap.get("groupId"),
					(Integer)serviceParamsMap.get("physical_warehouse_id"),
					(Integer)serviceParamsMap.get("customerId")
					);
			logger.info(resp);
		} catch (Exception e) {
			logger.error("productValidityExpiredReport paramNameValue:"+paramNameValue,e);
		}
    	
    	
    	Date end = new Date();
    	logger.info("productValidityExpiredReport-run end period:"+(end.getTime()-begin.getTime())/1000+"/s");
    }
	
}
