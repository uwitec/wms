package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OrderPrepack implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer order_id; //
	private String order_sn; //  加工任务单编号（j+当天时间+6位随机编码，eg:j160820000001） 
    private String oms_task_sn; //  oms任务单编号 
    private Integer physical_warehouse_id; //  物理仓库id，关联warehouse表 
    private Integer customer_id; //  货主id关联warehouse_customer表 
    private String type; //  任务类型:pack - 加工（预打包） unpack - 拆解 
    private String status; //  任务状态: init - 未处理reserved – 已分配 reserve_failed – 分配失败in_process – 处理中cancel - 已取消part_fulfilled - 部分完成fulfilled – 已完成 
    private Integer prepackage_product_id; //  预打包商品product_id，关联product表 
    private Integer packbox_product_id;   //耗材商品ID
    private Integer qty_need; //  需求数量，from oms 
    private Integer qty_actual; //  实绩操作数量，回传给oms 
    private Integer qty_used; //  已使用（预订）数量，供同步销售订单时使用 
    private String ignore_qty_used; //  是否忽略预打包商品已使用量，默认不忽略（部分业务组需要忽略此值） 
    private Date activity_start_time; //  活动开始时间 
    private Date activity_end_time; //  活动结束时间 
	private String note;// 留言
    private String created_user; //  创建者 
    private Date created_time; //  创建时间 
    private String last_updated_user; //  最后更新人 
    private Date last_updated_time; //  最后更新时间 
    private String pack_type;//varchar(16) DEFAULT NULL COMMENT 'CLOSED / OPEN',
    private Integer warehouse_id;  //逻辑仓库ID
    
    
    //辅助字段
    List<ProductPrepackage> productPrepackageList = new ArrayList<ProductPrepackage>();   //记录预打包商品和其他商品的关联列表 
    private String prepackageProductSkuCode; //预打包商品的sku_code 
    private Integer qtyNeedThisTime = 0; //  这次需要多少 
    private String skucode;//单品的sku_code
    
    
    
    //add by xhchen 2016-08-27
    public static final String  ORDER_PREPACK_STATUS_RESERVED="RESERVED";
    public static final String ORDER_PREPACK_STATUS_RESERVE_FAILED="RESERVE_FAILED";
    public static final String ORDER_PREPACK_STATUS_IN_PROCESS="IN_PROCESS";
    public static final String ORDER_PREPACK_STATUS_INIT="INIT";
    public static final String ORDER_PREPACK_TYPE="UNPACK";
    
    
    
    public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}
	public String getOms_task_sn() {
		return oms_task_sn;
	}
	public void setOms_task_sn(String oms_task_sn) {
		this.oms_task_sn = oms_task_sn;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getPrepackage_product_id() {
		return prepackage_product_id;
	}
	public void setPrepackage_product_id(Integer prepackage_product_id) {
		this.prepackage_product_id = prepackage_product_id;
	}
	public Integer getQty_need() {
		return qty_need;
	}
	public void setQty_need(Integer qty_need) {
		this.qty_need = qty_need;
	}
	public Integer getQty_actual() {
		return qty_actual;
	}
	public void setQty_actual(Integer qty_actual) {
		this.qty_actual = qty_actual;
	}
	public Integer getQty_used() {
		return qty_used;
	}
	public void setQty_used(Integer qty_used) {
		this.qty_used = qty_used;
	}
	public String getIgnore_qty_used() {
		return ignore_qty_used;
	}
	public void setIgnore_qty_used(String ignore_qty_used) {
		this.ignore_qty_used = ignore_qty_used;
	}
	public Date getActivity_start_time() {
		return activity_start_time;
	}
	public void setActivity_start_time(Date activity_start_time) {
		this.activity_start_time = activity_start_time;
	}
	public Date getActivity_end_time() {
		return activity_end_time;
	}
	public void setActivity_end_time(Date activity_end_time) {
		this.activity_end_time = activity_end_time;
	}
	public String getCreated_user() {
		return created_user;
	}
	public void setCreated_user(String created_user) {
		this.created_user = created_user;
	}
	public Date getCreated_time() {
		return created_time;
	}
	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}
	public String getLast_updated_user() {
		return last_updated_user;
	}
	public void setLast_updated_user(String last_updated_user) {
		this.last_updated_user = last_updated_user;
	}
	public Date getLast_updated_time() {
		return last_updated_time;
	}
	public void setLast_updated_time(Date last_updated_time) {
		this.last_updated_time = last_updated_time;
	}
	public List<ProductPrepackage> getProductPrepackageList() {
		return productPrepackageList;
	}
	public void setProductPrepackageList(
			List<ProductPrepackage> productPrepackageList) {
		this.productPrepackageList = productPrepackageList;
	}
	public String getPrepackageProductSkuCode() {
		return prepackageProductSkuCode;
	}
	public void setPrepackageProductSkuCode(String prepackageProductSkuCode) {
		this.prepackageProductSkuCode = prepackageProductSkuCode;
	}
	public Integer getQtyNeedThisTime() {
		return qtyNeedThisTime;
	}
	public void setQtyNeedThisTime(Integer qtyNeedThisTime) {
		this.qtyNeedThisTime = qtyNeedThisTime;
	}
	public String getPack_type() {
		return pack_type;
	}
	public void setPack_type(String pack_type) {
		this.pack_type = pack_type;
	}
	public String getSkucode() {
		return skucode;
	}
	public void setSkucode(String skucode) {
		this.skucode = skucode;
	}
	public Integer getPackbox_product_id() {
		return packbox_product_id;
	}
	public void setPackbox_product_id(Integer packbox_product_id) {
		this.packbox_product_id = packbox_product_id;
	}
	
	public Integer getWarehouse_id() {
        return warehouse_id;
    }
    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }
    public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	@Override
	public String toString() {
		return "OrderPrepack [order_id=" + order_id + ", order_sn=" + order_sn
				+ ", oms_task_sn=" + oms_task_sn + ", physical_warehouse_id="
				+ physical_warehouse_id + ", customer_id=" + customer_id
				+ ", type=" + type + ", status=" + status
				+ ", prepackage_product_id=" + prepackage_product_id
				+ ", qty_need=" + qty_need + ", qty_actual=" + qty_actual
				+ ", qty_used=" + qty_used + ", ignore_qty_used="
				+ ignore_qty_used + ", activity_start_time="
				+ activity_start_time + ", activity_end_time="
				+ activity_end_time + ", productPrepackageList="
				+ productPrepackageList + ", prepackageProductSkuCode="
				+ prepackageProductSkuCode + ", qtyNeedThisTime="
				+ qtyNeedThisTime + "]";
	}
	
	
	
	
	
	
	
	
}