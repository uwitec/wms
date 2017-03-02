package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.ScheduleQueueCount;
import com.leqee.wms.page.PageParameter;

public interface ScheduleQueueCountDao {

	List<ScheduleQueueCount> queryJob(@Param("physical_warehouse_id")int physical_warehouse_id, @Param("customer_id")int customer_id, @Param("task_type")String task_type,
			@Param("time")String time, @Param("status")String status);

	void insert(@Param("sqc")ScheduleQueueCount sqc);

	List<ScheduleQueueCount> queryJobByPage(@Param("physical_warehouse_id")int physical_warehouse_id, @Param("customer_id")int customer_id, @Param("task_type")String task_type,
			@Param("time")String time, @Param("status")String status,
			@Param("page")PageParameter page);

	ScheduleQueueCount getScheduleQueueCountByQueueId(@Param("queue_id")int queue_id);

	List<Map> getProductLoactionByCount(@Param("physical_warehouse_id")int physical_warehouse_id, @Param("customer_id")int customer_id,
			@Param("location_type")String location_type, @Param("hide_batch_sn")int hide_batch_sn,
			@Param("from_location_barcode")String from_location_barcode, @Param("to_location_barcode")String to_location_barcode, @Param("barcode")String barcode);

	void updateSqcStatus(@Param("queue_id")int queue_id, @Param("status")String status, @Param("num")int num);

	List<Map> getProductLoactionByCountByHand(@Param("physical_warehouse_id")int physical_warehouse_id,
			 @Param("customer_id")Integer customer_id, @Param("location_barcode")String location_barcode, @Param("barcode")String barcode,@Param("status") String status, @Param("warehouse_id")Integer warehouse_id);

	int getProductLoactionByCountByHandNotIn(@Param("physical_warehouse_id")int physical_warehouse_id,
			 @Param("customer_id")Integer customer_id, @Param("location_barcode")String location_barcode, @Param("barcode")String barcode,@Param("status") String status, @Param("warehouse_id")Integer warehouse_id);

}
