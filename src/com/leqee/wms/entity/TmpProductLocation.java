package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class TmpProductLocation implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer tmp_pl_id;
	
	private Integer customer_id;
	
	private Integer physical_warehouse_id;
	
	private String product_barcode;
	
	private Integer product_id;
	
	private String location_barcode;
	
	private Integer location_id;
	
	private Integer quantity;
	
	private String status;
	
	private String validity;
	
	private String guarantee_validity;
	
	private String serial_number;
	
	private String transfer_status;
	
	private String created_user;
	
	private Date created_time;
	
	private String last_updated_user;
	
	private Date last_updated_time;

	public Integer getTmp_pl_id() {
		return tmp_pl_id;
	}

	public void setTmp_pl_id(Integer tmp_pl_id) {
		this.tmp_pl_id = tmp_pl_id;
	}

	public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}

	public String getProduct_barcode() {
		return product_barcode;
	}

	public void setProduct_barcode(String product_barcode) {
		this.product_barcode = product_barcode;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public String getLocation_barcode() {
		return location_barcode;
	}

	public void setLocation_barcode(String location_barcode) {
		this.location_barcode = location_barcode;
	}

	public Integer getLocation_id() {
		return location_id;
	}

	public void setLocation_id(Integer location_id) {
		this.location_id = location_id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getGuarantee_validity() {
		return guarantee_validity;
	}

	public void setGuarantee_validity(String guarantee_validity) {
		this.guarantee_validity = guarantee_validity;
	}

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}

	public String getTransfer_status() {
		return transfer_status;
	}

	public void setTransfer_status(String transfer_status) {
		this.transfer_status = transfer_status;
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
	
}
