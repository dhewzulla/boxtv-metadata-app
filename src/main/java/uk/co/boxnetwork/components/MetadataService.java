package uk.co.boxnetwork.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.data.s3.VideoFilesLocation;
import uk.co.boxnetwork.model.AvailabilityWindow;
import uk.co.boxnetwork.model.BCNotification;
import uk.co.boxnetwork.model.CuePoint;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.EpisodeStatus;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.model.PublishedStatus;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.model.SeriesGroup;
import uk.co.boxnetwork.model.VideoStatus;
import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class MetadataService {
	private static final Logger logger=LoggerFactory.getLogger(MetadataService.class);
	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	@Autowired
	S3BucketService s3BucketService;
	  	
	@Autowired
	BCVideoService videoService;
	
	public List<uk.co.boxnetwork.data.SeriesGroup> getAllSeriesGroups(){
		List<uk.co.boxnetwork.data.SeriesGroup> seriesgrps=new ArrayList<uk.co.boxnetwork.data.SeriesGroup>();
		List<SeriesGroup> seriesgroups=boxMetadataRepository.findAllSeriesGroup();
		for(SeriesGroup seriesgroup:seriesgroups){
			seriesgrps.add(new uk.co.boxnetwork.data.SeriesGroup(seriesgroup));	
		}
		return seriesgrps;
	}
	public uk.co.boxnetwork.data.SeriesGroup getSeriesGroupById(Long id){
	SeriesGroup seriesGroup=boxMetadataRepository.findSeriesGroupById(id);
		if(seriesGroup==null){
			return null;
		}
		List<Series> serieses=boxMetadataRepository.findSeriesBySeriesGroup(seriesGroup);
		uk.co.boxnetwork.data.SeriesGroup ret=new uk.co.boxnetwork.data.SeriesGroup(seriesGroup);
		for(Series series:serieses){
			series.setSeriesGroup(null);
			uk.co.boxnetwork.data.Series series2=new uk.co.boxnetwork.data.Series(series);			
			ret.addSeries(series2);
		}				
		return ret;		
	}
	
	public List<uk.co.boxnetwork.data.Episode> getAllEpisodes(int beginIndex, int recordLimit){		
		return toDataEpisodes(boxMetadataRepository.findAllEpisodes(beginIndex,recordLimit));
	}
	public List<uk.co.boxnetwork.data.Episode> findEpisodes(String search){
		List<Episode> eposides=boxMetadataRepository.findEpisodes(search);
		logger.info("For search:"+search+" found matching:"+eposides.size());
		return toDataEpisodes(eposides);
	}
	private  List<uk.co.boxnetwork.data.Episode>  toDataEpisodes(List<Episode> eposides){
		List<uk.co.boxnetwork.data.Episode> ret=new ArrayList<uk.co.boxnetwork.data.Episode>();
		for(Episode episode:eposides){
			uk.co.boxnetwork.data.Episode dep=new uk.co.boxnetwork.data.Episode(episode,null);
			dep.setCuePoints(null);
			dep.setComplianceInformations(null);
			dep.setAvailabilities(null);
			ret.add(dep);
		}
		
		return ret;
	}
	private  List<uk.co.boxnetwork.data.Series>  toDataSeries(List<Series> series){
		List<uk.co.boxnetwork.data.Series> ret=new ArrayList<uk.co.boxnetwork.data.Series>();
		for(Series s:series){
			uk.co.boxnetwork.data.Series dep=new uk.co.boxnetwork.data.Series(s);			
			ret.add(dep);
		}		
		return ret;
	}
	 
	
	
public List<uk.co.boxnetwork.data.Series> getAllSeries(){		
		 return toDataSeries(boxMetadataRepository.findAllSeries());
	}
public uk.co.boxnetwork.data.Series getSeriesById(Long id){
	Series series=boxMetadataRepository.findSeriesById(id);
	if(series==null){
		return null;
	}
	List<Episode> episodes=boxMetadataRepository.findEpisodesBySeries(series);
	List<uk.co.boxnetwork.data.Episode> eps=toDataEpisodes(episodes);
	for(uk.co.boxnetwork.data.Episode ep:eps){
		ep.setScheduleEvents(null);
		ep.setSeries(null);
	}
	uk.co.boxnetwork.data.Series ret=new uk.co.boxnetwork.data.Series(series);
	ret.setEpisodes(eps);		
	return ret;		
}


	public uk.co.boxnetwork.data.Episode getEpisodeById(Long id){
		Episode episode=boxMetadataRepository.findEpisodeById(id);
		if(episode==null){
			return null;
		}
		List<ScheduleEvent> scheduleEvents=boxMetadataRepository.findScheduleEventByEpisode(episode);
		
		uk.co.boxnetwork.data.Episode ret=new uk.co.boxnetwork.data.Episode(episode,scheduleEvents);
				
		
		return ret;		
	}
	public uk.co.boxnetwork.data.CuePoint getCuePointById(Long id){
		CuePoint cuepoint=boxMetadataRepository.findCuePoint(id);
		if(cuepoint==null){
			return null;			
		}
		return new uk.co.boxnetwork.data.CuePoint(cuepoint);
		
	}
	public uk.co.boxnetwork.data.AvailabilityWindow getAvailabilityWindowId(Long id){
		AvailabilityWindow availabilityWindow=boxMetadataRepository.findAvailabilityWindowId(id);
		if(availabilityWindow==null){
			return null;			
		}
		return new uk.co.boxnetwork.data.AvailabilityWindow(availabilityWindow);
		
	}
	
	public void statusUpdateOnEpisodeUpdated(Episode existingEpisode, String oldIngestSource, String oldIngstProfile){
		EpisodeStatus episodeStatus=existingEpisode.getEpisodeStatus();
		MetadataStatus metadataStatus=GenericUtilities.calculateMetadataStatus(existingEpisode);
		if(metadataStatus!=null){
			episodeStatus.setMetadataStatus(metadataStatus);
		}
		else{
			episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_PUBLISH_CHANGES);
		}
		VideoStatus videoStatus=GenericUtilities.calculateVideoStatus(existingEpisode,oldIngestSource,oldIngstProfile);
		if(videoStatus!=null){
			episodeStatus.setVideoStatus(videoStatus);
		}
		if(episodeStatus.getId()==null){
			boxMetadataRepository.persistEpisodeStatus(episodeStatus);
    	}
    	else{
    		boxMetadataRepository.persistEpisodeStatus(episodeStatus);
    	}
 	   
    }
	
	public void statusBoundSourceVideo(Episode existingEpisode){
		EpisodeStatus episodeStatus=existingEpisode.getEpisodeStatus();
 	   VideoStatus videoStatus=GenericUtilities.calculateVideoStatus(existingEpisode);
 	    if(videoStatus!=null){
 	    	episodeStatus.setVideoStatus(videoStatus); 	    	
 	    }
 	    else{
 	    	episodeStatus.setVideoStatus(VideoStatus.NEEDS_RETRANSCODE);
 	    	
 	    } 	   
 	    boxMetadataRepository.persistEpisodeStatus(episodeStatus);
    }

	@Transactional
	public void update(long id, uk.co.boxnetwork.data.Episode episode){
		Episode existingEpisode=boxMetadataRepository.findEpisodeById(id);
		String oldIngestSource=existingEpisode.getIngestSource();
		String oldIngestProfile=existingEpisode.getIngestProfile();
		logger.info("Before upating the episode:"+existingEpisode);
		episode.update(existingEpisode);
		
		statusUpdateOnEpisodeUpdated(existingEpisode,oldIngestSource,oldIngestProfile);
		
		boxMetadataRepository.saveTags(episode.getTags());
		logger.info("After upating the episode:"+existingEpisode);
		checkS3ToUpdateVidoStatus(existingEpisode);
		boxMetadataRepository.persist(existingEpisode);		
	}
	
	public void update(long id, uk.co.boxnetwork.data.SeriesGroup seriesGroup){
		SeriesGroup existingSeriesGroup=boxMetadataRepository.findSeriesGroupById(id);		
		seriesGroup.update(existingSeriesGroup);
		boxMetadataRepository.mergeSeriesGroup(existingSeriesGroup);
		
		List<SeriesGroup> matchedSeriesGroups=boxMetadataRepository.findSeriesGroupByTitle(seriesGroup.getTitle());
		mergeSeriesGroupTo(matchedSeriesGroups,existingSeriesGroup);		
	}
	private void mergeSeriesGroupTo(List<SeriesGroup> matchedSeriesGroups, SeriesGroup targetSeriesGroup){
		
		List<SeriesGroup> recordsToDelete=new ArrayList<SeriesGroup>();
		for(SeriesGroup sg:matchedSeriesGroups){
			if(!sg.getId().equals(targetSeriesGroup.getId())){
				recordsToDelete.add(sg);
			}
		}
		if(recordsToDelete.size()==0){
			return;
		}
		for(SeriesGroup gr:recordsToDelete){
			List<Series> series=boxMetadataRepository.findSeriesBySeriesGroup(gr);
			for(Series sr:series){				
				sr.setSeriesGroup(targetSeriesGroup);
				boxMetadataRepository.updateSeries(sr);
			}	
			if(!GenericUtilities.isNotValidTitle(gr.getSynopsis())){
				if(GenericUtilities.isNotValidTitle(targetSeriesGroup.getSynopsis())){
					targetSeriesGroup.setSynopsis(gr.getSynopsis());
				}
				else{
					targetSeriesGroup.setSynopsis(targetSeriesGroup.getSynopsis()+" "+ gr.getSynopsis());
				}
			}
			
		}				
		boxMetadataRepository.removeSeriesGroup(recordsToDelete);		
	}
	
	
	private void statusUpde(Episode episode){
		EpisodeStatus episodeStatus=episode.getEpisodeStatus();
		MetadataStatus metadataStatus=GenericUtilities.calculateMetadataStatus(episode);
		if(metadataStatus!=null){
			episodeStatus.setMetadataStatus(metadataStatus);
		}
		else{
			episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_PUBLISH_CHANGES);
		}
		VideoStatus videoStatus=GenericUtilities.calculateVideoStatus(episode);
		if(videoStatus!=null){
			episodeStatus.setVideoStatus(videoStatus);
		}
		
		if(episodeStatus.getId()==null){
			boxMetadataRepository.persistEpisodeStatus(episodeStatus);
    	}
    	else{
    		boxMetadataRepository.persistEpisodeStatus(episodeStatus);
    	}
		
	}
	
	private void whenVideoAvailable(EpisodeStatus episodeStatus, String ingestProfile){
		if(episodeStatus.getVideoStatus()==VideoStatus.MISSING_VIDEO){
			if(GenericUtilities.isNotValidName(ingestProfile)){
				episodeStatus.setVideoStatus(VideoStatus.MISSING_PROFILE);									
			}
			else{
				episodeStatus.setVideoStatus(VideoStatus.NEEDS_TRANSCODE);
									
			}
			boxMetadataRepository.persistEpisodeStatus(episodeStatus);
		}
	}
		
	public void checkS3ToUpdateVidoStatus(Episode episode){
		
		if(!GenericUtilities.isNotValidName(episode.getIngestSource())){
			whenVideoAvailable(episode.getEpisodeStatus(),episode.getIngestProfile());			
		}
		else{			
				requestS3(episode);
				if(!GenericUtilities.isNotValidName(episode.getIngestSource())){
					whenVideoAvailable(episode.getEpisodeStatus(),episode.getIngestProfile());
				}
		}
	}
	
	@Transactional
	public uk.co.boxnetwork.data.Episode updateEpisodeById(uk.co.boxnetwork.data.Episode episode){
		Episode episodeInDB=boxMetadataRepository.findEpisodeById(episode.getId());
		if(episodeInDB==null){
			logger.info("not found");
			return null;
		}
		if(episode.updateWhenReceivedByMaterialId(episodeInDB)){
			boxMetadataRepository.persist(episodeInDB);
		}
		changeEpisodeStatus(episode,episodeInDB);
		return new uk.co.boxnetwork.data.Episode(episodeInDB,null);
	}
	public void  changeEpisodeStatus(uk.co.boxnetwork.data.Episode episode,Episode episodeInDB){
		
		EpisodeStatus episodeStatus=episode.getEpisodeStatus();
		EpisodeStatus episodeStatusInDB=episodeInDB.getEpisodeStatus();
		if(episodeStatus.getPublishedStatus()==PublishedStatus.ACTIVE){
			
			if(episodeInDB.getBrightcoveId()==null){
				logger.info("bcid is null");
				return;
			}
			
			BCVideoData videodata=videoService.changeVideoStatus(episodeInDB.getBrightcoveId(),"ACTIVE");
			
			logger.info("*****"+videodata);
			episodeStatusInDB.setPublishedStatus(PublishedStatus.ACTIVE);
			boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
		}
		else if(episodeStatus.getPublishedStatus()==PublishedStatus.INACTIVE){
			if(episodeInDB.getBrightcoveId()==null){
				episodeStatusInDB.setPublishedStatus(PublishedStatus.INACTIVE);
				boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
				logger.info("INACTIVE because bcid is null");
				return;
			}
			BCVideoData videodata=videoService.changeVideoStatus(episodeInDB.getBrightcoveId(),"INACTIVE");
			logger.info("INACTIVE is set");
			episodeStatusInDB.setPublishedStatus(PublishedStatus.INACTIVE);
			boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
		}
		else{
			logger.info("****:"+episodeStatus.getPublishedStatus());
		}
		
//		if(episodeStatus.update(episodeStatusInDB)){        	
//			boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
//		}
    }
	@Transactional
	public uk.co.boxnetwork.data.Episode reicevedEpisodeByMaterialId(uk.co.boxnetwork.data.Episode episode){
		String materiaId=episode.getMaterialId();
		
		if(materiaId==null){
			throw new RuntimeException("MaterialId is required");
		}
		materiaId=materiaId.trim();
		if(materiaId.length()==0){
			throw new RuntimeException("MaterialId is empty");
		}
		
		String contractNumber=GenericUtilities.getContractNumber(episode);
		String programmeId=GenericUtilities.getProgrammeNumber(episode);
		logger.info("contractNumber:"+contractNumber+" programmeId=["+programmeId+"]");
		Series existingSeries=null;
		SeriesGroup existingEeriesGroup=null;	
		Episode existingEpisode=null;		
		
		List<Episode> matchedEpisodes=boxMetadataRepository.findEpisodesByCtrPrg(programmeId);		
		
		if(matchedEpisodes.size()==0){
			logger.info("matching episode number not found programmeId:"+programmeId);
		}
		else if(matchedEpisodes.size()>1){
			throw new RuntimeException("more than one episodes matched to the materia id:"+materiaId);
		}
		else{
				existingEpisode=matchedEpisodes.get(0);
				existingSeries=existingEpisode.getSeries();
				if(existingSeries!=null){
					existingEeriesGroup=existingSeries.getSeriesGroup();
				}
		}	
	    if(existingSeries==null && (!GenericUtilities.isNotValidCrid(contractNumber))){
		     List<Series> matchSeries=boxMetadataRepository.findSeriesByContractNumber(contractNumber);
		     if(matchSeries.size()==0){
		    	 logger.info("matching series not found contractNumber:"+contractNumber);
		     }
		     else if(matchSeries.size()>1){
		    	 throw new RuntimeException("more than one series matching the contractNumber:"+contractNumber);
		     }
		     else{
		    	 	existingSeries=matchSeries.get(0);		    	 	
		    	 	existingEeriesGroup=existingSeries.getSeriesGroup();
		     }
	    }   
		if(existingEeriesGroup==null && episode.getSeries() !=null && episode.getSeries().getSeriesGroup() !=null && (!GenericUtilities.isNotValidTitle(episode.getSeries().getSeriesGroup().getTitle()))){
			List<SeriesGroup> matchedSeriesGroups=boxMetadataRepository.findSeriesGroupByTitle(episode.getSeries().getSeriesGroup().getTitle());
			if(matchedSeriesGroups.size()>0){
				existingEeriesGroup=matchedSeriesGroups.get(0);
			}
			else{
					existingEeriesGroup=new SeriesGroup();
					episode.getSeries().getSeriesGroup().update(existingEeriesGroup);
					boxMetadataRepository.persisSeriesGroup(existingEeriesGroup);
					logger.info("created a nw series group:"+existingEeriesGroup);
			}
		}
		else if(existingEeriesGroup==null){
			existingEeriesGroup=boxMetadataRepository.retrieveDefaultSeriesGroup();
		}
		
		if(existingSeries==null && episode.getSeries()!=null && GenericUtilities.isNotValidTitle(episode.getSeries().getName())){
			existingSeries=new Series();					
			episode.getSeries().update(existingSeries);						
			if(GenericUtilities.isNotValidCrid(existingSeries.getContractNumber())){
				existingSeries.setContractNumber(contractNumber);
			}									
			existingSeries.setSeriesGroup(existingEeriesGroup);
			boxMetadataRepository.persisSeries(existingSeries);
		}
		if(existingEpisode==null){
			logger.info("creating a new episode");
			existingEpisode=new Episode();			
			episode.update(existingEpisode);
			existingEpisode.setSeries(existingSeries);
			EpisodeStatus episodeStatus=new EpisodeStatus();
			episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER);
			episodeStatus.setVideoStatus(VideoStatus.NO_PLACEHOLDER);				
			boxMetadataRepository.persistEpisodeStatus(episodeStatus);
			existingEpisode.setEpisodeStatus(episodeStatus);
			boxMetadataRepository.saveTags(episode.getTags());
			boxMetadataRepository.persist(existingEpisode);
		}		
		else{	
			   logger.info("Upating the existing episode");
			    existingEpisode.setSeries(existingSeries);
				episode.updateWhenReceivedByMaterialId(existingEpisode);
				statusUpde(existingEpisode);
				boxMetadataRepository.saveTags(episode.getTags());
				existingEpisode.getEpisodeStatus().setMetadataStatus(MetadataStatus.NEEDS_TO_PUBLISH_CHANGES);
				boxMetadataRepository.persistEpisodeStatus(existingEpisode.getEpisodeStatus());
				boxMetadataRepository.persist(existingEpisode);
		}
		replaceCuePoints(existingEpisode,episode.getCuePoints());		
		checkS3ToUpdateVidoStatus(existingEpisode);		
		boxMetadataRepository.persist(existingEpisode);	
		uk.co.boxnetwork.data.Episode ret=new uk.co.boxnetwork.data.Episode(existingEpisode,null);
		ret.setComplianceInformations(existingEpisode.getComplianceInformations());
		return ret;
	}
	
	@Transactional
	public uk.co.boxnetwork.data.Series createNewSeries(uk.co.boxnetwork.data.Series series){
		Series ser=createOrUpdateSeries(series, series.getContractNumber());
		return new uk.co.boxnetwork.data.Series(ser);
	}
	public Series createOrUpdateSeries(uk.co.boxnetwork.data.Series series, String contractNumber){
		if(series==null){
			return null;
		}		
		SeriesGroup sg=createOrUpdateSeriesGroup(series.getSeriesGroup());
		Series existingSeries=null;
		if(contractNumber!=null){
			List<Series> matchSeries=boxMetadataRepository.findSeriesByContractNumber(contractNumber);
			if(matchSeries.size()==0){
				if(GenericUtilities.isNotValidTitle(series.getName())){
					return null;					
				}
				else{
					existingSeries=new Series();					
					series.update(existingSeries);
					if(GenericUtilities.isNotValidCrid(series.getContractNumber())){
						series.setContractNumber(contractNumber);
					}
					existingSeries.setSeriesGroup(sg);
					boxMetadataRepository.persisSeries(existingSeries);
					return existingSeries;
				}
			}
			else{
				  existingSeries=matchSeries.get(0);
				  logger.info("Upating the existing with the contract number contractNumber=["+contractNumber+"]existingSeries=["+existingSeries+"]");
				  if(GenericUtilities.isNotValidTitle(series.getName()) && GenericUtilities.isNotValidTitle(series.getSynopsis()) ){					  
					  if(sg==null){
						  return existingSeries;
					  }
					  else{
						  existingSeries.setSeriesGroup(sg);
						  boxMetadataRepository.mergeSeries(existingSeries);
						  return existingSeries;
					  }
				  }
				  else if(GenericUtilities.isNotValidTitle(series.getName())){
					  if(GenericUtilities.isNotValidTitle(series.getSynopsis())){						  
						  if(sg==null){
							  return existingSeries;
						  }
						  else{
							  	existingSeries.setSeriesGroup(sg);
							  	boxMetadataRepository.mergeSeries(existingSeries);
							  	return existingSeries;
						  }
					  }
					  else{
						  if(GenericUtilities.isNotValidTitle(existingSeries.getSynopsis())){
							  existingSeries.setSynopsis(series.getSynopsis());
							  existingSeries.setSeriesGroup(sg);
							  boxMetadataRepository.mergeSeries(existingSeries);
							  return existingSeries;
						  }
						  else{
							  existingSeries.setSynopsis(existingSeries.getSynopsis()+" "+series.getSynopsis());
							  existingSeries.setSeriesGroup(sg);
							  boxMetadataRepository.mergeSeries(existingSeries);
							  return existingSeries;
						  }
					  }
				  }
				  else if(GenericUtilities.isNotValidTitle(series.getSynopsis())){
					  existingSeries.setSeriesGroup(sg);
					  existingSeries.setName(series.getName());					  
					  boxMetadataRepository.mergeSeries(existingSeries);
				      return existingSeries;					  	  
				  }
				  else{
					  	existingSeries.setSeriesGroup(sg);
					  	existingSeries.setName(series.getName());
					  	if(GenericUtilities.isNotValidTitle(existingSeries.getSynopsis())){
							  existingSeries.setSynopsis(series.getSynopsis());							  
							  boxMetadataRepository.mergeSeries(existingSeries);
							  return existingSeries;
						  }
						  else{
							  existingSeries.setSynopsis(existingSeries.getSynopsis()+" "+series.getSynopsis());							  
							  boxMetadataRepository.mergeSeries(existingSeries);
							  return existingSeries;
						  }
					  
				  }
				
			}
			
		}
		else{
			
			if(GenericUtilities.isNotValidTitle(series.getName())){
					return null;					
			}
			else{
				existingSeries=new Series();					
				series.update(existingSeries);
				existingSeries.setSeriesGroup(sg);
				boxMetadataRepository.persisSeries(existingSeries);
				return existingSeries;
			}
		
		
		}
		
		
		
		
	}
	
	
	@Transactional
	public uk.co.boxnetwork.data.SeriesGroup createANewSeriesGroup(uk.co.boxnetwork.data.SeriesGroup seriesGroup){
		if(seriesGroup==null){
			throw new RuntimeException("nul value for seriesGroup");
		}				
		if(GenericUtilities.isNotValidTitle(seriesGroup.getTitle())){
			throw new RuntimeException("not valid title for seriesGroup");			
		}
		List<SeriesGroup> seriesGroups=boxMetadataRepository.findSeriesGroupByTitle(seriesGroup.getTitle());
		if(seriesGroups.size()==0){
			SeriesGroup newSeriesGroup=new SeriesGroup();
			seriesGroup.update(newSeriesGroup);			
			boxMetadataRepository.persisSeriesGroup(newSeriesGroup);
			return new uk.co.boxnetwork.data.SeriesGroup(newSeriesGroup);
		}
		else{
								
			   throw new RuntimeException("There is already a seriesgroup with the same title");	
		}
	}
	
	public SeriesGroup createOrUpdateSeriesGroup(uk.co.boxnetwork.data.SeriesGroup seriesGroup){
		if(seriesGroup==null){
			return boxMetadataRepository.retrieveDefaultSeriesGroup();
		}
		
		if(seriesGroup.getId()!=null){
			SeriesGroup seriesgroup=boxMetadataRepository.findSeriesGroupById(seriesGroup.getId());
			if(seriesgroup!=null){
				return seriesgroup;
			}
			else{
				logger.error("The series group with the id not found id:"+seriesGroup.getId());
				return boxMetadataRepository.retrieveDefaultSeriesGroup();
			}
		}
		if(GenericUtilities.isNotValidTitle(seriesGroup.getTitle())){
			logger.info("The series group title is not there, so ");
			return boxMetadataRepository.retrieveDefaultSeriesGroup();		
		}
		List<SeriesGroup> seriesGroups=boxMetadataRepository.findSeriesGroupByTitle(seriesGroup.getTitle());
		if(seriesGroups.size()==0){
			SeriesGroup newSeriesGroup=new SeriesGroup();
			seriesGroup.update(newSeriesGroup);
			
			boxMetadataRepository.persisSeriesGroup(newSeriesGroup);
			return newSeriesGroup;
		}
		else{
				SeriesGroup existingSeriesGroup=seriesGroups.get(0);
				mergeSeriesGroupTo(seriesGroups, existingSeriesGroup);
				if(GenericUtilities.isNotValidTitle(seriesGroup.getSynopsis())){
					return existingSeriesGroup;
				}
				if(GenericUtilities.isNotValidTitle(existingSeriesGroup.getSynopsis())){
					existingSeriesGroup.setSynopsis(seriesGroup.getSynopsis());
					boxMetadataRepository.mergeSeriesGroup(existingSeriesGroup);
					return existingSeriesGroup;
				}
				else {
					existingSeriesGroup.setSynopsis(seriesGroup.getSynopsis()+" "+seriesGroup.getSynopsis());
					boxMetadataRepository.mergeSeriesGroup(existingSeriesGroup);
					return existingSeriesGroup;
				}
					
				
		}
		
		
	}
	
	
	public void replaceCuePoints(Episode episodeToUpdate, List<uk.co.boxnetwork.data.CuePoint> cuePoints){
		if(cuePoints==null|| cuePoints.size()==0){
			logger.info("***number of cue points received is zero");
			return;
			
		}
		else{
			logger.info("***number of cue points received:"+cuePoints.size());
		}
	    for(CuePoint cp:episodeToUpdate.getCuePoints()){	    	
	    	boxMetadataRepository.remove(cp);	    	
	    }
	    episodeToUpdate.clearCuePoints();
	    
	    
		for(uk.co.boxnetwork.data.CuePoint cuepoint:cuePoints){
			    logger.info("Received cue point:"+cuepoint);
			    if(cuepoint.getTime()==null){
			    	logger.info("Ignoring the cue points that time is null");
			    	continue;

			    }
			     CuePoint cue=new CuePoint();
			     cuepoint.update(cue);			     
			     logger.info("after update:"+cue);
			     episodeToUpdate.addCuePoint(cue);
			     cue.setEpisode(episodeToUpdate);
			     
			     
			     boxMetadataRepository.persist(cue);
			     
		}		
	}
	
	
	@Transactional
	public void update(long id, uk.co.boxnetwork.data.Series series){
		Series existingSeries=boxMetadataRepository.findSeriesById(id);
		series.update(existingSeries);
		boxMetadataRepository.update(existingSeries);
		if(existingSeries.getSeriesGroup()==null){
			existingSeries.setSeriesGroup(boxMetadataRepository.retrieveDefaultSeriesGroup());
			boxMetadataRepository.update(existingSeries);
		}
		
	}
		
	public List<uk.co.boxnetwork.data.ScheduleEvent> getAllScheduleEventFrom(Date fromDate){
		List<ScheduleEvent> schedules= boxMetadataRepository.findScheduleEventsFrom(fromDate);
	
		List<uk.co.boxnetwork.data.ScheduleEvent> ret=new ArrayList<uk.co.boxnetwork.data.ScheduleEvent>();
		for(ScheduleEvent evt:schedules){
			ret.add(new uk.co.boxnetwork.data.ScheduleEvent(evt));			
		}
		return ret;
	}
	public ScheduleEvent getScheduleEventById(Long id){
		return boxMetadataRepository.findScheduleEventById(id);
	}
	
   @Transactional	 
   public void  bindVideoFile(String ingestFile){
	   String materialId=GenericUtilities.fileNameToMaterialID(ingestFile);
	   logger.info("should attach video file:"+ingestFile+" to the material id:"+materialId);
	   List<Episode> matchedEpisodes=boxMetadataRepository.findEpisodesByMatId(materialId+"%");
	   if(matchedEpisodes.size()==0){
		   logger.info("None of the episode has the matching materialId for the video file:"+ingestFile);
		   return;
	   }
	   else if(matchedEpisodes.size()==1){
		   Episode episode=matchedEpisodes.get(0);
		   episode.setIngestSource(s3BucketService.getFullVideoURL(ingestFile));
		   statusBoundSourceVideo(episode);
		   boxMetadataRepository.persist(episode);
		   
	   }
	   else{
		   logger.warn("more than epsiode matching the materialId=["+materialId+"] while binding video file:"+ingestFile+"]");
		   for(Episode episode: matchedEpisodes){
			   if(episode.getIngestSource()!=null){
				   episode.setIngestSource(s3BucketService.getFullVideoURL(ingestFile));
				   statusBoundSourceVideo(episode);
				   boxMetadataRepository.persist(episode);
			   }
		   }
	   }
	   
	   
   }
   
   
   public void deleteEpisodeById(Long episodeid){
	   Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	   if(episode==null){
		   logger.info("No epsiode to delete episodeid="+episodeid);
		   return;
	   }
	   logger.info("The episode is going to be deleted:"+episode.getId()+":"+episode);
	   List<ScheduleEvent> eventsToDelete=boxMetadataRepository.findScheduleEventByEpisode(episode);
	   logger.info("The number of schedule events to be deleted:"+eventsToDelete.size());
	   boxMetadataRepository.removeScheduleEvents(eventsToDelete);
	   boxMetadataRepository.removeCuePoints(episode.getCuePoints());	   
	   boxMetadataRepository.removeEpisode(episode);
	   if(episode.getEpisodeStatus()!=null){
		   	boxMetadataRepository.remove(episode.getEpisodeStatus());
	   }
	   
   }
   
   public void deleteSeriesGroupById(Long seriesgroupid){
	   boxMetadataRepository.removeSeriesGroupById(seriesgroupid);	   
   }
   public void deleteSeriesById(Long seriesid){
	   boxMetadataRepository.removeSeriesById(seriesid);
	   
   }
   
   public  void requestS3(Episode episode){
		 String fileNameFilter=episode.calculateSourceVideoFilePrefix();
		 if(fileNameFilter==null){
			 return;
		 }
		 VideoFilesLocation matchedfiles=s3BucketService.listFilesInVideoBucket(fileNameFilter);
		 String ingestFile=matchedfiles.highestVersion();
		 if(ingestFile!=null){
			 episode.setIngestSource(s3BucketService.getFullVideoURL(ingestFile));
		 }	 
		 	
	 }

	
  public void notifyTranscode(BCNotification notification){
	  if("FAILED".equals(notification.getStatus())){
		  boxMetadataRepository.markVideoTranscodeAsFailed(notification.getVideoId());		  
	  }
	  else if("SUCCESS".equals(notification.getStatus()) && "TITLE".equals(notification.getEntityType())){
		  boxMetadataRepository.markVideoTranscodeAsComplete(notification.getVideoId());		  
	  }
  }
  
  @Transactional
  public void updatePublishedStatus(Episode episode){
	  EpisodeStatus episodeStatus=episode.getEpisodeStatus();
	  if(episode.getBrightcoveId()==null){		  
		  if(episodeStatus.getPublishedStatus()!=PublishedStatus.NOT_PUBLISHED){
			  episodeStatus.setPublishedStatus(PublishedStatus.NOT_PUBLISHED);
			  boxMetadataRepository.persistEpisodeStatus(episodeStatus);
		  }		  
	  }
	  else{
		  BCVideoData videoData=videoService.getVideo(episode.getBrightcoveId());
		  if("INACTIVE".equals(videoData.getState())){
			  if(episodeStatus.getPublishedStatus()!=PublishedStatus.INACTIVE){
				  episodeStatus.setPublishedStatus(PublishedStatus.INACTIVE);
				  boxMetadataRepository.persistEpisodeStatus(episodeStatus);
			  }	  
		  }
		  if("ACTIVE".equals(videoData.getState())){
			  if(episodeStatus.getPublishedStatus()!=PublishedStatus.ACTIVE){
				  episodeStatus.setPublishedStatus(PublishedStatus.ACTIVE);
				  boxMetadataRepository.persistEpisodeStatus(episodeStatus);
			  }	  
		  }
		  
	  }
  }
  public  uk.co.boxnetwork.data.Episode publishMetadatatoBCByEpisodeId(Long id){
	  Episode uptoDateEpisode=boxMetadataRepository.findEpisodeById(id);	  
	  if(!GenericUtilities.isNotValidCrid(uptoDateEpisode.getBrightcoveId())){		  		  
			  BCVideoData videoData=videoService.publishEpisodeToBrightcove(id);
			  logger.info("The changes is pushed to the bc:"+videoData);
			  uptoDateEpisode=boxMetadataRepository.findEpisodeById(id);		  
	  }	  
	  return new uk.co.boxnetwork.data.Episode(uptoDateEpisode,null);
  }
  public  uk.co.boxnetwork.data.Series publishMetadatatoBCBySeriesId(Long id){
	  Series series=boxMetadataRepository.findSeriesById(id);
	  List<Episode> episodes=boxMetadataRepository.findEpisodesBySeries(series);
	  logger.info("Publishing all the episodes in this series");
	  for(Episode ep:episodes){
		  publishMetadatatoBCByEpisodeId(ep.getId()); 
	  }	    
	  return new uk.co.boxnetwork.data.Series(series);
  }
  
  public  uk.co.boxnetwork.data.SeriesGroup publishMetadatatoBCBySeriesGroupId(Long id){
	  SeriesGroup seriesgroup=boxMetadataRepository.findSeriesGroupById(id);
	  List<Series> series=boxMetadataRepository.findSeriesBySeriesGroup(seriesgroup);
	  logger.info("Publishing all the episodes in this series group");
	  for(Series sr:series){
		  publishMetadatatoBCBySeriesId(sr.getId()); 
	  }	    
	  return new uk.co.boxnetwork.data.SeriesGroup(seriesgroup);
  }

  
  public void createNewCuepoint(Long episodeid, uk.co.boxnetwork.data.CuePoint cuePoint){
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  CuePoint cue=new CuePoint();
	  cuePoint.update(cue);
	  boxMetadataRepository.persistCuePoint(cue,episode);
	  cuePoint.setId(cue.getId());
  }
  public void createNewAvailability(Long episodeid, uk.co.boxnetwork.data.AvailabilityWindow availabilityWindow){
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  AvailabilityWindow avwindow=new AvailabilityWindow();
	  availabilityWindow.update(avwindow);
	  boxMetadataRepository.persistAvailabilityWindow(avwindow,episode);
	  availabilityWindow.setId(avwindow.getId());
  }
  
  public void updateCuepoint(Long episodeid,Long cueid, uk.co.boxnetwork.data.CuePoint cuePoint){
	  CuePoint cue=boxMetadataRepository.findCuePoint(cueid);
	  if(!cue.getId().equals(cueid)){
		  throw new RuntimeException("Not permitted to update not matching cue cueid=["+cueid+"]cuePoint=["+cuePoint);  
	  }
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  if(!episode.getId().equals(episodeid)){
		  throw new RuntimeException("Not permitted to update not matching cue episodeid=["+episodeid+"]");
	  }
	  boxMetadataRepository.updateCue(cuePoint);	  
	  
  }
  public void updateAvailabilityWindow(Long episodeid,Long availabilitywindowid, uk.co.boxnetwork.data.AvailabilityWindow availabilitywindow){
	  AvailabilityWindow avwindow=boxMetadataRepository.findAvailabilityWindowId(availabilitywindowid);
	  if(!avwindow.getId().equals(availabilitywindowid)){
		  throw new RuntimeException("Not permitted to update not matching availability availabilitywindowid=["+availabilitywindowid+"]avwindow=["+avwindow);  
	  }
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  if(!episode.getId().equals(episodeid)){
		  throw new RuntimeException("Not permitted to update not matching availabilitywindow episodeid=["+episodeid+"]");
	  }
	  boxMetadataRepository.updateAvailabilityWindow(availabilitywindow);	  
	  
  }
  public uk.co.boxnetwork.data.CuePoint deleteCuepoint(Long episodeid, Long cueid){	  	  
	  CuePoint cue=boxMetadataRepository.findCuePoint(cueid);
	  if(cue.getEpisode().getId().equals(episodeid)){
		  boxMetadataRepository.removeCuePoint(cueid);		  
		  return new uk.co.boxnetwork.data.CuePoint(cue);
	  }
	  else{		  	
		  throw new RuntimeException("Not permitted to delete not matching cue cueid=["+cueid+"]episodeid=["+episodeid);		  
	  }
	  
  }
  public uk.co.boxnetwork.data.AvailabilityWindow deleteAvailabilityWindow(Long episodeid, Long avid){	  	  
	  AvailabilityWindow availabilityWindow=boxMetadataRepository.findAvailabilityWindowId(avid);
	  if(availabilityWindow.getEpisode().getId().equals(episodeid)){
		  boxMetadataRepository.removeAvailabilityWindow(avid);		  
		  return new uk.co.boxnetwork.data.AvailabilityWindow(availabilityWindow);
	  }
	  else{		  	
		  throw new RuntimeException("Not permitted to delete not matching availability avid=["+avid+"]episodeid=["+episodeid);		  
	  }
	  
  }
}
