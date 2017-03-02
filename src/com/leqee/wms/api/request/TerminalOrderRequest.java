package com.leqee.wms.api.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.request.domain.TerminalGoodsReqDomain;
import com.leqee.wms.api.response.TerminalOrderResponse;

public class TerminalOrderRequest extends BaseLeqeeRequest<TerminalOrderResponse> {

	private String oms_order_sn;

	private List<TerminalGoodsReqDomain> goodsReqDomains = new ArrayList<TerminalGoodsReqDomain>();

	public String getOms_order_sn() {
		return oms_order_sn;
	}

	public void setOms_order_sn(String oms_order_sn) {
		this.oms_order_sn = oms_order_sn;
	}

	public List<TerminalGoodsReqDomain> getGoodsReqDomains() {
		return goodsReqDomains;
	}

	public void setGoodsReqDomains(List<TerminalGoodsReqDomain> goodsReqDomains) {
		this.goodsReqDomains = goodsReqDomains;
	}

	@Override
	public String getApiMethodName() {
		return METHOD_TERMINAL_ORDER_REQUEST;
	}

	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<TerminalOrderResponse> getResponseClass() {
		return TerminalOrderResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub

	}

}
