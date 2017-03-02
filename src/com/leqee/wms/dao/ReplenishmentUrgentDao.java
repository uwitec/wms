package com.leqee.wms.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import com.leqee.wms.entity.ConfigReplenishmentUrgent;

public interface ReplenishmentUrgentDao {

	ConfigReplenishmentUrgent selectConfigReplenishmentUrgentByPhysicalCustomer(
			@Param("customer_id")Integer customer_id, @Param("physical_warehouse_id")Integer warehouse_id);
	
	public  List<Map<String, Object>> selectByMapByPage(Map<String, Object>map);
	
	int deleteReplenishmentUrgent(Integer customer_id,Integer physical_warehouse_id);
	
	List<ConfigReplenishmentUrgent> selectConfigReplenishmentUrgentByCustomerId(Map<String, Object>map);
	
	int updateReplenishmentUrgentByUpdateReplenishmentMap(Map<String, Object> updateReplenishmentMap);
	
	int insertConfigReplenishmentUrgentByMap(Map<String, Object> updateReplenishmentMap);
	
	
	ConfigReplenishmentUrgent selectReplenishmentUrgentIsExist(Integer customerId,Integer physical_warehouse_id);
	
}
