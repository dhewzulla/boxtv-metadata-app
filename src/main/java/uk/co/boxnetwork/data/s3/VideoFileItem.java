package uk.co.boxnetwork.data.s3;

public class VideoFileItem {
	private  String file;
    private  String presignedURL;
    private  String episodeTitle;
    private  Long episodeId;
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getPresignedURL() {
		return presignedURL;
	}
	public void setPresignedURL(String presignedURL) {
		this.presignedURL = presignedURL;
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
	
    
}
