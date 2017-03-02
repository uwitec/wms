package com.leqee.wms.api.response.domain;

import java.io.Serializable;
/**
 * 发货单详情返回实体
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
public class ShipmentDetailResDomain implements Serializable{

	private static final long serialVersionUID = 1L;

	private String oms_order_goods_sn;  //oms中订单商品表的主键
	
	private Integer wms_order_goods_id;   //wms中订单商品表的主键
	
	private String sku_code; //oms商品的唯一编码
	
	private Integer goods_number; //订单商品中的数量
	
	private String serial_number; //串号

	

	public String getOms_order_goods_sn() {
		return oms_order_goods_sn;
	}

	public void setOms_order_goods_sn(String oms_order_goods_sn) {
		this.oms_order_goods_sn = oms_order_goods_sn;
	}

	public Integer getWms_order_goods_id() {
		return wms_order_goods_id;
	}

	public void setWms_order_goods_id(Integer wms_order_goods_id) {
		this.wms_order_goods_id = wms_order_goods_id;
	}

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

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}

	
	
	
}
