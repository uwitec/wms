package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

public interface ExpressDao {
	List<Map> selectSaleOrderWarehouseShippingInfo(Integer orderId);

	List<Map> selectAllTrackingNumberByOrderId(int parseInt);

	String getTrackingNumber(Integer appId);

	void updateRepositoryTrackingNumber(String trackingNumber, String status);

	List<Map<String, Object>> getNotEnoughThermalMailnosList();

	List<Map<String, Object>> getThermalWarehouseShippingList();
	
}