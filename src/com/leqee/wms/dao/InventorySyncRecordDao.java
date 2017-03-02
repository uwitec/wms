package com.leqee.wms.dao;

import com.leqee.wms.entity.InventorySyncRecord;

public interface InventorySyncRecordDao {

	Integer insert(InventorySyncRecord inventorySyncRecord);
	InventorySyncRecord selectByPrimaryKey(int inventory_sync_record);
}
