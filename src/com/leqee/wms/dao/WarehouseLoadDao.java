package com.leqee.wms.dao;

import java.util.HashMap;
import java.util.List;

import com.leqee.wms.entity.WarehouseLoad;

public interface WarehouseLoadDao {

	List<WarehouseLoad> findWarehouseLoadList();

	void insertUserActionWarehouseLoad(HashMap<String, Object> map);

	int selectWarehouseLoadByid(int physical_warehouse_id);

}
