package com.leqee.wms.entity;

import java.math.BigDecimal;

public class InventorySyncItem {
	
	int inventory_item_detail_id;
	int  product_id;
	int  change_quantity;
	int  warehouse_id;
	int customer_id;
	String status;
	BigDecimal unit_cost;
	String provider_code;
	String batch_sn;
	
	
	public int getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(int customer_id) {
		this.customer_id = customer_id;
	}
	public int getInventory_item_detail_id() {
		return inventory_item_detail_id;
	}
	public void setInventory_item_detail_id(int inventory_item_detail_id) {
		this.inventory_item_detail_id = inventory_item_detail_id;
	}
	public int getProduct_id() {
		return product_id;
	}
	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}
	public int getChange_quantity() {
		return change_quantity;
	}
	public void setChange_quantity(int change_quantity) {
		this.change_quantity = change_quantity;
	}
	public int getWarehouse_id() {
		return warehouse_id;
	}
	public void setWarehouse_id(int warehouse_id) {
		this.warehouse_id = warehouse_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getUnit_cost() {
		return unit_cost;
	}
	public void setUnit_cost(BigDecimal unit_cost) {
		this.unit_cost = unit_cost;
	}
	public String getProvider_code() {
		return provider_code;
	}
	public void setProvider_code(String provider_code) {
		this.provider_code = provider_code;
	}
	public String getBatch_sn() {
		return batch_sn;
	}
	public void setBatch_sn(String batch_sn) {
		this.batch_sn = batch_sn;
	}
	@Override
	public String toString() {
		return "InventorySyncItem [product_id=" + product_id
				+ ", change_quantity=" + change_quantity + ", warehouse_id="
				+ warehouse_id + ", status=" + status + ", unit_cost="
				+ unit_cost + ", provider_code=" + provider_code
				+ ", batch_sn=" + batch_sn + "]";
	}
	
}
