package com.leqee.wms.entity;

import java.io.Serializable;


public class OrderReserveInventoryMapping implements Serializable {
    private Integer mapping_id;

    private Integer order_goods_id;

    private Integer inventory_item_id;

    private Integer quantity;
    
    private String oms_order_goods_sn;

	private Integer  order_id;
    
    private Integer order_goods_oms_id;
    
    private Integer product_id;

    public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

	public Integer getOrder_goods_oms_id() {
		return order_goods_oms_id;
	}

	public void setOrder_goods_oms_id(Integer order_goods_oms_id) {
		this.order_goods_oms_id = order_goods_oms_id;
	}

	public String getOms_order_goods_sn() {
		return oms_order_goods_sn;
	}

	public void setOms_order_goods_sn(String oms_order_goods_sn) {
		this.oms_order_goods_sn = oms_order_goods_sn;
	}

	private static final long serialVersionUID = 1L;

    public OrderReserveInventoryMapping(Integer mapping_id, Integer order_goods_id, Integer inventory_item_id, Integer quantity) {
        this.mapping_id = mapping_id;
        this.order_goods_id = order_goods_id;
        this.inventory_item_id = inventory_item_id;
        this.quantity = quantity;
    }

    public OrderReserveInventoryMapping() {
        super();
    }

    public Integer getMapping_id() {
        return mapping_id;
    }

    public void setMapping_id(Integer mapping_id) {
        this.mapping_id = mapping_id;
    }

    public Integer getOrder_goods_id() {
        return order_goods_id;
    }

    public void setOrder_goods_id(Integer order_goods_id) {
        this.order_goods_id = order_goods_id;
    }

    public Integer getInventory_item_id() {
        return inventory_item_id;
    }

    public void setInventory_item_id(Integer inventory_item_id) {
        this.inventory_item_id = inventory_item_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
        OrderReserveInventoryMapping other = (OrderReserveInventoryMapping) that;
        return (this.getMapping_id() == null ? other.getMapping_id() == null : this.getMapping_id().equals(other.getMapping_id()))
            && (this.getOrder_goods_id() == null ? other.getOrder_goods_id() == null : this.getOrder_goods_id().equals(other.getOrder_goods_id()))
            && (this.getInventory_item_id() == null ? other.getInventory_item_id() == null : this.getInventory_item_id().equals(other.getInventory_item_id()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMapping_id() == null) ? 0 : getMapping_id().hashCode());
        result = prime * result + ((getOrder_goods_id() == null) ? 0 : getOrder_goods_id().hashCode());
        result = prime * result + ((getInventory_item_id() == null) ? 0 : getInventory_item_id().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        return result;
    }
}