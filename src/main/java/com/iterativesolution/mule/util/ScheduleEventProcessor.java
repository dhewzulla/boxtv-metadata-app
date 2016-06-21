package com.iterativesolution.mule.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;



public class ScheduleEventProcessor {
	private static final Logger logger=LoggerFactory.getLogger(ScheduleEventProcessor.class);
	NamedParameterJdbcTemplate jdbcTemplate;
	
	public ScheduleEventProcessor(NamedParameterJdbcTemplate jdbcTemplate){
		this.jdbcTemplate=jdbcTemplate;		
	}
	public void processScheduleEventGroup(Element groupElement){		
		for ( Iterator<Element> i = groupElement.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();
			if(elem.getName().equals("ScheduleEvents")){
				processScheduleEvents(elem);
			}
		}
			
		
	}
	private void processScheduleEvents(Element scheduleEvents){
		for ( Iterator<Element> i = scheduleEvents.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();
			if(elem.getName().equals("BS.ScheduleEvent")){				
				processScheduleEvent(elem);
			}
		}
	}
	
	private void processScheduleEvent(Element scheduleEvent){
			
		    String asset_id=XmlToMapParser.getElementContentByPaths(scheduleEvent, "Asset/AssetID");
			String asset_name=XmlToMapParser.getElementContentByPaths(scheduleEvent, "Asset/AssetName");
			String asset_type=XmlToMapParser.getElementContentByPaths(scheduleEvent, "Asset/AssetType");
			
			String SQL = "INSERT INTO assets (asset_id, asset_name, asset_type) VALUES (:asset_id, :asset_name, :asset_type)";
			
			Map<String, String> namedParameters = new HashMap<String,String>();   
		    namedParameters.put("asset_id", asset_id);   
		    namedParameters.put("asset_name", asset_name);
		    namedParameters.put("asset_type", asset_type);
		    jdbcTemplate.update(SQL, namedParameters);  
		    		
	}
	
}
