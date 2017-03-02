package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.leqee.wms.util.WorkerUtil;

public class SysRole implements Serializable {

	private Integer id;
	
	private String name;          // 角色名称
	
	private String description;   // 描述 
	
	private String resource_ids;  // 资源列表
	
	private Boolean available = Boolean.TRUE;     // 是否可用
	
	private List<Integer> resourceIds = new ArrayList<Integer>();  // 拥有的资源列表
	
	private static final long serialVersionUID = 1L;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
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
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((resource_ids == null) ? 0 : resource_ids.hashCode());
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
		SysRole other = (SysRole) obj;
		if (available == null) {
			if (other.available != null)
				return false;
		} else if (!available.equals(other.available))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (resource_ids == null) {
			if (other.resource_ids != null)
				return false;
		} else if (!resource_ids.equals(other.resource_ids))
			return false;
		return true;
	}
	
}
