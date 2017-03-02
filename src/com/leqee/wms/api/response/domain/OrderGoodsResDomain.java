package com.leqee.wms.api.response.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * @author hzhang1
 * @date 2016-3-1
 * @version 1.0.0
 */
public class OrderGoodsResDomain implements Serializable{

	private static final long serialVersionUID = 1L;

	private String oms_order_goods_sn;  //oms中订单商品表的主键
	
	private Integer inventory_item_id;   //inventory_item表的主键
	
	private String sku_code; //商品的唯一编码
	
	private String goods_name; //商品名称
	
	private Integer goods_number; //订单商品中的数量
	
	private Integer quantity; //实际入库数量
	
	private String batch_sn; //商品批次号
	
	private Date validity; //商品生产日期
	
	private BigDecimal unit_cost; //采购单价
	
	private String status_id; //新旧状态
	
	private String serial_number; //串号
	
	private String provider_code; //供应商编码

	private Integer parent_inventory_item_id;   
	
	private Integer root_inventory_item_id;   
	

	public String getOms_order_goods_sn() {
		return oms_order_goods_sn;
	}

	public void setOms_order_goods_sn(String oms_order_goods_sn) {
		this.oms_order_goods_sn = oms_order_goods_sn;
	}

	public Integer getInventory_item_id() {
		return inventory_item_id;
	}

	public void setInventory_item_id(Integer inventory_item_id) {
		this.inventory_item_id = inventory_item_id;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public Integer getGoods_number() {
		return goods_number;
	}

	public void setGoods_number(Integer goods_number) {
		this.goods_number = goods_number;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getBatch_sn() {
		return batch_sn;
	}

	public void setBatch_sn(String batch_sn) {
		this.batch_sn = batch_sn;
	}

	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	public String getStatus_id() {
		return status_id;
	}

	public void setStatus_id(String status_id) {
		this.status_id = status_id;
	}

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
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

	
	
	
	
	
	
}
