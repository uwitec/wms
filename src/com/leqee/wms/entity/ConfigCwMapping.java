package com.leqee.wms.entity;

import java.io.Serializable;

public class ConfigCwMapping  implements Serializable {
	private static final long serialVersionUID = 1L;

    private int id;

    
	private int physical_warehouse_id;
	
	private int customer_id;
	
	private int warehouse_id;
	
	private int sequence_number;

	
    private String warehouse_name;
    
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

	public int getWarehouse_id() {
		return warehouse_id;
	}

	public void setWarehouse_id(int warehouse_id) {
		this.warehouse_id = warehouse_id;
	}

	public int getSequence_number() {
		return sequence_number;
	}

	public void setSequence_number(int sequence_number) {
		this.sequence_number = sequence_number;
	}

	public String getWarehouse_name() {
		return warehouse_name;
	}

	public void setWarehouse_name(String warehouse_name) {
		this.warehouse_name = warehouse_name;
	}
	
}
