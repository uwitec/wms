package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.SysResource;

public interface SysResourceDao {

	/**
	 * @author Jarvis
	 * @Description 根据资源ID进行查找
	 * 
	 * @param resourceId an Integer: 资源ID
	 * 
	 * @return sysResource a SysResource: 资源对象
	 * */
	public SysResource selectByResourceId(Integer resourceId);
	
	/**
	 * @author Jarvis
	 * @Description 查找所有菜单资源
	 * 
	 * @return Menus a List<SysResource>: 菜单列表
	 * */
	public List<SysResource> selectByType(String type);

	/**
	 * @author Jarvis
	 * @Description 查找所有有效的资源
	 * 
	 * @return resources a List<SysResource>: 资源列表
	 * */
	public List<SysResource> selectAllAvailable();
	
	public List<Map<String,String>> selectAllAvailableByUser(Integer id);

	/**
	 * @param roleId
	 */
	public List<Map<String, String>> getResourceListByRole(@Param("roleId") Integer roleId);
}
