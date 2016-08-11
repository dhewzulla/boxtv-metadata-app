package uk.co.boxnetwork.data.bc;

public class AddImageRequestParam {
	private String token;
	private BCImage image;
	private String file_checksum;
	private String video_id;
	private String video_reference_id;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public BCImage getImage() {
		return image;
	}
	public void setImage(BCImage image) {
		this.image = image;
	}
	public String getFile_checksum() {
		return file_checksum;
	}
	public void setFile_checksum(String file_checksum) {
		this.file_checksum = file_checksum;
	}
	public String getVideo_id() {
		return video_id;
	}
	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}
	public String getVideo_reference_id() {
		return video_reference_id;
	}
	public void setVideo_reference_id(String video_reference_id) {
		this.video_reference_id = video_reference_id;
	}
	
}
