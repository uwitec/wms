package com.leqee.wms.shiro;

import java.io.Serializable;

import org.apache.shiro.authc.UsernamePasswordToken;

public class MyUsernamePasswordToken extends UsernamePasswordToken implements Serializable{

	private String warehouseid="";

	public String getWarehouseid() {
		return warehouseid;
	}

	public void setWarehouseid(String warehouseid) {
		this.warehouseid = warehouseid;
	}
	
	public MyUsernamePasswordToken(String username,String password,String warehouseid)
	{   
		super(username,password);
		this.warehouseid=warehouseid;
		
	}
}
