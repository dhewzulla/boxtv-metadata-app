package uk.co.boxnetwork.components;
import java.io.IOException;

import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
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

import uk.co.boxnetwork.data.bc.BCAccessToken;
import uk.co.boxnetwork.data.bc.BCConfiguration;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.model.Episode;


@Service
public class BCVideoService {
	private static final Logger logger=LoggerFactory.getLogger(BCVideoService.class);
	@Autowired
    private BCConfiguration configuration;
	
	@Autowired
	private BCAccessTokenService bcAccessToenService;
	
	@Autowired
	BoxMedataRepository metadataRepository;
	
	
	public String  listVideo(){			
		    BCAccessToken accessToken=bcAccessToenService.getAccessToken();
			RestTemplate rest=new RestTemplate();
			//rest.setErrorHandler(new RestResponseHandler());
			HttpHeaders headers=new HttpHeaders();			
		    headers.add("Accept", "*/*");
			headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
			HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);		
		    ResponseEntity<String> responseEntity = rest.exchange(configuration.videoURL(), HttpMethod.GET, requestEntity, String.class);
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
	public String  createNewVideo(BCVideoData videodata){			
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
			
			logger.info("About to posting to brightcove:"+videoInJson);
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(videoInJson, headers);
			
		    ResponseEntity<String> responseEntity = rest.exchange(configuration.videoURL(), HttpMethod.POST, requestEntity, String.class);
		    
		    responseEntity.getStatusCode();	    
		    HttpStatus statusCode=responseEntity.getStatusCode();
		    logger.info(":::::::::statuscode:"+statusCode);
		    return responseEntity.getBody();
		
		} catch (Exception e) {
			logger.error("error while parsing the brightcove video data",e);
			if(e instanceof HttpStatusCodeException){
				 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
				 logger.info("******ERROR in RestTemplate*****"+errorResponse);
			}
			
			return null;
		}
		
		
	    
   }
	private BCVideoData jsonToBCVideoData(String videoInJson){
		ObjectMapper objectMapper=new ObjectMapper();	    
	    BCVideoData video;
		try {
			video = objectMapper.readValue(videoInJson, BCVideoData.class);
			return video;			
		} catch (IOException e) {
			logger.error("error while parsing the brightcove video data",e);
			logger.error(videoInJson);
			return null;
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
		  if(episode.getBrightcoveId()==null){
			  BCVideoData bcVideoData=new BCVideoData(episode);
			  String reponse=createNewVideo(bcVideoData);
			  logger.info("create video respomse:"+reponse);
			  BCVideoData createdVideo=jsonToBCVideoData(reponse);
			  logger.info("created video:"+createdVideo);
			  episode.setBrightcoveId(createdVideo.getId());
			  metadataRepository.update(episode);
			  return createdVideo;
		  }
		  return null;
		  
	}
	
}
