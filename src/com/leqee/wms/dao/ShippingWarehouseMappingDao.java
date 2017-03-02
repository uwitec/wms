package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.ShippingWarehouseMapping;

public interface ShippingWarehouseMappingDao {

	ShippingWarehouseMapping selectByShippingWarehouseId(@Param("shipping_id") Integer shipping_id, @Param("warehouse_id")  Integer warehouse_id);

	List<Map> getThermalShippingAppInfo();

	List<Map> getThermalCountByAppId(@Param("shipping_app_id") Integer shippingAppId);

	List<Map> getWarehouseShippingThermal();

	List<Integer> selectNeedApplyShippingAppId();
	
	Map selectByAppId(@Param("app_id") Integer shipping_app_id);

	Map selectSingleByAppId(@Param("app_id") Integer shipping_app_id);

	Integer selectShippingAppIdOne(@Param("shippingId")Integer shippingId,
			@Param("physicalWarehouseId")Integer physicalWarehouseId);

	List<String> searchShippingCodeBatch();
	
}