package uk.co.boxnetwork.mule.transformers.c4;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.BCVideoService;
import uk.co.boxnetwork.components.ImportC4ScheduleService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class ImportC4ScheduleTransformer extends BoxRestTransformer{
	private static final Logger logger=LoggerFactory.getLogger(ImportC4ScheduleTransformer.class);
	
	@Autowired
	ImportC4ScheduleService importC4ScheduleService;
	
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){		
		String payload=null;
		try {
			payload = message.getPayloadAsString();
			logger.info("recived the import schedule request:"+payload);
			
			ObjectMapper objectMapper=new ObjectMapper();	    
			ImportScheduleRequest request;
			
			request = objectMapper.readValue(payload, ImportScheduleRequest.class);
			importC4ScheduleService.importSchedule(request);
			return request;			
		} catch (Exception e) {
			logger.error("error while parsing the import schedule request:"+payload,e);
			
			return null;
		}
		  		  	    			 
	}
	  
   
}
