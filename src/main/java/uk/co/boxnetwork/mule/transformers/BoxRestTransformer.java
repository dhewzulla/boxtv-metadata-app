package uk.co.boxnetwork.mule.transformers;

import java.io.InputStream;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;

import uk.co.boxnetwork.data.ErrorMessage;

import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BoxRestTransformer  extends AbstractMessageTransformer{
	static final protected Logger logger=LoggerFactory.getLogger(BoxRestTransformer.class);
	
	protected String convertObjectToJson(Object obj) throws JsonProcessingException{
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();		
		objectMapper.setSerializationInclusion(Include.NON_NULL);					
		return objectMapper.writeValueAsString(obj);			
	}
	public String returnError(String desc,MuleMessage message){
		message.setOutboundProperty("http.status", 500);		
		return "{error:\""+desc+"\"}";		
	}
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		    logger.info("***Rest request received on "+this.getClass().getName());
	try{	
				addCORS(message, outputEncoding);
				String inboudMethod=message.getInboundProperty("http.method");
				Object returnObject=null;		
				if(inboudMethod.equals("GET")){
					returnObject=processGET(message, outputEncoding);
				}
				else if(inboudMethod.equals("PUT")){			
					returnObject=processPUT(message, outputEncoding);			
				}
				else if(inboudMethod.equals("POST")){
					returnObject=processPOST(message, outputEncoding);
				}
		
				else if(inboudMethod.equals("DELETE")){
					returnObject=processDELETE(message, outputEncoding);
				}
				else if(inboudMethod.equals("OPTIONS")){
					returnObject=processOPTIONS(message, outputEncoding);
				}
				else 
					returnObject=processDefault(message,outputEncoding,inboudMethod);	
				if(returnObject ==null){
					return "{\"message\":\"ok, empty response\"}";					
				}
				else if(returnObject instanceof org.mule.transport.NullPayload){
					logger.info("Service returned NullPauyload");
					return returnObject;
				}
				else if(returnObject instanceof String || returnObject instanceof InputStream){
					return returnObject;
				}
				else {
					try {
						return convertObjectToJson(returnObject);
					} catch (JsonProcessingException e) {
						logger.error("Unbale to convert the object to json: returnObject=p["+returnObject+"] class="+returnObject.getClass().getName() );
						return returnError("Unable to convert to json",message);
					}
				}
	     }
		catch(Exception e) {
						logger.error(e+" in transformer",e);						
						return returnError(e.toString(),message);
		 }
		
		
	}
	
	 protected Object processGET(MuleMessage message, String outputEncoding){
			
		return new ErrorMessage("The Method GET not supported");
	}
	 protected Object processPOST(MuleMessage message, String outputEncoding){
		
		return new ErrorMessage("The Method POST not supported");			 
	}	
	protected Object processPUT(MuleMessage message, String outputEncoding) throws Exception{
	   return new ErrorMessage("The Method PUT not supported");					 
	}
	protected Object processDELETE(MuleMessage message, String outputEncoding){	
		return new ErrorMessage("The Method DELETE not supported");			 
	}
	protected Object processOPTIONS(MuleMessage message, String outputEncoding){			
	   return message.getPayload();
	   
	}
	protected Object processDefault(MuleMessage message, String outputEncoding, String method){	   
		return new ErrorMessage("The Method "+method+" not supported");			 
	}
   
   public void addCORS(MuleMessage message, String outputEncoding){
		String origin=null;		 
		Map<String,String> headers=message.getInboundProperty("http.headers");
		if(headers!=null){
			origin= headers.get("Origin");
		}
		else{
			origin= "*";
		}
	    message.setOutboundProperty("Access-Control-Allow-Origin", origin);
	    message.setOutboundProperty("Access-Control-Max-Age", "600");
	    message.setOutboundProperty("Access-Control-Allow-Methods","GET,POST, PUT, PATCH, OPTIONS");
	    message.setOutboundProperty("Access-Control-Allow-Headers","X-Requested-With, Content-Type, Accept,apikey,accept, authorization");
   }
}
