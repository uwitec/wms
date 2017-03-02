package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.OrderReserveInventoryMapping;

public interface OrderReserveInventoryMappingDao {


    int deleteByPrimaryKey(Integer mapping_id);

    int insert(OrderReserveInventoryMapping record);

    int insertSelective(OrderReserveInventoryMapping record);

    OrderReserveInventoryMapping selectByPrimaryKey(Integer mapping_id);

    int updateByPrimaryKeySelective(OrderReserveInventoryMapping record);

    int updateByPrimaryKey(OrderReserveInventoryMapping record);

	void updateReserveNumBack(Integer mappingId, Integer backNum);

	void batchInsert(@Param("list")List<OrderReserveInventoryMapping> orimInsertList);
}