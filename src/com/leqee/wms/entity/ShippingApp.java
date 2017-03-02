package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ShippingApp implements Serializable {
    private Integer app_id;

    private String app_key;

    private String app_secret;

    private String apply_type;

    private Integer apply_amount;

    private String created_user;

    private Date created_time;

    private static final long serialVersionUID = 1L;

    public Integer getApp_id() {
        return app_id;
    }

    public void setApp_id(Integer app_id) {
        this.app_id = app_id;
    }

    public String getApp_key() {
        return app_key;
    }

    public void setApp_key(String app_key) {
        this.app_key = app_key == null ? null : app_key.trim();
    }

    public String getApp_secret() {
        return app_secret;
    }

    public void setApp_secret(String app_secret) {
        this.app_secret = app_secret == null ? null : app_secret.trim();
    }

    public String getApply_type() {
        return apply_type;
    }

    public void setApply_type(String apply_type) {
        this.apply_type = apply_type == null ? null : apply_type.trim();
    }

    public Integer getApply_amount() {
        return apply_amount;
    }

    public void setApply_amount(Integer apply_amount) {
        this.apply_amount = apply_amount;
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
        ShippingApp other = (ShippingApp) that;
        return (this.getApp_id() == null ? other.getApp_id() == null : this.getApp_id().equals(other.getApp_id()))
            && (this.getApp_key() == null ? other.getApp_key() == null : this.getApp_key().equals(other.getApp_key()))
            && (this.getApp_secret() == null ? other.getApp_secret() == null : this.getApp_secret().equals(other.getApp_secret()))
            && (this.getApply_type() == null ? other.getApply_type() == null : this.getApply_type().equals(other.getApply_type()))
            && (this.getApply_amount() == null ? other.getApply_amount() == null : this.getApply_amount().equals(other.getApply_amount()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getApp_id() == null) ? 0 : getApp_id().hashCode());
        result = prime * result + ((getApp_key() == null) ? 0 : getApp_key().hashCode());
        result = prime * result + ((getApp_secret() == null) ? 0 : getApp_secret().hashCode());
        result = prime * result + ((getApply_type() == null) ? 0 : getApply_type().hashCode());
        result = prime * result + ((getApply_amount() == null) ? 0 : getApply_amount().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}