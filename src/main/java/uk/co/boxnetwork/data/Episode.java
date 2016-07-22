package uk.co.boxnetwork.data;

import java.util.ArrayList;

import java.util.Collections;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;

import uk.co.boxnetwork.model.AdSuport;
import uk.co.boxnetwork.model.CertType;
import uk.co.boxnetwork.model.ComplianceInformation;
import uk.co.boxnetwork.model.ProgrammeContentType;






public class Episode {
	
    private Long id;

	private String  title;
	private String  name;
	
	
	
	private String assetId;
	
	
	private String programmeNumber;
	
	
		
	private Integer number;
	
	
	private String synopsis;
	
	
	
	private String materialId;
	
	
	private CertType certType;
	
	
	private String warningText;
	
	
	private String tags[];
	
	
	private AdSuport adsupport;
	
	
	private Date startDate;
	
	
	private Date endDate;
	
	
	
	
	private Series series;
	
	
	
	 
	
	private String brightcoveId;
	
	
	private String ingestProfile;
	
	
	private String ingestSource;
	
	
	private String txChannel;
	
	
	
	ProgrammeContentType contentType;
	
	
	private List<ScheduleEvent> scheduleEvents=new ArrayList<ScheduleEvent>();
	
	
	private Date lastModifiedAt;
	
	
	
	private Date createdAt;
	
	
	
	
	private Set<ComplianceInformation> ComplianceInformations;
	
	
	private List<CuePoint> cuePoints=new ArrayList<CuePoint>();
	
	
	private Double durationScheduled;
	

	 private String showType;
	
