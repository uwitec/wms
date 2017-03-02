package com.leqee.wms.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.biz.DepartmentBiz;
import com.leqee.wms.biz.SysResourceBiz;
import com.leqee.wms.biz.SysRoleBiz;
import com.leqee.wms.biz.SysUserBiz;
import com.leqee.wms.biz.WarehouseBiz;
import com.leqee.wms.biz.WarehouseCustomerBiz;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.WorkerUtil;

@Controller
@RequestMapping(value="/sysUser")
public class SysUserController {
	Logger logger = Logger.getLogger(SysUserController.class);
	
	private Cache<String, Object> cache;
	
	@Autowired
	SysUserBiz sysUserBiz;
	@Autowired
	SysRoleBiz sysRoleBiz;
	@Autowired
	WarehouseCustomerBiz warehouseCustomerBiz;
	@Autowired
	WarehouseBiz warehouseBiz;
	@Autowired
	SysResourceBiz sysResourceBiz;
	@Autowired
	DepartmentBiz departmentBiz;
	
	/**
	 * 解决日期属性自动转换绑定的问题
	 * */
	@InitBinder
    private void dateBinder(WebDataBinder binder) {
        //The date format to parse or output your dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Create a new CustomDateEditor
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        //Register it as custom editor for the Date type
        binder.registerCustomEditor(Date.class, editor);
    }
	
