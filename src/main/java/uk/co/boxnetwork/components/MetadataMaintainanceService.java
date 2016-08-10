package uk.co.boxnetwork.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.data.AppConfig;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.app.MediaCommand;
import uk.co.boxnetwork.data.bc.BCVideoSource;
import uk.co.boxnetwork.model.AvailabilityWindow;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.EpisodeStatus;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.model.SeriesGroup;
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
	
	
	@Autowired	
	private EntityManager entityManager;

	@Autowired
    private DataSource datasource;
	
	@Autowired
	MetadataService metataService;
	
	@Autowired
	AppConfig appConfig;
	
	
	private static final Logger logger=LoggerFactory.getLogger(MetadataMaintainanceService.class);
	
	
	@Transactional
	public void fixTxChannel(){
		logger.info("fixing the channel......");
		
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}
				for(Episode episode:episodes){
					if(fixTxChannel(episode)){													  
						repository.persist(episode);
					}			
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
	}
	
	
	public void setAvailableWindowForAll(Date from, Date to){
logger.info("adding the default availability window......");
		
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}
				for(Episode episode:episodes){
					setAvailableWindow(episode, from,to);													  
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
	}
	public void setAvailableWindow(Episode episode,Date from, Date to){
		repository.replaceAvailabilityWindow(episode.getId(), from, to);
		
		
	}
	public boolean fixTxChannel(Episode episode){
		if("Box Hits (SmashHits)".equals(episode.getTxChannel())){		  			  
			  episode.setTxChannel("Box Hits");
			  return true;
		}
		else if("Box Upfront (Heat)".equals(episode.getTxChannel())){			  
			  episode.setTxChannel("Box Upfront");
			  return true;
		 }		
		else
			return false;
	}
	
	public boolean fixTxChannel(uk.co.boxnetwork.data.Episode episode){
		if("Box Hits (SmashHits)".equals(episode.getTxChannel())){		  			  
			  episode.setTxChannel("Box Hits");
			  return true;
		}
		else if("Box Upfront (Heat)".equals(episode.getTxChannel())){			  
			  episode.setTxChannel("Box Upfront");
			  return true;
		 }		
		else
			return false;
	}
	@Transactional
    public void fixEpisodeStatusIfEmpty(){
    	logger.info("fixing the episode status......");
    	
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}		
							
					for(Episode episode:episodes){
							if(episode.getEpisodeStatus()==null){													  
								fixEpisodeStatusIfEmpty(episode);
								repository.persist(episode);
							}							
					}
					if(searchParam.isEnd(episodes.size())){
						break;
					}
					else{
						searchParam.nextBatch();
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
    			
    			if(videos!=null && videos.length>=2){
    				episodeStatus.setVideoStatus(VideoStatus.TRANSCODE_COMPLETE);
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
    		repository.persistEpisodeStatus(episodeStatus);
    	}
    	else{
    		repository.persistEpisodeStatus(episodeStatus);
    	}
    	episode.setEpisodeStatus(episodeStatus);    	
    }
    
    
    @Transactional     
    public void replaceIngestProfiles(String oldIngestProfile,String newIngestProfile){
    	logger.info("repacing the episode ingestProfile:oldIngestProfile="+oldIngestProfile+" newIngestProfile="+newIngestProfile);
    	
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}								
				for(Episode episode:episodes){
					replaceIngestProfiles(episode,oldIngestProfile,newIngestProfile);												  					
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
		logger.info("Completed the ingestprofile changes");
    }
    public void replaceIngestProfiles(Episode episode,String oldIngestProfile,String newIngestProfile){
    	if(episode.getIngestProfile()==null || (!episode.getIngestProfile().equals(oldIngestProfile))){
    		return;
    	}
    	episode.setIngestProfile(newIngestProfile);
    	EpisodeStatus episodeStatus=episode.getEpisodeStatus();
    	if(episodeStatus.getVideoStatus()==VideoStatus.TRANSCODE_COMPLETE ||episodeStatus.getVideoStatus()==VideoStatus.TRANSCODING){
    		episodeStatus.setVideoStatus(VideoStatus.NEEDS_RETRANSCODE);
    		repository.persistEpisodeStatus(episodeStatus);
    	}
    	repository.persist(episode);    	
    }
    
    
    public void removeOrphantSeriesGroup(){
    	SearchParam searchParam=new SearchParam(null, 0, appConfig.getRecordLimit());
    	while(true){
		    	List<SeriesGroup> seriesGroups=repository.findAllSeriesGroup(searchParam);
		    	if(seriesGroups.size()==0){
					break;
				}
		    	List<SeriesGroup> orphanned=new ArrayList<SeriesGroup>();
		    	
		    	for(SeriesGroup sg:seriesGroups){
		    		List<Series> series=repository.findSeriesBySeriesGroup(sg);
		    		if(series.size()==0){
		    			orphanned.add(sg);
		    		}
		    	}
		    	repository.removeSeriesGroup(orphanned);
		    	if(searchParam.isEnd(seriesGroups.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
    	}
    }
    
    
    
    
    
    
   
    
    public void dropProgrammeColumns(){
    //	JdbcTemplate jdbcTemlate=new JdbcTemplate(datasource);
    	//jdbcTemlate.execute("ALTER TABLE episode DROP COLUMN programme_id");
    	//jdbcTemlate.execute("ALTER TABLE schedule_event DROP COLUMN programme_id");
    	//jdbcTemlate.execute("ALTER TABLE series DROP COLUMN programme_id");
    	
//    	jdbcTemlate.execute("ALTER TABLE episode DROP FOREIGN KEY programme_id");
//    	jdbcTemlate.execute("ALTER TABLE schedule_event DROP FOREIGN KEY programme_id");
//    	jdbcTemlate.execute("ALTER TABLE series DROP FOREIGN KEY programme_id");
    	//jdbcTemlate.execute("ALTER TABLE programme DROP FOREIGN KEY programme_id");
    	    	
    }
    @Transactional
    public void dropProgrammeTable(){
    	JdbcTemplate jdbcTemlate=new JdbcTemplate(datasource);
    	
    	jdbcTemlate.execute("DROP TABLE programme");
    }

    public void updateAllPublishedStatys(){
    	
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}			
				for(Episode episode:episodes){
					updatePublishedStatus(episode);  
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
    }
    private void updatePublishedStatus(Episode episode){
    	metataService.updatePublishedStatus(episode);
    }
    
    public  MediaCommand processCommand(MediaCommand mediaCommand){
    	if("publish-all-changes".equals(mediaCommand.getCommand())){
    		
    		pushAllChangesToBrightcove();    		
    	}
    	else{
    		logger.info("ignore the command");
    	}
    	return mediaCommand;
     }
    
    
    public void pushAllChangesToBrightcove(){
    	
logger.info("pushing all changes to brightcove......");
		
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}
				for(Episode episode:episodes){
					pushChangesToBrightcove(episode);												  
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
    }
    
    void pushChangesToBrightcove(Episode episode){
    	if(episode.getEpisodeStatus().getMetadataStatus()==MetadataStatus.NEEDS_TO_PUBLISH_CHANGES){
    		metataService.publishMetadatatoBCByEpisodeId(episode.getId());
    	}
    	
    }
    
}
