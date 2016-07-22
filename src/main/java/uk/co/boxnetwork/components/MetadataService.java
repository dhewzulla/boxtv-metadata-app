package uk.co.boxnetwork.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.data.SeriesGroup;
import uk.co.boxnetwork.model.CuePoint;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class MetadataService {
	private static final Logger logger=LoggerFactory.getLogger(MetadataService.class);
	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	@Autowired
	S3BucketService s3BucketService;
	  	
	
	public List<SeriesGroup> getAllProgrammes(){
		List<SeriesGroup> programmes=new ArrayList<SeriesGroup>();
		List<uk.co.boxnetwork.model.Programme> prgs=boxMetadataRepository.findAllProgramme();
		for(uk.co.boxnetwork.model.Programme prg:prgs){
			programmes.add(new SeriesGroup(prg));
		}
		return programmes;
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
	@Transactional
	public void update(long id, uk.co.boxnetwork.data.Episode episode){
		Episode existingEpisode=boxMetadataRepository.findEpisodeById(id);
		episode.update(existingEpisode);
		boxMetadataRepository.update(existingEpisode);
		
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
		String programmeId=GenericUtilities.materialIdToProgrammeId(materiaId);
		
		List<Episode> existingEpisodes=boxMetadataRepository.findEpisodesByCtrPrg(programmeId);
		if(existingEpisodes.size()==0){
			throw new RuntimeException("episode not found");
		}
		if(existingEpisodes.size()>1){
			throw new RuntimeException("more than one episodes matched to the materia id:"+materiaId);
		}
		Episode episodeToUpdate=existingEpisodes.get(0); 
		episode.updateWhenReceivedByMaterialId(episodeToUpdate);		
		replaceCuePoints(episodeToUpdate,episode.getCuePoints());
		
		boxMetadataRepository.mergeEpisode(episodeToUpdate);
		uk.co.boxnetwork.data.Episode ret=new uk.co.boxnetwork.data.Episode(episodeToUpdate,null);
		ret.setComplianceInformations(episodeToUpdate.getComplianceInformations());
		return ret;
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
		   boxMetadataRepository.mergeEpisode(episode);		   
	   }
	   else{
		   logger.warn("more than epsiode matching the materialId=["+materialId+"] while binding video file:"+ingestFile+"]");
		   for(Episode episode: matchedEpisodes){
			   if(episode.getIngestSource()!=null){
				   episode.setIngestSource(s3BucketService.getFullVideoURL(ingestFile));
				   boxMetadataRepository.mergeEpisode(episode);
			   }
		   }
	   }
	   
	   
   }
   
   
	
	
	
}
