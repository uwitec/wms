package com.leqee.wms.api.request;

import java.util.Date;
import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.GetOrderShipmentListResponse;

/**
 * 查询未出库耗材列表
 * @author xhchen
 *
 */
public class GetOrderShipmentListRequest extends BaseLeqeeRequest<GetOrderShipmentListResponse> {

	private Date start_modified_time;

	private Date end_modified_time;

	private Integer page_no;

	private Integer page_size;

	private String is_in_out;

	private String wms_order_status;

	private String wms_order_type;

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

	public String getIs_in_out() {
		return is_in_out;
	}

	public void setIs_in_out(String is_in_out) {
		this.is_in_out = is_in_out;
	}

	public String getWms_order_status() {
		return wms_order_status;
	}

	public void setWms_order_status(String wms_order_status) {
		this.wms_order_status = wms_order_status;
	}

	public String getWms_order_type() {
		return wms_order_type;
	}

	public void setWms_order_type(String wms_order_type) {
		this.wms_order_type = wms_order_type;
	}

	@Override
	public String getApiMethodName() {
		return METHOD_GET_ORDER_SHIPMENT_LIST_REQUEST;
	}

	@Override
	public Map<String, String> getTextParams() {
		return null;
	}

	@Override
	public Class<GetOrderShipmentListResponse> getResponseClass() {
		return null;
	}

	@Override
	public void check() throws ApiRuleException {
	}

}
