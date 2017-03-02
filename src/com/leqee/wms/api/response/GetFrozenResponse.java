package com.leqee.wms.api.response;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;
import com.leqee.wms.api.response.domain.FrozenDetailResDomain;
import com.leqee.wms.api.response.domain.InventoryDetailResDomain;
import com.leqee.wms.api.response.domain.InventorySummaryResDomain;

/**
 * 商品冻结查询Response
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GetFrozenResponse extends LeqeeResponse{

	private static final long serialVersionUID = 2984393861338891411L;
	
	
	private List<FrozenDetailResDomain> frozenDetailResDomains ;


	public List<FrozenDetailResDomain> getFrozenDetailResDomains() {
		return frozenDetailResDomains;
	}


	public void setFrozenDetailResDomains(
			List<FrozenDetailResDomain> frozenDetailResDomains) {
		this.frozenDetailResDomains = frozenDetailResDomains;
	}


	
	
	
	
}
