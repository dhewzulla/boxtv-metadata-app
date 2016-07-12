package uk.co.boxnetwork.mule.transformers.metadata;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.module.http.internal.ParameterMap;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class EpisodeTransformer extends BoxRestTransformer{

		@Autowired
		MetadataService metadataService;
		
		@Override
		protected Object processGET(MuleMessage message, String outputEncoding){				
			String episodeid=MuleRestUtil.getPathPath(message);
			if(episodeid==null || episodeid.length()==0){
				return getAllEpisodes(message,outputEncoding);
			}
			else{
				return getAnEpisode(episodeid, message,outputEncoding);				
			}
		}
		
		private  Object getAllEpisodes(MuleMessage message, String outputEncoding){
			
			ParameterMap queryparams=message.getInboundProperty("http.query.params");
			if(queryparams==null || queryparams.get("search") ==null || queryparams.get("search").length()==0){				
					List<Episode> episodes=metadataService.getAllEpisodes();
					return episodes;
			}
			String search=queryparams.get("search");
			if(search.indexOf("%")==-1){
				search="%"+search+"%";				
			}
			return metadataService.findEpisodes(search);		    
		}
		
         private Object getAnEpisode(String episodeid, MuleMessage message, String outputEncoding){
    	  		Long id=Long.valueOf(episodeid);
			    return metadataService.getEpisodeById(id);									
		}
			
		
         @Override
       protected Object processPUT(MuleMessage message, String outputEncoding) throws Exception{
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
				   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
					
					
					objectMapper.setSerializationInclusion(Include.NON_NULL);
					episode = objectMapper.readValue(episodeInJson, uk.co.boxnetwork.data.Episode.class);
		   }
		   metadataService.update(id,episode);
		   
		   return metadataService.getEpisodeById(id);						 
		}                     
}
