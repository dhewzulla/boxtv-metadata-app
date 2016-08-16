package uk.co.boxnetwork.data.bc;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class BCCuePoint {
	  private String id;
	  private String name;
	  private String type;
	  private Double time;
	  private String metadata;
	  private Boolean force_stop;
	  public BCCuePoint(){
		  
	  }
	  public BCCuePoint(uk.co.boxnetwork.data.CuePoint cuepoint, String numberOfBraks){
		  this.name=cuepoint.getName();
		  this.type=cuepoint.getType();
		  this.time=cuepoint.getTime();
		  this.force_stop=cuepoint.getForce_stop();
		  if(cuepoint.getMetadata()!=null && cuepoint.getMetadata().getNumberOfAds()!=null){			  
				this.metadata="{\"numberOfAds\":"+cuepoint.getMetadata().getNumberOfAds()+"}";				
		  }
		  else if(numberOfBraks!=null && numberOfBraks.trim().length()>0){
			  this.metadata="{\"numberOfAds\":"+numberOfBraks+"}";
		  }
		  
		  		  
	  }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getTime() {
		return time;
	}
	public void setTime(Double time) {
		this.time = time;
	}
	public String getMetadata() {
		return metadata;
	}
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	public Boolean getForce_stop() {
		return force_stop;
	}
	public void setForce_stop(Boolean force_stop) {
		this.force_stop = force_stop;
	}
	  
}
