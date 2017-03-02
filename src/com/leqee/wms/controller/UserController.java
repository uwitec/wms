package com.leqee.wms.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/user")  //指定根路径
public class UserController {
	
	@RequestMapping(value = "/login" ,method=RequestMethod.POST )
	public String login( HttpServletRequest req , Map<String,Object> model ){
		String name = (String) req.getParameter("account");
		String password = (String) req.getParameter("password");
		
		Subject user = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(name,password);
		token.setRememberMe(true);
		try {
			user.login(token);
			return "/main";
		}catch (AuthenticationException e) {
			token.clear();
			return "redirect:/";
		}
	}

//	/**
//	 * 生成验证码
//	 * @param request
//	 * @param response
//	 * @throws IOException
//	 */
//	@RequestMapping(value = "/validateCode")
//	public void validateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		response.setHeader("Cache-Control", "no-cache");
//		String verifyCode = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_ONLY, 4, null);
//		request.getSession().setAttribute("validateCode", verifyCode);
//		response.setContentType("image/jpeg");
//		BufferedImage bim = ValidateCode.generateImageCode(verifyCode, 90, 30, 3, true, Color.WHITE, Color.BLACK, null);
//		ImageIO.write(bim, "JPEG", response.getOutputStream());
//	}
}
