package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.api.response.domain.InventoryDetailResDomain;
import com.leqee.wms.api.response.domain.InventorySummaryResDomain;
import com.leqee.wms.entity.InventoryItem;
import com.leqee.wms.entity.InventoryItemDetail;

public interface InventoryItemDao {
    
	public int insert(InventoryItem inventoryItem);
	
	public InventoryItem selectByPrimaryKey(int inventory_item_id);

	/**
	 * @author Jarvis
	 * @Description 查询串号商品在库数量
	 * 
	 * @param serialNumber a String: 串号
	 * 
	 * @return count an int: 在库数量
	 * */
	public int countBySerialNumber(String serialNumber);

	/**
	 * 库存查询API：查询库存Summary
	 * @param paramsMap
	 * @return
	 */
	public List<InventorySummaryResDomain> selectInventorySummarys(
			Map<String, Object> paramsMap);

	/**
	 * 库存查询API：查询库存Detail
	 * @param paramsMap
	 * @return
	 */
	public List<InventoryDetailResDomain> selectDetailResDomains(
			Map<String, Object> paramsMap);
	
	/**
	 * 根据rootItemId查找InventoryItem记录
	 * @param rootItemId
	 * @return
	 */
	public List<InventoryItem> selectByItemId(String rootItemId);
	
	/**
	 * 更新InventoryItem记录
	 * @param rootItemId
	 * @return
	 */
	public void updateInventoryItem(InventoryItem inventoryItem); 
	
	/**
	 * 根据serialNumber查找InventoryItem记录
	 * @param serialNumber
	 * @return
	 * */
	public List<InventoryItem> selectBySerialNumber(String serialNumber);

	public void batchupdate(@Param("list")List<Integer> inventoryItemIdList);

	public List<InventoryItem> selectInventoryItemListV2(
			@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("warehouse_id") int warehouse_id, @Param("list")List<Integer> productIdList,
			@Param("status") String status);

	public void updateInventoryItemByPackOut(
			@Param("list")List<InventoryItem> updateInventoryItemList);
	
	public InventoryItem selectByProductId(Integer product_id);
	
}
