package com.leqee.wms.api.response.domain;

/**
 * 
 * @author xhchen
 *
 */
public class OmsOrderTransferCodeResDomain {

	private String oms_order_sn;
	private String sku_code;
	private String shipping_code;//  '物流码',

	public String getOms_order_sn() {
		return oms_order_sn;
	}

	public void setOms_order_sn(String oms_order_sn) {
		this.oms_order_sn = oms_order_sn;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getShipping_code() {
		return shipping_code;
	}

	public void setShipping_code(String shipping_code) {
		this.shipping_code = shipping_code;
	}

}
