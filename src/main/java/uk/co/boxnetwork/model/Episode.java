package uk.co.boxnetwork.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
	
	
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "programme_id", nullable = true )
	private Programme programme;
	
	
	
	@Column(name="primary_id")
	private String primaryId;

	
	@Column(name="brightcove_id")
	private String brightcoveId;
	
	
	@Column(name="last_modified_at")
	private Date lastModifiedAt;
	
	
	@Column(name="created_at")
	private Date createdAt;
	
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "compliance_information_id", nullable = true )
	private ComplianceInformation ComplianceInformation;
	
	
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
	
	public Programme getProgramme() {
		return programme;
	}

	public void setProgramme(Programme programme) {
		this.programme = programme;
		if(series!=null){
			series.setProgramme(programme);
		}
	}
	

	public String getBrightcoveId() {
		return brightcoveId;
	}

	public void setBrightcoveId(String brightcoveId) {
		this.brightcoveId = brightcoveId;
	}

	
	public void merge(Episode episode){
		if(GenericUtilities.isNotValidTitle(this.title)){
			this.title=episode.getTitle();
		}
		if(GenericUtilities.isNotValidTitle(this.name)){
			this.name=episode.getName();
		}
		if(GenericUtilities.isNotAValidId(this.assetId)){
			this.assetId=episode.getAssetId();
		}
		if(GenericUtilities.isNotValidCrid(this.ctrPrg)){
			this.ctrPrg=episode.getCtrPrg();
		}
		if(GenericUtilities.isNotValidCrid(this.primaryId)){
			this.primaryId=episode.getPrimaryId();
		}
		if(this.programme==null){
			this.programme=episode.getProgramme();
		}
	}

	public ComplianceInformation getComplianceInformation() {
		return ComplianceInformation;
	}

	public void setComplianceInformation(ComplianceInformation complianceInformation) {
		ComplianceInformation = complianceInformation;
	}
	
		
}
