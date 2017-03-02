package com.leqee.wms.dao;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.Task;
import com.leqee.wms.entity.UserActionTask;

public interface UserActionTaskDao {
	
	public int insert(UserActionTask userActionTask);


	public void insertList(@Param("list")List<UserActionTask > userActionTaskList);

	public Integer batchInsert(@Param("userActionTaskList")List<UserActionTask> userActionTaskList);

	
}
