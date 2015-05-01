package com.s3web.common.action;

import com.opensymphony.xwork2.ActionSupport;
import com.s3web.common.beans.ConfigMap;

public class ConfigureAction extends ActionSupport{

	/**
	 * 
	 */
	
	private ConfigMap settings;

	public ConfigMap getSettings() {
		return settings;
	}

	public void setSettings(ConfigMap settings) {
		this.settings = settings;
	}

	public String execute(){
		//TODO store settings onto Database
		return SUCCESS;
	}

}
