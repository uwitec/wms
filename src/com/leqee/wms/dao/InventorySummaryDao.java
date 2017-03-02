package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.InventorySummary;
import com.leqee.wms.entity.InventorySummaryDetail;
import com.leqee.wms.entity.InventorySyncModifyInfo;

public interface InventorySummaryDao {

	public List<InventorySyncModifyInfo> getStockQuantityErrorRecord();

	public InventorySyncModifyInfo getErrorSrockQuantityRecord(int productId, int warehouseId,String statusId);

	public Integer modifyErrorStockQuantity(int inventorySummaryId, int diff);

	public List<InventorySyncModifyInfo> getAvailibleQuantityErrorRecord();

	public void modifyErrorAvailibleQuantity(int inventory_summary_id, int diff);
	
	public Integer getReserveNumNotDelivery(int productId, int warehouseId,String statusId);

	public List<InventorySyncModifyInfo> getErrorSrockDetailQuantityRecord();

	public Integer modifyErrorStockDetailQuantity(int inventory_summary_id,int diff);

	public InventorySummary selectInventorySummary(int product_id,
			int warehouse_id, String status);

	public Integer insert(InventorySummary inventorySummaryNew);
	
	public InventorySummary selectByPrimaryKey(int inventory_summary_id);

	public Integer updateStockQuantity(Integer inventory_summary_id,
			int change_quantity);

	public Integer updateStockAndAvailibleQuantity(Integer inventory_summary_id,
			int change_quantity, int change_quantity2);

	public Integer updateAvailibleNumberForReserve(Integer inventory_summary_id,
			int req);

	public List<Map> getProductNumForReserverByCustomerId(Integer customerId);

	public Integer updateInventorySummaryForReserve(Integer product_id,
			Integer warehouse_id, String status_id, Integer goods_number);
	
	public List<InventorySummary>  selectInventorySummaryByMap(Map<String, Object> map);

	public List<InventorySummary> selectInventorySummaryListByProductIdList(
			@Param("productIdAddList")List<Integer> productIdAddList);

	public void insertList(@Param("list")List<InventorySummary> insertInventorySummaryList);

	public void updateAddList(@Param("list")List<InventorySummary> updateInventorySummaryList);

	public void updateMinusList(@Param("list")List<InventorySummary> updateInventorySummaryList);

	public List<Map> getProductNumForReserverByCustomerIdV5(Integer customer_id);

	public List<Map> getProductNumForReserverByCustomerIdV4(Integer customer_id);

	public void updateInventorySummaryReserveList(@Param("list")List<InventorySummary> updateIsList);

}
