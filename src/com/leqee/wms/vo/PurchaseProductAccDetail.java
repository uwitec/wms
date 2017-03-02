package com.leqee.wms.vo;

import java.io.Serializable;
import java.util.List;

public class PurchaseProductAccDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer warehouse_id;
	
	private Integer order_goods_id;
	
	private Integer physical_warehouse_id;
	
	private String barcode;
	
	private Integer product_id;
	
	private String is_serial;
	
	private String batch_sn;
	
	private String goods_name;
	
	private String length_width_height;
	
	private Integer purchase_number;
	
	private Integer arrive_number;
	
	private Integer tray_number; //托盘数
	
	private String dui_die; //堆叠
	
//	private Integer normal_number;
//	
//	private Integer defective_number;
//	
//	private String validity;
//	
//	private String serial_no;
//	
//	private String tag;
	
	private List<PurchaseProduct> purchaseProductList;
	
	public List<PurchaseProduct> getPurchaseProductList() {
		return purchaseProductList;
	}

	public void setPurchaseProductList(List<PurchaseProduct> purchaseProductList) {
		this.purchaseProductList = purchaseProductList;
	}

	public Integer getWarehouse_id() {
		return warehouse_id;
	}

	public void setWarehouse_id(Integer warehouse_id) {
		this.warehouse_id = warehouse_id;
	}
	
	public Integer getOrder_goods_id() {
		return order_goods_id;
	}

	public void setOrder_goods_id(Integer order_goods_id) {
		this.order_goods_id = order_goods_id;
	}

	public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public String getIs_serial() {
		return is_serial;
	}

	public void setIs_serial(String is_serial) {
		this.is_serial = is_serial;
	}

	public String getBatch_sn() {
		return batch_sn;
	}

	public void setBatch_sn(String batch_sn) {
		this.batch_sn = batch_sn;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getLength_width_height() {
		return length_width_height;
	}

	public void setLength_width_height(String length_width_height) {
		this.length_width_height = length_width_height;
	}

	public Integer getPurchase_number() {
		return purchase_number;
	}

	public void setPurchase_number(Integer purchase_number) {
		this.purchase_number = purchase_number;
	}

	public Integer getArrive_number() {
		return arrive_number;
	}

	public void setArrive_number(Integer arrive_number) {
		this.arrive_number = arrive_number;
	}

	public Integer getTray_number() {
		return tray_number;
	}

	public void setTray_number(Integer tray_number) {
		this.tray_number = tray_number;
	}

	public String getDui_die() {
		return dui_die;
	}

	public void setDui_die(String dui_die) {
		this.dui_die = dui_die;
	}


//	public Integer getNormal_number() {
//		return normal_number;
//	}
//
//	public void setNormal_number(Integer normal_number) {
//		this.normal_number = normal_number;
//	}
//
//	public Integer getDefective_number() {
//		return defective_number;
//	}
//
//	public void setDefective_number(Integer defective_number) {
//		this.defective_number = defective_number;
//	}
//
//
//	public String getValidity() {
//		return validity;
//	}
//
//	public void setValidity(String validity) {
//		this.validity = validity;
//	}
//
//
//	public String getSerial_no() {
//		return serial_no;
//	}
//
//	public void setSerial_no(String serial_no) {
//		this.serial_no = serial_no;
//	}
//
//	public String getTag() {
//		return tag;
//	}
//
//	public void setTag(String tag) {
//		this.tag = tag;
//	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
