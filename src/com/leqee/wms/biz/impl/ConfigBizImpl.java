/**
 * 
 */
package com.leqee.wms.biz.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.ConfigBiz;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.util.WorkerUtil;

/**
 * @author sszheng
 *
 * Created on 2016年12月2日 上午10:26:32
 */
@Service
public class ConfigBizImpl implements ConfigBiz {
	@Autowired
	private ConfigDao configDao;
	@Autowired
	private CacheManager cacheManager;
	
	private Set<String> getCacheLimitIpSet() {
		Cache<String, Set<String>> ipLimitCache = cacheManager.getCache("limitIpCache");
		return ipLimitCache.get("limit_ip_set");
	}
	
	private void setCacheLimitIpSet(Set<String> limitIpSet) {
		Cache<String, Set<String>> ipLimitCache = cacheManager.getCache("limitIpCache");
		ipLimitCache.put("limit_ip_set", limitIpSet);
	}

	public Set<String> getLimitIpSet() {
		Set<String> limitIpSet = getCacheLimitIpSet();
		if(!WorkerUtil.isNullOrEmpty(limitIpSet)) {
			return limitIpSet;
		}
		limitIpSet = new HashSet<String>();
		String limitIps = configDao.getConfigValueByConfigName("LIMIT_IPS");
		if(!WorkerUtil.isNullOrEmpty(limitIps) ) {
			CollectionUtils.addAll(limitIpSet, limitIps.split(","));
			setCacheLimitIpSet(limitIpSet);
		}                                                                                                                                                                  
		return limitIpSet;
	}
	
}
