package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ProductLocationFreezeDetail implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final String TYPE_FREEZE = "FREEZE";
	public static final String TYPE_UNFREEZE = "UNFREEZE";

	private Integer freeze_detail_id;
	
	private Integer pl_id;
	
	private String type;
	
	private Integer change_quantity;
	
	private String description;
	
	private String created_user;
	
	private Date careted_time;

	public Integer getFreeze_detail_id() {
		return freeze_detail_id;
	}

	public void setFreeze_detail_id(Integer freeze_detail_id) {
		this.freeze_detail_id = freeze_detail_id;
	}

	public Integer getPl_id() {
		return pl_id;
	}

	public void setPl_id(Integer pl_id) {
		this.pl_id = pl_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getChange_quantity() {
		return change_quantity;
	}

	public void setChange_quantity(Integer change_quantity) {
		this.change_quantity = change_quantity;
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

	public Date getCareted_time() {
		return careted_time;
	}

	public void setCareted_time(Date careted_time) {
		this.careted_time = careted_time;
	}
	
}
