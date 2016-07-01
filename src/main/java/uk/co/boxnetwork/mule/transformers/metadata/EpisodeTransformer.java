package uk.co.boxnetwork.mule.transformers.metadata;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class EpisodeTransformer extends AbstractMessageTransformer{
		private static final Logger logger=LoggerFactory.getLogger(EpisodeTransformer.class);
		@Autowired
		MetadataService metadataService;
				
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
				
			String episodeid=MuleRestUtil.getPathPath(message);
			if(episodeid==null || episodeid.length()==0){
				return getAllEpisodes(message,outputEncoding);
			}
			else{
				return getAnEpisode(episodeid, message,outputEncoding);				
			}
		}
		private Object getAllEpisodes(MuleMessage message, String outputEncoding){
				List<Episode> episodes=metadataService.getAllEpisodes();
					return episodes;
		}
        private Object getAnEpisode(String episodeid, MuleMessage message, String outputEncoding){
    	  		Long id=Long.valueOf(episodeid);
			    return metadataService.getEpisodeById(id);									
		}
			
		private Object processPOST(MuleMessage message, String outputEncoding){
			
			return new ErrorMessage("The Method POST not supported");			 
		}
		
       private Object processPUT(MuleMessage message, String outputEncoding) throws Exception{
    	   String episodeid=MuleRestUtil.getPathPath(message);
    	   if(episodeid==null||episodeid.length()==0){
    		   return new ErrorMessage("The episodeid is missing in DELETE");
    	   }
		   Long id=Long.valueOf(episodeid);
		   uk.co.boxnetwork.data.Episode  episode=null;
		   
		   if(message.getPayload() instanceof uk.co.boxnetwork.data.Episode){
			   episode=(uk.co.boxnetwork.data.Episode)message.getPayload();			   
		   }
		   else{			   
			   		String episodeInJson=(String)message.getPayloadAsString();		   
				   logger.info("*****"+episodeInJson+"****");
					ObjectMapper objectMapper=new ObjectMapper();
					episode = objectMapper.readValue(episodeInJson, uk.co.boxnetwork.data.Episode.class);
		   }
		   metadataService.update(id,episode);
		   
		   return metadataService.getEpisodeById(id);						 
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
