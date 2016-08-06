package uk.co.boxnetwork.mule.transformers.metadata;

import java.util.Calendar;
import java.util.List;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;



import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class ScheduleTransformer extends BoxRestTransformer{
	
	@Autowired
	MetadataService metadataService;
			
	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){
		String schduleid=MuleRestUtil.getPathPath(message);
		
		if(schduleid==null){			
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.DATE, -30);
			
			return metadataService.getAllScheduleEventFrom(calendar.getTime());			
		}
		else{
			Long id=Long.valueOf(schduleid);
			return metadataService.getScheduleEventById(id);				
		}
		//return message.getPayload();			
	}
}