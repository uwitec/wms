package com.leqee.wms.api.response.domain;

/**
 * 库存Summary响应实体
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
public class InventorySummaryResDomain {
	private String sku_code;
	private String status_id;
	private Integer quantity;
	
	
	public String getSku_code() {
		return sku_code;
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	public String getStatus_id() {
		return status_id;
	}
	public void setStatus_id(String status_id) {
		this.status_id = status_id;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	
	
	
	
}
