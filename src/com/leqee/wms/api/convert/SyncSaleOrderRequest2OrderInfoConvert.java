package com.leqee.wms.api.convert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leqee.wms.api.request.SyncSaleOrderRequest;
import com.leqee.wms.api.request.domain.OrderGoodsReqDomain;
import com.leqee.wms.biz.OrderPrepackBiz;
import com.leqee.wms.convert.AbstractConvert;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.OrderPrepackDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.RegionDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.ShopDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderGoodsOms;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductPrepackage;
import com.leqee.wms.entity.Region;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.Shop;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.util.WorkerUtil;


@Component
public class SyncSaleOrderRequest2OrderInfoConvert extends AbstractConvert<SyncSaleOrderRequest, OrderInfo>{
	private Logger logger = Logger.getLogger(SyncSaleOrderRequest2OrderInfoConvert.class);
	

	@Autowired
	OrderGoodsReqDomain2OrderGoodsConvert orderGoodsReqDomain2OrderGoodsConvert;
	@Autowired
	OrderGoodsReqDomain2OrderGoodsOmsConvert orderGoodsReqDomain2OrderGoodsOmsConvert;
	@Autowired
	OrderGoodsOms2OrderGoodsConvert orderGoodsOms2OrderGoodsConvert;
	@Autowired
	ShippingDao shippingDao;
	@Autowired
	RegionDao regionDao;
	@Autowired
	WarehouseDao warehouseDao;
	@Autowired
	OrderPrepackBiz orderPrepackBiz;
	@Autowired
	ProductDao productDao;
	@Autowired
	ConfigDao configDao;
	@Autowired
	ShopDao shopDao;
	@Autowired
	OrderPrepackDao orderPrepackDao;
	
	
	
	
	
