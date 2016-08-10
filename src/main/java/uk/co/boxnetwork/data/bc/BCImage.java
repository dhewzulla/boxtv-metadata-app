package uk.co.boxnetwork.data.bc;

import uk.co.boxnetwork.data.AppConfig;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.util.GenericUtilities;

public class BCImage{	
	private String asset_id;
	private Boolean remote;
	private String src;
	private BCImageSource[] sources;
	
	public BCImage(){
		
	}
	public BCImage(String imagename,Episode episode, AppConfig appConfig, int width, int height){
		src=GenericUtilities.getImageWithSize(appConfig, imagename, width, height);
	}
	
	public String getAsset_id() {
		return asset_id;
	}
	public void setAsset_id(String asset_id) {
		this.asset_id = asset_id;
	}
	public Boolean getRemote() {
		return remote;
	}
	public void setRemote(Boolean remote) {
		this.remote = remote;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public BCImageSource[] getSources() {
		return sources;
	}
	public void setSources(BCImageSource[] sources) {
		this.sources = sources;
	}
	
	
}
