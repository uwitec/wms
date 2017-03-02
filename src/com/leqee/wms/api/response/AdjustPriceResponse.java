package com.leqee.wms.api.response;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;
import com.leqee.wms.api.response.domain.AdjustPriceResDomain;

/**
 * @author hzhang1
 * @date 2016-3-1
 * @version 1.0.0
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AdjustPriceResponse extends LeqeeResponse{
	
	private static final long serialVersionUID = 1L;
	
	
	private Date pre_arrival_time;   //修改到货时间前的time
	private Date post_arrival_time;   //修改到货时间后的time
	
	
	private List<AdjustPriceResDomain> adjustPriceResDomainList;

	public List<AdjustPriceResDomain> getAdjustPriceResDomainList() {
		return adjustPriceResDomainList;
	}

	public void setAdjustPriceResDomainList(
			List<AdjustPriceResDomain> adjustPriceResDomainList) {
		this.adjustPriceResDomainList = adjustPriceResDomainList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getPre_arrival_time() {
		return pre_arrival_time;
	}

	public void setPre_arrival_time(Date pre_arrival_time) {
		this.pre_arrival_time = pre_arrival_time;
	}

	public Date getPost_arrival_time() {
		return post_arrival_time;
	}

	public void setPost_arrival_time(Date post_arrival_time) {
		this.post_arrival_time = post_arrival_time;
	}
	
	
	
	
}
