package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hzhang1
 * @date 2016-3-4
 * @version 1.0.0
 */
public class InventoryItemVariance implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer inventory_item_variance_id;
	
	private Integer inventory_item_id;
	
	private Integer quantity;
	
	private String created_user;
	
	private Date created_time;
	
	private Date last_updated_time;
	
	private String comments;

	public Integer getInventory_item_variance_id() {
		return inventory_item_variance_id;
	}

	public void setInventory_item_variance_id(Integer inventory_item_variance_id) {
		this.inventory_item_variance_id = inventory_item_variance_id;
	}

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

	public Date getLast_updated_time() {
		return last_updated_time;
	}

	public void setLast_updated_time(Date last_updated_time) {
		this.last_updated_time = last_updated_time;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
