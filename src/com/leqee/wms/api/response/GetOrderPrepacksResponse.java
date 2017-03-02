package com.leqee.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.leqee.wms.api.LeqeeResponse;
import com.leqee.wms.api.response.domain.OrderPrepackResDomain;

public class GetOrderPrepacksResponse extends LeqeeResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<OrderPrepackResDomain> prepackResDomains = new ArrayList<OrderPrepackResDomain>();

	public List<OrderPrepackResDomain> getPrepackResDomains() {
		return prepackResDomains;
	}

	public void setPrepackResDomains(List<OrderPrepackResDomain> prepackResDomains) {
		this.prepackResDomains = prepackResDomains;
	}

}
