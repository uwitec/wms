package com.leqee.wms.biz;

import java.math.BigDecimal;
import java.util.Map;

import javax.jws.WebService;

@WebService
public interface SFBiz {

	public Map applyOneTrackingNumberByShipmentId(Integer orderId,Integer shipmentId, Map appInfo, Map receiveInfo);

	/* to do sf 保价费用计算
	public BigDecimal getShipmentOrderAmount(Integer orderId);

	public BigDecimal getLeqeeElecEduInsurance(Integer orderId);
	*/

}