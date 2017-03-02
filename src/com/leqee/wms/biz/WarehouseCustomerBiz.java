package com.leqee.wms.biz;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.leqee.wms.entity.WarehouseCustomer;

public interface WarehouseCustomerBiz {
	
	/**
     * 根据主键进行查找
     * @param customerId an Integer: 主键
     * @return
     */
	public WarehouseCustomer findByCustomerId(Integer customerId);
	
	/**
     * 根据主键集合进行查找
     * @param customerIds a Set<Integer>: 主键集合
     * @return
     */
	public List<WarehouseCustomer> findByCustomerIds(Set<Integer> customerIds);

	/**
     * 查找所有的货主
     * @return
     */
	public List<WarehouseCustomer> findAll();
	
	
	public List<Map<String,String>> getWarehouseCustomerListByUser(Integer id);
	
}
