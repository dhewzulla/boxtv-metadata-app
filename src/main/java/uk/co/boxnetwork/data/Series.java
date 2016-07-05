package uk.co.boxnetwork.data;

import java.util.Date;

public class Series {
    private Long id;
	private String assetId;
	private String primaryId;
	private String name;
	private String contractNumber;		
	private Programme programme;
	private Date lastModifiedAt;
	private Date createdAt;
	
	public Series(uk.co.boxnetwork.model.Series series) {
		super();
		this.id = series.getId();
		this.assetId = series.getAssetId();
		this.primaryId = series.getPrimaryId();
		this.name = series.getName();
		this.contractNumber = series.getContractNumber();
		this.programme = new Programme(series.getProgramme());
		this.lastModifiedAt=series.getLastModifiedAt();
		this.createdAt=series.getCreatedAt();
	}
	public void update(uk.co.boxnetwork.model.Series series){		
		series.setAssetId(this.assetId);
		series.setPrimaryId(this.primaryId);
		series.setName(this.name);
		series.setContractNumber(this.contractNumber);	
	}
	public Series(){
		
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public String getPrimaryId() {
		return primaryId;
	}
	public void setPrimaryId(String primaryId) {
		this.primaryId = primaryId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	public Programme getProgramme() {
		return programme;
	}
	public void setProgramme(Programme programme) {
		this.programme = programme;
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
	
  
}