	@Override
	public OrderInfo covertToTargetEntity(SyncSaleOrderRequest request ) {
		logger.info("covertToTargetEntity----------------------------");
		OrderInfo orderInfo = new OrderInfo();
		//根据shop表中的shop_id去设置orderinfo
		if(OrderInfo.ORDER_TYPE_SALE.equals(request.getOrder_type())){
			orderInfo.setOms_shop_id(request.getDistributor_id().toString());
		}else {
				orderInfo.setOms_shop_id("0");
			}
		orderInfo.setBuyer_name(WorkerUtil.getStringValue(request.getBuyer_name()));
		orderInfo.setBuyer_note(WorkerUtil.getStringValue(request.getBuyer_note()));
		orderInfo.setBuyer_phone(WorkerUtil.getStringValue(request.getBuyer_phone()));
		
		orderInfo.setCity_name(WorkerUtil.getStringValue(request.getCity_name()));
		orderInfo.setDistrict_name(WorkerUtil.getStringValue(request.getDistrict_name()));
		orderInfo.setProvince_name(WorkerUtil.getStringValue(request.getProvince_name()));
		
		orderInfo.setOrder_source(WorkerUtil.getStringValue(request.getOrder_source()));
		orderInfo.setOrder_status(OrderInfo.ORDER_STATUS_ACCEPT);
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
		List<OrderGoodsReqDomain> orderGoodsDomainList = request.getOrderGoodsReqDomainList();
		
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
		
		orderInfo.setProvider_name(WorkerUtil.getStringValue(request.getProvider_name()));
		orderInfo.setProvider_code(WorkerUtil.getStringValue(request.getProvider_code()));
		orderInfo.setProvider_order_type(WorkerUtil.getStringValue(request.getProvider_order_type()));
		orderInfo.setProvince_name(WorkerUtil.getStringValue(request.getProvince_name()));
		
		//合并订单和oms_shipment_sn
		orderInfo.setIs_merge_order(WorkerUtil.getStringValue(request.getIs_merge_order()));
		orderInfo.setSlave_oms_order_sns(WorkerUtil.getStringValue(request.getSlave_oms_order_sns()));
		orderInfo.setOms_shipment_sn(WorkerUtil.getStringValue(request.getOms_shipment_sn()));
		orderInfo.setOrder_level(request.getOrder_level());//wmsclient普通订单的默认order_level为1，所以orderInfo的默认order_level也为1
		
		//若该订单pay_time早于order_time48小时，说明该订单同步那一步卡的时间太久，则该订单order_level在wmsclient中已设为3，那么以下关于order_time的优先级判断跳过
		if(!WorkerUtil.isNullOrEmpty(request.getOrder_level())){
			if(request.getOrder_level()!=3){
				String medium_string = configDao.getConfigValueByConfigName("ORDER_LEVEL_MEDIUM");
				String high_string = configDao.getConfigValueByConfigName("ORDER_LEVEL_HIGH");
				Integer high = 999999;
				Integer medium = 999999;
				if(!WorkerUtil.isNullOrEmpty(medium_string)&&!WorkerUtil.isNullOrEmpty(high_string)){
					high = Integer.valueOf(high_string);
					medium = Integer.valueOf(medium_string);
				}
				Date now = new Date();
				Date orderTime = request.getOrder_time();
				if(!WorkerUtil.isNullOrEmpty(orderTime)&&!WorkerUtil.isNullOrEmpty(high)&&!WorkerUtil.isNullOrEmpty(medium)){
					long timeSpan = now.getTime() - orderTime.getTime();
					if(timeSpan > high*60*60*1000L){
						orderInfo.setOrder_level(3);
					}else if(timeSpan > medium*60*60*1000L){
						orderInfo.setOrder_level(2);
					}
				}
			}
		}
		
		
		orderInfo.setAftersale_phone(WorkerUtil.getStringValue(request.getAftersale_phone()));
		orderInfo.setIs_reserved("N");
		
		orderInfo.setCreated_time(new Date());
		orderInfo.setCreated_user("system");
		orderInfo.setLast_updated_time(new Date());
		orderInfo.setLast_updated_user("system");
		
		//销售订单需要初始化好shippingId
		if(OrderInfo.ORDER_TYPE_SALE.equals(orderInfo.getOrder_type())  ){
			Shipping shipping = shippingDao.selectByShippingCode(request.getShipping_code());
			
			if( !WorkerUtil.isNullOrEmpty(shipping) ){
				orderInfo.setShipping_id(shipping.getShipping_id());
			}
			
		}
		
		//省市区ID初始化
		String provinceName = orderInfo.getProvince_name() ;
		String cityName = orderInfo.getCity_name() ;
		String districtName = orderInfo.getDistrict_name();
		
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
			Map<String, Object> mapParams = new HashMap<String, Object>();
			int provinceId = orderInfo.getProvince_id() == null? -1 : orderInfo.getProvince_id() ;
			mapParams.put("name", cityName);
			mapParams.put("parentId", provinceId);
			Region region = regionDao.selectByNameAndParentId(mapParams);
			if(!WorkerUtil.isNullOrEmpty(region) ){
				if((int)(region.getRegion_type()) == 2){
					orderInfo.setCity_id((int)region.getRegion_id());
				}
			}
		}
		
		if(!WorkerUtil.isNullOrEmpty(districtName)){
			int cityId = orderInfo.getCity_id() == null? -1 : orderInfo.getCity_id() ;
			//TODO 后面考虑抽出Biz
			Map<String, Object> mapParams = new HashMap<String, Object>();
			mapParams.put("name", districtName);
			mapParams.put("parentId", cityId);
			Region region = regionDao.selectByNameAndParentId(mapParams);
			orderInfo.setDistrict_id(-1);  //初始化为-1，只有正确时，才会设置对应的id
			if(!WorkerUtil.isNullOrEmpty(region) ){
				if((int)(region.getRegion_type()) == 3){
					orderInfo.setDistrict_id((int)region.getRegion_id());
				}
			}
		}
		
		//初始化OrderGoodsOms列表
		List<OrderGoodsOms> orderGoodsOmsList = orderGoodsReqDomain2OrderGoodsOmsConvert.covertToTargetEntity(orderGoodsDomainList);
		orderInfo.setOrderGoodsOmsList(orderGoodsOmsList);
		
