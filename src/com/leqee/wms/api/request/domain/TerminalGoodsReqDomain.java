package com.leqee.wms.api.request.domain;

public class TerminalGoodsReqDomain {

	private String oms_order_goods_sn;//
	private Integer quatity;

	public String getOms_order_goods_sn() {
		return oms_order_goods_sn;
	}

	public void setOms_order_goods_sn(String oms_order_goods_sn) {
		this.oms_order_goods_sn = oms_order_goods_sn;
	}

	public Integer getQuatity() {
		return quatity;
	}

	public void setQuatity(Integer quatity) {
		this.quatity = quatity;
	}

}
