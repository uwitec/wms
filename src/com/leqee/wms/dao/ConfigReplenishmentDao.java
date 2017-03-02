package com.leqee.wms.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;


import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.Hardware;
import com.leqee.wms.entity.ProductLocation;

public interface ConfigReplenishmentDao {

	List<ConfigReplenishment> selectConfigByJob(@Param("physicalWarehouseId")Integer physicalWarehouseId,
			@Param("customerId")Integer customerId, @Param("productId")Integer productId, @Param("locationType")String locationType);

}
