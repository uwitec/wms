package com.leqee.wms.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class InventorySummaryDetail implements Serializable {
    
	private Integer inventory_summary_detail_id;

    private String serial_number;

    private String status_id;

    private Integer warehouse_id;

    private Integer product_id;

    private BigDecimal unit_price;

    private String provider_code;

    private String batch_sn;
    
    private Integer customer_id;

    private Integer stock_quantity;

    private String created_user;

    private Date created_stamp;

    private String last_updated_user;

    private Date last_updated_stamp;

	private static final long serialVersionUID = 1L;
    
    public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}



    public Integer getInventory_summary_detail_id() {
        return inventory_summary_detail_id;
    }

    public void setInventory_summary_detail_id(Integer inventory_summary_detail_id) {
        this.inventory_summary_detail_id = inventory_summary_detail_id;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number == null ? null : serial_number.trim();
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id == null ? null : status_id.trim();
    }

    public Integer getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public BigDecimal getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(BigDecimal unit_price) {
        this.unit_price = unit_price;
    }

    public String getProvider_code() {
        return provider_code;
    }

    public void setProvider_code(String provider_code) {
        this.provider_code = provider_code == null ? null : provider_code.trim();
    }

    public String getBatch_sn() {
        return batch_sn;
    }

    public void setBatch_sn(String batch_sn) {
        this.batch_sn = batch_sn == null ? null : batch_sn.trim();
    }

    public Integer getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(Integer stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public String getCreated_user() {
        return created_user;
    }

    public void setCreated_user(String created_user) {
        this.created_user = created_user == null ? null : created_user.trim();
    }

    public Date getCreated_stamp() {
        return created_stamp;
    }

    public void setCreated_stamp(Date created_stamp) {
        this.created_stamp = created_stamp;
    }

    public String getLast_updated_user() {
        return last_updated_user;
    }

    public void setLast_updated_user(String last_updated_user) {
        this.last_updated_user = last_updated_user == null ? null : last_updated_user.trim();
    }

    public Date getLast_updated_stamp() {
        return last_updated_stamp;
    }

    public void setLast_updated_stamp(Date last_updated_stamp) {
        this.last_updated_stamp = last_updated_stamp;
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
        InventorySummaryDetail other = (InventorySummaryDetail) that;
        return (this.getInventory_summary_detail_id() == null ? other.getInventory_summary_detail_id() == null : this.getInventory_summary_detail_id().equals(other.getInventory_summary_detail_id()))
            && (this.getSerial_number() == null ? other.getSerial_number() == null : this.getSerial_number().equals(other.getSerial_number()))
            && (this.getStatus_id() == null ? other.getStatus_id() == null : this.getStatus_id().equals(other.getStatus_id()))
            && (this.getWarehouse_id() == null ? other.getWarehouse_id() == null : this.getWarehouse_id().equals(other.getWarehouse_id()))
            && (this.getProduct_id() == null ? other.getProduct_id() == null : this.getProduct_id().equals(other.getProduct_id()))
            && (this.getUnit_price() == null ? other.getUnit_price() == null : this.getUnit_price().equals(other.getUnit_price()))
            && (this.getProvider_code() == null ? other.getProvider_code() == null : this.getProvider_code().equals(other.getProvider_code()))
            && (this.getBatch_sn() == null ? other.getBatch_sn() == null : this.getBatch_sn().equals(other.getBatch_sn()))
            && (this.getStock_quantity() == null ? other.getStock_quantity() == null : this.getStock_quantity().equals(other.getStock_quantity()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_stamp() == null ? other.getCreated_stamp() == null : this.getCreated_stamp().equals(other.getCreated_stamp()))
            && (this.getLast_updated_user() == null ? other.getLast_updated_user() == null : this.getLast_updated_user().equals(other.getLast_updated_user()))
            && (this.getLast_updated_stamp() == null ? other.getLast_updated_stamp() == null : this.getLast_updated_stamp().equals(other.getLast_updated_stamp()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getInventory_summary_detail_id() == null) ? 0 : getInventory_summary_detail_id().hashCode());
        result = prime * result + ((getSerial_number() == null) ? 0 : getSerial_number().hashCode());
        result = prime * result + ((getStatus_id() == null) ? 0 : getStatus_id().hashCode());
        result = prime * result + ((getWarehouse_id() == null) ? 0 : getWarehouse_id().hashCode());
        result = prime * result + ((getProduct_id() == null) ? 0 : getProduct_id().hashCode());
        result = prime * result + ((getUnit_price() == null) ? 0 : getUnit_price().hashCode());
        result = prime * result + ((getProvider_code() == null) ? 0 : getProvider_code().hashCode());
        result = prime * result + ((getBatch_sn() == null) ? 0 : getBatch_sn().hashCode());
        result = prime * result + ((getStock_quantity() == null) ? 0 : getStock_quantity().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_stamp() == null) ? 0 : getCreated_stamp().hashCode());
        result = prime * result + ((getLast_updated_user() == null) ? 0 : getLast_updated_user().hashCode());
        result = prime * result + ((getLast_updated_stamp() == null) ? 0 : getLast_updated_stamp().hashCode());
        return result;
    }

	@Override
	public String toString() {
		return "InventorySummaryDetail [inventory_summary_detail_id="
				+ inventory_summary_detail_id + ", serial_number="
				+ serial_number + ", status_id=" + status_id
				+ ", warehouse_id=" + warehouse_id + ", product_id="
				+ product_id + ", unit_price=" + unit_price
				+ ", provider_code=" + provider_code + ", batch_sn=" + batch_sn
				+ ", customer_id=" + customer_id + ", stock_quantity="
				+ stock_quantity + ", created_user=" + created_user
				+ ", created_stamp=" + created_stamp + ", last_updated_user="
				+ last_updated_user + ", last_updated_stamp="
				+ last_updated_stamp + "]";
	}

	
}