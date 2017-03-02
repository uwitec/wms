package com.leqee.wms.entity;

import java.io.Serializable;

public class Region implements Serializable {
    private Short region_id;

    private Short parent_id;

    private String region_name;

    private Short region_type;

    private Short agency_id;

    private static final long serialVersionUID = 1L;

    public Short getRegion_id() {
        return region_id;
    }

    public void setRegion_id(Short region_id) {
        this.region_id = region_id;
    }

    public Short getParent_id() {
        return parent_id;
    }

    public void setParent_id(Short parent_id) {
        this.parent_id = parent_id;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name == null ? null : region_name.trim();
    }
    
    public Short getRegion_type() {
		return region_type;
	}

	public void setRegion_type(Short region_type) {
		this.region_type = region_type;
	}

	public Short getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(Short agency_id) {
        this.agency_id = agency_id;
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
        Region other = (Region) that;
        return (this.getRegion_id() == null ? other.getRegion_id() == null : this.getRegion_id().equals(other.getRegion_id()))
            && (this.getParent_id() == null ? other.getParent_id() == null : this.getParent_id().equals(other.getParent_id()))
            && (this.getRegion_name() == null ? other.getRegion_name() == null : this.getRegion_name().equals(other.getRegion_name()))
            && (this.getRegion_type() == null ? other.getRegion_type() == null : this.getRegion_type().equals(other.getRegion_type()))
            && (this.getAgency_id() == null ? other.getAgency_id() == null : this.getAgency_id().equals(other.getAgency_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRegion_id() == null) ? 0 : getRegion_id().hashCode());
        result = prime * result + ((getParent_id() == null) ? 0 : getParent_id().hashCode());
        result = prime * result + ((getRegion_name() == null) ? 0 : getRegion_name().hashCode());
        result = prime * result + ((getRegion_type() == null) ? 0 : getRegion_type().hashCode());
        result = prime * result + ((getAgency_id() == null) ? 0 : getAgency_id().hashCode());
        return result;
    }
}