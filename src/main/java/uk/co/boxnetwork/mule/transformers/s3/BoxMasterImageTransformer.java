package uk.co.boxnetwork.mule.transformers.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import uk.co.boxnetwork.components.S3BucketService;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;

public class BoxMasterImageTransformer extends BoxRestTransformer{
	
	@Autowired
	private S3BucketService s3uckerService;
	
	
	protected Object processGET(MuleMessage message, String outputEncoding){
		logger.info("boximage.get request is received");
		Map<String, String> queryprarams=message.getInboundProperty("http.query.params");
		String prefix=null;
		if(queryprarams!=null && queryprarams.get("prefix")!=null){
			prefix=queryprarams.get("prefix").trim();			
		}
		return s3uckerService.listMasterImageItem(prefix);		
	}
	protected Object processPOST(MuleMessage message, String outputEncoding){
		Set<String> attachementnames=message.getInboundAttachmentNames();
				
		for(String attachementname:attachementnames){
			logger.info("receiving::::"+attachementname+" for images");
			DataHandler dataHandler=message.getInboundAttachment(attachementname);
			String filepath="/data/"+attachementname;
			InputStream in;
			OutputStream out=null;
			try {
				in = dataHandler.getInputStream();
				out=new FileOutputStream(filepath);
				StreamUtils.copy(in, out);
				out.close();
				Object obj=s3uckerService.uploadMasterImageFile(filepath, attachementname);
				File fp=new File(filepath);
				fp.delete();
				return obj;
				
			} catch (IOException e) {
				logger.error(e+ " whilte recevimg the upload",e);
			}
			finally{
				try{
					if(out!=null){
						out.close();
					}					
				}
				catch(Exception e){
					logger.error(e+ " whilte close",e);
				}
				File file=new File(filepath);
				file.delete();
			}
		
			
			break;
		}
		logger.info("*******Completed***");
		return message.getPayload();		
	}
		
	
}