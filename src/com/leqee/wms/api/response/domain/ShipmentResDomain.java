package com.leqee.wms.api.response.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
/**
 * 发货单返回实体
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
/**
 * 发货单返回实体
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
public class ShipmentResDomain implements Serializable{

	private static final long serialVersionUID = 1L;

	
	private String shipping_code; //快递编码
	
	private String tracking_number; //快递单号
	
	private String status; //发货单状态
	private BigDecimal shipping_wms_weight;
	
	


	private List<ShipmentDetailResDomain>  shipmentDetailResDomainList;  //发货单详情



	public String getShipping_code() {
		return shipping_code;
	}



	public void setShipping_code(String shipping_code) {
		this.shipping_code = shipping_code;
	}



	public String getTracking_number() {
		return tracking_number;
	}



	public void setTracking_number(String tracking_number) {
		this.tracking_number = tracking_number;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public List<ShipmentDetailResDomain> getShipmentDetailResDomainList() {
		return shipmentDetailResDomainList;
	}

	public BigDecimal getShipping_wms_weight() {
		return shipping_wms_weight;
	}



	public void setShipping_wms_weight(BigDecimal shipping_wms_weight) {
		this.shipping_wms_weight = shipping_wms_weight;
	}


	public void setShipmentDetailResDomainList(
			List<ShipmentDetailResDomain> shipmentDetailResDomainList) {
		this.shipmentDetailResDomainList = shipmentDetailResDomainList;
	}



	
	
	


}
