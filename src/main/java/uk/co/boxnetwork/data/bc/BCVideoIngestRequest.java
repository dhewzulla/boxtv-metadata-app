package uk.co.boxnetwork.data.bc;

import uk.co.boxnetwork.data.FileIngestRequest;

public class BCVideoIngestRequest {
	
	private BCMasterVideo master;
	private String profile;
	private String callbacks[];
	public BCVideoIngestRequest(){
		
	}
	public BCVideoIngestRequest(FileIngestRequest ingestRequest, BCConfiguration bcConfiguration){
		master=new BCMasterVideo();
		master.setUrl(bcConfiguration.getS3videoURL()+"/"+ingestRequest.getFile());
		if(ingestRequest.getProfile()!=null){
			profile=ingestRequest.getProfile();			
		}
		else
			profile=bcConfiguration.getDefaltIngestProfile();
		callbacks=new String[1];
		callbacks[0]=bcConfiguration.getIngestCallback();
	}
	public BCMasterVideo getMaster() {
		return master;
	}
	public void setMaster(BCMasterVideo master) {
		this.master = master;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String[] getCallbacks() {
		return callbacks;
	}
	public void setCallbacks(String[] callbacks) {
		this.callbacks = callbacks;
	}
	
		
}
