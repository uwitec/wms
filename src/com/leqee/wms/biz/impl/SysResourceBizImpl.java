package com.leqee.wms.biz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.leqee.wms.biz.SysResourceBiz;
import com.leqee.wms.dao.SysResourceDao;
import com.leqee.wms.entity.SysResource;
import com.leqee.wms.util.WorkerUtil;

@Service
public class SysResourceBizImpl implements SysResourceBiz {
	@Autowired
	SysResourceDao sysResourceDao;

	@Override
	public SysResource createResource(SysResource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SysResource updateResource(SysResource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteResource(Integer resourceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public SysResource findByResourceId(Integer resourceId) {
		return sysResourceDao.selectByResourceId(resourceId);
	}

	@Override
	public List<SysResource> findAllAvailable() {
		return sysResourceDao.selectAllAvailable();
	}

	@Override
	public Set<String> findPermissions(Set<Integer> resourceIds) {
		Set<String> permissions = new HashSet<String>();
		if(!WorkerUtil.isNullOrEmpty(resourceIds)){
			for (Integer resourceId : resourceIds) {
				SysResource sysResource = findByResourceId(resourceId);
				if(!WorkerUtil.isNullOrEmpty(sysResource)){
					permissions.add(sysResource.getPermission());
				}
			}
		}
		return permissions;
	}
	
	@Override
	public List<SysResource> findMenus(Set<String> permissions) {
		List<SysResource> allMenus = sysResourceDao.selectByType("menu");
		Map<Integer, SysResource> permittedPrimaryMenusMap = new LinkedHashMap<Integer, SysResource>();
		List<SysResource> permittedSecondaryMenusList = new ArrayList<SysResource>();
		List<SysResource> permittedMenusList = new ArrayList<SysResource>();
		if(!WorkerUtil.isNullOrEmpty(allMenus)){
			// 过滤掉没有权限的菜单
			// 同时将菜单划分成两类：一级 and 二级
			for (SysResource sysResource : allMenus) {
				if(sysResource.isRootNode())
					continue;
				if(!hasPermission(permissions, sysResource))
					continue;
				if(sysResource.getParent_id() == 1){
					permittedPrimaryMenusMap.put(sysResource.getId(), sysResource);
				}else{
					permittedSecondaryMenusList.add(sysResource);
				}
			}
			
			if(!WorkerUtil.isNullOrEmpty(permittedPrimaryMenusMap) && !WorkerUtil.isNullOrEmpty(permittedSecondaryMenusList)){
				for (SysResource sysResource : permittedSecondaryMenusList) {
					if(permittedPrimaryMenusMap.containsKey(sysResource.getParent_id()))
						permittedPrimaryMenusMap.get(sysResource.getParent_id()).getChildSysResourceList().add(sysResource);
				}
			}
			
			if(!WorkerUtil.isNullOrEmpty(permittedPrimaryMenusMap)){
				Iterator<Integer> it = permittedPrimaryMenusMap.keySet().iterator();
				while(it.hasNext()){
					Integer key = it.next();
					permittedMenusList.add(permittedPrimaryMenusMap.get(key));
				}
			}
			
		}
		
		return permittedMenusList;
	}
	
	private boolean hasPermission(Set<String> permissions, SysResource resource) {
        if(StringUtils.isEmpty(resource.getPermission())) {
            return true;
        }
        for(String permission : permissions) {
            WildcardPermission p1 = new WildcardPermission(permission);
            WildcardPermission p2 = new WildcardPermission(resource.getPermission());
            if(p1.implies(p2) || p2.implies(p1)) {
                return true;
            }
        }
        return false;
    }
	
	@Override
	public Set<String> findResources(Set<Integer> resourceIds) {
		Set<String> resources = new HashSet<String>();
		if(!WorkerUtil.isNullOrEmpty(resourceIds)){
			for (Integer resourceId : resourceIds) {
				SysResource sysResource = findByResourceId(resourceId);
				if(!WorkerUtil.isNullOrEmpty(sysResource)){
					resources.add(sysResource.getName());
				}
			}
		}
		return resources;
	}

	@Override
	public Map<String, String> findAllPermissionResourceMap() {
		Map<String, String> permissionResourceMap = new HashMap<String, String>();
		List<SysResource> resourcesList = findAllAvailable();
		if(!WorkerUtil.isNullOrEmpty(resourcesList)){
			for (SysResource sysResource : resourcesList) {
				permissionResourceMap.put(sysResource.getPermission(), sysResource.getName());
			}
		}
		return permissionResourceMap;
	}

	/* (non-Javadoc)
	 * @see com.leqee.wms.biz.SysResourceBiz#getResourceListByUser(java.lang.Integer)
	 */
	@Override
	public List<Map<String, String>> getResourceListByUser(Integer id) {
		return sysResourceDao.selectAllAvailableByUser(id);
	}

	/* (non-Javadoc)
	 * @see com.leqee.wms.biz.SysResourceBiz#getResourceListByRole(java.lang.Integer)
	 */
	@Override
	public List<Map<String, String>> getResourceListByRole(Integer roleId) {
		return sysResourceDao.getResourceListByRole(roleId);
	}

}
