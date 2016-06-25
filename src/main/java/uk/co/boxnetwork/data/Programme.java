package uk.co.boxnetwork.data;

import uk.co.boxnetwork.model.ProgrammeContentType;

public class Programme {
	private Long id;	
	private String title;
	private String synopsis;
	private ProgrammeContentType contentType;
	public Programme(uk.co.boxnetwork.model.Programme prg){
		id=prg.getId();
		title=prg.getTitle();
		synopsis=prg.getSynopsis();
		contentType=prg.getContentType();		
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
	
	
}
