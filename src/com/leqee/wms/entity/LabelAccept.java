package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class LabelAccept implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer task_id=0; //task表的主键

	private Integer inventory_location_id;
	
	private Integer location_id;
	
	private String location_barcode;
	
	private String location_kw_barcode;
	
	private Integer customer_id;
	
	private Integer physical_warehouse_id;
	
	private Integer warehouse_id;
	
	private Integer product_id;
	
	private String is_serial;
	
	private String serial_no;
	
	private String goods_barcode;
	
	private Date validity;
	
	private String batch_sn;
	
	private Integer tray_number;
	
	private Integer arrive_number;
	
	private Integer quantity;
	
	//private String pile;
	
	private String ti;
	
	private String hi;
	
	private String status_id;
	
	private String created_user;
	
	private Date created_time;
	
	private String last_updated_user;
	
	private Date last_updated_time;

	private Integer order_id;
	
	private Integer order_goods_id;
	
	public Integer getTask_id() {
		return task_id;
	}

	public void setTask_id(Integer task_id) {
		this.task_id = task_id;
	}

	public Integer getInventory_location_id() {
		return inventory_location_id;
	}

	public void setInventory_location_id(Integer inventory_location_id) {
		this.inventory_location_id = inventory_location_id;
	}

	public Integer getLocation_id() {
		return location_id;
	}

	public void setLocation_id(Integer location_id) {
		this.location_id = location_id;
	}

	public String getLocation_barcode() {
		return location_barcode;
	}

	public void setLocation_barcode(String location_barcode) {
		this.location_barcode = location_barcode;
	}

	public String getLocation_kw_barcode() {
		return location_kw_barcode;
	}

	public void setLocation_kw_barcode(String location_kw_barcode) {
		this.location_kw_barcode = location_kw_barcode;
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

	public Integer getWarehouse_id() {
		return warehouse_id;
	}

	public void setWarehouse_id(Integer warehouse_id) {
		this.warehouse_id = warehouse_id;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public String getIs_serial() {
		return is_serial;
	}

	public void setIs_serial(String is_serial) {
		this.is_serial = is_serial;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getGoods_barcode() {
		return goods_barcode;
	}

	public void setGoods_barcode(String goods_barcode) {
		this.goods_barcode = goods_barcode;
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

	public Integer getTray_number() {
		return tray_number;
	}

	public void setTray_number(Integer tray_number) {
		this.tray_number = tray_number;
	}

	public Integer getArrive_number() {
		return arrive_number;
	}

	public void setArrive_number(Integer arrive_number) {
		this.arrive_number = arrive_number;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getTi() {
		return ti;
	}

	public void setTi(String ti) {
		this.ti = ti;
	}

	public String getHi() {
		return hi;
	}

	public void setHi(String hi) {
		this.hi = hi;
	}

	public String getStatus_id() {
		return status_id;
	}

	public void setStatus_id(String status_id) {
		this.status_id = status_id;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
