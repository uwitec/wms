package com.leqee.wms.api.request;

import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.GetFrozenResponse;

/**
 * 商品冻结查询Request
 * @author qyyao
 * @date 2016-8-24
 * @version 1.0
 */
public class GetFrozenRequest
    extends  BaseLeqeeRequest<GetFrozenResponse> {

	//physical_warehouse_id和sku_code都是可选项，都不全默认查所有的
	
	private Integer physical_warehouse_id; //物理仓库编号
	private String sku_code; //oms的商家编码
	

	

	public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}


	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}


	public String getSku_code() {
		return sku_code;
	}


	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}


	

	@Override
	public String getApiMethodName() {
		return METHOD_GET_FROZEN_REQUEST;
	}


	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Class<GetFrozenResponse> getResponseClass() {
		return GetFrozenResponse.class;
	}


	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}
    

    






}

    
    