		//取的满足条件的任务规则，初始化orderGoods，并添加使用数量
		
		
		if(OrderInfo.ORDER_TYPE_SALE.equals(orderInfo.getOrder_type())){
			
			logger.info("OrderInfo.ORDER_TYPE_SALE------------------------");
			List<OrderGoods> orderGoodsList = new ArrayList<OrderGoods>();
			//初始化groupCode2ProductNumMap
			
			if(!WorkerUtil.isNullOrEmpty(orderGoodsOmsList)){
				
				String opearateName = "prePack("+orderInfo.getOms_order_sn()+")-> ";
				//统计套餐中各个商品的数量，可能存在多商品sku相同的情况
				Map<String,List<Map<Integer,Integer>>> groupCode2ProductNumMapList = new HashMap<String,List<Map<Integer,Integer> >>();  
				
				Map<String,Integer> groupCode2GroupNum = new HashMap<String, Integer>();
				
				Map<String,BigDecimal> groupCode2GroupPriceMap = new HashMap<String, BigDecimal>();
				
				//统计所有的商品的productId以及对应数量（包含套餐商品）
				Map<Integer,Integer> productId2SkuNumMap = new HashMap<Integer, Integer>();
				//统计所有商品的productId和sku_code的对应关系
				Map<Integer,String> productId2Skucode = new HashMap<Integer, String>();
				//没用的groupCode
				List<String> unusableGroupCodeList = new ArrayList<String>();  
		
				Date payTime = orderInfo.getPay_time();
				boolean onlyTcInclude = true;
				
				//初始化groupCode2ProductNumMap
				
				for (OrderGoodsOms orderGoodsOms : orderGoodsOmsList) { 
					//获取商品的productId以及对应数量（包含套餐商品）
					if(productId2SkuNumMap.containsKey(orderGoodsOms.getProduct_id())){
						productId2SkuNumMap.put(orderGoodsOms.getProduct_id(), productId2SkuNumMap.get(orderGoodsOms.getProduct_id())+orderGoodsOms.getGoods_number());
					}else{
						productId2SkuNumMap.put(orderGoodsOms.getProduct_id(), orderGoodsOms.getGoods_number());
					}
					//获取所有商品的productId和sku_code的对应关系
					if(!productId2Skucode.containsKey(orderGoodsOms.getProduct_id())){
						productId2Skucode.put(orderGoodsOms.getProduct_id(), orderGoodsOms.getSku_code());
					}
					String groupCode = orderGoodsOms.getGroup_code();
					Integer groupNumber = orderGoodsOms.getGroup_number();
					
					//
					if(!WorkerUtil.isNullOrEmpty(orderGoodsOms.getGroup_code())
							&& !WorkerUtil.isNullOrEmpty(orderGoodsOms.getGroup_number())
							&& orderGoodsOms.getGroup_number().intValue() > 0
							){
						
						if( groupCode2GroupNum.get(orderGoodsOms.getGroup_code())== null){
							groupCode2GroupNum.put(groupCode,groupNumber );
							groupCode2GroupPriceMap.put(groupCode, orderGoodsOms.getGoods_price().multiply(new BigDecimal( orderGoodsOms.getGoods_number())));
						}else{
							if(groupCode2GroupNum.get(groupCode)!= groupNumber){
								unusableGroupCodeList.add(groupCode);
							}
							groupCode2GroupPriceMap.put(groupCode,  groupCode2GroupPriceMap.get(orderGoodsOms.getGroup_code()).add( orderGoodsOms.getGoods_price().multiply(new BigDecimal( orderGoodsOms.getGoods_number()))));
	
						}
						
						List<Map<Integer,Integer>> productNumMapList = groupCode2ProductNumMapList.get(groupCode);
						// 1.如果不存在则先放进去
						if(productNumMapList == null){
							//初始化ProductNumMapList
							productNumMapList = new ArrayList<Map<Integer,Integer>>();
							Map<Integer,Integer> productNumMp = new HashMap<Integer, Integer>();
							productNumMp.put(orderGoodsOms.getProduct_id(), orderGoodsOms.getGoods_number());
							productNumMapList.add(productNumMp);
							groupCode2ProductNumMapList.put(groupCode, productNumMapList);
							
						}
						// 2.已存在则添加数量
						else{
							
							//初始化ProductNumMapList（需要按productId和number顺序排序）
							int index = -1;  //记录排序后需要插入的位置
							for(int i = 0 ; i < productNumMapList.size() ; i++){
								Map<Integer,Integer> productNumMp = productNumMapList.get(i);
								Integer productId = 0;
								Integer number = 0;
								for( Entry<Integer, Integer> productEntry :  productNumMp.entrySet() ){
									productId =productEntry.getKey();
									number =productEntry.getValue();
								}
								
								//如果
								if(orderGoodsOms.getProduct_id() < productId ){
									index = i ;
									break;
								}else if(orderGoodsOms.getProduct_id() == productId ){
									if(orderGoodsOms.getGoods_number() <= number ){
										index = i ;
										break;
									}
								}
							}
							//如果没有比较小的则赋值为最后一个
							if(index < 0){
								index = productNumMapList.size() ;
							}
							
							
							Map<Integer,Integer> productNumMp = new HashMap<Integer, Integer>();
							productNumMp.put(orderGoodsOms.getProduct_id(), orderGoodsOms.getGoods_number());
							productNumMapList.add(index,productNumMp);
							groupCode2ProductNumMapList.put(groupCode, productNumMapList);
						}
					}else{
						onlyTcInclude = false;
					}
				}
				if(!WorkerUtil.isNullOrEmpty(groupCode2GroupNum)){
					// 计算套餐的单价，除以数量
					for (Entry<String,Integer> entry : groupCode2GroupNum.entrySet()) {
						
						if( !WorkerUtil.isNullOrEmpty(unusableGroupCodeList) 
								&& unusableGroupCodeList.contains(entry.getKey())){
							logger.info(opearateName+"无用的groupCode=" + entry.getKey()+",不需要计算套餐的单价");
						}else{
							groupCode2GroupPriceMap.put(entry.getKey(), groupCode2GroupPriceMap.get(entry.getKey()).divide( new BigDecimal( entry.getValue()) ,2,
	                                BigDecimal.ROUND_DOWN  ));
						}
					}
					// 将groupCode2ProductNumMapList按ProductId和number排序
				}
				
				logger.info(opearateName + "订单中groupCode2GroupNum=" + groupCode2GroupNum.keySet());
				logger.info(opearateName + "订单中groupCode2GroupPriceMap=" + groupCode2GroupPriceMap);
				logger.info(opearateName + "订单中groupCode2ProductNumMapList=" + groupCode2ProductNumMapList);
				//去除没用的套餐编码
				logger.info(opearateName + "unusableGroupCodeList="+unusableGroupCodeList );
				if(!WorkerUtil.isNullOrEmpty(unusableGroupCodeList)){
					for (String groupCode : unusableGroupCodeList) {
						groupCode2ProductNumMapList.remove(groupCode);
					}
				}
				
				Warehouse warehouse = warehouseDao.selectByWarehouseId(orderInfo.getWarehouse_id());
				List<String> usedForPrepackGroupCodeList = new ArrayList<String>();
				//使用掉的预打包任务的order_id与数量的对应关系
				Map<Integer,Integer> prepackageOrderId2NumMap = new HashMap<Integer,Integer>();
				boolean isNoMoreOperation = false;
				
				/**--------- 单个套餐 全打包 ---------*/
				String groupCode = "";
				Integer groupNum = 0;
				for( Entry<String, Integer> groupCode2GroupNumEntry :  groupCode2GroupNum.entrySet() ){
					groupCode =groupCode2GroupNumEntry.getKey();
					groupNum =groupCode2GroupNumEntry.getValue();
				}
				if(groupCode2GroupNum.size()==1 && groupNum==1 && onlyTcInclude && !unusableGroupCodeList.contains(groupCode)){
					logger.info(opearateName+"/**--------- 数量为1且单个套餐 全打包 ---------*/");
					Product product = productDao.selectProductBySkuCode(groupCode);
					if(!WorkerUtil.isNullOrEmpty(product)){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("payTime", payTime);
						map.put("PrepackageProductId", product.getProduct_id());
						map.put("physicalWarehouseId", warehouse.getPhysical_warehouse_id());
						List<OrderPrepack> orderPrepackList = orderPrepackDao.selectOrderPrepackByPrepackageProductIdAndPayTime(map);
						if(!WorkerUtil.isNullOrEmpty(orderPrepackList)){
							OrderPrepack chosenOrderPrepack = null;
							for (OrderPrepack orderPrepack : orderPrepackList) {
								chosenOrderPrepack = orderPrepack;
								if(orderPrepack.getPack_type().equals("BOX_CLOSED")){//全打包优先于半打包，若有全打包预打包任务，则选择全打包预打包任务
									break;
								}
							}
							orderPrepackBiz.initOrderPrepackProductPrepackageList(chosenOrderPrepack);
							chosenOrderPrepack.setPrepackageProductSkuCode(groupCode);
							if(validOrderPrepack(groupCode2GroupNum,groupCode2ProductNumMapList,chosenOrderPrepack,opearateName)){
								prepackageOrderId2NumMap.put(chosenOrderPrepack.getOrder_id(), 1);
								isNoMoreOperation = true;
								OrderGoods orderGoods = new OrderGoods();
								Product tempProduct = productDao.selectByPrimaryKey(chosenOrderPrepack.getPrepackage_product_id());
								orderGoods.setGoods_name(tempProduct.getProduct_name());
								orderGoods.setGoods_number(1);
								orderGoods.setGoods_price(groupCode2GroupPriceMap.get(groupCode));
								orderGoods.setDiscount(new BigDecimal("0"));
								orderGoods.setBatch_sn("");
								orderGoods.setOrder_goods_type("PREPACKAGE");
								orderGoods.setStatus_id("NORMAL");
								orderGoods.setTax_rate(new BigDecimal("0"));
								orderGoods.setOms_order_goods_sn("0");
								orderGoods.setGroup_code(groupCode);
								orderGoods.setGroup_number(1);
								orderGoods.setProduct_id(tempProduct.getProduct_id());
								orderGoodsList.add(orderGoods);
								logger.info(opearateName+"为数量为1且单个套餐 全打包:groupcode:"+groupCode+",product_id:"+tempProduct.getProduct_id());
							}
						}
					}
				}else{
					/**--------- 单个单品 全打包 ---------*/
					if(groupCode2GroupNum.size()==0 && orderGoodsOmsList.size()==1 
							&& orderGoodsOmsList.get(0).getGoods_number()==1 
							&& WorkerUtil.isNullOrEmpty(orderGoodsOmsList.get(0).getGroup_code())){
						logger.info(opearateName+"/**--------- 单个单品 全打包 ---------*/");
						String skucode = orderGoodsOmsList.get(0).getSku_code()+"_p";
						Product product = productDao.selectProductBySkuCode(skucode);
						if(!WorkerUtil.isNullOrEmpty(product)){
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("payTime", payTime);
							map.put("PrepackageProductId", product.getProduct_id());
							map.put("physicalWarehouseId", warehouse.getPhysical_warehouse_id());
							List<OrderPrepack> orderPrepackList = orderPrepackDao.selectOrderPrepackByPrepackageProductIdAndPayTime(map);
							if(!WorkerUtil.isNullOrEmpty(orderPrepackList)){
								OrderPrepack chosenOrderPrepack = null;
								for (OrderPrepack orderPrepack : orderPrepackList) {
									chosenOrderPrepack = orderPrepack;
									if("BOX_CLOSED".equals(orderPrepack.getPack_type())){//全打包优先于半打包，若有全打包预打包任务，则选择全打包预打包任务
										break;
									}
								}
								prepackageOrderId2NumMap.put(chosenOrderPrepack.getOrder_id(), 1);
								chosenOrderPrepack.setSkucode(orderGoodsOmsList.get(0).getSku_code());
								chosenOrderPrepack.setPrepackageProductSkuCode(orderGoodsOmsList.get(0).getSku_code()+"_p");
								isNoMoreOperation = true;
								OrderGoods orderGoods = new OrderGoods();
								Product tempProduct = productDao.selectByPrimaryKey(chosenOrderPrepack.getPrepackage_product_id());
								orderGoods.setGoods_name(tempProduct.getProduct_name());
								orderGoods.setGoods_number(1);
								orderGoods.setGoods_price(orderGoodsOmsList.get(0).getGoods_price());
								orderGoods.setDiscount(new BigDecimal("0"));
								orderGoods.setBatch_sn("");
								orderGoods.setOrder_goods_type("PREPACKAGE");
								orderGoods.setStatus_id("NORMAL");
								orderGoods.setTax_rate(new BigDecimal("0"));
								orderGoods.setOms_order_goods_sn("0");
								orderGoods.setGroup_code(skucode);
								orderGoods.setGroup_number(1);
								orderGoods.setProduct_id(tempProduct.getProduct_id());
								orderGoodsList.add(orderGoods);
								logger.info(opearateName+"为数量为1的单品符合全打包：skucode:"+orderGoodsOmsList.get(0).getSku_code()+",product_id:"+tempProduct.getProduct_id());
							}
						}
					}
				}
				/**--------- 普通情况   只考虑半打包预打包任务 ---------*/
				if(!isNoMoreOperation && !WorkerUtil.isNullOrEmpty(groupCode2GroupNum)){
					logger.info(opearateName+"/**--------- 普通情况   只考虑半打包预打包任务 ---------*/");
					//优先处理套餐
					for( Entry<String, Integer> groupCode2GroupNumEntry :  groupCode2GroupNum.entrySet() ){
						String tempGroupCode = groupCode2GroupNumEntry.getKey();
						Integer tempGroupNum = groupCode2GroupNumEntry.getValue();
						Product product = productDao.selectProductBySkuCode(tempGroupCode);
						if(!WorkerUtil.isNullOrEmpty(product)&&!unusableGroupCodeList.contains(tempGroupCode)){
							Map<String,Object> map = new HashMap<String,Object>();
							map.put("payTime", payTime);
							map.put("PrepackageProductId", product.getProduct_id());
							map.put("physicalWarehouseId", warehouse.getPhysical_warehouse_id());
							map.put("packType", "BOX_OPEN");//只选取半打包的预打包任务
							List<OrderPrepack> orderPrepackList = orderPrepackDao.selectOrderPrepackByPrepackageProductIdAndPayTime(map);
							if(!WorkerUtil.isNullOrEmpty(orderPrepackList)){
								OrderPrepack chosenOrderPrepack = orderPrepackList.get(0);
								orderPrepackBiz.initOrderPrepackProductPrepackageList(chosenOrderPrepack);
								chosenOrderPrepack.setPrepackageProductSkuCode(tempGroupCode);
								if(validOrderPrepack(groupCode2GroupNum,groupCode2ProductNumMapList,chosenOrderPrepack,opearateName)){
									if(prepackageOrderId2NumMap.containsKey(chosenOrderPrepack.getOrder_id())){
										prepackageOrderId2NumMap.put(chosenOrderPrepack.getOrder_id(), 
												prepackageOrderId2NumMap.get(chosenOrderPrepack.getOrder_id()) + tempGroupNum);
									}else{
										prepackageOrderId2NumMap.put(chosenOrderPrepack.getOrder_id(), tempGroupNum);
									}
									usedForPrepackGroupCodeList.add(tempGroupCode);
									OrderGoods orderGoods = new OrderGoods();
									Product tempProduct = productDao.selectByPrimaryKey(chosenOrderPrepack.getPrepackage_product_id());
									orderGoods.setGoods_name(tempProduct.getProduct_name());
									orderGoods.setGoods_number(tempGroupNum);
									orderGoods.setGoods_price(groupCode2GroupPriceMap.get(tempGroupCode));
									orderGoods.setDiscount(new BigDecimal("0"));
									orderGoods.setBatch_sn("");
									orderGoods.setOrder_goods_type("PREPACKAGE");
									orderGoods.setStatus_id("NORMAL");
									orderGoods.setTax_rate(new BigDecimal("0"));
									orderGoods.setOms_order_goods_sn("0");
									orderGoods.setGroup_code(tempGroupCode);
									orderGoods.setGroup_number(tempGroupNum);
									orderGoods.setProduct_id(tempProduct.getProduct_id());
									orderGoodsList.add(orderGoods);
									logger.info(opearateName+"存在符合半打包的套餐商品：group_code:"+tempGroupCode+",product_id:"+tempProduct.getProduct_id());
								}
							}
						}
					}
				}
				/**--------- 再处理剩余单品 是否符合半打包 ---------*/
				if(!isNoMoreOperation){
					logger.info("/**--------- 再处理剩余单品 是否符合半打包 ---------*/");
					for (OrderGoodsOms orderGoodsOms : orderGoodsOmsList) {
						if(WorkerUtil.isNullOrEmpty(orderGoodsOms.getGroup_code()) 
								|| !usedForPrepackGroupCodeList.contains(orderGoodsOms.getGroup_code())){
							String skucode = productId2Skucode.get(orderGoodsOms.getProduct_id())+"_p";
							Product product = productDao.selectProductBySkuCode(skucode);
							if(!WorkerUtil.isNullOrEmpty(product)){
								Map<String,Object> map = new HashMap<String,Object>();
								map.put("payTime", payTime);
								map.put("PrepackageProductId", product.getProduct_id());
								map.put("physicalWarehouseId", warehouse.getPhysical_warehouse_id());
								map.put("packType", "BOX_OPEN");//只选取半打包的预打包任务
								List<OrderPrepack> orderPrepackList = orderPrepackDao.selectOrderPrepackByPrepackageProductIdAndPayTime(map);
								if(!WorkerUtil.isNullOrEmpty(orderPrepackList)){
									OrderPrepack chosenOrderPrepack = orderPrepackList.get(0);
									if(prepackageOrderId2NumMap.containsKey(chosenOrderPrepack.getOrder_id())){
										prepackageOrderId2NumMap.put(chosenOrderPrepack.getOrder_id(), 
												prepackageOrderId2NumMap.get(chosenOrderPrepack.getOrder_id()) + orderGoodsOms.getGoods_number());
									}else{
										prepackageOrderId2NumMap.put(chosenOrderPrepack.getOrder_id(), orderGoodsOms.getGoods_number());
									}
									OrderGoods orderGoods = new OrderGoods();
									Product tempProduct = productDao.selectByPrimaryKey(chosenOrderPrepack.getPrepackage_product_id());
									orderGoods.setGoods_name(orderGoodsOms.getGoods_name());
									orderGoods.setGoods_number(orderGoodsOms.getGoods_number());
									orderGoods.setGoods_price(orderGoodsOms.getGoods_price());
									orderGoods.setDiscount(new BigDecimal("0"));
									orderGoods.setBatch_sn("");
									orderGoods.setOrder_goods_type("PREPACKAGE");
									orderGoods.setStatus_id("NORMAL");
									orderGoods.setTax_rate(new BigDecimal("0"));
									orderGoods.setOms_order_goods_sn("0");
									orderGoods.setGroup_code(orderGoodsOms.getGroup_code());
									orderGoods.setGroup_number(orderGoodsOms.getGroup_number());
									orderGoods.setProduct_id(tempProduct.getProduct_id());
									orderGoodsList.add(orderGoods);
									logger.info(opearateName+"存在符合半打包的单品："+productId2Skucode.get(orderGoodsOms.getProduct_id())+",product_id:"+tempProduct.getProduct_id());
								}else{
									OrderGoods orderGoods = orderGoodsOms2OrderGoodsConvert.covertToTargetEntity(orderGoodsOms);
									orderGoodsList.add(orderGoods);
									logger.info(opearateName+"做普通商品处理的单品："+orderGoodsOms.getSku_code()+",product_id:"+orderGoodsOms.getProduct_id());
								}
							}else{
								OrderGoods orderGoods = orderGoodsOms2OrderGoodsConvert.covertToTargetEntity(orderGoodsOms);
								orderGoodsList.add(orderGoods);
								logger.info(opearateName+"做普通商品处理的单品："+orderGoodsOms.getSku_code()+",product_id:"+orderGoodsOms.getProduct_id());
							}
						}
					}
				}
				
				for (Entry<Integer, Integer> entry : prepackageOrderId2NumMap.entrySet()) {
					orderPrepackBiz.updateQtyUsed(entry.getKey(), entry.getValue());
				}
				
				orderInfo.setOrderGoodsList(orderGoodsList);
			}
		}else{
			List<OrderGoods> orderGoodsList = new ArrayList<OrderGoods>();

			for (OrderGoodsOms orderGoodsOms : orderGoodsOmsList) {
				OrderGoods orderGoods = orderGoodsOms2OrderGoodsConvert.covertToTargetEntity(orderGoodsOms);
				orderGoodsList.add(orderGoods);
			}
			orderInfo.setOrderGoodsList(orderGoodsList);
		}
		return orderInfo;
	}

	@Override
	public SyncSaleOrderRequest covertToSourceEntity(OrderInfo orderInfo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean validOrderPrepack(Map<String,Integer> groupCode2GroupNum, 
									Map<String, List<Map<Integer, Integer>>> groupCode2ProductNumMapList, 
									OrderPrepack orderPrepack,
									String opearateName){
		
		String prepackageSkuCode = orderPrepack.getPrepackageProductSkuCode();
		//校验套餐各个商品数量的正确性【是否是整倍数】
		List<Map<Integer,Integer>> productNumMapList  = groupCode2ProductNumMapList.get(prepackageSkuCode);
		List<ProductPrepackage> productPrepackageList = orderPrepack.getProductPrepackageList();
		
		
		if(WorkerUtil.isNullOrEmpty(productPrepackageList)){
			logger.error(opearateName + "oms_task_sn="+ orderPrepack.getOms_task_sn() +"的productPrepackageList为空" );
			return false;
		}
		
		
		if(WorkerUtil.isNullOrEmpty(productNumMapList)){
			logger.error(opearateName + "oms_task_sn="+ orderPrepack.getOms_task_sn() +"的productNumMapList为空" );
			return false;
		}
		
		//校验商品种类数是否一致[可能存在相同的sku]
		if(productNumMapList.size() != productPrepackageList.size() ){
			logger.error(opearateName + "oms_task_sn="+ orderPrepack.getOms_task_sn() +"的productNumMapList["+productNumMapList.size()+"]和productPrepackageList["+productPrepackageList.size()+"]商品种类数量不一致" );
			return false;
		}
		
		//校验商品种类是否一致数量是否成倍数
		Map<Integer, Integer> productIdTimes = new  HashMap<Integer,Integer>();
		
		boolean isValid = true;
		
		for(int i = 0; i < productPrepackageList.size(); i++){
			//任务单中ProductId->num
			Integer componentProductId = productPrepackageList.get(i).getComponent_product_id();
			Integer componentNum = productPrepackageList.get(i).getNumber();
			
			//订单中ProductId->num
			Integer productId = 0;
			Integer productNum = 0;
			for( Entry<Integer, Integer> productEntry :  productNumMapList.get(i).entrySet() ){
				productId =productEntry.getKey();
				productNum =productEntry.getValue();
			}
			
			
			//验证是否相等
			if(productId.intValue() != componentProductId.intValue()){
				logger.error(opearateName + "oms_task_sn="+ orderPrepack.getOms_task_sn() +"的componentProductId="+componentProductId+"与productId=" + productId +"不一致"   );
				isValid = false;
				break;
			}
			
			//验证余数
			int modNum = productNum % componentNum;
			if(modNum > 0){
				logger.error(opearateName + "oms_task_sn="+ orderPrepack.getOms_task_sn() +"的componentNum="+componentNum+"与productNum=" + productNum +"不成比例"   );
				isValid = false;
				break;
			}
			
			//验证倍数
			boolean isTimesValid = true; 
			int times = productNum / componentNum;
			if(!WorkerUtil.isNullOrEmpty(productIdTimes)){
				for( Entry<Integer, Integer> productIdTimeEntry : productIdTimes.entrySet()){
					if(productIdTimeEntry.getValue() != times){
						logger.error(opearateName + "oms_task_sn="+ orderPrepack.getOms_task_sn() +"的productId="+componentProductId+"["+times+"]与productId="+productIdTimeEntry.getKey()+"["+productIdTimeEntry.getValue()+"]倍数不一致 "   );
						isTimesValid = false;
						break;
					}
				}
			}
			
			
			// 验证groupNum是否一致
			if(times != groupCode2GroupNum.get(prepackageSkuCode)){
				logger.error(opearateName + "oms_task_sn="+ orderPrepack.getOms_task_sn() +"的times="+times+"与groupCode2GroupNum=" + groupCode2GroupNum.get(orderPrepack.getPrepackageProductSkuCode()) +"不等"   );
				isValid = false;
				break;
			}
			
			
			if(!isTimesValid){
				isValid = false;
				break;
			}
			productIdTimes.put(componentProductId, times);
			
		}
		
		
		if(!isValid ){
			return false;
		}
		return true;
	}

}
