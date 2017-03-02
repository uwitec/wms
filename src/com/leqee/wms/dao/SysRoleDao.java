package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.SysRole;

public interface SysRoleDao {

	/**
	 * @author Jarvis
	 * @Description 根据角色ID进行查找
	 * 
	 * @param roleId an Integer: 角色ID
	 * 
	 * @return sysRole a SysRole: 角色对象
	 * */
	public SysRole selectByRoleId(Integer roleId);

	/**
	 * @author Jarvis
	 * @Description 查找所有有效的角色
	 * 
	 * @return sysRoles a List<SysRole>: 有效的角色列表
	 * */
	public List<SysRole> selectAllAvailable();

	/**
	 * @author Jarvis
	 * @Description 查找所有角色
	 * 
	 * @return sysRoles a List<SysRole>: 角色列表
	 * */
	public List<SysRole> selectAll();

	/**
	 * @author Jarvis
	 * @Description 废除一个角色
	 * 
	 * */
	public int invalidateById(Integer roleId);

	/**
	 * @author Jarvis
	 * @Description 插入一条记录
	 * 
	 * @param role a SysRole: 角色对象
	 * */
	public int insert(SysRole role);

	/**
	 * @author Jarvis
	 * @Description 根据主键ID进行更新
	 * 
	 * @param role a SysRole: 角色对象
	 * 
	 * @return effectRows an int: 影响行数
	 * */
	public int update(SysRole role);

	/**
	 * @param userId
	 * @return
	 */
	public List<Map<String, String>> getRoleListByUser(@Param("userId") Integer userId);
}
