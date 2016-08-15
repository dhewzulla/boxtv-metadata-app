package uk.co.boxnetwork.mule.transformers.soundmouse;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.mail.handlers.message_rfc822;

import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.MediaCommand;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.util.GenericUtilities;

public class EpisodeIdToSoundMouseHeader   extends AbstractMessageTransformer{
	static final protected Logger logger=LoggerFactory.getLogger(EpisodeIdToSoundMouseHeader.class);
	@Autowired
	MetadataService metadataService;
	
	@Autowired
	MetadataMaintainanceService metadataMaintainanceService;
	
	@Autowired
	AppConfig appConfig;

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		
		Object obj=message.getPayload();
		if(obj instanceof MediaCommand){
			return processMediaCommand((MediaCommand)obj,message);
		}		
		return obj;
	}
	private Object processMediaCommand(MediaCommand command,MuleMessage message){
		if(GenericUtilities.DELIVER_SOUND_MOUSE_HEADER_FILE.equals(command.getCommand())){
			try{
				String soudnmouseFile=metadataService.getSoundMouseHeaderFile(command.getEpisodeid());
				message.setInvocationProperty("mediaCommand",command );
				return soudnmouseFile;			
				
			}
			catch(Exception e){
				logger.error(e+ "while getting the soundmouse header file",e);
				return command;
			}
		}
		else
			return command;
	}
	

}
