package com.leqee.wms.api.request;

import java.util.Date;
import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.GetOrderPrepacksResponse;

/**
 * 获取已完结的预打包任务信息
 * 
 * @author xhchen
 *
 */
public class GetOrderPrepacksRequest extends BaseLeqeeRequest<GetOrderPrepacksResponse> {

	private Date startTime;
	private Date endTime;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Override
	public String getApiMethodName() {
		return METHOD_GET_ORDERPREPACK_REQUEST;
	}

	@Override
	public Map<String, String> getTextParams() {
		return null;
	}

	@Override
	public Class<GetOrderPrepacksResponse> getResponseClass() {
		return GetOrderPrepacksResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {
	}

}
