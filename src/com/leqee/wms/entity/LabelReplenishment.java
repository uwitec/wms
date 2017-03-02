package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class LabelReplenishment implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer label_replenishment_id;
	
	private Integer task_id;
	
	private Integer pl_id;
	
	private Integer location_id;
	
	private String from_location_barcode;
	
	private String location_barcode;
	
	private Integer product_id;
	
	private Integer quantity;
	
	private String status;
	
	private Date validity;
	
	private String batch_sn;
	
	private String serial_number;
	
	private String created_user;
	
	private Date created_time;
	
	private String last_updated_user;
	
	private Date last_updated_time;

	public Integer getLabel_replenishment_id() {
		return label_replenishment_id;
	}

	public void setLabel_replenishment_id(Integer label_replenishment_id) {
		this.label_replenishment_id = label_replenishment_id;
	}

	public Integer getTask_id() {
		return task_id;
	}

	public void setTask_id(Integer task_id) {
		this.task_id = task_id;
	}
	
	public Integer getPl_id() {
		return pl_id;
	}

	public void setPl_id(Integer pl_id) {
		this.pl_id = pl_id;
	}

	public Integer getLocation_id() {
		return location_id;
	}

	public void setLocation_id(Integer location_id) {
		this.location_id = location_id;
	}
	
	public String getFrom_location_barcode() {
		return from_location_barcode;
	}

	public void setFrom_location_barcode(String from_location_barcode) {
		this.from_location_barcode = from_location_barcode;
	}

	public String getLocation_barcode() {
		return location_barcode;
	}

	public void setLocation_barcode(String location_barcode) {
		this.location_barcode = location_barcode;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
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

	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	public String getBatch_sn() {
		return batch_sn;
	}

	public void setBatch_sn(String batch_sn) {
		this.batch_sn = batch_sn;
	}

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
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
