package uk.co.boxnetwork.services;

import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.C4ScheduleSoapParser;
import uk.co.boxnetwork.data.C4Metadata;
import uk.co.boxnetwork.model.Asset;

public class C4ScheduleReceiver {
	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	@Autowired
	C4ScheduleSoapParser c4SchedulerParser;
	
	public void process(Document document){
		C4Metadata c4metadata=c4SchedulerParser.parse(document);
		for(Asset asset: c4metadata.getAssets()){
			boxMetadataRepository.createAsset(asset);
		}
	}
	
}
