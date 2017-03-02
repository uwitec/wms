package com.leqee.wms.api.response.domain;

import java.io.Serializable;
import java.util.Date;
/**
 * 订单信息响应实体
 * @author qyyao
 * @date 2016-3-7
 * @version 1.0
 */
public class OrderInfoResDomain implements Serializable{

	private static final long serialVersionUID = -5266352634669017051L;
	
	private Integer wms_order_id;
	private String wms_order_status;
	private Integer warehouse_id;
	private String oms_order_sn;
	private Date modified_time;
	
	
	public Integer getWms_order_id() {
		return wms_order_id;
	}
	public void setWms_order_id(Integer wms_order_id) {
		this.wms_order_id = wms_order_id;
	}
	public String getWms_order_status() {
		return wms_order_status;
	}
	public void setWms_order_status(String wms_order_status) {
		this.wms_order_status = wms_order_status;
	}
	public Integer getWarehouse_id() {
		return warehouse_id;
	}
	public void setWarehouse_id(Integer warehouse_id) {
		this.warehouse_id = warehouse_id;
	}
	public String getOms_order_sn() {
		return oms_order_sn;
	}
	public void setOms_order_sn(String oms_order_sn) {
		this.oms_order_sn = oms_order_sn;
	}
	public Date getModified_time() {
		return modified_time;
	}
	public void setModified_time(Date modified_time) {
		this.modified_time = modified_time;
	}
	
	
	
}
