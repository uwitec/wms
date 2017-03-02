package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.InventoryItemVariance;

public interface InventoryItemVarianceDao {
	
	int insert(InventoryItemVariance inventoryItemVariance);

	void batchInsert(@Param("list")List<InventoryItemVariance> inventoryItemVarianceList);
}
