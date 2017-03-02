package com.leqee.wms.biz.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.SysResourceBiz;
import com.leqee.wms.biz.SysRoleBiz;
import com.leqee.wms.biz.SysUserBiz;
import com.leqee.wms.biz.WarehouseBiz;
import com.leqee.wms.biz.WarehouseCustomerBiz;
import com.leqee.wms.dao.SysUserDao;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.util.PasswordHelper;
import com.leqee.wms.util.WorkerUtil;

@Service
public class SysUserBizImpl implements SysUserBiz {
	Logger logger = Logger.getLogger(SysUserBizImpl.class);
	
	@Autowired
	SysUserDao sysUserDao;
	@Autowired
	SysRoleBiz sysRoleBiz;
	@Autowired
	SysResourceBiz sysResouceBiz;
	@Autowired
	WarehouseCustomerBiz warehouseCustomerBiz;
	@Autowired
	WarehouseBiz warehouseBiz;
	@Autowired
	PasswordHelper passwordHelper;
	
	@Override
	public Response createUser(SysUser user) {
		// 初始化
		Response response = new Response(Response.OK, "创建用户成功!");
		
		try {
			if(!WorkerUtil.isNullOrEmpty(findByUsername(user.getUsername()))) {
				return ResponseFactory.createErrorResponse("创建用户失败：用户名已存在！");
			}
			
			user.setRole_ids();
			user.setCustomer_ids();
//			user.setWarehouse_ids();
			user.setCreated_time(new Date());
			
			//加密密码
	        passwordHelper.encryptPassword(user);
	        sysUserDao.insert(user);
	        if(user.getId() <= 0){
	        	response = ResponseFactory.createErrorResponse("插入用户表数据失败！");
	        }
		} catch (Exception e) {
			logger.error("创建用户时发生异常，异常信息：" + e.getMessage(),e);
			throw new RuntimeException("创建用户时发生异常，异常信息：" + e.getMessage());
		}
        
        return response;
        
	}

	@Override
	public int updateUser(SysUser sysUser) {
		return sysUserDao.update(sysUser);
	}

	@Override
	public Response invalidateById(Integer userId) {
		Response response = new Response(Response.OK, "删除用户成功");
		try {
			int effectRows = sysUserDao.invalidateById(userId);
			if(effectRows <= 0) {
				response = ResponseFactory.createErrorResponse("删除用户失败");
			}
		} catch (Exception e) {
			logger.error("删除用户（用户ID:"+userId+"）时发生异常，异常信息："+e.getMessage(), e);
			throw new RuntimeException("删除用户（用户ID:"+userId+"）时发生异常，异常信息："+e.getMessage());
		}
		return response;
	}

	@Override
	public Response changePassword(Integer userId, String newPassword) {
		Response response = new Response(Response.OK, "密码修改成功");
		try {
			SysUser sysUser = findOne(userId);
			sysUser.setPassword(newPassword);
			passwordHelper.encryptPassword(sysUser);
			int effectRows = updateUser(sysUser);
			if(effectRows <= 0){
				response = ResponseFactory.createErrorResponse("密码修改失败");
			}
		} catch (Exception e) {
			logger.error("修改用户（用户ID:"+ userId +"）密码时发生异常，异常信息："+e.getMessage(), e);
			throw new RuntimeException("修改用户（用户ID:"+ userId +"）密码时发生异常，异常信息："+e.getMessage());
		}
		return response;
	}

	@Override
	public SysUser findOne(Integer userId) {
		return sysUserDao.selectById(userId);
	}

	@Override
	public List<SysUser> findAll() {
		return sysUserDao.selectAll();
	}

	@Override
	public SysUser findByUsername(String username) {
		return sysUserDao.selectByUsername(username);
	}

