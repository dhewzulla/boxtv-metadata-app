package uk.co.boxnetwork.mule.transformers.metadata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mule.api.MuleMessage;

import org.mule.module.http.internal.ParameterMap;



import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;


import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.UpdatePraram;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;
import uk.co.boxnetwork.util.GenericUtilities;

public class EpisodeTransformer extends BoxRestTransformer{

		@Autowired
		MetadataService metadataService;
		
		@Autowired
		MetadataMaintainanceService metadataMaintainanceService;
		
		@Autowired
		AppConfig appConfig;
		
		
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
			SearchParam searchParam=new SearchParam(message,appConfig);
		    return metadataService.getAllEpisodes(searchParam);		    			
					    
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
				   logger.info("*****updating episode"+episodeInJson+"****");
				   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
					
					
					objectMapper.setSerializationInclusion(Include.NON_NULL);
					episode = objectMapper.readValue(episodeInJson, uk.co.boxnetwork.data.Episode.class);
		   }
		   UpdatePraram updatePraram=new UpdatePraram(message,appConfig);
		   if(updatePraram.isSwitchEpisodeSeries()){
			   metadataService.switchEpsiodeSeries(id,episode);			   
		   }
		   else{
			   metadataService.update(id,episode);
		   }
		   if(appConfig.shouldSendSoundmouseHeaderFile(episode)){			  
			   metadataMaintainanceService.scheduleToDeliverSoundmouseHeaderFile(episode.getId());
		   }
		   else{
			   logger.info("***will not deliver the changes to the soundmouse");
		   }
		   if(appConfig.getBrightcoveStatus()){			   
			   return metadataService.publishMetadatatoBCByEpisodeId(id);
		   }
		   else{
			   logger.info("Skipping the auto publish to brightcove");
			   return episode;
		   }
		   
		}  
         
         
         
    	protected Object processPOST(MuleMessage message, String outputEncoding){
    		try{	
	    		    String episodeInJson=(String)message.getPayloadAsString();		   
				   logger.info("*****Posted a new episode:"+episodeInJson+"****");
				   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
				   objectMapper.setSerializationInclusion(Include.NON_NULL);
				   uk.co.boxnetwork.data.Episode episode = objectMapper.readValue(episodeInJson, uk.co.boxnetwork.data.Episode.class);
				   
				   if(episode.getSynopsis()!=null){
						try{
								Pattern p = Pattern.compile("S\\d\\d E\\d\\d .*");
								Matcher m = p.matcher(episode.getSynopsis());
							    if(m.matches()){
							    	String seriesNumberString=episode.getSynopsis().substring(1,3);
							    	String episodeNumberString=episode.getSynopsis().substring(5,7);
							    	int seriesNumber=Integer.parseInt(seriesNumberString);
							    	int episodeNumber=Integer.parseInt(episodeNumberString);
							    	episode.setEpisodeSequenceNumber(episodeNumber);
							    	if(episode.getSeries()!=null){
							    		episode.getSeries().setSeriesNumber(seriesNumber);
							    	}
							    	episode.setSynopsis(episode.getSynopsis().substring(7));							    	
							    }
						}
						catch(Exception e){
							logger.error(e+" while parsing the epg title to get series number and episode number",e);
						}
						
					}
				   
				   
				   
				   if(episode.getId()==null){
					   metadataMaintainanceService.fixTxChannel(episode);
					   String validationError=GenericUtilities.validateEpisode(episode);
					   if(validationError!=null){
						   return returnError(validationError,message);
					   }				   
					   episode=metadataService.reicevedEpisodeByMaterialId(episode);					   
				   }
				   else{
					   episode=metadataService.updateEpisodeById(episode);	
				   }
				   if(episode.getEpisodeStatus()!=null && episode.getEpisodeStatus().getMetadataStatus()==MetadataStatus.NEEDS_TO_PUBLISH_CHANGES){
					  
					   if(appConfig.shouldSendSoundmouseHeaderFile(episode)){
						   metadataMaintainanceService.scheduleToDeliverSoundmouseHeaderFile(episode.getId());
					   }
					   else{
						   logger.info("***will not deliver the changes to the soundmouse");
					   }
					   if(appConfig.getBrightcoveStatus()){
						   return metadataService.publishMetadatatoBCByEpisodeId(episode.getId());   
					   }
					   else{
						   logger.info("Skipping the auto publish to brightcove");
						   return episode;
					   }
					   
				   }
				   else{
					   return episode;
				   }									   
				   
    		}
    		catch(Exception e){
    			throw new RuntimeException("proesing post:"+e,e);    			
    		}
        }
    	@Override
    	protected Object processDELETE(MuleMessage message, String outputEncoding){	
    		String episodeid=MuleRestUtil.getPathPath(message);
			if(episodeid==null || episodeid.length()==0){
				return returnError("Do not support delete all episodes",message);
			}
			else{
				Long id=Long.valueOf(episodeid);
				uk.co.boxnetwork.data.Episode episode=metadataService.getEpisodeById(id);
				if(episode==null){
					returnError("episode seems aready have been deleted",message);
				}
				metadataService.deleteEpisodeById(id);
				logger.info("Episode is deleted successfully id="+id);
				return episode;
			}			 
    	}         
         
}
