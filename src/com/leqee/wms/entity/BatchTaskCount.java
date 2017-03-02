package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class BatchTaskCount  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int batch_task_id;
	
	private String batch_task_sn;  //盘点任务编号
	
	private int mark=1;//这个波次是第几次
	
	private String count_sn;//盘点任务号  ----->schedule_queue_count
	
	private String created_user;
	
	private Date created_time;
	
	
	private int binded_user_id;
	
	private Date binded_time;

	public int getBatch_task_id() {
		return batch_task_id;
	}

	public void setBatch_task_id(int batch_task_id) {
		this.batch_task_id = batch_task_id;
	}	
	
	public String getBatch_task_sn() {
		return batch_task_sn;
	}

	public void setBatch_task_sn(String batch_task_sn) {
		this.batch_task_sn = batch_task_sn;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}



	public String getCount_sn() {
		return count_sn;
	}

	public void setCount_sn(String count_sn) {
		this.count_sn = count_sn;
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



	public int getBinded_user_id() {
		return binded_user_id;
	}

	public void setBinded_user_id(int binded_user_id) {
		this.binded_user_id = binded_user_id;
	}

	public Date getBinded_time() {
		return binded_time;
	}

	public void setBinded_time(Date binded_time) {
		this.binded_time = binded_time;
	}
	
	
}
