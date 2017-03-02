package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class Shop implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer shop_id;

	private String oms_shop_id;
		
	private String shop_name;
	
	private String file_name="";
	
	private Integer customer_id;
	
	private String created_user;
	
	
	private Date created_time;

    private String last_updated_user;

    private Date last_updated_time;
    
    public String getOms_shop_id() {
		return oms_shop_id;
	}

	public void setOms_shop_id(String oms_shop_id) {
		this.oms_shop_id = oms_shop_id;
	}

	public String getCreated_user() {
		return created_user;
	}

	public void setCreated_user(String created_user) {
		this.created_user = created_user;
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
		this.last_updated_user = last_updated_user;
	}

	public Date getLast_updated_time() {
		return last_updated_time;
	}

	public void setLast_updated_time(Date last_updated_time) {
		this.last_updated_time = last_updated_time;
	}

	

	public Integer getShop_id() {
		return shop_id;
	}

	public void setShop_id(Integer shop_id) {
		this.shop_id = shop_id;
	}

	public String getShop_name() {
		return shop_name;
	}

	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
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
        Shop other = (Shop) that;
        return (this.getOms_shop_id() == null ? other.getOms_shop_id() == null : this.getOms_shop_id().equals(other.getOms_shop_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOms_shop_id() == null) ? 0 : getOms_shop_id().hashCode());
        return result;
    }
	
}
