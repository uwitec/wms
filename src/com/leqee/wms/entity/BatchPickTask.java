package com.leqee.wms.entity;

import java.io.Serializable;

public class BatchPickTask  implements Serializable {
	
	private Integer batch_pick_task_id;

    private String created_user;

    private Integer batchpick_num;

    private String physical_warehouse_id;

    private String status;

    private String created_time;
    
    private String order_ids;

    
    private static final long serialVersionUID = 1L;


	public Integer getBatch_pick_task_id() {
		return batch_pick_task_id;
	}


	public void setBatch_pick_task_id(Integer batch_pick_task_id) {
		this.batch_pick_task_id = batch_pick_task_id;
	}


	

	public String getCreated_user() {
		return created_user;
	}


	public void setCreated_user(String created_user) {
		this.created_user = created_user;
	}


	public Integer getBatchpick_num() {
		return batchpick_num;
	}


	public void setBatchpick_num(Integer batchpick_num) {
		this.batchpick_num = batchpick_num;
	}





	public String getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}


	public void setPhysical_warehouse_id(String physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getCreated_time() {
		return created_time;
	}


	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}


	public String getOrder_ids() {
		return order_ids;
	}


	public void setOrder_ids(String order_ids) {
		this.order_ids = order_ids;
	}
    
    
    

}
