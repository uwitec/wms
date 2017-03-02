package com.leqee.wms.dao;

import java.util.Map;

import com.leqee.wms.entity.ShippingYundaTrackingNumber;

public interface ShippingYundaTrackingNumberDao {

	void insertTrackingInfoForOther(Integer shipmentId, String trackingNumber,
			String packagePosition, String packageNo, String station,
			String stationNo, String senderBranchNo, String senderBranch,
			String latticeMouthNo);

	Map selectPrintInfoByTrackingNumber(String trackingNumber);
	
}