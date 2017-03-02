package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class OmsOrderTransferCode implements Serializable {
	
    private Integer transfer_code_id;
	
	private Integer order_id;

    private String sku_code;

    private Integer product_id;

    private String transfer_code;

    private String created_user;

    private Date created_time;

    
    private static final long serialVersionUID = 1L;
    
    public Integer getTransfer_code_id() {
		return transfer_code_id;
	}

	public void setTransfer_code_id(Integer transfer_code_id) {
		this.transfer_code_id = transfer_code_id;
	}

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public String getTransfer_code() {
		return transfer_code;
	}

	public void setTransfer_code(String transfer_code) {
		this.transfer_code = transfer_code;
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
        OmsOrderTransferCode other = (OmsOrderTransferCode) that;
        return (this.getTransfer_code_id() == null ? other.getTransfer_code_id() == null : this.getTransfer_code_id().equals(other.getTransfer_code_id()))
            && (this.getOrder_id() == null ? other.getOrder_id() == null : this.getOrder_id().equals(other.getOrder_id()))
            && (this.getTransfer_code() == null ? other.getTransfer_code() == null : this.getTransfer_code().equals(other.getTransfer_code()))
            && (this.getSku_code() == null ? other.getSku_code() == null : this.getSku_code().equals(other.getSku_code()))
            && (this.getProduct_id() == null ? other.getProduct_id() == null : this.getProduct_id().equals(other.getProduct_id()))
            && (this.getCreated_user() == null ? other.getCreated_user() == null : this.getCreated_user().equals(other.getCreated_user()))
            && (this.getCreated_time() == null ? other.getCreated_time() == null : this.getCreated_time().equals(other.getCreated_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTransfer_code_id() == null) ? 0 : getTransfer_code_id().hashCode());
        result = prime * result + ((getOrder_id() == null) ? 0 : getOrder_id().hashCode());
        result = prime * result + ((getTransfer_code() == null) ? 0 : getTransfer_code().hashCode());
        result = prime * result + ((getSku_code() == null) ? 0 : getSku_code().hashCode());
        result = prime * result + ((getProduct_id() == null) ? 0 : getProduct_id().hashCode());
        result = prime * result + ((getCreated_user() == null) ? 0 : getCreated_user().hashCode());
        result = prime * result + ((getCreated_time() == null) ? 0 : getCreated_time().hashCode());
        return result;
    }
}