package com.s3web.util;


import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.s3web.common.service.AnalyticService;


public class SimpleClent {
	
	private static Logger logger = Logger.getLogger(SimpleClent.class);
	
	
	
	public static void main (String[] args) {
		/*if (args == null || args.length<1)
			throw new IllegalArgumentException();
		*/
		try{

			ApplicationContext context = new FileSystemXmlApplicationContext("WEB-INF/applicationContext.xml");

			AnalyticService analyticService = (AnalyticService)context.getBean("analyticService");
			logger.debug("Bean found : "+analyticService.getClass());
			logger.debug("Calling persistInputRawData ");
			analyticService.persistInputRawData();


		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

}
