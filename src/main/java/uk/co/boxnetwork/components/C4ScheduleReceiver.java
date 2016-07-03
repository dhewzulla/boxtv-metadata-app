package uk.co.boxnetwork.components;

import java.io.CharArrayReader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.C4ScheduleSoapParser;
import uk.co.boxnetwork.data.C4Metadata;

import uk.co.boxnetwork.model.ScheduleEvent;

@Service
public class C4ScheduleReceiver {
	private static final Logger logger=LoggerFactory.getLogger(C4ScheduleReceiver.class);
	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	@Autowired
	C4ScheduleSoapParser c4SchedulerParser;
	
	@Transactional
	public void process(Document document){
		C4Metadata c4metadata=c4SchedulerParser.parse(document);
		for(ScheduleEvent event: c4metadata.getScheduleEvents()){
			boxMetadataRepository.createEvent(event);
		}
	}
	@Transactional
	public void process(String scheduleDocument){
		SAXReader reader = new SAXReader();
		try {
			Document document=reader.read(new CharArrayReader((scheduleDocument.toCharArray())));
			C4Metadata c4metadata=c4SchedulerParser.parse(document);
			for(ScheduleEvent event: c4metadata.getScheduleEvents()){
				boxMetadataRepository.createEvent(event);
			}
			
		} catch (DocumentException e) {
			logger.error("Error in Parsing schedule document",e);
		   throw new RuntimeException(e+ "in parsig the schedule document",e);
		}
		
	}
	
}
