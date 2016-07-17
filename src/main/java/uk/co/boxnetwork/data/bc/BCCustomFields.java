package uk.co.boxnetwork.data.bc;

import uk.co.boxnetwork.model.Episode;

public class BCCustomFields {
  private String contenttype;
  private String txchannel;
  private String seriesnumber;
  private String seriestitle;
  
  private String certificationtype;
  private String programmetitle;
  private String programmesynopsis;
  private String warningtext;
  private String episodenumber;
  private String drm;
  
  
  
  
  public BCCustomFields(){
	  
  }
  public BCCustomFields(Episode episode){
	  if(episode.getSeries()!=null){
		  this.seriestitle= episode.getSeries().getName();		  
	  }
	  if(episode.getCertType()!=null){
		  certificationtype=episode.getCertType().getBcName();
	  }
	  if(episode.getProgramme()!=null){
		  programmetitle= episode.getProgramme().getTitle();
		  programmesynopsis=episode.getProgramme().getSynopsis();
	  }
	  warningtext=episode.getWarningText();	  		 
	  if(episode.getNumber()!=null){
		  episodenumber=String.valueOf(episode.getNumber());
		  if(episodenumber.length()==1){
			  episodenumber="0"+episodenumber;
		  }
	  }
	  
  }
  public String getContenttype() {
	return contenttype;
}
public void setContenttype(String contenttype) {
	this.contenttype = contenttype;
}
public String getTxchannel() {
	return txchannel;
}
public void setTxchannel(String txchannel) {
	this.txchannel = txchannel;
}
public String getSeriesnumber() {
	return seriesnumber;
}
public void setSeriesnumber(String seriesnumber) {
	this.seriesnumber = seriesnumber;
}
public String getSeriestitle() {
	return seriestitle;
}
public void setSeriestitle(String seriestitle) {
	this.seriestitle = seriestitle;
}
public String getCertificationtype() {
	return certificationtype;
}
public void setCertificationtype(String certificationtype) {
	this.certificationtype = certificationtype;
}
public String getProgrammetitle() {
	return programmetitle;
}
public void setProgrammetitle(String programmetitle) {
	this.programmetitle = programmetitle;
}
public String getProgrammesynopsis() {
	return programmesynopsis;
}
public void setProgrammesynopsis(String programmesynopsis) {
	this.programmesynopsis = programmesynopsis;
}
public String getWarningtext() {
	return warningtext;
}
public void setWarningtext(String warningtext) {
	this.warningtext = warningtext;
}
public String getEpisodenumber() {
	return episodenumber;
}
public void setEpisodenumber(String episodenumber) {
	this.episodenumber = episodenumber;
}
public String getDrm() {
	return drm;
}
public void setDrm(String drm) {
	this.drm = drm;
}

 
}
