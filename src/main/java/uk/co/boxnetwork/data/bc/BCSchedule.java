package uk.co.boxnetwork.data.bc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BCSchedule {
	 private String starts_at;
	 private String ends_at;
	public BCSchedule(){
		
	}
	public BCSchedule(Date fromDate, Date toDate){
		DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		starts_at=m_ISO8601Local.format(fromDate);
		ends_at=m_ISO8601Local.format(toDate);
	}
	public String getStarts_at() {
		return starts_at;
	}
	public void setStarts_at(String starts_at) {
		this.starts_at = starts_at;
	}
	public String getEnds_at() {
		return ends_at;
	}
	public void setEnds_at(String ends_at) {
		this.ends_at = ends_at;
	}
	 
}
