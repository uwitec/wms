package com.leqee.wms.biz.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.WarehouseCustomerBiz;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.util.WorkerUtil;

@Service
public class WarehouseCustomerBizImpl implements WarehouseCustomerBiz {
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	
	@Override
	public WarehouseCustomer findByCustomerId(Integer customerId) {
		return warehouseCustomerDao.selectByCustomerId(customerId);
	}

	@Override
	public List<WarehouseCustomer> findByCustomerIds(Set<Integer> customerIds) {
		List<WarehouseCustomer> warehouseCustomers = new ArrayList<WarehouseCustomer>();
		if(!WorkerUtil.isNullOrEmpty(customerIds)){
			for (Integer customerId : customerIds) {
				WarehouseCustomer warehouseCustomer = findByCustomerId(customerId);
				if(!WorkerUtil.isNullOrEmpty(warehouseCustomer)){
					warehouseCustomers.add(warehouseCustomer);
				}
			}
		}
		return warehouseCustomers;
	}

	@Override
	public List<WarehouseCustomer> findAll() {
		return warehouseCustomerDao.selectAll();
	}

	/* (non-Javadoc)
	 * @see com.leqee.wms.biz.WarehouseCustomerBiz#getWarehouseCustomerListByUser()
	 */
	@Override
	public List<Map<String, String>> getWarehouseCustomerListByUser(Integer id) {
		return warehouseCustomerDao.getWarehouseCustomerListByUser(id);
	}

}
