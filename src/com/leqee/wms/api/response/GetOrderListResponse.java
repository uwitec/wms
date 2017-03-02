package com.leqee.wms.api.response;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;
import com.leqee.wms.api.response.domain.OrderInfoResDomain;

/**
 * 查询订单列表Response
 * @author qyyao
 * @date 2016-3-7
 * @version 1.0
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GetOrderListResponse extends LeqeeResponse{
	private static final long serialVersionUID = 1L;
	
	private Integer total_count;
	
	private List<OrderInfoResDomain> OrderInfoResDomainList ;
	

	public Integer getTotal_count() {
		return total_count;
	}

	public void setTotal_count(Integer total_count) {
		this.total_count = total_count;
	}

	public List<OrderInfoResDomain> getOrderInfoResDomainList() {
		return OrderInfoResDomainList;
	}

	public void setOrderInfoResDomainList(
			List<OrderInfoResDomain> orderInfoResDomainList) {
		OrderInfoResDomainList = orderInfoResDomainList;
	}
	
	
	
	
	
	
	
	
	
}
