package uk.co.boxnetwork.data.bc;

import uk.co.boxnetwork.model.Episode;

public class BCGeo {
  private String[] countries;
  private Boolean exclude_countries=false;
  private Boolean restricted=true;
  public BCGeo(){
	  
  }
  public BCGeo(Episode episode){
	  countries=episode.getGeoAllowedCountries().split(",");
	  for(int i=0;i<countries.length;i++){
		  countries[i]=countries[i].trim();		  
	  }	  
  }
  public String[] getCountries() {
		return countries;
	}
	public void setCountries(String[] countries) {
		this.countries = countries;
	}
	public Boolean getExclude_countries() {
		return exclude_countries;
	}
	public void setExclude_countries(Boolean exclude_countries) {
		this.exclude_countries = exclude_countries;
	}
	public Boolean getRestricted() {
		return restricted;
	}
	public void setRestricted(Boolean restricted) {
		this.restricted = restricted;
	}
  
}
