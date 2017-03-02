package com.leqee.wms.api.response.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 返回耗材信息的基础类
 * @author xhchen
 *
 */
public class OrderShipmentResDomain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String order_id;
	private String oms_order_sn;

	private List<PackBoxResDomain> packBoxResDomains = new ArrayList<PackBoxResDomain>();;

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getOms_order_sn() {
		return oms_order_sn;
	}

	public void setOms_order_sn(String oms_order_sn) {
		this.oms_order_sn = oms_order_sn;
	}

	public List<PackBoxResDomain> getPackBoxResDomains() {
		return packBoxResDomains;
	}

	public void setPackBoxResDomains(List<PackBoxResDomain> packBoxResDomains) {
		this.packBoxResDomains = packBoxResDomains;
	}

}
