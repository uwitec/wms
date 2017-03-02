package com.leqee.wms.api.request;

import java.util.Date;
import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.GetVarianceImproveTaskListResponse;

/**
 * 查询盘点任务列表请求
 * @author qyyao
 * @date 2016-8-25
 * @version 1.0
 */
public class GetVarianceImproveTaskListRequest extends BaseLeqeeRequest<GetVarianceImproveTaskListResponse>{
	
	
	private Date start_modified_time;  
	private Date end_modified_time;
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
		return METHOD_GET_VARIANCE_IMPROVE_TASK_LIST_REQUEST;
	}

	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<GetVarianceImproveTaskListResponse> getResponseClass() {
		return GetVarianceImproveTaskListResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}

	
}
