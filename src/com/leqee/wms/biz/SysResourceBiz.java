package com.leqee.wms.biz;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.leqee.wms.entity.SysResource;

public interface SysResourceBiz {
	
	public SysResource createResource(SysResource resource);
	
    public SysResource updateResource(SysResource resource);
    
    public void deleteResource(Integer resourceId);

    public SysResource findByResourceId(Integer resourceId);
    
    /**
     * 获取所有权限&资源名称映射
     * @return
     */
    public Map<String, String> findAllPermissionResourceMap();
    
    /**
     * 查找所有有效的资源
     * @return
     */
    public List<SysResource> findAllAvailable();

    /**
     * 得到资源对应的权限字符串
     * @param resourceIds
     * @return
     */
    public Set<String> findPermissions(Set<Integer> resourceIds);

    /**
     * 根据用户权限得到菜单
     * @param permissions
     * @return
     */
    public List<SysResource> findMenus(Set<String> permissions);

    /**
     * 得到资源的名称
     * @param resourceIds
     * @return
     */
	public Set<String> findResources(Set<Integer> resourceIds);

	/**
	 * @param userId
	 * @return
	 */
	public List<Map<String, String>> getResourceListByUser(Integer userId);
	
	public List<Map<String, String>> getResourceListByRole(Integer roleId);
    
}
