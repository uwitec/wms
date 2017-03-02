package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class Pallet implements Serializable {
    private Integer pallet_id;

    private String pallet_no;

    private String ship_status;

    private Integer shipping_id;
    
    private Integer physical_warehouse_id;

    private String shipped_user;

    private Date shipped_time;

    private String created_user;

    private Date created_time;

    private static final long serialVersionUID = 1L;

    public Pallet(Integer pallet_id, String pallet_no, String ship_status, Integer shipping_id, String shipped_user, Date shipped_time, String created_user, Date created_time) {
        this.pallet_id = pallet_id;
        this.pallet_no = pallet_no;
        this.ship_status = ship_status;
        this.shipping_id = shipping_id;
        this.shipped_user = shipped_user;
        this.shipped_time = shipped_time;
        this.created_user = created_user;
        this.created_time = created_time;
    }

    public Pallet() {
        super();
    }

    public Integer getPallet_id() {
        return pallet_id;
    }

    public void setPallet_id(Integer pallet_id) {
        this.pallet_id = pallet_id;
    }

    public String getPallet_no() {
        return pallet_no;
    }

    public void setPallet_no(String pallet_no) {
        this.pallet_no = pallet_no == null ? null : pallet_no.trim();
    }

    public String getShip_status() {
        return ship_status;
    }

    public void setShip_status(String ship_status) {
        this.ship_status = ship_status == null ? null : ship_status.trim();
    }

    public Integer getShipping_id() {
        return shipping_id;
    }

    public void setShipping_id(Integer shipping_id) {
        this.shipping_id = shipping_id;
    }

    public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}

	public String getShipped_user() {
        return shipped_user;
    }

    public void setShipped_user(String shipped_user) {
        this.shipped_user = shipped_user == null ? null : shipped_user.trim();
    }

    public Date getShipped_time() {
        return shipped_time;
    }

    public void setShipped_time(Date shipped_time) {
        this.shipped_time = shipped_time;
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
        Pallet other = (Pallet) that;
        return (this.getPallet_id() == null ? other.getPallet_id() == null : this.getPallet_id().equals(other.getPallet_id()))
            && (this.getPallet_no() == null ? other.getPallet_no() == null : this.getPallet_no().equals(other.getPallet_no()))
            && (this.getShip_status() == null ? other.getShip_status() == null : this.getShip_status().equals(other.getShip_status()))
            && (this.getShipping_id() == null ? other.getShipping_id() == null : this.getShipping_id().equals(other.getShipping_id()))
            && (this.getShipped_user() == null ? other.getShipped_user() == null : this.getShipped_user().equals(other.getShipped_user()))
            && (this.getShipped_time() == null ? other.getShipped_time() == null : this.getShipped_time().equals(other.getShipped_time()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPallet_id() == null) ? 0 : getPallet_id().hashCode());
        result = prime * result + ((getPallet_no() == null) ? 0 : getPallet_no().hashCode());
        result = prime * result + ((getShip_status() == null) ? 0 : getShip_status().hashCode());
        result = prime * result + ((getShipping_id() == null) ? 0 : getShipping_id().hashCode());
        result = prime * result + ((getShipped_user() == null) ? 0 : getShipped_user().hashCode());
        result = prime * result + ((getShipped_time() == null) ? 0 : getShipped_time().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}