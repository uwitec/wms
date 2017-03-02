package com.leqee.wms.api;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;


/**
 * Request公共类
 * @author qyyao
 * @date 16-02-22
 *
 */
public abstract interface LeqeeRequest<T extends LeqeeResponse> {
	
	@JsonIgnore
	public abstract String getApiMethodName();

	@JsonIgnore
	public abstract Map<String, String> getTextParams();

	@JsonIgnore
	public abstract Long getTimestamp();

	@JsonIgnore
	public abstract String getTargetAppKey();

	@JsonIgnore
	public abstract Class<T> getResponseClass();

	@JsonIgnore
	public abstract Map<String, String> getHeaderMap();

	public abstract void check() throws ApiRuleException;

	public abstract void putOtherTextParam(String paramString1,
			String paramString2);
	
	
	
	
	
}
