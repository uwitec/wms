package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * BatchTask实体类
 * @author hzhang1
 * @date 2016-6-23
 * @version 1.0.0
 */
public class BatchTask implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TASK_TYPE_REPLENISHMENT = "REPLENISHMENT"; // 补货
	public static final String TASK_TYPE_PICK = "PICK"; // 拣货
	public static final String TASK_TYPE_VIRTURL_PICK = "VIRTURL_PICK"; // 虚拟拣货
	public static final String TASK_TYPE_PUT_AWAY = "PUT_AWAY"; // 上架
	public static final String TASK_TYPE_MOVE = "MOVE"; // 移库
	public static final String TASK_TYPE_COUNT = "COUNT"; //盘点
	
	
	private Integer batch_task_id;
	
	private String batch_task_sn;
	
	private String task_type;
	
	private char status;
	
	private String created_user;
	
	private Date created_time;

	public Integer getBatch_task_id() {
		return batch_task_id;
	}

	public void setBatch_task_id(Integer batch_task_id) {
		this.batch_task_id = batch_task_id;
	}

	public String getBatch_task_sn() {
		return batch_task_sn;
	}

	public void setBatch_task_sn(String batch_task_sn) {
		this.batch_task_sn = batch_task_sn;
	}

	public String getTask_type() {
		return task_type;
	}

	public void setTask_type(String task_type) {
		this.task_type = task_type;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
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
