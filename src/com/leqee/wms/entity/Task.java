package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.Date;

public class Task  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int ORDERREPLENISHMENTTASKLEVEL=3;
	
	// by hzhang1 任务类型静态变量添加
	public static final String TASK_TYPE_REPLENISHMENT = "REPLENISHMENT";  // 补货
	public static final String TASK_TYPE_PICK = "PICK"; // 拣货
	public static final String TASK_TYPE_PUT_AWAY = "PUT_AWAY"; // 上架
	public static final String TASK_TYPE_MOVE = "MOVE"; // 移库
	public static final String TASK_TYPE_COUNT = "COUNT"; // 盘点
	public static final String TASK_TYPE_VIRTUAL_PUT_AWAY = "VIRTUAL_PUT_AWAY"; // 上架
	
	public static final String TASK_TYPE_PREPACK_PUT_AWAY = "PREPACK_PUT_AWAY"; // 上架
	
	
	// 任务状态
	public static final String TASK_STATUS_INIT = "INIT"; // 未处理
	public static final String TASK_STATUS_BINDED = "BINDED"; // 已分配
	public static final String TASK_STATUS_IN_PROCESS = "IN_PROCESS"; // 执行中
	public static final String TASK_STATUS_UNSHELVE = "UNSHELVE"; // 执行中(已下架)
	public static final String TASK_STATUS_EXCEPTION = "EXCEPTION"; // 已取消
	public static final String TASK_STATUS_CANCEL = "CANCEL"; // 已取消
	public static final String TASK_STATUS_FULFILLED = "FULFILLED"; // 已完成
	
	// WEB / RF
	public static final String TASK_OPERATE_PLATFORM_WEB = "WEB"; 
	public static final String TASK_OPERATE_PLATFORM_RF = "RF";
	public static final int PIECE_REPLENISHMENT = 0;  //件拣货和其他任务
	public static final int BOX_REPLENISHMENT = 1; //箱拣货任务
	
	
	private Integer task_id;
	
	private Integer physical_warehouse_id;
	
	private Integer customer_id;
	
	private Integer task_level=1;
	
	
	private Integer from_pl_id=0;
	
	private Integer to_location_id=0;
	
	private Integer to_pl_id=0;
	
	private Integer product_id;
	
	private Integer quantity=0;
	
	private Integer first_bind_user_id;
	
	private Integer second_bind_user_id;
	
	private Integer cancel_reason=0;
	
	private Integer batch_task_id=0;
	
	private String task_type;
	
	private String task_status;
	
	private String first_bind_time;
	
	private String second_bind_time;
	
	private String operate_platform="WEB";
	
	private String created_user;
	
	private String created_time;
	
	private String last_updated_user;
	
	private String last_updated_time;
	
	private Integer batch_pick_id=0;
	
	private Integer order_id=0;
	
	private Integer mark=0;	

	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}

	public Integer getTask_id() {
		return task_id;
	}

	public void setTask_id(Integer task_id) {
		this.task_id = task_id;
	}

	public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}

	public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	public Integer getTask_level() {
		return task_level;
	}

	public void setTask_level(Integer task_level) {
		this.task_level = task_level;
	}

	public Integer getFrom_pl_id() {
		return from_pl_id;
	}

	public void setFrom_pl_id(Integer from_pl_id) {
		this.from_pl_id = from_pl_id;
	}

	public Integer getTo_location_id() {
		return to_location_id;
	}

	public void setTo_location_id(Integer to_location_id) {
		this.to_location_id = to_location_id;
	}

	public Integer getTo_pl_id() {
		return to_pl_id;
	}

	public void setTo_pl_id(Integer to_pl_id) {
		this.to_pl_id = to_pl_id;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getFirst_bind_user_id() {
		return first_bind_user_id;
	}

	public void setFirst_bind_user_id(Integer first_bind_user_id) {
		this.first_bind_user_id = first_bind_user_id;
	}

	public Integer getSecond_bind_user_id() {
		return second_bind_user_id;
	}

	public void setSecond_bind_user_id(Integer second_bind_user_id) {
		this.second_bind_user_id = second_bind_user_id;
	}

	public Integer getCancel_reason() {
		return cancel_reason;
	}

	public void setCancel_reason(Integer cancel_reason) {
		this.cancel_reason = cancel_reason;
	}

	public Integer getBatch_task_id() {
		return batch_task_id;
	}

	public void setBatch_task_id(Integer batch_task_id) {
		this.batch_task_id = batch_task_id;
	}

	public String getTask_type() {
		return task_type;
	}

	public void setTask_type(String task_type) {
		this.task_type = task_type;
	}

	public String getTask_status() {
		return task_status;
	}

	public void setTask_status(String task_status) {
		this.task_status = task_status;
	}

	public String getFirst_bind_time() {
		return first_bind_time;
	}

	public void setFirst_bind_time(String first_bind_time) {
		this.first_bind_time = first_bind_time;
	}

	public String getSecond_bind_time() {
		return second_bind_time;
	}

	public void setSecond_bind_time(String second_bind_time) {
		this.second_bind_time = second_bind_time;
	}

	public String getOperate_platform() {
		return operate_platform;
	}

	public void setOperate_platform(String operate_platform) {
		this.operate_platform = operate_platform;
	}

	public String getCreated_user() {
		return created_user;
	}

	public void setCreated_user(String created_user) {
		this.created_user = created_user;
	}

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public String getLast_updated_user() {
		return last_updated_user;
	}

	public void setLast_updated_user(String last_updated_user) {
		this.last_updated_user = last_updated_user;
	}

	public String getLast_updated_time() {
		return last_updated_time;
	}

	public void setLast_updated_time(String last_updated_time) {
		this.last_updated_time = last_updated_time;
	}

	public Integer getBatch_pick_id() {
		return batch_pick_id;
	}

	public void setBatch_pick_id(Integer batch_pick_id) {
		this.batch_pick_id = batch_pick_id;
	}

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

	
}
