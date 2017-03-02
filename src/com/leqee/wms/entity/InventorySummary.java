package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class InventorySummary implements Serializable {
    private Integer inventory_summary_id;

    private String serial_number;

    private String status_id;

    private Integer warehouse_id;

    private Integer product_id;

    private Integer customer_id;

    private Integer stock_quantity;

    private Integer available_to_reserved;

    private String created_user;

    private Date created_stamp;

    private String last_updated_user;

    private Date last_updated_stamp;

    private static final long serialVersionUID = 1L;

    public Integer getInventory_summary_id() {
        return inventory_summary_id;
    }

    public void setInventory_summary_id(Integer inventory_summary_id) {
        this.inventory_summary_id = inventory_summary_id;
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



    public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	public Integer getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(Integer stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public Integer getAvailable_to_reserved() {
        return available_to_reserved;
    }

    public void setAvailable_to_reserved(Integer available_to_reserved) {
        this.available_to_reserved = available_to_reserved;
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
	public String toString() {
		return "InventorySummary [inventory_summary_id=" + inventory_summary_id
				+ ", serial_number=" + serial_number + ", status_id="
				+ status_id + ", warehouse_id=" + warehouse_id
				+ ", product_id=" + product_id + ", customer_id=" + customer_id
				+ ", stock_quantity=" + stock_quantity
				+ ", available_to_reserved=" + available_to_reserved
				+ ", created_user=" + created_user + ", created_stamp="
				+ created_stamp + ", last_updated_user=" + last_updated_user
				+ ", last_updated_stamp=" + last_updated_stamp + "]";
	}

   }