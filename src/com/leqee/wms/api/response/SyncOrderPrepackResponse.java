package com.leqee.wms.api.response;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.leqee.wms.api.LeqeeResponse;

/**
 * 预打包任务推送
 * 
 * @author xhchen
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SyncOrderPrepackResponse extends LeqeeResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5807441018865632395L;
	private String oms_task_sn;
	private String wms_order_sn;

	public String getOms_task_sn() {
		return oms_task_sn;
	}

	public void setOms_task_sn(String oms_task_sn) {
		this.oms_task_sn = oms_task_sn;
	}

	public String getWms_order_sn() {
		return wms_order_sn;
	}

	public void setWms_order_sn(String wms_order_sn) {
		this.wms_order_sn = wms_order_sn;
	}

}
