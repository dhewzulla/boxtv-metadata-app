package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="programme")
public class Programme {
	@Id
	@GeneratedValue
    private Long id;

	private String  title;
	
	private String synopsis;
	
	@Column(name="content_type")
	ProgrammeContentType contentType;

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
