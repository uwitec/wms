package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class OrderGoodsBatch implements Serializable {
    private Integer order_goods_batch_id;

    private Integer order_goods_id;

    private String batch_sn;

    private Integer num;

    private String created_user;

    private Date created_time;

    private static final long serialVersionUID = 1L;

    public Integer getOrder_goods_batch_id() {
        return order_goods_batch_id;
    }

    public void setOrder_goods_batch_id(Integer order_goods_batch_id) {
        this.order_goods_batch_id = order_goods_batch_id;
    }

    public Integer getOrder_goods_id() {
        return order_goods_id;
    }

    public void setOrder_goods_id(Integer order_goods_id) {
        this.order_goods_id = order_goods_id;
    }

    public String getBatch_sn() {
        return batch_sn;
    }

    public void setBatch_sn(String batch_sn) {
        this.batch_sn = batch_sn == null ? null : batch_sn.trim();
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
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
        OrderGoodsBatch other = (OrderGoodsBatch) that;
        return (this.getOrder_goods_batch_id() == null ? other.getOrder_goods_batch_id() == null : this.getOrder_goods_batch_id().equals(other.getOrder_goods_batch_id()))
            && (this.getOrder_goods_id() == null ? other.getOrder_goods_id() == null : this.getOrder_goods_id().equals(other.getOrder_goods_id()))
            && (this.getBatch_sn() == null ? other.getBatch_sn() == null : this.getBatch_sn().equals(other.getBatch_sn()))
            && (this.getNum() == null ? other.getNum() == null : this.getNum().equals(other.getNum()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOrder_goods_batch_id() == null) ? 0 : getOrder_goods_batch_id().hashCode());
        result = prime * result + ((getOrder_goods_id() == null) ? 0 : getOrder_goods_id().hashCode());
        result = prime * result + ((getBatch_sn() == null) ? 0 : getBatch_sn().hashCode());
        result = prime * result + ((getNum() == null) ? 0 : getNum().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}