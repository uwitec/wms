package com.leqee.wms.dao;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.ShippingTrackingNumberRepository;

public interface ShippingTrackingNumberRepositoryDao {
	void insertTrackingNumer(@Param("appId")Integer app_id,@Param("trackingNumber")String mailno,@Param("status") String status, @Param("createdUser")String created_user, @Param("createdTime")Date created_time);

	String searchMaxTrackingNumber(@Param("appId") Integer appId);
	
	String getTrackingNumber(@Param("appId") Integer appId);

	void updateRepositoryTrackingNumber(String trackingNumber, String status);


	List<String> getTrackingNumbers(Integer shippingAppId,Integer trackingNumberSize);

	Integer updateRepositoryTrackingNumbers(@Param("trackingNumbers")List<String> trackingNumbers,
			@Param("status")String status);

	List<Map> selectShippingInfoList(String shippingCode, Double hours);

	List<String> selectReUseTns();

	void batchInsertTnRepository(@Param("list")List<ShippingTrackingNumberRepository> shippingTrackingNumberRepositoryList);

	List<Map> selectBatchAppId();

	Map selectShippingNameByAppId(Integer appId);

	Map selectWarehouseNameByIds(@Param("list")List<Integer> wid);
}