package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.leqee.wms.util.WorkerUtil;

public class SysUser implements Serializable {

	private Integer id;
	
	private String username;       // 用户名
	
	private String password;       // 密码
	
	private String salt;           // 盐（MD5加密使用）
	
	private String realname;       // 真实姓名
	
	private String email;          // 邮箱
	
	private String role_ids;       // 角色（权限）列表
	
	private String resource_ids;   // 资源（权限）列表
	
	private String customer_ids;   // 货主（权限）列表
	
	private String warehouse_ids;  // 仓库（权限）列表
	
	private String department;     // 部门
	
	private Boolean available = Boolean.TRUE;     // 是否可用
	
	private Date created_time;     // 创建时间
	
	private Date last_login_time;  // 上次登录时间
	
	private String ip_type;  // 上次登录时间
	
	private Integer department_id;
	
	private List<Integer> roleIds = new ArrayList<Integer>(); //拥有的角色列表
	
	private List<Integer> customerIds = new ArrayList<Integer>();  // 拥有的货主列表
	
	private List<Integer> warehouseIds = new ArrayList<Integer>(); // 拥有的仓库列表
	
	private List<Integer> resourceIds = new ArrayList<Integer>();  // 拥有的资源列表
	
	private static final long serialVersionUID = 1L;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public String getCredentialsSalt() {
        return username + salt;
    }

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole_ids() {
		return role_ids;
	}

	public void setRole_ids(String role_ids) {
		this.role_ids = role_ids;
	}
	
	public Set<Integer> getRoleIdsSet() {
		Set<Integer> roleIdsSet = new HashSet<Integer>();
        if(!WorkerUtil.isNullOrEmpty(role_ids)) {
        	String[] roleIdsArr = role_ids.split(",");
        	if(roleIdsArr != null && roleIdsArr.length > 0) 
        		for(String roleIdStr : roleIdsArr) 
                    if(!WorkerUtil.isNullOrEmpty(roleIdStr)) 
                    	roleIdsSet.add(Integer.valueOf(roleIdStr));
        }
        return roleIdsSet;
    }

	public String getResource_ids() {
		return resource_ids;
	}

	public void setResource_ids(String resource_ids) {
		this.resource_ids = resource_ids;
	}
	
	public Set<Integer> getResourceIdsSet(){
		Set<Integer> resourceIdsSet = new HashSet<Integer>();
		if(!WorkerUtil.isNullOrEmpty(resource_ids)){
			String[] resourceIdsArr = resource_ids.split(",");
			if(resourceIdsArr != null && resourceIdsArr.length > 0)
				for (String resourceIdStr : resourceIdsArr) 
					if(!WorkerUtil.isNullOrEmpty(resourceIdStr))
						resourceIdsSet.add(Integer.valueOf(resourceIdStr));
		}
		return resourceIdsSet;
	}

	public String getCustomer_ids() {
		return customer_ids;
	}

	public void setCustomer_ids(String customer_ids) {
		this.customer_ids = customer_ids;
	}
	
	public Set<Integer> getCustomerIdsSet() {
		Set<Integer> customerIdsSet = new HashSet<Integer>();
		if(!WorkerUtil.isNullOrEmpty(customer_ids)){
			String[] customerIdsArr = customer_ids.split(",");
			if(customerIdsArr != null && customer_ids.length() > 0) 
				for (String customerIdStr : customerIdsArr) 
					if(!WorkerUtil.isNullOrEmpty(customerIdStr))
						customerIdsSet.add(Integer.valueOf(customerIdStr));
		}
		return customerIdsSet;
	}

	public String getWarehouse_ids() {
		return warehouse_ids;
	}

	public void setWarehouse_ids(String warehouse_ids) {
		this.warehouse_ids = warehouse_ids;
	}
	
