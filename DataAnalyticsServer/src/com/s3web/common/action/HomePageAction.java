package com.s3web.common.action;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/*extends ActionSupport implements Action */

public class HomePageAction {
	
	/**
	 * 
	 */
	
	private String someText="Nature";
	
	public String getSomeText() {
		return someText;
	}

	public void setSomeText(String someText) {
		this.someText = someText;
	}

	public String execute(){		
		return "success";
	}

}
