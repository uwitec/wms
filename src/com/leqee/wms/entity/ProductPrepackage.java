package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ProductPrepackage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer product_prepackage_id; //
	private Integer prepackage_product_id; //  预打包商品product_id 
    private Integer component_product_id; //  预打包商品组件product_id 
    private Integer number; //  组件数量 
    
    private String sku_code;
    private Integer customer_id;
    private String barcode;
    private String product_name;
    
	public String getSku_code() {
		return sku_code;
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	public Integer getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public Integer getProduct_prepackage_id() {
		return product_prepackage_id;
	}
	public void setProduct_prepackage_id(Integer product_prepackage_id) {
		this.product_prepackage_id = product_prepackage_id;
	}
	public Integer getPrepackage_product_id() {
		return prepackage_product_id;
	}
	public void setPrepackage_product_id(Integer prepackage_product_id) {
		this.prepackage_product_id = prepackage_product_id;
	}
	public Integer getComponent_product_id() {
		return component_product_id;
	}
	public void setComponent_product_id(Integer component_product_id) {
		this.component_product_id = component_product_id;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
		
}