package com.leqee.wms.job;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.api.biz.OrderInfoApiBiz;
import com.leqee.wms.api.request.SyncSaleOrderRequest;
import com.leqee.wms.api.request.domain.OrderGoodsReqDomain;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.entity.Product;


@Service
public class TestJob extends CommonJob{

	@Autowired
	OrderInfoApiBiz orderInfoApiBiz;
	
	@Autowired
	ProductDao productDao;
	
	/** 日志对象 */
	private Logger logger = Logger.getLogger(TestJob.class);
	
    public void abcdefghijklmn(String paramNameValue) {
        logger.info("TestJob-run start");
        Map<String, Object> serviceParamsMap = getServiceParamsMap(paramNameValue);
        Integer customerId = (Integer)serviceParamsMap.get("customerId");
        Integer physicalWarehouseId = (Integer)serviceParamsMap.get("physicalWarehouseId");
        
        HashMap<String,Object> customerGoodMap = new HashMap<String,Object>();
        customerGoodMap.put("4", "201887_0,201885_0,201906_0,201901_0,201898_0,201908_0,201903_0,201891_0,201907_0,201910_0");
    	customerGoodMap.put("65558", "166614_0,127852_0,154094_0,178270_0,178269_0,178268_0,178272_0,178275_0");
    	customerGoodMap.put("65572", "137877_0,137900_0,137901_0,137902_0,137903_0,137904_0,137905_0,137906_0,137907_0,137908_0");
    	customerGoodMap.put("65553", "105827_0,140557_0,157620_0,178891_0,179472_0,180055_0,180056_0,180057_0,180361_0,180360_0");
    	customerGoodMap.put("65609", "189262_0,189263_0,189264_0,189265_0,189266_0,189267_0,189268_0,189269_0,189278_0,189279_0");
    	
    	HashMap<String,Object> customerShopMap = new HashMap<String,Object>();
    	customerShopMap.put("4", "丝蕴天猫官方旗舰店");
    	customerShopMap.put("65558", "高洁丝官方旗舰店");
    	customerShopMap.put("65572", "玛氏宠物食品");
    	customerShopMap.put("65553", "雀巢");
    	customerShopMap.put("65609", "亨氏唯品会");
    	
        HashMap<String,Object> resMap = new HashMap<String, Object>();
        for(int t=0;t<3000;t++){
        	try {   
				SyncSaleOrderRequest request = new SyncSaleOrderRequest();
				request.setDistributor_id(1161);
				request.setAftersale_phone("");
				request.setBrand_name("leqee");
				request.setBuyer_name("leqee_hzhang1");
				request.setBuyer_note("压力测试造单");
				request.setBuyer_phone("110");
				request.setCity_name("河东区");
				request.setCollecting_payment_amount(new BigDecimal(281));
				request.setCurrency("RMB");
				request.setDeclaring_value_amount(new BigDecimal(0));
				request.setDiscount_amount(new BigDecimal(0));
				request.setDistrict_name("");
				request.setEmail("hzhang1@leqee.com");
				
				request.setInvoice_amount(new BigDecimal(0));
				request.setInvoice_note("");
				request.setInvoice_title("天天跳跳糖");
				request.setIs_payment_collected("N");
				request.setIs_value_declared("N");
				request.setMobile_number("110");
				request.setNick_name("novalist");
				request.setNote("none");
				
				request.setOms_order_type("SALE");
				request.setOrder_source("taobao");
				request.setOrder_time(new Date());
				request.setOrder_type("SALE");
				request.setPay_time(new Date());
				request.setPhone_number("110");
				request.setPostal_code("");
				request.setProvider_code("");
				request.setProvider_name("");
				request.setProvider_order_type("");
				request.setProvince_name("天津");
				request.setShipping_amount(new BigDecimal(6));
				request.setReceive_name("novalist");
				request.setSeller_note("尽早送达");
				request.setSex("unknown");
				request.setShipment_category("");
				request.setShipping_address("聚财路");
				request.setShipping_code("ZTO");
				request.setShop_order_sn("");
				request.setTms_company("");
				request.setTms_contact("");
				request.setTms_mobile("");
				request.setTms_shipping_no("");
				request.setIs_merge_order("N");
				request.setSlave_oms_order_sns("");
				request.setOms_shipment_sn(""); 
				
				if(physicalWarehouseId.equals(1)){
					request.setWarehouse_id(8);
				}else{
					request.setWarehouse_id(12121);
				}
				request.setShop_name(customerShopMap.get(customerId.toString()).toString());
				request.setPay_amount(new BigDecimal(2));
				request.setGoods_amount(new BigDecimal(2));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String sn = sdf.format(new Date());
				int x=1+(int)(Math.random()*50);
				request.setOms_order_sn(sn+x);
				
				String arr[] = customerGoodMap.get(customerId.toString()).toString().split(",");
	
				int temp = 1+(int)(Math.random()*3);
				List<OrderGoodsReqDomain> orderGoodsReqDomainList =  new ArrayList<OrderGoodsReqDomain>();
				List<Integer> existSkuCodeList = new ArrayList<Integer>();
				for(int i=0;i<temp;i++){
					OrderGoodsReqDomain orderGoodsReq = new OrderGoodsReqDomain();
					orderGoodsReq.setBatch_sn("");
					orderGoodsReq.setDiscount(new BigDecimal(0));
					orderGoodsReq.setGoods_number(1+(int)(Math.random()*8));
					orderGoodsReq.setGoods_price(new BigDecimal(2));
					orderGoodsReq.setOms_order_goods_sn("");
					
					int z = 0;
					boolean flag = true;
					int y = 0;
					while(true){
						y = (int)(Math.random()*7);
						if(!existSkuCodeList.contains(y)){
							existSkuCodeList.add(y);
							break;
						}
						z++;
						if(z == 8){
							flag = false;
							break;
						}
					}
					if(!flag){
						continue;
					}
					Product product = productDao.selectBySkuCode(arr[y]);
					orderGoodsReq.setSku_code(arr[y]);
					orderGoodsReq.setGoods_name(product.getProduct_name());
					orderGoodsReq.setStatus_id("NORMAL");
					orderGoodsReq.setTax_rate(new BigDecimal(1.17));
					orderGoodsReqDomainList.add(orderGoodsReq);
				}
				request.setOrderGoodsReqDomainList(orderGoodsReqDomainList);
				resMap = orderInfoApiBiz.syncSaleOrder(request,customerId);
				logger.info("TestJob-run end 第"+(t+1)+"个造单完成");
			}catch (Exception e) {
				logger.error("syncSaleOrder异常", e);
				e.printStackTrace();
				resMap.put("result", "failure");
				resMap.put("msg", e.getMessage());
				resMap.put("error_code", "410010");
			}
		}
        logger.info("TestJob-run end");
    }
	
}
