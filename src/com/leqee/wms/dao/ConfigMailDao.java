package com.leqee.wms.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.ConfigMail;

public interface ConfigMailDao {

	String getToMailsByType(@Param("type")String mailTypeInventeryNotEnough, @Param("physical_warehouse_id")int physical_warehouse_id, @Param("customer_id")int customer_id);

	HashMap getCountConfigs(@Param("physical_warehouse_id")int physical_warehouse_id, @Param("customer_id")int customer_id,@Param("type") String type);

	void update(@Param("mail")String mail,@Param("physical_warehouse_id") int physical_warehouse_id, @Param("customer_id")int customer_id,
			@Param("type")String type);

	void insert(@Param("mail")String mail,@Param("physical_warehouse_id") int physical_warehouse_id, @Param("customer_id")int customer_id,
			@Param("type")String type);

	List<ConfigMail> getAllConfigMailTypeAndNames(); 

}
