package com.s3web.util;


import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.s3web.common.service.AnalyticService;


public class AnalyticScheduler implements Job {
	
	private static Logger logger = Logger.getLogger(AnalyticScheduler.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try{
			System.out.println("Job AnalyticScheduler is runing");
			System.out.println();
			ApplicationContext context = new FileSystemXmlApplicationContext("/Library/Tomcat/apache-tomcat-6.0.43/webapps/DataAnalyticsServer/WEB-INF/applicationContext.xml");
			System.out.println("Printing context " + context.getBeanDefinitionCount());
			AnalyticService analyticService = (AnalyticService)context.getBean("analyticService");
			logger.debug("Bean found : "+analyticService.getClass());
			logger.debug("Calling doAnalysis ");
			analyticService.doAnalysis();


		}catch (Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logger.debug("Exception in AnalyticScheduler job  : "+sw.toString() );
		}
		
	
		
	}

}
