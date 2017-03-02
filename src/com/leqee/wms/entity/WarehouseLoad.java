package com.leqee.wms.entity;

import java.io.Serializable;

public class WarehouseLoad implements Serializable {

	/**
	 * 物理仓库名称
	 */
	private String warehouse_name;

	/**
	 * 物理仓库号
	 */
	private int physical_warehouse_id;

	/**
	 * 日最大单量
	 */
	private int loads;

	/**
	 * 当前单量
	 */
	private int nums;

	private static final long serialVersionUID = 1L;

	public String getWarehouse_name() {
		return warehouse_name;
	}

	public void setWarehouse_name(String warehouse_name) {
		this.warehouse_name = warehouse_name;
	}

	public int getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(int physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}

	public int getLoads() {
		return loads;
	}

	public void setLoads(int loads) {
		this.loads = loads;
	}

	public int getNums() {
		return nums;
	}

	public void setNums(int nums) {
		this.nums = nums;
	}

}
