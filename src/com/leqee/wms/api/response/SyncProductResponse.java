package com.leqee.wms.api.response;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class SyncProductResponse extends LeqeeResponse {

	private static final long serialVersionUID = 1L;
	
	private String product_id;
	
	private String sku_code;

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	
}
