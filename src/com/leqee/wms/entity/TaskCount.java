package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class TaskCount implements Serializable {

	//任务状态 {INIT：初始化;  CANCEL:已; FULFILLED:已完成; ON_FIRST:出盘中;
	//         OVER_FIRST:初盘已回单; ON_SECOND:复盘中;　OVER_SECOND复盘已回单:;ON_THIRD:三盘中;}
	public static final String STATUS_INIT="INIT";
	public static final String STATUS_CANCEL="CANCEL";
	public static final String STATUS_FULFILLED="FULFILLED";
	public static final String STATUS_ON_FIRST="ON_FIRST";
	public static final String STATUS_OVER_FIRST="OVER_FIRST";
	public static final String STATUS_ON_SECOND="ON_SECOND";
	public static final String STATUS_OVER_SECOND="OVER_SECOND";
	public static final String STATUS_ON_THIRD="ON_THIRD";
	
	private int task_id = 0;

	private String count_sn;

	private int mark = 0;// 这个波次是第几次

	private String batch_task_sn1; // ----->batch_task_count
	private String batch_task_sn2;
	private String batch_task_sn3;

	private Integer physical_warehouse_id;
	private Integer warehouse_id=0;
	private String warehouse_name="";
	private Integer customer_id;
	
	private String customer_name;

	private String task_type = ""; // NORMAL:普通盘点

	private String barcode = "";

	private String product_name = "";

	private int product_id = 0;
	
	private String location_barcode;

	private Integer hide_real_num; // 0：隐藏真实数目 1不隐藏

	private Integer hide_batch_sn = 0; // 0:忽略批次号 1:不忽略

	private String validity="1970-01-01 00:00:00"; // 生产日期

	private String batch_sn=""; // 商品的批次号

	private String product_status; // 商品新旧 NORMAL DEFECTIVE

	//任务状态 {INIT：初始化;  CANCEL:已; FULFILLED:已完成; ON_FIRST:出盘中;
	//         OVER_FIRST:初盘已回单; ON_SECOND:复盘中;　OVER_SECOND复盘已回单:;ON_THIRD:三盘中;}
	private String status; 
	private int num_real;

	private int num_first;
	private int num_second;
	private int num_third;

	private int num_dif=0;// 误差 （=真实值-理论值）
	
	private int location_id;

	private String created_user;

	private Date created_time;

	private String updated_user_first;

	private Date updated_time_first;
	
	private String updated_user_second;

	private Date updated_time_second;
	
	private String updated_user_third;

	private Date updated_time_third;
	
	private String is_improved="N";

	private static final long serialVersionUID = 1L;

	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public String getCount_sn() {
		return count_sn;
	}

	public void setCount_sn(String count_sn) {
		this.count_sn = count_sn;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public String getBatch_task_sn1() {
		return batch_task_sn1;
	}

	public void setBatch_task_sn1(String batch_task_sn1) {
		this.batch_task_sn1 = batch_task_sn1;
	}

	public String getBatch_task_sn2() {
		return batch_task_sn2;
	}

	public void setBatch_task_sn2(String batch_task_sn2) {
		this.batch_task_sn2 = batch_task_sn2;
	}

	public String getBatch_task_sn3() {
		return batch_task_sn3;
	}

	public void setBatch_task_sn3(String batch_task_sn3) {
		this.batch_task_sn3 = batch_task_sn3;
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

	public String getWarehouse_name() {
		return warehouse_name;
	}

	public void setWarehouse_name(String warehouse_name) {
		this.warehouse_name = warehouse_name;
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

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
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

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getBatch_sn() {
		return batch_sn;
	}

	public void setBatch_sn(String batch_sn) {
		this.batch_sn = batch_sn;
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

	public int getNum_real() {
		return num_real;
	}

	public void setNum_real(int num_real) {
		this.num_real = num_real;
	}

	public int getNum_first() {
		return num_first;
	}

	public void setNum_first(int num_first) {
		this.num_first = num_first;
	}

	public int getNum_second() {
		return num_second;
	}

	public void setNum_second(int num_second) {
		this.num_second = num_second;
	}

	public int getNum_third() {
		return num_third;
	}

	public void setNum_third(int num_third) {
		this.num_third = num_third;
	}

	public int getNum_dif() {
		return num_dif;
	}

	public void setNum_dif(int num_dif) {
		this.num_dif = num_dif;
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

	public String getUpdated_user_first() {
		return updated_user_first;
	}

	public void setUpdated_user_first(String updated_user_first) {
		this.updated_user_first = updated_user_first;
	}

	public Date getUpdated_time_first() {
		return updated_time_first;
	}

	public void setUpdated_time_first(Date updated_time_first) {
		this.updated_time_first = updated_time_first;
	}

	public String getUpdated_user_second() {
		return updated_user_second;
	}

	public void setUpdated_user_second(String updated_user_second) {
		this.updated_user_second = updated_user_second;
	}

	public Date getUpdated_time_second() {
		return updated_time_second;
	}

	public void setUpdated_time_second(Date updated_time_second) {
		this.updated_time_second = updated_time_second;
	}

	public String getUpdated_user_third() {
		return updated_user_third;
	}

	public void setUpdated_user_third(String updated_user_third) {
		this.updated_user_third = updated_user_third;
	}

	public Date getUpdated_time_third() {
		return updated_time_third;
	}

	public void setUpdated_time_third(Date updated_time_third) {
		this.updated_time_third = updated_time_third;
	}

	public int getLocation_id() {
		return location_id;
	}

	public void setLocation_id(int location_id) {
		this.location_id = location_id;
	}

	public String getLocation_barcode() {
		return location_barcode;
	}

	public void setLocation_barcode(String location_barcode) {
		this.location_barcode = location_barcode;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getIs_improved() {
		return is_improved;
	}

	public void setIs_improved(String is_improved) {
		this.is_improved = is_improved;
	}
	
	

}
