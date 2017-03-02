package com.leqee.wms.biz;

public interface HardwareBiz {
    
	//检测手持设备绑定的仓库号
	boolean checkHardwareCode(String hardwareCode, int physical_warehouse_id);

}
