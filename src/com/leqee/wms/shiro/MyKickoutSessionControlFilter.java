package com.leqee.wms.shiro;


import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import com.leqee.wms.api.util.WorkerUtil;

public class MyKickoutSessionControlFilter extends AccessControlFilter{
	
	private int maxSession;
	private boolean kickoutAfter;
	private SpringCacheManagerWrapper cacheManager;
	private MySessionManager sessionManager;
	private String kickoutUrl;
	
	public int getMaxSession() {
		return maxSession;
	}

	public void setMaxSession(int maxSession) {
		this.maxSession = maxSession;
	}

	public boolean isKickoutAfter() {
		return kickoutAfter;
	}

	public void setKickoutAfter(boolean kickoutAfter) {
		this.kickoutAfter = kickoutAfter;
	}

	public SpringCacheManagerWrapper getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(SpringCacheManagerWrapper cacheManager) {
		this.cacheManager = cacheManager;
	}

	public MySessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(MySessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public String getKickoutUrl() {
		return kickoutUrl;
	}

	public void setKickoutUrl(String kickoutUrl) {
		this.kickoutUrl = kickoutUrl;
	}

	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {  
	    Subject subject = getSubject(request, response);  
	    if(!subject.isAuthenticated() && !subject.isRemembered()) {  
	        //如果没有登录，直接进行之后的流程  
	        return true;  
	    }  
	  
	    Session session = subject.getSession();  
	    String username = (String) subject.getPrincipal();  
	    Serializable sessionId = session.getId();  
	  
	    //TODO 同步控制  
	    Cache<String, Deque<Serializable>> cache=cacheManager.getCache("shiro-activeSessionCaches");
	    
	    Cache<String,Serializable> sfcache=cacheManager.getCache("shiro-sfactiveSessionCaches");
	    
	    //如果被踢出了，直接退出，重定向到踢出后的地址  
	    if (session.getAttribute("kickout") != null) {  
	        //会话被踢出了  
	        try {  
	            subject.logout();  
	        } catch (Exception e) { //ignore  
	        }  
	        saveRequest(request);  
	        WebUtils.issueRedirect(request, response, kickoutUrl);  
	        return false;  
	    }  
	    
	    //历史手持设备登陆id
	    Serializable sfsid=sfcache.get(username);
	    
	    Deque<Serializable> deque = cache.get(username);  
	    if(deque == null) {  
	        deque = new LinkedList<Serializable>();  
	        cache.put(username, deque);  
	    }  
	  
	    //如果队列里没有此sessionId，且用户没有被踢出；放入队列  
	    if(!deque.contains(sessionId) && session.getAttribute("kickout") == null) {  
	        deque.push(sessionId);  
	    }  
	  
	    
	    //如果队列里的sessionId数超出最大会话数，开始踢人  
	    while(deque.size() > maxSession && !WorkerUtil.isNullOrEmpty(sfsid)) {  
	        Serializable kickoutSessionId = null;
	        deque.remove(sfsid);
	        if(kickoutAfter) { //如果踢出后者  
	            kickoutSessionId = deque.removeFirst();  
	        } else { //否则踢出前者  
	            kickoutSessionId = deque.removeLast();  
	        }
	        deque.push(sfsid);
	        try {  
	            Session kickoutSession =  
	                sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));  
	            if(kickoutSession != null) {  
	                //设置会话的kickout属性表示踢出了  
	                kickoutSession.setAttribute("kickout", true);  
	            }  
	        } catch (Exception e) {//ignore exception  
	        }  
	    }  
	    
	    
	    //如果队列里的sessionId数超出最大会话数，开始踢人  
	    while(deque.size() > maxSession-1 && WorkerUtil.isNullOrEmpty(sfsid)) {  
	        Serializable kickoutSessionId = null;  
	        if(kickoutAfter) { //如果踢出后者  
	            kickoutSessionId = deque.removeFirst();  
	        } else { //否则踢出前者  
	            kickoutSessionId = deque.removeLast();  
	        }  
	        try {  
	            Session kickoutSession =  
	                sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));  
	            if(kickoutSession != null) {  
	                //设置会话的kickout属性表示踢出了  
	                kickoutSession.setAttribute("kickout", true);  
	            }  
	        } catch (Exception e) {//ignore exception  
	        }  
	    }  
	    
	  
	    //如果被踢出了，直接退出，重定向到踢出后的地址  
	    if (session.getAttribute("kickout") != null) {  
	        //会话被踢出了  
	        try {  
	            subject.logout();  
	        } catch (Exception e) { //ignore  
	        }  
	        saveRequest(request);  
	        WebUtils.issueRedirect(request, response, kickoutUrl);  
	        return false;  
	    }  
	    return true;  
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest arg0,
			ServletResponse arg1, Object arg2) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}   
}
