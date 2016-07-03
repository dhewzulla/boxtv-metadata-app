package uk.co.boxnetwork.mule.transformers.metadata;




import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;


public class TagsTransformer  extends BoxRestTransformer{
	
	@Autowired
	BoxMedataRepository boxMedataRepository;
			
	@Override
	protected  Object processGET(MuleMessage message, String outputEncoding){					
			String[] tags=boxMedataRepository.getAllTags();
			if(tags==null){
				tags=new String[0];				
			}			
			return tags;
	}      
   
}