	@Override
	public Set<String> findRoles(String username) {
		SysUser sysUser = findByUsername(username);
		if(WorkerUtil.isNullOrEmpty(sysUser)){
			return Collections.emptySet();
		}
		return sysRoleBiz.findRoles(sysUser.getRoleIdsSet());
	}

	@Override
	public Set<String> findPermissions(String username) {
		Set<String> permissions = new HashSet<String>();
		SysUser sysUser = findByUsername(username);
		if(WorkerUtil.isNullOrEmpty(sysUser)){
			return Collections.emptySet();
		}
		permissions.addAll(sysRoleBiz.findPermissions(sysUser.getRoleIdsSet()));
		permissions.addAll(sysResouceBiz.findPermissions(sysUser.getResourceIdsSet()));
		
		return permissions;
	}

	@Override
	public List<WarehouseCustomer> findCustomers(String username) {
		SysUser sysUser = findByUsername(username);
		if(WorkerUtil.isNullOrEmpty(sysUser)){
			return Collections.emptyList();
		}
		for(Integer customerId : sysUser.getCustomerIdsSet()){
			logger.info("customerId: " + customerId);
		}
		return warehouseCustomerBiz.findByCustomerIds(sysUser.getCustomerIdsSet());
	}

	@Override
	public List<Warehouse> findWarehouses(String username) {
		SysUser sysUser = findByUsername(username);
		if(WorkerUtil.isNullOrEmpty(sysUser)){
			return Collections.emptyList();
		}
		for (Integer warehouseId : sysUser.getWarehouseIdsSet()) {
			logger.info("warehouseId: " + warehouseId);
		}
		return warehouseBiz.findByWarehouseIds(sysUser.getWarehouseIdsSet());
	}
	
	@Override
	public Set<String> findResources(String username) {
		Set<String> resources = new HashSet<String>();
		SysUser sysUser = findByUsername(username);
		if(WorkerUtil.isNullOrEmpty(sysUser)){
			return Collections.emptySet();
		}
		resources.addAll(sysResouceBiz.findResources(sysUser.getResourceIdsSet()));
		
		return resources;
	}
	
	@Override
	public Set<Integer> findPhysicalWarehouseIds(String username){
		Set<Integer> physicalWarehouseIds = new HashSet<Integer>();
		
		// 查找有权限访问的所有仓库
		List<Warehouse> warehouses = findWarehouses(username);
		if(WorkerUtil.isNullOrEmpty(warehouses)){
			return Collections.emptySet();
		}
		
		// 遍历有权限访问的所有仓库，获取每个仓库所属的物理仓库ID，放入集合中
		for (Warehouse warehouse : warehouses)
			if(!WorkerUtil.isNullOrEmpty(warehouse.getPhysical_warehouse_id()))
				physicalWarehouseIds.add(warehouse.getPhysical_warehouse_id());
		
		return physicalWarehouseIds;
	}

	@Override
	public List<Warehouse> findLogicWarehouses(String username,
			Integer physicalWarehouseId) {
		List<Warehouse> logicWarehouses = new ArrayList<Warehouse>();
		
		// 查找有权限访问的所有仓库
		List<Warehouse> warehouses = findWarehouses(username);
		if(WorkerUtil.isNullOrEmpty(warehouses)){
			return Collections.emptyList();
		}
		// 遍历有权限访问的所有仓库，获取每个仓库所属的物理仓库ID，放入集合中
		for (Warehouse warehouse : warehouses){
			if("Y".equals(warehouse.getIs_physical()) && warehouse.getPhysical_warehouse_id().equals(physicalWarehouseId)){
				// 当前物理仓,直接返回物理仓下所有的逻辑仓
				return warehouseBiz.findLogicWarehousesByPhysicalWarehouseId(physicalWarehouseId);
			}else if(warehouse.getPhysical_warehouse_id().equals(physicalWarehouseId)){
				// 当前物理仓下的逻辑仓
				logicWarehouses.add(warehouse);
			}
		}
		
		return logicWarehouses;
	}

}
