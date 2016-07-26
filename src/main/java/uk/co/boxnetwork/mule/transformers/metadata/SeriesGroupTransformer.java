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
   
   
    @Override	
   protected Object processPUT(MuleMessage message, String outputEncoding) throws Exception{
	   String seriesgroupid=MuleRestUtil.getPathPath(message);
	   if(seriesgroupid==null||seriesgroupid.length()==0){
		   return returnError("seriesgroupid is required",message);
	   }
	   Long id=Long.valueOf(seriesgroupid);
	   uk.co.boxnetwork.data.SeriesGroup  seriesgroup=null;
	   
	   if(message.getPayload() instanceof uk.co.boxnetwork.data.SeriesGroup){
		   seriesgroup=(uk.co.boxnetwork.data.SeriesGroup)message.getPayload();			   
	   }
	   else{			   
		   		String seriesGroupInJson=(String)message.getPayloadAsString();		   
			   logger.info("*****updating:"+seriesGroupInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
				
				
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				seriesgroup = objectMapper.readValue(seriesGroupInJson, uk.co.boxnetwork.data.SeriesGroup.class);
	   }
	   metadataService.update(id,seriesgroup);	   
	   return metadataService.getSeriesGroupById(id);						 
	}
    
    @Override
	protected Object processDELETE(MuleMessage message, String outputEncoding){	
		String seriesgroupid=MuleRestUtil.getPathPath(message);
		if(seriesgroupid==null || seriesgroupid.length()==0){
			return returnError("Do not support delete all seriesgroup",message);
		}
		else{
			Long id=Long.valueOf(seriesgroupid);
			uk.co.boxnetwork.data.SeriesGroup series=metadataService.getSeriesGroupById(id);
			metadataService.deleteSeriesGroupById(id);
			
			logger.info("SeriesGroup is deleted successfully id="+id);
			return series;
		}			 
	}
	
}
