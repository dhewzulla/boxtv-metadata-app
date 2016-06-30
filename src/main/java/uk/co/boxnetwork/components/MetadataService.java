package uk.co.boxnetwork.components;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.data.Programme;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.ScheduleEvent;

@Service
public class MetadataService {

	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	
	public List<Programme> getAllProgrammes(){
		List<Programme> programmes=new ArrayList<Programme>();
		List<uk.co.boxnetwork.model.Programme> prgs=boxMetadataRepository.findAllProgramme();
		for(uk.co.boxnetwork.model.Programme prg:prgs){
			programmes.add(new Programme(prg));
		}
		return programmes;
	}
	
	
	public List<Episode> getAllEpisodes(){
		
		return boxMetadataRepository.findAllEpisodes();
	}
	public uk.co.boxnetwork.data.Episode getEpisodeById(Long id){
		Episode episode=boxMetadataRepository.findEpisodeById(id);
		List<ScheduleEvent> scheduleEvents=boxMetadataRepository.findScheduleEventByEpisode(episode);
		uk.co.boxnetwork.data.Episode ret=new uk.co.boxnetwork.data.Episode(episode);
		ret.setScheduleEvents(scheduleEvents);
		return ret;		
	}
	@Transactional
	public void update(long id, uk.co.boxnetwork.data.Episode episode){
		Episode existingEpisode=boxMetadataRepository.findEpisodeById(id);
		episode.update(existingEpisode);
		boxMetadataRepository.update(existingEpisode);
		
	}
		
	public List<ScheduleEvent> getAllScheduleEvent(){
		return boxMetadataRepository.findAllScheduleEvent();
	}
	public ScheduleEvent getScheduleEventById(Long id){
		return boxMetadataRepository.findScheduleEventById(id);
	}
	
	
}
