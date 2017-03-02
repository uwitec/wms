package com.leqee.wms.biz;

import java.util.List;
import java.util.Set;

import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.response.Response;

public interface SysUserBiz {

	/**
     * 创建用户
     * @param user
     */
    public Response createUser(SysUser user);

    /**
     * 更新用户
     * @param user
     */
    public int updateUser(SysUser user);

    /**
     * 废弃用户
     * @param user
     */
    public Response invalidateById(Integer userId);

    /**
     * 修改密码
     * @param userId
     * @param newPassword
     */
    public Response changePassword(Integer userId, String newPassword);

    /**
     * 根据主键ID查找用户
     * @param userId
     */
    SysUser findOne(Integer userId);

    /**
     * 查找所有用户
     * @return
     */
    List<SysUser> findAll();

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public SysUser findByUsername(String username);

    /**
     * 根据用户名查找其角色
     * @param username
     * @return
     */
    public Set<String> findRoles(String username);

    /**
     * 根据用户名查找其权限
     * @param username
     * @return
     */
    public Set<String> findPermissions(String username);

    /**
     * 根据用户名查找其货主
     * @param username
     * @return
     */
	public List<WarehouseCustomer> findCustomers(String username);

	/**
     * 根据用户名查找其仓库
     * @param username
     * @return
     */
	public List<Warehouse> findWarehouses(String username);

	/**
     * 根据用户名查找其可用资源名称
     * @param username
     * @return
     */
	public Set<String> findResources(String username);
	
	/**
     * 根据用户名查找其可用物理仓IDs
     * @param username
     * @return
     */
	public Set<Integer> findPhysicalWarehouseIds(String username);

	/**
     * 根据用户名和物理仓查找其可用逻辑仓
     * @param username
     * @return
     */
	public List<Warehouse> findLogicWarehouses(String username,
			Integer physicalWarehouseId);
}
