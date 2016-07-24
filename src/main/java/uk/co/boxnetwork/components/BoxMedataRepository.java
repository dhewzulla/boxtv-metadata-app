package uk.co.boxnetwork.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import uk.co.boxnetwork.model.BCNotification;
import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.model.CertificationCategory;
import uk.co.boxnetwork.model.CertificationTime;
import uk.co.boxnetwork.model.CertificationType;
import uk.co.boxnetwork.model.ComplianceInformation;
import uk.co.boxnetwork.model.CuePoint;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.MediaTag;
import uk.co.boxnetwork.model.Programme;
import uk.co.boxnetwork.model.ProgrammeCertification;
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
       public void merge(CuePoint cuePoint){
    	   entityManager.merge(cuePoint);
       }
       public void persist(CuePoint cuePoint){
    	   entityManager.persist(cuePoint);
       }

       
       public void persisSeries(Series series){
		    	   Date lastModifiedAt=new Date();
		    	   series.setLastModifiedAt(lastModifiedAt);
		    	   series.setCreatedAt(lastModifiedAt);
				   entityManager.persist(series);

       }
       public void remove(CuePoint cuePoint){    	   
    		   entityManager.remove(cuePoint);    	   
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
       public void persist(ComplianceInformation compliance){
    	   entityManager.persist(compliance);
       }
       
       public void merge(ComplianceInformation compliance){
    	   entityManager.persist(compliance);
       }
       public void persist(ProgrammeCertification programmeCertification){
    	   entityManager.persist(programmeCertification);
       }
       public void merge(ProgrammeCertification programmeCertification){
    	   entityManager.merge(programmeCertification);    	   
       }
       public void persis(CertificationType certificationType){
    	   entityManager.persist(certificationType);
       }
       public void merge(CertificationType certificationType){
    	   entityManager.merge(certificationType);
       }
       public void persist(CertificationCategory certificationcategory){
    	   entityManager.persist(certificationcategory);    	   
       }
       public void merge(CertificationCategory certificationcategory){
    	   entityManager.merge(certificationcategory);    	   
       }
       public void persist(CertificationTime certificationTime){
    	   entityManager.persist(certificationTime);    	   
       }
       public void merge(CertificationTime certificationTime){
    	   entityManager.merge(certificationTime);    	   
       }
       
       public void persist(BCNotification bcNotification){
    	   entityManager.persist(bcNotification);
       }
       
       public void update(Series series){
		   mergeSeries(series);		   
	   }
       @Transactional
	   public void createEvent(ScheduleEvent newEvent){		    		   
		    
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
       /*
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
	   */
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
	   public CertificationCategory saveCertificationCategory(CertificationCategory certificationCategory){
		   if(certificationCategory==null){
			   return null;			   
		   }
		   List<CertificationCategory> matched=findCertificationCategoryById(certificationCategory.getId());
		   if(matched.size()==0){
			   persist(certificationCategory);
		   }
		   else{
			   matched.get(0).update(certificationCategory);			   
			   merge(certificationCategory);
		   }
			return certificationCategory;			
	   }
	   public CertificationTime saveCertificationTime(CertificationTime certificationTime){
		   if(certificationTime==null){
			   return certificationTime;
		   }
		   List<CertificationTime> matched=findCertificationTimeById(certificationTime.getId());
		   if(matched.size()==0){
			   persist(certificationTime);
		   }
		   else{
			   matched.get(0).update(certificationTime);			   
			   merge(certificationTime);
		   }
		   return certificationTime;
	   }
	   public CertificationType saveCertificationType(CertificationType certificationType){
		   if(certificationType==null){
			   return null;
		   }
		   certificationType.setCertificationCategory(saveCertificationCategory(certificationType.getCertificationCategory()));
		   certificationType.setCertificationTime(saveCertificationTime(certificationType.getCertificationTime()));
		   List<CertificationType> matched=findCertificationTypeById(certificationType.getId());
		   if(matched.size()==0){
			   persis(certificationType);
		   }
		   else{
			   matched.get(0).update(certificationType);			   
			   merge(certificationType);
		   }
		   return certificationType;
	   }
	   public ProgrammeCertification saveProgrammeCertification(ProgrammeCertification programmeCertification){
		   if(programmeCertification == null){
			   return null;
		   }
		   programmeCertification.setCertificationType(saveCertificationType(programmeCertification.getCertificationType()));
		   List<ProgrammeCertification> matchedProgramemeCertification=findProgrammeCertificationById(programmeCertification.getId());
		   if(matchedProgramemeCertification.size()==0){
			   persist(programmeCertification);
		   }
		   else{
			   matchedProgramemeCertification.get(0).update(programmeCertification);			   
			   merge( matchedProgramemeCertification.get(0));
		   }
		   
		   return programmeCertification;
	   }
	   public Set<ComplianceInformation> saveComplianceInformations(Set<ComplianceInformation> complianceInformations){		   
		   if(complianceInformations.size()==0){
			   return complianceInformations;
		   }
		   Set<ComplianceInformation> ret=new HashSet<ComplianceInformation>();
		   for(ComplianceInformation complianceInformation:complianceInformations){
			   ret.add(saveComplianceInfo(complianceInformation));			   
		   }
		   return ret;
	   }
	   public ComplianceInformation saveComplianceInfo(ComplianceInformation complianceInformation){
		   complianceInformation.setProgrammeCertification(saveProgrammeCertification(complianceInformation.getProgrammeCertification()));
		   List<ComplianceInformation> matchedCompliances=findComplianceInformationById(complianceInformation.getId());
		   if(matchedCompliances.size()==0){
			   persist(complianceInformation);			   
		   }
		   else{
			   matchedCompliances.get(0).update(complianceInformation);
			   merge(matchedCompliances.get(0));
		   }
		   return complianceInformation;
	   }
	   
	 
	   public Episode saveEpisode(Episode episode){
		   if(episode==null){
			   return null;			   
		   }
		   else{
			   episode.setComplianceInformations(saveComplianceInformations(episode.getComplianceInformations()));
			   episode.adjustBeforeSave();
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
		   else if(series.getId()!=null){
			   persisSeries(series);
			   return series;
		   }
		   else{
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
	   public Programme findProgrammeById(Long id){
		   return entityManager.find(Programme.class, id);		   
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
	   public List<Episode> findEpisodesBySeries(Series series){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.series=:series", Episode.class);
		   return query.setParameter("series",series).getResultList();
	   }

	   public List<Series> findSeriesByProgramme(Programme programme){
		   TypedQuery<Series> query=entityManager.createQuery("SELECT e FROM series e where e.programme=:programme", Series.class);
		   return query.setParameter("programme",programme).getResultList();
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
	   public List<Episode> findEpisodes(String search){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.title LIKE :search OR e.materialId LIKE :search OR e.series.name LIKE :search", Episode.class);
		   return query.setParameter("search",search).getResultList();
	   }
	   public List<Episode> findEpisodesByMatId(String matid){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.materialId LIKE :matid", Episode.class);
		   return query.setParameter("matid",matid).getResultList();
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
	   public List<ComplianceInformation> findComplianceInformationById(String id){
		   TypedQuery<ComplianceInformation> query=entityManager.createQuery("SELECT c FROM compliance_information c where c.id=:id", ComplianceInformation.class);
		   return query.setParameter("id",id).getResultList();
	   }
	   public List<ProgrammeCertification> findProgrammeCertificationById(String id){
		   TypedQuery<ProgrammeCertification> query=entityManager.createQuery("SELECT p FROM programme_certification p where p.id=:id", ProgrammeCertification.class);
		   return query.setParameter("id",id).getResultList();
	   }
	   public List<CertificationType> findCertificationTypeById(String id){
		   TypedQuery<CertificationType> query=entityManager.createQuery("SELECT c FROM certification_type c where c.id=:id", CertificationType.class);
		   return query.setParameter("id",id).getResultList();
	   }
	   public List<CertificationCategory> findCertificationCategoryById(String id){
		   TypedQuery<CertificationCategory> query=entityManager.createQuery("SELECT c FROM certification_category c where c.id=:id", CertificationCategory.class);
		   return query.setParameter("id",id).getResultList();
	   }
	   public List<CertificationTime> findCertificationTimeById(String id){
		   TypedQuery<CertificationTime> query=entityManager.createQuery("SELECT c FROM certification_time c where c.id=:id", CertificationTime.class);
		   return query.setParameter("id",id).getResultList();
	   }
	   public List<BCNotification> findAllBCNotification(){
		   TypedQuery<BCNotification> query=entityManager.createQuery("SELECT b FROM bc_notification b", BCNotification.class);
		   return query.getResultList();
	   }
	   public List<BCNotification> findBCNotificationByJobId(String jobid){
		   TypedQuery<BCNotification> query=entityManager.createQuery("SELECT b FROM bc_notification b where b.jobId=:jobId", BCNotification.class);
		   return query.setParameter("jobId",jobid).getResultList();
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
	   
	        
	   @Transactional
       public void updateUser(BoxUser user){
    	   entityManager.merge(user);
       }
	   @Transactional
       public void createUser(BoxUser user){
    	   entityManager.persist(user);
       }
	   @Transactional
	   public void deleteByUsername(String username){
		   BoxUser user= entityManager.find(BoxUser.class, username);
		   if(user!=null){
			   entityManager.remove(user);
		   }
	   }
       public List<BoxUser> findUserByUsername(String username){
    	   TypedQuery<BoxUser> query=entityManager.createQuery("SELECT u FROM user u where u.username=:username", BoxUser.class);    	   
		   return query.setParameter("username",username).getResultList();		   
	   }
	   public List<BoxUser> findAllUsers(){		   
    	   TypedQuery<BoxUser> query=entityManager.createQuery("SELECT u FROM user u", BoxUser.class);
		   List<BoxUser> users=query.getResultList();
		   return users;
       }
	   
	   
	   
}
