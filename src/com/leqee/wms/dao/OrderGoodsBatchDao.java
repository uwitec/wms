package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.OrderGoodsBatch;

public interface OrderGoodsBatchDao {
    int deleteByPrimaryKey(Integer order_goods_batch_id);

    int insert(OrderGoodsBatch record);

    int insertSelective(OrderGoodsBatch record);

    OrderGoodsBatch selectByPrimaryKey(Integer order_goods_batch_id);

    int updateByPrimaryKeySelective(OrderGoodsBatch record);

    int updateByPrimaryKey(OrderGoodsBatch record);

	/**
	 * @param orderGoodsId
	 * @return
	 */
	List<String> selectBatchSnsByOrderGoodsId(@Param("orderGoodsId") long orderGoodsId);

	/**
	 * @param orderGoodsId
	 */
	void deleteByOrderGoods(@Param("orderGoodsId") Integer orderGoodsId);

	/**
	 * @param orderGoodsId
	 * @param batchSn
	 * @return
	 */
	OrderGoodsBatch selectByOrderGoodsIdAndBatchSn(@Param("orderGoodsId") int orderGoodsId, @Param("batchSn") String batchSn);
}