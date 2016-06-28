package uk.co.boxnetwork.mule.transformers.metadata;

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
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class ScheduleTransformer extends AbstractMessageTransformer{
	private static final Logger logger=LoggerFactory.getLogger(ScheduleTransformer.class);
	@Autowired
	MetadataService metadataService;
			
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		
		String schduleid=MuleRestUtil.getPathPath(message);
		
		if(schduleid==null){			
			List<ScheduleEvent> episodes=metadataService.getAllScheduleEvent();
			return episodes;
		}
		else{
			Long id=Long.valueOf(schduleid);
			return metadataService.getScheduleEventById(id);				
		}
		//return message.getPayload();			
	}
}