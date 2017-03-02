package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ScheduleQueueCount implements Serializable {
	
	public static final String STATUS_INIT="INIT";
	public static final String STATUS_FULFILLED="FULFILLED";
	public static final String STATUS_CANCEL="CANCEL";

	private int queue_id = 0;

	private String count_sn;

	private Integer physical_warehouse_id;

	private Integer customer_id = 0;
	
	private String customer_name = "";  //货主名称
	
	private int num=0;  //当前生成任务量

	private String task_type = ""; // 1:普通盘点

	private Integer hide_real_num = 0; // 0：隐藏真实数目 1不隐藏

	private Integer hide_batch_sn = 0; // 0:忽略批次号 1:不忽略

	private String status;

	private String location_type; // 库位类型

	private String from_location_barcode; // 库位id，关联location表

	private String to_location_barcode; // 库位id，关联location表
	
	private String barcode=""; // 库位id，关联location表

	private String created_user;

	private Date created_time;

	private String last_updated_user;

	private Date last_updated_time;

	private static final long serialVersionUID = 1L;

	public int getQueue_id() {
		return queue_id;
	}

	public void setQueue_id(int queue_id) {
		this.queue_id = queue_id;
	}

	public String getCount_sn() {
		return count_sn;
	}

	public void setCount_sn(String count_sn) {
		this.count_sn = count_sn;
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

	public String getTask_type() {
		return task_type;
	}

	public void setTask_type(String task_type) {
		this.task_type = task_type;
	}

	public Integer getHide_real_num() {
		return hide_real_num;
	}

	public void setHide_real_num(Integer hide_real_num) {
		this.hide_real_num = hide_real_num;
	}

	public Integer getHide_batch_sn() {
		return hide_batch_sn;
	}

	public void setHide_batch_sn(Integer hide_batch_sn) {
		this.hide_batch_sn = hide_batch_sn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLocation_type() {
		return location_type;
	}

	public void setLocation_type(String location_type) {
		this.location_type = location_type;
	}

	public String getFrom_location_barcode() {
		return from_location_barcode;
	}

	public void setFrom_location_barcode(String from_location_barcode) {
		this.from_location_barcode = from_location_barcode;
	}

	public String getTo_location_barcode() {
		return to_location_barcode;
	}

	public void setTo_location_barcode(String to_location_barcode) {
		this.to_location_barcode = to_location_barcode;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
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

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

}
