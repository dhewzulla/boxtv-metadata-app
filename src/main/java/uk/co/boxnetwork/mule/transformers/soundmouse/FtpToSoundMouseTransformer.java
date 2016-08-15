package uk.co.boxnetwork.mule.transformers.soundmouse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.StreamUtils;

import uk.co.boxnetwork.model.MediaCommand;

import org.springframework.integration.support.MessageBuilder;
public class FtpToSoundMouseTransformer extends AbstractMessageTransformer{
	static final protected Logger logger=LoggerFactory.getLogger(FtpToSoundMouseTransformer.class);
	@Autowired
	MessageChannel ftpChannel; 
	
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		Object obj=message.getPayload();
		MediaCommand mediaCommand=(MediaCommand)message.getInvocationProperty("mediaCommand");		
		String filepath="/data/"+mediaCommand.getFilename();
		if(!(obj instanceof String)){
			return obj;
		}
		String bb=(String)obj;
		logger.info("Sending the following message to the ftp channel:"+bb);
		
		try {
			PrintWriter out = new PrintWriter(filepath);
			out.write(bb);
			out.close();
			File file=new File(filepath);
			Message<File> mess=MessageBuilder.withPayload(file).build();
			ftpChannel.send(mess);
			
		} catch (Exception e) {
			logger.error(e+"While sending to the ftp channel",e);
		}
		
		
		
		
		
		
		//Thread.sleep(2000);
		return bb;		
	}
	
}