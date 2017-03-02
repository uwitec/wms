package com.leqee.wms.api.request;

import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.CancelOrderResponse;

/**
 * 取消订单Request
 * @author qyyao
 * @date 2016-2-23
 * @version 1.0
 */
public class CancelOrderRequest
    extends  BaseLeqeeRequest<CancelOrderResponse> {

	
	private Integer order_id;
	private String order_type;
	private Integer warehouse_id;;
	
	
	
	public Integer getOrder_id() {
		return order_id;
	}


	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}


	public String getOrder_type() {
		return order_type;
	}


	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}


	public Integer getWarehouse_id() {
		return warehouse_id;
	}


	public void setWarehouse_id(Integer warehouse_id) {
		this.warehouse_id = warehouse_id;
	}


	@Override
	public String getApiMethodName() {
		return METHOD_CANCEL_ORDER_REQUEST;
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

    
    
