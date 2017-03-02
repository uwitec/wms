package com.leqee.wms.api.convert;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leqee.wms.api.request.SyncRmaOrderRequest;
import com.leqee.wms.api.request.domain.OrderGoodsReqDomain;
import com.leqee.wms.convert.AbstractConvert;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.RegionDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.ShopDao;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Region;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.Shop;
import com.leqee.wms.util.WorkerUtil;


@Component
public class SyncRmaOrderRequest2OrderInfoConvert extends AbstractConvert<SyncRmaOrderRequest, OrderInfo>{

	
	private Logger logger = Logger.getLogger(SyncSaleOrderRequest2OrderInfoConvert.class);
	
	@Autowired
	OrderGoodsReqDomain2OrderGoodsConvert orderGoodsReqDomain2OrderGoodsConvert;
	@Autowired
	ShippingDao shippingDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	RegionDao regionDao;
	@Autowired
	ShopDao shopDao;
	
	@Override
	public OrderInfo covertToTargetEntity(SyncRmaOrderRequest request) {
		OrderInfo orderInfo = new OrderInfo();
	
		orderInfo.setOms_shop_id(request.getDistributor_id().toString());
		orderInfo.setBuyer_name(WorkerUtil.getStringValue(request.getBuyer_name()));
		orderInfo.setBuyer_note(WorkerUtil.getStringValue(request.getBuyer_note()));
		orderInfo.setBuyer_phone(WorkerUtil.getStringValue(request.getBuyer_phone()));
		
		orderInfo.setCity_name(WorkerUtil.getStringValue(request.getCity_name()));
		orderInfo.setDistrict_name(WorkerUtil.getStringValue(request.getDistrict_name()));
		orderInfo.setProvince_name(WorkerUtil.getStringValue(request.getProvince_name()));
		
		orderInfo.setOrder_source(WorkerUtil.getStringValue(request.getOrder_source()));
		orderInfo.setOrder_status("");
		orderInfo.setOrder_time(request.getOrder_time());
		orderInfo.setPay_time(request.getPay_time());
		orderInfo.setOrder_type(WorkerUtil.getStringValue(request.getOrder_type()));
		
		orderInfo.setPay_amount(WorkerUtil.getDecimalValue(request.getPay_amount()));
		
		orderInfo.setCollecting_payment_amount(WorkerUtil.getDecimalValue(request.getCollecting_payment_amount()));
		orderInfo.setCurrency(WorkerUtil.getStringValue(request.getCurrency()));
		orderInfo.setDeclaring_value_amount(WorkerUtil.getDecimalValue(request.getDeclaring_value_amount()));
		orderInfo.setDiscount_amount(WorkerUtil.getDecimalValue(request.getDiscount_amount()));
		orderInfo.setEmail(WorkerUtil.getStringValue(request.getEmail()));
		
		orderInfo.setInvoice_amount(WorkerUtil.getDecimalValue(request.getInvoice_amount()));
		orderInfo.setInvoice_note(WorkerUtil.getStringValue(request.getInvoice_note()));
		orderInfo.setInvoice_title(WorkerUtil.getStringValue(request.getInvoice_title()));
		
		orderInfo.setIs_payment_collected(WorkerUtil.getStringValue(request.getIs_payment_collected()));
		orderInfo.setIs_value_declared(WorkerUtil.getStringValue(request.getIs_value_declared()));
		orderInfo.setMobile_number(WorkerUtil.getStringValue(request.getMobile_number()));
		orderInfo.setNick_name(WorkerUtil.getStringValue(request.getNick_name()));
		
		orderInfo.setNote(WorkerUtil.getStringValue(request.getNote()));
		orderInfo.setSeller_note(WorkerUtil.getStringValue(request.getSeller_note()));
		
		orderInfo.setOms_order_sn(WorkerUtil.getStringValue(request.getOms_order_sn()));
		orderInfo.setOms_order_type(WorkerUtil.getStringValue(request.getOms_order_type()));
		
		orderInfo.setGoods_amount(WorkerUtil.getDecimalValue(request.getGoods_amount()));
		orderInfo.setPhone_number(WorkerUtil.getStringValue(request.getPhone_number()));
		orderInfo.setPostal_code(WorkerUtil.getStringValue(request.getPostal_code()));
		orderInfo.setReceive_name(WorkerUtil.getStringValue(request.getReceive_name()));
		orderInfo.setSex(WorkerUtil.getStringValue(request.getSex()));
		orderInfo.setShipping_address(WorkerUtil.getStringValue(request.getShipping_address()));
		orderInfo.setShipping_amount(WorkerUtil.getDecimalValue(request.getShipping_amount()));
		orderInfo.setBrand_name(WorkerUtil.getStringValue(request.getBrand_name()));
		orderInfo.setShop_name(WorkerUtil.getStringValue(request.getShop_name()));
		orderInfo.setShop_order_sn(WorkerUtil.getStringValue(request.getShop_order_sn()));
		
		orderInfo.setWarehouse_id(request.getWarehouse_id());
		
		orderInfo.setTms_company(WorkerUtil.getStringValue(request.getTms_company()));
		orderInfo.setTms_contact(WorkerUtil.getStringValue(request.getTms_contact()));
		orderInfo.setTms_mobile(WorkerUtil.getStringValue(request.getTms_mobile()));
		orderInfo.setTms_shipping_no(WorkerUtil.getStringValue(request.getTms_shipping_no()));
		
		
		orderInfo.setOrder_status(OrderInfo.ORDER_STATUS_ACCEPT);
		orderInfo.setProvider_code("");
		orderInfo.setIs_reserved("N");
		orderInfo.setProvider_order_type("");
		orderInfo.setProvider_name("");
		orderInfo.setCreated_time(new Date());
		orderInfo.setCreated_user("system");
		orderInfo.setLast_updated_time(new Date());
		orderInfo.setLast_updated_user("system");
		//合并订单和oms_shipment_sn
		orderInfo.setIs_merge_order(WorkerUtil.getStringValue(request.getIs_merge_order()));
		orderInfo.setSlave_oms_order_sns(WorkerUtil.getStringValue(request.getSlave_oms_order_sns()));
		orderInfo.setOms_shipment_sn(WorkerUtil.getStringValue(request.getOms_shipment_sn()));
		
		
		//退货订单相关初始化
		orderInfo.setParent_oms_order_sn(WorkerUtil.getStringValue( request.getParent_oms_order_sn()));
		orderInfo.setRoot_oms_order_sn(WorkerUtil.getStringValue( request.getRoot_oms_order_sn()));
		
		OrderInfo order = orderInfoDao.selectByOmsOrderSn(orderInfo.getParent_oms_order_sn());
		if(!WorkerUtil.isNullOrEmpty(order)){
			orderInfo.setParent_order_id(order.getOrder_id());
		}
		order = orderInfoDao.selectByOmsOrderSn(orderInfo.getRoot_oms_order_sn());
		if(!WorkerUtil.isNullOrEmpty(order)){
			orderInfo.setRoot_order_id(order.getOrder_id());
		}
		
		//初始化好shippingId
		Shipping shipping = shippingDao.selectByShippingCode(request.getShipping_code());
		
		if( !WorkerUtil.isNullOrEmpty(shipping) ){
			orderInfo.setShipping_id(shipping.getShipping_id());
		}
		//省市ID初始化
		String provinceName = orderInfo.getProvince_name() ;
		String cityName = orderInfo.getCity_name() ;
		
		if( !WorkerUtil.isNullOrEmpty(provinceName) ){
			provinceName = provinceName .replaceAll("省", "").replaceAll("市", "");
			Region region = regionDao.selectByName(provinceName);
			if(!WorkerUtil.isNullOrEmpty(region)){
				if((int)(region.getRegion_type()) == 1){
					orderInfo.setProvince_id((int)region.getRegion_id()  );
				}
			}
		}
		if( !WorkerUtil.isNullOrEmpty(cityName) ){
			cityName = cityName .replaceAll("市", "").replaceAll("区", "").replaceAll("县", "");
			Region region = regionDao.selectByName(cityName);
			if(!WorkerUtil.isNullOrEmpty(region) ){
				int parentId =  region.getParent_id();
				int provinceId = orderInfo.getProvince_id() == null? -1 : orderInfo.getProvince_id() ;
				if(parentId == provinceId ){
					if((int)(region.getRegion_type()) == 2){
						orderInfo.setCity_id((int)region.getRegion_id());
					}
				}
			}
		}
		
		
		
		
		orderInfo.setOrder_type(OrderInfo.ORDER_TYPE_RETURN);  //退货订单类型
		
		//request的订单商品转化为orderInfo的订单商品
		List<OrderGoodsReqDomain> orderGoodsDomainList = request.getOrderGoodsReqDomainList();
		orderInfo.setOrderGoodsList(orderGoodsReqDomain2OrderGoodsConvert.covertToTargetEntity(orderGoodsDomainList));
		return orderInfo;
	}

	@Override
	public SyncRmaOrderRequest covertToSourceEntity(OrderInfo orderInfo) {
		// TODO Auto-generated method stub
		return null;
	}


}
