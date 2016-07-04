package uk.co.boxnetwork.components;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import uk.co.boxnetwork.data.BasicAuthenticatedURLConfiguration;
import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.data.bc.BCAccessToken;
import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class ImportC4ScheduleService {
	private static final Logger logger=LoggerFactory.getLogger(ImportC4ScheduleService.class);
 @Autowired
 @Qualifier("c4ScheduleConfiguration")
 private BasicAuthenticatedURLConfiguration c4ScheduleConfiguration;
 
 @Autowired
 @Qualifier("c4CertificationConfiguration")
 private BasicAuthenticatedURLConfiguration c4CertificationConfiguration;

 @Autowired
 private C4ScheduleReceiver c4scheduleReceiver;
 
 public String requestSchedulService(ImportScheduleRequest request){	
		RestTemplate rest=new RestTemplate();
		
		logger.info("c4ScheduleConfiguration="+c4ScheduleConfiguration+" c4CertificationConfiguration=["+c4CertificationConfiguration+"]request="+request);
		 String requestContent=GenericUtilities.readFileContent("data/schedule/request.xml");
		 requestContent=requestContent.replace("${channelId}",request.getChannelId());
		 requestContent=requestContent.replace("${fromDate}",request.getFromDate());
		 requestContent=requestContent.replace("${toDate}",request.getToDate());
		 requestContent=requestContent.replace("${type}",request.getType());
		 requestContent=requestContent.replace("${info}",request.getInfo());
		 
		 logger.info("*******requestContent**************:"+requestContent);
		 
		
		
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();
		headers.add("Content-Type", "text/xml;charset=UTF-8");	    
		headers.add("Authorization", "Basic " + c4ScheduleConfiguration.basicAuthentication());
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestContent, headers);		
	    ResponseEntity<String> responseEntity = rest.exchange(c4ScheduleConfiguration.getUrl(), HttpMethod.POST, requestEntity, String.class);
	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info(":::::::::statuscode:"+statusCode);
	    return responseEntity.getBody();	    	
	}
 
 
 
 
 public void importSchedule(ImportScheduleRequest request){	 	 
	 String schedule=requestSchedulService(request);
	 try{
		 c4scheduleReceiver.process(schedule);
	 }
	 catch(Exception ex){
		 logger.error("error is parsing the schedule", ex);
		 logger.error("response returned from the pirate:"+schedule);		 
	 }
 }
 
	
}
