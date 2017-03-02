package com.leqee.wms.vo;

import java.util.ArrayList;
import java.util.List;

import com.leqee.wms.util.WorkerUtil;

public class ReturnAcceptVO {
	
	private Integer orderId;   // 退货订单ID
	
	private List<ReturnProductAccDetail> returnProductAccDetails;   // 退货商品列表

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public List<ReturnProductAccDetail> getReturnProductAccDetails() {
		return returnProductAccDetails;
	}

	public void setReturnProductAccDetails(
			List<ReturnProductAccDetail> returnProductAccDetails) {
		this.returnProductAccDetails = returnProductAccDetails;
	}

	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.19
	 * 
	 * 对退货商品列表进行合并
	 * 
	 * */
	public List<ReturnProductAccDetail> mergeReturnProductAccDetails() {
		List<ReturnProductAccDetail> mergedReturnProductAccDetails = new ArrayList<ReturnProductAccDetail>();
		if(!this.returnProductAccDetails.isEmpty()){
			for (ReturnProductAccDetail returnProductAccDetail : this.returnProductAccDetails) {
				if(WorkerUtil.isNullOrEmpty(returnProductAccDetail.getIsSerial())) 
					continue; // 空数据过滤不作处理
				
				if("Y".equals(returnProductAccDetail.getIsSerial())) {
					mergedReturnProductAccDetails.add(returnProductAccDetail);
				}else{
					if(!mergedReturnProductAccDetails.isEmpty()){
						boolean isMerged = false;
						for (ReturnProductAccDetail mergedReturnGood : mergedReturnProductAccDetails) {
							if (mergedReturnGood.getProductId() == returnProductAccDetail.getProductId()
									&& mergedReturnGood.getStatus().equals(returnProductAccDetail.getStatus())
									&& mergedReturnGood.getValidity().equals(returnProductAccDetail.getValidity())
									&& mergedReturnGood.getBatch_sn().equals(returnProductAccDetail.getBatch_sn())
									&& mergedReturnGood.getOrderGoodsId() == returnProductAccDetail.getOrderGoodsId()){
								mergedReturnGood.setNum(mergedReturnGood.getNum()+returnProductAccDetail.getNum());
								isMerged = true;
								break;
							}
						}
						
						if(!isMerged){
							mergedReturnProductAccDetails.add(returnProductAccDetail);
						}
						
					}else{
						mergedReturnProductAccDetails.add(returnProductAccDetail);
					}
				}
				
			}
		}
		
		return mergedReturnProductAccDetails;
	}
	
}
