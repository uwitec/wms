package com.leqee.wms.biz;

import java.util.Map;

import javax.jws.WebService;

import com.leqee.wms.entity.ShippingApp;

@WebService
public interface YDBiz {

	public Map applyOneTrackingNumberByShipmentId(Integer orderId,Integer shipmentId, Map appInfo, Map receiveInfo,Integer warehouseId);

}