package com.leqee.wms.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.api.util.WorkerUtil;
import com.leqee.wms.biz.HardwareBiz;
import com.leqee.wms.biz.SysResourceBiz;
import com.leqee.wms.biz.SysUserBiz;
import com.leqee.wms.biz.WarehouseBiz;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.exception.IpLimitException;
import com.leqee.wms.exception.LackWarehousePermissionException;

@Controller
public class LoginController {
	Logger logger = Logger.getLogger(LoginController.class);

	@Autowired
	WarehouseBiz warehouseBiz;
	
	@Autowired
	SysUserBiz sysUserBiz;
	
	@Autowired
	HardwareBiz hardwareBiz;
	
	@Autowired
	SysResourceBiz sysResourceBiz;
	
	@RequestMapping(value = "/login")
	public String showLoginForm(HttpServletRequest req, Model model) {
		logger.info("/login");
		
		// 若已登录，直接跳转到首页
		Subject subject = SecurityUtils.getSubject();
		if(subject.isAuthenticated()){
			return "redirect:/";
		}
		
		String exceptionClassName = (String) req
				.getAttribute("shiroLoginFailure");
		String error = null;
		if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
			error = "用户名不存在";
		} else if (IncorrectCredentialsException.class.getName().equals(
				exceptionClassName)) {
			error = "密码错误";
		} else if (LockedAccountException.class.getName().equals(
				exceptionClassName)) {
			error = "用户已被封号";
		} else if (IpLimitException.class.getName().equals(exceptionClassName)) {
			error = "该IP"+ com.leqee.wms.util.WorkerUtil.getRemoteIP(req) +"禁止访问";
		} else if (ExcessiveAttemptsException.class.getName().equals(exceptionClassName)) {
			error = "密码输入错误累计已达5次，建议联系管理员";
		} else if (LackWarehousePermissionException.class.getName().equals(
				exceptionClassName)) {
			error = "无此仓库权限";
		} else if (AuthenticationException.class.getName().equals(
				exceptionClassName)) {
			error = "登录失败";
		} else if (exceptionClassName != null) {
			error = "其他错误：" + exceptionClassName;
		}
		model.addAttribute("error", error);
		
		// 返回物理仓库列表
		List<Warehouse> physicalWarehouseList = warehouseBiz.findAllPhysical();
		model.addAttribute("physicalWarehouseList", physicalWarehouseList);
		
		return "login";
	}

	@RequestMapping(value = "/appIndex")
	@ResponseBody
	public Map<String, Object> appIndex(HttpServletRequest req) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 返回物理仓库
		List<Warehouse> pwarehouseList = warehouseBiz.findAllPhysical();
		resultMap.put("success", true);
		resultMap.put("pwarehouseList", pwarehouseList);

		return resultMap;
	}

	@RequestMapping(value = "/appLogin")
	@ResponseBody
	public Map<String, Object> appLogin(HttpServletRequest req,HttpServletResponse rsp) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		

		org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
