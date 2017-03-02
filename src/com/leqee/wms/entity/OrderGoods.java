package com.leqee.wms.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderGoods implements Serializable {
	
	public static final String ORDER_GOODS_TYPE_GOODS = "GOODS";
	public static final String ORDER_GOODS_TYPE_PACKBOX = "PACKBOX" ;
	public static final String ORDER_GOODS_TYPE_PREPACKAGE = "PREPACKAGE" ;
	
	
	
    private Integer order_goods_id;

    private Integer order_id;
    
    private String oms_order_goods_sn;
    
    private Integer order_goods_oms_id;

	private Integer warehouse_id;

    private Integer customer_id;

    private Integer product_id;

    private String goods_name;

    private Integer goods_number;

    private BigDecimal goods_price;

    private BigDecimal discount;

    private Integer inventory_change_number;

    private String batch_sn;

    private String status_id;

    private BigDecimal tax_rate;

    private String order_goods_type;

    private String created_user;

    private Date created_time;

    private String last_updated_user;

    private Date last_updated_time;
    
    //商品条码
    private String barcode;

	//辅助字段，不作为数据库字段
    private String is_serial;
    
    
    private String group_code; //  
    private Integer group_number; // 
    
    
    
    private static final long serialVersionUID = 1L;

    public Integer getOrder_goods_oms_id() {
		return order_goods_oms_id;
	}

	public void setOrder_goods_oms_id(Integer order_goods_oms_id) {
		this.order_goods_oms_id = order_goods_oms_id;
	}
    public Integer getOrder_goods_id() {
        return order_goods_id;
    }

    public void setOrder_goods_id(Integer order_goods_id) {
        this.order_goods_id = order_goods_id;
    }
    public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

	public String getOms_order_goods_sn() {
		return oms_order_goods_sn;
	}

	public void setOms_order_goods_sn(String oms_order_goods_sn) {
		this.oms_order_goods_sn = oms_order_goods_sn;
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

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name == null ? null : goods_name.trim();
    }

    public Integer getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(Integer goods_number) {
        this.goods_number = goods_number;
    }

    public BigDecimal getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(BigDecimal goods_price) {
        this.goods_price = goods_price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Integer getInventory_change_number() {
        return inventory_change_number;
    }

    public void setInventory_change_number(Integer inventory_change_number) {
        this.inventory_change_number = inventory_change_number;
    }

    public String getBatch_sn() {
        return batch_sn;
    }

    public void setBatch_sn(String batch_sn) {
        this.batch_sn = batch_sn == null ? null : batch_sn.trim();
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id == null ? null : status_id.trim();
    }

    public BigDecimal getTax_rate() {
        return tax_rate;
    }

    public void setTax_rate(BigDecimal tax_rate) {
        this.tax_rate = tax_rate;
    }

    public String getOrder_goods_type() {
        return order_goods_type;
    }

    public void setOrder_goods_type(String order_goods_type) {
        this.order_goods_type = order_goods_type == null ? null : order_goods_type.trim();
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
    

    public String getGroup_code() {
		return group_code;
	}

	public void setGroup_code(String group_code) {
		this.group_code = group_code;
	}

	public Integer getGroup_number() {
		return group_number;
	}

	public void setGroup_number(Integer group_number) {
		this.group_number = group_number;
	}

	public String getIs_serial() {
		return is_serial;
	}

	public void setIs_serial(String is_serial) {
		this.is_serial = is_serial;
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
        OrderGoods other = (OrderGoods) that;
        return (this.getOrder_goods_id() == null ? other.getOrder_goods_id() == null : this.getOrder_goods_id().equals(other.getOrder_goods_id()))
            && (this.getOrder_id() == null ? other.getOrder_id() == null : this.getOrder_id().equals(other.getOrder_id()))
            && (this.getWarehouse_id() == null ? other.getWarehouse_id() == null : this.getWarehouse_id().equals(other.getWarehouse_id()))
            && (this.getCustomer_id() == null ? other.getCustomer_id() == null : this.getCustomer_id().equals(other.getCustomer_id()))
            && (this.getProduct_id() == null ? other.getProduct_id() == null : this.getProduct_id().equals(other.getProduct_id()))
            && (this.getGoods_name() == null ? other.getGoods_name() == null : this.getGoods_name().equals(other.getGoods_name()))
            && (this.getGoods_number() == null ? other.getGoods_number() == null : this.getGoods_number().equals(other.getGoods_number()))
            && (this.getGoods_price() == null ? other.getGoods_price() == null : this.getGoods_price().equals(other.getGoods_price()))
            && (this.getDiscount() == null ? other.getDiscount() == null : this.getDiscount().equals(other.getDiscount()))
            && (this.getInventory_change_number() == null ? other.getInventory_change_number() == null : this.getInventory_change_number().equals(other.getInventory_change_number()))
            && (this.getBatch_sn() == null ? other.getBatch_sn() == null : this.getBatch_sn().equals(other.getBatch_sn()))
            && (this.getStatus_id() == null ? other.getStatus_id() == null : this.getStatus_id().equals(other.getStatus_id()))
            && (this.getTax_rate() == null ? other.getTax_rate() == null : this.getTax_rate().equals(other.getTax_rate()))
            && (this.getOrder_goods_type() == null ? other.getOrder_goods_type() == null : this.getOrder_goods_type().equals(other.getOrder_goods_type()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()))
            && (this.getLast_updated_user() == null ? other.getLast_updated_user() == null : this.getLast_updated_user().equals(other.getLast_updated_user()))
            && (this.getLast_updated_time() == null ? other.getLast_updated_time() == null : this.getLast_updated_time().equals(other.getLast_updated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOrder_goods_id() == null) ? 0 : getOrder_goods_id().hashCode());
        result = prime * result + ((getOrder_id() == null) ? 0 : getOrder_id().hashCode());
        result = prime * result + ((getWarehouse_id() == null) ? 0 : getWarehouse_id().hashCode());
        result = prime * result + ((getCustomer_id() == null) ? 0 : getCustomer_id().hashCode());
        result = prime * result + ((getProduct_id() == null) ? 0 : getProduct_id().hashCode());
        result = prime * result + ((getGoods_name() == null) ? 0 : getGoods_name().hashCode());
        result = prime * result + ((getGoods_number() == null) ? 0 : getGoods_number().hashCode());
        result = prime * result + ((getGoods_price() == null) ? 0 : getGoods_price().hashCode());
        result = prime * result + ((getDiscount() == null) ? 0 : getDiscount().hashCode());
        result = prime * result + ((getInventory_change_number() == null) ? 0 : getInventory_change_number().hashCode());
        result = prime * result + ((getBatch_sn() == null) ? 0 : getBatch_sn().hashCode());
        result = prime * result + ((getStatus_id() == null) ? 0 : getStatus_id().hashCode());
        result = prime * result + ((getTax_rate() == null) ? 0 : getTax_rate().hashCode());
        result = prime * result + ((getOrder_goods_type() == null) ? 0 : getOrder_goods_type().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        result = prime * result + ((getLast_updated_user() == null) ? 0 : getLast_updated_user().hashCode());
        result = prime * result + ((getLast_updated_time() == null) ? 0 : getLast_updated_time().hashCode());
        return result;
    }
}