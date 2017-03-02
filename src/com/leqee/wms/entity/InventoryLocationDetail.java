package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class InventoryLocationDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer inventory_location_detail_id;
	
	private Integer inventory_location_id;
	
	private Integer product_id;
	
	private Integer change_quantity;
	
	private String action_type;
	
	private Integer order_id;
	
	private Integer order_goods_id;
	
	private String created_user;
	
	private Date created_time;

	public Integer getInventory_location_detail_id() {
		return inventory_location_detail_id;
	}

	public void setInventory_location_detail_id(Integer inventory_location_detail_id) {
		this.inventory_location_detail_id = inventory_location_detail_id;
	}

	public Integer getInventory_location_id() {
		return inventory_location_id;
	}

	public void setInventory_location_id(Integer inventory_location_id) {
		this.inventory_location_id = inventory_location_id;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public Integer getChange_quantity() {
		return change_quantity;
	}

	public void setChange_quantity(Integer change_quantity) {
		this.change_quantity = change_quantity;
	}

	public String getAction_type() {
		return action_type;
	}

	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

	public Integer getOrder_goods_id() {
		return order_goods_id;
	}

	public void setOrder_goods_id(Integer order_goods_id) {
		this.order_goods_id = order_goods_id;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
