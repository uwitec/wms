package com.leqee.wms.api.request;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.SyncProductResponse;
import com.leqee.wms.api.response.SyncRegionResponse;
import com.leqee.wms.api.response.SyncShopResponse;


	
public class SyncShopRequest extends BaseLeqeeRequest<SyncShopResponse> {
	
	
	private Integer customer_id;
	
	private String oms_shop_id;
	
	private String shop_name;
	
	private String created_user;

	private Date created_time;

    private String last_updated_user;

    private Date last_updated_time;
	
	public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	public String getOms_shop_id() {
		return oms_shop_id;
	}

	public void setOms_shop_id(String oms_shop_id) {
		this.oms_shop_id = oms_shop_id;
	}

	public String getShop_name() {
		return shop_name;
	}

	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

	public String getCreated_user() {
		return created_user;
	}

	public void setCreated_user(String created_user) {
		this.created_user = created_user;
	}

	public Date getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}

	public String getLast_updated_user() {
		return last_updated_user;
	}

	public void setLast_updated_user(String last_updated_user) {
		this.last_updated_user = last_updated_user;
	}

	public Date getLast_updated_time() {
		return last_updated_time;
	}

	public void setLast_updated_time(Date last_updated_time) {
		this.last_updated_time = last_updated_time;
	}

	
   

	
	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<SyncShopResponse> getResponseClass() {
		return SyncShopResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getApiMethodName() {
		return METHOD_SYNC_SHOP_REQUEST;
	}
    
    
    
    
    
    
}
