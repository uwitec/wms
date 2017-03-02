package com.leqee.wms.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SysResource implements Serializable {

	private Integer id;
	
	private String name;        // 资源名称
	
	private String type;        // 类型：menu-菜单，button-按钮
	
	private String url;         // 访问路径
	
	private Integer parent_id;  // 父资源ID
	
	private String permission;  // 需要权限
	
	private Boolean available = Boolean.TRUE;     // 是否可用
	
	private Integer sequence_number;  // 资源顺序
	
	private List<SysResource> childSysResourceList = new ArrayList<SysResource>(); // 子资源列表
	
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getParent_id() {
		return parent_id;
	}

	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}
	
	public Integer getSequence_number() {
		return sequence_number;
	}

	public void setSequence_number(Integer sequence_number) {
		this.sequence_number = sequence_number;
	}

	public List<SysResource> getChildSysResourceList() {
		return childSysResourceList;
	}

	public void setChildSysResourceList(List<SysResource> childSysResourceList) {
		this.childSysResourceList = childSysResourceList;
	}
	
	public boolean isRootNode() {
        return parent_id == 0;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((available == null) ? 0 : available.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parent_id == null) ? 0 : parent_id.hashCode());
		result = prime * result
				+ ((permission == null) ? 0 : permission.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		SysResource other = (SysResource) obj;
		if (available == null) {
			if (other.available != null)
				return false;
		} else if (!available.equals(other.available))
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
		if (parent_id == null) {
			if (other.parent_id != null)
				return false;
		} else if (!parent_id.equals(other.parent_id))
			return false;
		if (permission == null) {
			if (other.permission != null)
				return false;
		} else if (!permission.equals(other.permission))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
}
