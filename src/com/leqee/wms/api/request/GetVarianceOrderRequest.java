package com.leqee.wms.api.request;

import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.GetVarianceOrderResponse;

/**
 * @author hzhang1
 * @date 2016-3-1
 * @version 1.0.0
 */
public class GetVarianceOrderRequest extends BaseLeqeeRequest<GetVarianceOrderResponse>{
	
	
	private String oms_order_sn;
	
	
	public String getOms_order_sn() {
		return oms_order_sn;
	}

	public void setOms_order_sn(String oms_order_sn) {
		this.oms_order_sn = oms_order_sn;
	}

	@Override
	public String getApiMethodName() {
		return METHOD_GET_VARIANCE_ORDER_REQUEST;
	}

	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<GetVarianceOrderResponse> getResponseClass() {
		return GetVarianceOrderResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}
	
}
