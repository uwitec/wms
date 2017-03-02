package com.leqee.wms.api.response.domain;

import java.io.Serializable;
/**
 * 订单耗材返回实体
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
public class OrderPackboxResDomain implements Serializable{

	private static final long serialVersionUID = 1L;

	private String sku_code; //商品的唯一编码
	private Integer goods_number; //订单商品中的数量


	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public Integer getGoods_number() {
		return goods_number;
	}

	public void setGoods_number(Integer goods_number) {
		this.goods_number = goods_number;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
