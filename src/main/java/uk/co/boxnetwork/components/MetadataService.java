package uk.co.boxnetwork.components;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileOutputStream;

import java.io.OutputStreamWriter;

import java.io.Writer;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import uk.co.boxnetwork.data.SearchParam;

import uk.co.boxnetwork.data.bc.BCAnalyticsResponse;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.data.s3.MediaFilesLocation;
import uk.co.boxnetwork.data.soundmouse.SoundMouseData;
import uk.co.boxnetwork.data.soundmouse.SoundMouseItem;
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
	
	@Autowired
	CommandServices commandService;

	@Autowired
	BCAnalyticService bcAnalyticService;

	public List<uk.co.boxnetwork.data.SeriesGroup> getAllSeriesGroups(SearchParam searchParam){
		List<uk.co.boxnetwork.data.SeriesGroup> seriesgrps=new ArrayList<uk.co.boxnetwork.data.SeriesGroup>();
		List<SeriesGroup> seriesgroups=boxMetadataRepository.findAllSeriesGroup(searchParam);
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
	
	public List<uk.co.boxnetwork.data.Episode> getAllEpisodes(SearchParam searchParam){		
		return toDataEpisodes(boxMetadataRepository.findAllEpisodes(searchParam));
	}
	/*
	public List<uk.co.boxnetwork.data.Episode> findEpisodes(String search){
		List<Episode> eposides=boxMetadataRepository.findEpisodes(search);
		logger.info("For search:"+search+" found matching:"+eposides.size());
		return toDataEpisodes(eposides);
	}
	*/
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
	 
	
	
public List<uk.co.boxnetwork.data.Series> getAllSeries(SearchParam searchParam){		
		 return toDataSeries(boxMetadataRepository.findAllSeries(searchParam));
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
			logger.warn("not found episode:"+id);
			return null;
		}
		List<ScheduleEvent> scheduleEvents=boxMetadataRepository.findScheduleEventByEpisode(episode);
		
		uk.co.boxnetwork.data.Episode ret=new uk.co.boxnetwork.data.Episode(episode,scheduleEvents);
				
		
		return ret;		
	}
	
	
	
	public String getSoundMouseHeaderFile(Long id) throws Exception{
		Episode episode=boxMetadataRepository.findEpisodeById(id);		
		if(episode==null){
			logger.warn("not found episode:"+id);
			return null;
		}
	
		return GenericUtilities.getSoundmouseHeader(episode);
		
	}
	public String getSoundMouseSmurfFile(Long id) throws Exception{
		Episode episode=boxMetadataRepository.findEpisodeById(id);		
		if(episode==null){
			logger.warn("not found episode:"+id);
			throw new RuntimeException("episode not found");
		}
		
		if(GenericUtilities.isNotAValidId(episode.getBrightcoveId())){
			logger.warn("not found episode:"+id);
			throw new RuntimeException("episode is not published");
		}
		SoundMouseData soundMouseData=new SoundMouseData();				
		String itemContent=buildSoundmouseSmurfItem(soundMouseData,episode);
		String smurfContent=GenericUtilities.getSoundmouseSmurf(soundMouseData);
		return GenericUtilities.createFullSmurfContent(smurfContent,itemContent);
		
	}
	public String buildSoundmouseSmurfItem(SoundMouseData soundMouseData,Episode episode) throws Exception{
		StringBuilder builder=new StringBuilder();
		logger.info("getting report for the range "+soundMouseData+" for id=["+episode.getId()+"]["+episode.getTitle()+"]:"+episode.getBrightcoveId());
		BCAnalyticsResponse bcAnalyticResponse=bcAnalyticService.getMediaItemReport(soundMouseData.getFrom(), soundMouseData.getTo(), episode.getBrightcoveId());
		if(bcAnalyticResponse.getItems()==null|| bcAnalyticResponse.getItems().length==0){
			logger.info("empty analytic response returned for the range "+soundMouseData+" for id=["+episode.getId()+"]["+episode.getTitle()+"]:"+episode.getBrightcoveId());
			return null;
		}				
		if(soundMouseData.init(episode,bcAnalyticResponse.getItems()[0])){
			for(CuePoint cuePoint:episode.getCuePoints()){
				SoundMouseItem soundMouseItem=new SoundMouseItem();
				soundMouseItem.init(cuePoint);
				soundMouseItem.setAnalyticData(bcAnalyticResponse.getItems()[0]);
				builder.append(GenericUtilities.getSoundmouseSmurfForCuepoint(soundMouseData, soundMouseItem));				
			}
		}
		else{
			SoundMouseItem soundMouseItem=new SoundMouseItem();
			soundMouseItem.init(episode);
			soundMouseItem.setAnalyticData(bcAnalyticResponse.getItems()[0]);
			builder.append(GenericUtilities.getSoundmouseSmurfForCuepoint(soundMouseData, soundMouseItem));
		}
		return builder.toString();
	}
	
	
	public SoundMouseData createSoundMouseSmurfFile() throws Exception{
		List<Episode> episodes=boxMetadataRepository.findEpisodeToReport();
		if(episodes.size()==0){
			logger.warn("there is no episode to report");
			return null;
		}		
		SoundMouseData soundMouseData=new SoundMouseData();				
		File smurfItemFile=new File("/tmp/box_"+System.currentTimeMillis()+".txt");
		
		Writer writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(smurfItemFile), "utf-8"));
		try{
				for(Episode episode:episodes){
					String itemContent=buildSoundmouseSmurfItem(soundMouseData,episode);
					if(itemContent!=null && itemContent.length()>0){
						writer.write(itemContent);
					}
				}
		}
		finally{
			writer.close();
		}
		try{
				String smurfContent=GenericUtilities.getSoundmouseSmurf(soundMouseData);
				logger.info("smurfContent::::"+smurfContent);
				logger.info("creating the smurf file:"+soundMouseData.getSmurfFilePath());
				File smurffile=new File(soundMouseData.getSmurfFilePath());
				GenericUtilities.createFullSmurfContent(smurfContent, smurfItemFile, smurffile);
				
		}
		finally{
			smurfItemFile.delete();
		}
		return  soundMouseData;		
	}
	
	public uk.co.boxnetwork.data.CuePoint getCuePointById(Long id){
		CuePoint cuepoint=boxMetadataRepository.findCuePoint(id);
		if(cuepoint==null){
			logger.warn("not found cue:"+id);
			return null;			
		}
		return new uk.co.boxnetwork.data.CuePoint(cuepoint);
		
	}
	public uk.co.boxnetwork.data.AvailabilityWindow getAvailabilityWindowId(Long id){
		AvailabilityWindow availabilityWindow=boxMetadataRepository.findAvailabilityWindowId(id);
		if(availabilityWindow==null){
			logger.warn("not found availability:"+id);
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
			if(GenericUtilities.isNotAValidId(existingEpisode.getBrightcoveId())){
				episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER);
			}
			else{
				episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_PUBLISH_CHANGES);
			}
			
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
		if(existingEpisode==null){
			logger.warn("not found episode:"+id);
			return;
		}
		String oldIngestSource=existingEpisode.getIngestSource();
		String oldIngestProfile=existingEpisode.getIngestProfile();
		logger.info("Before upating the episode:"+existingEpisode);
		episode.update(existingEpisode);
		statusUpdateOnEpisodeUpdated(existingEpisode,oldIngestSource,oldIngestProfile);
		
		boxMetadataRepository.saveTags(episode.getTags());
		logger.info("After upating the episode:"+existingEpisode);
		
		checkS3ToUpdateVidoStatus(existingEpisode);
		if(GenericUtilities.isNotValidCrid(existingEpisode.getImageURL())){
			String fname=retrieveEpisodeImageFromS3(existingEpisode);
			if(fname!=null){
				existingEpisode.setImageURL(fname);				
			}
		}		
		boxMetadataRepository.persist(existingEpisode);		
	}
	@Transactional
	public void switchEpsiodeSeries(long id, uk.co.boxnetwork.data.Episode episode){
		Episode existingEpisode=boxMetadataRepository.findEpisodeById(id);
		if(existingEpisode==null){
			logger.warn("not found episode:"+id);
			return;
		}
		Series series=boxMetadataRepository.findSeriesById(episode.getSeries().getId());
		if(series==null){
			logger.warn("not found series:"+episode.getSeries().getId());
			return;
		}
		existingEpisode.setSeries(series);
		boxMetadataRepository.persist(existingEpisode);		
	}
	
	public void update(long id, uk.co.boxnetwork.data.SeriesGroup seriesGroup){
		SeriesGroup existingSeriesGroup=boxMetadataRepository.findSeriesGroupById(id);		
		if(existingSeriesGroup==null){
			return;
		}
		seriesGroup.update(existingSeriesGroup);
		if(GenericUtilities.isNotValidCrid(existingSeriesGroup.getImageURL())){
			String fname=retrieveSeriesGroupImageFromS3(existingSeriesGroup);
			if(fname!=null){
				existingSeriesGroup.setImageURL(fname);				
			}
		}
		

		boxMetadataRepository.mergeSeriesGroup(existingSeriesGroup);
		
		List<SeriesGroup> matchedSeriesGroups=boxMetadataRepository.findSeriesGroupByTitle(seriesGroup.getTitle());
		mergeSeriesGroupTo(matchedSeriesGroups,existingSeriesGroup);		
	}
	private void mergeSeriesGroupTo(List<SeriesGroup> matchedSeriesGroups, SeriesGroup targetSeriesGroup){
		
		List<SeriesGroup> recordsToDelete=new ArrayList<SeriesGroup>();
		for(SeriesGroup sg:matchedSeriesGroups){
			if(sg.getId()!=null){
				if(!sg.getId().equals(targetSeriesGroup.getId())){
					recordsToDelete.add(sg);
				}
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
					   Double durationUploaded=checkVideoDuration(episode);
					   if(durationUploaded!=null){
						   episode.setDurationUploaded(durationUploaded);
					   }					   
					whenVideoAvailable(episode.getEpisodeStatus(),episode.getIngestProfile());
				}
		}
	}
	
	public String retrieveSeriesGroupImageFromS3(SeriesGroup seriesGroup){
		if(seriesGroup==null){			
			return null;			
		}
		if(GenericUtilities.isNotValidTitle(seriesGroup.getTitle())){
			logger.info("Series group does not have valid title");
			return null;
		}
		String websafeTitle=GenericUtilities.toWebsafeTitle(seriesGroup.getTitle());
		MediaFilesLocation matchedImages=s3BucketService.listMasterImagesInImagesBucket(websafeTitle);
		logger.info("matching the file name in the image s3 bucket:websafeTitle=["+websafeTitle+"]matchedImages:"+matchedImages.getFiles().size());
		return matchedImages.retrieveMatchBasefilename(websafeTitle);		
	}
	public String retrieveSeriesImageFromS3(Series series){
		if(series==null){
			return null;
		}		
		if(GenericUtilities.isNotValidContractNumber(series.getContractNumber())){
			return null;			
		}				
		MediaFilesLocation matchedImages=s3BucketService.listMasterImagesInImagesBucket(series.getContractNumber());
		String matchfilename=matchedImages.retrieveMatchBasefilename(series.getContractNumber());
		if(matchfilename!=null){
			return matchfilename;
		}
		if(GenericUtilities.isNotValidTitle(series.getName())){
			return null;
		}
		String websafetitle=GenericUtilities.toWebsafeTitle(series.getName());	
		matchedImages=s3BucketService.listMasterImagesInImagesBucket(websafetitle+"_"+series.getContractNumber());
		return matchedImages.retrieveMatchBasefilename(websafetitle+"_"+series.getContractNumber());
	}
	public String retrieveEpisodeImageFromS3(Episode episode){
		if(episode==null){
			return null;
		}	
		String matchedfile=null;
		if(!GenericUtilities.isNotValidCrid(episode.getMaterialId())){
			String matid=GenericUtilities.materialIdToImageFileName(episode.getMaterialId());			
			MediaFilesLocation matchedEpisodeImages=s3BucketService.listMasterImagesInImagesBucket(matid);
			matchedfile=matchedEpisodeImages.retrieveMatchBasefilename(matid);
			if(matchedfile!=null){
				return matchedfile;
			}
		}
		if(GenericUtilities.isNotValidContractNumber(episode.getCtrPrg()) || GenericUtilities.isNotValidTitle(episode.getTitle()) ){				
			return null;
		}		
		String websafeTitle=GenericUtilities.toWebsafeTitle(episode.getTitle());
		String matid=GenericUtilities.materialIdToImageFileName(episode.getMaterialId());		
		MediaFilesLocation matchedEpisodeImages=s3BucketService.listMasterImagesInImagesBucket(websafeTitle+"_"+matid);				
		return matchedEpisodeImages.retrieveMatchBasefilename(websafeTitle+"_"+matid);
	}
	
	
	@Transactional
	public uk.co.boxnetwork.data.Episode updateEpisodeById(uk.co.boxnetwork.data.Episode episode){
		Episode episodeInDB=boxMetadataRepository.findEpisodeById(episode.getId());
		if(episodeInDB==null){
			logger.info("not found episode");
			return null;
		}
		if(episode.updateWhenReceivedByMaterialId(episodeInDB)){
			boxMetadataRepository.persist(episodeInDB);
		}
		if(episode.getEpisodeStatus()!=null){
			changeEpisodeStatus(episode,episodeInDB);
		}
		return new uk.co.boxnetwork.data.Episode(episodeInDB,null);
	}
	public void  changeEpisodeStatus(uk.co.boxnetwork.data.Episode episode,Episode episodeInDB){
		
		EpisodeStatus episodeStatus=episode.getEpisodeStatus();
		EpisodeStatus episodeStatusInDB=episodeInDB.getEpisodeStatus();
		if(episodeStatus.getPublishedStatus()==PublishedStatus.ACTIVE){
			
			if(episodeInDB.getBrightcoveId()==null){
				   logger.error("brigtcoveid is null when trying to activate the epsideo");
					throw new RuntimeException("Needs to create the place holder first");
			}
			else if(episodeStatusInDB.canActivate()){				   
				BCVideoData videodata=videoService.changeVideoStatus(episodeInDB.getBrightcoveId(),"ACTIVE");			
				logger.info("*****"+videodata);
				episodeStatusInDB.setPublishedStatus(PublishedStatus.ACTIVE);
				boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
			}
			else{
				logger.error("trying to activate the epsideo not approved");
				throw new RuntimeException("Needs to approve the episode first");
			}
		}
		else if(episodeStatus.getPublishedStatus()==PublishedStatus.INACTIVE){
			if(episodeInDB.getBrightcoveId()==null){				
				if(episodeStatusInDB.getPublishedStatus()==PublishedStatus.ACTIVE){
					episodeStatusInDB.setPublishedStatus(PublishedStatus.INACTIVE);
					boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
					logger.info("INACTIVE because bcid is null");
					
				}
				else{
					logger.info("ignoring deactivation command because bcid is null");
					
				}
			}
			else{
					BCVideoData videodata=videoService.changeVideoStatus(episodeInDB.getBrightcoveId(),"INACTIVE");
					logger.info("INACTIVE is set");
					if(episodeStatusInDB.getPublishedStatus()==PublishedStatus.ACTIVE){
						episodeStatusInDB.setPublishedStatus(PublishedStatus.INACTIVE);
						boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
					}
			}			
		}
		else if(episodeStatus.getPublishedStatus()!=episodeStatusInDB.getPublishedStatus()){
			episodeStatusInDB.setPublishedStatus(episodeStatus.getPublishedStatus());
			boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
		}		
		else{
			logger.info("****publish status not changed:"+episodeStatus.getPublishedStatus());
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
		SeriesGroup existingSeriesGroup=null;	
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
					existingSeriesGroup=existingSeries.getSeriesGroup();
					
				}
		}	
		if(existingSeries==null && episode.getSeries()!=null && episode.getSeries().getId()!=null){
			existingSeries=boxMetadataRepository.findSeriesById(episode.getSeries().getId());
			if(existingEpisode!=null){
				existingSeriesGroup=existingSeries.getSeriesGroup();
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
		    	 	existingSeriesGroup=existingSeries.getSeriesGroup();
		     }
	    }   
		if(existingSeriesGroup==null && episode.getSeries() !=null && episode.getSeries().getSeriesGroup() !=null && (!GenericUtilities.isNotValidTitle(episode.getSeries().getSeriesGroup().getTitle()))){
			List<SeriesGroup> matchedSeriesGroups=boxMetadataRepository.findSeriesGroupByTitle(episode.getSeries().getSeriesGroup().getTitle());
			if(matchedSeriesGroups.size()>0){
				existingSeriesGroup=matchedSeriesGroups.get(0);
			}
			else{
					existingSeriesGroup=new SeriesGroup();
					episode.getSeries().getSeriesGroup().update(existingSeriesGroup);
					boxMetadataRepository.persisSeriesGroup(existingSeriesGroup);
					logger.info("created a nw series group:"+existingSeriesGroup);
			}
		}
		else if(existingSeriesGroup==null){
			existingSeriesGroup=boxMetadataRepository.retrieveDefaultSeriesGroup();
		}
		
		if(existingSeries==null && episode.getSeries()!=null && GenericUtilities.isNotValidTitle(episode.getSeries().getName())){
			existingSeries=new Series();					
			episode.getSeries().update(existingSeries);						
			if(GenericUtilities.isNotValidCrid(existingSeries.getContractNumber())){
				existingSeries.setContractNumber(contractNumber);
			}									
			existingSeries.setSeriesGroup(existingSeriesGroup);
			boxMetadataRepository.persisSeries(existingSeries);
		}
		if(GenericUtilities.isNotValidCrid(existingSeriesGroup.getImageURL())){
			String fname=retrieveSeriesGroupImageFromS3(existingSeriesGroup);
			if(fname!=null){
				if(GenericUtilities.isNotValidCrid(existingSeriesGroup.getImageURL())){
					existingSeriesGroup.setImageURL(fname);
					boxMetadataRepository.mergeSeriesGroup(existingSeriesGroup);
				}
			}
		}
		if(existingSeries!=null){
			checks3ToSetSeriesImage(existingSeries);
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
			if(GenericUtilities.isNotValidCrid(existingEpisode.getImageURL())){
				String fname=retrieveEpisodeImageFromS3(existingEpisode);
				if(fname!=null){
					existingEpisode.setImageURL(fname);				
				}
			}			
			boxMetadataRepository.persist(existingEpisode);
			boxMetadataRepository.replaceAvailabilityWindow(existingEpisode.getId(), Calendar.getInstance().getTime(),GenericUtilities.nextYearDate());
		}		
		else{	
			   logger.info("Upating the existing episode");
			    existingEpisode.setSeries(existingSeries);
				episode.updateWhenReceivedByMaterialId(existingEpisode);				
				boxMetadataRepository.saveTags(episode.getTags());
				statusUpde(existingEpisode);
				if(GenericUtilities.isNotValidCrid(existingEpisode.getImageURL())){
					String fname=retrieveEpisodeImageFromS3(existingEpisode);
					if(fname!=null){
						existingEpisode.setImageURL(s3BucketService.getMasterImageFullURL(fname));				
					}
				}
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
		checks3ToSetSeriesImage(ser);
		return new uk.co.boxnetwork.data.Series(ser);
	}
	
	private void checks3ToSetSeriesImage(Series existingSeries){
		if(GenericUtilities.isNotValidCrid(existingSeries.getImageURL())){
			String fname=retrieveSeriesImageFromS3(existingSeries);
			if(fname!=null){
				existingSeries.setImageURL(fname);
				boxMetadataRepository.mergeSeries(existingSeries);
			}
		}
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
			if(GenericUtilities.isNotValidCrid(newSeriesGroup.getImageURL())){
				String fname=retrieveSeriesGroupImageFromS3(newSeriesGroup);
				if(fname!=null){
					newSeriesGroup.setImageURL(fname);				
				}
			}
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
		if(episodeToUpdate.getCuePoints()!=null){
		    for(CuePoint cp:episodeToUpdate.getCuePoints()){	    	
		    	boxMetadataRepository.remove(cp);	    	
		    }
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
		if(existingSeries==null){
			return;
		}
		series.update(existingSeries);
		checks3ToSetSeriesImage(existingSeries);
		
		boxMetadataRepository.update(existingSeries);
		if(existingSeries.getSeriesGroup()==null){
			existingSeries.setSeriesGroup(boxMetadataRepository.retrieveDefaultSeriesGroup());
			boxMetadataRepository.update(existingSeries);
		}
		
	}
	@Transactional
	public void switchSeriesGroup(long id, uk.co.boxnetwork.data.Series series){
		String seriesGroupTitle=SeriesGroup.DEFAULT_SERIES_GROUP_TITLE;
		if(series.getSeriesGroup()!=null && series.getSeriesGroup().getTitle()!=null && series.getSeriesGroup().getTitle().trim().length()>0){
			seriesGroupTitle=series.getSeriesGroup().getTitle().trim();
		}
		Series existingSeries=boxMetadataRepository.findSeriesById(id);
		if(existingSeries==null){
			return;
		}		
		SeriesGroup seriesGroup=null;
		if(series.getSeriesGroup()!=null && series.getSeriesGroup().getId()!=null){
			seriesGroup=boxMetadataRepository.findSeriesGroupById(series.getSeriesGroup().getId());			
		}
		if(seriesGroup==null){
			List<SeriesGroup> matchedSeriesGroups=boxMetadataRepository.findSeriesGroupByTitle(seriesGroupTitle);
			if(matchedSeriesGroups.size()>0){
				seriesGroup=matchedSeriesGroups.get(0);
			}
		}
		if(seriesGroup==null){
			seriesGroup=new SeriesGroup();
			seriesGroup.setTitle(seriesGroupTitle);
			boxMetadataRepository.persisSeriesGroup(seriesGroup);
		}
		existingSeries.setSeriesGroup(seriesGroup);
		boxMetadataRepository.update(existingSeries);
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
		   Double durationUploaded=checkVideoDuration(episode);
		   if(durationUploaded!=null){
			   episode.setDurationUploaded(durationUploaded);
		   }
		   boxMetadataRepository.persist(episode);
		   
	   }
	   else{
		   logger.warn("more than epsiode matching the materialId=["+materialId+"] while binding video file:"+ingestFile+"]");
		   for(Episode episode: matchedEpisodes){
			   if(episode.getIngestSource()!=null){
				   episode.setIngestSource(s3BucketService.getFullVideoURL(ingestFile));
				   statusBoundSourceVideo(episode);
				   
				   Double durationUploaded=checkVideoDuration(episode);
				   if(durationUploaded!=null){
					   episode.setDurationUploaded(durationUploaded);
				   }
				   boxMetadataRepository.persist(episode);
			   }
		   }
	   }
	   
	   
   }
   
   public Double checkVideoDuration(Episode episode){
	   try{
		   if(GenericUtilities.isNotValidCrid(episode.getIngestSource())){
			   return null;
		   }
		   String url=s3BucketService.generatedPresignedURL(episode.getIngestSource(), 20);
		   String commandResult=commandService.getVideoDuration(url);
		   Double duration=Double.valueOf(commandResult);		   
		   
		   return duration;
	   }
	   catch(Exception e){
		   logger.error(e+" while getting the duration:"+e,e);
		   return null;
	   }
   }
   
   public void deleteEpisodeById(Long episodeid){	   
		 boxMetadataRepository.removeEpisode(episodeid);
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
		 MediaFilesLocation matchedfiles=s3BucketService.listFilesInVideoBucket(fileNameFilter);
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
		  if(episodeStatus.getMetadataStatus()!=MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER){			  			 
			  episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER);
			  episodeStatus.setVideoStatus(VideoStatus.NO_PLACEHOLDER);
			  boxMetadataRepository.persistEpisodeStatus(episodeStatus);
		  }
		  else if(episodeStatus.getVideoStatus()!=VideoStatus.NO_PLACEHOLDER){
			  episodeStatus.setVideoStatus(VideoStatus.NO_PLACEHOLDER);
			  boxMetadataRepository.persistEpisodeStatus(episodeStatus);
		  }
	  }
	  else{
			  BCVideoData videoData=videoService.getVideo(episode.getBrightcoveId());
			  if("INACTIVE".equals(videoData.getState())){
				  if(episodeStatus.getPublishedStatus()==PublishedStatus.ACTIVE){					  
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
			  if(episodeStatus.getMetadataStatus()==MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER){
				  episodeStatus.setMetadataStatus(MetadataStatus.PUBLISHED);				  
				  boxMetadataRepository.persistEpisodeStatus(episodeStatus);
			  }
	  }
  }
  public  uk.co.boxnetwork.data.Episode publishMetadatatoBCByEpisodeId(Long id){
	  Episode uptoDateEpisode=boxMetadataRepository.findEpisodeById(id);	
	  if(uptoDateEpisode==null){
		  logger.warn("not found episode:"+id);
		  return null;
	  }
	  if(!GenericUtilities.isNotValidCrid(uptoDateEpisode.getBrightcoveId())){		  		  
			  BCVideoData videoData=videoService.publishEpisodeToBrightcove(id);
			  logger.info("The changes is pushed to the bc:"+videoData);
			  uptoDateEpisode=boxMetadataRepository.findEpisodeById(id);		  
	  }	  
	  return new uk.co.boxnetwork.data.Episode(uptoDateEpisode,null);
  }
  
  public  uk.co.boxnetwork.data.Series publishMetadatatoBCBySeriesId(Long id){
	  Series series=boxMetadataRepository.findSeriesById(id);
	  if(series==null){
		  logger.warn("not found series:"+id);
		  return null;
	  }
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
	  if(episode==null){
		  logger.warn("not found episode:"+episodeid);
		  return;
	  }
	  CuePoint cue=new CuePoint();
	  cuePoint.update(cue);
	  boxMetadataRepository.persistCuePoint(cue,episode);
	  cuePoint.setId(cue.getId());
  }
  public void createNewAvailability(Long episodeid, uk.co.boxnetwork.data.AvailabilityWindow availabilityWindow){
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  if(episode==null){
		  logger.warn("not found episode:"+episodeid);
		  return;
	  }
	  AvailabilityWindow avwindow=new AvailabilityWindow();
	  availabilityWindow.update(avwindow);
	  boxMetadataRepository.persistAvailabilityWindow(avwindow,episode);
	  availabilityWindow.setId(avwindow.getId());
  }
  
  public void updateCuepoint(Long episodeid,Long cueid, uk.co.boxnetwork.data.CuePoint cuePoint){
	  CuePoint cue=boxMetadataRepository.findCuePoint(cueid);
	  if(cue.getId()!=null){
		  if(!cue.getId().equals(cueid)){
			  throw new RuntimeException("Not permitted to update not matching cue cueid=["+cueid+"]cuePoint=["+cuePoint);  
		  }
	  }
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  if(episode.getId()!=null){
		  if(!episode.getId().equals(episodeid)){
			  throw new RuntimeException("Not permitted to update not matching cue episodeid=["+episodeid+"]");
		  }
	  }
	  boxMetadataRepository.updateCue(cuePoint);	  
	  
  }
  public void updateAvailabilityWindow(Long episodeid,Long availabilitywindowid, uk.co.boxnetwork.data.AvailabilityWindow availabilitywindow){
	  AvailabilityWindow avwindow=boxMetadataRepository.findAvailabilityWindowId(availabilitywindowid);
	  if(avwindow.getId()!=null){
		  if(!avwindow.getId().equals(availabilitywindowid)){
			  throw new RuntimeException("Not permitted to update not matching availability availabilitywindowid=["+availabilitywindowid+"]avwindow=["+avwindow);  
		  }
	  }
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  if(episode.getId()!=null){
		  if(!episode.getId().equals(episodeid)){
			  throw new RuntimeException("Not permitted to update not matching availabilitywindow episodeid=["+episodeid+"]");
		  }
	  }
	  boxMetadataRepository.updateAvailabilityWindow(availabilitywindow);	  
	  
  }
  public uk.co.boxnetwork.data.CuePoint deleteCuepoint(Long episodeid, Long cueid){	  	  
	  CuePoint cue=boxMetadataRepository.findCuePoint(cueid);
	  if(cue==null){
		  logger.error("could not find the cue point to delete cueid=["+cueid+"]");
		  return null;
	  }
	  if(cue.getEpisode().getId()!=null && cue.getEpisode().getId().equals(episodeid)){
		  boxMetadataRepository.removeCuePoint(cueid);		  
		  return new uk.co.boxnetwork.data.CuePoint(cue);
	  }
	  else{		  	
		  throw new RuntimeException("Not permitted to delete not matching cue cueid=["+cueid+"]episodeid=["+episodeid);		  
	  }
	  
  }
  public uk.co.boxnetwork.data.AvailabilityWindow deleteAvailabilityWindow(Long episodeid, Long avid){	  	  
	  AvailabilityWindow availabilityWindow=boxMetadataRepository.findAvailabilityWindowId(avid);
	  if(availabilityWindow==null){
		  return null;
	  }
	  if(availabilityWindow.getEpisode().getId()!=null && availabilityWindow.getEpisode().getId().equals(episodeid)){
		  boxMetadataRepository.removeAvailabilityWindow(avid);		  
		  return new uk.co.boxnetwork.data.AvailabilityWindow(availabilityWindow);
	  }
	  else{		  	
		  throw new RuntimeException("Not permitted to delete not matching availability avid=["+avid+"]episodeid=["+episodeid);		  
	  }
	  
  }
  
  
  private boolean matchImageToEpisodes(List<Episode> matchedEpisodes,String imagefile){
	  if(matchedEpisodes==null||matchedEpisodes.size()==0){
		  return false;
	  }	 
	   for(Episode episode:matchedEpisodes){					  					  
			  if(GenericUtilities.isNotValidCrid(episode.getImageURL())){
				  logger.info("setting imageURL for episode:"+episode.getId()+":"+imagefile+":"+imagefile);
				  boxMetadataRepository.setEpisodeImage(episode.getId(), imagefile);
			  }
			  else{
				  logger.info("imageURL is already set for episode:"+episode.getId()+":"+imagefile+":"+imagefile);						  
			  }
	   }				  
	  return true;
  }
  private boolean matchImageToSeries(List<Series> matchedSeries,String ImageName){
	  if(matchedSeries==null || matchedSeries.size()==0){
		  return false;
	  }
	  for(Series series:matchedSeries){					  					  
			  if(GenericUtilities.isNotValidCrid(series.getImageURL())){
				  logger.info("setting imageURL for series:"+series.getId()+":"+ImageName);
				  boxMetadataRepository.setSeriesImage(series.getId(), ImageName);
			  }
			  else{
				  logger.info("imageURL is already set for series:"+series.getId()+":"+ImageName);						  
			  }		  
	   }				  
		  return true;
  }
private boolean matchImageToSeriesGroup(List<SeriesGroup> matchedSeriesGroup,String imagefile){
	if(matchedSeriesGroup==null || matchedSeriesGroup.size()==0){
		  return false;
	  }	  
	 for(SeriesGroup seriesgroup:matchedSeriesGroup){
			  if(GenericUtilities.isNotValidCrid(seriesgroup.getImageURL())){
				  logger.info("setting imageURL for series group:"+seriesgroup.getId()+":"+imagefile);
				  boxMetadataRepository.setSeriesGroupImage(seriesgroup.getId(), imagefile);
			  }
			  else{
				  logger.info("imageURL is already for series group:"+seriesgroup.getId()+":"+imagefile);
			  }
		  }
	 return true;
	}

  public void notifyMasterImageDelete(String imagefile){
	  String basename=imagefile;
		
		int ib=imagefile.indexOf(".");
						
		if(ib!=-1){
			 basename=imagefile.substring(0,ib);
			 if(basename.length()<=1){
				 logger.error("filename too short, so will not be ldeted");
				 return;
			 }
		}
		
		List<FileItem> items=s3BucketService.listGenereratedImages(basename);
		for(FileItem item:items){
			logger.info("Deleting the file:"+item.getFile());
			s3BucketService.deleteImagesInImageBucket(item.getFile());
		}
		
	  
  }
  @Transactional
  public void setSeriesImage(Long seriesid, String imageURL){
	  Series series=boxMetadataRepository.findSeriesById(seriesid);
	  series.setImageURL(imageURL);
	  boxMetadataRepository.persisSeries(series);	  
	  logger.info("clearing the image in the series");
	  
	  
  }
  @Transactional
  public void setEpisodeImage(Long episodeid, String imageURL){
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  episode.setImageURL(imageURL);
	  boxMetadataRepository.persist(episode);	  
	  logger.info("clearing the image in the episode");
  }
  
  public void notifyMasterImageUploaded(String imagefile){
	  logger.info("processing mater image notification:"+imagefile);
	  if(imagefile.length()==0){
		  logger.info("ingoring:"+imagefile);
		  return;
	  }	 
	  int ie=imagefile.indexOf(".");
	  if(ie<=0){
		  logger.info("ignoring the  file:"+imagefile);
		  return;
	  }
	  
	  String parts[]=imagefile.substring(0,ie).split("_");
	  
	  if(parts.length==0 || parts[0].length()==0){
		  logger.error("the part of the image is _");
		  return;
	  }
	  
	  	        
	  if(parts.length==1){		  			  
		      List<Series> matchedSeries=boxMetadataRepository.findSeriesByContractNumber(parts[0]);
			  if(matchImageToSeries(matchedSeries,imagefile)){
				  logger.info("matched contractnumber for the series");				  
			  }	
			  List<SeriesGroup> matchedSeriesGroup=boxMetadataRepository.findSeriesGroupByTitle(GenericUtilities.fromWebsafeTitle(parts[0]));
			  if(matchImageToSeriesGroup(matchedSeriesGroup,imagefile)){
				  logger.info("matched series group with title");				
			  }	
			  return;
	  }
	  List<Episode> matchedEpisodes=boxMetadataRepository.findEpisodesByMatId(GenericUtilities.partsToMatId(parts,0));
	  if(matchImageToEpisodes(matchedEpisodes, imagefile)){
		  logger.info("matched material id");
		  return;
	  }	
	  String matchTitle=GenericUtilities.fromWebsafeTitle(parts[0]);
	  String matid=GenericUtilities.partsToMatId(parts, 1);	  
      
	  List<Series> matchedSeries=boxMetadataRepository.findSeriesByNameAndContractNumber(matchTitle,matid);
	 
	   if(matchImageToSeries(matchedSeries, imagefile)){
		  logger.info("matched series by name and contract number");
		  return;	
	   }
	  
	  matchedEpisodes=boxMetadataRepository.findEpisodesByTitleAndProgramId(matchTitle,matid);
	  
      if(matchImageToEpisodes(matchedEpisodes, imagefile)){
			  logger.info("matched episodes for title and programmeid");
			  return;
	  }
      
      if(parts.length>2){
    	  matchedEpisodes=boxMetadataRepository.findEpisodesByCtrPrg(matid);
    	  matchImageToEpisodes(matchedEpisodes, imagefile);
      }      
  }
  
  
  @Transactional
  public Episode importEpisode(Episode episode){
	  Series series=null;  
	  SeriesGroup seriesGroup=null;
	  
	  String contractNumber=null;
	  
	  String progrimeId=episode.getCtrPrg();
	  if(GenericUtilities.isNotValidCrid(progrimeId)){
		  throw new RuntimeException("Episode does not have a valid programmeId");
	  }
	  if(GenericUtilities.isNotValidTitle(episode.getTitle())){
		  throw new RuntimeException("Episode does not have a valid title");
	  }
	  List<Episode> matchedEpisodes=boxMetadataRepository.findEpisodesByCtrPrg(progrimeId);
	  if(matchedEpisodes.size()>0){
		  throw new RuntimeException("The episode with programmeId:["+progrimeId+"] already exists");
	  }
	  
	  if(episode.getSeries()!=null){
		  contractNumber=episode.getSeries().getContractNumber();
	  }
	  if(GenericUtilities.isEmpty(contractNumber)){
		  series=boxMetadataRepository.retrieveDefaultSeries();		  
	  }
	  else{
		    List<Series> matchedSeries=boxMetadataRepository.findSeriesByContractNumber(contractNumber);
		    if(matchedSeries.size()==0){
		    	series=episode.getSeries();		    	
		    	if(series.getSeriesGroup()!=null && (!GenericUtilities.isNotValidTitle(series.getSeriesGroup().getTitle()))){
		    		boxMetadataRepository.persisSeriesGroup(series.getSeriesGroup());
		    	}
		    	else{
		    		series.setSeriesGroup(boxMetadataRepository.retrieveDefaultSeriesGroup());
		    	}		    	
		    	boxMetadataRepository.persisSeries(series);
		    }
		    else{		    	
		    	series=matchedSeries.get(0);		    	
		    }
		    
	  }
	  episode.setSeries(series);
	  
	  if(!GenericUtilities.isEmpty(episode.getTags())){
		  String atgs[]=GenericUtilities.commandDelimitedToArray(episode.getTags());
		  boxMetadataRepository.saveTags(atgs);
	  }
	  episode.setIngestSource(null);
	  boxMetadataRepository.persist(episode);
	  if(episode.getCuePoints()!=null && episode.getCuePoints().size()>0){
		  for(CuePoint cuepoint:episode.getCuePoints()){
			  boxMetadataRepository.persist(cuepoint);
		  }
	  }
	  if(episode.getAvailabilities()!=null && episode.getAvailabilities().size()>0){
		  for(AvailabilityWindow availabilityWindow:episode.getAvailabilities()){
			  boxMetadataRepository.persist(availabilityWindow);
		  }
	  }
	  boxMetadataRepository.persistEpisodeStatus(episode.getEpisodeStatus());
	  
		checkS3ToUpdateVidoStatus(episode);
		if(GenericUtilities.isNotValidCrid(episode.getImageURL())){
			String fname=retrieveEpisodeImageFromS3(episode);
			if(fname!=null){
				episode.setImageURL(fname);
				boxMetadataRepository.persist(episode);
			}
		}	

		
	  
	  return episode;
  }
}
