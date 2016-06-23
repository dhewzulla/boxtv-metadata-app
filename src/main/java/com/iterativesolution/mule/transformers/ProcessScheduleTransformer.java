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





import com.iterativesolution.mule.util.XmlToMapParser;

import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.model.Asset;
import uk.co.boxnetwork.services.C4ScheduleReceiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class ProcessScheduleTransformer extends AbstractMessageTransformer{
	
	private static final Logger logger=LoggerFactory.getLogger(ProcessScheduleTransformer.class);
	
	
	private C4ScheduleReceiver scheduleReceiver;
	
	
	
	
	

	
  

	public void setScheduleReceiver(C4ScheduleReceiver scheduleReceiver) {
		this.scheduleReceiver = scheduleReceiver;
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
			scheduleReceiver.process(document);			
			
			return message.getPayload();
	        
	    } catch (Exception e) {
	        System.out.println("Error: " + e);
	        e.printStackTrace();
	    }
	    return null;
	}

}
