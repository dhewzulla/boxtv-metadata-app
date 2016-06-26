package uk.co.boxnetwork.mule.util;

import java.util.Set;

import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuleRestUtil {
	private static final Logger logger=LoggerFactory.getLogger(MuleRestUtil.class);
  public static String getPathPath(MuleMessage message){
	  String listenerPath=message.getInboundProperty("http.listener.path");
	  int ind=listenerPath.indexOf('*');
	  if(ind>=0){
		  listenerPath=listenerPath.substring(0,ind);		  		  
	  }
	  String requesturi=message.getInboundProperty("http.request.uri");
	  if(requesturi.length()<=listenerPath.length()){
		  return null;		  
	  }
	  else{
		  return requesturi.substring(listenerPath.length());		  
	  }
  }
  public static void printAllInboundProperties(MuleMessage message){
	  Set<String> keys=message.getInboundPropertyNames();
		for(String k:keys){
			logger.info("::::"+k+":"+message.getInboundProperty(k));
		}
		
  }
}
