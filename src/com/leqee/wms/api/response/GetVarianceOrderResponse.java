package com.leqee.wms.api.response;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;
import com.leqee.wms.api.response.domain.OrderGoodsResDomain;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GetVarianceOrderResponse extends LeqeeResponse{
	
	private static final long serialVersionUID = 1L;
	
	private String wms_order_id; //返回wms订单的order_id
	
	private String wms_order_status; //返回wms订单的状态
	
	private Integer warehouse_id; //返回订单对应的仓库

	private String oms_order_sn; //返回oms的订单号

	private List<OrderGoodsResDomain> orderGoodsResDomainList;
	
	public String getWms_order_id() {
		return wms_order_id;
	}

	public void setWms_order_id(String wms_order_id) {
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
	
	public List<OrderGoodsResDomain> getOrderGoodsResDomainList() {
		return orderGoodsResDomainList;
	}

	public void setOrderGoodsResDomainList(List<OrderGoodsResDomain> orderGoodsResDomainList) {
		this.orderGoodsResDomainList = orderGoodsResDomainList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
