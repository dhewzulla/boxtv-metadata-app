package uk.co.boxnetwork.data.s3;

public class S3Configuration {
	private String s3videoURL;
	private String videoBucket;
	private String imageBucket;
    private String imageMasterFolder;
    private String imagePublicFolder;
    private String s3imagesURL;
    
	
	

	public String getImagePublicFolder() {
		return imagePublicFolder;
	}

	public void setImagePublicFolder(String imagePublicFolder) {
		this.imagePublicFolder = imagePublicFolder;
	}

	public String getImageMasterFolder() {
		return imageMasterFolder;
	}
	

	public void setImageMasterFolder(String imageMasterFolder) {
		this.imageMasterFolder = imageMasterFolder;
	}

	public String getS3videoURL() {
		return s3videoURL;
	}

	public void setS3videoURL(String s3videoURL) {
		this.s3videoURL = s3videoURL;
	}

	public String getVideoBucket() {
		return videoBucket;
	}

	public void setVideoBucket(String videoBucket) {
		this.videoBucket = videoBucket;
	}

	public String getImageBucket() {
		return imageBucket;
	}

	public void setImageBucket(String imageBucket) {
		this.imageBucket = imageBucket;
	}

	public String getS3imagesURL() {
		return s3imagesURL;
	}

	public void setS3imagesURL(String s3imagesURL) {
		this.s3imagesURL = s3imagesURL;
	}

		
	
}
