package com.leqee.wms.biz;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.leqee.wms.entity.InventorySyncItem;
import com.leqee.wms.entity.OrderReserveDetail;

/**
 * 
 * 用inventory_item_detail表中增加的记录，来维护inventory_summary表
 * 
 */
public interface ReserveOrderInventoryBiz {

	
	 
	 public List<Integer> getAllCustomerIdForReserve(String con);

	 void reserveGTOrderByOrderId(int orderId);

	int reserveGTOrderInventoryByOrderGoodsId(
			OrderReserveDetail orderReserveDetail);

	void reserveOrderByOrderId(Map orderMap);

	void updateOrderReserveStatusByOrderId(Integer orderId);

	int reserveOrderInventoryByOrderGoodsId(
			OrderReserveDetail orderReserveDetail);

	void finishedOrderInventoryReservation(Integer orderId);
	
	public void cancelOrderInventoryReservation(Integer order_id);
}
