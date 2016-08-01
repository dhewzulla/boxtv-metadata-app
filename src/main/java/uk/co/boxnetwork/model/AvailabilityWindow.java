package uk.co.boxnetwork.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;



@Entity(name="availability_window")
public class AvailabilityWindow implements Comparable<AvailabilityWindow>{
	
	@Id
	@GeneratedValue
    private Long id;
 
	private Date start;
	private Date end;

	@ManyToOne
    @JoinColumn(name="episode_id") 
    private Episode episode;

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
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public Episode getEpisode() {
	return episode;
}
public void setEpisode(Episode episode) {
	this.episode = episode;
}
@Override
public int compareTo(AvailabilityWindow o) {
	return (int)(this.getStart().getTime()-o.getStart().getTime());
}
 
	
}
