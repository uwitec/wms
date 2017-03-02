/*
 * 商品相关（同步商品，查询商品等）
 */
package com.leqee.wms.api.biz;

import java.util.HashMap;
import com.leqee.wms.api.request.SyncProductRequest;
import com.leqee.wms.entity.Product;
/**
 * @author hzhang1
 * @date 2016-2-26
 * @version 1.0.0
 */
public interface ProductApiBiz {
	
	HashMap<String,Object> syncProduct(SyncProductRequest productAddRequest,Integer customerId);
	
	HashMap<String,Object> checkProduct(Product product);
	
	public Integer getcustomerIdByAppKey(String app_key);
}
