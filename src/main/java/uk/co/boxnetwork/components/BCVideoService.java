package uk.co.boxnetwork.components;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.data.FileIngestRequest;
import uk.co.boxnetwork.data.bc.BCAccessToken;
import uk.co.boxnetwork.data.bc.BCConfiguration;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.data.bc.BCVideoIngestRequest;
import uk.co.boxnetwork.data.bc.BcIngestResponse;
import uk.co.boxnetwork.model.BCNotification;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.util.GenericUtilities;


@Service
public class BCVideoService {
	private static final Logger logger=LoggerFactory.getLogger(BCVideoService.class);
	@Autowired
    private BCConfiguration configuration;
	
	@Autowired
	private BCAccessTokenService bcAccessToenService;
	
	@Autowired
	BoxMedataRepository metadataRepository;
	
	public String  listVideo(String limit, String offset, String sort,String q){
		return listVideo(GenericUtilities.bcInteger(limit),GenericUtilities.bcInteger(offset),GenericUtilities.bcString(sort),GenericUtilities.bcString(q));		
	}
	public String  listVideo(Integer limit, Integer offset, String sort,String q){			
		    BCAccessToken accessToken=bcAccessToenService.getAccessToken();
			RestTemplate rest=new RestTemplate();
			//rest.setErrorHandler(new RestResponseHandler());
			HttpHeaders headers=new HttpHeaders();			
		    headers.add("Accept", "*/*");
			headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
			HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);	
			String videoURL=configuration.videoURL(limit, offset, sort,q);
			logger.info("videoURL=["+videoURL+"]");
		    ResponseEntity<String> responseEntity = rest.exchange(videoURL, HttpMethod.GET, requestEntity, String.class);
		    responseEntity.getStatusCode();	    
		    HttpStatus statusCode=responseEntity.getStatusCode();
		    logger.info(":::::::::statuscode:"+statusCode);
		    return responseEntity.getBody();
	}
	public String  getViodeInJson(String videoid){			
	    BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate();
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);		
	    ResponseEntity<String> responseEntity = rest.exchange(configuration.videoURL(videoid), HttpMethod.GET, requestEntity, String.class);
	    responseEntity.getStatusCode();	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info(":::::::::statuscode:"+statusCode);
	    return responseEntity.getBody();
	    
   }
	public String updateVideo(BCVideoData videodata, String brightcoveid){
		   BCAccessToken accessToken=bcAccessToenService.getAccessToken();			
		   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();				
		   objectMapper.setSerializationInclusion(Include.NON_NULL);
			try {
				String videoInJson = objectMapper.writeValueAsString(videodata);				
				logger.info("About to PATCH to brightcove, brightcoveid=["+brightcoveid+"] content="+videoInJson);
				String videoURL=configuration.videoURL(brightcoveid);
					//httpMethod=HttpMethod.PATCH;
					//					int ib=videoURL.indexOf("/v1/");
					//					
					//					videoURL="http://192.168.0.16:8888/v1/"+videoURL.substring(ib+4);
					PostMethod m = new PostMethod(videoURL) {					
						        @Override public String getName() { return "PATCH"; }					
				   };
				   m.setRequestHeader("Authorization", "Bearer " + accessToken.getAccess_token());
				   m.setRequestEntity(new StringRequestEntity(videoInJson, "application/json", "UTF-8"));
				   HttpClient c = new HttpClient();				
				   int sc = c.executeMethod(m);
				   logger.info(":::::::::statuscode:"+sc);
				   return m.getResponseBodyAsString();				
			} catch (Exception e) {
				logger.error("error while parsing the brightcove video data",e);
				if(e instanceof HttpStatusCodeException){
					 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
					 logger.info("******ERROR in RestTemplate*****"+errorResponse);
					 throw new RuntimeException(errorResponse,e);
				}
				else{
					 throw new RuntimeException(e+" while updatting the video in brightcove",e);
				}
				
				
			}
	}
	public String  createVideo(BCVideoData videodata){			
	    BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate();
		
				
		
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		
		
		try {
			String videoInJson = objectMapper.writeValueAsString(videodata);
			
			logger.info("About to POST to brightcove, content="+videoInJson);
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(videoInJson, headers);
			String videoURL=configuration.videoURL(null,null,null,null);
			
		    ResponseEntity<String> responseEntity = rest.exchange(videoURL,HttpMethod.POST , requestEntity, String.class);
		    
		    responseEntity.getStatusCode();	    
		    HttpStatus statusCode=responseEntity.getStatusCode();
		    logger.info(":::::::::statuscode:"+statusCode);
		    return responseEntity.getBody();
		
		} catch (Exception e) {
			logger.error("error while parsing the brightcove video data",e);
			if(e instanceof HttpStatusCodeException){
				 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
				 logger.error("******ERROR in RestTemplate*****"+errorResponse);
				 throw new RuntimeException(errorResponse,e);
			}
			else{
				 throw new RuntimeException(e+" while creating video in brightcove",e);
			}
			
			
		}
		
		
	    
   }
	
	public String deleteVideo(String brightcoveID){
		brightcoveID=brightcoveID.trim();
		if(brightcoveID.length()<5){
			throw new RuntimeException("brightcoveID is too short for delete:"+brightcoveID);
		}
		 BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		 RestTemplate rest=new RestTemplate();			
		 HttpHeaders headers=new HttpHeaders();			
		 headers.add("Accept", "*/*");
		 headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
			try {
			
				
				logger.info("About to DELETE  from brightcoveID="+brightcoveID);
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
				String videoURL=configuration.videoURL(brightcoveID);				
			    ResponseEntity<String> responseEntity = rest.exchange(videoURL,HttpMethod.DELETE , requestEntity, String.class);			    
			    responseEntity.getStatusCode();	    
			    HttpStatus statusCode=responseEntity.getStatusCode();
			    
			    logger.info(":::::::::statuscode:"+statusCode);
			    if(!statusCode.is2xxSuccessful()){
			    	throw new RuntimeException("Returned not successfull response from the server:statusCode="+statusCode+":"+statusCode.getReasonPhrase());
			    }
			    return responseEntity.getBody();
			
			} catch (Exception e) {
				logger.error("error while parsing the brightcove video data",e);
				if(e instanceof HttpStatusCodeException){
					 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
					 logger.error("******ERROR in RestTemplate*****"+errorResponse);
					 throw new RuntimeException(errorResponse,e);
				}
				else{
					 throw new RuntimeException(e+" while creating video in brightcove",e);
				}
				
				
			}
	}
	
	public BcIngestResponse ingestVideo(BCVideoIngestRequest ingestRequest, String videoid){
		BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate();
		
				
		
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		
		
		try {
			String requestInJson = objectMapper.writeValueAsString(ingestRequest);
			
			logger.info("About to posting to brightcove, requestInJson="+requestInJson);
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(requestInJson, headers);
			
			String ingestURL=configuration.ingestUrl(videoid);
		     ResponseEntity<String> responseEntity = rest.exchange(ingestURL,HttpMethod.POST , requestEntity, String.class);
			 
		    
		    responseEntity.getStatusCode();	    
		    HttpStatus statusCode=responseEntity.getStatusCode();
		    logger.info(":::::::::statuscode:"+statusCode);
		    String responseBody= responseEntity.getBody();
		    logger.info("The Ingest response:"+responseBody);
		    BcIngestResponse response= jsonToBcIngestResponse(responseBody);
		    response.setCallback(configuration.getIngestCallback()+"/"+response.getId());
		    return response;
		
		} catch (Exception e) {
			logger.error("error while parsing the brightcove video data",e);
			if(e instanceof HttpStatusCodeException){
				 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
				 logger.info("******ERROR in RestTemplate*****"+errorResponse);
				 throw new RuntimeException(errorResponse,e);
			}
			else{
				 throw new RuntimeException(e+" while creating video in brightcove",e);
			}
		}
		
		
	}
	
	public  BCVideoData jsonToBCVideoData(String videoInJson){
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);	
		
	    BCVideoData video;
		try {
			video = objectMapper.readValue(videoInJson, BCVideoData.class);
			return video;			
		} catch (IOException e) {
			logger.error("error while parsing the brightcove video data",e);
			logger.error(videoInJson);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	private BcIngestResponse jsonToBcIngestResponse(String responseInJson){
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);	
		
		BcIngestResponse response;
		try {
			response = objectMapper.readValue(responseInJson, BcIngestResponse.class);
			return response;			
		} catch (IOException e) {
			logger.error("error while parsing the brightcove video data",e);
			logger.error(responseInJson);
			throw new RuntimeException("Error parsing the ingest response"+e,e);
		}
	}
	public BCVideoData  getVideo(String videoid){			
	    String videoInJson=getViodeInJson(videoid);
	    return jsonToBCVideoData(videoInJson);		
	    
   }
	
	@Transactional
	public BCVideoData publishEpisodeToBrightcove(long episodeid){
		  Episode  episode=metadataRepository.findEpisodeById(episodeid);		  
		  if(episode==null){
			  throw new RuntimeException("The episodeid is not found in the database"); 
		  }
		  List<ScheduleEvent> schedules=metadataRepository.findScheduleEventByEpisode(episode);
		  BCVideoData newbcVideoData=new BCVideoData(episode);
		  newbcVideoData.calculateSchedule(schedules);
		  if(episode.getBrightcoveId()==null){			 
			  newbcVideoData.setName(newbcVideoData.getName());			  
			  String reponse=createVideo(newbcVideoData);
			  logger.info("create video resposse:"+reponse);
			  BCVideoData createdVideo=jsonToBCVideoData(reponse);
			  logger.info("created video:"+createdVideo);
			  episode.setBrightcoveId(createdVideo.getId());
			  metadataRepository.update(episode);
			  return createdVideo;
		  }
		  else{
			  
			  BCVideoData existingbcVideoData=getVideo(episode.getBrightcoveId());
			  DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			 
			  /*
			  try {
				  
				  Date brightmodifiedAt=m_ISO8601Local.parse(existingbcVideoData.getUpdated_at());
				  Calendar cal = Calendar.getInstance();
				  cal.setTime(brightmodifiedAt);
				  cal.add(Calendar.HOUR, -2);
				  brightmodifiedAt = cal.getTime();
				  
				  if(episode.getLastModifiedAt().before(brightmodifiedAt)){
					  logger.error("Brightcove date is latest that the boxmedia db");
					  throw new RuntimeException("could not update brightcove, for it contains the latest date:episode=["+episode+"]");					  
				  }
				  
			  	} catch (ParseException e) {
			  		logger.error(e+" parsing the updated_at in brightcove",e);		
			  		throw new RuntimeException("wrong format in updated_at in brightcove date:episode=["+episode+"]:"+existingbcVideoData.getUpdated_at());
			  	}
			  	*/
			  
			   BCVideoData updateVideo=new BCVideoData();
			   updateVideo.setState(null);			   
			   updateVideo.copyFrom(newbcVideoData);
			   if(existingbcVideoData.getId().equals(episode.getBrightcoveId())){
				  String reponse=updateVideo(updateVideo,episode.getBrightcoveId());
				  logger.info("update video respomse:"+reponse);
				  BCVideoData updatedVideo=jsonToBCVideoData(reponse);
				  logger.info("updatedVideo video:"+updatedVideo);
				  return updatedVideo;
			  }
			  else{
				  logger.error("Different Brightcoveid is returned from the videocloud:"+existingbcVideoData.getId()+":"+episode.getBrightcoveId());		
			  		throw new RuntimeException("wrong format in updated_at in brightcove date:episode=["+episode+"]:"+existingbcVideoData.getUpdated_at()); 
			  }
			  
		  }
		  
		  
	}
	
	@Transactional
	public String deleteEpisodeFromBrightcove(long episodeid){
		  Episode  episode=metadataRepository.findEpisodeById(episodeid);		  
		  if(episode==null){
			  throw new RuntimeException("The episodeid is not found in the database for removing from the brightcove"); 
		  }
		  
		  if(episode.getBrightcoveId()==null){			 
			  throw new RuntimeException("The Episode is not published");
		  }
		  else{
			  
			  BCVideoData existingbcVideoData=getVideo(episode.getBrightcoveId());
			  if(existingbcVideoData==null){
				  throw new RuntimeException("The video is not found in the brightcove");
			  }
			  DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			  if(existingbcVideoData.getId().equals(episode.getBrightcoveId())){				  
				  String reponse=deleteVideo(episode.getBrightcoveId());
				  logger.info("successfully deleted:"+episode.getBrightcoveId()+" for episode=["+episode+"]reponse=["+reponse+"]");
				  episode.setBrightcoveId(null);
				  metadataRepository.update(episode);
				  return reponse;
			  }
			  else{
				  logger.error("Different Brightcoveid is returned from the videocloud:"+existingbcVideoData.getId()+":"+episode.getBrightcoveId());		
			  	   throw new RuntimeException("Different Brightcoveid is returned from the videocloud:"+existingbcVideoData.getId()+":"+episode.getBrightcoveId()); 
			  }
			  
		  }
		  
		  
	}
	
	
	@Transactional
	public BcIngestResponse ingestVideoToBrightCove(FileIngestRequest ingestRequest){
		  Episode  episode=metadataRepository.findEpisodeById(ingestRequest.getEpisodeid());		  
		  if(episode==null){
			  throw new RuntimeException("The episodeid is not found in the database"); 
		  }		  
		  if(episode.getBrightcoveId()==null){
			  throw new RuntimeException("The episode is not published to brightcove:"+episode);			  
		  }
		  else if(episode.getIngestSource()==null){
			  throw new RuntimeException("The ingestSource  is not set in episode: episode="+episode);			  
		  }
		  else if(episode.getIngestProfile()==null){
			  throw new RuntimeException("The ingestSource  is not specified"+episode);			  
		  }	
		  else{
			  	  BCVideoIngestRequest bcVideoIngestRequest=new BCVideoIngestRequest(episode,configuration);			  	  
				  return ingestVideo(bcVideoIngestRequest,episode.getBrightcoveId());
			  }
			  
		  
	}
	@Transactional
	public void persist(BCNotification bcNotification){
		metadataRepository.persist(bcNotification);		
	}
	
	
	public List<BCNotification> findAllBCNotification(){
		return metadataRepository.findAllBCNotification();		
	}
	public List<BCNotification> findBCNotificationByJobId(String jobid){
		return metadataRepository.findBCNotificationByJobId(jobid);		
	}
	
}
