package com.leqee.wms.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class InventoryItem implements Serializable {
    private Integer inventory_item_id;

    private Integer physical_warehouse_id;

    private Integer warehouse_id;

    private Integer customer_id;

    private Integer product_id;

    private Integer quantity;

    private String status;

    private BigDecimal unit_cost;

    private String provider_code;

    private Date validity;

    private String batch_sn;

    private String serial_number;

    private String inventory_item_acct_type_id;

    private String currency;

    private Integer parent_inventory_item_id;

    private Integer root_inventory_item_id;

    private String created_user;

    private Date created_time;

    private String last_updated_user;

    private Date last_updated_time;

    private static final long serialVersionUID = 1L;

    public Integer getInventory_item_id() {
        return inventory_item_id;
    }

    public void setInventory_item_id(Integer inventory_item_id) {
        this.inventory_item_id = inventory_item_id;
    }

    public Integer getPhysical_warehouse_id() {
        return physical_warehouse_id;
    }

    public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
        this.physical_warehouse_id = physical_warehouse_id;
    }

    public Integer getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public BigDecimal getUnit_cost() {
        return unit_cost;
    }

    public void setUnit_cost(BigDecimal unit_cost) {
        this.unit_cost = unit_cost;
    }

    public String getProvider_code() {
        return provider_code;
    }

    public void setProvider_code(String provider_code) {
        this.provider_code = provider_code == null ? null : provider_code.trim();
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public String getBatch_sn() {
        return batch_sn;
    }

    public void setBatch_sn(String batch_sn) {
        this.batch_sn = batch_sn == null ? null : batch_sn.trim();
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number == null ? null : serial_number.trim();
    }

    public String getInventory_item_acct_type_id() {
        return inventory_item_acct_type_id;
    }

    public void setInventory_item_acct_type_id(String inventory_item_acct_type_id) {
        this.inventory_item_acct_type_id = inventory_item_acct_type_id == null ? null : inventory_item_acct_type_id.trim();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    public Integer getParent_inventory_item_id() {
        return parent_inventory_item_id;
    }

    public void setParent_inventory_item_id(Integer parent_inventory_item_id) {
        this.parent_inventory_item_id = parent_inventory_item_id;
    }

    public Integer getRoot_inventory_item_id() {
        return root_inventory_item_id;
    }

    public void setRoot_inventory_item_id(Integer root_inventory_item_id) {
        this.root_inventory_item_id = root_inventory_item_id;
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

    public String getLast_updated_user() {
        return last_updated_user;
    }

    public void setLast_updated_user(String last_updated_user) {
        this.last_updated_user = last_updated_user == null ? null : last_updated_user.trim();
    }

    public Date getLast_updated_time() {
        return last_updated_time;
    }

    public void setLast_updated_time(Date last_updated_time) {
        this.last_updated_time = last_updated_time;
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
        InventoryItem other = (InventoryItem) that;
        return (this.getInventory_item_id() == null ? other.getInventory_item_id() == null : this.getInventory_item_id().equals(other.getInventory_item_id()))
            && (this.getPhysical_warehouse_id() == null ? other.getPhysical_warehouse_id() == null : this.getPhysical_warehouse_id().equals(other.getPhysical_warehouse_id()))
            && (this.getWarehouse_id() == null ? other.getWarehouse_id() == null : this.getWarehouse_id().equals(other.getWarehouse_id()))
            && (this.getCustomer_id() == null ? other.getCustomer_id() == null : this.getCustomer_id().equals(other.getCustomer_id()))
            && (this.getProduct_id() == null ? other.getProduct_id() == null : this.getProduct_id().equals(other.getProduct_id()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getUnit_cost() == null ? other.getUnit_cost() == null : this.getUnit_cost().equals(other.getUnit_cost()))
            && (this.getProvider_code() == null ? other.getProvider_code() == null : this.getProvider_code().equals(other.getProvider_code()))
            && (this.getValidity() == null ? other.getValidity() == null : this.getValidity().equals(other.getValidity()))
            && (this.getBatch_sn() == null ? other.getBatch_sn() == null : this.getBatch_sn().equals(other.getBatch_sn()))
            && (this.getSerial_number() == null ? other.getSerial_number() == null : this.getSerial_number().equals(other.getSerial_number()))
            && (this.getInventory_item_acct_type_id() == null ? other.getInventory_item_acct_type_id() == null : this.getInventory_item_acct_type_id().equals(other.getInventory_item_acct_type_id()))
            && (this.getCurrency() == null ? other.getCurrency() == null : this.getCurrency().equals(other.getCurrency()))
            && (this.getParent_inventory_item_id() == null ? other.getParent_inventory_item_id() == null : this.getParent_inventory_item_id().equals(other.getParent_inventory_item_id()))
            && (this.getRoot_inventory_item_id() == null ? other.getRoot_inventory_item_id() == null : this.getRoot_inventory_item_id().equals(other.getRoot_inventory_item_id()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()))
            && (this.getLast_updated_user() == null ? other.getLast_updated_user() == null : this.getLast_updated_user().equals(other.getLast_updated_user()))
            && (this.getLast_updated_time() == null ? other.getLast_updated_time() == null : this.getLast_updated_time().equals(other.getLast_updated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getInventory_item_id() == null) ? 0 : getInventory_item_id().hashCode());
        result = prime * result + ((getPhysical_warehouse_id() == null) ? 0 : getPhysical_warehouse_id().hashCode());
        result = prime * result + ((getWarehouse_id() == null) ? 0 : getWarehouse_id().hashCode());
        result = prime * result + ((getCustomer_id() == null) ? 0 : getCustomer_id().hashCode());
        result = prime * result + ((getProduct_id() == null) ? 0 : getProduct_id().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getUnit_cost() == null) ? 0 : getUnit_cost().hashCode());
        result = prime * result + ((getProvider_code() == null) ? 0 : getProvider_code().hashCode());
        result = prime * result + ((getValidity() == null) ? 0 : getValidity().hashCode());
        result = prime * result + ((getBatch_sn() == null) ? 0 : getBatch_sn().hashCode());
        result = prime * result + ((getSerial_number() == null) ? 0 : getSerial_number().hashCode());
        result = prime * result + ((getInventory_item_acct_type_id() == null) ? 0 : getInventory_item_acct_type_id().hashCode());
        result = prime * result + ((getCurrency() == null) ? 0 : getCurrency().hashCode());
        result = prime * result + ((getParent_inventory_item_id() == null) ? 0 : getParent_inventory_item_id().hashCode());
        result = prime * result + ((getRoot_inventory_item_id() == null) ? 0 : getRoot_inventory_item_id().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        result = prime * result + ((getLast_updated_user() == null) ? 0 : getLast_updated_user().hashCode());
        result = prime * result + ((getLast_updated_time() == null) ? 0 : getLast_updated_time().hashCode());
        return result;
    }
}