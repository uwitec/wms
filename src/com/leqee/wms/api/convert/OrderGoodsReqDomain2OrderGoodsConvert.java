package com.leqee.wms.api.convert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.leqee.wms.api.request.domain.OrderGoodsReqDomain;
import com.leqee.wms.api.util.WorkerUtil;
import com.leqee.wms.convert.AbstractConvert;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.Product;

/**
 * API中的orderItem和orderGoods之间互相转化的类
 * @author qyyao
 * @date 2016-2-23
 * @version 1.0
 */
@Component
public class OrderGoodsReqDomain2OrderGoodsConvert extends AbstractConvert<OrderGoodsReqDomain, OrderGoods>  {
	@Autowired
	ProductDao productDao;
	
	@Override
	public OrderGoods covertToTargetEntity(OrderGoodsReqDomain orderGoodsReqDomain) {
		OrderGoods orderGood = new OrderGoods();
		orderGood.setGoods_name(orderGoodsReqDomain.getGoods_name());
		orderGood.setGoods_number(orderGoodsReqDomain.getGoods_number());
		orderGood.setGoods_price(orderGoodsReqDomain.getGoods_price());
		orderGood.setDiscount(orderGoodsReqDomain.getDiscount());
		orderGood.setBatch_sn(WorkerUtil.getStringValue( orderGoodsReqDomain.getBatch_sn()));
		orderGood.setStatus_id(orderGoodsReqDomain.getStatus_id());
		orderGood.setTax_rate(orderGoodsReqDomain.getTax_rate());
		orderGood.setOms_order_goods_sn(orderGoodsReqDomain.getOms_order_goods_sn());
		
		Product product= productDao.selectBySkuCode(orderGoodsReqDomain.getSku_code());
		Integer productId = 0;
		if(!WorkerUtil.isNullOrEmpty(product)){
			productId = product.getProduct_id();
			orderGood.setIs_serial(product.getIs_serial());   //设置下该商品是否串号
		}
		orderGood.setProduct_id(productId);
		return orderGood;
	}

	@Override
	public OrderGoodsReqDomain covertToSourceEntity(OrderGoods orderGoodsReqDomain) {
		// TODO Auto-generated method stub
		return null;
	}

}
