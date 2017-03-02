package com.leqee.wms.api.request.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderGoodsReqDomain implements Serializable{
	 	
		private String goods_name;

	    private Integer goods_number;

	    private BigDecimal goods_price;

	    private BigDecimal discount;

	    private String batch_sn;

	    private String status_id;

	    private BigDecimal tax_rate;

	    private String sku_code;
	    
	    private String oms_order_goods_sn;
	    
	    private String group_code;

	    private Integer group_number;
	    

	    private static final long serialVersionUID = 1L;
	    
	    
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

		public String getSku_code() {
			return sku_code;
		}

		public void setSku_code(String sku_code) {
			this.sku_code = sku_code;
		}

		public static long getSerialversionuid() {
			return serialVersionUID;
		}

		public String getOms_order_goods_sn() {
			return oms_order_goods_sn;
		}

		public void setOms_order_goods_sn(String oms_order_goods_sn) {
			this.oms_order_goods_sn = oms_order_goods_sn;
		}

		
	    
}
