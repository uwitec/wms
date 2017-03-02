package com.leqee.wms.biz;

import java.util.List;

import com.leqee.wms.entity.TaobaoShopConf;


public interface TaobaoShopConfBiz {
	public List<TaobaoShopConf> selectAllTaobaoShopConf();

	public void deleteTaobaoShopConfById(long taobaoShopConfId);
	
	public void addTaobaoShopConf(TaobaoShopConf taobaoShopConf);

}
