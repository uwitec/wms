package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class Shipping implements Serializable {
	
	public static final String SHIPPING_CODE_SF = "SF";
	public static final String SHIPPING_CODE_SFLY = "SFLY";
	
	
	
    private Integer shipping_id;

    private String shipping_code;

    private String shipping_name;

    private String shipping_company;

    private String is_cod;

    private String created_user;

    private Date created_time;

    
    private static final long serialVersionUID = 1L;
    
    
    

    public Integer getShipping_id() {
        return shipping_id;
    }

    public void setShipping_id(Integer shipping_id) {
        this.shipping_id = shipping_id;
    }

    public String getShipping_code() {
        return shipping_code;
    }

    public void setShipping_code(String shipping_code) {
        this.shipping_code = shipping_code == null ? null : shipping_code.trim();
    }

    public String getShipping_name() {
        return shipping_name;
    }

    public void setShipping_name(String shipping_name) {
        this.shipping_name = shipping_name == null ? null : shipping_name.trim();
    }

    public String getShipping_company() {
        return shipping_company;
    }

    public void setShipping_company(String shipping_company) {
        this.shipping_company = shipping_company == null ? null : shipping_company.trim();
    }

    public String getIs_cod() {
        return is_cod;
    }

    public void setIs_cod(String is_cod) {
        this.is_cod = is_cod == null ? null : is_cod.trim();
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
        Shipping other = (Shipping) that;
        return (this.getShipping_id() == null ? other.getShipping_id() == null : this.getShipping_id().equals(other.getShipping_id()))
            && (this.getShipping_code() == null ? other.getShipping_code() == null : this.getShipping_code().equals(other.getShipping_code()))
            && (this.getShipping_name() == null ? other.getShipping_name() == null : this.getShipping_name().equals(other.getShipping_name()))
            && (this.getShipping_company() == null ? other.getShipping_company() == null : this.getShipping_company().equals(other.getShipping_company()))
            && (this.getIs_cod() == null ? other.getIs_cod() == null : this.getIs_cod().equals(other.getIs_cod()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getShipping_id() == null) ? 0 : getShipping_id().hashCode());
        result = prime * result + ((getShipping_code() == null) ? 0 : getShipping_code().hashCode());
        result = prime * result + ((getShipping_name() == null) ? 0 : getShipping_name().hashCode());
        result = prime * result + ((getShipping_company() == null) ? 0 : getShipping_company().hashCode());
        result = prime * result + ((getIs_cod() == null) ? 0 : getIs_cod().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}