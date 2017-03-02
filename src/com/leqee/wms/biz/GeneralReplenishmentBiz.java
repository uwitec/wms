package com.leqee.wms.biz;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;

/**
 * 一般补货任务 调度调用类
 */
public interface GeneralReplenishmentBiz {
	
	String generalReplenishTask(Integer physical_warehouse_id,
			Integer customer_id, String boxPiece, int productId);
}
