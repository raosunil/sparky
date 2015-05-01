package com.s3web.common.action;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.s3web.common.beans.AQMResults;
import com.s3web.common.service.AnalyticService;
import com.s3web.util.ApplicationContextProvider;



public class JSONAQMResultsAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3653792586356865547L;


	private Logger logger = Logger.getLogger(JSONAQMResultsAction.class);
	
	
	
	private List<AQMResults> listofqmresult;
	





	public String execute(){
		ApplicationContext context =  ApplicationContextProvider.getApplicationContext();
		AnalyticService analyticService = (AnalyticService)context.getBean("analyticService");
		listofqmresult = analyticService.fetchAQMResults();
		return Action.SUCCESS;
	}
	
	public List<AQMResults> getListofqmresult() {
		return listofqmresult;
	}

	public void setListofqmresult(List<AQMResults> listofqmresult) {
		this.listofqmresult = listofqmresult;
	}


	
	
	
	
}
