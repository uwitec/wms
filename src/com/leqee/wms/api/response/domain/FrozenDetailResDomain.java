package com.leqee.wms.api.response.domain;


/**
 * 冻结Detail响应实体
 * @author qyyao
 * @date 2016-8-24
 * @version 1.0
 */
public class FrozenDetailResDomain {
	private int customer_id;
	private String sku_code;   //oms的商家编码
	private Integer warehouse_id ;  //实际仓库ID
	private Integer physical_warehouse_id ;  //物理仓库ID
	private Integer quantity ;   //冻结数量
	private String status_id;   //新旧状态
	
	
	
	public int getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(int customer_id) {
		this.customer_id = customer_id;
	}
	public String getSku_code() {
		return sku_code;
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	
	public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}
	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getStatus_id() {
		return status_id;
	}
	public void setStatus_id(String status_id) {
		this.status_id = status_id;
	}
    public Integer getWarehouse_id() {
        return warehouse_id;
    }
    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }
	
	
	
	
	
	
	
}
