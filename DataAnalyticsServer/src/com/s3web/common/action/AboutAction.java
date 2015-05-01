package com.s3web.common.action;

import com.opensymphony.xwork2.ActionSupport;

public class AboutAction extends ActionSupport{

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
		return SUCCESS;
	}

}
