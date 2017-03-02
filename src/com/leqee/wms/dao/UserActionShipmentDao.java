package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.UserActionShipment;

public interface UserActionShipmentDao {
	public void insertUserActionShipmentRecord(UserActionShipment userActionShipment);

	public void batchInsertUserActionShipmentRecord(
			@Param("userActionShipmentList")List<UserActionShipment> userActionShipmentList);
}