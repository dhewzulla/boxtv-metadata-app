package uk.co.boxnetwork.mule.transformers.s3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.S3BucketService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.data.s3.VideoFilesLocation;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;

public class S3VideoBucketTransformer extends BoxRestTransformer{
	@Autowired
	private S3BucketService s3uckerService;
	
	private List<FileItem> filter(MuleMessage message, String outputEncoding,List<FileItem> files){
		Map<String, String> queryprarams=message.getInboundProperty("http.query.params");
		if(queryprarams==null){
			return files;
		}
		String filepart=queryprarams.get("file");
		if(filepart==null||filepart.length()==0){
			return files;
		}
		if(files.size()==0){
			return files;
		}
	   	List<FileItem> filteredFiles=new ArrayList<FileItem>();
    	for(FileItem file:files){
    		if(file.getFile().contains(filepart)){
    			filteredFiles.add(file);
    		}
    	}
    	return filteredFiles;
		
		
	}
	protected Object processGET(MuleMessage message, String outputEncoding){
		logger.info("s3 list request is received");
		VideoFilesLocation files=s3uckerService.listFilesInVideoBucket(null);
		return 	filter(message, outputEncoding,files.getFiles());
	
	}
}
