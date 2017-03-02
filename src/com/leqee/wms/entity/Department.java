/**
 * 
 */
package com.leqee.wms.entity;

import java.io.Serializable;

/**
 * @author sszheng
 *
 * Created on 2017年1月12日 上午11:36:42
 */
public class Department implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer department_id;
	
	private String department_name;

	/**
	 * @return the department_id
	 */
	public Integer getDepartment_id() {
		return department_id;
	}

	/**
	 * @param department_id the department_id to set
	 */
	public void setDepartment_id(Integer department_id) {
		this.department_id = department_id;
	}

	/**
	 * @return the department_name
	 */
	public String getDepartment_name() {
		return department_name;
	}

	/**
	 * @param department_name the department_name to set
	 */
	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((department_id == null) ? 0 : department_id.hashCode());
		result = prime * result + ((department_name == null) ? 0 : department_name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Department other = (Department) obj;
		if (department_id == null) {
			if (other.department_id != null)
				return false;
		} else if (!department_id.equals(other.department_id))
			return false;
		if (department_name == null) {
			if (other.department_name != null)
				return false;
		} else if (!department_name.equals(other.department_name))
			return false;
		return true;
	}
	
}
