package uk.co.boxnetwork.data;

import java.util.Date;



public class AvailabilityWindow {
	
    private Long id;
 
	private Date start;
	private Date end;

public AvailabilityWindow(uk.co.boxnetwork.model.AvailabilityWindow availability){
	
	this.id=availability.getId();
	this.start=availability.getStart();
	this.end=availability.getEnd();
	
}
public void update(uk.co.boxnetwork.model.AvailabilityWindow availability){
	availability.setStart(this.start);
	availability.setEnd(end);

	
}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
    
}
