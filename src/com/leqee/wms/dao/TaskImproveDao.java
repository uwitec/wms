package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.api.response.domain.VarianceImproveTaskResDomain;
import com.leqee.wms.entity.TaskCountProductLocation;
import com.leqee.wms.entity.TaskImprove;
import com.leqee.wms.page.PageParameter;

public interface TaskImproveDao {

	void batchInsert(@Param("list")List<TaskImprove> taskImproveList);

	void insertTcplList(@Param("list")List<TaskCountProductLocation> tcplList);

	List<VarianceImproveTaskResDomain> selectVarianceImproveTaskResDomainList(
			Map<String, Object> paramsMap);

	List<Map> getHistoryTaskImprove(@Param("physical_warehouse_id")int physical_warehouse_id, @Param("customer_id")int customer_id,
			@Param("start")String start, @Param("end")String end);

	List<Map> getHistoryTaskImproveByPage(@Param("physical_warehouse_id")int physical_warehouse_id,
			 @Param("customer_id")int customer_id, @Param("start")String start,  @Param("end")String end, @Param("page") PageParameter page);

}
