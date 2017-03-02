
package com.leqee.wms.biz.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.LocationBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.OrderGoodsBiz;
import com.leqee.wms.biz.SFBiz;
import com.leqee.wms.biz.ShipmentBiz;
import com.leqee.wms.biz.ShipmentDetailBiz;
import com.leqee.wms.biz.ZTOBiz;
import com.leqee.wms.dao.BatchPickDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.LocationDao;
import com.leqee.wms.dao.OrderDao;
import com.leqee.wms.dao.OrderGoodsDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.ShipmentDetailDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.UserActionOrderDao;
import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.InventoryItem;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ShipmentDetail;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.UserActionOrder;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;


@Service
public class LocationBizImpl implements LocationBiz {
	Logger logger = Logger.getLogger(LocationBizImpl.class);
	@Autowired
	LocationDao locationDao;
	
	public void updateLocationBarByLocationId (Integer locationId,String locationBar){
		if(locationDao.updateLocationBarByLocationId(locationId,locationBar)<=0){
			throw new RuntimeException("updateLocationBarByLocationId更新失败，受影响行数为0");
		}
	}
	public List<Map<String, Object>> selectByLocationId(Map<String, Object>map){
		return locationDao.selectByLocationId(map);
	}
	
	

}
