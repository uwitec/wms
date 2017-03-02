package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ShippingTrackingNumberRepository implements Serializable {
    private Integer shipping_app_id;

    private String tracking_number;

    private String status;

    private String created_user;

    private Date created_time;

    private static final long serialVersionUID = 1L;

    public Integer getShipping_app_id() {
        return shipping_app_id;
    }

    public void setShipping_app_id(Integer shipping_app_id) {
        this.shipping_app_id = shipping_app_id;
    }

    public String getTracking_number() {
        return tracking_number;
    }

    public void setTracking_number(String tracking_number) {
        this.tracking_number = tracking_number == null ? null : tracking_number.trim();
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
        ShippingTrackingNumberRepository other = (ShippingTrackingNumberRepository) that;
        return (this.getShipping_app_id() == null ? other.getShipping_app_id() == null : this.getShipping_app_id().equals(other.getShipping_app_id()))
            && (this.getTracking_number() == null ? other.getTracking_number() == null : this.getTracking_number().equals(other.getTracking_number()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getShipping_app_id() == null) ? 0 : getShipping_app_id().hashCode());
        result = prime * result + ((getTracking_number() == null) ? 0 : getTracking_number().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}