package uk.co.boxnetwork.mule.transformers.s3;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.ImportC4ScheduleService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.FileIngestRequest;
import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.data.s3.S3Notifivcation;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class S3NotificationTransformer extends BoxRestTransformer{

	
	
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){		
		try{
			com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			String requestInJson = (String)message.getPayloadAsString();							
			logger.info("S3 Notification is received"+requestInJson);
			S3Notifivcation s3Notification = objectMapper.readValue(requestInJson, S3Notifivcation.class);		
			return s3Notification;			
		}
		catch(Exception e){
			throw new RuntimeException("failed on processing S3VideoConcatTransformer request",e);
		}
		  		  	    			 
	}
	
}
