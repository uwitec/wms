package com.leqee.wms.shiro;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

import com.leqee.wms.biz.SysUserBiz;
import com.leqee.wms.biz.WarehouseBiz;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.exception.LackWarehousePermissionException;
import com.leqee.wms.util.WorkerUtil;

public class MyFormAuthenticationFilter extends FormAuthenticationFilter {
	Logger logger = Logger.getLogger(MyFormAuthenticationFilter.class);
	
	@Autowired
	SysUserBiz sysUserBiz;
	@Autowired
	WarehouseBiz warehouseBiz;
	
	private String physical_warehouse_id;

	public String getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(String physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}
	
	@Override
	protected boolean executeLogin(ServletRequest request,
			ServletResponse response) throws Exception {
		// 获取前端传递过来的参数
		Integer physicalWarehouseId = WorkerUtil.isNullOrEmpty(request.getParameter("physical_warehouse_id")) ? null : Integer.valueOf(request.getParameter("physical_warehouse_id"));
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		// 尝试获取用户的真实IP地址
		String remoteIp = WorkerUtil.getRemoteIP(request);
		// 构造Shiro登录用身份验证token
		UsernamePasswordToken token = new UsernamePasswordToken(username,password,remoteIp);
		
		Subject subject = SecurityUtils.getSubject();
		try {
			// 登录
			subject.login(token);

			// 判断是否有此物理仓权限
			if(!sysUserBiz.findPhysicalWarehouseIds(username).contains(physicalWarehouseId)){
				logger.info("LackWarehousePermission");
				// 无仓库权限一定要登出，否则下次登录将会失效 Bug resolved at 2016.04.23 by zjli
				subject.logout();
				return super.onLoginFailure(token, new LackWarehousePermissionException(), request, response);
			}
			
			// 设置Session
			Session session = subject.getSession();
			
			// 当前登录的物理仓
			Warehouse currentPhysicalWarehouse = warehouseBiz.findByWarehouseId(physicalWarehouseId);
			session.setAttribute("currentPhysicalWarehouse", currentPhysicalWarehouse);
			
			// 拥有权限的逻辑仓
			List<Warehouse> logicWarehouses = new ArrayList<Warehouse>();
			logicWarehouses = sysUserBiz.findLogicWarehouses(username, physicalWarehouseId);
	    	session.setAttribute("userLogicWarehouses", logicWarehouses);
			
			return super.onLoginSuccess(token, subject, request, response);
		} catch (AuthenticationException e) {
			logger.error("LoginFailure AuthenticationException",e);
			subject.logout();
			return super.onLoginFailure(token, e, request, response);
		} catch (Exception e) {
			logger.error("LoginFailure Exception", e);
			subject.logout();
			return true;
		}
	}
	
}
