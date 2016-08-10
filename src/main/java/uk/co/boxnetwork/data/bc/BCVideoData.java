package uk.co.boxnetwork.data.bc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;

import uk.co.boxnetwork.data.AppConfig;
import uk.co.boxnetwork.model.AdSuport;
import uk.co.boxnetwork.model.AvailabilityWindow;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.ScheduleEvent;



@JsonIgnoreProperties(ignoreUnknown = true)
public class BCVideoData {
	
		  private String id;
		  private String account_id;
		  private String ad_keys;
		  private Boolean complete;
		  private String created_at;
		  private BCCuePoint[] cue_points;		  
		  private BCCustomFields custom_fields;
		  private String delivery_type;
		  private String description;
		  private String digital_master_id;
		  private Integer duration;
		  private String economics;
		  private String folder_id;
		  private BCGeo geo;
		  private BCImages images;
		  private String link;
		  private String long_description;
		  private String name;
		  private String original_filename;
		  private String published_at;
		  private String reference_id;
		  private BCSchedule schedule;
		  private String sharing;
		  
		  private String state="INACTIVE";
		  private String[] tags;
		  private String[] text_tracks;
		  private String updated_at;
		 public BCVideoData(){
			 
		 }
		 
		 private void buildRefereceId(Episode episode){
			 
			 if(episode.getMaterialId()!=null){
				 String parts[]=episode.getMaterialId().split("/");
				 StringBuilder builder=new StringBuilder();
				 builder.append("V");				 
				 int counter=0;
				 for(String p:parts){
					 builder.append("_");
					 builder.append(p);
					 counter++;					 
				 }
				 while(counter<4){
					 builder.append("_001");					 
					 counter++;
				 }
				 this.reference_id=builder.toString();
			 }
			 else {
				 this.reference_id="box/media/episode/"+episode.getId();				 
			 }
		 }
		 
		 public BCVideoData(Episode episode,List<ScheduleEvent> schedules,AppConfig appConfig){			 	
			 this.name=episode.getTitle();		
			 if(episode.getTags()!=null){
				 this.tags=episode.getTags().split(",");
			 }
			 
			 buildRefereceId(episode);
			 this.description=episode.getSynopsis();
			 
			 if(episode.getAdsupport()==AdSuport.FREE){
				 this.economics="Free";
			 }
			 else if(episode.getAdsupport()==AdSuport.AD_SUPPORTED){
				 this.economics="AD_SUPPORTED";				 
			 }
			custom_fields=new BCCustomFields(episode);
			populateCuePoints(episode);
			if(episode.getGeoAllowedCountries()!=null && episode.getGeoAllowedCountries().trim().length()>0){
				geo=new BCGeo(episode);				
			}	
//			String imagename=calculateImageURL(episode);
//			if(imagename!=null){
//				images=new BCImages(imagename,episode,appConfig);
//				
//				
//			}
			calculateSchedule(episode, schedules);
			
			
		 }
		 public String calculateImageURL(Episode episode){
			 String imagename=episode.getImageURL();
			 if(imagename==null && episode.getSeries()!=null){
				 imagename=episode.getSeries().getImageURL();
			 }
			 if(imagename==null && episode.getSeries()!=null && episode.getSeries().getSeriesGroup()!=null){
				 imagename=episode.getSeries().getSeriesGroup().getImageURL();
			 }
			 return imagename;
			 
		 }
		 public void populateCuePoints(Episode episode){
			 if(episode.getCuePoints()==null|| episode.getCuePoints().size()==0){
				 return;
			 }
			 if(episode.getCuePoints()!=null){
				    List<uk.co.boxnetwork.data.CuePoint> cuepoints=new ArrayList<uk.co.boxnetwork.data.CuePoint>();				    
					for(uk.co.boxnetwork.model.CuePoint cuep:episode.getCuePoints()){
						cuepoints.add(new uk.co.boxnetwork.data.CuePoint(cuep));						
					}
					Collections.sort(cuepoints);
				    this.cue_points=new BCCuePoint[cuepoints.size()];
				    for(int i=0;i<cuepoints.size();i++){
				    	this.cue_points[i]=new BCCuePoint(cuepoints.get(i),episode.getNumberOfAdsPerBreak());				    	
				    }
				}
		 }
		 
		 
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getAccount_id() {
			return account_id;
		}
		public void setAccount_id(String account_id) {
			this.account_id = account_id;
		}
		public String getAd_keys() {
			return ad_keys;
		}
		public void setAd_keys(String ad_keys) {
			this.ad_keys = ad_keys;
		}
		
		public BCCuePoint[] getCue_points() {
			return cue_points;
		}

		public void setCue_points(BCCuePoint[] cue_points) {
			this.cue_points = cue_points;
		}

		
		public String getCreated_at() {
			return created_at;
		}
		public void setCreated_at(String created_at) {
			this.created_at = created_at;
		}
		
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getDigital_master_id() {
			return digital_master_id;
		}
		public void setDigital_master_id(String digital_master_id) {
			this.digital_master_id = digital_master_id;
		}
		
		public String getEconomics() {
			return economics;
		}
		public void setEconomics(String economics) {
			this.economics = economics;
		}
		public String getFolder_id() {
			return folder_id;
		}
		public void setFolder_id(String folder_id) {
			this.folder_id = folder_id;
		}
		
		
		public BCGeo getGeo() {
			return geo;
		}

		public void setGeo(BCGeo geo) {
			this.geo = geo;
		}

