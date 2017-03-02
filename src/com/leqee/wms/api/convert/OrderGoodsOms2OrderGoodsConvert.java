package com.leqee.wms.api.convert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.leqee.wms.api.request.domain.OrderGoodsReqDomain;
import com.leqee.wms.api.util.WorkerUtil;
import com.leqee.wms.convert.AbstractConvert;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderGoodsOms;
import com.leqee.wms.entity.Product;

/**
 * API中的orderGoodsOms和orderGoods之间互相转化的类
 * @author qyyao
 * @date 2016-2-23
 * @version 1.0
 */
@Component
public class OrderGoodsOms2OrderGoodsConvert extends AbstractConvert<OrderGoodsOms, OrderGoods>  {
	@Autowired
	ProductDao productDao;
	
	@Override
	public OrderGoods covertToTargetEntity(OrderGoodsOms orderGoodsOms) {
		OrderGoods orderGoods = new OrderGoods();
		orderGoods.setGoods_name(orderGoodsOms.getGoods_name());
		orderGoods.setGoods_number(orderGoodsOms.getGoods_number());
		orderGoods.setGoods_price(orderGoodsOms.getGoods_price());
		orderGoods.setDiscount(orderGoodsOms.getDiscount());
		orderGoods.setBatch_sn(WorkerUtil.getStringValue( orderGoodsOms.getBatch_sn()));
		orderGoods.setStatus_id(orderGoodsOms.getStatus_id());
		orderGoods.setTax_rate(orderGoodsOms.getTax_rate());
		orderGoods.setOms_order_goods_sn(orderGoodsOms.getOms_order_goods_sn());
		
		orderGoods.setGroup_code(orderGoodsOms.getGroup_code());
		orderGoods.setGroup_number(orderGoodsOms.getGroup_number());
		
		orderGoods.setProduct_id(orderGoodsOms.getProduct_id());
		return orderGoods;
	}

	@Override
	public OrderGoodsOms covertToSourceEntity(OrderGoods t) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
