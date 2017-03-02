package com.leqee.wms.entity;

import java.math.BigDecimal;

public class InventorySyncModifyInfo {
    
	int inventory_summary_id;
	
	int inventory_summary_detail_id;
	
	int product_id;
	
	int warehouse_id;
		
	int item_sum;
	
	String status_id;
	
	String batch_sn;
	
	BigDecimal unit_price;
	
	String provider_code;
	
	int stock_quantity;
	
	public int getInventory_summary_detail_id() {
		return inventory_summary_detail_id;
	}

	public void setInventory_summary_detail_id(int inventory_summary_detail_id) {
		this.inventory_summary_detail_id = inventory_summary_detail_id;
	}

	public String getBatch_sn() {
		return batch_sn;
	}

	public void setBatch_sn(String batch_sn) {
		this.batch_sn = batch_sn;
	}

	public BigDecimal getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(BigDecimal unit_price) {
		this.unit_price = unit_price;
	}

	public String getProvider_code() {
		return provider_code;
	}

	public void setProvider_code(String provider_code) {
		this.provider_code = provider_code;
	}

	int available_to_reserved;
	
	int diff;
	
	int reserve_num;

	public int getReserve_num() {
		return reserve_num;
	}

	public void setReserve_num(int reserve_num) {
		this.reserve_num = reserve_num;
	}

	public int getAvailable_to_reserved() {
		return available_to_reserved;
	}

	public void setAvailable_to_reserved(int available_to_reserved) {
		this.available_to_reserved = available_to_reserved;
	}

	public int getDiff() {
		return diff;
	}

	public void setDiff(int diff) {
		this.diff = diff;
	}

	public String getStatus_id() {
		return status_id;
	}

	public void setStatus_id(String status_id) {
		this.status_id = status_id;
	}

	public int getInventory_summary_id() {
		return inventory_summary_id;
	}

	public void setInventory_summary_id(int inventory_summary_id) {
		this.inventory_summary_id = inventory_summary_id;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public int getWarehouse_id() {
		return warehouse_id;
	}

	public void setWarehouse_id(int warehouse_id) {
		this.warehouse_id = warehouse_id;
	}

	public int getStock_quantity() {
		return stock_quantity;
	}

	public void setStock_quantity(int stock_quantity) {
		this.stock_quantity = stock_quantity;
	}

	public int getItem_sum() {
		return item_sum;
	}

	public void setItem_sum(int item_sum) {
		this.item_sum = item_sum;
	}

	@Override
	public String toString() {
		return "InventorySyncModifyInfo [inventory_summary_id="
				+ inventory_summary_id + ", inventory_summary_detail_id="
				+ inventory_summary_detail_id + ", product_id=" + product_id
				+ ", warehouse_id=" + warehouse_id + ", stock_quantity="
				+ stock_quantity + ", item_sum=" + item_sum + ", status_id="
				+ status_id + ", batch_sn=" + batch_sn + ", unit_price="
				+ unit_price + ", provider_code=" + provider_code
				+ ", available_to_reserved=" + available_to_reserved
				+ ", diff=" + diff + ", reserve_num=" + reserve_num + "]";
	}
}
