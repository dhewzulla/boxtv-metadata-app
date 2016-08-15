package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="media_command")
public class MediaCommand {
	
	@Id
	@GeneratedValue
    private Long id;

	
	  private String command;
	  
	  @Column(name="episode_id")
	  private Long episodeid;
	  
	  private String filename;
	
	  public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}

	public Long getEpisodeid() {
		return episodeid;
	}

	public void setEpisodeid(Long episodeid) {
		this.episodeid = episodeid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "MediaCommand [id=" + id + ", command=" + command + ", episodeid=" + episodeid + ", filename=" + filename
				+ "]";
	}
	
  
}
