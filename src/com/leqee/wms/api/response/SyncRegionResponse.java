package com.leqee.wms.api.response;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class SyncRegionResponse extends LeqeeResponse {

	private static final long serialVersionUID = 1L;
	
	private Integer region_id;
	
	private String region_name;
	
	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}

	public String getRegion_name() {
		return region_name;
	}

	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}

	

	

	
}
