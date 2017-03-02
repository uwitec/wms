/**
 * 
 */
package com.leqee.wms.biz;

import java.util.List;

import com.leqee.wms.entity.Department;

/**
 * @author sszheng
 *
 * Created on 2017年1月12日 上午11:44:48
 */
public interface DepartmentBiz {
	List<Department> selectAll();
}
