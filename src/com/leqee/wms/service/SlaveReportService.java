package com.leqee.wms.service;

import com.leqee.wms.response.Response;

public interface SlaveReportService {

	Response performanceReport(Integer physicalWarehouseId, String type);

	Response fulfilledReplenishmentReport();

	Response selectStockGoodsDeleted();
	
	


}
