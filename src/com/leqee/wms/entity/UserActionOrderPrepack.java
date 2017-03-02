package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class UserActionOrderPrepack implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long action_id;
	
	private Integer order_id;
	
	private String order_status;
	
	private String action_type;
	
	private String action_note;
	
	private String created_user;
	
	private Date created_time;

	public Long getAction_id() {
		return action_id;
	}

	public void setAction_id(Long action_id) {
		this.action_id = action_id;
	}

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

	public String getOrder_status() {
		return order_status;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public String getAction_type() {
		return action_type;
	}

	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}

	public String getAction_note() {
		return action_note;
	}

	public void setAction_note(String action_note) {
		this.action_note = action_note;
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


   
}