package uk.co.boxnetwork.mule.transformers.tasks;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.ImportC4ScheduleService;
import uk.co.boxnetwork.components.TimedTaskService;
import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.model.TimedTask;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;


public class TimedTaskServiceTransformer extends BoxRestTransformer{
	
	@Autowired
	private TimedTaskService timedTaskService;
	
	
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){		
		String payload=null;
		try {
			payload = message.getPayloadAsString();
			logger.info("recived the timed Task Service:"+payload);
			
			com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			
			
			objectMapper.setSerializationInclusion(Include.NON_NULL);	    
			TimedTask timedTask = objectMapper.readValue(payload, TimedTask.class);
			timedTaskService.persist(timedTask);
			return timedTask;			
		} catch (Exception e) {			
			throw new RuntimeException("Error in TimedTaskServiceTransformer:"+e,e);
		}
		  		  	    			 
	}
	  
   
}