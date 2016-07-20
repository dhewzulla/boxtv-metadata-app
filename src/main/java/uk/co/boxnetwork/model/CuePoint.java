package uk.co.boxnetwork.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="cue_points")
public class CuePoint {
	@Id
	@GeneratedValue
    private Long id;
    
	private String name;
	
	private String type;
	
	private Double time;
	
	private String metadata;
	
	private String code;
	
	private Boolean force_stop;
	
	@ManyToOne
    @JoinColumn(name="episode_id") 
    private Episode episode;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getTime() {
		return time;
	}

	public void setTime(Double time) {
		this.time = time;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public Boolean getForce_stop() {
		return force_stop;
	}

	public void setForce_stop(Boolean force_stop) {
		this.force_stop = force_stop;
	}

	public Episode getEpisode() {
		return episode;
	}

	public void setEpisode(Episode episode) {
		this.episode = episode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	
	
	
}
