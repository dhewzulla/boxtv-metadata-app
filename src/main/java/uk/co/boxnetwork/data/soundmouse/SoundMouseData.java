package uk.co.boxnetwork.data.soundmouse;

import java.util.Calendar;
import java.util.Date;

import uk.co.boxnetwork.data.bc.BCAnalyticData;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.util.GenericUtilities;

public class SoundMouseData {
	private  Date fromDate;
	private Date toDate;
	private String from;
	private String to;
	private Date now;
	private String smurfFilename;
	private String smurfFilePath;
	private String createdAt;
	private String reportStartDate;
	private String reportEndDate;
	private Integer mediaCount=0;
	private Long usageCount=0l;
	private Episode episode;	
	private BCAnalyticData analyticData;
	

	public SoundMouseData(){
		init();
	}
	public void init(){	
		Calendar fromCalendar=Calendar.getInstance();
		fromCalendar.set(Calendar.DAY_OF_MONTH, 1);
		fromCalendar.add(Calendar.MONTH,-1);
		this.fromDate=fromCalendar.getTime();	
	
		Calendar toCalendar=Calendar.getInstance();
	    toCalendar.set(Calendar.DAY_OF_MONTH, 1);
	    toCalendar.add(Calendar.DATE,-1);
	    this.toDate=toCalendar.getTime();	
	    this.now=new Date();
	    this.from=GenericUtilities.toStandardDateFormat(fromDate);
		this.to=GenericUtilities.toStandardDateFormat(toDate);
		this.smurfFilename="SMURF_BOX_CH4001_"+GenericUtilities.toSoundMouseSmurfFileFormat(now)+"_version00001.xml";
		this.smurfFilePath="/data/"+smurfFilename;
		this.createdAt=GenericUtilities.toUTCFormat(this.now);
		this.reportStartDate=GenericUtilities.toUTCFormat(fromDate);
		this.reportEndDate=GenericUtilities.toUTCFormat(toDate);
		
	}
	public boolean init(Episode episode,BCAnalyticData analyticData){
		this.episode = episode;
		this.episode.makeSoundMouseFriendy();
		this.analyticData = analyticData;
		this.usageCount+=analyticData.getVideo_view();
		if(GenericUtilities.shouldReportOnCuepoint(episode)){
			this.mediaCount+=this.episode.getCuePoints().size();
			return true;
		}
		else{
			this.mediaCount+=1;
			return false;
		}
	}
	
	
	public String toString(){
		return "report date range:["+from+","+to+"]";		
	}
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
	}
	public String getSmurfFilename() {
		return smurfFilename;
	}
	public void setSmurfFilename(String smurfFilename) {
		this.smurfFilename = smurfFilename;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public Date getNow() {
		return now;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public String getReportStartDate() {
		return reportStartDate;
	}
	public String getReportEndDate() {
		return reportEndDate;
	}
	public Integer getMediaCount() {
		return mediaCount;
	}
	public Episode getEpisode() {
		return episode;
	}
	
	public BCAnalyticData getAnalyticData() {
		return analyticData;
	}
	
	public Long getUsageCount() {
		return usageCount;
	}
	public String getSmurfFilePath() {
		return smurfFilePath;
	}
	
	
	
	
}
