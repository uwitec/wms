package com.leqee.wms.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderGoodsOms implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer order_goods_id; //
	private Integer order_id; //  订单号 
    private String oms_order_goods_sn; //  oms订单商品表的主键rec_id 
    private Integer warehouse_id; //  仓库编号 
    private Integer customer_id; //  卖家编码，保证唯一 
    private Integer product_id; //  商品id 
    private String goods_name; //  商品名 
    private Integer goods_number; //  商品数量 
    private BigDecimal goods_price; //  商品单价 
    private BigDecimal discount; //  商品折扣金额 
    private Integer inventory_change_number; //  
    private String batch_sn; //  批次号 
    private String status_id; //  商品新旧状态normaldefective 
    private BigDecimal tax_rate; //  税率 
    private String order_goods_type; //  goods  packbox 
    private String group_code; //  
    private Integer group_number; //  
    private String created_user; //  创建者 
    private Date created_time; //  创建时间 
    private String last_updated_user; //  最后更新人 
    private Date last_updated_time; //  最后更新时间 
    
    
    //辅助字段，不作为数据库字段
    private String is_serial;
    private String sku_code;
    
	public String getIs_serial() {
		return is_serial;
	}
	public void setIs_serial(String is_serial) {
		this.is_serial = is_serial;
	}
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
		this.goods_name = goods_name;
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
		this.batch_sn = batch_sn;
	}
	public String getStatus_id() {
		return status_id;
	}
	public void setStatus_id(String status_id) {
		this.status_id = status_id;
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
		this.order_goods_type = order_goods_type;
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
	public String getSku_code() {
		return sku_code;
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	
}