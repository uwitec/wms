package com.leqee.wms.entity;

import java.io.Serializable;

public class ConfigPrintDispatch  implements Serializable {

	private int config_id;
	
	private String shop_name;
	
	private int customer_id;
	
	private String template_file_name;
	
	private int page_line;
	
	private String created_user;
	
	private String created_time;
	
	private static final long serialVersionUID = 1L;

	public int getConfig_id() {
		return config_id;
	}

	public void setConfig_id(int config_id) {
		this.config_id = config_id;
	}

	public String getShop_name() {
		return shop_name;
	}

	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

	public int getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(int customer_id) {
		this.customer_id = customer_id;
	}

	public String getTemplate_file_name() {
		return template_file_name;
	}

	public void setTemplate_file_name(String template_file_name) {
		this.template_file_name = template_file_name;
	}
	
	public int getPage_line() {
		return page_line;
	}

	public void setPage_line(int page_line) {
		this.page_line = page_line;
	}

	public String getCreated_user() {
		return created_user;
	}

	public void setCreated_user(String created_user) {
		this.created_user = created_user;
	}

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
