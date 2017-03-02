package com.leqee.wms.vo;

import java.io.Serializable;

/**
 * @author Jarvis
 * @CreatedDate 2016.02.05
 * 
 * 记录退货入库商品条码、串号、新旧、数量等情况
 * 
 * */
public class ReturnProductAccDetail implements Serializable {
	
	private int productId; 
	private String barcode;       // 条码
	private String serialNumber;  // 串号
	private String status;        // 商品新旧状态,良品:NORMAL,不良品:DEFECTIVE
	private int num = 1;          // 数量
	private String isSerial;      // 是否串号控制,是:Y,否:N
	private int orderGoodsId;
	private String validity;
	private String batch_sn;
	
	private static final long serialVersionUID = 1L;

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getBatch_sn() {
		return batch_sn;
	}

	public void setBatch_sn(String batch_sn) {
		this.batch_sn = batch_sn;
	}
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getIsSerial() {
		return isSerial;
	}

	public void setIsSerial(String isSerial) {
		this.isSerial = isSerial;
	}

	public int getOrderGoodsId() {
		return orderGoodsId;
	}

	public void setOrderGoodsId(int orderGoodsId) {
		this.orderGoodsId = orderGoodsId;
	}

}
