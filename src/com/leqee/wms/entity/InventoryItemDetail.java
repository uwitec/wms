package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class InventoryItemDetail implements Serializable {
    private Integer inventory_item_detail_id;

    private Integer inventory_item_id;

    private Integer product_id;

    private Integer change_quantity;

    private Integer order_id;

    private Integer order_goods_id;
    
    private Integer customer_id;

    private Integer warehouse_id;

    private Integer packbox_customer_id;

    private Integer packbox_warehouse_id;

    private String created_user;

    private Date created_time;

    private String last_updated_user;

    private Date last_updated_time;
    
    private InventoryItem inventoryItem;
    
    private int order_prepack_id=0;

    private String oms_order_goods_sn;
    
    private Integer order_goods_oms_id;
    
    private static final long serialVersionUID = 1L;

    public Integer getInventory_item_detail_id() {
        return inventory_item_detail_id;
    }

    public void setInventory_item_detail_id(Integer inventory_item_detail_id) {
        this.inventory_item_detail_id = inventory_item_detail_id;
    }

    public Integer getInventory_item_id() {
        return inventory_item_id;
    }

    public void setInventory_item_id(Integer inventory_item_id) {
        this.inventory_item_id = inventory_item_id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public Integer getChange_quantity() {
        return change_quantity;
    }

    public void setChange_quantity(Integer change_quantity) {
        this.change_quantity = change_quantity;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getOrder_goods_id() {
        return order_goods_id;
    }

    public void setOrder_goods_id(Integer order_goods_id) {
        this.order_goods_id = order_goods_id;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public Integer getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public Integer getPackbox_customer_id() {
		return packbox_customer_id;
	}

	public void setPackbox_customer_id(Integer packbox_customer_id) {
		this.packbox_customer_id = packbox_customer_id;
	}

	public Integer getPackbox_warehouse_id() {
        return packbox_warehouse_id;
    }

    public void setPackbox_warehouse_id(Integer packbox_warehouse_id) {
        this.packbox_warehouse_id = packbox_warehouse_id;
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

    public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	
	public int getOrder_prepack_id() {
		return order_prepack_id;
	}

	public void setOrder_prepack_id(int order_prepack_id) {
		this.order_prepack_id = order_prepack_id;
	}

	public String getOms_order_goods_sn() {
		return oms_order_goods_sn;
	}

	public void setOms_order_goods_sn(String oms_order_goods_sn) {
		this.oms_order_goods_sn = oms_order_goods_sn;
	}

	public Integer getOrder_goods_oms_id() {
		return order_goods_oms_id;
	}

	public void setOrder_goods_oms_id(Integer order_goods_oms_id) {
		this.order_goods_oms_id = order_goods_oms_id;
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
        InventoryItemDetail other = (InventoryItemDetail) that;
        return (this.getInventory_item_detail_id() == null ? other.getInventory_item_detail_id() == null : this.getInventory_item_detail_id().equals(other.getInventory_item_detail_id()))
            && (this.getInventory_item_id() == null ? other.getInventory_item_id() == null : this.getInventory_item_id().equals(other.getInventory_item_id()))
            && (this.getProduct_id() == null ? other.getProduct_id() == null : this.getProduct_id().equals(other.getProduct_id()))
            && (this.getChange_quantity() == null ? other.getChange_quantity() == null : this.getChange_quantity().equals(other.getChange_quantity()))
            && (this.getOrder_id() == null ? other.getOrder_id() == null : this.getOrder_id().equals(other.getOrder_id()))
            && (this.getOrder_goods_id() == null ? other.getOrder_goods_id() == null : this.getOrder_goods_id().equals(other.getOrder_goods_id()))
            && (this.getCustomer_id() == null ? other.getCustomer_id() == null : this.getCustomer_id().equals(other.getCustomer_id()))
            && (this.getWarehouse_id() == null ? other.getWarehouse_id() == null : this.getWarehouse_id().equals(other.getWarehouse_id()))
            && (this.getPackbox_customer_id() == null ? other.getPackbox_customer_id() == null : this.getPackbox_customer_id().equals(other.getPackbox_customer_id()))
            && (this.getPackbox_warehouse_id() == null ? other.getPackbox_warehouse_id() == null : this.getPackbox_warehouse_id().equals(other.getPackbox_warehouse_id()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()))
            && (this.getLast_updated_user() == null ? other.getLast_updated_user() == null : this.getLast_updated_user().equals(other.getLast_updated_user()))
            && (this.getLast_updated_time() == null ? other.getLast_updated_time() == null : this.getLast_updated_time().equals(other.getLast_updated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getInventory_item_detail_id() == null) ? 0 : getInventory_item_detail_id().hashCode());
        result = prime * result + ((getInventory_item_id() == null) ? 0 : getInventory_item_id().hashCode());
        result = prime * result + ((getProduct_id() == null) ? 0 : getProduct_id().hashCode());
        result = prime * result + ((getChange_quantity() == null) ? 0 : getChange_quantity().hashCode());
        result = prime * result + ((getOrder_id() == null) ? 0 : getOrder_id().hashCode());
        result = prime * result + ((getOrder_goods_id() == null) ? 0 : getOrder_goods_id().hashCode());
        result = prime * result + ((getCustomer_id() == null) ? 0 : getCustomer_id().hashCode());
        result = prime * result + ((getWarehouse_id() == null) ? 0 : getWarehouse_id().hashCode());
        result = prime * result + ((getPackbox_customer_id() == null) ? 0 : getPackbox_customer_id().hashCode());
        result = prime * result + ((getPackbox_warehouse_id() == null) ? 0 : getPackbox_warehouse_id().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        result = prime * result + ((getLast_updated_user() == null) ? 0 : getLast_updated_user().hashCode());
        result = prime * result + ((getLast_updated_time() == null) ? 0 : getLast_updated_time().hashCode());
        return result;
    }
}