package uk.co.boxnetwork.data;

public class AppConfig {
 private int recordLimit;
 
 private String imagetemplateurl;
 
 private Boolean brightcoveStatus;
  
 

public Boolean getBrightcoveStatus() {
	return brightcoveStatus;
}

public void setBrightcoveStatus(Boolean brightcoveStatus) {
	this.brightcoveStatus = brightcoveStatus;
}

public int getRecordLimit() {
	return recordLimit;
}

public void setRecordLimit(int recordLimit) {
	this.recordLimit = recordLimit;
}


public String getImagetemplateurl() {
	return imagetemplateurl;
}

public void setImagetemplateurl(String imagetemplateurl) {
	this.imagetemplateurl = imagetemplateurl;
}


 
}
