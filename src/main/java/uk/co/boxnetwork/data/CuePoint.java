package uk.co.boxnetwork.data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.co.boxnetwork.model.Episode;

public class CuePoint implements Comparable<CuePoint>{
	
	private String name;
	
	private String type;
	
	private Double time;
	
	private String metadata;
	
	private String code;
	
	private Boolean force_stop;
	public CuePoint(){
		
	}
    public CuePoint(uk.co.boxnetwork.model.CuePoint cuePoint){
    	this.name=cuePoint.getName();
    	this.type=cuePoint.getType();
    	this.time=cuePoint.getTime();
    	this.metadata=cuePoint.getMetadata();
    	this.code=cuePoint.getCode();
    }
    public void update(uk.co.boxnetwork.model.CuePoint cuePoint){
    	cuePoint.setName(this.name);
    	cuePoint.setType(this.type);
    	cuePoint.setTime(this.time);
    	cuePoint.setMetadata(this.metadata);
    	cuePoint.setCode(this.code);
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getForce_stop() {
		return force_stop;
	}

	public void setForce_stop(Boolean force_stop) {
		this.force_stop = force_stop;
	}
	@Override
	public int compareTo(CuePoint o) {
		return (int)(this.time-o.getTime());
	}
	
	
	
		
}
