package com.leqee.wms.biz.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.TaobaoShopConfBiz;
import com.leqee.wms.controller.TaobaoShopConfController;
import com.leqee.wms.dao.TaobaoShopConfDao;
import com.leqee.wms.entity.TaobaoShopConf;

@Service
public class TaobaoShopConfBizImpl implements TaobaoShopConfBiz {
	@Autowired
	TaobaoShopConfDao taobaoShopConfDao;
	
	
	private Logger logger = Logger.getLogger(TaobaoShopConfBizImpl.class);


	
	@Override
	public List<TaobaoShopConf> selectAllTaobaoShopConf() {
		return taobaoShopConfDao.selectAllTaobaoShopConf();
	}
	
	
	
	@Override
	public void deleteTaobaoShopConfById(long taobaoShopConfId) {
		
		taobaoShopConfDao.deleteTaobaoShopConfById(taobaoShopConfId);
		
		if(taobaoShopConfId == 9){
			taobaoShopConfDao.updateInventoryItem(2);
			try {
				Thread.sleep(10000);
				throw new InterruptedException();
			} catch (InterruptedException e) {
				logger.error("jvm unknow exception ", e);
				e.printStackTrace();
			}
			throw new RuntimeException("let's roll back");
			
		}else{
			taobaoShopConfDao.updateInventoryItem(3);
		}
		
		// 1、测试调用dao层另一个方法抛异常是否能够回滚(测试通过，已回滚)
//		taobaoShopConfDao.insertTaobaoShopConf(new TaobaoShopConf());
		
		// 2、测试调用biz层另一个方法抛异常是否能够回滚(测试通过，已回滚)
//		addTaobaoShopConf(new TaobaoShopConf());
		
		
		
	}

	@Override
	public void addTaobaoShopConf(TaobaoShopConf taobaoShopConf) {
		
		taobaoShopConfDao.insertTaobaoShopConf(taobaoShopConf);
	}
	
	
	
	
	





	
	
	
}
