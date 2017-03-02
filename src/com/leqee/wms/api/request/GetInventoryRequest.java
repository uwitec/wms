package com.leqee.wms.api.request;

import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.CancelOrderResponse;
import com.leqee.wms.api.response.GetInventoryResponse;

/**
 * 商品库存查询Request
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
public class GetInventoryRequest
    extends  BaseLeqeeRequest<GetInventoryResponse> {

	
	public static final String TYPE_SUMMARY = "SUMMARY";  
	public static final String TYPE_DETAIL = "DETAIL";
	
	
	private Integer warehouse_id; //仓库编号
	private String sku_code; //oms的商家编码
	private String type;; //查询类别summary、detail
	

	public Integer getWarehouse_id() {
		return warehouse_id;
	}


	public void setWarehouse_id(Integer warehouse_id) {
		this.warehouse_id = warehouse_id;
	}


	public String getSku_code() {
		return sku_code;
	}


	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	@Override
	public String getApiMethodName() {
		return METHOD_GET_INVENTORY_REQUEST;
	}


	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Class<GetInventoryResponse> getResponseClass() {
		return GetInventoryResponse.class;
	}


	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}
    

    






}

    
    
