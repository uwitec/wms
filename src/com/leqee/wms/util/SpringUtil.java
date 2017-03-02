package com.leqee.wms.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class SpringUtil implements ApplicationContextAware {
	
    private static ApplicationContext ac = null;
    
    public void setApplicationContext(ApplicationContext arg0)
            throws BeansException {
        ac = arg0;
    }
    
    /**
     * 得到spring的上下文ApplicationContext
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext(){
        return ac;
    }

    /**
     * 得到spring中定义的一个bean
     * @param beanName  bean的id
     * @return
     */
    public static Object getBean(String beanName){
        return ac.getBean(beanName);
    }
}
