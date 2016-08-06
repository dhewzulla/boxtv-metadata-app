package uk.co.boxnetwork.mule.transformers.s3;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import uk.co.boxnetwork.components.S3BucketService;
import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;

public class BoxVideoTransformer extends BoxRestTransformer{
	
	@Autowired
	private S3BucketService s3uckerService;
	
	
	protected Object processGET(MuleMessage message, String outputEncoding){
		logger.info("boxvideo.get request is received");
		Map<String, String> queryprarams=message.getInboundProperty("http.query.params");
		String prefix=null;
		if(queryprarams!=null && queryprarams.get("prefix")!=null){
			prefix=queryprarams.get("prefix").trim();			
		}
		return s3uckerService.listVideoFileItem(prefix);		
	}
	protected Object processPOST(MuleMessage message, String outputEncoding){
		Set<String> attachementnames=message.getInboundAttachmentNames();
				
		for(String attachementname:attachementnames){
			logger.info("reciving::::"+attachementname);
			DataHandler dataHandler=message.getInboundAttachment(attachementname);
			
			InputStream in;
			try {
				in = dataHandler.getInputStream();
				OutputStream out=new FileOutputStream("/data/uploaded.mp4");
				StreamUtils.copy(in, out);
				out.close();				
			} catch (IOException e) {
				logger.error(e+ " whilte recevimg the upload",e);
			}
			
			break;
		}
		logger.info("*******Completed***");
		return message.getPayload();		
	}
	
	
}