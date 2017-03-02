package com.leqee.wms.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.UserActionOrder;

public interface UserActionOrderDao {

	/**
	 * @author Jarvis
	 * @Description 插入一条记录
	 * 
	 * @param userActionOrder a UserActionOrder: UserActionOrder实体对象
	 * 
	 * @return effectRows an int: 影响行数
	 * */
	public int insert(UserActionOrder userActionOrder);

	public void insertList(HashMap<String, Object> parmMap);

	public void batchInsert(@Param("userActionOrderList")List<UserActionOrder> userActionOrderList);

}
