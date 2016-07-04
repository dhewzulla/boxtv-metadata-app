package uk.co.boxnetwork.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import uk.co.boxnetwork.model.AdSuport;
import uk.co.boxnetwork.model.CertType;
import uk.co.boxnetwork.model.ScheduleEvent;



public class Episode {
	
    private Long id;

	private String  title;
	private String  name;
	
	
	
	private String assetId;
	
	
	private String ctrPrg;
	
	
		
	private Integer number;
	
	
	private String synopsis;
	
	
	
	private String materialId;
	
	
	private CertType certType;
	
	
	private String warningText;
	
	
	private String tags[];
	
	
	private AdSuport adsupport;
	
	
	private Date startDate;
	
	
	private Date endDate;
	
	
	
	
	private Series series;
	
	private Programme programme;
	
	 
	
	private String brightcoveId;
	
	private List<ScheduleEvent> scheduleEvents=new ArrayList<ScheduleEvent>();
	
	

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAssetId() {
		return assetId;
	}


	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}


	public String getCtrPrg() {
		return ctrPrg;
	}


	public void setCtrPrg(String ctrPrg) {
		this.ctrPrg = ctrPrg;
	}


	public Integer getNumber() {
		return number;
	}


	public void setNumber(Integer number) {
		this.number = number;
	}


	public String getSynopsis() {
		return synopsis;
	}


	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}


	public String getMaterialId() {
		return materialId;
	}


	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}


	public CertType getCertType() {
		return certType;
	}


	public void setCertType(CertType certType) {
		this.certType = certType;
	}


	public String getWarningText() {
		return warningText;
	}


	public void setWarningText(String warningText) {
		this.warningText = warningText;
	}


	

	public AdSuport getAdsupport() {
		return adsupport;
	}


	public void setAdsupport(AdSuport adsupport) {
		this.adsupport = adsupport;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public Series getSeries() {
		return series;
	}


	public void setSeries(Series series) {
		this.series = series;
	}


	public Programme getProgramme() {
		return programme;
	}


	public void setProgramme(Programme programme) {
		this.programme = programme;
	}


	public List<ScheduleEvent> getScheduleEvents() {
		return scheduleEvents;
	}


	public void setScheduleEvents(List<ScheduleEvent> scheduleEvents) {
		this.scheduleEvents = scheduleEvents;
	}

   public String getBrightcoveId() {
		return brightcoveId;
	}


	public void setBrightcoveId(String brightcoveId) {
		this.brightcoveId = brightcoveId;
	}


public Episode(){
	   
   }
	public Episode(uk.co.boxnetwork.model.Episode episode) {
		super();
		this.id = episode.getId();
		this.title = episode.getTitle();
		this.name = episode.getName();
		this.assetId = episode.getAssetId();
		this.ctrPrg = episode.getCtrPrg();
		this.number = episode.getNumber();
		this.synopsis = episode.getSynopsis();
		this.materialId = episode.getMaterialId();
		this.certType = episode.getCertType();
		this.warningText = episode.getWarningText();
		
		if(episode.getTags()==null){
			this.tags =null;			
		}
		else{
			this.tags=episode.getTags().split(",");
			for(int i=0;i<this.tags.length;i++){
				this.tags[i]=this.tags[i].trim();				
			}
		}
		this.adsupport = episode.getAdsupport();
		this.startDate = episode.getStartDate();
		this.endDate = episode.getEndDate();
		this.brightcoveId=episode.getBrightcoveId();
		this.series = new Series(episode.getSeries());
		this.programme = new Programme(episode.getProgramme());
		
	}
	public void update(uk.co.boxnetwork.model.Episode episode) {
		
		episode.setTitle(this.title);		
		episode.setName(this.name);
		episode.setAssetId(this.assetId);
		episode.setCtrPrg(this.ctrPrg);
		episode.setNumber(this.number);
		episode.setSynopsis(this.synopsis);
		episode.setMaterialId(this.materialId);
		if("".equals(this.certType)){
			this.certType=null;
		}
		episode.setCertType(this.certType);
		episode.setWarningText(this.warningText);
		if(this.tags==null ||this.tags.length==0){
			episode.setTags(null);	
		}
		else{
			String v=this.tags[0].trim();
			List<String> added=new ArrayList<String>();
			added.add(v);			
			for(int i=1;i<this.tags.length;i++){
				this.tags[i]=this.tags[i].trim();
				if(added.contains(this.tags[i])){
					continue;					
				}
				v=v+", ";
				v=v+this.tags[i];		
			}
			episode.setTags(v);
		}
		
		if("".equals(this.adsupport)){
			this.adsupport=null;	
		}
		episode.setAdsupport(this.adsupport);	
		episode.setBrightcoveId(this.brightcoveId);
	}


	public String[] getTags() {
		return tags;
	}


	public void setTags(String[] tags) {
		this.tags = tags;
	}
	
	
}
