package uk.co.boxnetwork.mule.transformers.tasks;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.TimedTaskService;

public class CheckTimedTaskTransformer extends AbstractMessageTransformer{
		
	
	@Autowired
	private TimedTaskService timedTaskService;
	
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		logger.info("checking the timed tasks");
		timedTaskService.checkAndRunTasks();		
		return message.getPayload();
	}
	
}