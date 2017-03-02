package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.TmpProductLocation;

public interface TmpProductLocationDao {

	int insert (TmpProductLocation tmpProductLocation); 
	
	int insertList(List<TmpProductLocation> tmpProductLocationList);
	
	int update(@Param("tmpPlId") Integer tmpPlId,
			@Param("customerId") Integer customerId,
			@Param("physicalWarehouseId") Integer physicalWarehouseId,
			@Param("actionUser") String actionUser,
			@Param("transferStatus") String transferStatus,
			@Param("transferNote") String transferNote);
}
