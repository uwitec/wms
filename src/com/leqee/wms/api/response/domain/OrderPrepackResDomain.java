package com.leqee.wms.api.response.domain;

/**
 * 
 * @author xhchen
 *
 */
public class OrderPrepackResDomain {

	private String oms_task_sn;

	private Integer qty_need;// ` int(11) unsigned NOT NULL DEFAULT '0' COMMENT
								// '需求数量，from OMS',
	private Integer qty_actual;// ` int(11) unsigned NOT NULL DEFAULT '0'
								// COMMENT '实绩操作数量，回传给OMS',
	private Integer qty_used;// ` int(11) unsigned NOT NULL DEFAULT '0' COMMENT
								// '已使用（预订）数量，供同步销售订单时使用',

	public String getOms_task_sn() {
		return oms_task_sn;
	}

	public void setOms_task_sn(String oms_task_sn) {
		this.oms_task_sn = oms_task_sn;
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

}
