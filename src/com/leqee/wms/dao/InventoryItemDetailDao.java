package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.InventoryItemDetail;

public interface InventoryItemDetailDao {
   
	public int insert(InventoryItemDetail inventoryItemDetail);
	/**
	 * hchen1
	 * @param orderId
	 * @return
	 */
	public List<InventoryItemDetail> selectByReturnOrderId(Integer orderId);
	
	public InventoryItemDetail selectByPrimaryKey(int inventory_item_detail_id);

	/**
	 * @author Jarvis
	 * @Description 根据订单号进行查找
	 * 
	 * @param orderId an int: 订单ID
	 * 
	 * @return inventoryItemDetailList a List<InventoryItemDetail>: InventoryItemDetail列表
	 * */
	public List<InventoryItemDetail> selectByOrderId(Integer orderId);

	/**
	 * @author Jarvis
	 * @Description 根据订单ID列表进行查找
	 * 
	 * @param orderIdList a List: 订单ID列表
	 * 
	 * @return inventoryItemDetailList a List<InventoryItemDetail>: InventoryItemDetail列表
	 * */
	public List<InventoryItemDetail> selectByOrderIdList(List<Integer> orderIdList);
	
	public List<InventoryItemDetail> selectByOrderIdAndGoodsId(Map map);

	public void batchInsert(@Param("list")List<InventoryItemDetail> inventoryItemDetailList);
}
