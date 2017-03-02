package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * @author hzhang1
 * @date 2016-3-7
 * @version 1.0.0
 */
public class InventoryGoodsFreeze implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer mapping_id;
	
	private Integer customer_id;
	
	private Integer product_id;
	
	private Integer warehouse_id;
	
	private Integer physical_warehouse_id;
	
	private Integer reserve_number;
	
	private String status;
	
	private String created_user;
	
	private Date created_time;
	
	private String last_updated_user;
	
	private Date last_updated_time;
	
	private String freeze_reason;

	public Integer getMapping_id() {
		return mapping_id;
	}

	public void setMapping_id(Integer mapping_id) {
		this.mapping_id = mapping_id;
	}

	public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public Integer getWarehouse_id() {
		return warehouse_id;
	}

	public void setWarehouse_id(Integer warehouse_id) {
		this.warehouse_id = warehouse_id;
	}

	public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}

	public Integer getReserve_number() {
		return reserve_number;
	}

	public void setReserve_number(Integer reserve_number) {
		this.reserve_number = reserve_number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getLast_updated_user() {
		return last_updated_user;
	}

	public void setLast_updated_user(String last_updated_user) {
		this.last_updated_user = last_updated_user;
	}

	public Date getLast_updated_time() {
		return last_updated_time;
	}

	public void setLast_updated_time(Date last_updated_time) {
		this.last_updated_time = last_updated_time;
	}

	public String getFreeze_reason() {
		return freeze_reason;
	}

	public void setFreeze_reason(String freeze_reason) {
		this.freeze_reason = freeze_reason;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
