package com.leqee.wms.biz;

import java.util.Map;

import javax.jws.WebService;

import com.leqee.wms.entity.ShippingApp;

@WebService
public interface HTBiz {
	/*获取面单号段*/
	public void batchApplyHTTrackingNumber(Map shippingApp) throws Exception;

	public void reportHTTrackingNumber(Map htShippingInfo) throws Exception;

	public String getHTMarkForSingleOrder(Map<String, Object> singleOrder); 
}