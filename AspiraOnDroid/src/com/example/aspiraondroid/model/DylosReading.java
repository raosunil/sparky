package com.example.aspiraondroid.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sunil Rao
 */
public class DylosReading {
    
	private int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private String type = "Feature";
	
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	private Map properties = new LinkedHashMap();
	


	private Map geometry = new LinkedHashMap();

	/*private String deviceId;
    private String userId;
    private String dateTime;
    private int smallParticleCount;
    private int largeParticleCount;
    private double geoLatitude;
    private double geoLongitude;
    private String geoMethod;*/
    
    public DylosReading(String deviceId, String userId, long d, int s, int l, double geoLatitude, double geoLongitude, String geoMethod)  {
    	
    	properties.put("type", "Dylos");
    	properties.put("deviceId", deviceId);
    	properties.put("dateTime", d);
    	properties.put("method", geoMethod);
    	properties.put("userId", userId);
    	properties.put("smallParticle", s);
    	properties.put("largeParticle", l);
    	
    	geometry.put("type", "Point");
    	double[] coordinatesArray = {geoLatitude,geoLongitude};
    	geometry.put("coordinates",coordinatesArray);
    }
    
	
/*	{
		  "type": "Feature",
		  "properties": {...},
		  "geometry": {
			"type": "Point",
			"coordinates": [
			  63.017578125,
			  48.10743118848039
			]
		  }
	}*/
	
/*	   "properties":{
	     "type":"Dylos",
		 "deviceId":"aqm0",
		 "dateTime":"Fri Mar 21 13:37:09 MST 2014",
		 "method":"manual",
		 "userId":"user0",
		 "smallParticle":93,
		 "largeParticle":26
	   }*/
	public Map getProperties() {
		return properties;
	}

	public void setProperties(Map properties) {
		this.properties = properties;
	}

	public Map getGeometry() {
		return geometry;
	}

	public void setGeometry(Map geometry) {
		this.geometry = geometry;
	}

}
