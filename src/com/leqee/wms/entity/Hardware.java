package com.leqee.wms.entity;

import java.io.Serializable;

public class Hardware  implements Serializable {

	private String hardwarecode="";
	
	private int physical_warehouse_id;
	
	private static final long serialVersionUID = 1L;


	public String getHardwarecode() {
		return hardwarecode;
	}

	public void setHardwarecode(String hardwarecode) {
		this.hardwarecode = hardwarecode;
	}

	public int getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(int physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}
	
	
}
