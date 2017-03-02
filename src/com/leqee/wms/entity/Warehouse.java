package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class Warehouse implements Serializable {
	
	// 仓库类型
	public final static String WAREHOUSE_TYPE_PHYSICAL = "PHYSICAL";       // 物理仓
	public final static String WAREHOUSE_TYPE_NORMAL = "NORMAL";           // 普通逻辑仓
	public final static String WAREHOUSE_TYPE_PACK = "PACK";               // 耗材逻辑仓
	public final static String WAREHOUSE_TYPE_RETURN = "RETURN";           // 退货逻辑仓
	public final static String WAREHOUSE_TYPE_MARKET = "MARKET";           // 市场物资仓
	public final static String WAREHOUSE_TYPE_SECOND_HAND = "SECOND_HAND"; // 二手逻辑仓
	
    private Integer warehouse_id;

    private String warehouse_name;

    private String warehouse_type;

    private Integer physical_warehouse_id;

    private Integer packbox_warehouse_id;
    
    private String is_physical;

    private Integer province_id;

    private String province_name;

    private Integer city_id;

    private String city_name;

    private Integer district_id;

    private String district_name;

    private String contact;

    private String contact_mobile;

    private String address;

    private String created_user;

    private Date created_time;

    private static final long serialVersionUID = 1L;

    
    public Integer getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public String getWarehouse_name() {
        return warehouse_name;
    }

    public void setWarehouse_name(String warehouse_name) {
        this.warehouse_name = warehouse_name == null ? null : warehouse_name.trim();
    }

    public String getWarehouse_type() {
        return warehouse_type;
    }

    public void setWarehouse_type(String warehouse_type) {
        this.warehouse_type = warehouse_type == null ? null : warehouse_type.trim();
    }


    public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getPackbox_warehouse_id() {
        return packbox_warehouse_id;
    }

    public void setPackbox_warehouse_id(Integer packbox_warehouse_id) {
        this.packbox_warehouse_id = packbox_warehouse_id;
    }

    public String getIs_physical() {
		return is_physical;
	}

	public void setIs_physical(String is_physical) {
		this.is_physical = is_physical;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact == null ? null : contact.trim();
    }

    public String getContact_mobile() {
        return contact_mobile;
    }

    public void setContact_mobile(String contact_mobile) {
        this.contact_mobile = contact_mobile == null ? null : contact_mobile.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
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
        Warehouse other = (Warehouse) that;
        return (this.getWarehouse_id() == null ? other.getWarehouse_id() == null : this.getWarehouse_id().equals(other.getWarehouse_id()))
            && (this.getWarehouse_name() == null ? other.getWarehouse_name() == null : this.getWarehouse_name().equals(other.getWarehouse_name()))
            && (this.getWarehouse_type() == null ? other.getWarehouse_type() == null : this.getWarehouse_type().equals(other.getWarehouse_type()))
            && (this.getPhysical_warehouse_id() == null ? other.getPhysical_warehouse_id() == null : this.getPhysical_warehouse_id().equals(other.getPhysical_warehouse_id()))
            && (this.getPackbox_warehouse_id() == null ? other.getPackbox_warehouse_id() == null : this.getPackbox_warehouse_id().equals(other.getPackbox_warehouse_id()))
            && (this.getProvince_id() == null ? other.getProvince_id() == null : this.getProvince_id().equals(other.getProvince_id()))
            && (this.getProvince_name() == null ? other.getProvince_name() == null : this.getProvince_name().equals(other.getProvince_name()))
            && (this.getCity_id() == null ? other.getCity_id() == null : this.getCity_id().equals(other.getCity_id()))
            && (this.getCity_name() == null ? other.getCity_name() == null : this.getCity_name().equals(other.getCity_name()))
            && (this.getDistrict_id() == null ? other.getDistrict_id() == null : this.getDistrict_id().equals(other.getDistrict_id()))
            && (this.getDistrict_name() == null ? other.getDistrict_name() == null : this.getDistrict_name().equals(other.getDistrict_name()))
            && (this.getContact() == null ? other.getContact() == null : this.getContact().equals(other.getContact()))
            && (this.getContact_mobile() == null ? other.getContact_mobile() == null : this.getContact_mobile().equals(other.getContact_mobile()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getWarehouse_id() == null) ? 0 : getWarehouse_id().hashCode());
        result = prime * result + ((getWarehouse_name() == null) ? 0 : getWarehouse_name().hashCode());
        result = prime * result + ((getWarehouse_type() == null) ? 0 : getWarehouse_type().hashCode());
        result = prime * result + ((getPhysical_warehouse_id() == null) ? 0 : getPhysical_warehouse_id().hashCode());
        result = prime * result + ((getPackbox_warehouse_id() == null) ? 0 : getPackbox_warehouse_id().hashCode());
        result = prime * result + ((getProvince_id() == null) ? 0 : getProvince_id().hashCode());
        result = prime * result + ((getProvince_name() == null) ? 0 : getProvince_name().hashCode());
        result = prime * result + ((getCity_id() == null) ? 0 : getCity_id().hashCode());
        result = prime * result + ((getCity_name() == null) ? 0 : getCity_name().hashCode());
        result = prime * result + ((getDistrict_id() == null) ? 0 : getDistrict_id().hashCode());
        result = prime * result + ((getDistrict_name() == null) ? 0 : getDistrict_name().hashCode());
        result = prime * result + ((getContact() == null) ? 0 : getContact().hashCode());
        result = prime * result + ((getContact_mobile() == null) ? 0 : getContact_mobile().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}