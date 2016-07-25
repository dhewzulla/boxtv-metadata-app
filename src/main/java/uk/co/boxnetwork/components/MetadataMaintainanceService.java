package uk.co.boxnetwork.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.data.bc.BCVideoSource;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.EpisodeStatus;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.model.TimedTask;
import uk.co.boxnetwork.model.VideoStatus;
import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class MetadataMaintainanceService {
	@Autowired
	private BoxMedataRepository repository;

	@Autowired
	private BCVideoService videoService;
	

	@Autowired
	private TimedTaskService timedTasks;

	private static final Logger logger=LoggerFactory.getLogger(MetadataMaintainanceService.class);
	
	@Transactional
	public void fixTxChannel(){
		logger.info("fixing the channel......");
		List<Episode> episodes=repository.findAllEpisodes();
		
		for(Episode episode:episodes){
			if(fixTxChannel(episode)){													  
				repository.mergeEpisode(episode);
			}			
		}
	}
	public boolean fixTxChannel(Episode episode){
		if("Box Upfront".equals(episode.getTxChannel())){
			episode.setTxChannel("Box Upfront (Heat)");
			return true;
		}
		else
			return false;
	}
	
	public boolean fixTxChannel(uk.co.boxnetwork.data.Episode episode){
		if("Box Upfront".equals(episode.getTxChannel())){
			episode.setTxChannel("Box Upfront (Heat)");
			return true;
		}
		else
			return false;
	}
	@Transactional
    public void fixEpisodeStatusIfEmpty(){
    	logger.info("fixing the episode status......");
		List<Episode> episodes=repository.findAllEpisodes();		
		for(Episode episode:episodes){
			if(episode.getEpisodeStatus()==null){													  
				fixEpisodeStatusIfEmpty(episode);
				repository.mergeEpisode(episode);
			}			
		}
		logger.info("Completed the fixing the episode status");
    }
	@Transactional
    public void deleteAllTasls(){
    	logger.info("deleting all tasks......");
    	List<TimedTask> tasks=timedTasks.findAllTimedTasks();
    	for(TimedTask task: tasks){
    		timedTasks.remove(task);
    	}
    	logger.info("Completed the deleting the tasks");
    }
    public void fixEpisodeStatusIfEmpty(Episode episode){    	
    	if(episode.getEpisodeStatus()!=null){
    		return;
    	}
    	EpisodeStatus episodeStatus=new EpisodeStatus();
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
    	else{
    			BCVideoSource[] videos=videoService.getVideoSource(episode.getBrightcoveId());
    			
    			if(videos!=null && videos.length>2){
    				episodeStatus.setVideoStatus(VideoStatus.TRANSCODED);
    				episodeStatus.setNumberOfTranscodedFiles(videos.length);
    			}
    			else{
    				episodeStatus.setVideoStatus(VideoStatus.NEEDS_TRANSCODE);
    				if(videos!=null){
    					episodeStatus.setNumberOfTranscodedFiles(videos.length);
    				}
    			}
    	}
    	if(episodeStatus.getId()==null){
    		repository.persist(episodeStatus);
    	}
    	else{
    		repository.merge(episodeStatus);
    	}
    	episode.setEpisodeStatus(episodeStatus);    	
    }

}
