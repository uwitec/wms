package com.leqee.wms.entity;

import java.io.Serializable;

public class ShipmentDetail implements Serializable {
    private Integer shipment_detail_id;

    private Integer shipment_id;

    private Integer order_goods_id;

    private Integer product_id;

    private String goods_name;

    private Integer goods_number;

    private String serial_number;

    private static final long serialVersionUID = 1L;

    public Integer getShipment_detail_id() {
        return shipment_detail_id;
    }

    public void setShipment_detail_id(Integer shipment_detail_id) {
        this.shipment_detail_id = shipment_detail_id;
    }

    public Integer getShipment_id() {
        return shipment_id;
    }

    public void setShipment_id(Integer shipment_id) {
        this.shipment_id = shipment_id;
    }

    public Integer getOrder_goods_id() {
        return order_goods_id;
    }

    public void setOrder_goods_id(Integer order_goods_id) {
        this.order_goods_id = order_goods_id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name == null ? null : goods_name.trim();
    }

    public Integer getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(Integer goods_number) {
        this.goods_number = goods_number;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number == null ? null : serial_number.trim();
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
        ShipmentDetail other = (ShipmentDetail) that;
        return (this.getShipment_detail_id() == null ? other.getShipment_detail_id() == null : this.getShipment_detail_id().equals(other.getShipment_detail_id()))
            && (this.getShipment_id() == null ? other.getShipment_id() == null : this.getShipment_id().equals(other.getShipment_id()))
            && (this.getOrder_goods_id() == null ? other.getOrder_goods_id() == null : this.getOrder_goods_id().equals(other.getOrder_goods_id()))
            && (this.getProduct_id() == null ? other.getProduct_id() == null : this.getProduct_id().equals(other.getProduct_id()))
            && (this.getGoods_name() == null ? other.getGoods_name() == null : this.getGoods_name().equals(other.getGoods_name()))
            && (this.getGoods_number() == null ? other.getGoods_number() == null : this.getGoods_number().equals(other.getGoods_number()))
            && (this.getSerial_number() == null ? other.getSerial_number() == null : this.getSerial_number().equals(other.getSerial_number()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getShipment_detail_id() == null) ? 0 : getShipment_detail_id().hashCode());
        result = prime * result + ((getShipment_id() == null) ? 0 : getShipment_id().hashCode());
        result = prime * result + ((getOrder_goods_id() == null) ? 0 : getOrder_goods_id().hashCode());
        result = prime * result + ((getProduct_id() == null) ? 0 : getProduct_id().hashCode());
        result = prime * result + ((getGoods_name() == null) ? 0 : getGoods_name().hashCode());
        result = prime * result + ((getGoods_number() == null) ? 0 : getGoods_number().hashCode());
        result = prime * result + ((getSerial_number() == null) ? 0 : getSerial_number().hashCode());
        return result;
    }
}