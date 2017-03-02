package com.leqee.wms.api.response;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class SyncShopResponse extends LeqeeResponse {

	private static final long serialVersionUID = 1L;
	
	private Integer shop_id;
	
	public Integer getShop_id() {
		return shop_id;
	}

	public void setShop_id(Integer shop_id) {
		this.shop_id = shop_id;
	}

	public String getShop_name() {
		return shop_name;
	}

	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

	private String shop_name;
	
	

	

	

	
}
