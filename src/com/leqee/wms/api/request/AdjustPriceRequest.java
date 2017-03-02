package com.leqee.wms.api.request;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.response.AdjustPriceResponse;

public class AdjustPriceRequest extends BaseLeqeeRequest<AdjustPriceResponse>{

	

	private String oms_order_sn;
	
	private String sku_code;
	
	private String provider_code;   //供应商code
	
	private BigDecimal unit_cost;
	
	private Integer oms_order_goods_sn;
	
	private Date arrival_time;  //到货时间
	
	public String getOms_order_sn() {
		return oms_order_sn;
	}

	public void setOms_order_sn(String oms_order_sn) {
		this.oms_order_sn = oms_order_sn;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public BigDecimal getUnit_cost() {
		return unit_cost;
	}

	public void setUnit_cost(BigDecimal unit_cost) {
		this.unit_cost = unit_cost;
	}

	public Integer getOms_order_goods_sn() {
		return oms_order_goods_sn;
	}

	public void setOms_order_goods_sn(Integer oms_order_goods_sn) {
		this.oms_order_goods_sn = oms_order_goods_sn;
	}
	
	public String getProvider_code() {
		return provider_code;
	}

	public void setProvider_code(String provider_code) {
		this.provider_code = provider_code;
	}

	public Date getArrival_time() {
		return arrival_time;
	}

	public void setArrival_time(Date arrival_time) {
		this.arrival_time = arrival_time;
	}

	@Override
	public String getApiMethodName() {
		return METHOD_ADJUST_PRICE_REQUEST;
	}

	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<AdjustPriceResponse> getResponseClass() {
		return AdjustPriceResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}

}
