package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ConfigReplenishment implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	private Integer config_id; //  
    private Integer product_id; //  商品id，关联product表 
    private Integer physical_warehouse_id; //  物理仓库id，关联warehouse表 
    private Integer customer_id; //  货主id关联warehouse_customer表 
    private String from_piece_location_barcode; //  件拣货区库位id，关联location表 
    private String to_piece_location_barcode; //  件拣货区库位id，关联location表 
    private Integer piece_location_max_quantity; //  件拣货区最高存量 
    private Integer piece_location_min_quantity; //  件拣货区最低存量 
    private String from_box_location_barcode; //  箱拣货区库位id，关联location表 
    private String to_box_location_barcode; //  箱拣货区库位id，关联location表 
    private Integer box_location_max_quantity; //  箱拣货区最高存量 
    private Integer box_location_min_quantity; //  箱拣货区最低存量 
    private String created_user; //  创建者 
    private Date created_time; //  创建时间 
    private String last_updated_user; //  最后更新人 
    private Date last_updated_time; //  最后更新时间
	public Integer getConfig_id() {
		return config_id;
	}
	public void setConfig_id(Integer config_id) {
		this.config_id = config_id;
	}
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}
	public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}
	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}
	public Integer getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}
	
	
	public String getFrom_piece_location_barcode() {
		return from_piece_location_barcode;
	}
	public void setFrom_piece_location_barcode(String from_piece_location_barcode) {
		this.from_piece_location_barcode = from_piece_location_barcode;
	}
	public String getTo_piece_location_barcode() {
		return to_piece_location_barcode;
	}
	public void setTo_piece_location_barcode(String to_piece_location_barcode) {
		this.to_piece_location_barcode = to_piece_location_barcode;
	}
	public String getFrom_box_location_barcode() {
		return from_box_location_barcode;
	}
	public void setFrom_box_location_barcode(String from_box_location_barcode) {
		this.from_box_location_barcode = from_box_location_barcode;
	}
	public String getTo_box_location_barcode() {
		return to_box_location_barcode;
	}
	public void setTo_box_location_barcode(String to_box_location_barcode) {
		this.to_box_location_barcode = to_box_location_barcode;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Integer getPiece_location_max_quantity() {
		return piece_location_max_quantity;
	}
	public void setPiece_location_max_quantity(Integer piece_location_max_quantity) {
		this.piece_location_max_quantity = piece_location_max_quantity;
	}
	public Integer getPiece_location_min_quantity() {
		return piece_location_min_quantity;
	}
	public void setPiece_location_min_quantity(Integer piece_location_min_quantity) {
		this.piece_location_min_quantity = piece_location_min_quantity;
	}
	
	public Integer getBox_location_max_quantity() {
		return box_location_max_quantity;
	}
	public void setBox_location_max_quantity(Integer box_location_max_quantity) {
		this.box_location_max_quantity = box_location_max_quantity;
	}
	public Integer getBox_location_min_quantity() {
		return box_location_min_quantity;
	}
	public void setBox_location_min_quantity(Integer box_location_min_quantity) {
		this.box_location_min_quantity = box_location_min_quantity;
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