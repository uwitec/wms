package com.leqee.wms.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Location implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String LOCATION_TYPE_PURCHASE_SEQ = "PURCHASE_SEQ"; //收货标签
	public static final String LOCATION_TYPE_REPLENISH_SEQ = "REPLENISH_SEQ"; //补货标签
	public static final String LOCATION_TYPE_STOCK = "STOCK_LOCATION"; //存储区
	public static final String LOCATION_TYPE_PICK = "PICK_LOCATION"; //零拣区
	public static final String LOCATION_TYPE_BOX_PICK = "BOX_PICK_LOCATION"; //箱拣货区
	public static final String LOCATION_TYPE_PIECE_PICK = "PIECE_PICK_LOCATION"; //件拣货区
	public static final String LOCATION_TYPE_TRANSIT = "TRANSIT_LOCATION"; //中转区
	public static final String LOCATION_TYPE_RECEIVE = "RECEIVE_LOCATION"; //收货区,暂不用
	public static final String LOCATION_TYPE_DELIVERY = "DELIVERY_LOCATION"; //发货区，暂不用
	public static final String LOCATION_TYPE_UNSHELVE = "UNSHELVE_LOCATION"; //下架暂存库位（虚拟)，暂不用
	public static final String LOCATION_TYPE_PACKBOX = "PACKBOX_LOCATION"; //耗材区
	public static final String LOCATION_TYPE_RETURN = "RETURN_LOCATION"; //退货库位
	public static final String LOCATION_TYPE_DEFECTIVE = "DEFECTIVE_LOCATION"; //二手区
	public static final String LOCATION_TYPE_RETURN_NORMAL = "RETURN_NORMAL_LOCATION"; //退货良品暂存库位
	public static final String LOCATION_TYPE_RETURN_DEFECTIVE = "RETURN_DEFECTIVE_LOCATION"; //退货不良品暂存库位
	public static final String LOCATION_TYPE_VARIANCE_ADD = "VARIANCE_ADD_LOCATION"; //盘盈区
	public static final String LOCATION_TYPE_VARIANCE_MIMUS = "VARIANCE_MINUS_LOCATION"; //盘亏区
	public static final String LOCATION_TYPE_VARIANCE_STOCK_ADD = "VARIANCE_ADD_STOCK_LOCATION"; //盘盈调整池
	public static final String LOCATION_TYPE_VARIANCE_STOCK_MIMUS = "VARIANCE_MINUS_STOCK_LOCATION"; //盘亏调整池
	public static final String LOCATION_TYPE_QUALITY_CHECK = "QUALITY_CHECK_LOCATION"; //质检库位
	public static final String LOCATION_TYPE_PACKBOX_PICK = "PACKBOX_PICK_LOCATION"; //耗材拣货区
	
	//public static final String LOCATION_VARIANCE_ADD_BARCODE = "Z010101"; //盘亏区
	//public static final String LOCATION_VARIANCE_MIMUS_BARCODE = "Z010102"; //盘亏区
	
	//公用订单状态映射MAP
    public static Map<String,Object> LOCATION_TYPE_MAP =null;
    static{
    	LOCATION_TYPE_MAP = new HashMap<String,Object>();
    	LOCATION_TYPE_MAP.put("PURCHASE_SEQ","收货标签");
    	LOCATION_TYPE_MAP.put("REPLENISH_SEQ","补货标签");
    	LOCATION_TYPE_MAP.put("STOCK_LOCATION","存储区");
    	LOCATION_TYPE_MAP.put("PICK_LOCATION","零拣区");
    	LOCATION_TYPE_MAP.put("BOX_PICK_LOCATION","箱拣货区");
    	LOCATION_TYPE_MAP.put("PIECE_PICK_LOCATION","件拣货区");
    	LOCATION_TYPE_MAP.put("TRANSIT_LOCATION","中转区");
    	LOCATION_TYPE_MAP.put("RECEIVE_LOCATION","收货区");
    	LOCATION_TYPE_MAP.put("DELIVERY_LOCATION","发货区");
    	LOCATION_TYPE_MAP.put("UNSHELVE_LOCATION","下架暂存库位");
    	LOCATION_TYPE_MAP.put("PACKBOX_LOCATION","耗材区");
    	LOCATION_TYPE_MAP.put("RETURN_LOCATION","退货库位");
    	LOCATION_TYPE_MAP.put("DEFECTIVE_LOCATION","二手区");
    	LOCATION_TYPE_MAP.put("RETURN_NORMAL_LOCATION","退货良品暂存库位");
    	LOCATION_TYPE_MAP.put("RETURN_DEFECTIVE_LOCATION","退货不良品暂存库位");
    	LOCATION_TYPE_MAP.put("VARIANCE_ADD_LOCATION","盘盈区");
    	LOCATION_TYPE_MAP.put("VARIANCE_MINUS_LOCATION","盘亏区");
    	LOCATION_TYPE_MAP.put("VARIANCE_ADD_STOCK_LOCATION","盘盈调整池");
    	LOCATION_TYPE_MAP.put("VARIANCE_MINUS_STOCK_LOCATION","盘亏调整池");
    	LOCATION_TYPE_MAP.put("QUALITY_CHECK_LOCATION","质检库位");
    	LOCATION_TYPE_MAP.put("PACKBOX_PICK_LOCATION","耗材拣货区");
    	
    }
	
	public static final String IS_DELETE_Y = "Y"; //库位标记是否被删除：是
	public static final String IS_DELETE_N = "N"; //库位标记是否被删除：否
	
	
	private Integer location_id;
	
	private Integer physical_warehouse_id;
	
	private String location_barcode;
	
	private String area; //区域
	
	private String partition_id; //库区
	
	private String aisle; //排
	
	private String bay; //列
	
	private String lev; //层
	
	private String location_type; //库位类型
	
	private String circle_class; //循环级别
	
	private String price_class; //价值级别
	
	private Integer can_mix_product=0; //允许混放商品
	
	private Integer can_mix_batch=0; //是否允许混放批次
	
	private Integer not_auto_recmd; //是否在计算库容包括
	
	private Integer max_lpn_qty; //可放托盘数量
	
	private BigDecimal volume; //体积
	
	private BigDecimal weight; //宽
	
	private BigDecimal length; //长
	
	private BigDecimal height; //高
	
	private BigDecimal axis_x; //x坐标
	
	private BigDecimal axis_y; //y坐标
	
	private BigDecimal axis_z; //z坐标
	
	private Integer max_prod_qty; //数量限制
	
	private String ignore_lpn; //忽略LPN
	
	private Integer putaway_seq; //上架顺序
	
	private Integer pick_seq; //拣货顺序
	
	private Integer customer_id; //货主
	
	private Date created_time;
	
	private String created_user;
	
	private Date last_updated_time;
	
	private String last_updated_user;
	
	private String is_delete; //是否删除
	
	private String is_empty; //库位状态，是否为空
	
	
	public Integer getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public Integer getLocation_id() {
		return location_id;
	}

	public void setLocation_id(Integer location_id) {
		this.location_id = location_id;
	}

	public String getLocation_barcode() {
		return location_barcode;
	}

	public void setLocation_barcode(String location_barcode) {
		this.location_barcode = location_barcode;
	}

	public String getLocation_type() {
		return location_type;
	}

	public void setLocation_type(String location_type) {
		this.location_type = location_type;
	}

	public void setPhysical_warehouse_id(Integer physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPartition_id() {
		return partition_id;
	}

	public void setPartition_id(String partition_id) {
		this.partition_id = partition_id;
	}

	public String getAisle() {
		return aisle;
	}

	public void setAisle(String aisle) {
		this.aisle = aisle;
	}

	public String getBay() {
		return bay;
	}

	public void setBay(String bay) {
		this.bay = bay;
	}

	public String getLev() {
		return lev;
	}

	public void setLev(String lev) {
		this.lev = lev;
	}


	public String getCircle_class() {
		return circle_class;
	}

	public void setCircle_class(String circle_class) {
		this.circle_class = circle_class;
	}

	public String getPrice_class() {
		return price_class;
	}

	public void setPrice_class(String price_class) {
		this.price_class = price_class;
	}

	public Integer getCan_mix_product() {
		return can_mix_product;
	}

	public void setCan_mix_product(Integer can_mix_product) {
		this.can_mix_product = can_mix_product;
	}

	public Integer getCan_mix_batch() {
		return can_mix_batch;
	}

	public void setCan_mix_batch(Integer can_mix_batch) {
		this.can_mix_batch = can_mix_batch;
	}

	public Integer getNot_auto_recmd() {
		return not_auto_recmd;
	}

	public void setNot_auto_recmd(Integer not_auto_recmd) {
		this.not_auto_recmd = not_auto_recmd;
	}

	public Integer getMax_lpn_qty() {
		return max_lpn_qty;
	}

	public void setMax_lpn_qty(Integer max_lpn_qty) {
		this.max_lpn_qty = max_lpn_qty;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public BigDecimal getAxis_x() {
		return axis_x;
	}

	public void setAxis_x(BigDecimal axis_x) {
		this.axis_x = axis_x;
	}

	public BigDecimal getAxis_y() {
		return axis_y;
	}

	public void setAxis_y(BigDecimal axis_y) {
		this.axis_y = axis_y;
	}

	public BigDecimal getAxis_z() {
		return axis_z;
	}

	public void setAxis_z(BigDecimal axis_z) {
		this.axis_z = axis_z;
	}

	public Integer getMax_prod_qty() {
		return max_prod_qty;
	}

	public void setMax_prod_qty(Integer max_prod_qty) {
		this.max_prod_qty = max_prod_qty;
	}

	public String getIgnore_lpn() {
		return ignore_lpn;
	}

	public void setIgnore_lpn(String ignore_lpn) {
		this.ignore_lpn = ignore_lpn;
	}

	public Integer getPutaway_seq() {
		return putaway_seq;
	}

	public void setPutaway_seq(Integer putaway_seq) {
		this.putaway_seq = putaway_seq;
	}

	public Integer getPick_seq() {
		return pick_seq;
	}

	public void setPick_seq(Integer pick_seq) {
		this.pick_seq = pick_seq;
	}

	public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	public Date getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}

	

	public Date getLast_updated_time() {
		return last_updated_time;
	}

	public void setLast_updated_time(Date last_updated_time) {
		this.last_updated_time = last_updated_time;
	}

	public String getLast_updated_user() {
		return last_updated_user;
	}

	public void setLast_updated_user(String last_updated_user) {
		this.last_updated_user = last_updated_user;
	}

	public String getCreated_user() {
		return created_user;
	}

	public void setCreated_user(String created_user) {
		this.created_user = created_user;
	}

	public String getIs_delete() {
		return is_delete;
	}

	public void setIs_delete(String is_delete) {
		this.is_delete = is_delete;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getIs_empty() {
		return is_empty;
	}

	public void setIs_empty(String is_empty) {
		this.is_empty = is_empty;
	}
	
}
