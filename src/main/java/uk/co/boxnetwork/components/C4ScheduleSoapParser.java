package uk.co.boxnetwork.components;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;



import uk.co.boxnetwork.data.C4Metadata;
import uk.co.boxnetwork.model.Asset;
import uk.co.boxnetwork.mule.util.XmlToMapParser;

@Component
public class C4ScheduleSoapParser {
	private String schedulepath[]={"Envelope","Body","GetSchedulesByParamsResponse","GetSchedulesByParamsResult","ScheduleEventGroups"};
	
	
	public C4Metadata parse(Document document){
		C4Metadata c4Medata=new C4Metadata();
		
		Element shceduleEvenGroups=XmlToMapParser.getElementByPaths(document,schedulepath);
		for ( Iterator<Element> i = shceduleEvenGroups.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();				
			processScheduleEventGroup(elem,c4Medata);				    
	    }
		return c4Medata;
		
	}	
	public void processScheduleEventGroup(Element groupElement,C4Metadata c4Medata){		
		for ( Iterator<Element> i = groupElement.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();
			if(elem.getName().equals("ScheduleEvents")){
				processScheduleEvents(elem, c4Medata);
			}
		}
			
		
	}
	private void processScheduleEvents(Element scheduleEvents,C4Metadata c4Medata){
		for ( Iterator<Element> i = scheduleEvents.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();
			if(elem.getName().equals("BS.ScheduleEvent")){				
				processScheduleEvent(elem,c4Medata);
			}
		}
	}
	
	private void processScheduleEvent(Element scheduleEvent,C4Metadata c4Medata){
			
		    String asset_id=XmlToMapParser.getElementContentByPaths(scheduleEvent, "Asset/AssetID");
			String asset_name=XmlToMapParser.getElementContentByPaths(scheduleEvent, "Asset/AssetName");
			String asset_type=XmlToMapParser.getElementContentByPaths(scheduleEvent, "Asset/AssetType");
			
			Asset asset=new Asset();
			asset.setId(asset_id);
			asset.setName(asset_name);
			asset.setType(asset_type);			
			c4Medata.addAsset(asset);			  
		    		
	}
	
	
	
}
