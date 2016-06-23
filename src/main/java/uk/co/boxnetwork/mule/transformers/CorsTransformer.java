package uk.co.boxnetwork.mule.transformers;

import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.mule.util.ConfigSelector;



public class CorsTransformer extends AbstractMessageTransformer{
	private static final Logger logger=LoggerFactory.getLogger(CorsTransformer.class);
	public enum OriginLocation {	
		header,
		invocation		
    }
	private ConfigSelector corsconfig; 
	private OriginLocation originLocation= OriginLocation.header;
	private String originVariableName="Origin";
	
	
	public void setOriginVariableName(String originVariableName) {
		this.originVariableName = originVariableName;
	}

	public void setOriginLocation(OriginLocation originLocation) {
		this.originLocation = originLocation;
	}

	public void setCorsconfig(ConfigSelector corsconfig) {
		this.corsconfig = corsconfig;
	}

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		String origin=null;
		 
		if(originLocation==OriginLocation.header){
			Map<String,String> headers=message.getInboundProperty("http.headers");
			origin= headers.get(originVariableName);
		}
		else if(originLocation==OriginLocation.invocation){
			origin=message.getInvocationProperty(originVariableName);			
		}
		else
			throw new RuntimeException("The origin location is not set");
			      
		 
		 Map<String,Object> selectedConfig=corsconfig.selectConfig(origin);
		 if(selectedConfig!=null){
			 message.setOutboundProperty("Access-Control-Allow-Origin", origin);
			 message.setOutboundProperty("Access-Control-Max-Age", selectedConfig.get("max-age"));
			 message.setOutboundProperty("Access-Control-Allow-Methods","GET");
			 message.setOutboundProperty("Access-Control-Allow-Headers",originVariableName+", X-Requested-With, Content-Type, Accept,apikey");
			 logger.info("allowed origin:"+origin);
		 }
		 else
			 logger.info("not allowed origin:"+origin);
		return message.getPayload();
	}
	
	
	

}
