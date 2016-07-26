package uk.co.boxnetwork.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.data.s3.VideoFilesLocation;
import uk.co.boxnetwork.model.CuePoint;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.EpisodeStatus;
import uk.co.boxnetwork.model.MetadataStatus;
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
	
	public List<uk.co.boxnetwork.data.Episode> getAllEpisodes(){
		
		return toData(boxMetadataRepository.findAllEpisodes());
	}
	public List<uk.co.boxnetwork.data.Episode> findEpisodes(String search){
		List<Episode> eposides=boxMetadataRepository.findEpisodes(search);
		return toData(eposides);
	}
	private  List<uk.co.boxnetwork.data.Episode>  toData(List<Episode> eposides){
		List<uk.co.boxnetwork.data.Episode> ret=new ArrayList<uk.co.boxnetwork.data.Episode>();
		for(Episode episode:eposides){
			ret.add(new uk.co.boxnetwork.data.Episode(episode,null));
		}
		
		return ret;
	}
	
public List<Series> getAllSeries(){		
		 return boxMetadataRepository.findAllSeries();
	}
public uk.co.boxnetwork.data.Series getSeriesById(Long id){
	Series series=boxMetadataRepository.findSeriesById(id);
	if(series==null){
		return null;
	}
	List<Episode> episodes=boxMetadataRepository.findEpisodesBySeries(series);
	uk.co.boxnetwork.data.Series ret=new uk.co.boxnetwork.data.Series(series);
	for(Episode episode:episodes){
		episode.setSeries(null);
		episode.setComplianceInformations(null);
		episode.setCuePoints(null);
	}
	ret.setEpisodes(episodes);		
	return ret;		
}


	public uk.co.boxnetwork.data.Episode getEpisodeById(Long id){
		Episode episode=boxMetadataRepository.findEpisodeById(id);
		List<ScheduleEvent> scheduleEvents=boxMetadataRepository.findScheduleEventByEpisode(episode);
		
		uk.co.boxnetwork.data.Episode ret=new uk.co.boxnetwork.data.Episode(episode,scheduleEvents);
				
		
		return ret;		
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
			boxMetadataRepository.persist(episodeStatus);
    	}
    	else{
    		boxMetadataRepository.merge(episodeStatus);
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
 	    boxMetadataRepository.merge(episodeStatus);
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
			boxMetadataRepository.persist(episodeStatus);
    	}
    	else{
    		boxMetadataRepository.merge(episodeStatus);
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
			boxMetadataRepository.merge(episodeStatus);
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
		
		Series series=createNewSeries(episode.getSeries(),contractNumber);
		
		String programmeId=GenericUtilities.getProgrammeNumber(episode);
		Episode existingEpisode=null;
		
		
		List<Episode> existingEpisodes=boxMetadataRepository.findEpisodesByCtrPrg(programmeId);
		if(existingEpisodes.size()==0){
			existingEpisode=new Episode();
			existingEpisode.setSeries(series);
			episode.update(existingEpisode);
			EpisodeStatus episodeStatus=new EpisodeStatus();
			episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER);
			episodeStatus.setVideoStatus(VideoStatus.NO_PLACEHOLDER);
			
			boxMetadataRepository.persist(episodeStatus);
			existingEpisode.setEpisodeStatus(episodeStatus);
			boxMetadataRepository.saveTags(episode.getTags());
		}
		else if(existingEpisodes.size()>1){
			throw new RuntimeException("more than one episodes matched to the materia id:"+materiaId);
		}
		else{
				existingEpisode=existingEpisodes.get(0);
				logger.info("Before upating the episode with material id:"+existingEpisode);
				existingEpisode.setSeries(series);
				episode.updateWhenReceivedByMaterialId(existingEpisode);
				statusUpde(existingEpisode);
				boxMetadataRepository.saveTags(episode.getTags());
				
		}
		replaceCuePoints(existingEpisode,episode.getCuePoints());
		checkS3ToUpdateVidoStatus(existingEpisode);
		if(existingEpisode.getId()==null){
			
			boxMetadataRepository.persist(existingEpisode);
		}
		else{		
				logger.info("After upating the episode with material id:"+existingEpisode);
				boxMetadataRepository.persist(existingEpisode);
		}				
		uk.co.boxnetwork.data.Episode ret=new uk.co.boxnetwork.data.Episode(existingEpisode,null);
		ret.setComplianceInformations(existingEpisode.getComplianceInformations());
		return ret;
	}
	
	public Series createNewSeries(uk.co.boxnetwork.data.Series series, String contractNumber){
		if(series==null){
			return null;
		}		
		SeriesGroup sg=createNewSeriesGroup(series.getSeriesGroup());
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
	public SeriesGroup createNewSeriesGroup(uk.co.boxnetwork.data.SeriesGroup seriesGroup){
		if(seriesGroup==null){
			return null;
		}
		if(GenericUtilities.isNotValidTitle(seriesGroup.getTitle())){
			return null;			
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
			     CuePoint cue=new CuePoint();
			     cuepoint.update(cue);			     
			     episodeToUpdate.addCuePoint(cue);
			     cue.setEpisode(episodeToUpdate);
			     boxMetadataRepository.merge(cue);
			     
		}		
	}
	
	
	@Transactional
	public void update(long id, uk.co.boxnetwork.data.Series series){
		Series existingSeries=boxMetadataRepository.findSeriesById(id);
		series.update(existingSeries);
		boxMetadataRepository.update(existingSeries);
		
	}
		
	public List<uk.co.boxnetwork.data.ScheduleEvent> getAllScheduleEvent(){
		List<ScheduleEvent> schedules= boxMetadataRepository.findAllScheduleEvent();
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

	
	
}
