package uk.co.boxnetwork.mule.transformers.metadata;

import java.util.List;

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.Payload;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.SeriesGroup;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class SeriesGroupTransformer extends BoxRestTransformer{
	@Autowired
	MetadataService metadataService;
	
	
			
	@Override
	protected  Object processGET(MuleMessage message, String outputEncoding){
			
		String seriesgroupdid=MuleRestUtil.getPathPath(message);
		if(seriesgroupdid==null || seriesgroupdid.length()==0){
			return getAllSeriesGroups(message,outputEncoding);
		}
		else{
			return getAnSeriesGroup(seriesgroupdid, message,outputEncoding);				
		}
	}
	private Object getAllSeriesGroups(MuleMessage message, String outputEncoding){
			return metadataService.getAllSeriesGroups();			
	}
    private Object getAnSeriesGroup(String seriesgroupdid, MuleMessage message, String outputEncoding){
	  		Long id=Long.valueOf(seriesgroupdid);
		    return metadataService.getSeriesGroupById(id);		    
	}
   
    /*
    @Override	
   protected Object processPUT(MuleMessage message, String outputEncoding) throws Exception{
	   String seriesid=MuleRestUtil.getPathPath(message);
	   if(seriesid==null||seriesid.length()==0){
		   return new ErrorMessage("The episodeid is missing in DELETE");
	   }
	   Long id=Long.valueOf(seriesid);
	   uk.co.boxnetwork.data.Series  series=null;
	   
	   if(message.getPayload() instanceof uk.co.boxnetwork.data.Series){
		   series=(uk.co.boxnetwork.data.Series)message.getPayload();			   
	   }
	   else{			   
		   		String seriesInJson=(String)message.getPayloadAsString();		   
			   logger.info("*****"+seriesInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
				
				
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				series = objectMapper.readValue(seriesInJson, uk.co.boxnetwork.data.Series.class);
	   }
	   metadataService.update(id,series);	   
	   return metadataService.getSeriesById(id);						 
	}
	*/
}
