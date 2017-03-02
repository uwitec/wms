package com.leqee.wms.biz;

import java.util.List;

import com.leqee.wms.entity.OrderGoods;

public interface OrderGoodsBiz {

	/**
	 * 根据shipmentId和productId进行查找
	 * @param shipmentId an Integer:
	 * @param productId an Integer:
	 * @return
	 * */
	List<OrderGoods> findByShipmentIdAndProductId(Integer shipmentId,
			Integer productId);
	

}
