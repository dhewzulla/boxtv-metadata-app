package uk.co.boxnetwork.data.bc;

import uk.co.boxnetwork.data.AppConfig;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.util.GenericUtilities;

public class AddImageRequest {
	
	public static String calculateImageURL(Episode episode){
		 String imagename=episode.getImageURL();
		 if(imagename==null && episode.getSeries()!=null){
			 imagename=episode.getSeries().getImageURL();
		 }
		 if(imagename==null && episode.getSeries()!=null && episode.getSeries().getSeriesGroup()!=null){
			 imagename=episode.getSeries().getSeriesGroup().getImageURL();
		 }
		 
		 return imagename;
		 
	 }
	
	public static  AddImageRequest createAddTVideoRequest(String imageurl, String token, String videoid, String imagename, String imageType){
		AddImageRequest ret=new AddImageRequest();
		ret.setMethod("add_image");
		ret.params=new AddImageRequestParam();
		ret.params.setToken(token);
		ret.params.setVideo_id(videoid);
		BCImage image=new BCImage();
		image.setRemoteUrl(imageurl);
		image.setDisplayName(imagename);
		image.setType(imageType);
		ret.params.setImage(image);
		return ret;		
	}
	
	public static  AddImageRequest createAddTVideoStillImageRequest(BCConfiguration bcConfig, AppConfig appConfig, Episode episode){
		String imagename=calculateImageURL(episode);
		if(imagename==null){
			return null;
		}
		if(episode.getBrightcoveId()==null){
			return null;
		}
		String imageURL=GenericUtilities.getImageWithSize(appConfig, imagename, 1920, 1080);
		return createAddTVideoRequest(imageURL,bcConfig.getApiToken(),episode.getBrightcoveId(),"PostImage","VIDEO_STILL");
				
	}
	public static  AddImageRequest createAddTThumbnailImageRequest(BCConfiguration bcConfig, AppConfig appConfig, Episode episode){
		String imagename=calculateImageURL(episode);
		if(imagename==null){
			return null;
		}
		if(episode.getBrightcoveId()==null){
			return null;
		}
		String imageURL=GenericUtilities.getImageWithSize(appConfig, imagename, 320, 180);
		return createAddTVideoRequest(imageURL,bcConfig.getApiToken(),episode.getBrightcoveId(),"ThumnailImage","THUMBNAIL");				
	}
	private String method;
	
	private AddImageRequestParam params;
	public String getMethod() {
		return method;
	}
	 
	public void setMethod(String method) {
		this.method = method;
	}
	public AddImageRequestParam getParams() {
		return params;
	}
	public void setParams(AddImageRequestParam params) {
		this.params = params;
	}
	
}
