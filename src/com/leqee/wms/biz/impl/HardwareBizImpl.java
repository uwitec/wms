package com.leqee.wms.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.HardwareBiz;
import com.leqee.wms.dao.HardwareDao;
import com.leqee.wms.entity.Hardware;
@Service
public class HardwareBizImpl implements HardwareBiz {

	
	@Autowired
	HardwareDao hardwareDao;
	
	@Override
	public boolean checkHardwareCode(String hardwareCode,
			int physical_warehouse_id) {
		
		Hardware hw=new Hardware();
		hw.setHardwarecode(hardwareCode);
		hw.setPhysical_warehouse_id(physical_warehouse_id);
		
		//在手持设备的表中查出对应标识的个数；如果为0则未使用过，进行绑定 ；如果为1则需要进行校验
		int count = hardwareDao.getCountHardwareCode(hardwareCode);
		
		if(count==1)
		{
			int count2=hardwareDao.getCountHardwareCodeAndPWarehouse(hw);
			if(count2==1)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(count==0)
		{
			hardwareDao.insert(hw);
			return true;
		}
		
		return false;
	}

}