		public String getLink() {
			return link;
		}
		public void setLink(String link) {
			this.link = link;
		}
		public String getLong_description() {
			return long_description;
		}
		public void setLong_description(String long_description) {
			this.long_description = long_description;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getOriginal_filename() {
			return original_filename;
		}
		public void setOriginal_filename(String original_filename) {
			this.original_filename = original_filename;
		}
		public String getPublished_at() {
			return published_at;
		}
		public void setPublished_at(String published_at) {
			this.published_at = published_at;
		}
		public String getReference_id() {
			return reference_id;
		}
		public void setReference_id(String reference_id) {
			this.reference_id = reference_id;
		}
		
		public String getSharing() {
			return sharing;
		}
		public void setSharing(String sharing) {
			this.sharing = sharing;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String[] getTags() {
			return tags;
		}
		public void setTags(String[] tags) {
			this.tags = tags;
		}
		public String[] getText_tracks() {
			return text_tracks;
		}
		public void setText_tracks(String[] text_tracks) {
			this.text_tracks = text_tracks;
		}
		public String getUpdated_at() {
			return updated_at;
		}
		public void setUpdated_at(String updated_at) {
			this.updated_at = updated_at;
		}
		public BCCustomFields getCustom_fields() {
			return custom_fields;
		}
		public void setCustom_fields(BCCustomFields custom_fields) {
			this.custom_fields = custom_fields;
		}
		public BCImages getImages() {
			return images;
		}
		public void setImages(BCImages images) {
			this.images = images;
		}

		public Integer getDuration() {
			return duration;
		}

		public void setDuration(Integer duration) {
			this.duration = duration;
		}
		

		public BCSchedule getSchedule() {
			return schedule;
		}

		public void setSchedule(BCSchedule schedule) {
			this.schedule = schedule;
		}

		
		
		@Override
		public String toString() {
			return "BCVideoData [id=" + id + ", account_id=" + account_id + ", ad_keys=" + ad_keys + ", complete="
					+ complete + ", created_at=" + created_at + ", cue_points=" + Arrays.toString(cue_points)
					+ ", custom_fields=" + custom_fields + ", description=" + description + ", digital_master_id="
					+ digital_master_id + ", duration=" + duration + ", economics=" + economics + ", folder_id="
					+ folder_id + ", geo=" + geo + ", images=" + images + ", link=" + link + ", long_description="
					+ long_description + ", name=" + name + ", original_filename=" + original_filename
					+ ", published_at=" + published_at + ", reference_id=" + reference_id + ", schedule=" + schedule
					+ ", sharing=" + sharing + ", state=" + state + ", tags=" + Arrays.toString(tags) + ", text_tracks="
					+ Arrays.toString(text_tracks) + ", updated_at=" + updated_at + "]";
		}

		public void copyFrom(BCVideoData bcVideo){			  
			  
			  if(bcVideo.getCue_points()!=null){
				  this.cue_points=bcVideo.getCue_points();
			  }
			  this.description=bcVideo.getDescription();
			  this.economics=bcVideo.getEconomics();
			  this.long_description=bcVideo.getLong_description();
			  this.name=bcVideo.getName();
			  this.tags=bcVideo.getTags();
			  this.schedule=bcVideo.getSchedule();
			  this.custom_fields=bcVideo.getCustom_fields();	
			  if(bcVideo.getReference_id()!=null){
				  this.reference_id=bcVideo.getReference_id();
			  }
			 
		}
		public void calculateSchedule(Episode episode,List<ScheduleEvent> schedules){
			Set<AvailabilityWindow> availabilityWindows=episode.getAvailabilities();			
			Date today=new Date();
			AvailabilityWindow 	availabilityWindow=null;
			if(availabilityWindows!=null && availabilityWindows.size()>0){
					if(availabilityWindows.size()==1){
						availabilityWindow=availabilityWindows.iterator().next();
						schedule =new BCSchedule(new Date(availabilityWindow.getStart()),new Date(availabilityWindow.getEnd()));
						return;
					}
					else{
						List<AvailabilityWindow> sortedAvailabilityWindows=new ArrayList<AvailabilityWindow>(availabilityWindows);
						Collections.sort(sortedAvailabilityWindows);
						
						for(int i=0;i<sortedAvailabilityWindows.size();i++){
							availabilityWindow=sortedAvailabilityWindows.get(i);
							if(today.before(new Date(availabilityWindow.getEnd()))){																
								schedule =new BCSchedule(new Date(availabilityWindow.getStart()),new Date(availabilityWindow.getEnd()));
								return;									
																
							}							
						}
						schedule =new BCSchedule(new Date(availabilityWindow.getStart()),new Date(availabilityWindow.getEnd()));
						return;								
					}							
			}
		   if(schedules==null||schedules.size()==0){
				return;				
			}
			Date fromDate=schedules.get(0).getScheduleTimestamp();
			Date toDate=fromDate;
			
			for(int i=1;i<schedules.size();i++){
				Date d=schedules.get(i).getScheduleTimestamp();
				if(d.before(fromDate)){
					fromDate=d;
				}
				if(d.after(toDate)){
					toDate=d;
				}				
			}
			schedule =new BCSchedule(fromDate,toDate);			
		}

		public Boolean getComplete() {
			return complete;
		}

		public void setComplete(Boolean complete) {
			this.complete = complete;
		}

		public String getDelivery_type() {
			return delivery_type;
		}

		public void setDelivery_type(String delivery_type) {
			this.delivery_type = delivery_type;
		}
		
		 
}
