package uk.co.boxnetwork.data.s3;

import java.util.ArrayList;
import java.util.List;

public class VideoFilesLocation {
  private String baseUrl;
  private List<FileItem> files=new ArrayList<FileItem>();
  public String getBaseUrl() {
	return baseUrl;
  }
  public void setBaseUrl(String baseUrl) {
	this.baseUrl = baseUrl;
  }
  public List<FileItem> getFiles() {
	return files;
  }
  public void setFiles(List<FileItem> files) {
	this.files = files;
  }
  public String highestVersion(){
		if(files.size()==0){
			return null;
		}		
		return files.get(0).getFile();			
		
	} 
  
}
