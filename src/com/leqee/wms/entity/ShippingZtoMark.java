package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ShippingZtoMark implements Serializable {
    private Integer mark_id;

    private Integer province_id;

    private String province_name;

    private Integer city_id;

    private String city_name;

    private Integer district_id;

    private String district_name;

    private String mark;

    private String marke;

    private Date created_stamp;

    private Date last_updated_stamp;

    private static final long serialVersionUID = 1L;

    public Integer getMark_id() {
        return mark_id;
    }

    public void setMark_id(Integer mark_id) {
        this.mark_id = mark_id;
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

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark == null ? null : mark.trim();
    }

    public String getMarke() {
        return marke;
    }

    public void setMarke(String marke) {
        this.marke = marke == null ? null : marke.trim();
    }

    public Date getCreated_stamp() {
        return created_stamp;
    }

    public void setCreated_stamp(Date created_stamp) {
        this.created_stamp = created_stamp;
    }

    public Date getLast_updated_stamp() {
        return last_updated_stamp;
    }

    public void setLast_updated_stamp(Date last_updated_stamp) {
        this.last_updated_stamp = last_updated_stamp;
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
        ShippingZtoMark other = (ShippingZtoMark) that;
        return (this.getMark_id() == null ? other.getMark_id() == null : this.getMark_id().equals(other.getMark_id()))
            && (this.getProvince_id() == null ? other.getProvince_id() == null : this.getProvince_id().equals(other.getProvince_id()))
            && (this.getProvince_name() == null ? other.getProvince_name() == null : this.getProvince_name().equals(other.getProvince_name()))
            && (this.getCity_id() == null ? other.getCity_id() == null : this.getCity_id().equals(other.getCity_id()))
            && (this.getCity_name() == null ? other.getCity_name() == null : this.getCity_name().equals(other.getCity_name()))
            && (this.getDistrict_id() == null ? other.getDistrict_id() == null : this.getDistrict_id().equals(other.getDistrict_id()))
            && (this.getDistrict_name() == null ? other.getDistrict_name() == null : this.getDistrict_name().equals(other.getDistrict_name()))
            && (this.getMark() == null ? other.getMark() == null : this.getMark().equals(other.getMark()))
            && (this.getMarke() == null ? other.getMarke() == null : this.getMarke().equals(other.getMarke()))
            && (this.getCreated_stamp() == null ? other.getCreated_stamp() == null : this.getCreated_stamp().equals(other.getCreated_stamp()))
            && (this.getLast_updated_stamp() == null ? other.getLast_updated_stamp() == null : this.getLast_updated_stamp().equals(other.getLast_updated_stamp()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMark_id() == null) ? 0 : getMark_id().hashCode());
        result = prime * result + ((getProvince_id() == null) ? 0 : getProvince_id().hashCode());
        result = prime * result + ((getProvince_name() == null) ? 0 : getProvince_name().hashCode());
        result = prime * result + ((getCity_id() == null) ? 0 : getCity_id().hashCode());
        result = prime * result + ((getCity_name() == null) ? 0 : getCity_name().hashCode());
        result = prime * result + ((getDistrict_id() == null) ? 0 : getDistrict_id().hashCode());
        result = prime * result + ((getDistrict_name() == null) ? 0 : getDistrict_name().hashCode());
        result = prime * result + ((getMark() == null) ? 0 : getMark().hashCode());
        result = prime * result + ((getMarke() == null) ? 0 : getMarke().hashCode());
        result = prime * result + ((getCreated_stamp() == null) ? 0 : getCreated_stamp().hashCode());
        result = prime * result + ((getLast_updated_stamp() == null) ? 0 : getLast_updated_stamp().hashCode());
        return result;
    }
}