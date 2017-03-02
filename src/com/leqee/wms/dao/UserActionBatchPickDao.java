package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.UserActionBatchPick;

public interface UserActionBatchPickDao {
	
	/**
	 * @author Jarvis
	 * @Description 插入一条记录
	 * 
	 * @param userActionBatchPick a UserActionBatchPick: UserActionBatchPick实体对象
	 * 
	 * @return effectRows an int: 影响行数
	 * */
	public int insert(UserActionBatchPick userActionBatchPick);

	public void batchInsert(@Param("userActionBatchPickList")List<UserActionBatchPick> userActionBatchPickList);
}
