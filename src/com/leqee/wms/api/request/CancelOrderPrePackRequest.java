package com.leqee.wms.api.request;

import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.CancelOrderResponse;

public class CancelOrderPrePackRequest extends  BaseLeqeeRequest<CancelOrderResponse> {
	
	private String oms_task_sn;
	
	public String getOms_task_sn() {
		return oms_task_sn;
	}

	public void setOms_task_sn(String oms_task_sn) {
		this.oms_task_sn = oms_task_sn;
	}

	@Override
	public String getApiMethodName() {
		return METHOD_CANCEL_ORDERPREPACK_REQUEST;
	}

	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<CancelOrderResponse> getResponseClass() {
		return CancelOrderResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}
}
