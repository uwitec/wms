package com.leqee.wms.controller;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.util.BarcodeUtil;
import com.leqee.wms.util.WorkerUtil;

/**
 * 公用控制器
 * @author hzhang1
 * @date 2016-3-15
 * @version 1.0.0
 */
@Controller
@RequestMapping(value="common")
public class CommonController {
	
	@RequestMapping(value="barcode/generate")
	@ResponseBody
	public String barcode( HttpServletRequest req , HttpServletResponse response){
		
		String barcode = req.getParameter("barcode");
		String type = req.getParameter("type");
		String text = req.getParameter("text");
		if(WorkerUtil.isNullOrEmpty(type)) type = "code39";
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
        try {
        	if(WorkerUtil.isNullOrEmpty(text)){//默认为39码，且存在文本
        		bout = BarcodeUtil.generateBarcode(barcode);
        	}else{
        		bout = BarcodeUtil.generateBarcode(type,barcode,text);
        	}
        	
            response.setContentType("image/png");
            response.setContentLength(bout.size());
            response.getOutputStream().write(bout.toByteArray());
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "test";
	}

}
