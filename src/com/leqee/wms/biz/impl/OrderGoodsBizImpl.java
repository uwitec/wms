package com.leqee.wms.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.OrderGoodsBiz;
import com.leqee.wms.dao.OrderGoodsDao;
import com.leqee.wms.entity.OrderGoods;

@Service
public class OrderGoodsBizImpl implements OrderGoodsBiz {
	@Autowired
	OrderGoodsDao orderGoodsDao;

	@Override
	public List<OrderGoods> findByShipmentIdAndProductId(Integer shipmentId,
			Integer productId) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("shipmentId", shipmentId);
		paramsMap.put("productId", productId);
		return orderGoodsDao.selectByShipmentIdAndProductId(paramsMap);
	}
	
}
