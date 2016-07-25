package uk.co.boxnetwork.mule.transformers.bc;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.module.http.internal.ParameterMap;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.BCVideoService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.bc.BCConfiguration;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.transformers.metadata.EpisodeTransformer;
import uk.co.boxnetwork.mule.util.MenuMessageUtil;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class VideoTransmer extends  BoxRestTransformer{
	@Autowired
	BCVideoService videoService;
	
		
	
	@Override
		protected Object processGET(MuleMessage message, String outputEncoding){
			
		String videoid=MuleRestUtil.getPathPath(message);
		if(videoid==null || videoid.length()==0){
			ParameterMap queryparams=message.getInboundProperty("http.query.params");
			return videoService.listVideo(queryparams.get("limit"),queryparams.get("offset"),queryparams.get("sort"),queryparams.get("q"));
		}
		else{
			if(videoid.endsWith("/sources")){
				return getVideoSources(message,outputEncoding,videoid.substring(0,videoid.length()-"/soures".length()-1));
			}
			else{				
				return getVideo(message,outputEncoding,videoid);
			}
		}
	}
	
	private Object getVideo(MuleMessage message, String outputEncoding, String videoid){
		ParameterMap queryparams=message.getInboundProperty("http.query.params");
		if(queryparams!=null &&"true".equals(queryparams.get("raw"))){
			return videoService.getViodeInJson(videoid);
		}
		else{
			return videoService.getVideo(videoid);
		}
	}
	private Object getVideoSources(MuleMessage message, String outputEncoding, String videoid){
		ParameterMap queryparams=message.getInboundProperty("http.query.params");
		if(queryparams!=null &&"true".equals(queryparams.get("raw"))){
			return videoService.getViodeSourcesInJson(videoid);
		}
		else{
			return videoService.getVideoSource(videoid);
		}
		
		
	}
	
    
	
	
      
   
}
