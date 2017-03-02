package com.leqee.wms.biz.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.KeyInfoBiz;
import com.leqee.wms.dao.KeyInfoDao;

@Service(value="keyInfoBiz")
public class KeyInfoBizImpl implements KeyInfoBiz {

	@Autowired
	KeyInfoDao keyInfoDao;
	
	@Override
	public Long selectKeyValueByKeyName(Map map) {
		
		return keyInfoDao.selectKeyValueByKeyName(map);
	}

	@Override
	public void updateKeyValueByKeyName(String keyName, int poolSize) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("keyName", keyName);
		map.put("poolSize", poolSize);
		
		keyInfoDao.updateKeyValueByKeyName(map);
	}

	@Override
	public void insertKeyName(String keyName) {
		// TODO Auto-generated method stub
		keyInfoDao.insertKeyName(keyName);
	}

}
