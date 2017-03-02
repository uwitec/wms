package com.leqee.wms.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderInfo implements Serializable {
	
	//订单类型  order_type
    public final static String ORDER_TYPE_SALE = "SALE";
    public final static String ORDER_TYPE_PURCHASE = "PURCHASE";
    public final static String ORDER_TYPE_SUPPLIER_RETURN = "SUPPLIER_RETURN";
    public final static String ORDER_TYPE_RETURN = "RETURN";
    public final static String ORDER_TYPE_VARIANCE_ADD = "VARIANCE_ADD";
    public final static String ORDER_TYPE_VARIANCE_MINUS = "VARIANCE_MINUS";
	
    
    //订单状态
    public final static String ORDER_STATUS_ACCEPT = "ACCEPT";           //仓库接单（未处理）
    public final static String ORDER_STATUS_BATCH_PICK = "BATCH_PICK";   //生成波次单
    public final static String ORDER_STATUS_PICKING = "PICKING";         //拣货中
    public final static String ORDER_STATUS_RECHECKED = "RECHECKED";     //已复核
    public final static String ORDER_STATUS_WEIGHED = "WEIGHED";		 //已称重
    public final static String ORDER_STATUS_DELEVERED = "DELIVERED";	 //已发货
    public final static String ORDER_STATUS_IN_PROCESS = "IN_PROCESS";   //处理中
    public final static String ORDER_STATUS_ON_SHIP = "ON_SHELF";		 //待上架
    public final static String ORDER_STATUS_FULFILLED = "FULFILLED";	 //已完成
    public final static String ORDER_STATUS_EXCEPTION = "EXCEPTION";	 //异常
    public final static String ORDER_STATUS_CANCEL = "CANCEL";
    
    public final static String ORDER_STATUS_ABORTED = "ABORTED";   //已中止
   
    
    //公用订单状态映射MAP
    public static Map<String,Object> ORDER_STATUS_MAP =null;
    static{
    	ORDER_STATUS_MAP = new HashMap<String,Object>();
    	ORDER_STATUS_MAP.put("ACCEPT", "未处理");
    	ORDER_STATUS_MAP.put("BATCH_PICK", "生成波次单");
    	ORDER_STATUS_MAP.put("PICKING", "拣货中");
    	ORDER_STATUS_MAP.put("RECHECKED", "已复核");
    	ORDER_STATUS_MAP.put("WEIGHED", "已称重");
    	ORDER_STATUS_MAP.put("DELIVERED", "已发货");
    	ORDER_STATUS_MAP.put("IN_PROCESS", "处理中");
    	ORDER_STATUS_MAP.put("ON_SHIP", "待上架");
    	ORDER_STATUS_MAP.put("FULFILLED", "已完成");
    	ORDER_STATUS_MAP.put("EXCEPTION", "异常");
    	ORDER_STATUS_MAP.put("CANCEL", "已取消");
    }
    
    //TODO 待定义，先定义几个简单类型
    public final static String ORDER_STATUS_NORMAL = "NORMAL";
	
	
    private Integer order_id;  

    private String order_status;

    private Integer customer_id;

    private Integer physical_warehouse_id;

    private Integer warehouse_id;

    private String order_type;

    private String oms_order_sn;

    private String oms_order_type;

    private Date order_time;

    private Date pay_time;

    private String is_reserved;

    private Date reserved_time;

    private Date shipping_time;
    
    private Date arrival_time;

    private BigDecimal goods_amount;

    private BigDecimal shipping_amount;

    private BigDecimal discount_amount;

    private BigDecimal pay_amount;

    private String buyer_note;

    private String seller_note;

    private String note;

    private String receive_name;

    private String postal_code;

    private String phone_number;

    private String mobile_number;

    private Integer province_id;

    private String province_name;

    private Integer city_id;

    private String city_name;

    private Integer district_id;

    private String district_name;

    private String shipping_address;

    private String email;

    private String sex;

    private String invoice_title;

    private String invoice_note;

    private BigDecimal invoice_amount;

    private String is_value_declared;

    private BigDecimal declaring_value_amount;

    private String is_payment_collected;

    private BigDecimal collecting_payment_amount;

    private String priority_code;

    private String order_source;

    private Integer parent_order_id;

    private Integer root_order_id;

    private String parent_oms_order_sn;

    private String root_oms_order_sn;

    private String brand_name;

    private String shop_name;

    private String shop_order_sn;

    private String buyer_name;

    private String buyer_phone;

    private String nick_name;

    private Integer shipping_id;
    
    private String oms_shop_id;


	private String tracking_number;
    
    private BigDecimal shipping_wms_weight;

    private String provider_code;

    private String provider_order_type;

    private String provider_name;

    private String tms_company;

    private String tms_contact;

    private String tms_mobile;

    private String tms_shipping_no;

    private String currency;

    private String created_user;

    private Date created_time;

    private String last_updated_user;

    private Date last_updated_time;
    
    private String batch_order_sn;
    
    // 仓库名称
    private String warehouse_name;
    
    // 物理仓库名称
    private String physical_warehouse_name;
    
    // 货主名称
    private String customer_name;
    
    // 货主名称
    private int canAllot=0; //0未判断  1：足够 2：不够
    
    private String aftersale_phone;  // 售后电话
    
    //合并订单相关和发货单字段
    private String is_merge_order;   //  是否合并订单,Y（是）,N（否）
    private String slave_oms_order_sns; //合并订单从订单order_sn列表，用,分隔
    private String oms_shipment_sn;  //oms的发货单号
    private Integer order_level;//订单优先级
    
    
    
    // 订单商品列表
    private List<OrderGoods> orderGoodsList = new ArrayList<OrderGoods>();
    
    // oms订单商品列表
    private List<OrderGoodsOms> orderGoodsOmsList = new ArrayList<OrderGoodsOms>();
    
    
    

    

	public List<OrderGoodsOms> getOrderGoodsOmsList() {
		return orderGoodsOmsList;
	}

	public void setOrderGoodsOmsList(List<OrderGoodsOms> orderGoodsOmsList) {
		this.orderGoodsOmsList = orderGoodsOmsList;
	}

	public List<OrderGoods> getOrderGoodsList() {
		return orderGoodsList;
	}

	public void setOrderGoodsList(List<OrderGoods> orderGoodsList) {
		this.orderGoodsList = orderGoodsList;
	}

	private static final long serialVersionUID = 1L;

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status == null ? null : order_status.trim();
    }

    public Integer getCustomer_id() {
        return customer_id;
    }
    public String getOms_shop_id() {
		return oms_shop_id;
	}

	public void setOms_shop_id(String oms_shop_id) {
		this.oms_shop_id = oms_shop_id;
	}
   
    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public Integer getPhysical_warehouse_id() {
        return physical_warehouse_id;
    }

    public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
        this.physical_warehouse_id = physical_warehouse_id;
    }

    public Integer getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type == null ? null : order_type.trim();
    }

    public String getOms_order_sn() {
        return oms_order_sn;
    }

    public void setOms_order_sn(String oms_order_sn) {
        this.oms_order_sn = oms_order_sn == null ? null : oms_order_sn.trim();
    }

    public String getOms_order_type() {
        return oms_order_type;
    }

    public void setOms_order_type(String oms_order_type) {
        this.oms_order_type = oms_order_type == null ? null : oms_order_type.trim();
    }

    public Date getOrder_time() {
        return order_time;
    }

    public void setOrder_time(Date order_time) {
        this.order_time = order_time;
    }

    public Date getPay_time() {
        return pay_time;
    }

    public void setPay_time(Date pay_time) {
        this.pay_time = pay_time;
    }

    public String getIs_reserved() {
        return is_reserved;
    }

    public void setIs_reserved(String is_reserved) {
        this.is_reserved = is_reserved == null ? null : is_reserved.trim();
    }

    public Date getReserved_time() {
        return reserved_time;
    }

    public void setReserved_time(Date reserved_time) {
        this.reserved_time = reserved_time;
    }

    public Date getShipping_time() {
        return shipping_time;
    }

    public void setShipping_time(Date shipping_time) {
        this.shipping_time = shipping_time;
    }

    public BigDecimal getGoods_amount() {
        return goods_amount;
    }

    public void setGoods_amount(BigDecimal goods_amount) {
        this.goods_amount = goods_amount;
    }

    public BigDecimal getShipping_amount() {
        return shipping_amount;
    }

    public void setShipping_amount(BigDecimal shipping_amount) {
        this.shipping_amount = shipping_amount;
    }

    public BigDecimal getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(BigDecimal discount_amount) {
        this.discount_amount = discount_amount;
    }

    public BigDecimal getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(BigDecimal pay_amount) {
        this.pay_amount = pay_amount;
    }

    public String getBuyer_note() {
        return buyer_note;
    }

    public void setBuyer_note(String buyer_note) {
        this.buyer_note = buyer_note == null ? null : buyer_note.trim();
    }

    public String getSeller_note() {
        return seller_note;
    }

    public void setSeller_note(String seller_note) {
        this.seller_note = seller_note == null ? null : seller_note.trim();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }

    public String getReceive_name() {
        return receive_name;
    }

    public void setReceive_name(String receive_name) {
        this.receive_name = receive_name == null ? null : receive_name.trim();
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code == null ? null : postal_code.trim();
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number == null ? null : phone_number.trim();
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number == null ? null : mobile_number.trim();
    }

    public Integer getProvince_id() {
        return province_id;
    }

    public void setProvince_id(Integer province_id) {
        this.province_id = province_id;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name == null ? null : province_name.trim();
    }

    public Integer getCity_id() {
        return city_id;
    }

    public void setCity_id(Integer city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name == null ? null : city_name.trim();
    }

    public Integer getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(Integer district_id) {
        this.district_id = district_id;
    }

    public String getDistrict_name() {
        return district_name;
    }

    public void setDistrict_name(String district_name) {
        this.district_name = district_name == null ? null : district_name.trim();
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address == null ? null : shipping_address.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getInvoice_title() {
        return invoice_title;
    }

    public void setInvoice_title(String invoice_title) {
        this.invoice_title = invoice_title == null ? null : invoice_title.trim();
    }

    public String getInvoice_note() {
        return invoice_note;
    }

    public void setInvoice_note(String invoice_note) {
        this.invoice_note = invoice_note == null ? null : invoice_note.trim();
    }

    public BigDecimal getInvoice_amount() {
        return invoice_amount;
    }

    public void setInvoice_amount(BigDecimal invoice_amount) {
        this.invoice_amount = invoice_amount;
    }

    public String getIs_value_declared() {
        return is_value_declared;
    }

    public void setIs_value_declared(String is_value_declared) {
        this.is_value_declared = is_value_declared == null ? null : is_value_declared.trim();
    }

    public BigDecimal getDeclaring_value_amount() {
        return declaring_value_amount;
    }

    public void setDeclaring_value_amount(BigDecimal declaring_value_amount) {
        this.declaring_value_amount = declaring_value_amount;
    }

    public String getIs_payment_collected() {
        return is_payment_collected;
    }

    public void setIs_payment_collected(String is_payment_collected) {
        this.is_payment_collected = is_payment_collected == null ? null : is_payment_collected.trim();
    }

    public BigDecimal getCollecting_payment_amount() {
        return collecting_payment_amount;
    }

    public void setCollecting_payment_amount(BigDecimal collecting_payment_amount) {
        this.collecting_payment_amount = collecting_payment_amount;
    }

    public String getPriority_code() {
        return priority_code;
    }

    public void setPriority_code(String priority_code) {
        this.priority_code = priority_code == null ? null : priority_code.trim();
    }

    public String getOrder_source() {
        return order_source;
    }

    public void setOrder_source(String order_source) {
        this.order_source = order_source == null ? null : order_source.trim();
    }

    public Integer getParent_order_id() {
        return parent_order_id;
    }

    public void setParent_order_id(Integer parent_order_id) {
        this.parent_order_id = parent_order_id;
    }

    public Integer getRoot_order_id() {
        return root_order_id;
    }

    public void setRoot_order_id(Integer root_order_id) {
        this.root_order_id = root_order_id;
    }

    public String getParent_oms_order_sn() {
        return parent_oms_order_sn;
    }

    public void setParent_oms_order_sn(String parent_oms_order_sn) {
        this.parent_oms_order_sn = parent_oms_order_sn == null ? null : parent_oms_order_sn.trim();
    }

    public String getRoot_oms_order_sn() {
        return root_oms_order_sn;
    }

    public void setRoot_oms_order_sn(String root_oms_order_sn) {
        this.root_oms_order_sn = root_oms_order_sn == null ? null : root_oms_order_sn.trim();
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name == null ? null : brand_name.trim();
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name == null ? null : shop_name.trim();
    }

    public String getShop_order_sn() {
        return shop_order_sn;
    }

    public void setShop_order_sn(String shop_order_sn) {
        this.shop_order_sn = shop_order_sn == null ? null : shop_order_sn.trim();
    }

    public String getBuyer_name() {
        return buyer_name;
    }

    public void setBuyer_name(String buyer_name) {
        this.buyer_name = buyer_name == null ? null : buyer_name.trim();
    }

    public String getBuyer_phone() {
        return buyer_phone;
    }

    public void setBuyer_phone(String buyer_phone) {
        this.buyer_phone = buyer_phone == null ? null : buyer_phone.trim();
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name == null ? null : nick_name.trim();
    }

    public Integer getShipping_id() {
        return shipping_id;
    }

    public void setShipping_id(Integer shipping_id) {
        this.shipping_id = shipping_id;
    }

    public String getTracking_number() {
        return tracking_number;
    }

    public void setTracking_number(String tracking_number) {
        this.tracking_number = tracking_number == null ? null : tracking_number.trim();
    }

    public String getProvider_code() {
        return provider_code;
    }

    public void setProvider_code(String provider_code) {
        this.provider_code = provider_code == null ? null : provider_code.trim();
    }

    public String getProvider_order_type() {
        return provider_order_type;
    }

    public void setProvider_order_type(String provider_order_type) {
        this.provider_order_type = provider_order_type == null ? null : provider_order_type.trim();
    }

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name == null ? null : provider_name.trim();
    }

    public String getTms_company() {
        return tms_company;
    }

    public void setTms_company(String tms_company) {
        this.tms_company = tms_company == null ? null : tms_company.trim();
    }

    public String getTms_contact() {
        return tms_contact;
    }

    public void setTms_contact(String tms_contact) {
        this.tms_contact = tms_contact == null ? null : tms_contact.trim();
    }

    public String getTms_mobile() {
        return tms_mobile;
    }

    public void setTms_mobile(String tms_mobile) {
        this.tms_mobile = tms_mobile == null ? null : tms_mobile.trim();
    }

    public String getTms_shipping_no() {
        return tms_shipping_no;
    }

    public void setTms_shipping_no(String tms_shipping_no) {
        this.tms_shipping_no = tms_shipping_no == null ? null : tms_shipping_no.trim();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    public String getCreated_user() {
        return created_user;
    }

    public void setCreated_user(String created_user) {
        this.created_user = created_user == null ? null : created_user.trim();
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public String getLast_updated_user() {
        return last_updated_user;
    }

    public void setLast_updated_user(String last_updated_user) {
        this.last_updated_user = last_updated_user == null ? null : last_updated_user.trim();
    }

    public Date getLast_updated_time() {
        return last_updated_time;
    }

    public void setLast_updated_time(Date last_updated_time) {
        this.last_updated_time = last_updated_time;
    }
    
    public Date getArrival_time() {
		return arrival_time;
	}

	public void setArrival_time(Date arrival_time) {
		this.arrival_time = arrival_time;
	}

	public BigDecimal getShipping_wms_weight() {
		return shipping_wms_weight;
	}

	public void setShipping_wms_weight(BigDecimal shipping_wms_weight) {
		this.shipping_wms_weight = shipping_wms_weight;
	}

	public String getBatch_order_sn() {
		return batch_order_sn;
	}

	public void setBatch_order_sn(String batch_order_sn) {
		this.batch_order_sn = batch_order_sn;
	}
	
	public String getWarehouse_name() {
		return warehouse_name;
	}

	public void setWarehouse_name(String warehouse_name) {
		this.warehouse_name = warehouse_name;
	}

	public String getPhysical_warehouse_name() {
		return physical_warehouse_name;
	}

	public void setPhysical_warehouse_name(String physical_warehouse_name) {
		this.physical_warehouse_name = physical_warehouse_name;
	}
	
	public String getIs_merge_order() {
		return is_merge_order;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
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
	
	public String getAftersale_phone() {
        return aftersale_phone;
    }

    public void setAftersale_phone(String aftersale_phone) {
        this.aftersale_phone = aftersale_phone;
    }

    public int getCanAllot() {
		return canAllot;
	}

	public void setCanAllot(int canAllot) {
		this.canAllot = canAllot;
	}

	@Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        OrderInfo other = (OrderInfo) that;
        return (this.getOrder_id() == null ? other.getOrder_id() == null : this.getOrder_id().equals(other.getOrder_id()))
            && (this.getOrder_status() == null ? other.getOrder_status() == null : this.getOrder_status().equals(other.getOrder_status()))
            && (this.getCustomer_id() == null ? other.getCustomer_id() == null : this.getCustomer_id().equals(other.getCustomer_id()))
            && (this.getPhysical_warehouse_id() == null ? other.getPhysical_warehouse_id() == null : this.getPhysical_warehouse_id().equals(other.getPhysical_warehouse_id()))
            && (this.getWarehouse_id() == null ? other.getWarehouse_id() == null : this.getWarehouse_id().equals(other.getWarehouse_id()))
            && (this.getOrder_type() == null ? other.getOrder_type() == null : this.getOrder_type().equals(other.getOrder_type()))
            && (this.getOms_order_sn() == null ? other.getOms_order_sn() == null : this.getOms_order_sn().equals(other.getOms_order_sn()))
            && (this.getOms_order_type() == null ? other.getOms_order_type() == null : this.getOms_order_type().equals(other.getOms_order_type()))
            && (this.getOrder_time() == null ? other.getOrder_time() == null : this.getOrder_time().equals(other.getOrder_time()))
            && (this.getPay_time() == null ? other.getPay_time() == null : this.getPay_time().equals(other.getPay_time()))
            && (this.getIs_reserved() == null ? other.getIs_reserved() == null : this.getIs_reserved().equals(other.getIs_reserved()))
            && (this.getReserved_time() == null ? other.getReserved_time() == null : this.getReserved_time().equals(other.getReserved_time()))
            && (this.getShipping_time() == null ? other.getShipping_time() == null : this.getShipping_time().equals(other.getShipping_time()))
            && (this.getGoods_amount() == null ? other.getGoods_amount() == null : this.getGoods_amount().equals(other.getGoods_amount()))
            && (this.getShipping_amount() == null ? other.getShipping_amount() == null : this.getShipping_amount().equals(other.getShipping_amount()))
            && (this.getDiscount_amount() == null ? other.getDiscount_amount() == null : this.getDiscount_amount().equals(other.getDiscount_amount()))
            && (this.getPay_amount() == null ? other.getPay_amount() == null : this.getPay_amount().equals(other.getPay_amount()))
            && (this.getBuyer_note() == null ? other.getBuyer_note() == null : this.getBuyer_note().equals(other.getBuyer_note()))
            && (this.getSeller_note() == null ? other.getSeller_note() == null : this.getSeller_note().equals(other.getSeller_note()))
            && (this.getNote() == null ? other.getNote() == null : this.getNote().equals(other.getNote()))
            && (this.getReceive_name() == null ? other.getReceive_name() == null : this.getReceive_name().equals(other.getReceive_name()))
            && (this.getPostal_code() == null ? other.getPostal_code() == null : this.getPostal_code().equals(other.getPostal_code()))
            && (this.getPhone_number() == null ? other.getPhone_number() == null : this.getPhone_number().equals(other.getPhone_number()))
            && (this.getMobile_number() == null ? other.getMobile_number() == null : this.getMobile_number().equals(other.getMobile_number()))
            && (this.getProvince_id() == null ? other.getProvince_id() == null : this.getProvince_id().equals(other.getProvince_id()))
            && (this.getProvince_name() == null ? other.getProvince_name() == null : this.getProvince_name().equals(other.getProvince_name()))
            && (this.getCity_id() == null ? other.getCity_id() == null : this.getCity_id().equals(other.getCity_id()))
            && (this.getCity_name() == null ? other.getCity_name() == null : this.getCity_name().equals(other.getCity_name()))
            && (this.getDistrict_id() == null ? other.getDistrict_id() == null : this.getDistrict_id().equals(other.getDistrict_id()))
            && (this.getDistrict_name() == null ? other.getDistrict_name() == null : this.getDistrict_name().equals(other.getDistrict_name()))
            && (this.getShipping_address() == null ? other.getShipping_address() == null : this.getShipping_address().equals(other.getShipping_address()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getSex() == null ? other.getSex() == null : this.getSex().equals(other.getSex()))
            && (this.getInvoice_title() == null ? other.getInvoice_title() == null : this.getInvoice_title().equals(other.getInvoice_title()))
            && (this.getInvoice_note() == null ? other.getInvoice_note() == null : this.getInvoice_note().equals(other.getInvoice_note()))
            && (this.getInvoice_amount() == null ? other.getInvoice_amount() == null : this.getInvoice_amount().equals(other.getInvoice_amount()))
            && (this.getIs_value_declared() == null ? other.getIs_value_declared() == null : this.getIs_value_declared().equals(other.getIs_value_declared()))
            && (this.getDeclaring_value_amount() == null ? other.getDeclaring_value_amount() == null : this.getDeclaring_value_amount().equals(other.getDeclaring_value_amount()))
            && (this.getIs_payment_collected() == null ? other.getIs_payment_collected() == null : this.getIs_payment_collected().equals(other.getIs_payment_collected()))
            && (this.getCollecting_payment_amount() == null ? other.getCollecting_payment_amount() == null : this.getCollecting_payment_amount().equals(other.getCollecting_payment_amount()))
            && (this.getPriority_code() == null ? other.getPriority_code() == null : this.getPriority_code().equals(other.getPriority_code()))
            && (this.getOrder_source() == null ? other.getOrder_source() == null : this.getOrder_source().equals(other.getOrder_source()))
            && (this.getParent_order_id() == null ? other.getParent_order_id() == null : this.getParent_order_id().equals(other.getParent_order_id()))
            && (this.getRoot_order_id() == null ? other.getRoot_order_id() == null : this.getRoot_order_id().equals(other.getRoot_order_id()))
            && (this.getParent_oms_order_sn() == null ? other.getParent_oms_order_sn() == null : this.getParent_oms_order_sn().equals(other.getParent_oms_order_sn()))
            && (this.getRoot_oms_order_sn() == null ? other.getRoot_oms_order_sn() == null : this.getRoot_oms_order_sn().equals(other.getRoot_oms_order_sn()))
            && (this.getBrand_name() == null ? other.getBrand_name() == null : this.getBrand_name().equals(other.getBrand_name()))
            && (this.getShop_name() == null ? other.getShop_name() == null : this.getShop_name().equals(other.getShop_name()))
            && (this.getShop_order_sn() == null ? other.getShop_order_sn() == null : this.getShop_order_sn().equals(other.getShop_order_sn()))
            && (this.getBuyer_name() == null ? other.getBuyer_name() == null : this.getBuyer_name().equals(other.getBuyer_name()))
            && (this.getBuyer_phone() == null ? other.getBuyer_phone() == null : this.getBuyer_phone().equals(other.getBuyer_phone()))
            && (this.getNick_name() == null ? other.getNick_name() == null : this.getNick_name().equals(other.getNick_name()))
            && (this.getShipping_id() == null ? other.getShipping_id() == null : this.getShipping_id().equals(other.getShipping_id()))
            && (this.getTracking_number() == null ? other.getTracking_number() == null : this.getTracking_number().equals(other.getTracking_number()))
            && (this.getProvider_code() == null ? other.getProvider_code() == null : this.getProvider_code().equals(other.getProvider_code()))
            && (this.getProvider_order_type() == null ? other.getProvider_order_type() == null : this.getProvider_order_type().equals(other.getProvider_order_type()))
            && (this.getProvider_name() == null ? other.getProvider_name() == null : this.getProvider_name().equals(other.getProvider_name()))
            && (this.getTms_company() == null ? other.getTms_company() == null : this.getTms_company().equals(other.getTms_company()))
            && (this.getTms_contact() == null ? other.getTms_contact() == null : this.getTms_contact().equals(other.getTms_contact()))
            && (this.getTms_mobile() == null ? other.getTms_mobile() == null : this.getTms_mobile().equals(other.getTms_mobile()))
            && (this.getTms_shipping_no() == null ? other.getTms_shipping_no() == null : this.getTms_shipping_no().equals(other.getTms_shipping_no()))
            && (this.getCurrency() == null ? other.getCurrency() == null : this.getCurrency().equals(other.getCurrency()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()))
            && (this.getLast_updated_user() == null ? other.getLast_updated_user() == null : this.getLast_updated_user().equals(other.getLast_updated_user()))
            && (this.getLast_updated_time() == null ? other.getLast_updated_time() == null : this.getLast_updated_time().equals(other.getLast_updated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOrder_id() == null) ? 0 : getOrder_id().hashCode());
        result = prime * result + ((getOrder_status() == null) ? 0 : getOrder_status().hashCode());
        result = prime * result + ((getCustomer_id() == null) ? 0 : getCustomer_id().hashCode());
        result = prime * result + ((getPhysical_warehouse_id() == null) ? 0 : getPhysical_warehouse_id().hashCode());
        result = prime * result + ((getWarehouse_id() == null) ? 0 : getWarehouse_id().hashCode());
        result = prime * result + ((getOrder_type() == null) ? 0 : getOrder_type().hashCode());
        result = prime * result + ((getOms_order_sn() == null) ? 0 : getOms_order_sn().hashCode());
        result = prime * result + ((getOms_order_type() == null) ? 0 : getOms_order_type().hashCode());
        result = prime * result + ((getOrder_time() == null) ? 0 : getOrder_time().hashCode());
        result = prime * result + ((getPay_time() == null) ? 0 : getPay_time().hashCode());
        result = prime * result + ((getIs_reserved() == null) ? 0 : getIs_reserved().hashCode());
        result = prime * result + ((getReserved_time() == null) ? 0 : getReserved_time().hashCode());
        result = prime * result + ((getShipping_time() == null) ? 0 : getShipping_time().hashCode());
        result = prime * result + ((getGoods_amount() == null) ? 0 : getGoods_amount().hashCode());
        result = prime * result + ((getShipping_amount() == null) ? 0 : getShipping_amount().hashCode());
        result = prime * result + ((getDiscount_amount() == null) ? 0 : getDiscount_amount().hashCode());
        result = prime * result + ((getPay_amount() == null) ? 0 : getPay_amount().hashCode());
        result = prime * result + ((getBuyer_note() == null) ? 0 : getBuyer_note().hashCode());
        result = prime * result + ((getSeller_note() == null) ? 0 : getSeller_note().hashCode());
        result = prime * result + ((getNote() == null) ? 0 : getNote().hashCode());
        result = prime * result + ((getReceive_name() == null) ? 0 : getReceive_name().hashCode());
        result = prime * result + ((getPostal_code() == null) ? 0 : getPostal_code().hashCode());
        result = prime * result + ((getPhone_number() == null) ? 0 : getPhone_number().hashCode());
        result = prime * result + ((getMobile_number() == null) ? 0 : getMobile_number().hashCode());
        result = prime * result + ((getProvince_id() == null) ? 0 : getProvince_id().hashCode());
        result = prime * result + ((getProvince_name() == null) ? 0 : getProvince_name().hashCode());
        result = prime * result + ((getCity_id() == null) ? 0 : getCity_id().hashCode());
        result = prime * result + ((getCity_name() == null) ? 0 : getCity_name().hashCode());
        result = prime * result + ((getDistrict_id() == null) ? 0 : getDistrict_id().hashCode());
        result = prime * result + ((getDistrict_name() == null) ? 0 : getDistrict_name().hashCode());
        result = prime * result + ((getShipping_address() == null) ? 0 : getShipping_address().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getSex() == null) ? 0 : getSex().hashCode());
        result = prime * result + ((getInvoice_title() == null) ? 0 : getInvoice_title().hashCode());
        result = prime * result + ((getInvoice_note() == null) ? 0 : getInvoice_note().hashCode());
        result = prime * result + ((getInvoice_amount() == null) ? 0 : getInvoice_amount().hashCode());
        result = prime * result + ((getIs_value_declared() == null) ? 0 : getIs_value_declared().hashCode());
        result = prime * result + ((getDeclaring_value_amount() == null) ? 0 : getDeclaring_value_amount().hashCode());
        result = prime * result + ((getIs_payment_collected() == null) ? 0 : getIs_payment_collected().hashCode());
        result = prime * result + ((getCollecting_payment_amount() == null) ? 0 : getCollecting_payment_amount().hashCode());
        result = prime * result + ((getPriority_code() == null) ? 0 : getPriority_code().hashCode());
        result = prime * result + ((getOrder_source() == null) ? 0 : getOrder_source().hashCode());
        result = prime * result + ((getParent_order_id() == null) ? 0 : getParent_order_id().hashCode());
        result = prime * result + ((getRoot_order_id() == null) ? 0 : getRoot_order_id().hashCode());
        result = prime * result + ((getParent_oms_order_sn() == null) ? 0 : getParent_oms_order_sn().hashCode());
        result = prime * result + ((getRoot_oms_order_sn() == null) ? 0 : getRoot_oms_order_sn().hashCode());
        result = prime * result + ((getBrand_name() == null) ? 0 : getBrand_name().hashCode());
        result = prime * result + ((getShop_name() == null) ? 0 : getShop_name().hashCode());
        result = prime * result + ((getShop_order_sn() == null) ? 0 : getShop_order_sn().hashCode());
        result = prime * result + ((getBuyer_name() == null) ? 0 : getBuyer_name().hashCode());
        result = prime * result + ((getBuyer_phone() == null) ? 0 : getBuyer_phone().hashCode());
        result = prime * result + ((getNick_name() == null) ? 0 : getNick_name().hashCode());
        result = prime * result + ((getShipping_id() == null) ? 0 : getShipping_id().hashCode());
        result = prime * result + ((getTracking_number() == null) ? 0 : getTracking_number().hashCode());
        result = prime * result + ((getProvider_code() == null) ? 0 : getProvider_code().hashCode());
        result = prime * result + ((getProvider_order_type() == null) ? 0 : getProvider_order_type().hashCode());
        result = prime * result + ((getProvider_name() == null) ? 0 : getProvider_name().hashCode());
        result = prime * result + ((getTms_company() == null) ? 0 : getTms_company().hashCode());
        result = prime * result + ((getTms_contact() == null) ? 0 : getTms_contact().hashCode());
        result = prime * result + ((getTms_mobile() == null) ? 0 : getTms_mobile().hashCode());
        result = prime * result + ((getTms_shipping_no() == null) ? 0 : getTms_shipping_no().hashCode());
        result = prime * result + ((getCurrency() == null) ? 0 : getCurrency().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        result = prime * result + ((getLast_updated_user() == null) ? 0 : getLast_updated_user().hashCode());
        result = prime * result + ((getLast_updated_time() == null) ? 0 : getLast_updated_time().hashCode());
        return result;
    }
}