package com.leqee.wms.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Shipment implements Serializable {
	
	public static final String STATUS_SHIPPED = "SHIPPED";
	public static final String STATUS_WEIGHED = "WEIGHED";
	public static final String STATUS_INIT = "INIT";
	
	
    private Integer shipment_id;

    private Integer order_id;

    private String status;
   
	private String tracking_number;

    private Integer shipping_id;
    
    private BigDecimal shipping_wms_weight;

    private BigDecimal shipping_out_weight;

    private BigDecimal shipping_fee;

    private BigDecimal shipping_insured_fee;

    private BigDecimal estimated_shipping_fee;

    private String bill_note;

    private Integer packbox_product_id;

    private String packbox_is_out;

    private Date packbox_created_time;

    private Date packbox_out_time;

    private String mark;

    private String sf_destcode;

    private String sf_origincode;

    private String created_user;

    private Date created_time;

    private String last_updated_user;

    private Date last_updated_time;

    private Integer shipment_order_sequence_number;
    
    private List<ShipmentDetail> shipmentDetailList ;
    
    
    private static final long serialVersionUID = 1L;

    public Integer getShipment_id() {
        return shipment_id;
    }

    public void setShipment_id(Integer shipment_id) {
        this.shipment_id = shipment_id;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getTracking_number() {
        return tracking_number;
    }

    public void setTracking_number(String tracking_number) {
        this.tracking_number = tracking_number == null ? null : tracking_number.trim();
    }

    
    public Integer getShipping_id() {
		return shipping_id;
	}

	public void setShipping_id(Integer shipping_id) {
		this.shipping_id = shipping_id;
	}

	public BigDecimal getShipping_wms_weight() {
        return shipping_wms_weight;
    }

    public void setShipping_wms_weight(BigDecimal shipping_wms_weight) {
        this.shipping_wms_weight = shipping_wms_weight;
    }

    public BigDecimal getShipping_out_weight() {
        return shipping_out_weight;
    }

    public void setShipping_out_weight(BigDecimal shipping_out_weight) {
        this.shipping_out_weight = shipping_out_weight;
    }

    public BigDecimal getShipping_fee() {
        return shipping_fee;
    }

    public void setShipping_fee(BigDecimal shipping_fee) {
        this.shipping_fee = shipping_fee;
    }

    public BigDecimal getShipping_insured_fee() {
        return shipping_insured_fee;
    }

    public void setShipping_insured_fee(BigDecimal shipping_insured_fee) {
        this.shipping_insured_fee = shipping_insured_fee;
    }

    public BigDecimal getEstimated_shipping_fee() {
        return estimated_shipping_fee;
    }

    public void setEstimated_shipping_fee(BigDecimal estimated_shipping_fee) {
        this.estimated_shipping_fee = estimated_shipping_fee;
    }

    public String getBill_note() {
        return bill_note;
    }

    public void setBill_note(String bill_note) {
        this.bill_note = bill_note == null ? null : bill_note.trim();
    }

    public Integer getPackbox_product_id() {
        return packbox_product_id;
    }

    public void setPackbox_product_id(Integer packbox_product_id) {
        this.packbox_product_id = packbox_product_id;
    }

    public String getPackbox_is_out() {
        return packbox_is_out;
    }

    public void setPackbox_is_out(String packbox_is_out) {
        this.packbox_is_out = packbox_is_out == null ? null : packbox_is_out.trim();
    }

    public Date getPackbox_created_time() {
        return packbox_created_time;
    }

    public void setPackbox_created_time(Date packbox_created_time) {
        this.packbox_created_time = packbox_created_time;
    }

    public Date getPackbox_out_time() {
        return packbox_out_time;
    }

    public void setPackbox_out_time(Date packbox_out_time) {
        this.packbox_out_time = packbox_out_time;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark == null ? null : mark.trim();
    }

    public String getSf_destcode() {
        return sf_destcode;
    }

    public void setSf_destcode(String sf_destcode) {
        this.sf_destcode = sf_destcode == null ? null : sf_destcode.trim();
    }
    


    public String getSf_origincode() {
        return sf_origincode;
    }

    public void setSf_origincode(String sf_origincode) {
        this.sf_origincode = sf_origincode == null ? null : sf_origincode.trim();
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

    public List<ShipmentDetail> getShipmentDetailList() {
		return shipmentDetailList;
	}

	public void setShipmentDetailList(List<ShipmentDetail> shipmentDetailList) {
		this.shipmentDetailList = shipmentDetailList;
	}

	public Integer getShipment_order_sequence_number() {
		return shipment_order_sequence_number;
	}

	public void setShipment_order_sequence_number(
			Integer shipment_order_sequence_number) {
		this.shipment_order_sequence_number = shipment_order_sequence_number;
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
        Shipment other = (Shipment) that;
        return (this.getShipment_id() == null ? other.getShipment_id() == null : this.getShipment_id().equals(other.getShipment_id()))
            && (this.getOrder_id() == null ? other.getOrder_id() == null : this.getOrder_id().equals(other.getOrder_id()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getTracking_number() == null ? other.getTracking_number() == null : this.getTracking_number().equals(other.getTracking_number()))
            && (this.getShipping_id() == null ? other.getShipping_id() == null : this.getShipping_id().equals(other.getShipping_id()))
            && (this.getShipping_wms_weight() == null ? other.getShipping_wms_weight() == null : this.getShipping_wms_weight().equals(other.getShipping_wms_weight()))
            && (this.getShipping_out_weight() == null ? other.getShipping_out_weight() == null : this.getShipping_out_weight().equals(other.getShipping_out_weight()))
            && (this.getShipping_fee() == null ? other.getShipping_fee() == null : this.getShipping_fee().equals(other.getShipping_fee()))
            && (this.getShipping_insured_fee() == null ? other.getShipping_insured_fee() == null : this.getShipping_insured_fee().equals(other.getShipping_insured_fee()))
            && (this.getEstimated_shipping_fee() == null ? other.getEstimated_shipping_fee() == null : this.getEstimated_shipping_fee().equals(other.getEstimated_shipping_fee()))
            && (this.getBill_note() == null ? other.getBill_note() == null : this.getBill_note().equals(other.getBill_note()))
            && (this.getPackbox_product_id() == null ? other.getPackbox_product_id() == null : this.getPackbox_product_id().equals(other.getPackbox_product_id()))
            && (this.getPackbox_is_out() == null ? other.getPackbox_is_out() == null : this.getPackbox_is_out().equals(other.getPackbox_is_out()))
            && (this.getPackbox_created_time() == null ? other.getPackbox_created_time() == null : this.getPackbox_created_time().equals(other.getPackbox_created_time()))
            && (this.getPackbox_out_time() == null ? other.getPackbox_out_time() == null : this.getPackbox_out_time().equals(other.getPackbox_out_time()))
            && (this.getMark() == null ? other.getMark() == null : this.getMark().equals(other.getMark()))
            && (this.getSf_destcode() == null ? other.getSf_destcode() == null : this.getSf_destcode().equals(other.getSf_destcode()))
            && (this.getSf_origincode() == null ? other.getSf_origincode() == null : this.getSf_origincode().equals(other.getSf_origincode()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()))
            && (this.getLast_updated_user() == null ? other.getLast_updated_user() == null : this.getLast_updated_user().equals(other.getLast_updated_user()))
            && (this.getLast_updated_time() == null ? other.getLast_updated_time() == null : this.getLast_updated_time().equals(other.getLast_updated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getShipment_id() == null) ? 0 : getShipment_id().hashCode());
        result = prime * result + ((getOrder_id() == null) ? 0 : getOrder_id().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getTracking_number() == null) ? 0 : getTracking_number().hashCode());
        result = prime * result + ((getShipping_id() == null) ? 0 : getShipping_id().hashCode());
        result = prime * result + ((getShipping_wms_weight() == null) ? 0 : getShipping_wms_weight().hashCode());
        result = prime * result + ((getShipping_out_weight() == null) ? 0 : getShipping_out_weight().hashCode());
        result = prime * result + ((getShipping_fee() == null) ? 0 : getShipping_fee().hashCode());
        result = prime * result + ((getShipping_insured_fee() == null) ? 0 : getShipping_insured_fee().hashCode());
        result = prime * result + ((getEstimated_shipping_fee() == null) ? 0 : getEstimated_shipping_fee().hashCode());
        result = prime * result + ((getBill_note() == null) ? 0 : getBill_note().hashCode());
        result = prime * result + ((getPackbox_product_id() == null) ? 0 : getPackbox_product_id().hashCode());
        result = prime * result + ((getPackbox_is_out() == null) ? 0 : getPackbox_is_out().hashCode());
        result = prime * result + ((getPackbox_created_time() == null) ? 0 : getPackbox_created_time().hashCode());
        result = prime * result + ((getPackbox_out_time() == null) ? 0 : getPackbox_out_time().hashCode());
        result = prime * result + ((getMark() == null) ? 0 : getMark().hashCode());
        result = prime * result + ((getSf_destcode() == null) ? 0 : getSf_destcode().hashCode());
        result = prime * result + ((getSf_origincode() == null) ? 0 : getSf_origincode().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        result = prime * result + ((getLast_updated_user() == null) ? 0 : getLast_updated_user().hashCode());
        result = prime * result + ((getLast_updated_time() == null) ? 0 : getLast_updated_time().hashCode());
        return result;
    }
}