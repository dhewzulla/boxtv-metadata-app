package uk.co.boxnetwork.components;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.C4ScheduleSoapParser;
import uk.co.boxnetwork.data.C4Metadata;
import uk.co.boxnetwork.model.Asset;

@Service
public class C4ScheduleReceiver {
	private static final Logger logger=LoggerFactory.getLogger(C4ScheduleReceiver.class);
	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	@Autowired
	C4ScheduleSoapParser c4SchedulerParser;
	
	public void process(Document document){
		C4Metadata c4metadata=c4SchedulerParser.parse(document);
		for(Asset asset: c4metadata.getAssets()){
			if(boxMetadataRepository.getAssetById(asset.getId())==null){
				boxMetadataRepository.createAsset(asset);
			}
			else{
				logger.info("asset already exists");
			}
		}
	}
	
}
