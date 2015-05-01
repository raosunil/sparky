package com.s3web.common.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
A Hash map of configurations used by the Analytical Engine

*
*/
public class ConfigMap {
	
	private Map<String,String> mapSettings = new HashMap<String,String>();

	
	/*public Set<String> getKeys(){
		return mapSettings.keySet();
	}
	
	public void setValue(String key, String value){
		mapSettings.replace(key, value);
	}*/
	
	public String getValue(String key){
		return mapSettings.get(key);
	}
	
	public List<String> getValues(){
		return (List<String>) mapSettings.values();
	}

	



}
