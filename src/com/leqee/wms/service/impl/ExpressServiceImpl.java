package com.leqee.wms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.api.util.Constants;
import com.leqee.wms.api.util.MailUtil;
import com.leqee.wms.biz.BatchPickBiz;
import com.leqee.wms.biz.DepponBiz;
import com.leqee.wms.biz.EMSBiz;
import com.leqee.wms.biz.HTBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.STOBiz;
import com.leqee.wms.biz.ShipmentBiz;
import com.leqee.wms.biz.ZTOBiz;
import com.leqee.wms.dao.ConfigMailDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ShippingTrackingNumberRepositoryDao;
import com.leqee.wms.dao.ShippingWarehouseMappingDao;
import com.leqee.wms.dao.ShippingZtoMarkDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.service.CommonScheduleService;
import com.leqee.wms.service.ExpressService;
import com.leqee.wms.util.LockUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ExpressServiceImpl extends CommonScheduleService implements ExpressService {
	@Autowired
	ShippingWarehouseMappingDao shippingWarehouseMappingDao;
	@Autowired
	ShippingZtoMarkDao shippingZtoMarkDao;
	@Autowired
	OrderProcessDao orderProcessDao;
	@Autowired
	ZTOBiz ztoBiz;
	@Autowired
	HTBiz htBiz;
	@Autowired
	STOBiz stoBiz;
	@Autowired
	EMSBiz emsBiz;
	@Autowired
	DepponBiz depponBiz;
	@Autowired
	ShippingTrackingNumberRepositoryDao shippingTrackingNumberRepositoryDao;
	@Autowired
	BatchPickBiz batchPickBiz;
	@Autowired
	OrderBiz orderBiz;
	@Autowired
	ShipmentBiz shipmentBiz;
	@Autowired
	WarehouseDao warehouseDao;
	
	private ConfigMailDao configMailDaoSlave;
    @Resource(name = "sqlSessionSlave")
	public void setConfigMailDaoSlave(SqlSession sqlSession) {
	  this.configMailDaoSlave = sqlSession.getMapper(ConfigMailDao.class);
	}
	
	private ShippingTrackingNumberRepositoryDao shippingTrackingNumberRepositorySlaveDao;
	@Resource(name = "sqlSessionSlave")
	public void setShippingTrackingNumberRepositorySlaveDao(SqlSession sqlSession) {
	  this.shippingTrackingNumberRepositorySlaveDao = sqlSession.getMapper(ShippingTrackingNumberRepositoryDao.class);
	}
	
	private Logger logger = Logger.getLogger(ExpressServiceImpl.class);
	
	public void batchApplyTrackingNumber(Integer shipping_app_id) throws Exception {
		Map shippingApp = shippingWarehouseMappingDao.selectByAppId(shipping_app_id);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(shippingApp),"没有搜到（批量+热敏）方式获取单号的快递账号信息");
		String methodName = "batchApplyTrackingNumber";
		String log_prefix = methodName + "_shippingAppId_" + shipping_app_id;
		LockUtil.lock(shipping_app_id,methodName);
		logger.info(log_prefix + " start, lock the lock");
		try{
			if("ZTO".equals(shippingApp.get("shipping_code"))) {//中通
				logger.info("begin to apply batch zto mailnos");
				ztoBiz.batchApplyZTOTrackingNumber(shippingApp);
				logger.info("apply batch zto mailnos end");
			}else if("HT".equals(shippingApp.get("shipping_code"))) {// 汇通
				logger.info("begin to apply batch ht mailnos");
				htBiz.batchApplyHTTrackingNumber(shippingApp);
				logger.info("apply batch ht mailnos end");
			}else if("STO".equals(shippingApp.get("shipping_code"))) {// 申通
				logger.info("begin to apply batch STO mailnos");
				stoBiz.batchApplySTOTrackingNumber(shippingApp);
				logger.info("apply batch STO mailnos end");
			}else if("EMS".equals(shippingApp.get("shipping_code"))){
				logger.info("begin to apply batch EMS mailnos");
				emsBiz.batchApplyEMSTrackingNumber(shippingApp);
				logger.info("apply batch EMS mailnos end");
			}else{
				logger.info("other SHIPPING_CODE :"+shippingApp.get("shipping_code"));
			}
		}catch(Exception e){
			logger.error(log_prefix + "_exception: " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}finally{
			// 释放锁
			LockUtil.unlock(shipping_app_id,methodName);
			logger.info(log_prefix + " end, unlock the lock");
		}
	}
	
	public void batchUpdateMark(String shippingCode) throws Exception {
		List<Map<String,Object>> singleOrders = orderProcessDao.selectSingleOrderForMark(shippingCode);
		if(!WorkerUtil.isNullOrEmpty(singleOrders)){
			for (Map<String, Object> singleOrder : singleOrders) {
				if("ZTO".equalsIgnoreCase(shippingCode)){
					ztoBiz.getZTOMarkForSingleOrder(singleOrder);
				}else if("HT".equalsIgnoreCase(shippingCode)){
					htBiz.getHTMarkForSingleOrder(singleOrder);
				}
			}
		}
	}

	@Override
	public List<Map> getAllThermalInfo() {
		List<Map> thermalShippingAppInfo = shippingWarehouseMappingDao.getThermalShippingAppInfo();
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(thermalShippingAppInfo), "没有批量获取热敏快递单号的组合");
		for (int i = 0; i < thermalShippingAppInfo.size(); i++) {
			Integer shippingAppId = Integer.parseInt(String.valueOf(thermalShippingAppInfo.get(i).get("shipping_app_id")));
			List<Map> thermalCount = shippingWarehouseMappingDao.getThermalCountByAppId(shippingAppId);
			if(!WorkerUtil.isNullOrEmpty(thermalCount)){
				for (int j = 0; j < thermalCount.size(); j++) {
					thermalShippingAppInfo.get(i).put(thermalCount.get(j).get("status"), thermalCount.get(j).get("count"));
				}
			}
		}
		return thermalShippingAppInfo;
	}

	@Override
	public List<Map> getWarehouseShippingThermal() {
		List<Map> warehouseShippingThermal = shippingWarehouseMappingDao.getWarehouseShippingThermal();
		return warehouseShippingThermal;
	}

	@Override
	public List<Integer> selectNeedApplyShippingAppId() {
		List<Integer> shippingAppIdList = shippingWarehouseMappingDao.selectNeedApplyShippingAppId();
		return shippingAppIdList;
	}

	@Override
	public List<Map> selectShippingInfoList(String shippingCode,Double hours) {
		List<Map> shippingInfoList = shippingTrackingNumberRepositorySlaveDao.selectShippingInfoList(shippingCode,hours);
		return shippingInfoList;
	}

	@Override
	public void reportTrackingNumber(Map shippingInfo,String shippingCode) throws Exception{
		if(shippingCode.equalsIgnoreCase("HT")){
			htBiz.reportHTTrackingNumber(shippingInfo);
		}else if(shippingCode.equalsIgnoreCase("ZTO")){
			ztoBiz.reportZTOTrackingNumber(shippingInfo);
		}else if(shippingCode.equalsIgnoreCase("STO")){
			stoBiz.reportSTOTrackingNumber(shippingInfo);
		}else if(shippingCode.equalsIgnoreCase("DEPPON")){
			depponBiz.reportDepponTrackingNumber(shippingInfo);
		}else if(shippingCode.equalsIgnoreCase("EMS")){
			emsBiz.reportEMSTrackingNumber(shippingInfo);
		}
	}

	@Override
	public Integer releaseTrackingNumber() {
		List<String> trackingNumbers = shippingTrackingNumberRepositorySlaveDao.selectReUseTns();
		if(WorkerUtil.isNullOrEmpty(trackingNumbers)){
			return 0;
		}else{
			Integer effectRows = shippingTrackingNumberRepositoryDao
					.updateRepositoryTrackingNumbers(trackingNumbers, "N");
			return effectRows;
		}
	}

	@Override
	public Response batchAddFirstShipmentAndMark(Integer physicalWarehouseId,
			String batchPickGroupIds,Double hours, String endDate) {
		// 初始化数据
		Response response = new Response();
		List<Integer> batchPickGroupIdList = new ArrayList<Integer>();
		Date startDateTime = new Date();
		Date endDateTime = new Date();
		String methodName = "batchAddFirstShipmentAndMark";

		// 验证输入参数
		response = validAndInitParamsV2(physicalWarehouseId,batchPickGroupIds, hours,
				endDate, batchPickGroupIdList, startDateTime, endDateTime, methodName,
				logger);
		Assert.notNull(response, "[验证输入参数出错]response不能为空");
		if (!Response.OK.equals(response.getCode())) {
			return response;
		}
		
		String log_prefix = methodName +"_physicalWarehouseId_" + physicalWarehouseId.toString() 
				+ "_batchPickGroupIds_" + batchPickGroupIds ;
		
		// 加锁
		LockUtil.lock(physicalWarehouseId,batchPickGroupIds, methodName);
		logger.info(log_prefix + " start, lock the lock");
		
		try{
			// 分业务组处理销售订单出库
			if(!WorkerUtil.isNullOrEmpty(batchPickGroupIdList)){
		    	List<String> errorOrderInfo = new ArrayList<String>();
				for (Integer cId : batchPickGroupIdList) {
					logger.info(log_prefix + " batch_pick_group_id:"+cId+" begin apply first_shipment("+physicalWarehouseId +")");
				    List<Integer> BatchPickIdList = batchPickBiz.getToApplyFirstShipmentBatchPickIdList(physicalWarehouseId, cId, startDateTime, endDateTime);
				    logger.info(log_prefix + " batch_pick_group_id:"+cId+" batchPickSize:" + BatchPickIdList.size());
				    
				    if(!WorkerUtil.isNullOrEmpty(BatchPickIdList)) {
				    	// 批次分开处理，即使出现错误，也不影响后面的波次~
				    	for (Integer batchPickId : BatchPickIdList) {
				    		boolean flag = true;
				    		logger.info(log_prefix + " start_batchPickId:" + batchPickId);
				    		//add cancel order apply
				    		shipmentBiz.applyFirstShipmentForCancelOrder(batchPickId);
				    		
				    		List<Integer> orderIdList = orderProcessDao.getToApplyFirstShipmentOrderIdListByBatchPickId(batchPickId);
				    		if(!WorkerUtil.isNullOrEmpty(orderIdList)){
				    			List<String> shippingCodeBatch = shippingWarehouseMappingDao.searchShippingCodeBatch();
				    			if(!WorkerUtil.isNullOrEmpty(shippingCodeBatch)){
					    			for (String shippingCode : shippingCodeBatch) {
					    				List<Map<String,Object>> ztoShippingAppOrders = orderBiz.getShippingAppOrders(orderIdList,shippingCode);
										if(!WorkerUtil.isNullOrEmpty(ztoShippingAppOrders)){
											try{
												shipmentBiz.batchApplyShipmentTnForFirstShipment(ztoShippingAppOrders,shippingCode);
											}catch (Exception e) {
												errorOrderInfo.add(e.getMessage());
												flag = false;
											}
										}
									}
				    			}
								List<Integer> otherOrderIds = orderBiz.getSingleOrderIdByOrderIds(orderIdList); // 其他快递
								if(!WorkerUtil.isNullOrEmpty(otherOrderIds)){
									// 每个订单分别createShipment + addTrackingNumber
									for (Integer otherOrderId : otherOrderIds) {
										try{
											Map<String,Object> result = shipmentBiz.applyShipmentTnForFirstShipment(otherOrderId);
											if(Response.FAILURE.equals(String.valueOf(result.get("result")))){
												errorOrderInfo.add(String.valueOf(result.get("note")));
												flag = false;
											}
										}catch (RuntimeException e) {
											errorOrderInfo.add(e.getMessage());
											flag = false;
										}
									}
								}
							}
				    		try{
					    		if(flag){
					    			//update batchPick : flow_status = SHIPMENT_Y
					    			batchPickBiz.updateFlowStatus(batchPickId,"SHIPMENT_Y"); 
					    		}else{
					    			//update batchPick : flow_status = SHIPMENT_E
					    			batchPickBiz.updateFlowStatus(batchPickId,"SHIPMENT_E");
					    		}
				    		}catch (RuntimeException e) {
								errorOrderInfo.add("波次单号"+batchPickId+"数据锁，稍后执行");
							}
				    		logger.info(log_prefix + " end_batchPickId:" + batchPickId);
						}
					}
				    logger.info(log_prefix + " batch_pick_group_id:"+cId+" end apply first_shipment("+physicalWarehouseId +")");
				}
				if (!WorkerUtil.isNullOrEmpty(errorOrderInfo)) {
//					logger.info("错误信息："+errorOrderInfo);
					boolean flag_send = false;
					StringBuilder sb = new StringBuilder();
					for (String errorInfo : errorOrderInfo) {
						if(!errorInfo.contains("更新快递面单失败") && !errorInfo.contains("创建发货单失败")){
							sb.append("<span>"+errorInfo+"</span><br/>");
							flag_send=true;
						}
					}
					if(flag_send){
			    		String warehouseName = warehouseDao.selectByWarehouseId(physicalWarehouseId).getWarehouse_name();
						MailUtil mu = new MailUtil();
						mu.setSubject("【Leqee WMS 报警】【"+warehouseName+"】订单获取首个面单失败");
						mu.setContent(sb.toString());
						String toEmails = configMailDaoSlave.getToMailsByType(
								Constants.errorApplyFirstShipment, physicalWarehouseId, 0);
						if(WorkerUtil.isNullOrEmpty(toEmails) || "".equalsIgnoreCase(toEmails)){
							toEmails = "wms@leqee.com";
						}
						mu.setToEmails(toEmails);
						mu.sendEmail();
					}
				}
			}
		}catch(Exception e){
			response = ResponseFactory.createExceptionResponse(log_prefix + "_exception: " + e.getMessage());
			logger.error(log_prefix + "_exception: " + e.getMessage(), e);
		}finally{
			// 释放锁
			LockUtil.unlock(physicalWarehouseId,batchPickGroupIds, methodName);
			logger.info(log_prefix + " end, unlock the lock");
		}
		
		return response;
	}

	@Override
	public List<Map<String, Object>> getThermalRepositoryList() {
		List<Map> shippingAppIdList = shippingTrackingNumberRepositorySlaveDao.selectBatchAppId();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(!WorkerUtil.isNullOrEmpty(shippingAppIdList)){
			for (Map shippingAppIdMap : shippingAppIdList) {
				Map<String,Object> map = new HashMap<String,Object>();
				Integer appId = Integer.parseInt(String.valueOf(shippingAppIdMap.get("app_id")));
				List<Map> appAmountList = shippingWarehouseMappingDao.getThermalCountByAppId(appId);
				if(appAmountList.size()==0){
					continue;
				}else{
					for (int i = 0; i < appAmountList.size(); i++) {
						String status = appAmountList.get(i).get("status").toString();
						if("N".equalsIgnoreCase(status)) map.put("unused", appAmountList.get(i).get("count").toString());
						if("Y".equalsIgnoreCase(status)) map.put("waitBack", appAmountList.get(i).get("count").toString());
						if("E".equalsIgnoreCase(status)) map.put("error_done", appAmountList.get(i).get("count").toString());
					}
					map.put("app_id", appId);
					map.put("app_key", String.valueOf(shippingAppIdMap.get("app_key")));
					Map map1 = shippingTrackingNumberRepositorySlaveDao.selectShippingNameByAppId(appId);
					if(WorkerUtil.isNullOrEmpty(map1)) continue;
					map.put("shipping_name", String.valueOf(map1.get("name")));
					String[] group_wid = String.valueOf(map1.get("group_wid")).split(",");
					List<Integer> wid = new ArrayList<Integer>();
					for (String i : group_wid) {
						wid.add(Integer.parseInt(i));
					}
					Map map2 = shippingTrackingNumberRepositorySlaveDao.selectWarehouseNameByIds(wid);
					if(WorkerUtil.isNullOrEmpty(map2)) continue;
					map.put("physical_warehouse_name", String.valueOf(map2.get("group_physical")));
					map.put("group_warehouse_name", String.valueOf(map2.get("group_warehouse_name")));
					list.add(map);
				}
			}
		}
		return list;
	}

}