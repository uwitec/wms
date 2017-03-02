package com.leqee.wms.service;

import java.util.Map;

import com.leqee.wms.response.Response;

public interface ReportService {

	/**
	 * 销售订单耗材不足报警
	 * @param groupId
	 * @param customerId
	 * @param hours
	 * @param endDate
	 * 
	 * @return response
	 */
	Response packboxNotEnough();

	Response productSpecNull();

	Response productExceptionLocation();
	
	Response maintainInventoryItemAndProductLocation(Integer groupId,Integer physical_warehouse_id,Integer customerId );

	Response thermalMailnosNotEnough();
	
	Response productSumNotEnough(Integer groupId,Integer physical_warehouse_id,Integer customerId);

	Response productExceptionLocation(Integer physcial_warehouse_id);

	Response getSaleNums(Map<String, Object> serviceParamsMap);
	
	Response productValidityExpiredReport(Integer groupId,Integer physical_warehouse_id,Integer customerId);

	
	

}
