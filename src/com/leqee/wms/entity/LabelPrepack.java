package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class LabelPrepack implements Serializable {
	

	private static final long serialVersionUID = 1L;

	private Integer label_prepack_id;
    
    private Integer task_id;
    
    private String status;
    
    private Integer location_id;
    
    private String location_barcode;
    
    private Integer order_id;
    
    private Integer packbox_product_id;
    
    private String created_user;
    
    private Date created_time;
    
    private String last_updated_user;
    
    private Date last_updated_time;
    
    private Integer packbox_num;
    
    private Integer packbox_need_out;
    
    public Integer getPackbox_num() {
		return packbox_num;
	}

	public void setPackbox_num(Integer packbox_num) {
		this.packbox_num = packbox_num;
	}

	public Integer getPackbox_need_out() {
		return packbox_need_out;
	}

	public void setPackbox_need_out(Integer packbox_need_out) {
		this.packbox_need_out = packbox_need_out;
	}

    
    public Integer getLabel_prepack_id() {
		return label_prepack_id;
	}

	public void setLabel_prepack_id(Integer label_prepack_id) {
		this.label_prepack_id = label_prepack_id;
	}

	public Integer getTask_id() {
		return task_id;
	}

	public void setTask_id(Integer task_id) {
		this.task_id = task_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

	public Integer getPackbox_product_id() {
		return packbox_product_id;
	}

	public void setPackbox_product_id(Integer packbox_product_id) {
		this.packbox_product_id = packbox_product_id;
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