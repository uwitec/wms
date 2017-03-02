package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class OrderReserveDetail implements Serializable {
    private Integer order_goods_id;

    private Integer order_id;

    private Integer warehouse_id;

    private Integer product_id;

    private Integer goods_number;

    private Integer reserved_number;

    private String inventory_status;

    private String status;

    private Date reserved_time;

    private Date created_time;
    
    private Integer order_goods_oms_id;

    public Integer getOrder_goods_oms_id() {
		return order_goods_oms_id;
	}

	public void setOrder_goods_oms_id(Integer order_goods_oms_id) {
		this.order_goods_oms_id = order_goods_oms_id;
	}

	private static final long serialVersionUID = 1L;

    public Integer getOrder_goods_id() {
        return order_goods_id;
    }

    public void setOrder_goods_id(Integer order_goods_id) {
        this.order_goods_id = order_goods_id;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
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

    public Integer getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(Integer goods_number) {
        this.goods_number = goods_number;
    }

    public Integer getReserved_number() {
        return reserved_number;
    }

    public void setReserved_number(Integer reserved_number) {
        this.reserved_number = reserved_number;
    }

    public String getInventory_status() {
        return inventory_status;
    }

    public void setInventory_status(String inventory_status) {
        this.inventory_status = inventory_status == null ? null : inventory_status.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getReserved_time() {
        return reserved_time;
    }

    public void setReserved_time(Date reserved_time) {
        this.reserved_time = reserved_time;
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
        OrderReserveDetail other = (OrderReserveDetail) that;
        return (this.getOrder_goods_id() == null ? other.getOrder_goods_id() == null : this.getOrder_goods_id().equals(other.getOrder_goods_id()))
            && (this.getOrder_id() == null ? other.getOrder_id() == null : this.getOrder_id().equals(other.getOrder_id()))
            && (this.getWarehouse_id() == null ? other.getWarehouse_id() == null : this.getWarehouse_id().equals(other.getWarehouse_id()))
            && (this.getProduct_id() == null ? other.getProduct_id() == null : this.getProduct_id().equals(other.getProduct_id()))
            && (this.getGoods_number() == null ? other.getGoods_number() == null : this.getGoods_number().equals(other.getGoods_number()))
            && (this.getReserved_number() == null ? other.getReserved_number() == null : this.getReserved_number().equals(other.getReserved_number()))
            && (this.getInventory_status() == null ? other.getInventory_status() == null : this.getInventory_status().equals(other.getInventory_status()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getReserved_time() == null ? other.getReserved_time() == null : this.getReserved_time().equals(other.getReserved_time()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOrder_goods_id() == null) ? 0 : getOrder_goods_id().hashCode());
        result = prime * result + ((getOrder_id() == null) ? 0 : getOrder_id().hashCode());
        result = prime * result + ((getWarehouse_id() == null) ? 0 : getWarehouse_id().hashCode());
        result = prime * result + ((getProduct_id() == null) ? 0 : getProduct_id().hashCode());
        result = prime * result + ((getGoods_number() == null) ? 0 : getGoods_number().hashCode());
        result = prime * result + ((getReserved_number() == null) ? 0 : getReserved_number().hashCode());
        result = prime * result + ((getInventory_status() == null) ? 0 : getInventory_status().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getReserved_time() == null) ? 0 : getReserved_time().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}