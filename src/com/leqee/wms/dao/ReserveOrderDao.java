
package com.leqee.wms.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.InventoryAvailaleReserve;
import com.leqee.wms.entity.InventoryItem;
import com.leqee.wms.entity.InventorySummary;
import com.leqee.wms.entity.InventorySyncItem;
import com.leqee.wms.entity.OrderInfo;

public interface ReserveOrderDao {
     
	public Integer getLastSyncInventoryItemDetailId(int customerId);
		
	public List<InventorySyncItem> getIncrementInventoryItem(int inventoryItemDetailId,int customerId);
	
	public List<Integer> getReservedOrderIdForSale(int customerId);
	
	public List<Map> getReservedOrderIdForGT(@Param("customerId")int customerId, @Param("condition")String condition);
	
	public InventorySummary lockInventorySummaryForReserve(int inventorySummaryId);
	
	public List<Integer> getDeliverOrderIdForFinishReserve();

	public Integer getLastReservedOrderId(int customerId);

	public List<Integer> getReservedOrderIdForSaleAfter(int customer_id,
			int order_id);
	
	public List<Integer> getReservedOrderIdForSaleBefore(int customerId,
			int order_id);

	public List<Map> getOrderIdForReserve(Map paramsHashMap);

	public Integer getLastSyncDetailId();

	public Integer getMaxIncreasedDetailId();

	public List<InventorySyncItem> getIncrementInventoryItems(
			Integer inventoryItemDetailId, Integer maxIncreasedDetailId);
	
	public List<InventorySyncItem> getMinusInventoryItems(
			Integer inventoryItemDetailId, Integer maxIncreasedDetailId);

	public List<InventorySyncItem> getIncrementInventoryItemsDetail(
			Integer inventoryItemDetailId, Integer maxIncreasedDetailId);

	public List<Map> getOrderNotEnoughAndHasInstorageByCustomerId(
			
			@Param("customerId")Integer customerId, @Param("created_time")Date created_time,@Param("condition") String condition);

	public InventorySummary getInventorySummaryById(int inventory_summary_id);

	public List<Map> getReservedOrderIdForV(@Param("customerId")Integer customerId, @Param("condition")String condition);

	public String checkProductIsSerial(@Param("product_id")Integer product_id);

	public List<InventoryAvailaleReserve> getAvailaleInventoryListForDeliver(
			@Param("product_id")Integer product_id, @Param("warehouse_id")Integer warehouse_id, @Param("status_id")String status_id,
			@Param("condition") String condition);
	
	public List<Map> getPrepackOrderIdForReserve(Map paramsHashMap);
	
	public List<InventoryAvailaleReserve> getAvailaleInventoryListForDeliverV1(@Param("product_id")Integer product_id, @Param("warehouse_id")Integer warehouse_id, @Param("status_id")String status_id,
			@Param("condition") String condition);

	public Integer getLastSyncDetailIdByCustomerId(@Param("customer_id")Integer customer_id);

	public Integer getMaxIncreasedDetailIdByCustomerId(@Param("customer_id")Integer customer_id);

	public List<InventorySyncItem> getIncrementInventoryItemsByCustomerId(
			Integer inventoryItemDetailId, Integer maxIncreasedDetailId,
			Integer customer_id);

	public List<InventorySyncItem> getMinusInventoryItemsByCustomerId(
			Integer inventoryItemDetailId, Integer maxIncreasedDetailId,
			Integer customer_id);

	//注释掉的原本方法 start
	public List<Map> getReservedOrderIdForGTAndVarianceMinus(
			@Param("customer_id")Integer customer_id, @Param("condition")String condition);

	public List<Map> getReservedOrderIdForSaleOrder(@Param("customer_id")Integer customer_id,
			@Param("condition")String condition);

	
	
	public List<Integer> getReservedOrderIdListForGTAndVarianceMinus(
			@Param("customer_id")Integer customer_id, @Param("condition")String condition);

	public List<Map> getReservedOrderIdForGTAndVarianceMinusV2(
			@Param("list")List<Integer> gTOrVMinusOrderIdList);

	public List<Integer> getReservedOrderIdListForSaleOrder(
			@Param("customer_id")Integer customer_id, @Param("condition")String condition);

	public List<Map> getReservedOrderIdForSaleOrderV2(
			@Param("list")List<Integer> saleOrderIdList);

	//注释掉的原本方法 end
	
	//黄神指点后 修改的新方法 start
	public List<Map> getReservedOrderIdListForGTAndVarianceMinusByHzl(
			@Param("customer_id")Integer customer_id, @Param("condition")String condition);
	
	public List<Map> getReservedOrderIdForSaleOrderByHzl(@Param("customer_id")Integer customer_id,
			@Param("condition")String condition, @Param("reserve_status")String reserve_status);
	//黄神指点后 修改的新方法 end
}
