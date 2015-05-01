package com.s3web.util;

import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

public class CustomJsonConfig extends JsonConfig {
	
	public CustomJsonConfig(){
		super();
		setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
	}

}
