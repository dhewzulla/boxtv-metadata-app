package uk.co.boxnetwork.components;

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
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import uk.co.boxnetwork.data.bc.BCConfiguration;
import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.data.s3.VideoFilesLocation;

@Service
public class S3BucketService {
	static final protected Logger logger=LoggerFactory.getLogger(S3BucketService.class);
	@Autowired
    private BCConfiguration configuration;
	
	
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
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            String key=objectSummary.getKey();
            if(key.endsWith("/")){
            	continue;
            	
            }                            
            FileItem itm=new FileItem();
            itm.setFile(key);
            ret.add(itm);                    	
        }
        return ret;
	}
	public VideoFilesLocation listFilesInVideoBucket(String prefix){
		VideoFilesLocation videoFilesLocations=new VideoFilesLocation();
		videoFilesLocations.setBaseUrl(configuration.getS3videoURL());
		videoFilesLocations.setFiles(listFiles(configuration.getVideoBucket(),prefix));
		return videoFilesLocations;
	}
	
	
	public String getFullVideoURL(String fileName){
		return configuration.getS3videoURL()+"/"+fileName;
	}
	public String generatedPresignedURL(String url, int expiredInSeconds){
		if(url==null){
			return null;
		}
		url=url.trim();
		if(url.length()==0){
			return null;
		}
		String baseURL=configuration.getS3videoURL();
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
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(configuration.getVideoBucket(), filename);
		generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
		generatePresignedUrlRequest.setExpiration(expiration);
		AmazonS3 s3=getAmazonS3();
		URL signedURL= s3.generatePresignedUrl(generatePresignedUrlRequest);
		return signedURL.toString();
	}

}
