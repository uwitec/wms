package com.leqee.wms.api.response.domain;

import java.io.Serializable;
import java.util.Date;
/**
 * 盘点任务明细响应实体
 * @author qyyao
 * @date 2016-3-7
 * @version 1.0
 */
public class VarianceImproveTaskResDomain implements Serializable{

	private static final long serialVersionUID = -5266352634669017051L;
	
	private Integer task_improve_id;   //表示一个盘点任务ID，唯一，去重用
	private String task_improve_sn;   //盘点任务编码，多个task_improve_id为一组
	private Integer physical_warehouse_id;
	private Integer warehouse_id;  //仓库ID  
	private Integer customer_id;   //货主ID
	private String sku_code;
	private String status;
	private String status_id;
	private Integer num;
	private Date modified_time;
	
	
	public Integer getTask_improve_id() {
		return task_improve_id;
	}
	public void setTask_improve_id(Integer task_improve_id) {
		this.task_improve_id = task_improve_id;
	}
	public String getTask_improve_sn() {
		return task_improve_sn;
	}
	public void setTask_improve_sn(String task_improve_sn) {
		this.task_improve_sn = task_improve_sn;
	}
	public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}
	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}
	public String getSku_code() {
		return sku_code;
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Integer getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}
	public Date getModified_time() {
		return modified_time;
	}
	public void setModified_time(Date modified_time) {
		this.modified_time = modified_time;
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
