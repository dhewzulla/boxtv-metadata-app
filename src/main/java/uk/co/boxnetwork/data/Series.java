package uk.co.boxnetwork.data;

import java.util.Date;
import java.util.List;

public class Series {
    private Long id;
	private String assetId;
	private String primaryId;
	private String name;
	private String contractNumber;		
	private SeriesGroup seriesGroup;
	private Date lastModifiedAt;
	private Date createdAt;
	private List<uk.co.boxnetwork.data.Episode> episodes;
	private String synopsis;
	
	
	public Series(uk.co.boxnetwork.model.Series series) {
		super();
		this.id = series.getId();
		this.assetId = series.getAssetId();
		this.primaryId = series.getPrimaryId();
		this.name = series.getName();
		this.contractNumber = series.getContractNumber();
		if(series.getSeriesGroup()!=null){
			this.seriesGroup = new SeriesGroup(series.getSeriesGroup());
		}
		this.lastModifiedAt=series.getLastModifiedAt();
		this.createdAt=series.getCreatedAt();
		this.synopsis=series.getSynopsis();		
	}
	public void update(uk.co.boxnetwork.model.Series series){		
		series.setAssetId(this.assetId);
		series.setPrimaryId(this.primaryId);
		series.setName(this.name);
		series.setContractNumber(this.contractNumber);	
		series.setSynopsis(this.synopsis);
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
	
	public SeriesGroup getSeriesGroup() {
		return seriesGroup;
	}
	public void setSeriesGroup(SeriesGroup seriesGroup) {
		this.seriesGroup = seriesGroup;
	}
	public String getSynopsis() {
		return synopsis;
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	public List<uk.co.boxnetwork.data.Episode> getEpisodes() {
		return episodes;
	}
	public void setEpisodes(List<uk.co.boxnetwork.data.Episode> episodes) {
		this.episodes = episodes;
	}
	
	
  
}
