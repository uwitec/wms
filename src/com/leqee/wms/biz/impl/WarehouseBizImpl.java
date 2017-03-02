package com.leqee.wms.biz.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.WarehouseBiz;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.util.WorkerUtil;

@Service
public class WarehouseBizImpl implements WarehouseBiz {
	@Autowired
	WarehouseDao warehouseDao;

	@Override
	public Warehouse findByWarehouseId(Integer warehouseId) {
		return warehouseDao.selectByWarehouseId(warehouseId);
	}

	@Override
	public List<Warehouse> findByWarehouseIds(Set<Integer> warehouseIds) {
		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		if(!WorkerUtil.isNullOrEmpty(warehouseIds)){
			for (Integer warehouseId : warehouseIds) {
				Warehouse warehouse = findByWarehouseId(warehouseId);
				if(!WorkerUtil.isNullOrEmpty(warehouse)){
					warehouses.add(warehouse);
				}
			}
		}
		return warehouses;
	}

	@Override
	public List<Warehouse> findAll() {
		return warehouseDao.selectWarehouseList();
	}

	@Override
	public List<Warehouse> findAllPhysical() {
		return warehouseDao.selectPhysicalWarehouseList();
	}

	@Override
	public List<Warehouse> findAllWarehouseList() {
		return warehouseDao.selectAllWarehouseList();
	}

	@Override
	public List<Warehouse> findLogicWarehousesByPhysicalWarehouseId(
			Integer physicalWarehouseId) {
		return warehouseDao.selectLogicWarehousesByPhysicalWarehouseId(physicalWarehouseId);
	}

	/* (non-Javadoc)
	 * @see com.leqee.wms.biz.WarehouseBiz#getWarehouseListByUser(java.lang.Integer)
	 */
	@Override
	public List<Map<String, String>> getWarehouseListByUser(Integer userId) {
		return warehouseDao.getWarehouseListByUser(userId);
	}

}
