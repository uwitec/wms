package com.leqee.wms.api.response;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;

/**
 * 取消订单Response
 * @author qyyao
 * @date 2016-2-26
 * @version 1.0
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CancelOrderResponse extends LeqeeResponse{
	private static final long serialVersionUID = 1L;
	
	
}
