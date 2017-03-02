package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ShippingWarehouseMapping implements Serializable {
    private Integer mapping_id;

    private Integer shipping_id;

    private Integer warehouse_id;

    private Integer shipping_app_id;

    private String sf_monthly_balance_account;

    private Byte sf_payment_method;

    private String sf_tp_areacode;

    private String paper_type;

    private String created_user;

    private Date created_time;

    private static final long serialVersionUID = 1L;

    public Integer getMapping_id() {
        return mapping_id;
    }

    public void setMapping_id(Integer mapping_id) {
        this.mapping_id = mapping_id;
    }

    public Integer getShipping_id() {
        return shipping_id;
    }

    public void setShipping_id(Integer shipping_id) {
        this.shipping_id = shipping_id;
    }

    public Integer getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public Integer getShipping_app_id() {
        return shipping_app_id;
    }

    public void setShipping_app_id(Integer shipping_app_id) {
        this.shipping_app_id = shipping_app_id;
    }

    public String getSf_monthly_balance_account() {
        return sf_monthly_balance_account;
    }

    public void setSf_monthly_balance_account(String sf_monthly_balance_account) {
        this.sf_monthly_balance_account = sf_monthly_balance_account == null ? null : sf_monthly_balance_account.trim();
    }

    public Byte getSf_payment_method() {
        return sf_payment_method;
    }

    public void setSf_payment_method(Byte sf_payment_method) {
        this.sf_payment_method = sf_payment_method;
    }

    public String getSf_tp_areacode() {
        return sf_tp_areacode;
    }

    public void setSf_tp_areacode(String sf_tp_areacode) {
        this.sf_tp_areacode = sf_tp_areacode == null ? null : sf_tp_areacode.trim();
    }

    public String getPaper_type() {
        return paper_type;
    }

    public void setPaper_type(String paper_type) {
        this.paper_type = paper_type == null ? null : paper_type.trim();
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
        ShippingWarehouseMapping other = (ShippingWarehouseMapping) that;
        return (this.getMapping_id() == null ? other.getMapping_id() == null : this.getMapping_id().equals(other.getMapping_id()))
            && (this.getShipping_id() == null ? other.getShipping_id() == null : this.getShipping_id().equals(other.getShipping_id()))
            && (this.getWarehouse_id() == null ? other.getWarehouse_id() == null : this.getWarehouse_id().equals(other.getWarehouse_id()))
            && (this.getShipping_app_id() == null ? other.getShipping_app_id() == null : this.getShipping_app_id().equals(other.getShipping_app_id()))
            && (this.getSf_monthly_balance_account() == null ? other.getSf_monthly_balance_account() == null : this.getSf_monthly_balance_account().equals(other.getSf_monthly_balance_account()))
            && (this.getSf_payment_method() == null ? other.getSf_payment_method() == null : this.getSf_payment_method().equals(other.getSf_payment_method()))
            && (this.getSf_tp_areacode() == null ? other.getSf_tp_areacode() == null : this.getSf_tp_areacode().equals(other.getSf_tp_areacode()))
            && (this.getPaper_type() == null ? other.getPaper_type() == null : this.getPaper_type().equals(other.getPaper_type()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMapping_id() == null) ? 0 : getMapping_id().hashCode());
        result = prime * result + ((getShipping_id() == null) ? 0 : getShipping_id().hashCode());
        result = prime * result + ((getWarehouse_id() == null) ? 0 : getWarehouse_id().hashCode());
        result = prime * result + ((getShipping_app_id() == null) ? 0 : getShipping_app_id().hashCode());
        result = prime * result + ((getSf_monthly_balance_account() == null) ? 0 : getSf_monthly_balance_account().hashCode());
        result = prime * result + ((getSf_payment_method() == null) ? 0 : getSf_payment_method().hashCode());
        result = prime * result + ((getSf_tp_areacode() == null) ? 0 : getSf_tp_areacode().hashCode());
        result = prime * result + ((getPaper_type() == null) ? 0 : getPaper_type().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}