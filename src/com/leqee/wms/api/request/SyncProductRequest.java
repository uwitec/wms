package com.leqee.wms.api.request;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.SyncProductResponse;


	
public class SyncProductRequest extends BaseLeqeeRequest<SyncProductResponse> {
	


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
    
    private Integer spec;

    private BigDecimal weight;

    private BigDecimal unit_price;

    private Integer validity;

    private String validity_unit;

    private String is_delete;

    private String is_maintain_weight;

    private String is_maintain_warranty;

    private String is_maintain_batch_sn;

    private String is_contraband;

    private String is_serial;
    
    private String product_type;  // 商品类型:GOODS(普通商品)，PACKBOX(耗材商品)

    private String created_user;

    private Date created_time;

    private String last_updated_user;

    private Date last_updated_time;

    private Integer warranty_warning_days;
    
    private Integer warranty_unsalable_days;
    
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
		this.brand_name = brand_name;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getCat_name() {
		return cat_name;
	}

	public void setCat_name(String cat_name) {
		this.cat_name = cat_name;
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
		this.validity_unit = validity_unit;
	}

	public String getIs_delete() {
		return is_delete;
	}

	public void setIs_delete(String is_delete) {
		this.is_delete = is_delete;
	}

	public String getIs_maintain_weight() {
		return is_maintain_weight;
	}

	public void setIs_maintain_weight(String is_maintain_weight) {
		this.is_maintain_weight = is_maintain_weight;
	}

	public String getIs_maintain_warranty() {
		return is_maintain_warranty;
	}

	public void setIs_maintain_warranty(String is_maintain_warranty) {
		this.is_maintain_warranty = is_maintain_warranty;
	}

	public String getIs_maintain_batch_sn() {
		return is_maintain_batch_sn;
	}

	public void setIs_maintain_batch_sn(String is_maintain_batch_sn) {
		this.is_maintain_batch_sn = is_maintain_batch_sn;
	}

	public String getIs_contraband() {
		return is_contraband;
	}

	public void setIs_contraband(String is_contraband) {
		this.is_contraband = is_contraband;
	}

	public String getIs_serial() {
		return is_serial;
	}

	public void setIs_serial(String is_serial) {
		this.is_serial = is_serial;
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
		this.created_user = created_user;
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
		this.last_updated_user = last_updated_user;
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
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<SyncProductResponse> getResponseClass() {
		return SyncProductResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getApiMethodName() {
		return METHOD_SYNC_PRODUCT_REQUEST;
	}
    
    
    
    
    
    
}
