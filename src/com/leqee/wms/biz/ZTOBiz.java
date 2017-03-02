package com.leqee.wms.biz;

import java.util.Map;

import javax.jws.WebService;


@WebService
public interface ZTOBiz {
	/*获取面单号段*/
	public void batchApplyZTOTrackingNumber(Map shippingApp) throws Exception;

	public String getZTOMarkForSingleOrder(Map<String, Object> singleOrder);

	public void reportZTOTrackingNumber(Map htShippingInfo) throws Exception ;

}