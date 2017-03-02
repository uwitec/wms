package com.leqee.wms.biz.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.SysResourceBiz;
import com.leqee.wms.biz.SysRoleBiz;
import com.leqee.wms.dao.SysRoleDao;
import com.leqee.wms.entity.SysRole;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.util.WorkerUtil;

@Service
public class SysRoleBizImpl implements SysRoleBiz {
	Logger logger = Logger.getLogger(SysRoleBizImpl.class);
	
	@Autowired
	SysRoleDao sysRoleDao;
	@Autowired
	SysResourceBiz sysResourceBiz;
	

	@Override
	public Response createRole(SysRole role) {
		// 初始化
		Response response = new Response(Response.OK, "创建角色成功!");
		try {
	        sysRoleDao.insert(role);
	        if(role.getId() <= 0){
	        	response = ResponseFactory.createErrorResponse("插入角色表数据失败！");
	        }
		} catch (Exception e) {
			logger.error("创建角色时发生异常，异常信息：" + e.getMessage(),e);
			throw new RuntimeException("创建角色时发生异常，异常信息：" + e.getMessage());
		}
        
        return response;
	}

	@Override
	public int updateRole(SysRole role) {
		return sysRoleDao.update(role);
	}

	@Override
	public SysRole findByRoleId(Integer roleId) {
		return sysRoleDao.selectByRoleId(roleId); 
	}

	@Override
	public List<SysRole> findAllAvailable() {
		return sysRoleDao.selectAllAvailable();
	}

	@Override
	public Set<String> findRoles(Set<Integer> roleIds) {
		Set<String> roles = new HashSet<String>();
		if(!WorkerUtil.isNullOrEmpty(roleIds)){
			for (Integer roleId : roleIds) {
				SysRole sysRole = findByRoleId(roleId);
				if(!WorkerUtil.isNullOrEmpty(sysRole)){
					roles.add(sysRole.getName());
				}
			}
		}
		return roles;
	}

	@Override
	public Set<String> findPermissions(Set<Integer> roleIds) {
		Set<Integer> resourceIds = new HashSet<Integer>();
		if(!WorkerUtil.isNullOrEmpty(roleIds)){
			for (Integer roleId : roleIds) {
				SysRole sysRole = findByRoleId(roleId);
				if(!WorkerUtil.isNullOrEmpty(sysRole)&&sysRole.getAvailable()){
					resourceIds.addAll(sysRole.getResourceIdsSet());
				}
			}
		}
		return sysResourceBiz.findPermissions(resourceIds);
	}

	@Override
	public List<SysRole> findAll() {
		return sysRoleDao.selectAll();
	}

	@Override
	public Response invalidateById(Integer roleId) {
		Response response = new Response(Response.OK, "删除角色成功");
		try {
			int effectRows = sysRoleDao.invalidateById(roleId);
			if(effectRows <= 0) {
				response = ResponseFactory.createErrorResponse("删除角色失败");
			}
		} catch (Exception e) {
			logger.error("删除角色（角色ID:"+roleId+"）时发生异常，异常信息："+e.getMessage(), e);
			throw new RuntimeException("删除角色（角色ID:"+roleId+"）时发生异常，异常信息："+e.getMessage());
		}
		return response;
	}

	@Override
	public Set<String> findResources(Integer roleId) {
		Set<String> resources = new HashSet<String>();
		SysRole sysRole = findByRoleId(roleId);
		if(WorkerUtil.isNullOrEmpty(sysRole)){
			return Collections.emptySet();
		}
		resources.addAll(sysResourceBiz.findResources(sysRole.getResourceIdsSet()));
		
		return resources;
	}

	/* (non-Javadoc)
	 * @see com.leqee.wms.biz.SysRoleBiz#getRoleListByUser(java.lang.Integer)
	 */
	@Override
	public List<Map<String, String>> getRoleListByUser(Integer userId) {
		return sysRoleDao.getRoleListByUser(userId);
	}

}
