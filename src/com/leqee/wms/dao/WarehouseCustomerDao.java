package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.WarehouseCustomer;

/**
 * @author hzhang1
 * @date 2016-2-26
 * @version 1.0.0
 */
public interface WarehouseCustomerDao {
	public WarehouseCustomer selectByAppKey(String appKey);

	/**
     * 根据主键ID进行查找
     * @param customerId an Integer: 主键ID
     * @return
     */
	public WarehouseCustomer selectByCustomerId(Integer customerId);

	public List<Integer> selectAllCustomerId(@Param("con")String con);

	/**
     * 查找所有记录
     * @return
     */
	public List<WarehouseCustomer> selectAll();
	public List<Integer> selectAllId();
	
	/**
	 * 根据groupId查找customerId列表
	 * @param groupId
	 */
	public List<Integer> selectCustomerIdListByGroupId(Integer groupId);
	
	public Integer selectCustomerIdByName(String customerName);
	
	public List<Integer> selectCustomerIdByCustomerName(List<String> customerIdList);

	public List<Integer> selectAllCustomerIdsV2();

	public List<WarehouseCustomer> selectAllInit();
	
	public List<Map<String, String>> getWarehouseCustomerListByUser(Integer id);
	
}
