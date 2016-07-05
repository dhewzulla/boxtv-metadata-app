package uk.co.boxnetwork.data;

import java.util.Date;

import uk.co.boxnetwork.model.ProgrammeContentType;

public class Programme {
	private Long id;	
	private String title;
	private String synopsis;
	private ProgrammeContentType contentType;
	private Date lastModifiedAt;
	private Date createdAt;
	public Programme(uk.co.boxnetwork.model.Programme prg){
		id=prg.getId();
		title=prg.getTitle();
		synopsis=prg.getSynopsis();
		contentType=prg.getContentType();
		lastModifiedAt=prg.getLastModifiedAt();
		createdAt=prg.getCreatedAt();
	}
	public Programme(){
		
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
	public ProgrammeContentType getContentType() {
		return contentType;
	}
	public void setContentType(ProgrammeContentType contentType) {
		this.contentType = contentType;
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
