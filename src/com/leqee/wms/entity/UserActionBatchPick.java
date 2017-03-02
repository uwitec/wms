package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class UserActionBatchPick implements Serializable {
    private Long action_id;

    private Integer batch_pick_id;

    private String status;

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

    public Integer getBatch_pick_id() {
        return batch_pick_id;
    }

    public void setBatch_pick_id(Integer batch_pick_id) {
        this.batch_pick_id = batch_pick_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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
        UserActionBatchPick other = (UserActionBatchPick) that;
        return (this.getAction_id() == null ? other.getAction_id() == null : this.getAction_id().equals(other.getAction_id()))
            && (this.getBatch_pick_id() == null ? other.getBatch_pick_id() == null : this.getBatch_pick_id().equals(other.getBatch_pick_id()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
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
        result = prime * result + ((getBatch_pick_id() == null) ? 0 : getBatch_pick_id().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getAction_type() == null) ? 0 : getAction_type().hashCode());
        result = prime * result + ((getAction_note() == null) ? 0 : getAction_note().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}