	@RequiresPermissions("sys:user:view")
	@RequestMapping(method = RequestMethod.GET)
	public String list(HttpServletRequest req, Model model) throws UnsupportedEncodingException {
		// 获取传递过来的信息参数
		String msg = "";
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("msg")))
			msg =  URLDecoder.decode(req.getParameter("msg"),"UTF-8");
		
        model.addAttribute("sysUserList", sysUserBiz.findAll());
        model.addAttribute("msg", msg);
        return "sysUser/list";
    }
	
	@RequiresPermissions("sys:user:create")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String showCreateForm(Model model) {
        setCommonData(model, -1);
        model.addAttribute("sysUser", new SysUser());
        model.addAttribute("op", "新增");
        return "sysUser/edit";
    }
	
	@RequiresPermissions("sys:user:create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(SysUser sysUser, Model model) throws UnsupportedEncodingException {
		try {
	        Response response = sysUserBiz.createUser(sysUser);
	        model.addAttribute("msg", URLEncoder.encode(response.getMsg(),"UTF-8"));
		} catch (Exception e) {
			logger.error("创建新用户时发生异常，异常信息：" + e.getMessage(), e);
			model.addAttribute("msg", URLEncoder.encode("创建失败，异常信息：" + e.getMessage(),"UTF-8"));
		}
        return "redirect:/sysUser";
    }

	/**
	 * @Description 更新前准备
	 * 
	 * */
	@RequiresPermissions("sys:user:update")
    @RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        setCommonData(model, id);
        SysUser sysUser = sysUserBiz.findOne(id);
        sysUser.setRoleIds();
        sysUser.setWarehouseIds();
        sysUser.setCustomerIds();
        sysUser.setResourceIds();
        
        // 获取资源权限字符串并传给前端
        Set<String> resourceNameSet = sysUserBiz.findResources(sysUser.getUsername());
        if(!WorkerUtil.isNullOrEmpty(resourceNameSet)){
        	StringBuilder s = new StringBuilder();
        	for (String resourceName : resourceNameSet) {
				s.append(resourceName);
				s.append(",");
			}
        	model.addAttribute("resourceNames", s.toString());
        }
        
        // 获取仓库权限字符串并传给前端
        List<Warehouse> warehouseList = sysUserBiz.findWarehouses(sysUser.getUsername());
        if(!WorkerUtil.isNullOrEmpty(warehouseList)){
        	StringBuilder s = new StringBuilder();
        	for (Warehouse warehouse : warehouseList) {
        		s.append(warehouse.getWarehouse_name());
        		s.append(",");
			}
        	model.addAttribute("warehouseNames", s.toString());
        }
        
        model.addAttribute("sysUser", sysUser);
        model.addAttribute("op", "修改");
        return "sysUser/edit";
    }

	/**
	 * @throws UnsupportedEncodingException 
	 * @Description 更新
	 * 
	 * */
    @RequiresPermissions("sys:user:update")
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
    public String update(SysUser user, Model model) throws UnsupportedEncodingException {
    	String msg = "修改成功";
    	try {
    		user.setRole_ids();
    		user.setCustomer_ids();
//    		user.setWarehouse_ids();
    		logger.info("warehouse_ids: "+ user.getWarehouse_ids());
    		logger.info("resource_ids: "+ user.getResource_ids());
    		int effectRows = sysUserBiz.updateUser(user);
    		if(effectRows <= 0){
    			msg = "修改失败";
    		}
		} catch (Exception e) {
			logger.error("修改用户信息时发生异常，异常信息为: " + e.getMessage(), e);
			msg = "修改失败，异常信息：" + e.getMessage();
		}
    	model.addAttribute("msg", URLEncoder.encode(msg,"UTF-8"));
        return "redirect:/sysUser";
    }
    
    /**
	 * @Description 重置密码前准备
	 * 
	 * */
	@RequiresPermissions("sys:user:update")
    @RequestMapping(value = "/{id}/changePassword", method = RequestMethod.GET)
    public String showChangePasswordForm(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("sysUser", sysUserBiz.findOne(id));
        model.addAttribute("op", "重置密码");
        return "sysUser/changePassword";
    }

	/**
	 * @throws UnsupportedEncodingException 
	 * @Description 重置密码
	 * 
	 * */
    @RequiresPermissions("sys:user:update")
    @RequestMapping(value = "/{id}/changePassword", method = RequestMethod.POST)
    public String changePassword(@PathVariable("id") Integer id, String newPassword, Model model) throws UnsupportedEncodingException {
        try {
			Response response = sysUserBiz.changePassword(id, newPassword);
			model.addAttribute("msg", URLEncoder.encode(response.getMsg(),"UTF-8"));
			
			// 获取用户名称
			SysUser sysUser = sysUserBiz.findOne(id);
			String username = sysUser.getUsername();
			
			// 清除缓存
	        RealmSecurityManager securityManager =  
	  			     (RealmSecurityManager) SecurityUtils.getSecurityManager(); 
	   		CacheManager cacheManager= securityManager.getCacheManager();
	   		
	   		cache = cacheManager.getCache("authenticationCache");
	   		Set<String> authenticationCacheKeys = cache.keys();
	   		for (String key : authenticationCacheKeys) {
				if(username.equalsIgnoreCase(key)) {
					cache.remove(key);
				}
			}
	   		
		} catch (Exception e) {
			logger.error("重置密码时发生异常，异常信息：" + e.getMessage(), e);
			model.addAttribute("msg", URLEncoder.encode("重置密码失败，异常信息：" + e.getMessage(),"UTF-8"));
		}
        return "redirect:/sysUser";
    }
    
    /**
     * @throws UnsupportedEncodingException 
	 * @Description 删除
	 * 
	 * */
    @RequiresPermissions("sys:user:delete")
    @RequestMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") Integer id, Model model) throws UnsupportedEncodingException  {
    	try {
    		Response response = sysUserBiz.invalidateById(id);
    		model.addAttribute("msg", java.net.URLEncoder.encode(response.getMsg(),"UTF-8"));  // 防止中文乱码
		} catch (Exception e) {
			logger.error("删除用户时发生异常，异常信息：" + e.getMessage(), e);
			model.addAttribute("msg", java.net.URLEncoder.encode("删除失败，异常信息：" + e.getMessage(),"UTF-8"));
		}
        
        return "redirect:/sysUser";
    }
	
    
    /**
	 * @Description 个人设置前准备
	 * 
	 * */
    @RequestMapping(value = "/{id}/personalSetting", method = RequestMethod.GET)
    public String showPersonalSettingForm(@PathVariable("id") Integer id, Model model) {
        SysUser sysUser = sysUserBiz.findOne(id);
        model.addAttribute("sysUser", sysUser);
        return "sysUser/personalSetting";
    }

	/**
	 * @Description 个人设置
	 * 
	 * */
    @RequestMapping(value = "/{id}/personalSetting", method = RequestMethod.POST)
    public String personalSetting(SysUser user, Model model){
    	String msg = "设置成功";
    	try {
    		int effectRows = sysUserBiz.updateUser(user);
    		if(effectRows <= 0){
    			msg = "设置失败";
    		}
		} catch (Exception e) {
			logger.error("设置个人信息时发生异常，异常信息为: " + e.getMessage(), e);
			msg = "设置失败，异常信息：" + e.getMessage();
		}
    	model.addAttribute("msg", msg);
        return "welcome";
    }
    
    /**
	 * @Description 修改密码前准备
	 * 
	 * */
    @RequestMapping(value = "/{id}/modifyPassword", method = RequestMethod.GET)
    public String showModifyPasswordForm(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("sysUser", sysUserBiz.findOne(id));
        return "sysUser/modifyPassword";
    }

	/**
	 * @Description 修改密码
	 * 
	 * */
    @RequestMapping(value = "/{id}/modifyPassword", method = RequestMethod.POST)
    public String modifyPassword(@PathVariable("id") Integer id, String newPassword, Model model) {
        try {
			Response response = sysUserBiz.changePassword(id, newPassword);
			model.addAttribute("msg", response.getMsg());
			
			// 获取用户名称
			SysUser sysUser = sysUserBiz.findOne(id);
			String username = sysUser.getUsername();
			
			// 清除缓存
	        RealmSecurityManager securityManager =  
	  			     (RealmSecurityManager) SecurityUtils.getSecurityManager(); 
	   		CacheManager cacheManager= securityManager.getCacheManager();
	   		
	   		cache = cacheManager.getCache("authenticationCache");
	   		Set<String> authenticationCacheKeys = cache.keys();
	   		for (String key : authenticationCacheKeys) {
				if(username.equalsIgnoreCase(key)) {
					cache.remove(key);
				}
			}
	   		
		} catch (Exception e) {
			logger.error("修改密码时发生异常，异常信息：" + e.getMessage(), e);
			model.addAttribute("msg", "修改密码失败，异常信息：" + e.getMessage());
		}
        
        // 退出当前登录
        return "redirect:/logout";
    }	
	
	private void setCommonData(Model model, Integer userId) {
		model.addAttribute("sysRoleList", sysRoleBiz.getRoleListByUser(userId));
        model.addAttribute("customerList", warehouseCustomerBiz.getWarehouseCustomerListByUser(userId));
        List<Map<String,String>> warehouseList = warehouseBiz.getWarehouseListByUser(userId);
        // 将物理仓的physical_warehouse_id设置为0,为前端生成树状图做准备
        if(!WorkerUtil.isNullOrEmpty(warehouseList)){
        	for (Map<String, String> warehouse : warehouseList) {
				if("Y".equals(warehouse.get("is_physical"))){
					warehouse.put("physical_warehouse_id", "0");
				}
			}
        }
        model.addAttribute("warehouseList", warehouseList);
        
        model.addAttribute("resourceList", sysResourceBiz.getResourceListByUser(userId));
        
        model.addAttribute("departmentList", departmentBiz.selectAll());
    }
}
