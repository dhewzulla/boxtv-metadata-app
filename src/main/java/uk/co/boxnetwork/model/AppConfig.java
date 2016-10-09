package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import uk.co.boxnetwork.util.GenericUtilities;

@Entity(name="app_config")
public class AppConfig {
	@Id
	@GeneratedValue
    private Long id;
	

	@Column(name="version")	
	private Integer version;
	 
	
 @Column(name="record_limit")	
 private Integer recordLimit;
 
 @Column(name="image_template_url")
 private String imagetemplateurl;
 
 @Column(name="brightcove_status")
 private Boolean brightcoveStatus;
  
 @Column(name="convert_image")
 private Boolean convertImage;
 
 
 @Column(name="send_update_to_soundmouse")
 private Boolean sendUpdateToSoundMouse;
 
 @Column(name="visibility_category")
 private String visibilityCategory;
 
 @Column(name="s3_video_url")
 private String s3videoURL;
 
 @Column(name="video_bucket")
private String videoBucket;
 
 @Column(name="image_bucket")
private String imageBucket;
 
 @Column(name="image_master_folder")
 private String imageMasterFolder;
 
 @Column(name="image_public_folder")
 private String imagePublicFolder;
 
 @Column(name="s3_images_url")
 private String s3imagesURL;
 
 
 
 
 

public Boolean getBrightcoveStatus() {
	return brightcoveStatus;
}

public void setBrightcoveStatus(Boolean brightcoveStatus) {
	this.brightcoveStatus = brightcoveStatus;
}

public Integer getRecordLimit() {
	return recordLimit;
}

public void setRecordLimit(Integer recordLimit) {
	this.recordLimit = recordLimit;
}


public String getImagetemplateurl() {
	return imagetemplateurl;
}

public void setImagetemplateurl(String imagetemplateurl) {
	this.imagetemplateurl = imagetemplateurl;
}

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public Boolean getConvertImage() {
	return convertImage;
}

public void setConvertImage(Boolean convertImage) {
	this.convertImage = convertImage;
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

public String getImageMasterFolder() {
	return imageMasterFolder;
}

public void setImageMasterFolder(String imageMasterFolder) {
	this.imageMasterFolder = imageMasterFolder;
}

public String getImagePublicFolder() {
	return imagePublicFolder;
}

public void setImagePublicFolder(String imagePublicFolder) {
	this.imagePublicFolder = imagePublicFolder;
}

public String getS3imagesURL() {
	return s3imagesURL;
}

public void setS3imagesURL(String s3imagesURL) {
	this.s3imagesURL = s3imagesURL;
}

public void exportConfig(AppConfig config){
     config.setBrightcoveStatus(this.brightcoveStatus);
     config.setImagetemplateurl(this.imagetemplateurl);
     config.setRecordLimit(this.recordLimit);	
     config.setId(config.getId());
     config.setVersion(this.version);
     config.setConvertImage(this.convertImage);
     config.setSendUpdateToSoundMouse(this.sendUpdateToSoundMouse);
     config.setVisibilityCategory(this.visibilityCategory);
     
     config.setS3videoURL(this.s3videoURL);
     config.setVideoBucket(this.videoBucket);
     config.setImageBucket(this.imageBucket);
     config.setImageMasterFolder(this.imageMasterFolder);
     config.setImagePublicFolder(this.imagePublicFolder);
     config.setS3imagesURL(this.s3imagesURL);
     
}

public void importConfig(AppConfig config){	
    this.brightcoveStatus=config.getBrightcoveStatus();
    this.imagetemplateurl=config.getImagetemplateurl();
    this.recordLimit=config.getRecordLimit();
    this.version=config.getVersion();
    this.convertImage=config.getConvertImage();
    this.sendUpdateToSoundMouse=config.getSendUpdateToSoundMouse();
    this.visibilityCategory=config.getVisibilityCategory();
    
    
    this.s3videoURL=config.getS3videoURL();
    this.videoBucket=config.getVideoBucket();
    this.imageBucket=config.getImageBucket();
    this.imageMasterFolder=config.getImageMasterFolder();
    this.imagePublicFolder=config.getImagePublicFolder();
    this.s3imagesURL=config.getS3imagesURL();    
}

public Integer getVersion() {
	return version;
}

public void setVersion(Integer version) {
	this.version = version;
}

public Boolean getSendUpdateToSoundMouse() {
	return sendUpdateToSoundMouse;
}

public void setSendUpdateToSoundMouse(Boolean sendUpdateToSoundMouse) {
	this.sendUpdateToSoundMouse = sendUpdateToSoundMouse;
}

public String getVisibilityCategory() {
	return visibilityCategory;
}

public void setVisibilityCategory(String visibilityCategory) {
	this.visibilityCategory = visibilityCategory;
}

 public boolean shouldSendSoundmouseHeaderFile(uk.co.boxnetwork.data.Episode episode){
	 if(sendUpdateToSoundMouse==null){
		 return false;
	 }
	 if(!sendUpdateToSoundMouse){
		 return false;
	 }	 
	 if(GenericUtilities.isNotAValidId(episode.getBrightcoveId())){
		 return false;
	 }	 
	 return true;
 }
}
