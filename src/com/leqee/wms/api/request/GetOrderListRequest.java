package com.leqee.wms.api.request;

import java.util.Date;
import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.GetOrderListResponse;

/**
 * 查询订单列表请求
 * @author qyyao
 * @date 2016-3-7
 * @version 1.0
 */
public class GetOrderListRequest extends BaseLeqeeRequest<GetOrderListResponse>{
	
	
	private Date start_modified_time;
	private Date end_modified_time;
	private String wms_order_type;
	private String wms_order_status;
	private Integer page_no;
	private Integer page_size;
	
	public Date getStart_modified_time() {
		return start_modified_time;
	}

	public void setStart_modified_time(Date start_modified_time) {
		this.start_modified_time = start_modified_time;
	}

	public Date getEnd_modified_time() {
		return end_modified_time;
	}

	public void setEnd_modified_time(Date end_modified_time) {
		this.end_modified_time = end_modified_time;
	}

	public String getWms_order_type() {
		return wms_order_type;
	}

	public void setWms_order_type(String wms_order_type) {
		this.wms_order_type = wms_order_type;
	}

	public String getWms_order_status() {
		return wms_order_status;
	}

	public void setWms_order_status(String wms_order_status) {
		this.wms_order_status = wms_order_status;
	}

	public Integer getPage_no() {
		return page_no;
	}

	public void setPage_no(Integer page_no) {
		this.page_no = page_no;
	}

	public Integer getPage_size() {
		return page_size;
	}

	public void setPage_size(Integer page_size) {
		this.page_size = page_size;
	}

	@Override
	public String getApiMethodName() {
		return METHOD_GET_ORDER_LIST_REQUEST;
	}

	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<GetOrderListResponse> getResponseClass() {
		return GetOrderListResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}

	
}
