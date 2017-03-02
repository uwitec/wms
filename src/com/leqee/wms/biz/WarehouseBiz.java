package com.leqee.wms.biz;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.leqee.wms.entity.Warehouse;

public interface WarehouseBiz {
	/**
     * 根据主键进行查找
     * @param warehouseId an Integer: 主键集合
     * @return
     */
	public Warehouse findByWarehouseId(Integer warehouseId);

	/**
     * 根据主键集合进行查找
     * @param warehouseIds a Set<Integer>: 主键集合
     * @return
     */
	public List<Warehouse> findByWarehouseIds(Set<Integer> warehouseIds);

	/**
     * 查找所有的逻辑仓库
     * @return
     */
	public List<Warehouse> findAll();

	/**
	 * 查找所有的物理仓库
	 * @return 
	 */
	public List<Warehouse> findAllPhysical();
	
	/**
     * 查找所有的仓库（包括物理仓、逻辑仓）
     * @return
     */
	public List<Warehouse> findAllWarehouseList();

	/**
     * 查找指定物理仓下的所有逻辑仓
     * @return
     */
	public List<Warehouse> findLogicWarehousesByPhysicalWarehouseId(
			Integer physicalWarehouseId);
	
	
	public List<Map<String,String>> getWarehouseListByUser(Integer userId);
}
