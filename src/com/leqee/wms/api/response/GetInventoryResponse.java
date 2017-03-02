package com.leqee.wms.api.response;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;
import com.leqee.wms.api.response.domain.InventoryDetailResDomain;
import com.leqee.wms.api.response.domain.InventorySummaryResDomain;

/**
 * 商品库存查询Response
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GetInventoryResponse extends LeqeeResponse{

	private static final long serialVersionUID = 2984393861338891411L;
	
	private List<InventorySummaryResDomain> inventorySummaryResDomains ;
	
	private List<InventoryDetailResDomain> inventoryDetailResDomains ;

	
	public List<InventorySummaryResDomain> getInventorySummaryResDomains() {
		return inventorySummaryResDomains;
	}

	public void setInventorySummaryResDomains(
			List<InventorySummaryResDomain> inventorySummaryResDomains) {
		this.inventorySummaryResDomains = inventorySummaryResDomains;
	}

	public List<InventoryDetailResDomain> getInventoryDetailResDomains() {
		return inventoryDetailResDomains;
	}

	public void setInventoryDetailResDomains(
			List<InventoryDetailResDomain> inventoryDetailResDomains) {
		this.inventoryDetailResDomains = inventoryDetailResDomains;
	}
	
	
	
	
	
	
	
}
