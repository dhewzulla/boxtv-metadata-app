package uk.co.boxnetwork.mule.transformers;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BoxRestTransformer  extends AbstractMessageTransformer{
	static final protected Logger logger=LoggerFactory.getLogger(BoxRestTransformer.class);
	
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		
		MuleRestUtil.addCORS(message, outputEncoding);
		String inboudMethod=message.getInboundProperty("http.method");
		if(inboudMethod.equals("GET")){
			return processGET(message, outputEncoding);
		}
		else if(inboudMethod.equals("PUT")){
			try {
				return processPUT(message, outputEncoding);
			} catch (Exception e) {
				logger.error(e+"while processing PUT",e);
				return new ErrorMessage(e+ "while processing PUT");	
			}
		}
		else if(inboudMethod.equals("POST")){
			return processPOST(message, outputEncoding);
		}

		else if(inboudMethod.equals("DELETE")){
			return processDELETE(message, outputEncoding);
		}
		else if(inboudMethod.equals("OPTIONS")){
			return processOPTIONS(message, outputEncoding);
		}
		else 
			return processDefault(message,outputEncoding,inboudMethod);							
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
   
   
}
