package com.leqee.wms.biz;

import java.util.List;
import java.util.Map;

import com.leqee.wms.entity.OrderGoods;

public interface LocationBiz {

	public void updateLocationBarByLocationId (Integer locationId,String locationBar);
	public List<Map<String, Object>> selectByLocationId(Map<String, Object>map);

}
