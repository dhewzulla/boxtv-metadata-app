package uk.co.boxnetwork.mule.transformers.bc;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.BCVideoService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.BCConfiguration;
import uk.co.boxnetwork.data.BCVideoData;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.mule.transformers.metadata.EpisodeTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class VideoTransmer extends AbstractMessageTransformer{
	private static final Logger logger=LoggerFactory.getLogger(EpisodeTransformer.class);
	@Autowired
	BCVideoService videoService;
	
			
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
	private Object processGET(MuleMessage message, String outputEncoding){
			
		String videoid=MuleRestUtil.getPathPath(message);
		if(videoid==null || videoid.length()==0){
			return getAllVideos(message,outputEncoding);
		}
		else{
			return getAVideo(videoid, message,outputEncoding);				
		}
	}
	private Object getAllVideos(MuleMessage message, String outputEncoding){		
		return videoService.listVideo();		
	}
    private Object getAVideo(String videoid, MuleMessage message, String outputEncoding){
	  		Long id=Long.valueOf(videoid);
		    return null;									
	}
		
	private Object processPOST(MuleMessage message, String outputEncoding){
		
		return new ErrorMessage("The Method POST not supported");			 
	}
	
   private Object processPUT(MuleMessage message, String outputEncoding) throws Exception{
	   String videoid=MuleRestUtil.getPathPath(message);
	   if(videoid==null||videoid.length()==0){
		   return new ErrorMessage("The videoid is missing in DELETE");
	   }
	   Long id=Long.valueOf(videoid);
	   uk.co.boxnetwork.data.Episode  episode=null;
	   
	   return null;						 
	}
   private Object processDELETE(MuleMessage message, String outputEncoding){
		
		return new ErrorMessage("The Method DELETE not supported");			 
	}
   private Object processOPTIONS(MuleMessage message, String outputEncoding){			
	   return message.getPayload();
	   
	}
   private Object processDefault(MuleMessage message, String outputEncoding, String method){
	   
		return new ErrorMessage("The Method "+method+" not supported");			 
	}
   
   
}
