package com.leqee.wms.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.leqee.wms.util.WorkerUtil;

@Controller
@RequestMapping(value = "/cache")
public class CacheController {
	
	private Logger logger = Logger.getLogger(CacheController.class);
	
	private Cache<String, Object> cache;
	
	/**
	 * 页面加载
	 * */
	@RequestMapping(value = "/show")
	public String showCache(HashMap<String, Object> model) {
		
		// 缓存类型
		List<String> cacheList = new ArrayList<String>();
		
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = factory.newDocumentBuilder();
	 
	        
	        System.out.println(getClass().getResource("/").getPath() );
	        
	        Document document = db.parse(new File(getClass().getResource("/").getPath()+File.separator+"ehcache.xml"));
	        NodeList node = document.getElementsByTagName("cache");
	        
	        for(int i=0;i<node.getLength();i++){
	            Element element = (Element)node.item(i);
	            // 获取属性学号
	            String name = element.getAttribute("name");
	            System.out.println("name：" + name);
	            cacheList.add(name);
	        }
		}
		catch(Exception e){
			System.out.println("error,erroe message:"+e.getMessage());
		}
		
		model.put("cacheList", cacheList);
		return "/cache/cacheShow";
	}
	

	/**
	 * 查询缓存
	 * */
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> search(@RequestParam(value = "cache_name", required = false, defaultValue = "") String cache_name){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		RealmSecurityManager securityManager =  
			     (RealmSecurityManager) SecurityUtils.getSecurityManager(); 
		CacheManager cacheManager= securityManager.getCacheManager();
		
		// 缓存类型
		List<String> cacheList = new ArrayList<String>();
		
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = factory.newDocumentBuilder();	 

	        Document document = db.parse(new File(getClass().getResource("/").getPath()+File.separator+"ehcache.xml"));
	        NodeList node = document.getElementsByTagName("cache");
	        
	        for(int i=0;i<node.getLength();i++){
	            Element element = (Element)node.item(i);
	            String name = element.getAttribute("name");
	            System.out.println("name：" + name);
	            cacheList.add(name);
	        }
		}
		catch(Exception e){
			System.out.println("error,erroe message:"+e.getMessage());
		}
		
		
//		cacheList.add("passwordRetryCache");
//		cacheList.add("authorizationCache");
//		cacheList.add("authenticationCache");
//		cacheList.add("shiro-activeSessionCache");
//		cacheList.add("shopNameCache");
//		cacheList.add("dashboardCache");
		
		// 合法的缓存类型
		if(cacheList.contains(cache_name)){
			resMap.put("cache_name", cache_name);
			cache = cacheManager.getCache(cache_name);
			
			if(!WorkerUtil.isNullOrEmpty(cache)){
				Set<String> keys = cache.keys();
				resMap.put("keys", keys);
			}
		}
		
		return resMap;
	}
	
	/**
     *  删除缓存
	 * 
	 * */
    @RequestMapping(value = "/{cache_name}/{key}/delete")
    public String delete(@PathVariable("cache_name") String cache_name, @PathVariable("key") String key, Model model) throws UnsupportedEncodingException  {
    	try {
    		RealmSecurityManager securityManager =  
   			     (RealmSecurityManager) SecurityUtils.getSecurityManager(); 
    		CacheManager cacheManager= securityManager.getCacheManager();
    		
    		cache = cacheManager.getCache(cache_name);
    		cache.remove(key);
    		
    		model.addAttribute("msg", java.net.URLEncoder.encode("删除成功","UTF-8"));  // 防止中文乱码
		} catch (Exception e) {
			model.addAttribute("msg", java.net.URLEncoder.encode("删除失败，异常信息：" + e.getMessage(),"UTF-8"));
		}
        
        return "redirect:/cache/show";
    }
	
	
	
}
