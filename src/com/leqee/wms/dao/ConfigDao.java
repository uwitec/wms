package com.leqee.wms.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.Config;


public interface ConfigDao {

	String getConfigValueByFrezen(@Param("physical_warehouse_id")int physical_warehouse_id, @Param("customer_id")int customer_id,
			@Param("config_name")String config_name);

	//查询订单是否可以使用 超级复核
	Map<String,Object> checkSuperRecheckConfigForOrder(Integer orderId);
	
	String getConfigValueByConfigName(String ConfigName);
}
