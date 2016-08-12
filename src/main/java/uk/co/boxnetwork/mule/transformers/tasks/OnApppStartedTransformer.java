package uk.co.boxnetwork.mule.transformers.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.springframework.beans.factory.annotation.Autowired;



import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.util.GenericUtilities;

public class OnApppStartedTransformer extends AbstractMessageTransformer{
	
	
@Autowired
private MetadataMaintainanceService metadataMaintainanceService;

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		logger.info("Application initialization");
		metadataMaintainanceService.syncAppConfigWithDatabase();
		//metadataMaintainanceService.fixTxChannel();
		//metadataMaintainanceService.deleteAllTasls();
		//metadataMaintainanceService.fixEpisodeStatusIfEmpty();
		//metadataMaintainanceService.replaceIngestProfiles("high-resolution","box-plus-network-1080p-profile");			
		//metadataMaintainanceService.removeOrphantSeriesGroup();
		//metadataMaintainanceService.updateAllPublishedStatys();
		
		//metadataMaintainanceService.setAvailableWindowForAll(Calendar.getInstance().getTime(),GenericUtilities.nextYearDate());
		//metadataMaintainanceService.autoPublishChangesToBrightcove();
		return message.getPayload();
	}
	
	
}