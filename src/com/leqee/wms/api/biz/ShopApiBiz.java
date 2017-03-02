/*
 * 省市区地址同步
 */
package com.leqee.wms.api.biz;

import java.util.HashMap;
import com.leqee.wms.api.request.SyncProductRequest;
import com.leqee.wms.api.request.SyncRegionRequest;
import com.leqee.wms.api.request.SyncShopRequest;
import com.leqee.wms.entity.Product;
/**
 * @author hzhang1
 * @date 2016-2-26
 * @version 1.0.0
 */
public interface ShopApiBiz {
	
	HashMap<String,Object> syncShop(SyncShopRequest shopRequest);
	
}
