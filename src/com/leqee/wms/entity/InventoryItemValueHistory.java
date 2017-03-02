package com.leqee.wms.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author hzhang1
 * @date 2016-3-1
 * @version 1.0.0
 */
public class InventoryItemValueHistory implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Integer inventory_item_value_hist_id;
	
	private Integer inventory_item_id;
	
	private BigDecimal unit_cost;
	
	private Date date_time;
	
	private String created_user;
	
	private Date created_time;
	
	private Date last_updated_time;

	public Integer getInventory_item_value_hist_id() {
		return inventory_item_value_hist_id;
	}

	public void setInventory_item_value_hist_id(Integer inventory_item_value_hist_id) {
		this.inventory_item_value_hist_id = inventory_item_value_hist_id;
	}

	public Integer getInventory_item_id() {
		return inventory_item_id;
	}

	public void setInventory_item_id(Integer inventory_item_id) {
		this.inventory_item_id = inventory_item_id;
	}

	public BigDecimal getUnit_cost() {
		return unit_cost;
	}

	public void setUnit_cost(BigDecimal unit_cost) {
		this.unit_cost = unit_cost;
	}

	public Date getDate_time() {
		return date_time;
	}

	public void setDate_time(Date date_time) {
		this.date_time = date_time;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
