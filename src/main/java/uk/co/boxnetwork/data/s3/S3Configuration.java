package uk.co.boxnetwork.data.s3;

public class S3Configuration {
	private String s3videoURL;
	private String videoBucket;
	private String imageBucket;
	private String imageFolder;
	
	

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

	public String getImageFolder() {
		return imageFolder;
	}

	public void setImageFolder(String imageFolder) {
		this.imageFolder = imageFolder;
	}
	
	
}
