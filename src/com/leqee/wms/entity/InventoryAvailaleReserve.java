package com.leqee.wms.entity;

import java.util.Date;

public class InventoryAvailaleReserve {
    
	Integer inventory_item_id;
	
	Integer quantity;
	
	Integer reserved_quantity;
	
	Date create_time;
	
	Date validity ;

	

	public Integer getInventory_item_id() {
		return inventory_item_id;
	}

	public void setInventory_item_id(Integer inventory_item_id) {
		this.inventory_item_id = inventory_item_id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getReserved_quantity() {
		return reserved_quantity;
	}

	public void setReserved_quantity(Integer reserved_quantity) {
		this.reserved_quantity = reserved_quantity;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	@Override
	public String toString() {
		return "InventoryAvailaleReserve [inventory_item_id="
				+ inventory_item_id + ", quantity=" + quantity
				+ ", reserved_quantity=" + reserved_quantity + ", create_time="
				+ create_time + ", validity=" + validity + "]";
	}

	
		
}