	public Set<Integer> getWarehouseIdsSet() {
		Set<Integer> warehouseIdsSet = new HashSet<Integer>();
		if(!WorkerUtil.isNullOrEmpty(warehouse_ids)){
			String[] warehouseIdsArr = warehouse_ids.split(",");
			if(warehouseIdsArr != null && warehouseIdsArr.length > 0)
				for (String warehouseIdStr : warehouseIdsArr) 
					if(!WorkerUtil.isNullOrEmpty(warehouseIdStr))
						warehouseIdsSet.add(Integer.valueOf(warehouseIdStr));
		}
		return warehouseIdsSet;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public Date getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}

	public Date getLast_login_time() {
		return last_login_time;
	}

	public void setLast_login_time(Date last_login_time) {
		this.last_login_time = last_login_time;
	}
	
	/**
	 * @return the ip_type
	 */
	public String getIp_type() {
		return ip_type;
	}

	/**
	 * @param ip_type the ip_type to set
	 */
	public void setIp_type(String ip_type) {
		this.ip_type = ip_type;
	}
	
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

	public List<Integer> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Integer> roleIds) {
		this.roleIds = roleIds;
	}
	
	/**
	 * @author Jarvis
	 * @Description 通过解析roleIds（List）设置role_ids（String）属性
	 * 
	 * */
	public void setRole_ids() {
        if(CollectionUtils.isEmpty(roleIds)) {
            setRole_ids("");
        }else{
        	StringBuilder s = new StringBuilder();
            for(Integer roleId : roleIds) {
                s.append(roleId);
                s.append(",");
            }
            setRole_ids(s.toString());
        }
    }
	
	/**
	 * @author Jarvis
	 * @Description 通过解析role_ids（String）设置roleIds（List）属性
	 * 
	 * */
	public void setRoleIds() {
		if(StringUtils.isEmpty(role_ids)) {
            return;
        }
        String[] roleIdStrs = role_ids.split(",");
        for(String roleIdStr : roleIdStrs) {
            if(StringUtils.isEmpty(roleIdStr)) {
                continue;
            }
            getRoleIds().add(Integer.valueOf(roleIdStr));
        }
	}

	public List<Integer> getCustomerIds() {
		return customerIds;
	}

	public void setCustomerIds(List<Integer> customerIds) {
		this.customerIds = customerIds;
	}
	
	/**
	 * @author Jarvis
	 * @Description 通过解析customerIds（List）设置customer_ids（String）属性
	 * 
	 * */
	public void setCustomer_ids(){
		if(CollectionUtils.isEmpty(customerIds)){
			setCustomer_ids("");
		}else{
			StringBuilder s = new StringBuilder();
			for (Integer customerId : customerIds) {
				s.append(customerId);
				s.append(",");
			}
			setCustomer_ids(s.toString());
		}
	}
	
	/**
	 * @author Jarvis
	 * @Description 通过解析customer_ids（String）设置customerIds（List）属性
	 * 
	 * */
	public void setCustomerIds(){
		if(StringUtils.isEmpty(customer_ids)) {
            return;
        }
        String[] customerIdStrs = customer_ids.split(",");
        for(String customerIdStr : customerIdStrs) {
            if(StringUtils.isEmpty(customerIdStr)) {
                continue;
            }
            getCustomerIds().add(Integer.valueOf(customerIdStr));
        }
	}

	public List<Integer> getWarehouseIds() {
		return warehouseIds;
	}

	public void setWarehouseIds(List<Integer> warehouseIds) {
		this.warehouseIds = warehouseIds;
	}
	
	/**
	 * @author Jarvis
	 * @Description 通过解析warehouseIds（List）设置warehouse_ids（String）属性
	 * 
	 * */
	public void setWarehouse_ids() {
		if(CollectionUtils.isEmpty(warehouseIds)){
			setWarehouse_ids("");
		}else{
			StringBuilder s = new StringBuilder();
			for (Integer warehouseId : warehouseIds) {
				s.append(warehouseId);
				s.append(",");
			}
			setWarehouse_ids(s.toString());
		}
	}
	
	/**
	 * @author Jarvis
	 * @Description 通过解析warehouse_ids（String）设置warehouseIds（List）属性
	 * 
	 * */
	public void setWarehouseIds(){
		if(StringUtils.isEmpty(warehouse_ids)) {
            return;
        }
        String[] warehouseIdStrs = warehouse_ids.split(",");
        for(String warehouseIdStr : warehouseIdStrs) {
            if(StringUtils.isEmpty(warehouseIdStr)) {
                continue;
            }
            getWarehouseIds().add(Integer.valueOf(warehouseIdStr));
        }
	}

	public List<Integer> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(List<Integer> resourceIds) {
		this.resourceIds = resourceIds;
	}
	
	/**
	 * @author Jarvis
	 * @Description 通过解析resourceIds（List）设置resource_ids（String）属性
	 * 
	 * */
	public void setResource_ids() {
		if(CollectionUtils.isEmpty(resourceIds)){
			setResource_ids("");
		}else{
			StringBuilder s = new StringBuilder();
			for (Integer resourceId : resourceIds) {
				s.append(resourceId);
				s.append(",");
			}
			setResource_ids(s.toString());
		}
	}
	
	/**
	 * @author Jarvis
	 * @Description 通过解析resource_ids（String）设置resourceIds（List）属性
	 * 
	 * */
	public void setResourceIds(){
		if(StringUtils.isEmpty(resource_ids)) {
            return;
        }
        String[] resourceIdStrs = resource_ids.split(",");
        for(String resourceIdStr : resourceIdStrs) {
            if(StringUtils.isEmpty(resourceIdStr)) {
                continue;
            }
            getResourceIds().add(Integer.valueOf(resourceIdStr));
        }
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((available == null) ? 0 : available.hashCode());
		result = prime * result
				+ ((created_time == null) ? 0 : created_time.hashCode());
		result = prime * result
				+ ((customer_ids == null) ? 0 : customer_ids.hashCode());
		result = prime * result
				+ ((department == null) ? 0 : department.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((last_login_time == null) ? 0 : last_login_time.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((realname == null) ? 0 : realname.hashCode());
		result = prime * result
				+ ((resource_ids == null) ? 0 : resource_ids.hashCode());
		result = prime * result
				+ ((role_ids == null) ? 0 : role_ids.hashCode());
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		result = prime * result
				+ ((warehouse_ids == null) ? 0 : warehouse_ids.hashCode());
		result = prime * result
				+ ((ip_type == null) ? 0 : ip_type.hashCode());
		result = prime * result
				+ ((department_id == null) ? 0 : department_id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SysUser other = (SysUser) obj;
		if (available == null) {
			if (other.available != null)
				return false;
		} else if (!available.equals(other.available))
			return false;
		if (created_time == null) {
			if (other.created_time != null)
				return false;
		} else if (!created_time.equals(other.created_time))
			return false;
		if (customer_ids == null) {
			if (other.customer_ids != null)
				return false;
		} else if (!customer_ids.equals(other.customer_ids))
			return false;
		if (department == null) {
			if (other.department != null)
				return false;
		} else if (!department.equals(other.department))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (last_login_time == null) {
			if (other.last_login_time != null)
				return false;
		} else if (!last_login_time.equals(other.last_login_time))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (realname == null) {
			if (other.realname != null)
				return false;
		} else if (!realname.equals(other.realname))
			return false;
		if (resource_ids == null) {
			if (other.resource_ids != null)
				return false;
		} else if (!resource_ids.equals(other.resource_ids))
			return false;
		if (role_ids == null) {
			if (other.role_ids != null)
				return false;
		} else if (!role_ids.equals(other.role_ids))
			return false;
		if (salt == null) {
			if (other.salt != null)
				return false;
		} else if (!salt.equals(other.salt))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (ip_type == null) {
			if (other.ip_type != null)
				return false;
		} else if (!ip_type.equals(other.ip_type))
			return false;
		if (department_id == null) {
			if (other.department_id != null)
				return false;
		} else if (!department_id.equals(other.department_id))
			return false;
		if (warehouse_ids == null) {
			if (other.warehouse_ids != null)
				return false;
		} else if (!warehouse_ids.equals(other.warehouse_ids))
			return false;
		return true;
	}
	
}