	 private String prAuk;
	 
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAssetId() {
		return assetId;
	}


	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}


	

	public Integer getNumber() {
		return number;
	}


	public void setNumber(Integer number) {
		this.number = number;
	}


	public String getSynopsis() {
		return synopsis;
	}


	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}


	public String getMaterialId() {
		return materialId;
	}


	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}


	public CertType getCertType() {
		return certType;
	}


	public void setCertType(CertType certType) {
		this.certType = certType;
	}


	public String getWarningText() {
		return warningText;
	}


	public void setWarningText(String warningText) {
		this.warningText = warningText;
	}


	

	public AdSuport getAdsupport() {
		return adsupport;
	}


	public void setAdsupport(AdSuport adsupport) {
		this.adsupport = adsupport;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public Series getSeries() {
		return series;
	}


	public void setSeries(Series series) {
		this.series = series;
	}


	

	public List<ScheduleEvent> getScheduleEvents() {
		return scheduleEvents;
	}


	public void setScheduleEvents(List<ScheduleEvent> scheduleEvents) {		
		this.scheduleEvents = scheduleEvents;
	}

   public String getBrightcoveId() {
		return brightcoveId;
	}


	public void setBrightcoveId(String brightcoveId) {
		this.brightcoveId = brightcoveId;
	}


public Episode(){
	   
   }
	public Episode(uk.co.boxnetwork.model.Episode episode,List<uk.co.boxnetwork.model.ScheduleEvent> scheduleEvents) {
		super();
		this.id = episode.getId();
		this.title = episode.getTitle();
		this.name = episode.getName();
		this.assetId = episode.getAssetId();
		this.programmeNumber = episode.getCtrPrg();
		this.number = episode.getNumber();
		this.synopsis = episode.getSynopsis();
		this.materialId = episode.getMaterialId();
		this.certType = episode.getCertType();
		this.warningText = episode.getWarningText();
		this.contentType=episode.getContentType();
		this.durationScheduled=episode.getDurationScheduled();
		this.showType=episode.getShowType();
		this.prAuk=episode.getPrAuk();
		
		if(episode.getTags()==null){
			this.tags =null;			
		}
		else{
			this.tags=episode.getTags().split(",");
			for(int i=0;i<this.tags.length;i++){
				this.tags[i]=this.tags[i].trim();				
			}
		}
		this.adsupport = episode.getAdsupport();
		this.startDate = episode.getStartDate();
		this.endDate = episode.getEndDate();
		this.brightcoveId=episode.getBrightcoveId();
		this.series = new Series(episode.getSeries());
		
		this.lastModifiedAt=episode.getLastModifiedAt();
		this.createdAt=episode.getCreatedAt();
		
		this.ingestProfile=episode.getIngestProfile();
		this.ingestSource=episode.getIngestSource();
		this.txChannel=episode.getTxChannel();
		
		this.setComplianceInformations(episode.getComplianceInformations());
		if(episode.getCuePoints()!=null){
			for(uk.co.boxnetwork.model.CuePoint cuep:episode.getCuePoints()){
				this.addCuePoint(new CuePoint(cuep));
			}
		}
		if(scheduleEvents!=null){
			for(uk.co.boxnetwork.model.ScheduleEvent evt:scheduleEvents){
				addScheduleEvent(new ScheduleEvent(evt));			
			}
		}
		
	}
	public void update(uk.co.boxnetwork.model.Episode episode) {		
		episode.setTitle(this.title);		
		episode.setName(this.name);
		episode.setAssetId(this.assetId);
		episode.setCtrPrg(this.programmeNumber);
		episode.setNumber(this.number);
		episode.setSynopsis(this.synopsis);
		episode.setMaterialId(this.materialId);
		episode.setContentType(this.contentType);
		episode.setDurationScheduled(this.durationScheduled);
		episode.setShowType(this.showType);
		episode.setPrAuk(this.prAuk);
		if("".equals(this.certType)){
			this.certType=null;
		}
		episode.setCertType(this.certType);
		episode.setWarningText(this.warningText);
		if(this.tags==null ||this.tags.length==0){
			episode.setTags(null);	
		}
		else{
			String v=this.tags[0].trim();
			List<String> added=new ArrayList<String>();
			added.add(v);			
			for(int i=1;i<this.tags.length;i++){
				this.tags[i]=this.tags[i].trim();
				if(added.contains(this.tags[i])){
					continue;					
				}
				v=v+", ";
				v=v+this.tags[i];		
			}
			episode.setTags(v);
		}
		
		if("".equals(this.adsupport)){
			this.adsupport=null;	
		}
		episode.setAdsupport(this.adsupport);	
		episode.setBrightcoveId(this.brightcoveId);
		
		episode.setIngestProfile(this.ingestProfile);
		episode.setIngestSource(this.ingestSource);	
		episode.setTxChannel(this.txChannel);		
	}


	  public void updateWhenReceivedByMaterialId(uk.co.boxnetwork.model.Episode episode) {
		
		episode.setTitle(this.title);		
		if(this.number!=null){
			episode.setNumber(this.number);
		}
		if(this.synopsis!=null){
			episode.setSynopsis(this.synopsis);
		}
		if(this.certType!=null){
			episode.setCertType(this.certType);
		}
		if(this.warningText!=null){
			episode.setWarningText(this.warningText);
		}
		if(this.adsupport!=null){
			episode.setAdsupport(this.adsupport);
		}
		if(this.durationScheduled!=null){
			episode.setDurationScheduled(this.durationScheduled);
		}
			
		if(this.brightcoveId != null){
			episode.setBrightcoveId(this.brightcoveId);
		}
		if(this.ingestProfile!=null){
			episode.setIngestProfile(this.ingestProfile);
		}
		if(this.ingestSource!=null){
			episode.setIngestSource(this.ingestSource);
		}
		if(this.txChannel!=null){
			episode.setTxChannel(this.txChannel);
		}
		if(this.getContentType()!=null){
			episode.setContentType(this.contentType);
		}

		if(this.getMaterialId()!=null){
			episode.setMaterialId(materialId);
		}
		if(this.showType!=null){
			episode.setShowType(this.showType);
		}
		if(this.prAuk!=null){
			episode.setPrAuk(this.prAuk);
		}
		
	}
	
	

	public String[] getTags() {
		return tags;
	}


	public void setTags(String[] tags) {
		this.tags = tags;
	}


	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}


	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}


	public Date getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	public Set<ComplianceInformation> getComplianceInformations() {
		return ComplianceInformations;
	}


	public void setComplianceInformations(Set<ComplianceInformation> complianceInformations) {
		ComplianceInformations = complianceInformations;
	}


	public String getIngestProfile() {
		return ingestProfile;
	}


	public void setIngestProfile(String ingestProfile) {
		this.ingestProfile = ingestProfile;
	}


	public String getIngestSource() {
		return ingestSource;
	}


	public void setIngestSource(String ingestSource) {
		this.ingestSource = ingestSource;
	}


	public String getTxChannel() {
		return txChannel;
	}


	public void setTxChannel(String txChannel) {
		this.txChannel = txChannel;
	}


	public List<CuePoint> getCuePoints() {
		return cuePoints;
	}


	public void setCuePoints(List<CuePoint> cuePoints) {
		this.cuePoints = cuePoints;
	}

  public void addCuePoint(CuePoint cuePoint){
	  this.cuePoints.add(cuePoint);
	  Collections.sort(this.cuePoints);
  }
 public void addScheduleEvent(ScheduleEvent evt){
	 this.scheduleEvents.add(evt);
 }


public String getProgrammeNumber() {
	return programmeNumber;
}


public void setProgrammeNumber(String programmeNumber) {
	this.programmeNumber = programmeNumber;
}


public ProgrammeContentType getContentType() {
	return contentType;
}


public void setContentType(ProgrammeContentType contentType) {
	this.contentType = contentType;
}


public Double getDurationScheduled() {
	return durationScheduled;
}


public void setDurationScheduled(Double durationScheduled) {
	this.durationScheduled = durationScheduled;
}


public String getShowType() {
	return showType;
}


public void setShowType(String showType) {
	this.showType = showType;
}


public String getPrAuk() {
	return prAuk;
}


public void setPrAuk(String prAuk) {
	this.prAuk = prAuk;
}


	
}