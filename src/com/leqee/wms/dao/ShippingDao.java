package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import com.leqee.wms.entity.Shipping;

public interface ShippingDao {
    
	public Shipping selectByPrimaryKey(int shippingId);

	public Shipping selectByShippingCode(String shippingCode);

	public List<Shipping> selectAllShipping();

	public List<Map<String,Object>> searchAllElecShipping(int physical_warehouse_id);

	public List<Map<String, Object>> searchElecShipping(Integer warehouse_id);
	
}
