package com.leqee.wms.entity;

import java.io.Serializable;

public class Config  implements Serializable {
	private static final long serialVersionUID = 1L;

	
	private int id;
	
	private String config_name;
	
	private String config_value;
	
	private int physical_warehouse_id;
	
	private int customer_id;
	
	private String description;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(int physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}

	public int getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(int customer_id) {
		this.customer_id = customer_id;
	}

	public String getConfig_name() {
		return config_name;
	}

	public void setConfig_name(String config_name) {
		this.config_name = config_name;
	}

	public String getConfig_value() {
		return config_value;
	}

	public void setConfig_value(String config_value) {
		this.config_value = config_value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	
}
