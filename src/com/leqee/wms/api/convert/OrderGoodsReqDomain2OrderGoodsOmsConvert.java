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
 * API中的orderItem和orderGoods之间互相转化的类
 * @author qyyao
 * @date 2016-2-23
 * @version 1.0
 */
@Component
public class OrderGoodsReqDomain2OrderGoodsOmsConvert extends AbstractConvert<OrderGoodsReqDomain, OrderGoodsOms>  {
	@Autowired
	ProductDao productDao;
	
	@Override
	public OrderGoodsOms covertToTargetEntity(OrderGoodsReqDomain orderGoodsReqDomain) {
		OrderGoodsOms orderGoodsOms = new OrderGoodsOms();
		orderGoodsOms.setGoods_name(orderGoodsReqDomain.getGoods_name());
		orderGoodsOms.setGoods_number(orderGoodsReqDomain.getGoods_number());
		orderGoodsOms.setGoods_price(orderGoodsReqDomain.getGoods_price());
		orderGoodsOms.setDiscount(orderGoodsReqDomain.getDiscount());
		orderGoodsOms.setBatch_sn(WorkerUtil.getStringValue( orderGoodsReqDomain.getBatch_sn()));
		orderGoodsOms.setStatus_id(orderGoodsReqDomain.getStatus_id());
		orderGoodsOms.setTax_rate(orderGoodsReqDomain.getTax_rate());
		orderGoodsOms.setOms_order_goods_sn(orderGoodsReqDomain.getOms_order_goods_sn());
		
		orderGoodsOms.setGroup_code(orderGoodsReqDomain.getGroup_code());
		orderGoodsOms.setGroup_number(orderGoodsReqDomain.getGroup_number());
		orderGoodsOms.setSku_code(orderGoodsReqDomain.getSku_code());
		Product product= productDao.selectBySkuCode(orderGoodsReqDomain.getSku_code());
		Integer productId = 0;
		if(!WorkerUtil.isNullOrEmpty(product)){
			productId = product.getProduct_id();
			orderGoodsOms.setIs_serial(product.getIs_serial());   //设置下该商品是否串号
		}
		orderGoodsOms.setProduct_id(productId);
		return orderGoodsOms;
	}

	@Override
	public OrderGoodsReqDomain covertToSourceEntity(OrderGoodsOms orderGoodsOms) {
		// TODO Auto-generated method stub
		return null;
	}

}
