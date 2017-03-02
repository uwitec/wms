package com.leqee.wms.api.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.leqee.wms.api.ApiRuleException;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.api.request.domain.PrepackReqDomain;
import com.leqee.wms.api.response.SyncOrderPrepackResponse;

/**
 * 预打包订单
 * 
 * @author xhchen
 *
 */
public class SyncOrderPrepackRequest extends BaseLeqeeRequest<SyncOrderPrepackResponse> {

	private Integer job_id;// int(11) NOT NULL AUTO_INCREMENT,
	private String job_type; //状态
	private Integer party_id;// int(11) NOT NULL,
	private String tc_code;// varchar(128) NOT NULL COMMENT '套餐编码',
	private String tc_barcode;//预打包任务barCode
	private String tc_name;// varchar(128) NOT NULL COMMENT '套餐名称',
	private String packbox_barcode;// varchar(128) DEFAULT NULL COMMENT '耗材条码',
	private String packbox_sku_code;  //耗材sku_code
	private String pack_type;//varchar(16) DEFAULT NULL COMMENT 'CLOSED / OPEN',
	private Date start_time;// datetime DEFAULT NULL,
	private Date end_time;// datetime DEFAULT NULL,
	private Integer quantity;// int(11) NOT NULL COMMENT '任务个数',
	private Integer finished_quantity;// int(11) DEFAULT NULL COMMENT '完成个数',
	private Integer available_quantity;// int(11) DEFAULT NULL COMMENT '用剩个数后面有事还会改',
	private String note;// varchar(128) DEFAULT NULL,
	private String ignore_qty_user;//是否忽略打包商品已使用量
	private Integer physical_facility_id;//逻辑仓编号
	private Integer warehouse_id;  //添加逻辑仓编号

	// 订单商品列表
	private List<PrepackReqDomain> prepackReqDomain = new ArrayList<PrepackReqDomain>();

	public Integer getJob_id() {
		return job_id;
	}

	public void setJob_id(Integer job_id) {
		this.job_id = job_id;
	}

	public Integer getParty_id() {
		return party_id;
	}

	public void setParty_id(Integer party_id) {
		this.party_id = party_id;
	}

	public String getTc_code() {
		return tc_code;
	}

	public void setTc_code(String tc_code) {
		this.tc_code = tc_code;
	}

	public String getTc_barcode() {
		return tc_barcode;
	}

	public void setTc_barcode(String tc_barcode) {
		this.tc_barcode = tc_barcode;
	}

	public String getTc_name() {
		return tc_name;
	}

	public void setTc_name(String tc_name) {
		this.tc_name = tc_name;
	}

	public String getPackbox_barcode() {
		return packbox_barcode;
	}

	public void setPackbox_barcode(String packbox_barcode) {
		this.packbox_barcode = packbox_barcode;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getFinished_quantity() {
		return finished_quantity;
	}

	public void setFinished_quantity(Integer finished_quantity) {
		this.finished_quantity = finished_quantity;
	}

	public Integer getAvailable_quantity() {
		return available_quantity;
	}

	public void setAvailable_quantity(Integer available_quantity) {
		this.available_quantity = available_quantity;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	public List<PrepackReqDomain> getPrepackReqDomain() {
		return prepackReqDomain;
	}

	public void setPrepackReqDomain(List<PrepackReqDomain> prepackReqDomain) {
		this.prepackReqDomain = prepackReqDomain;
	}

	public String getIgnore_qty_user() {
		return ignore_qty_user;
	}

	public void setIgnore_qty_user(String ignore_qty_user) {
		this.ignore_qty_user = ignore_qty_user;
	}

	public Integer getPhysical_facility_id() {
		return physical_facility_id;
	}

	public void setPhysical_facility_id(Integer physical_facility_id) {
		this.physical_facility_id = physical_facility_id;
	}
	public String getJob_type() {
		return job_type;
	}

	public void setJob_type(String job_type) {
		this.job_type = job_type;
	}
	
	

	public String getPackbox_sku_code() {
		return packbox_sku_code;
	}

	public void setPackbox_sku_code(String packbox_sku_code) {
		this.packbox_sku_code = packbox_sku_code;
	}

	public String getPack_type() {
		return pack_type;
	}

	public void setPack_type(String pack_type) {
		this.pack_type = pack_type;
	}

	public Integer getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    @Override
	public String getApiMethodName() {
		return METHOD_SYNC_ORDERPREPACK_REQUEST;
	}

	@Override
	public Map<String, String> getTextParams() {
		return null;
	}

	@Override
	public Class<SyncOrderPrepackResponse> getResponseClass() {
		return SyncOrderPrepackResponse.class;
	}

	@Override
	public void check() throws ApiRuleException {

	}

}
