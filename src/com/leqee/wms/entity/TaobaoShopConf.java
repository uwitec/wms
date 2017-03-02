package com.leqee.wms.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class TaobaoShopConf implements Serializable , Cloneable {

	private static final long serialVersionUID = 5016293684713527906L;
	
    
    //淘宝店铺类型
	public final static String SHOP_TYPE_TAOBAO = "taobao";
	public final static String SHOP_TYPE_360BUY = "360buy";
	public final static String SHOP_TYPE_AMAZON = "amazon";
	public final static String SHOP_TYPE_SCN = "scn";
	public final static String SHOP_TYPE_WEIXIN = "weixin";
	public final static String SHOP_TYPE_WEIGOU = "weigou";
	public final static String SHOP_TYPE_VIPSHOP = "vipshop";
	public final static String SHOP_TYPE_YHD = "yhd";
	public final static String SHOP_TYPE_CHINAMOBILE = "ChinaMobile";
	public final static String SHOP_TYPE_JUMEI = "jumei";
	public final static String SHOP_TYPE_WEIXINQS = "weixinqs";
	public final static String SHOP_TYPE_KOUDAITONG = "koudaitong";
	public final static String SHOP_TYPE_WEIXINJF = "weixinjf";
	public final static String SHOP_TYPE_SFHK = "sfhk";
	public final static String SHOP_TYPE_360BUY_OVERSEAS = "360buy_overseas";
	public final static String SHOP_TYPE_MIYA = "miya";
	public final static String SHOP_TYPE_SUNING = "suning";
	public final static String SHOP_TYPE_BAIDUMALL = "baidumall";
	public final static String SHOP_TYPE_CUNTAO = "cuntao";
    
    

	private long taobao_shop_conf_id;
	private String role;
	private String nick;
	private long user_id;
	private String userId; 
	private Integer pay_id;
	private String pay_name;
	private String application_key;
	private long taobao_api_params_id;
	private String status;
	private long party_id;
	private String facility_id;
	private String distributor_id;
	private long shipping_id;
	private String type;
	private char is_erp_display;
	private String is_stock_update;
	private String shop_type;
	private BigDecimal inventory_ratio;
	private String group_id;
	private String is_main;
	
	

	@Override
	public String toString() {
		return "this is the TaobaoShopConf for " + nick;
	}

	public long getTaobao_shop_conf_id() {
		return taobao_shop_conf_id;
	}

	public void setTaobao_shop_conf_id(long taobao_shop_conf_id) {
		this.taobao_shop_conf_id = taobao_shop_conf_id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getPay_id() {
		return pay_id;
	}

	public void setPay_id(Integer pay_id) {
		this.pay_id = pay_id;
	}

	public String getPay_name() {
		return pay_name;
	}

	public void setPay_name(String pay_name) {
		this.pay_name = pay_name;
	}

	public String getApplication_key() {
		return application_key;
	}

	public void setApplication_key(String application_key) {
		this.application_key = application_key;
	}

	public long getTaobao_api_params_id() {
		return taobao_api_params_id;
	}

	public void setTaobao_api_params_id(long taobao_api_params_id) {
		this.taobao_api_params_id = taobao_api_params_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getParty_id() {
		return party_id;
	}

	public void setParty_id(long party_id) {
		this.party_id = party_id;
	}

	public String getFacility_id() {
		return facility_id;
	}

	public void setFacility_id(String facility_id) {
		this.facility_id = facility_id;
	}

	public String getDistributor_id() {
		return distributor_id;
	}

	public void setDistributor_id(String distributor_id) {
		this.distributor_id = distributor_id;
	}

	public long getShipping_id() {
		return shipping_id;
	}

	public void setShipping_id(long shipping_id) {
		this.shipping_id = shipping_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public char getIs_erp_display() {
		return is_erp_display;
	}

	public void setIs_erp_display(char is_erp_display) {
		this.is_erp_display = is_erp_display;
	}

	public String getShop_type() {
		return shop_type;
	}

	public void setShop_type(String shop_type) {
		this.shop_type = shop_type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public BigDecimal getInventory_ratio() {
		return inventory_ratio;
	}

	public void setInventory_ratio(BigDecimal inventory_ratio) {
		this.inventory_ratio = inventory_ratio;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getIs_main() {
		return is_main;
	}

	public void setIs_main(String is_main) {
		this.is_main = is_main;
	}
	

	public String getIs_stock_update() {
		return is_stock_update;
	}

	public void setIs_stock_update(String is_stock_update) {
		this.is_stock_update = is_stock_update;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	
	

}
