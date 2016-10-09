package uk.co.boxnetwork.components;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;


import uk.co.boxnetwork.data.s3.FileItem;

import uk.co.boxnetwork.data.s3.VideoFileItem;
import uk.co.boxnetwork.data.s3.VideoFileList;
import uk.co.boxnetwork.data.s3.MediaFilesLocation;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class S3BucketService {
	static final protected Logger logger=LoggerFactory.getLogger(S3BucketService.class);
	
	
	@Autowired
	private AppConfig appConfig;	

	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	private  AWSCredentials getAWSCredentials(){			   
	    return  new ProfileCredentialsProvider().getCredentials();
	    
	}
	public  AmazonS3 getAmazonS3(){
		AWSCredentials credentials=getAWSCredentials();
		AmazonS3 s3 = new AmazonS3Client(credentials);
	    Region r = Region.getRegion(Regions.EU_WEST_1);
	    s3.setRegion(r);
	    return s3;	    
	}
	public List<FileItem> listFiles(String bucketname, String prefix){
		AmazonS3 s3=getAmazonS3();		
		ListObjectsRequest request=new ListObjectsRequest().withBucketName(bucketname);
		if(prefix!=null){
			request.withPrefix(prefix);			
		}		    
		
		ObjectListing objectListing = s3.listObjects(request);
		
		
		List<FileItem> ret=new ArrayList<FileItem>(); 
		do{
		        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
		            String key=objectSummary.getKey();
		            if(key.endsWith("/")){
		            	continue;
		            	
		            }                            
		            FileItem itm=new FileItem();
		            itm.setFile(key);
		            itm.setLastModifiedDate(objectSummary.getLastModified());
		            objectSummary.getLastModified();
		            ret.add(itm);                    	
		        }
		        objectListing=s3.listNextBatchOfObjects(objectListing);
		}while(objectListing.isTruncated());
        return ret;
	}
	public MediaFilesLocation listFilesInVideoBucket(String prefix){
		MediaFilesLocation videoFilesLocations=new MediaFilesLocation();
		videoFilesLocations.setBaseUrl(appConfig.getS3videoURL());
		videoFilesLocations.setFiles(listFiles(appConfig.getVideoBucket(),prefix));
		logger.info("******number of s3 file for prefix=["+prefix+"]:"+videoFilesLocations.getFiles().size());
		
		return videoFilesLocations;
	}
	public void deleteImagesInImageBucket(String keyName){
		if(keyName==null|| keyName.trim().length()<=1){
			throw new RuntimeException("refuxed to do the delte operation on the s3 bucket:"+keyName);
		}
		logger.info("deleting the image in the ImageBucket:"+keyName);
		AmazonS3 s3=getAmazonS3();		
		s3.deleteObject(new DeleteObjectRequest(appConfig.getImageBucket(), keyName));
	}
	public void deletePublicImage(String keyName){
		 deleteImagesInImageBucket(appConfig.getImagePublicFolder()+"/"+keyName);
	}
	public void deleteMasterImage(String keyName){
		 deleteImagesInImageBucket(appConfig.getImageMasterFolder()+"/"+keyName);
	}
	public List<FileItem> listGenereratedImages(String prefix){		
		String path=appConfig.getImagePublicFolder();
		if(prefix!=null){
			path=path+"/"+prefix;
		}		
		List<FileItem>  files=listFiles(appConfig.getImageBucket(),path);
		return files;
	}
	
	public MediaFilesLocation listMasterImagesInImagesBucket(String prefix){
		MediaFilesLocation videoFilesLocations=new MediaFilesLocation();
		videoFilesLocations.setBaseUrl(appConfig.getS3imagesURL());
		String path=appConfig.getImageMasterFolder();
		if(prefix!=null){
			path=path+"/"+prefix;
		}
		List<FileItem>  files=listFiles(appConfig.getImageBucket(),path);
		videoFilesLocations.setFiles(files);
		if(files.size()>0){			
			for(FileItem item:files){
				if(item.getFile().length() > ( appConfig.getImageMasterFolder().length()+1) ){
					item.setFile(item.getFile().substring(appConfig.getImageMasterFolder().length()+1));
				}				
			}
			
		}
		logger.info("******number of s3 file for prefix=["+prefix+"]:"+videoFilesLocations.getFiles().size());		
		return videoFilesLocations;
	}
	
	public MediaFilesLocation uploadVideoFile(String filepath, String destFilename){
		MediaFilesLocation videoFileLocation=listFilesInVideoBucket(destFilename);
		if(videoFileLocation.getFiles().size()>0){
			throw new RuntimeException("The file already exist on the video buccket:"+videoFileLocation.getFiles().get(0));
		}
		AmazonS3 s3Client=getAmazonS3();
		File file=new File(filepath);
		s3Client.putObject(new PutObjectRequest(appConfig.getVideoBucket(), destFilename, file));
		videoFileLocation.addFilename(destFilename);
		return videoFileLocation;		
	}
	public MediaFilesLocation uploadMasterImageFile(String filepath, String destFilename){
		MediaFilesLocation imageFileLocation=listMasterImagesInImagesBucket(destFilename);
		if(imageFileLocation.getFiles().size()>0){
			throw new RuntimeException("The file already exist on the image buccket:"+imageFileLocation.getFiles().get(0));
		}
		AmazonS3 s3Client=getAmazonS3();
		File file=new File(filepath);
		
		s3Client.putObject(new PutObjectRequest(appConfig.getImageBucket(), appConfig.getImageMasterFolder()+"/"+destFilename, file));
		imageFileLocation.addFilename(destFilename);
		return imageFileLocation;		
	}
	public VideoFileList listVideoFileItem(String prefix){
		MediaFilesLocation videoFileLocation=listFilesInVideoBucket(prefix);
		List<VideoFileItem> videos=new ArrayList<VideoFileItem>();
		
		
		if(videoFileLocation.getFiles()!=null && videoFileLocation.getFiles().size()>0){
			for(FileItem fitem:videoFileLocation.getFiles()){
				VideoFileItem vitem=new VideoFileItem();
				vitem.setFile(fitem.getFile());	
				vitem.setLastModifidDate(fitem.getLastModifiedDate());
				String materialId=GenericUtilities.fileNameToMaterialID(fitem.getFile());
				List<Episode> matchedEpisodes=boxMetadataRepository.findEpisodesByMatId(materialId+"%");
				if(matchedEpisodes.size()>0){
					vitem.setEpisodeTitle(matchedEpisodes.get(0).getTitle());
					vitem.setEpisodeId(matchedEpisodes.get(0).getId());
					Double scheduledDuration=matchedEpisodes.get(0).getDurationScheduled();
					Double uploadedDuration=matchedEpisodes.get(0).getDurationUploaded();
					if(scheduledDuration!=null && uploadedDuration!=null){
						Double errorValue=(uploadedDuration-scheduledDuration);
						vitem.setDurationError(errorValue.longValue());
					}
				}
				
				
				videos.add(vitem);
			}
		}
		VideoFileList videoFileList=new VideoFileList();
		videoFileList.setBaseUrl(videoFileLocation.getBaseUrl());
		videoFileList.setFiles(videos);
		return videoFileList;
	}
	
	public MediaFilesLocation listMasterImageItem(String prefix){		
		return listMasterImagesInImagesBucket(prefix);		
	}
	
	public String getFullVideoURL(String fileName){
		return appConfig.getS3videoURL()+"/"+fileName;
	}
	public String getMasterImageFullURL(String fileName){
		return appConfig.getS3imagesURL()+"/"+appConfig.getImageMasterFolder()+"/"+fileName;		
	}
	
	
	public String generatedPresignedURL(String url, int expiredInSeconds){
		if(url==null){
			return null;
		}
		url=url.trim();
		if(url.length()==0){
			return null;
		}
		String baseURL=appConfig.getS3videoURL();
		
		
		if(!url.startsWith(baseURL)){
			throw new IllegalArgumentException("the url cannot be signed");
		}
		String filename=url.substring(baseURL.length());
		if(filename.startsWith("/")){
			filename=filename.substring(1);
		}
		logger.info("The video file to sign:"+filename);
		java.util.Date expiration = new java.util.Date();
		long milliSeconds = expiration.getTime();
		milliSeconds += 1000 * expiredInSeconds; // Add 1 hour.
		expiration.setTime(milliSeconds);
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(appConfig.getVideoBucket(), filename);
		generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
		generatePresignedUrlRequest.setExpiration(expiration);
		AmazonS3 s3=getAmazonS3();
		URL signedURL= s3.generatePresignedUrl(generatePresignedUrlRequest);
		return signedURL.toString();
	}

}
