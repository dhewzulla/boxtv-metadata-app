package uk.co.boxnetwork.mule.transformers.metadata;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class SeriesTransformer  extends BoxRestTransformer{
	
	@Autowired
	MetadataService metadataService;
			
	@Override
	protected  Object processGET(MuleMessage message, String outputEncoding){
			
		String seriesid=MuleRestUtil.getPathPath(message);
		if(seriesid==null || seriesid.length()==0){
			return getAllSeries(message,outputEncoding);
		}
		else{
			return getAnSeries(seriesid, message,outputEncoding);				
		}
	}
	private Object getAllSeries(MuleMessage message, String outputEncoding){
			List<Series> series=metadataService.getAllSeries();
				return series;
	}
    private Object getAnSeries(String seriesid, MuleMessage message, String outputEncoding){
	  		Long id=Long.valueOf(seriesid);
		    return metadataService.getSeriesById(id);		    
	}
   
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
   
   
}