package com.leqee.wms.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.response.Response;
import com.leqee.wms.service.ExpressService;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ApplyTrackingNumbersJob extends CommonJob {

	@Autowired
	ExpressService expressService;

	/** 日志对象 */
	private Logger logger = Logger.getLogger(ApplyTrackingNumbersJob.class);
	//申请面单
	public void applyTrackingNumbers() {
		logger.info("applyTrackingNumbers-run start");
		
		List<Integer> shippingAppIdList = expressService.selectNeedApplyShippingAppId();
		if(!WorkerUtil.isNullOrEmpty(shippingAppIdList)){
			for (Integer shippingAppId : shippingAppIdList) {
				try {
					expressService.batchApplyTrackingNumber(shippingAppId);
				} catch (Exception e) {
					logger.error("shippingAppId("+shippingAppId+")快递单号批量拉取失败！" + e.getMessage() , e);
				}
			}
		}
		logger.info("applyTrackingNumbers-run END");

	}
	
	//面单回传
	public void reportTrackingNumbers(String paramNameValue){
		String methodName = "job:ApplyTrackingNumbersJob->reportTrackingNumbers";
		logger.info(methodName+ "_start paramNameValue=" + paramNameValue);
		Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
		String shippingCode = (String)serviceParamsMap.get("shippingCode");
		Double hours = (Double)serviceParamsMap.get("hours");
		if("".equalsIgnoreCase(shippingCode)){
			logger.info(methodName+" error :参数shippingCode不能为空！");
		}else{
			List<Map> shippingInfoList = expressService.selectShippingInfoList(shippingCode,hours); 
			if(!WorkerUtil.isNullOrEmpty(shippingInfoList)){
				logger.info("reportTrackingNumber size ："+shippingInfoList.size());
				for (int i = 0; i < shippingInfoList.size(); i++) {
					Map shippingInfo = shippingInfoList.get(i);
					try{
						expressService.reportTrackingNumber(shippingInfo,shippingCode);
					}catch(Exception e){
						logger.info(shippingCode+"reportTrackingNumber error ："+e.getMessage()+" tracking_number : "+shippingInfo.get("tracking_number"));
					}
				}
			}
		}
		logger.info(methodName+ "_end paramNameValue=" + paramNameValue);
	}
	

	//大头笔 replace by batchAddFirstShipment
	public void batchUpdateMark(){
		logger.info("batchUpdateMark start");
		List<String> shippingCodeList = new ArrayList<String>();
		shippingCodeList.add("ZTO");
		shippingCodeList.add("HT");
		for (String shippingCode : shippingCodeList) {
			try{
				expressService.batchUpdateMark(shippingCode); 
			}catch(Exception e){
				logger.info("batchUpdate"+shippingCode+"Mark error ："+e.getMessage());
			}
		}
		logger.info("batchUpdateMark END");
	}
	
	//首单面单申请 + 大头笔获取
	public void batchAddFirstShipmentAndMark(String paramNameValue){
		String methodName = "job:ApplyTrackingNumbersJob->batchAddFirstShipmentAndMark";
		try{
			logger.info(methodName+ "_start paramNameValue=" + paramNameValue);
			Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
			Response resp =  expressService.batchAddFirstShipmentAndMark( (Integer)serviceParamsMap.get("physicalWarehouseId"),
					(String)serviceParamsMap.get("batchPickGroupIds"),(Double)serviceParamsMap.get("hours") , (String)serviceParamsMap.get("endDate"));
			logger.info(methodName+ "_end resp=" + resp.toString());
		}catch(Exception e){
			logger.error(methodName +"_error exception=" + e.getMessage() , e);
		}
	}
	
	//面单释放重用
	public void releaseTrackingNumbers(){
		logger.info("releaseTrackingNumbers start");
		Integer col = expressService.releaseTrackingNumber(); 
		logger.info("shippingCode release trackingNumber count:"+col);
		logger.info("releaseTrackingNumbers END");
	}
		
}