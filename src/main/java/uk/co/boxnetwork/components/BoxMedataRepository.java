package uk.co.boxnetwork.components;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.model.Episode;
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

	   @Transactional 
	   public void createEvent(ScheduleEvent newEvent){		    		   
		    Programme programme=createProgrammeFromScheduleEvent(newEvent);
		    if(programme!=null){
		    	List<Programme> matchedProgrammes=findProgrammeByTitle(programme.getTitle());
		    	if(matchedProgrammes.size()==0){		    		
		    		entityManager.persist(programme);
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
				entityManager.persist(newEvent);
			}
			else{
				logger.info("querying the sechedule event:");
				List<ScheduleEvent> existingEvents=findScheduleEventByScheduleEventId(newEvent.getScheduleEventID());
				if(existingEvents.size()==0){
					newEvent.setEpisode(saveEpisode(newEvent.getEpisode()));
					entityManager.persist(newEvent);
				}
				else{
					if(existingEvents.size()>1){
						logger.warn("There are mot than matching events matching evenid="+newEvent.getScheduleEventID());
					}
					ScheduleEvent existingEvent=existingEvents.get(0);
					existingEvent.setEpisode(saveEpisode(newEvent.getEpisode()));
					existingEvent.merge(newEvent);
					entityManager.merge(existingEvent);					
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
		   logger.info("creating new episode");
		   	entityManager.persist(episode);
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
			   
			   existingEpisode.merge(episode);
			   
			   
			   logger.info("merging the existing episode");			   
			   entityManager.merge(existingEpisode);
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
			   entityManager.persist(series);
			   logger.info("creating new series");
			   return series;
		   }
		   else{
			   if(matchedSeries.size()>1){
				   logger.warn("Marching the series more than once:"+messageOnDuplication);				   
			   }
			   Series existingSeries=matchedSeries.get(0);
			   existingSeries.merge(series);
			   logger.info("merging existing series");
			   entityManager.merge(existingSeries);
			   
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
						   entityManager.persist(series);
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
	   
	   public List<ScheduleEvent> findScheduleEventByScheduleEventId(String scheduleEventID){
		   TypedQuery<ScheduleEvent> query=entityManager.createQuery("SELECT s FROM schedule_event s where s.scheduleEventID=:scheduleEventID", ScheduleEvent.class);
		   return query.setParameter("scheduleEventID",scheduleEventID).getResultList();
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
	  
	  
	   
	   
	   
}
