package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class InventorySyncRecord implements Serializable {
    private Integer inventory_sync_record;

    private Integer inventory_item_detail_id;

    private Integer customer_id;

    private Date created_stamp;

    private static final long serialVersionUID = 1L;

    public Integer getInventory_sync_record() {
        return inventory_sync_record;
    }

    public void setInventory_sync_record(Integer inventory_sync_record) {
        this.inventory_sync_record = inventory_sync_record;
    }

    public Integer getInventory_item_detail_id() {
        return inventory_item_detail_id;
    }

    public void setInventory_item_detail_id(Integer inventory_item_detail_id) {
        this.inventory_item_detail_id = inventory_item_detail_id;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public Date getCreated_stamp() {
        return created_stamp;
    }

    public void setCreated_stamp(Date created_stamp) {
        this.created_stamp = created_stamp;
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
        InventorySyncRecord other = (InventorySyncRecord) that;
        return (this.getInventory_sync_record() == null ? other.getInventory_sync_record() == null : this.getInventory_sync_record().equals(other.getInventory_sync_record()))
            && (this.getInventory_item_detail_id() == null ? other.getInventory_item_detail_id() == null : this.getInventory_item_detail_id().equals(other.getInventory_item_detail_id()))
            && (this.getCustomer_id() == null ? other.getCustomer_id() == null : this.getCustomer_id().equals(other.getCustomer_id()))
            && (this.getCreated_stamp() == null ? other.getCreated_stamp() == null : this.getCreated_stamp().equals(other.getCreated_stamp()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getInventory_sync_record() == null) ? 0 : getInventory_sync_record().hashCode());
        result = prime * result + ((getInventory_item_detail_id() == null) ? 0 : getInventory_item_detail_id().hashCode());
        result = prime * result + ((getCustomer_id() == null) ? 0 : getCustomer_id().hashCode());
        result = prime * result + ((getCreated_stamp() == null) ? 0 : getCreated_stamp().hashCode());
        return result;
    }
}