package com.s3web.common.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;

import com.opensymphony.xwork2.ActionSupport;
import com.s3web.common.service.AnalyticService;
import com.s3web.util.ApplicationContextProvider;



public class OrderAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2018732315686222256L;



	private Logger logger = Logger.getLogger(OrderAction.class);

	
	
	private String someText="Nature";
	
	private List mydataList;
	
	
	
	
	public List getMydataList() {
		return mydataList;
	}

	public void setMydataList(List mydataList) {
		this.mydataList = mydataList;
	}

	public String getSomeText() {
		return someText;
	}

	public void setSomeText(String someText) {
		this.someText = someText;
	}

	public String execute(){	

		ApplicationContext context =  ApplicationContextProvider.getApplicationContext();
		//MyDataService mydataservice = (MyDataService)context.getBean("mydataService");
		//mydataList = mydataservice.fetchMyData();
		//logger.debug("Printing the values"+ mydataList.size());

		AnalyticService analyticService = (AnalyticService)context.getBean("analyticService");
		//TODO Temporary - need to pipe results of final results to mydatalist
		mydataList = analyticService.fetchAQMData();
		HttpServletRequest request = ServletActionContext.getRequest();
		request.setAttribute("mydataList", mydataList);

		/*logger.debug("Calling doAnalysis");
		analyticService.doAnalysis();*/

		return SUCCESS;
	}

	/*@Override
	public void setSession(Map<String, Object> sessMap) {
		// TODO Auto-generated method stub
		this.sessionMap = sessMap;
		
	}
*/
}
