package uk.co.boxnetwork.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.model.Episode;

@Service
public class MetadataMaintainanceService {
	@Autowired
	private BoxMedataRepository repository;

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
	

}
