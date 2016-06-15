package com.iterativesolution.video.metadata;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

	public ObjectFactory(){
		
	}
	public VideoData createVideoData(){
		return new VideoData();
	}
	
	
}
