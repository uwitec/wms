package com.leqee.wms.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.leqee.wms.biz.TaobaoShopConfBiz;
import com.leqee.wms.entity.TaobaoShopConf;


@Controller
@RequestMapping(value="/taobaoShopConf")  //指定根路径
public class TaobaoShopConfController  {

	static int threadNum; 
	
	private Logger logger = Logger.getLogger(TaobaoShopConfController.class);

	@Autowired
	TaobaoShopConfBiz taobaoShopConfBiz;
	
	/**
	 * 一、 基本流程测试
	 */
	@RequestMapping(value="/list") 
	public String list( HttpServletRequest req , Map<String,Object> model ){
		//1、获取客户端参数
		String username =  req.getParameter("username") ;
		String msg =  req.getParameter("msg") ;
		
		//2、打印日志
		logger.info(username);
		//System.out.println(username);
		
		//3、整合mybatis
		List<TaobaoShopConf> taobaoShopConfList = taobaoShopConfBiz.selectAllTaobaoShopConf();
		model.put("taobaoShopConfList", taobaoShopConfList );    
		model.put("username", username );    
		model.put("msg", msg );    
		
//		synchronized (this) {
//			threadNum ++;
//			//System.out.println("当前线程数量："+threadNum);
//		}
//		
//		try {
//			Thread.sleep(100000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		synchronized (this) {
//			threadNum --;
//			//System.out.println("当前线程数量："+threadNum);
//		}
		
		return "/taobaoShopConf/list";    
	}
	
	
	/**
	 * 二、 事务测试待定
	 */
	@RequestMapping(value="/delete/{taobaoShopConfId}") 
	public String delete(Model model,@PathVariable("taobaoShopConfId") long taobaoShopConfId ){
		
		try {
			taobaoShopConfBiz.deleteTaobaoShopConfById(taobaoShopConfId);
			model.addAttribute("msg", "SUCCESS");    
		} catch (Exception e) {
			model.addAttribute("msg", "ERROR:"+e.getMessage());    
			e.printStackTrace();
		}
		
		
		return "redirect:/taobaoShopConf/list";    
	}
	
	
	
	
}
