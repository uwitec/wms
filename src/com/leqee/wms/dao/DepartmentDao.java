/**
 * 
 */
package com.leqee.wms.dao;

import java.util.List;

import com.leqee.wms.entity.Department;

/**
 * @author sszheng
 *
 * Created on 2017年1月12日 上午11:38:57
 */
public interface DepartmentDao {
	List<Department> selectAll();
}
