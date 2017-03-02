package com.leqee.wms.entity;

import java.io.Serializable;

public class TaskAllotProductlocation   implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	
	private Integer task_id;
	
	private Integer pl_id;
	
	private Integer product_id;
	
	private Integer qty_available;
	
	private Integer quantity;
	
	private Integer change_quantity=0;

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

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public Integer getQty_available() {
		return qty_available;
	}

	public void setQty_available(Integer qty_available) {
		this.qty_available = qty_available;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getChange_quantity() {
		return change_quantity;
	}

	public void setChange_quantity(Integer change_quantity) {
		this.change_quantity = change_quantity;
	}

	
	
}
