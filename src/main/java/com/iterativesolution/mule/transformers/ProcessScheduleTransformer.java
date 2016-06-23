package com.iterativesolution.mule.transformers;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;

import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;


import com.iterativesolution.mule.box.repository.AssetDAO;


import com.iterativesolution.mule.util.XmlToMapParser;

import uk.co.boxnetwork.model.Asset;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class ProcessScheduleTransformer extends AbstractMessageTransformer{
	private String schedulepath[]={"Envelope","Body","GetSchedulesByParamsResponse","GetSchedulesByParamsResult","ScheduleEventGroups"};
	private static final Logger logger=LoggerFactory.getLogger(ProcessScheduleTransformer.class);
	
	private NamedParameterJdbcTemplate jdbcTemplate; 
	private AssetDAO assetDAO;
	
	
	
	
	public AssetDAO getAssetDAO() {
		return assetDAO;
	}


	public void setAssetDAO(AssetDAO assetDAO) {
		this.assetDAO = assetDAO;
	}


	public void setDataSource(DataSource  datasource) {		
		this.jdbcTemplate =new NamedParameterJdbcTemplate(datasource);
		
	}
  

	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		try {
			SAXReader reader = new SAXReader();
			Object payload=message.getPayload();
			Document document=null;
			if(payload instanceof InputStream){
				document=reader.read((InputStream)payload);				
			}
			else if(payload instanceof InputSource){				 
				document=reader.read((InputSource)payload);
			}
			else if(payload instanceof Reader){
				document=reader.read((Reader)payload);
			}
			else if(payload instanceof byte[]){
				document=reader.read(new ByteArrayInputStream((byte[])payload));
			}
			else if(payload instanceof String){
				document=reader.read(new CharArrayReader(((String)payload).toCharArray()));
			}
			else
				throw new TransformerException(this,new Exception("payload is not the expected type:"+payload.getClass()));
			
			Element shceduleEvenGroups=XmlToMapParser.getElementByPaths(document,schedulepath);
			
//			for ( Iterator<Element> i = shceduleEvenGroups.elementIterator(); i.hasNext(); ) {
//					Element elem = (Element) i.next();
//					ScheduleEventProcessor scheduleEventProcess=new ScheduleEventProcessor(jdbcTemplate);
//					scheduleEventProcess.processScheduleEventGroup(elem);				    
//			}
			Asset asset=new Asset();
			asset.setId(String.valueOf(System.currentTimeMillis()));
			asset.setName("test");
			asset.setType("dd");
			assetDAO.createAsset(asset);
			
			return message.getPayload();
	        
	    } catch (Exception e) {
	        System.out.println("Error: " + e);
	        e.printStackTrace();
	    }
	    return null;
	}

}
