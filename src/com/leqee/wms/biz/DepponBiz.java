package com.leqee.wms.biz;

import java.util.Map;

import javax.jws.WebService;

import com.leqee.wms.entity.ShippingApp;

@WebService
public interface DepponBiz {

	public void reportDepponTrackingNumber(Map shippingInfo) throws Exception;

}