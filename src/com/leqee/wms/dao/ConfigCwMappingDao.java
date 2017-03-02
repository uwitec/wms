package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.ConfigCwMapping;

public interface ConfigCwMappingDao {

	public List<ConfigCwMapping> getConfigCwMappingList(
			@Param("physical_warehouse_id")int physical_warehouse_id, @Param("customer_id")int customer_id);

	public List<ConfigCwMapping> getConfigCwMappingListByPhysicalWarehouseId(
			@Param("physical_warehouse_id")String physical_warehouse_id);

}
