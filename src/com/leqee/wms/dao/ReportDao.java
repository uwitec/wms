package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

public interface ReportDao {

	List<Map<String, Object>> getPickList(String startTime, String endTime,
			Integer physicalWarehouseId);

	List<Map<String, Object>> getRecheckList(String startTime, String endTime,
			Integer physicalWarehouseId);

	List<Map<String, Object>> getReplenishmentList(String startTime,
			String endTime, Integer physicalWarehouseId);

	List<Map<String, Object>> getCountTaskList(String startTime,
			String endTime, Integer physicalWarehouseId);

	List<Map<String, Object>> getMovekList(String startTime, String endTime,
			Integer physicalWarehouseId);

	List<Map<String, Object>> getFulfilledReplenishmentList(String startTime,
			String endTime);

	List<Map> getOrderToDoList(Map<String, Object> searchMap);

	List<Map> getOrderListByPayTime(Map<String, Object> searchMap);

	List<Map> selectTnsForFinishList(Integer shippingAppId);

	List<Map<String, Object>> selectStockGoodsDeletedList();

	List<Map<String, Object>> getReplenishmentToDoList(Map<String, Object> searchMap);

}
