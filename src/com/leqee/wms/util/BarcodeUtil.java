package com.leqee.wms.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;


/**
 * 生成条形码工具类
 * @author hzhang1
 * @date 2016-3-15
 * @version 1.0.0
 */
public class BarcodeUtil {
	
	/**
	 * 没有涉及长 & 高参数
	 * @param barcode
	 * @return
	 */
	public static ByteArrayOutputStream generateBarcode(String barcode){

		ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
        try {
            //Create the barcode bean
            Code39Bean bean = new Code39Bean();
             
            final int dpi = 150;
             
            //Configure the barcode generator
            bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); //这个ModuleWidth参数设置疏港的空间。。太小太密。。默认的太小了。
            bean.setHeight(15);
            bean.setWideFactor(3);
            bean.doQuietZone(false);  //两边空白区
             
            try {
                //Set up the canvas provider for monochrome JPEG output 
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                		bout, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
             
                //Generate the barcode
                bean.generateBarcode(canvas, barcode);
             
                //Signal end of generation
                canvas.finish();
            } finally {
            	bout.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bout;
	}
	
	/**
	 * 重载长 & 高参数
	 * @param barcode
	 * @param height
	 * @param width
	 * @return
	 */
	public static ByteArrayOutputStream generateBarcode(String barcode,Integer height,Integer width){

		ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
        try {
            //Create the barcode bean
            Code39Bean bean = new Code39Bean();
             
            final int dpi = 150;
             
            //Configure the barcode generator
            bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); //这个ModuleWidth参数设置疏港的空间。。太小太密。。默认的太小了。
            bean.setHeight(15);
            bean.setWideFactor(3);
            bean.doQuietZone(false);  //两边空白区
             
            try {
                //Set up the canvas provider for monochrome JPEG output 
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                		bout, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
             
                //Generate the barcode
                bean.generateBarcode(canvas, barcode);
             
                //Signal end of generation
                canvas.finish();
            } finally {
            	bout.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bout;
	}
	/**
	 * 根据 条码类型，附属文字大小决定打印样式
	 * @param type  
	 * @param barcode
	 * @param text
	 * @return
	 */
	public static ByteArrayOutputStream generateBarcode(String type,String barcode, String text) {
		if("code128".equalsIgnoreCase(type)) type = "code128";
		if(WorkerUtil.isNullOrEmpty(barcode) || "".equalsIgnoreCase(barcode)) barcode = "000";
		ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
        try {
        	AbstractBarcodeBean bean ;
        	if(type=="code128"){
        		bean = new Code128Bean();
        	}else{
        		bean = new Code39Bean();
        		((Code39Bean) bean).setWideFactor(3);
        	}
             
            final int dpi = 150;
             
            bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); //这个ModuleWidth参数设置疏港的空间。。太小太密。。默认的太小了。
            bean.setHeight(15);
            bean.setFontSize(Integer.parseInt(text));
            
            bean.doQuietZone(false);  //两边空白区
             
            try {
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                		bout, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
             
                bean.generateBarcode(canvas, barcode);
             
                canvas.finish();
            } finally {
            	bout.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bout;
	}
	
	
	
}
