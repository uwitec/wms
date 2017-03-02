/**
 * 
 */
package com.leqee.wms.job;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.schedule.job.TaskUtils;
import com.leqee.wms.util.WorkerUtil;

/**
 * @author sszheng
 *
 * Created on 2016年12月13日 下午2:16:13
 */
@Service
public class TagValidityStatusJob {
	private Logger logger = Logger.getLogger(TagValidityStatusJob.class);
	
	private static final int PAGESIZE = 5000;
	private static final int LOOPCOUNT = 5000;
	
	private static final String DEFAULT_EXCEPT_LOCATION_TYPES = Location.LOCATION_TYPE_PURCHASE_SEQ + "," 
			+ Location.LOCATION_TYPE_REPLENISH_SEQ + "," 
			+ Location.LOCATION_TYPE_TRANSIT + "," 
			+ Location.LOCATION_TYPE_VARIANCE_ADD + "," 
			+ Location.LOCATION_TYPE_VARIANCE_MIMUS + "," 
			+ Location.LOCATION_TYPE_VARIANCE_STOCK_ADD + "," 
			+ Location.LOCATION_TYPE_VARIANCE_STOCK_MIMUS;
	
	private static final String DEFAULT_TARGET_VALIDITY_STATUSES = ProductLocation.Validity_STATUS_NORMAL + ","
			+ ProductLocation.Validity_STATUS_UNSALABLE + ","
			+ ProductLocation.Validity_STATUS_WARNING;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private ProductLocationDao productLocationDao;
	
	public void tagValidityStatus(String paramNameValue) {
		
		logger.info("TagValidityStatusJob start: " + paramNameValue);
		Map<String, List<String>> map = getServiceParamsMap(paramNameValue);
		List<String> exceptLocationTypeList = map.get("exceptLocationTypeList");
		List<String> targetValidityStatusList = map.get("targetValidityStatusList");
		
		for(int currentPage = 1; currentPage <= LOOPCOUNT; currentPage++ ) {
			Map<String, List<Long>> classifiedPLIdMap = new HashMap<String, List<Long>>();
			
			PageParameter page = new PageParameter(currentPage, PAGESIZE);
			List<Map<String,Object>> list = productLocationDao.selectNeedTaggedByPage(exceptLocationTypeList,targetValidityStatusList, page);
			if(list.size() == 0) {
				logger.info("TagValidityStatusJob end success");
				break;
			}
			filterAndTagProductLocation(list, classifiedPLIdMap);
			batchUpdateValidityStatus(classifiedPLIdMap);
			
			if(currentPage == LOOPCOUNT) {
				logger.error( new Date() + "TagValidityStatusJob end exception: " + "循环超过" + LOOPCOUNT + "次");
			}
		}
		
	}
	
	/**
	 * @param classifiedPLIdMap
	 */
	private void batchUpdateValidityStatus(Map<String, List<Long>> classifiedPLIdMap) {
		Iterator<String> iter = classifiedPLIdMap.keySet().iterator(); 
		while(iter.hasNext()) {
			String validityStatus = iter.next();
			List<Long> list = classifiedPLIdMap.get(validityStatus);
			if(!list.isEmpty()) {
				productLocationDao.batchUpdateValidityStatus(validityStatus, list);
			}
		}
	}

	private Map<String, List<String>> getServiceParamsMap(String paramNameValue) {
		Map<String, String> paramNameValueMap = TaskUtils.getParamNameValueMap(paramNameValue);
		Map<String, List<String>> serviceParamsMap = new HashMap<String, List<String>>();
		
		String exceptLocationTypes = WorkerUtil.isNullOrEmpty(paramNameValueMap.get("except_location_types")) ? DEFAULT_EXCEPT_LOCATION_TYPES: paramNameValueMap.get("except_location_types");
		String targetValidityStatuses = WorkerUtil.isNullOrEmpty(paramNameValueMap.get("targer_validity_statuses")) ? DEFAULT_TARGET_VALIDITY_STATUSES: paramNameValueMap.get("targer_validity_statuses");
		
		List<String> exceptLocationTypeList = Arrays.asList(exceptLocationTypes.split(","));
		List<String> targetValidityStatusList = Arrays.asList(targetValidityStatuses.split(","));
		
		serviceParamsMap.put("exceptLocationTypeList", exceptLocationTypeList);
		serviceParamsMap.put("targetValidityStatusList", targetValidityStatusList);
		
		return serviceParamsMap;
	}
	
	private void filterAndTagProductLocation(List<Map<String,Object>> list, Map<String, List<Long>> classifiedPLIdMap) {
		 Iterator<Map<String, Object>> iter = list.iterator();
		 while(iter.hasNext()) {
			 Map<String, Object> map = iter.next();
			 Timestamp validity = (Timestamp) map.get("validity");
			 String validityUnit = (String)map.get("validity_unit");
			 String isMaintainWarranty = (String)map.get("is_maintain_warranty");
			 Integer warrantyWarningDays = (Integer)map.get("warranty_warning_days");
			 Integer warrantyUnsalableDays = (Integer)map.get("warranty_unsalable_days");
			 Number validityDays = (Number)map.get("validity_days");
			 Long plId = (Long)map.get("pl_id");
			 
			 String validityStatus = ProductLocation.Validity_STATUS_NORMAL;
			 if(needCheckValidityStatus(isMaintainWarranty)) {
				 validityStatus = ProductLocation.checkValidityStatus(sdf.format(new Date(validity.getTime())), validityDays.intValue(), validityUnit, warrantyWarningDays, warrantyUnsalableDays);
			 }
			 addToClassifiedPLidMap(validityStatus, plId, classifiedPLIdMap);
		 }
	}
	
	/**
	 * @param validityStatus
	 * @param plId
	 * @param classifiedPLIdMap
	 */
	private void addToClassifiedPLidMap(String validityStatus, Long plId, Map<String, List<Long>> classifiedPLIdMap) {
		List<Long> list = classifiedPLIdMap.get(validityStatus);
		if(list == null) {
			list = new ArrayList<Long>();
			classifiedPLIdMap.put(validityStatus, list);
		}
		list.add(plId);
	}

	private boolean needCheckValidityStatus(String isMaintainWarranty) {
		return !WorkerUtil.isNullOrEmpty(isMaintainWarranty) && "Y".equals(isMaintainWarranty);
	}
}
