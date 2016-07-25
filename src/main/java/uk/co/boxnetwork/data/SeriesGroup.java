package uk.co.boxnetwork.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.boxnetwork.model.ProgrammeContentType;

public class SeriesGroup {
	private Long id;	
	private String title;
	private String synopsis;
	
	private Date lastModifiedAt;
	private Date createdAt;
	private List<Series> series=new ArrayList<Series>();
	
	public SeriesGroup(uk.co.boxnetwork.model.SeriesGroup prg){
		id=prg.getId();
		title=prg.getTitle();
		synopsis=prg.getSynopsis();		
		lastModifiedAt=prg.getLastModifiedAt();
		createdAt=prg.getCreatedAt();
	}
	public void update(uk.co.boxnetwork.model.SeriesGroup seriesgroup){
		seriesgroup.setTitle(this.title);
		seriesgroup.setSynopsis(this.synopsis);
	}
	public SeriesGroup(){
		
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
	public String getSynopsis() {
		return synopsis;
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
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
	public List<Series> getSeries() {
		return series;
	}
	public void setSeries(List<Series> series) {
		this.series = series;
	}
   public void addSeries(Series series){
	   this.series.add(series);
	   
   }
	
}
