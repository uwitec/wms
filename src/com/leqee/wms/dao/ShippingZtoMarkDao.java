package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.ShippingZtoMark;

public interface ShippingZtoMarkDao {

	List<ShippingZtoMark> getAllRegionsForMark();

	void updateZTOMark(@Param("markId") Integer markId, @Param("mark")  String mark,@Param("marke")   String marke);

	String getMark(@Param("orderId") Integer orderId);

	
}