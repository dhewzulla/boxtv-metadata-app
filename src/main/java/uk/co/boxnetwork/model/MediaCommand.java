package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="media_command")
public class MediaCommand {
	public static String DELIVER_SOUND_MOUSE_HEADER_FILE="deliver_soundmouse_header_file";
	public static String DELIVER_SOUND_MOUSE_SMURF_FILE="deliver_soundmouse_smurf_file";
	@Id
	@GeneratedValue
    private Long id;

	
	  private String command;
	  
	  @Column(name="episode_id")
	  private Long episodeid;
	  
	  private String filename;
	  
	  
	  private String filepath;
	  
	
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

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	@Override
	public String toString() {
		return "MediaCommand [id=" + id + ", command=" + command + ", episodeid=" + episodeid + ", filename=" + filename
				+ "]";
	}
	
  
}
