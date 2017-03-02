package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ScheduleQueueReplenishment implements Serializable {
    private Integer queue_id;

    private Integer physical_warehouse_id;

    private Integer customer_id;

    private Integer product_id;

    private String box_piece;

    private Integer task_level;

    private String queue_status;

    private String created_user;

    private Date created_time;

    private Date last_updated_time;

    private static final long serialVersionUID = 1L;

	public Integer getQueue_id() {
		return queue_id;
	}

	public void setQueue_id(Integer queue_id) {
		this.queue_id = queue_id;
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

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public String getBox_piece() {
		return box_piece;
	}

	public void setBox_piece(String box_piece) {
		this.box_piece = box_piece;
	}

	public Integer getTask_level() {
		return task_level;
	}

	public void setTask_level(Integer task_level) {
		this.task_level = task_level;
	}

	public String getQueue_status() {
		return queue_status;
	}

	public void setQueue_status(String queue_status) {
		this.queue_status = queue_status;
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

	public Date getLast_updated_time() {
		return last_updated_time;
	}

	public void setLast_updated_time(Date last_updated_time) {
		this.last_updated_time = last_updated_time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

        result = prime * result + ((getBox_piece() == null) ? 0 : getBox_piece().hashCode());
        result = prime * result + ((getCustomer_id() == null) ? 0 : getCustomer_id().hashCode());
        result = prime * result + ((getPhysical_warehouse_id() == null) ? 0 : getPhysical_warehouse_id().hashCode());
        result = prime * result + ((getProduct_id() == null) ? 0 : getProduct_id().hashCode());
        result = prime * result + ((getQueue_status() == null) ? 0 : getQueue_status().hashCode());
        result = prime * result + ((getQueue_id() == null) ? 0 : getQueue_id().hashCode());
        result = prime * result + ((getTask_level() == null) ? 0 : getTask_level().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        result = prime * result + ((getLast_updated_time() == null) ? 0 : getLast_updated_time().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScheduleQueueReplenishment other = (ScheduleQueueReplenishment) obj;
		return (this.getBox_piece() == null ? other.getBox_piece() == null : this.getBox_piece().equals(other.getBox_piece()))
            && (this.getCustomer_id() == null ? other.getCustomer_id() == null : this.getCustomer_id().equals(other.getCustomer_id()))
            && (this.getLast_updated_time() == null ? other.getLast_updated_time() == null : this.getLast_updated_time().equals(other.getLast_updated_time()))
            && (this.getPhysical_warehouse_id() == null ? other.getPhysical_warehouse_id() == null : this.getPhysical_warehouse_id().equals(other.getPhysical_warehouse_id()))
            && (this.getProduct_id() == null ? other.getProduct_id() == null : this.getProduct_id().equals(other.getProduct_id()))
            && (this.getQueue_id() == null ? other.getQueue_id() == null : this.getQueue_id().equals(other.getQueue_id()))
            && (this.getQueue_status() == null ? other.getQueue_status() == null : this.getQueue_status().equals(other.getQueue_status()))
            && (this.getTask_level() == null ? other.getTask_level() == null : this.getTask_level().equals(other.getTask_level()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
	}
    

}