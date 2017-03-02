package com.leqee.wms.biz;

import java.util.Map;

import javax.jws.WebService;

@WebService
public interface YTOBiz {

	Map applyOneTrackingNumberByShipmentId(Integer orderId, Integer shipmentId,Map appInfo, Map receiveInfo);

}
