package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class PalletShipmentMapping {
	
	private  int shipment_id;
	
	private String pallet_no;
	
    private String bind_status;

    private String bind_user;

    private Date bind_time;

    private String unbind_user;

    private Date unbind_time;

    private static final long serialVersionUID = 1L;

  

    public PalletShipmentMapping() {
        super();
    }

    public String getBind_status() {
        return bind_status;
    }

    public void setBind_status(String bind_status) {
        this.bind_status = bind_status == null ? null : bind_status.trim();
    }

    public String getBind_user() {
        return bind_user;
    }

    public void setBind_user(String bind_user) {
        this.bind_user = bind_user == null ? null : bind_user.trim();
    }

    public Date getBind_time() {
        return bind_time;
    }

    public void setBind_time(Date bind_time) {
        this.bind_time = bind_time;
    }

    public String getUnbind_user() {
        return unbind_user;
    }

    public void setUnbind_user(String unbind_user) {
        this.unbind_user = unbind_user == null ? null : unbind_user.trim();
    }

    public Date getUnbind_time() {
        return unbind_time;
    }

    public void setUnbind_time(Date unbind_time) {
        this.unbind_time = unbind_time;
    }

	public int getShipment_id() {
		return shipment_id;
	}

	public void setShipment_id(int shipment_id) {
		this.shipment_id = shipment_id;
	}

	public String getPallet_no() {
		return pallet_no;
	}

	public void setPallet_no(String pallet_no) {
		this.pallet_no = pallet_no;
	}

	@Override
	public String toString() {
		return "PalletShipmentMapping [shipment_id=" + shipment_id
				+ ", pallet_no=" + pallet_no + ", bind_status=" + bind_status
				+ ", bind_user=" + bind_user + ", bind_time=" + bind_time
				+ ", unbind_user=" + unbind_user + ", unbind_time="
				+ unbind_time + "]";
	}

   
}