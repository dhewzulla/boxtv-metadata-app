package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

public void exportConfig(AppConfig config){
     config.setBrightcoveStatus(this.brightcoveStatus);
     config.setImagetemplateurl(this.imagetemplateurl);
     config.setRecordLimit(this.recordLimit);	
     config.setId(config.getId());
     config.setVersion(this.version);
     config.setConvertImage(this.convertImage);
}

public void importConfig(AppConfig config){	
    this.brightcoveStatus=config.getBrightcoveStatus();
    this.imagetemplateurl=config.getImagetemplateurl();
    this.recordLimit=config.getRecordLimit();
    this.version=config.getVersion();
    this.convertImage=config.getConvertImage();
    
}

public Integer getVersion() {
	return version;
}

public void setVersion(Integer version) {
	this.version = version;
}

 
}