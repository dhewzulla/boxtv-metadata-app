package uk.co.boxnetwork.mule.transformers.tasks;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.TimedTaskService;
import uk.co.boxnetwork.model.MediaCommand;

public class CheckTimedTaskTransformer extends AbstractMessageTransformer{
		
	static final protected Logger logger=LoggerFactory.getLogger(CheckTimedTaskTransformer.class);
	@Autowired
	private TimedTaskService timedTaskService;
	
	@Autowired
	private MetadataMaintainanceService metadataMaintainService;
	
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		logger.info("checking the timed tasks");
		timedTaskService.checkAndRunTasks();
		
		MediaCommand mediaCommand=metadataMaintainService.getMeidaCommandForDeliverSoundMouseHeaderFile();
		if(mediaCommand!=null){
			logger.info("******should be sent to tg the vm queue"+mediaCommand);
			return mediaCommand;
		}
		else{
			logger.info("No media command for deliver soundmouse file");
			return message.getPayload();
		}
	}
	
}