package uk.co.boxnetwork.mule.transformers.s3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

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
		return s3uckerService.listFilesInVideoBucket(prefix);
	}
	
}