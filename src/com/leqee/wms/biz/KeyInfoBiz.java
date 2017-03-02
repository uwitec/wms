package com.leqee.wms.biz;

import java.util.Map;

public interface KeyInfoBiz {

	Long selectKeyValueByKeyName(Map map);

	void updateKeyValueByKeyName(String keyName, int poolSize);

	void insertKeyName(String keyName);

}
