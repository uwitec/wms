package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.Warehouse;

/**
 * @author hzhang1
 * @date 2016-2-26
 * @version 1.0.0
 */
public interface WarehouseDao {
	
	public Warehouse selectByWarehouseId(Integer warehouse_id);
	
	public Warehouse selectByWarehouseName(String warehouse_name);
	/**
	 * 查询所有仓库列表，只包含逻辑仓
	 * @param
	 * @return warehouseList: a List<Warehouse>
	 * */
	public List<Warehouse> selectWarehouseList();
	
	public List<Integer> selectWarehouseIdList();
	
	/**
	 * 查询所有仓库列表，只包含物理仓
	 * @param
	 * @return warehouseList: a List<Warehouse>
	 * */
	public List<Warehouse> selectPhysicalWarehouseList();
	
	/**
	 * 查询所有仓库列表，包含物理仓和逻辑仓
	 * @param
	 * @return warehouseList: a List<Warehouse>
	 * */
	public List<Warehouse> selectAllWarehouseList();

	/**
	 * 查找指定物理仓下的所有逻辑仓
	 * @param physicalWarehouseId: an Integer
	 * @return warehouseList: a List<Warehouse>
	 * */
	public List<Warehouse> selectLogicWarehousesByPhysicalWarehouseId(
			Integer physicalWarehouseId);

	public List<Integer> selectPhysicalWarehouseIdList();

	/**
	 * @param userId
	 * @return
	 */
	public List<Map<String, String>> getWarehouseListByUser(@Param("userId") Integer userId);
	
	
}
