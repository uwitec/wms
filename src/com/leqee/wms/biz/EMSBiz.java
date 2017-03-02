package com.leqee.wms.biz;

import java.util.Map;

import javax.jws.WebService;

/**
 * EMS号段相关接口
 * @author hzhang1
 * @date 2017-1-19
 * @version 1.0.0
 */
@WebService
public interface EMSBiz {

	/*获取面单号段*/
	public void batchApplyEMSTrackingNumber(Map shippingApp) throws Exception;

	public void reportEMSTrackingNumber(Map shippingInfo) throws Exception;
}
