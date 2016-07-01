package uk.co.boxnetwork.components;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.stereotype.Service;

import uk.co.boxnetwork.data.BCAccessToken;
import uk.co.boxnetwork.data.BCConfiguration;
import uk.co.boxnetwork.data.BCVideoData;
import uk.co.boxnetwork.util.RestResponseHandler;

@Service
public class BCVideoService {
	private static final Logger logger=LoggerFactory.getLogger(BCVideoService.class);
	@Autowired
    private BCConfiguration configuration;
	
	@Autowired
	private BCAccessTokenService bcAccessToenService;
	
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
}
