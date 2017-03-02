package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.UserActionOrderPrepack;



public interface UserActionOrderPrepackDao {

	void batchInsert(@Param("list")List<UserActionOrderPrepack> userActionOrderPrepackList);
    
	void insert(UserActionOrderPrepack userActionOrder);
}
