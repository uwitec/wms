package com.leqee.wms.api.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.request.domain.OrderGoodsReqDomain;
import com.leqee.wms.api.response.SyncSaleOrderResponse;

/**
 * 同步发货单Request
 * @author qyyao
 * @date 2016-2-23
 * @version 1.0
 */
public class SyncSaleOrderRequest
    extends  BaseLeqeeRequest<SyncSaleOrderResponse> {


	private String aftersale_phone; // 售后电话-打印在快递面单上
	private String brand_name;
	//买家基本信息
	private String buyer_name;
	//备注
	private String buyer_note;
	
    private String buyer_phone;
	private String city_name;
    private BigDecimal collecting_payment_amount;
	
	//其他
	private String currency;
	
	private BigDecimal declaring_value_amount;
	
	private BigDecimal discount_amount;
    private String district_name;
	
	private String email;
	
	//订单金额相关
	private BigDecimal goods_amount;
   
	private BigDecimal invoice_amount;
	
	private String invoice_note;
	
	//发票
	private String invoice_title;
	
	private String is_payment_collected;
	
	//保价和贷款
	private String is_value_declared;
	
	private String mobile_number;
	
    private String nick_name;
	private String note;
	//订单号及相关
	private String oms_order_sn;   //oms订单编号，必须
	private String oms_order_type;
    private String order_source;
	
	//订单时间相关
	private Date order_time;
	
	private String order_type;     //订单类型

    // 订单商品列表
    private List<OrderGoodsReqDomain> orderGoodsReqDomainList = new ArrayList<OrderGoodsReqDomain>();
	
	private BigDecimal pay_amount;
	
	private Date pay_time;
	
	private String phone_number;
	private Integer distributor_id; // oms分销商id
	
    
    //店铺和品牌相关

	

	private String postal_code;
	
	//供应商相关
	private String provider_code;
	
	private String provider_name;
    
	private String provider_order_type;
	//收货地址
	private String province_name;
	
	//收货人及联系方式
	private String receive_name;


    private String seller_note;
	
	private String sex;
	
	private String shipment_category; // 包裹所属类目-打印在快递面单上
	
	private String shipping_address;
	
	private BigDecimal shipping_amount;
	//快递相关
	private String shipping_code;
	private String shop_name;
	

	private String shop_order_sn;

	//tms相关
	private String tms_company;
    
    private String tms_contact;
	private String tms_mobile;
	private String tms_shipping_no;

    //仓库相关
	private Integer warehouse_id;


	//合并订单相关
	private String is_merge_order; // 是否合并订单,Y（是）,N（否）
	private String slave_oms_order_sns; // 合并订单从订单order_sn列表，用,分隔
	private String oms_shipment_sn ; // oms的发货单号
	private Integer order_level;
	
	//套餐相关(预打包逻辑)
	private String group_code;    //套餐编码
	private Integer group_number;  //套餐数量
	
	
	@Override
	public void check() throws ApiRuleException {
		// TODO Auto-generated method stub
		
	}

	
	

	public String getGroup_code() {
		return group_code;
	}








	public void setGroup_code(String group_code) {
		this.group_code = group_code;
	}








	public Integer getGroup_number() {
		return group_number;
	}








	public void setGroup_number(Integer group_number) {
		this.group_number = group_number;
	}

	public Integer getDistributor_id() {
		return distributor_id;
	}




	public void setDistributor_id(Integer distributor_id) {
		this.distributor_id = distributor_id;
	}







	public String getAftersale_phone() {
		return aftersale_phone;
	}


	@Override
	public String getApiMethodName() {
		return METHOD_SYNC_SALE_ORDER_REQUEST;
	}


	public String getBrand_name() {
		return brand_name;
	}


	public String getBuyer_name() {
		return buyer_name;
	}


	public String getBuyer_note() {
		return buyer_note;
	}


	public String getBuyer_phone() {
		return buyer_phone;
	}


	public String getCity_name() {
		return city_name;
	}


	public BigDecimal getCollecting_payment_amount() {
		return collecting_payment_amount;
	}


	public String getCurrency() {
		return currency;
	}


	public BigDecimal getDeclaring_value_amount() {
		return declaring_value_amount;
	}


	public BigDecimal getDiscount_amount() {
		return discount_amount;
	}


	public String getDistrict_name() {
		return district_name;
	}


	public String getEmail() {
		return email;
	}


	public BigDecimal getGoods_amount() {
		return goods_amount;
	}


	public BigDecimal getInvoice_amount() {
		return invoice_amount;
	}


	public String getInvoice_note() {
		return invoice_note;
	}


	public String getInvoice_title() {
		return invoice_title;
	}


	public String getIs_payment_collected() {
		return is_payment_collected;
	}


	public String getIs_value_declared() {
		return is_value_declared;
	}


	public String getMobile_number() {
		return mobile_number;
	}


	public String getNick_name() {
		return nick_name;
	}


	public String getNote() {
		return note;
	}


	public String getOms_order_sn() {
		return oms_order_sn;
	}


	public String getOms_order_type() {
		return oms_order_type;
	}


	public String getOrder_source() {
		return order_source;
	}


	public Date getOrder_time() {
		return order_time;
	}


	public String getOrder_type() {
		return order_type;
	}


	public List<OrderGoodsReqDomain> getOrderGoodsReqDomainList() {
		return orderGoodsReqDomainList;
	}


	public BigDecimal getPay_amount() {
		return pay_amount;
	}


	public Date getPay_time() {
		return pay_time;
	}


	public String getPhone_number() {
		return phone_number;
	}


	public String getPostal_code() {
		return postal_code;
	}


	public String getProvider_code() {
		return provider_code;
	}


	public String getProvider_name() {
		return provider_name;
	}


	public String getProvider_order_type() {
		return provider_order_type;
	}


	public String getProvince_name() {
		return province_name;
	}


	public String getReceive_name() {
		return receive_name;
	}


	@Override
	public Class<SyncSaleOrderResponse> getResponseClass() {
		return SyncSaleOrderResponse.class;
	}


	public String getSeller_note() {
		return seller_note;
	}


	public String getSex() {
		return sex;
	}


	public String getShipment_category() {
		return shipment_category;
	}


	public String getShipping_address() {
		return shipping_address;
	}


	public BigDecimal getShipping_amount() {
		return shipping_amount;
	}


	public String getShipping_code() {
		return shipping_code;
	}


	public String getShop_name() {
		return shop_name;
	}


	public String getShop_order_sn() {
		return shop_order_sn;
	}


	@Override
	public Map<String, String> getTextParams() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getTms_company() {
		return tms_company;
	}


	public String getTms_contact() {
		return tms_contact;
	}


	public String getTms_mobile() {
		return tms_mobile;
	}


	public String getTms_shipping_no() {
		return tms_shipping_no;
	}


	public Integer getWarehouse_id() {
		return warehouse_id;
	}


	public void setAftersale_phone(String aftersale_phone) {
		this.aftersale_phone = aftersale_phone;
	}


	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}


	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}


	public void setBuyer_note(String buyer_note) {
		this.buyer_note = buyer_note;
	}


	public void setBuyer_phone(String buyer_phone) {
		this.buyer_phone = buyer_phone;
	}


	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}


	public void setCollecting_payment_amount(BigDecimal collecting_payment_amount) {
		this.collecting_payment_amount = collecting_payment_amount;
	}



	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public void setDeclaring_value_amount(BigDecimal declaring_value_amount) {
		this.declaring_value_amount = declaring_value_amount;
	}


	public void setDiscount_amount(BigDecimal discount_amount) {
		this.discount_amount = discount_amount;
	}


	public void setDistrict_name(String district_name) {
		this.district_name = district_name;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public void setGoods_amount(BigDecimal goods_amount) {
		this.goods_amount = goods_amount;
	}


	public void setInvoice_amount(BigDecimal invoice_amount) {
		this.invoice_amount = invoice_amount;
	}


	public void setInvoice_note(String invoice_note) {
		this.invoice_note = invoice_note;
	}


	public void setInvoice_title(String invoice_title) {
		this.invoice_title = invoice_title;
	}


	public void setIs_payment_collected(String is_payment_collected) {
		this.is_payment_collected = is_payment_collected;
	}


	public void setIs_value_declared(String is_value_declared) {
		this.is_value_declared = is_value_declared;
	}


	public void setMobile_number(String mobile_number) {
		this.mobile_number = mobile_number;
	}


	

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}


	public void setNote(String note) {
		this.note = note;
	}


	public void setOms_order_sn(String oms_order_sn) {
		this.oms_order_sn = oms_order_sn;
	}


	public void setOms_order_type(String oms_order_type) {
		this.oms_order_type = oms_order_type;
	}


	public void setOrder_source(String order_source) {
		this.order_source = order_source;
	}


	public void setOrder_time(Date order_time) {
		this.order_time = order_time;
	}


	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}


	public void setOrderGoodsReqDomainList(
			List<OrderGoodsReqDomain> orderGoodsReqDomainList) {
		this.orderGoodsReqDomainList = orderGoodsReqDomainList;
	}


	public void setPay_amount(BigDecimal pay_amount) {
		this.pay_amount = pay_amount;
	}


	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
	}


	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}


	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}


	public void setProvider_code(String provider_code) {
		this.provider_code = provider_code;
	}


	public void setProvider_name(String provider_name) {
		this.provider_name = provider_name;
	}
	
	public void setProvider_order_type(String provider_order_type) {
		this.provider_order_type = provider_order_type;
	}


	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}


	public void setReceive_name(String receive_name) {
		this.receive_name = receive_name;
	}


	public void setSeller_note(String seller_note) {
		this.seller_note = seller_note;
	}


	public void setSex(String sex) {
		this.sex = sex;
	}


	public void setShipment_category(String shipment_category) {
		this.shipment_category = shipment_category;
	}


	public void setShipping_address(String shipping_address) {
		this.shipping_address = shipping_address;
	}


	public void setShipping_amount(BigDecimal shipping_amount) {
		this.shipping_amount = shipping_amount;
	}

	public void setShipping_code(String shipping_code) {
		this.shipping_code = shipping_code;
	}


	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}


	public void setShop_order_sn(String shop_order_sn) {
		this.shop_order_sn = shop_order_sn;
	}


	public void setTms_company(String tms_company) {
		this.tms_company = tms_company;
	}


	public void setTms_contact(String tms_contact) {
		this.tms_contact = tms_contact;
	}


	public void setTms_mobile(String tms_mobile) {
		this.tms_mobile = tms_mobile;
	}


	public void setTms_shipping_no(String tms_shipping_no) {
		this.tms_shipping_no = tms_shipping_no;
	}


	public void setWarehouse_id(Integer warehouse_id) {
		this.warehouse_id = warehouse_id;
	}


	public String getIs_merge_order() {
		return is_merge_order;
	}


	public void setIs_merge_order(String is_merge_order) {
		this.is_merge_order = is_merge_order;
	}


	public String getSlave_oms_order_sns() {
		return slave_oms_order_sns;
	}


	public void setSlave_oms_order_sns(String slave_oms_order_sns) {
		this.slave_oms_order_sns = slave_oms_order_sns;
	}


	public String getOms_shipment_sn() {
		return oms_shipment_sn;
	}


	public void setOms_shipment_sn(String oms_shipment_sn) {
		this.oms_shipment_sn = oms_shipment_sn;
	}


	public Integer getOrder_level() {
		return order_level;
	}


	public void setOrder_level(Integer order_level) {
		this.order_level = order_level;
	}


	

	
	
	
	
    


}

    
    
