package uk.co.boxnetwork.data;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BCVideoData {
	
		  private String id;
		  private String account_id;
		  private String ad_keys;
		  private boolean complete;
		  private String created_at;
		  private String[] cue_points;
		  private String[] custom_fields;
		  private String description;
		  private String digital_master_id;
		  private int duration;
		  private String economics;
		  private String folder_id;
		  private String geo;
		  private String[] images;
		  private String link;
		  private String long_description;
		  private String name;
		  private String original_filename;
		  private String published_at;
		  private String reference_id;
		  private String schedule;
		  private String sharing;
		  
		  private String state;
		  private String[] tags;
		  private String[] text_tracks;
		  private String updated_at;
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
		public boolean isComplete() {
			return complete;
		}
		public void setComplete(boolean complete) {
			this.complete = complete;
		}
		public String getCreated_at() {
			return created_at;
		}
		public void setCreated_at(String created_at) {
			this.created_at = created_at;
		}
		public String[] getCue_points() {
			return cue_points;
		}
		public void setCue_points(String[] cue_points) {
			this.cue_points = cue_points;
		}
		public String[] getCustom_fields() {
			return custom_fields;
		}
		public void setCustom_fields(String[] custom_fields) {
			this.custom_fields = custom_fields;
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
		public int getDuration() {
			return duration;
		}
		public void setDuration(int duration) {
			this.duration = duration;
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
		public String getGeo() {
			return geo;
		}
		public void setGeo(String geo) {
			this.geo = geo;
		}
		public String[] getImages() {
			return images;
		}
		public void setImages(String[] images) {
			this.images = images;
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
		public String getSchedule() {
			return schedule;
		}
		public void setSchedule(String schedule) {
			this.schedule = schedule;
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
		@Override
		public String toString() {
			return "BCVideoData [id=" + id + ", account_id=" + account_id + ", ad_keys=" + ad_keys + ", complete="
					+ complete + ", created_at=" + created_at + ", cue_points=" + Arrays.toString(cue_points)
					+ ", custom_fields=" + Arrays.toString(custom_fields) + ", description=" + description
					+ ", digital_master_id=" + digital_master_id + ", duration=" + duration + ", economics=" + economics
					+ ", folder_id=" + folder_id + ", geo=" + geo + ", images=" + Arrays.toString(images) + ", link="
					+ link + ", long_description=" + long_description + ", name=" + name + ", original_filename="
					+ original_filename + ", published_at=" + published_at + ", reference_id=" + reference_id
					+ ", schedule=" + schedule + ", sharing=" + sharing + ", state=" + state + ", tags="
					+ Arrays.toString(tags) + ", text_tracks=" + Arrays.toString(text_tracks) + ", updated_at="
					+ updated_at + "]";
		}
		 
}
