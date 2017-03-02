/**
 * 
 */
package com.leqee.wms.biz.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.DepartmentBiz;
import com.leqee.wms.dao.DepartmentDao;
import com.leqee.wms.entity.Department;

/**
 * @author sszheng
 *
 * Created on 2017年1月12日 上午11:45:56
 */
@Service
public class DepartmentBizImpl implements DepartmentBiz {

	@Autowired
	private DepartmentDao departmentDao;
	
	/* (non-Javadoc)
	 * @see com.leqee.wms.biz.DepartmentBiz#selectAll()
	 */
	@Override
	public List<Department> selectAll() {
		return departmentDao.selectAll();
	}

}
