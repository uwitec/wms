package com.leqee.wms.biz;

import java.util.List;
import java.util.Map;

public interface SupplierReturnBiz {

	List<Map<String, Object>> supplierReturnSearch(Map<String, Object> searchMap);

	List<Map<String, Object>> getGoodsReturnPrintList(String orderIds);

	Map<String, Object> outGoods(String orderId,String taskQty);
	
}
