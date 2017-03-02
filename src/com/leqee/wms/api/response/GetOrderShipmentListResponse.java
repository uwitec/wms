package com.leqee.wms.api.response;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;
import com.leqee.wms.api.response.domain.OrderShipmentResDomain;

/**
 * 查询未出库的耗材列表
 * 
 * @author xhchen
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GetOrderShipmentListResponse extends LeqeeResponse {

	private static final long serialVersionUID = 1L;

	private Integer total_count;

	private List<OrderShipmentResDomain> orderShipmentResDomains;

	public Integer getTotal_count() {
		return total_count;
	}

	public void setTotal_count(Integer total_count) {
		this.total_count = total_count;
	}

	public List<OrderShipmentResDomain> getOrderShipmentResDomains() {
		return orderShipmentResDomains;
	}

	public void setOrderShipmentResDomains(List<OrderShipmentResDomain> orderShipmentResDomains) {
		this.orderShipmentResDomains = orderShipmentResDomains;
	}

}
