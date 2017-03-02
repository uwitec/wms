package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.OrderGoodsOms;

public interface OrderGoodsOmsDao {
    
	public int insert(OrderGoodsOms orderGoods);
	
	public OrderGoodsOms selectByPrimaryKey(int order_goods_id);
	
	public OrderGoodsOms selectByOrderIdAndOrderGoodsOmsId(Map map);

	public List<OrderGoodsOms> selectByOrderId(Integer orderId);

	public List<Map> selectGoodsInfoByOrderId(Integer orderId);
	
	public OrderGoodsOms selectByOmsOrderGoodsOmsSn(String oms_order_goods_sn);

	/**
	 * 根据ShipmentId和ProductId进行查找
	 * @param shipmentId
	 * @param orderGoodsId
	 * @return 
	 * */
	public List<OrderGoodsOms> selectByShipmentIdAndProductId(Map<String, Object> paramsMap);

	public void deletePackGoods(Integer orderId);

	public void batchInsert(@Param("orderGoodsList")List<OrderGoodsOms> orderGoodsList);

	public int updateReservedNumBack(Integer orderGoodsId, Integer realBackNum);

	public Map<String, Object> selectProductSkuCode(Integer orderGoodsId);
}
