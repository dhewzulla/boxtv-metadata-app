package uk.co.boxnetwork.data.bc;

import uk.co.boxnetwork.data.AppConfig;
import uk.co.boxnetwork.model.Episode;

public class BCImages {
	private BCImage thumbnail;
	private BCImage poster;
	public BCImages(){
		
	}
    public BCImages(String imagename,Episode episode, AppConfig appConfig){
    	thumbnail=new BCImage(imagename,episode,appConfig, 320,180);
    	poster=new BCImage(imagename,episode,appConfig, 1920,1080);
	}
	public BCImage getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(BCImage thumbnail) {
		this.thumbnail = thumbnail;
	}
	public BCImage getPoster() {
		return poster;
	}
	public void setPoster(BCImage poster) {
		this.poster = poster;
	}
		
}
