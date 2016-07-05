package uk.co.boxnetwork.components;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.MediaTag;
import uk.co.boxnetwork.model.Programme;
import uk.co.boxnetwork.model.ProgrammeContentType;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.util.GenericUtilities;


@Repository
public class BoxMedataRepository {
	private static final Logger logger=LoggerFactory.getLogger(BoxMedataRepository.class);
      @Autowired	
	  private EntityManager entityManager;

	    
       public void persisEvent(ScheduleEvent newEvent){
    	   Date lastModifiedAt=new Date();
			newEvent.setLastModifiedAt(lastModifiedAt);
			newEvent.setCreatedAt(lastModifiedAt);
			entityManager.persist(newEvent);
       }
       public void mergeEvent(ScheduleEvent event){
    	   Date lastModifiedAt=new Date();
    	   event.setLastModifiedAt(lastModifiedAt);			
    	   entityManager.merge(event);
       }
       public void persisProgramme(Programme programme){
    	    Date lastModifiedAt=new Date();
   			programme.setLastModifiedAt(lastModifiedAt);
   			programme.setCreatedAt(lastModifiedAt);
   			entityManager.persist(programme);
       }
       public void persisEpisode(Episode episode){
    	   Date lastModifiedAt=new Date();
    	   episode.setLastModifiedAt(lastModifiedAt);
    	   episode.setCreatedAt(lastModifiedAt);
		   entityManager.persist(episode);
       }
       public void mergeEpisode(Episode episode){
    	   Date lastModifiedAt=new Date();
    	   episode.setLastModifiedAt(lastModifiedAt);    	   
		   entityManager.persist(episode);
       }
       public void persisSeries(Series series){
    	   Date lastModifiedAt=new Date();
    	   series.setLastModifiedAt(lastModifiedAt);
    	   series.setCreatedAt(lastModifiedAt);
		   entityManager.persist(series);
       }
       public void mergeSeries(Series series){
    	   Date lastModifiedAt=new Date();
    	   series.setLastModifiedAt(lastModifiedAt);    	   
		   entityManager.persist(series);
       }
       public void persisMediaTag(MediaTag mediaTag){
    	   Date lastModifiedAt=new Date();
    	   mediaTag.setCreatedAt(lastModifiedAt);    	   
		   entityManager.persist(mediaTag);
       }
       
       public void update(Series series){
		   mergeSeries(series);		   
	   }
       
	   public void createEvent(ScheduleEvent newEvent){		    		   
		    Programme programme=createProgrammeFromScheduleEvent(newEvent);
		    if(programme!=null){
		    	List<Programme> matchedProgrammes=findProgrammeByTitle(programme.getTitle());
		    	if(matchedProgrammes.size()==0){
		    		persisProgramme(programme);		    		
		    		newEvent.setProgramme(programme);
		    	}
		    	else{
		    		if(matchedProgrammes.size()>1){
		    			logger.warn("There are mot than matching programmes:title="+programme.getTitle());		    			
		    		}
		    		Programme existingProgramme=matchedProgrammes.get(0);		    		
		    		newEvent.setProgramme(existingProgramme);		    		
		    	}		    	
		    }		   		   
					    
			if(GenericUtilities.isNotAValidId(newEvent.getScheduleEventID())){
				newEvent.setEpisode(saveEpisode(newEvent.getEpisode()));
				persisEvent(newEvent);				
			}
			else{
				logger.info("querying the sechedule event:");
				List<ScheduleEvent> existingEvents=findScheduleEventByScheduleEventId(newEvent.getScheduleEventID());
				if(existingEvents.size()==0){
					newEvent.setEpisode(saveEpisode(newEvent.getEpisode()));
					persisEvent(newEvent);
					
				}
				else{
					if(existingEvents.size()>1){
						logger.warn("There are mot than matching events matching evenid="+newEvent.getScheduleEventID());
					}
					ScheduleEvent existingEvent=existingEvents.get(0);
					existingEvent.setEpisode(saveEpisode(newEvent.getEpisode()));
					existingEvent.merge(newEvent);
					mergeEvent(existingEvent);
				}
				
			}
					        		    				
	   }	
	   private Programme createProgrammeFromScheduleEvent(ScheduleEvent newEvent){
		   String programmeTitle=null;
		   
		   if(newEvent.getEpisode()!=null){
			   if(newEvent.getEpisode().getSeries()!=null){				   
					   if(!GenericUtilities.isNotValidTitle(newEvent.getEpisode().getSeries().getName())){						   
						   programmeTitle=newEvent.getEpisode().getSeries().getName();
					   }
			   }
		   }
		   if(programmeTitle==null){
				   if(GenericUtilities.isNotValidTitle(newEvent.getAssetName())){							   
					   programmeTitle=programmeTitle=newEvent.getGroupName();
				   }
				   else{
					   programmeTitle=newEvent.getAssetName();
				   }
		   }			   
	      if(GenericUtilities.isNotValidTitle(programmeTitle)){
				   return null;
		   }
		   Programme programme=new Programme();
		   programme.setTitle(programmeTitle);
           programme.setContentType(ProgrammeContentType.LIVE_TV);		
           return programme;
	   }
	   
