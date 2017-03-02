package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ProductLocationDetail implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Integer product_location_detail_id;
	
	private Integer pl_id;
	
	private Integer change_quantity;
	
	private Integer task_id;
	
	private String description;
	
	private String created_user;
	
	private Date created_time;
	
	private String last_updated_user;
	
	private Date last_updated_time;
	
	private Integer order_id;
	
	private Integer order_goods_id;
	
	private int task_count_id;//task_count表主键 task_id
	
	private String count_sn;
	

	public Integer getProduct_location_detail_id() {
		return product_location_detail_id;
	}

	public void setProduct_location_detail_id(Integer product_location_detail_id) {
		this.product_location_detail_id = product_location_detail_id;
	}

	public Integer getPl_id() {
		return pl_id;
	}

	public void setPl_id(Integer pl_id) {
		this.pl_id = pl_id;
	}

	public Integer getChange_quantity() {
		return change_quantity;
	}

	public void setChange_quantity(Integer change_quantity) {
		this.change_quantity = change_quantity;
	}

	public Integer getTask_id() {
		return task_id;
	}

	public void setTask_id(Integer task_id) {
		this.task_id = task_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public int getTask_count_id() {
		return task_count_id;
	}

	public void setTask_count_id(int task_count_id) {
		this.task_count_id = task_count_id;
	}

	public String getCount_sn() {
		return count_sn;
	}

	public void setCount_sn(String count_sn) {
		this.count_sn = count_sn;
	}
	
	
}
