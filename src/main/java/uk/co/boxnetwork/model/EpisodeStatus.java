package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="episode_status")
public class EpisodeStatus {
   
	@Id
	@GeneratedValue
    private Long id;
	
	@Column(name="metadata_status")
	private MetadataStatus  metadataStatus=MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER;
	
	@Column(name="video_status")
	private VideoStatus videoStatus=VideoStatus.MISSING_VIDEO;

	@Column(name="numberOfTranscodedFiles")
	private Integer numberOfTranscodedFiles=0;

	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public VideoStatus getVideoStatus() {
		return videoStatus;
	}

	public void setVideoStatus(VideoStatus videoStatus) {
		this.videoStatus = videoStatus;
	}

	public MetadataStatus getMetadataStatus() {
		return metadataStatus;
	}

	public void setMetadataStatus(MetadataStatus metadataStatus) {
		this.metadataStatus = metadataStatus;
	}

	public Integer getNumberOfTranscodedFiles() {
		return numberOfTranscodedFiles;
	}

	public void setNumberOfTranscodedFiles(Integer numberOfTranscodedFiles) {
		this.numberOfTranscodedFiles = numberOfTranscodedFiles;
	}
	
	
   
}
