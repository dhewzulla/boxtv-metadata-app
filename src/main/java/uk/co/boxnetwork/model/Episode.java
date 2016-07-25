package uk.co.boxnetwork.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.util.GenericUtilities;



@Entity(name="episode")
public class Episode {
   
	@Id
	@GeneratedValue
    private Long id;

	private String  title;
	private String  name;
	
	
	@Column(name="asset_id")
	private String assetId;
	
	@Column(name="ctr_prg")
	private String ctrPrg;
	
	
	
		
	private Integer number;
	
	
	private String synopsis;
	
	@Column(name="material_id")
	private String materialId;
	
	@Column(name="cert_type")
	private CertType certType;
	
	@Column(name="warning_text")
	private String warningText;
	
	
	private String tags;
	
	@Column(name="ad_support")
	private AdSuport adsupport;
	
	@Column(name="start_date")
	private Date startDate;
	
	@Column(name="end_date")
	private Date endDate;
	
	
	
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "series_id", nullable = true )
	private Series series;
	
	
	
	
	
	@Column(name="primary_id")
	private String primaryId;

	
	@Column(name="brightcove_id")
	private String brightcoveId;
	
	@Column(name="ingest_profile")
	private String ingestProfile;
	
	@Column(name="ingest_source")
	private String ingestSource;
	
	@Column(name="tx_channel")
	private String txChannel;
	
	
	@Column(name="content_type")
	ProgrammeContentType contentType;
	
	
	
	@Column(name="last_modified_at")
	private Date lastModifiedAt;
	
	
	@Column(name="created_at")
	private Date createdAt;
	
	@Column(name="duration_scheduled")
	private Double durationScheduled;
	
	@Column(name="show_type")
	private String showType;
	
	@Column(name="pr_auk")
	private String prAuk;
	
	
	
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinTable
	(
			name="episode_complaince",
			joinColumns={ @JoinColumn(name="episode_id", referencedColumnName="id") },
		    inverseJoinColumns={ @JoinColumn(name="compliance_information_id", referencedColumnName="id") }
	)
	@JoinColumn( name = "compliance_information_id", nullable = true )
	private Set<ComplianceInformation> complianceInformations;
	
	
	@OneToMany(mappedBy="episode", fetch=FetchType.EAGER)	
	private Set<CuePoint> cuePoints;
	
	
	@OneToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "status_id", nullable = true )
	private EpisodeStatus episodeStatus;
	
	
	
	public EpisodeStatus getEpisodeStatus() {
		return episodeStatus;
	}

	public void setEpisodeStatus(EpisodeStatus episodeStatus) {
		this.episodeStatus = episodeStatus;
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

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
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

	

	public String getPrimaryId() {
		return primaryId;
	}

	public void setPrimaryId(String primaryId) {
		this.primaryId = primaryId;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getCtrPrg() {
		return ctrPrg;
	}

	public void setCtrPrg(String ctrPrg) {
		this.ctrPrg = ctrPrg;
	}
	
		

	public String getBrightcoveId() {
		return brightcoveId;
	}

	public void setBrightcoveId(String brightcoveId) {
		this.brightcoveId = brightcoveId;
	}

	     
    public Set<ComplianceInformation> getComplianceInformations() {
		return complianceInformations;
	}

	public void setComplianceInformations(Set<ComplianceInformation> complianceInformations) {
		this.complianceInformations = complianceInformations;
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

	public void adjustBeforeSave(){
		if(getMaterialId()==null){
			setMaterialId(getCtrPrg());
		}
    	if(complianceInformations!=null){
    		for(ComplianceInformation complianceInformation:complianceInformations){
    			complianceInformation.adjustBeforeSave(this);
    		}    		
    	}
    	if(series!=null){
    		series.adjustBeforeSave(this);
    	}
    	if(ingestProfile==null){
    		ingestProfile="high-resolution";
    	}
    }
	public String calculateSourceVideoFilePrefix(){		
			 String materialID=null;
			 if(getMaterialId()!=null && getMaterialId().trim().length()>0){
				 materialID=getMaterialId();
			 }
			 if(materialID==null){
				 materialID=getCtrPrg();		 
			 }
			 if(materialID==null){
				 return null;
			 }
			 materialID=materialID.trim();
			 if(materialID.length()==0){
				 return null;
			 }
			 return GenericUtilities.materialIdToFileName(materialID);
	}

		
   public void addCuePoint(CuePoint cuepoint){
	   this.cuePoints.add(cuepoint);
   }
   public void rempveCuePoint(CuePoint cuepoint){
	   this.cuePoints.remove(cuepoint);
   }

   public void clearCuePoints(){
	   this.cuePoints.clear();
   }

public Set<CuePoint> getCuePoints() {
	return cuePoints;
}

public void setCuePoints(Set<CuePoint> cuePoints) {
	this.cuePoints = cuePoints;
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


	public void applyVideoStatus( VideoStatus videoStatus){
		if(episodeStatus==null){
			this.episodeStatus=new EpisodeStatus();
		}
		this.episodeStatus.setVideoStatus(videoStatus);	
	}

   
}
