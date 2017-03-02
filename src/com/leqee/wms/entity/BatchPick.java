package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class BatchPick implements Serializable {
    private Integer batch_pick_id;

    private String batch_pick_sn;

    private String status;

    private String batch_process_type="NORMAL";
    
    private String flow_status="SHIPMENT_N";
    
    private String recheck_mark = "N";

    private String created_user;

    private Date created_time;
    
    private String bind_user_id;
    
    private Date bind_time;
    
    private int physical_warehouse_id;
    
    private int print_count;
    
    private Date first_print_time;
    
    private String reserve_status="N";
    
    private int batch_pick_group_id=0;
    
    private int warehouse_id=0;

    public String getBind_user_id() {
		return bind_user_id;
	}

	public void setBind_user_id(String bind_user_id) {
		this.bind_user_id = bind_user_id;
	}

	public Date getBind_time() {
		return bind_time;
	}

	public void setBind_time(Date bind_time) {
		this.bind_time = bind_time;
	}

	private static final long serialVersionUID = 1L;

    public Integer getBatch_pick_id() {
        return batch_pick_id;
    }

    public void setBatch_pick_id(Integer batch_pick_id) {
        this.batch_pick_id = batch_pick_id;
    }

    public String getBatch_pick_sn() {
        return batch_pick_sn;
    }

    public void setBatch_pick_sn(String batch_pick_sn) {
        this.batch_pick_sn = batch_pick_sn == null ? null : batch_pick_sn.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getBatch_process_type() {
        return batch_process_type;
    }

    public void setBatch_process_type(String batch_process_type) {
        this.batch_process_type = batch_process_type == null ? null : batch_process_type.trim();
    }

    public String getFlow_status() {
		return flow_status;
	}

	public void setFlow_status(String flow_status) {
		this.flow_status = flow_status;
	}

	public String getRecheck_mark() {
		return recheck_mark;
	}

	public void setRecheck_mark(String recheck_mark) {
		this.recheck_mark = recheck_mark;
	}

	public String getCreated_user() {
        return created_user;
    }

    public void setCreated_user(String created_user) {
        this.created_user = created_user == null ? null : created_user.trim();
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public int getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(int physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}
	
	public int getPrint_count() {
		return print_count;
	}

	public void setPrint_count(int print_count) {
		this.print_count = print_count;
	}
	
	public Date getFirst_print_time() {
        return first_print_time;
    }

    public void setFirst_print_time(Date first_print_time) {
        this.first_print_time = first_print_time;
    }

	public String getReserve_status() {
		return reserve_status;
	}

	public void setReserve_status(String reserve_status) {
		this.reserve_status = reserve_status;
	}

	public int getBatch_pick_group_id() {
		return batch_pick_group_id;
	}

	public void setBatch_pick_group_id(int batch_pick_group_id) {
		this.batch_pick_group_id = batch_pick_group_id;
	}

	public int getWarehouse_id() {
		return warehouse_id;
	}

	public void setWarehouse_id(int warehouse_id) {
		this.warehouse_id = warehouse_id;
	}

	@Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        BatchPick other = (BatchPick) that;
        return (this.getBatch_pick_id() == null ? other.getBatch_pick_id() == null : this.getBatch_pick_id().equals(other.getBatch_pick_id()))
            && (this.getBatch_pick_sn() == null ? other.getBatch_pick_sn() == null : this.getBatch_pick_sn().equals(other.getBatch_pick_sn()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getBatch_process_type() == null ? other.getBatch_process_type() == null : this.getBatch_process_type().equals(other.getBatch_process_type()))
            && (this.getFlow_status() == null ? other.getFlow_status() == null : this.getFlow_status().equals(other.getFlow_status()))
            && (this.getRecheck_mark() == null ? other.getRecheck_mark() == null : this.getRecheck_mark().equals(other.getRecheck_mark()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getBatch_pick_id() == null) ? 0 : getBatch_pick_id().hashCode());
        result = prime * result + ((getBatch_pick_sn() == null) ? 0 : getBatch_pick_sn().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getBatch_process_type() == null) ? 0 : getBatch_process_type().hashCode());
        result = prime * result + ((getFlow_status() == null) ? 0 : getFlow_status().hashCode());
        result = prime * result + ((getRecheck_mark() == null) ? 0 : getRecheck_mark().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}