package com.s3web.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.struts2.rest.handler.ContentTypeHandler;

public class CustomJsonLibHandler implements ContentTypeHandler  {

	@Override
	public String fromObject(Object obj, String resultCode, Writer stream)
			throws IOException {
		// TODO Auto-generated method stub
		 if (obj != null) {
		      if (isArray(obj)) {
		        JSONArray jsonArray = JSONArray.fromObject(obj,new CustomJsonConfig());
		        stream.write(jsonArray.toString());
		      } else {
		        JSONObject jsonObject = JSONObject.fromObject(obj,new CustomJsonConfig());
		        stream.write(jsonObject.toString());
		      }
		    }
		    return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return "text/javascript";
	}

	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return "json";
	}
	 
	private boolean isArray(Object obj)
	  {
	    return ((obj instanceof Collection)) || (obj.getClass().isArray());
	  }

	@Override
	public void toObject(Reader in, Object target) throws IOException {
		// TODO Auto-generated method stub
		 StringBuilder sb = new StringBuilder();
		    char[] buffer = new char[1024];
		    int len = 0;
		    while ((len = in.read(buffer)) > 0) {
		      sb.append(buffer, 0, len);
		    }
		    if ((target != null) && (sb.length() > 0) && (sb.charAt(0) == '[')) {
		      JSONArray jsonArray = JSONArray.fromObject(sb.toString() ,new CustomJsonConfig());
		      if (target.getClass().isArray())
		        JSONArray.toArray(jsonArray, target, new CustomJsonConfig());
		      else
		        JSONArray.toList(jsonArray, target, new CustomJsonConfig());
		    }
		    else
		    {
		      JSONObject jsonObject = JSONObject.fromObject(sb.toString(),new CustomJsonConfig());
		      JSONObject.toBean(jsonObject, target, new CustomJsonConfig());
		    }
	}

}