	   private Episode createNewEpisode(Episode episode){
		    episode.setSeries(saveSeries(episode.getSeries()));		    
		    persisEpisode(episode);		    
		   	saveTags(episode.getTags());
		   	return episode;
	   }
	   
	   private Episode returnExistingEpisodeOrPersist(Episode episode,List<Episode> matchedEpisodes,String messageOnDuplication){
		   if(matchedEpisodes.size()==0){
			   return createNewEpisode(episode);			   
		   }
		   else{
			   if(matchedEpisodes.size()>1){
				   logger.warn("Marching the episodes more than once:"+messageOnDuplication);
			   }
			   Episode existingEpisode=matchedEpisodes.get(0);
			   			   
			   
			   existingEpisode.setSeries(saveSeries(episode.getSeries()));
			   update(existingEpisode);
			   return existingEpisode;
		   }		   
	   }
	   public Episode saveEpisode(Episode episode){
		   if(episode==null){
			   return null;			   
		   }
		   else{
			   if(GenericUtilities.isNotAValidId(episode.getPrimaryId())){
				   if(GenericUtilities.isNotValidCrid(episode.getCtrPrg())){
					   if(GenericUtilities.isNotValidTitle(episode.getTitle())){
						   if(GenericUtilities.isNotValidTitle(episode.getName())){
							   return createNewEpisode(episode);							      
						   }
						   else{
							   List<Episode> matchedEpisodes=findEpisodesByName(episode.getName());
							   return returnExistingEpisodeOrPersist(episode,matchedEpisodes,"title="+episode.getName());
						   }
						   
							
					   }
					   else{
						   List<Episode> matchedEpisodes=findEpisodesByTitle(episode.getTitle());
						   return returnExistingEpisodeOrPersist(episode,matchedEpisodes,"title="+episode.getTitle());
					   }
				   }
				   else{
					   List<Episode> matchedEpisodes=findEpisodesByCtrPrg(episode.getCtrPrg());
					   return returnExistingEpisodeOrPersist(episode,matchedEpisodes,"ctrprg="+episode.getCtrPrg());
				   }
			   }
			   else{
				
					   List<Episode> matchedEpisodes=findEpisodesByPrimaryId(episode.getPrimaryId());
					   return returnExistingEpisodeOrPersist(episode,matchedEpisodes,"primaryid="+episode.getPrimaryId());
			   }
			   
		   }
	   }
	   
	   
	   private Series returnExistingSeriesOrPersist(Series series,List<Series> matchedSeries,String messageOnDuplication){
		   if(matchedSeries.size()==0){
			   persisSeries(series);			   
			   
			   return series;
		   }
		   else{
			   if(matchedSeries.size()>1){
				   logger.warn("Marching the series more than once:"+messageOnDuplication);				   
			   }
			   Series existingSeries=matchedSeries.get(0);
			   existingSeries.merge(series);
			   mergeSeries(existingSeries);
			   return existingSeries;
		   }
	   }
	   public Series saveSeries(Series series){
		   if(series==null){
			   		return null;
		   }
		   if(GenericUtilities.isNotAValidId(series.getPrimaryId())){
				   if(GenericUtilities.isNotAValidId(series.getContractNumber())){
					   if(GenericUtilities.isNotValidTitle(series.getName())){
						   persisSeries(series);
						   
						   return series;
					   }
					   else{
						   List<Series> matchedSeries=findSeriesByName(series.getName());
						   return returnExistingSeriesOrPersist(series,matchedSeries,"name="+series.getName());
					   }
				   }  
				   else{
						   List<Series> matchedSeries=findSeriesByContractNumber(series.getContractNumber());
						   return returnExistingSeriesOrPersist(series,matchedSeries,"contractnumber="+series.getContractNumber());
				   }
		   }
		   else{			   
			   			List<Series> matchedSeries=findSeriesByPrimaryId(series.getPrimaryId());
			   			return returnExistingSeriesOrPersist(series,matchedSeries,"primaryId="+series.getPrimaryId());			   						   
		   }
	   }
	   public ScheduleEvent findScheduleEventById(Long id){
		   return entityManager.find(ScheduleEvent.class, id);		   
	   }
	   public List<ScheduleEvent> findAllScheduleEvent(){
		   TypedQuery<ScheduleEvent> query=entityManager.createQuery("SELECT s FROM schedule_event s", ScheduleEvent.class);
		   return query.getResultList();
	   }
	   public List<ScheduleEvent> findScheduleEventByEpisode(Episode episode){		   
		   TypedQuery<ScheduleEvent> query=entityManager.createQuery("SELECT s FROM schedule_event s where s.episode=:episode", ScheduleEvent.class);
		   return query.setParameter("episode",episode).getResultList();
	   }

