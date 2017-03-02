package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class TaskImprove implements Serializable{

	private int id;
	
	private String task_improve_sn;
	
	private int num;
	
	private String product_status;
	
	private String status;
	
	private int product_id;
	
	private String barcode = "";

	private String product_name = "";
	
	private String created_user;

	private Date created_time;
	
	private String user;
	
	private int physical_warehouse_id;
	
	private int customer_id;

	private Date last_update_time;
	
	private int warehouse_id=0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getProduct_status() {
		return product_status;
	}

	public void setProduct_status(String product_status) {
		this.product_status = product_status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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

	public String getTask_improve_sn() {
		return task_improve_sn;
	}

	public void setTask_improve_sn(String task_improve_sn) {
		this.task_improve_sn = task_improve_sn;
	}

	public Date getLast_update_time() {
		return last_update_time;
	}

	public void setLast_update_time(Date last_update_time) {
		this.last_update_time = last_update_time;
	}

	public int getWarehouse_id() {
		return warehouse_id;
	}

	public void setWarehouse_id(int warehouse_id) {
		this.warehouse_id = warehouse_id;
	}

	private static final long serialVersionUID = 1L;

}
