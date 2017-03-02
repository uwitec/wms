package com.leqee.wms.dao;

import com.leqee.wms.entity.Hardware;

public interface HardwareDao {

	int getCountHardwareCode(String hardwareCode);

	int getCountHardwareCodeAndPWarehouse(Hardware hw);

	void insert(Hardware hw);

}