	   public List<ScheduleEvent> findScheduleEventByScheduleEventId(String scheduleEventID){
		   TypedQuery<ScheduleEvent> query=entityManager.createQuery("SELECT s FROM schedule_event s where s.scheduleEventID=:scheduleEventID", ScheduleEvent.class);
		   return query.setParameter("scheduleEventID",scheduleEventID).getResultList();
	   }
	   
	   
	   public Series findSeriesById(Long id){
		   return entityManager.find(Series.class, id);		   
	   }
	   public List<Series> findAllSeries(){
		   TypedQuery<Series> query=entityManager.createQuery("SELECT s FROM series s", Series.class);
		   return query.getResultList();
	   }
	   
	   public List<Programme> findProgrammeByTitle(String title){		   
		   TypedQuery<Programme> query=entityManager.createQuery("SELECT p FROM programme p where p.title=:title", Programme.class);
		   return query.setParameter("title",title).getResultList();
	   }
	   public List<Programme> findAllProgramme(){		   
		   TypedQuery<Programme> query=entityManager.createQuery("SELECT p FROM programme p", Programme.class);
		   return query.getResultList();
	   }
	   public List<Episode> findEpisodesByName(String name){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.name=:name", Episode.class);
		   return query.setParameter("name",name).getResultList();
	   }
	   public List<Episode> findEpisodesByTitle(String title){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.title=:title", Episode.class);
		   return query.setParameter("title",title).getResultList();
	   }
	   public Episode findEpisodeById(Long id){
		   return entityManager.find(Episode.class, id);		   
	   }
	   
	   public void update(Episode episode){
		   mergeEpisode(episode);		   
		   saveTags(episode.getTags());
	   }
	  
	   public List<Episode> findAllEpisodes(){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e", Episode.class);
		   return query.getResultList();
	   }
	   
	   public List<Episode> findEpisodesByCtrPrg(String ctrPrg){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.ctrPrg=:ctrPrg", Episode.class);
		   return query.setParameter("ctrPrg",ctrPrg).getResultList();
	   }
	   public List<Episode> findEpisodesByPrimaryId(String primaryId){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.primaryId=:primaryId", Episode.class);
		   return query.setParameter("primaryId",primaryId).getResultList();
	   }
	   public List<Series> findSeriesByPrimaryId(String primaryId){
		   TypedQuery<Series> query=entityManager.createQuery("SELECT s FROM series s where s.primaryId=:primaryId", Series.class);
		   return query.setParameter("primaryId",primaryId).getResultList();
	   }
	   public List<Series> findSeriesByContractNumber(String contractNumber){
		   TypedQuery<Series> query=entityManager.createQuery("SELECT s FROM series s where s.contractNumber=:contractNumber", Series.class);
		   return query.setParameter("contractNumber",contractNumber).getResultList();
	   }
	   public List<Series> findSeriesByName(String name){
		   TypedQuery<Series> query=entityManager.createQuery("SELECT s FROM series s where s.name=:name", Series.class);
		   return query.setParameter("name",name).getResultList();
	   }
	   public void saveTags(String tagCommaSeparated){
		   
		   if(tagCommaSeparated==null){
			   return;
		   }
		   tagCommaSeparated=tagCommaSeparated.trim();
		   if(tagCommaSeparated.length()==0){
			   return;
		   }
		   String tagvalue[]= tagCommaSeparated.split(",");
		   for(int i=0;i<tagvalue.length;i++){
			   saveTag(tagvalue[i]);
		   }
		   
	   }
	   
	   
	   @Transactional
	   public void saveTag(String tag){
		   if(tag==null){
			   return;			   
		   }
		   tag=tag.trim();
		   if(tag.length()<2){
			   return;			   
		   }
		   tag=tag.toLowerCase();
		   TypedQuery<MediaTag> query=entityManager.createQuery("SELECT t FROM tag t where t.name=:name", MediaTag.class);
		   List<MediaTag> tags=query.setParameter("name",tag).getResultList();
		   if(tags.size()==0){
			   MediaTag mediaTag=new MediaTag();
			   mediaTag.setName(tag);
			   persisMediaTag(mediaTag);			   
		   }
	   }
	   public String[] getAllTags(){
		   TypedQuery<MediaTag> query=entityManager.createQuery("SELECT t FROM tag t", MediaTag.class);
		   List<MediaTag> tags=query.getResultList();
		   if(tags.size()==0){
			   return null;
		   }
		   String[] ret=new String[tags.size()];
		   for(int i=0;i<ret.length;i++){
			   ret[i]=tags.get(i).getName();
		   }
		   return ret;		   
	   }
	   
	   
	   
}
