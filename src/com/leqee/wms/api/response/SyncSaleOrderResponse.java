package com.leqee.wms.api.response;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;

/**
 * 同步发货单Response
 * @author qyyao
 * @date 2016-2-26
 * @version 1.0
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class SyncSaleOrderResponse extends LeqeeResponse{
	private static final long serialVersionUID = 1L;
	
	private Integer wms_order_id;   
	private String oms_order_sn;
	
	
	public Integer getWms_order_id() {
		return wms_order_id;
	}
	public void setWms_order_id(Integer wms_order_id) {
		this.wms_order_id = wms_order_id;
	}
	public String getOms_order_sn() {
		return oms_order_sn;
	}
	public void setOms_order_sn(String oms_order_sn) {
		this.oms_order_sn = oms_order_sn;
	}
	
	
	
	
	
}
