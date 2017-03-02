package com.leqee.wms.api.response.domain;

import java.io.Serializable;

/**
 * 耗材信息
 * 
 * @author xhchen
 *
 */
public class PackBoxResDomain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sku_code;

	private String tracking_number;
	
	private String is_in_out;

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getTracking_number() {
		return tracking_number;
	}

	public void setTracking_number(String tracking_number) {
		this.tracking_number = tracking_number;
	}

	public String getIs_in_out() {
		return is_in_out;
	}

	public void setIs_in_out(String is_in_out) {
		this.is_in_out = is_in_out;
	}

}
