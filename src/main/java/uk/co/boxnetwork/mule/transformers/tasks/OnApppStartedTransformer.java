package uk.co.boxnetwork.mule.transformers.tasks;

import java.util.List;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.springframework.beans.factory.annotation.Autowired;


import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.model.Episode;

public class OnApppStartedTransformer extends AbstractMessageTransformer{
	
	
@Autowired
private MetadataMaintainanceService metadataMaintainanceService;

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		logger.info("Application initialization");
		metadataMaintainanceService.fixTxChannel();
		return message.getPayload();
	}
	
	
}