package com.leqee.wms.biz;

import java.util.Map;

import javax.jws.WebService;

import com.leqee.wms.entity.ShippingApp;

@WebService
public interface STOBiz {
	/*获取面单号段*/
	public void batchApplySTOTrackingNumber(Map shippingApp) throws Exception;

	public void reportSTOTrackingNumber(Map shippingInfo) throws Exception;

}