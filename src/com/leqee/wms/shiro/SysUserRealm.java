package com.leqee.wms.shiro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.leqee.wms.biz.ConfigBiz;
import com.leqee.wms.biz.SysResourceBiz;
import com.leqee.wms.biz.SysUserBiz;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.exception.IpLimitException;
import com.leqee.wms.util.WorkerUtil;

/**
 * @author Jarvis
 * @CreatedDate 2016.02.29
 * @Description 系统用户域
 * 
 * */
public class SysUserRealm extends AuthorizingRealm {
	Logger logger = Logger.getLogger(SysUserRealm.class);
	
	@Autowired
    SysUserBiz sysUserBiz;
	@Autowired
	SysResourceBiz sysResourceBiz;
	@Autowired
	private ConfigBiz configBiz;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String)principals.getPrimaryPrincipal();

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(sysUserBiz.findRoles(username));
        authorizationInfo.setStringPermissions(sysUserBiz.findPermissions(username));
        
        return authorizationInfo;
    }
    
    private boolean checkUserIp(UsernamePasswordToken token, SysUser sysUser) {
    	if("ANYWHERE".equals(sysUser.getIp_type())){
    		return true;
    	} else if("COMPANY".equals(sysUser.getIp_type())) {
    		Set<String> limitIpSet = configBiz.getLimitIpSet();
    		String currentIp = token.getHost().equals("0:0:0:0:0:0:0:1")? "127.0.0.1" : token.getHost();
    		logger.info("currentIp: " + currentIp);
    		if(WorkerUtil.isNullOrEmpty(limitIpSet) || WorkerUtil.isNullOrEmpty(currentIp)) {
    			return false;
    		}                                                                                                                                                                                                                                                           
    		return limitIpSet.contains(currentIp);
    	}
    	return false;
    }

	@Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		logger.info("doGetAuthenticationInfo");
        String username = (String)token.getPrincipal();

        SysUser sysUser = sysUserBiz.findByUsername(username);

        if(sysUser == null) {
            throw new UnknownAccountException();//没找到帐号
        }
        
        if(Boolean.FALSE.equals(sysUser.getAvailable())) {
            throw new LockedAccountException(); //帐号锁定
        }
        
        if(!checkUserIp((UsernamePasswordToken) token, sysUser)) {
        	throw new IpLimitException();
        }
        
    	logger.info("交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配");
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
        		sysUser.getUsername(), //用户名
        		sysUser.getPassword(), //密码
                ByteSource.Util.bytes(sysUser.getCredentialsSalt()), // salt = username + salt
                getName()  //realm name
        );
        
        logger.info("开始设置Session级别变量");
        // Session级别变量设置
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 设置用户信息
        session.setAttribute("currentUser", sysUser);
        
        // 设置货主列表
        List<WarehouseCustomer> customers = new ArrayList<WarehouseCustomer>();
    	customers = sysUserBiz.findCustomers(username);
    	session.setAttribute("userCustomers", customers);
    	
//    	// 设置仓库列表
//    	List<Warehouse> warehouses = new ArrayList<Warehouse>();
//    	warehouses = sysUserBiz.findWarehouses(username);
//    	session.setAttribute("userWarehouses", warehouses);
    	
    	// 设置权限&资源名称映射（暂时放在Session中目标是放在Application级别变量中）
    	Map<String, String> permissionResourceMap = sysResourceBiz.findAllPermissionResourceMap();
    	session.setAttribute("permissionResourceMap", permissionResourceMap);
    	
    	// 更新最后登录时间
    	sysUser.setLast_login_time(new Date());
    	sysUserBiz.updateUser(sysUser);
        
        return authenticationInfo;
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }

}
