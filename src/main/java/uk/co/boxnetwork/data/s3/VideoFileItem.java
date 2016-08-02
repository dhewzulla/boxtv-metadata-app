package uk.co.boxnetwork.data.s3;

import java.util.Date;

public class VideoFileItem {
	private  String file;    
    private  String episodeTitle;
    private  Long episodeId;
    private Date lastModifidDate;
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
	public String getEpisodeTitle() {
		return episodeTitle;
	}
	public void setEpisodeTitle(String episodeTitle) {
		this.episodeTitle = episodeTitle;
	}
	public Long getEpisodeId() {
		return episodeId;
	}
	public void setEpisodeId(Long episodeId) {
		this.episodeId = episodeId;
	}
	public Date getLastModifidDate() {
		return lastModifidDate;
	}
	public void setLastModifidDate(Date lastModifidDate) {
		this.lastModifidDate = lastModifidDate;
	}
	
    
}
