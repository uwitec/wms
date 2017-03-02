package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class ShippingYundaTrackingNumber implements Serializable {
    private Integer shipment_id;

    private String tracking_number;

    private String package_position;

    private String package_no;

    private String station;

    private String station_no;

    private String sender_branch_no;

    private String sender_branch;

    private String lattice_mouth_no;

    private Date created_stamp;

    private String pdf_info;

    private static final long serialVersionUID = 1L;

    public Integer getShipment_id() {
        return shipment_id;
    }

    public void setShipment_id(Integer shipment_id) {
        this.shipment_id = shipment_id;
    }

    public String getTracking_number() {
        return tracking_number;
    }

    public void setTracking_number(String tracking_number) {
        this.tracking_number = tracking_number == null ? null : tracking_number.trim();
    }

    public String getPackage_position() {
        return package_position;
    }

    public void setPackage_position(String package_position) {
        this.package_position = package_position == null ? null : package_position.trim();
    }

    public String getPackage_no() {
        return package_no;
    }

    public void setPackage_no(String package_no) {
        this.package_no = package_no == null ? null : package_no.trim();
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station == null ? null : station.trim();
    }

    public String getStation_no() {
        return station_no;
    }

    public void setStation_no(String station_no) {
        this.station_no = station_no == null ? null : station_no.trim();
    }

    public String getSender_branch_no() {
        return sender_branch_no;
    }

    public void setSender_branch_no(String sender_branch_no) {
        this.sender_branch_no = sender_branch_no == null ? null : sender_branch_no.trim();
    }

    public String getSender_branch() {
        return sender_branch;
    }

    public void setSender_branch(String sender_branch) {
        this.sender_branch = sender_branch == null ? null : sender_branch.trim();
    }

    public String getLattice_mouth_no() {
        return lattice_mouth_no;
    }

    public void setLattice_mouth_no(String lattice_mouth_no) {
        this.lattice_mouth_no = lattice_mouth_no == null ? null : lattice_mouth_no.trim();
    }

    public Date getCreated_stamp() {
        return created_stamp;
    }

    public void setCreated_stamp(Date created_stamp) {
        this.created_stamp = created_stamp;
    }

    public String getPdf_info() {
        return pdf_info;
    }

    public void setPdf_info(String pdf_info) {
        this.pdf_info = pdf_info == null ? null : pdf_info.trim();
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
        ShippingYundaTrackingNumber other = (ShippingYundaTrackingNumber) that;
        return (this.getShipment_id() == null ? other.getShipment_id() == null : this.getShipment_id().equals(other.getShipment_id()))
            && (this.getTracking_number() == null ? other.getTracking_number() == null : this.getTracking_number().equals(other.getTracking_number()))
            && (this.getPackage_position() == null ? other.getPackage_position() == null : this.getPackage_position().equals(other.getPackage_position()))
            && (this.getPackage_no() == null ? other.getPackage_no() == null : this.getPackage_no().equals(other.getPackage_no()))
            && (this.getStation() == null ? other.getStation() == null : this.getStation().equals(other.getStation()))
            && (this.getStation_no() == null ? other.getStation_no() == null : this.getStation_no().equals(other.getStation_no()))
            && (this.getSender_branch_no() == null ? other.getSender_branch_no() == null : this.getSender_branch_no().equals(other.getSender_branch_no()))
            && (this.getSender_branch() == null ? other.getSender_branch() == null : this.getSender_branch().equals(other.getSender_branch()))
            && (this.getLattice_mouth_no() == null ? other.getLattice_mouth_no() == null : this.getLattice_mouth_no().equals(other.getLattice_mouth_no()))
            && (this.getCreated_stamp() == null ? other.getCreated_stamp() == null : this.getCreated_stamp().equals(other.getCreated_stamp()))
            && (this.getPdf_info() == null ? other.getPdf_info() == null : this.getPdf_info().equals(other.getPdf_info()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getShipment_id() == null) ? 0 : getShipment_id().hashCode());
        result = prime * result + ((getTracking_number() == null) ? 0 : getTracking_number().hashCode());
        result = prime * result + ((getPackage_position() == null) ? 0 : getPackage_position().hashCode());
        result = prime * result + ((getPackage_no() == null) ? 0 : getPackage_no().hashCode());
        result = prime * result + ((getStation() == null) ? 0 : getStation().hashCode());
        result = prime * result + ((getStation_no() == null) ? 0 : getStation_no().hashCode());
        result = prime * result + ((getSender_branch_no() == null) ? 0 : getSender_branch_no().hashCode());
        result = prime * result + ((getSender_branch() == null) ? 0 : getSender_branch().hashCode());
        result = prime * result + ((getLattice_mouth_no() == null) ? 0 : getLattice_mouth_no().hashCode());
        result = prime * result + ((getCreated_stamp() == null) ? 0 : getCreated_stamp().hashCode());
        result = prime * result + ((getPdf_info() == null) ? 0 : getPdf_info().hashCode());
        return result;
    }
}