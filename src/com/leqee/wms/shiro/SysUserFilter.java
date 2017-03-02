package com.leqee.wms.shiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.beans.factory.annotation.Autowired;

import com.leqee.wms.biz.SysResourceBiz;
import com.leqee.wms.biz.SysUserBiz;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.util.WorkerUtil;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author Jarvis
 * @CreatedDate 2016.02.29
 * @Description 自定义用户拦截器
 * 
 * */
public class SysUserFilter extends PathMatchingFilter {
	Logger logger = Logger.getLogger(SysUserFilter.class);

    @Autowired
    SysUserBiz sysUserBiz;
    
    @Autowired
    SysResourceBiz sysResourceBiz;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
    	Subject subject = SecurityUtils.getSubject();
        String username = (String)subject.getPrincipal();
        
        SysUser sysUser = sysUserBiz.findByUsername(username);
        request.setAttribute("user", sysUser);
        
        // 判断Session是否有设置,若未设置,则进行设置
        Session session = subject.getSession();
        if(WorkerUtil.isNullOrEmpty(session.getAttribute("currentUser"))
        		|| WorkerUtil.isNullOrEmpty(session.getAttribute("userCustomers"))
//        		|| WorkerUtil.isNullOrEmpty(session.getAttribute("userWarehouses"))
        		|| WorkerUtil.isNullOrEmpty(session.getAttribute("permissionResourceMap"))){
        	logger.info("开始设置Session级别变量");
            
            // 设置用户信息
            session.setAttribute("currentUser", sysUser);
            
            // 设置货主列表
            List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>();
        	customers = sysUserBiz.findCustomers(username);
        	session.setAttribute("userCustomers", customers);
        	
//        	// 设置仓库列表
//        	List<Warehouse> warehouses = new ArrayList<Warehouse>();
//        	warehouses = sysUserBiz.findWarehouses(username);
//        	session.setAttribute("userWarehouses", warehouses);
        	
        	// 设置权限&资源名称映射（暂时放在Session中目标是放在Application级别变量中）
        	Map<String, String> permissionResourceMap = sysResourceBiz.findAllPermissionResourceMap();
        	session.setAttribute("permissionResourceMap", permissionResourceMap);
        	
        }
        
        
        return true;
    }
}
