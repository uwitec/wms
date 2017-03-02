package com.leqee.wms.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Product implements Serializable {
    private Integer product_id;

    private String brand_name;

    private String barcode;

    private String sku_code;

    private String product_name;

    private String cat_name;

    private Integer customer_id;

    private BigDecimal volume;

    private Integer length;

    private Integer width;

    private Integer height;

    private BigDecimal weight;

    private Integer spec;
    
    private BigDecimal unit_price;

    private Integer validity=0;

    private String validity_unit="MONTH";

    private String is_delete;

    private String is_maintain_weight;

    private String is_maintain_warranty;

    private String is_maintain_batch_sn;

    private String is_contraband;

    private String is_serial;

    private String product_type ;  // 商品类型:GOODS(普通商品)，PACKBOX(耗材商品)
    
    private String created_user;

    private Date created_time;

    private String last_updated_user;

    private Date last_updated_time;
    
    private String is_high_price;
    
    private int box_pick_start_number=9999;

    private Integer ti;
    
    private Integer hi;
    
    private String is_maintain_guarantee;
    
    private BigDecimal receive_rule;
    
    private BigDecimal replenishment_rule;
    
    private BigDecimal stock_allocation_rule;
    
    private String is_fragile;
    
    private String is_three_c;
    
    private String is_maintain_virtual_stock;
    
    private Integer virtual_stock;
    
    private String is_need_transfer_code;   //add by xhchen 
    
    private Integer warranty_warning_days;
    
    private Integer warranty_unsalable_days;
    
    private static final long serialVersionUID = 1L;

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name == null ? null : brand_name.trim();
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode == null ? null : barcode.trim();
    }

    public String getSku_code() {
        return sku_code;
    }

    public void setSku_code(String sku_code) {
        this.sku_code = sku_code == null ? null : sku_code.trim();
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name == null ? null : product_name.trim();
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name == null ? null : cat_name.trim();
    }

    public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(BigDecimal unit_price) {
        this.unit_price = unit_price;
    }

   
    public Integer getValidity() {
		return validity;
	}

	public void setValidity(Integer validity) {
		this.validity = validity;
	}

	public String getValidity_unit() {
        return validity_unit;
    }

    public void setValidity_unit(String validity_unit) {
        this.validity_unit = validity_unit == null ? null : validity_unit.trim();
    }

    public String getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(String is_delete) {
        this.is_delete = is_delete == null ? null : is_delete.trim();
    }

    public String getIs_maintain_weight() {
        return is_maintain_weight;
    }

    public void setIs_maintain_weight(String is_maintain_weight) {
        this.is_maintain_weight = is_maintain_weight == null ? null : is_maintain_weight.trim();
    }

    public String getIs_maintain_warranty() {
        return is_maintain_warranty;
    }

    public void setIs_maintain_warranty(String is_maintain_warranty) {
        this.is_maintain_warranty = is_maintain_warranty == null ? null : is_maintain_warranty.trim();
    }

    public String getIs_maintain_batch_sn() {
        return is_maintain_batch_sn;
    }

    public void setIs_maintain_batch_sn(String is_maintain_batch_sn) {
        this.is_maintain_batch_sn = is_maintain_batch_sn == null ? null : is_maintain_batch_sn.trim();
    }

    public String getIs_contraband() {
        return is_contraband;
    }

    public void setIs_contraband(String is_contraband) {
        this.is_contraband = is_contraband == null ? null : is_contraband.trim();
    }

    public String getIs_serial() {
        return is_serial;
    }

    public void setIs_serial(String is_serial) {
        this.is_serial = is_serial == null ? null : is_serial.trim();
    }

    public String getProduct_type() {
		return product_type;
	}

	public void setProduct_type(String product_type) {
		this.product_type = product_type;
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

    public Integer getSpec() {
		return spec;
	}

	public void setSpec(Integer spec) {
		this.spec = spec;
	}

	public int getBox_pick_start_number() {
		return box_pick_start_number;
	}

	public void setBox_pick_start_number(int box_pick_start_number) {
		this.box_pick_start_number = box_pick_start_number;
	}

	public String getIs_high_price() {
		return is_high_price;
	}

	public void setIs_high_price(String is_high_price) {
		this.is_high_price = is_high_price;
	}

	public Integer getTi() {
		return ti;
	}

	public void setTi(Integer ti) {
		this.ti = ti;
	}

	public Integer getHi() {
		return hi;
	}

	public void setHi(Integer hi) {
		this.hi = hi;
	}

	public String getIs_maintain_guarantee() {
		return is_maintain_guarantee;
	}

	public void setIs_maintain_guarantee(String is_maintain_guarantee) {
		this.is_maintain_guarantee = is_maintain_guarantee;
	}

	public BigDecimal getReceive_rule() {
		return receive_rule;
	}

	public void setReceive_rule(BigDecimal receive_rule) {
		this.receive_rule = receive_rule;
	}

	public BigDecimal getReplenishment_rule() {
		return replenishment_rule;
	}

	public void setReplenishment_rule(BigDecimal replenishment_rule) {
		this.replenishment_rule = replenishment_rule;
	}

	public BigDecimal getStock_allocation_rule() {
		return stock_allocation_rule;
	}

	public void setStock_allocation_rule(BigDecimal stock_allocation_rule) {
		this.stock_allocation_rule = stock_allocation_rule;
	}

	public String getIs_fragile() {
		return is_fragile;
	}

	public void setIs_fragile(String is_fragile) {
		this.is_fragile = is_fragile;
	}

	public String getIs_three_c() {
		return is_three_c;
	}

	public void setIs_three_c(String is_three_c) {
		this.is_three_c = is_three_c;
	}

	public String getIs_maintain_virtual_stock() {
		return is_maintain_virtual_stock;
	}

	public void setIs_maintain_virtual_stock(String is_maintain_virtual_stock) {
		this.is_maintain_virtual_stock = is_maintain_virtual_stock;
	}

	public Integer getVirtual_stock() {
		return virtual_stock;
	}

	public void setVirtual_stock(Integer virtual_stock) {
		this.virtual_stock = virtual_stock;
	}
	
	public String getIs_need_transfer_code() {
		return is_need_transfer_code;
	}

	public void setIs_need_transfer_code(String is_need_transfer_code) {
		this.is_need_transfer_code = is_need_transfer_code;
	}
	
	public Integer getWarranty_warning_days() {
		return warranty_warning_days;
	}

	public void setWarranty_warning_days(Integer warranty_warning_days) {
		this.warranty_warning_days = warranty_warning_days;
	}

	public Integer getWarranty_unsalable_days() {
		return warranty_unsalable_days;
	}

	public void setWarranty_unsalable_days(Integer warranty_unsalable_days) {
		this.warranty_unsalable_days = warranty_unsalable_days;
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
        Product other = (Product) that;
        return (this.getProduct_id() == null ? other.getProduct_id() == null : this.getProduct_id().equals(other.getProduct_id()))
            && (this.getBrand_name() == null ? other.getBrand_name() == null : this.getBrand_name().equals(other.getBrand_name()))
            && (this.getBarcode() == null ? other.getBarcode() == null : this.getBarcode().equals(other.getBarcode()))
            && (this.getSku_code() == null ? other.getSku_code() == null : this.getSku_code().equals(other.getSku_code()))
            && (this.getProduct_name() == null ? other.getProduct_name() == null : this.getProduct_name().equals(other.getProduct_name()))
            && (this.getCat_name() == null ? other.getCat_name() == null : this.getCat_name().equals(other.getCat_name()))
            && (this.getCustomer_id() == null ? other.getCustomer_id() == null : this.getCustomer_id().equals(other.getCustomer_id()))
            && (this.getVolume() == null ? other.getVolume() == null : this.getVolume().equals(other.getVolume()))
            && (this.getLength() == null ? other.getLength() == null : this.getLength().equals(other.getLength()))
            && (this.getWidth() == null ? other.getWidth() == null : this.getWidth().equals(other.getWidth()))
            && (this.getHeight() == null ? other.getHeight() == null : this.getHeight().equals(other.getHeight()))
            && (this.getWeight() == null ? other.getWeight() == null : this.getWeight().equals(other.getWeight()))
            && (this.getUnit_price() == null ? other.getUnit_price() == null : this.getUnit_price().equals(other.getUnit_price()))
            && (this.getValidity() == null ? other.getValidity() == null : this.getValidity().equals(other.getValidity()))
            && (this.getValidity_unit() == null ? other.getValidity_unit() == null : this.getValidity_unit().equals(other.getValidity_unit()))
            && (this.getIs_delete() == null ? other.getIs_delete() == null : this.getIs_delete().equals(other.getIs_delete()))
            && (this.getIs_maintain_weight() == null ? other.getIs_maintain_weight() == null : this.getIs_maintain_weight().equals(other.getIs_maintain_weight()))
            && (this.getIs_maintain_warranty() == null ? other.getIs_maintain_warranty() == null : this.getIs_maintain_warranty().equals(other.getIs_maintain_warranty()))
            && (this.getIs_maintain_batch_sn() == null ? other.getIs_maintain_batch_sn() == null : this.getIs_maintain_batch_sn().equals(other.getIs_maintain_batch_sn()))
            && (this.getIs_contraband() == null ? other.getIs_contraband() == null : this.getIs_contraband().equals(other.getIs_contraband()))
            && (this.getIs_serial() == null ? other.getIs_serial() == null : this.getIs_serial().equals(other.getIs_serial()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()))
            && (this.getLast_updated_user() == null ? other.getLast_updated_user() == null : this.getLast_updated_user().equals(other.getLast_updated_user()))
            && (this.getLast_updated_time() == null ? other.getLast_updated_time() == null : this.getLast_updated_time().equals(other.getLast_updated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getProduct_id() == null) ? 0 : getProduct_id().hashCode());
        result = prime * result + ((getBrand_name() == null) ? 0 : getBrand_name().hashCode());
        result = prime * result + ((getBarcode() == null) ? 0 : getBarcode().hashCode());
        result = prime * result + ((getSku_code() == null) ? 0 : getSku_code().hashCode());
        result = prime * result + ((getProduct_name() == null) ? 0 : getProduct_name().hashCode());
        result = prime * result + ((getCat_name() == null) ? 0 : getCat_name().hashCode());
        result = prime * result + ((getCustomer_id() == null) ? 0 : getCustomer_id().hashCode());
        result = prime * result + ((getVolume() == null) ? 0 : getVolume().hashCode());
        result = prime * result + ((getLength() == null) ? 0 : getLength().hashCode());
        result = prime * result + ((getWidth() == null) ? 0 : getWidth().hashCode());
        result = prime * result + ((getHeight() == null) ? 0 : getHeight().hashCode());
        result = prime * result + ((getWeight() == null) ? 0 : getWeight().hashCode());
        result = prime * result + ((getUnit_price() == null) ? 0 : getUnit_price().hashCode());
        result = prime * result + ((getValidity() == null) ? 0 : getValidity().hashCode());
        result = prime * result + ((getValidity_unit() == null) ? 0 : getValidity_unit().hashCode());
        result = prime * result + ((getIs_delete() == null) ? 0 : getIs_delete().hashCode());
        result = prime * result + ((getIs_maintain_weight() == null) ? 0 : getIs_maintain_weight().hashCode());
        result = prime * result + ((getIs_maintain_warranty() == null) ? 0 : getIs_maintain_warranty().hashCode());
        result = prime * result + ((getIs_maintain_batch_sn() == null) ? 0 : getIs_maintain_batch_sn().hashCode());
        result = prime * result + ((getIs_contraband() == null) ? 0 : getIs_contraband().hashCode());
        result = prime * result + ((getIs_serial() == null) ? 0 : getIs_serial().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        result = prime * result + ((getLast_updated_user() == null) ? 0 : getLast_updated_user().hashCode());
        result = prime * result + ((getLast_updated_time() == null) ? 0 : getLast_updated_time().hashCode());
        return result;
    }
}