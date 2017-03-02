package com.leqee.wms.api.response.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存Detail响应实体
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
public class InventoryDetailResDomain {
	private String sku_code;   //oms的商家编码
	private Integer inventory_item_id ;  //库存子项ID
	private Integer warehouse_id ;
	private Integer physical_warehouse_id ;
	private Integer quantity ;
	private String status_id;
	private BigDecimal unit_cost;
	private String provider_code;
	private Date validity;   //生产日期，类型String还是Date待定
	private String batch_sn;  //批次号
	private String serial_number;  //串号
	private String inventory_item_acct_type_id; //库存类型
	public String getSku_code() {
		return sku_code;
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	public Integer getInventory_item_id() {
		return inventory_item_id;
	}
	public void setInventory_item_id(Integer inventory_item_id) {
		this.inventory_item_id = inventory_item_id;
	}
	public Integer getWarehouse_id() {
		return warehouse_id;
	}
	public void setWarehouse_id(Integer warehouse_id) {
		this.warehouse_id = warehouse_id;
	}
	public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}
	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getStatus_id() {
		return status_id;
	}
	public void setStatus_id(String status_id) {
		this.status_id = status_id;
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
		this.provider_code = provider_code;
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
		this.batch_sn = batch_sn;
	}
	public String getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}
	public String getInventory_item_acct_type_id() {
		return inventory_item_acct_type_id;
	}
	public void setInventory_item_acct_type_id(String inventory_item_acct_type_id) {
		this.inventory_item_acct_type_id = inventory_item_acct_type_id;
	}
	
	
	
	
	
}
