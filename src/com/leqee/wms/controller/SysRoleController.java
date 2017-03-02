package com.leqee.wms.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.leqee.wms.biz.SysResourceBiz;
import com.leqee.wms.biz.SysRoleBiz;
import com.leqee.wms.entity.SysRole;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.WorkerUtil;

@Controller
@RequestMapping(value="/sysRole")
public class SysRoleController {
	Logger logger = Logger.getLogger(SysRoleController.class);
	
	@Autowired
	SysRoleBiz sysRoleBiz;
	@Autowired
	SysResourceBiz sysResourceBiz;
	
	@RequiresPermissions("sys:role:view")
	@RequestMapping(method = RequestMethod.GET)
	public String list(HttpServletRequest req, Model model) throws UnsupportedEncodingException {
		// 获取传递过来的信息参数
		String msg = "";
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("msg")))
			msg =  URLDecoder.decode(req.getParameter("msg"),"UTF-8");
		
        model.addAttribute("sysRoleList", sysRoleBiz.findAll());
        model.addAttribute("msg", msg);
        return "sysRole/list";
    }
	
	/**
	 * @Description 创建前准备
	 * 
	 * */
	@RequiresPermissions("sys:role:create")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String showCreateForm(Model model) {
        setCommonData(model, -1);
        model.addAttribute("sysRole", new SysRole());
        model.addAttribute("op", "新增");
        return "sysRole/edit";
    }
	
	/**
     * @throws UnsupportedEncodingException 
	 * @Description 创建
	 * 
	 * */
	@RequiresPermissions("sys:role:create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(SysRole sysRole, Model model) throws UnsupportedEncodingException {
		try {
	        Response response = sysRoleBiz.createRole(sysRole);
	        model.addAttribute("msg", URLEncoder.encode(response.getMsg(),"UTF-8"));
		} catch (Exception e) {
			logger.error("创建新角色时发生异常，异常信息：" + e.getMessage(), e);
			model.addAttribute("msg", URLEncoder.encode("创建失败，异常信息：" + e.getMessage(),"UTF-8"));
		}
        return "redirect:/sysRole";
    }
	
	/**
	 * @Description 更新前准备
	 * 
	 * */
	@RequiresPermissions("sys:role:update")
    @RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        setCommonData(model, id);
        SysRole sysRole = sysRoleBiz.findByRoleId(id);
        sysRole.setResourceIds();
        
        // 获取资源权限字符串并传给前端
        Set<String> resourceNameSet = sysRoleBiz.findResources(sysRole.getId());
        if(!WorkerUtil.isNullOrEmpty(resourceNameSet)){
        	StringBuilder s = new StringBuilder();
        	for (String resourceName : resourceNameSet) {
				s.append(resourceName);
				s.append(",");
			}
        	model.addAttribute("resourceNames", s.toString());
        }
        
        model.addAttribute("sysRole", sysRole);
        model.addAttribute("op", "修改");
        return "sysRole/edit";
    }

	/**
	 * @throws UnsupportedEncodingException 
	 * @Description 更新
	 * 
	 * */
    @RequiresPermissions("sys:role:update")
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
    public String update(SysRole sysRole, Model model) throws UnsupportedEncodingException {
    	String msg = "修改成功";
    	try {
    		int effectRows = sysRoleBiz.updateRole(sysRole);
    		if(effectRows <= 0){
    			msg = "修改失败";
    		}
		} catch (Exception e) {
			logger.error("修改角色信息时发生异常，异常信息为: " + e.getMessage(), e);
			msg = "修改失败，异常信息：" + e.getMessage();
		}
    	model.addAttribute("msg", URLEncoder.encode(msg,"UTF-8"));
        return "redirect:/sysRole";
    }
	
	
	/**
     * @throws UnsupportedEncodingException 
	 * @Description 删除
	 * 
	 * */
    @RequiresPermissions("sys:role:delete")
    @RequestMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") Integer id, Model model) throws UnsupportedEncodingException  {
    	try {
    		Response response = sysRoleBiz.invalidateById(id);
    		model.addAttribute("msg", java.net.URLEncoder.encode(response.getMsg(),"UTF-8"));  // 防止中文乱码
		} catch (Exception e) {
			logger.error("删除角色时发生异常，异常信息：" + e.getMessage(), e);
			model.addAttribute("msg", java.net.URLEncoder.encode("删除失败，异常信息：" + e.getMessage(),"UTF-8"));
		}
        
        return "redirect:/sysRole";
    }
    
    private void setCommonData(Model model, Integer roleId) {
    	List<Map<String, String>> resourceList = sysResourceBiz.getResourceListByRole(roleId);
        model.addAttribute("resourceList", resourceList);
    }
	

}
