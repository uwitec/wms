package com.leqee.wms.dao;

import com.leqee.wms.entity.UserActionWarehouseLoad;

public interface UserActionWarehouseLoadDao {

	/**
	 * 插入：修改仓库存储的负荷时记录日志
	 * 
	 * @param userActionWarehouseLoad
	 * @return
	 */
	public void insertUserActionWarehouseLoadRecord(
			UserActionWarehouseLoad userActionWarehouseLoad);

}
