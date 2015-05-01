package com.s3web.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware {
	
	private static ApplicationContext ctx;
	
	public static  ApplicationContext getApplicationContext(){
		return ctx;
	}

   
    public void setApplicationContext(ApplicationContext ctx)
            throws BeansException {
        this.ctx = ctx;  
       
    }

}
