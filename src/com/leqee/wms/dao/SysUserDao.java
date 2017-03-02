package com.leqee.wms.dao;

import java.util.List;

import com.leqee.wms.entity.SysUser;

public interface SysUserDao {

	/**
	 * @author Jarvis
	 * @Description 根据用户名进行查找
	 * 
	 * @param username a String: 用户名
	 * 
	 * @return sysUser a SysUser: 用户对象
	 * */
	public SysUser selectByUsername(String username);

	/**
	 * @author Jarvis
	 * @Description 查找所有用户
	 * 
	 * @return sysUsers a List<SysUser>: 用户列表
	 * */
	public List<SysUser> selectAll();
	
	/**
	 * @author Jarvis
	 * @Description 插入一条记录
	 * 
	 * @param user a SysUser: 用户对象
	 * */
	public int insert(SysUser user);

	/**
	 * @author Jarvis
	 * @Description （假）删除一个用户
	 * 
	 * */
	public int invalidateById(Integer userId);
	
	/**
	 * @author Jarvis
	 * @Description 根据主键ID进行查找
	 * 
	 * @param userId an Integer: 用户名
	 * 
	 * @return sysUser a SysUser: 用户对象
	 * */
	public SysUser selectById(Integer userId);
	
	/**
	 * @author Jarvis
	 * @Description 根据主键ID进行更新
	 * 
	 * @param sysUser a SysUser: 用户对象
	 * 
	 * @return effectRows an int: 影响行数
	 * */
	public int update(SysUser sysUser);
	
	
}
