package com.leqee.wms.api.response.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AdjustPriceResDomain implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String oms_order_sn; //返回oms订单的order_sn
	
	private Integer oms_order_goods_sn; //返回oms订单商品对应的唯一主键
	
	private String sku_code; //返回商品sku_code
	
	private BigDecimal pre_unit_cost; //返回调整供价前的价格
	
	private BigDecimal post_unit_cost; //返回调整供价后的价格
	
	
	private String pre_provider_code;   //调整供应商code前的code
	private String post_provider_code;   //调整供应商code后的code
	
	

	
	

	public String getOms_order_sn() {
		return oms_order_sn;
	}

	public void setOms_order_sn(String oms_order_sn) {
		this.oms_order_sn = oms_order_sn;
	}


	public Integer getOms_order_goods_sn() {
		return oms_order_goods_sn;
	}

	public void setOms_order_goods_sn(Integer oms_order_goods_sn) {
		this.oms_order_goods_sn = oms_order_goods_sn;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public BigDecimal getPre_unit_cost() {
		return pre_unit_cost;
	}

	public void setPre_unit_cost(BigDecimal pre_unit_cost) {
		this.pre_unit_cost = pre_unit_cost;
	}

	public BigDecimal getPost_unit_cost() {
		return post_unit_cost;
	}

	public void setPost_unit_cost(BigDecimal post_unit_cost) {
		this.post_unit_cost = post_unit_cost;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPre_provider_code() {
		return pre_provider_code;
	}

	public void setPre_provider_code(String pre_provider_code) {
		this.pre_provider_code = pre_provider_code;
	}

	public String getPost_provider_code() {
		return post_provider_code;
	}

	public void setPost_provider_code(String post_provider_code) {
		this.post_provider_code = post_provider_code;
	}


	
	
	
}
