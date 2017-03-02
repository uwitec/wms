package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class UserActionOrder implements Serializable {
    private Long action_id;

    private Integer order_id;

    private Integer order_goods_id;

    private String order_status;

    private String action_type;

    private String action_note;

    private String created_user;

    private Date created_time;

    private static final long serialVersionUID = 1L;

    public Long getAction_id() {
        return action_id;
    }

    public void setAction_id(Long action_id) {
        this.action_id = action_id;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getOrder_goods_id() {
        return order_goods_id;
    }

    public void setOrder_goods_id(Integer order_goods_id) {
        this.order_goods_id = order_goods_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status == null ? null : order_status.trim();
    }

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type == null ? null : action_type.trim();
    }

    public String getAction_note() {
        return action_note;
    }

    public void setAction_note(String action_note) {
        this.action_note = action_note == null ? null : action_note.trim();
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
        UserActionOrder other = (UserActionOrder) that;
        return (this.getAction_id() == null ? other.getAction_id() == null : this.getAction_id().equals(other.getAction_id()))
            && (this.getOrder_id() == null ? other.getOrder_id() == null : this.getOrder_id().equals(other.getOrder_id()))
            && (this.getOrder_goods_id() == null ? other.getOrder_goods_id() == null : this.getOrder_goods_id().equals(other.getOrder_goods_id()))
            && (this.getOrder_status() == null ? other.getOrder_status() == null : this.getOrder_status().equals(other.getOrder_status()))
            && (this.getAction_type() == null ? other.getAction_type() == null : this.getAction_type().equals(other.getAction_type()))
            && (this.getAction_note() == null ? other.getAction_note() == null : this.getAction_note().equals(other.getAction_note()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAction_id() == null) ? 0 : getAction_id().hashCode());
        result = prime * result + ((getOrder_id() == null) ? 0 : getOrder_id().hashCode());
        result = prime * result + ((getOrder_goods_id() == null) ? 0 : getOrder_goods_id().hashCode());
        result = prime * result + ((getOrder_status() == null) ? 0 : getOrder_status().hashCode());
        result = prime * result + ((getAction_type() == null) ? 0 : getAction_type().hashCode());
        result = prime * result + ((getAction_note() == null) ? 0 : getAction_note().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}