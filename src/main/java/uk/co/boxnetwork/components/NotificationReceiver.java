package uk.co.boxnetwork.components;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;

import uk.co.boxnetwork.data.s3.S3Configuration;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class NotificationReceiver {	
	private static final Logger logger=LoggerFactory.getLogger(NotificationReceiver.class);
	
	@Autowired
	private S3Configuration s3Configuration;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private MetadataService metadataService; 
	
	@Autowired
	private CommandServices commandService; 
	
	
	@Autowired
	S3BucketService s3Service;
	
	private String getBucketName(Map<String, Object> messageMap){
		return GenericUtilities.getValueInMap(messageMap,"Records[0].s3.bucket.name");
	}
	private String getObjectKey(Map<String, Object> messageMap){
		return GenericUtilities.getValueInMap(messageMap,"Records[0].s3.object.key");
	}
	private String getEventName(Map<String, Object> messageMap){
		return GenericUtilities.getValueInMap(messageMap,"Records[0].eventName");
	}
	
	
	public void notify(Map<String, Object> messageMap){		
		if(!"Notification".equals(messageMap.get("Type") ) ){
			logger.warn(" Type is not notification:"+messageMap.get("Type"));
			return;
		}
		String messageInJson=(String)messageMap.get("Message");
		if(messageInJson==null){
			logger.warn("Message in the notification is null");
			return;			
		}
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);				
		try {
			Map<String, Object> msg = (Map<String, Object>)objectMapper.readValue(messageInJson, Map.class);
			notifyMeessage(msg);
		} catch (IOException e) {
			logger.error("error in json when parsing the Message in the notification:"+messageInJson, e);
		}
	}
	
	private void notifyMeessage(Map<String, Object> messageMap){		
		String key=getObjectKey(messageMap);
		if(key==null){
			logger.warn("Key is null in the notification message");
			return;			
		}
		if(key.endsWith("/")){
			logger.warn("Folder notification is ignored");
			return;
		}
		String eventName=getEventName(messageMap);
		if(eventName==null){
			logger.warn("EventName is null in the notification message");
			return;
		}
		String bucket=getBucketName(messageMap);
		if(bucket ==null){
			logger.warn("bucket is null in the notification message");
			return;
		}
		
		if(eventName.contains("ObjectCreated")){
			onFileUpload(bucket, key);
		}
		else if(eventName.contains("ObjectRemoved")){
			onFileDeleted(bucket, key);
		}
		else{
			logger.info("Ignoring event:"+eventName);
		}
		
		
	}
	private void onFileUpload(String bucketName, String file){
		if(s3Configuration.getImageBucket().equals(bucketName)){
			onImageBucketUpload(file);
		}
		else if(s3Configuration.getVideoBucket().equals(bucketName)){
			onVideoBucketUpload(file);
		}
	}
	
	private void onFileDeleted(String bucketName, String file){
		if(s3Configuration.getImageBucket().equals(bucketName)){
			onImageBucketFileDeleted(file);
		}
		else if(s3Configuration.getVideoBucket().equals(bucketName)){
			onVideoBucketFileDeleted(file);
		}
	}
	
	
	private void onImageBucketUpload(String file){
		logger.info("Uploaded to the image bucket"+ file);
		if(file.startsWith(s3Configuration.getImageMasterFolder())){
			
			if(appConfig.getConvertImage()==null || (!appConfig.getConvertImage())){
				logger.info("**** will not convert images because of the config");			
			}
			else{				
						    try{
					    		commandService.convertFromMasterImage(file);
					    }
					    catch(Throwable e){
							logger.error(e+" while setting the image in the metadata on notification"+file,e );
						}
				
			}
			
			try{
				String imageFile=file.substring(s3Configuration.getImageMasterFolder().length()+1);
				metadataService.notifyMasterImageUploaded(imageFile);
			}
			catch(Throwable e){
				logger.error(e+" while setting the image in the metadata on notification"+file,e );
			}
		}
	}
   private void onVideoBucketUpload(String file){
	   logger.info("Uploaded to the video bucket:"+file);
	   metadataService.bindVideoFile(file);
	   
	}
	
	private void onImageBucketFileDeleted(String file){
		logger.info("image file is deleted:"+file);
		if(appConfig.getConvertImage()==null || (!appConfig.getConvertImage())){
			logger.info("**** ignoring delete because of the config");
			return;
		}
		
		if(file.startsWith(s3Configuration.getImageMasterFolder())){			
			try{
				String imageFile=file.substring(s3Configuration.getImageMasterFolder().length()+1);
				metadataService.notifyMasterImageDelete(imageFile);
			}
			catch(Throwable e){
				logger.error(e+" while setting the image in the metadata on notification"+file,e );
			}
		}
				
	}
	private void onVideoBucketFileDeleted(String file){
		logger.info("video file is deleted:"+file);		
	}
}
