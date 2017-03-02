package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ConfigReplenishmentUrgent implements Serializable  {
	
private static final long serialVersionUID = 1L;
private Integer config_id; //  
private Integer physical_warehouse_id; //  物理仓库id，关联warehouse表 
private Integer customer_id; //  货主id关联warehouse_customer表 
private Integer replenishment_condition; //  紧急补货数量规则:1.按照订单需求量补货；2.按照max【订单需求量，最高存量】补货；3.按照订单需求量+最高存量补货 
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
public Integer getReplenishment_condition() {
	return replenishment_condition;
}
public void setReplenishment_condition(Integer replenishment_condition) {
	this.replenishment_condition = replenishment_condition;
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
public static long getSerialversionuid() {
	return serialVersionUID;
}

}