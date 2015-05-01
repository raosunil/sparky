package com.s3web.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.struts2.rest.handler.ContentTypeHandler;

import com.thoughtworks.xstream.XStream;

/**
* Handles XML content
*/
public class XStreamHandler implements ContentTypeHandler {

   public String fromObject(Object obj, String resultCode, Writer out) throws IOException {
      if (obj != null) {
         XStream xstream = new XStream();
         xstream.processAnnotations(obj.getClass());
         xstream.toXML(obj, out);
      }
      return null;
   }

   public void toObject(Reader in, Object target) {
      XStream xstream = new XStream();
      xstream.processAnnotations(target.getClass());
      xstream.fromXML(in, target);
   }

   public String getContentType() {
      return "application/xml";
   }

   public String getExtension() {
      return "xml";
   }
}