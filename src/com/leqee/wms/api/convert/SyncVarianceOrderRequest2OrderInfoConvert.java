package com.leqee.wms.api.convert;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.leqee.wms.api.request.SyncVarianceOrderRequest;
import com.leqee.wms.api.request.domain.OrderGoodsReqDomain;
import com.leqee.wms.convert.AbstractConvert;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.util.WorkerUtil;


/**
 * @author hzhang1
 * @date 2016-2-26
 * @version 1.0.0
 */
@Component
public class SyncVarianceOrderRequest2OrderInfoConvert extends AbstractConvert<SyncVarianceOrderRequest,OrderInfo>{

	@Autowired
	OrderGoodsReqDomain2OrderGoodsConvert orderGoodsReqDomain2OrderGoodsConvert;
	
	@Override
	public OrderInfo covertToTargetEntity(SyncVarianceOrderRequest request) {
		
		/**
		 * -v订单同步必须字段：
		 * oms_order_sn
		 * warehouse_id
		 * order_type
		 * 
		 * 不是必须字段：
		 * note
		 * currency
		 * oms_order_type
		 */
		
		OrderInfo orderInfo = new OrderInfo();
		
		orderInfo.setOms_order_sn(WorkerUtil.getStringValue(request.getOms_order_sn()));
		orderInfo.setWarehouse_id(request.getWarehouse_id());
		orderInfo.setOrder_type(WorkerUtil.getStringValue(request.getOrder_type()));
		
		// 不是必须的字段
		orderInfo.setNote(WorkerUtil.getStringValue(request.getNote()));
		if(WorkerUtil.isNullOrEmpty(request.getCurrency())){
			orderInfo.setCurrency("RMB");
		}else{
			orderInfo.setCurrency(WorkerUtil.getStringValue(request.getCurrency()));
		}
		
		orderInfo.setOms_order_type(WorkerUtil.getStringValue(request.getOrder_type()));
		
		orderInfo.setOrder_time(request.getOrder_time());
		orderInfo.setCreated_time(new Date());
		orderInfo.setCreated_user("system");
		orderInfo.setLast_updated_time(new Date());
		orderInfo.setLast_updated_user("system");
		orderInfo.setOms_shop_id("0");
		//合并订单和oms_shipment_sn
		orderInfo.setIs_merge_order(WorkerUtil.getStringValue(request.getIs_merge_order()));
		orderInfo.setSlave_oms_order_sns(WorkerUtil.getStringValue(request.getSlave_oms_order_sns()));
		orderInfo.setOms_shipment_sn(WorkerUtil.getStringValue(request.getOms_shipment_sn()));
		
		// 其他还需要设置数据库不能为空的字段
		orderInfo.setOrder_status(OrderInfo.ORDER_STATUS_ACCEPT);
		orderInfo.setIs_reserved("N");
		orderInfo.setReceive_name("");
		orderInfo.setSex("");
		orderInfo.setIs_value_declared("");
		orderInfo.setIs_payment_collected("");
		orderInfo.setBrand_name("");
		orderInfo.setProvider_code("");
		orderInfo.setProvider_order_type("");
		orderInfo.setProvider_name("");
		List<OrderGoodsReqDomain> orderGoodsDomainList = request.getOrderGoodsReqDomainList();
		orderInfo.setOrderGoodsList(orderGoodsReqDomain2OrderGoodsConvert.covertToTargetEntity(orderGoodsDomainList));

		return orderInfo;
	}

	@Override
	public SyncVarianceOrderRequest covertToSourceEntity(OrderInfo orderInfo) {
		// TODO Auto-generated method stub
		return null;
	}

}
