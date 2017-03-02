package com.leqee.wms.biz;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.leqee.wms.entity.SysRole;
import com.leqee.wms.response.Response;

public interface SysRoleBiz {

	/**
     * 创建角色
     * @param role a SysRole
     * @return response a Response
     */
	public Response createRole(SysRole role);
	
	/**
     * 更新角色
     * @param role
     */
    public int updateRole(SysRole role);
    
    /**
     * 根据主键ID查找角色
     * @param role
     */
    public SysRole findByRoleId(Integer roleId);
    
    /**
     * 查找所有有效的角色
     * @return
     */
    public List<SysRole> findAllAvailable();

    /**
     * 根据角色编号得到角色标识符列表
     * @param roleIds
     * @return
     */
    Set<String> findRoles(Set<Integer> roleIds);

    /**
     * 根据角色编号得到权限字符串列表
     * @param roleIds
     * @return
     */
    Set<String> findPermissions(Set<Integer> roleIds);

    /**
     * 查找所有有效的角色
     * @return
     */
	public List<SysRole> findAll();

	/**
     * 废弃角色
     * @param roleId
     */
	public Response invalidateById(Integer roleId);

	/**
	 * 根据主键ID查找资源集合
	 * @params roleId an Integer
	 * */
	public Set<String> findResources(Integer roleId);
	
	public List<Map<String, String>> getRoleListByUser(Integer userId);

}
