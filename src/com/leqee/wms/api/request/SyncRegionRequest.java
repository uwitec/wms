package com.leqee.wms.api.request;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.SyncProductResponse;
import com.leqee.wms.api.response.SyncRegionResponse;


	
public class SyncRegionRequest extends BaseLeqeeRequest<SyncRegionResponse> {
	
	
	private Integer region_id;
	
	private Integer parent_id;
	
	private String region_name;
	
	private Integer region_type;
	
	private String created_user;

	private Date created_time;

    private String last_updated_user;

    private Date last_updated_time;
    
    public String getCreated_user() {
 		return created_user;
 	}

 	public void setCreated_user(String created_user) {
 		this.created_user = created_user;
 	}

 	public Date getCreated_time() {
 		return created_time;
 	}

 	public void setCreated_time(Date created_time) {
 		this.created_time = created_time;
 	}

 	public String getLast_updated_user() {
 		return last_updated_user;
 	}

 	public void setLast_updated_user(String last_updated_user) {
 		this.last_updated_user = last_updated_user;
 	}

 	public Date getLast_updated_time() {
 		return last_updated_time;
 	}

 	public void setLast_updated_time(Date last_updated_time) {
 		this.last_updated_time = last_updated_time;
 	}
	
	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}

	public Integer getParent_id() {
		return parent_id;
	}

	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}

	public String getRegion_name() {
		return region_name;
	}

	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}

	public Integer getRegion_type() {
		return region_type;
	}

	public void setRegion_type(Integer region_type) {
		this.region_type = region_type;
	}

	
	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<SyncRegionResponse> getResponseClass() {
		return SyncRegionResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getApiMethodName() {
		return METHOD_SYNC_REGION_REQUEST;
	}
    
    
    
    
    
    
}
