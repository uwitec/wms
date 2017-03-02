package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.OrderGoods;

public interface OrderGoodsDao {
    
	public int insert(OrderGoods orderGoods);
	
	public OrderGoods selectByPrimaryKey(int order_goods_id);
	
	public OrderGoods selectByOrderIdAndOrderGoodsId(Map map);

	public List<OrderGoods> selectByOrderId(Integer orderId);
	
	public List<OrderGoods> selectByOrderIdV1(Integer orderId);

	public List<Map> selectGoodsInfoByOrderId(Integer orderId);
	
	public OrderGoods selectByOmsOrderGoodsSn(String oms_order_goods_sn);

	/**
	 * 根据ShipmentId和ProductId进行查找
	 * @param shipmentId
	 * @param orderGoodsId
	 * @return 
	 * */
	public List<OrderGoods> selectByShipmentIdAndProductId(Map<String, Object> paramsMap);

	public void deletePackGoods(Integer orderId);

	public void batchInsert(@Param("orderGoodsList")List<OrderGoods> orderGoodsList);

	public int updateReservedNumBack(Integer orderGoodsId, Integer realBackNum);

	public Map<String, Object> selectProductSkuCode(Integer orderGoodsId);
	
	public Integer selectGoodsNumber (Integer orderId,Integer product_id);

	public List<String> checkOrderGoodsNeedTransferCode(Integer order_id);
	
	public int updateOrderGoodsForPackBox(@Param("order_goods_id")Integer order_goods_id,@Param("product_id")Integer product_id,@Param("goods_name")String goods_name);
}
