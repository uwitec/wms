package com.leqee.wms.vo;

import java.util.List;

public class PurchaseAcceptVO {
	
	private Integer order_id;
	
	private List<PurchaseProductAccDetail> purchaseProductAccDetailList;

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

	public List<PurchaseProductAccDetail> getPurchaseProductAccDetailList() {
		return purchaseProductAccDetailList;
	}

	public void setPurchaseProductAccDetailList(
			List<PurchaseProductAccDetail> purchaseProductAccDetailList) {
		this.purchaseProductAccDetailList = purchaseProductAccDetailList;
	}
	
	
}
