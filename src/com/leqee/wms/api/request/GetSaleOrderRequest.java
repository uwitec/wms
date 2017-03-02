package com.leqee.wms.api.request;

import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.GetSaleOrderResponse;


public class GetSaleOrderRequest extends BaseLeqeeRequest<GetSaleOrderResponse>{
	
	private String oms_order_sn;
	
	private String packbox_order_goods;  //是否获取耗材信息

	public String getOms_order_sn() {
		return oms_order_sn;
	}

	public void setOms_order_sn(String oms_order_sn) {
		this.oms_order_sn = oms_order_sn;
	}

	public String getPackbox_order_goods() {
		return packbox_order_goods;
	}

	public void setPackbox_order_goods(String packbox_order_goods) {
		this.packbox_order_goods = packbox_order_goods;
	}

	@Override
	public String getApiMethodName() {
		return METHOD_GET_SALE_ORDER_REQUEST;
	}

	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<GetSaleOrderResponse> getResponseClass() {
		return GetSaleOrderResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}

	
}
