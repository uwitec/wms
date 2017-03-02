package com.leqee.wms.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.OrderGoodsBiz;
import com.leqee.wms.biz.OrderInfoBiz;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.util.WorkerUtil;

@Service
public class OrderInfoBizImpl implements OrderInfoBiz {
	
//	@Autowired
//	OrderInfoDao orderInfoDao;
//	@Autowired
//	OrderGoodsBiz orderGoodsBiz;
//
//	@Override
//	public OrderInfo getOrderInfoById(Integer orderId) {
//		//要在这里初始化好商品
//		OrderInfo orderInfo =  orderInfoDao.selectByPrimaryKey(orderId);
//		orderInfo.setOrderGoodsList(orderGoodsBiz.getOrderGoodsByOrderId(orderId));
//		return orderInfo;
//	}
//
//	@Override
//	public Integer saveOrderInfoAndOrderGoodsList(OrderInfo orderInfo) {
//		
//		Integer orderId = orderInfoDao.insert(orderInfo);
//		
//		if( WorkerUtil.isNullOrEmpty(orderInfo.getOrderGoodsList()) ){
//			for(OrderGoods orderGoods : orderInfo.getOrderGoodsList()){
//				orderGoodsBiz.saveOrderGoods(orderGoods);
//			}
//		}
//		return orderId;
//	}
	
	


	
	
	
}
