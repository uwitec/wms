package com.leqee.wms.entity;

import java.io.Serializable;

public class TaskCountProductLocation  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private int task_id;
	
	private int pl_id;
	
	private int num;
	
	private String count_sn;
	
	
	private String created_time;


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getTask_id() {
		return task_id;
	}


	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}


	public int getPl_id() {
		return pl_id;
	}


	public void setPl_id(int pl_id) {
		this.pl_id = pl_id;
	}


	public int getNum() {
		return num;
	}


	public void setNum(int num) {
		this.num = num;
	}


	public String getCount_sn() {
		return count_sn;
	}


	public void setCount_sn(String count_sn) {
		this.count_sn = count_sn;
	}


	public String getCreated_time() {
		return created_time;
	}


	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}
	
	
	
}