//		int physical_warehouse_id =Integer.valueOf(req.getParameter("physical_warehouse_id"));
//		String hardwareCode=req.getParameter("hardwareCode");
//		boolean bool=hardwareBiz.checkHardwareCode(hardwareCode,physical_warehouse_id);
//        if(!bool)
//        {
//        	resultMap.put("success", false);
//        	resultMap.put("errorCode", "0001");
//        	return resultMap;
//        }
		
		UsernamePasswordToken token = new UsernamePasswordToken(username,
				password, req.getRemoteHost());
		
		//MyUsernamePasswordToken token = new MyUsernamePasswordToken(username,
		//		password,"1");

		try {
			// 4、登录，即身份验证
			subject.login(token);
			Session session = subject.getSession();	        
	        SysUser sysUser = sysUserBiz.findByUsername(username);
	        // 设置用户信息
	        session.setAttribute("currentUser", sysUser);
			String realName=sysUser.getRealname();
			// 判断是否有此物理仓权限
	    	List<Warehouse> warehouses = new ArrayList<Warehouse>();
	    	warehouses = sysUserBiz.findWarehouses(username);
			if(WorkerUtil.isNullOrEmpty(warehouses)){
				resultMap.put("success", false);
				resultMap.put("errorCode", "0003");
				return resultMap;
			}
			// 当前登录的物理仓

			Warehouse currentPhysicalWarehouse = warehouses.get(0);
			session.setAttribute("currentPhysicalWarehouse", currentPhysicalWarehouse);
						
			// 拥有权限的逻辑仓
			List<Warehouse> logicWarehouses = new ArrayList<Warehouse>();
			logicWarehouses = sysUserBiz.findLogicWarehouses(username, currentPhysicalWarehouse.getPhysical_warehouse_id());
			session.setAttribute("userLogicWarehouses", logicWarehouses);
			
			//Map<String, String> permissionResourceMap = sysResourceBiz.findAllPermissionResourceMap();
			//permissionResourceMap.remove(null);
			//Set<String> permissions =  sysUserBiz.findPermissions(username);
			
			resultMap.put("success", true);
			resultMap.put("realName", realName);
			resultMap.put("physical_warehouse_name", currentPhysicalWarehouse.getWarehouse_name());
			//resultMap.put("permissions", permissions);
			//resultMap.put("permissionResourceMap", permissionResourceMap);
			resultMap.put("locationTypeMap", Location.LOCATION_TYPE_MAP);
			resultMap.put("sid", session.getId());

		} catch (AuthenticationException e) {
			if(e.getClass().equals(IpLimitException.class)) {
				//TODO 需要设置新的errorCode
				resultMap.put("success", false);
				resultMap.put("errorCode", "0004");
				resultMap.put("errorInfo", "该ip:" + req.getRemoteHost() + "禁止登陆");
			} else {
				resultMap.put("success", false);
				resultMap.put("errorCode", "0002");
			}
		}

		return resultMap;
	}

	@RequestMapping(value = "/appLogout")
	@ResponseBody
	public Map<String, Object> appLogout(HttpServletRequest req) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();  
	    String username = (String) subject.getPrincipal();  
	    Serializable sessionId = session.getId();
	    
	    RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
	    Cache<String, Deque<Serializable>> cache=cacheManager.getCache("shiro-activeSessionCaches");
	    Deque<Serializable> deque = cache.get(username);  
	    if(deque == null) {  
	        deque = new LinkedList<Serializable>();   
	    } 
	   //如果队列里没有此sessionId，且用户没有被踢出；放入队列  
	    if(deque.contains(sessionId)) {  
	        deque.remove(sessionId);
	    }
	    Cache<String,Serializable> sfcache=cacheManager.getCache("shiro-sfactiveSessionCaches");
	    sfcache.remove(username);
		try {
			// 4、登出
			subject.logout();
			resultMap.put("success", true);

		} catch (AuthenticationException e) {
			resultMap.put("success", false);
			resultMap.put("errorCode", "0001");
		}

		return resultMap;
	}
	
	@RequestMapping(value = "/logout2")
	public String logout2(HttpServletRequest req) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
		
		Session session = subject.getSession();  
	    String username = (String) subject.getPrincipal();  
	    Serializable sessionId = session.getId();
	    
	    RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
	    Cache<String, Deque<Serializable>> cache=cacheManager.getCache("shiro-activeSessionCaches");
	    Deque<Serializable> deque = cache.get(username);  
	    if(deque == null) {  
	        deque = new LinkedList<Serializable>();   
	    } 
	   //如果队列里没有此sessionId，且用户没有被踢出；放入队列  
	    if(deque.contains(sessionId)) {  
	        deque.remove(sessionId);
	    }  
	    
		try {
			// 4、登出
			subject.logout();
			//System.out.println("success");

		} catch (AuthenticationException e) {
			//System.out.println("error");
		}

		return "redirect:/";
	}

}
