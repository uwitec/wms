package com.leqee.wms.dao;

import java.util.List;

import com.leqee.wms.entity.TaobaoShopConf;
public interface TaobaoShopConfDao {
	
	public List<TaobaoShopConf> selectAllTaobaoShopConf();

	public void deleteTaobaoShopConfById(long taobaoShopConfId);
	
	public void insertTaobaoShopConf(TaobaoShopConf taobaoShopConf);

	
	public int updateInventoryItem(int num);
	
}





