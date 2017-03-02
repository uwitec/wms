package com.leqee.wms.dao;

import java.math.BigDecimal;

import com.leqee.wms.entity.InventorySummaryDetail;

public interface InventorySummaryDetailDao {


	public Integer insert(InventorySummaryDetail inventorySummaryDetail);
	
	public InventorySummaryDetail selectByPrimaryKey(int inventory_summary_detail_id);

	Integer updateDetailStockQuantity(Integer inventory_summary_detail_id,
			int change_quantity);
	
	public InventorySummaryDetail selectInventorySummaryDetailInfo(int product_id,int warehouse_id , String status_id,BigDecimal unit_price,String provider_code ,String batch_sn);

}
