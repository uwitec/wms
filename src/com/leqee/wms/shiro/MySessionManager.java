package com.leqee.wms.shiro;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import com.leqee.wms.api.util.WorkerUtil;
import com.leqee.wms.entity.SysUser;

/**
 * session管理器
 * 重写getSessionId方法
 * @author JackRams
 *
 */
public class MySessionManager extends DefaultWebSessionManager {
            
	/**
	 * 此主方法可以接收来自cookie中的sessionid与外部调用参数传来sessionid并切换到对应的会话
	 * 如都不存在sessionid则交由shiro原类处理
	 */
	@Override
	protected Serializable getSessionId(ServletRequest request,	ServletResponse response) {
        // 其实这里还可以使用如下参数：cookie中的session名称：如：JSESSIONID=xxx,路径中的 ;JESSIONID=xxx，但建议还是使用 __sid参数。
		javax.servlet.http.Cookie[] cookies = ((HttpServletRequest)request).getCookies();
		String sid = request.getParameter("sid");
		if(sid==null || sid.equals("")){
			if(cookies !=null){
				for(javax.servlet.http.Cookie c : cookies){
					if(c.getName().equals("SHAREJSESSIONID")){
						sid=c.getValue();
					}
				}
			}
		}else{
			RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
					.getSecurityManager();
			CacheManager cacheManager = securityManager.getCacheManager();
			Cache<String,Serializable> sfcache=cacheManager.getCache("shiro-sfactiveSessionCaches");
			
			Session session = getSession(new DefaultSessionKey(sid)); 
			SysUser sysUser = (SysUser) session.getAttribute("currentUser");
			String username=sysUser.getUsername();
			Serializable sfsid=sfcache.get(username);
			if(!WorkerUtil.isNullOrEmpty(sfsid) && !sfsid.equals(sid)){
				Cache<String, Deque<Serializable>> cache=cacheManager.getCache("shiro-activeSessionCaches");
				Deque<Serializable> deque = cache.get(username);  
			    if(deque == null) {  
			        deque = new LinkedList<Serializable>();  
			        cache.put(username, deque);  
			    }  
			  
			    //如果队列里没有此sessionId，且用户没有被踢出；放入队列  
			    if(deque.contains(sfsid)) {  
			        deque.remove(sfsid);
			        Session session2 = getSession(new DefaultSessionKey(sfsid));
			        session2.setAttribute("kickout", true);
			      //如果被踢出了，直接退出，重定向到踢出后的地址  
				    if (session.getAttribute("kickout") != null) {  
				        //会话被踢出了  
				        try {  
				        	org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
				            subject.logout();  
				            //saveRequest(request);  
					        WebUtils.issueRedirect(request, response, "/login?kickout=1");
				        } catch (Exception e) { //ignore  
				        }  
				          
				        return false;  
				    }  
			    }  

			}
		    sfcache.put(username, sid);
		}
        if (StringUtils.isNotBlank(sid)) {  
            // 是否将sid保存到cookie，浏览器模式下使用此参数。  
            if (WebUtils.isTrue(request, "__cookie")){ 

                HttpServletRequest rq = (HttpServletRequest)request;  
                HttpServletResponse rs = (HttpServletResponse)response;  
                Cookie template = getSessionIdCookie();  
                Cookie cookie = new SimpleCookie(template);  
                cookie.setValue(sid); cookie.saveTo(rq, rs); 
            }  
            // 设置当前session状态  
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, ShiroHttpServletRequest.URL_SESSION_ID_SOURCE); // session来源与url  
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sid);  
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);  
            return sid;
        }else{  
            return super.getSessionId(request, response);  
        }
	}

}
