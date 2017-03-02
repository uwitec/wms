package com.leqee.wms.entity;

import java.io.Serializable;

public class SameDetails implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//商品拼接串
	private String goods_name="";
	
	//商品编号拼接串
	private String product_key="";
	
	//商品数目拼接串
	private String product_num="";
	
	//商品数目拼接串
	private String order_ids="";
	
	//批捡展示字符串
	private String shows_batchpick="";
	
	private Integer shipping_id;
	
	//满足此条件的订单数
	private int sku_sum;
	//满足此条件的订单数
	private boolean mark=false;
	
	private int serial_nums;


	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getProduct_key() {
		return product_key;
	}

	public void setProduct_key(String product_key) {
		this.product_key = product_key;
	}

	public String getProduct_num() {
		return product_num;
	}

	public void setProduct_num(String product_num) {
		this.product_num = product_num;
	}

	public String getOrder_ids() {
		return order_ids;
	}

	public void setOrder_ids(String order_ids) {
		this.order_ids = order_ids;
	}

	public String getShows_batchpick() {
		return shows_batchpick;
	}

	public void setShows_batchpick(String shows_batchpick) {
		this.shows_batchpick = shows_batchpick;
	}

	public int getSku_sum() {
		return sku_sum;
	}

	public void setSku_sum(int sku_sum) {
		this.sku_sum = sku_sum;
	}

	public boolean isMark() {
		return mark;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}

	public int getSerial_nums() {
		return serial_nums;
	}

	public void setSerial_nums(int serial_nums) {
		this.serial_nums = serial_nums;
	}

	public Integer getShipping_id() {
		return shipping_id;
	}

	public void setShipping_id(Integer shipping_id) {
		this.shipping_id = shipping_id;
	}
	
	
	
}
