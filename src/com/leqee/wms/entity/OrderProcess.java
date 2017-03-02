package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class OrderProcess implements Serializable {
	
	//orderProcess表区分批量单走大后门字段
	
	public final static String BATCH_TRICK_STATUS_INIT = "INIT";
	
    private Integer order_id;

    private String oms_order_sn;

    private Integer customer_id;

    private Integer warehouse_id;

    private String reserve_status;

    private Date reserve_time;

    private String inventory_out_status;

    private Date inventory_out_time;

    private Integer batch_pick_id;

    private String order_batch_group;

    private Integer order_batch_sequence_number;
    
    private Integer is_first_shipment = 0;
    
    private String batch_trick_status = "INIT";
    
    private Integer recheck_type = 0;

    private String recheck_status;

    private Date recheck_time;

    private Date shipping_time;

    private String order_type;

    private String status;

    private String created_user;

    private Date created_time;
    
    private int in_batchpick_task;
    
    
    private String product_key;
    private String product_num;
    
    private Integer has_serial_goods;  //是否包含串号商品
    private Integer issf;  //是否是顺丰快递
    private String is2B;  //是否是2B订单

    private static final long serialVersionUID = 1L;

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getOms_order_sn() {
        return oms_order_sn;
    }

    public void setOms_order_sn(String oms_order_sn) {
        this.oms_order_sn = oms_order_sn == null ? null : oms_order_sn.trim();
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public Integer getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public String getReserve_status() {
        return reserve_status;
    }

    public void setReserve_status(String reserve_status) {
        this.reserve_status = reserve_status == null ? null : reserve_status.trim();
    }

    public Date getReserve_time() {
        return reserve_time;
    }

    public void setReserve_time(Date reserve_time) {
        this.reserve_time = reserve_time;
    }

    public String getInventory_out_status() {
        return inventory_out_status;
    }

    public void setInventory_out_status(String inventory_out_status) {
        this.inventory_out_status = inventory_out_status == null ? null : inventory_out_status.trim();
    }

    public Date getInventory_out_time() {
        return inventory_out_time;
    }

    public void setInventory_out_time(Date inventory_out_time) {
        this.inventory_out_time = inventory_out_time;
    }

    public Integer getBatch_pick_id() {
        return batch_pick_id;
    }

    public void setBatch_pick_id(Integer batch_pick_id) {
        this.batch_pick_id = batch_pick_id;
    }

    public String getOrder_batch_group() {
        return order_batch_group;
    }

    public void setOrder_batch_group(String order_batch_group) {
        this.order_batch_group = order_batch_group == null ? null : order_batch_group.trim();
    }

    public Integer getOrder_batch_sequence_number() {
        return order_batch_sequence_number;
    }

    public void setOrder_batch_sequence_number(Integer order_batch_sequence_number) {
        this.order_batch_sequence_number = order_batch_sequence_number;
    }

    public Integer getIs_first_shipment() {
		return is_first_shipment;
	}

	public void setIs_first_shipment(Integer is_first_shipment) {
		this.is_first_shipment = is_first_shipment;
	}

	public String getBatch_trick_status() {
		return batch_trick_status;
	}

	public void setBatch_trick_status(String batch_trick_status) {
		this.batch_trick_status = batch_trick_status;
	}

	public Integer getRecheck_type() {
		return recheck_type;
	}

	public void setRecheck_type(Integer recheck_type) {
		this.recheck_type = recheck_type;
	}

	public String getRecheck_status() {
        return recheck_status;
    }

    public void setRecheck_status(String recheck_status) {
        this.recheck_status = recheck_status == null ? null : recheck_status.trim();
    }

    public Date getRecheck_time() {
        return recheck_time;
    }

    public void setRecheck_time(Date recheck_time) {
        this.recheck_time = recheck_time;
    }

    public Date getShipping_time() {
        return shipping_time;
    }

    public void setShipping_time(Date shipping_time) {
        this.shipping_time = shipping_time;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type == null ? null : order_type.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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

    public int getIn_batchpick_task() {
		return in_batchpick_task;
	}

	public void setIn_batchpick_task(int in_batchpick_task) {
		this.in_batchpick_task = in_batchpick_task;
	}

	
	public Integer getHas_serial_goods() {
		return has_serial_goods;
	}

	public void setHas_serial_goods(Integer has_serial_goods) {
		this.has_serial_goods = has_serial_goods;
	}

	public String getProduct_key() {
		return product_key;
	}

	public void setProduct_key(String product_key) {
		this.product_key = product_key;
	}

	public String getProduct_num() {
		return product_num;
	}

	public void setProduct_num(String product_num) {
		this.product_num = product_num;
	}

	
	public Integer getIssf() {
		return issf;
	}

	public void setIssf(Integer issf) {
		this.issf = issf;
	}
	
	
	
	
	public String getIs2B() {
		return is2B;
	}

	public void setIs2B(String is2b) {
		is2B = is2b;
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
        OrderProcess other = (OrderProcess) that;
        return (this.getOrder_id() == null ? other.getOrder_id() == null : this.getOrder_id().equals(other.getOrder_id()))
            && (this.getOms_order_sn() == null ? other.getOms_order_sn() == null : this.getOms_order_sn().equals(other.getOms_order_sn()))
            && (this.getCustomer_id() == null ? other.getCustomer_id() == null : this.getCustomer_id().equals(other.getCustomer_id()))
            && (this.getWarehouse_id() == null ? other.getWarehouse_id() == null : this.getWarehouse_id().equals(other.getWarehouse_id()))
            && (this.getReserve_status() == null ? other.getReserve_status() == null : this.getReserve_status().equals(other.getReserve_status()))
            && (this.getReserve_time() == null ? other.getReserve_time() == null : this.getReserve_time().equals(other.getReserve_time()))
            && (this.getInventory_out_status() == null ? other.getInventory_out_status() == null : this.getInventory_out_status().equals(other.getInventory_out_status()))
            && (this.getInventory_out_time() == null ? other.getInventory_out_time() == null : this.getInventory_out_time().equals(other.getInventory_out_time()))
            && (this.getBatch_pick_id() == null ? other.getBatch_pick_id() == null : this.getBatch_pick_id().equals(other.getBatch_pick_id()))
            && (this.getOrder_batch_group() == null ? other.getOrder_batch_group() == null : this.getOrder_batch_group().equals(other.getOrder_batch_group()))
            && (this.getOrder_batch_sequence_number() == null ? other.getOrder_batch_sequence_number() == null : this.getOrder_batch_sequence_number().equals(other.getOrder_batch_sequence_number()))
            && (this.getIs_first_shipment() == null ? other.getIs_first_shipment() == null : this.getIs_first_shipment().equals(other.getIs_first_shipment()))
            && (this.getBatch_trick_status() == null ? other.getBatch_trick_status() == null : this.getBatch_trick_status().equals(other.getBatch_trick_status()))
            && (this.getRecheck_type() == null ? other.getRecheck_type() == null : this.getRecheck_type().equals(other.getRecheck_type()))
            && (this.getRecheck_status() == null ? other.getRecheck_status() == null : this.getRecheck_status().equals(other.getRecheck_status()))
            && (this.getRecheck_time() == null ? other.getRecheck_time() == null : this.getRecheck_time().equals(other.getRecheck_time()))
            && (this.getShipping_time() == null ? other.getShipping_time() == null : this.getShipping_time().equals(other.getShipping_time()))
            && (this.getOrder_type() == null ? other.getOrder_type() == null : this.getOrder_type().equals(other.getOrder_type()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOrder_id() == null) ? 0 : getOrder_id().hashCode());
        result = prime * result + ((getOms_order_sn() == null) ? 0 : getOms_order_sn().hashCode());
        result = prime * result + ((getCustomer_id() == null) ? 0 : getCustomer_id().hashCode());
        result = prime * result + ((getWarehouse_id() == null) ? 0 : getWarehouse_id().hashCode());
        result = prime * result + ((getReserve_status() == null) ? 0 : getReserve_status().hashCode());
        result = prime * result + ((getReserve_time() == null) ? 0 : getReserve_time().hashCode());
        result = prime * result + ((getInventory_out_status() == null) ? 0 : getInventory_out_status().hashCode());
        result = prime * result + ((getInventory_out_time() == null) ? 0 : getInventory_out_time().hashCode());
        result = prime * result + ((getBatch_pick_id() == null) ? 0 : getBatch_pick_id().hashCode());
        result = prime * result + ((getOrder_batch_group() == null) ? 0 : getOrder_batch_group().hashCode());
        result = prime * result + ((getOrder_batch_sequence_number() == null) ? 0 : getOrder_batch_sequence_number().hashCode());
        result = prime * result + ((getIs_first_shipment() == null) ? 0 : getIs_first_shipment().hashCode());
        result = prime * result + ((getBatch_trick_status() == null) ? 0 : getBatch_trick_status().hashCode());
        result = prime * result + ((getRecheck_type() == null) ? 0 : getRecheck_type().hashCode());
        result = prime * result + ((getRecheck_status() == null) ? 0 : getRecheck_status().hashCode());
        result = prime * result + ((getRecheck_time() == null) ? 0 : getRecheck_time().hashCode());
        result = prime * result + ((getShipping_time() == null) ? 0 : getShipping_time().hashCode());
        result = prime * result + ((getOrder_type() == null) ? 0 : getOrder_type().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}